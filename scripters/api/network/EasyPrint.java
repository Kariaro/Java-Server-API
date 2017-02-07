package scripters.api.network;

public interface EasyPrint extends CharSequence {
	public default char charAt(int index) { return toString().charAt(index); }
	public default int length() { return toString().length(); }
	public default CharSequence subSequence(int start, int end) { return toString().subSequence(start, end); }
}
