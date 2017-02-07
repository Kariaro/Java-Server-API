package scripters.api.network;

import static scripters.api.network.ContentType.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Header implements EasyPrint {
	public Map<String, String> info = new HashMap<String, String>();
	
	private Connection conn = Connection.NONE;
	private Status status = Status.OK;
	private Protocol protocol = HTTP.HTTP_1_1;
	private Data data = new Data();
	private Content type = NOT_FOUND;
	private String server_name = "";
	private boolean request;
	private byte[] raw;
	
	private String simple_date = "";
	private String expire_date = "";
	private String last_date = "";
	public String TODO = "";
	
	public String resource = "";
	public String method = "";
	public String client = "";
	public String host = "";
	
	private Header() { }
	public static Header create() { return create(false); }
	public static Header create(boolean request) { Header H = new Header(); H.request = request; return H; }
	
	public static Header read(byte[] b) { return read(b, false); }
	public static Header read(byte[] b, boolean request) {
		Header H = new Header();
		H.request = request;
		H.raw = b;
		
		String[] header = null;
		String data_packet = null;
		/** Initialize the 'header' and 'data_packet' */ {
			String b_string = new String(b);
			int index = b_string.indexOf("\r\n\r\n");
			if(index == -1) {
				b_string = b_string.substring(0, b_string.length());
				try { H.setData(b_string.getBytes()); } catch(IOException e) { e.printStackTrace(); }
				return H;
			} else {
				String c_string = b_string.substring(0, index);
				header = c_string.split("\r\n");
				String d_string = b_string.substring(4 + index);
				data_packet = d_string;
			}
		}
		
		/** Read the HEAD of the responce. */
		for(int i = 0; i < header.length; i++) {
			String s = header[i];
			
			if(i == 0) {
				if(s.equals("")) continue;
				String[] a = s.split(" ");
				if(a.length == 3) {
					String me = a[0].toUpperCase();
					String pa = a[1];
					String st = a[2].toUpperCase();
					
					try {
						H.status = Status.getStatus(Integer.valueOf(pa));
					} catch(Exception e) {
						H.status = Status.OK;
						H.protocol = Protocol.valueOf(st);
						H.resource = pa;
					}
					
					H.method = me;
					continue;
				}
			}
			
			/** Test */ {
				int ind = s.indexOf(": ");
				if(ind < 0); else {
					H.set(s.substring(0,ind), s.substring(ind+2));
					continue;
				}
				
				//if(!fin) {
				//	try {
				//		H.TODO += ("/* TODO */ Add \"" + s.substring(0, s.indexOf(": ")) + "\"\n");
				//		} catch(Exception e) {
				//		H.TODO += ("/* TODO and ERROR */ Add \"" + s + "\"\n");
				//	}
				//}
			}
		}
		
		if(data_packet != null) {
			try {
				H.data.write(data_packet);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		return H;
	}
	
	/** Cusom method */
	public byte[] getRaw() { return raw.clone(); }
	
	/** Status */
	public void setStatus(Status s) { status = s == null ? Status.OK:s; }
	public Status getStatus() { return status; }
	
	/** Protocol */
	public void setProtocol(Protocol p) { protocol = p == null ? HTTP.HTTP_1_1:p; }
	public void setProtocol(String s) { setProtocol(Protocol.valueOf(s)); }
	public Protocol getProtocol() { return protocol; }
	
	/** Server */
	public void removeServerName() { server_name = ""; remove("Server"); }
	public void setServerName(String s) {
		if(s == null) { removeServerName(); return; }
		server_name = s; set("Server", s);
	}
	public String getServerName() {
		if(has("Server")) server_name = get("Server");
		return server_name;
	}
	
	/** Date's */
	public void removeDates() { removeSimpleDate(); removeLastModified(); removeExpireDate(); }
	public void removeSimpleDate() { simple_date = ""; remove("Date"); }
	public void removeLastModified() { last_date = ""; remove("Last-Modified"); }
	public void removeExpireDate() { expire_date = ""; remove("Expires"); }
	public void setSimpleDate(String s) { if(!Date.validate(s)) { removeSimpleDate(); return; } simple_date = s; set("Date", s); }
	public void setLastModified(String s) { if(!Date.validate(s)) { removeLastModified(); return; } last_date = s; set("Last-Modified", s); }
	public void setExpireDate(String s) { if(!Date.validate(s)) { removeExpireDate(); return; } expire_date = s; set("Expires", s); }
	public void setDates(String s) { if(Date.validate(s)) { setSimpleDate(s); setLastModified(s); setExpireDate(s); } }
	public String getSimpleDate() { if(has("Date")) simple_date = get("Date"); return simple_date; }
	public String getLastModified() { if(has("Last-Modified")) last_date = get("Last-Modified"); return last_date; }
	public String getExpireDate() { if(has("Expires")) expire_date = get("Expires"); return expire_date; }
	
	/** Data */
	public int getContentLength() { int s = data.length(); set("Content-Length", s + ""); return s; }
	public void removeData() throws IOException { data.clear(); remove("Content-Length"); }
	public void setData(byte[] b) throws IOException { data.clear(); data.write(b); set("Content-Length", data.length() + ""); }
	public void setData(Data d) { if(d != null) { data = d; set("Content-Length", data.length() + ""); } }
	public Data getData() { return data; }
	
	/** ContentType */
	public void removeContentType() { type = NOT_FOUND; remove("Content-Type"); }
	public void setContentType(Content c) {
		if(c == null || c.getContentType() == ContentType.NOT_FOUND) { removeContentType(); return; }
		type = c; set("Content-Type", type);
	}
	public void setContentType(String s) { setContentType(FindByTypeName(s)); }
	public Content getContentType() {
		if(has("Content-Type")) type = Content.valueOf(get("Content-Type"));
		return type;
	}
	
	/** Connection */
	public void removeConnection() { conn = Connection.NONE; remove("Connection"); }
	public void setConnection(Connection c) {
		if(c == null || c == Connection.NONE) { removeConnection(); return; }
		conn = c; set("Connection", conn.toString());
	}
	public void setConnection(String s) { setConnection(Connection.get(s)); }
	public Connection getConnectionType() {
		if(has("Connection")) conn = Connection.get(get("Connection"));
		return conn;
	}
	
	/** Add, Get, Has */
	public Header set(String name, CharSequence val) { info.put(name, val.toString()); return this; }
	public void remove(String name) { info.remove(name); }
	public String get(String name) { return info.get(name); }
	private boolean has(String s) { return get(s) != null; }
	
	public String getHeader() {
		StringBuilder builder = new StringBuilder();
		if(info.entrySet().size() == 0) return "";
		
		if(!method.equals("")) {
			if(request) builder.append(method).append(" ").append(resource).append(" ").append(protocol).append("\r\n");
			else builder.append(protocol).append(" ").append(status).append("\r\n");
		} else builder.append(protocol).append(" ").append(status).append("\r\n");
		
		for(Entry<String, String> e : info.entrySet()) {
			String name = e.getKey();
			String valu = e.getValue();
			builder.append(name).append(": ").append(valu).append("\r\n");
		}
		builder.append("\r\n");
		return builder.toString();
	}
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getHeader()).append("\r\n");
		if(data.length() > 0) builder.append(new String(data.getData()));
		return builder.toString();
	}
}
