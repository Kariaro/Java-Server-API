package scripters.api.network;

import static scripters.api.network.ContentType.*;

import java.nio.charset.Charset;

public class Content implements EasyPrint {
	private ContentType type = NOT_FOUND;
	private String boundary = null;
	private Charset charset = null;
	
	public Content(String... arr) { init_class(arr); }
	private void init_class(String... arr) {
		if(arr.length == 1) {
			String s = arr[0];
			if(s.indexOf(';') < 0) init(s); else {
				String[] array = s.replace(" ","").split(";");
				for(int i = 0; i < array.length; i++) init(array[i]);
			}
		}
		if(arr.length > 1) for(int i = 0; i < arr.length; i++) init(arr[i]);
	}
	
	public void init(String s) {
		{ ContentType c = FindByTypeName(s); if(c != NOT_FOUND) type = c; }
		if(s.startsWith("charset="))  charset  = Charset.forName(s.substring("charset=".length()));
		if(s.startsWith("boundary=")) boundary = s.substring("boundary=".length());
	}
	public boolean hasContentType() { return type != NOT_FOUND; }
	public ContentType getContentType() { return type; }
	
	public boolean hasBoundary() { return boundary != null; }
	public String getBoundary() { return boundary; }
	
	public boolean hasCharset() { return charset != null; }
	public Charset getCharset() { return charset; }
	
	public static Content valueOf(String s) { return new Content(s); }
	public String toString() {
		String ret = "";
		if(hasContentType()) ret += type; else ret += "text/plain"; 
		if(hasBoundary()) ret += "; boundary=" + boundary;
		if(hasCharset()) ret += "; charset=" + charset.name();
		return ret;
	}
}
