package cloudbase.core.gc;

@SuppressWarnings("serial")
public class GarbageCollectionException extends Exception {
    GarbageCollectionException(String why) { super(why); }
    GarbageCollectionException(String why, Throwable e) { super(why, e); }
    GarbageCollectionException(Throwable e) { super(e); }
}
