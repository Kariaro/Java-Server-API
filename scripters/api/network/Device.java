package scripters.api.network;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Device implements Runnable {
	private boolean connected;
	private final Socket socket;
	private final int ID;
	private int delay = 100, timeout = 10000, max_timeouts = 4;
	
	Device(Socket s, int id) { this.socket = s; this.ID = id; }
	
	public static Device createConnection(InetSocketAddress address) throws IOException { return new Device(new Socket(address.getAddress(), address.getPort()), -1); }
	public static Device createConnection(InetAddress address, int port) throws IOException { return new Device(new Socket(address, port), -1); }
	
	public boolean isClosed() { return socket.isClosed() || !connected || socket.isInputShutdown() || socket.isInputShutdown(); }
	public boolean isConnected() { return connected; }
	public Socket getSocket() { return socket; }
	public int getID() { return ID; }
	
	public void setMaxTimeouts(int max) { max_timeouts = max < -1 ? -1:(timeout * max > 0x5265C00 ? 0x5265C00:max); }
	public int getMaxTimeouts() { return max_timeouts; }
	public void setTimeout(int millis) {
		timeout = millis < 0 ? 0:(millis > 0x5265C00 ? 0x5265C00:millis);
		try { if(timeout > 0) socket.setSoTimeout(timeout); } catch(SocketException e) { e.printStackTrace(); }
	}
	public int getTimeout() { return timeout; }
	public void setDelay(int millis) { delay = millis < 0 ? 0:(millis > 0xEA60 ? 0xEA60:millis); }
	public int getDelay() { return delay; }
	
	public synchronized void disconnect() {
		if(!isClosed()) try {
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(connected) connected = false;
	}
	
	public synchronized void connect() { if(!socket.isClosed() && !connected) connected = true; }
	public synchronized boolean WriteToClient(byte[] B) {
		try {
			OutputStream stream = getOutputStream();
			stream.write(B);
			stream.flush();
		} catch(Exception e) {
			e.printStackTrace();
			disconnect();
			return true;
		}
		return false;
	}
	
	public synchronized boolean WriteToClient(Header H) {
		if(H == null) return false;
		try {
			OutputStream stream = getOutputStream();
			stream.write(H.getHeader().getBytes()); stream.flush();
			stream.write(H.getData().getData()); stream.flush();
		} catch(IOException e) {
			e.printStackTrace();
			disconnect();
			return true;
		}
		return false;
	}
	
	public synchronized void run() {
		if(!socket.isClosed()) connect();
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			if(timeout > 0) socket.setSoTimeout(timeout);
			
			int read_byte = 0;
			String data = "";
			int timeouts = 0;
			long now = System.currentTimeMillis();
			while(!isClosed()) {
				if(in.available() != 0) {
					read_byte = in.read();
					data += (char)read_byte;
					now = System.currentTimeMillis();
				} else {
					if(read_byte != -2) timeouts = 0;
					read_byte = -2;
					Thread.sleep(delay);
					if(timeout > -1) if(System.currentTimeMillis() - now > timeout) {
						timeouts++;
						Header header = null;
						if(data != null) header = Header.read(data.getBytes());
						fireEvent(0, DeviceEvent.create(this, header));
						if(timeouts >= max_timeouts) {
							disconnect();
							fireEvent(1, DeviceEvent.create(this, header));
						} else now = System.currentTimeMillis();
					}
				}
				
				if(read_byte < 0 && data.length() > 0) {
					Header header = Header.read(data.getBytes());
					fireEvent(0, DeviceEvent.create(this, header));
					data = "";
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally { disconnect(); }
	}
	
	private List<DeviceListener> listeners = new ArrayList<DeviceListener>();
	public DeviceListener[] getListeners() { return (DeviceListener[])listeners.toArray(); }
	public synchronized void addListener(DeviceListener l) { listeners.add(l); }
	public synchronized void removeListener(DeviceListener l) {
		int i = listeners.indexOf(l);
		if(i>-1) listeners.remove(i);
	}
	
	public synchronized void fireEvent(int i, DeviceEvent event) {
		for(DeviceListener l : listeners) {
			if(event.consumed()) break;
			if(i == 0) l.clientConnection(event);
			if(i == 1) l.clientDisconnect(event);
		}
	}
	
	public OutputStream getOutputStream() throws IOException { return socket.getOutputStream(); }
	public InputStream getInputStream() throws IOException { return socket.getInputStream(); }
	
	static class Resource {
		private ContentType type;
		private Status status;
		private File file;
		
		public Resource(String s, String t, Status a) { this(new File(s), ContentType.FindByTypeName(t), a); }
		public Resource(String s, String t) { this(new File(s), ContentType.FindByTypeName(t), Status.OK); }
		public Resource(String s, ContentType t) { this(new File(s), t, Status.OK); }
		
		public Resource(File f, ContentType t, Status a) { status = a; type = t; file = f; }
		public File getFile() { return file; }
		public ContentType getType() { return type; }
		public Status getStatus() { return status; }
	}
}
