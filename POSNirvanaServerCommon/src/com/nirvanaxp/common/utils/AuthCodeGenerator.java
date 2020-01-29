/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils;

import java.util.Random;

import com.nirvanaxp.server.util.NirvanaLogger;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthCodeGenerator.
 */
public class AuthCodeGenerator
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(AuthCodeGenerator.class.getName());

	/**
	 * Generate auth code.
	 *
	 * @return the string
	 */
	public static String generateAuthCode()
	{
		/*logger.finer("Entering generateAuthCode()");
		// getting the current time in nanoseconds
		long decimalNumber = System.nanoTime();
		logger.finer("current time in nanoseconds: " + decimalNumber);

		// To convert time stamp to alphanumeric code.
		// We need to convert base10(decimal) to base36
		String strBaseDigits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String strTempVal = "";
		int mod = 0;
		// String concat is costly, instead we could have use string-buffer or
		// string-builder
		// but here it wont make much difference.
		while (decimalNumber != 0)
		{
			mod = (int) (decimalNumber % 36);
			strTempVal = strBaseDigits.substring(mod, mod + 1) + strTempVal;
			decimalNumber = decimalNumber / 36;
		}*/
		
		String strTempVal = (100000 + new Random().nextInt(900000)) +"";
				
		logger.finer("alphanumeric code generated from TimeStamp : ", strTempVal);
		return strTempVal;

	}

}
