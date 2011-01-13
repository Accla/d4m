package edu.mit.ll.oct4j;

public class OctaveProcFactory {

	public static String OCTAVE_PROGRAM="octave.program";
	public static String D4M_HOME="d4m.home";
	private static String octave=null;
	private static String workDir= null;
	public static OctaveProcessor getInstance(String program, String workdir) {
		octave = program;
		workDir = workdir;
		return getInstance();
	}
	public static OctaveProcessor getInstance() {

		String octaveProgram = octave;
		if( octave == null ) {
			octaveProgram = System.getProperty(OCTAVE_PROGRAM, "octave");
		}
		String curDirectory= workDir;
		if(workDir == null)
			curDirectory = System.getProperty("d4m.home");

		System.out.println("OCTAVE program == "+octaveProgram);
		System.out.println("D4M_HOME == "+curDirectory);
		//If there is no d4m.home defined then use the current directory to D4M src
		if(curDirectory == null)
			curDirectory=	System.getProperty("user.dir");
		OctaveProcessor octave = new OctaveProcessor(octaveProgram,curDirectory);
		return octave;
	}
}
