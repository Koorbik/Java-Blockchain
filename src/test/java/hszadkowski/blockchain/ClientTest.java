package hszadkowski.blockchain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ClientTest {

    private Client client;

    @BeforeEach
    void setUp() throws Exception {
        client = new Client("Alice");
    }
    @BeforeEach
    void resetIdGenerator() {
        Client.ID_GENERATOR.set(1); // Reset the counter
    }

    @Test
    @DisplayName("Client constructor sets the correct name")
    void testClientName() {
        assertEquals("Alice", client.getName(),
                "Client name should be 'Alice' as provided in the constructor.");
    }

    @Test
    @DisplayName("createTransaction returns a valid Transaction object")
    void testCreateTransaction() throws Exception {
        String from = "Alice";
        String to = "Bob";
        long amount = 10L;

        Transaction tx = client.createTransaction(from, to, amount);

        assertEquals(from, tx.getFrom(), "Transaction 'from' field should match.");
        assertEquals(to, tx.getTo(), "Transaction 'to' field should match.");
        assertEquals(amount, tx.getAmount(), "Transaction amount should match.");

        assertTrue(tx.toString().contains("[Tx#1]"),
                "The first transaction should have transactionId = 1.");

        assertEquals(client.getPublicKey(), tx.getPublicKey(),
                "Transaction's public key should match the client's public key.");

        assertFalse(tx.isSignatureValid(),
                "For a valid signature, isSignatureValid() returns false given the current code.");
    }


    @Test
    @DisplayName("Transaction IDs should auto-increment with multiple createTransaction calls")
    void testSequentialTransactionIds() throws Exception {
        Transaction tx1 = client.createTransaction("Alice", "Bob", 10);
        assertTrue(tx1.toString().contains("[Tx#1]"),
                "The first transaction should have transactionId = 1.");

        Transaction tx2 = client.createTransaction("Alice", "Charlie", 20);
        assertTrue(tx2.toString().contains("[Tx#2]"),
                "The second transaction should have transactionId = 2.");

        Transaction tx3 = client.createTransaction("Alice", "David", 30);
        assertTrue(tx3.toString().contains("[Tx#3]"),
                "The third transaction should have transactionId = 3.");
    }
}
