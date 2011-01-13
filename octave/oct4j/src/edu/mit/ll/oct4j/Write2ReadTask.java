package edu.mit.ll.oct4j;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class Write2ReadTask implements ReadTaskIF {

	private Writer writer;
	public Write2ReadTask(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void doIt() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doIt(Reader reader) throws IOException {
		// TODO Auto-generated method stub
		UtilityIO.copy(reader, writer);
	}

	@Override
	public Reader getReader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseOctDataObject getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
