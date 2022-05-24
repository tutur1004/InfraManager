package fr.milekat.hostmanager.hosts.exeptions;

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
    public HostExecuteException(Throwable throwable, String message) {
        super(throwable);
        this.message = message;
    }

    /**
     * Get error message (If exist)
     */
    public String get() {
        return message;
    }
}
