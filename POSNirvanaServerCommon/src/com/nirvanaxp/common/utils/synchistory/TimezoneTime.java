/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.synchistory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.time.Timezone;

public class TimezoneTime {

	// private static final NirvanaLogger logger = new
	// NirvanaLogger(TimezoneTime.class.getName());

	public static Date getSunday(Date today) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(today);

		int dow = cal.get(Calendar.DAY_OF_WEEK);

		while (dow != Calendar.SUNDAY) {
			int date = cal.get(Calendar.DATE);

			int month = cal.get(Calendar.MONTH);

			int year = cal.get(Calendar.YEAR);

			if (date == getMonthLastDate(month, year)) {

				if (month == Calendar.DECEMBER) {
					month = Calendar.JANUARY;

					cal.set(Calendar.YEAR, year + 1);
				} else {
					month++;
				}

				cal.set(Calendar.MONTH, month);

				date = 1;
			} else {
				date++;
			}

			cal.set(Calendar.DATE, date);

			dow = cal.get(Calendar.DAY_OF_WEEK);
		}

		return cal.getTime();
	}

	private static int getMonthLastDate(int month, int year) {
		switch (month) {
		case Calendar.JANUARY:
		case Calendar.MARCH:
		case Calendar.MAY:
		case Calendar.JULY:
		case Calendar.AUGUST:
		case Calendar.OCTOBER:
		case Calendar.DECEMBER:
			return 31;

		case Calendar.APRIL:
		case Calendar.JUNE:
		case Calendar.SEPTEMBER:
		case Calendar.NOVEMBER:
			return 30;

		default: // Calendar.FEBRUARY
			return year % 4 == 0 ? 29 : 28;
		}
	}

	public String[] getCurrentTimeofLocation(String locationId, EntityManager em) {
		String currentDate = "";
		// int locationID = 0;
		String[] timeArray = new String[3];

		Location l = em.find(Location.class, locationId);
		// locationID = l.getId();
		// if (l.getTimezoneId() == null) {
		// locationID = l.getLocationsId();
		// }
		Timezone t = em.find(Timezone.class, l.getTimezoneId());
		String timezone = t.getDisplayName();

		String[] parts = timezone.split(" ");
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(parts[1]));
		// DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String month = "" + (cal.get(Calendar.MONTH) + 1);

		String day = "" + cal.get(Calendar.DAY_OF_MONTH);
		int dayNew = 0;
		if (day != null) {
			dayNew = Integer.parseInt(day);
			if (dayNew < 10) {
				day = "0" + day;
			}
		}

		int monthNew = 0;
		if (month != null) {
			monthNew = Integer.parseInt(month);
			if (monthNew < 10) {
				month = "0" + month;
			}
		}

		String hour = "" + cal.get(Calendar.HOUR_OF_DAY);
		int hourNew = 0;
		if (hour != null) {
			hourNew = Integer.parseInt(hour);
			if (hourNew < 10) {
				hour = "0" + hour;
			}
		}

		String min = "" + cal.get(Calendar.MINUTE);
		int minNew = 0;
		if (min != null) {
			minNew = Integer.parseInt(min);
			if (minNew < 10) {
				min = "0" + min;
			}
		}

		String sec = "" + cal.get(Calendar.SECOND);
		int secNew = 0;
		if (sec != null) {
			secNew = Integer.parseInt(sec);
			if (secNew < 10) {
				sec = "0" + sec;
			}
		}

		currentDate = cal.get(Calendar.YEAR) + "-" + month + "-" + day;
		timeArray[0] = currentDate.toString();
		timeArray[1] = cal.getTimeInMillis() + "";
		timeArray[2] = currentDate + " " + hour + ":" + min + ":" + sec;

		return timeArray;
	}

	public String[] getUserTimeByLocation(String userTime, String locationId,
			EntityManager em) {
		String currentDate = "";
		// String locationId = 0;
		String[] timeArray = new String[3];

		Location l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);
		// locationID = l.getId();
		// if (l.getTimezoneId() == null) {
		// locationID = l.getLocationsId();
		// }
		// SimpleDateFormat dateTime = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

		Timezone t = em.find(Timezone.class, l.getTimezoneId());
		String timezone = t.getDisplayName();

		String[] parts = timezone.split(" ");
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(parts[1]));
		// DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String month = "" + (cal.get(Calendar.MONTH) + 1);

		String day = "" + cal.get(Calendar.DAY_OF_MONTH);
		int dayNew = 0;
		if (day != null) {
			dayNew = Integer.parseInt(day);
			if (dayNew < 10) {
				day = "0" + day;
			}
		}

		int monthNew = 0;
		if (month != null) {
			monthNew = Integer.parseInt(month);
			if (monthNew < 10) {
				month = "0" + month;
			}
		}

		String hour = "" + cal.get(Calendar.HOUR_OF_DAY);
		int hourNew = 0;
		if (hour != null) {
			hourNew = Integer.parseInt(hour);
			if (hourNew < 10) {
				hour = "0" + hour;
			}
		}

		String min = "" + cal.get(Calendar.MINUTE);
		int minNew = 0;
		if (min != null) {
			minNew = Integer.parseInt(min);
			if (minNew < 10) {
				min = "0" + min;
			}
		}

		String sec = "" + cal.get(Calendar.SECOND);
		int secNew = 0;
		if (sec != null) {
			secNew = Integer.parseInt(sec);
			if (secNew < 10) {
				sec = "0" + sec;
			}
		}

		currentDate = cal.get(Calendar.YEAR) + "-" + month + "-" + day;
		timeArray[0] = currentDate.toString();
		timeArray[1] = cal.getTimeInMillis() + "";
		timeArray[2] = currentDate + " " + hour + ":" + min + ":" + sec;

		return timeArray;
	}

	public long getDayCount(String locationId, EntityManager em, String start,
			String end) throws ParseException {

		start = getDateAccordingToGMT(start, locationId, em);
		end = getDateAccordingToGMT(end, locationId, em);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		long diff = -1;
		// try
		// {
		Date dateStart = simpleDateFormat.parse(start);
		Date dateEnd = simpleDateFormat.parse(end);

		// time is always 00:00:00 so rounding should help to ignore the
		// missing hour when going from winter to summer time as well as the
		// extra hour in the other direction
		diff = Math.round((dateEnd.getTime() - dateStart.getTime())
				/ (double) 86400000);
		// }
		// catch (Exception e)
		// {
		// // handle the exception according to your own situation
		// logger.severe(e);
		//
		// }
		return diff;
	}

	public String getCurrentDate(EntityManager em, String locationId) {

		
		String localTimezoneName = "";

		String queryString = "select distinct display_name  from locations l left join timezone t on l.timezone_id=t.id where l.id= "
				+ locationId;
		Object obj = em.createNativeQuery(queryString).getSingleResult();
		if (obj != null) {
			// if this has primary key not 0
			localTimezoneName = (String) obj;
		}

		Date today = new Date(new TimezoneTime().getGMTTimeInMilis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setTimeZone(TimeZone.getTimeZone(localTimezoneName.substring(4)));
		return format.format(today);

	}
	
	public String getCurrentTime(EntityManager em, String locationId) {

		String localTimezoneName = "";

		String queryString = "select distinct display_name  from locations l left join timezone t on l.timezone_id=t.id where l.id= "
				+ locationId;
		Object obj = em.createNativeQuery(queryString).getSingleResult();
		if (obj != null) {
			// if this has primary key not 0
			localTimezoneName = (String) obj;
		}
             Calendar calendar = Calendar.getInstance();
             SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            timeFormat.setTimeZone(TimeZone
                    .getTimeZone(localTimezoneName.substring(4)));

            return timeFormat.format(calendar.getTime());
        

    }
	
	public String getCurrentTimeHHMM(EntityManager em, String locationId) {

		String localTimezoneName = "";

		String queryString = "select distinct display_name  from locations l left join timezone t on l.timezone_id=t.id where l.id= '"
				+ locationId+"'";
		Object obj = em.createNativeQuery(queryString).getSingleResult();
		if (obj != null) {
			// if this has primary key not 0
			localTimezoneName = (String) obj;
		}
             Calendar calendar = Calendar.getInstance();
             SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeFormat.setTimeZone(TimeZone
                    .getTimeZone(localTimezoneName.substring(4)));

            return timeFormat.format(calendar.getTime());
        

    }

	public String getDateAccordingToGMTForWalkin(String date, String locationId,
			EntityManager em) {
		String gmt = null;
		Location l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);
		Timezone t = em.find(Timezone.class, l.getTimezoneId());
		TimeZone toTime = TimeZone.getTimeZone("GMT");
		TimeZone fromTime = null;
		if (t.getDisplayName() != null) {
			fromTime = TimeZone.getTimeZone(t.getDisplayName().substring(4));
		}
		gmt = convertTimeZone(date, fromTime, toTime);
		return date;
	}
	
	public String getDateAccordingToGMT(String date, String locationId,
			EntityManager em) {
		String gmt = null;
		Location l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);
		Timezone t = em.find(Timezone.class, l.getTimezoneId());
		TimeZone toTime = TimeZone.getTimeZone("GMT");
		TimeZone fromTime = null;
		if (t.getDisplayName() != null) {
			fromTime = TimeZone.getTimeZone(t.getDisplayName().substring(4));
		}
		gmt = convertTimeZone(date, fromTime, toTime);
		return gmt;
	}

	public String getDateAccordingFromLocationToGMT(String date,
			String locationId, EntityManager em) {
		String gmt = null;
		Location l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);
		Timezone t = em.find(Timezone.class, l.getTimezoneId());
		TimeZone toTime = TimeZone.getTimeZone("GMT");
		TimeZone fromTime = null;
		if (t.getDisplayName() != null) {
			fromTime = TimeZone.getTimeZone(t.getDisplayName().substring(4));
		}
		gmt = convertTimeZone(date, toTime, fromTime);
		return gmt;
	}

	public String getDateAccordingToGMTForConnection(EntityManager em,
			String date, String locationId) {
		String gmt = null;
		String localTimezoneName = "";

		String queryString = "select distinct display_name  from locations l left join timezone t on l.timezone_id=t.id where l.id= '"
				+ locationId+"'";
		Object obj = em.createNativeQuery(queryString).getSingleResult();
		if (obj != null) {
			// if this has primary key not 0
			localTimezoneName = (String) obj;
		}

		TimeZone fromTime = null;
		TimeZone toTime = TimeZone.getTimeZone("GMT");
		if (localTimezoneName != null) {
			fromTime = TimeZone.getTimeZone(localTimezoneName.substring(4));
		}

		gmt = convertTimeZone(date, fromTime, toTime);

		return gmt;
	}

	public String getDateAccordingToLocationFromGMTForConnection(
			EntityManager em, String date, String locationId) {
		String gmt = null;

		String localTimezoneName = "";

		String queryString = "select distinct display_name  from locations l left join timezone t on l.timezone_id=t.id where l.id= ?";
		Object obj = em.createNativeQuery(queryString)
				.setParameter(1, locationId).getSingleResult();
		if (obj != null) {
			// if this has primary key not 0
			localTimezoneName = (String) obj;
		}

		TimeZone fromTime = null;
		TimeZone toTime = TimeZone.getTimeZone("GMT");
		if (localTimezoneName != null) {
			fromTime = TimeZone.getTimeZone(localTimezoneName.substring(4));
		}

		gmt = convertTimeZone(date, toTime, fromTime);

		return gmt;
	}

	public String getDateTimeFromGMTToLocation(EntityManager em, String date,
			String locationId) {
		String gmt = null;

		String localTimezoneName = "";

		String queryString = "select distinct display_name  from locations l left join timezone t on l.timezone_id=t.id where l.id= ?";
		Object obj = em.createNativeQuery(queryString)
				.setParameter(1, locationId).getSingleResult();
		if (obj != null) {
			// if this has primary key not 0
			localTimezoneName = (String) obj;
		}

		TimeZone toTime = null;
		TimeZone fromTime = TimeZone.getTimeZone("GMT");
		if (localTimezoneName != null) {
			toTime = TimeZone.getTimeZone(localTimezoneName.substring(4));
		}

		gmt = convertTimeZone(date, fromTime, toTime);

		return gmt;
	}

	public String convertTimeZone(String date, TimeZone fromTZ, TimeZone toTZ) {

		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int day = Integer.parseInt(date.substring(8, 10));
		int hour = Integer.parseInt(date.substring(11, 13));
		int min = Integer.parseInt(date.substring(14, 16));
		int seconds = Integer.parseInt(date.substring(17, 19));

		DateTime dt = new DateTime(DateTimeZone.forTimeZone(fromTZ)).withDate(
				year, month, day).withTime(hour, min, seconds, 00);

		DateTime convertedDate = dt.withZone(DateTimeZone.forTimeZone(toTZ));

		String monthOfYear = "" + convertedDate.getMonthOfYear();
		if (convertedDate.getMonthOfYear() < 10) {
			monthOfYear = "0" + convertedDate.getMonthOfYear();
		}
		String dayOfMonth = "" + convertedDate.getDayOfMonth();
		if (convertedDate.getDayOfMonth() < 10) {
			dayOfMonth = "0" + convertedDate.getDayOfMonth();
		}

		String hourOfDay = "" + convertedDate.getHourOfDay();
		if (convertedDate.getHourOfDay() < 10) {
			hourOfDay = "0" + convertedDate.getHourOfDay();
		}

		String minutesOfHour = "" + convertedDate.getMinuteOfHour();
		if (convertedDate.getMinuteOfHour() < 10) {
			minutesOfHour = "0" + convertedDate.getMinuteOfHour();
		}
		String secondOfHour = "" + convertedDate.getSecondOfMinute();
		if (convertedDate.getSecondOfMinute() < 10) {
			secondOfHour = "0" + convertedDate.getSecondOfMinute();
		}

		String str = convertedDate.getYear() + "-" + monthOfYear + "-"
				+ dayOfMonth + " " + hourOfDay + ":" + minutesOfHour + ":"
				+ secondOfHour;

		return str;
	}

	public String getDateFromTimeStamp(Timestamp timestamp) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(timestamp.getTime()));
		String month = "" + (cal.get(Calendar.MONTH) + 1);

		String day = "" + cal.get(Calendar.DAY_OF_MONTH);
		int dayNew = 0;
		if (day != null) {
			dayNew = Integer.parseInt(day);
			if (dayNew < 10) {
				day = "0" + day;
			}
		}

		int monthNew = 0;
		if (month != null) {
			monthNew = Integer.parseInt(month);
			if (monthNew < 10) {
				month = "0" + month;
			}
		}

		String hour = "" + cal.get(Calendar.HOUR_OF_DAY);
		int hourNew = 0;
		if (hour != null) {
			hourNew = Integer.parseInt(hour);
			if (hourNew < 10) {
				hour = "0" + hour;
			}
		}

		String min = "" + cal.get(Calendar.MINUTE);
		int minNew = 0;
		if (min != null) {
			minNew = Integer.parseInt(min);
			if (minNew < 10) {
				min = "0" + min;
			}
		}

		String sec = "" + cal.get(Calendar.SECOND);
		int secNew = 0;
		if (sec != null) {
			secNew = Integer.parseInt(sec);
			if (secNew < 10) {
				sec = "0" + sec;
			}
		}

		String currentDate = cal.get(Calendar.YEAR) + "-" + month + "-" + day;
		currentDate = currentDate + " " + hour + ":" + min + ":" + sec;

		return currentDate;

	}

	public String getDateAsString(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Using DateFormat format method we can create a string
		// representation of a date with the defined format.
		String reportDate = df.format(date);
		return reportDate;
	}

	/**
	 * Gets the time from created.
	 *
	 * @param timeInMilliSec
	 *            the time in milli sec
	 * @return the time from created
	 */
	public String getTimeFromCreatedUpdated(long timeInMilliSec,
			EntityManager em, String locationId) {

		Calendar calendar = Calendar.getInstance();

		String localTimezoneName = "";

		String queryString = "select distinct display_name  from locations l left join timezone t on l.timezone_id=t.id where l.id= '"
				+ locationId+"'";
		Object obj = em.createNativeQuery(queryString).getSingleResult();
		if (obj != null) {
			// if this has primary key not 0
			localTimezoneName = (String) obj;
		}

		calendar.setTimeInMillis(timeInMilliSec);
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
		formatter.setTimeZone(TimeZone.getTimeZone(localTimezoneName
				.substring(4)));
		String time = formatter.format(calendar.getTime());

		return time;
	}

	public String getLocationSpecificTimeToAdd(String locationId, EntityManager em) {
		String localTimezoneName = "";

		String queryString = "select t.display_name  from locations l left join timezone t on l.timezone_id=t.id where l.id= ?"
				;
		Object obj = em.createNativeQuery(queryString).setParameter(1, locationId).getSingleResult();
		if (obj != null) {
			localTimezoneName = (String) obj;
		}

		String[] parts = localTimezoneName.split(" ");

		return parts[1].substring(3);

	}

	public String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String getYearOfLocation(EntityManager em, String locationId) {

		
		String localTimezoneName = "";

		String queryString = "select distinct display_name  from locations l left join timezone t on l.timezone_id=t.id where l.id= "
				+ locationId;
		Object obj = em.createNativeQuery(queryString).getSingleResult();
		if (obj != null) {
			// if this has primary key not 0
			localTimezoneName = (String) obj;
		}

		Date today = new Date(new TimezoneTime().getGMTTimeInMilis());
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		df.setTimeZone(TimeZone.getTimeZone(localTimezoneName.substring(4)));
		return df.format(today);

	}
	
	public int getDOYOfLocation(EntityManager em, String locationId) {

		
		String localTimezoneName = "";

		String queryString = "select distinct display_name  from locations l left join timezone t on l.timezone_id=t.id where l.id= "
				+ locationId;
		Object obj = em.createNativeQuery(queryString).getSingleResult();
		if (obj != null) {
			// if this has primary key not 0
			localTimezoneName = (String) obj;
		}

		Date today = new Date(new TimezoneTime().getGMTTimeInMilis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		format.setTimeZone(TimeZone.getTimeZone(localTimezoneName.substring(4)));
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(today);
		
		return calendar.get(Calendar.DAY_OF_YEAR);

	}
	public long getGMTTimeInMilis(){

		//return    Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 format.setTimeZone(TimeZone.getTimeZone("UTC"));
		 Date date = new Date();
		 SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Date dateTime =null;
		try {
			dateTime = dateParser.parse(format.format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  dateTime.getTime();
	
	}
}