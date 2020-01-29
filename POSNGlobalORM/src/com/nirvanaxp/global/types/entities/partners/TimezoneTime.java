/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.partners;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimezoneTime {

	// private static final NirvanaLogger logger = new
	// NirvanaLogger(TimezoneTime.class.getName());

 
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