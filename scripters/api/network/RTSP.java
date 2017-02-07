package scripters.api.network;

public final class RTSP extends Protocol implements EasyPrint {
	public static final RTSP RTSP_0_9 = new RTSP(0, 9);
	public static final RTSP RTSP_1_0 = new RTSP(1, 0);
	public static final RTSP RTSP_1_1 = new RTSP(1, 1);
	public static final RTSP RTSP_2_0 = new RTSP(2, 0);
	public RTSP(int major, int minor) { super(major, minor); }
	@Override public String toString() { return "RTSP/" + major + "." + minor; }
}