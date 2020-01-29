/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.mail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.reservation.Reservation;

public class SendMail
{

	private static final NirvanaLogger logger = new NirvanaLogger(SendMail.class.getName());

	public static boolean sendMail(SMTPCredentials smtpCredentials)
	{

		// Get the session object
		Properties properties = System.getProperties();
		properties.put("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.host", smtpCredentials.getHost());
		properties.put("mail.smtp.port", smtpCredentials.getPort());
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		final String username = smtpCredentials.getUserName();
		final String pass = smtpCredentials.getPassword();

		Authenticator auth = new Authenticator()
		{
			public PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(username, pass);
			}
		};

		Session session = Session.getInstance(properties, auth);

		// compose the message
		try
		{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(smtpCredentials.getFromEmail()));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(smtpCredentials.getToEmail()));
			message.setSentDate(new Date(new TimezoneTime().getGMTTimeInMilis()));
			message.setSubject(smtpCredentials.getSubject());
			message.setText(smtpCredentials.getText());
			/*
			 * String message = "<i>Greetings!</i><br>"; message +=
			 * "<b>Wish you a nice day!</b><br>"; message +=
			 * "<font color=red>Duke</font>";
			 */
			// msg.setContent(message, "text/html");
			Transport transport = null;
			try
			{
				transport = session.getTransport();
				transport.connect(smtpCredentials.getHost(), username, pass);
				transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
				transport.close();
			}
			finally
			{
				if (transport != null)
				{

					transport.close();
				}

			}

			return true;

		}
		catch (MessagingException mex)
		{
			logger.severe(mex, "Failed to send email");
		}

		return false;
	}

	public static boolean sendHtmlMail(SMTPCredentials smtpCredentials)
	{

		// Get the session object
		Properties properties = System.getProperties();
		properties.put("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.host", smtpCredentials.getHost());
		properties.put("mail.smtp.port", smtpCredentials.getPort());
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		final String username = smtpCredentials.getUserName();
		final String pass = smtpCredentials.getPassword();

		Authenticator auth = new Authenticator()
		{
			public PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(username, pass);
			}
		};

		Session session = Session.getInstance(properties, auth);

		// compose the message
		try
		{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(smtpCredentials.getFromEmail()));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(smtpCredentials.getToEmail()));
			message.setSentDate(new Date(new TimezoneTime().getGMTTimeInMilis()));
			message.setSubject(smtpCredentials.getSubject());
			message.setContent(smtpCredentials.getText(), "text/html");
			Transport transport = null;
			try
			{
				transport = session.getTransport();
				transport.connect(smtpCredentials.getHost(), username, pass);
				transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
				transport.close();
			}
			finally
			{
				if (transport != null)
				{

					transport.close();
				}

			}

			return true;

		}
		catch (MessagingException mex)
		{
			logger.severe(mex, "Failed to send email");
		}

		return false;
	}

	public static void sendEmailToCustomer(HttpServletRequest httpRequest, Reservation reservation, Location l, String webSiteUrl, String action) throws FileNotFoundException, IOException
	{

			String locationName = "";
			if (reservation.getEmail() != null && reservation.getEmail().length() > 0)
			{
				if (l != null)
				{
					locationName = l.getName();
				}
				int partySize = reservation.getPartySize();
				String dateString = getDateForEmail(reservation);
				String time = getTimeForEmail(reservation);
				reservation.getTime();
				SMTPCredentials smtpCredentials = new SMTPCredentials();
				ConfigFileReader.initSMTPCredentials(smtpCredentials, action);
				String msg = smtpCredentials.getText();
				String subject = smtpCredentials.getSubject();
				subject = subject.replace("BusinessName", locationName);
				msg = msg.replace("CustomerName", reservation.getFirstName());
				msg = msg.replace("BusinessName", locationName);
				msg = msg.replace("CustomerFullName", reservation.getFirstName() + " " + reservation.getLastName());
				msg = msg.replace("ReservationDate", dateString);
				msg = msg.replace("ReservationTime", time);
				msg = msg.replace("GuestCount", "" + partySize);
				String businessString = "";
				if (l.getAddress() != null)
				{
					if (l.getAddress().getAddress1() != null)
					{
						businessString = businessString + l.getAddress().getAddress1() + " ";
					}

					if (l.getAddress().getAddress2() != null)
					{
						businessString = businessString + l.getAddress().getAddress2() + " ";
					}

					if (l.getAddress().getCity() != null)
					{
						businessString = businessString + l.getAddress().getCity() + " ";
					}

					if (l.getAddress().getZip() != null)
					{
						businessString = businessString + l.getAddress().getZip() + " ";
					}

					if (l.getAddress().getPhone() != null)
					{
						businessString = "<br>" + businessString + l.getAddress().getPhone();
					}
				}
				msg = msg.replace("BuisnessAddress", businessString);
				if (l.getWebsite() != null)
				{
					if (l.getWebsite().startsWith("http://") || l.getWebsite().startsWith("https://"))
					{
						msg = msg.replace("BusinessWebSiteUrl", "" + l.getWebsite());
					}
					else
					{
						msg = msg.replace("BusinessWebSiteUrl", "http://" + l.getWebsite());
					}
				}
				else
				{
					msg = msg.replace("BusinessWebSiteUrl", "#");
				}

				if (webSiteUrl != null)
				{
					if (webSiteUrl.startsWith("http://") || webSiteUrl.startsWith("https://"))
					{
						msg = msg.replace("CancelReservationUrl", "" + webSiteUrl + reservation.getId());
					}
					else
					{
						msg = msg.replace("CancelReservationUrl", "http://" + webSiteUrl + reservation.getId());
					}
				}
				else
				{
					msg = msg.replace("Change or cancel this reservation ", "");
					msg = msg.replace("CancelReservationUrl", "#");
				}
				smtpCredentials.setSubject(subject);
				smtpCredentials.setText(msg);
				smtpCredentials.setToEmail(reservation.getEmail());
				SendMail.sendHtmlMail(smtpCredentials);
			}
		

	}

	public static void sendEmailToCustomerForCancel(HttpServletRequest httpRequest, Reservation reservation, Location l, String webSiteUrl) throws FileNotFoundException, IOException
	{

		 String locationName = "";
			if (reservation.getEmail() != null && reservation.getEmail().length() > 0)
			{
				if (l != null)
				{
					locationName = l.getName();
				}

				// reservation.getTime();
				SMTPCredentials smtpCredentials = new SMTPCredentials();
				ConfigFileReader.initSMTPCredentials(smtpCredentials, ConfigFileReader.CONFIG_FILE_NAME_FOR_RESERVATION_CANCEL_SMTP);
				String msg = smtpCredentials.getText();
				String subject = smtpCredentials.getSubject();
				subject = subject.replace("BusinessName", locationName);
				msg = msg.replace("CustomerName", reservation.getFirstName());
				msg = msg.replace("BusinessName", locationName);
				String businessString = "";
				if (l.getAddress() != null)
				{
					if (l.getAddress().getAddress1() != null)
					{
						businessString = businessString + l.getAddress().getAddress1() + " ";
					}

					if (l.getAddress().getAddress2() != null)
					{
						businessString = businessString + l.getAddress().getAddress2() + " ";
					}

					if (l.getAddress().getCity() != null)
					{
						businessString = businessString + l.getAddress().getCity() + " ";
					}

					if (l.getAddress().getZip() != null)
					{
						businessString = businessString + l.getAddress().getZip() + " ";
					}

					if (l.getAddress().getPhone() != null)
					{
						businessString = "<br>" + businessString + l.getAddress().getPhone();
					}
				}
				msg = msg.replace("BuisnessAddress", businessString);

				smtpCredentials.setSubject(subject);
				smtpCredentials.setText(msg);
				smtpCredentials.setToEmail(reservation.getEmail());
				SendMail.sendHtmlMail(smtpCredentials);
			}
		
	}

	private static String getTimeForEmail(Reservation reservation)
	{

		String timeString = reservation.getTime();

		SimpleDateFormat formatterForFormat = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat formatterForParse = new SimpleDateFormat("HH:mm:ss");
		Date date=null;
		try
		{
			date = formatterForParse.parse(timeString);
		}
		catch (ParseException e)
		{
			logger.severe(e);
		}
		timeString = formatterForFormat.format(date);

		return timeString;
	}

	private static String getDateForEmail(Reservation reservation)
	{
		String dateString = reservation.getDate();

		SimpleDateFormat formatterForFormat = new SimpleDateFormat("E, MMM dd yyyy");
		SimpleDateFormat formatterForParse = new SimpleDateFormat("yyyy-MM-dd");
		Date date=null;
		try
		{
			date = formatterForParse.parse(dateString);
		}
		catch (ParseException e)
		{
			logger.severe(e);
		}
		dateString = formatterForFormat.format(date);

		return dateString;
	}

}
