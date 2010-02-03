package cloudbase.core.gc;

import org.apache.hadoop.fs.Path;

public interface AllFileNames {
    boolean hasNext() throws GarbageCollectionException;
    Path next() throws GarbageCollectionException;
}
