package cloudbase.core.gc;

import org.apache.hadoop.fs.Path;

public interface FileReferencer {
    boolean isReferenced(Path path);
}
