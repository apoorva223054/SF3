/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.helper;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.reasons.ReasonType;
import com.nirvanaxp.types.entities.reasons.Reasons;

// TODO: Auto-generated Javadoc
/**
 * The Class ReasonManagementHelper.
 */
public class ReasonManagementHelper
{

	/**
	 * Adds the reasons.
	 *
	 * @param em
	 *            the em
	 * @param reason
	 *            the reason
	 * @return the reasons
	 * @throws Exception 
	 */
	public Reasons addReasons(EntityManager em, Reasons reason,HttpServletRequest httpRequest) throws Exception
	{

		reason.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		reason.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		 
		if(reason.getId()==null)
		reason.setId(new StoreForwardUtility().generateUUID());
	 
		
		em.persist(reason);
		return reason;

	}

	/**
	 * Update reasons.
	 *
	 * @param em
	 *            the em
	 * @param reason
	 *            the reason
	 * @return the reasons
	 */
	public Reasons updateReasons(EntityManager em, Reasons reason)
	{

		reason.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(reason);
		return reason;

	}

	/**
	 * Delete reasons.
	 *
	 * @param em
	 *            the em
	 * @param reason
	 *            the reason
	 * @return the reasons
	 */
	public Reasons deleteReasons(EntityManager em, Reasons reason)
	{
		reason = (Reasons) new CommonMethods().getObjectById("Reasons", em,Reasons.class, reason.getId());
		reason.setStatus("D");
		reason.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(reason);
		return reason;

	}

	/**
	 * Adds the reason type.
	 *
	 * @param em
	 *            the em
	 * @param reasonType
	 *            the reason type
	 * @return the reason type
	 */
	public ReasonType addReasonType(EntityManager em, ReasonType reasonType,HttpServletRequest httpRequest,String locationId)
	{

		reasonType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		reasonType.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		try {
			if(reasonType.getId() ==null)
			reasonType.setId(new StoreForwardUtility().generateUUID());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		em.persist(reasonType);
		return reasonType;

	}

	/**
	 * Update reason type.
	 *
	 * @param em
	 *            the em
	 * @param reasonType
	 *            the reason type
	 * @return the reason type
	 */
	public ReasonType updateReasonType(EntityManager em, ReasonType reasonType)
	{
		reasonType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(reasonType);
		return reasonType;

	}

	/**
	 * Delete reason type.
	 *
	 * @param em
	 *            the em
	 * @param reasonType
	 *            the reason type
	 * @return the reason type
	 */
	public ReasonType deleteReasonType(EntityManager em, ReasonType reasonType)
	{
		reasonType = (ReasonType) new CommonMethods().getObjectById("ReasonType", em,ReasonType.class,reasonType.getId());
		reasonType.setStatus("D");
		reasonType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(reasonType);
		return reasonType;

	}

}
