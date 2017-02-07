package scripters.api.network;

import static scripters.api.network.Connection.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scripters.api.network.Device.Resource;

public class ServerInstance {
	private static ServerInstance instance;
	
	protected static String INFO = "[" + ServerInstance.class.getSimpleName() + "]: ";
	protected HTTP http_version = HTTP.HTTP_1_1;
	protected boolean isRunning = false;
	
	protected int connection_timeout = 4000;
	protected int max_timeouts = 1;
	protected int max_connections = 50;
	protected int port = 8000;
	
	private final List<Integer> DEVICES = new ArrayList<>();
	protected ServerSocket server;
	protected ThreadGroup client_group;
	protected Thread client_init;
	protected boolean loaded = false;
	private ServerInstance() { this(8000, 50); }
	private ServerInstance(int port, int max_connections) {
		this.port = port;
		this.max_connections = max_connections;
		
		try {
			server = new ServerSocket(port, max_connections);
		} catch(IOException e) {
			ServerLog.log(ServerLog.ERROR, "Error creating ServerSocket() with port " + port + "!", e);
		}
		
		client_group = new ThreadGroup("Server");
		client_init = new Thread(new Runnable() {
			public void run() {
				try {
					ServerLog.log(ServerLog.INFO, "The server started on port " + port, null);
					for(;isRunning;) {
						synchronized(client_group) {
							Socket socket = server.accept();
							ServerLog.log(ServerLog.INFO, "Connection established!", null);
							int id = 0;
							for(int i = 0; i < DEVICES.size(); i++) {
								int n = DEVICES.get(i);
								if(id == n) id++;
							} DEVICES.add(id);
							
							Device device = new Device(socket, id);
							device.setDelay(1);
							device.setTimeout(connection_timeout);
							device.setMaxTimeouts(2);
							
							/** This is a bit cheaty but, it works. */
							device.addListener(new DeviceListener() {
								public void clientConnection(DeviceEvent event) {
									requestSite(event.getPacket(), event.getDevice());
								}
								public void clientDisconnect(DeviceEvent event) {
									DEVICES.remove(new Integer(event.getDevice().getID()));
									for(DeviceListener l : listeners) { if(event.consumed()) break; l.clientDisconnect(event); }
								}
								public void clientTimeout(DeviceEvent event) {
									for(DeviceListener l : listeners) { if(event.consumed()) break; l.clientTimeout(event); }
								}
							});
							Thread t = new Thread(client_group, device, "[Device: " + DEVICES + "]");
							t.start();
						}
					}
				} catch(IOException e) {
					ServerLog.log(ServerLog.ERROR, "Error has occured in main loop. "+ port, e);
				}
			}
		});
		
		{
			boolean b = true;
			loaded = !(client_init == null || server == null || client_group == null);
			ServerLog.log(ServerLog.INFO, "Done initializing... (Checking if everything got loaded corectly)", null);
			ServerLog.log(ServerLog.INFO, "", null); b = client_init == null;
			ServerLog.log(ServerLog.INFO - (b ? 2:0), "Client Thread: " + (b ? "Failed":"Success"), null); b = server == null;
			ServerLog.log(ServerLog.INFO - (b ? 2:0), "Server Socket: " + (b ? "Failed":"Success"), null); b = client_group == null;
			ServerLog.log(ServerLog.INFO - (b ? 2:0), "Thread Group: " + (b ? "Failed":"Success"), null);
			ServerLog.log(ServerLog.INFO, "", null);
		}
	}
	
	public void startServer() throws Exception {
		if(server == null) { ServerLog.log(ServerLog.ERROR, "Server is null!", null); return; }
		if(!loaded) { ServerLog.log(ServerLog.WARNING, "Server did not load properly, (You may need to reload the server)", null); return; }
		if(!client_init.isAlive()) {
			isRunning = true;
			client_init.start();
		}
	}
	
	public void closeServer() {
		if(server == null) { ServerLog.log(ServerLog.ERROR, "Server is null!", null); return; }
		if(server.isClosed()) { ServerLog.log(ServerLog.WARNING, "Server is already closed!", null); return; }
		try {
			server.close();
			isRunning = false;
			if(client_init.isAlive()) { client_init.interrupt(); }
			loaded = false;
		} catch(Exception e) {
			ServerLog.log(ServerLog.ERROR, "Can't close server... (Try restarting your program!)", e);
		}
	}
	
	public void restartServer() {
		try {
			closeServer();
			server = new ServerSocket(port, max_connections);
			startServer();
		} catch(Exception e) {
			ServerLog.log(ServerLog.ERROR, "Error restarting Server with port " + port + "!", e);
		}
	}
	
	public void setPort(int port) {
		this.port = port < 0 ? 0:(port > 65535 ? 65535:port);
		restartServer();
	}
	public void setMaxConnections(int max) {
		this.max_connections = max;
		restartServer();
	}
	public static ServerInstance createInstance(int port, int max_connections) {
		if(instance == null) {
			instance = new ServerInstance(port, max_connections);
			if(!instance.loaded) ServerLog.log(ServerLog.ERROR, "Error loading the Server instance...", null);
			else ServerLog.log(ServerLog.INFO, "Sucessfully created a Server instance.. note (you can currently only have one server at a time).", null);
			return instance;
		} else { 
			ServerLog.log(ServerLog.INFO, "I built this script to only work with one Server instance at a time... I may add multi use later. get the current created instance with 'getInstance()'", null);
			return instance;
		}
	}
	public synchronized void setConnectionTimeout(int millis) { connection_timeout = millis < -1 ? 0:millis; }
	public synchronized void setMaxTimeouts(int max) { max_timeouts = max < 1 ? 1:max; }
	public static ServerInstance getInstance() { return instance; }
	public List<DeviceListener> listeners = new ArrayList<DeviceListener>();
	public List<DeviceListener> getDeviceListeners() { return listeners; }
	public synchronized void addDeviceListener(DeviceListener listener) { listeners.add(listener); }
	public synchronized void removeDeviceListener(DeviceListener listener) {
		int i = listeners.indexOf(listener);
		if(i>-1)listeners.remove(i);
	}
	private boolean ignore_fnf = true; /* fnf = File not found */
	
	/** Default, ignores files that's not found... */
	public final void setIgnoreFileNotFound(boolean b) { ignore_fnf = b; }
	
	public Map<String, Resource> website_path = new HashMap<String, Resource>();
	/**
	 * <h1>To set the responce code of the page</h1>
	 * This is default '200 OK'<br>
	 * You can either use a predefined variable from {@link Status} or just write a number to set the responce code.
	 * <br><br>
	 * <h1>To set the ContentType of the page</h1>
	 * This is already done by {@link ContentType#FindByExtension(String)} but you can specify your own by adding a ContentType object.
	 * <br><br>
	 * @param filePath
	 * @param websitePath
	 * @param obj
	 * @throws FileNotFoundException
	 */
	public void addPath(String filePath, String websitePath, Object... obj) throws FileNotFoundException {
		if(server == null) { ServerLog.log(ServerLog.ERROR, "Server is null!", null); return; }
		
		ContentType type = ContentType.TEXT_PLAIN;
		if(filePath.lastIndexOf(".") > -1) type = ContentType.FindByExtension(filePath.substring(filePath.lastIndexOf(".")));
		
		File f = new File(filePath);
		if(!f.exists()) {
			FileNotFoundException e = new FileNotFoundException("The file '" + f  + "' does not exist!");
			ServerLog.log(ignore_fnf ? ServerLog.WARNING:ServerLog.ERROR, "The file '" + f  + "' does not exist!", e);
			if(!ignore_fnf) throw e;
		} else {
			ServerLog.log(ServerLog.INFO, "Adding '" + websitePath + "' to website! " + type, null);
			Status s = Status.OK;
			for(Object o : obj) {
				if(o instanceof Status) s = (Status)o;
				if(o instanceof Integer) s = Status.getStatus((Integer)o);
				if(o instanceof ContentType) type = (ContentType)o;
			}
			website_path.put(websitePath, new Resource(f, type, s));
		}
	}
	
	protected synchronized void requestSite(Header header, Device device) {
		if(header == null) return;
		String path = header.resource;
		if(website_path.containsKey(path)) {
			Resource r = website_path.get(path);
			try {
				Header H = Header.create();
				H.setProtocol(http_version);
				H.setStatus(r.getStatus());
				H.setLastModified(Date.getDate(1, 1, 23, 1, 2002, 59, 0));
				H.setSimpleDate(Date.getDate());
				H.setContentType(r.getType());
				H.setConnection(KEEP_ALIVE);
				H.set("Cache-Control", "private, no-store, no-cache, max-age=0");
				
				try(FileInputStream stream = new FileInputStream(r.getFile())) {
					byte[] bytes = new byte[(int)r.getFile().length()];
					stream.read(bytes);
					H.setData(bytes);
				} catch(Exception e) { e.printStackTrace(); }
				
				try {
					device.WriteToClient(H);
				} catch(Exception e) { e.printStackTrace(); }
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			DeviceEvent event = DeviceEvent.create(device, header);
			for(DeviceListener l : listeners) { if(event.consumed()) break; l.clientConnection(event); }
			if(!event.consumed()) {
				Header H = Header.create();
				H.setProtocol(HTTP.HTTP_1_1);
				H.setContentType(ContentType.TEXT_PLAIN);
				H.setConnection(CLOSE);
				try { H.setData("\n\n".getBytes()); } catch(Exception e) { e.printStackTrace(); }
				device.WriteToClient(H);
			}
		}
	}
}