package scripters.api.network;

/** This class is responsible for all the logging */
public class ServerLog {
	public static final int ERROR   = 0;
	public static final int WARNING = 1;
	public static final int INFO    = 2;
	private ServerLog() {}
	
	private static boolean auto_print = true;
	private static MessageListener listener = null;
	
	/** When Server Logs a message, Print it. Default on.<br>If disabled it calls a message listener*/
	public static final void setWriteLogs(boolean b) { auto_print = b; }
	
	/** Log a server message.<br><br>
	  * This method is private because this is made for server logs.<br>
	  * And if you really want to log your own messages, use<br><br>
	  * {@code System.out.println("your text");}*/
	static final void log(int id, String message, Exception error) {
		if(auto_print) {
			switch(id) {
				case 0: System.out.print(ERROR_TAG); break;
				case 1: System.out.print(WARNING_TAG); break;
				case 2: System.out.print(INFO_TAG); break;
			}
			System.out.println(message);
			if(error != null) { error.printStackTrace(System.out); System.out.println(); }
		} else if(listener != null) listener.message(id, message, error);
	}
	
	/** I don't know why anyone whould use more than one message listener so. */
	public static final void setMessageListener(MessageListener l) { listener = l; }
	
	/** How a pre-built error message might be displayed.<br>Example '&lttag&gt: &ltmessage&gt' */
	public static final void setErrorTag(String tag) { ERROR_TAG = tag; }
	public static String ERROR_TAG = "[Error]: ";
	
	/** How a pre-built error message might be displayed.<br>Example '&lttag&gt: &ltmessage&gt' */
	public static final void setWarningTag(String tag) { WARNING_TAG = tag; }
	public static String WARNING_TAG = "[Warning]: ";
	
	/** How a pre-built error message might be displayed.<br>Example '&lttag&gt: &ltmessage&gt' */
	public static final void setInfoTag(String tag) { INFO_TAG = tag; }
	public static String INFO_TAG = "[Info]: ";
}
