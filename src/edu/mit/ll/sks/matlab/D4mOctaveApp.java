/**
 * 
 */
package edu.mit.ll.sks.matlab;

import org.apache.log4j.Logger;

import edu.mit.ll.oct4j.OctDataObject;
import edu.mit.ll.oct4j.OctaveProcFactory;
import edu.mit.ll.oct4j.OctaveProcessor;

/**
 * @author cyee
 *
 */
public class D4mOctaveApp implements MathAppIF {
	private static Logger log = Logger.getLogger(D4mOctaveApp.class.getName());
	private OctaveProcessor octave=null;
	private String result=null;
	private String octaveProgram=null;
	private String workDir=null;

	/**
	 *  args[0]   program executable 
	 *  args[...]   paths for addpath
	 */
	/*
	public D4mOctaveApp(MathAppConfigBean config) {
		String [] params = config.getArgs();
		//params[0] = octave program file
		//params[1] = top-level work directory
		//params[2 ...n] = path to scripts --> use addpath

		this.octaveProgram = params[0];
		this.workDir = params[1];
		for(int i = 2; i < params.length; i++) {

		}
	}
	 */
	public D4mOctaveApp() {
		this.octave = OctaveProcFactory.getInstance();

		String d4m_home=System.getProperty("d4m.home");
		if(d4m_home == null) {
			//if D4M_HOME is not defined, then use the current user directory as the D4M_HOME
			d4m_home = System.getProperty("user.dir");
		}
		String d4m_src="addpath('"+d4m_home+"/matlab_src');";
		String d4m_examples="addpath('"+d4m_home+ "/examples');";
		this.octave.exec(d4m_src);
		this.octave.exec(d4m_examples);
		this.octave.exec("DBinit");
		//Initialize octave
		//addpath(d4m_api/matlab_src)
		//addpath(d4m_api/examples)
		//DBinit

	}

	/* (non-Javadoc)
	 * @see edu.mit.ll.sks.matlab.MathAppIF#getResult()
	 */
	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return this.result;
	}

	/* (non-Javadoc)
	 * @see edu.mit.ll.sks.matlab.MathAppIF#process(java.lang.String, java.lang.String)
	 */
	/**
	 *   host cloudbase host
	 *   request  =  query in JSON format
	 */
	@Override
	public void process(String host, String request) {
		//		String cmd="resp=D4MwebQueryResponse('"+request + "','"+host+"' );";
		//		this.octave.exec(cmd);
		//		OctDataObject data = this.octave.get("resp");
		//		this.result = data.toString();
		process("D4MwebQueryResponse",new String [] {request, host});
	}

	@Override
	public void stop() {
		this.octave.shutdown();
	}
	@Override
	public void process(String script, String [] params) {
		long startTime = System.currentTimeMillis();
		this.octave.exec(makeCommand(script,params));
		long endTime = System.currentTimeMillis();
		log.info("EXEC est. time (ms) = "+((double)(endTime-startTime)));
		boolean gotData=false;
		int count = 0;
		startTime = System.nanoTime();
		while(!gotData) {
			
			OctDataObject data = this.octave.get("resp");
			if(data == null) {
				try {
					Thread.sleep(1000);
					count++;
				} catch (InterruptedException e) {
					log.warn("Sleep interrupted.",e);
				}
			} else {
				this.result = data.toString();
				gotData=true;
			}
			if(count > 60) {
				this.result = null;
				log.debug("No data/response");
				break;
			}
		}
		endTime = System.nanoTime();
		log.info("GET est. time (ms) = "+ (((double)(endTime-startTime))/1000000.0) );
		
	}

	public String makeCommand(String script, String [] params) {
		long startTime = System.currentTimeMillis();
		StringBuffer sb = new StringBuffer();
		sb.append("resp=");
		sb.append(script);
		sb.append("(");
		int len = params.length;
		int count =0;
		for(String p : params) {
			if(count > 0)
				sb.append(",");
			sb.append("'");
			int indx = p.indexOf('\r');
			if(indx == -1)
				indx = p.indexOf('\n');
			if(indx > -1)
				p = p.substring(0,indx);
			sb.append(p);
			sb.append("'");
			count++;

		}
		sb.append(");");
		long endTime = System.currentTimeMillis();
		log.info("MAKE_COMMAND est. time (ms) = "+((double)(endTime-startTime)));
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {

		D4mOctaveApp octApp = new D4mOctaveApp();
		String script = "D4MwebQueryResponse";
		String [] params = new String [] {"blah","yahoo.com"};
		System.out.println(octApp.makeCommand(script,params));
		octApp.stop();
	}
}
