/**
 * 
 */
package edu.mit.ll.oct4j;

import java.util.logging.Logger;

/**
 * @author cyee
 *
 */
public class OctaveMain {
	private static Logger log = Logger.getLogger(OctaveMain.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OctaveProcessor octave = OctaveProcFactory.getInstance();
		octave.exec("a=path");
		OctDataObject data = octave.get("a");

		log.info(" %%%% a = "+data.toString() + "  %%%%");
		octave.exec("mylist=ls");
		data = octave.get("mylist");
		
		String d4m_home = System.getProperty("d4m.home");
		
		String d4m_src = d4m_home+"/matlab_src";
		String d4m_example = d4m_home+"/examples";
		log.info("LIST::"+data.toString());
		octave.exec("ap=addpath('"+d4m_home+"');");
		octave.exec("ap=addpath('"+d4m_src+"');");
		octave.exec("addpath('"+d4m_example+"');");
		octave.exec("a=path");
		
		data = octave.get("a");
		log.info("NEW PATH =" +data.toString());
		octave.exec("DBinit");
		octave.exec("sz=OctaveJavaExample()");
		data = octave.get("sz");
		System.out.println("SIZE of list = "+data.toString());
		octave.shutdown();
		
		
		System.out.println(""+Double.valueOf(1E-6));
		System.out.println(10E-6);
	}

}
