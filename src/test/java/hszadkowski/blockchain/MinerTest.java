package hszadkowski.blockchain;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinerTest {

    @Test
    @DisplayName("Miner successfully mines a block")
    void testMinerMinesBlock() {

        Blockchain blockchain = mock(Blockchain.class);
        when(blockchain.getNextBlockId()).thenReturn(1);
        when(blockchain.getLastHash()).thenReturn("0000000000abcdef");
        when(blockchain.getDifficulty()).thenReturn(1);
        when(blockchain.hasReachedTarget())
                .thenReturn(false, false, false, false, false, true);
        when(blockchain.collectTransactionsForNewBlock("Miner1"))
                .thenReturn(List.of(new Transaction("BLOCKCHAIN", "Miner1", 100)));

        Client minerClient = mock(Client.class);
        when(minerClient.getName()).thenReturn("Miner1");

        mockStatic(StringUtil.class);
        when(StringUtil.applySha256(anyString())).thenReturn("0validhash");

        Miner miner = new Miner(blockchain, minerClient);
        miner.run();

        verify(blockchain, times(2)).addBlock(any(Block.class));
    }


    @Test
    @DisplayName("Miner stops mining when interrupted")
    void testMinerStopsWhenInterrupted() throws Exception {
        Blockchain blockchain = mock(Blockchain.class);
        when(blockchain.getNextBlockId()).thenReturn(1);
        when(blockchain.getLastHash()).thenReturn("0000000000abcdef");
        when(blockchain.getDifficulty()).thenReturn(4);
        when(blockchain.hasReachedTarget()).thenReturn(false);

        Client minerClient = mock(Client.class);
        when(minerClient.getName()).thenReturn("Miner2");

        Miner miner = new Miner(blockchain, minerClient);

        Thread minerThread = new Thread(miner);
        minerThread.start();

        minerThread.interrupt();

        minerThread.join(100);

        verify(blockchain, never()).addBlock(any(Block.class));
    }

    @Test
    @DisplayName("Miner does not mine if blockchain has reached target")
    void testMinerSkipsMiningWhenTargetReached() throws Exception {
        Blockchain blockchain = mock(Blockchain.class);
        when(blockchain.hasReachedTarget()).thenReturn(true);

        Client minerClient = mock(Client.class);

        Miner miner = new Miner(blockchain, minerClient);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(miner);

        Thread.sleep(100);

        verify(blockchain, never()).addBlock(any(Block.class));

        executor.shutdownNow();
    }

    @Test
    @DisplayName("Miner computes valid hash in the do-while loop")
    void testMinerComputesValidHash() {
        Blockchain blockchain = mock(Blockchain.class);
        when(blockchain.getNextBlockId()).thenReturn(1);
        when(blockchain.getLastHash()).thenReturn("0000000000abcdef");
        when(blockchain.getDifficulty()).thenReturn(4);
        when(blockchain.hasReachedTarget())
                .thenReturn(false, false, false, false, true);
        when(blockchain.collectTransactionsForNewBlock("Miner1"))
                .thenReturn(List.of(new Transaction("BLOCKCHAIN", "Miner1", 100)));

        Client minerClient = mock(Client.class);
        when(minerClient.getName()).thenReturn("Miner1");

        try (var mockedStatic = mockStatic(StringUtil.class)) {
            mockedStatic.when(() -> StringUtil.applySha256(anyString()))
                    .thenReturn("1111invalidhash",
                            "0000validhash");

            Miner miner = new Miner(blockchain, minerClient);
            miner.run();

            verify(blockchain, times(2)).getNextBlockId();
            verify(blockchain, times(2)).getLastHash();
            verify(blockchain, times(5)).hasReachedTarget();
            verify(blockchain, times(1)).addBlock(any(Block.class));

            mockedStatic.verify(() -> StringUtil.applySha256(anyString()), times(3));
        }
    }

}
