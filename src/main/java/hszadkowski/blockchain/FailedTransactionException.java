package hszadkowski.blockchain;

public class FailedTransactionException extends Exception {
    public FailedTransactionException(String message) {
        super(message);
    }
}
