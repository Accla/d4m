package cloudbase.core.aggregation;

import java.io.IOException;

import cloudbase.core.data.Value;

public class LongSummation implements Aggregator {
	long sum = 0;
	
	public Value aggregate() {
		return new Value(longToBytes(sum));
	}

	public void collect(Value value) {
		try {
			sum += bytesToLong(value.get());
		}
		catch (IOException e) {
			System.err.println("ERROR: LongSummation trying to convert bytes to long, but byte array isn't length 8");
		}
	}

	public void reset() {
		sum = 0;
	}
	
	public static long bytesToLong(byte[] b) throws IOException {
		return bytesToLong(b,0);
	}
	
	public static long bytesToLong(byte[] b, int offset) throws IOException {
		if (b.length<offset+8)
			throw new IOException("trying to convert to long, but byte array isn't long enough, wanted "+(offset+8)+" found "+b.length);
		return (((long)b[offset+0] << 56) +
				((long)(b[offset+1] & 255) << 48) +
				((long)(b[offset+2] & 255) << 40) +
				((long)(b[offset+3] & 255) << 32) +
				((long)(b[offset+4] & 255) << 24) +
				((b[offset+5] & 255) << 16) +
				((b[offset+6] & 255) <<  8) +
				((b[offset+7] & 255) <<  0));
	}

	public static byte[] longToBytes(long l) {
		byte[] b = new byte[8];
	    b[0] = (byte)(l >>> 56);
	    b[1] = (byte)(l >>> 48);
	    b[2] = (byte)(l >>> 40);
	    b[3] = (byte)(l >>> 32);
	    b[4] = (byte)(l >>> 24);
	    b[5] = (byte)(l >>> 16);
	    b[6] = (byte)(l >>>  8);
	    b[7] = (byte)(l >>>  0);
	    return b;
	}
}
