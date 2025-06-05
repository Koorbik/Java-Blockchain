package hszadkowski.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import jakarta.annotation.Generated;

public final class Transaction {
    private final String from;
    private final String to;
    private final long amount;
    private final long transactionId;
    private final byte[] signature;
    private final PublicKey publicKey;

    public Transaction(final String from,
                       final String to,
                       final long amount,
                       final long transactionId,
                       final byte[] signature,
                       final PublicKey publicKey) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.transactionId = transactionId;
        this.signature = signature;
        this.publicKey = publicKey;
    }

    /**
     * A special constructor for an award transaction (miner gets 100 VC),
     * skip signature since “BLOCKCHAIN” is not an actual person.
     */
    public Transaction(final String from, final String to, final long amount) {
        this(from, to, amount, 0, null, null);
    }

    @Generated(value = "ExcludeFromCoverage")
    public String getFrom() {
        return from;
    }

    @Generated(value = "ExcludeFromCoverage")
    public String getTo() {
        return to;
    }

    @Generated(value = "ExcludeFromCoverage")
    public long getAmount() {
        return amount;
    }

    @Generated(value = "ExcludeFromCoverage")
    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public boolean isAwardTransaction() {
        return "BLOCKCHAIN".equals(from);
    }

    public boolean isSignatureValid() {
        if (isAwardTransaction()) {
            return false;
        }
        try {
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initVerify(publicKey);
            String dataString = from + to + amount + transactionId;
            rsa.update(dataString.getBytes(StandardCharsets.UTF_8));
            return !rsa.verify(signature);
        } catch (Exception e) {
            return true;
        }
    }

    public String getSignatureBase64() {
        return signature == null ? "null" : Base64.getEncoder().encodeToString(signature);
    }

    @Override
    public String toString() {
        return "[Tx#" + transactionId + "] " + from + " -> " + to + " : " + amount + " (sig=" + getSignatureBase64() + ")";
    }
}
