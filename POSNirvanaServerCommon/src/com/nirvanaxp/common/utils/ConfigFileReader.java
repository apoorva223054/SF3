/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

import com.nirvanaxp.common.utils.mail.SMTPCredentials;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;

public class ConfigFileReader
{
 
//   public static final String CONFIG_PROPERTY_FILE_LOCATION = "C:/tomee/conf/nirvana/";
  //    public static final String CONFIG_PROPERTY_FILE_LOCATION = "C:/apoorvdoc/dev/Dev/Server/tomee/tomee7.0.0-SNAPSHOT/conf/";
 
    public static final String CONFIG_PROPERTY_FILE_LOCATION = "/opt/tomee/conf/nirvana/";
// 
 //   public static final String CONFIG_PROPERTY_FILE_LOCATION = "D:/tomee/conf/nirvana/";
 //  public static final String CONFIG_PROPERTY_FILE_LOCATION = "C:/xampp/tomcat/conf/";
	 
 
 
 
	
	public static final String CONFIG_FILE_NAME = "config.txt";
	private static Properties propCacheForConfig = null;
	private static long lastLoadTimeForConfig = 0l;

	public static final String CONFIG_FILE_NAME_FOR_MESSAGE = "message.txt";
	private static Properties propCacheForMessage = null;
	private static long lastLoadTimeForMessageConfig = 0l;

	public static final String CONFIG_FILE_NAME_FOR_SMTP = "SMTPConfig.txt";
	private static Properties propCacheForSMTP = null;
	private static long lastLoadTimeForSMTPConfig = 0l;

	
	public static final String CONFIG_FILE_NAME_FOR_SINGUP_PROCESS_SMTP = "SingupProcessSMTPConfig.txt";
	private static Properties propCacheForSingupProcess = null;
	private static long lastLoadTimeForSingupProcess = 0l;
	
	public static final String CONFIG_FILE_NAME_FOR_SINGUP_SUCCESS_SMTP = "SingupSuccessSMTPConfig.txt";
	private static Properties propCacheForSingupSuccess = null;
	private static long lastLoadTimeForSingupSuccess = 0l;
	
	public static final String CONFIG_FILE_NAME_FOR_RESERVATION_SMTP = "ResevationSMTPConfig.txt";
	private static Properties propCacheForReservation = null;
	private static long lastLoadTimeForReservationConfig = 0l;

	public static final String CONFIG_FILE_NAME_FOR_RESERVATION_UPDATE_SMTP = "UpdateResevationSMTPConfig.txt";
	private static Properties propCacheForUpdateReservation = null;
	private static long lastLoadTimeForUpdateReservationConfig = 0l;

	public static final String CONFIG_FILE_NAME_FOR_RESERVATION_CANCEL_SMTP = "CancelResevationSMTPConfig.txt";
	private static Properties propCacheForCancelReservation = null;
	private static long lastLoadTimeForCancelReservationConfig = 0l;

	public static final String CONFIG_FILE_NAME_FOR_FORGOT_PASSWORD_SMTP = "ForgetPassordSMTConfig.txt";
	private static Properties propCacheForForgotPassword = null;
	private static long lastLoadTimeForForgotPassConfig = 0l;

	private static final long REFRESH_DELAY=60000l; 
	

	private static Properties loadProperties(String configFileName) throws FileNotFoundException, IOException
	{
		switch (configFileName)
		{
		case CONFIG_FILE_NAME:
		{

			if (propCacheForConfig == null || lastLoadTimeForConfig < (new TimezoneTime().getGMTTimeInMilis() - REFRESH_DELAY))
			{
				propCacheForConfig = load(CONFIG_FILE_NAME, propCacheForConfig);
				lastLoadTimeForConfig = new TimezoneTime().getGMTTimeInMilis();
			}
			return propCacheForConfig;
		}

		case CONFIG_FILE_NAME_FOR_FORGOT_PASSWORD_SMTP:
		{

			if (propCacheForForgotPassword == null || lastLoadTimeForForgotPassConfig < (new TimezoneTime().getGMTTimeInMilis() - REFRESH_DELAY))
			{
				propCacheForForgotPassword = load(CONFIG_FILE_NAME_FOR_FORGOT_PASSWORD_SMTP, propCacheForForgotPassword);
				lastLoadTimeForForgotPassConfig = new TimezoneTime().getGMTTimeInMilis();
			}
			return propCacheForForgotPassword;
		}

		case CONFIG_FILE_NAME_FOR_MESSAGE:
		{

			if (propCacheForMessage == null || lastLoadTimeForMessageConfig < (new TimezoneTime().getGMTTimeInMilis() - REFRESH_DELAY))
			{
				propCacheForMessage = load(CONFIG_FILE_NAME_FOR_MESSAGE, propCacheForMessage);
				lastLoadTimeForMessageConfig = new TimezoneTime().getGMTTimeInMilis();
			}
			return propCacheForMessage;
		}

		case CONFIG_FILE_NAME_FOR_RESERVATION_SMTP:
		{

			if (propCacheForReservation == null || lastLoadTimeForReservationConfig < (new TimezoneTime().getGMTTimeInMilis() - REFRESH_DELAY))
			{
				propCacheForReservation = load(CONFIG_FILE_NAME_FOR_RESERVATION_SMTP, propCacheForReservation);
				lastLoadTimeForReservationConfig = new TimezoneTime().getGMTTimeInMilis();
			}
			return propCacheForReservation;
		}

		case CONFIG_FILE_NAME_FOR_RESERVATION_UPDATE_SMTP:
		{

			if (propCacheForUpdateReservation == null || lastLoadTimeForUpdateReservationConfig < (new TimezoneTime().getGMTTimeInMilis() - REFRESH_DELAY))
			{
				propCacheForUpdateReservation = load(CONFIG_FILE_NAME_FOR_RESERVATION_UPDATE_SMTP, propCacheForUpdateReservation);
				lastLoadTimeForUpdateReservationConfig = new TimezoneTime().getGMTTimeInMilis();
			}
			return propCacheForUpdateReservation;
		}

		case CONFIG_FILE_NAME_FOR_RESERVATION_CANCEL_SMTP:
		{

			if (propCacheForCancelReservation == null || lastLoadTimeForCancelReservationConfig < (new TimezoneTime().getGMTTimeInMilis() - REFRESH_DELAY))
			{
				propCacheForCancelReservation = load(CONFIG_FILE_NAME_FOR_RESERVATION_CANCEL_SMTP, propCacheForCancelReservation);
				lastLoadTimeForCancelReservationConfig = new TimezoneTime().getGMTTimeInMilis();
			}
			return propCacheForCancelReservation;
		}
		case CONFIG_FILE_NAME_FOR_SMTP:
		{

			if (propCacheForSMTP == null || lastLoadTimeForSMTPConfig < (new TimezoneTime().getGMTTimeInMilis() - REFRESH_DELAY))
			{
				propCacheForSMTP = load(CONFIG_FILE_NAME_FOR_SMTP, propCacheForSMTP);
				lastLoadTimeForSMTPConfig = new TimezoneTime().getGMTTimeInMilis();
			}
			return propCacheForSMTP;
		}
		
		case CONFIG_FILE_NAME_FOR_SINGUP_PROCESS_SMTP:
		{

			if (propCacheForSingupProcess == null || lastLoadTimeForSingupProcess < (new TimezoneTime().getGMTTimeInMilis() - REFRESH_DELAY))
			{
				propCacheForSingupProcess = load(CONFIG_FILE_NAME_FOR_SINGUP_PROCESS_SMTP, propCacheForSingupProcess);
				lastLoadTimeForSingupProcess = new TimezoneTime().getGMTTimeInMilis();
			}
			return propCacheForSingupProcess;
		}
		
		case CONFIG_FILE_NAME_FOR_SINGUP_SUCCESS_SMTP:
		{

			if (propCacheForSingupSuccess == null || lastLoadTimeForSingupSuccess < (new TimezoneTime().getGMTTimeInMilis() - REFRESH_DELAY))
			{
				propCacheForSingupSuccess = load(CONFIG_FILE_NAME_FOR_SINGUP_SUCCESS_SMTP, propCacheForSingupSuccess);
				lastLoadTimeForSingupSuccess = new TimezoneTime().getGMTTimeInMilis();
			}
			return propCacheForSingupSuccess;
		}
		
		}
		return null;
	}

	private synchronized static Properties load(String fileName, Properties props) throws FileNotFoundException, IOException
	{
		if (props != null)
		{
			props.clear();
		}
		else
		{
			props = new Properties();
		}
		props.load(new FileInputStream(CONFIG_PROPERTY_FILE_LOCATION + fileName));
		return props;

	}

	public static UserCredentials readUsernamePasswordFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String username = propCacheForConfig.getProperty("username").trim();
		String databasePassword = propCacheForConfig.getProperty("password").trim();
		String databaseString = propCacheForConfig.getProperty("databaseString").trim();
		UserCredentials user = new UserCredentials(username, databasePassword, databaseString);
		return user;
	}

	public static String readAccountCreationPathFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String bashFilePath = propCacheForConfig.getProperty("bashFilePath").trim();
		return bashFilePath;
	}

	public static String readPaymentLogsPathFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String paymentLogFilePath = propCacheForConfig.getProperty("PaymentLogFilePath").trim();
		return paymentLogFilePath;

	}

	// TODO - this property should be in database
	public static String getMaxAllowedDevicesForAccountFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String maxAllowedDevicesForAccount = propCacheForConfig.getProperty("maxAllowedDevicesForAccount").trim();
		return maxAllowedDevicesForAccount;

	}

	// TODO - this property should be in database
	public static String getMaxAllowedDevicesForBussinessFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String maxAllowedDevicesForAccount = propCacheForConfig.getProperty("maxAllowedDevicesForBussiness").trim();
		return maxAllowedDevicesForAccount;

	}

	public static String getCreateAccountScriptPathFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String accountScriptPath = propCacheForConfig.getProperty("accountScriptPath").trim();
		return accountScriptPath;

	}

	// this method create directories as per location
	public static boolean makeDirectories(String uploadedFileLocation)
	{
		String directoryName = uploadedFileLocation.substring(0, uploadedFileLocation.lastIndexOf("/"));
		File existFile = new File(directoryName);
		if (!existFile.exists())
		{
			return existFile.mkdirs();
		}
			
		return true;

	}

	public static String correctDateFormat(String date)
	{
		if (date != null && date.contains("-") && date.length() < 10)
		{
			// this string has one 0 missing
			String dateArr[] = date.split("-");
			if (dateArr[1].length() == 1)
			{
				dateArr[1] = "0" + dateArr[1];
			}
			if (dateArr[2].length() == 1)
			{
				dateArr[2] = "0" + dateArr[2];
			}
			String correctDate = dateArr[0] + "-" + dateArr[1] + "-" + dateArr[2];
			return correctDate;
		}
		else
		{
			return date;
		}
	}

	public static String getHostNameFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String logInCookieName = propCacheForConfig.getProperty("logInCookieName").trim();
		return logInCookieName;

	}

	public static String getAccountURLFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String logInCookieName = propCacheForConfig.getProperty("accountURL").trim();
		return logInCookieName;

	}

	public static String getWebSocketURLPart1FromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String webSocketURL = propCacheForConfig.getProperty("webSocketURLPart1").trim();
		System.out.println("Socket url:-" + webSocketURL);
		return webSocketURL;

	}

	public static String getWebSocketURLPart2FromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String webSocketURL = propCacheForConfig.getProperty("webSocketURLPart2").trim();
		System.out.println("Socket url:-" + webSocketURL);
		return webSocketURL;

	}

	public static String getGlobalDatabaseNameFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String globalDatabaseName = propCacheForConfig.getProperty("globalSchemaName").trim();
		return globalDatabaseName;

	}

	public static String getDriverNameFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String driverName = propCacheForConfig.getProperty("driverName").trim();
		return driverName;

	}

	public static String getNirvanaXPDatabaseNameFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String nirvanaXPDatabaseName = propCacheForConfig.getProperty("nirvanaXPSchemaName").trim();
		return nirvanaXPDatabaseName;

	}

	public static String getImageUploadPathFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String serverImageUploadPath = propCacheForConfig.getProperty("serverImageUploadPath").trim();
		return serverImageUploadPath;

	}

	public static BigDecimal getDefaultInventoryThreshold() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String defaultThreashold = propCacheForConfig.getProperty("DefaultInventoryThreshold").trim();
		if (defaultThreashold != null && defaultThreashold.length() > 0)
		{
			int defaultinventoryThreashold = Integer.parseInt(defaultThreashold);
			return new BigDecimal(defaultinventoryThreashold);
		}

		return new BigDecimal(0);
	}

	public static String getQRCodeUploadPathFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String serverImageUploadPath = propCacheForConfig.getProperty("QRCodeUploadPath").trim();
		return serverImageUploadPath;

	}

	public static String getQRCodeUploadPathFromFile2() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String serverImageUploadPath = propCacheForConfig.getProperty("QRCodeUploadPath2").trim();
		return serverImageUploadPath;

	}
	
	public static String getUserQRCodeUploadPath() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String serverImageUploadPath = propCacheForConfig.getProperty("UserQRCodeUploadPath").trim();
		return serverImageUploadPath;

	}

	public static String getAdminFeedbackURL() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String adminFeedbackURL = propCacheForConfig.getProperty("AdminFeedbackURL").trim();
		return adminFeedbackURL;

	}

	public static int getSessionHoldTimeForReservationFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String reservationSessionHoldTimeinStr = propCacheForConfig.getProperty("ReservationSessionHoldTime").trim();
		int reservationSessionHoldTime = 0;
		if (reservationSessionHoldTimeinStr != null)
		{
			reservationSessionHoldTime = Integer.parseInt(reservationSessionHoldTimeinStr);
		}
		return reservationSessionHoldTime;

	}

	public static SMTPCredentials getSMTPCredentials() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME_FOR_SMTP);
		SMTPCredentials smtpCredentials = new SMTPCredentials();
		 propCacheForSMTP.getProperty("predefinedText1");
		smtpCredentials.setUserName(propCacheForSMTP.getProperty("smtpUsername").trim());
		smtpCredentials.setPassword(propCacheForSMTP.getProperty("smtpPassword").trim());
		smtpCredentials.setPort(propCacheForSMTP.getProperty("port").trim());
		smtpCredentials.setHost(propCacheForSMTP.getProperty("host").trim());

		smtpCredentials.setFromEmail(propCacheForSMTP.getProperty("fromEmail").trim());
		smtpCredentials.setSubject(propCacheForSMTP.getProperty("subjectForEmailVerification").trim());
		smtpCredentials.setText(propCacheForSMTP.getProperty("predefinedText1").trim());
		smtpCredentials.setVerificationUrl(propCacheForSMTP.getProperty("verificationUrl").trim());
		return smtpCredentials;

	}
	
	public static SMTPCredentials getSMTPCredentialsForSingupProcess() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME_FOR_SINGUP_PROCESS_SMTP);
		SMTPCredentials smtpCredentials = new SMTPCredentials();
		smtpCredentials.setUserName(propCacheForSingupProcess.getProperty("smtpUsername").trim());
		smtpCredentials.setPassword(propCacheForSingupProcess.getProperty("smtpPassword").trim());
		smtpCredentials.setPort(propCacheForSingupProcess.getProperty("port").trim());
		smtpCredentials.setHost(propCacheForSingupProcess.getProperty("host").trim());

		smtpCredentials.setFromEmail(propCacheForSingupProcess.getProperty("fromEmail").trim());
		smtpCredentials.setSubject(propCacheForSingupProcess.getProperty("subject").trim());
		smtpCredentials.setText(propCacheForSingupProcess.getProperty("predefinedText").trim());
		return smtpCredentials;

	}

	public static SMTPCredentials getSMTPCredentialsForSingupSuccess() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME_FOR_SINGUP_SUCCESS_SMTP);
		SMTPCredentials smtpCredentials = new SMTPCredentials();
		smtpCredentials.setUserName(propCacheForSingupSuccess.getProperty("smtpUsername").trim());
		smtpCredentials.setPassword(propCacheForSingupSuccess.getProperty("smtpPassword").trim());
		smtpCredentials.setPort(propCacheForSingupSuccess.getProperty("port").trim());
		smtpCredentials.setHost(propCacheForSingupSuccess.getProperty("host").trim());

		smtpCredentials.setFromEmail(propCacheForSingupSuccess.getProperty("fromEmail").trim());
		smtpCredentials.setSubject(propCacheForSingupSuccess.getProperty("subject").trim());
		smtpCredentials.setText(propCacheForSingupSuccess.getProperty("predefinedText").trim());
		return smtpCredentials;

	}


	public static String getMesssageSender1FromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME_FOR_MESSAGE);
		String messageSender = propCacheForMessage.getProperty("messageSender1").trim();
		return messageSender;

	}

	public static String getMesssageSender2FromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME_FOR_MESSAGE);
		String messageSender = propCacheForMessage.getProperty("messageSender2").trim();
		return messageSender;

	}

	public static String getPathForOneAppForAllConfigFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String oneAllForAllConfigFile = propCacheForConfig.getProperty("OneAllForAllConfigFilePath").trim();
		return oneAllForAllConfigFile;

	}

	public static String getWaitTimeForAcountFromFile() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String waitTimeForAcount = propCacheForConfig.getProperty("waitTimeForAcount").trim();
		return waitTimeForAcount;

	}

	public static SMTPCredentials initSMTPCredentials(SMTPCredentials smtpCredentials, String configFileName) throws FileNotFoundException, IOException
	{
		Properties prop = loadProperties(configFileName);
		// prop.setProperty("host", "<a></a>");
		smtpCredentials.setUserName(prop.getProperty("smtpUsername").trim());
		smtpCredentials.setPassword(prop.getProperty("smtpPassword").trim());
		smtpCredentials.setPort(prop.getProperty("port").trim());
		smtpCredentials.setHost(prop.getProperty("host").trim());
		smtpCredentials.setFromEmail(prop.getProperty("fromEmail").trim());
		smtpCredentials.setSubject(prop.getProperty("subjectForEmailVerification").trim());
		smtpCredentials.setText(prop.getProperty("predefinedText1").trim());
		return smtpCredentials;

	}

	public static long readLockExpirationLimit() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		return Long.parseLong(((String) propCacheForConfig.get("LockExpirationTimeLimit")).trim());
	}

	/**************** Braintree Gateway Configs *******************/

	/**
	 * SANDBOX, "n6bsnzbm8kf2wpzw", "r2cv5y3y42jzcwjr",
	 * "df40249ed8de8e5a57e5ec8d54323d47"
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */

	public static String getBraintreeEnvironment() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String env = propCacheForConfig.getProperty("BraintreeEnvironment").trim();
		return env;
	}

	public static String getBraintreeMerchantId() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String env = propCacheForConfig.getProperty("BraintreeMerchantId").trim();
		return env;
	}

	public static String getBraintreePublicKey() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String env = propCacheForConfig.getProperty("BraintreePublicKey").trim();
		return env;
	}

	public static String getBraintreePrivateKey() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String env = propCacheForConfig.getProperty("BraintreePrivateKey").trim();
		return env;
	}

	public static String getRservationUrl() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String env = propCacheForConfig.getProperty("rservationUrl").trim();
		return env;
	}

	public static String getQrcodeSize() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String qrCodeSize = propCacheForConfig.getProperty("QrcodeSize").trim();
		return qrCodeSize;

	}
	
	public static int getMaxImageUploadSize() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		int maxImageUploadSize = Integer.parseInt(propCacheForConfig.getProperty("MaxImageUploadSize").trim());
		return maxImageUploadSize;

	}

	public static String getWebsiteLogoPath() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String websiteLogoPath = propCacheForConfig.getProperty("websiteLogoPath").trim();
		return websiteLogoPath;

	}

	public static String getQRCodeServerName() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);

		String qrCodeServerName = propCacheForConfig.getProperty("QRCodeServerName").trim();
		return qrCodeServerName;

	}

	public static String getEpsonTestXML() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		
		String epsonTestXML = propCacheForConfig.getProperty("EpsonTestXML").trim();
		return epsonTestXML;
	}
	
	public static String getPeoplesoftExceptionFilePath() throws FileNotFoundException, IOException
	{
		loadProperties(CONFIG_FILE_NAME);
		String peoplesoftExceptionFilePath = propCacheForConfig.getProperty("peoplesoftExceptionFilePath").trim();
		return peoplesoftExceptionFilePath;

	}
}
