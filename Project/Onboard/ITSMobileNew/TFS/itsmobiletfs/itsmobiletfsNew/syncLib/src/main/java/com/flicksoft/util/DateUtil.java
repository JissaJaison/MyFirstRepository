/**
 * 
 */
package com.flicksoft.util;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
/**
 * Class that contains the date time related functions
 *
 */
public class DateUtil {
	  public static final Date MIN_VALUE = new Date(Long.MIN_VALUE);
	  public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	  public static String now() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	    return sdf.format(cal.getTime());

	  }
}
