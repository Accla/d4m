package edu.mit.ll.oct4j;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Logger;

public class Read2WriteTask implements WriteTaskIF {
	private static Logger log = Logger.getLogger(Read2WriteTask.class.getName());
	private Reader reader =null;
	/**
	 * @param reader
	 */
	public Read2WriteTask(Reader reader) {
		super();
		this.reader = reader;
	}

	
	
	@Override
	public void doIt() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doIt(Writer writer) throws IOException {
		long numChars = UtilityIO.copy(reader, writer);
		log.info("Total number of characters = "+numChars);
	}

	@Override
	public Writer getWriter() {
		// TODO Auto-generated method stub
		return null;
	}

}
