package hszadkowski.blockchain;

import java.nio.charset.StandardCharsets;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.concurrent.atomic.AtomicLong;

public final class Client {
    static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private final String name;
    private final KeyPair keyPair;

    public Client(final String name) throws NoSuchAlgorithmException {
        this.name = name;
        this.keyPair = generateKeyPair();
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        return keyGen.generateKeyPair();
    }

    public Transaction createTransaction(final String from, final String to, final long amt) throws Exception {
        long txId = ID_GENERATOR.getAndIncrement();
        String dataToSign = from + to + amt + txId;
        byte[] signature = sign(dataToSign.getBytes(StandardCharsets.UTF_8));

        return new Transaction(from, to, amt, txId, signature, keyPair.getPublic());
    }

    private byte[] sign(final byte[] data) throws Exception {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(keyPair.getPrivate());
        rsa.update(data);
        return rsa.sign();
    }

    public String getName() {
        return name;
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

}
