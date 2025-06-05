package hszadkowski.blockchain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockTest {

    @Test
    @DisplayName("Block creation initializes all fields correctly")
    void testBlockInitialization() {
        int id = 1;
        long timestamp = 1670000000L;
        String previousHash = "0000000000abcdef";
        String hash = "abcdef1234567890";
        int magicNumber = 42;
        long generationTime = 10;
        String minerName = "Alice";

        Transaction minerReward = new Transaction("BLOCKCHAIN", "Alice", 100);

        Block block = new Block(id, timestamp, previousHash, hash, magicNumber, generationTime,
                Collections.singletonList(minerReward), minerName);

        assertEquals(previousHash, block.getPreviousHash(), "Previous hash should match.");
        assertEquals(hash, block.getHash(), "Hash should match.");
        assertEquals(generationTime, block.getGenerationTime(), "Generation time should match.");
        assertEquals(1, block.getTransactions().size(), "Block should contain exactly 1 transaction (miner reward).");
        assertEquals(minerReward, block.getTransactions().get(0), "Transaction should be the miner's reward.");
    }

    @Test
    @DisplayName("Block toString() handles no additional transactions")
    void testBlockToString_NoTransactions() {
        int id = 1;
        long timestamp = 1670000000L;
        String previousHash = "0000000000abcdef";
        String hash = "abcdef1234567890";
        int magicNumber = 42;
        long generationTime = 10;
        String minerName = "Alice";

        Transaction minerReward = new Transaction("BLOCKCHAIN", "Alice", 100);

        Block block = new Block(id, timestamp, previousHash, hash, magicNumber, generationTime,
                Collections.singletonList(minerReward), minerName);

        String blockString = block.toString();

        assertTrue(blockString.contains("Alice gets 100 VC"), "Block should indicate miner reward.");
        assertTrue(blockString.contains("No transactions"), "Block should indicate no transactions.");
    }

    @Test
    @DisplayName("Block toString() handles multiple transactions")
    void testBlockToString_WithTransactions() {
        int id = 2;
        long timestamp = 1670000000L;
        String previousHash = "abcdef1234567890";
        String hash = "123456abcdef7890";
        int magicNumber = 123;
        long generationTime = 15;
        String minerName = "Bob";

        Transaction minerReward = new Transaction("BLOCKCHAIN", "Bob", 100);
        Transaction tx1 = new Transaction("Alice", "Charlie", 50);
        Transaction tx2 = new Transaction("Charlie", "Dave", 30);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(minerReward);
        transactions.add(tx1);
        transactions.add(tx2);

        Block block = new Block(id, timestamp, previousHash, hash, magicNumber, generationTime,
                transactions, minerName);

        String blockString = block.toString();

        assertTrue(blockString.contains("Bob gets 100 VC"), "Block should indicate miner reward.");
        assertTrue(blockString.contains("Alice sent 50 VC to Charlie"), "Block should include the first transaction.");
        assertTrue(blockString.contains("Charlie sent 30 VC to Dave"), "Block should include the second transaction.");
    }

    @Test
    @DisplayName("getHash() and getPreviousHash() return correct values")
    void testHashGetters() {
        int id = 3;
        long timestamp = 1670000000L;
        String previousHash = "prev123hash";
        String hash = "new456hash";
        int magicNumber = 456;
        long generationTime = 5;
        String minerName = "Charlie";

        Transaction minerReward = new Transaction("BLOCKCHAIN", "Charlie", 100);

        Block block = new Block(id, timestamp, previousHash, hash, magicNumber, generationTime,
                Collections.singletonList(minerReward), minerName);

        assertEquals(previousHash, block.getPreviousHash(), "getPreviousHash() should return the correct value.");
        assertEquals(hash, block.getHash(), "getHash() should return the correct value.");
    }
}
