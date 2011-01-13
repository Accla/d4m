package edu.mit.ll.oct4j;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.Logger;

/*
 * OctaveProcessor is the main object to hold the octave process
 */
public class OctaveProcessor {
	private static Logger log = Logger.getLogger(OctaveProcessor.class);
	private String workDir=null;
	private String programName=null;
	private OctaveCmdExecutor octaveCmd=null;
	private Writer writer = new OutputStreamWriter(System.out);
	private Random randomNumGen = new Random();

	public OctaveProcessor() {
		super();
	}
	/**
	 * @param programName
	 * @param workDir
	 *
	 */
	public OctaveProcessor(String programName, String workDir) {
		super();
		this.workDir = workDir;
		this.programName = programName;
		long startTime = System.nanoTime();
		this.octaveCmd = new OctaveCmdExecutor(programName, workDir);
		double estimatedTime = ((double)(System.nanoTime() - startTime)) * 1E-06;
		log.info(" Octave Process is ready ... "+Double.toString(estimatedTime)+ " ms");
	}

	public void exec(String cmd) {

		//execIt(cmd)
		String mark = String.format("%06x%06x", randomNumGen.nextInt(1 << 23), randomNumGen.nextInt(1 << 23));
		log.info("CMD="+cmd);
		long startTime = System.nanoTime();
		set(String.format("oct4j_%1$s_eval",mark), cmd);
		double estimatedTime = ((double)(System.nanoTime() - startTime)) * 1E-06;
		log.info("1. SET  est. time = "+Double.toString(estimatedTime)+ " ms");
		execIt(String.format("eval(oct4j_%1$s_eval, \"oct4j_%1$s_lasterr = lasterr();\");", mark));
		OctDataObject lastError = get( String.format("oct4j_%1$s_lasterr", mark));
		execIt(String.format("clear oct4j_%1$s_eval oct4j_%1$s_lasterr", mark));
		if(lastError != null)
			log.debug("ERROR = "+lastError.toString());

	}

	private void execIt(String cmd) {
		long startTime = System.nanoTime();

		SimpleWriteTask writeTask = new SimpleWriteTask(cmd);
		Write2ReadTask readTask = new Write2ReadTask(this.writer);
		this.octaveCmd.exec(writeTask, readTask);
		double estimatedTime = ((double)(System.nanoTime() - startTime)) * 1E-06;
		log.info(" ExecIt ["+ cmd+"]  est. time = "+Double.toString(estimatedTime)+ " ms");
		
	}
	public void exec (String variableName, String cmd) {
		this.octaveCmd.exec(variableName, cmd);
	}
	/*
	 *  get the value assigned to this variableName
	 */
	public OctDataObject get(String variableName) {
		BaseOctDataObject obj = this.octaveCmd.get(variableName);
		OctDataObject ret=null;

		if(obj instanceof StringDO) {
			StringDO sdo = (StringDO)obj;
			String val = sdo.get();
			ret = new OctDataObject(variableName, val);
		} else if (obj instanceof DoubleDO) {
			DoubleDO ddo = (DoubleDO)obj;
			Double val= ddo.get();
			ret = new OctDataObject(variableName, val);
		}
		return ret;
	}

	/*
	 *  assign the variable the specified value in Octave
	 */
	public void set(String var, String value) {
		StringDO sdo = new StringDO(value);
		Map<String,BaseOctDataObject> datamap = new HashMap<String,BaseOctDataObject>();
		datamap.put(var,sdo);
		this.octaveCmd.set(datamap);
	}

	public void quit() {
		this.octaveCmd.quit();
	}
	public void shutdown () {
		this.octaveCmd.shutdown();
	}
}
