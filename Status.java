package scripters.api.network;

/**
 * This class includes some of the Return codes.<br>
 */
public final class Status implements EasyPrint {
	public static final Status CONTINUE = new Status(100 ,"Continue");
	public static final Status SWITCHING_PROTOCOLS = new Status(101 ,"Switching Protocols");
	public static final Status CHECKPOINT = new Status(103 ,"Checkpoint");
	public static final Status OK = new Status(200 ,"OK");
	public static final Status CREATED = new Status(201 ,"Created");
	public static final Status ACCEPTED = new Status(202 ,"Accepted");
	public static final Status NON_AUTHORITATIVE_INFORMATION = new Status(203 ,"Non-Authoritative Information");
	public static final Status NO_CONTENT = new Status(204 ,"No Content");
	public static final Status RESET_CONTENT = new Status(205 ,"Reset Content");
	public static final Status PARTIAL_CONTENT = new Status(206 ,"Partial Content");
	public static final Status MULTIPLE_CHOICES = new Status(300 ,"Multiple Choices");
	public static final Status MOVED_PERMANENTLY = new Status(301 ,"Moved Permanently");
	public static final Status FOUND = new Status(302 ,"Found");
	public static final Status SEE_OTHER = new Status(303 ,"See Other");
	public static final Status NOT_MODIFIED = new Status(304 ,"Not Modified");
	public static final Status SWITCH_PROXY = new Status(306 ,"Switch Proxy");
	public static final Status TEMPORARY_REDIRECT = new Status(307 ,"Temporary Redirect");
	public static final Status RESUME_INCOMPLETE = new Status(308 ,"Resume Incomplete");
	public static final Status BAD_REQUEST = new Status(400 ,"Bad Request");
	public static final Status UNAUTHORIZED = new Status(401 ,"Unauthorized");
	public static final Status PAYMENT_REQUIRED = new Status(402 ,"Payment Required");
	public static final Status FORBIDDEN = new Status(403 ,"Forbidden");
	public static final Status NOT_FOUND = new Status(404 ,"Not Found");
	public static final Status METHOD_NOT_ALLOWED = new Status(405 ,"Method Not Allowed");
	public static final Status NOT_ACCEPTABLE = new Status(406 ,"Not Acceptable");
	public static final Status PROXY_AUTHENTICATION_REQUIRED = new Status(407 ,"Proxy Authentication Required");
	public static final Status REQUEST_TIMEOUT = new Status(408 ,"Request Timeout");
	public static final Status CONFLICT = new Status(409 ,"Conflict");
	public static final Status GONE = new Status(410 ,"Gone");
	public static final Status LENGTH_REQUIRED = new Status(411 ,"Length Required");
	public static final Status PRECONDITION_FAILED = new Status(412 ,"Precondition Failed");
	public static final Status REQUEST_ENTITY_TOO_LARGE = new Status(413 ,"Request Entity Too Large");
	public static final Status REQUEST_URI_TOO_LONG = new Status(414 ,"Request-URI Too Long");
	public static final Status UNSUPPORTED_MEDIA_TYPE = new Status(415 ,"Unsupported Media Type");
	public static final Status REQUESTED_RANGE_NOT_SATISFIABLE = new Status(416 ,"Requested Range Not Satisfiable");
	public static final Status EXPECTATION_FAILED = new Status(417 ,"Expectation Failed");
	public static final Status INTERNAL_SERVER_ERROR = new Status(500 ,"Internal Server Error");
	public static final Status NOT_IMPLEMENTED = new Status(501 ,"Not Implemented");
	public static final Status BAD_GATEWAY = new Status(502 ,"Bad Gateway");
	public static final Status SERVICE_UNAVAILABLE = new Status(503 ,"Service Unavailable");
	public static final Status GATEWAY_TIMEOUT = new Status(504 ,"Gateway Timeout");
	public static final Status HTTP_VERSION_NOT_SUPPORTED = new Status(505 ,"HTTP Version Not Supported");
	public static final Status NETWORK_AUTHENTICATION_REQUIRED = new Status(511, "Network Authentication Required");
	
	private final String msg;
	private final int id;
	private Status(int id, String msg) {
		this.id = id;
		this.msg = msg;
	}
	
	public final String getMsg() { return msg; }
	public final int getID() { return id; }
	public String toString() { return id + " " + msg; }
	
	public static final Status getStatus(int id) {
		java.lang.reflect.Field[] fields = Status.class.getFields();
		
		for(java.lang.reflect.Field f : fields) {
			Status s = null;
			try { s = (Status)f.get(new Status(0,"")); } catch(Exception e) { e.printStackTrace(); }
			if(s.id == id) return s;
		}
		return Status.NOT_FOUND;
	}
}
