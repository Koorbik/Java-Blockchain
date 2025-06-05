package hszadkowski.blockchain;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class BlockchainApp {
    private static final int TARGET_BLOCKS = 15;
    private static final int MINER_POOL_SIZE = 10;

    private Blockchain blockchain;
    private ExecutorService miners;
    private ExecutorService txService;

    public static void main(String[] args) throws Exception {
        BlockchainApp blockchainApp = new BlockchainApp();
        blockchainApp.runBlockchain();
    }

    public void runBlockchain() throws Exception {
        initializeBlockchain();
        initializeMiners();
        simulateTransactions();
        waitForCompletion();
    }

    private void initializeBlockchain() {
        miners = Executors.newFixedThreadPool(MINER_POOL_SIZE);
        blockchain = new Blockchain(TARGET_BLOCKS, miners);
    }

    private void initializeMiners() throws MiningException {
        try {
            Client miner1 = new Client("miner1");
            Client miner2 = new Client("miner2");
            Client miner7 = new Client("miner7");
            Client miner9 = new Client("miner9");

            for (int i = 0; i < MINER_POOL_SIZE; i++) {
                Client randomMiner = switch (i % 4) {
                    case 0 -> miner1;
                    case 1 -> miner2;
                    case 2 -> miner7;
                    default -> miner9;
                };
                miners.submit(new Miner(blockchain, randomMiner));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new MiningException("Error initializing Clients");
        }
    }


    private void simulateTransactions() {
        txService = Executors.newSingleThreadExecutor();
        txService.submit(() -> {
            try {
                Client miner100 = new Client("miner100");
                Client miner200 = new Client("miner200");
                Client nick = new Client("Nick");
                Client bob = new Client("Bob");
                Client alice = new Client("Alice");
                Client carShop = new Client("CarShop");

                Thread.sleep(50);
                addTransaction(miner100.createTransaction("miner100", "miner1", 30));
                addTransaction(miner100.createTransaction("miner100", "miner2", 30));
                addTransaction(miner100.createTransaction("miner100", "Nick", 30));

                Thread.sleep(500);
                addTransaction(miner100.createTransaction("miner100", "Bob", 10));
                addTransaction(miner200.createTransaction("miner200", "Alice", 10));

                Thread.sleep(20);
                addTransaction(nick.createTransaction("Nick", "ShoesShop", 1));
                addTransaction(nick.createTransaction("Nick", "FastFood", 2));

                Thread.sleep(30);
                addTransaction(nick.createTransaction("Nick", "CarShop", 15));
                addTransaction(miner200.createTransaction("miner200", "CarShop", 90));

                Thread.sleep(2000);
                addTransaction(carShop.createTransaction("CarShop", "Worker1", 10));
                addTransaction(carShop.createTransaction("CarShop", "Worker2", 10));
                addTransaction(carShop.createTransaction("CarShop", "Worker3", 10));

                Thread.sleep(5000);
                addTransaction(carShop.createTransaction("CarShop", "Director1", 30));
                addTransaction(carShop.createTransaction("CarShop", "CarPartsShop", 45));
                addTransaction(bob.createTransaction("Bob", "GamingShop", 5));
                addTransaction(alice.createTransaction("Alice", "BeautyShop", 5));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addTransaction(Transaction transaction) {
        try {
            blockchain.addTransaction(transaction);
        } catch (Exception e) {
            System.err.println("Failed to add transaction: " + e.getMessage());
        }
    }

    private void waitForCompletion() throws InterruptedException {
        miners.shutdown();
        miners.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

        txService.shutdown();
        txService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
}
