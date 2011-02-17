package edu.mit.ll.oct4j;
public class MiscUtil {
	public static String RESPONSE_VAR="resp";
	
	public static String makeCommand(String script, String [] params) {
		//long startTime = System.currentTimeMillis();
		StringBuffer sb = new StringBuffer();
		sb.append(RESPONSE_VAR).append("=");
		sb.append(script);
		sb.append("(");
		int len = params.length;
		int count =0;
		for(String p : params) {
			if(count > 0 && len > 1)
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
		//long endTime = System.currentTimeMillis();
		//log.info("MAKE_COMMAND est. time (ms) = "+((double)(endTime-startTime)));
		return sb.toString();
	}

}
