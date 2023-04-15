package server;

import java.io.IOException;

@FunctionalInterface
public interface EventHandler {
    /**
     * When an event is happening, since they all call the handle from the interface EventHandler, it helps guide it
     * to the right reaction wished.
     * @param cmd the action the client wishes to do
     * @param arg when the action wanted is the load the courses, arg is the session from which the client wishes to
     *            view the courses from
     * @throws IOException throws an exception if the cmd and/r the arg are null
     */
    void handle(String cmd, String arg) throws IOException, ClassNotFoundException;
}