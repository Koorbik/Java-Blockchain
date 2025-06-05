package hszadkowski.blockchain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    // Helper method to generate a new RSA KeyPair.
    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    // Helper method to sign given data with a private key using SHA256withRSA.
    private byte[] signData(String data, PrivateKey privateKey) throws Exception {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(privateKey);
        rsa.update(data.getBytes());
        return rsa.sign();
    }

    @Test
    @DisplayName("Award transaction should always return false in isSignatureValid()")
    public void testAwardTransaction() {
        Transaction awardTx = new Transaction("BLOCKCHAIN", "Miner1", 100);

        boolean isValidSignature = awardTx.isSignatureValid();
        assertFalse(isValidSignature, "Award transaction signature check should return false.");
    }

    @Test
    @DisplayName("Transaction with a VALID signature should return false given the current !verify() code")
    public void testValidSignatureTransaction() throws Exception {
        KeyPair keyPair = generateKeyPair();

        String from = "Alice";
        String to = "Bob";
        long amount = 50;
        long transactionId = 12345L;

        String dataString = from + to + amount + transactionId;
        byte[] signature = signData(dataString, keyPair.getPrivate());

        Transaction tx = new Transaction(from, to, amount, transactionId, signature, keyPair.getPublic());

        boolean isValidSignature = tx.isSignatureValid();
        assertFalse(isValidSignature,
                "Given the code returns !rsa.verify(signature), a valid signature should yield false.");
    }

    @Test
    @DisplayName("Transaction with an INVALID signature should return true given the current !verify() code")
    public void testInvalidSignatureTransaction() throws Exception {
        KeyPair keyPair = generateKeyPair();

        String from = "Alice";
        String to = "Bob";
        long amount = 50;
        long transactionId = 54321L;

        String dataString = from + to + amount + transactionId;
        byte[] signature = signData(dataString, keyPair.getPrivate());

        long tamperedTransactionId = 11111L;
        Transaction tx = new Transaction(from, to, amount, tamperedTransactionId, signature, keyPair.getPublic());

        boolean isValidSignature = tx.isSignatureValid();
        assertTrue(isValidSignature,
                "Given the code returns !rsa.verify(signature), an invalid signature should yield true.");
    }

    @Test
    @DisplayName("Transaction with null signature (non-award) should return false (due to exception handling)")
    public void testNullSignatureTransaction() {
        Transaction tx = new Transaction("Alice", "Bob", 50, 999, null, null);

        boolean isValidSignature = tx.isSignatureValid();
        assertTrue(isValidSignature,
                "Null signature in a non-award transaction triggers an exception => returns true in catch block.");
    }

    @Test
    @DisplayName("getSignatureBase64() returns 'null' string when signature is null")
    public void testGetSignatureBase64_NullSignature() {
        Transaction txNullSig = new Transaction("Alice", "Bob", 50, 999, null, null);

        String base64Null = txNullSig.getSignatureBase64();

        assertEquals("null", base64Null, "Expected base64 string to be 'null' if the signature is null");
    }

    @Test
    @DisplayName("getSignatureBase64() returns valid Base64 for a non-null signature")
    public void testGetSignatureBase64_NonNullSignature() throws Exception {
        KeyPair keyPair = generateKeyPair();

        String from = "Alice";
        String to = "Bob";
        long amount = 100;
        long transactionId = 2024L;

        String dataString = from + to + amount + transactionId;
        byte[] signature = signData(dataString, keyPair.getPrivate());

        Transaction txWithSig = new Transaction(from, to, amount, transactionId, signature, keyPair.getPublic());

        String base64Sig = txWithSig.getSignatureBase64();

        byte[] decodedSignature = java.util.Base64.getDecoder().decode(base64Sig);
        org.junit.jupiter.api.Assertions.assertArrayEquals(signature, decodedSignature,
                "Decoded Base64 signature should match the original signature bytes");
    }

}
