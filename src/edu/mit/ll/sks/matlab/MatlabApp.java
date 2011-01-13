/**
 * 
 */
package edu.mit.ll.sks.matlab;

import matlabcontrol.LocalMatlabProxy;
import matlabcontrol.MatlabInvocationException;

/**
 * @author cyee
 *
 */
public class MatlabApp implements MathAppIF {
	String result=null;
	/**
	 * 
	 */
	public MatlabApp() {
		// TODO Auto-generated constructor stub
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
	@Override
	public void process(String host, String request) {
		// TODO Auto-generated method stub
		try {
			this.result = (String)LocalMatlabProxy.returningFeval("D4MwebQueryResponse", new Object[] { request.toString(), host });
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void process(String script, String[] params) {
		try {
			Object [] obj = new Object[params.length];
			for(int i=0; i < params.length; i++) {
				obj[i] = params[i];
			}
			
			this.result = 
				(String)LocalMatlabProxy.returningFeval("D4MwebQueryResponse",obj);
				//(String)LocalMatlabProxy.returningFeval("D4MwebQueryResponse", new Object[] { params[0], params[1] });
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
