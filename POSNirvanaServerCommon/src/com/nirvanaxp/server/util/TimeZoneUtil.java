package com.nirvanaxp.server.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeZoneUtil
{
	private static final NirvanaLogger logger = new NirvanaLogger(TimeZoneUtil.class.getName());
	public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy/MM/dd hh:mm:ss Z";
	
	public static void main (String[] args)
	{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");
		TimeZone zone = TimeZone.getTimeZone("America/New_York");
		System.out.println("TimeZone: "+zone.getID());
		Calendar calHere = Calendar.getInstance();
		sdf.setTimeZone(zone);
		System.out.println("Time in New York: "+sdf.format(calHere.getTime()));
		try
		{
			System.out.println("time in millis: " + sdf.parse(sdf.format(calHere.getTime())).getTime());
		}
		catch (ParseException e)
		{
			
			 logger.severe(e);
		}
		
		try
		{
			System.out.println("sql time in millis: " + new Timestamp(sdf.parse(sdf.format(calHere.getTime())).getTime()).toString());
		}
		catch (ParseException e)
		{
			
			 logger.severe(e);
		}
		
		sdf.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
//		calHere.setTimeZone(TimeZone.getTimeZone("America/Chicago"));

		//Calendar cal = convertToNewTimeZone(calHere, zone);
		System.out.println("Time Here: "+sdf.format(calHere.getTime()));
		try
		{
			System.out.println("time in millis: " + sdf.parse(sdf.format(calHere.getTime())).getTime());
		}
		catch (ParseException e)
		{
			
			 logger.severe(e);
		}
		
		try
		{
			System.out.println("sql time in millis: " + new Timestamp(sdf.parse(sdf.format(calHere.getTime())).getTime()).toString());
		}
		catch (ParseException e)
		{
			
			 logger.severe(e);
		}
	}
	
	public static Calendar convertToGmt(Calendar c) {
	    java.util.Date date = c.getTime();
	    TimeZone tz = c.getTimeZone();
	    long timeInMilliseconds = date.getTime();
	    int offsetFromUTC = tz.getOffset(timeInMilliseconds);
	    Calendar gmtCal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
	    gmtCal.setTime(date);
	    gmtCal.add(Calendar.MILLISECOND, offsetFromUTC);
	    return gmtCal;
	}
	
	public static Calendar convertToNewTimeZone(Calendar calendar, TimeZone timezone) {
	    Calendar newCal = new GregorianCalendar(timezone);
	    newCal.setLenient(false);
	    boolean am = (newCal.get(Calendar.AM_PM) == Calendar.AM);
	    newCal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
	    newCal.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
	    newCal.set(Calendar.DATE, calendar.get(Calendar.DATE));
	    newCal.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
	    newCal.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
	    newCal.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
	    newCal.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND));
	    boolean ampm = (calendar.get(Calendar.AM_PM) == Calendar.PM);
	    if (am && ampm) { // cal = 0 but we want 1
	        newCal.roll(Calendar.AM_PM, 1);
	    } else if (!am && !ampm) { //cal = 1 but we want 0
	        newCal.roll(Calendar.AM_PM, -1);
	    }
	    return newCal;
	}
	public static String convertDateFormat(String date) throws ParseException{

		  SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
	        Date date2 = dt.parse(date);

	        // *** same for the format String below
	        SimpleDateFormat dt1 = new SimpleDateFormat("MM/dd/yyyy");
	       return (dt1.format(date2));
	}
}
