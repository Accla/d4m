package cloudbase.core.gc;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.conf.CBConfiguration;

public class SnapGCResources implements GarbageCollectorResources {
    private static Logger log = Logger.getLogger(SnapGCResources.class);
    
    long time;
    DefaultGCResources resources;

    String randomName;
    static final Text BAD = new Text("BAD");

    SnapGCResources(DefaultGCResources resources) throws IOException, GarbageCollectionException {
        this.resources = resources;
        this.randomName = UUID.randomUUID().toString();
        log.info("Garbage collector snapshots in " + getBaseFileName());
    }
    
    public void start() throws GarbageCollectionException {
        resources.start();
        try {
            this.time = System.currentTimeMillis();
            SequenceFile.Writer writer = 
                SequenceFile.createWriter(resources.getFileSystem(), 
                                          new Configuration(),
                                          getHdfsFileName(), 
                                          Text.class, 
                                          Text.class,
                                          CompressionType.BLOCK);
            final Text empty = new Text(new byte[0]);
            Text filename = new Text();
            AllFileNames names = resources.getAllFileNames();
            while (names.hasNext()) {
                filename.set(names.next().toString());
                writer.append(filename, empty);
            }
            writer.close();
            DefaultGCResources.MyFileReferencer refs = 
                (DefaultGCResources.MyFileReferencer)resources.getFileReferencer();
            writer = SequenceFile.createWriter(resources.getFileSystem(),
                                               new Configuration(),
                                               getMetaFileName(),
                                               Text.class,
                                               Text.class,
                                               CompressionType.BLOCK);
            for (Path path : refs.inUse) {
                filename.set(path.toString());
                writer.append(filename, empty);
            }
            for (String table : refs.badTables) {
                filename.set(table);
                writer.append(table, BAD);
            }
            writer.close();
        } catch (IOException e) {
            throw new GarbageCollectionException(e);
        }
    }
    
    String getBaseFileName() {
        return CBConstants.BASE_DIR + "/gc/" + randomName; 
    }
    
    Path makePath(String which) {
        return new Path(getBaseFileName() + "/" + which + "." + String.format("%014d", time));
    }
    
    Path getHdfsFileName() {
        return makePath("hdfs");
    }
    
    Path getMetaFileName() {
        return makePath("meta");
    }
    
    FileSystem getFileSystem() {
        return resources.getFileSystem();
    }
    
    class SnapAllNames implements AllFileNames {
        
        SequenceFile.Reader reader;
        Text next = new Text();
        boolean hasNext = true;
        
        SnapAllNames(Path path) throws GarbageCollectionException {
            try {
                reader = new SequenceFile.Reader(getFileSystem(), 
                                                 path, 
                                                 new Configuration());
                hasNext = reader.next(next);
            } catch (IOException e) {
                throw new GarbageCollectionException(e);
            }
        }
        

        @Override
        public boolean hasNext() throws GarbageCollectionException {
            return hasNext;
        }

        @Override
        public Path next() throws GarbageCollectionException {
            Path result = new Path(next.toString());
            try {
                hasNext = reader.next(next);
            } catch (IOException e) {
                throw new GarbageCollectionException(e);
            }
            return result;
        }
    }
    
    
    @Override
    public AllFileNames getAllFileNames() throws GarbageCollectionException {
        return new SnapAllNames(getHdfsFileName());
    }

    @Override
    public FileDeleter getFileDeleter() throws GarbageCollectionException {
        return resources.getFileDeleter();
    }
    
    class SnapFileReferencer implements FileReferencer {
        
        Set<Path> refs = new HashSet<Path>();
        Set<Path> badTables = new HashSet<Path>();
        
        SnapFileReferencer(Path sequenceFile) throws GarbageCollectionException {
            try {
                SequenceFile.Reader reader = new SequenceFile.Reader(getFileSystem(),
                                                                     sequenceFile,
                                                                     new Configuration());
                Text path = new Text();
                Text bad = new Text();
                while (reader.next(path, bad)) {
                    if (bad.compareTo(BAD) != 0) {
                        refs.add(new Path(path.toString()));
                    } else {
                        badTables.add(new Path(path.toString()));
                    }
                }
                
            } catch (IOException e) {
                throw new GarbageCollectionException(e);
            }
        }
        

        @Override
        public boolean isReferenced(Path path) {
            if (badTables.contains(path.getParent().getParent().getName())) {
                return true;
            }
            return refs.contains(path);
        }
    }
    
    

    @Override
    public FileReferencer getFileReferencer() throws GarbageCollectionException {
        return new SnapFileReferencer(getMetaFileName());
    }
    
    static class PlaybackGCResources extends SnapGCResources {

        FileSystem fs;
        
        PlaybackGCResources(FileSystem fs, String uuid) throws IOException,
                GarbageCollectionException {
            super(null);
            this.fs = fs;
            this.randomName = uuid;
        }
    
        void next(long time) {
            this.time = time;
        }
        
        FileSystem getFileSystem() {
            return fs;
        }
        
        public void start() {}
        
        public class DebugFileDeleter implements FileDeleter {
            @Override
            public void delete(Path path) {
                System.out.println(String.format("At time %d deleting %s",
                                                 time,
                                                 path.toString()));
            }
        }

        @Override
        public FileDeleter getFileDeleter() throws GarbageCollectionException {
            return new DebugFileDeleter();
        }
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: SnapGCResources " + CBConstants.BASE_DIR + "/<location> <critical>");
            System.exit(1);
        }
        Cloudbase.init("//conf//gc_logger.ini");
        FileSystem fs = FileSystem.get(new Configuration());
        
        Path snaps = new Path(args[0]);
        if (!fs.exists(snaps)) {
            System.err.println("Usage: unable to find " + snaps);
            System.exit(1);
        }

        CBConfiguration cbc = CBConfiguration.getInstance();
        int gcDelay = cbc.getInt("cloudbase.gc.mapfile.time.delay", 60) * 1000;
        int roundsDelay = cbc.getInt("cloudbase.gc.mapfile.rounds.delay", 10);
        if (roundsDelay < 1) {
            log.info("Too few rounds to wait for stable metadata");
            roundsDelay = 2;
        }
        double criticalMass = .9;
        log.info("time delay: " + gcDelay + " rounds delay: "+roundsDelay);
        if (args.length > 1) {
            criticalMass = Double.parseDouble(args[1]);
        }
        
        FileStatus[] files = fs.listStatus(snaps);
        Arrays.sort(files);
        PlaybackGCResources resources = new PlaybackGCResources(fs, snaps.getName());
        GarbageCollector gc = new GarbageCollector(resources, 0, roundsDelay, criticalMass, false);
        for (FileStatus file : files) {
            Path hdfs = file.getPath();
            if (hdfs.getName().startsWith("hdfs")) {
                Path meta = new Path(hdfs.getParent(),
                                     hdfs.getName().replace("hdfs", "meta"));
                if (!fs.exists(meta)) {
                    System.err.println("Unable to find matching meta file for hdfs file: " + hdfs);
                    System.exit(1);
                }
                long time = Long.parseLong(meta.getName().substring(5));
                System.out.println("Simulating collect at: " + time);
                resources.next(time);
                gc.collect();
            }
        }
        System.exit(0);
    }

}
