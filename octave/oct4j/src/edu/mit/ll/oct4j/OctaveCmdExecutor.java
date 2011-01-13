package edu.mit.ll.oct4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class OctaveCmdExecutor {
	private static Logger log = Logger.getLogger(OctaveCmdExecutor.class.getName());
	private Process process=null;
	private BufferedReader readProc=null;
	private Writer writeProc=null;
	private int NUM_POOL=2;
	private ExecutorService execThreadPool = 
		Executors.newFixedThreadPool(NUM_POOL,new OctThreadFactory("D4M"));

	private String [] CMD_ARGS = {null,"--silent","--no-line-editing","--no-init-file","--no-history","--traditional"};

	private BaseOctDataObject result=null;

	private Writer errorWriter = new OutputStreamWriter(System.err);
	private Thread errorThread=null;

	/**
	 * @param programFile   full path to program executable
	 * @param workDir   current working directory
	 */
	public OctaveCmdExecutor(String programFile, String workDir) {
		// TODO Auto-generated constructor stub
		// Create Process
		// Create the BufferedRead from InputStream (stdout)
		// Create the Writer from the Outputstream  (stdin)
		if(programFile != null)
			CMD_ARGS[0] = programFile;
		ProcessBuilder pb = new ProcessBuilder(CMD_ARGS);
		pb.directory(new File(workDir));

		try {
			this.process = pb.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//stdout
		//this.readProc = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("Latin1")));
		this.readProc = new BufferedReader(new InputStreamReader(process.getInputStream()));
		//stdin
		this.writeProc = new  OutputStreamWriter(this.process.getOutputStream());

		//stderr
		this.errorThread = new Thread(new ErrorStreamRunnable(new InputStreamReader(process.getErrorStream())));

	}


	/**
	 * Execute the command and assign the output to the variable
	 * Return the result?
	 * @param variableName  variable containing the output of the executed command
	 * @param cmd   command to execute
	 */
	public void exec(String variableName, String cmd) {


	}

	public void exec(String cmd) {

	}
	public void exec(WriteTaskIF writeTask, ReadTaskIF readTask) {
		String ending = getEnding();
		final Future<Void> wFuture = 
			execThreadPool.submit(new CallableWriteData( writeTask,this.writeProc, ending));
		final Future<Void> rFuture = 
			execThreadPool.submit(new CallableReadData(readTask, this.readProc, ending));

		//Write future
		try {
			wFuture.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CancellationException e) {
			rFuture.cancel(true);
		}
		catch (RuntimeException e) {
			shutdown();
		}


		//Read future
		try {
			if(!rFuture.isCancelled())
				rFuture.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (RuntimeException e) {
			shutdown();
		}

	}

	public BaseOctDataObject get(String varName) {
		this.result = null;
		if(variableExist(varName))  {
			WriteTaskIF writeTask = 
				new Read2WriteTask(new StringReader("save -text - " + varName+"\n"));
			DataReadTask readTask = new DataReadTask(varName);
			log.info("GO GET -->"+varName);
			exec(writeTask, readTask);
			this.result = readTask.getData();
		}
		return getResult();
	}

	public BaseOctDataObject getResult() {
		return result;
	}

	public void setResult(BaseOctDataObject result) {
		this.result = result;
	}


	public String getEnding() {
		return  "-=+X+=- oct4j_filler-"+Long.toString(System.currentTimeMillis())+" -=+X+=-";
	}

	public void shutdown() {
		//"Hardkill"
		//close the writer, reader
		//shutdown the ThreadPool

		this.execThreadPool.shutdownNow();
		this.process.destroy();
		killErrorThread();

	}
	public void quit() {
		//Exit octave process
		try {
			this.writeProc.write("exit\n");
			this.writeProc.close();

			killErrorThread();

			String line = this.readProc.readLine();
			if(line != null) {
				log.fine("READER: last line when exiting - "+line);
			}
			this.readProc.close();
			
			//shutdown octave process
			int exitval=0;
			try {
				exitval=this.process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(exitval != 0) {
				log.fine("BAD- Octave process exit problem - EXIT_VALUE="+exitval);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			this.execThreadPool.shutdown();
		}
	}

	private void killErrorThread() {
		this.errorThread.interrupt();
		try {
			this.errorThread.join();
		} catch (InterruptedException e) {
			log.fine(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	public void set(Map<String,BaseOctDataObject> datamap) {
		StringWriter outWriter = new StringWriter();
		exec(new DataWriteTask(datamap), new Write2ReadTask(outWriter));
		String output = outWriter.toString();
		if (output.length() != 0) {
			throw new IllegalStateException("Unexpected output, " + output);
		}

	}
	public void set(String varname, String value) {
		StringDO sdo = new StringDO(value);
		Map<String,BaseOctDataObject> datamap = new HashMap<String,BaseOctDataObject>();
		datamap.put(varname, sdo);
		set(datamap);
	}

	class ErrorStreamRunnable implements Runnable {
		private Logger log = Logger.getLogger(ErrorStreamRunnable.class.getName());
		/**
		 * @param reader
		 */
		public ErrorStreamRunnable(Reader reader) {
			super();
			this.reader = reader;
		}

		private Reader reader=null;

		@Override
		public void run() {
			final char[] b = new char[UtilityIO.BUFFER_SIZE];
			while (true) {
				final int len;
				try {
					len = reader.read(b);
				} catch (final IOException e) {
					log.fine("Error when reading from reader"+e);
					throw new RuntimeException(e);
				}
				if (len == -1) {
					break;
				}
				try {
					synchronized (this) {
						if (errorWriter != null) {
							errorWriter.write(b, 0, len);
							errorWriter.flush();
						}
					}
				} catch (final IOException e) {
					log.fine("Error when writing to writer"+e.getLocalizedMessage());
					throw new RuntimeException(e);
				}
			}

		}


	}

	public boolean variableExist(String varname) {
		boolean retval=false;
		StringWriter existResult = new StringWriter();
		this.exec(new Read2WriteTask(new StringReader("printf(\"%d\", exist(\"" + varname + "\",\"var\"));")),
				new Write2ReadTask(existResult));
		String s = existResult.toString();
		String ONE="1";
		String ZERO="0";
		if(s != null && s.equals(ONE))
			retval=true;
		else if( s != null && s.contains(ZERO))
			retval = false;
		return retval;
	}
}
