package scripters.api.network;

public class Protocol implements EasyPrint {
	public final int minor, major;
	protected Protocol(int major, int minor) { this.minor = minor < 0 ? 0:minor; this.major = major < 0 ? 0:major; }
	public final int getMinor() { return minor; }
	public final int getMajor() { return major; }
	public final static Protocol valueOf(String s) {
		s = s.toUpperCase();
		boolean b = java.util.regex.Pattern.compile("^([A-Z\\p{Punct}\\/0-9]+)$").matcher(s).matches();
		if(b) {
			int i = s.indexOf('/');
			if(i < 0) {
				final String name = s;
				return new Protocol(1, 0) { @Override public String toString() { return name + "/1.0"; }; };
			} else {
				final String name = s.substring(0, i);
				String num = s.substring(i + 1);
				i = num.indexOf('.');
				int num1 = 1;
				if(i < 0) {
					try { num1 = Integer.valueOf(num); } catch(Exception e) {}
					return new Protocol(num1, 0) { @Override public String toString() { return name + "/" + getMajor() + ".0"; }; };
				} else {
					int num2 = 0;
					try { num1 = Integer.valueOf(num.substring(0,i)); } catch(Exception e) {}
					try { num2 = Integer.valueOf(num.substring(i+1)); } catch(Exception e) {}
					return new Protocol(num1, num2) { @Override public String toString() { return name + "/" + getMajor() + "." + getMinor(); }; };
				}
			}
		} else return HTTP.HTTP_1_0;
	}
	public String toString() { return "/" + major + "." + minor; }
}
