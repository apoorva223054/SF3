/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.synchistory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GMTDate
{

	Date date;

	public String incrementGmtDateByOneHour(String date) throws ParseException
	{

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date dateTimes = dateFormatter.parse(date);
		Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gmtCal.setTime(dateTimes);
		gmtCal.add(Calendar.HOUR, 1);
		this.date = gmtCal.getTime();
		String incrementedDateByHour = dateFormatter.format(gmtCal.getTime());
		return incrementedDateByHour;
	}

	public String incrementGmtDateByOneDay(String date) throws ParseException
	{

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date dateTimes = dateFormatter.parse(date);
		Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gmtCal.setTime(dateTimes);
		gmtCal.add(Calendar.DATE, 1);
		this.date = gmtCal.getTime();
		String incrementedDateByHour = dateFormatter.format(gmtCal.getTime());
		return incrementedDateByHour;
	}

	public String incrementTimeByMinutes(String date, int minutes) throws ParseException
	{

		DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date dateTimes = dateFormatter.parse(date);
		Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gmtCal.setTime(dateTimes);
		gmtCal.add(Calendar.MINUTE, minutes);
		this.date = gmtCal.getTime();
		String incrementedDateByMinutes = dateFormatter.format(this.date);
		return incrementedDateByMinutes;
	}

	public Date getDate()
	{
		return date;
	}

}
