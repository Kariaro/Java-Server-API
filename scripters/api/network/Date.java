package scripters.api.network;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.regex.Pattern;

public class Date {
	public static final java.util.Calendar c = java.util.Calendar.getInstance();
	public static String getDate() {
		return getDate(
			java.time.LocalDate.now().getDayOfWeek().getValue(),
			java.time.LocalDate.now().getDayOfMonth(),c.get(11),
			java.time.LocalDate.now().getMonth().getValue(),
			java.time.LocalDate.now().getYear(),c.get(12),c.get(13)
		);
	}
	/**
	 * 
	 * @param dow	= Day of Week
	 * @param dom	= Day of Month
	 * @param hod	= Hour of Day
	 * @param moy	= Month of Year
	 * @param year	= Year
	 * @param min	= Minute
	 * @param sec	= Second
	 * 
	 * @return
	 */
	public static String getDate(int dow, int dom, int hod, int moy, int year, int min, int sec) {
		StringBuilder builder = new StringBuilder();
		builder.append(DayOfWeek.of(dow).getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH)).append(", ")
		.append(dom).append(" ")
		.append(Month.of(moy).getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH)).append(" ")
		.append(year).append(" ")
		.append(hod).append(":").append(min).append(":").append(sec).append(" ")
		.append(c.getTimeZone().getDisplayName((hod > 8 && hod < 16), 0, java.util.Locale.ENGLISH));
		return builder.toString();
	}
	
	public static boolean validate(String s) {
		Pattern p = Pattern.compile("^([A-Za-z]){3}, ([0-9]+) ([A-Za-z]){3} ([0-9]+) ([0-9]+):([0-9]+):([0-9]+) ([A-Za-z]+)$");
		return p.matcher(s).find();
	}
}
