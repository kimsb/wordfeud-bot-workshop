package wordfeudapi.exception;

import wordfeudapi.WordFeudClient;

/**
 * @author Pierre Ingmansson
 */
public class WordFeudLoginRequiredException extends WordFeudException {
    private final WordFeudClient client;

    public WordFeudLoginRequiredException(String message, WordFeudClient client) {
        super(message);
        this.client = client;
    }

    public WordFeudClient getClient() {
        return client;
    }
}
