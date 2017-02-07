package scripters.api.network;

public interface MessageListener {
	public void message(int id, String message, Exception error);
}