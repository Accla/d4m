package cloudbase.core.util;

import java.util.Map.Entry;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.data.Key;
import cloudbase.core.data.Value;

public class PrintTable {
	public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException
	
	{
		if (args.length==3) {
			System.out.println("Insufficient paratmers, needs username and password");
			return;
		}
		Connector connector = new Connector(new HdfsZooInstance(), args[1], args[2].getBytes());
		Scanner scanner = connector.createScanner(args[0], CBConstants.NO_AUTHS);
		
		for (Entry<Key, Value> entry : scanner)
			System.out.println(Shell.formatEntry(entry));
	}
}
