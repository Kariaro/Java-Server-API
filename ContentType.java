package scripters.api.network;

import java.util.ArrayList;
import java.util.List;

/** I thought about it and removed final from this class so you can create your own ContentType subclass to initialize.<bt>But you can now easly write {@link ContentType#createNew(String, String...)} and then {@link ContentType#addContentType()}*/
public class ContentType extends Content implements EasyPrint {
	public static final ContentType NOT_FOUND = new ContentType("none");
	public static final ContentType IMAGE_JPG = new ContentType("image/jpg", ".jpg", ".jpeg", ".jpe");
	public static final ContentType IMAGE_PNG = new ContentType("image/png", ".png");
	public static final ContentType IMAGE_GIF = new ContentType("image/gif", ".gif");
	public static final ContentType TEXT_HTML = new ContentType("text/html", ".htm", ".html");
	public static final ContentType TEXT_PLAIN = new ContentType("text/plain", ".txt");
	public static final ContentType VIDEO_QUICKTIME = new ContentType("video/quicktime", ".qt", ".mov");
	public static final ContentType VIDEO_MPEG = new ContentType("video/mpeg", ".mpg", ".mpeg", ".mpe");
	public static final ContentType AUDIO_BASIC = new ContentType("audio/basic", ".au", ".snd");
	public static final ContentType AUDIO_X_WAVE = new ContentType("audio/x-wave", ".wav");
	public static final ContentType MULTIPART_FORM_DATA = new ContentType("multipart/form-data");
	public static final ContentType OCTET_STREAM = new ContentType("application/octet-stream", ".class");
	private static final List<ContentType> TYPES = new ArrayList<ContentType>();
	
	private String[] ext;
	private String name;
	private ContentType(String name, String... ext) { this.name = name; this.ext = ext; }
	
	/** If you want to add your ContentType to the list you need to call {@link ContentType#addContentType()}<br><br>
	  * <h1>How to use</h1>
	  * Name is basicly the type name.<br>Example {@code "text/notplain"}<br><br>
	  * Ext is a array of file extensions that your type is listed under, you need a dot in the begining of each extension.<br>Example {@code ".extension"}<br><br> */
	public static ContentType createNew(String name, String... ext) { return new ContentType(name, ext); }
	
	/** If you want to add your custom ContentType to the list.<br>This is not automaticly called and you can't remove added Types! */
	public void addContentType() { TYPES.add(this); }
	
	public int getExtensions() { return ext.length; }
	public String getExtension(int i) { return ext[i]; }
	public String getTypeName() { return name; }
	public boolean match(String name) {
		for(String s : ext) if(name.endsWith(s)) return true;
		return false;
	}
	/** Gets the ContentType from a file extension
	  * @return This returns NOT_FOUND if the extension is not listed by the program.<br>If you want to add your own ContentType see {@link ContentType#createNew(String,String...)} */
	public static ContentType FindByExtension(String ext) {
		for(ContentType t : TYPES) for(String q : t.ext) if(ext.endsWith(q)) return t;
		return NOT_FOUND;
	}
	/** Gest the ContentType from the ContentType name
	  * @return This returns NOT_FOUND if the name was not listed by the program.<br>If you want to add your own ContentType see {@link ContentType#createNew(String,String...)} */
	public static ContentType FindByTypeName(String name) {
		for(ContentType t : TYPES) if(t.name.equals(name)) return t;
		return NOT_FOUND;
	}
	public ContentType getContentType() { return this; }
	public String toString() { return name; }
	
	static {
		TYPES.add(NOT_FOUND);
		TYPES.add(IMAGE_JPG);
		TYPES.add(IMAGE_PNG);
		TYPES.add(IMAGE_GIF);
		TYPES.add(TEXT_HTML);
		TYPES.add(TEXT_PLAIN);
		TYPES.add(VIDEO_QUICKTIME);
		TYPES.add(VIDEO_MPEG);
		TYPES.add(AUDIO_BASIC);
		TYPES.add(AUDIO_X_WAVE);
		TYPES.add(MULTIPART_FORM_DATA);
		TYPES.add(OCTET_STREAM);
	}
}
