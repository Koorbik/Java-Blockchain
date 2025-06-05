package hszadkowski.blockchain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.security.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockchainTest {

    private Blockchain blockchain;
    private Client aliceClient;

    private byte[] signData(String data, PrivateKey privateKey) throws Exception {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(privateKey);
        rsa.update(data.getBytes());
        return rsa.sign();
    }

    @BeforeEach
    void setUp() throws Exception {
        blockchain = new Blockchain(
                3,
                Executors.newSingleThreadExecutor()
        );

        aliceClient = new Client("Alice");
    }

    @Test
    @DisplayName("Initial blockchain state")
    void testInitialState() {
        assertEquals(1, blockchain.getNextBlockId());
        assertEquals(1, blockchain.getNextBlockId(),
                "With an empty chain, nextBlockId should be 1.");

        assertEquals(0, blockchain.getDifficulty(),
                "Difficulty should start at 0.");

        assertEquals("0", blockchain.getLastHash(),
                "Last hash should be '0' when the chain is empty.");

        assertFalse(blockchain.hasReachedTarget(),
                "Target should not be reached at initialization.");
    }

    @Test
    @DisplayName("Add block with no previous blocks (genesis scenario)")
    void testAddBlock_GenesisBlock() {
        Block genesis = new Block(
                1,
                System.currentTimeMillis(),
                "0",
                "0000validhash",
                12345,
                2,
                List.of(new Transaction("BLOCKCHAIN", "Alice", 100)),
                "Alice"
        );

        blockchain.addBlock(genesis);
        assertEquals(2, blockchain.getNextBlockId(),
                "After adding the first block, the nextBlockId should be 2.");
        assertEquals(genesis.getHash(), blockchain.getLastHash(),
                "Last hash should match the genesis block's hash.");
    }

    @Test
    @DisplayName("Adding a valid block increments chain, modifies transactions, and checks difficulty")
    void testAddBlock_ValidBlock() {
        Block genesis = new Block(
                1,
                System.currentTimeMillis(),
                "0",
                "0",
                9999,
                2,
                List.of(new Transaction("BLOCKCHAIN", "Alice", 100)),
                "Alice"
        );
        blockchain.addBlock(genesis);
        assertEquals(0, blockchain.getDifficulty());

        Block block2 = new Block(
                2,
                System.currentTimeMillis(),
                genesis.getHash(),
                "0",
                5555,
                6,
                List.of(new Transaction("BLOCKCHAIN", "Bob", 100)),
                "Bob"
        );
        blockchain.addBlock(block2);
        assertEquals(2, blockchain.getNextBlockId() - 1,
                "Blockchain should now have 2 blocks in total.");
        assertEquals(0, blockchain.getDifficulty(),
                "After generationTime=6, difficulty should decrement by 1 (from 1 back to 0).");
    }

    @Test
    @DisplayName("Add invalid block (wrong previousHash) is rejected")
    void testAddBlock_InvalidPreviousHash() {
        Block invalidBlock = new Block(
                1,
                System.currentTimeMillis(),
                "NOT_ZERO", // invalid previous hash
                "0000validhash",
                123,
                0,
                List.of(new Transaction("BLOCKCHAIN", "Alice", 100)),
                "Alice"
        );
        blockchain.addBlock(invalidBlock);

        assertEquals("0", blockchain.getLastHash(),
                "The last hash should remain '0' if the block was invalid.");
        assertEquals(1, blockchain.getNextBlockId(),
                "Chain is still empty => next ID=1. The invalid block not added.");
    }

    @Test
    @DisplayName("Transactions with invalid signature are rejected in addBlock")
    void testAddBlock_InvalidSignatureTransaction() throws Exception {
        long txId = 1L;

        String wrongData = "AliceXyz50" + txId;
        byte[] badSignature = signData(wrongData, aliceClient.getPrivateKey());

        Transaction invalidTx = new Transaction("Alice", "Bob", 50, txId, badSignature, aliceClient.getPublicKey());

        Block block = new Block(
                1,
                System.currentTimeMillis(),
                "0",
                "0",
                999,
                2,
                List.of(invalidTx),
                "Alice"
        );

        blockchain.addBlock(block);

        assertEquals(0, blockchain.getDifficulty(),
                "Difficulty does not change because block was rejected");
        assertEquals(1, blockchain.getNextBlockId(),
                "No block was added => nextBlockId remains 1");
    }

    @Test
    @DisplayName("Adjust difficulty logic for generationTime < 1 and > 5")
    void testAdjustDifficulty() {
        blockchain.adjustDifficulty(0);
        assertEquals(1, blockchain.getDifficulty(),
                "Generation time=0 => difficulty++ => from 0 to 1");

        blockchain.adjustDifficulty(10);
        assertEquals(0, blockchain.getDifficulty(),
                "Generation time=10 => difficulty-- => from 1 to 0");

        blockchain.adjustDifficulty(3);
        assertEquals(0, blockchain.getDifficulty(),
                "Generation time=3 => stays the same => remains 0");
    }

    @Test
    @DisplayName("Add transaction with invalid signature is not queued")
    void testAddTransaction_InvalidSignature() throws Exception {
        long txId = 1L;
        byte[] badSignature = signData("AliceXyz50" + txId, aliceClient.getPrivateKey());
        Transaction invalidTx = new Transaction("Alice", "Bob", 50, txId, badSignature, aliceClient.getPublicKey());

        blockchain.addTransaction(invalidTx);

        List<Transaction> pending = blockchain.collectTransactionsForNewBlock("MinerXYZ");
        assertEquals(1, pending.size(),
                "Only the miner's award transaction should be present. Invalid TX not queued.");
    }

    @Test
    @DisplayName("Add transaction with insufficient funds is rejected")
    void testAddTransaction_InsufficientFunds() throws Exception {
        long txId = 1L;
        String dataString = "AliceBob9999999" + txId;
        byte[] signature = signData(dataString, aliceClient.getPrivateKey());
        Transaction bigTx = new Transaction("Alice", "Bob", 9999999, txId, signature, aliceClient.getPublicKey());

        blockchain.addTransaction(bigTx);

        List<Transaction> pending = blockchain.collectTransactionsForNewBlock("MinerXYZ");
        assertEquals(1, pending.size(),
                "Only the miner's award transaction is present. The big TX was rejected (insufficient funds).");
    }

    @Test
    @DisplayName("Add transaction with valid signature and enough funds is queued")
    void testAddTransaction_ValidSignature() throws Exception {
        long txId = 1L;
        String dataString = "AliceBob50" + txId;
        byte[] signature = signData(dataString, aliceClient.getPrivateKey());
        Transaction validTx = new Transaction("Alice", "Bob", 50, txId, signature, aliceClient.getPublicKey());

        blockchain.addTransaction(validTx);

        List<Transaction> pending = blockchain.collectTransactionsForNewBlock("MinerXYZ");
        assertEquals(2, pending.size(),
                "We should have a miner's award TX plus the valid transaction in the queue.");
        Transaction second = pending.get(1);
        assertEquals("Alice", second.getFrom());
        assertEquals("Bob", second.getTo());
        assertEquals(50, second.getAmount());
    }

    @Test
    @DisplayName("Get balance includes blocks in the chain")
    void testGetBalance() {
        Block blockAlice = new Block(
                1,
                System.currentTimeMillis(),
                "0",
                "0",
                1111,
                2,
                List.of(new Transaction("BLOCKCHAIN", "Alice", 100)),
                "Alice"
        );
        blockchain.addBlock(blockAlice);

        Block blockBob = new Block(
                2,
                System.currentTimeMillis(),
                blockAlice.getHash(),
                "0",
                2222,
                2,
                List.of(new Transaction("BLOCKCHAIN", "Bob", 100)),
                "Bob"
        );
        blockchain.addBlock(blockBob);

        long aliceBalance = blockchain.getBalance("Alice");
        long bobBalance   = blockchain.getBalance("Bob");
        long carlBalance  = blockchain.getBalance("Carl"); // not in chain => still default 100

        assertEquals(200, aliceBalance, "Alice should have 200 after the first block's award.");
        assertEquals(200, bobBalance,   "Bob should have 200 after the second block's award.");
        assertEquals(100, carlBalance,  "Carl not in any block => default 100.");
    }

    @Test
    @DisplayName("Transaction is rejected due to insufficient funds (void return type)")
    void testTransactionInsufficientFunds_Void() throws Exception {
        Client aliceClient = new Client("Alice");

        long txId = 1L;
        String dataString = "AliceBob500" + txId;
        byte[] signature = signData(dataString, aliceClient.getPrivateKey());
        Transaction insufficientFundsTx = new Transaction("Alice", "Bob", 500, txId, signature, aliceClient.getPublicKey());

        blockchain.addTransaction(insufficientFundsTx);

        List<Transaction> pendingTransactions = blockchain.collectTransactionsForNewBlock("MinerXYZ");
        assertEquals(1, pendingTransactions.size(),
                "Only the miner's reward transaction should be present. The insufficient funds transaction should not be queued.");
    }

    @Test
    @DisplayName("Target reached: Blockchain shuts down minerExecutor when targetBlocks are achieved")
    void testTargetReached() {
        ExecutorService minerExecutor = Executors.newFixedThreadPool(1);
        Blockchain blockchain = new Blockchain(2, minerExecutor);

        Block block1 = new Block(
                1,
                System.currentTimeMillis(),
                "0",
                "0000validhash",
                12345,
                2,
                List.of(new Transaction("BLOCKCHAIN", "Alice", 100)),
                "Alice"
        );

        Block block2 = new Block(
                2,
                System.currentTimeMillis(),
                block1.getHash(),
                "0000validhash",
                67890,
                2,
                List.of(new Transaction("BLOCKCHAIN", "Bob", 100)),
                "Bob"
        );

        blockchain.addBlock(block1);

        assertFalse(blockchain.hasReachedTarget(), "Target should not be reached after adding the first block.");
        assertFalse(minerExecutor.isShutdown(), "Executor should not be shut down before reaching the target.");

        blockchain.addBlock(block2);

        assertTrue(blockchain.hasReachedTarget(), "Target should be reached after adding enough blocks.");
        assertTrue(minerExecutor.isShutdown(), "Executor should be shut down after reaching the target.");
    }
}
