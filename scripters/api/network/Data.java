package scripters.api.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Data implements EasyPrint {
	private ByteArrayOutputStream stream;
	public Data() {
		stream = new ByteArrayOutputStream();
	}
	public int length() {
		return stream.size();
	}
	public byte[] getData() {
		return stream.toByteArray();
	}
	public void write(byte[] b, int o, int l) throws IOException { stream.write(b, o, l); }
	public void write(byte[] b) throws IOException { stream.write(b); }
	public void write(String s) throws IOException { write(s.getBytes()); }
	public void write(Object o) throws IOException { write(o.toString()); }
	public void write(char c) throws IOException { stream.write((int)c); }
	public void write(int i) throws IOException { stream.write(i); }
	public ByteArrayOutputStream stream() { return stream; }
	public void clear() throws IOException { stream.flush(); }
	public void close() throws IOException { stream.close(); }
	public String toString() { return new String(getData()); }
}
