package fr.milekat.hostmanager.hosts.exeptions;

import fr.milekat.hostmanager.Main;

public class HostExecuteException extends Exception {
    private final String message;

    /**
     * Issue during a host execution
     */
    public HostExecuteException(String executeState) {
        this.message = executeState;
    }

    /**
     * Issue during a host execution
     */
    public HostExecuteException(Throwable exception, String message) {
        super(exception);
        this.message = message;
        if (Main.DEBUG) {
            exception.printStackTrace();
        }
    }

    /**
     * Get error message (If exist)
     */
    public String get() {
        return message;
    }
}
