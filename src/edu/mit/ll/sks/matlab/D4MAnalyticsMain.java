/**
 * 
 */
package edu.mit.ll.sks.matlab;

/**
 * @author cyee
 *
 */
public class D4MAnalyticsMain {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		if(args.length < 4) {
			usage();
			System.exit(1);
		}

		//args[0]  cloudbase host
		//args [1]  matlab|octave
		//args[2]   octave program
		// args[3]  d4m_home path
		//args[4]   matlab script name
		String host=args[0];

		String mathapp = args[1];
		System.setProperty(D4mAnalyticsJsonListener.APP_SYS_PROPERTY, mathapp);

		String octaveProgram = args[2];
		System.setProperty("octave.program", octaveProgram);
		String d4m_home = args[3];
		
		System.out.println("**** "+host+","+ mathapp+","+octaveProgram+","+d4m_home +"****");
		System.setProperty(D4mAnalyticsJsonListener.D4M_SYS_PROPERTY, d4m_home);
		String script=null;
		if(args.length > 4) {
			script=args[4];
		}


		D4mAnalyticsJsonListener d4m = new D4mAnalyticsJsonListener("localhost", host,script);
		Thread.sleep(Long.MAX_VALUE);
	}

	public static void usage( ) {
		System.out.println(" D4MAnalyticsMain [cbHost] [mathapp] [octave program] [D4M_HOME] [script]");
		System.out.println("   -  cbHost is the cloudbase host address");
		System.out.println("   -  mathapp  is 'matlab' or 'octave'. If it is not provided, then matlab will be used. ");
		System.out.println("   -  octave program");
		System.out.println("   -  D4M_HOME is the D4M api directory. If it is not provided, then the current user directory will be used.");
		System.out.println("   -  script   name of matlab/octave script to execute");
	}
}
