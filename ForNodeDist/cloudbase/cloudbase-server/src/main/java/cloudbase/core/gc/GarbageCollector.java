package cloudbase.core.gc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import cloudbase.core.Cloudbase;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.util.UtilWaitThread;


public class GarbageCollector implements Runnable {
	private static Logger log = Logger.getLogger(GarbageCollector.class.getName());
	private int gcDelay;
    private int roundsDelay;
    private double criticalMass;
    private volatile boolean stop = false;
    private boolean safemode = false;
    
    private GarbageCollectorResources resources;
    TreeMap<Path, Integer> mapsForDeletion = new TreeMap<Path,Integer>();
    
    public GarbageCollector(GarbageCollectorResources resources, 
                            int gcDelay, int roundsDelay, double criticalMass, boolean safemode) {
        this.resources = resources;
        this.gcDelay = gcDelay;
        this.roundsDelay = roundsDelay;
        this.criticalMass = criticalMass;
        this.safemode = safemode;
    }
    
    public void stop() {
        this.stop = true;
    }
    
    public void collect() throws GarbageCollectionException {
        // After the scan, forget any files up for deletion if they are missing
        resources.start();
        Set<Path> missing = new HashSet<Path>(mapsForDeletion.keySet());
        
        FileReferencer referenced = resources.getFileReferencer();
        AllFileNames fileLister = resources.getAllFileNames();
        FileDeleter deleter = resources.getFileDeleter();

        int total = 0;
        while (fileLister.hasNext()) {
            Path path = fileLister.next();
            total++;
            missing.remove(path);
            if (!referenced.isReferenced(path)) {
                if (mapsForDeletion.containsKey(path)) {
                    mapsForDeletion.put(path, mapsForDeletion.get(path) + 1);
                } else {
                    mapsForDeletion.put(path, 1);
                }
            } else {
                if (mapsForDeletion.containsKey(path)) {
                    log.warn("A candidate for deletion is now referenced by the metadata table: " + path);
                    mapsForDeletion.remove(path);
                }
            }
         
        }
        if (!safemode && total == 0) {
            log.error("No map files found: has cloudbase been initilized?");
            return;
        }
        int count = mapsForDeletion.size();
        if (count > 0) {
            log.debug("Tracking " + count + " files as candidates for deletion");
        }
        if (!safemode && (total > 10 && (count * 1.0 / total) >= criticalMass)) {
            log.warn(count + " candidates for deletion out of a total of " + total + ".  That exceed threshold of " + criticalMass + ", exiting");
            System.exit(666);
        }
        mapsForDeletion.entrySet().remove(missing);
        int deleted = 0;
        Iterator<Path> it = mapsForDeletion.keySet().iterator();
        if (safemode)
        	log.info("Listing all candidates for deletion");
        while (it.hasNext()) {
            Path p = it.next();
            if (safemode)
            	log.info(p);
            else if (mapsForDeletion.get(p) > roundsDelay) {
                log.debug("deleting " + p);
                deleter.delete(p);
                it.remove();
                deleted++;
            } 
        }
        if (safemode) {
        	log.info("End candidates for deletion");
        	System.exit(0);
        }
        if (deleted > 0) {
            log.info("Deleted " + deleted + " map files");
        }
    }
	
	public void run() {
	    while (!stop) {
	        try {
	            long start = System.currentTimeMillis();
	            collect();
	            long stop = System.currentTimeMillis();
	            log.info(String.format("Collect cycle took %.2f", ((stop - start) / 1000.) ));
            } catch (GarbageCollectionException ex) {
                log.warn("Exception occurred during garbage collection, skipping.", ex);
            } catch (Throwable t) {
                log.error(t);
                return;
            }
            UtilWaitThread.sleep(gcDelay);
        }
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, TableNotFoundException {
		Cloudbase.init("//conf//gc_logger.ini");
        CBConfiguration cbc = CBConfiguration.getInstance();
        int gcDelay = cbc.getInt("cloudbase.gc.mapfile.time.delay", 60) * 1000;
        int roundsDelay = cbc.getInt("cloudbase.gc.mapfile.rounds.delay", 10);
        if (roundsDelay < 1) {
            log.info("Too few rounds to wait for stable metadata");
            roundsDelay = 2;
        }
        double criticalMass = .9;
        log.info("time delay: " + gcDelay + " rounds delay: "+roundsDelay);
		
        boolean safeMode = false;
		if (args.length > 0) {
			criticalMass = Double.parseDouble(args[0]);
			if(args.length > 1 && args[1].toUpperCase().equals("SAFEMODE"))
                safeMode = true;
        }
		FileSystem  fs = FileSystem.get(new Configuration());
	    DefaultGCResources defaults = new DefaultGCResources(fs, new Path("/"));
	    GarbageCollectorResources resources = defaults;
	    if (cbc.getBoolean("cloudbase.gc.useSnapshots", false)) {
	        try {
	            resources = new SnapGCResources(defaults);
	        } catch (Throwable ex) {
	            log.error("Unable to use SnapGCResources", ex);
	        }
	        log.info("Using SnapGCResources to garbage collect");
        }
        GarbageCollector gc = new GarbageCollector(resources, gcDelay, roundsDelay, criticalMass, safeMode);
		gc.run();
	}
}
