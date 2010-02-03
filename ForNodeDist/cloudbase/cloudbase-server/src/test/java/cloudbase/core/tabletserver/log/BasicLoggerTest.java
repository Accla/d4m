package cloudbase.core.tabletserver.log;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cloudbase.core.Cloudbase;
import cloudbase.core.data.ColumnUpdate;
import cloudbase.core.data.Value;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.tabletserver.log.BasicLogger;
import cloudbase.core.tabletserver.log.MutationReceiver;
import cloudbase.core.tabletserver.log.TabletLog;

import junit.framework.TestCase;



public class BasicLoggerTest extends TestCase{

	private File testDir = new File("/tmp/walogTest");
	
	private void delete(File f){
		
		if(!f.exists())
			return;
		
		if(f.isDirectory()){
			File[] files = f.listFiles();
			for (File file : files) {
				delete(file);
			}
		}
		
		f.delete();
		//System.out.println("Deleted "+f);
	}
	
	private static int test_run = 1;
	
	protected void setUp() throws InterruptedException{
		
		if(test_run == 1) delete(testDir);
		testDir.mkdirs();
		
		Cloudbase.setInstance(""+(test_run++));
		
		Logger.getLogger("cloudbase.core.tabletserver.log").setLevel(Level.ERROR);
	}
	
	private void playMutations(TabletLog tl, String events[][]) throws IOException{
		for (String[] event : events) {
			Mutation m1 = new Mutation(new Text(event[0]));
			String[] sa = event[1].split(":");
			m1.put(new Text(sa[0]), new Text(sa[1]), Long.parseLong(event[3]), new Value(event[2].getBytes()));
			
			tl.log(m1);
		}
	}
	
	private void checkMutations(Collection<String[][]> mutationsExpected, ArrayList<Mutation> mutationsReceived) {
		
		HashSet<String> mutationsR = new HashSet<String>();
		
		for (Mutation mutation : mutationsReceived) {
			Collection<ColumnUpdate> cvps = mutation.getUpdates();
			for (ColumnUpdate cvp : cvps) {
				String entry = new String(mutation.getRow())+":"+new String(cvp.getColumnFamily())+":"+new String(cvp.getColumnQualifier())+":"+new String(cvp.getValue())+":"+cvp.getTimestamp();
				assertFalse(mutationsR.contains(entry));
				mutationsR.add(entry);
			}
		}
		
		HashSet<String> mutationsE = new HashSet<String>();
		
		for (String[][] muts : mutationsExpected) {
			for (String[] mutdata : muts) {
				String entry = mutdata[0]+":"+mutdata[1]+":"+mutdata[2]+":"+mutdata[3];
				assertFalse(mutationsE.contains(entry));
				mutationsE.add(entry);
			}
		}
		
		
		assertTrue(mutationsR.containsAll(mutationsE));
		assertTrue(mutationsE.containsAll(mutationsR));
		assertTrue(mutationsE.size() == mutationsR.size());
		
	}
	
	private void checkMutations(String[][] mutationsExpected, ArrayList<Mutation> mutationsReceived) {
		checkMutations(Collections.singletonList(mutationsExpected), mutationsReceived);
	}
	
	public void test1() throws IOException{
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		checkMutations(mutations1, mutationsReceived);
	}
	
	public void test2() throws IOException{
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");
		
		String mutations2[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
		
		playMutations(bl, mutations2);
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations1);
		allMuts.add(mutations2);
		checkMutations(allMuts, mutationsReceived);
	}

	public void test3() throws IOException{
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");
		
		String mutations2[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
		
		playMutations(bl, mutations2);
		
		bl.minorCompactionFinished("minC_1");
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations2);
		checkMutations(allMuts, mutationsReceived);
	}
	
	public void test4() throws IOException{
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");
		
		String mutations2[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
		
		playMutations(bl, mutations2);
		
		bl.minorCompactionFinished("minC_1");
		
		String mutations3[][] = new String[][]{
			new String[]{"row_05","cf_05:cq_05","val_05","1"}
		};
			
		playMutations(bl, mutations3);
		
		bl.minorCompactionStarted("minC_2");
		
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations2);
		allMuts.add(mutations3);
		checkMutations(allMuts, mutationsReceived);
	}

	public void test5() throws IOException{
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");
		
		String mutations2[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
		
		playMutations(bl, mutations2);
		
		bl.minorCompactionFinished("minC_1");
		
		String mutations3[][] = new String[][]{
			new String[]{"row_05","cf_05:cq_05","val_05","1"}
		};
			
		playMutations(bl, mutations3);
		
		bl.minorCompactionStarted("minC_2");
		bl.minorCompactionFinished("minC_2");
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		checkMutations(allMuts, mutationsReceived);
	}

	public void test6() throws IOException{
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");
		
		String mutations2[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
		
		playMutations(bl, mutations2);
		
		bl.minorCompactionFinished("minC_1");
		
		String mutations3[][] = new String[][]{
			new String[]{"row_05","cf_05:cq_05","val_05","1"}
		};
			
		playMutations(bl, mutations3);
		
		bl.minorCompactionStarted("minC_2");
		
		String mutations4[][] = new String[][]{
			new String[]{"row_06","cf_06:cq_06","val_06","1"}
		};
				
		playMutations(bl, mutations4);
		
		bl.minorCompactionFinished("minC_2");
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations4);
		checkMutations(allMuts, mutationsReceived);
	}

	public void test7() throws IOException{
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");
		
		String mutations2[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
		
		playMutations(bl, mutations2);
		
		bl.minorCompactionFinished("minC_1");
		
		String mutations3[][] = new String[][]{
			new String[]{"row_05","cf_05:cq_05","val_05","1"}
		};
			
		playMutations(bl, mutations3);
		
		bl.minorCompactionStarted("minC_2");
		bl.minorCompactionFinished("minC_2");
		
		String mutations4[][] = new String[][]{
			new String[]{"row_06","cf_06:cq_06","val_06","1"}
		};
				
		playMutations(bl, mutations4);
		
		
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations4);
		checkMutations(allMuts, mutationsReceived);
	}
	
	public void test8() throws IOException{		
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");
		
		String mutations2[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
		
		playMutations(bl, mutations2);
		
		//no finish assume server failed
		//bl.minorCompactionFinished("minC_1");
		
		String mutations3[][] = new String[][]{
			new String[]{"row_05","cf_05:cq_05","val_05","1"}
		};
			
		playMutations(bl, mutations3);
		
		bl.minorCompactionStarted("minC_2");
		
		String mutations4[][] = new String[][]{
			new String[]{"row_06","cf_06:cq_06","val_06","1"}
		};
				
		playMutations(bl, mutations4);
		
		//no finish assume server failed again
		//bl.minorCompactionFinished("minC_2");
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations1);
		allMuts.add(mutations2);
		allMuts.add(mutations3);
		allMuts.add(mutations4);
		checkMutations(allMuts, mutationsReceived);
	}

	public void test9() throws IOException{		
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");
		
		String mutations2[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
		
		playMutations(bl, mutations2);
		
		//no finish assume server failed
		//bl.minorCompactionFinished("minC_1");
		
		String mutations3[][] = new String[][]{
			new String[]{"row_05","cf_05:cq_05","val_05","1"}
		};
			
		playMutations(bl, mutations3);
		
		bl.minorCompactionStarted("minC_2");
		
		String mutations4[][] = new String[][]{
			new String[]{"row_06","cf_06:cq_06","val_06","1"}
		};
				
		playMutations(bl, mutations4);
		
		//no finish assume server failed again
		bl.minorCompactionFinished("minC_2");
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations4);
		checkMutations(allMuts, mutationsReceived);
	}
	
	
	public void test10() throws IOException{		
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");
		
		String mutations2[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
		
		playMutations(bl, mutations2);
		
		//no finish assume server failed
		//bl.minorCompactionFinished("minC_1");
		
		String mutations3[][] = new String[][]{
			new String[]{"row_05","cf_05:cq_05","val_05","1"}
		};
			
		playMutations(bl, mutations3);
		
		//assume tab server starts at minC_1 since minC_1 did not finish before
		bl.minorCompactionStarted("minC_1");
		
		String mutations4[][] = new String[][]{
			new String[]{"row_06","cf_06:cq_06","val_06","1"}
		};
				
		playMutations(bl, mutations4);
		
		bl.minorCompactionFinished("minC_1");
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations4);
		checkMutations(allMuts, mutationsReceived);
	}

	
	public void test11() throws IOException{		
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");
		
		String mutations2[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
		
		playMutations(bl, mutations2);
		
		//no finish assume server failed
		//bl.minorCompactionFinished("minC_1");
		
		String mutations3[][] = new String[][]{
			new String[]{"row_05","cf_05:cq_05","val_05","1"}
		};
			
		playMutations(bl, mutations3);
		
		//assume tab server starts at minC_1 since minC_1 did not finish before
		bl.minorCompactionStarted("minC_1");
		
		bl.startNewLogFile();
		
		String mutations4[][] = new String[][]{
			new String[]{"row_06","cf_06:cq_06","val_06","1"}
		};
				
		playMutations(bl, mutations4);
		
		bl.minorCompactionFinished("minC_1");
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations4);
		checkMutations(allMuts, mutationsReceived);
	}

	public void test12() throws IOException{	
		for(int ml = 1; ml < 8; ml++){
			
			if(ml > 1){
				try {
					setUp();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());

			bl.open(new MutationReceiver(){public void receive(Mutation m){}});
			bl.setMaxEventsToLogToOneFile(ml);

			String mutations1[][] = new String[][]{
					new String[]{"row_01","cf_01:cq_01","val_01","1"},
					new String[]{"row_02","cf_02:cq_02","val_02","1"},
					new String[]{"row_03","cf_03:cq_03","val_03","1"},
					new String[]{"row_04","cf_04:cq_04","val_04","1"},
					new String[]{"row_05","cf_05:cq_05","val_05","1"},
					new String[]{"row_06","cf_06:cq_06","val_06","1"}
			};

			playMutations(bl, mutations1);

			bl.close();

			bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());

			final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();

			bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});

			bl.close();


			ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
			allMuts.add(mutations1);
			checkMutations(allMuts, mutationsReceived);
		}
	}

	public void test13() throws IOException{		
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		bl.setMaxEventsToLogToOneFile(2);
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"},
			new String[]{"row_03","cf_03:cq_03","val_03","1"},
			new String[]{"row_04","cf_04:cq_04","val_04","1"},
			new String[]{"row_05","cf_05:cq_05","val_05","1"},
			new String[]{"row_06","cf_06:cq_06","val_06","1"}
		};
		
		playMutations(bl, mutations1);
		
		bl.minorCompactionStarted("minC_1");		
		bl.minorCompactionFinished("minC_1");
		
		String mutations4[][] = new String[][]{
			new String[]{"row_07","cf_07:cq_07","val_07","1"}
		};
		
		playMutations(bl, mutations4);
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations4);
		checkMutations(allMuts, mutationsReceived);
	}
	
	public void test14() throws IOException{
		
		/*
		 * This test excercise a problem observed in ticked 641.
		 * 
		 * walog file 1 had a finish with no start, this was causing an NPE
		 * 
		 * To repoduce this create a minor compaction that spans two walog files.
		 * Then complete a second minor compaction in the second log file. This
		 * will cause the first log file to be deleted (all its changes are 
		 * minor compacted).  The second log file will now contain a finish
		 * with no start.  This test creates that situation. 
		 *
		 */
		
		BasicLogger bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){}});
		bl.setMaxEventsToLogToOneFile(1000000); //will explicitly delete start new log files in this test
		
		String mutations1[][] = new String[][]{
			new String[]{"row_01","cf_01:cq_01","val_01","1"},
			new String[]{"row_02","cf_02:cq_02","val_02","1"}
		};
			
		playMutations(bl, mutations1);
				
		bl.minorCompactionStarted("minC_1");
		
		bl.startNewLogFile();
		
		String mutations2[][] = new String[][]{
			new String[]{"row_03","cf_03:cq_03","val_03","1"}
		};
			
		playMutations(bl, mutations2);
		
		
		
		bl.minorCompactionFinished("minC_1");
		
		bl.minorCompactionStarted("minC_2");
		
		String mutations3[][] = new String[][]{
			new String[]{"row_04","cf_04:cq_04","val_04","1"}
		};
				
		playMutations(bl, mutations3);
		
		bl.minorCompactionFinished("minC_2");
		
		String mutations4[][] = new String[][]{
			new String[]{"row_05","cf_05:cq_05","val_05","1"}
		};
				
		playMutations(bl, mutations4);
		
		bl.close();
		
		bl = new BasicLogger(new KeyExtent(new Text("!METADATA"), null, null), testDir.getAbsolutePath());
		
		final ArrayList<Mutation> mutationsReceived = new ArrayList<Mutation>();
		
		bl.open(new MutationReceiver(){public void receive(Mutation m){mutationsReceived.add(m);}});
		
		bl.close();
		
		ArrayList<String[][]> allMuts = new ArrayList<String[][]>();
		allMuts.add(mutations3);
		allMuts.add(mutations4);
		checkMutations(allMuts, mutationsReceived);
	}
	
	
	protected void tearDown(){
		//delete(testDir);
	}

}
