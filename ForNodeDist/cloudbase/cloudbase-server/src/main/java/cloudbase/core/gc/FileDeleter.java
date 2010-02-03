package cloudbase.core.gc;

import org.apache.hadoop.fs.Path;

public interface FileDeleter {
    void delete(Path path);
}
