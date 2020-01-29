/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.URLName;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.FieldType;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.category.CategoryToDiscount;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter;
import com.nirvanaxp.types.entities.catalog.category.Category_;
import com.nirvanaxp.types.entities.catalog.course.Course;
import com.nirvanaxp.types.entities.catalog.course.Course_;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemsToDiscount;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.ItemsType;
import com.nirvanaxp.types.entities.catalog.items.ItemsType_;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.Discount_;
import com.nirvanaxp.types.entities.discounts.DiscountsType;
import com.nirvanaxp.types.entities.discounts.DiscountsType_;
import com.nirvanaxp.types.entities.email.BusinessEmailSetting;
import com.nirvanaxp.types.entities.email.EmailTemplate;
import com.nirvanaxp.types.entities.email.SmtpConfig;
import com.nirvanaxp.types.entities.employee.EmployeeOperation;
import com.nirvanaxp.types.entities.feedback.FeedbackField;
import com.nirvanaxp.types.entities.feedback.FeedbackQuestion;
import com.nirvanaxp.types.entities.feedback.FeedbackType;
import com.nirvanaxp.types.entities.feedback.Smiley;
import com.nirvanaxp.types.entities.function.Function;
import com.nirvanaxp.types.entities.function.Function_;
import com.nirvanaxp.types.entities.inventory.UnitConversion;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationToApplication;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.locations.LocationsToFunction;
import com.nirvanaxp.types.entities.locations.MetaBusinessTypeToFunction;
import com.nirvanaxp.types.entities.locations.MetaBusinessTypeToFunction_;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.orders.DeliveryOption;
import com.nirvanaxp.types.entities.orders.OrderAdditionalQuestion;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup_;
import com.nirvanaxp.types.entities.orders.OrderSource_;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;
import com.nirvanaxp.types.entities.payment.PaymentType;
import com.nirvanaxp.types.entities.payment.PaymentType_;
import com.nirvanaxp.types.entities.payment.PaymentWay;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.printers.Printer_;
import com.nirvanaxp.types.entities.printers.PrintersInterface;
import com.nirvanaxp.types.entities.printers.PrintersInterface_;
import com.nirvanaxp.types.entities.printers.PrintersType;
import com.nirvanaxp.types.entities.printers.PrintersType_;
import com.nirvanaxp.types.entities.reasons.ReasonType;
import com.nirvanaxp.types.entities.reasons.Reasons;
import com.nirvanaxp.types.entities.reservation.ContactPreference;
import com.nirvanaxp.types.entities.reservation.RequestType;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus;
import com.nirvanaxp.types.entities.reservation.ReservationsType;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.roles.Role_;
import com.nirvanaxp.types.entities.roles.RolesToFunction;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax_;
import com.nirvanaxp.types.entities.sms.SMSTemplate;

// TODO: Auto-generated Javadoc
/**
 * The Class LookupUtil.
 */
class LookupUtil
{
	private final static NirvanaLogger logger = new NirvanaLogger(LookupUtil.class.getName());

	Location getLocationsById(HttpServletRequest httpRequest, EntityManager em, String id)
	{

		Location l = (Location)new CommonMethods().getObjectById("Location", em, Location.class, id);

		return l;

	}
	/**
	 * Adds the location to function constants.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 * @throws Exception
	 *             the exception
	 */
	void addLocationToFunctionConstants(EntityManager em, String locationId, String userId,HttpServletRequest httpRequest) throws Exception
	{
		

		Location location =getLocationsById(httpRequest, em, locationId);
		String queryString = "select lta from LocationToApplication lta where lta.locationsId=?  ";
		TypedQuery<LocationToApplication> query = em.createQuery(queryString, LocationToApplication.class)
				.setParameter(1, locationId);
		LocationToApplication resultSet = query.getSingleResult();
		if (resultSet != null && location != null)
		{
			
			if (resultSet.getApplicationsId() != 0)
			{
				List<MetaBusinessTypeToFunction> metaDataList = getMetaDataForBussinessTypeId(resultSet.getApplicationsId(), em);
				logger.severe(metaDataList.size()+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+metaDataList.toString());
				if (metaDataList != null && metaDataList.size() > 0)
				{
					
					int index = 0;
					for (MetaBusinessTypeToFunction metaBusinessTypeToFunctions : metaDataList)
					{
						LocationsToFunction locationsToFunction = new LocationsToFunction();
						locationsToFunction.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						locationsToFunction.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						locationsToFunction.setFunctionsId(metaBusinessTypeToFunctions.getFunctionsId());
						locationsToFunction.setFunctionsName(metaBusinessTypeToFunctions.getDisplayName());
						locationsToFunction.setLocationsId(locationId);
						locationsToFunction.setCreatedBy(userId);
						locationsToFunction.setUpdatedBy(userId);
						locationsToFunction.setUpdatedBy(userId);
						locationsToFunction.setStatus(metaBusinessTypeToFunctions.getStatus());
						index++;
						locationsToFunction.setDisplaySequence(index);

						em.persist(locationsToFunction);
						logger.severe(locationsToFunction.toString()+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@locationsToFunction" );
					}

				}
			}

		}
	}

	/**
	 * Gets the meta data for bussiness type id.
	 *
	 * @param bussinessTypeId
	 *            the bussiness type id
	 * @param em
	 *            the em
	 * @return the meta data for bussiness type id
	 */
	private List<MetaBusinessTypeToFunction> getMetaDataForBussinessTypeId(int bussinessTypeId, EntityManager em)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<MetaBusinessTypeToFunction> criteria = builder.createQuery(MetaBusinessTypeToFunction.class);
		Root<MetaBusinessTypeToFunction> r = criteria.from(MetaBusinessTypeToFunction.class);
		TypedQuery<MetaBusinessTypeToFunction> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(MetaBusinessTypeToFunction_.businessTypeId), bussinessTypeId) }));
		List<MetaBusinessTypeToFunction> result = query.getResultList();
		return result;

	}

	/**
	 * Adds the reservation constants.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 */
	void addReservationConstants(EntityManager em, String userId, String locationId, HttpServletRequest httpRequest)
	{

		// todo shlok need to handle exception in below
		// modulise code here
		ReservationsStatus r = new ReservationsStatus();

		r.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		r.setCreatedBy(userId);
		r.setDisplayName("Cancelled");
		r.setDisplaySequence(1);
		r.setHexCodeValues("#EC1C24");
		r.setLocationsId(locationId);
		r.setName("Cancelled");
		r.setShowToCustomer((byte) 1);
		r.setStatus("F");
		r.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		r.setIsServerDriven(0);
		r.setUpdatedBy(userId);
		try
		{
			if (r.getId() == null)
				r.setId(new StoreForwardUtility().generateDynamicIntId(em, r.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(r);

		ReservationsStatus rStatus = new ReservationsStatus();

		rStatus.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus.setCreatedBy(userId);
		rStatus.setDisplayName("Confirmed");
		rStatus.setDisplaySequence(2);
		rStatus.setHexCodeValues("#D6DE23");
		rStatus.setLocationsId(locationId);
		rStatus.setName("Confirmed");
		rStatus.setShowToCustomer((byte) 1);
		rStatus.setStatus("F");
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus.setIsServerDriven(1);
		rStatus.setUpdatedBy(userId);
		try
		{
			rStatus.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus);

		ReservationsStatus rStatus1 = new ReservationsStatus();

		rStatus1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus1.setCreatedBy(userId);
		rStatus1.setDisplayName("Seated");
		rStatus1.setDisplaySequence(3);
		rStatus1.setHexCodeValues("#4D1313");
		rStatus1.setLocationsId(locationId);
		rStatus1.setName("Check In");
		rStatus1.setShowToCustomer((byte) 0);
		rStatus1.setStatus("F");
		rStatus1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus1.setUpdatedBy(userId);
		rStatus1.setIsServerDriven(1);
		try
		{
			rStatus1.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus1.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus1);

		ReservationsStatus rStatus2 = new ReservationsStatus();

		rStatus2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus2.setCreatedBy(userId);
		rStatus2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus2.setUpdatedBy(userId);
		rStatus2.setDisplayName("Waiting");
		rStatus2.setDisplaySequence(4);
		rStatus2.setHexCodeValues("#004E6E");
		rStatus2.setLocationsId(locationId);
		rStatus2.setName("Waiting");
		rStatus2.setShowToCustomer((byte) 0);
		rStatus2.setStatus("F");
		rStatus2.setIsServerDriven(1);
		try
		{
			rStatus2.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus2.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus2);

		ReservationsStatus rStatus3 = new ReservationsStatus();

		rStatus3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus3.setCreatedBy(userId);
		rStatus3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus3.setUpdatedBy(userId);
		rStatus3.setDisplayName("Send Text");
		rStatus3.setDisplaySequence(4);
		rStatus3.setHexCodeValues("#26A9E0");
		rStatus3.setLocationsId(locationId);
		rStatus3.setName("Send Text");
		rStatus3.setShowToCustomer((byte) 0);
		rStatus3.setStatus("F");
		rStatus3.setIsServerDriven(0);
		try
		{
			rStatus3.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus3.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus3);

		ReservationsStatus rStatus4 = new ReservationsStatus();

		rStatus4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus4.setCreatedBy(userId);
		rStatus4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus4.setUpdatedBy(userId);
		rStatus4.setDisplayName("Send Email");
		rStatus4.setDisplaySequence(4);
		rStatus4.setHexCodeValues("#C3996B");
		rStatus4.setLocationsId(locationId);
		rStatus4.setName("Send Email");
		rStatus4.setShowToCustomer((byte) 0);
		rStatus4.setStatus("F");
		rStatus4.setIsServerDriven(0);
		try
		{
			rStatus4.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus4.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus4);

		ReservationsStatus rStatus5 = new ReservationsStatus();

		rStatus5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus5.setCreatedBy(userId);
		rStatus5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus5.setUpdatedBy(userId);
		rStatus5.setDisplayName("Checked Out");
		rStatus5.setDisplaySequence(6);
		rStatus5.setHexCodeValues("#2cb522");
		rStatus5.setLocationsId(locationId);
		rStatus5.setName("Checked Out");
		rStatus5.setShowToCustomer((byte) 1);
		rStatus5.setStatus("F");
		rStatus5.setIsServerDriven(1);
		try
		{
			rStatus5.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus5.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus5);

		ReservationsStatus rStatus6 = new ReservationsStatus();

		rStatus6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus6.setCreatedBy(userId);
		rStatus6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus6.setUpdatedBy(userId);
		rStatus6.setDisplayName("Void Walkin");
		rStatus6.setDisplaySequence(6);
		rStatus6.setHexCodeValues("#53fg522");
		rStatus6.setLocationsId(locationId);
		rStatus6.setName("Void Walkin");
		rStatus6.setShowToCustomer((byte) 0);
		rStatus6.setStatus("F");
		rStatus6.setIsServerDriven(1);
		try
		{
			rStatus6.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus6.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus6);

		ReservationsStatus rStatus7 = new ReservationsStatus();
		rStatus7.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus7.setCreatedBy(userId);
		rStatus7.setDisplayName("Tab");
		rStatus7.setDisplaySequence(7);
		rStatus7.setHexCodeValues("#D6DE85");
		rStatus7.setLocationsId(locationId);
		rStatus7.setName("Tab");
		rStatus7.setShowToCustomer((byte) 0);
		rStatus7.setStatus("F");
		rStatus7.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus7.setIsServerDriven(0);
		rStatus7.setUpdatedBy(userId);
		try
		{
			rStatus7.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus7.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus7);

		ReservationsStatus rStatus9 = new ReservationsStatus();
		rStatus9.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus9.setCreatedBy(userId);
		rStatus9.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus9.setUpdatedBy(userId);
		rStatus9.setDisplayName("Preassigned");
		rStatus9.setDisplaySequence(7);
		rStatus9.setHexCodeValues("#579416");
		rStatus9.setLocationsId(locationId);
		rStatus9.setName("Preassigned");
		rStatus9.setShowToCustomer((byte) 0);
		rStatus9.setStatus("F");
		rStatus9.setIsServerDriven(0);
		try
		{
			rStatus9.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus9.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus9);

		ReservationsStatus rStatus8 = new ReservationsStatus();
		rStatus8.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus8.setCreatedBy(userId);
		rStatus8.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus8.setUpdatedBy(userId);
		rStatus8.setDisplayName("Order Ahead");
		rStatus8.setDisplaySequence(8);
		rStatus8.setHexCodeValues("#579406");
		rStatus8.setLocationsId(locationId);
		rStatus8.setName("Pre Order");
		rStatus8.setShowToCustomer((byte) 0);
		rStatus8.setStatus("F");
		rStatus8.setIsServerDriven(0);
		try
		{
			rStatus8.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus8.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus8);

		ReservationsStatus rStatus12 = new ReservationsStatus();
		rStatus12.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus12.setCreatedBy(userId);
		rStatus12.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus12.setUpdatedBy(userId);
		rStatus12.setDisplayName("Checked In");
		rStatus12.setDisplaySequence(9);
		rStatus12.setHexCodeValues("#573b3d");
		rStatus12.setLocationsId(locationId);
		rStatus12.setName("Checked In");
		rStatus12.setShowToCustomer((byte) 1);
		rStatus12.setStatus("F");
		rStatus12.setIsServerDriven(1);
		try
		{
			rStatus12.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus12.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus12);

		ReservationsStatus rStatus13 = new ReservationsStatus();
		rStatus13.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus13.setCreatedBy(userId);
		rStatus13.setDisplayName("Order Merged");
		rStatus13.setDisplaySequence(10);
		rStatus13.setHexCodeValues("#4D1313");
		rStatus13.setLocationsId(locationId);
		rStatus13.setName("Order Merged");
		rStatus13.setShowToCustomer((byte) 0);
		rStatus13.setStatus("F");
		rStatus13.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus13.setUpdatedBy(userId);
		rStatus13.setIsServerDriven(1);
		try
		{
			rStatus13.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus13.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus13);
		logger.severe("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!@@@@@@@@@@@@@@@@"+rStatus13.getId());
		ReservationsStatus rStatus14 = new ReservationsStatus();
		rStatus14.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus14.setCreatedBy(userId);
		rStatus14.setDisplayName("Running Late");
		rStatus14.setDisplaySequence(11);
		rStatus14.setHexCodeValues("#4D1313");
		rStatus14.setLocationsId(locationId);
		rStatus14.setName("Running Late");
		rStatus14.setShowToCustomer((byte) 0);
		rStatus14.setStatus("F");
		rStatus14.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus14.setUpdatedBy(userId);
		rStatus14.setIsServerDriven(1);
		try
		{
			rStatus14.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus14.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}		ReservationsStatus rStatus15 = new ReservationsStatus();
		rStatus15.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus15.setCreatedBy(userId);
		rStatus15.setDisplayName("Partially Arrived");
		rStatus15.setDisplaySequence(10);
		rStatus15.setHexCodeValues("#4D1313");
		rStatus15.setLocationsId(locationId);
		rStatus15.setName("Partially Arrived");
		rStatus15.setShowToCustomer((byte) 0);
		rStatus15.setStatus("F");
		rStatus15.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus15.setUpdatedBy(userId);
		rStatus15.setIsServerDriven(1);
		try
		{
			rStatus15.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus15.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}		ReservationsStatus rStatus16 = new ReservationsStatus();
		rStatus16.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus16.setCreatedBy(userId);
		rStatus16.setDisplayName("All Arrived");
		rStatus16.setDisplaySequence(10);
		rStatus16.setHexCodeValues("#4D1313");
		rStatus16.setLocationsId(locationId);
		rStatus16.setName("All Arrived");
		rStatus16.setShowToCustomer((byte) 0);
		rStatus16.setStatus("F");
		rStatus16.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus16.setUpdatedBy(userId);
		rStatus16.setIsServerDriven(1);
		try
		{
			rStatus16.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus16.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}		ReservationsStatus rStatus18 = new ReservationsStatus();
		rStatus18.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus18.setCreatedBy(userId);
		rStatus18.setDisplayName("No Show");
		rStatus18.setDisplaySequence(10);
		rStatus18.setHexCodeValues("#4D1313");
		rStatus18.setLocationsId(locationId);
		rStatus18.setName("No Show");
		rStatus18.setShowToCustomer((byte) 0);
		rStatus18.setStatus("F");
		rStatus18.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus18.setUpdatedBy(userId);
		rStatus18.setIsServerDriven(1);
		try
		{
			rStatus18.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus18.getLocationsId(), httpRequest, "reservations_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rStatus18);

	}

	/**
	 * Adds the source for dine in source group.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param oGroupDineIn
	 *            the o group dine in
	 * @param em
	 *            the em
	 * @param globalLocationId
	 *            the global location id
	 */
	private void addSourceForDineInSourceGroup(String userId, String locationId, OrderSourceGroup oGroupDineIn, EntityManager em, String globalLocationId)
	{

		// todo shlok need to handle exception in below
		// modulise code here
		OrderSource oSource = new OrderSource();

		oSource.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource.setCreatedBy(userId);
		oSource.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource.setUpdatedBy(userId);
		oSource.setLocationsId(locationId);
		oSource.setStatus("F");
		oSource.setDisplaySequence(1);
		oSource.setDisplayName("Reservation");
		oSource.setName("Reservation");
		oSource.setOrderSourceGroupId(oGroupDineIn.getId());
		oSource.setGlobalId(getOrderSource(em, oSource.getName(), globalLocationId));
		oSource.setId(new StoreForwardUtility().generateUUID());
		em.persist(oSource);

		OrderSource oSource1 = new OrderSource();

		oSource1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource1.setCreatedBy(userId);
		oSource1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource1.setUpdatedBy(userId);
		oSource1.setLocationsId(locationId);
		oSource1.setStatus("F");
		oSource1.setDisplaySequence(2);
		oSource1.setDisplayName("Walk In");
		oSource1.setName("Walk In");
		oSource1.setOrderSourceGroupId(oGroupDineIn.getId());
		oSource1.setGlobalId(getOrderSource(em, oSource1.getName(), globalLocationId));
		oSource1.setId(new StoreForwardUtility().generateUUID());
		em.persist(oSource1);

		OrderSource oSource2 = new OrderSource();

		oSource2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource2.setCreatedBy(userId);
		oSource2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource2.setUpdatedBy(userId);
		oSource2.setLocationsId(locationId);
		oSource2.setStatus("F");
		oSource2.setDisplaySequence(3);
		oSource2.setDisplayName("Waitlist");
		oSource2.setName("Waitlist");
		oSource2.setOrderSourceGroupId(oGroupDineIn.getId());
		oSource2.setGlobalId(getOrderSource(em, oSource2.getName(), globalLocationId));
		oSource2.setId(new StoreForwardUtility().generateUUID());
		em.persist(oSource2);

		OrderSource oSource3 = new OrderSource();

		oSource3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource3.setCreatedBy(userId);
		oSource3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource3.setUpdatedBy(userId);
		oSource3.setLocationsId(locationId);
		oSource3.setStatus("F");
		oSource3.setDisplaySequence(3);
		oSource3.setDisplayName("Web");
		oSource3.setName("Web");
		oSource3.setOrderSourceGroupId(oGroupDineIn.getId());
		oSource3.setGlobalId(getOrderSource(em, oSource3.getName(), globalLocationId));
		oSource3.setId(new StoreForwardUtility().generateUUID());
		em.persist(oSource3);

		OrderSource oSource4 = new OrderSource();

		oSource4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource4.setCreatedBy(userId);
		oSource4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource4.setUpdatedBy(userId);
		oSource4.setLocationsId(locationId);
		oSource4.setStatus("F");
		oSource4.setDisplaySequence(3);
		oSource4.setDisplayName("Mobile App");
		oSource4.setName("Mobile App");
		oSource4.setOrderSourceGroupId(oGroupDineIn.getId());
		oSource4.setGlobalId(getOrderSource(em, oSource4.getName(), globalLocationId));
		oSource4.setId(new StoreForwardUtility().generateUUID());
		em.persist(oSource4);

		OrderSource oSource5 = new OrderSource();

		oSource5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource5.setCreatedBy(userId);
		oSource5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oSource5.setUpdatedBy(userId);
		oSource5.setLocationsId(locationId);
		oSource5.setStatus("F");
		oSource5.setDisplaySequence(3);
		oSource5.setDisplayName("Production");
		oSource5.setName("Production");
		oSource5.setOrderSourceGroupId(oGroupDineIn.getId());
		oSource5.setGlobalId(getOrderSource(em, oSource5.getName(), globalLocationId));
		oSource5.setId(new StoreForwardUtility().generateUUID());
		em.persist(oSource5);

	}

	/**
	 * Adds the source group for pick up and delivery.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param oGroupDineIn
	 *            the o group dine in
	 * @param em
	 *            the em
	 * @param globalLocationId
	 *            the global location id
	 */
	private void addSourceGroupForPickUpAndDelivery(String userId, String locationId, OrderSourceGroup oGroupDineIn, EntityManager em, String globalLocationId)
	{

		for (int index = 1; index <= 6; index++)
		{

			OrderSource oSource = new OrderSource();
			oSource.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			oSource.setCreatedBy(userId);
			oSource.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			oSource.setUpdatedBy(userId);
			oSource.setLocationsId(locationId);
			oSource.setStatus("F");
			oSource.setOrderSourceGroupId(oGroupDineIn.getId());
			oSource.setDisplaySequence(index);

			if (index == 1)
			{
				oSource.setDisplayName("Phone");
				oSource.setName("Phone");
				oSource.setGlobalId(getOrderSource(em, oSource.getName(), globalLocationId));
				oSource.setId(new StoreForwardUtility().generateUUID());
				em.persist(oSource);

			}
			else if (index == 2)
			{
				oSource.setDisplayName("Web");
				oSource.setName("Web");
				oSource.setGlobalId(getOrderSource(em, oSource.getName(), globalLocationId));
				oSource.setId(new StoreForwardUtility().generateUUID());
				em.persist(oSource);

			}
			else if (index == 3)
			{
				oSource.setDisplayName("Mobile App");
				oSource.setName("Mobile App");
				oSource.setGlobalId(getOrderSource(em, oSource.getName(), globalLocationId));
				oSource.setId(new StoreForwardUtility().generateUUID());
				em.persist(oSource);

			}
			else if (index == 4)
			{

				oSource.setDisplayName("Walk In");
				oSource.setName("Walk In");
				oSource.setGlobalId(getOrderSource(em, oSource.getName(), globalLocationId));
				oSource.setId(new StoreForwardUtility().generateUUID());
				em.persist(oSource);

			}
			else if (index == 5)
			{
				oSource.setDisplayName("Internal");
				oSource.setName("Internal");
				oSource.setGlobalId(getOrderSource(em, oSource.getName(), globalLocationId));
				oSource.setId(new StoreForwardUtility().generateUUID());
				em.persist(oSource);
			}
			else if (index == 6)
			{
				oSource.setDisplayName("External");
				oSource.setName("External");
				oSource.setGlobalId(getOrderSource(em, oSource.getName(), globalLocationId));
				oSource.setId(new StoreForwardUtility().generateUUID());
				em.persist(oSource);
			}
		}

	}

	/**
	 * Adds the order sourse group constants.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param globalLocationId
	 *            the global location id
	 */
	void addOrderSourseGroupConstants(EntityManager em, String userId, String locationId, String globalLocationId, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code here
		OrderSourceGroup oGroupDineIn = new OrderSourceGroup();

		oGroupDineIn.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oGroupDineIn.setCreatedBy(userId);
		oGroupDineIn.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oGroupDineIn.setUpdatedBy(userId);
		oGroupDineIn.setLocationsId(locationId);
		oGroupDineIn.setStatus("F");

		oGroupDineIn.setDisplaySequence(1);
		oGroupDineIn.setDisplayName("In Store");
		oGroupDineIn.setName("In Store");
		oGroupDineIn.setDescription("Used when guests dine in thru reservation,walk in or waitlist");
		oGroupDineIn.setGlobalOrderSourceGroupId(getOrderSourceGroup(em, oGroupDineIn.getName(), globalLocationId));
		try
		{
			if (oGroupDineIn.getId() == null)
				oGroupDineIn.setId(new StoreForwardUtility().generateDynamicIntId(em, oGroupDineIn.getLocationsId(), httpRequest, "order_source_group"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oGroupDineIn);

		addSourceForDineInSourceGroup(userId, locationId, oGroupDineIn, em, globalLocationId);
		addOrderDetailStatusConstants(em, userId, locationId, oGroupDineIn.getId());
		addOrderStatus1Constants(em, userId, locationId, oGroupDineIn.getId(), httpRequest);

		OrderSourceGroup oGroupPickUp = new OrderSourceGroup();

		oGroupPickUp.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oGroupPickUp.setCreatedBy(userId);
		oGroupPickUp.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oGroupPickUp.setUpdatedBy(userId);
		oGroupPickUp.setLocationsId(locationId);
		oGroupPickUp.setStatus("F");

		oGroupPickUp.setDisplaySequence(2);
		oGroupPickUp.setDisplayName("Pick Up");
		oGroupPickUp.setName("Pick Up");
		oGroupPickUp.setDescription("Used when order placed online or via phone");
		oGroupPickUp.setGlobalOrderSourceGroupId(getOrderSourceGroup(em, oGroupPickUp.getName(), globalLocationId));
		try
		{
			oGroupPickUp.setId(new StoreForwardUtility().generateDynamicIntId(em, oGroupPickUp.getLocationsId(), httpRequest, "order_source_group"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oGroupPickUp);

		addSourceGroupForPickUpAndDelivery(userId, locationId, oGroupPickUp, em, globalLocationId);
		addOrderStatusForPickUpAndDeleiverySourceGroup(em, userId, locationId, oGroupPickUp.getId(), true, httpRequest);

		OrderSourceGroup oGroupDelievery = new OrderSourceGroup();

		oGroupDelievery.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oGroupDelievery.setCreatedBy(userId);
		oGroupDelievery.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oGroupDelievery.setUpdatedBy(userId);
		oGroupDelievery.setLocationsId(locationId);
		oGroupDelievery.setStatus("F");

		oGroupDelievery.setDisplaySequence(3);
		oGroupDelievery.setDisplayName("Delivery");
		oGroupDelievery.setName("Delivery");
		oGroupDelievery.setDescription("Used when order placed for take home purpose");
		oGroupDelievery.setGlobalOrderSourceGroupId(getOrderSourceGroup(em, oGroupDelievery.getName(), globalLocationId));
		try
		{
			oGroupDelievery.setId(new StoreForwardUtility().generateDynamicIntId(em, oGroupDelievery.getLocationsId(), httpRequest, "order_source_group"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oGroupDelievery);

		addSourceGroupForPickUpAndDelivery(userId, locationId, oGroupDelievery, em, globalLocationId);
		addOrderStatusForPickUpAndDeleiverySourceGroup(em, userId, locationId, oGroupDelievery.getId(), false, httpRequest);

		OrderSourceGroup oGroupInventory = new OrderSourceGroup();

		oGroupInventory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oGroupInventory.setCreatedBy(userId);
		oGroupInventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oGroupInventory.setUpdatedBy(userId);
		oGroupInventory.setLocationsId(locationId);
		oGroupInventory.setStatus("F");

		oGroupInventory.setDisplaySequence(3);
		oGroupInventory.setDisplayName("Inventory");
		oGroupInventory.setName("Inventory");
		oGroupInventory.setDescription("Used when request order placed for business");
		oGroupInventory.setGlobalOrderSourceGroupId(getOrderSourceGroup(em, oGroupInventory.getName(), globalLocationId));
		try
		{
			oGroupInventory.setId(new StoreForwardUtility().generateDynamicIntId(em, oGroupInventory.getLocationsId(), httpRequest, "order_source_group"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oGroupInventory);

		addSourceGroupForPickUpAndDelivery(userId, locationId, oGroupInventory, em, globalLocationId);
		addOrderStatusForInventory(em, userId, locationId, oGroupInventory.getId(), httpRequest);

	}

	/**
	 * Adds the feedback constants.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 */
	void addFeedbackConstants(EntityManager em, String userId, String locationId, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code here
		FeedbackType defaultFeedbackType = null;
		defaultFeedbackType = new FeedbackType();
		defaultFeedbackType.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		defaultFeedbackType.setCreatedBy(userId);
		defaultFeedbackType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		defaultFeedbackType.setUpdatedBy(userId);
		defaultFeedbackType.setLocationsId(locationId);
		defaultFeedbackType.setStatus("A");
		defaultFeedbackType.setFeedbackTypeName("Default");
		 
		em.persist(defaultFeedbackType);

		FeedbackType customFeedbackType = null;
		customFeedbackType = new FeedbackType();
		customFeedbackType.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		customFeedbackType.setCreatedBy(userId);
		customFeedbackType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		customFeedbackType.setUpdatedBy(userId);
		customFeedbackType.setLocationsId(locationId);
		customFeedbackType.setStatus("I");
		customFeedbackType.setFeedbackTypeName("Custom");
		 
		em.persist(customFeedbackType);

		FeedbackField feedbackField1 = new FeedbackField();
		feedbackField1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField1.setCreatedBy(userId);
		feedbackField1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField1.setUpdatedBy(userId);
		feedbackField1.setLocationsId(locationId);
		feedbackField1.setStatus("F");
		feedbackField1.setFieldName("Phone");
		feedbackField1.setFieldTypeId(1);
		feedbackField1.setDisplaySequence(3);
		feedbackField1.setDisplayName("Phone");

		em.persist(feedbackField1);

		FeedbackField feedbackField2 = new FeedbackField();
		feedbackField2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField2.setCreatedBy(userId);
		feedbackField2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField2.setUpdatedBy(userId);
		feedbackField2.setLocationsId(locationId);
		feedbackField2.setStatus("F");
		feedbackField2.setFieldName("Email");
		feedbackField2.setFieldTypeId(1);
		feedbackField2.setDisplaySequence(4);
		feedbackField2.setDisplayName("Email");

		em.persist(feedbackField2);

		FeedbackField feedbackField3 = new FeedbackField();
		feedbackField3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField3.setCreatedBy(userId);
		feedbackField3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField3.setUpdatedBy(userId);
		feedbackField3.setLocationsId(locationId);
		feedbackField3.setStatus("F");
		feedbackField3.setFieldName("Date of birth");
		feedbackField3.setFieldTypeId(1);
		feedbackField3.setDisplaySequence(5);
		feedbackField3.setDisplayName("Date of birth");

		em.persist(feedbackField3);

		FeedbackField feedbackField6 = new FeedbackField();
		feedbackField6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField6.setCreatedBy(userId);
		feedbackField6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField6.setUpdatedBy(userId);
		feedbackField6.setLocationsId(locationId);
		feedbackField6.setStatus("F");
		feedbackField6.setFieldName("First Name");
		feedbackField6.setFieldTypeId(1);
		feedbackField6.setDisplaySequence(1);
		feedbackField6.setDisplayName("First Name");

		em.persist(feedbackField6);

		FeedbackField feedbackField7 = new FeedbackField();
		feedbackField7.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField7.setCreatedBy(userId);
		feedbackField7.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField7.setUpdatedBy(userId);
		feedbackField7.setLocationsId(locationId);
		feedbackField7.setStatus("F");
		feedbackField7.setFieldName("Last Name");
		feedbackField7.setFieldTypeId(1);
		feedbackField7.setDisplaySequence(2);
		feedbackField7.setDisplayName("Last Name");

		em.persist(feedbackField7);

		FeedbackField feedbackField8 = new FeedbackField();
		feedbackField8.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField8.setCreatedBy(userId);
		feedbackField8.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField8.setUpdatedBy(userId);
		feedbackField8.setLocationsId(locationId);
		feedbackField8.setStatus("F");
		feedbackField8.setFieldName("Comments/Suggestions");
		feedbackField8.setFieldTypeId(3);
		feedbackField8.setDisplaySequence(6);
		feedbackField8.setDisplayName("Comments/Suggestions");
		em.persist(feedbackField8);

		FeedbackField feedbackField9 = new FeedbackField();
		feedbackField9.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField9.setCreatedBy(userId);
		feedbackField9.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackField9.setUpdatedBy(userId);
		feedbackField9.setLocationsId(locationId);
		feedbackField9.setStatus("F");
		feedbackField9.setFieldName("Manager Response");
		feedbackField9.setFieldTypeId(3);
		feedbackField9.setDisplaySequence(6);
		feedbackField9.setDisplayName("Manager Response");

		em.persist(feedbackField9);

		FeedbackQuestion feedbackQuestion1 = new FeedbackQuestion();
		feedbackQuestion1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackQuestion1.setCreatedBy(userId);
		feedbackQuestion1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackQuestion1.setUpdatedBy(userId);
		feedbackQuestion1.setLocationsId(locationId);
		feedbackQuestion1.setStatus("F");
		feedbackQuestion1.setFeedbackQuestion("Customer Experience");
		if (defaultFeedbackType != null)
		{
			feedbackQuestion1.setFeedbackTypeId(defaultFeedbackType.getId());
		}
		feedbackQuestion1.setDisplaySequence(1);
		try
		{
			feedbackQuestion1.setId(new StoreForwardUtility().generateDynamicIntId(em, feedbackQuestion1.getLocationsId(), httpRequest, "feedback_question"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(feedbackQuestion1);

		FeedbackQuestion feedbackQuestion2 = new FeedbackQuestion();
		feedbackQuestion2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackQuestion2.setCreatedBy(userId);
		feedbackQuestion2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		feedbackQuestion2.setUpdatedBy(userId);
		feedbackQuestion2.setLocationsId(locationId);
		feedbackQuestion2.setStatus("F");
		feedbackQuestion2.setFeedbackQuestion("Overall Experience");
		if (customFeedbackType != null)
		{
			feedbackQuestion2.setFeedbackTypeId(customFeedbackType.getId());
		}

		feedbackQuestion2.setDisplaySequence(2);
		try
		{
			feedbackQuestion2.setId(new StoreForwardUtility().generateDynamicIntId(em, feedbackQuestion2.getLocationsId(), httpRequest, "feedback_question"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(feedbackQuestion2);

		if (defaultFeedbackType != null && customFeedbackType != null)
		{
			addSmileyTypeFeedbackConstant(em, userId, locationId, defaultFeedbackType.getId(), customFeedbackType.getId());
		}

	}

	/**
	 * Adds the smiley type feedback constant.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param defaultFeedbackTypeId
	 *            the default feedback type id
	 * @param customFeedbackTypeId
	 *            the custom feedback type id
	 */
	void addSmileyTypeFeedbackConstant(EntityManager em, String userId, String locationId, int defaultFeedbackTypeId, int customFeedbackTypeId)
	{
		// todo shlok need to handle exception in below
		// modulise code here
		Smiley smiley1 = new Smiley();
		smiley1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley1.setCreatedBy(userId);
		smiley1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley1.setUpdatedBy(userId);
		smiley1.setSimleyName("Happy face");
		smiley1.setImageName("face-good-blue.png");

		smiley1.setFeedbackTypeId(defaultFeedbackTypeId);
		smiley1.setStarValue(5);

		em.persist(smiley1);

		Smiley smiley2 = new Smiley();
		smiley2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley2.setCreatedBy(userId);
		smiley2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley2.setUpdatedBy(userId);
		smiley2.setSimleyName("Straight face");
		smiley2.setImageName("face-medium-blue.png");
		smiley2.setFeedbackTypeId(defaultFeedbackTypeId);
		smiley2.setStarValue(3);

		em.persist(smiley2);

		Smiley smiley3 = new Smiley();
		smiley3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley3.setCreatedBy(userId);
		smiley3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley3.setUpdatedBy(userId);
		smiley3.setSimleyName("Sad face");
		smiley3.setImageName("face-bad-blue.png");
		smiley3.setFeedbackTypeId(defaultFeedbackTypeId);
		smiley3.setStarValue(1);

		em.persist(smiley3);

		Smiley smiley4 = new Smiley();
		smiley4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley4.setCreatedBy(userId);
		smiley4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley4.setUpdatedBy(userId);
		smiley4.setSimleyName("1 star");
		smiley4.setImageName(null);
		smiley4.setFeedbackTypeId(customFeedbackTypeId);
		smiley4.setStarValue(1);

		em.persist(smiley4);

		Smiley smiley5 = new Smiley();
		smiley5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley5.setCreatedBy(userId);
		smiley5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley5.setUpdatedBy(userId);
		smiley5.setSimleyName("2 star");
		smiley5.setImageName(null);
		smiley5.setFeedbackTypeId(customFeedbackTypeId);
		smiley5.setStarValue(2);

		em.persist(smiley5);

		Smiley smiley6 = new Smiley();
		smiley6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley6.setCreatedBy(userId);
		smiley6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley6.setUpdatedBy(userId);
		smiley6.setSimleyName("3 star");
		smiley6.setImageName(null);
		smiley6.setFeedbackTypeId(customFeedbackTypeId);
		smiley6.setStarValue(3);

		em.persist(smiley6);

		Smiley smiley7 = new Smiley();
		smiley7.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley7.setCreatedBy(userId);
		smiley7.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley7.setUpdatedBy(userId);
		smiley7.setSimleyName("4 star");
		smiley7.setImageName(null);
		smiley7.setFeedbackTypeId(customFeedbackTypeId);
		smiley7.setStarValue(4);

		em.persist(smiley7);

		Smiley smiley8 = new Smiley();
		smiley8.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley8.setCreatedBy(userId);
		smiley8.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smiley8.setUpdatedBy(userId);
		smiley8.setSimleyName("5 star");
		smiley8.setImageName(null);
		smiley8.setFeedbackTypeId(customFeedbackTypeId);
		smiley8.setStarValue(5);

		em.persist(smiley8);

	}

	/**
	 * Adds the reason type constant.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 */
	void addReasonTypeConstant(EntityManager em, String locationId, String userId, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code here
		ReasonType reasonType = getReasonTypeByName("Void Reasons", em);
		if (reasonType != null)
		{
			Reasons reasons = new Reasons();
			reasons.setCreatedBy(userId);
			reasons.setUpdatedBy(userId);
			reasons.setDisplayName("Seating Tab Order");
			reasons.setLocationsId(locationId);
			reasons.setName("Seating Tab Order");
			reasons.setReasonTypeId(reasonType.getId());
			reasons.setStatus("F");
			reasons.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				if (reasons.getId() == null)
					reasons.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons);

		}
		ReasonType reasonType1 = getReasonTypeByName("Cancel Reasons", em);
		if (reasonType1 != null)
		{
			Reasons reasons = new Reasons();
			reasons.setCreatedBy(userId);
			reasons.setUpdatedBy(userId);
			reasons.setDisplayName("Seating Tab Order");
			reasons.setLocationsId(locationId);
			reasons.setName("Seating Tab Order");
			reasons.setReasonTypeId(reasonType1.getId());
			reasons.setStatus("F");
			reasons.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons);

			Reasons reasons2 = new Reasons();
			reasons2.setCreatedBy(userId);
			reasons2.setUpdatedBy(userId);
			reasons2.setDisplayName("Order Merged");
			reasons2.setLocationsId(locationId);
			reasons2.setName("Order Merged");
			reasons2.setReasonTypeId(reasonType1.getId());
			reasons2.setStatus("F");
			reasons2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons2.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons2.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons2);

		}
		ReasonType reasonType2 = getReasonTypeByName("Order Suspended", em);
		if (reasonType2 != null)
		{
			Reasons reasons3 = new Reasons();
			reasons3.setCreatedBy(userId);
			reasons3.setUpdatedBy(userId);
			reasons3.setDisplayName("Order Delayed");
			reasons3.setLocationsId(locationId);
			reasons3.setName("Order Delayed");
			reasons3.setReasonTypeId(reasonType2.getId());
			reasons3.setStatus("F");
			reasons3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons3.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons3.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons3);
		}
		ReasonType reasonType3 = getReasonTypeByName("Forcefully Closed", em);
		if (reasonType3 != null)
		{
			Reasons reasons4 = new Reasons();
			reasons4.setCreatedBy(userId);
			reasons4.setUpdatedBy(userId);
			reasons4.setDisplayName("Wrong Order Delivered");
			reasons4.setLocationsId(locationId);
			reasons4.setName("Wrong Order Delivered");
			reasons4.setReasonTypeId(reasonType3.getId());
			reasons4.setStatus("F");
			reasons4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons4.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons4.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons4);
		}

		ReasonType reasonType4 = getReasonTypeByName("Paid Out", em);
		if (reasonType4 != null)
		{
			Reasons reasons = new Reasons();
			reasons.setCreatedBy(userId);
			reasons.setUpdatedBy(userId);
			reasons.setDisplayName("Close Out with Cash Reconcile");
			reasons.setLocationsId(locationId);
			reasons.setName("Close Out with Cash Reconcile");
			reasons.setReasonTypeId(reasonType4.getId());
			reasons.setStatus("F");
			reasons.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons);

			Reasons reasons2 = new Reasons();
			reasons2.setCreatedBy(userId);
			reasons2.setUpdatedBy(userId);
			reasons2.setDisplayName("Close Out without Cash Reconcile");
			reasons2.setLocationsId(locationId);
			reasons2.setName("Close Out without Cash Reconcile");
			reasons2.setReasonTypeId(reasonType4.getId());
			reasons2.setStatus("F");
			reasons2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons2.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons2.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons2);

			Reasons reasons3 = new Reasons();
			reasons3.setCreatedBy(userId);
			reasons3.setUpdatedBy(userId);
			reasons3.setDisplayName("Close Out with carry forward");
			reasons3.setLocationsId(locationId);
			reasons3.setName("Close Out with carry forward");
			reasons3.setReasonTypeId(reasonType4.getId());
			reasons3.setStatus("F");
			reasons3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons3.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons3.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons3);

		}

		ReasonType reasonType5 = getReasonTypeByName("Paid In", em);
		if (reasonType5 != null)
		{
			Reasons reasons = new Reasons();
			reasons.setCreatedBy(userId);
			reasons.setUpdatedBy(userId);
			reasons.setDisplayName("Business Start with Cash Loan");
			reasons.setLocationsId(locationId);
			reasons.setName("Business Start with Cash Loan");
			reasons.setReasonTypeId(reasonType5.getId());
			reasons.setStatus("F");
			reasons.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons);

		}

		ReasonType reasonType6 = getReasonTypeByName("Discount Reasons", em);
		if (reasonType6 != null)
		{
			Reasons reasons4 = new Reasons();
			reasons4.setCreatedBy(userId);
			reasons4.setUpdatedBy(userId);
			reasons4.setDisplayName("No Reasons");
			reasons4.setLocationsId(locationId);
			reasons4.setName("No Reasons");
			reasons4.setReasonTypeId(reasonType6.getId());
			reasons4.setStatus("F");
			reasons4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons4.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons4.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons4);
		}

		ReasonType reasonType7 = getReasonTypeByName("Inventory Reasons", em);
		if (reasonType7 != null)
		{
			Reasons reasons4 = new Reasons();
			reasons4.setCreatedBy(userId);
			reasons4.setUpdatedBy(userId);
			reasons4.setDisplayName("Return");
			reasons4.setLocationsId(locationId);
			reasons4.setName("Return");
			reasons4.setReasonTypeId(reasonType7.getId());
			reasons4.setStatus("F");
			reasons4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons4.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons4.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons4);

			Reasons reasons5 = new Reasons();
			reasons5.setCreatedBy(userId);
			reasons5.setUpdatedBy(userId);
			reasons5.setDisplayName("Discrepancy");
			reasons5.setLocationsId(locationId);
			reasons5.setName("Discrepancy");
			reasons5.setReasonTypeId(reasonType7.getId());
			reasons5.setStatus("F");
			reasons5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons5.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons5.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons5);

			Reasons reasons6 = new Reasons();
			reasons6.setCreatedBy(userId);
			reasons6.setUpdatedBy(userId);
			reasons6.setDisplayName("Wastage");
			reasons6.setLocationsId(locationId);
			reasons6.setName("Wastage");
			reasons6.setReasonTypeId(reasonType7.getId());
			reasons6.setStatus("F");
			reasons6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reasons6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				reasons6.setId(new StoreForwardUtility().generateDynamicIntId(em, reasons6.getLocationsId(), httpRequest, "reasons"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(reasons6);
		}

	}

	/**
	 * Gets the reason type by name.
	 *
	 * @param name
	 *            the name
	 * @param em
	 *            the em
	 * @return the reason type by name
	 */
	private ReasonType getReasonTypeByName(String name, EntityManager em)
	{
		String queryString = "select rt from ReasonType rt  where rt.name ='" + name + "'";
		TypedQuery<ReasonType> query = em.createQuery(queryString, ReasonType.class);
		ReasonType resultSet = query.getSingleResult();

		return resultSet;
	}

	/**
	 * Adds the printer constant.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param globalLocationId
	 *            the global location id
	 */
	void addPrinterConstant(EntityManager em, String userId, String locationId, String globalLocationId, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code here

		PrintersType printersType1 = new PrintersType();
		printersType1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersType1.setCreatedBy(userId);
		printersType1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersType1.setUpdatedBy(userId);
		printersType1.setLocationsId(locationId);
		printersType1.setStatus("F");
		printersType1.setName("Kitchen Printer");
		printersType1.setGlobalPrintersTypeId(getPrintersType(em, printersType1.getName(), globalLocationId));
		try
		{
			if (printersType1.getId() == null)
				printersType1.setId(new StoreForwardUtility().generateDynamicIntId(em, printersType1.getLocationsId(), httpRequest, "printers_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(printersType1);

		PrintersType printersType2 = new PrintersType();
		printersType2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersType2.setCreatedBy(userId);
		printersType2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersType2.setUpdatedBy(userId);
		printersType2.setLocationsId(locationId);
		printersType2.setStatus("F");
		printersType2.setName("Bar Printer");
		printersType2.setGlobalPrintersTypeId(getPrintersType(em, printersType2.getName(), globalLocationId));
		try
		{
			printersType2.setId(new StoreForwardUtility().generateDynamicIntId(em, printersType2.getLocationsId(), httpRequest, "printers_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(printersType2);

		PrintersType printersType3 = new PrintersType();
		printersType3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersType3.setCreatedBy(userId);
		printersType3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersType3.setUpdatedBy(userId);
		printersType3.setLocationsId(locationId);
		printersType3.setStatus("F");
		printersType3.setName("Receipt Printer");
		printersType3.setGlobalPrintersTypeId(getPrintersType(em, printersType3.getName(), globalLocationId));
		try
		{
			printersType3.setId(new StoreForwardUtility().generateDynamicIntId(em, printersType3.getLocationsId(), httpRequest, "printers_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(printersType3);

		PrintersType printersType4 = new PrintersType();
		printersType4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersType4.setCreatedBy(userId);
		printersType4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersType4.setUpdatedBy(userId);
		printersType4.setLocationsId(locationId);
		printersType4.setStatus("F");
		printersType4.setName("Clock In Clock Out Printer");
		printersType4.setGlobalPrintersTypeId(getPrintersType(em, printersType4.getName(), globalLocationId));
		try
		{
			printersType4.setId(new StoreForwardUtility().generateDynamicIntId(em, printersType4.getLocationsId(), httpRequest, "printers_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(printersType4);

		PrintersInterface printersInterface1 = new PrintersInterface();
		printersInterface1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersInterface1.setCreatedBy(userId);
		printersInterface1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersInterface1.setUpdatedBy(userId);
		printersInterface1.setLocationsId(locationId);
		printersInterface1.setStatus("F");
		printersInterface1.setName("Bluetooth");
		printersInterface1.setGlobalPrintersInterfaceId(getPrintersInterface(em, printersInterface1.getName(), globalLocationId));
		try
		{
			printersInterface1.setId(new StoreForwardUtility().generateDynamicIntId(em, printersInterface1.getLocationsId(), httpRequest, "printers_interface"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(printersInterface1);

		PrintersInterface printersInterface2 = new PrintersInterface();
		printersInterface2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersInterface2.setCreatedBy(userId);
		printersInterface2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersInterface2.setUpdatedBy(userId);
		printersInterface2.setLocationsId(locationId);
		printersInterface2.setStatus("F");
		printersInterface2.setName("IP");
		printersInterface2.setGlobalPrintersInterfaceId(getPrintersInterface(em, printersInterface2.getName(), globalLocationId));
		try
		{
			printersInterface2.setId(new StoreForwardUtility().generateDynamicIntId(em, printersInterface2.getLocationsId(), httpRequest, "printers_interface"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(printersInterface2);
		
		PrintersInterface printersInterface3 = new PrintersInterface();
		printersInterface3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersInterface3.setCreatedBy(userId);
		printersInterface3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersInterface3.setUpdatedBy(userId);
		printersInterface3.setLocationsId(locationId);
		printersInterface3.setStatus("F");
		printersInterface3.setName("USB");
		printersInterface3.setGlobalPrintersInterfaceId(getPrintersInterface(em, printersInterface3.getName(), globalLocationId));
		try
		{
			printersInterface3.setId(new StoreForwardUtility().generateDynamicIntId(em, printersInterface3.getLocationsId(), httpRequest, "printers_interface"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(printersInterface3);

		Printer printers = new Printer();
		printers.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printers.setCreatedBy(userId);
		printers.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printers.setUpdatedBy(userId);
		printers.setLocationsId(locationId);
		printers.setStatus("F");
		printers.setDisplayName("No Printer");
		printers.setDisplaySequence(1);
		printers.setPrintersModelId(0);
		printers.setPrintersTypeId("");
		printers.setPrintersInterfaceId("");
		printers.setIpAddress("0");
		printers.setPort(0);
		printers.setPrintersName("No Printer");
		printers.setIsActive(0);
		printers.setGlobalPrinterId(getPrinters(em, printers.getPrintersName(), globalLocationId));
		try
		{
			printers.setId(new StoreForwardUtility().generateDynamicIntId(em, printers.getLocationsId(), httpRequest, "printers"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(printers);

		PrintersType printersType5 = new PrintersType();
		printersType5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersType5.setCreatedBy(userId);
		printersType5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printersType5.setUpdatedBy(userId);
		printersType5.setLocationsId(locationId);
		printersType5.setStatus("F");
		printersType5.setName("Label Printer");
		printersType5.setGlobalPrintersTypeId(getPrintersType(em, printersType5.getName(), globalLocationId));
		try
		{
			printersType5.setId(new StoreForwardUtility().generateDynamicIntId(em, printersType5.getLocationsId(), httpRequest, "printers_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(printersType5);

	}

	/**
	 * Gets the unit of measurement by name and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the unit of measurement by name and location id
	 */
	private UnitOfMeasurement getUnitOfMeasurementByNameAndLocationId(EntityManager em, String locationId, String name)
	{
		UnitOfMeasurement measurement = null;
		try
		{
			String queryString = "select s from UnitOfMeasurement s where s.name = ? and s.locationId = ? and s.status != 'D'";
			TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class).setParameter(1, name).setParameter(2, locationId);
			measurement = query.getSingleResult();

		}
		catch (Exception e)
		{
			// todo shlok need to handle exception in below
			// handle exception
		}
		return measurement;
	}

	/**
	 * Delete all unit of measurement constant.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 */
	void deleteAllUnitOfMeasurementConstant(EntityManager em, String locationId)
	{

		Query query = em.createNativeQuery("delete from email_template");
		query.executeUpdate();

		query = em.createNativeQuery("delete from smtp_config");
		query.executeUpdate();

		query = em.createNativeQuery("delete from unit_of_measurement");
		query.executeUpdate();

		query = em.createNativeQuery("delete from unit_conversion");
		query.executeUpdate();
		 

		query = em.createNativeQuery("ALTER TABLE `unit_conversion`  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1 ");
		query.executeUpdate();

		query = em.createNativeQuery("delete from order_status where locations_id='"+locationId+"'");
		query.executeUpdate();

		query = em.createNativeQuery("delete from order_detail_status where locations_id='0' or locations_id is null ");
		query.executeUpdate();

	}

	/**
	 * Adds the unit of measurement constant.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param globalLocationId
	 *            the global location id
	 * @throws ParseException
	 *             the parse exception
	 */
	void addUnitOfMeasurementConstant(EntityManager em, String userId, String locationId, String globalLocationId, HttpServletRequest httpRequest) throws ParseException
	{

		// todo shlok need to handle exception in below
		// modulise code
		UnitOfMeasurement unitOfMeasurement1 = new UnitOfMeasurement();
		unitOfMeasurement1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement1.setCreatedBy(userId);
		unitOfMeasurement1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement1.setUpdatedBy(userId);
		unitOfMeasurement1.setLocationId(locationId);
		unitOfMeasurement1.setStatus("F");
		unitOfMeasurement1.setDisplayName("ML");
		unitOfMeasurement1.setName("ML");
		unitOfMeasurement1.setDisplaySequence(1 + "");
		unitOfMeasurement1.setUomTypeId(0);
		unitOfMeasurement1.setStockUomId(null);
		unitOfMeasurement1.setSellableQty(new BigDecimal(0));
		unitOfMeasurement1.setStockQty(new BigDecimal(0));
		UnitOfMeasurement globalUOM = getUnitOfMeasurementByNameAndLocationId(em, globalLocationId, unitOfMeasurement1.getName());
		if (globalUOM != null)
		{
			unitOfMeasurement1.setGlobalId(globalUOM.getId());
		}
		try
		{
			if (unitOfMeasurement1.getId() == null)
				unitOfMeasurement1.setId(new StoreForwardUtility().generateDynamicIntId(em, unitOfMeasurement1.getLocationId(), httpRequest, "unit_of_measurement"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(unitOfMeasurement1);

		UnitConversion conversion1 = new UnitConversion();
		conversion1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion1.setCreatedBy(userId);
		conversion1.setFromUOMId(unitOfMeasurement1.getId());
		conversion1.setToUOMId(unitOfMeasurement1.getId());
		conversion1.setStatus("A");
		conversion1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion1.setUpdatedBy(userId);
		conversion1.setConversionRatio(new BigDecimal(1));
		em.persist(conversion1);

		UnitOfMeasurement unitOfMeasurement2 = new UnitOfMeasurement();
		unitOfMeasurement2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement2.setCreatedBy(userId);
		unitOfMeasurement2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement2.setUpdatedBy(userId);
		unitOfMeasurement2.setLocationId(locationId);
		unitOfMeasurement2.setStatus("F");
		unitOfMeasurement2.setDisplayName("GRAM");
		unitOfMeasurement2.setName("GRAM");
		unitOfMeasurement2.setDisplaySequence(2 + "");
		unitOfMeasurement2.setUomTypeId(0);
		unitOfMeasurement2.setStockUomId(null);
		unitOfMeasurement2.setSellableQty(new BigDecimal(0));
		unitOfMeasurement2.setStockQty(new BigDecimal(0));
		globalUOM = getUnitOfMeasurementByNameAndLocationId(em, globalLocationId, unitOfMeasurement2.getName());
		if (globalUOM != null)
		{
			unitOfMeasurement2.setGlobalId(globalUOM.getId());
		}
		try
		{
			unitOfMeasurement2.setId(new StoreForwardUtility().generateDynamicIntId(em, unitOfMeasurement2.getLocationId(), httpRequest, "unit_of_measurement"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(unitOfMeasurement2);

		UnitConversion conversion2 = new UnitConversion();
		conversion2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion2.setCreatedBy(userId);
		conversion2.setFromUOMId(unitOfMeasurement2.getId());
		conversion2.setToUOMId(unitOfMeasurement2.getId());
		conversion2.setStatus("A");
		conversion2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion2.setUpdatedBy(userId);
		conversion2.setConversionRatio(new BigDecimal(1));
		em.persist(conversion2);

		UnitOfMeasurement unitOfMeasurement3 = new UnitOfMeasurement();
		unitOfMeasurement3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement3.setCreatedBy(userId);
		unitOfMeasurement3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement3.setUpdatedBy(userId);
		unitOfMeasurement3.setLocationId(locationId);
		unitOfMeasurement3.setStatus("F");
		unitOfMeasurement3.setDisplayName("UNIT");
		unitOfMeasurement3.setName("UNIT");
		unitOfMeasurement3.setDisplaySequence(3 + "");
		unitOfMeasurement3.setUomTypeId(0);
		unitOfMeasurement3.setStockUomId(null);
		unitOfMeasurement3.setSellableQty(new BigDecimal(0));
		unitOfMeasurement3.setStockQty(new BigDecimal(0));
		globalUOM = getUnitOfMeasurementByNameAndLocationId(em, globalLocationId, unitOfMeasurement3.getName());
		if (globalUOM != null)
		{
			unitOfMeasurement3.setGlobalId(globalUOM.getId());
		}
		try
		{
			unitOfMeasurement3.setId(new StoreForwardUtility().generateDynamicIntId(em, unitOfMeasurement3.getLocationId(), httpRequest, "unit_of_measurement"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(unitOfMeasurement3);

		UnitConversion conversion3 = new UnitConversion();
		conversion3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion3.setCreatedBy(userId);
		conversion3.setFromUOMId(unitOfMeasurement3.getId());
		conversion3.setToUOMId(unitOfMeasurement3.getId());
		conversion3.setStatus("A");
		conversion3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion3.setUpdatedBy(userId);
		conversion3.setConversionRatio(new BigDecimal(1));
		em.persist(conversion3);

		UnitOfMeasurement unitOfMeasurement4 = new UnitOfMeasurement();
		unitOfMeasurement4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement4.setCreatedBy(userId);
		unitOfMeasurement4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement4.setUpdatedBy(userId);
		unitOfMeasurement4.setLocationId(locationId);
		unitOfMeasurement4.setStatus("F");
		unitOfMeasurement4.setDisplayName("KG");
		unitOfMeasurement4.setName("KG");
		unitOfMeasurement4.setDisplaySequence(4 + "");
		unitOfMeasurement4.setUomTypeId(0);
		unitOfMeasurement4.setStockUomId(null);
		unitOfMeasurement4.setSellableQty(new BigDecimal(0));
		unitOfMeasurement4.setStockQty(new BigDecimal(0));
		globalUOM = getUnitOfMeasurementByNameAndLocationId(em, globalLocationId, unitOfMeasurement4.getName());
		if (globalUOM != null)
		{
			unitOfMeasurement4.setGlobalId(globalUOM.getId());
		}
		try
		{
			unitOfMeasurement4.setId(new StoreForwardUtility().generateDynamicIntId(em, unitOfMeasurement4.getLocationId(), httpRequest, "unit_of_measurement"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(unitOfMeasurement4);

		UnitConversion conversion4 = new UnitConversion();
		conversion4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion4.setCreatedBy(userId);
		conversion4.setFromUOMId(unitOfMeasurement4.getId());
		conversion4.setToUOMId(unitOfMeasurement4.getId());
		conversion4.setStatus("A");
		conversion4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion4.setUpdatedBy(userId);
		conversion4.setConversionRatio(new BigDecimal(1));
		em.persist(conversion4);

		UnitOfMeasurement unitOfMeasurement5 = new UnitOfMeasurement();
		unitOfMeasurement5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement5.setCreatedBy(userId);
		unitOfMeasurement5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement5.setUpdatedBy(userId);
		unitOfMeasurement5.setLocationId(locationId);
		unitOfMeasurement5.setStatus("F");
		unitOfMeasurement5.setDisplayName("POUNDS");
		unitOfMeasurement5.setName("POUNDS");
		unitOfMeasurement5.setDisplaySequence(5 + "");
		unitOfMeasurement5.setUomTypeId(0);
		unitOfMeasurement5.setStockUomId(null);
		unitOfMeasurement5.setSellableQty(new BigDecimal(0));
		unitOfMeasurement5.setStockQty(new BigDecimal(0));
		globalUOM = getUnitOfMeasurementByNameAndLocationId(em, globalLocationId, unitOfMeasurement5.getName());
		if (globalUOM != null)
		{
			unitOfMeasurement5.setGlobalId(globalUOM.getId());
		}
		try
		{
			unitOfMeasurement5.setId(new StoreForwardUtility().generateDynamicIntId(em, unitOfMeasurement5.getLocationId(), httpRequest, "unit_of_measurement"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(unitOfMeasurement5);

		UnitConversion conversion5 = new UnitConversion();
		conversion5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion5.setCreatedBy(userId);
		conversion5.setFromUOMId(unitOfMeasurement5.getId());
		conversion5.setToUOMId(unitOfMeasurement5.getId());
		conversion5.setStatus("A");
		conversion5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion5.setUpdatedBy(userId);
		conversion5.setConversionRatio(new BigDecimal(1));
		em.persist(conversion5);

		UnitOfMeasurement unitOfMeasurement6 = new UnitOfMeasurement();
		unitOfMeasurement6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement6.setCreatedBy(userId);
		unitOfMeasurement6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement6.setUpdatedBy(userId);
		unitOfMeasurement6.setLocationId(locationId);
		unitOfMeasurement6.setStatus("F");
		unitOfMeasurement6.setDisplayName("STONES");
		unitOfMeasurement6.setName("STONES");
		unitOfMeasurement6.setDisplaySequence(6 + "");
		unitOfMeasurement6.setUomTypeId(0);
		unitOfMeasurement6.setStockUomId(null);
		unitOfMeasurement6.setSellableQty(new BigDecimal(0));
		unitOfMeasurement6.setStockQty(new BigDecimal(0));
		globalUOM = getUnitOfMeasurementByNameAndLocationId(em, globalLocationId, unitOfMeasurement6.getName());
		if (globalUOM != null)
		{
			unitOfMeasurement6.setGlobalId(globalUOM.getId());
		}
		try
		{
			unitOfMeasurement6.setId(new StoreForwardUtility().generateDynamicIntId(em, unitOfMeasurement6.getLocationId(), httpRequest, "unit_of_measurement"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(unitOfMeasurement6);

		UnitConversion conversion6 = new UnitConversion();
		conversion6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion6.setCreatedBy(userId);
		conversion6.setFromUOMId(unitOfMeasurement6.getId());
		conversion6.setToUOMId(unitOfMeasurement6.getId());
		conversion6.setStatus("A");
		conversion6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion6.setUpdatedBy(userId);
		conversion6.setConversionRatio(new BigDecimal(1));
		em.persist(conversion6);

		UnitOfMeasurement unitOfMeasurement7 = new UnitOfMeasurement();
		unitOfMeasurement7.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement7.setCreatedBy(userId);
		unitOfMeasurement7.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement7.setUpdatedBy(userId);
		unitOfMeasurement7.setLocationId(locationId);
		unitOfMeasurement7.setStatus("F");
		unitOfMeasurement7.setDisplayName("OUNCES");
		unitOfMeasurement7.setName("OUNCES");
		unitOfMeasurement7.setDisplaySequence(7 + "");
		unitOfMeasurement7.setUomTypeId(0);
		unitOfMeasurement7.setStockUomId(null);
		unitOfMeasurement7.setSellableQty(new BigDecimal(0));
		unitOfMeasurement7.setStockQty(new BigDecimal(0));
		globalUOM = getUnitOfMeasurementByNameAndLocationId(em, globalLocationId, unitOfMeasurement7.getName());
		if (globalUOM != null)
		{
			unitOfMeasurement7.setGlobalId(globalUOM.getId());
		}
		try
		{
			unitOfMeasurement7.setId(new StoreForwardUtility().generateDynamicIntId(em, unitOfMeasurement7.getLocationId(), httpRequest, "unit_of_measurement"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(unitOfMeasurement7);

		UnitConversion conversion7 = new UnitConversion();
		conversion7.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion7.setCreatedBy(userId);
		conversion7.setFromUOMId(unitOfMeasurement7.getId());
		conversion7.setToUOMId(unitOfMeasurement7.getId());
		conversion7.setStatus("A");
		conversion7.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion7.setUpdatedBy(userId);
		conversion7.setConversionRatio(new BigDecimal(1));
		em.persist(conversion7);

		UnitOfMeasurement unitOfMeasurement8 = new UnitOfMeasurement();
		unitOfMeasurement8.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement8.setCreatedBy(userId);
		unitOfMeasurement8.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement8.setUpdatedBy(userId);
		unitOfMeasurement8.setLocationId(locationId);
		unitOfMeasurement8.setStatus("F");
		unitOfMeasurement8.setDisplayName("GALLONS");
		unitOfMeasurement8.setName("GALLONS");
		unitOfMeasurement8.setDisplaySequence(8 + "");
		unitOfMeasurement8.setUomTypeId(0);
		unitOfMeasurement8.setStockUomId(null);
		unitOfMeasurement8.setSellableQty(new BigDecimal(0));
		unitOfMeasurement8.setStockQty(new BigDecimal(0));
		globalUOM = getUnitOfMeasurementByNameAndLocationId(em, globalLocationId, unitOfMeasurement8.getName());
		if (globalUOM != null)
		{
			unitOfMeasurement8.setGlobalId(globalUOM.getId());
		}
		try
		{
			unitOfMeasurement8.setId(new StoreForwardUtility().generateDynamicIntId(em, unitOfMeasurement8.getLocationId(), httpRequest, "unit_of_measurement"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(unitOfMeasurement8);

		UnitConversion conversion8 = new UnitConversion();
		conversion8.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion8.setCreatedBy(userId);
		conversion8.setFromUOMId(unitOfMeasurement8.getId());
		conversion8.setToUOMId(unitOfMeasurement8.getId());
		conversion8.setStatus("A");
		conversion8.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion8.setUpdatedBy(userId);
		conversion8.setConversionRatio(new BigDecimal(1));
		em.persist(conversion8);

		UnitOfMeasurement unitOfMeasurement9 = new UnitOfMeasurement();
		unitOfMeasurement9.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement9.setCreatedBy(userId);
		unitOfMeasurement9.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement9.setUpdatedBy(userId);
		unitOfMeasurement9.setLocationId(locationId);
		unitOfMeasurement9.setStatus("F");
		unitOfMeasurement9.setDisplayName("LITERS");
		unitOfMeasurement9.setName("LITERS");
		unitOfMeasurement9.setDisplaySequence(9 + "");
		unitOfMeasurement9.setUomTypeId(0);
		unitOfMeasurement9.setStockUomId(null);
		unitOfMeasurement9.setSellableQty(new BigDecimal(0));
		unitOfMeasurement9.setStockQty(new BigDecimal(0));
		globalUOM = getUnitOfMeasurementByNameAndLocationId(em, globalLocationId, unitOfMeasurement9.getName());
		if (globalUOM != null)
		{
			unitOfMeasurement9.setGlobalId(globalUOM.getId());
		}
		try
		{
			unitOfMeasurement9.setId(new StoreForwardUtility().generateDynamicIntId(em, unitOfMeasurement9.getLocationId(), httpRequest, "unit_of_measurement"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(unitOfMeasurement9);

		UnitConversion conversion9 = new UnitConversion();
		conversion9.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion9.setCreatedBy(userId);
		conversion9.setFromUOMId(unitOfMeasurement9.getId());
		conversion9.setToUOMId(unitOfMeasurement9.getId());
		conversion9.setStatus("A");
		conversion9.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion9.setUpdatedBy(userId);
		conversion9.setConversionRatio(new BigDecimal(1));
		em.persist(conversion9);

		// Adding respective conversion list

		UnitConversion conversion10 = new UnitConversion();
		conversion10.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion10.setCreatedBy(userId);
		conversion10.setFromUOMId(unitOfMeasurement1.getId());
		conversion10.setToUOMId(unitOfMeasurement9.getId());
		conversion10.setStatus("A");
		conversion10.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion10.setUpdatedBy(userId);
		conversion10.setConversionRatio(new BigDecimal(0.001));
		em.persist(conversion10);

		UnitConversion conversion11 = new UnitConversion();
		conversion11.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion11.setCreatedBy(userId);
		conversion11.setFromUOMId(unitOfMeasurement1.getId());
		conversion11.setToUOMId(unitOfMeasurement7.getId());
		conversion11.setStatus("A");
		conversion11.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion11.setUpdatedBy(userId);
		conversion11.setConversionRatio(new BigDecimal(0.030));
		em.persist(conversion11);

		UnitConversion conversion12 = new UnitConversion();
		conversion12.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion12.setCreatedBy(userId);
		conversion12.setFromUOMId(unitOfMeasurement2.getId());
		conversion12.setToUOMId(unitOfMeasurement4.getId());
		conversion12.setStatus("A");
		conversion12.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion12.setUpdatedBy(userId);
		conversion12.setConversionRatio(new BigDecimal(0.001));
		em.persist(conversion12);

		UnitConversion conversion13 = new UnitConversion();
		conversion13.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion13.setCreatedBy(userId);
		conversion13.setFromUOMId(unitOfMeasurement2.getId());
		conversion13.setToUOMId(unitOfMeasurement7.getId());
		conversion13.setStatus("A");
		conversion13.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion13.setUpdatedBy(userId);
		conversion13.setConversionRatio(new BigDecimal(0.040));
		em.persist(conversion13);

		UnitConversion conversion14 = new UnitConversion();
		conversion14.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion14.setCreatedBy(userId);
		conversion14.setFromUOMId(unitOfMeasurement4.getId());
		conversion14.setToUOMId(unitOfMeasurement5.getId());
		conversion14.setStatus("A");
		conversion14.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion14.setUpdatedBy(userId);
		conversion14.setConversionRatio(new BigDecimal(2.20));
		em.persist(conversion14);

		UnitConversion conversion15 = new UnitConversion();
		conversion15.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion15.setCreatedBy(userId);
		conversion15.setFromUOMId(unitOfMeasurement4.getId());
		conversion15.setToUOMId(unitOfMeasurement6.getId());
		conversion15.setStatus("A");
		conversion15.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion15.setUpdatedBy(userId);
		conversion15.setConversionRatio(new BigDecimal(0.16));
		em.persist(conversion15);

		UnitConversion conversion16 = new UnitConversion();
		conversion16.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion16.setCreatedBy(userId);
		conversion16.setFromUOMId(unitOfMeasurement4.getId());
		conversion16.setToUOMId(unitOfMeasurement2.getId());
		conversion16.setStatus("A");
		conversion16.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion16.setUpdatedBy(userId);
		conversion16.setConversionRatio(new BigDecimal(1000.00));
		em.persist(conversion16);

		UnitConversion conversion17 = new UnitConversion();
		conversion17.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion17.setCreatedBy(userId);
		conversion17.setFromUOMId(unitOfMeasurement5.getId());
		conversion17.setToUOMId(unitOfMeasurement4.getId());
		conversion17.setStatus("A");
		conversion17.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion17.setUpdatedBy(userId);
		conversion17.setConversionRatio(new BigDecimal(0.4535923));
		em.persist(conversion17);

		UnitConversion conversion18 = new UnitConversion();
		conversion18.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion18.setCreatedBy(userId);
		conversion18.setFromUOMId(unitOfMeasurement5.getId());
		conversion18.setToUOMId(unitOfMeasurement7.getId());
		conversion18.setStatus("A");
		conversion18.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion18.setUpdatedBy(userId);
		conversion18.setConversionRatio(new BigDecimal(16.00));
		em.persist(conversion18);

		UnitConversion conversion19 = new UnitConversion();
		conversion19.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion19.setCreatedBy(userId);
		conversion19.setFromUOMId(unitOfMeasurement6.getId());
		conversion19.setToUOMId(unitOfMeasurement4.getId());
		conversion19.setStatus("A");
		conversion19.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion19.setUpdatedBy(userId);
		conversion19.setConversionRatio(new BigDecimal(6.35));
		em.persist(conversion19);

		UnitConversion conversion20 = new UnitConversion();
		conversion20.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion20.setCreatedBy(userId);
		conversion20.setFromUOMId(unitOfMeasurement7.getId());
		conversion20.setToUOMId(unitOfMeasurement1.getId());
		conversion20.setStatus("A");
		conversion20.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion20.setUpdatedBy(userId);
		conversion20.setConversionRatio(new BigDecimal(29.57));
		em.persist(conversion20);

		UnitConversion conversion21 = new UnitConversion();
		conversion21.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion21.setCreatedBy(userId);
		conversion21.setFromUOMId(unitOfMeasurement7.getId());
		conversion21.setToUOMId(unitOfMeasurement9.getId());
		conversion21.setStatus("A");
		conversion21.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion21.setUpdatedBy(userId);
		conversion21.setConversionRatio(new BigDecimal(0.03));
		em.persist(conversion21);

		UnitConversion conversion22 = new UnitConversion();
		conversion22.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion22.setCreatedBy(userId);
		conversion22.setFromUOMId(unitOfMeasurement7.getId());
		conversion22.setToUOMId(unitOfMeasurement5.getId());
		conversion22.setStatus("A");
		conversion22.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion22.setUpdatedBy(userId);
		conversion22.setConversionRatio(new BigDecimal(0.06));
		em.persist(conversion22);

		UnitConversion conversion23 = new UnitConversion();
		conversion23.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion23.setCreatedBy(userId);
		conversion23.setFromUOMId(unitOfMeasurement7.getId());
		conversion23.setToUOMId(unitOfMeasurement2.getId());
		conversion23.setStatus("A");
		conversion23.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion23.setUpdatedBy(userId);
		conversion23.setConversionRatio(new BigDecimal(28.35));
		em.persist(conversion23);

		UnitConversion conversion24 = new UnitConversion();
		conversion24.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion24.setCreatedBy(userId);
		conversion24.setFromUOMId(unitOfMeasurement8.getId());
		conversion24.setToUOMId(unitOfMeasurement9.getId());
		conversion24.setStatus("A");
		conversion24.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion24.setUpdatedBy(userId);
		conversion24.setConversionRatio(new BigDecimal(3.79));
		em.persist(conversion24);

		UnitConversion conversion25 = new UnitConversion();
		conversion25.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion25.setCreatedBy(userId);
		conversion25.setFromUOMId(unitOfMeasurement9.getId());
		conversion25.setToUOMId(unitOfMeasurement8.getId());
		conversion25.setStatus("A");
		conversion25.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion25.setUpdatedBy(userId);
		conversion25.setConversionRatio(new BigDecimal(0.26));
		em.persist(conversion25);

		UnitConversion conversion26 = new UnitConversion();
		conversion26.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion26.setCreatedBy(userId);
		conversion26.setFromUOMId(unitOfMeasurement9.getId());
		conversion26.setToUOMId(unitOfMeasurement7.getId());
		conversion26.setStatus("A");
		conversion26.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion26.setUpdatedBy(userId);
		conversion26.setConversionRatio(new BigDecimal(33.81));
		em.persist(conversion26);

		UnitConversion conversion27 = new UnitConversion();
		conversion27.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion27.setCreatedBy(userId);
		conversion27.setFromUOMId(unitOfMeasurement9.getId());
		conversion27.setToUOMId(unitOfMeasurement1.getId());
		conversion27.setStatus("A");
		conversion27.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		conversion27.setUpdatedBy(userId);
		conversion27.setConversionRatio(new BigDecimal(1000.00));
		em.persist(conversion27);

	}

	/**
	 * Adds the discount type constant.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param globalLocationId
	 *            the global location id
	 * @throws ParseException
	 *             the parse exception
	 */
	void addDiscountTypeConstant(EntityManager em, String userId, String locationId, String globalLocationId) throws ParseException
	{
		// todo shlok need to handle exception in below
		// modulise code
		DiscountsType discountsType1 = new DiscountsType();
		discountsType1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discountsType1.setCreatedBy(userId);
		discountsType1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discountsType1.setUpdatedBy(userId);
		discountsType1.setLocationsId(locationId);
		discountsType1.setStatus("F");
		discountsType1.setDisplaySequence(1);
		discountsType1.setDisplayName("Amount Off");
		discountsType1.setDiscountsType("Amount Off");
		discountsType1.setGlobalDiscountTypeId(getDiscountType(em, discountsType1.getDiscountsType(), globalLocationId));
		discountsType1.setId(new StoreForwardUtility().generateUUID());
		em.persist(discountsType1);

		Discount customDiscount = new Discount();
		customDiscount.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		customDiscount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		customDiscount.setDiscountsTypeId(discountsType1.getId());
		customDiscount.setDisplayName("Custom Discount");
		customDiscount.setName("Custom Discount");
		customDiscount.setDiscountsValue(new BigDecimal(0.0));
		customDiscount.setIsGroup(0);
		customDiscount.setCreatedBy(userId);
		customDiscount.setUpdatedBy(userId);
		customDiscount.setLocationsId(locationId);
		customDiscount.setEffectiveStartDate(new Date(new TimezoneTime().getGMTTimeInMilis()));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateInString = "2099-12-31";
		Date date = sdf.parse(dateInString);
		customDiscount.setEffectiveEndDate(date);

		customDiscount.setShortDescription("Custom discount");
		customDiscount.setDescription("Custom discount");
		customDiscount.setIsFeatured(0);
		customDiscount.setIsActive(0);
		customDiscount.setStatus("F");
		customDiscount.setComments("");
		customDiscount.setDisplaySequence(0);
		customDiscount.setGlobalId(getDiscounts(em, customDiscount.getName(), globalLocationId));
		customDiscount.setId(new StoreForwardUtility().generateUUID());
		em.persist(customDiscount);

		DiscountsType discountsType2 = new DiscountsType();
		discountsType2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discountsType2.setCreatedBy(userId);
		discountsType2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discountsType2.setUpdatedBy(userId);
		discountsType2.setLocationsId(locationId);
		discountsType2.setStatus("F");
		discountsType2.setDisplaySequence(2);
		discountsType2.setDisplayName("Percentage Off");
		discountsType2.setDiscountsType("Percentage Off");
		discountsType2.setGlobalDiscountTypeId(getDiscountType(em, discountsType2.getDiscountsType(), globalLocationId));
		discountsType2.setId(new StoreForwardUtility().generateUUID());
		em.persist(discountsType2);

		Discount discounts = new Discount();
		discounts.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts.setCreatedBy(userId);
		discounts.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts.setUpdatedBy(userId);
		discounts.setLocationsId(locationId);
		discounts.setStatus("F");
		discounts.setDisplaySequence(1);
		discounts.setDisplayName("No Discount");
		discounts.setShortDescription("No Discount");
		discounts.setDescription("No Discount");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateInString = "2099-12-31";
		date = sdf.parse(dateInString);
		discounts.setEffectiveStartDate(new Date());
		discounts.setEffectiveEndDate(date);
		discounts.setIsFeatured(0);
		discounts.setIsActive(0);
		discounts.setDisplaySequence(1);
		discounts.setComments("");
		discounts.setName("No Discount");
		discounts.setIsGroup(1);
		discounts.setDiscountsValue(new BigDecimal(0));
		discounts.setDiscountsTypeId(discountsType2.getId());
		discounts.setGlobalId(getDiscounts(em, discounts.getName(), globalLocationId));
		discounts.setId(new StoreForwardUtility().generateUUID());
		em.persist(discounts);

		Discount discounts1 = new Discount();
		discounts1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts1.setCreatedBy(userId);
		discounts1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts1.setUpdatedBy(userId);
		discounts1.setLocationsId(locationId);
		discounts1.setStatus("F");
		discounts1.setDisplaySequence(2);
		discounts1.setDisplayName("Custom Discount");
		discounts1.setShortDescription("Custom Discount");
		discounts1.setDescription("Custom Discount");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateInString = "2099-12-31";
		date = sdf.parse(dateInString);
		discounts1.setEffectiveStartDate(new Date());
		discounts1.setEffectiveEndDate(date);
		discounts1.setIsFeatured(0);
		discounts1.setIsActive(0);
		discounts1.setDisplaySequence(1);
		discounts1.setComments("");
		discounts1.setName("Custom Discount");
		discounts1.setIsGroup(0);
		discounts1.setDiscountsValue(new BigDecimal(0));
		discounts1.setDiscountsTypeId(discountsType2.getId());
		discounts1.setGlobalId(getDiscounts(em, discounts1.getName(), globalLocationId));
		discounts1.setId(new StoreForwardUtility().generateUUID());
		em.persist(discounts1);
		
		Discount discounts2 = new Discount();
		discounts2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts2.setCreatedBy(userId);
		discounts2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts2.setUpdatedBy(userId);
		discounts2.setLocationsId(locationId);
		discounts2.setStatus("F");
		discounts2.setDisplaySequence(2);
		discounts2.setDisplayName("NC1");
		discounts2.setShortDescription("NC1");
		discounts2.setDescription("NC1");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateInString = "2099-12-31";
		date = sdf.parse(dateInString);
		discounts2.setEffectiveStartDate(new Date());
		discounts2.setEffectiveEndDate(date);
		discounts2.setIsFeatured(0);
		discounts2.setIsActive(0);
		discounts2.setDisplaySequence(1);
		discounts2.setComments("");
		discounts2.setName("NC1");
		discounts2.setIsGroup(0);
		discounts2.setDiscountsValue(new BigDecimal(0));
		discounts2.setDiscountsTypeId(discountsType2.getId());
		discounts2.setGlobalId(getDiscounts(em, discounts2.getName(), globalLocationId));
		discounts2.setId(new StoreForwardUtility().generateUUID());
		em.persist(discounts2);
		logger.severe("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!@@@@@@@@@@@@@@@@"+discounts2.toString());
		
		Discount discounts3 = new Discount();
		discounts3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts3.setCreatedBy(userId);
		discounts3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts3.setUpdatedBy(userId);
		discounts3.setLocationsId(locationId);
		discounts3.setStatus("F");
		discounts3.setDisplaySequence(2);
		discounts3.setDisplayName("NC2");
		discounts3.setShortDescription("NC2");
		discounts3.setDescription("NC1");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateInString = "2099-12-31";
		date = sdf.parse(dateInString);
		discounts3.setEffectiveStartDate(new Date());
		discounts3.setEffectiveEndDate(date);
		discounts3.setIsFeatured(0);
		discounts3.setIsActive(0);
		discounts3.setDisplaySequence(1);
		discounts3.setComments("");
		discounts3.setName("NC2");
		discounts3.setIsGroup(0);
		discounts3.setDiscountsValue(new BigDecimal(0));
		discounts3.setDiscountsTypeId(discountsType2.getId());
		discounts3.setGlobalId(getDiscounts(em, discounts3.getName(), globalLocationId));
		discounts3.setId(new StoreForwardUtility().generateUUID());
		em.persist(discounts3);
		
		Discount discounts4 = new Discount();
		discounts4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts4.setCreatedBy(userId);
		discounts4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts4.setUpdatedBy(userId);
		discounts4.setLocationsId(locationId);
		discounts4.setStatus("F");
		discounts4.setDisplaySequence(2);
		discounts4.setDisplayName("NC3");
		discounts4.setShortDescription("NC3");
		discounts4.setDescription("NC3");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateInString = "2099-12-31";
		date = sdf.parse(dateInString);
		discounts4.setEffectiveStartDate(new Date());
		discounts4.setEffectiveEndDate(date);
		discounts4.setIsFeatured(0);
		discounts4.setIsActive(0);
		discounts4.setDisplaySequence(1);
		discounts4.setComments("");
		discounts4.setName("NC3");
		discounts4.setIsGroup(0);
		discounts4.setDiscountsValue(new BigDecimal(0));
		discounts4.setDiscountsTypeId(discountsType2.getId());
		discounts4.setGlobalId(getDiscounts(em, discounts4.getName(), globalLocationId));
		discounts4.setId(new StoreForwardUtility().generateUUID());
		em.persist(discounts4);
		
		Discount discounts5 = new Discount();
		discounts5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts5.setCreatedBy(userId);
		discounts5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts5.setUpdatedBy(userId);
		discounts5.setLocationsId(locationId);
		discounts5.setStatus("F");
		discounts5.setDisplaySequence(2);
		discounts5.setDisplayName("NC4");
		discounts5.setShortDescription("NC4");
		discounts5.setDescription("NC4");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateInString = "2099-12-31";
		date = sdf.parse(dateInString);
		discounts5.setEffectiveStartDate(new Date());
		discounts5.setEffectiveEndDate(date);
		discounts5.setIsFeatured(0);
		discounts5.setIsActive(0);
		discounts5.setDisplaySequence(1);
		discounts5.setComments("");
		discounts5.setName("NC4");
		discounts5.setIsGroup(0);
		discounts5.setDiscountsValue(new BigDecimal(0));
		discounts5.setDiscountsTypeId(discountsType2.getId());
		discounts5.setGlobalId(getDiscounts(em, discounts5.getName(), globalLocationId));
		discounts5.setId(new StoreForwardUtility().generateUUID());
		em.persist(discounts5);
		
		Discount discounts6 = new Discount();
		discounts6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts6.setCreatedBy(userId);
		discounts6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discounts6.setUpdatedBy(userId);
		discounts6.setLocationsId(locationId);
		discounts6.setStatus("F");
		discounts6.setDisplaySequence(2);
		discounts6.setDisplayName("NC5");
		discounts6.setShortDescription("NC5");
		discounts6.setDescription("NC5");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateInString = "2099-12-31";
		date = sdf.parse(dateInString);
		discounts6.setEffectiveStartDate(new Date());
		discounts6.setEffectiveEndDate(date);
		discounts6.setIsFeatured(0);
		discounts6.setIsActive(0);
		discounts6.setDisplaySequence(1);
		discounts6.setComments("");
		discounts6.setName("NC5");
		discounts6.setIsGroup(0);
		discounts6.setDiscountsValue(new BigDecimal(0));
		discounts6.setDiscountsTypeId(discountsType2.getId());
		discounts6.setGlobalId(getDiscounts(em, discounts6.getName(), globalLocationId));
		discounts6.setId(new StoreForwardUtility().generateUUID());
		em.persist(discounts6);

	}

	/**
	 * Gets the discount type.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param globalLocationId
	 *            the global location id
	 * @return the discount type
	 */
	String getDiscountType(EntityManager em, String name, String globalLocationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<DiscountsType> criteria = builder.createQuery(DiscountsType.class);
		Root<DiscountsType> r = criteria.from(DiscountsType.class);
		TypedQuery<DiscountsType> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(DiscountsType_.locationsId), globalLocationId), builder.equal(r.get(DiscountsType_.discountsType), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the discounts.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param globalLocationId
	 *            the global location id
	 * @return the discounts
	 */
	String getDiscounts(EntityManager em, String name, String globalLocationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
		Root<Discount> r = criteria.from(Discount.class);
		TypedQuery<Discount> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Discount_.locationsId), globalLocationId), builder.equal(r.get(Discount_.name), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return null;
	}

	/**
	 * Adds the payment method type constant.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 */
	void addPaymentMethodTypeConstant(EntityManager em, String userId, String locationId, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code
		PaymentType cashPaymentType = getPaymentType(em, "cash");
		PaymentMethodType paymentMethodType1 = null;
		paymentMethodType1 = new PaymentMethodType();
		paymentMethodType1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType1.setCreatedBy(userId);
		paymentMethodType1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType1.setUpdatedBy(userId);
		paymentMethodType1.setLocationsId(locationId);
		paymentMethodType1.setStatus("F");
		paymentMethodType1.setDisplaySequence(1);
		paymentMethodType1.setDisplayName("Cash");
		paymentMethodType1.setName("Cash");
		paymentMethodType1.setPaymentTypeId(cashPaymentType.getId());
		paymentMethodType1.setId(new StoreForwardUtility().generateUUID());
		em.persist(paymentMethodType1);
			
		 

		PaymentMethod paymentMethod1 = new PaymentMethod();
		paymentMethod1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod1.setCreatedBy(userId);
		paymentMethod1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod1.setUpdatedBy(userId);
		paymentMethod1.setLocationsId(locationId);
		paymentMethod1.setStatus("F");
		paymentMethod1.setDisplaySequence(1);
		paymentMethod1.setDisplayName("Cash");
		paymentMethod1.setName("Cash");
		paymentMethod1.setDescription("Cash");
		paymentMethod1.setPaymentMethodTypeId(paymentMethodType1.getId());
		paymentMethod1.setId(new StoreForwardUtility().generateUUID());
		 
		em.persist(paymentMethod1);

		PaymentType creditCardPaymentType = getPaymentType(em, "credit card");
		PaymentMethodType paymentMethodType2 = null;
		paymentMethodType2 = new PaymentMethodType();
		paymentMethodType2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType2.setCreatedBy(userId);
		paymentMethodType2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType2.setUpdatedBy(userId);
		paymentMethodType2.setLocationsId(locationId);
		paymentMethodType2.setStatus("F");
		paymentMethodType2.setDisplaySequence(2);
		paymentMethodType2.setDisplayName("Credit Card");
		paymentMethodType2.setName("Credit Card");
		paymentMethodType2.setPaymentTypeId(creditCardPaymentType.getId());
		try
		{
			paymentMethodType2.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethodType2.getLocationsId(), httpRequest, "payment_method_type"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethodType2);

		PaymentMethod paymentMethod2 = new PaymentMethod();
		paymentMethod2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod2.setCreatedBy(userId);
		paymentMethod2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod2.setUpdatedBy(userId);
		paymentMethod2.setLocationsId(locationId);
		paymentMethod2.setStatus("F");
		paymentMethod2.setDisplaySequence(1);
		paymentMethod2.setDisplayName("Visa");
		paymentMethod2.setName("Visa");
		paymentMethod2.setDescription("Visa");
		paymentMethod2.setPaymentMethodTypeId(paymentMethodType2.getId());
		try
		{
			paymentMethod2.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod2.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethod2);

		PaymentMethod paymentMethod3 = new PaymentMethod();
		paymentMethod3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod3.setCreatedBy(userId);
		paymentMethod3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod3.setUpdatedBy(userId);
		paymentMethod3.setLocationsId(locationId);
		paymentMethod3.setStatus("F");
		paymentMethod3.setDisplaySequence(2);
		paymentMethod3.setDisplayName("Masters Card");
		paymentMethod3.setName("Masters Card");
		paymentMethod3.setDescription("Masters Card");
		paymentMethod3.setPaymentMethodTypeId(paymentMethodType2.getId());
		try
		{
			paymentMethod3.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod3.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethod3);

		PaymentMethod paymentMethod4 = new PaymentMethod();
		paymentMethod4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod4.setCreatedBy(userId);
		paymentMethod4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod4.setUpdatedBy(userId);
		paymentMethod4.setLocationsId(locationId);
		paymentMethod4.setStatus("F");
		paymentMethod4.setDisplaySequence(3);
		paymentMethod4.setDisplayName("American Express");
		paymentMethod4.setName("American Express");
		paymentMethod4.setDescription("American Express");
		paymentMethod4.setPaymentMethodTypeId(paymentMethodType2.getId());
		try
		{
			paymentMethod4.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod4.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethod4);

		PaymentMethod paymentMethod5 = new PaymentMethod();
		paymentMethod5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod5.setCreatedBy(userId);
		paymentMethod5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod5.setUpdatedBy(userId);
		paymentMethod5.setLocationsId(locationId);
		paymentMethod5.setStatus("F");
		paymentMethod5.setDisplaySequence(4);
		paymentMethod5.setDisplayName("Discover");
		paymentMethod5.setName("Discover");
		paymentMethod5.setDescription("Discover");
		paymentMethod5.setPaymentMethodTypeId(paymentMethodType2.getId());
		try
		{
			paymentMethod5.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod5.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethod5);

		PaymentMethod paymentMethod6 = new PaymentMethod();
		paymentMethod6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod6.setCreatedBy(userId);
		paymentMethod6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod6.setUpdatedBy(userId);
		paymentMethod6.setLocationsId(locationId);
		paymentMethod6.setStatus("F");
		paymentMethod6.setDisplaySequence(5);
		paymentMethod6.setDisplayName("Other");
		paymentMethod6.setName("Other");
		paymentMethod6.setDescription("Other");
		paymentMethod6.setPaymentMethodTypeId(paymentMethodType2.getId());
		try
		{
			paymentMethod6.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod6.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethod6);
		PaymentType manualCreditCard = getPaymentType(em, "Manual Credit Card");
		PaymentMethodType paymentMethodType3 = null;
		paymentMethodType3 = new PaymentMethodType();
		paymentMethodType3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType3.setCreatedBy(userId);
		paymentMethodType3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType3.setUpdatedBy(userId);
		paymentMethodType3.setLocationsId(locationId);
		paymentMethodType3.setStatus("F");
		paymentMethodType3.setDisplaySequence(3);
		paymentMethodType3.setDisplayName("Manual Credit Card");
		paymentMethodType3.setName("Manual Credit Card");
		paymentMethodType3.setPaymentTypeId(manualCreditCard.getId());
		try
		{
			paymentMethodType3.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethodType3.getLocationsId(), httpRequest, "payment_method_type"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethodType3);

		PaymentMethod paymentMethod31 = new PaymentMethod();
		paymentMethod31.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod31.setCreatedBy(userId);
		paymentMethod31.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod31.setUpdatedBy(userId);
		paymentMethod31.setLocationsId(locationId);
		paymentMethod31.setStatus("F");
		paymentMethod31.setDisplaySequence(1);
		paymentMethod31.setDisplayName("Visa");
		paymentMethod31.setName("Visa");
		paymentMethod31.setDescription("Visa");
		paymentMethod31.setPaymentMethodTypeId(paymentMethodType3.getId());
		try
		{
			paymentMethod31.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod31.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethod31);

		PaymentMethod paymentMethod32 = new PaymentMethod();
		paymentMethod32.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod32.setCreatedBy(userId);
		paymentMethod32.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod32.setUpdatedBy(userId);
		paymentMethod32.setLocationsId(locationId);
		paymentMethod32.setStatus("F");
		paymentMethod32.setDisplaySequence(2);
		paymentMethod32.setDisplayName("Masters Card");
		paymentMethod32.setName("Masters Card");
		paymentMethod32.setDescription("Masters Card");
		paymentMethod32.setPaymentMethodTypeId(paymentMethodType3.getId());
		try
		{
			paymentMethod32.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod32.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethod32);

		PaymentMethod paymentMethod33 = new PaymentMethod();
		paymentMethod33.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod33.setCreatedBy(userId);
		paymentMethod33.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod33.setUpdatedBy(userId);
		paymentMethod33.setLocationsId(locationId);
		paymentMethod33.setStatus("F");
		paymentMethod33.setDisplaySequence(3);
		paymentMethod33.setDisplayName("American Express");
		paymentMethod33.setName("American Express");
		paymentMethod33.setDescription("American Express");
		paymentMethod33.setPaymentMethodTypeId(paymentMethodType3.getId());
		try
		{
			paymentMethod33.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod33.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethod33);

		PaymentMethod paymentMethod34 = new PaymentMethod();
		paymentMethod34.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod34.setCreatedBy(userId);
		paymentMethod34.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod34.setUpdatedBy(userId);
		paymentMethod34.setLocationsId(locationId);
		paymentMethod34.setStatus("F");
		paymentMethod34.setDisplaySequence(4);
		paymentMethod34.setDisplayName("Discover");
		paymentMethod34.setName("Discover");
		paymentMethod34.setDescription("Discover");
		paymentMethod34.setPaymentMethodTypeId(paymentMethodType3.getId());
		try
		{
			paymentMethod34.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod34.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		em.persist(paymentMethod34);

		PaymentMethod paymentMethod35 = new PaymentMethod();
		paymentMethod35.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod35.setCreatedBy(userId);
		paymentMethod35.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod35.setUpdatedBy(userId);
		paymentMethod35.setLocationsId(locationId);
		paymentMethod35.setStatus("F");
		paymentMethod35.setDisplaySequence(5);
		paymentMethod35.setDisplayName("Other");
		paymentMethod35.setName("Other");
		paymentMethod35.setDescription("Other");
		paymentMethod35.setPaymentMethodTypeId(paymentMethodType3.getId());
		try
		{
			paymentMethod35.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod35.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethod35);

		PaymentType manualCCEntry = getPaymentType(em, "Manual CC Entry");
		PaymentMethodType paymentMethodType4 = null;
		paymentMethodType4 = new PaymentMethodType();
		paymentMethodType4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType4.setCreatedBy(userId);
		paymentMethodType4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType4.setUpdatedBy(userId);
		paymentMethodType4.setLocationsId(locationId);
		paymentMethodType4.setStatus("F");
		paymentMethodType4.setDisplaySequence(3);
		paymentMethodType4.setDisplayName("Manual CC Entry");
		paymentMethodType4.setName("Manual CC Entry");
		paymentMethodType4.setPaymentTypeId(manualCCEntry.getId());
		try
		{
			paymentMethodType4.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethodType4.getLocationsId(), httpRequest, "payment_method_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethodType4);

		PaymentType creditTerm = getPaymentType(em, "Credit Term");
		PaymentMethodType paymentMethodType5 = null;
		paymentMethodType5 = new PaymentMethodType();
		paymentMethodType5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType5.setCreatedBy(userId);
		paymentMethodType5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType5.setUpdatedBy(userId);
		paymentMethodType5.setLocationsId(locationId);
		paymentMethodType5.setStatus("F");
		paymentMethodType5.setDisplaySequence(3);
		paymentMethodType5.setDisplayName("Credit Term");
		paymentMethodType5.setName("Credit Term");
		paymentMethodType5.setPaymentTypeId(creditTerm.getId());
		try
		{
			paymentMethodType5.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethodType5.getLocationsId(), httpRequest, "payment_method_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethodType5);

		PaymentMethod paymentMethod51 = new PaymentMethod();
		paymentMethod51.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod51.setCreatedBy(userId);
		paymentMethod51.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod51.setUpdatedBy(userId);
		paymentMethod51.setLocationsId(locationId);
		paymentMethod51.setStatus("F");
		paymentMethod51.setDisplaySequence(1);
		paymentMethod51.setDisplayName("Credit Term");
		paymentMethod51.setName("Credit Term");
		paymentMethod51.setDescription("Credit Term");
		paymentMethod51.setPaymentMethodTypeId(paymentMethodType5.getId());
		try
		{
			paymentMethod51.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod51.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethod51);

		PaymentMethodType paymentMethodType6 = null;
		paymentMethodType6 = new PaymentMethodType();
		paymentMethodType6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType6.setCreatedBy(userId);
		paymentMethodType6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType6.setUpdatedBy(userId);
		paymentMethodType6.setLocationsId(locationId);
		paymentMethodType6.setStatus("I");
		paymentMethodType6.setDisplaySequence(3);
		paymentMethodType6.setDisplayName("Void Order");
		paymentMethodType6.setName("void order");
		paymentMethodType6.setPaymentTypeId(cashPaymentType.getId());
		try
		{
			paymentMethodType6.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethodType6.getLocationsId(), httpRequest, "payment_method_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethodType6);

		PaymentMethod paymentMethod61 = new PaymentMethod();
		paymentMethod61.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod61.setCreatedBy(userId);
		paymentMethod61.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod61.setUpdatedBy(userId);
		paymentMethod61.setLocationsId(locationId);
		paymentMethod61.setStatus("F");
		paymentMethod61.setDisplaySequence(1);
		paymentMethod61.setDisplayName("Void Order");
		paymentMethod61.setName("void order");
		paymentMethod61.setDescription("Void Order");
		paymentMethod61.setPaymentMethodTypeId(paymentMethodType6.getId());
		try
		{
			paymentMethod61.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod61.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethod61);

		PaymentMethodType paymentMethodType7 = null;
		paymentMethodType7 = new PaymentMethodType();
		paymentMethodType7.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType7.setCreatedBy(userId);
		paymentMethodType7.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType7.setUpdatedBy(userId);
		paymentMethodType7.setLocationsId(locationId);
		paymentMethodType7.setStatus("I");
		paymentMethodType7.setDisplaySequence(3);
		paymentMethodType7.setDisplayName("Table Ready");
		paymentMethodType7.setName("table ready");
		paymentMethodType7.setPaymentTypeId(cashPaymentType.getId());
		try
		{
			paymentMethodType7.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethodType7.getLocationsId(), httpRequest, "payment_method_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethodType7);

		PaymentMethod paymentMethod71 = new PaymentMethod();
		paymentMethod71.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod71.setCreatedBy(userId);
		paymentMethod71.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod71.setUpdatedBy(userId);
		paymentMethod71.setLocationsId(locationId);
		paymentMethod71.setStatus("F");
		paymentMethod71.setDisplaySequence(1);
		paymentMethod71.setDisplayName("Table Ready");
		paymentMethod71.setName("table ready");
		paymentMethod71.setDescription("table ready");
		paymentMethod71.setPaymentMethodTypeId(paymentMethodType7.getId());
		try
		{
			paymentMethod71.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod71.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethod71);

		PaymentMethodType paymentMethodType8 = null;
		paymentMethodType8 = new PaymentMethodType();
		paymentMethodType8.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType8.setCreatedBy(userId);
		paymentMethodType8.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType8.setUpdatedBy(userId);
		paymentMethodType8.setLocationsId(locationId);
		paymentMethodType8.setStatus("I");
		paymentMethodType8.setDisplaySequence(3);
		paymentMethodType8.setDisplayName("Discount");
		paymentMethodType8.setName("discount");
		paymentMethodType8.setPaymentTypeId(cashPaymentType.getId());
		try
		{
			paymentMethodType8.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethodType8.getLocationsId(), httpRequest, "payment_method_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethodType8);

		PaymentMethod paymentMethod81 = new PaymentMethod();
		paymentMethod81.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod81.setCreatedBy(userId);
		paymentMethod81.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod81.setUpdatedBy(userId);
		paymentMethod81.setLocationsId(locationId);
		paymentMethod81.setStatus("F");
		paymentMethod81.setDisplaySequence(1);
		paymentMethod81.setDisplayName("Discount");
		paymentMethod81.setName("discount");
		paymentMethod81.setDescription("discount");
		paymentMethod81.setPaymentMethodTypeId(paymentMethodType8.getId());
		try
		{
			paymentMethod81.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod81.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethod81);

		PaymentMethodType paymentMethodType62 = null;
		paymentMethodType62 = new PaymentMethodType();
		paymentMethodType62.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType62.setCreatedBy(userId);
		paymentMethodType62.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType62.setUpdatedBy(userId);
		paymentMethodType62.setLocationsId(locationId);
		paymentMethodType62.setStatus("I");
		paymentMethodType62.setDisplaySequence(3);
		paymentMethodType62.setDisplayName("Cancel Order");
		paymentMethodType62.setName("Cancel Order");
		paymentMethodType62.setPaymentTypeId(cashPaymentType.getId());
		try
		{
			paymentMethodType62.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethodType62.getLocationsId(), httpRequest, "payment_method_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethodType62);

		PaymentMethod paymentMethod82 = new PaymentMethod();
		paymentMethod82.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod82.setCreatedBy(userId);
		paymentMethod82.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod82.setUpdatedBy(userId);
		paymentMethod82.setLocationsId(locationId);
		paymentMethod82.setStatus("F");
		paymentMethod82.setDisplaySequence(1);
		paymentMethod82.setDisplayName("Cancel Order");
		paymentMethod82.setName("Cancel Order");
		paymentMethod82.setDescription("Cancel Order");
		paymentMethod82.setPaymentMethodTypeId(paymentMethodType62.getId());
		try
		{
			paymentMethod82.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod82.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethod82);

		PaymentMethodType paymentMethodType9 = null;
		paymentMethodType9 = new PaymentMethodType();
		paymentMethodType9.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType9.setCreatedBy(userId);
		paymentMethodType9.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType9.setUpdatedBy(userId);
		paymentMethodType9.setLocationsId(locationId);
		paymentMethodType9.setStatus("F");
		paymentMethodType9.setDisplaySequence(3);
		paymentMethodType9.setDisplayName("PreAuth Manual CC Entry");
		paymentMethodType9.setName("PreAuth Manual CC Entry");
		paymentMethodType9.setPaymentTypeId(manualCCEntry.getId());
		try
		{
			paymentMethodType9.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethodType9.getLocationsId(), httpRequest, "payment_method_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethodType9);

		PaymentMethod paymentMethod83 = new PaymentMethod();
		paymentMethod83.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod83.setCreatedBy(userId);
		paymentMethod83.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod83.setUpdatedBy(userId);
		paymentMethod83.setLocationsId(locationId);
		paymentMethod83.setStatus("F");
		paymentMethod83.setDisplaySequence(1);
		paymentMethod83.setDisplayName("Other");
		paymentMethod83.setName("Other");
		paymentMethod83.setDescription("Other");
		paymentMethod83.setPaymentMethodTypeId(paymentMethodType9.getId());
		try
		{
			paymentMethod83.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod83.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethod83);

		PaymentMethodType paymentMethodType10 = null;
		paymentMethodType10 = new PaymentMethodType();
		paymentMethodType10.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType10.setCreatedBy(userId);
		paymentMethodType10.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType10.setUpdatedBy(userId);
		paymentMethodType10.setLocationsId(locationId);
		paymentMethodType10.setStatus("F");
		paymentMethodType10.setDisplaySequence(3);
		paymentMethodType10.setDisplayName("PreAuth Credit Card");
		paymentMethodType10.setName("PreAuth Credit Card");
		paymentMethodType10.setPaymentTypeId(creditCardPaymentType.getId());
		try
		{
			paymentMethodType10.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethodType10.getLocationsId(), httpRequest, "payment_method_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethodType10);

		PaymentMethod paymentMethod84 = new PaymentMethod();
		paymentMethod84.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod84.setCreatedBy(userId);
		paymentMethod84.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod84.setUpdatedBy(userId);
		paymentMethod84.setLocationsId(locationId);
		paymentMethod84.setStatus("F");
		paymentMethod84.setDisplaySequence(1);
		paymentMethod84.setDisplayName("Other");
		paymentMethod84.setName("Other");
		paymentMethod84.setDescription("Other");
		paymentMethod84.setPaymentMethodTypeId(paymentMethodType10.getId());
		try
		{
			paymentMethod84.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentMethod84.getLocationsId(), httpRequest, "payment_method"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentMethod84);
	}

	/**
	 * Adds the reservation type constant.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 */
	void addReservationTypeConstant(EntityManager em, String userId, String locationId, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code
		ReservationsType resType1 = new ReservationsType();
		resType1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		resType1.setCreatedBy(userId);
		resType1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		resType1.setUpdatedBy(userId);
		resType1.setLocationsId(locationId);
		resType1.setStatus("F");
		resType1.setDisplaySequence(1);
		resType1.setDisplayName("Reservation");
		resType1.setName("Reservation");
		
		em.persist(resType1);

		ReservationsType resType2 = new ReservationsType();
		resType2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		resType2.setCreatedBy(userId);
		resType2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		resType2.setUpdatedBy(userId);
		resType2.setLocationsId(locationId);
		resType2.setStatus("F");
		resType2.setDisplaySequence(2);
		resType2.setDisplayName("Waitlist");
		resType2.setName("Waitlist");

		em.persist(resType2);

		ReservationsType resType3 = new ReservationsType();
		resType3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		resType3.setCreatedBy(userId);
		resType3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		resType3.setUpdatedBy(userId);
		resType3.setLocationsId(locationId);
		resType3.setStatus("F");
		resType3.setDisplaySequence(3);
		resType3.setDisplayName("Walk in");
		resType3.setName("Walk in");

		em.persist(resType3);

	}

	/**
	 * Adds the payment ways constant.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 */
	void addPaymentWaysConstant(EntityManager em, String userId, String locationId, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code
		PaymentWay paymentWay1 = new PaymentWay();
		paymentWay1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentWay1.setCreatedBy(userId);
		paymentWay1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentWay1.setUpdatedBy(userId);
		paymentWay1.setLocationsId(locationId);
		paymentWay1.setStatus("F");
		paymentWay1.setDisplaySequence(1);
		paymentWay1.setDisplayName("All in One");
		paymentWay1.setName("All in One");
		try
		{
//			if (paymentWay1.getId() == 0)
//				paymentWay1.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentWay1.getLocationsId(), httpRequest, "payment_ways"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentWay1);

		PaymentWay paymentWay2 = new PaymentWay();
		paymentWay2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentWay2.setCreatedBy(userId);
		paymentWay2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentWay2.setUpdatedBy(userId);
		paymentWay2.setLocationsId(locationId);
		paymentWay2.setStatus("F");
		paymentWay2.setDisplaySequence(2);
		paymentWay2.setDisplayName("By Guest Count");
		paymentWay2.setName("Evenly Split by Guest");
		try
		{
//			paymentWay2.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentWay2.getLocationsId(), httpRequest, "payment_ways"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentWay2);

		PaymentWay paymentWay3 = new PaymentWay();
		paymentWay3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentWay3.setCreatedBy(userId);
		paymentWay3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentWay3.setUpdatedBy(userId);
		paymentWay3.setLocationsId(locationId);
		paymentWay3.setStatus("F");
		paymentWay3.setDisplaySequence(3);
		paymentWay3.setDisplayName("By Seat");
		paymentWay3.setName("Split by Seat");
		try
		{
//			paymentWay3.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentWay3.getLocationsId(), httpRequest, "payment_ways"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentWay3);
		
		PaymentWay paymentWay4 = new PaymentWay();
		paymentWay4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentWay4.setCreatedBy(userId);
		paymentWay4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentWay4.setUpdatedBy(userId);
		paymentWay4.setLocationsId(locationId);
		paymentWay4.setStatus("F");
		paymentWay4.setDisplaySequence(3);
		paymentWay4.setDisplayName("Split by Reporting Category");
		paymentWay4.setName("Split by Reporting Category");
		try
		{
//			paymentWay3.setId(new StoreForwardUtility().generateDynamicIntId(em, paymentWay3.getLocationsId(), httpRequest, "payment_ways"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(paymentWay4);

	}

	/**
	 * Adds the order status 1 constants.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param oSource
	 *            the o source
	 */
	void addOrderStatus1Constants(EntityManager em, String userId, String locationId, String oSource, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code
		OrderStatus oStatus = new OrderStatus();
		oStatus.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus.setCreatedBy(userId);
		oStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus.setUpdatedBy(userId);
		oStatus.setLocationsId(locationId);
		oStatus.setStatus("F");
		oStatus.setDisplaySequence(1);
		oStatus.setDisplayName("Table ready");
		oStatus.setName("Ready to Order");
		oStatus.setOrderSourceGroupId(oSource);
		oStatus.setImageUrl("tableready.png");
		oStatus.setIsServerDriven((byte) 1);
		oStatus.setStatusColour("#C1C2C3");
		oStatus.setDescription("Check paid and table bused");
		try
		{
			if (oStatus.getId() == null)
				oStatus.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus);

		OrderStatus oStatus1 = new OrderStatus();

		oStatus1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus1.setCreatedBy(userId);
		oStatus1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus1.setUpdatedBy(userId);
		oStatus1.setLocationsId(locationId);
		oStatus1.setStatus("F");
		oStatus1.setDisplaySequence(2);
		oStatus1.setDisplayName("Seat");
		oStatus1.setName("Order Created");
		oStatus1.setOrderSourceGroupId(oSource);
		oStatus1.setIsServerDriven((byte) 0);
		oStatus1.setStatusColour("#662D91");
		oStatus1.setDescription("Click the table to allocate it to guests.");
		oStatus1.setImageUrl("orderplaced.png");
		try
		{
			oStatus1.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus1.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus1);

		OrderStatus oStatus2 = new OrderStatus();

		oStatus2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus2.setCreatedBy(userId);
		oStatus2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus2.setUpdatedBy(userId);
		oStatus2.setLocationsId(locationId);
		oStatus2.setStatus("F");
		oStatus2.setDisplaySequence(3);
		oStatus2.setDisplayName("Paid");
		oStatus2.setName("Order Paid");
		oStatus2.setOrderSourceGroupId(oSource);
		oStatus2.setIsServerDriven((byte) 0);
		oStatus2.setStatusColour("#00652E");
		oStatus2.setDescription("The bill is paid");
		oStatus2.setImageUrl("paid.png");
		try
		{
			oStatus2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus2);

		OrderStatus oStatus3 = new OrderStatus();

		oStatus3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus3.setCreatedBy(userId);
		oStatus3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus3.setUpdatedBy(userId);
		oStatus3.setLocationsId(locationId);
		oStatus3.setStatus("F");
		oStatus3.setDisplaySequence(4);
		oStatus3.setDisplayName("Bus Ready");
		oStatus3.setName("Bus Ready");
		oStatus3.setOrderSourceGroupId(oSource);
		oStatus3.setIsServerDriven((byte) 1);
		oStatus3.setStatusColour("#414042");
		oStatus3.setDescription("The table requires cleaning before it can be ready for the next guest.");
		oStatus3.setImageUrl("busready.png");
		try
		{
			oStatus3.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus3.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus3);

		OrderStatus oStatus4 = new OrderStatus();

		oStatus4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus4.setCreatedBy(userId);
		oStatus4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus4.setUpdatedBy(userId);
		oStatus4.setLocationsId(locationId);
		oStatus4.setStatus("F");
		oStatus4.setDisplaySequence(5);
		oStatus4.setDisplayName("Order placed");
		oStatus4.setName("Order Placed");
		oStatus4.setOrderSourceGroupId(oSource);
		oStatus4.setIsServerDriven((byte) 0);
		oStatus4.setStatusColour("#c73030");
		oStatus4.setDescription("Order was placed and sent to the kitchen");
		oStatus4.setImageUrl("orderplaced.png");
		try
		{
			oStatus4.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus4.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus4);

		OrderStatus oStatus5 = new OrderStatus();

		oStatus5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus5.setCreatedBy(userId);
		oStatus5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus5.setUpdatedBy(userId);
		oStatus5.setLocationsId(locationId);
		oStatus5.setStatus("F");
		oStatus5.setDisplaySequence(7);
		oStatus5.setDisplayName("Check Presented");
		oStatus5.setName("Check Presented");
		oStatus5.setOrderSourceGroupId(oSource);
		oStatus5.setIsServerDriven((byte) 0);
		oStatus5.setStatusColour("#00a651");
		oStatus5.setDescription("The bill is presented but not paid");
		oStatus5.setImageUrl("check.png");
		try
		{
			oStatus5.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus5.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus5);

		OrderStatus oStatus6 = new OrderStatus();

		oStatus6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus6.setCreatedBy(userId);
		oStatus6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus6.setUpdatedBy(userId);
		oStatus6.setLocationsId(locationId);
		oStatus6.setStatus("F");
		oStatus6.setDisplaySequence(10);
		oStatus6.setDisplayName("Re-Open");
		oStatus6.setName("Reopen");
		oStatus6.setOrderSourceGroupId(oSource);
		oStatus6.setIsServerDriven((byte) 0);
		oStatus6.setStatusColour("#1a36d6");
		oStatus6.setDescription("Order Reopened");
		oStatus6.setImageUrl("check.png");
		try
		{
			oStatus6.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus6.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus6);

		OrderStatus oStatus7 = new OrderStatus();

		oStatus7.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus7.setCreatedBy(userId);
		oStatus7.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus7.setUpdatedBy(userId);
		oStatus7.setLocationsId(locationId);
		oStatus7.setStatus("F");
		oStatus7.setDisplaySequence(6);
		oStatus7.setDisplayName("Order Placed Fire");
		oStatus7.setName("Order Placed Fire");
		oStatus7.setOrderSourceGroupId(oSource);
		oStatus7.setIsServerDriven((byte) 0);
		oStatus7.setStatusColour("#B20000");
		oStatus7.setImageUrl("fire.png");
		oStatus7.setDescription("Order sent for fire");
		try
		{
			oStatus7.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus7.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus7);

		OrderStatus oStatus14 = new OrderStatus();

		oStatus14.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus14.setCreatedBy(userId);
		oStatus14.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus14.setUpdatedBy(userId);
		oStatus14.setLocationsId(locationId);
		// #34408:
		oStatus14.setStatus("I");
		oStatus14.setDisplaySequence(8);
		oStatus14.setDisplayName("Void Order");
		oStatus14.setName("Void Order");
		oStatus14.setOrderSourceGroupId(oSource);
		oStatus14.setIsServerDriven((byte) 1);
		oStatus14.setStatusColour("#C1C2C8");
		oStatus14.setDescription("Cancelling an order.Server can void an order if he has access to void.");
		oStatus14.setImageUrl("void.png");
		try
		{
			oStatus14.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus14.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus14);

		OrderStatus oStatus8 = new OrderStatus();

		oStatus8.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus8.setCreatedBy(userId);
		oStatus8.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus8.setUpdatedBy(userId);
		oStatus8.setLocationsId(locationId);
		oStatus8.setStatus("F");
		oStatus8.setDisplaySequence(9);
		oStatus8.setDisplayName("Discount Applied");
		oStatus8.setName("Discount Applied");
		oStatus8.setOrderSourceGroupId(oSource);
		oStatus8.setIsServerDriven((byte) 0);
		oStatus8.setStatusColour("#98818e");
		oStatus8.setDescription("Discount applied on order");
		try
		{
			oStatus8.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus8.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus8);

		OrderStatus oStatus9 = new OrderStatus();

		oStatus9.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus9.setCreatedBy(userId);
		oStatus9.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus9.setUpdatedBy(userId);
		oStatus9.setLocationsId(locationId);
		oStatus9.setStatus("F");
		oStatus9.setDisplaySequence(11);
		oStatus9.setDisplayName("Discount Replaced");
		oStatus9.setName("Discount Replaced");
		oStatus9.setOrderSourceGroupId(oSource);
		oStatus9.setIsServerDriven((byte) 0);
		oStatus9.setStatusColour("#0e0c0d");
		oStatus9.setDescription("Discount replaced on order");
		try
		{
			oStatus9.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus9.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus9);

		OrderStatus oStatus10 = new OrderStatus();

		oStatus10.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus10.setCreatedBy(userId);
		oStatus10.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus10.setUpdatedBy(userId);
		oStatus10.setLocationsId(locationId);
		oStatus10.setStatus("F");
		oStatus10.setDisplaySequence(12);
		oStatus10.setDisplayName("Discount Removed");
		oStatus10.setName("Discount Removed");
		oStatus10.setOrderSourceGroupId(oSource);
		oStatus10.setIsServerDriven((byte) 0);
		oStatus10.setStatusColour("#cdC2C8");
		oStatus10.setDescription("Discount removed on order");
		try
		{
			oStatus10.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus10.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus10);

		OrderStatus oStatus11 = new OrderStatus();

		oStatus11.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus11.setCreatedBy(userId);
		oStatus11.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus11.setUpdatedBy(userId);
		oStatus11.setLocationsId(locationId);
		oStatus11.setStatus("F");
		oStatus11.setDisplaySequence(13);
		oStatus11.setName("Item Discount Removed");
		oStatus11.setDisplayName("Item Discount Removed");
		oStatus11.setOrderSourceGroupId(oSource);
		oStatus11.setIsServerDriven((byte) 0);
		oStatus11.setStatusColour("#1c8fdb");
		oStatus11.setDescription("Item Discount Removed on order");
		try
		{
			oStatus11.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus11.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus11);

		OrderStatus oStatus12 = new OrderStatus();

		oStatus12.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus12.setCreatedBy(userId);
		oStatus12.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus12.setUpdatedBy(userId);
		oStatus12.setLocationsId(locationId);
		oStatus12.setStatus("F");
		oStatus12.setDisplaySequence(14);
		oStatus12.setName("Item Discount Replaced");
		oStatus12.setDisplayName("Item Discount Replaced");
		oStatus12.setOrderSourceGroupId(oSource);
		oStatus12.setIsServerDriven((byte) 0);
		oStatus12.setStatusColour("#dbce1c");
		oStatus12.setDescription("Item Discount Replaced on order");
		try
		{
			oStatus12.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus12.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus12);

		OrderStatus oStatus13 = new OrderStatus();

		oStatus13.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus13.setCreatedBy(userId);
		oStatus13.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus13.setUpdatedBy(userId);
		oStatus13.setLocationsId(locationId);
		oStatus13.setStatus("F");
		oStatus13.setDisplaySequence(15);
		oStatus13.setName("Item Discount Applied");
		oStatus13.setDisplayName("Item Discount Applied");
		oStatus13.setOrderSourceGroupId(oSource);
		oStatus13.setIsServerDriven((byte) 0);
		oStatus13.setStatusColour("#dbce1c");
		oStatus13.setDescription("Item Discount Applied on order");
		try
		{
			oStatus13.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus13.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus13);

		OrderStatus oStatus15 = new OrderStatus();

		oStatus15.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus15.setCreatedBy(userId);
		oStatus15.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus15.setUpdatedBy(userId);
		oStatus15.setLocationsId(locationId);
		oStatus15.setStatus("F");
		oStatus15.setDisplaySequence(2);
		oStatus15.setDisplayName("Table Merged");
		oStatus15.setName("Table Merged");
		oStatus15.setOrderSourceGroupId(oSource);
		oStatus15.setIsServerDriven((byte) 0);
		oStatus15.setStatusColour("#662D80");
		oStatus15.setDescription("Click the table to merge.");
		try
		{
			oStatus15.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus15.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus15);

		OrderStatus oStatus16 = new OrderStatus();

		oStatus16.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus16.setCreatedBy(userId);
		oStatus16.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus16.setUpdatedBy(userId);
		oStatus16.setLocationsId(locationId);
		oStatus16.setStatus("F");
		oStatus16.setDisplaySequence(16);
		oStatus16.setDisplayName("OA Received");
		oStatus16.setName("Order Ahead Received");
		oStatus16.setOrderSourceGroupId(oSource);
		oStatus16.setIsServerDriven((byte) 0);
		oStatus16.setStatusColour("#349934");
		oStatus16.setDescription("Order ahead received status");
		try
		{
			oStatus16.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus16.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus16);

		OrderStatus oStatus17 = new OrderStatus();

		oStatus17.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus17.setCreatedBy(userId);
		oStatus17.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus17.setUpdatedBy(userId);
		oStatus17.setLocationsId(locationId);
		oStatus17.setStatus("F");
		oStatus17.setDisplaySequence(17);
		oStatus17.setDisplayName("OA Placed");
		oStatus17.setName("Order Ahead Placed");
		oStatus17.setOrderSourceGroupId(oSource);
		oStatus17.setIsServerDriven((byte) 0);
		oStatus17.setStatusColour("#4d0808");
		oStatus17.setDescription("Order ahead Placed status");
		try
		{
			oStatus17.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus17.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus17);

		OrderStatus oStatus18 = new OrderStatus();

		oStatus18.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus18.setCreatedBy(userId);
		oStatus18.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus18.setUpdatedBy(userId);
		oStatus18.setLocationsId(locationId);
		oStatus18.setStatus("F");
		oStatus18.setDisplaySequence(18);
		oStatus18.setDisplayName("OA Check Presented");
		oStatus18.setName("Order Ahead Check Presented");
		oStatus18.setOrderSourceGroupId(oSource);
		oStatus18.setIsServerDriven((byte) 0);
		oStatus18.setStatusColour("#000000");
		oStatus18.setDescription("Order ahead Check Presented status");
		try
		{
			oStatus18.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus18.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus18);

		OrderStatus oStatus19 = new OrderStatus();

		oStatus19.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus19.setCreatedBy(userId);
		oStatus19.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus19.setUpdatedBy(userId);
		oStatus19.setLocationsId(locationId);
		oStatus19.setStatus("I");
		oStatus19.setDisplaySequence(18);
		oStatus19.setDisplayName("Order Suspend");
		oStatus19.setName("Order Suspend");
		oStatus19.setOrderSourceGroupId(oSource);
		oStatus19.setIsServerDriven((byte) 1);
		oStatus19.setStatusColour("#000000");
		oStatus19.setDescription("Order Suspend");
		try
		{
			oStatus19.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus19.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus19);

		OrderStatus oStatus20 = new OrderStatus();

		oStatus20.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus20.setCreatedBy(userId);
		oStatus20.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus20.setUpdatedBy(userId);
		oStatus20.setLocationsId(locationId);
		oStatus20.setStatus("I");
		oStatus20.setDisplaySequence(18);
		oStatus20.setDisplayName("Manager Response");
		oStatus20.setName("Manager Response");
		oStatus20.setOrderSourceGroupId(oSource);
		oStatus20.setIsServerDriven((byte) 1);
		oStatus20.setStatusColour("#000000");
		oStatus20.setDescription("Manager Response");
		try
		{
			oStatus20.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus20.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus20);

		OrderStatus oStatus21 = new OrderStatus();

		oStatus21.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus21.setCreatedBy(userId);
		oStatus21.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus21.setUpdatedBy(userId);
		oStatus21.setLocationsId(locationId);
		oStatus21.setStatus("F");
		oStatus21.setDisplaySequence(8);
		oStatus21.setDisplayName("Cancel Order");
		oStatus21.setName("Cancel Order");
		oStatus21.setOrderSourceGroupId(oSource);
		oStatus21.setIsServerDriven((byte) 1);
		oStatus21.setStatusColour("#C1C2C8");
		oStatus21.setDescription("Cancelling an order.Server can Cancel an order if he has access to Cancel.");
		oStatus21.setImageUrl("void.png");
		try
		{
			oStatus21.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus21.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus21);

		OrderStatus oStatus22 = new OrderStatus();

		oStatus22.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus22.setCreatedBy(userId);
		oStatus22.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus22.setUpdatedBy(userId);
		oStatus22.setLocationsId(locationId);
		oStatus22.setStatus("F");
		oStatus22.setDisplaySequence(9);
		oStatus22.setDisplayName("Order Ahead Paid");
		oStatus22.setName("Order Ahead Paid");
		oStatus22.setOrderSourceGroupId(oSource);
		oStatus22.setIsServerDriven((byte) 0);
		oStatus22.setStatusColour("#573b3d");
		oStatus22.setDescription("Order Ahead Paid");
		oStatus22.setImageUrl("");
		try
		{
			oStatus22.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus22.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus22);

		OrderStatus oStatus25 = new OrderStatus();

		oStatus25.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus25.setCreatedBy(userId);
		oStatus25.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus25.setUpdatedBy(userId);
		oStatus25.setLocationsId(locationId);
		oStatus25.setStatus("F");
		oStatus25.setDisplaySequence(21);
		oStatus25.setDisplayName("PaidPrint");
		oStatus25.setName("PaidPrint");
		oStatus25.setOrderSourceGroupId(oSource);
		oStatus25.setIsServerDriven((byte) 0);
		oStatus25.setStatusColour("#JDGC00");
		oStatus25.setDescription("PaidPrint");
		try
		{
			oStatus25.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus25.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus25);

		OrderStatus oStatus26_source2 = new OrderStatus();

		oStatus26_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus26_source2.setCreatedBy(userId);
		oStatus26_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus26_source2.setUpdatedBy(userId);
		oStatus26_source2.setLocationsId(locationId);
		oStatus26_source2.setStatus("F");
		oStatus26_source2.setDisplaySequence(22);
		oStatus26_source2.setDisplayName("PaidNoPrint");
		oStatus26_source2.setName("PaidNoPrint");
		oStatus26_source2.setOrderSourceGroupId(oSource);
		oStatus26_source2.setIsServerDriven((byte) 0);
		oStatus26_source2.setStatusColour("#CEEC00");
		oStatus26_source2.setDescription("PaidNoPrint");
		try
		{
			oStatus26_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus26_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus26_source2);

		OrderStatus oStatus27_source2 = new OrderStatus();
		oStatus27_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus27_source2.setCreatedBy(userId);
		oStatus27_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus27_source2.setUpdatedBy(userId);
		oStatus27_source2.setLocationsId(locationId);
		oStatus27_source2.setStatus("F");
		oStatus27_source2.setDisplaySequence(7);
		oStatus27_source2.setDisplayName("Ready To Serve");
		oStatus27_source2.setName("Ready To Serve");
		oStatus27_source2.setOrderSourceGroupId(oSource);
		oStatus27_source2.setIsServerDriven((byte) 0);
		oStatus27_source2.setStatusColour("#349934");
		oStatus27_source2.setImageUrl("readyToServe.png");
		oStatus27_source2.setDescription("Ready To Serve");
		try
		{
			oStatus27_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus27_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus27_source2);

		OrderStatus oStatus28_source2 = new OrderStatus();

		oStatus28_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus28_source2.setCreatedBy(userId);
		oStatus28_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus28_source2.setUpdatedBy(userId);
		oStatus28_source2.setLocationsId(locationId);
		oStatus28_source2.setStatus("F");
		oStatus28_source2.setDisplaySequence(2);
		oStatus28_source2.setDisplayName("Cooking");
		oStatus28_source2.setName("Cooking");
		oStatus28_source2.setOrderSourceGroupId(oSource);
		oStatus28_source2.setIsServerDriven((byte) 0);
		oStatus28_source2.setStatusColour("#349934");
		oStatus28_source2.setImageUrl("cooking.png");
		oStatus28_source2.setDescription("Order is being cooked");
		try
		{
			oStatus28_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus28_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus28_source2);

		OrderStatus oStatus29_source2 = new OrderStatus();

		oStatus29_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus29_source2.setCreatedBy(userId);
		oStatus29_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus29_source2.setUpdatedBy(userId);
		oStatus29_source2.setLocationsId(locationId);
		oStatus29_source2.setStatus("F");
		oStatus29_source2.setDisplaySequence(2);
		oStatus29_source2.setDisplayName("Appetizers Served");
		oStatus29_source2.setName("Appetizers Served");
		oStatus29_source2.setOrderSourceGroupId(oSource);
		oStatus29_source2.setIsServerDriven((byte) 0);
		oStatus29_source2.setStatusColour("	#FFEBCD");
		oStatus29_source2.setImageUrl("");
		oStatus29_source2.setDescription("Appetizers Served");
		try
		{
			oStatus29_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus29_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus29_source2);

		OrderStatus oStatus30_source2 = new OrderStatus();

		oStatus30_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus30_source2.setCreatedBy(userId);
		oStatus30_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus30_source2.setUpdatedBy(userId);
		oStatus30_source2.setLocationsId(locationId);
		oStatus30_source2.setStatus("F");
		oStatus30_source2.setDisplaySequence(2);
		oStatus30_source2.setDisplayName("Drinks Served");
		oStatus30_source2.setName("Drinks Served");
		oStatus30_source2.setOrderSourceGroupId(oSource);
		oStatus30_source2.setIsServerDriven((byte) 0);
		oStatus30_source2.setStatusColour("	#6495ED");
		oStatus30_source2.setImageUrl("");
		oStatus30_source2.setDescription("Drinks Served");
		try
		{
			oStatus30_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus30_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus30_source2);

		OrderStatus oStatus31_source2 = new OrderStatus();

		oStatus31_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus31_source2.setCreatedBy(userId);
		oStatus31_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus31_source2.setUpdatedBy(userId);
		oStatus31_source2.setLocationsId(locationId);
		oStatus31_source2.setStatus("F");
		oStatus31_source2.setDisplaySequence(2);
		oStatus31_source2.setDisplayName("Repeat Drink Served");
		oStatus31_source2.setName("Repeat Drink Served");
		oStatus31_source2.setOrderSourceGroupId(oSource);
		oStatus31_source2.setIsServerDriven((byte) 0);
		oStatus31_source2.setStatusColour("	#483D8B");
		oStatus31_source2.setImageUrl("");
		oStatus31_source2.setDescription("Repeat Drink Served");
		try
		{
			oStatus31_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus31_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus31_source2);

		OrderStatus oStatus32_source2 = new OrderStatus();

		oStatus32_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus32_source2.setCreatedBy(userId);
		oStatus32_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus32_source2.setUpdatedBy(userId);
		oStatus32_source2.setLocationsId(locationId);
		oStatus32_source2.setStatus("F");
		oStatus32_source2.setDisplaySequence(2);
		oStatus32_source2.setDisplayName("Food Served");
		oStatus32_source2.setName("Food Served");
		oStatus32_source2.setOrderSourceGroupId(oSource);
		oStatus32_source2.setIsServerDriven((byte) 0);
		oStatus32_source2.setStatusColour("	#00BFFF");
		oStatus32_source2.setImageUrl("");
		oStatus32_source2.setDescription("Food Served");
		try
		{
			oStatus32_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus32_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus32_source2);

		OrderStatus oStatus33_source2 = new OrderStatus();

		oStatus33_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus33_source2.setCreatedBy(userId);
		oStatus33_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus33_source2.setUpdatedBy(userId);
		oStatus33_source2.setLocationsId(locationId);
		oStatus33_source2.setStatus("F");
		oStatus33_source2.setDisplaySequence(2);
		oStatus33_source2.setDisplayName("Desserts Served");
		oStatus33_source2.setName("Desserts Served");
		oStatus33_source2.setOrderSourceGroupId(oSource);
		oStatus33_source2.setIsServerDriven((byte) 0);
		oStatus33_source2.setStatusColour("	#DAA520");
		oStatus33_source2.setImageUrl("");
		oStatus33_source2.setDescription("Desserts Served");
		try
		{
			oStatus33_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus33_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus33_source2);

		OrderStatus oStatus34_source2 = new OrderStatus();

		oStatus34_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus34_source2.setCreatedBy(userId);
		oStatus34_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus34_source2.setUpdatedBy(userId);
		oStatus34_source2.setLocationsId(locationId);
		oStatus34_source2.setStatus("F");
		oStatus34_source2.setDisplaySequence(2);
		oStatus34_source2.setDisplayName("Production Complete");
		oStatus34_source2.setName("Close Production");
		oStatus34_source2.setOrderSourceGroupId(oSource);
		oStatus34_source2.setIsServerDriven((byte) 0);
		oStatus34_source2.setStatusColour("	#DAA520");
		oStatus34_source2.setImageUrl("");
		oStatus34_source2.setDescription("Close Production");
		try
		{
			oStatus34_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus34_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus34_source2);

		OrderStatus oStatus35_source2 = new OrderStatus();

		oStatus35_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus35_source2.setCreatedBy(userId);
		oStatus35_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus35_source2.setUpdatedBy(userId);
		oStatus35_source2.setLocationsId(locationId);
		oStatus35_source2.setStatus("F");
		oStatus35_source2.setDisplaySequence(2);
		oStatus35_source2.setDisplayName("Quotation");
		oStatus35_source2.setName("Quotation");
		oStatus35_source2.setOrderSourceGroupId(oSource);
		oStatus35_source2.setIsServerDriven((byte) 0);
		oStatus35_source2.setStatusColour("	#DAA520");
		oStatus35_source2.setImageUrl("");
		oStatus35_source2.setDescription("Quotation");
		try
		{
			oStatus35_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus35_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus35_source2);

		OrderStatus oStatus36_source2 = new OrderStatus();

		oStatus36_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus36_source2.setCreatedBy(userId);
		oStatus36_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus36_source2.setUpdatedBy(userId);
		oStatus36_source2.setLocationsId(locationId);
		oStatus36_source2.setStatus("F");
		oStatus36_source2.setDisplaySequence(2);
		oStatus36_source2.setDisplayName("Order Confirm");
		oStatus36_source2.setName("Order Confirm");
		oStatus36_source2.setOrderSourceGroupId(oSource);
		oStatus36_source2.setIsServerDriven((byte) 0);
		oStatus36_source2.setStatusColour("	#DAA520");
		oStatus36_source2.setImageUrl("");
		oStatus36_source2.setDescription("Order Confirm");
		try
		{
			oStatus36_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus36_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus36_source2);

	}

	/**
	 * Adds the order status for pick up and deleivery source group.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param orderSourceGroup
	 *            the order source group
	 * @param isPickUp
	 *            the is pick up
	 */
	private void addOrderStatusForPickUpAndDeleiverySourceGroup(EntityManager em, String userId, String locationId, String orderSourceGroup, boolean isPickUp, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code
		OrderStatus oStatus5_source2 = new OrderStatus();

		oStatus5_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus5_source2.setCreatedBy(userId);
		oStatus5_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus5_source2.setUpdatedBy(userId);
		oStatus5_source2.setLocationsId(locationId);
		oStatus5_source2.setStatus("F");
		oStatus5_source2.setDisplaySequence(1);
		oStatus5_source2.setDisplayName("Order placed");
		oStatus5_source2.setName("Order Placed");
		oStatus5_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus5_source2.setIsServerDriven((byte) 0);
		oStatus5_source2.setStatusColour("#c73030");
		oStatus5_source2.setDescription("Order was placed and sent to the kitchen");
		oStatus5_source2.setImageUrl("orderplaced.png");
		try
		{
			if (oStatus5_source2.getId() == null)
				oStatus5_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus5_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus5_source2);

		OrderStatus oStatus6_source2 = new OrderStatus();

		oStatus6_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus6_source2.setCreatedBy(userId);
		oStatus6_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus6_source2.setUpdatedBy(userId);
		oStatus6_source2.setLocationsId(locationId);
		oStatus6_source2.setStatus("F");
		oStatus6_source2.setDisplaySequence(2);
		oStatus6_source2.setDisplayName("Cooking");
		oStatus6_source2.setName("Cooking");
		oStatus6_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus6_source2.setIsServerDriven((byte) 0);
		oStatus6_source2.setStatusColour("#349934");
		oStatus6_source2.setImageUrl("cooking.png");
		oStatus6_source2.setDescription("Order is being cooked");
		try
		{
			oStatus6_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus6_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus6_source2);

		OrderStatus oStatus7_source2 = new OrderStatus();

		oStatus7_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus7_source2.setCreatedBy(userId);
		oStatus7_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus7_source2.setUpdatedBy(userId);
		oStatus7_source2.setLocationsId(locationId);
		oStatus7_source2.setStatus("F");
		oStatus7_source2.setDisplaySequence(3);
		oStatus7_source2.setDisplayName("Quality Check");
		oStatus7_source2.setName("Quality Check");
		oStatus7_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus7_source2.setIsServerDriven((byte) 1);
		oStatus7_source2.setStatusColour("#00652E");
		oStatus7_source2.setDescription("Order being checked for takeout");
		try
		{
			oStatus7_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus7_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus7_source2);

		OrderStatus oStatus8_source2 = new OrderStatus();

		oStatus8_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus8_source2.setCreatedBy(userId);
		oStatus8_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus8_source2.setUpdatedBy(userId);
		oStatus8_source2.setLocationsId(locationId);
		oStatus8_source2.setStatus("F");
		oStatus8_source2.setDisplaySequence(4);
		if (isPickUp)
		{
			oStatus8_source2.setDisplayName("Ready for pick up");
			oStatus8_source2.setName("Ready for pick up");
			oStatus8_source2.setDescription("Order is ready to be picked up");
		}
		else
		{
			oStatus8_source2.setDisplayName("Ready for delivery");
			oStatus8_source2.setName("Ready for delivery");
			oStatus8_source2.setDescription("Order is ready to be delivered");
		}

		oStatus8_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus8_source2.setIsServerDriven((byte) 1);
		oStatus8_source2.setStatusColour("#414042");
		try
		{
			oStatus8_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus8_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus8_source2);

		OrderStatus oStatus9_source2 = new OrderStatus();

		oStatus9_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus9_source2.setCreatedBy(userId);
		oStatus9_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus9_source2.setUpdatedBy(userId);
		oStatus9_source2.setLocationsId(locationId);
		oStatus9_source2.setStatus("F");
		oStatus9_source2.setDisplaySequence(5);
		if (isPickUp)
		{
			oStatus9_source2.setDisplayName("Picked up");
			oStatus9_source2.setDescription("Order has already been picked up");
		}
		else
		{
			oStatus9_source2.setDisplayName("Delivery");
			oStatus9_source2.setDescription("Order has been deleiverd to customer");
		}

		oStatus9_source2.setName("Ready to Order");
		oStatus9_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus9_source2.setIsServerDriven((byte) 1);
		oStatus9_source2.setStatusColour("#49e01b");
		oStatus9_source2.setImageUrl("");
		try
		{
			oStatus9_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus9_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus9_source2);

		OrderStatus oStatus10_source2 = new OrderStatus();

		oStatus10_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus10_source2.setCreatedBy(userId);
		oStatus10_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus10_source2.setUpdatedBy(userId);
		oStatus10_source2.setLocationsId(locationId);
		oStatus10_source2.setStatus("F");
		oStatus10_source2.setDisplaySequence(6);
		oStatus10_source2.setDisplayName("Reopen");
		oStatus10_source2.setName("Reopen");
		oStatus10_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus10_source2.setIsServerDriven((byte) 0);
		oStatus10_source2.setStatusColour("#1255e6");
		oStatus10_source2.setDescription("Order Repopened");
		try
		{
			oStatus10_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus10_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus10_source2);

		OrderStatus oStatus11_source2 = new OrderStatus();

		oStatus11_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus11_source2.setCreatedBy(userId);
		oStatus11_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus11_source2.setUpdatedBy(userId);
		oStatus11_source2.setLocationsId(locationId);
		oStatus11_source2.setStatus("F");
		oStatus11_source2.setDisplaySequence(7);
		oStatus11_source2.setDisplayName("Paid");
		oStatus11_source2.setName("Order Paid");
		oStatus11_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus11_source2.setIsServerDriven((byte) 0);
		oStatus11_source2.setStatusColour("#00652e");
		oStatus11_source2.setDescription("Order Paid");
		oStatus11_source2.setImageUrl("paid.png");
		try
		{
			oStatus11_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus11_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus11_source2);

		OrderStatus oStatus12_source2 = new OrderStatus();

		oStatus12_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus12_source2.setCreatedBy(userId);
		oStatus12_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus12_source2.setUpdatedBy(userId);
		oStatus12_source2.setLocationsId(locationId);
		// #34408:
		oStatus12_source2.setStatus("I");
		oStatus12_source2.setDisplaySequence(8);
		oStatus12_source2.setDisplayName("Void Order");
		oStatus12_source2.setName("Void Order");
		oStatus12_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus12_source2.setIsServerDriven((byte) 1);
		oStatus12_source2.setStatusColour("#C1C2C8");
		oStatus12_source2.setImageUrl("void.png");
		oStatus12_source2.setDescription("Cancelling an order.Server can void an order if he has access to void.");
		try
		{
			oStatus12_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus12_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus12_source2);

		OrderStatus oStatus13_source2 = new OrderStatus();

		oStatus13_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus13_source2.setCreatedBy(userId);
		oStatus13_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus13_source2.setUpdatedBy(userId);
		oStatus13_source2.setLocationsId(locationId);
		oStatus13_source2.setStatus("F");
		oStatus13_source2.setDisplaySequence(9);
		oStatus13_source2.setDisplayName("Discount Applied");
		oStatus13_source2.setName("Discount Applied");
		oStatus13_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus13_source2.setIsServerDriven((byte) 0);
		oStatus13_source2.setStatusColour("#8f7784");
		oStatus13_source2.setDescription("Discount applied on order");
		try
		{
			oStatus13_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus13_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus13_source2);

		OrderStatus oStatus14_source2 = new OrderStatus();

		oStatus14_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus14_source2.setCreatedBy(userId);
		oStatus14_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus14_source2.setUpdatedBy(userId);
		oStatus14_source2.setLocationsId(locationId);
		oStatus14_source2.setStatus("F");
		oStatus14_source2.setDisplaySequence(10);
		oStatus14_source2.setDisplayName("Discount Replaced");
		oStatus14_source2.setName("Discount Replaced");
		oStatus14_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus14_source2.setIsServerDriven((byte) 0);
		oStatus14_source2.setStatusColour("#2579E6");
		oStatus14_source2.setDescription("Discount replaced on order");
		try
		{
			oStatus14_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus14_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus14_source2);

		OrderStatus oStatus15_source2 = new OrderStatus();

		oStatus15_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus15_source2.setCreatedBy(userId);
		oStatus15_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus15_source2.setUpdatedBy(userId);
		oStatus15_source2.setLocationsId(locationId);
		oStatus15_source2.setStatus("F");
		oStatus15_source2.setDisplaySequence(11);
		oStatus15_source2.setDisplayName("Discount Removed");
		oStatus15_source2.setName("Discount Removed");
		oStatus15_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus15_source2.setIsServerDriven((byte) 0);
		oStatus15_source2.setStatusColour("#cdC2C8");
		oStatus15_source2.setDescription("Discount removed on order");
		try
		{
			oStatus15_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus15_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus15_source2);

		OrderStatus oStatus16_source2 = new OrderStatus();

		oStatus16_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus16_source2.setCreatedBy(userId);
		oStatus16_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus16_source2.setUpdatedBy(userId);
		oStatus16_source2.setLocationsId(locationId);
		oStatus16_source2.setStatus("F");
		oStatus16_source2.setDisplaySequence(12);
		oStatus16_source2.setName("Item Discount Removed");
		oStatus16_source2.setDisplayName("Item Discount Removed");
		oStatus16_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus16_source2.setIsServerDriven((byte) 0);
		oStatus16_source2.setStatusColour("#1c8fdb");
		oStatus16_source2.setDescription("Item Discount Removed on order");
		try
		{
			oStatus16_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus16_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus16_source2);

		OrderStatus oStatus17_source2 = new OrderStatus();

		oStatus17_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus17_source2.setCreatedBy(userId);
		oStatus17_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus17_source2.setUpdatedBy(userId);
		oStatus17_source2.setLocationsId(locationId);
		oStatus17_source2.setStatus("F");
		oStatus17_source2.setDisplaySequence(13);
		oStatus17_source2.setName("Item Discount Replaced");
		oStatus17_source2.setDisplayName("Item Discount Replaced");
		oStatus17_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus17_source2.setIsServerDriven((byte) 0);
		oStatus17_source2.setStatusColour("#dbce1c");
		oStatus17_source2.setDescription("Item Discount Removed on order");
		try
		{
			oStatus17_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus17_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus17_source2);

		OrderStatus oStatus18_source2 = new OrderStatus();

		oStatus18_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus18_source2.setCreatedBy(userId);
		oStatus18_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus18_source2.setUpdatedBy(userId);
		oStatus18_source2.setLocationsId(locationId);
		oStatus18_source2.setStatus("F");
		oStatus18_source2.setDisplaySequence(14);
		oStatus18_source2.setName("Item Discount Applied");
		oStatus18_source2.setDisplayName("Item Discount Applied");
		oStatus18_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus18_source2.setIsServerDriven((byte) 0);
		oStatus18_source2.setStatusColour("#dbce1c");
		oStatus18_source2.setDescription("Item Discount Applied on order");
		try
		{
			oStatus18_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus18_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus18_source2);

		OrderStatus oStatus19_source2 = new OrderStatus();

		oStatus19_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus19_source2.setCreatedBy(userId);
		oStatus19_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus19_source2.setUpdatedBy(userId);
		oStatus19_source2.setLocationsId(locationId);
		oStatus19_source2.setStatus("F");
		oStatus19_source2.setDisplaySequence(15);
		oStatus19_source2.setName("Check Presented");
		oStatus19_source2.setDisplayName("Check Presented");
		oStatus19_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus19_source2.setIsServerDriven((byte) 1);
		oStatus19_source2.setStatusColour("#dbce1c");
		oStatus19_source2.setDescription("Check Presented on order");
		oStatus19_source2.setImageUrl("check.png");
		try
		{
			oStatus19_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus19_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus19_source2);

		OrderStatus oStatus20_source2 = new OrderStatus();

		oStatus20_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus20_source2.setCreatedBy(userId);
		oStatus20_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus20_source2.setUpdatedBy(userId);
		oStatus20_source2.setLocationsId(locationId);
		oStatus20_source2.setStatus("F");
		oStatus20_source2.setDisplaySequence(16);
		oStatus20_source2.setDisplayName("OA Received");
		oStatus20_source2.setName("Order Ahead Received");
		oStatus20_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus20_source2.setIsServerDriven((byte) 0);
		oStatus20_source2.setStatusColour("#349934");
		oStatus20_source2.setDescription("Order ahead received status");
		try
		{
			oStatus20_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus20_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus20_source2);

		OrderStatus oStatus21_source2 = new OrderStatus();

		oStatus21_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus21_source2.setCreatedBy(userId);
		oStatus21_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus21_source2.setUpdatedBy(userId);
		oStatus21_source2.setLocationsId(locationId);
		oStatus21_source2.setStatus("F");
		oStatus21_source2.setDisplaySequence(17);
		oStatus21_source2.setDisplayName("OA Placed");
		oStatus21_source2.setName("Order Ahead Placed");
		oStatus21_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus21_source2.setIsServerDriven((byte) 0);
		oStatus21_source2.setStatusColour("#4d0808");
		oStatus21_source2.setDescription("Order ahead Placed status");
		try
		{
			oStatus21_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus21_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus21_source2);

		OrderStatus oStatus22_source2 = new OrderStatus();

		oStatus22_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus22_source2.setCreatedBy(userId);
		oStatus22_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus22_source2.setUpdatedBy(userId);
		oStatus22_source2.setLocationsId(locationId);
		oStatus22_source2.setStatus("F");
		oStatus22_source2.setDisplaySequence(18);
		oStatus22_source2.setDisplayName("OA Check Presented");
		oStatus22_source2.setName("Order Ahead Check Presented");
		oStatus22_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus22_source2.setIsServerDriven((byte) 0);
		oStatus22_source2.setStatusColour("#000000");
		oStatus22_source2.setDescription("Order ahead Check Presented status");
		try
		{
			oStatus22_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus22_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus22_source2);

		OrderStatus oStatus23_source2 = new OrderStatus();

		oStatus23_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus23_source2.setCreatedBy(userId);
		oStatus23_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus23_source2.setUpdatedBy(userId);
		oStatus23_source2.setLocationsId(locationId);
		oStatus23_source2.setStatus("F");
		oStatus23_source2.setDisplaySequence(19);
		oStatus23_source2.setDisplayName("OA Paid");
		oStatus23_source2.setName("Order Ahead Paid");
		oStatus23_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus23_source2.setIsServerDriven((byte) 0);
		oStatus23_source2.setStatusColour("#c47272");
		oStatus23_source2.setDescription("Order ahead Paid status");
		try
		{
			oStatus23_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus23_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus23_source2);

		OrderStatus oStatus24_source2 = new OrderStatus();

		oStatus24_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus24_source2.setCreatedBy(userId);
		oStatus24_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus24_source2.setUpdatedBy(userId);
		oStatus24_source2.setLocationsId(locationId);
		oStatus24_source2.setStatus("F");
		oStatus24_source2.setDisplaySequence(20);
		oStatus24_source2.setDisplayName("Add to Cart");
		oStatus24_source2.setName("Add to Cart");
		oStatus24_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus24_source2.setIsServerDriven((byte) 0);
		oStatus24_source2.setStatusColour("#CCCC00");
		oStatus24_source2.setDescription("add order to cart");
		try
		{
			oStatus24_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus24_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus24_source2);

		OrderStatus oStatus25_source2 = new OrderStatus();

		oStatus25_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus25_source2.setCreatedBy(userId);
		oStatus25_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus25_source2.setUpdatedBy(userId);
		oStatus25_source2.setLocationsId(locationId);
		oStatus25_source2.setStatus("F");
		oStatus25_source2.setDisplaySequence(21);
		oStatus25_source2.setDisplayName("PaidPrint");
		oStatus25_source2.setName("PaidPrint");
		oStatus25_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus25_source2.setIsServerDriven((byte) 0);
		oStatus25_source2.setStatusColour("#JDGC00");
		oStatus25_source2.setDescription("PaidPrint");
		try
		{
			oStatus25_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus25_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus25_source2);

		OrderStatus oStatus26_source2 = new OrderStatus();

		oStatus26_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus26_source2.setCreatedBy(userId);
		oStatus26_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus26_source2.setUpdatedBy(userId);
		oStatus26_source2.setLocationsId(locationId);
		oStatus26_source2.setStatus("F");
		oStatus26_source2.setDisplaySequence(22);
		oStatus26_source2.setDisplayName("PaidNoPrint");
		oStatus26_source2.setName("PaidNoPrint");
		oStatus26_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus26_source2.setIsServerDriven((byte) 0);
		oStatus26_source2.setStatusColour("#CEEC00");
		oStatus26_source2.setDescription("PaidNoPrint");
		try
		{
			oStatus26_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus26_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus26_source2);

		OrderStatus oStatus27_source2 = new OrderStatus();

		oStatus27_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus27_source2.setCreatedBy(userId);
		oStatus27_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus27_source2.setUpdatedBy(userId);
		oStatus27_source2.setLocationsId(locationId);
		oStatus27_source2.setStatus("F");
		oStatus27_source2.setDisplaySequence(22);
		oStatus27_source2.setDisplayName("Order Suspend");
		oStatus27_source2.setName("Order Suspend");
		oStatus27_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus27_source2.setIsServerDriven((byte) 0);
		oStatus27_source2.setStatusColour("#CEEC00");
		oStatus27_source2.setDescription("Order Suspend");
		try
		{
			oStatus27_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus27_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus27_source2);

		OrderStatus oStatus28_source2 = new OrderStatus();
		oStatus28_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus28_source2.setCreatedBy(userId);
		oStatus28_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus28_source2.setUpdatedBy(userId);
		oStatus28_source2.setLocationsId(locationId);
		oStatus28_source2.setStatus("I");
		oStatus28_source2.setDisplaySequence(18);
		oStatus28_source2.setDisplayName("Manager Response");
		oStatus28_source2.setName("Manager Response");
		oStatus28_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus28_source2.setIsServerDriven((byte) 1);
		oStatus28_source2.setStatusColour("#000000");
		oStatus28_source2.setDescription("Manager Response");
		try
		{
			oStatus28_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus28_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus28_source2);

		OrderStatus oStatus29_source2 = new OrderStatus();
		oStatus29_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus29_source2.setCreatedBy(userId);
		oStatus29_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus29_source2.setUpdatedBy(userId);
		oStatus29_source2.setLocationsId(locationId);
		oStatus29_source2.setStatus("F");
		oStatus29_source2.setDisplaySequence(18);
		oStatus29_source2.setDisplayName("Cancel Order");
		oStatus29_source2.setName("Cancel Order");
		oStatus29_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus29_source2.setIsServerDriven((byte) 1);
		oStatus29_source2.setStatusColour("#C1C2C8");
		oStatus29_source2.setDescription("Cancel Order");
		try
		{
			oStatus29_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus29_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus29_source2);

		OrderStatus oStatus31_source2 = new OrderStatus();

		oStatus31_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus31_source2.setCreatedBy(userId);
		oStatus31_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus31_source2.setUpdatedBy(userId);
		oStatus31_source2.setLocationsId(locationId);
		oStatus31_source2.setStatus("F");
		oStatus31_source2.setDisplaySequence(6);
		oStatus31_source2.setDisplayName("Order Placed Fire");
		oStatus31_source2.setName("Order Placed Fire");
		oStatus31_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus31_source2.setIsServerDriven((byte) 0);
		oStatus31_source2.setStatusColour("#B20000");
		oStatus31_source2.setImageUrl("fire.png");
		oStatus31_source2.setDescription("Order sent for fire");
		try
		{
			oStatus31_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus31_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus31_source2);

		OrderStatus oStatus32_source2 = new OrderStatus();
		oStatus32_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus32_source2.setCreatedBy(userId);
		oStatus32_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus32_source2.setUpdatedBy(userId);
		oStatus32_source2.setLocationsId(locationId);
		oStatus32_source2.setStatus("F");
		oStatus32_source2.setDisplaySequence(7);
		oStatus32_source2.setDisplayName("Ready To Serve");
		oStatus32_source2.setName("Ready To Serve");
		oStatus32_source2.setOrderSourceGroupId(orderSourceGroup);
		oStatus32_source2.setIsServerDriven((byte) 0);
		oStatus32_source2.setStatusColour("#349934");
		oStatus32_source2.setImageUrl("readyToServe.png");
		oStatus32_source2.setDescription("Ready To Serve");
		try
		{
			oStatus32_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus32_source2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus32_source2);

		if (!isPickUp)
		{
			OrderStatus oStatus33_source2 = new OrderStatus();
			oStatus33_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			oStatus33_source2.setCreatedBy(userId);
			oStatus33_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			oStatus33_source2.setUpdatedBy(userId);
			oStatus33_source2.setLocationsId(locationId);
			oStatus33_source2.setStatus("F");
			oStatus33_source2.setDisplaySequence(7);
			oStatus33_source2.setDisplayName("Assign Driver");
			oStatus33_source2.setName("Assign Driver");
			oStatus33_source2.setOrderSourceGroupId(orderSourceGroup);
			oStatus33_source2.setIsServerDriven((byte) 0);
			oStatus33_source2.setStatusColour("#349934");
			oStatus33_source2.setImageUrl("");
			oStatus33_source2.setDescription("Assign Driver");
			try
			{
				oStatus33_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus33_source2.getLocationsId(), httpRequest, "order_status"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(oStatus33_source2);
		}

		if (!isPickUp)
		{
			OrderStatus oStatus34_source2 = new OrderStatus();
			oStatus34_source2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			oStatus34_source2.setCreatedBy(userId);
			oStatus34_source2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			oStatus34_source2.setUpdatedBy(userId);
			oStatus34_source2.setLocationsId(locationId);
			oStatus34_source2.setStatus("F");
			oStatus34_source2.setDisplaySequence(7);
			oStatus34_source2.setDisplayName("Picked up From Restaurant");
			oStatus34_source2.setName("Picked up From Restaurant");
			oStatus34_source2.setOrderSourceGroupId(orderSourceGroup);
			oStatus34_source2.setIsServerDriven((byte) 0);
			oStatus34_source2.setStatusColour("#349934");
			oStatus34_source2.setImageUrl("");
			oStatus34_source2.setDescription("Picked up From Restaurant");
			try
			{
				oStatus34_source2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus34_source2.getLocationsId(), httpRequest, "order_status"));
			}
			catch (Exception e)
			{

				logger.severe(e);
			}
			em.persist(oStatus34_source2);

			// OrderStatus oStatus35_source2 = new OrderStatus();
			// oStatus35_source2.setCreated(new
			// Date(new TimezoneTime().getGMTTimeInMilis()));
			// oStatus35_source2.setCreatedBy(userId);
			// oStatus35_source2.setUpdated(new
			// Date(new TimezoneTime().getGMTTimeInMilis()));
			// oStatus35_source2.setUpdatedBy(userId);
			// oStatus35_source2.setLocationsId(locationId);
			// oStatus35_source2.setStatus("F");
			// oStatus35_source2.setDisplaySequence(7);
			// oStatus35_source2.setDisplayName("Delivered");
			// oStatus35_source2.setName("Delivered");
			// oStatus35_source2.setOrderSourceGroupId(orderSourceGroup);
			// oStatus35_source2.setIsServerDriven((byte) 0);
			// oStatus35_source2.setStatusColour("#349934");
			// oStatus35_source2.setImageUrl("");
			// oStatus35_source2.setDescription("Delivered");
			// em.persist(oStatus35_source2);
		}

	}

	/**
	 * Adds the order detail status constants.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param oSource
	 *            the o source
	 */
	void addOrderDetailStatusConstants(EntityManager em, String userId, String locationId, String oSource)
	{
		// todo shlok need to handle exception in below
		// modulise code
		OrderDetailStatus oDetailStatus1 = new OrderDetailStatus();

		oDetailStatus1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus1.setCreatedBy(userId);
		oDetailStatus1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus1.setUpdatedBy(userId);
		oDetailStatus1.setLocationsId(locationId);
		oDetailStatus1.setStatus("F");
		oDetailStatus1.setDisplaySequence(1);
		oDetailStatus1.setDisplayName("Send");
		oDetailStatus1.setName("Sent to Kitchen");
		oDetailStatus1.setOrderSourceGroupId(oSource);
		oDetailStatus1.setIsServerDriven((byte) 0);
		oDetailStatus1.setStatusColour("#A6293C");
		oDetailStatus1.setDescription("An item is sent to kitchen");

		em.persist(oDetailStatus1);

		OrderDetailStatus oDetailStatus2 = new OrderDetailStatus();

		oDetailStatus2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus2.setCreatedBy(userId);
		oDetailStatus2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus2.setUpdatedBy(userId);
		oDetailStatus2.setLocationsId(locationId);
		oDetailStatus2.setStatus("F");
		oDetailStatus2.setDisplaySequence(2);
		oDetailStatus2.setDisplayName("Printed");
		oDetailStatus2.setName("KOT Printed");
		oDetailStatus2.setOrderSourceGroupId(oSource);
		oDetailStatus2.setIsServerDriven((byte) 0);
		oDetailStatus2.setStatusColour("#99916B");
		oDetailStatus2.setDescription("An item is printed successfully in the kitchen printer");

		em.persist(oDetailStatus2);

		OrderDetailStatus oDetailStatus3 = new OrderDetailStatus();

		oDetailStatus3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus3.setCreatedBy(userId);
		oDetailStatus3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus3.setUpdatedBy(userId);
		oDetailStatus3.setLocationsId(locationId);
		oDetailStatus3.setStatus("F");
		oDetailStatus3.setDisplaySequence(3);
		oDetailStatus3.setDisplayName("Not Printed");
		oDetailStatus3.setName("KOT Not Printed");
		oDetailStatus3.setOrderSourceGroupId(oSource);
		oDetailStatus3.setIsServerDriven((byte) 0);
		oDetailStatus3.setStatusColour("#F05123");
		oDetailStatus3.setDescription("An item did not print successfully");

		em.persist(oDetailStatus3);

		OrderDetailStatus oDetailStatus4 = new OrderDetailStatus();

		oDetailStatus4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus4.setCreatedBy(userId);
		oDetailStatus4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus4.setUpdatedBy(userId);
		oDetailStatus4.setLocationsId(locationId);
		oDetailStatus4.setStatus("F");
		oDetailStatus4.setDisplaySequence(4);
		oDetailStatus4.setDisplayName("Resend");
		oDetailStatus4.setName("Resend");
		oDetailStatus4.setOrderSourceGroupId(oSource);
		oDetailStatus4.setIsServerDriven((byte) 0);
		oDetailStatus4.setDescription("An item which is not printed successfully can be printed again");

		em.persist(oDetailStatus4);

		OrderDetailStatus oDetailStatus5 = new OrderDetailStatus();

		oDetailStatus5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus5.setCreatedBy(userId);
		oDetailStatus5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus5.setUpdatedBy(userId);
		oDetailStatus5.setLocationsId(locationId);
		oDetailStatus5.setStatus("F");
		oDetailStatus5.setDisplaySequence(5);
		oDetailStatus5.setDisplayName("Save");
		oDetailStatus5.setName("Item saved");
		oDetailStatus5.setOrderSourceGroupId(oSource);
		oDetailStatus5.setIsServerDriven((byte) 0);
		oDetailStatus5.setStatusColour("#9C3D3D");
		oDetailStatus5.setDescription("An item is saved but not send to kitchen");

		em.persist(oDetailStatus5);

		OrderDetailStatus oDetailStatus6 = new OrderDetailStatus();

		oDetailStatus6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus6.setCreatedBy(userId);
		oDetailStatus6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus6.setUpdatedBy(userId);
		oDetailStatus6.setLocationsId(locationId);
		oDetailStatus6.setStatus("F");
		oDetailStatus6.setDisplaySequence(6);
		oDetailStatus6.setDisplayName("Remove");
		oDetailStatus6.setName("Item Removed");
		oDetailStatus6.setOrderSourceGroupId(oSource);
		oDetailStatus6.setIsServerDriven((byte) 0);
		oDetailStatus6.setDescription("An saved item is removed");

		em.persist(oDetailStatus6);

		OrderDetailStatus oDetailStatus7 = new OrderDetailStatus();

		oDetailStatus7.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus7.setCreatedBy(userId);
		oDetailStatus7.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus7.setUpdatedBy(userId);
		oDetailStatus7.setLocationsId(locationId);
		oDetailStatus7.setStatus("F");
		oDetailStatus7.setDisplaySequence(7);
		oDetailStatus7.setDisplayName("Save");
		oDetailStatus7.setName("Attribute saved");
		oDetailStatus7.setOrderSourceGroupId(oSource);
		oDetailStatus7.setIsServerDriven((byte) 0);
		oDetailStatus7.setStatusColour("#9C3D3D");
		oDetailStatus7.setDescription("An attribute is saved but not send to kitchen");

		em.persist(oDetailStatus7);

		OrderDetailStatus oDetailStatus8 = new OrderDetailStatus();

		oDetailStatus8.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus8.setCreatedBy(userId);
		oDetailStatus8.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus8.setUpdatedBy(userId);
		oDetailStatus8.setLocationsId(locationId);
		oDetailStatus8.setStatus("F");
		oDetailStatus8.setDisplaySequence(8);
		oDetailStatus8.setDisplayName("Remove");
		oDetailStatus8.setName("Attribute Removed");
		oDetailStatus8.setOrderSourceGroupId(oSource);
		oDetailStatus8.setIsServerDriven((byte) 0);
		oDetailStatus8.setDescription("An saved attribute is removed");

		em.persist(oDetailStatus8);

		OrderDetailStatus oDetailStatus9 = new OrderDetailStatus();

		oDetailStatus9.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus9.setCreatedBy(userId);
		oDetailStatus9.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus9.setUpdatedBy(userId);
		oDetailStatus9.setLocationsId(locationId);
		oDetailStatus9.setStatus("F");
		oDetailStatus9.setDisplaySequence(9);
		oDetailStatus9.setDisplayName("Cancel");
		oDetailStatus9.setName("Recall");
		oDetailStatus9.setOrderSourceGroupId(oSource);
		oDetailStatus9.setIsServerDriven((byte) 0);
		oDetailStatus9.setDescription("An item is recalled if the item is already sent and not under preparation");

		em.persist(oDetailStatus9);

		OrderDetailStatus oDetailStatus10 = new OrderDetailStatus();

		oDetailStatus10.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus10.setCreatedBy(userId);
		oDetailStatus10.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus10.setUpdatedBy(userId);
		oDetailStatus10.setLocationsId(locationId);
		oDetailStatus10.setStatus("F");
		oDetailStatus10.setDisplaySequence(9);
		oDetailStatus10.setDisplayName("Void Item");
		oDetailStatus10.setName("Void Item");
		oDetailStatus10.setOrderSourceGroupId(oSource);
		oDetailStatus10.setIsServerDriven((byte) 0);
		oDetailStatus10.setDescription("An item is voided.");

		em.persist(oDetailStatus10);

		OrderDetailStatus oDetailStatus11 = new OrderDetailStatus();

		oDetailStatus11.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus11.setCreatedBy(userId);
		oDetailStatus11.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus11.setUpdatedBy(userId);
		oDetailStatus11.setLocationsId(locationId);
		oDetailStatus11.setStatus("F");
		oDetailStatus11.setDisplaySequence(10);
		oDetailStatus11.setDisplayName("Out Of Stock");
		oDetailStatus11.setName("Out Of Stock");
		oDetailStatus11.setOrderSourceGroupId(oSource);
		oDetailStatus11.setIsServerDriven((byte) 0);
		oDetailStatus11.setDescription("An item is out of stock.");

		em.persist(oDetailStatus11);

		OrderDetailStatus oDetailStatus12 = new OrderDetailStatus();

		oDetailStatus12.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus12.setCreatedBy(userId);
		oDetailStatus12.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus12.setUpdatedBy(userId);
		oDetailStatus12.setLocationsId(locationId);
		oDetailStatus12.setStatus("F");
		oDetailStatus12.setDisplaySequence(11);
		oDetailStatus12.setDisplayName("Ordered Qty More Than Avail Qty");
		oDetailStatus12.setName("Ordered Qty More Than Avail Qty");
		oDetailStatus12.setOrderSourceGroupId(oSource);
		oDetailStatus12.setIsServerDriven((byte) 0);
		oDetailStatus12.setDescription("Ordered Qty More Than Avail Qty.");

		em.persist(oDetailStatus12);

		OrderDetailStatus oDetailStatus14 = new OrderDetailStatus();

		oDetailStatus14.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus14.setCreatedBy(userId);
		oDetailStatus14.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus14.setUpdatedBy(userId);
		oDetailStatus14.setLocationsId(locationId);
		oDetailStatus14.setStatus("I");
		oDetailStatus14.setDisplaySequence(9);
		oDetailStatus14.setDisplayName("Cancel Item");
		oDetailStatus14.setName("Cancel Item");
		oDetailStatus14.setOrderSourceGroupId(oSource);
		oDetailStatus14.setIsServerDriven((byte) 0);
		oDetailStatus14.setDescription("An item is Cancel.");

		em.persist(oDetailStatus14);

		OrderDetailStatus oDetailStatus15 = new OrderDetailStatus();

		oDetailStatus15.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus15.setCreatedBy(userId);
		oDetailStatus15.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus15.setUpdatedBy(userId);
		oDetailStatus15.setLocationsId(locationId);
		oDetailStatus15.setStatus("F");
		oDetailStatus15.setDisplaySequence(9);
		oDetailStatus15.setDisplayName("Raw Material");
		oDetailStatus15.setName("Raw Material");
		oDetailStatus15.setOrderSourceGroupId(oSource);
		oDetailStatus15.setIsServerDriven((byte) 0);
		oDetailStatus15.setDescription("An item is Raw Material.");

		em.persist(oDetailStatus15);

		OrderDetailStatus oDetailStatus16 = new OrderDetailStatus();

		oDetailStatus16.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus16.setCreatedBy(userId);
		oDetailStatus16.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus16.setUpdatedBy(userId);
		oDetailStatus16.setLocationsId(locationId);
		oDetailStatus16.setStatus("F");
		oDetailStatus16.setDisplaySequence(9);
		oDetailStatus16.setDisplayName("Item Cancelled");
		oDetailStatus16.setName("Item Cancelled");
		oDetailStatus16.setOrderSourceGroupId(oSource);
		oDetailStatus16.setIsServerDriven((byte) 0);
		oDetailStatus16.setDescription("An attribute is saved but not send to kitchen");

		em.persist(oDetailStatus16);

		OrderDetailStatus oDetailStatus17 = new OrderDetailStatus();

		oDetailStatus17.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus17.setCreatedBy(userId);
		oDetailStatus17.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus17.setUpdatedBy(userId);
		oDetailStatus17.setLocationsId(locationId);
		oDetailStatus17.setStatus("F");
		oDetailStatus17.setDisplaySequence(1);
		oDetailStatus17.setDisplayName("Item Requested");
		oDetailStatus17.setName("Item Requested");
		oDetailStatus17.setOrderSourceGroupId(oSource);
		oDetailStatus17.setIsServerDriven((byte) 0);
		oDetailStatus17.setStatusColour("#A6293C");
		oDetailStatus17.setDescription("Item Requested");

		em.persist(oDetailStatus17);

		OrderDetailStatus oDetailStatus18 = new OrderDetailStatus();

		oDetailStatus18.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus18.setCreatedBy(userId);
		oDetailStatus18.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus18.setUpdatedBy(userId);
		oDetailStatus18.setLocationsId(locationId);
		oDetailStatus18.setStatus("F");
		oDetailStatus18.setDisplaySequence(2);
		oDetailStatus18.setDisplayName("Item Partially Arrived");
		oDetailStatus18.setName("Item Partially Arrived");
		oDetailStatus18.setOrderSourceGroupId(oSource);
		oDetailStatus18.setIsServerDriven((byte) 0);
		oDetailStatus18.setStatusColour("#99916B");
		oDetailStatus18.setDescription("Item Partially Arrived");

		em.persist(oDetailStatus18);

		OrderDetailStatus oDetailStatus19 = new OrderDetailStatus();

		oDetailStatus19.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus19.setCreatedBy(userId);
		oDetailStatus19.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus19.setUpdatedBy(userId);
		oDetailStatus19.setLocationsId(locationId);
		oDetailStatus19.setStatus("F");
		oDetailStatus19.setDisplaySequence(3);
		oDetailStatus19.setDisplayName("Item Received");
		oDetailStatus19.setName("Item Received");
		oDetailStatus19.setOrderSourceGroupId(oSource);
		oDetailStatus19.setIsServerDriven((byte) 0);
		oDetailStatus19.setStatusColour("#F05123");
		oDetailStatus19.setDescription("Item Received");

		em.persist(oDetailStatus19);

		OrderDetailStatus oDetailStatus20 = new OrderDetailStatus();

		oDetailStatus20.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus20.setCreatedBy(userId);
		oDetailStatus20.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus20.setUpdatedBy(userId);
		oDetailStatus20.setLocationsId(locationId);
		oDetailStatus20.setStatus("F");
		oDetailStatus20.setDisplaySequence(4);
		oDetailStatus20.setDisplayName("Item Pending");
		oDetailStatus20.setName("Item Pending");
		oDetailStatus20.setOrderSourceGroupId(oSource);
		oDetailStatus20.setIsServerDriven((byte) 0);
		oDetailStatus20.setDescription("Item Pending");

		em.persist(oDetailStatus20);

		OrderDetailStatus oDetailStatus21 = new OrderDetailStatus();

		oDetailStatus21.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus21.setCreatedBy(userId);
		oDetailStatus21.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus21.setUpdatedBy(userId);
		oDetailStatus21.setLocationsId(locationId);
		oDetailStatus21.setStatus("F");
		oDetailStatus21.setDisplaySequence(5);
		oDetailStatus21.setDisplayName("Item Rejected");
		oDetailStatus21.setName("Item Rejected");
		oDetailStatus21.setOrderSourceGroupId(oSource);
		oDetailStatus21.setIsServerDriven((byte) 0);
		oDetailStatus21.setStatusColour("#9C3D3D");
		oDetailStatus21.setDescription("Item Rejected");

		em.persist(oDetailStatus21);

		OrderDetailStatus oDetailStatus22 = new OrderDetailStatus();

		oDetailStatus22.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus22.setCreatedBy(userId);
		oDetailStatus22.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus22.setUpdatedBy(userId);
		oDetailStatus22.setLocationsId(locationId);
		oDetailStatus22.setStatus("F");
		oDetailStatus22.setDisplaySequence(6);
		oDetailStatus22.setDisplayName("PO Item Requested");
		oDetailStatus22.setName("PO Item Requested");
		oDetailStatus22.setOrderSourceGroupId(oSource);
		oDetailStatus22.setIsServerDriven((byte) 0);
		oDetailStatus22.setDescription("PO Item Requested");

		em.persist(oDetailStatus22);

		OrderDetailStatus oDetailStatus23 = new OrderDetailStatus();

		oDetailStatus23.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus23.setCreatedBy(userId);
		oDetailStatus23.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus23.setUpdatedBy(userId);
		oDetailStatus23.setLocationsId(locationId);
		oDetailStatus23.setStatus("F");
		oDetailStatus23.setDisplaySequence(7);
		oDetailStatus23.setDisplayName("PO Item Partially Arrived");
		oDetailStatus23.setName("PO Item Partially Arrived");
		oDetailStatus23.setOrderSourceGroupId(oSource);
		oDetailStatus23.setIsServerDriven((byte) 0);
		oDetailStatus23.setStatusColour("#9C3D3D");
		oDetailStatus23.setDescription("PO Item Partially Arrived");

		em.persist(oDetailStatus23);

		OrderDetailStatus oDetailStatus24 = new OrderDetailStatus();

		oDetailStatus24.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus24.setCreatedBy(userId);
		oDetailStatus24.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus24.setUpdatedBy(userId);
		oDetailStatus24.setLocationsId(locationId);
		oDetailStatus24.setStatus("F");
		oDetailStatus24.setDisplaySequence(8);
		oDetailStatus24.setDisplayName("PO Item Received");
		oDetailStatus24.setName("PO Item Received");
		oDetailStatus24.setOrderSourceGroupId(oSource);
		oDetailStatus24.setIsServerDriven((byte) 0);
		oDetailStatus24.setDescription("PO Item Received");

		em.persist(oDetailStatus24);

		OrderDetailStatus oDetailStatus25 = new OrderDetailStatus();

		oDetailStatus25.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus25.setCreatedBy(userId);
		oDetailStatus25.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus25.setUpdatedBy(userId);
		oDetailStatus25.setLocationsId(locationId);
		oDetailStatus25.setStatus("F");
		oDetailStatus25.setDisplaySequence(9);
		oDetailStatus25.setDisplayName("PO Item Pending");
		oDetailStatus25.setName("PO Item Pending");
		oDetailStatus25.setOrderSourceGroupId(oSource);
		oDetailStatus25.setIsServerDriven((byte) 0);
		oDetailStatus25.setDescription("PO Item Pending");

		em.persist(oDetailStatus25);

		OrderDetailStatus oDetailStatus26 = new OrderDetailStatus();

		oDetailStatus26.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus26.setCreatedBy(userId);
		oDetailStatus26.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus26.setUpdatedBy(userId);
		oDetailStatus26.setLocationsId(locationId);
		oDetailStatus26.setStatus("F");
		oDetailStatus26.setDisplaySequence(9);
		oDetailStatus26.setDisplayName("PO Item Rejected");
		oDetailStatus26.setName("PO Item Rejected");
		oDetailStatus26.setOrderSourceGroupId(oSource);
		oDetailStatus26.setIsServerDriven((byte) 0);
		oDetailStatus26.setDescription("PO Item Rejected");

		em.persist(oDetailStatus26);

		OrderDetailStatus oDetailStatus27 = new OrderDetailStatus();

		oDetailStatus27.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus27.setCreatedBy(userId);
		oDetailStatus27.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus27.setUpdatedBy(userId);
		oDetailStatus27.setLocationsId(locationId);
		oDetailStatus27.setStatus("F");
		oDetailStatus27.setDisplaySequence(9);
		oDetailStatus27.setDisplayName("Request Item Allocated");
		oDetailStatus27.setName("Request Item Allocated");
		oDetailStatus27.setOrderSourceGroupId(oSource);
		oDetailStatus27.setIsServerDriven((byte) 0);
		oDetailStatus27.setDescription("Request Item Allocated");

		em.persist(oDetailStatus27);

		OrderDetailStatus oDetailStatus28 = new OrderDetailStatus();

		oDetailStatus28.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus28.setCreatedBy(userId);
		oDetailStatus28.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus28.setUpdatedBy(userId);
		oDetailStatus28.setLocationsId(locationId);
		oDetailStatus28.setStatus("F");
		oDetailStatus28.setDisplaySequence(9);
		oDetailStatus28.setDisplayName("Request In Process");
		oDetailStatus28.setName("Request In Process");
		oDetailStatus28.setOrderSourceGroupId(oSource);
		oDetailStatus28.setIsServerDriven((byte) 0);
		oDetailStatus28.setDescription("Request Item In Process");

		em.persist(oDetailStatus28);

		OrderDetailStatus oDetailStatus29 = new OrderDetailStatus();

		oDetailStatus29.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus29.setCreatedBy(userId);
		oDetailStatus29.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus29.setUpdatedBy(userId);
		oDetailStatus29.setLocationsId(locationId);
		oDetailStatus29.setStatus("F");
		oDetailStatus29.setDisplaySequence(9);
		oDetailStatus29.setDisplayName("Request Partially Processed");
		oDetailStatus29.setName("Request Partially Processed");
		oDetailStatus29.setOrderSourceGroupId(oSource);
		oDetailStatus29.setIsServerDriven((byte) 0);
		oDetailStatus29.setDescription("Request Partially Processed");

		em.persist(oDetailStatus29);

		OrderDetailStatus oDetailStatus30 = new OrderDetailStatus();

		oDetailStatus30.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus30.setCreatedBy(userId);
		oDetailStatus30.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus30.setUpdatedBy(userId);
		oDetailStatus30.setLocationsId(locationId);
		oDetailStatus30.setStatus("F");
		oDetailStatus30.setDisplaySequence(9);
		oDetailStatus30.setDisplayName("Item Forcefully Close");
		oDetailStatus30.setName("Item Forcefully Close");
		oDetailStatus30.setOrderSourceGroupId(oSource);
		oDetailStatus30.setIsServerDriven((byte) 0);
		oDetailStatus30.setDescription("Item Forcefully Close");

		em.persist(oDetailStatus30);

		OrderDetailStatus oDetailStatus31 = new OrderDetailStatus();
		oDetailStatus31.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus31.setCreatedBy(userId);
		oDetailStatus31.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus31.setUpdatedBy(userId);
		oDetailStatus31.setLocationsId(locationId);
		oDetailStatus31.setStatus("F");
		oDetailStatus31.setDisplaySequence(7);
		oDetailStatus31.setDisplayName("Physical Inventory Adjusted");
		oDetailStatus31.setName("Physical Inventory Adjusted");
		oDetailStatus31.setOrderSourceGroupId(oSource);
		oDetailStatus31.setIsServerDriven((byte) 1);
		oDetailStatus31.setStatusColour("#054552e");
		oDetailStatus31.setDescription("Physical Inventory Adjusted");
		em.persist(oDetailStatus31);

		OrderDetailStatus oDetailStatus34 = new OrderDetailStatus();
		oDetailStatus34.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus34.setCreatedBy(userId);
		oDetailStatus34.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus34.setUpdatedBy(userId);
		oDetailStatus34.setLocationsId(locationId);
		oDetailStatus34.setStatus("F");
		oDetailStatus34.setDisplaySequence(7);
		oDetailStatus34.setDisplayName("Item Sent");
		oDetailStatus34.setName("Item Sent");
		oDetailStatus34.setOrderSourceGroupId(oSource);
		oDetailStatus34.setIsServerDriven((byte) 1);
		oDetailStatus34.setStatusColour("#054552e");
		oDetailStatus34.setDescription("Item Sent");
		em.persist(oDetailStatus34);

		OrderDetailStatus oDetailStatus35 = new OrderDetailStatus();
		oDetailStatus35.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus35.setCreatedBy(userId);
		oDetailStatus35.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus35.setUpdatedBy(userId);
		oDetailStatus35.setLocationsId(locationId);
		oDetailStatus35.setStatus("F");
		oDetailStatus35.setDisplaySequence(7);
		oDetailStatus35.setDisplayName("Item Recalled");
		oDetailStatus35.setName("Item Recalled");
		oDetailStatus35.setOrderSourceGroupId(oSource);
		oDetailStatus35.setIsServerDriven((byte) 1);
		oDetailStatus35.setStatusColour("#054552e");
		oDetailStatus35.setDescription("Item Recalled");
		em.persist(oDetailStatus35);

		OrderDetailStatus oDetailStatus36 = new OrderDetailStatus();
		oDetailStatus36.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus36.setCreatedBy(userId);
		oDetailStatus36.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus36.setUpdatedBy(userId);
		oDetailStatus36.setLocationsId(locationId);
		oDetailStatus36.setStatus("F");
		oDetailStatus36.setDisplaySequence(7);
		oDetailStatus36.setDisplayName("New PO Inserted");
		oDetailStatus36.setName("New PO Inserted");
		oDetailStatus36.setOrderSourceGroupId(oSource);
		oDetailStatus36.setIsServerDriven((byte) 1);
		oDetailStatus36.setStatusColour("#054552e");
		oDetailStatus36.setDescription("New PO Inserted");
		em.persist(oDetailStatus36);

		OrderDetailStatus oDetailStatus37 = new OrderDetailStatus();
		oDetailStatus37.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus37.setCreatedBy(userId);
		oDetailStatus37.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus37.setUpdatedBy(userId);
		oDetailStatus37.setLocationsId(locationId);
		oDetailStatus37.setStatus("F");
		oDetailStatus37.setDisplaySequence(7);
		oDetailStatus37.setDisplayName("Item Ready");
		oDetailStatus37.setName("Item Ready");
		oDetailStatus37.setOrderSourceGroupId(oSource);
		oDetailStatus37.setIsServerDriven((byte) 1);
		oDetailStatus37.setStatusColour("#054552e");
		oDetailStatus37.setDescription("Item Ready");
		em.persist(oDetailStatus37);

		OrderDetailStatus oDetailStatus38 = new OrderDetailStatus();
		oDetailStatus38.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus38.setCreatedBy(userId);
		oDetailStatus38.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oDetailStatus38.setUpdatedBy(userId);
		oDetailStatus38.setLocationsId(locationId);
		oDetailStatus38.setStatus("F");
		oDetailStatus38.setDisplaySequence(7);
		oDetailStatus38.setDisplayName("Item Displayed");
		oDetailStatus38.setName("Item Displayed");
		oDetailStatus38.setOrderSourceGroupId(oSource);
		oDetailStatus38.setIsServerDriven((byte) 1);
		oDetailStatus38.setStatusColour("#054552e");
		oDetailStatus38.setDescription("Item Displayed");
		em.persist(oDetailStatus38);

	}

	/**
	 * Adds the contact preference constants.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 */
	void addContactPreferenceConstants(EntityManager em, String userId, String locationId)
	{
		ContactPreference contactP = new ContactPreference();
		contactP.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		contactP.setCreatedBy(userId);
		contactP.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		contactP.setUpdatedBy(userId);
		contactP.setLocationsId(locationId);
		contactP.setStatus("F");
		contactP.setDisplaySequence(1);
		contactP.setDisplayName("None");
		contactP.setName("None");
		contactP.setId(new StoreForwardUtility().generateUUID());
		em.persist(contactP);

	}

	/**
	 * Adds the request type constants.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 */
	void addRequestTypeConstants(EntityManager em, String userId, String locationId, HttpServletRequest httpRequest)
	{
		RequestType rType = new RequestType();
		rType.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rType.setCreatedBy(userId);
		rType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rType.setUpdatedBy(userId);
		rType.setLocationsId(locationId);
		rType.setStatus("F");
		rType.setDisplaySequence(1);
		rType.setDisplayName("None");
		rType.setRequestName("None");
		try
		{
			if (rType.getId() == null)
				rType.setId(new StoreForwardUtility().generateDynamicIntId(em, rType.getLocationsId(), httpRequest, "request_type"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(rType);

	}

	/**
	 * Adds the course constants.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param globalLocationId
	 *            the global location id
	 */
	void addCourseConstants(EntityManager em, String userId, String locationId, String globalLocationId)
	{
		Course course = new Course();

		course.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		course.setCreatedBy(userId);
		course.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		course.setUpdatedBy(userId);
		course.setLocationsId(locationId);
		course.setStatus("F");
		course.setDisplaySequence(1);
		course.setDisplayName("General");
		course.setCourseName("General");
		course.setGlobalCourseId(getCourse(em, course.getCourseName(), globalLocationId));
		course.setId(new StoreForwardUtility().generateUUID());
	
		em.persist(course);

	}

	/**
	 * Adds the employee operations.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 */
	void addEmployeeOperations(EntityManager em, String userId, String locationId, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code
		EmployeeOperation employeeOperation = new EmployeeOperation();

		employeeOperation.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation.setCreatedBy(userId);
		employeeOperation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation.setUpdatedBy(userId);
		employeeOperation.setLocationsId(locationId);
		employeeOperation.setStatus("F");
		employeeOperation.setDisplaySequence(1);
		employeeOperation.setOperationDisplayName("Clock In");
		employeeOperation.setOperationName("Clock In");
		employeeOperation.setHexCode("#525D76");
		employeeOperation.setImageUrl("clockIn.png");
		try
		{
			if (employeeOperation.getId() == null)
				employeeOperation.setId(new StoreForwardUtility().generateDynamicIntId(em, employeeOperation.getLocationsId(), httpRequest, "employee_operations"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(employeeOperation);

		EmployeeOperation employeeOperation1 = new EmployeeOperation();

		employeeOperation1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation1.setCreatedBy(userId);
		employeeOperation1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation1.setUpdatedBy(userId);
		employeeOperation1.setLocationsId(locationId);
		employeeOperation1.setStatus("F");
		employeeOperation1.setDisplaySequence(2);
		employeeOperation1.setOperationDisplayName("Clock Out");
		employeeOperation1.setOperationName("Clock Out");
		employeeOperation1.setHexCode("#dddD76");
		employeeOperation1.setImageUrl("clockOut.png");
		try
		{
			employeeOperation1.setId(new StoreForwardUtility().generateDynamicIntId(em, employeeOperation1.getLocationsId(), httpRequest, "employee_operations"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(employeeOperation1);

		EmployeeOperation employeeOperation2 = new EmployeeOperation();

		employeeOperation2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation2.setCreatedBy(userId);
		employeeOperation2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation2.setUpdatedBy(userId);
		employeeOperation2.setLocationsId(locationId);
		employeeOperation2.setStatus("F");
		employeeOperation2.setDisplaySequence(3);
		employeeOperation2.setOperationDisplayName("Break In");
		employeeOperation2.setOperationName("Break In");
		employeeOperation2.setHexCode("#e0d904");
		employeeOperation2.setImageUrl("");
		try
		{
			employeeOperation2.setId(new StoreForwardUtility().generateDynamicIntId(em, employeeOperation2.getLocationsId(), httpRequest, "employee_operations"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(employeeOperation2);

		EmployeeOperation employeeOperation3 = new EmployeeOperation();

		employeeOperation3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation3.setCreatedBy(userId);
		employeeOperation3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation3.setUpdatedBy(userId);
		employeeOperation3.setLocationsId(locationId);
		employeeOperation3.setStatus("F");
		employeeOperation3.setDisplaySequence(3);
		employeeOperation3.setOperationDisplayName("Break Out");
		employeeOperation3.setOperationName("Break Out");
		employeeOperation3.setHexCode("#ebeb1d");
		employeeOperation3.setImageUrl("");
		try
		{
			employeeOperation3.setId(new StoreForwardUtility().generateDynamicIntId(em, employeeOperation3.getLocationsId(), httpRequest, "employee_operations"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(employeeOperation3);

		EmployeeOperation employeeOperation4 = new EmployeeOperation();

		employeeOperation4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation4.setCreatedBy(userId);
		employeeOperation4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation4.setUpdatedBy(userId);
		employeeOperation4.setLocationsId(locationId);
		employeeOperation4.setStatus("F");
		employeeOperation4.setDisplaySequence(3);
		employeeOperation4.setOperationDisplayName("Open Register");
		employeeOperation4.setOperationName("Open Register");
		employeeOperation4.setHexCode("#ebsds1d");
		employeeOperation4.setImageUrl("");
		try
		{
			employeeOperation4.setId(new StoreForwardUtility().generateDynamicIntId(em, employeeOperation4.getLocationsId(), httpRequest, "employee_operations"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(employeeOperation4);

		EmployeeOperation employeeOperation5 = new EmployeeOperation();

		employeeOperation5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation5.setCreatedBy(userId);
		employeeOperation5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation5.setUpdatedBy(userId);
		employeeOperation5.setLocationsId(locationId);
		employeeOperation5.setStatus("F");
		employeeOperation5.setDisplaySequence(6);
		employeeOperation5.setOperationDisplayName("Paid In");
		employeeOperation5.setOperationName("Paid In");
		employeeOperation5.setHexCode("#ebsds1d");
		employeeOperation5.setImageUrl("");
		try
		{
			employeeOperation5.setId(new StoreForwardUtility().generateDynamicIntId(em, employeeOperation5.getLocationsId(), httpRequest, "employee_operations"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(employeeOperation5);

		EmployeeOperation employeeOperation6 = new EmployeeOperation();

		employeeOperation6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation6.setCreatedBy(userId);
		employeeOperation6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation6.setUpdatedBy(userId);
		employeeOperation6.setLocationsId(locationId);
		employeeOperation6.setStatus("F");
		employeeOperation6.setDisplaySequence(6);
		employeeOperation6.setOperationDisplayName("Paid Out");
		employeeOperation6.setOperationName("Paid Out");
		employeeOperation6.setHexCode("#ebsds1d");
		employeeOperation6.setImageUrl("");
		try
		{
			employeeOperation6.setId(new StoreForwardUtility().generateDynamicIntId(em, employeeOperation6.getLocationsId(), httpRequest, "employee_operations"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(employeeOperation6);

	}

	/**
	 * Gets the all functions.
	 *
	 * @param em
	 *            the em
	 * @return the all functions
	 */
	List<Function> getAllFunctions(EntityManager em)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Function> criteria = builder.createQuery(Function.class);
		Root<Function> r = criteria.from(Function.class);

		TypedQuery<Function> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(Function_.status), "D"), builder.notEqual(r.get(Function_.name), "Tip Settlement")));
		return query.getResultList();

	}

	/**
	 * Gets the all roles for location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the all roles for location id
	 */
	List<Role> getAllRolesForLocationId(EntityManager em, String locationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
		Root<Role> r = criteria.from(Role.class);
		TypedQuery<Role> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Role_.locationsId), locationId)));
		return query.getResultList();

	}

	/**
	 * Adds the roles to functions.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param createdBy
	 *            the created by
	 * @throws Exception
	 *             the exception
	 */
	void addRolesToFunctions(EntityManager em, String locationId, String createdBy) throws Exception
	{

		List<Function> functionsList = getAllFunctions(em);
		List<Role> rolesList = getAllRolesForLocationId(em, locationId);
		if (functionsList != null && functionsList.size() > 0)
		{

			if (rolesList != null && rolesList.size() > 0)
			{

				for (Role role : rolesList)
				{

					if (role != null && role.getRoleName() != null && role.getRoleName().equalsIgnoreCase("POS Customer") == false)
					{
						for (Function function : functionsList)
						{

							RolesToFunction rolesToFunction = new RolesToFunction();
							rolesToFunction.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							rolesToFunction.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							rolesToFunction.setCreatedBy(createdBy);
							rolesToFunction.setUpdatedBy(createdBy);
							rolesToFunction.setStatus("A");
							rolesToFunction.setRolesId(role.getId());
							rolesToFunction.setFunctionsId(function.getId());
							rolesToFunction.setId(new StoreForwardUtility().generateUUID());
							em.persist(rolesToFunction);
						}
					}

				}

			}
		}

	}

	/**
	 * Adds the category constant.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 * @param globalLocationId
	 *            the global location id
	 */
	void addCategoryConstant(EntityManager em, String locationId, String userId, String globalLocationId)
	{
		// todo shlok need to handle exception in below
		// modulise code

		Category category1 = new Category();
		category1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		category1.setCreatedBy(userId);
		category1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		category1.setUpdatedBy(userId);
		category1.setLocationsId(locationId);
		category1.setStatus("R");
		category1.setName("Raw Material");
		category1.setDisplayName("Raw Material");
		category1.setSortSequence(1);
		category1.setInventoryAccrual(0);
		category1.setIsinventoryAccrualOverriden(0);
		category1.setIsRealTimeUpdateNeeded(0);
		category1.setIsUpdateOverridden(0);
		category1.setGlobalCategoryId(getCategory(em, category1.getName(), globalLocationId));
		category1.setId(new StoreForwardUtility().generateUUID());
		em.persist(category1);
		em.getTransaction().commit();
		em.getTransaction().begin();
		
		String discountsId = getNoDiscountIdByLocationId(em, locationId);
		String printersId = getNoPrinterIdByLocationId(em, locationId);

		CategoryToDiscount categoryToDiscount1 = new CategoryToDiscount();

		categoryToDiscount1.setCategoryId(category1.getId());
		categoryToDiscount1.setDiscountsId(discountsId);
		categoryToDiscount1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		categoryToDiscount1.setCreatedBy(userId);
		categoryToDiscount1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		categoryToDiscount1.setUpdatedBy(userId);
		categoryToDiscount1.setStatus("A");
		categoryToDiscount1.setId(new StoreForwardUtility().generateUUID());
		em.persist(categoryToDiscount1);

		em.getTransaction().commit();
		em.getTransaction().begin();
		
		CategoryToPrinter categoryToPrinter1 = new CategoryToPrinter();

		categoryToPrinter1.setCategoryId(category1.getId());
		categoryToPrinter1.setPrintersId(printersId);
		categoryToPrinter1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		categoryToPrinter1.setCreatedBy(userId);
		categoryToPrinter1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		categoryToPrinter1.setUpdatedBy(userId);
		categoryToPrinter1.setStatus("A");
		categoryToPrinter1.setId(new StoreForwardUtility().generateUUID());
		em.persist(categoryToPrinter1);
		
		em.getTransaction().commit();
		em.getTransaction().begin();
		

		Category category2 = new Category();
		category2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		category2.setCreatedBy(userId);
		category2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		category2.setUpdatedBy(userId);
		category2.setLocationsId(locationId);
		category2.setStatus("F");
		category2.setName("Generic");
		category2.setDisplayName("Generic");
		category2.setSortSequence(2);
		category2.setInventoryAccrual(0);
		category2.setIsinventoryAccrualOverriden(0);
		category2.setIsRealTimeUpdateNeeded(0);
		category2.setIsUpdateOverridden(0);
		category2.setGlobalCategoryId(getCategory(em, category2.getName(), globalLocationId));
		category2.setId(new StoreForwardUtility().generateUUID());
		em.persist(category2);

		em.getTransaction().commit();
		em.getTransaction().begin();
		
		CategoryToDiscount categoryToDiscount2 = new CategoryToDiscount();

		categoryToDiscount2.setCategoryId(category2.getId());
		categoryToDiscount2.setDiscountsId(discountsId);
		categoryToDiscount2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		categoryToDiscount2.setCreatedBy(userId);
		categoryToDiscount2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		categoryToDiscount2.setUpdatedBy(userId);
		categoryToDiscount2.setStatus("A");
		categoryToDiscount2.setId(new StoreForwardUtility().generateUUID());
		em.persist(categoryToDiscount2);

		em.getTransaction().commit();
		em.getTransaction().begin();
		
		CategoryToPrinter categoryToPrinter2 = new CategoryToPrinter();

		categoryToPrinter2.setCategoryId(category2.getId());
		categoryToPrinter2.setPrintersId(printersId);
		categoryToPrinter2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		categoryToPrinter2.setCreatedBy(userId);
		categoryToPrinter2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		categoryToPrinter2.setUpdatedBy(userId);
		categoryToPrinter2.setStatus("A");
		categoryToPrinter2.setId(new StoreForwardUtility().generateUUID());
		em.persist(categoryToPrinter2);

		em.getTransaction().commit();
		em.getTransaction().begin();
		
		ItemsType itemsType = getItemType(em, "Sale Only");

		Item item1 = new Item();
		item1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		item1.setCreatedBy(userId);
		item1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		item1.setUpdatedBy(userId);
		item1.setLocationsId(locationId);
		item1.setStatus("F");
		item1.setName("Generic");
		item1.setDisplayName("Generic");
		item1.setShortName("Generic");
		item1.setPriceSelling(new BigDecimal(0));
		item1.setDisplaySequence(1);
		item1.setIsRealTimeUpdateNeeded(0);
		item1.setIsOnlineItem(0);
		item1.setIsinventoryAccrualOverriden(0);
		item1.setInventoryAccrual(0);
		item1.setIsinventoryAccrualOverriden(0);
		item1.setIsRealTimeUpdateNeeded(0);
		item1.setItemNumber("111");
		item1.setDistributionPrice(new BigDecimal(0));
		if (itemsType != null)
		{
			item1.setItemType(itemsType.getId());
			item1.setItemTypeName(itemsType.getName());
		}

		item1.setCourseId(getCourceId(em, "General", locationId));
		item1.setId(new StoreForwardUtility().generateUUID());
		em.persist(item1);

		CategoryItem categoryItem = new CategoryItem();
		categoryItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		categoryItem.setCreatedBy(userId);
		categoryItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		categoryItem.setUpdatedBy(userId);
		categoryItem.setStatus("A");
		categoryItem.setItemsId(item1.getId());
		categoryItem.setCategoryId(category2.getId());

		em.persist(categoryItem);
		em.getTransaction().commit();
		em.getTransaction().begin();
		

		ItemsToPrinter itemsToPrinter = new ItemsToPrinter();
		itemsToPrinter.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		itemsToPrinter.setCreatedBy(userId);
		itemsToPrinter.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		itemsToPrinter.setUpdatedBy(userId);
		itemsToPrinter.setStatus("A");
		itemsToPrinter.setItemsId(item1.getId());
		itemsToPrinter.setPrintersId(printersId);

		em.persist(itemsToPrinter);
		em.getTransaction().commit();
		em.getTransaction().begin();

		ItemsToDiscount itemsToDiscount = new ItemsToDiscount();
		itemsToDiscount.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		itemsToDiscount.setCreatedBy(userId);
		itemsToDiscount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		itemsToDiscount.setUpdatedBy(userId);
		itemsToDiscount.setStatus("A");
		itemsToDiscount.setItemsId(item1.getId());
		itemsToDiscount.setDiscountsId(discountsId);
		itemsToDiscount.setId(new StoreForwardUtility().generateUUID());
		em.persist(itemsToDiscount);
		em.getTransaction().commit();
		em.getTransaction().begin();

	}

	/**
	 * Gets the no discount id by location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the no discount id by location id
	 */
	private String getNoDiscountIdByLocationId(EntityManager em, String locationId)
	{
		String discoutId = null;

		TypedQuery<Discount> query = em.createQuery("select d from Discount d where d.locationsId = ? and d.status != 'D'  and d.name ='No Discount' ", Discount.class).setParameter(1, locationId);
		Discount discount = query.getSingleResult();
		if (discount != null)
		{
			discoutId = discount.getId();
			return discoutId;
		}
		return discoutId;
	}

	/**
	 * Gets the no printer id by location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the no printer id by location id
	 */
	private String getNoPrinterIdByLocationId(EntityManager em, String locationId)
	{
		String printerId = null;
		TypedQuery<Printer> query = em.createQuery("select p from Printer p where p.locationsId ='" + locationId + "' and p.status != 'D' and p.printersName ='No Printer' ", Printer.class);
		Printer printer = query.getSingleResult();
		if (printer != null)
		{
			printerId = printer.getId();
			 if(printerId != null && (printerId.length()==0 || printerId.equals("0"))){return null;}else{	return printerId;}
		}
		 if(printerId != null && (printerId.length()==0 || printerId.equals("0"))){return null;}else{	return printerId;}
	}

	/**
	 * Adds the email constant.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 */
	void addSMSTemplateConstant(EntityManager em, String locationId, String userId)
	{

		SMSTemplate sms1 = new SMSTemplate();
		sms1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		sms1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		sms1.setCreatedBy(userId);
		sms1.setUpdatedBy(userId);
		sms1.setLocationId(locationId);
		sms1.setTemplateName("Order Placed");
		sms1.setTemplateDisplayName("Order Placed");
		sms1.setTemplateText("Thank you for placing your order with <BusinessName>.Your Order <OrderNumber>.");
		sms1.setTemplateValue("Order Placed");
		sms1.setStatus("A");
		em.persist(sms1);

		SMSTemplate sms2 = new SMSTemplate();
		sms2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		sms2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		sms2.setCreatedBy(userId);
		sms2.setUpdatedBy(userId);
		sms2.setLocationId(locationId);
		sms2.setTemplateName("Signup");
		sms2.setTemplateDisplayName("Signup");
		sms2.setTemplateText("<BusinessName> - Thank you for signing up.");
		sms2.setTemplateValue("Signup");
		sms2.setStatus("A");
		em.persist(sms2);

		SMSTemplate sms3 = new SMSTemplate();
		sms3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		sms3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		sms3.setCreatedBy(userId);
		sms3.setUpdatedBy(userId);
		sms3.setLocationId(locationId);
		sms3.setTemplateName("Check Presented");
		sms3.setTemplateDisplayName("Check Presented");
		sms3.setTemplateText("This <BalDue> amount has to paid to <BusinessName>.");
		sms3.setTemplateValue("Check Presented");
		sms3.setStatus("A");
		em.persist(sms3);

		SMSTemplate sms4 = new SMSTemplate();
		sms4.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		sms4.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		sms4.setCreatedBy(userId);
		sms4.setUpdatedBy(userId);
		sms4.setLocationId(locationId);
		sms4.setTemplateName("Order Confirmation SMS");
		sms4.setTemplateDisplayName("Order Confirmation SMS");
		sms4.setTemplateText("Order <OrderNumber> <OrderSourceGroup>-<OrderSource> received. Name : <GuestName> Phone: <GuestPhone> Sched on <SchedDateTime>");
		sms4.setTemplateValue("Order Confirmation SMS");
		sms4.setStatus("A");
		em.persist(sms4);
	}

	/**
	 * Adds the email constant.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 */
	void addEmailConstant(EntityManager em, String locationId, String userId)
	{

		// adding template
		EmailTemplate emailTemplate = addEmailTemplate(em, locationId, "RESERVATION_CREATED", "BUSINESS_NAME : Reservation Confirmation.",
				"<html> <head></head>  <body>   <span style=\"font-size:9.0pt;font-family:&quot;Verdana&quot;,&quot;sans-serif&quot;; color:#1F497D\">Dear CUSTOMER_FIRST_NAME,<br /><br />Your reservation at <b>BUSINESS_NAME</b> is confirmed!<br /><br /><ins><b>Reservation Details:</b></ins><br /><br />    <table>     <tbody>      <tr>       <td>Name:</td>       <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>CUSTOMER_FULL_NAME</b></td>      </tr>      <tr>       <td>Date:</td>       <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>RESERVATION_DATE</b></td>      </tr>       <tr>       <td>Time:</td>       <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>RESERVATION_TIME</b></td>     </tr>      <tr>       <td>Party Size:</td>       <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>GUEST_COUNT</b></td>      </tr>     </tbody>    </table> CANCEL_RESERVATION_URL<br /><b> BUSINESS_NAME<br />BUSINESS_ADDRESS</b><br /><br /><a href=\"BUSINESS_WEBSITE_URL\">See menus, map &amp; more &gt;</a><br /><br />Have a fantastic experience!!<br /><br />Thank you,<br />Team BUSINESS_NAME.</span><br><div align=\"center\">Please note: This email was sent from a notification-only address that cannot accept incoming email. Please do not reply to this message.</div>	  </body></html>",
				"A", userId);
		addBusinessEmailSetting(em, locationId, emailTemplate.getId(), userId);
		emailTemplate = addEmailTemplate(em, locationId, "RESERVATION_UPDATED", "BUSINESS_NAME : Reservation Updation.",
				"<html> <head></head>  <body>   <span style=\"font-size:9.0pt;font-family:&quot;Verdana&quot;,&quot;sans-serif&quot;; color:#1F497D\">Dear CUSTOMER_FIRST_NAME,<br /><br />Your reservation at <b>BUSINESS_NAME</b> is confirmed!<br /><br /><ins><b>Reservation Details:</b></ins><br /><br />    <table>     <tbody>      <tr>       <td>Name:</td>       <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>CUSTOMER_FULL_NAME</b></td>      </tr>      <tr>       <td>Date:</td>       <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>RESERVATION_DATE</b></td>      </tr>       <tr>       <td>Time:</td>       <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>RESERVATION_TIME</b></td>     </tr>      <tr>       <td>Party Size:</td>       <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>GUEST_COUNT</b></td>      </tr>     </tbody>    </table> CANCEL_RESERVATION_URL<br /><b> BUSINESS_NAME<br />BUSINESS_ADDRESS</b><br /><br /><a href=\"BUSINESS_WEBSITE_URL\">See menus, map &amp; more &gt;</a><br /><br />Have a fantastic experience!!<br /><br />Thank you,<br />Team BUSINESS_NAME.</span>  <br><div align=\"center\">Please note: This email was sent from a notification-only address that cannot accept incoming email. Please do not reply to this message.</div>	</body></html>",
				"A", userId);
		addBusinessEmailSetting(em, locationId, emailTemplate.getId(), userId);

		emailTemplate = addEmailTemplate(em, locationId, "RESERVATION_CANCELLED", "BUSINESS_NAME : Reservation Cancelled",
				"<html> <head></head>  <body>   <span style=\"font-size:9.0pt;font-family:&quot;Verdana&quot;,&quot;sans-serif&quot;; color:#1F497D\">Dear CUSTOMER_FIRST_NAME,<br><br>Your reservation at <b>BUSINESS_NAME</b> has been canceled successfully as per your request.<br><br> BUSINESS_NAME<br>BUSINESS_ADDRESS</b><br><br><a href=BUSINESS_WEBSITE_URL>See menus, map & more ></a><br><br>Thank you,<br>Team BUSINESS_NAME.</span><br><div align=\"center\">Please note: This email was sent from a notification-only address that cannot accept incoming email. Please do not reply to this message.</div> <br><div align=\"center\">Please note: This email was sent from a notification-only address that cannot accept incoming email. Please do not reply to this message.</div>		</body></html>",
				"A", userId);
		addBusinessEmailSetting(em, locationId, emailTemplate.getId(), userId);
		emailTemplate = addEmailTemplate(em, locationId, "ORDER_CONFIRMATION", "Order Confirmation", "ORDER_CONFIRMATION_EMAIL_STRING", "A", userId);
		addBusinessEmailSetting(em, locationId, emailTemplate.getId(), userId);

		// for payment receipt
		emailTemplate = addEmailTemplate(em, locationId, "ORDER_PAYMENT_CONFIRMATION", "Order Payment Confirmation", "ORDER_TRANSACTION_EMAIL_STRING", "A", userId);
		addBusinessEmailSetting(em, locationId, emailTemplate.getId(), userId);

		emailTemplate = addEmailTemplate(em, locationId, "REQUEST_ORDER_CONFIRMATION", "'Purchase Order", "REQUEST_ORDER_CONFIRMATION_STRING", "A", userId);
		addBusinessEmailSetting(em, locationId, emailTemplate.getId(), userId);

		emailTemplate = addEmailTemplate(em, locationId, "EOD_SETTLEDMENT_CONFIRMATION", "End Of Day Summary", "EOD_SETTLEDMENT_BODY_STRING", "A", userId);
		addBusinessEmailSetting(em, locationId, emailTemplate.getId(), userId);

		emailTemplate = addEmailTemplate(em, locationId, "EOD_TIP_SETTLEMENT_BODY_STRING", "Employees not clocked out", "EOD_TIP_SETTLEMENT_BODY_STRING", "A", userId);
		addBusinessEmailSetting(em, locationId, emailTemplate.getId(), userId);

		// adding smtp config
		addSMTPConfig(em, locationId, "AKIAII6OYCTIR5LOAHNA", "ApfXrWLjVDS96VGb1CBSPrH95LqZsNlkIG5qq4hULxdI", "587", "email-smtp.us-east-1.amazonaws.com", "wecare@nirvanaxp.com", "A", userId);

	}

	/**
	 * Adds the business email setting.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param templateId
	 *            the template id
	 * @param userId
	 *            the user id
	 */
	private void addBusinessEmailSetting(EntityManager em, String locationId, int templateId, String userId)
	{
		BusinessEmailSetting businessEmailSetting = new BusinessEmailSetting();
		businessEmailSetting.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		businessEmailSetting.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		businessEmailSetting.setCreatedBy(userId);
		businessEmailSetting.setUpdatedBy(userId);
		businessEmailSetting.setLocationId(locationId);
		businessEmailSetting.setEmailTemplateId(templateId);
		businessEmailSetting.setStatus("A");

		em.persist(businessEmailSetting);

	}

	/**
	 * Adds the email template.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param operationName
	 *            the operation name
	 * @param emailSubject
	 *            the email subject
	 * @param emailBody
	 *            the email body
	 * @param status
	 *            the status
	 * @param userId
	 *            the user id
	 * @return the email template
	 */
	private EmailTemplate addEmailTemplate(EntityManager em, String locationId, String operationName, String emailSubject, String emailBody, String status, String userId)
	{
		EmailTemplate emailTemplate = new EmailTemplate();
		emailTemplate.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		emailTemplate.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		emailTemplate.setCreatedBy(userId);
		emailTemplate.setUpdatedBy(userId);
		emailTemplate.setLocationId(locationId);
		emailTemplate.setOperationName(operationName);
		emailTemplate.setEmailSubject(emailSubject);
		emailTemplate.setEmailBody(emailBody);
		emailTemplate.setStatus(status);

		try {
			em.persist(emailTemplate);
		} catch (Exception e) {
			logger.severe(e);
		}

		return emailTemplate;
	}

	/**
	 * Adds the SMTP config.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param smtpUserName
	 *            the smtp user name
	 * @param smtpPassword
	 *            the smtp password
	 * @param smtpPort
	 *            the smtp port
	 * @param smtpHost
	 *            the smtp host
	 * @param senderEmail
	 *            the sender email
	 * @param status
	 *            the status
	 * @param userId
	 *            the user id
	 */
	private void addSMTPConfig(EntityManager em, String locationId, String smtpUserName, String smtpPassword, String smtpPort, String smtpHost, String senderEmail, String status, String userId)
	{
		SmtpConfig smtpConfig = new SmtpConfig();
		smtpConfig.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smtpConfig.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		smtpConfig.setCreatedBy(userId);
		smtpConfig.setUpdatedBy(userId);
		smtpConfig.setLocationId(locationId);
		smtpConfig.setSmtpUsername(smtpUserName);
		smtpConfig.setSmtpPassword(smtpPassword);
		smtpConfig.setSmtpPort(smtpPort);
		smtpConfig.setSmtpHost(smtpHost);
		smtpConfig.setSenderEmail(senderEmail);
		smtpConfig.setStatus(status);

		em.persist(smtpConfig);

	}

	/**
	 * Adds the batch detail.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 * @throws Exception 
	 */
	void addBatchDetail(EntityManager em, String locationId, String userId,HttpServletRequest request) throws Exception
	{
		BatchDetail batchDetail = new BatchDetail();
		batchDetail.setLocationId(locationId);
		batchDetail.setStatus("A");
		batchDetail.setUpdatedBy(userId);
		batchDetail.setStartTime(new TimezoneTime().getGMTTimeInMilis());
		try
		{
			batchDetail.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		batchDetail.setIsTipCalculated("N");
		batchDetail.setId(locationId+"-"+ new StoreForwardUtility().getAndUpdateCountOfTableIndex(em, locationId, "batch_detail", true));
		batchDetail.setDayOfYear(new StoreForwardUtility().getDayOfYear(locationId, em));
		
  
		 
		em.persist(batchDetail);

	}

	/**
	 * Adds the sales tax.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 * @param globalLocationId
	 *            the global location id
	 */
	void addSalesTax(EntityManager em, String locationId, String userId, String globalLocationId, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code
		SalesTax salesTax = new SalesTax();
		salesTax.setLocationsId(locationId);
		salesTax.setStatus("I");
		salesTax.setUpdatedBy(userId);
		salesTax.setCreated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		salesTax.setUpdated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		salesTax.setDisplayName("Gratuity");
		salesTax.setIsItemSpecific(0);
		salesTax.setNumberOfPeople(0);
		salesTax.setRate(new BigDecimal(0));
		salesTax.setTaxId(null);
		salesTax.setStatus("F");
		salesTax.setTaxName("Gratuity");
		salesTax.setCreatedBy(userId);
		salesTax.setGlobalId(getSalesTax(em, salesTax.getTaxName(), globalLocationId));
		try
		{
			if (salesTax.getId() == null)
				salesTax.setId(new StoreForwardUtility().generateDynamicIntId(em, salesTax.getLocationsId(), httpRequest, "sales_tax"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(salesTax);

		SalesTax tax1 = new SalesTax();
		tax1.setLocationsId(locationId);
		tax1.setUpdatedBy(userId);
		tax1.setCreated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		tax1.setUpdated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		tax1.setDisplayName("Tax1");
		tax1.setIsItemSpecific(0);
		tax1.setNumberOfPeople(0);
		tax1.setRate(new BigDecimal(0));
		tax1.setTaxId(null);
		tax1.setStatus("A");
		tax1.setTaxName("Tax1");
		tax1.setCreatedBy(userId);
		tax1.setGlobalId(getSalesTax(em, tax1.getTaxName(), globalLocationId));
		try
		{
			tax1.setId(new StoreForwardUtility().generateDynamicIntId(em, tax1.getLocationsId(), httpRequest, "sales_tax"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(tax1);

		SalesTax tax2 = new SalesTax();
		tax2.setLocationsId(locationId);
		tax2.setUpdatedBy(userId);
		tax2.setCreated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		tax2.setUpdated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		tax2.setDisplayName("Tax2");
		tax2.setIsItemSpecific(0);
		tax2.setNumberOfPeople(0);
		tax2.setRate(new BigDecimal(0));
		tax2.setTaxId(null);
		tax2.setStatus("A");
		tax2.setTaxName("Tax2");
		tax2.setCreatedBy(userId);
		tax2.setGlobalId(getSalesTax(em, tax2.getTaxName(), globalLocationId));
		try
		{
			tax2.setId(new StoreForwardUtility().generateDynamicIntId(em, tax2.getLocationsId(), httpRequest, "sales_tax"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(tax2);

		SalesTax tax3 = new SalesTax();
		tax3.setLocationsId(locationId);
		tax3.setUpdatedBy(userId);
		tax3.setCreated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		tax3.setUpdated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		tax3.setDisplayName("Tax3");
		tax3.setIsItemSpecific(0);
		tax3.setNumberOfPeople(0);
		tax3.setRate(new BigDecimal(0));
		tax3.setTaxId(null);
		tax3.setStatus("A");
		tax3.setTaxName("Tax3");
		tax3.setCreatedBy(userId);
		tax3.setGlobalId(getSalesTax(em, tax3.getTaxName(), globalLocationId));
		try
		{
			tax3.setId(new StoreForwardUtility().generateDynamicIntId(em, tax3.getLocationsId(), httpRequest, "sales_tax"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(tax3);

		SalesTax tax4 = new SalesTax();
		tax4.setLocationsId(locationId);
		tax4.setUpdatedBy(userId);
		tax4.setCreated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		tax4.setUpdated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		tax4.setDisplayName("Tax4");
		tax4.setIsItemSpecific(0);
		tax4.setNumberOfPeople(0);
		tax4.setRate(new BigDecimal(0));
		tax4.setTaxId(null);
		tax4.setStatus("A");
		tax4.setTaxName("Tax4");
		tax4.setCreatedBy(userId);
		tax4.setGlobalId(getSalesTax(em, tax4.getTaxName(), globalLocationId));
		try
		{
			tax4.setId(new StoreForwardUtility().generateDynamicIntId(em, tax4.getLocationsId(), httpRequest, "sales_tax"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(tax4);

		SalesTax salesTax2 = new SalesTax();
		salesTax2.setLocationsId(locationId);
		salesTax2.setStatus("I");
		salesTax2.setUpdatedBy(userId);
		salesTax2.setCreated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		salesTax2.setUpdated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		salesTax2.setDisplayName("Service Charge");
		salesTax2.setIsItemSpecific(0);
		salesTax2.setNumberOfPeople(0);
		salesTax2.setRate(new BigDecimal(0));
		salesTax2.setTaxId(null);
		salesTax2.setStatus("F");
		salesTax2.setTaxName("Service Charge");
		salesTax2.setCreatedBy(userId);
		salesTax2.setGlobalId(getSalesTax(em, salesTax2.getTaxName(), globalLocationId));
		try
		{
			salesTax2.setId(new StoreForwardUtility().generateDynamicIntId(em, salesTax2.getLocationsId(), httpRequest, "sales_tax"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(salesTax2);

	}

	/**
	 * Adds the delivery option.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 */
	void addDeliveryOption(EntityManager em, String locationId, String userId, HttpServletRequest httpRequest)
	{
		DeliveryOption deliveryOption = new DeliveryOption();
		deliveryOption.setLocationId(locationId);
		deliveryOption.setUpdatedBy(userId);
		deliveryOption.setCreated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		deliveryOption.setUpdated(new Date((new TimezoneTime().getGMTTimeInMilis())));
		deliveryOption.setDisplayName("No Charge");
		deliveryOption.setDisplaySequence(1);
		deliveryOption.setOptionTypeId(0);
		deliveryOption.setAmount(new BigDecimal(0));
		deliveryOption.setStatus("A");
		deliveryOption.setName("No Charge");
		deliveryOption.setCreatedBy(userId);
		try
		{
			if (deliveryOption.getId() == null)
				deliveryOption.setId(new StoreForwardUtility().generateDynamicIntId(em, deliveryOption.getLocationId(), httpRequest, "delivery_option"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(deliveryOption);

	}

	/**
	 * Gets the payment type.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @return the payment type
	 */
	private PaymentType getPaymentType(EntityManager em, String name)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<PaymentType> cl = builder.createQuery(PaymentType.class);
		Root<PaymentType> l = cl.from(PaymentType.class);
		TypedQuery<PaymentType> query = em.createQuery(cl.select(l).where(builder.equal(l.get(PaymentType_.name), name)));
		return query.getSingleResult();

	}

	/**
	 * Adds the roles.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 */
	void addRoles(EntityManager em, String locationId, String userId, HttpServletRequest httpRequest)
	{
		Role role = new Role();
		role.setLocationsId(locationId);
		role.setStatus("A");
		role.setUpdatedBy(userId);
		role.setCreatedBy(userId);
		role.setDisplayName("No Category");
		role.setRoleName("No Category");
		role.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		role.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		try
		{
			if (role.getId() == null)
				role.setId(new StoreForwardUtility().generateDynamicIntId(em, role.getLocationsId(), httpRequest, "roles"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(role);

	}

	/**
	 * Gets the category.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param globalLocationId
	 *            the global location id
	 * @return the category
	 */
	String getCategory(EntityManager em, String name, String globalLocationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		Root<Category> r = criteria.from(Category.class);
		TypedQuery<Category> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Category_.locationsId), globalLocationId), builder.equal(r.get(Category_.name), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			// todo shlok need to handle exception in below

		}
		return null;
	}

	/**
	 * Gets the cource id.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param globalLocationId
	 *            the global location id
	 * @return the cource id
	 */
	String getCourceId(EntityManager em, String name, String globalLocationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
		Root<Course> r = criteria.from(Course.class);
		TypedQuery<Course> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Course_.locationsId), globalLocationId), builder.equal(r.get(Course_.courseName), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			// todo shlok need to handle exception in below

		}
		return null;
	}

	/**
	 * Gets the item type.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @return the item type
	 */
	ItemsType getItemType(EntityManager em, String name)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ItemsType> criteria = builder.createQuery(ItemsType.class);
		Root<ItemsType> r = criteria.from(ItemsType.class);
		TypedQuery<ItemsType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsType_.name), name)));
		try
		{
			return query.getSingleResult();
		}
		catch (Exception e)
		{

			// todo shlok need to handle exception in below
		}
		return null;
	}

	/**
	 * Gets the sales tax.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param globalLocationId
	 *            the global location id
	 * @return the sales tax
	 */
	String getSalesTax(EntityManager em, String name, String globalLocationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
		Root<SalesTax> r = criteria.from(SalesTax.class);
		TypedQuery<SalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(SalesTax_.locationsId), globalLocationId), builder.equal(r.get(SalesTax_.taxName), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{

			// todo shlok need to handle exception in below
		}
		return null;
	}

	/**
	 * Gets the order source group.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param locationId
	 *            the location id
	 * @return the order source group
	 */
	String getOrderSourceGroup(EntityManager em, String name, String locationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
		Root<OrderSourceGroup> r = criteria.from(OrderSourceGroup.class);
		TypedQuery<OrderSourceGroup> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceGroup_.locationsId), locationId), builder.equal(r.get(OrderSourceGroup_.name), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return null;
	}

	/**
	 * Gets the order source.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param globalLocationId
	 *            the global location id
	 * @return the order source
	 */
	String getOrderSource(EntityManager em, String name, String globalLocationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
		Root<OrderSource> r = criteria.from(OrderSource.class);
		TypedQuery<OrderSource> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSource_.locationsId), globalLocationId), builder.equal(r.get(OrderSource_.name), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			// todo shlok need to handle exception in below

		}
		return null;
	}

	/**
	 * Gets the course.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param globalLocationId
	 *            the global location id
	 * @return the course
	 */
	String getCourse(EntityManager em, String name, String globalLocationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
		Root<Course> r = criteria.from(Course.class);
		TypedQuery<Course> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Course_.locationsId), globalLocationId), builder.equal(r.get(Course_.courseName), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			// todo shlok need to handle exception in below

		}
		return null;
	}

	/**
	 * Gets the printers type.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param globalLocationId
	 *            the global location id
	 * @return the printers type
	 */
	String getPrintersType(EntityManager em, String name, String globalLocationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<PrintersType> criteria = builder.createQuery(PrintersType.class);
		Root<PrintersType> r = criteria.from(PrintersType.class);
		TypedQuery<PrintersType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(PrintersType_.locationsId), globalLocationId), builder.equal(r.get(PrintersType_.name), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{

			// todo shlok need to handle exception in below
		}
		return null;
	}

	/**
	 * Gets the printers interface.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param globalLocationId
	 *            the global location id
	 * @return the printers interface
	 */
	String getPrintersInterface(EntityManager em, String name, String globalLocationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<PrintersInterface> criteria = builder.createQuery(PrintersInterface.class);
		Root<PrintersInterface> r = criteria.from(PrintersInterface.class);
		TypedQuery<PrintersInterface> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(PrintersInterface_.locationsId), globalLocationId), builder.equal(r.get(PrintersInterface_.name), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{

			// todo shlok need to handle exception in below
		}
		return null;
	}

	/**
	 * Gets the printers.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param globalLocationId
	 *            the global location id
	 * @return the printers
	 */
	String getPrinters(EntityManager em, String name, String globalLocationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
		Root<Printer> r = criteria.from(Printer.class);
		TypedQuery<Printer> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Printer_.locationsId), globalLocationId), builder.equal(r.get(Printer_.printersName), name)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			// todo shlok need to handle exception in below

		}
		return null;
	}

	/**
	 * Adds the order status for inventory.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param orderSourceGroup
	 *            the order source group
	 */
	void addOrderStatusForInventory(EntityManager em, String userId, String locationId, String orderSourceGroup, HttpServletRequest httpRequest)
	{
		// todo shlok need to handle exception in below
		// modulise code
		OrderStatus oStatus5 = new OrderStatus();
		oStatus5.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus5.setCreatedBy(userId);
		oStatus5.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus5.setUpdatedBy(userId);
		oStatus5.setLocationsId(locationId);
		oStatus5.setStatus("F");
		oStatus5.setDisplaySequence(1);
		oStatus5.setDisplayName("Request Created");
		oStatus5.setName("Request Created");
		oStatus5.setOrderSourceGroupId(orderSourceGroup);
		oStatus5.setIsServerDriven((byte) 1);
		oStatus5.setStatusColour("#c73030");
		oStatus5.setDescription("Request created for inventory");
		try
		{
			if (oStatus5.getId() == null)
				oStatus5.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus5.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus5);

		OrderStatus oStatus6 = new OrderStatus();
		oStatus6.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus6.setCreatedBy(userId);
		oStatus6.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus6.setUpdatedBy(userId);
		oStatus6.setLocationsId(locationId);
		oStatus6.setStatus("F");
		oStatus6.setDisplaySequence(2);
		oStatus6.setDisplayName("Request Saved");
		oStatus6.setName("Request Saved");
		oStatus6.setOrderSourceGroupId(orderSourceGroup);
		oStatus6.setIsServerDriven((byte) 1);
		oStatus6.setStatusColour("#7535a6");
		oStatus6.setDescription("Request saved for inventory");
		try
		{
			oStatus6.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus6.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus6);

		OrderStatus oStatus7 = new OrderStatus();
		oStatus7.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus7.setCreatedBy(userId);
		oStatus7.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus7.setUpdatedBy(userId);
		oStatus7.setLocationsId(locationId);
		oStatus7.setStatus("F");
		oStatus7.setDisplaySequence(3);
		oStatus7.setDisplayName("Request Cancelled");
		oStatus7.setName("Request Cancelled");
		oStatus7.setOrderSourceGroupId(orderSourceGroup);
		oStatus7.setIsServerDriven((byte) 1);
		oStatus7.setStatusColour("#00652E");
		oStatus7.setDescription("Request order Cancelled");
		try
		{
			oStatus7.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus7.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus7);

		OrderStatus oStatus10 = new OrderStatus();
		oStatus10.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus10.setCreatedBy(userId);
		oStatus10.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus10.setUpdatedBy(userId);
		oStatus10.setLocationsId(locationId);
		oStatus10.setStatus("F");
		oStatus10.setDisplaySequence(6);
		oStatus10.setDisplayName("Request Sent");
		oStatus10.setName("Request Sent");
		oStatus10.setOrderSourceGroupId(orderSourceGroup);
		oStatus10.setIsServerDriven((byte) 1);
		oStatus10.setStatusColour("#1255e6");
		oStatus10.setDescription("Request order Published");
		try
		{
			oStatus10.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus10.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus10);

		OrderStatus oStatus11 = new OrderStatus();
		oStatus11.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus11.setCreatedBy(userId);
		oStatus11.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus11.setUpdatedBy(userId);
		oStatus11.setLocationsId(locationId);
		oStatus11.setStatus("F");
		oStatus11.setDisplaySequence(7);
		oStatus11.setDisplayName("Request In Process");
		oStatus11.setName("Request In Process");
		oStatus11.setOrderSourceGroupId(orderSourceGroup);
		oStatus11.setIsServerDriven((byte) 1);
		oStatus11.setStatusColour("#00652e");
		oStatus11.setDescription("Requeste Order In Process");
		try
		{
			oStatus11.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus11.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus11);

		OrderStatus oStatus12 = new OrderStatus();
		oStatus12.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus12.setCreatedBy(userId);
		oStatus12.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus12.setUpdatedBy(userId);
		oStatus12.setLocationsId(locationId);
		oStatus12.setStatus("F");
		oStatus12.setDisplaySequence(8);
		oStatus12.setDisplayName("Request Received");
		oStatus12.setName("Request Received");
		oStatus12.setOrderSourceGroupId(orderSourceGroup);
		oStatus12.setIsServerDriven((byte) 1);
		oStatus12.setStatusColour("#C1C2C8");
		oStatus12.setDescription("Request Order Received");
		try
		{
			oStatus12.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus12.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus12);

		OrderStatus oStatus13 = new OrderStatus();
		oStatus13.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus13.setCreatedBy(userId);
		oStatus13.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus13.setUpdatedBy(userId);
		oStatus13.setLocationsId(locationId);
		oStatus13.setStatus("F");
		oStatus13.setDisplaySequence(1);
		oStatus13.setDisplayName("Request Partially Received");
		oStatus13.setName("Request Partially Received");
		oStatus13.setOrderSourceGroupId(orderSourceGroup);
		oStatus13.setIsServerDriven((byte) 1);
		oStatus13.setStatusColour("#c73030");
		oStatus13.setDescription("Request Partially Received");
		try
		{
			oStatus13.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus13.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus13);

		OrderStatus oStatus14 = new OrderStatus();
		oStatus14.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus14.setCreatedBy(userId);
		oStatus14.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus14.setUpdatedBy(userId);
		oStatus14.setLocationsId(locationId);
		oStatus14.setStatus("F");
		oStatus14.setDisplaySequence(2);
		oStatus14.setDisplayName("Request Rejected");
		oStatus14.setName("Request Rejected");
		oStatus14.setOrderSourceGroupId(orderSourceGroup);
		oStatus14.setIsServerDriven((byte) 1);
		oStatus14.setStatusColour("#7535a6");
		oStatus14.setDescription("Request Rejected");
		try
		{
			oStatus14.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus14.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus14);

		OrderStatus oStatus15 = new OrderStatus();
		oStatus15.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus15.setCreatedBy(userId);
		oStatus15.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus15.setUpdatedBy(userId);
		oStatus15.setLocationsId(locationId);
		oStatus15.setStatus("F");
		oStatus15.setDisplaySequence(3);
		oStatus15.setDisplayName("Request Forcefully Closed");
		oStatus15.setName("Request Forcefully Closed");
		oStatus15.setOrderSourceGroupId(orderSourceGroup);
		oStatus15.setIsServerDriven((byte) 1);
		oStatus15.setStatusColour("#00652E");
		oStatus15.setDescription("Request Forcefully Closed");
		try
		{
			oStatus15.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus15.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus15);

		OrderStatus oStatus16 = new OrderStatus();
		oStatus16.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus16.setCreatedBy(userId);
		oStatus16.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus16.setUpdatedBy(userId);
		oStatus16.setLocationsId(locationId);
		oStatus16.setStatus("F");
		oStatus16.setDisplaySequence(6);
		oStatus16.setDisplayName("PO Created");
		oStatus16.setName("PO Created");
		oStatus16.setOrderSourceGroupId(orderSourceGroup);
		oStatus16.setIsServerDriven((byte) 1);
		oStatus16.setStatusColour("#1255e6");
		oStatus16.setDescription("PO Created");
		try
		{
			oStatus16.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus16.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus16);

		OrderStatus oStatus17 = new OrderStatus();
		oStatus17.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus17.setCreatedBy(userId);
		oStatus17.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus17.setUpdatedBy(userId);
		oStatus17.setLocationsId(locationId);
		oStatus17.setStatus("F");
		oStatus17.setDisplaySequence(7);
		oStatus17.setDisplayName("PO Saved");
		oStatus17.setName("PO Saved");
		oStatus17.setOrderSourceGroupId(orderSourceGroup);
		oStatus17.setIsServerDriven((byte) 1);
		oStatus17.setStatusColour("#00652e");
		oStatus17.setDescription("PO Saved");
		try
		{
			oStatus17.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus17.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus17);

		OrderStatus oStatus18 = new OrderStatus();
		oStatus18.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus18.setCreatedBy(userId);
		oStatus18.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus18.setUpdatedBy(userId);
		oStatus18.setLocationsId(locationId);
		oStatus18.setStatus("F");
		oStatus18.setDisplaySequence(8);
		oStatus18.setDisplayName("PO Cancelled");
		oStatus18.setName("PO Cancelled");
		oStatus18.setOrderSourceGroupId(orderSourceGroup);
		oStatus18.setIsServerDriven((byte) 1);
		oStatus18.setStatusColour("#C1C2C8");
		oStatus18.setDescription("PO Cancelled");
		try
		{
			oStatus18.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus18.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus18);

		OrderStatus oStatus19 = new OrderStatus();
		oStatus19.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus19.setCreatedBy(userId);
		oStatus19.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus19.setUpdatedBy(userId);
		oStatus19.setLocationsId(locationId);
		oStatus19.setStatus("F");
		oStatus19.setDisplaySequence(1);
		oStatus19.setDisplayName("PO Sent");
		oStatus19.setName("PO Sent");
		oStatus19.setOrderSourceGroupId(orderSourceGroup);
		oStatus19.setIsServerDriven((byte) 1);
		oStatus19.setStatusColour("#c73030");
		oStatus19.setDescription("PO Sent");
		try
		{
			oStatus19.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus19.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus19);

		OrderStatus oStatus20 = new OrderStatus();
		oStatus20.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus20.setCreatedBy(userId);
		oStatus20.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus20.setUpdatedBy(userId);
		oStatus20.setLocationsId(locationId);
		oStatus20.setStatus("F");
		oStatus20.setDisplaySequence(2);
		oStatus20.setDisplayName("PO Received");
		oStatus20.setName("PO Received");
		oStatus20.setOrderSourceGroupId(orderSourceGroup);
		oStatus20.setIsServerDriven((byte) 1);
		oStatus20.setStatusColour("#7535a6");
		oStatus20.setDescription("PO Received");
		try
		{
			oStatus20.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus20.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus20);

		OrderStatus oStatus21 = new OrderStatus();
		oStatus21.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus21.setCreatedBy(userId);
		oStatus21.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus21.setUpdatedBy(userId);
		oStatus21.setLocationsId(locationId);
		oStatus21.setStatus("F");
		oStatus21.setDisplaySequence(3);
		oStatus21.setDisplayName("PO Partially Received");
		oStatus21.setName("PO Partially Received");
		oStatus21.setOrderSourceGroupId(orderSourceGroup);
		oStatus21.setIsServerDriven((byte) 1);
		oStatus21.setStatusColour("#00652E");
		oStatus21.setDescription("PO Partially Received");
		try
		{
			oStatus21.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus21.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus21);

		OrderStatus oStatus22 = new OrderStatus();
		oStatus22.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus22.setCreatedBy(userId);
		oStatus22.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus22.setUpdatedBy(userId);
		oStatus22.setLocationsId(locationId);
		oStatus22.setStatus("F");
		oStatus22.setDisplaySequence(6);
		oStatus22.setDisplayName("PO Rejected");
		oStatus22.setName("PO Rejected");
		oStatus22.setOrderSourceGroupId(orderSourceGroup);
		oStatus22.setIsServerDriven((byte) 1);
		oStatus22.setStatusColour("#1255e6");
		oStatus22.setDescription("PO Rejected");
		try
		{
			oStatus22.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus22.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus22);

		OrderStatus oStatus23 = new OrderStatus();
		oStatus23.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus23.setCreatedBy(userId);
		oStatus23.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus23.setUpdatedBy(userId);
		oStatus23.setLocationsId(locationId);
		oStatus23.setStatus("F");
		oStatus23.setDisplaySequence(7);
		oStatus23.setDisplayName("PO Forcefully Closed");
		oStatus23.setName("PO Forcefully Closed");
		oStatus23.setOrderSourceGroupId(orderSourceGroup);
		oStatus23.setIsServerDriven((byte) 1);
		oStatus23.setStatusColour("#00652e");
		oStatus23.setDescription("PO Forcefully Closed");
		try
		{
			oStatus23.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus23.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus23);

		OrderStatus oStatus24 = new OrderStatus();
		oStatus24.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus24.setCreatedBy(userId);
		oStatus24.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus24.setUpdatedBy(userId);
		oStatus24.setLocationsId(locationId);
		oStatus24.setStatus("F");
		oStatus24.setDisplaySequence(7);
		oStatus24.setDisplayName("Request Partially Processed");
		oStatus24.setName("Request Partially Processed");
		oStatus24.setOrderSourceGroupId(orderSourceGroup);
		oStatus24.setIsServerDriven((byte) 1);
		oStatus24.setStatusColour("#054552e");
		oStatus24.setDescription("Request Partially Processed");
		try
		{
			oStatus24.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus24.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus24);

		OrderStatus oStatus25 = new OrderStatus();
		oStatus25.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus25.setCreatedBy(userId);
		oStatus25.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus25.setUpdatedBy(userId);
		oStatus25.setLocationsId(locationId);
		oStatus25.setStatus("F");
		oStatus25.setDisplaySequence(7);
		oStatus25.setDisplayName("Item saved");
		oStatus25.setName("Item saved");
		oStatus25.setOrderSourceGroupId(orderSourceGroup);
		oStatus25.setIsServerDriven((byte) 1);
		oStatus25.setStatusColour("#8cb3f2");
		oStatus25.setDescription("Item saved");
		try
		{
			oStatus25.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus25.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus25);

	}

	/**
	 * Adds the served order status constants.
	 *
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 */
	public void addServedOrderStatusConstants(EntityManager em, String userId, String locationId, HttpServletRequest httpRequest)
	{

		String orderSourceGroupId = getOrderSourceGroup(em, "In Store", locationId);
		OrderStatus oStatus = new OrderStatus();
		oStatus.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus.setCreatedBy(userId);
		oStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus.setUpdatedBy(userId);
		oStatus.setLocationsId(locationId);
		oStatus.setStatus("A");
		oStatus.setDisplaySequence(1);
		oStatus.setDisplayName("Drink Served");
		oStatus.setName("Drink Served");
		oStatus.setOrderSourceGroupId(orderSourceGroupId);
		oStatus.setImageUrl("drinkserved.png");
		oStatus.setIsServerDriven((byte) 1);
		oStatus.setStatusColour("#150958");
		oStatus.setDescription("Drink ordered or refill ordered from the menu and sent to the kitchen and served");
		try
		{
			if (oStatus.getId() == null)
				oStatus.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus);

		OrderStatus oStatus1 = new OrderStatus();

		oStatus1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus1.setCreatedBy(userId);
		oStatus1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus1.setUpdatedBy(userId);
		oStatus1.setLocationsId(locationId);
		oStatus1.setStatus("A");
		oStatus1.setDisplaySequence(2);
		oStatus1.setDisplayName("Appetizer Served");
		oStatus1.setName("Appetizer Served");
		oStatus1.setOrderSourceGroupId(orderSourceGroupId);
		oStatus1.setIsServerDriven((byte) 1);
		oStatus1.setStatusColour("#f05123");
		oStatus1.setDescription("Appetizers ordered from the menu and sent to the kitchen and served.");
		oStatus1.setImageUrl("apitizer.png");
		try
		{
			oStatus1.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus1.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus1);

		OrderStatus oStatus2 = new OrderStatus();

		oStatus2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus2.setCreatedBy(userId);
		oStatus2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus2.setUpdatedBy(userId);
		oStatus2.setLocationsId(locationId);
		oStatus2.setStatus("A");
		oStatus2.setDisplaySequence(3);
		oStatus2.setDisplayName("Food Served");
		oStatus2.setName("Food Served");
		oStatus2.setOrderSourceGroupId(orderSourceGroupId);
		oStatus2.setIsServerDriven((byte) 1);
		oStatus2.setStatusColour("#2b0099");
		oStatus2.setDescription("Food ordered from the menu and sent to the kitchen and served");
		oStatus2.setImageUrl("foodserved1.png");
		try
		{
			oStatus2.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus2.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus2);

		OrderStatus oStatus3 = new OrderStatus();

		oStatus3.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus3.setCreatedBy(userId);
		oStatus3.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oStatus3.setUpdatedBy(userId);
		oStatus3.setLocationsId(locationId);
		oStatus3.setStatus("A");
		oStatus3.setDisplaySequence(4);
		oStatus3.setDisplayName("Dessert Served");
		oStatus3.setName("Dessert Served");
		oStatus3.setOrderSourceGroupId(orderSourceGroupId);
		oStatus3.setIsServerDriven((byte) 1);
		oStatus3.setStatusColour("#640000");
		oStatus3.setDescription("Dessert ordered from the menu and sent to the kitchen and served");
		oStatus3.setImageUrl("Dessert1.png");
		try
		{
			oStatus3.setId(new StoreForwardUtility().generateDynamicIntId(em, oStatus3.getLocationsId(), httpRequest, "order_status"));
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		em.persist(oStatus3);

	}

	/**
	 * Gets the order source group by location id and name.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the order source group by location id and name
	 * @throws Exception
	 *             the exception
	 */
	public OrderSourceGroup getOrderSourceGroupByLocationIdAndName(EntityManager em, String locationId, String name) throws Exception
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.locationsId), locationId),
					builder.equal(orderSourceGroup.get(OrderSourceGroup_.name), name), builder.notEqual(orderSourceGroup.get(OrderSourceGroup_.status), "D")));
			return query.getSingleResult();
		}
		catch (NoResultException e)
		{
			// todo shlok need to handle exception in below

		}
		return null;
	}

	void addOrderAdditionalQuestionsn(EntityManager em, String userId, HttpServletRequest httpRequest)
	{
		int fieldTypeId = getFieldTypeByName("Test Box", em);
		if (fieldTypeId > 0)
		{
			OrderAdditionalQuestion additionalQuestions = new OrderAdditionalQuestion();
			additionalQuestions.setQuestion("Guest Comment");
			additionalQuestions.setFieldTypeId(fieldTypeId);
			additionalQuestions.setUpdatedBy(userId);
			additionalQuestions.setCreated(new Date((new TimezoneTime().getGMTTimeInMilis())));
			additionalQuestions.setUpdated(new Date((new TimezoneTime().getGMTTimeInMilis())));
			additionalQuestions.setStatus("F");
			additionalQuestions.setCreatedBy(userId);
			additionalQuestions.setId(new StoreForwardUtility().generateUUID());
			
			 
			em.persist(additionalQuestions);

			OrderAdditionalQuestion additionalQuestions1 = new OrderAdditionalQuestion();
			additionalQuestions1.setQuestion("Business Comment");
			additionalQuestions1.setFieldTypeId(fieldTypeId);
			additionalQuestions1.setUpdatedBy(userId);
			additionalQuestions1.setCreated(new Date((new TimezoneTime().getGMTTimeInMilis())));
			additionalQuestions1.setUpdated(new Date((new TimezoneTime().getGMTTimeInMilis())));
			additionalQuestions1.setStatus("F");
			additionalQuestions1.setCreatedBy(userId);
			additionalQuestions1.setId(new StoreForwardUtility().generateUUID());
			 
			
			em.persist(additionalQuestions1);

			OrderAdditionalQuestion additionalQuestions2 = new OrderAdditionalQuestion();
			additionalQuestions2.setQuestion("Assign Employee");
			additionalQuestions2.setFieldTypeId(fieldTypeId);
			additionalQuestions2.setUpdatedBy(userId);
			additionalQuestions2.setCreated(new Date((new TimezoneTime().getGMTTimeInMilis())));
			additionalQuestions2.setUpdated(new Date((new TimezoneTime().getGMTTimeInMilis())));
			additionalQuestions2.setStatus("F");
			additionalQuestions2.setCreatedBy(userId);
			additionalQuestions2.setId(new StoreForwardUtility().generateUUID());
			 
			em.persist(additionalQuestions2);
		}

	}

	public int getFieldTypeByName(String name, EntityManager em)
	{
		try
		{
			String queryString = "select p from FieldType p where p.fieldTypeName =?  " + " ";
			TypedQuery<FieldType> query = em.createQuery(queryString, FieldType.class).setParameter(1, name);
			FieldType resultSet = query.getSingleResult();
			return resultSet.getId();
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return 0;
	}

}