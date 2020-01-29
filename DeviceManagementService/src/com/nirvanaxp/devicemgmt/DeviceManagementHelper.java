/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.devicemgmt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.jaxrs.packets.DeviceToRegisterPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.device.DeviceToRegister;
import com.nirvanaxp.types.entities.device.DeviceToRegisterHistory;
import com.nirvanaxp.types.entities.device.DeviceToRegister_;

// TODO: Auto-generated Javadoc
/**
 * This class is used for performing database operations related to Employee
 * Operation.
 * 
 * @author Apoorva
 *
 */
class DeviceManagementHelper
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(DeviceManagementHelper.class.getName());

	/**
	 * Adds the device to register.
	 *
	 * @param em the em
	 * @param packet the packet
	 * @return the list
	 */
	List<DeviceToRegister> addDeviceToRegister(EntityManager em, DeviceToRegisterPacket packet)
	{

		List<DeviceToRegister> registers = new ArrayList<DeviceToRegister>();
		for (DeviceToRegister deviceToRegister : packet.getDeviceToRegisters())
		{
			deviceToRegister.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			deviceToRegister.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			if(deviceToRegister!= null ){
				deviceToRegister.setId(new StoreForwardUtility().generateUUID() );
			}
			deviceToRegister = em.merge(deviceToRegister);

			// inserting on history to get
			insertIntoHistory(em, deviceToRegister);

			registers.add(deviceToRegister);
		}

		return registers;

	}

	/**
	 * Gets the device to register by location id.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param locationId the location id
	 * @param deviceId the device id
	 * @return the device to register by location id
	 */
	List<DeviceToRegister> getDeviceToRegisterByLocationId(EntityManager em, HttpServletRequest httpRequest, String locationId, String deviceId)
	{
		List<DeviceToRegister> deviceToRegisters = null;

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<DeviceToRegister> criteria = builder.createQuery(DeviceToRegister.class);
		Root<DeviceToRegister> deviceToRegister = criteria.from(DeviceToRegister.class);
		TypedQuery<DeviceToRegister> query = em.createQuery(criteria.select(deviceToRegister).where(builder.equal(deviceToRegister.get(DeviceToRegister_.locationsId), locationId),
				builder.equal(deviceToRegister.get(DeviceToRegister_.deviceId), deviceId)));
		try
		{
			deviceToRegisters = query.getResultList();
		}
		catch (Exception e)
		{

			logger.severe(httpRequest, MessageConstants.ERROR_MESSAGE_NO_RESULT_FOUND_FOR_REFERENCE_NUMBER);
		}
		return deviceToRegisters;
	}

	/**
	 * Insert into history.
	 *
	 * @param em the em
	 * @param deviceToRegister the device to register
	 */
	public void insertIntoHistory(EntityManager em, DeviceToRegister deviceToRegister)
	{
		DeviceToRegisterHistory history = new DeviceToRegisterHistory();
		history.setDeviceToRegisterId(deviceToRegister.getId());
		history.setCreated(deviceToRegister.getCreated());
		history.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		history.setCreatedBy(deviceToRegister.getCreatedBy());
		history.setUpdatedBy(deviceToRegister.getUpdatedBy());
		history.setDeviceId(deviceToRegister.getDeviceId());
		history.setRegisterId(deviceToRegister.getRegisterId());
		history.setLocationsId(deviceToRegister.getLocationsId());
		history.setStatus(deviceToRegister.getStatus());
		EntityTransaction tx = em.getTransaction();

		em.persist(history);

	}
}
