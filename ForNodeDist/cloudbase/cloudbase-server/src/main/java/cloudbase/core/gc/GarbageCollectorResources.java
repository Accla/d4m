package cloudbase.core.gc;

public interface GarbageCollectorResources {
    void start() throws GarbageCollectionException;
    AllFileNames getAllFileNames() throws GarbageCollectionException;
    FileReferencer getFileReferencer() throws GarbageCollectionException;
    FileDeleter getFileDeleter() throws GarbageCollectionException;
}
