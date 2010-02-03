package cloudbase.core.tabletserver.log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cloudbase.core.Cloudbase;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;

public class BasicLogger extends TabletLog {
	private static final byte OPEN_EVENT = 1;
	private static final byte MUTATION_EVENT = 2;
	private static final byte COMPACTION_START_EVENT = 3;
	private static final byte COMPACTION_FINISH_EVENT = 4;

	private String logdir;
	private DataOutputStream out;
	private KeyExtent ke;

	static Logger log = Logger.getLogger(BasicLogger.class.getName());
	
	private int loggedMutationCount = 0;
	
	private int maxEventsToLogToOneFile = 500000;
	private ExecutorService threadPool;
	
	void setMaxEventsToLogToOneFile(int max){
		maxEventsToLogToOneFile = max;
	}
	
	private void deleteAllLogFiles(){
		File ld = new File(logdir);
		
		File[] files = ld.listFiles(new FileFilter(){
			public boolean accept(File pathname) {
				//TODO use reg exp instead of starts with to make it stronger
				return pathname.getName().startsWith(ke.toString()+"_");
			}}
		);
		
		int seqStart = (ke.toString()+"_").length();
		
		for (File file : files) {
			Integer.parseInt(file.getName().substring(seqStart,seqStart+7));
			log.debug("deleting "+file);
			file.delete();
		}
	}
	
	private ArrayList<Integer> getSequenceNumbers(){
		File ld = new File(logdir);
		
		File[] files = ld.listFiles(new FileFilter(){
			public boolean accept(File pathname) {
				//TODO use reg exp instead of starts with to make it stronger
				return pathname.getName().startsWith(ke.toString()+"_");
			}}
		);
		
		if(files==null || files.length == 0){
			return new ArrayList<Integer>();
		}
		
		ArrayList<Integer> sequenceNumbers = new ArrayList<Integer>();
		
		int seqStart = (ke.toString()+"_").length();
		
	
		for (File file : files) {
			int seq = Integer.parseInt(file.getName().substring(seqStart,seqStart+7));
			sequenceNumbers.add(seq);
		}
		
		Collections.sort(sequenceNumbers);
		
		return sequenceNumbers;
	}
	
	private File getLogFile(int seq){
		return new File(logdir+"/"+ke.toString()+"_"+String.format("%07d", seq)+".log");
	}
	
	public BasicLogger(KeyExtent ke, String logDir) {
		this.ke = ke;
		this.logdir = logDir;
		threadPool = Executors.newFixedThreadPool(1);
	}
	
	public synchronized void log(Mutation m) throws IOException{
		out.write(MUTATION_EVENT);
		//ke.write(out);
		m.write(out);
		out.flush();
		loggedMutationCount++;
		
		if(loggedMutationCount > maxEventsToLogToOneFile){
			startNewLogFile();
		}
	}

	public synchronized void minorCompactionStarted(String fullyQualifiedFileName) throws IOException{
		out.write(COMPACTION_START_EVENT);
		//ke.write(out);
		out.writeUTF(fullyQualifiedFileName);
		out.flush();
	}

	public synchronized void minorCompactionFinished(String fullyQualifiedFileName) throws IOException{
		out.write(COMPACTION_FINISH_EVENT);
		//ke.write(out);
		out.writeUTF(fullyQualifiedFileName);
		out.flush();
	}

	private DataInputStream openLogFile(File f) throws IOException{
		
		if(!f.exists()){
			log.warn("Log file does not exist "+f);
			return null;
		}
		
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
		
		int eventType = in.read();
		if(eventType != OPEN_EVENT){
			in.close();
			throw new IOException(f+" is not a properly formated log file");
		}else{
			String uid = in.readUTF();
			if(!uid.equals(Cloudbase.getInstanceID())){
				in.close();
				File moveFile = new File(f.getParent()+"/old_"+uid+"_"+f.getName());
				log.warn("Log "+f+" is for a different cloudbase instance, moving it to "+moveFile);
				in = null;
				
				f.renameTo(moveFile);
			}
		}
		
		return in;
	}
	
	private void recover(MutationReceiver mr) throws IOException {
		
		ArrayList<Integer> seqNums = getSequenceNumbers();
		
		//this loop will remove any log files that are no good
		for (Integer seq : seqNums) {
			DataInputStream in = openLogFile(getLogFile(seq));
			if(in != null){
				in.close();
			}
		}
		
		seqNums = getSequenceNumbers();
		if(seqNums.size() == 0){
			return;
		}
		
		long t1 = System.currentTimeMillis();
		log.info("Starting recovery for "+ke);
		
		String fqfnStart = null;
		String fqfnFinish = null;
		
		Map<String, Integer> startedCompactions = new HashMap<String, Integer>();
		Set<String> finishedCompactions = new HashSet<String>();
		
		long mutationCount = 0;
		long countAtLastStart = 0;
		
		int eventType;
		
		int seqIndex = 0;
		
		boolean sawFinishThatNeverStarted =  false;
		
		while(seqIndex < seqNums.size()){
			DataInputStream in = openLogFile(getLogFile(seqNums.get(seqIndex++)));
			//System.out.println("Opened : "+getLogFile(seqNums.get(seqIndex-1)));
			
			while((eventType = in.read()) >= 0){
				switch(eventType){
				case MUTATION_EVENT:
					Mutation m = new Mutation();
					m.readFields(in);
					mutationCount++;
					break;
				case COMPACTION_START_EVENT:
					fqfnStart = in.readUTF();
					//System.out.println("fqfnStart "+fqfnStart);
					
					int seen = 0;
					if(startedCompactions.containsKey(fqfnStart)){
						seen = startedCompactions.get(fqfnStart);
					}

					startedCompactions.put(fqfnStart, seen + 1);
					countAtLastStart = mutationCount;

					if(sawFinishThatNeverStarted){
						if(seqIndex == 1){
							//saw a start after the finish that never started, so assume changes were likely commited
							sawFinishThatNeverStarted = false;
						}else{
							log.error("First file had finish that never started and uncommited changes! "+ke);
							throw new IOException("First file had finish that never started and uncommited changes! "+ke);
						}
					}
					
					break;
				case COMPACTION_FINISH_EVENT:
					fqfnFinish = in.readUTF();
					//System.out.println("fqfnFinish "+fqfnFinish);
					
					if(fqfnStart == null && seqIndex == 1 && finishedCompactions.size() == 0 && startedCompactions.size() == 0){
						//this is the first file, a minor compaction spanned two files and the
						//previous file was deleted
						sawFinishThatNeverStarted = true;
						log.debug("Saw finish ("+fqfnFinish+") for "+ke+" that never started, assuming previous file was deleted");
					}else{
						if(!fqfnFinish.equals(fqfnStart)){
							log.error("previous start ("+fqfnStart+") not same as current finish ("+fqfnFinish+") for "+ke);
							throw new IOException("previous start ("+fqfnStart+") not same as current finish ("+fqfnFinish+") for "+ke);
						}

						mutationCount -= countAtLastStart;
					}
					
					if(finishedCompactions.contains(fqfnFinish)){
						log.warn("Saw finish compaction twice for "+ke+"");
					}
					
					finishedCompactions.add(fqfnFinish);
					
					break;
				}
			}
			in.close();
		}
		
		if(mutationCount == 0){
			//this log file contains not uncommitted changes, wack it
			log.info("All mutations in write ahead log for "+ke+" were minor compacted, deleting log...");
			//f.delete();
			deleteAllLogFiles();
			return;
		}
		
		//replay all mutations seen after the last start that finished
		String startReplayCompaction = fqfnFinish;
		int startCount = 0;
		
		boolean replay = false;
		if(startReplayCompaction == null){
			//no compaction starts seen in log, so replay all mutations from beginning
			replay = true;
		}
		
		log.info("Need to replay "+mutationCount+" mutations for "+ke);
		
		seqIndex = 0;
		while(seqIndex < seqNums.size()){
			DataInputStream in = openLogFile(getLogFile(seqNums.get(seqIndex++)));
			while((eventType = in.read()) >= 0){
				switch(eventType){
				case MUTATION_EVENT:
					Mutation m = new Mutation();
					m.readFields(in);
					if(replay){
						mr.receive(m);
					}
					break;
				case COMPACTION_START_EVENT:
					fqfnStart = in.readUTF();
					if(fqfnStart.equals(startReplayCompaction)){
						startCount++;
						if(startedCompactions.get(startReplayCompaction) == startCount){
							replay = true;
						}
					}
					break;
				case COMPACTION_FINISH_EVENT:
					fqfnFinish = in.readUTF();

					break;
				}
			}

			in.close();
		}
		
		long t2 = System.currentTimeMillis();
		String time = String.format("%6.2f", (t2 - t1)/1000.0);
		log.info("Finished recovery for "+ke+" in "+time+" seconds.");
	}

	private void open() throws IOException{
		ArrayList<Integer> seqNums = getSequenceNumbers();
		int seq = 0;
		if(seqNums.size() > 0){
			seq = seqNums.get(seqNums.size() - 1)+1;
		}
		
		File file = getLogFile(seq);
		
		boolean exist = file.exists();
		
		file.getParentFile().mkdirs();
		
		out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file, true)));
		if(!exist){
			out.write(OPEN_EVENT);
			out.writeUTF(Cloudbase.getInstanceID());
			out.flush();
		}
		
		loggedMutationCount = 0;
	}
	
	public synchronized void open(MutationReceiver mr) throws IOException {
		if(out != null){
			throw new IOException("Already open");
		}
		
		recover(mr);
		
		open();
	}
	
	public void close() throws IOException {
		synchronized(this){
			out.close();
			out = null;
			startGC();
		
			threadPool.shutdown();
		}
		
		while(!threadPool.isTerminated()){
			try {
				threadPool.awaitTermination(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	synchronized void startNewLogFile() throws IOException {
		out.close();
		open();
		
		startGC();
	}
	
	private volatile boolean gcRunning = false;
	
	private void startGC(){
		Runnable r = new Runnable(){
			public void run() {
				try {
					garbageCollect();
				} catch (IOException e) {
					log.warn("Log file garbage collection failed", e);
				}
			}
		};
		
		threadPool.submit(r);
	}
	
	void garbageCollect() throws IOException{
		
		synchronized (this) {
			while(gcRunning){
				try {
					this.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
			
			gcRunning = true;
		}
		
		try {
			_garbageCollect();
		} catch (IOException e) {
			throw e;
		}finally{
			synchronized (this) {
				gcRunning = false;
				this.notifyAll();
			}
		}
	}
	
	private void _garbageCollect() throws IOException{
		ArrayList<Integer> seqNums = getSequenceNumbers();
		
		//this loop will remove any log files that are no good
		for (Integer seq : seqNums) {
			DataInputStream in = openLogFile(getLogFile(seq));
			if(in != null){
				in.close();
			}
		}
		
		seqNums = getSequenceNumbers();
		if(seqNums.size() <= 1){
			return;
		}
		
		int seqIndex = 0;
		int eventType = 0;
		String fqfnStart = null;
		String fqfnFinish = null;
		
		int startIndex = -1;
		int finishedStartIndex = -1;
		
		int max = seqNums.size();
		if(out != null){
			max--;
		}
		
		if(max == 1){
			return;
		}
		
		int startCount = 0;
		int finishCount = 0;
		
		while(seqIndex < max){
			DataInputStream in = openLogFile(getLogFile(seqNums.get(seqIndex)));
			
			//System.out.println("GC considering "+getLogFile(seqNums.get(seqIndex)));
			
			while((eventType = in.read()) >= 0){
				switch(eventType){
				case MUTATION_EVENT:
					Mutation m = new Mutation();
					m.readFields(in);
					
					break;
				case COMPACTION_START_EVENT:
					fqfnStart = in.readUTF();
					
					//System.out.println("startIndex = "+seqIndex+" "+getLogFile(seqNums.get(seqIndex)));
					startIndex = seqIndex;
					
					startCount++;
					
					break;
				case COMPACTION_FINISH_EVENT:
					fqfnFinish = in.readUTF();

					if(fqfnStart == null && seqIndex == 0 && finishCount == 0 && startCount == 0){
						log.debug("Saw finish ("+fqfnFinish+") for "+ke+" that never started, assuming previous file was deleted");
					}else{
						if(!fqfnFinish.equals(fqfnStart)){
							System.err.println(seqIndex);
							log.error("previous start ("+fqfnStart+") not same as current finish ("+fqfnFinish+") for "+ke);
							throw new IOException("previous start ("+fqfnStart+") not same as current finish ("+fqfnFinish+") for "+ke);
						}
						
						//System.out.println("finishedStartIndex = "+startIndex);
						finishedStartIndex = startIndex;
					}
					
					finishCount++;
					break;
				}
			}

			seqIndex++;
			
			in.close();
		}
		
		if(finishedStartIndex != -1){
			//if a compaction successfully finished in file N
			//then we can remove up to N - 1
			
			for(int i = 0; i < finishedStartIndex; i++){
				File f2gc = getLogFile(seqNums.get(i));
				log.debug("Garbage collecting log file "+f2gc);
				f2gc.delete();
			}
		}
		
	}
	
	public static void printWriteAheadLog(String file) throws IOException{
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		
		int eventType;
		
		while((eventType = in.read()) >= 0){
			switch(eventType){
			case OPEN_EVENT :
				String uid = in.readUTF();
				System.out.println("OPEN_EVENT : "+uid);
				break;
			case MUTATION_EVENT:
				Mutation m = new Mutation();
				m.readFields(in);
				System.out.println("MUTATION_EVENT : "+m);
				break;
			case COMPACTION_START_EVENT:
				String fqfnStart = in.readUTF();
				System.out.println("COMPACTION_START_EVENT : "+fqfnStart);
				break;
			case COMPACTION_FINISH_EVENT:
				String fqfnFinish = in.readUTF();
				System.out.println("COMPACTION_FINISH_EVENT : "+fqfnFinish);
				break;
			default :
				System.out.println("ERROR : unknown event "+eventType+" QUITING");
				return;
			}
		}
		
		in.close();
	}
}
