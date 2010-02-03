package cloudbase.core.gc;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Value;
import cloudbase.core.util.UtilWaitThread;

public class GarbageCollectorTest extends TestCase {
    
    class MyGarbageResources extends DefaultGCResources {
        
        String workingDirectory;
        
        Iterable<Entry<Key, Value>> scan = null;
        
        MyGarbageResources(FileSystem fileSystem, String[] initialFiles, Iterable<Entry<Key, Value>> scan) throws IOException {
            super(fileSystem, fileSystem.getWorkingDirectory());
            workingDirectory = fileSystem.getWorkingDirectory().toString();
            
            this.scan = scan;

            fileSystem.delete(new Path("cloudbase"), true);
            for (String file : initialFiles)
                assertTrue(fileSystem.mkdirs(new Path(file)));

        }
        
        class Relative extends MyFileNamesToDelete {
            public String relative() { return workingDirectory; }
            
            Relative() throws GarbageCollectionException {
                super();
            }
        }
        
        public AllFileNames getAllFileNames() throws GarbageCollectionException {
            return new Relative();
        }

        
        public FileReferencer getFileReferencer() throws GarbageCollectionException {
            return new MyFileReferencer(workingDirectory + CBConstants.TABLES_DIR, scan);
        }
        
    }
    
    void recurse(FileSystem fs, Path root, Set<String> set) throws IOException {
        FileStatus[] parts = fs.listStatus(root);
        if (parts.length > 0) {
            for (FileStatus part : parts) {
                recurse(fs, part.getPath(), set);
            }
        } else {
            set.add(root.toString());
        }
    }
    
    Set<String> getFiles(FileSystem fs) throws IOException {
        Set<String> result = new TreeSet<String>();
        recurse(fs, new Path(fs.getWorkingDirectory(), "cloudbase"), result);
        return result;
    }
    
    boolean equals(FileSystem fs, String[] files) throws IOException {
        return getFiles(fs).equals(new TreeSet<String>(Arrays.asList(files)));
    }

    GarbageCollector makeGC(FileSystem fs, int rounds, String[] allFiles, Iterable<Entry<Key, Value>> metaData) throws Exception {
        MyGarbageResources resources = new MyGarbageResources(fs, allFiles, metaData);
        assertTrue(equals(fs, allFiles));
        return new GarbageCollector(resources, 1000, rounds, .9, false); 
    }
 
    FileSystem fs;
    String[] allFiles;
    Map<Key, Value> metaData = new TreeMap<Key, Value>();
    String[] collected;
    
    private static Key key(String row, String cf, String cq) {
        return new Key(new Text(row), 
                        new Text(cf),
                        new Text(cq));
    }
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.getLogger("cloudbase.core.gc").setLevel(Level.FATAL);
        Logger.getLogger("cloudbase.core.conf.CBConfiguration").setLevel(Level.ERROR);
        fs = FileSystem.getLocal(new Configuration());
        String base = fs.getWorkingDirectory().toString();
        String rootTablet = base + CBConstants.ROOT_TABLET_DIR + "/map_00000_00000";
        String exampleBulkMapFile = base + "/cloudbase/tables/xyzzy/bulk_010101/processing_proc_mapdir";
        
        String[] allFilesLocal = 
        {
            base + "/cloudbase/tables/table1/tablet1/map_00000_00000",
            base + "/cloudbase/tables/table1/tablet1/map_00000_00001",
            base + "/cloudbase/tables/table1/tablet1/map_00000_00002",
            base + "/cloudbase/tables/table1/tablet1/map_00000_00002_tmp",
            exampleBulkMapFile,
            rootTablet
        };
        allFiles = allFilesLocal;
        Key prevRow = key("table1<", "metadata", "prevrow");
        metaData.put(prevRow, KeyExtent.encodePrevEndRow(null));
        Key sstable = key("table1<", "sstables", "/tablet1/map_00000_00002");
        metaData.put(sstable, new Value("abc".getBytes()));
        
        String[] collectedLocal =
        {
         base + "/cloudbase/tables/table1/tablet1/map_00000_00002",
         base + "/cloudbase/tables/table1/tablet1/map_00000_00002_tmp",
         exampleBulkMapFile,
         rootTablet
        };

        collected = collectedLocal; 
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        fs.delete(new Path(fs.getWorkingDirectory(), "cloudbase"), true);
    }

    public void testMetaDataScanError() throws Exception {
        Map<Key, Value> badMeta = new TreeMap<Key, Value>();
        badMeta.putAll(metaData);
        badMeta.put(key("table1<", "metadata", "prevrow"), new Value("table1<".getBytes()));
        MyGarbageResources resources = new MyGarbageResources(fs, allFiles, badMeta.entrySet());
        assertTrue(equals(fs, allFiles));
        GarbageCollector gc = new GarbageCollector(resources, 1000, 0, .9, false); 
        Thread t = new Thread(gc);
        t.start();
        UtilWaitThread.sleep(1000);
        gc.stop();
        t.join();
        assertTrue(equals(fs, allFiles));
    }

    public void testBasic() throws Exception {
        GarbageCollector gc = makeGC(fs, 0, allFiles, metaData.entrySet());
        assertTrue(equals(fs, allFiles));
        gc.collect();
        assertTrue(equals(fs, collected));
    }

    public void testRounds() throws Exception {
        GarbageCollector gc = makeGC(fs, 3, allFiles, metaData.entrySet());
        for (int i = 0; i < 3; i++) {
            gc.collect();
            assertTrue(equals(fs, allFiles));
        }
        gc.collect();
        assertTrue(equals(fs, collected));
    }
    
    public void testMapNameFilter() {
        Path testPath= new Path("hdfs://cloud1:6093/cloudbase/tables/!METADATA/root_tablet/map_00000_00000");
        SSTableFilter filter = new SSTableFilter(new Path("/"));
        assertFalse(filter.accept(testPath));
    }
    public void testRun() throws Exception {
        GarbageCollector gc = makeGC(fs, 1, allFiles, metaData.entrySet());
        Thread thread = new Thread(gc, "Running Garbage Collector");
        thread.start();
        UtilWaitThread.sleep(100);
        assertTrue(equals(fs, allFiles));
        UtilWaitThread.sleep(1500);
        assertTrue(equals(fs, collected));
        gc.stop();
        thread.join();   
    }
    
    public void testSSTableFilter() {
        Path good = new Path("/cloudbase/tables/table/AAAAAA/map_00007_00001");
        Path bad = new Path("/cloudbase/tables/table/AAAAAA/map_00007_00001_tmp");
        SSTableFilter filter = new SSTableFilter(new Path("/"));
        assertFalse(filter.accept(bad));
        assertTrue(filter.accept(good));
        
    }
    
}
