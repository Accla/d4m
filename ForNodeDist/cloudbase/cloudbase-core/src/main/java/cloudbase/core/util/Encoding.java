package cloudbase.core.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.io.Text;

public class Encoding {
	
    public static String encodeDirectoryName(Text row) throws UnsupportedEncodingException{
    	byte[] rowBytes = row.getBytes();
    	if(rowBytes.length != row.getLength()){
    		rowBytes = new byte[row.getLength()];
    		System.arraycopy(row.getBytes(), 0, rowBytes, 0, row.getLength());
    	}
    	
    	String encodedRow = new String(Base64.encodeBase64(rowBytes, false));
    	
    	encodedRow = encodedRow.replace('/', '_').replace('+', '-');
    	
    	int index = encodedRow.length() - 1;
    	
    	while(index >=0 && encodedRow.charAt(index) == '=')
    		index--;
    	
    	encodedRow = encodedRow.substring(0, index+1);
    	
    	
    	return "/key_" + encodedRow;
    }
    
    public static void main(String[] args) throws UnsupportedEncodingException {
    	
    	byte b[] = new byte[50];
    	
    	for(int i = 0; i < b.length; i++){
    		b[i] = (byte) i;
    	}
    	
    	System.out.println(encodeDirectoryName(new Text(b)));
	}
	    
}
