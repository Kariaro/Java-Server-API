package scripters.api.network;

public enum Connection {
	CLOSE, NONE, KEEP_ALIVE, UPGRADE;
	public static Connection get(String s) {
		s = s.toUpperCase().replace('-','_');
		for(Connection c : values()) if(c.name().equals(s)) return c;
		return NONE;
	}
	public String toString() { return name().toLowerCase().replace('_','-'); }
}