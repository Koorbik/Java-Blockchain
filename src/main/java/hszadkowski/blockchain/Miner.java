package hszadkowski.blockchain;

import java.util.List;

public final class Miner implements Runnable {
    private final Blockchain blockchain;
    private final Client minerClient;

    public Miner(final Blockchain blockchain, final Client minerClient) {
        this.blockchain = blockchain;
        this.minerClient = minerClient;
    }

    @Override
    public void run() {
        try {
            while (!blockchain.hasReachedTarget()) {
                int id = blockchain.getNextBlockId();
                String previousHash = blockchain.getLastHash();

                List<Transaction> transactionsForBlock =
                        blockchain.collectTransactionsForNewBlock(minerClient.getName());

                long startTime = System.currentTimeMillis();
                int magicNumber;
                String hash;
                do {

                    if (Thread.interrupted()) {
                        return;
                    }

                    if (blockchain.getDifficulty() < 0) {
                        throw new MiningException("Invalid difficulty level: " + blockchain.getDifficulty());
                    }

                    magicNumber = (int) (Math.random() * Integer.MAX_VALUE);
                    String data = id + previousHash + magicNumber + transactionsForBlock;
                    hash = StringUtil.applySha256(data);
                } while (!hash.startsWith("0".repeat(blockchain.getDifficulty()))
                        && !blockchain.hasReachedTarget());

                long generationTime = (System.currentTimeMillis() - startTime) / 1000L;

                // Possibly another miner has already reached the target, so checking again
                if (blockchain.hasReachedTarget()) {
                    return;
                }

                Block block = new Block(id, System.currentTimeMillis(), previousHash, hash,
                        magicNumber, generationTime, transactionsForBlock,
                        minerClient.getName());

                blockchain.addBlock(block);
            }
        } catch (MiningException e) {
            System.err.println("Mining failed: " + e.getMessage());
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}
