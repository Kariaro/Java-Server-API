package scripters.api.network;

public interface DeviceListener {
	public void clientConnection(DeviceEvent event);
	public void clientDisconnect(DeviceEvent event);
}
