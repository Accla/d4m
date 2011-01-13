package edu.mit.ll.oct4j;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class DataWriteTask implements WriteTaskIF {

	/**
	 * @param map
	 */
	public DataWriteTask(Map<String, BaseOctDataObject> map) {
		super();
		this.map = map;
	}

	private Map<String, BaseOctDataObject> map=null;

	@Override
	public void doIt() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doIt(Writer writer) throws IOException {
		// TODO Auto-generated method stub
        writer.write("load(\"-text\", \"-\")\n");
        for ( Map.Entry<String, BaseOctDataObject> entry : map.entrySet()) {
             String name = entry.getKey();
            UtilityIO.write(writer, name, entry.getValue());
        }

        writer.write("# name: \n");
        writer.flush();
	}

	@Override
	public Writer getWriter() {
		// TODO Auto-generated method stub
		return null;
	}

}
