package cloudbase.core.util;

import org.apache.hadoop.io.Text;

public final class TextUtil
{
	public static byte[] getBytes(Text text) {
		byte[] bytes = text.getBytes();
		if (bytes.length == text.getLength()) {
			return text.getBytes();
		}
		byte[] copy = new byte[text.getLength()];
		System.arraycopy(text.getBytes(), 0, copy, 0, copy.length);
		return copy;
	}
}