package scripters.api.network;

public final class DeviceEvent {
	private boolean consume = false;
	private Device source;
	private Header packet;
	private DeviceEvent(Device source, Header packet) { this.source = source; this.packet = packet; }
	public static DeviceEvent create(Device source, Header packet) { return new DeviceEvent(source, packet); }
	
	public Device getDevice() { return consume ? null:source; }
	public Header getPacket() { return consume ? null:packet; }
	
	public DeviceEvent clone() { return new DeviceEvent(source, packet); }
	
	public void consume() { consume = true; }
	boolean consumed() { return consume; }
}
