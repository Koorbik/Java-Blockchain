package hszadkowski.blockchain;

import java.util.List;

public final class Block {
    private final int id;
    private final long timestamp;
    private final String previousHash;
    private final String hash;
    private final int magicNumber;
    private final long generationTime; // in seconds
    private final List<Transaction> transactions;
    private final String minerName;

    public Block(final int id,
                 final long timestamp,
                 final String previousHash,
                 final String hash,
                 final int magicNumber,
                 final long generationTime,
                 final List<Transaction> transactions,
                 final String minerName) {
        this.id = id;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.hash = hash;
        this.magicNumber = magicNumber;
        this.generationTime = generationTime;
        this.transactions = transactions;
        this.minerName = minerName;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public long getGenerationTime() {
        return generationTime;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public int getId() {return id;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Block:\n");
        sb.append("Created by: ").append(minerName).append("\n");
        sb.append(minerName).append(" gets 100 VC\n");
        sb.append("Id: ").append(id).append("\n");
        sb.append("Timestamp: ").append(timestamp).append("\n");
        sb.append("Magic number: ").append(magicNumber).append("\n");
        sb.append("Hash of the previous block:\n").append(previousHash).append("\n");
        sb.append("Hash of the block:\n").append(hash).append("\n");

        sb.append("Block data:\n");
        if (transactions.size() <= 1) {
            sb.append("No transactions\n");
        } else {
            for (int i = 1; i < transactions.size(); i++) {
                Transaction tx = transactions.get(i);
                sb.append(tx.getFrom()).append(" sent ")
                        .append(tx.getAmount()).append(" VC to ")
                        .append(tx.getTo()).append("\n");
            }
        }

        sb.append("Block was generating for ").append(generationTime).append(" seconds");
        return sb.toString();
    }
}
