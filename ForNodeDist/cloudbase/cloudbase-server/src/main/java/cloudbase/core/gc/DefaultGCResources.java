package cloudbase.core.gc;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.ScannerImpl;
import cloudbase.core.client.mapreduce.bulk.BulkOperations;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.zookeeper.ZooConstants;

/*
 * While cloudbase is writing out new map files it uses the _tmp suffix.
 * When finished writing data to a file, it does the following steps.
 *   1 add new file to metadata table
 *   2 rename file (removing _tmp suffix)
 *   
 * The order of the steps above is important, if reversed there is a small
 * time window where the garbage collector could delete a new file.  
 */

public class DefaultGCResources implements GarbageCollectorResources {
    private static Logger log = Logger.getLogger(DefaultGCResources.class.getName());
    private FileSystem fs;
    private SSTableFilter filt;

    private final Path NO_MORE_FILES = new Path("//");

    private ScannerImpl mdScanner = null;

    public DefaultGCResources(FileSystem fs, Path root) throws IOException {
        this.fs = fs;
        this.filt = new SSTableFilter(root);
    }

    @Override
    public void start() throws GarbageCollectionException {
    }
    

    public class MyFileNamesToDelete implements AllFileNames, Runnable {

        ArrayBlockingQueue<Path> nextMapFile = new ArrayBlockingQueue<Path>(1000);

        private volatile GarbageCollectionException error = null;

        Path next = null;

        MyFileNamesToDelete() throws GarbageCollectionException {
            Thread thread = new Thread(this, "Map File Lister Thread");
            thread.start();

        }

        public FileSystem fileSystem() {
            return fs;
        }

        public String relative() {
            return "";
        }

        @Override
        public boolean hasNext() throws GarbageCollectionException {
            if (error != null) throw error;
            try {
                next = nextMapFile.take();
            } catch (Exception ex) {
                throw new GarbageCollectionException(ex);
            }               
            if (error != null) throw error;
            return next != NO_MORE_FILES;

        }

        @Override
        public Path next() {
            Path result = next;
            next = null;
            return result;
        }

        public void run() {
            try {
                TreeSet<Path> processingFiles = new TreeSet<Path>();

                // Some exception to normal tablets
                FileStatus[] procFiles;
                procFiles = fs.globStatus(new Path(relative() + CBConstants.TABLES_DIR+"/*/bulk_*/processing_proc_*"));
                for(FileStatus f : procFiles){
                    processingFiles.add(f.getPath().getParent());
                }

                // All the map files
                FileStatus[] tables = fs.listStatus(new Path(relative() + CBConstants.TABLES_DIR));
                for (FileStatus table : tables) {
                    FileStatus[] tablets = fs.listStatus(table.getPath());
                    for (FileStatus tablet : tablets) {
                        if(!processingFiles.contains(tablet.getPath())) {
                            FileStatus[] maps = fs.listStatus(tablet.getPath(), filt);
                            for (FileStatus map : maps) {
                                nextMapFile.put(map.getPath());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                error = new GarbageCollectionException(e);
            } finally {
                try {
                    nextMapFile.put(NO_MORE_FILES);
                } catch (InterruptedException e) {
                    log.error("Unexpected exception", e);
                }
            }
        }

    }

    public class MyFileDeleter implements FileDeleter {

        MyFileDeleter() {
        }

        @Override
        public void delete(Path path) {
            try {
                fs.delete(path, true);
            } catch (IOException e) {
                log.error("Unable to delete file " + path, e);
            }
        }

    }

    public class MyFileReferencer implements FileReferencer {

        HashSet<Path> inUse = new HashSet<Path>();
        HashSet<String> badTables = new HashSet<String>();

        MyFileReferencer(String tabletsDir, Iterable<Entry<Key, Value>> mdScanner) throws GarbageCollectionException {
            try {
            	Text colf = new Text();
                Text colq = new Text();
                HashMap<String, SortedSet<KeyExtent>> tablets = new HashMap<String, SortedSet<KeyExtent>>();

                for (Entry<Key,Value> entry : mdScanner) {
                    Text table = new Text(KeyExtent.tableOfMetadataRow(entry.getKey().getRow()));
                    
                    colf = entry.getKey().getColumnFamily(colf);
                    colq = entry.getKey().getColumnQualifier(colq);

                    if(colf.equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) &&
                    	colq.equals(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME)){
                        KeyExtent tabletKe = new KeyExtent(entry.getKey().getRow(), entry.getValue());
                        SortedSet<KeyExtent> extents = tablets.get(tabletKe.getTableName().toString());
                        if (extents == null) {
                            extents = new TreeSet<KeyExtent>();
                            tablets.put(tabletKe.getTableName().toString(), extents);
                        }
                        extents.add(tabletKe);
                    }
                    else {
                        Path p = new Path(tabletsDir + "/" + table.toString() + entry.getKey().getColumnQualifier().toString());
                        p = p.makeQualified(fs);
                        if (!fs.exists(p)) {
                            log.warn("METADATA table references a map file that no longer exists: " + p.toString());
                        }
                        inUse.add(p);
                        
                    }
                }

                for (Entry<String, SortedSet<KeyExtent>> entry : tablets.entrySet()) {
                    try {
                    	BulkOperations.validateMetadataEntries(entry.getKey(), entry.getValue());
                    } catch (Exception ex) {
                        badTables.add(entry.getKey());
                        log.warn("Bad extents found for table: " + entry.getKey());
                    }
                }
            }
            catch (Exception e) {
                log.warn("CB down, skipping garbage collect",e);
                throw new GarbageCollectionException(e);
            }


        }

        @Override
        public boolean isReferenced(Path path) {
            if (badTables.contains(path.getParent().getParent().getName())) {
                return true;
            }
            return inUse.contains(path);
        }

    }


    @Override
    public FileDeleter getFileDeleter() {
        return new MyFileDeleter();
    }

    @Override
    public FileReferencer getFileReferencer() throws GarbageCollectionException  {

        while (mdScanner==null) {
            try {
                mdScanner = new ScannerImpl(new HdfsZooInstance(), new AuthInfo(ZooConstants.SYSTEM_USERNAME, ZooConstants.SYSTEM_PASSWORD), CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);
                mdScanner.fetchColumn(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME);
                mdScanner.fetchColumnFamily(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY);
            } catch (TableNotFoundException e) {
                log.error("No Metadata table?  CB must be down", e);
                throw new GarbageCollectionException(e);
            } catch (CBException e) {
                log.error("Communication error or general cloudbase failure", e);
                throw new GarbageCollectionException(e);
			} catch (CBSecurityException e) {
                log.error("Security Violation", e);
                throw new GarbageCollectionException(e);
			}
        }
        mdScanner.setRange(new Range(new Key(), null));
        return new MyFileReferencer(CBConstants.TABLES_DIR, mdScanner);
    }

    @Override
    public AllFileNames getAllFileNames() throws GarbageCollectionException {
        return new MyFileNamesToDelete();
    }

    public FileSystem getFileSystem() {
        return fs;
    }

}
