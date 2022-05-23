package updater.credosc.com;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTime {

	public static String now() {
		SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		return formatter.format(date);
	}
}
