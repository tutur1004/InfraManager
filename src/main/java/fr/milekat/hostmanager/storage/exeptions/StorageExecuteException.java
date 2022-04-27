package fr.milekat.hostmanager.storage.exeptions;

public class StorageExecuteException extends Exception {
    private final String executeState;

    /**
     * Issue during a storage execution
     */
    public StorageExecuteException(Throwable throwable, String executeState) {
        super(throwable);
        this.executeState = executeState;
    }

    /**
     * Get Execute State (If exist)
     */
    public String getExecuteState() {
        return executeState;
    }
}
