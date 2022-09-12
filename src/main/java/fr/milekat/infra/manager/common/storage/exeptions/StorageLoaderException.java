package fr.milekat.infra.manager.common.storage.exeptions;

public class StorageLoaderException extends Throwable {
    public StorageLoaderException(String errorMessage) {
        super(errorMessage);
    }
}
