/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class CreditCard
{

	private String cardNumber;

	/**
	 * Card verification number
	 */
	private String CVNum;

	/**
	 * 
	 */
	private String expiryDate;

	/**
	 * 
	 */
	private String nameOnCard;
	/**
	 * To be extracted through card swipe
	 */
	private String magData;

	/**
	 * Cardholder�s billing address zip code used in address verification.
	 * This parameter will remove invalid characters
	 */
	private String zip;

	/**
	 * Cardholder�s billing street address used in address verification. This
	 * parameter will remove invalid characters.
	 */
	private String street;

	private String track1EncytedHexData;

	private String track1EncytedDataInString;

	private String track2EncytedDataInString;

	private String track2EncytedHexData;

	private String ksn;

	private String extData;

	private String secureFormat;

	private String rawDataInHexString;

	private String trackData;

	private String allowDup;

	private String expMonth;

	private String expYear;

	private String cvv2;

	private String encryptedFormat;

	private String encryptedBlock;

	private String encryptedKey;

	private String maskedData1InStringFormat;

	private String maskedData2InStringFormat;

	public String getEncryptedFormat()
	{
		return encryptedFormat;
	}

	public void setEncryptedFormat(String encryptedFormat)
	{
		this.encryptedFormat = encryptedFormat;
	}

	public String getEncryptedBlock()
	{
		return encryptedBlock;
	}

	public void setEncryptedBlock(String encryptedBlock)
	{
		this.encryptedBlock = encryptedBlock;
	}

	public String getEncryptedKey()
	{
		return encryptedKey;
	}

	public void setEncryptedKey(String encryptedKey)
	{
		this.encryptedKey = encryptedKey;
	}

	public String getAllowDup()
	{
		return allowDup;
	}

	public void setAllowDup(String allowDup)
	{
		this.allowDup = allowDup;
	}

	public String getTrackData()
	{
		return trackData;
	}

	public void setTrackData(String trackData)
	{
		this.trackData = trackData;
	}

	public String getRawDataInHexString()
	{
		return rawDataInHexString;
	}

	public void setRawDataInHexString(String rawDataInHexString)
	{
		this.rawDataInHexString = rawDataInHexString;
	}

	public String getCardNumber()
	{
		return cardNumber;
	}

	public void setCardNumber(String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	public String getCVNum()
	{
		return CVNum;
	}

	public void setCVNum(String cVNum)
	{
		CVNum = cVNum;
	}

	public String getExpiryDate()
	{
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate)
	{
		this.expiryDate = expiryDate;
	}

	public String getNameOnCard()
	{
		return nameOnCard;
	}

	public void setNameOnCard(String nameOnCard)
	{
		this.nameOnCard = nameOnCard;
	}

	public String getMagData()
	{
		return magData;
	}

	public String getTrack1EncytedHexData()
	{
		return track1EncytedHexData;
	}

	public void setTrack1EncytedHexData(String track1EncytedHexData)
	{
		this.track1EncytedHexData = track1EncytedHexData;
	}

	public String getTrack2EncytedHexData()
	{
		return track2EncytedHexData;
	}

	public void setTrack2EncytedHexData(String track2EncytedHexData)
	{
		this.track2EncytedHexData = track2EncytedHexData;
	}

	public void setMagData(String magData)
	{
		this.magData = magData;
	}

	public String getZip()
	{
		return zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getKsn()
	{
		return ksn;
	}

	public void setKsn(String ksn)
	{
		this.ksn = ksn;
	}

	public String getExtData()
	{
		return extData;
	}

	public String getSecureFormat()
	{
		return secureFormat;
	}

	public void setExtData(String extData)
	{
		this.extData = extData;
	}

	public void setSecureFormat(String secureFormat)
	{
		this.secureFormat = secureFormat;
	}

	public String getExtDataWithBothTrack()
	{
		/*
		 * <ExtData> <SecurityInfo>62994901160000400084<\SecurityInfo>
		 * <Track1>B4E4DD300964D69C556B090F3D5608CA3C0F7ACC6034181863E2
		 * F04BB145C694C763E4EA9137284E01A1B15ECE39191F0D0B1CCE13BF300A6A22F
		 * FCE69C4BD1B608B34CC23FE1D89C1231620649478C600462A09675FEF349E8125
		 * D81B086025<\Track1> <Track2><\Track2>
		 * <SecureFormat>SecureMag<\SecureFormat> <\ExtData>
		 */
		String extData = "";
		String securityInfo = "<SecurityInfo>" + ksn + "</SecurityInfo>";
		String track1 = "<Track1>" + track1EncytedHexData + track2EncytedHexData + "</Track1>";
		String secureFormat = "<SecureFormat>" + this.secureFormat + "</SecureFormat>";
		extData = extData + securityInfo + track1 + secureFormat;
		return extData;

	}

	public String getExtDataWithSingleTrack()
	{
		List<NameValuePair> extData = new ArrayList<NameValuePair>();
		extData.add(new BasicNameValuePair("SecurityInfo", ksn));
		extData.add(new BasicNameValuePair("Track1", track1EncytedHexData + track2EncytedHexData));
		extData.add(new BasicNameValuePair("SecureFormat", secureFormat));
		return extData.toString();

	}

	public String getTrack1EncytedDataInString()
	{
		return track1EncytedDataInString;
	}

	public void setTrack1EncytedDataInString(String track1EncytedDataInString)
	{
		this.track1EncytedDataInString = track1EncytedDataInString;
	}

	public String getTrack2EncytedDataInString()
	{
		return track2EncytedDataInString;
	}

	public void setTrack2EncytedDataInString(String track2EncytedDataInString)
	{
		this.track2EncytedDataInString = track2EncytedDataInString;
	}

	public String getExpMonth()
	{
		return expMonth;
	}

	public void setExpMonth(String expMonth)
	{
		this.expMonth = expMonth;
	}

	public String getExpYear()
	{
		return expYear;
	}

	public void setExpYear(String expYear)
	{
		this.expYear = expYear;
	}

	public String getCvv2()
	{
		return cvv2;
	}

	public void setCvv2(String cvv2)
	{
		this.cvv2 = cvv2;
	}

	/**
	 * @return the maskedData1InStringFormat
	 */
	public String getMaskedData1InStringFormat()
	{
		return maskedData1InStringFormat;
	}

	/**
	 * @param maskedData1InStringFormat
	 *            the maskedData1InStringFormat to set
	 */
	public void setMaskedData1InStringFormat(String maskedData1InStringFormat)
	{
		this.maskedData1InStringFormat = maskedData1InStringFormat;
	}

	/**
	 * @return the maskedData2InStringFormat
	 */
	public String getMaskedData2InStringFormat()
	{
		return maskedData2InStringFormat;
	}

	/**
	 * @param maskedData2InStringFormat
	 *            the maskedData2InStringFormat to set
	 */
	public void setMaskedData2InStringFormat(String maskedData2InStringFormat)
	{
		this.maskedData2InStringFormat = maskedData2InStringFormat;
	}
}
