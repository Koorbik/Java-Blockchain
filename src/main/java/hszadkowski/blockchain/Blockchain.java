package hszadkowski.blockchain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

public final class Blockchain {
    private final List<Block> chain = Collections.synchronizedList(new ArrayList<>());
    private final ConcurrentLinkedQueue<Transaction> transactionQueue = new ConcurrentLinkedQueue<>();

    private final int targetBlocks;
    private int difficulty = 0;
    private volatile boolean targetReached = false;
    private final ExecutorService minerExecutor;

    public Blockchain(final int targetBlocks, final ExecutorService minerExecutor) {
        this.targetBlocks = targetBlocks;
        this.minerExecutor = minerExecutor;
    }

    public synchronized int getNextBlockId() {
        return chain.size() + 1;
    }

    public synchronized int getDifficulty() {
        return difficulty;
    }

    public synchronized boolean hasReachedTarget() {
        return targetReached;
    }

    public synchronized String getLastHash() {
        return chain.isEmpty() ? "0" : chain.get(chain.size() - 1).getHash();
    }

    public synchronized void addBlock(final Block block) {
        if (targetReached) {
            return;
        }

        if (isValidBlock(block)) {
            chain.add(block);
            for (Transaction tx : block.getTransactions()) {
                transactionQueue.remove(tx);
            }

            System.out.println(block);
            adjustDifficulty(block.getGenerationTime());

            if (chain.size() >= targetBlocks) {
                targetReached = true;
                if (!minerExecutor.isShutdown()) {
                    minerExecutor.shutdownNow();
                }
            }
        }
    }

    private boolean isValidBlock(final Block block) {
        if (!block.getHash().startsWith("0".repeat(difficulty))) {
            return false;
        }
        if (chain.isEmpty()) {
            if (!"0".equals(block.getPreviousHash())) {
                return false;
            }
        } else {
            Block lastBlock = chain.get(chain.size() - 1);
            if (!lastBlock.getHash().equals(block.getPreviousHash())) {
                return false;
            }
        }

        for (Transaction tx : block.getTransactions()) {
            if (!tx.isAwardTransaction() && tx.isSignatureValid()) {
                System.out.println("Invalid signature for transaction: " + tx);
                return false;
            }
        }
        return true;
    }

    public synchronized void adjustDifficulty(final long generationTime) {
        if (generationTime < 1) {
            difficulty++;
            System.out.println("N was increased to " + difficulty + "\n");
        } else if (generationTime > 5) {
            difficulty = Math.max(0, difficulty - 1);
            System.out.println("N was decreased to " + difficulty + "\n");
        } else {
            System.out.println("N stays the same (" + difficulty + ")\n");
        }
    }

    public synchronized void addTransaction(final Transaction transaction) {
        if (!transaction.isAwardTransaction()) {
            if (transaction.isSignatureValid()) {
                System.out.println("Rejected invalid signature for transaction: " + transaction);
                return;
            }
            long senderBalance = getBalance(transaction.getFrom());
            if (senderBalance < transaction.getAmount()) {
                System.out.println("Rejected transaction (insufficient funds): " + transaction);
                return;
            }
        }
        transactionQueue.add(transaction);
    }

    public synchronized List<Transaction> collectTransactionsForNewBlock(final String minerName) {
        List<Transaction> txs = new ArrayList<>(transactionQueue);
        Transaction awardTx = new Transaction("BLOCKCHAIN", minerName, 100);
        txs.add(0, awardTx);
        return txs;
    }

    public synchronized long getBalance(final String user) {
        if (user.equals("BLOCKCHAIN")) {
            return Long.MAX_VALUE;
        }
        long balance = 100;
        for (Block block : chain) {
            for (Transaction tx : block.getTransactions()) {
                if (tx.getFrom().equals(user)) {
                    balance -= tx.getAmount();
                }
                if (tx.getTo().equals(user)) {
                    balance += tx.getAmount();
                }
            }
        }
        return balance;
    }
}
