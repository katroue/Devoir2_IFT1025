package server;

import java.io.IOException;

/**
 *
 */
@FunctionalInterface
public interface EventHandler {
    /**
     *
     * @param cmd
     * @param arg
     * @throws IOException
     */
    void handle(String cmd, String arg) throws IOException, ClassNotFoundException;
}