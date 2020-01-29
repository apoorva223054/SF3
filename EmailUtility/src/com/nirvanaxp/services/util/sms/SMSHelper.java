package com.nirvanaxp.services.util.sms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.sms.BusinessToSMSEvent;
import com.nirvanaxp.types.entities.sms.BusinessToSMSEvent_;
import com.nirvanaxp.types.entities.sms.SMSSetting;
import com.nirvanaxp.types.entities.sms.SMSSetting_;
import com.nirvanaxp.types.entities.sms.SMSTemplate;
import com.nirvanaxp.types.entities.sms.SMSTemplate_;

// TODO: Auto-generated Javadoc
/**
 * The Class SMSHelper.
 */
public class SMSHelper
{

	/**
	 * Gets the SMS setting by location id.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @return the SMS setting by location id
	 */
	public SMSSetting getSMSSettingByLocationId(EntityManager em, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SMSSetting> criteria = builder.createQuery(SMSSetting.class);
		Root<SMSSetting> r = criteria.from(SMSSetting.class);
		TypedQuery<SMSSetting> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(SMSSetting_.locationId), locationId), builder.notEqual(r.get(SMSSetting_.status), "D")));

		SMSSetting config = (SMSSetting) query.getSingleResult();

		return config;
	}

	/**
	 * Gets the SMS template by location id.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @param operationName the operation name
	 * @return the SMS template by location id
	 */
	public SMSTemplate getSMSTemplateByLocationId(EntityManager em, String locationId, String operationName)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SMSTemplate> criteria = builder.createQuery(SMSTemplate.class);
		Root<SMSTemplate> r = criteria.from(SMSTemplate.class);
		TypedQuery<SMSTemplate> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(SMSTemplate_.locationId), locationId),
				builder.equal(r.get(SMSTemplate_.templateName), operationName), builder.notEqual(r.get(SMSTemplate_.status), "D")));

		return (SMSTemplate) query.getSingleResult();
	}

	/**
	 * Send SMS by SMS template id location id.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @param emailTemplateId the email template id
	 * @return true, if successful
	 */
	public boolean sendSMSBySMSTemplateIdLocationId(EntityManager em, String locationId, int emailTemplateId)
	{
		boolean result = false;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<BusinessToSMSEvent> criteria = builder.createQuery(BusinessToSMSEvent.class);
		Root<BusinessToSMSEvent> r = criteria.from(BusinessToSMSEvent.class);
		TypedQuery<BusinessToSMSEvent> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(BusinessToSMSEvent_.locationId), locationId),
				builder.equal(r.get(BusinessToSMSEvent_.smsTemplateId), emailTemplateId), builder.notEqual(r.get(BusinessToSMSEvent_.status), "D")));

		BusinessToSMSEvent businessSMSSetting = query.getSingleResult();
		if (businessSMSSetting != null)
		{
			result = true;
		}
		else
		{
			result = false;
		}
		return result;
	}

	/**
	 * Gets the time for email.
	 *
	 * @param reservation the reservation
	 * @return the time for email
	 * @throws ParseException the parse exception
	 */
	public String getTimeForEmail(Reservation reservation) throws ParseException
	{

		String timeString = reservation.getTime();

		SimpleDateFormat formatterForFormat = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat formatterForParse = new SimpleDateFormat("HH:mm:ss");
		Date date = formatterForParse.parse(timeString);
		timeString = formatterForFormat.format(date);

		return timeString;
	}

	/**
	 * Gets the date for email.
	 *
	 * @param reservation the reservation
	 * @return the date for email
	 * @throws ParseException the parse exception
	 */
	public String getDateForEmail(Reservation reservation) throws ParseException
	{
		String dateString = reservation.getDate();

		SimpleDateFormat formatterForFormat = new SimpleDateFormat("E, MMM dd yyyy");
		SimpleDateFormat formatterForParse = new SimpleDateFormat("yyyy-MM-dd");
		Date date = formatterForParse.parse(dateString);
		dateString = formatterForFormat.format(date);

		return dateString;
	}

}
