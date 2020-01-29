/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

public class CalculationRoundUp
{

	public Double fixDigitAfterDecimalPoint(Double value)
	{

		/*
		 * Double resultValue; resultValue=Double.parseDouble(new
		 * DecimalFormat("##.##").format( value));
		 */
		return roundOffTo2Digit(value);

	}

	public Double roundOffTo2Digit(Double value)
	{

		long valueIntoInt = (long) (value * 1000);
		String valueInt = "" + valueIntoInt;
		String s = valueInt.substring(valueInt.length() - 1);
		Integer lastdigit = Integer.parseInt(s);
		double convertValue = 0;
		if (lastdigit >= 5)
		{
			valueIntoInt = valueIntoInt + (10 - lastdigit);
			convertValue = (double) valueIntoInt;
		}
		else
		{
			convertValue = (double) valueIntoInt;
		}
		double finalValue = (double) (convertValue / 1000);
		String newAmt = String.format("%.2f", finalValue);
		double resultValue = Double.parseDouble(newAmt);

		return resultValue;

	}
	
	public Double roundOffTo5Digit(Double value)
	{

		long valueIntoInt = (long) (value * 1000);
		String valueInt = "" + valueIntoInt;
		String s = valueInt.substring(valueInt.length() - 1);
		Integer lastdigit = Integer.parseInt(s);
		double convertValue = 0;
		if (lastdigit >= 5)
		{
			valueIntoInt = valueIntoInt + (10 - lastdigit);
			convertValue = (double) valueIntoInt;
		}
		else
		{
			convertValue = (double) valueIntoInt;
		}
		double finalValue = (double) (convertValue / 1000);
		String newAmt = String.format("%.5f", finalValue);
		double resultValue = Double.parseDouble(newAmt);

		return resultValue;

	}

	public String checkTwoDigitAfterDecimal(double value)
	{
		String changeValue = "";

		changeValue = String.format("%.2f", value);
		if (changeValue.substring(changeValue.indexOf(".")).length() == 1)
		{
			changeValue = changeValue + "0";
		}

		return changeValue;

	}

}