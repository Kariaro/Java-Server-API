package scripters.api.network;

public final class HTTP extends Protocol implements EasyPrint {
	public static final HTTP HTTP_0_9 = new HTTP(0, 9);
	public static final HTTP HTTP_1_0 = new HTTP(1, 0);
	public static final HTTP HTTP_1_1 = new HTTP(1, 1);
	public static final HTTP HTTP_2_0 = new HTTP(2, 0);
	public HTTP(int major, int minor) { super(major, minor); }
	@Override public String toString() { return "HTTP/" + major + "." + minor; }
}