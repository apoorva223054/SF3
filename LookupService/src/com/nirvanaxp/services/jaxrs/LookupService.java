/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.common.utils.relationalentity.helper.RoleRelationsHelper;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.global.types.entities.countries.City;
import com.nirvanaxp.global.types.entities.countries.City_;
import com.nirvanaxp.global.types.entities.countries.State;
import com.nirvanaxp.global.types.entities.countries.State_;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.helper.OrderSourceGroupToPaymentgatewayTypeHelper;
import com.nirvanaxp.services.jaxrs.helper.OrderSourceToPaymentgatewayTypeHelper;
import com.nirvanaxp.services.jaxrs.helper.OrderSourceToSalesTaxHelper;
import com.nirvanaxp.services.jaxrs.helper.PrinterReceiptHelper;
import com.nirvanaxp.services.jaxrs.helper.ReasonManagementHelper;
import com.nirvanaxp.services.jaxrs.helper.SalesTaxHelper;
import com.nirvanaxp.services.jaxrs.packets.AdditionalQuestionAnswerPacket;
import com.nirvanaxp.services.jaxrs.packets.BusinessHourPacket;
import com.nirvanaxp.services.jaxrs.packets.CdoMgmtPacket;
import com.nirvanaxp.services.jaxrs.packets.ContactPrefrencesPacket;
import com.nirvanaxp.services.jaxrs.packets.CourseListPacket;
import com.nirvanaxp.services.jaxrs.packets.CoursePacket;
import com.nirvanaxp.services.jaxrs.packets.DeliveryOptionPacket;
import com.nirvanaxp.services.jaxrs.packets.DeviceToPinpadPacket;
import com.nirvanaxp.services.jaxrs.packets.DiscountPacket;
import com.nirvanaxp.services.jaxrs.packets.DiscountsTypePacket;
import com.nirvanaxp.services.jaxrs.packets.DisplaySequenceUpdateList;
import com.nirvanaxp.services.jaxrs.packets.FeedbackFieldPacket;
import com.nirvanaxp.services.jaxrs.packets.FeedbackQuestionPacket;
import com.nirvanaxp.services.jaxrs.packets.FeedbackTypePacket;
import com.nirvanaxp.services.jaxrs.packets.FloorPlanPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemAttributeToDatePacket;
import com.nirvanaxp.services.jaxrs.packets.ItemGroupPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsAttributeListPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsAttributePacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsAttributeTypeListPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsAttributeTypePacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsCharListPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsCharPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsSchedulePacket;
import com.nirvanaxp.services.jaxrs.packets.LocationToFunctionsListPacket;
import com.nirvanaxp.services.jaxrs.packets.LocationsToShiftPreAssignServerPacket;
import com.nirvanaxp.services.jaxrs.packets.NutritionsPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderAdditionalQuestionsPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderSourceGroupPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderSourceGroupToPaymentgatewayTypePacket;
import com.nirvanaxp.services.jaxrs.packets.OrderSourcePacket;
import com.nirvanaxp.services.jaxrs.packets.OrderSourceToPaymentgatewayTypePacket;
import com.nirvanaxp.services.jaxrs.packets.OrderSourceToSalesTaxPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderStatusListPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderStatusPacket;
import com.nirvanaxp.services.jaxrs.packets.PaymentGatewayToPinpadPacket;
import com.nirvanaxp.services.jaxrs.packets.PaymentMethodPacket;
import com.nirvanaxp.services.jaxrs.packets.PaymentMethodTypePacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.PrinterInfoPacket;
import com.nirvanaxp.services.jaxrs.packets.PrinterModelPacket;
import com.nirvanaxp.services.jaxrs.packets.PrinterPacket;
import com.nirvanaxp.services.jaxrs.packets.PrinterReceiptPacket;
import com.nirvanaxp.services.jaxrs.packets.ReasonPacket;
import com.nirvanaxp.services.jaxrs.packets.ReasonTypePacket;
import com.nirvanaxp.services.jaxrs.packets.RequestTypePacket;
import com.nirvanaxp.services.jaxrs.packets.ReservationSchedulePacket;
import com.nirvanaxp.services.jaxrs.packets.ReservationStatusListPacket;
import com.nirvanaxp.services.jaxrs.packets.ReservationStatusPacket;
import com.nirvanaxp.services.jaxrs.packets.ReservationsTypePacket;
import com.nirvanaxp.services.jaxrs.packets.RolePacket;
import com.nirvanaxp.services.jaxrs.packets.SMSTemplatePacket;
import com.nirvanaxp.services.jaxrs.packets.SalesTaxPacket;
import com.nirvanaxp.services.jaxrs.packets.ShiftSchedulePacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.CdoMgmt;
import com.nirvanaxp.types.entities.CdoMgmt_;
import com.nirvanaxp.types.entities.Day;
import com.nirvanaxp.types.entities.Day_;
import com.nirvanaxp.types.entities.FieldType;
import com.nirvanaxp.types.entities.Floorplan;
import com.nirvanaxp.types.entities.Floorplan_;
import com.nirvanaxp.types.entities.business.BusinessHour;
import com.nirvanaxp.types.entities.catalog.category.ItemAttributeToDate;
import com.nirvanaxp.types.entities.catalog.course.Course;
import com.nirvanaxp.types.entities.catalog.course.Course_;
import com.nirvanaxp.types.entities.catalog.items.ItemGroup;
import com.nirvanaxp.types.entities.catalog.items.ItemGroup_;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType_;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute_;
import com.nirvanaxp.types.entities.catalog.items.ItemsChar;
import com.nirvanaxp.types.entities.catalog.items.ItemsChar_;
import com.nirvanaxp.types.entities.catalog.items.ItemsSchedule;
import com.nirvanaxp.types.entities.catalog.items.ItemsSchedule_;
import com.nirvanaxp.types.entities.catalog.items.Nutritions;
import com.nirvanaxp.types.entities.catalog.items.Nutritions_;
import com.nirvanaxp.types.entities.custom.AttributeDisplayService;
import com.nirvanaxp.types.entities.device.DeviceToPinPad;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.DiscountToReasons;
import com.nirvanaxp.types.entities.discounts.Discount_;
import com.nirvanaxp.types.entities.discounts.DiscountsType;
import com.nirvanaxp.types.entities.discounts.DiscountsType_;
import com.nirvanaxp.types.entities.feedback.FeedbackField;
import com.nirvanaxp.types.entities.feedback.FeedbackField_;
import com.nirvanaxp.types.entities.feedback.FeedbackQuestion;
import com.nirvanaxp.types.entities.feedback.FeedbackQuestion_;
import com.nirvanaxp.types.entities.feedback.FeedbackType;
import com.nirvanaxp.types.entities.feedback.FeedbackType_;
import com.nirvanaxp.types.entities.feedback.Smiley;
import com.nirvanaxp.types.entities.feedback.Smiley_;
import com.nirvanaxp.types.entities.function.Functions;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.locations.LocationsToFunction;
import com.nirvanaxp.types.entities.locations.LocationsToShiftPreAssignServer;
import com.nirvanaxp.types.entities.locations.receipt.PrinterReceipt;
import com.nirvanaxp.types.entities.locations.receipt.PrinterReceipt_;
import com.nirvanaxp.types.entities.orders.DeliveryOption;
import com.nirvanaxp.types.entities.orders.DeliveryOption_;
import com.nirvanaxp.types.entities.orders.OptionType;
import com.nirvanaxp.types.entities.orders.OrderAdditionalQuestion;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus_;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToShiftSchedule;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToShiftSchedule_;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup_;
import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceToShiftSchedule;
import com.nirvanaxp.types.entities.orders.OrderSourceToShiftSchedule_;
import com.nirvanaxp.types.entities.orders.OrderSource_;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.orders.ShiftSchedule;
import com.nirvanaxp.types.entities.orders.ShiftSchedule_;
import com.nirvanaxp.types.entities.orders.ShiftSlots;
import com.nirvanaxp.types.entities.orders.ShiftSlots_;
import com.nirvanaxp.types.entities.payment.PaymentGatewayToPinpad;
import com.nirvanaxp.types.entities.payment.PaymentGatewayToPinpad_;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;
import com.nirvanaxp.types.entities.payment.PaymentMethodType_;
import com.nirvanaxp.types.entities.payment.PaymentMethod_;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType_;
import com.nirvanaxp.types.entities.payment.PaymentType;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.printers.Printer_;
import com.nirvanaxp.types.entities.printers.PrintersInterface;
import com.nirvanaxp.types.entities.printers.PrintersInterface_;
import com.nirvanaxp.types.entities.printers.PrintersModel;
import com.nirvanaxp.types.entities.printers.PrintersModel_;
import com.nirvanaxp.types.entities.printers.PrintersType;
import com.nirvanaxp.types.entities.printers.PrintersType_;
import com.nirvanaxp.types.entities.reasons.Reasons;
import com.nirvanaxp.types.entities.reasons.Reasons_;
import com.nirvanaxp.types.entities.reservation.ContactPreference;
import com.nirvanaxp.types.entities.reservation.ContactPreference_;
import com.nirvanaxp.types.entities.reservation.RequestType;
import com.nirvanaxp.types.entities.reservation.RequestType_;
import com.nirvanaxp.types.entities.reservation.ReservationsSchedule;
import com.nirvanaxp.types.entities.reservation.ReservationsScheduleDay;
import com.nirvanaxp.types.entities.reservation.ReservationsScheduleXref;
import com.nirvanaxp.types.entities.reservation.ReservationsSchedule_;
import com.nirvanaxp.types.entities.reservation.ReservationsSlot;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus_;
import com.nirvanaxp.types.entities.reservation.ReservationsType;
import com.nirvanaxp.types.entities.reservation.ReservationsType_;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.roles.Role_;
import com.nirvanaxp.types.entities.salestax.OrderSourceToSalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax_;
import com.nirvanaxp.types.entities.sms.SMSTemplate;
import com.nirvanaxp.types.entities.sms.SMSTemplate_;
import com.nirvanaxp.types.entities.time.Timezone;
import com.nirvanaxp.types.entities.time.Timezone_;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

// TODO: Auto-generated Javadoc
/**
 * The Class LookupService.
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class LookupService extends AbstractNirvanaService
{

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The logger. */
	private NirvanaLogger logger = new NirvanaLogger(LookupService.class.getName());

	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	/**
	 * This method used to fetch Reservations Status.
	 *
	 * @return Reservations Status List
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allReservationsStatus")
	public String allReservationsStatus(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsStatus> criteria = builder.createQuery(ReservationsStatus.class);
			Root<ReservationsStatus> r = criteria.from(ReservationsStatus.class);
			TypedQuery<ReservationsStatus> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch Get All Reservations Status By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return list of reservation status status not equals to delete and order
	 *         by display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsStatusByLocationId/{locationId}")
	public String getAllReservationsStatusByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			// removed the collection sort logic :- giving null pointer in new
			// implementation of display sequence
			// by Apoorva July 6, 2015 sprint 7
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select r from ReservationsStatus r where r.locationsId=? and r.status !='D' order by r.displaySequence asc";
			TypedQuery<ReservationsStatus> query = em.createQuery(queryString, ReservationsStatus.class).setParameter(1, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch Get Reservations Status By Id.
	 *
	 * @param id
	 *            the id
	 * @return Reservations Status
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getReservationsStatusById/{id}")
	public String getReservationsStatusById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsStatus> criteria = builder.createQuery(ReservationsStatus.class);
			Root<ReservationsStatus> r = criteria.from(ReservationsStatus.class);
			TypedQuery<ReservationsStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsStatus_.id), id)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch Get Reservations Status By Location Id And
	 * Name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return Reservations Status which is location id, name, and not delete
	 *         status
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getReservationsStatusByLocationIdAndName/{locationId}/{name}")
	public String getReservationsStatusByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsStatus> criteria = builder.createQuery(ReservationsStatus.class);
			Root<ReservationsStatus> r = criteria.from(ReservationsStatus.class);
			TypedQuery<ReservationsStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsStatus_.locationsId), locationId),
					builder.equal(r.get(ReservationsStatus_.name), name), builder.notEqual(r.get(ReservationsStatus_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch Get Reservations Status By Location Id And
	 * Display Sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return Reservations Status by locationId, displaySequence and not delete
	 *         status
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getReservationsStatusByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getReservationsStatusByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsStatus> criteria = builder.createQuery(ReservationsStatus.class);
			Root<ReservationsStatus> r = criteria.from(ReservationsStatus.class);
			TypedQuery<ReservationsStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsStatus_.locationsId), locationId),
					builder.equal(r.get(ReservationsStatus_.displaySequence), displaySequence), builder.notEqual(r.get(ReservationsStatus_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch Get All Reservations Type.
	 *
	 * @return Reservations Type list
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allReservationsType")
	public String getAllReservationsType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsType> criteria = builder.createQuery(ReservationsType.class);
			Root<ReservationsType> r = criteria.from(ReservationsType.class);
			TypedQuery<ReservationsType> query = em.createQuery(criteria.select(r));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch Get All Reservations Type By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return Reservations Type list by location Id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsTypeByLocationId/{locationId}")
	public String getAllReservationsTypeByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsType> criteria = builder.createQuery(ReservationsType.class);
			Root<ReservationsType> r = criteria.from(ReservationsType.class);
			TypedQuery<ReservationsType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsType_.locationsId), locationId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch Get All Feedback Question
	 * getAllFeedbackQuestion.
	 *
	 * @return Feedback Question List
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allFeedbackQuestion")
	public String getAllFeedbackQuestion(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<FeedbackQuestion> criteria = builder.createQuery(FeedbackQuestion.class);
			Root<FeedbackQuestion> r = criteria.from(FeedbackQuestion.class);
			TypedQuery<FeedbackQuestion> query = em.createQuery(criteria.select(r));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 * This method used to fetch get All Feedback Question By Location Id And
	 * Feedback Type Id.
	 *
	 * @param locationId
	 *            the location id
	 * @param feedbackTypeId
	 *            the feedback type id
	 * @return Feedback Question list By Location Id And Feedback Type Id and
	 *         not included delete status
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllFeedbackQuestionByLocationIdAndFeedbackTypeId/{locationId}/{feedbackTypeId}")
	public String getAllFeedbackQuestionByLocationIdAndFeedbackTypeId(@PathParam("locationId") String locationId, @PathParam("feedbackTypeId") String feedbackTypeId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<FeedbackQuestion> criteria = builder.createQuery(FeedbackQuestion.class);
			Root<FeedbackQuestion> r = criteria.from(FeedbackQuestion.class);
			TypedQuery<FeedbackQuestion> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(FeedbackQuestion_.locationsId), locationId),
					builder.equal(r.get(FeedbackQuestion_.feedbackTypeId), feedbackTypeId), builder.notEqual(r.get(FeedbackQuestion_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch Get All Feedback Type By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return Feedback Type by Location Id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllFeedbackTypeByLocationId/{locationId}")
	public String getAllFeedbackTypeByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<FeedbackType> criteria = builder.createQuery(FeedbackType.class);
			Root<FeedbackType> r = criteria.from(FeedbackType.class);
			TypedQuery<FeedbackType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(FeedbackType_.locationsId), locationId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch Get All Field Type.
	 *
	 * @return Field Type list
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allFieldType")
	public String getAllFieldType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<FieldType> criteria = builder.createQuery(FieldType.class);
			Root<FieldType> r = criteria.from(FieldType.class);
			TypedQuery<FieldType> query = em.createQuery(criteria.select(r));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch get All Feedback Field.
	 *
	 * @return Feedback Field List
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allFeedbackField")
	public String getAllFeedbackField(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<FeedbackField> criteria = builder.createQuery(FeedbackField.class);
			Root<FeedbackField> r = criteria.from(FeedbackField.class);
			TypedQuery<FeedbackField> query = em.createQuery(criteria.select(r));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch get All Feedback Field By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return Feedback Field By Location Id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllFeedbackFieldByLocationId/{locationId}")
	public String getAllFeedbackFieldByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<FeedbackField> criteria = builder.createQuery(FeedbackField.class);
			Root<FeedbackField> r = criteria.from(FeedbackField.class);
			TypedQuery<FeedbackField> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(FeedbackField_.locationsId), locationId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch all Request Type.
	 *
	 * @return Request Type list
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allRequestType")
	public String allRequestType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<RequestType> criteria = builder.createQuery(RequestType.class);
			Root<RequestType> r = criteria.from(RequestType.class);
			TypedQuery<RequestType> query = em.createQuery(criteria.select(r));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 * This method used to fetch get AllRequest Type By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return Request Type by locations Id, status not delete and order by
	 *         r.displaySequence asc";
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllRequestTypeByLocationId/{locationId}")
	public String getAllRequestTypeByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select r from RequestType r where r.locationsId=? and r.status !='D' order by r.displaySequence asc";
			TypedQuery<RequestType> query = em.createQuery(queryString, RequestType.class).setParameter(1, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch get Request Type By Id.
	 *
	 * @param id
	 *            the id
	 * @return Request Type
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getRequestTypeById/{id}")
	public String getRequestTypeById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<RequestType> criteria = builder.createQuery(RequestType.class);
			Root<RequestType> r = criteria.from(RequestType.class);
			TypedQuery<RequestType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(RequestType_.id), id)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch get Request Type By Location Id And Name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return Request Type By Location Id And Name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getRequestTypeByLocationIdAndName/{locationId}/{name}")
	public String getRequestTypeByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<RequestType> criteria = builder.createQuery(RequestType.class);
			Root<RequestType> r = criteria.from(RequestType.class);
			TypedQuery<RequestType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(RequestType_.locationsId), locationId), builder.equal(r.get(RequestType_.requestName), name),
					builder.notEqual(r.get(RequestType_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch get Request Type By Location Id And Display
	 * Sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return Request Type
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getRequestTypeByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getRequestTypeByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<RequestType> criteria = builder.createQuery(RequestType.class);
			Root<RequestType> r = criteria.from(RequestType.class);
			TypedQuery<RequestType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(RequestType_.locationsId), locationId),
					builder.equal(r.get(RequestType_.displaySequence), displaySequence), builder.notEqual(r.get(RequestType_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to fetch all Contact Preference.
	 *
	 * @return Contact Preference
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allContactPreference")
	public String allContactPreference(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ContactPreference> criteria = builder.createQuery(ContactPreference.class);
			Root<ContactPreference> r = criteria.from(ContactPreference.class);
			TypedQuery<ContactPreference> query = em.createQuery(criteria.select(r));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to get All Contact Preference By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return Contact Preference By Location Id, status not included delete and
	 *         display Sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllContactPreferenceByLocationId/{locationId}")
	public String getAllContactPreferenceByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select c from ContactPreference c where c.locationsId=? and c.status !='D' order by c.displaySequence asc";
			TypedQuery<ContactPreference> query = em.createQuery(queryString, ContactPreference.class).setParameter(1, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to get Contact Preference By Id.
	 *
	 * @param id
	 *            the id
	 * @return Contact Preference
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getContactPreferenceById/{id}")
	public String getContactPreferenceById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ContactPreference> criteria = builder.createQuery(ContactPreference.class);
			Root<ContactPreference> r = criteria.from(ContactPreference.class);
			TypedQuery<ContactPreference> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ContactPreference_.id), id)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to get Contact Preference By Location Id And Name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return Contact Preference By Location Id, status not included delete and
	 *         name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getContactPreferenceByLocationIdAndName/{locationId}/{name}")
	public String getContactPreferenceByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ContactPreference> criteria = builder.createQuery(ContactPreference.class);
			Root<ContactPreference> r = criteria.from(ContactPreference.class);
			TypedQuery<ContactPreference> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ContactPreference_.locationsId), locationId),
					builder.equal(r.get(ContactPreference_.name), name), builder.notEqual(r.get(ContactPreference_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to get Contact Preference By Location Id And Display
	 * Sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return Contact Preference By Location Id, status not included delete and
	 *         display Sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getContactPreferenceByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getContactPreferenceByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ContactPreference> criteria = builder.createQuery(ContactPreference.class);
			Root<ContactPreference> r = criteria.from(ContactPreference.class);
			TypedQuery<ContactPreference> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ContactPreference_.locationsId), locationId),
					builder.equal(r.get(ContactPreference_.displaySequence), displaySequence), builder.notEqual(r.get(ContactPreference_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to all Printers Model.
	 *
	 * @return Printers Model
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allPrintersModel")
	public String getAllPrintersModel(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrintersModel> criteria = builder.createQuery(PrintersModel.class);
			Root<PrintersModel> r = criteria.from(PrintersModel.class);
			TypedQuery<PrintersModel> query = em.createQuery(criteria.select(r));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to get All Printer.
	 *
	 * @return Printer
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allPrinter")
	public String getAllPrinter(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> r = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(r));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to get All Printer By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return Printer
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllPrinterByLocationId/{locationId}")
	public String getPrinterByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> r = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Printer_.locationsId), locationId), builder.notEqual(r.get(Printer_.status), "D")));
			List<Printer> printers = query.getResultList();

			if (printers != null && printers.size() > 0)
				for (Printer printer : printers)
				{
					if (printer.getPrintersTypeId() != null)
					{

						printer.setPrintersType((PrintersType) new CommonMethods().getObjectById("PrintersType", em,PrintersType.class, printer.getPrintersTypeId()));
					}
				}

			return new JSONUtility(httpRequest).convertToJsonString(printers);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to get All Active Printer By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return Printer By Location Id, status included active
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllActivePrinterByLocationId/{locationId}")
	public String getAllActivePrinterByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> r = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Printer_.locationsId), locationId), builder.equal(r.get(Printer_.status), "A")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to get All Printers Type By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return Printers By Location Id, status not included delete
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllPrintersTypeByLocationId/{locationId}")
	public String getPrintersTypeByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrintersType> criteria = builder.createQuery(PrintersType.class);
			Root<PrintersType> r = criteria.from(PrintersType.class);
			TypedQuery<PrintersType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(PrintersType_.locationsId), locationId), builder.notEqual(r.get(PrintersType_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method used to get get All Printers Interface By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return Printers Interface By Location Id, status not included delete
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllPrintersInterfaceByLocationId/{locationId}")
	public String getPrintersInterfaceByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrintersInterface> criteria = builder.createQuery(PrintersInterface.class);
			Root<PrintersInterface> r = criteria.from(PrintersInterface.class);
			TypedQuery<PrintersInterface> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(PrintersInterface_.locationsId), locationId),
					builder.notEqual(r.get(PrintersInterface_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * get Printer By Id.
	 *
	 * @param id
	 *            the id
	 * @return Printer
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterById/{id}")
	public String getPrinterById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> r = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Printer_.id), id)));
			Printer p = query.getSingleResult();
			String queryString = "select l from Location l where l.id in  (select p.locationsId from Printer p where p.globalPrinterId=? and p.status not in ('D')) ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());

			List<Location> resultSet = query2.getResultList();
			p.setLocationList(resultSet);

			return new JSONUtility(httpRequest).convertToJsonString(p);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * get Printer By Location Id And Name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return Printer
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterByLocationIdAndName/{locationId}/{name}")
	public String getPrinterByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> r = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Printer_.locationsId), locationId), builder.equal(r.get(Printer_.printersName), name),
					builder.notEqual(r.get(Printer_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * get Printer By Location Id And Display Sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return the printer by location id and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getPrinterByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> r = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Printer_.locationsId), locationId),
					builder.equal(r.get(Printer_.displaySequence), displaySequence), builder.notEqual(r.get(Printer_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * all Order Status.
	 *
	 * @return the all order status
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allOrderStatus")
	public String getAllOrderStatus(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 * get All Order Status By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all order status by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderStatusByLocationId/{locationId}")
	public String getAllOrderStatusByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.locationsId), locationId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 * all Roles.
	 *
	 * @return the all roles
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allRoles")
	public String getAllRoles(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> r = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * get All Roles By Location Id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all roles by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllRolesByLocationId/{locationId}")
	public String getAllRolesByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> r = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Role_.locationsId), locationId), builder.notEqual(r.get(Role_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * get All Exception Id.
	 *
	 * @param id
	 *            the id
	 * @return the all roles by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllRolesById/{id}")
	public String getAllRolesById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> r = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Role_.id), id)));

			Role role = query.getSingleResult();
			if (role != null)
			{
				RoleRelationsHelper itemRelationsHelper = new RoleRelationsHelper();
				itemRelationsHelper.setShouldEliminateDStatus(true);

				role.setRolesToFunctions(itemRelationsHelper.getRoleToFunctions(role.getId(), em));

				return new JSONUtility(httpRequest).convertToJsonString(role);

			}
			return null;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all orderdetail sattus.
	 *
	 * @return the all orderdetail sattus
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderDetailStatus")
	public String getAllOrderdetailSattus(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
			Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
			TypedQuery<OrderDetailStatus> query = em.createQuery(criteria.select(r));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order detail status by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all order detail status by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderDetailStatusByLocationId/{locationId}")
	public String getAllOrderDetailStatusByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
			Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
			TypedQuery<OrderDetailStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.locationsId), locationId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order details status by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all order details status by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderDetailsStatusByLocationId/{locationId}")
	public String getAllOrderDetailsStatusByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
			Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
			TypedQuery<OrderDetailStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.locationsId), locationId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/allDiscounts")
	public String getAllDiscounts(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
			Root<Discount> r = criteria.from(Discount.class);
			TypedQuery<Discount> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the discounts by location id and discount type id.
	 *
	 * @param locationId
	 *            the location id
	 * @param discoutsTypeId
	 *            the discouts type id
	 * @return the discounts by location id and discount type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountsByLocationIdAndDiscountTypeId/{locationId}/{discoutsTypeId}")
	public String getDiscountsByLocationIdAndDiscountTypeId(@PathParam("locationId") String locationId, @PathParam("discoutsTypeId") String discoutsTypeId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select d from Discount d where d.locationsId=? and d.discountsTypeId = ? and d.status !='D' order by d.displaySequence asc";
			TypedQuery<Discount> query = em.createQuery(queryString, Discount.class).setParameter(1, locationId).setParameter(2, discoutsTypeId);

			List<Discount> discountList = query.getResultList();

			if (discountList != null && discountList.size() > 0)
			{
				for (Discount discount : discountList)
				{

					List<DiscountToReasons> discountTR = null;
					try
					{
						String queryString1 = "select d from DiscountToReasons d where d.discountsId=? and d.status !='D'";
						TypedQuery<DiscountToReasons> query1 = em.createQuery(queryString1, DiscountToReasons.class).setParameter(1, discount.getId());

						discountTR = query1.getResultList();
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						logger.severe("No Entity found for Discount To Reasons for discount id " + discount.getId());
					}
					if (discountTR != null)
					{
						discount.setDiscountToReasonsList(discountTR);
					}
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(discountList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the discounts type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the discounts type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountsTypeByLocationId/{locationId}")
	public String getDiscountsTypeByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DiscountsType> criteria = builder.createQuery(DiscountsType.class);
			Root<DiscountsType> discount_type = criteria.from(DiscountsType.class);
			TypedQuery<DiscountsType> query = em.createQuery(criteria.select(discount_type).where(builder.equal(discount_type.get(DiscountsType_.locationsId), locationId),
					builder.notEqual(discount_type.get(DiscountsType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the discounts type by id.
	 *
	 * @param id
	 *            the id
	 * @return the discounts type by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountsTypeById/{id}")
	public String getDiscountsTypeById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DiscountsType> criteria = builder.createQuery(DiscountsType.class);
			Root<DiscountsType> discountsType = criteria.from(DiscountsType.class);
			TypedQuery<DiscountsType> query = em.createQuery(criteria.select(discountsType).where(builder.equal(discountsType.get(DiscountsType_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the discounts type by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the discounts type by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountsTypeByLocationIdAndName/{locationId}/{name}")
	public String getDiscountsTypeByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DiscountsType> criteria = builder.createQuery(DiscountsType.class);
			Root<DiscountsType> discountType = criteria.from(DiscountsType.class);
			TypedQuery<DiscountsType> query = em.createQuery(criteria.select(discountType).where(builder.equal(discountType.get(DiscountsType_.locationsId), locationId),
					builder.equal(discountType.get(DiscountsType_.discountsType), name), builder.notEqual(discountType.get(DiscountsType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the discounts by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the discounts by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountsByLocationId/{locationId}")
	public String getDiscountsByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
			Root<Discount> discount = criteria.from(Discount.class);
			TypedQuery<Discount> query = em.createQuery(criteria.select(discount).where(builder.equal(discount.get(Discount_.locationsId), locationId),
					builder.equal(discount.get(Discount_.isGroup), 1), builder.notEqual(discount.get(Discount_.status), "D"), builder.notEqual(discount.get(Discount_.status), "I")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all discounts type.
	 *
	 * @return the all discounts type
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allDiscountsType")
	public String getAllDiscountsType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DiscountsType> criteria = builder.createQuery(DiscountsType.class);
			Root<DiscountsType> r = criteria.from(DiscountsType.class);
			TypedQuery<DiscountsType> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the discounts type by location id and display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return the discounts type by location id and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountsTypeByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getDiscountsTypeByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DiscountsType> criteria = builder.createQuery(DiscountsType.class);
			Root<DiscountsType> discountsType = criteria.from(DiscountsType.class);
			TypedQuery<DiscountsType> query = em.createQuery(criteria.select(discountsType).where(builder.equal(discountsType.get(DiscountsType_.locationsId), locationId),
					builder.equal(discountsType.get(DiscountsType_.displaySequence), displaySequence), builder.notEqual(discountsType.get(DiscountsType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the discount by location id and discount type id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param discountTypeId
	 *            the discount type id
	 * @param name
	 *            the name
	 * @return the discount by location id and discount type id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountByLocationIdAndDiscountTypeIdAndName/{locationId}/{discountTypeId}/{name}")
	public String getDiscountByLocationIdAndDiscountTypeIdAndName(@PathParam("locationId") String locationId, @PathParam("discountTypeId") String discountTypeId, @PathParam("name") String name,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
			Root<Discount> discount = criteria.from(Discount.class);
			TypedQuery<Discount> query = em.createQuery(criteria.select(discount).where(builder.equal(discount.get(Discount_.locationsId), locationId),
					builder.equal(discount.get(Discount_.discountsTypeId), discountTypeId), builder.equal(discount.get(Discount_.name), name), builder.notEqual(discount.get(Discount_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the discount by location id and payment type id and display
	 * sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param discountTypeId
	 *            the discount type id
	 * @param displaySequence
	 *            the display sequence
	 * @return the discount by location id and payment type id and display
	 *         sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountByLocationIdAndDiscountTypeIdAndDisplaySequence/{locationId}/{discountTypeId}/{displaySequence}")
	public String getDiscountByLocationIdAndPaymentTypeIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("discountTypeId") String discountTypeId,
			@PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
			Root<Discount> discount = criteria.from(Discount.class);
			
			TypedQuery<Discount> query = em.createQuery(criteria.select(discount).where(builder.equal(discount.get(Discount_.locationsId), locationId),
					builder.equal(discount.get(Discount_.discountsTypeId), discountTypeId), builder.equal(discount.get(Discount_.displaySequence), displaySequence),
					builder.notEqual(discount.get(Discount_.status), "D")));	
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the discount by id.
	 *
	 * @param id
	 *            the id
	 * @return the discount by id
	 * @throws Exception
	 *             the exception
	 */
	// discount by id
	@GET
	@Path("/getDiscountById/{id}")
	public String getDiscountById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
			Root<Discount> discount = criteria.from(Discount.class);
			TypedQuery<Discount> query = em.createQuery(criteria.select(discount).where(builder.equal(discount.get(Discount_.id), id)));
			Discount p = query.getSingleResult();

			String queryString = "select l from Location l where l.id in  (select p.locationsId from Discount p where p.globalId=? and p.status not in ('D'))) ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());
			List<Location> resultSet = query2.getResultList();
			p.setLocationList(resultSet);

			List<DiscountToReasons> discountTR = null;
			try
			{
				String queryString1 = "select d from DiscountToReasons d where d.discountsId=? and d.status !='D'";
				TypedQuery<DiscountToReasons> query1 = em.createQuery(queryString1, DiscountToReasons.class).setParameter(1, p.getId());

				discountTR = query1.getResultList();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				logger.severe("No Entity found for Discount To Reasons for discount id " + p.getId());
			}
			if (discountTR != null)
			{
				p.setDiscountToReasonsList(discountTR);
			}

			return new JSONUtility(httpRequest).convertToJsonString(p);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllOrderSourceGroupByLocationId/{locationId}")
	public String getAllOrderSourceionId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.locationsId), locationId),
					builder.notEqual(orderSourceGroup.get(OrderSourceGroup_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order source group by id.
	 *
	 * @param id
	 *            the id
	 * @return the all order source group by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceGroupById/{id}")
	public String getAllOrderSourceGroupById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order source by id.
	 *
	 * @param id
	 *            the id
	 * @return the all order source by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceById/{id}")
	public String getAllOrderSourceById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> orderSource = criteria.from(OrderSource.class);
			TypedQuery<OrderSource> query = em.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSource_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source group by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the order source group by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceGroupByLocationIdAndName/{locationId}/{name}")
	public String getOrderSourceGroupByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.locationsId), locationId),
					builder.equal(orderSourceGroup.get(OrderSourceGroup_.name), name), builder.notEqual(orderSourceGroup.get(OrderSourceGroup_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source group by location id and display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return the order source group by location id and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceGroupByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getOrderSourceGroupByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.locationsId), locationId),
					builder.equal(orderSourceGroup.get(OrderSourceGroup_.displaySequence), displaySequence), builder.notEqual(orderSourceGroup.get(OrderSourceGroup_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order status by id.
	 *
	 * @param id
	 *            the id
	 * @return the order status by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderStatusById/{id}")
	public String getOrderStatusById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> orderStatus = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(orderStatus).where(builder.equal(orderStatus.get(OrderStatus_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order source by location id and order source group id.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the all order source by location id and order source group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderSourceByLocationIdAndOrderSourceGroupId/{locationId}/{orderSourceGroupId}")
	public String getAllOrderSourceByLocationIdAndOrderSourceGroupId(@PathParam("locationId") String locationId, @PathParam("orderSourceGroupId") String orderSourceGroupId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> orderSource = criteria.from(OrderSource.class);
			TypedQuery<OrderSource> query = em.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSource_.locationsId), locationId),
					builder.equal(orderSource.get(OrderSource_.orderSourceGroupId), orderSourceGroupId), builder.notEqual(orderSource.get(OrderSource_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source by location id and order source group id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @param name
	 *            the name
	 * @return the order source by location id and order source group id and
	 *         name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceByLocationIdAndOrderSourceGroupIdAndName/{locationId}/{orderSourceGroupId}/{name}")
	public String getOrderSourceByLocationIdAndOrderSourceGroupIdAndName(@PathParam("locationId") String locationId, @PathParam("orderSourceGroupId") String orderSourceGroupId,
			@PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> orderSource = criteria.from(OrderSource.class);
			TypedQuery<OrderSource> query = em.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSource_.locationsId), locationId),
					builder.equal(orderSource.get(OrderSource_.orderSourceGroupId), orderSourceGroupId), builder.equal(orderSource.get(OrderSource_.name), name),
					builder.notEqual(orderSource.get(OrderSource_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source by location id and order source group id and
	 * display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @param displaySequence
	 *            the display sequence
	 * @return the order source by location id and order source group id and
	 *         display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceByLocationIdAndOrderSourceGroupIdAndDisplaySequence/{locationId}/{orderSourceGroupId}/{displaySequence}")
	public String getOrderSourceByLocationIdAndOrderSourceGroupIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("orderSourceGroupId") String orderSourceGroupId,
			@PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> orderSource = criteria.from(OrderSource.class);
			TypedQuery<OrderSource> query = em.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSource_.locationsId), locationId),
					builder.equal(orderSource.get(OrderSource_.orderSourceGroupId), orderSourceGroupId), builder.equal(orderSource.get(OrderSource_.displaySequence), displaySequence),
					builder.notEqual(orderSource.get(OrderSource_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order status by location id and order source group id.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceId
	 *            the order source id
	 * @return the all order status by location id and order source group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderStatusByLocationIdAndOrderSourceGroupId/{locationId}/{orderSourceGroupId}")
	public String getAllOrderStatusByLocationIdAndOrderSourceGroupId(@PathParam("locationId") String locationId, @PathParam("orderSourceGroupId") String orderSourceId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.locationsId), locationId),
					builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceId), builder.notEqual(r.get(OrderStatus_.status), "D")));

			List<OrderStatus> orderStatus = query.getResultList();
			Collections.sort(orderStatus, new Comparator<OrderStatus>()
			{
				@Override
				public int compare(OrderStatus p1, OrderStatus p2)
				{
					return p1.getDisplaySequence() - p2.getDisplaySequence();
				}

			});
			return new JSONUtility(httpRequest).convertToJsonString(orderStatus);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order status by location id and order source id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceId
	 *            the order source id
	 * @param name
	 *            the name
	 * @return the order status by location id and order source id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderStatusByLocationIdAndOrderSourceGroupIdAndName/{locationId}/{orderSourceId}/{name}")
	public String getOrderStatusByLocationIdAndOrderSourceIdAndName(@PathParam("locationId") String locationId, @PathParam("orderSourceId") String orderSourceId, @PathParam("name") String name,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.locationsId), locationId),
					builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceId), builder.equal(r.get(OrderStatus_.name), name), builder.notEqual(r.get(OrderStatus_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order status by location id and order source group ids.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceIds
	 *            the order source ids
	 * @return the order status by location id and order source group ids
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderStatusByLocationIdAndOrderSourceGroupIds/{locationId}/{orderSourceIds}")
	public String getOrderStatusByLocationIdAndOrderSourceGroupIds(@PathParam("locationId") String locationId, @PathParam("orderSourceIds") String orderSourceIds,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.locationsId), locationId),
					builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceIds)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the order status by location id and order source id and display
	 * sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceId
	 *            the order source id
	 * @param displaySequence
	 *            the display sequence
	 * @return the order status by location id and order source id and display
	 *         sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderStatusByLocationIdAndOrderSourceGroupIdAndDisplaySequence/{locationId}/{orderSourceId}/{displaySequence}")
	public String getOrderStatusByLocationIdAndOrderSourceIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("orderSourceId") int orderSourceId,
			@PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.locationsId), locationId),
					builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceId), builder.equal(r.get(OrderStatus_.displaySequence), displaySequence),
					builder.notEqual(r.get(OrderStatus_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all role by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the all role by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllRoleByLocationIdAndName/{locationId}/{name}")
	public String getAllRoleByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> role = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(role).where(builder.equal(role.get(Role_.locationsId), locationId), builder.equal(role.get(Role_.roleName), name),
					builder.notEqual(role.get(Role_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all role by location id and display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return the all role by location id and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllRoleByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getAllRoleByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> role = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(role).where(builder.equal(role.get(Role_.locationsId), locationId),
					builder.equal(role.get(Role_.displaySequence), displaySequence), builder.notEqual(role.get(Role_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getPaymentMethodTypeByLocationId/{locationId}")
	public String getPaymentMethodTypeByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethodType> criteria = builder.createQuery(PaymentMethodType.class);
			Root<PaymentMethodType> paymentMethodType = criteria.from(PaymentMethodType.class);
			TypedQuery<PaymentMethodType> query;
			
			
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);

			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				query = em.createQuery(criteria.select(paymentMethodType).where(builder.equal(paymentMethodType.get(PaymentMethodType_.locationsId), locationId),
						builder.notEqual(paymentMethodType.get(PaymentMethodType_.status), "D")).orderBy(builder.asc(paymentMethodType.get(PaymentMethodType_.displayName))));
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				query = em.createQuery(criteria.select(paymentMethodType).where(builder.equal(paymentMethodType.get(PaymentMethodType_.locationsId), locationId),
					builder.notEqual(paymentMethodType.get(PaymentMethodType_.status), "D")).orderBy(builder.desc(paymentMethodType.get(PaymentMethodType_.displayName))));
			
			}
			else
			{
				query = em.createQuery(criteria.select(paymentMethodType).where(builder.equal(paymentMethodType.get(PaymentMethodType_.locationsId), locationId),
						builder.notEqual(paymentMethodType.get(PaymentMethodType_.status), "D")).orderBy(builder.asc(paymentMethodType.get(PaymentMethodType_.displaySequence))));
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment transaction type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the payment transaction type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentTransactionTypeByLocationId/{locationId}")
	public String getPaymentTransactionTypeByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentTransactionType> criteria = builder.createQuery(PaymentTransactionType.class);
			Root<PaymentTransactionType> paymentTransactionType = criteria.from(PaymentTransactionType.class);
			TypedQuery<PaymentTransactionType> query = em.createQuery(criteria.select(paymentTransactionType).where(
					builder.equal(paymentTransactionType.get(PaymentTransactionType_.locationsId), locationId), builder.notEqual(paymentTransactionType.get(PaymentTransactionType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment method type by id.
	 *
	 * @param id
	 *            the id
	 * @return the payment method type by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentMethodTypeById/{id}")
	public String getPaymentMethodTypeById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethodType> criteria = builder.createQuery(PaymentMethodType.class);
			Root<PaymentMethodType> paymentMethodType = criteria.from(PaymentMethodType.class);
			TypedQuery<PaymentMethodType> query = em.createQuery(criteria.select(paymentMethodType).where(builder.equal(paymentMethodType.get(PaymentMethodType_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment method type by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the payment method type by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentMethodTypeByLocationIdAndName/{locationId}/{name}")
	public String getPaymentMethodTypeByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethodType> criteria = builder.createQuery(PaymentMethodType.class);
			Root<PaymentMethodType> paymentMethodType = criteria.from(PaymentMethodType.class);
			TypedQuery<PaymentMethodType> query = em.createQuery(criteria.select(paymentMethodType).where(builder.equal(paymentMethodType.get(PaymentMethodType_.locationsId), locationId),
					builder.equal(paymentMethodType.get(PaymentMethodType_.name), name), builder.notEqual(paymentMethodType.get(PaymentMethodType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment method type by location id and display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return the payment method type by location id and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentMethodTypeByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getPaymentMethodTypeByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select p from PaymentMethodType p where p.status != 'D' and p.displaySequence = " + displaySequence + " and p.locationsId = '" + locationId  + "' order by p.displaySequence asc ";
			TypedQuery<PaymentMethodType> query = em.createQuery(queryString, PaymentMethodType.class);
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment method by location id and payment type id.
	 *
	 * @param locationId
	 *            the location id
	 * @param paymentMethodTypeId
	 *            the payment method type id
	 * @return the payment method by location id and payment type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentMethodByLocationIdAndPaymentTypeId/{locationId}/{paymentMethodTypeId}")
	public String getPaymentMethodByLocationIdAndPaymentTypeId(@PathParam("locationId") String locationId, @PathParam("paymentMethodTypeId") String paymentMethodTypeId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethod> criteria = builder.createQuery(PaymentMethod.class);
			Root<PaymentMethod> paymentMethod = criteria.from(PaymentMethod.class);
			TypedQuery<PaymentMethod> query = em.createQuery(criteria.select(paymentMethod).where(builder.equal(paymentMethod.get(PaymentMethod_.locationsId), locationId),
					builder.equal(paymentMethod.get(PaymentMethod_.paymentMethodTypeId), paymentMethodTypeId), builder.notEqual(paymentMethod.get(PaymentMethod_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment method by location id and payment type id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param paymentMethodTypeId
	 *            the payment method type id
	 * @param name
	 *            the name
	 * @return the payment method by location id and payment type id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentMethodByLocationIdAndPaymentTypeIdAndName/{locationId}/{paymentMethodTypeId}/{name}")
	public String getPaymentMethodByLocationIdAndPaymentTypeIdAndName(@PathParam("locationId") String locationId, @PathParam("paymentMethodTypeId") int paymentMethodTypeId,
			@PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethod> criteria = builder.createQuery(PaymentMethod.class);
			Root<PaymentMethod> paymentMethod = criteria.from(PaymentMethod.class);
			TypedQuery<PaymentMethod> query = em.createQuery(criteria.select(paymentMethod).where(builder.equal(paymentMethod.get(PaymentMethod_.locationsId), locationId),
					builder.equal(paymentMethod.get(PaymentMethod_.paymentMethodTypeId), paymentMethodTypeId), builder.equal(paymentMethod.get(PaymentMethod_.name), name),
					builder.notEqual(paymentMethod.get(PaymentMethod_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment method by location id and payment type id and display
	 * sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param paymentMethodTypeId
	 *            the payment method type id
	 * @param displaySequence
	 *            the display sequence
	 * @return the payment method by location id and payment type id and display
	 *         sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentMethodByLocationIdAndPaymentTypeIdAndDisplaySequence/{locationId}/{paymentMethodTypeId}/{displaySequence}")
	public String getPaymentMethodByLocationIdAndPaymentTypeIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("paymentMethodTypeId") int paymentMethodTypeId,
			@PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethod> criteria = builder.createQuery(PaymentMethod.class);
			Root<PaymentMethod> paymentMethod = criteria.from(PaymentMethod.class);
			TypedQuery<PaymentMethod> query = em.createQuery(criteria.select(paymentMethod).where(builder.equal(paymentMethod.get(PaymentMethod_.displaySequence), displaySequence),
					builder.equal(paymentMethod.get(PaymentMethod_.locationsId), locationId), builder.equal(paymentMethod.get(PaymentMethod_.paymentMethodTypeId), paymentMethodTypeId),
					builder.notEqual(paymentMethod.get(PaymentMethod_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment method by id.
	 *
	 * @param id
	 *            the id
	 * @return the payment method by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentMethodById/{id}")
	public String getPaymentMethodById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethod> criteria = builder.createQuery(PaymentMethod.class);
			Root<PaymentMethod> paymentMethod = criteria.from(PaymentMethod.class);
			TypedQuery<PaymentMethod> query = em.createQuery(criteria.select(paymentMethod).where(builder.equal(paymentMethod.get(PaymentMethod_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllCourses")
	public String getAllCourses(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
			Root<Course> r = criteria.from(Course.class);
			TypedQuery<Course> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all courses by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all courses by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCourseByLocationId/{locationId}")
	public String getAllCoursesByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
			Root<Course> r = criteria.from(Course.class);
			TypedQuery<Course> query = null;
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Course_.locationsId), locationId), builder.notEqual(r.get(Course_.status), "D"))
						.orderBy(builder.asc(r.get(Course_.displayName))));
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Course_.locationsId), locationId), builder.notEqual(r.get(Course_.status), "D"))
						.orderBy(builder.desc(r.get(Course_.displayName))));
			}
			else
			{
				query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Course_.locationsId), locationId), builder.notEqual(r.get(Course_.status), "D"))
						.orderBy(builder.asc(r.get(Course_.displaySequence))));
			}
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the courses by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the courses by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCoursesByLocationId/{locationId}")
	public String getCoursesByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
			Root<Course> r = criteria.from(Course.class);
			TypedQuery<Course> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Course_.locationsId), locationId), builder.notEqual(r.get(Course_.status), "D"))
					.orderBy(builder.asc(r.get(Course_.displaySequence))));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the course by id.
	 *
	 * @param id
	 *            the id
	 * @return the course by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCourseById/{id}")
	public String getCourseById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
			Root<Course> course = criteria.from(Course.class);
			TypedQuery<Course> query = em.createQuery(criteria.select(course).where(builder.equal(course.get(Course_.id), id)));
			Course p = query.getSingleResult();
			String queryString = "select l from Location l where l.id in   (select p.locationsId from Course p where p.globalCourseId=?  and p.status not in ('D')) ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());
			List<Location> resultSet = query2.getResultList();
			p.setLocationList(resultSet);

			return new JSONUtility(httpRequest).convertToJsonString(p);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the course by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the course by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCourseByLocationIdAndName/{locationId}/{name}")
	public String getCourseByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
			Root<Course> course = criteria.from(Course.class);
			TypedQuery<Course> query = em.createQuery(criteria.select(course).where(builder.equal(course.get(Course_.locationsId), locationId), builder.equal(course.get(Course_.courseName), name),
					builder.notEqual(course.get(Course_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the course by location id and display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return the course by location id and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCourseByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getCourseByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
			Root<Course> course = criteria.from(Course.class);
			TypedQuery<Course> query = em.createQuery(criteria.select(course).where(builder.equal(course.get(Course_.locationsId), locationId),
					builder.equal(course.get(Course_.displaySequence), displaySequence), builder.notEqual(course.get(Course_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * All items char.
	 *
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allItemsChar")
	public String allItemsChar(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsChar> criteria = builder.createQuery(ItemsChar.class);
			Root<ItemsChar> r = criteria.from(ItemsChar.class);
			TypedQuery<ItemsChar> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all items char by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all items char by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllItemsCharByLocationId/{locationId}")
	public String getAllItemsCharByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsChar> criteria = builder.createQuery(ItemsChar.class);
			Root<ItemsChar> r = criteria.from(ItemsChar.class);
			TypedQuery<ItemsChar> query = null;
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);

			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsChar_.locationsId), locationId), builder.notEqual(r.get(ItemsChar_.status), "D"))
						.orderBy(builder.asc(r.get(ItemsChar_.displayName))));
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsChar_.locationsId), locationId), builder.notEqual(r.get(ItemsChar_.status), "D"))
						.orderBy(builder.asc(r.get(ItemsChar_.displayName))));
			}
			else
			{
				query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsChar_.locationsId), locationId), builder.notEqual(r.get(ItemsChar_.status), "D"))
						.orderBy(builder.asc(r.get(ItemsChar_.sortSequence))));
			}
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the items char by id.
	 *
	 * @param id
	 *            the id
	 * @return the items char by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsCharById/{id}")
	public String getItemsCharById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsChar> criteria = builder.createQuery(ItemsChar.class);
			Root<ItemsChar> ic = criteria.from(ItemsChar.class);
			TypedQuery<ItemsChar> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ItemsChar_.id), id)));
			ItemsChar p = query.getSingleResult();
			String queryString = "select l from Location l where l.id in   (select p.locationsId from ItemsChar p where p.globalItemCharId=? and p.status not in ('D')) ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());
			List<Location> resultSet = query2.getResultList();
			p.setLocationList(resultSet);
			return new JSONUtility(httpRequest).convertToJsonString(p);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the items char by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the items char by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsCharByLocationIdAndName/{locationId}/{name}")
	public String getItemsCharByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			;

			CriteriaQuery<ItemsChar> criteria = builder.createQuery(ItemsChar.class);
			Root<ItemsChar> ic = criteria.from(ItemsChar.class);
			TypedQuery<ItemsChar> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ItemsChar_.locationsId), locationId), builder.equal(ic.get(ItemsChar_.name), name),
					builder.notEqual(ic.get(ItemsChar_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the items char by location id and display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return the items char by location id and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsCharByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getItemsCharByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			;
			CriteriaQuery<ItemsChar> criteria = builder.createQuery(ItemsChar.class);
			Root<ItemsChar> ic = criteria.from(ItemsChar.class);
			TypedQuery<ItemsChar> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ItemsChar_.locationsId), locationId),
					builder.equal(ic.get(ItemsChar_.sortSequence), displaySequence), builder.notEqual(ic.get(ItemsChar_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the floorplan.
	 *
	 * @param floorPlanPacket
	 *            the floor plan packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addFloorplan")
	public String addFloorplan(FloorPlanPacket floorPlanPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, floorPlanPacket);

			floorPlanPacket.getFloorplan().setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			floorPlanPacket.getFloorplan().setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			EntityTransaction tx = em.getTransaction();
			try
			{
				// start transaction
				tx.begin();
				em.persist(floorPlanPacket.getFloorplan());
				tx.commit();
			}
			catch (RuntimeException e)
			{
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive())
				{
					tx.rollback();
				}
				throw e;
			}
			String json = new StoreForwardUtility().returnJsonPacket(floorPlanPacket, "FloorPlanPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, floorPlanPacket.getLocationId(), Integer.parseInt(floorPlanPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(floorPlanPacket.getFloorplan());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all floorplan by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all floorplan by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllFloorplanByLocationId/{locationId}")
	public String getAllFloorplanByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Floorplan> criteria = builder.createQuery(Floorplan.class);
			Root<Floorplan> r = criteria.from(Floorplan.class);
			TypedQuery<Floorplan> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Floorplan_.locationsId), locationId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all items attribute type.
	 *
	 * @return the all items attribute type
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allItemsAttributeType")
	public String getAllItemsAttributeType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsAttributeType> criteria = builder.createQuery(ItemsAttributeType.class);
			Root<ItemsAttributeType> r = criteria.from(ItemsAttributeType.class);
			TypedQuery<ItemsAttributeType> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all items attribute type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all items attribute type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllItemsAttributeTypeByLocationId/{locationId}")
	public String getAllItemsAttributeTypeByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();

			CriteriaQuery<ItemsAttributeType> criteria = builder.createQuery(ItemsAttributeType.class);
			Root<ItemsAttributeType> r = criteria.from(ItemsAttributeType.class);
			TypedQuery<ItemsAttributeType> query = null;
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsAttributeType_.locationsId), locationId), builder.notEqual(r.get(ItemsAttributeType_.status), "D"))
						.orderBy(builder.asc(r.get(ItemsAttributeType_.displayName))));

			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsAttributeType_.locationsId), locationId), builder.notEqual(r.get(ItemsAttributeType_.status), "D"))
						.orderBy(builder.desc(r.get(ItemsAttributeType_.displayName))));

			}
			else
			{
				query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsAttributeType_.locationsId), locationId), builder.notEqual(r.get(ItemsAttributeType_.status), "D"))
						.orderBy(builder.asc(r.get(ItemsAttributeType_.sortSequence))));

			}
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the items attribute type by id.
	 *
	 * @param id
	 *            the id
	 * @return the items attribute type by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsAttributeTypeById/{id}")
	public String getItemsAttributeTypeById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsAttributeType> criteria = builder.createQuery(ItemsAttributeType.class);
			Root<ItemsAttributeType> ic = criteria.from(ItemsAttributeType.class);
			// todo shlok need to handle exception in below line
			TypedQuery<ItemsAttributeType> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ItemsAttributeType_.id), id)));
			ItemsAttributeType p = query.getSingleResult();
			String queryString = "select l from Location l where l.id in  (select p.locationsId from ItemsAttributeType p where p.globalItemAttributeTypeId=? and p.status not in ('D')) ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());
			List<Location> resultSet = query2.getResultList();
			p.setLocationList(resultSet);
			return new JSONUtility(httpRequest).convertToJsonString(p);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the items attribute type by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the items attribute type by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsAttributeTypeByLocationIdAndName/{locationId}/{name}")
	public String getItemsAttributeTypeByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsAttributeType> criteria = builder.createQuery(ItemsAttributeType.class);
			Root<ItemsAttributeType> ic = criteria.from(ItemsAttributeType.class);
			TypedQuery<ItemsAttributeType> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ItemsAttributeType_.locationsId), locationId),
					builder.equal(ic.get(ItemsAttributeType_.name), name), builder.notEqual(ic.get(ItemsAttributeType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the items attribute type by location id and display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return the items attribute type by location id and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsAttributeTypeByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getItemsAttributeTypeByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsAttributeType> criteria = builder.createQuery(ItemsAttributeType.class);
			Root<ItemsAttributeType> ic = criteria.from(ItemsAttributeType.class);
			TypedQuery<ItemsAttributeType> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ItemsAttributeType_.locationsId), locationId),
					builder.equal(ic.get(ItemsAttributeType_.sortSequence), displaySequence), builder.notEqual(ic.get(ItemsAttributeType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all items attribute by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param itemsAttributeTypeId
	 *            the items attribute type id
	 * @return Item attribute array
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllItemsAttributeByLocationIdAndItemsAttributeTypeId/{locationId}/{itemsAttributeTypeId}")
	public String getAllItemsAttributeByLocationId(@PathParam("locationId") String locationId, @PathParam("itemsAttributeTypeId") String itemsAttributeTypeId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			List<ItemsAttribute> ans = new ArrayList<ItemsAttribute>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select items_attribute0_.id as items_attribute0_id, items_attribute0_.created as items_attribute0_created,"
					+ " items_attribute0_.created_by as items_attribute0_created_by,"
					+ " items_attribute0_.is_active as items_attribute0_is_active,"
					+ " items_attribute0_.msr_price as items_attribute0_msr_price,"
					+ " items_attribute0_.multi_select as items_attribute0_multi_select, "
					+ " items_attribute0_.selling_price as items_attribute0_selling_price, "
					+ "items_attribute0_.sort_sequence as items_attribute0_sort_sequence,"
					+ " items_attribute0_.description as items_attribute0_description,"
					+ " items_attribute0_.display_name as items_attribute0_display_name,"
					+ " items_attribute0_.hex_code_values as items_attribute0_hex_code_values, items_attribute0_.image_name as items_attribute0_image_name, "
					+ " items_attribute0_.locations_id as items_attribute0_locations_id, items_attribute0_.name as items_attribute0_name, items_attribute0_.short_name as items_attribute0_short_name,  "
					+ " items_attribute0_.updated as items_attribute0_updated, items_attribute0_.updated_by as items_attribute0_updated_by, items_attribute0_.status as items_attribute0_status, "
					+ " items_attribute0_.image_name as items_attribute0_image_naame"
					+ " from items_attribute items_attribute0_ join items_attribute_type_to_items_attribute items_attribute_type_to_items_attribute0_ "
					+ "where items_attribute0_.id=items_attribute_type_to_items_attribute0_.items_attribute_id " + "and items_attribute0_.locations_id=? " + "and items_attribute0_.status!= 'D' "
					+ "And items_attribute_type_to_items_attribute0_.items_attribute_type_id=?";
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);

			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				sql += " order by items_attribute0_.display_name asc";
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				sql += " order by items_attribute0_.display_name desc";
			}
			else
			{
				sql += " order by items_attribute0_.sort_sequence asc";
			}

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).setParameter(2, itemsAttributeTypeId).getResultList();
			for (Object[] objRow : resultList)
			{
				// if this has primary key not 0
				ItemsAttribute itemsAttribute = new ItemsAttribute();
				itemsAttribute.setId((String) objRow[0]);
				itemsAttribute.setCreated((Date) objRow[1]);
				itemsAttribute.setCreatedBy((String) objRow[2]);
				if (objRow[3] != null)
					itemsAttribute.setIsActive(((Byte) objRow[3]).intValue());
				if ((BigDecimal) objRow[4] != null)
				{
					itemsAttribute.setMsrPrice((BigDecimal) objRow[4]);
				}
				if (objRow[5] != null)
					itemsAttribute.setMultiSelect(((Byte) objRow[5]).intValue());

				if ((BigDecimal) objRow[6] != null)
				{
					itemsAttribute.setSellingPrice((BigDecimal) objRow[6]);
				}
				itemsAttribute.setSortSequence((Integer) objRow[7]);
				if ((String) objRow[8] != null)
				{
					itemsAttribute.setDescription((String) objRow[8]);
				}
				if ((String) objRow[9] != null)
				{
					itemsAttribute.setDisplayName((String) objRow[9]);
				}
				if ((String) objRow[10] != null)
				{
					itemsAttribute.setHexCodeValues((String) objRow[10]);
				}
				if ((String) objRow[11] != null)
				{
					itemsAttribute.setImageName((String) objRow[11]);
				}
				itemsAttribute.setLocationsId((String) objRow[12]);
				if ((String) objRow[13] != null)
				{
					itemsAttribute.setName((String) objRow[13]);
				}
				if ((String) objRow[14] != null)
				{
					itemsAttribute.setShortName((String) objRow[14]);
				}
				itemsAttribute.setUpdated((Date) objRow[15]);
				itemsAttribute.setUpdatedBy((String) objRow[16]);
				if (objRow[17] != null)
				{
					itemsAttribute.setStatus(objRow[17].toString());
				}

				if ((String) objRow[18] != null)
				{
					itemsAttribute.setImageName((String) objRow[18]);
				}

				ans.add(itemsAttribute);

			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the items attribute by id.
	 *
	 * @param id
	 *            the id
	 * @return the items attribute by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsAttributeById/{id}")
	public String getItemsAttributeById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsAttribute> criteria = builder.createQuery(ItemsAttribute.class);
			Root<ItemsAttribute> ic = criteria.from(ItemsAttribute.class);
			TypedQuery<ItemsAttribute> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ItemsAttribute_.id), id)));
			ItemsAttribute p = query.getSingleResult();

			String queryString = "select l from Location l where l.id in  (select p.locationsId from ItemsAttribute p where p.globalId=? and p.status!='D') ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());
			List<Location> resultSet = query2.getResultList();
			p.setLocationList(resultSet);
			return new JSONUtility(httpRequest).convertToJsonString(p);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the items attribute by location id and items attribute type id and
	 * name.
	 *
	 * @param locationId
	 *            the location id
	 * @param itemsAttributeTypeId
	 *            the items attribute type id
	 * @param name
	 *            the name
	 * @return item attribute array
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsAttributeByLocationIdAndItemsAttributeTypeIdAndName/{locationId}/{itemsAttributeTypeId}/{name}")
	public String getItemsAttributeByLocationIdAndItemsAttributeTypeIdAndName(@PathParam("locationId") String locationId, @PathParam("itemsAttributeTypeId") String itemsAttributeTypeId,
			@PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			String queryString = "select ia from ItemsAttribute ia, ItemsAttributeTypeToItemsAttribute iat  " + "where ia.id=iat.itemsAttributeId and ia.name=? and ia.locationsId=? and"
					+ " ia.status!= 'D' AND iat.itemsAttributeTypeId=?";
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TypedQuery<ItemsAttribute> query = em.createQuery(queryString, ItemsAttribute.class).setParameter(1, name).setParameter(2, locationId).setParameter(3, itemsAttributeTypeId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the items attribute by location id and items attribute type id and
	 * display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param itemsAttributeTypeId
	 *            the items attribute type id
	 * @param displaySequence
	 *            the display sequence
	 * @return the items attribute by location id and items attribute type id
	 *         and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsAttributeByLocationIdAndItemsAttributeTypeIdAndDisplaySequence/{locationId}/{itemsAttributeTypeId}/{displaySequence}")
	public String getItemsAttributeByLocationIdAndItemsAttributeTypeIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("itemsAttributeTypeId") String itemsAttributeTypeId,
			@PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			String queryString = "select ia from ItemsAttribute ia, ItemsAttributeTypeToItemsAttribute iat  " + "where ia.id=iat.itemsAttributeId and ia.sortSequence=?" + " and ia.locationsId=? and "
					+ "ia.status!= 'D' AND iat.itemsAttributeTypeId=?";
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TypedQuery<ItemsAttribute> query = em.createQuery(queryString, ItemsAttribute.class).setParameter(1, displaySequence).setParameter(2, locationId).setParameter(3, itemsAttributeTypeId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * All reservations schedule.
	 *
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allReservationsSchedule")
	public String allReservationsSchedule(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsSchedule> criteria = builder.createQuery(ReservationsSchedule.class);
			Root<ReservationsSchedule> r = criteria.from(ReservationsSchedule.class);
			TypedQuery<ReservationsSchedule> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all reservations schedule by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all reservations schedule by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsScheduleByLocationId/{locationId}")
	public String getAllReservationsScheduleByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			String queryString = "SELECT res FROM ReservationsSchedule res " + " " + " where res.locationId=? and res.status!='D'";

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TypedQuery<ReservationsSchedule> query = em.createQuery(queryString, ReservationsSchedule.class).setParameter(1, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all reservations schedule by location id and order source group
	 * id.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the all reservations schedule by location id and order source
	 *         group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsScheduleByLocationIdAndOrderSourceGroupId/{locationId}/{orderSourceGroupId}")
	public String getAllReservationsScheduleByLocationIdAndOrderSourceGroupId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("orderSourceGroupId") String orderSourceGroupId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsSchedule> criteria = builder.createQuery(ReservationsSchedule.class);
			Root<ReservationsSchedule> r = criteria.from(ReservationsSchedule.class);
			TypedQuery<ReservationsSchedule> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsSchedule_.locationId), locationId),
					builder.notEqual(r.get(ReservationsSchedule_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all reservations schedule by location id and week.
	 *
	 * @param locationId
	 *            the location id
	 * @param week
	 *            the week
	 * @return the all reservations schedule by location id and week
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsScheduleByLocationIdAndWeek/{locationId}/{week}")
	public String getAllReservationsScheduleByLocationIdAndWeek(@PathParam("locationId") String locationId, @PathParam("week") int week, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsSchedule> criteria = builder.createQuery(ReservationsSchedule.class);
			Root<ReservationsSchedule> r = criteria.from(ReservationsSchedule.class);
			TypedQuery<ReservationsSchedule> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsSchedule_.locationId), locationId),
					builder.notEqual(r.get(ReservationsSchedule_.status), "D"), builder.lessThanOrEqualTo(r.get(ReservationsSchedule_.startWeek), week),
					builder.greaterThanOrEqualTo(r.get(ReservationsSchedule_.endWeek), week)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// to check the time for reservation schedule
	/**
	 * Gets the all reservations schedule for validation.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the all reservations schedule for validation
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/getAllReservationsScheduleForValidation")
	public String getAllReservationsScheduleForValidation(ReservationSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			ReservationsSchedule res = lookupPacket.getReservationsSchedule();

			String queryString = "SELECT res FROM ReservationsSchedule res,ReservationsScheduleDay res_day " + " where res.id=res_day.reservationsScheduleId and ((res.fromTime between '"
					+ res.getFromTime() + "' and '" + res.getToTime() + "')" + " or (res.toTime between '" + res.getFromTime() + "' and '" + res.getToTime() + "' )" + " or (res.fromTime < '"
					+ res.getFromTime() + "' and res.toTime > '" + res.getToTime() + "')) " + " and ((res.fromDate between '" + res.getFromDate() + "' and '" + res.getToDate() + "')"
					+ " or (res.toDate between '" + res.getFromDate() + "' and '" + res.getToDate() + "' ))" + " and res.locationId='" + res.getLocationId()
					+ "' and res.status!='D' and res_day.daysId in (";

			for (ReservationsScheduleDay day : res.getReservationsScheduleDays())
			{
				queryString = queryString + " " + day.getDaysId() + ",";
			}
			queryString = queryString.substring(0, queryString.length() - 1);
			queryString = queryString + ")";

			if (res.getId() != null)
			{
				queryString = queryString + " and res.id!=" + "'"+res.getId()+ "'";
			}
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TypedQuery<ReservationsSchedule> query = em.createQuery(queryString, ReservationsSchedule.class);
			
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all reservations schedule for validation with order source group
	 * id.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the all reservations schedule for validation with order source
	 *         group id
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/getAllReservationsScheduleForValidationWithOrderSourceGroupId")
	public String getAllReservationsScheduleForValidationWithOrderSourceGroupId(ReservationSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			ReservationsSchedule res = lookupPacket.getReservationsSchedule();

			String queryString = "SELECT res FROM ReservationsSchedule res,ReservationsScheduleDay res_day " + " " + " where " + " res.id=res_day.reservationsScheduleId and ((res.fromTime between '"
					+ res.getFromTime() + "' and '" + res.getToTime() + "')" + " or (res.toTime between '" + res.getFromTime() + "' and '" + res.getToTime() + "' )" + " or (res.fromTime < '"
					+ res.getFromTime() + "' and res.toTime > '" + res.getToTime() + "')) " + " and ((res.fromDate between '" + res.getFromDate() + "' and '" + res.getToDate() + "')"
					+ " or (res.toDate between '" + res.getFromDate() + "' and '" + res.getToDate() + "' ))" + " and res.locationId='" + res.getLocationId()
					+ "' and res.status!='D' and res_day.daysId in (";

			for (ReservationsScheduleDay day : res.getReservationsScheduleDays())
			{
				queryString = queryString + " " + day.getDaysId() + ",";
			}
			queryString = queryString.substring(0, queryString.length() - 1);
			queryString = queryString + ")";

			if (res.getId() != null)
			{
				queryString = queryString + " and res.id!=" + res.getId();
			}
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TypedQuery<ReservationsSchedule> query = em.createQuery(queryString, ReservationsSchedule.class);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ReservationSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all xref by reservation schedule id and from time and to time.
	 *
	 * @param reservationScheduleId
	 *            the reservation schedule id
	 * @param fromTime
	 *            the from time
	 * @param toTime
	 *            the to time
	 * @param id
	 *            the id
	 * @return the all xref by reservation schedule id and from time and to time
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllXrefByReservationScheduleIdAndFromTimeAndToTime/{reservationScheduleId}/{fromTime}/{toTime}/{id}")
	public String getAllXrefByReservationScheduleIdAndFromTimeAndToTime(@PathParam("reservationScheduleId") String reservationScheduleId, @PathParam("fromTime") String fromTime,
			@PathParam("toTime") String toTime, @PathParam("id") int id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			String queryString = "SELECT res FROM ReservationsScheduleXref res " + " where ((res.fromTime between ? and ? )" + " or (res.toTime between ? and ? )"
					+ "or (res.fromTime < ? and res.toTime > ?)) " + " and res.reservationsScheduleId=?  and status!='D'";
			if (id != 0)
			{
				queryString = queryString + " and res.id!=" + id;
			}
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TypedQuery<ReservationsScheduleXref> query = em.createQuery(queryString, ReservationsScheduleXref.class).setParameter(1, fromTime).setParameter(2, toTime).setParameter(3, fromTime)
					.setParameter(4, toTime).setParameter(5, fromTime).setParameter(6, toTime).setParameter(7, reservationScheduleId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the reservations schedule by id.
	 *
	 * @param id
	 *            the id
	 * @return the reservations schedule by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getReservationsScheduleById/{id}")
	public String getReservationsScheduleById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsSchedule> criteria = builder.createQuery(ReservationsSchedule.class);
			Root<ReservationsSchedule> ic = criteria.from(ReservationsSchedule.class);
			TypedQuery<ReservationsSchedule> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ReservationsSchedule_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	

	/**
	 * Gets the all function.
	 *
	 * @return the all function
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllFunction")
	public String getAllFunction(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Functions> criteria = builder.createQuery(Functions.class);
			Root<Functions> function = criteria.from(Functions.class);
			TypedQuery<Functions> query = em.createQuery(criteria.select(function));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * All day.
	 *
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allDay")
	public String allDay(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Day> criteria = builder.createQuery(Day.class);
			Root<Day> r = criteria.from(Day.class);
			TypedQuery<Day> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the day by id.
	 *
	 * @param id
	 *            the id
	 * @return the day by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDayById/{id}")
	public String getDayById(@PathParam("id") int id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Day> criteria = builder.createQuery(Day.class);

			Root<Day> r = criteria.from(Day.class);
			TypedQuery<Day> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Day_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all cdo mgmt.
	 *
	 * @return the all cdo mgmt
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCdoMgmt")
	public String getAllCdoMgmt(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CdoMgmt> criteria = builder.createQuery(CdoMgmt.class);
			Root<CdoMgmt> r = criteria.from(CdoMgmt.class);
			TypedQuery<CdoMgmt> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all cdo mgmt by update date.
	 *
	 * @param updatedDate
	 *            the updated date
	 * @return the all cdo mgmt by update date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCdoMgmtByUpdatedDate/{updatedDate}")
	public String getAllCdoMgmtByUpdateDate(@PathParam("updatedDate") Timestamp updatedDate, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CdoMgmt> criteria = builder.createQuery(CdoMgmt.class);
			Root<CdoMgmt> r = criteria.from(CdoMgmt.class);
			TypedQuery<CdoMgmt> query = em.createQuery(criteria.select(r).where(builder.greaterThan(r.get(CdoMgmt_.updated), updatedDate)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the lookup values.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/addLookupValues/{userId}/{locationId}")
	public boolean addLookupValues(@PathParam("userId") String userId, @PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			tx = em.getTransaction();
			tx.begin();

			String globalLocationId = getGlobalLocationId(em);

			LookupUtil lookupUtil = new LookupUtil();

			lookupUtil.addLocationToFunctionConstants(em, locationId, userId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addDiscountTypeConstant(em, userId, locationId, globalLocationId);
			tx.commit();
			tx.begin();
			lookupUtil.addUnitOfMeasurementConstant(em, userId, locationId, globalLocationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addReservationConstants(em, userId, locationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addOrderSourseGroupConstants(em, userId, locationId, globalLocationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addContactPreferenceConstants(em, userId, locationId);
			tx.commit();
			tx.begin();
			lookupUtil.addRequestTypeConstants(em, userId, locationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addCourseConstants(em, userId, locationId, globalLocationId);
			tx.commit();
			tx.begin();
			lookupUtil.addPaymentWaysConstant(em, userId, locationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addReservationTypeConstant(em, userId, locationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addPaymentMethodTypeConstant(em, userId, locationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addPrinterConstant(em, userId, locationId, globalLocationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addFeedbackConstants(em, userId, locationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addReasonTypeConstant(em, locationId, userId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addEmployeeOperations(em, userId, locationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addRolesToFunctions(em, locationId, userId);
			tx.commit();
			tx.begin();
			lookupUtil.addCategoryConstant(em, locationId, userId, globalLocationId);
			tx.commit();
			tx.begin();
			lookupUtil.addEmailConstant(em, locationId, userId);
			tx.commit();
			tx.begin();
			lookupUtil.addSMSTemplateConstant(em, locationId, userId);
			tx.commit();
			tx.begin();
			// added by Apoorv to add first batch of the business
//			lookupUtil.addBatchDetail(em, locationId, userId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addSalesTax(em, locationId, userId, globalLocationId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addDeliveryOption(em, locationId, userId,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addOrderAdditionalQuestionsn(em, userId,httpRequest);
			tx.commit();
			tx.begin();
			// Removed by Uzma Instruction
			// lookupUtil.addPaymentGatewayConstants(em, userId, locationId);

			tx.commit();
			return true;
		}
		catch (RuntimeException e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the local and global values for account creation.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/addLocalAndGlobalValuesForAccountCreation/{userId}/{locationId}")
	public boolean addLocalAndGlobalValuesForAccountCreation(@PathParam("userId") String userId, @PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			tx = em.getTransaction();
			tx.begin();

			String globalLocationId = getGlobalLocationId(em);
			LookupUtil lookupUtil = new LookupUtil();

			lookupUtil.deleteAllUnitOfMeasurementConstant(em, globalLocationId);
			tx.commit();
			tx.begin();
			// adding for global table
			lookupUtil.addUnitOfMeasurementConstant(em, userId, globalLocationId, null,httpRequest);
			tx.commit();
			tx.begin();
			String oGroupDineIn = lookupUtil.getOrderSourceGroup(em, "In Store", globalLocationId);
			tx.commit();
			tx.begin();
			lookupUtil.addOrderDetailStatusConstants(em, userId, globalLocationId, oGroupDineIn);
			tx.commit();
			tx.begin();
			lookupUtil.addOrderStatus1Constants(em, userId, globalLocationId, oGroupDineIn,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addOrderStatusForInventory(em, userId, globalLocationId, oGroupDineIn,httpRequest);
			tx.commit();
			tx.begin();
			lookupUtil.addEmailConstant(em, globalLocationId, userId);

			// find root location that is not global location
			TypedQuery<Location> queryL = em.createQuery("select l from Location l where " + "(l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = 1 and l.isGlobalLocation = 0 ", Location.class);
			List<Location> locationList = queryL.getResultList();

			if (locationList != null)
			{
				for (Location location : locationList)
				{
					lookupUtil.addUnitOfMeasurementConstant(em, "21", location.getId(), globalLocationId,httpRequest);
					lookupUtil.addServedOrderStatusConstants(em, userId, location.getId(),httpRequest);
					lookupUtil.addEmailConstant(em, location.getId(), userId);

				}
			}

			tx.commit();
			return true;
		}
		catch (RuntimeException e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the demo data.
	 *
	 * @param businessId
	 *            the business id
	 * @param locationId
	 *            the location id
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/addDemoData/{businessId}/{locationId}")
	public boolean addDemoData(@PathParam("businessId") int businessId, @PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			boolean result = false;
			int resultList = 0;
			String queryString = " call usp_accountdemodata( ?,? )";

			EntityTransaction tx = em.getTransaction();
			try
			{
				tx.begin();
				resultList = em.createNativeQuery(queryString).setParameter(1, businessId).setParameter(2, locationId).executeUpdate();
				tx.commit();
			}
			catch (RuntimeException e)
			{
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive())
				{
					tx.rollback();
				}
				// TODO Ankur - why shouldn't we throw exception here?
				throw e;
			}
			if (resultList > 0)
			{
				result = true;
			}

			int resultListForScript = 0;
			String queryStringForScript = " call p_globalDataGeneration()";

			EntityTransaction txForScript = em.getTransaction();
			try
			{
				txForScript.begin();
				resultListForScript = em.createNativeQuery(queryStringForScript).executeUpdate();
				txForScript.commit();
			}
			catch (RuntimeException e)
			{
				if (txForScript != null && txForScript.isActive())
				{
					txForScript.rollback();
				}
				throw e;
			}
			if (resultListForScript > 0)
			{
				result = true;
			}

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Location> criteria = builder.createQuery(Location.class);
			Root<Location> r = criteria.from(Location.class);
			TypedQuery<Location> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Location_.isGlobalLocation), 1)));
			Location globalLocation = null;
			globalLocation = query.getSingleResult();
			globalLocation.setName("Global Location");
			globalLocation.setBusinessId(0);
			em.getTransaction().begin();
			globalLocation = em.merge(globalLocation);
			em.getTransaction().commit();

			LocationSetting locationSetting= new LocationSetting();
			locationSetting.setLocationId(globalLocation.getId());
			locationSetting.setStatus("A");
			locationSetting.setCreatedBy("1");
			locationSetting.setUpdatedBy("1");
			em.getTransaction().begin();
			locationSetting.setId(new StoreForwardUtility().generateUUID());
			locationSetting = em.merge(locationSetting);
			em.getTransaction().commit();
		return result;
		}
		catch (RuntimeException e)
		{
			if (em != null && em.getTransaction() != null && em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Sync cdo by cdo name and updated date.
	 *
	 * @param cdoName
	 *            the cdo name
	 * @param updatedDate
	 *            the updated date
	 * @return CDO database
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/syncCdoByCdoNameAndUpdatedDate/{cdoName}/{updatedDate}")
	public String syncCdoByCdoNameAndUpdatedDate(@PathParam("cdoName") String cdoName, @PathParam("updatedDate") String updatedDate, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		LookupServiceBean bean = new LookupServiceBean();
		EntityManager em = null;
		try
		{
			// By Apoorva
			// for getting non deleted records we set isLogin =1, if we want
			// deleted records then we need to pass 0
			int isLogin = 1;
			int isLocationSpecific = 0;

			if (!"countries".equals(cdoName))
			{
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			}
			else
			{
				em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			}

			return bean.syncCdoByCdoNameAndUpdatedDateRecords(cdoName, updatedDate, sessionId, em, httpRequest, isLogin,isLocationSpecific,"0");
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the reservations status.
	 *
	 * @param reservationStatusPacket
	 *            the reservation status packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addReservationsStatus")
	public String addReservationsStatus(ReservationStatusPacket reservationStatusPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, reservationStatusPacket);

			tx = em.getTransaction();
			tx.begin();
			ReservationsStatus reservationsStatus = new LookupServiceBean().addReservationsStatus(reservationStatusPacket.getReservationsStatus(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addReservationsStatus.name(), reservationStatusPacket);
			reservationStatusPacket.setReservationsStatus(reservationsStatus);
			String json = new StoreForwardUtility().returnJsonPacket(reservationStatusPacket, "ReservationStatusPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reservationStatusPacket.getLocationId(), Integer.parseInt(reservationStatusPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update reservations status.
	 *
	 * @param reservationStatusPacket
	 *            the reservation status packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateReservationsStatus")
	public String updateReservationsStatus(ReservationStatusPacket reservationStatusPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, reservationStatusPacket);

			tx = em.getTransaction();
			tx.begin();
			ReservationsStatus reservationsStatus = new LookupServiceBean().updateReservationsStatus(reservationStatusPacket.getReservationsStatus(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateReservationsStatus.name(), reservationStatusPacket);
			String json = new StoreForwardUtility().returnJsonPacket(reservationStatusPacket, "ReservationStatusPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reservationStatusPacket.getLocationId(), Integer.parseInt(reservationStatusPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete reservations status.
	 *
	 * @param reservationStatusPacket
	 *            the reservation status packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteReservationsStatus")
	public String deleteReservationsStatus(ReservationStatusPacket reservationStatusPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, reservationStatusPacket);

			tx = em.getTransaction();
			tx.begin();
			ReservationsStatus reservationsStatus = new LookupServiceBean().deleteReservationsStatus(reservationStatusPacket.getReservationsStatus(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteReservationsStatus.name(), reservationStatusPacket);
			String json = new StoreForwardUtility().returnJsonPacket(reservationStatusPacket, "ReservationStatusPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reservationStatusPacket.getLocationId(), Integer.parseInt(reservationStatusPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the feedback question.
	 *
	 * @param feedbackQuestionPacket
	 *            the feedback question packet
	 * @return the feedback question
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addFeedbackQuestion")
	public FeedbackQuestion addFeedbackQuestion(FeedbackQuestionPacket feedbackQuestionPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, feedbackQuestionPacket);

			tx = em.getTransaction();
			tx.begin();
			FeedbackQuestion reservationsStatus = new LookupServiceBean().addFeedbackQuestion(feedbackQuestionPacket.getFeedbackQuestion(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addFeedbackQuestion.name(), feedbackQuestionPacket);
			String json = new StoreForwardUtility().returnJsonPacket(feedbackQuestionPacket, "FeedbackQuestionPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, feedbackQuestionPacket.getLocationId(), Integer.parseInt(feedbackQuestionPacket.getMerchantId()));
			
			return reservationsStatus;
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update feedback question.
	 *
	 * @param feedbackQuestionPacket
	 *            the feedback question packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateFeedbackQuestion")
	public String updateFeedbackQuestion(FeedbackQuestionPacket feedbackQuestionPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, feedbackQuestionPacket);

			tx = em.getTransaction();
			tx.begin();
			FeedbackQuestion reservationsStatus = new LookupServiceBean().updateFeedbackQuestion(feedbackQuestionPacket.getFeedbackQuestion(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateFeedbackQuestion.name(), feedbackQuestionPacket);
			String json = new StoreForwardUtility().returnJsonPacket(feedbackQuestionPacket, "FeedbackQuestionPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, feedbackQuestionPacket.getLocationId(), Integer.parseInt(feedbackQuestionPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete feedback question.
	 *
	 * @param feedbackQuestionPacket
	 *            the feedback question packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteFeedbackQuestion")
	public String deleteFeedbackQuestion(FeedbackQuestionPacket feedbackQuestionPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, feedbackQuestionPacket);

			tx = em.getTransaction();
			tx.begin();
			FeedbackQuestion reservationsStatus = new LookupServiceBean().deleteFeedbackQuestion(feedbackQuestionPacket.getFeedbackQuestion(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteFeedbackQuestion.name(), feedbackQuestionPacket);
			String json = new StoreForwardUtility().returnJsonPacket(feedbackQuestionPacket, "FeedbackQuestionPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,feedbackQuestionPacket.getLocationId(), Integer.parseInt(feedbackQuestionPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the feedback field.
	 *
	 * @param feedbackFieldPacket
	 *            the feedback field packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addFeedbackField")
	public String addFeedbackField(FeedbackFieldPacket feedbackFieldPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, feedbackFieldPacket);

			tx = em.getTransaction();
			tx.begin();
			FeedbackField reservationsStatus = new LookupServiceBean().addFeedbackField(feedbackFieldPacket.getFeedbackField(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addFeedbackField.name(), feedbackFieldPacket);
			String json = new StoreForwardUtility().returnJsonPacket(feedbackFieldPacket, "FeedbackFieldPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, feedbackFieldPacket.getLocationId(), Integer.parseInt(feedbackFieldPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update feedback field.
	 *
	 * @param feedbackFieldPacket
	 *            the feedback field packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateFeedbackField")
	public String updateFeedbackField(FeedbackFieldPacket feedbackFieldPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, feedbackFieldPacket);

			tx = em.getTransaction();
			tx.begin();
			FeedbackField reservationsStatus = new LookupServiceBean().updateFeedbackField(feedbackFieldPacket.getFeedbackField(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateFeedbackField.name(), feedbackFieldPacket);
			String json = new StoreForwardUtility().returnJsonPacket(feedbackFieldPacket, "FeedbackFieldPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, feedbackFieldPacket.getLocationId(), Integer.parseInt(feedbackFieldPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete feedback field.
	 *
	 * @param feedbackFieldPacket
	 *            the feedback field packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteFeedbackField")
	public String deleteFeedbackField(FeedbackFieldPacket feedbackFieldPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, feedbackFieldPacket);

			tx = em.getTransaction();
			tx.begin();
			FeedbackField reservationsStatus = new LookupServiceBean().deleteFeedbackField(feedbackFieldPacket.getFeedbackField(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteFeedbackField.name(), feedbackFieldPacket);
			String json = new StoreForwardUtility().returnJsonPacket(feedbackFieldPacket, "FeedbackFieldPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, feedbackFieldPacket.getLocationId(), Integer.parseInt(feedbackFieldPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update feedback field status.
	 *
	 * @param feedbackFieldPacket
	 *            the feedback field packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateFeedbackFieldStatus")
	public String updateFeedbackFieldStatus(FeedbackFieldPacket feedbackFieldPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, feedbackFieldPacket);

			tx = em.getTransaction();
			tx.begin();
			FeedbackField reservationsStatus = new LookupServiceBean().updateFeedbackFieldStatus(feedbackFieldPacket.getFeedbackField(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateFeedbackFieldStatus.name(), feedbackFieldPacket);
			String json = new StoreForwardUtility().returnJsonPacket(feedbackFieldPacket, "FeedbackFieldPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, feedbackFieldPacket.getLocationId(), Integer.parseInt(feedbackFieldPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the reservations type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addReservationsType")
	public String addReservationsType(ReservationsTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ReservationsType reservationsType = new LookupServiceBean().addReservationsType(lookupPacket.getReservationsType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addReservationsType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ReservationsTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(reservationsType);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update reservations type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateReservationsType")
	public String updateReservationsType(ReservationsTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ReservationsType reservationsType = new LookupServiceBean().updateReservationsType(lookupPacket.getReservationsType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateReservationsType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ReservationsTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(reservationsType);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete reservations type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteReservationsType")
	public String deleteReservationsType(ReservationsTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ReservationsType reservationsType = new LookupServiceBean().deleteReservationsType(lookupPacket.getReservationsType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteReservationsType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ReservationsTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(reservationsType);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the request type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addRequestType")
	public String addRequestType(RequestTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			RequestType requestType = new LookupServiceBean().addRequestType(lookupPacket.getRequestType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addRequestType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "RequestTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(requestType);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update request type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateRequestType")
	public String updateRequestType(RequestTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			RequestType requestType = new LookupServiceBean().updateRequestType(lookupPacket.getRequestType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateRequestType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "RequestTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(requestType);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete request type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteRequestType")
	public String deleteRequestType(RequestTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			RequestType requestType = new LookupServiceBean().deleteRequestType(lookupPacket.getRequestType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteRequestType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "RequestTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(requestType);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the contact preference.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addContactPreference")
	public String addContactPreference(ContactPrefrencesPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ContactPreference contactPreference = new LookupServiceBean().addContactPreference(lookupPacket.getContactPreference(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addContactPreference.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ContactPrefrencesPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(contactPreference);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update contact preference.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateContactPreference")
	public String updateContactPreference(ContactPrefrencesPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ContactPreference contactPreference = new LookupServiceBean().updateContactPreference(lookupPacket.getContactPreference(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateContactPreference.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ContactPrefrencesPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(contactPreference);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete contact preference.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteContactPreference")
	public String deleteContactPreference(ContactPrefrencesPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ContactPreference contactPreference = new LookupServiceBean().deleteContactPreference(lookupPacket.getContactPreference(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteContactPreference.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ContactPrefrencesPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(contactPreference);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the reservations schedule.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addReservationsSchedule")
	public String addReservationsSchedule(ReservationSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
		
			ReservationsSchedule reservationsSchedule = new LookupServiceBean().addReservationsSchedule(lookupPacket.getReservationsSchedule(), httpRequest, em);
			long startime = new TimezoneTime().getGMTTimeInMilis();
			tx.commit();
			tx.begin();
			addReservationSlotsByProcedure(reservationsSchedule, em, lookupPacket.getBlockDates());
			long delta = new TimezoneTime().getGMTTimeInMilis() - startime;
			logger.warn(httpRequest, "total time taken to add reservation slots sec = " + delta / 1000);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addReservationsSchedule.name(), lookupPacket);
			lookupPacket.getReservationsSchedule().setId(reservationsSchedule.getId());
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ReservationSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(reservationsSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}

	}

	/**
	 * De activate slot for date.
	 *
	 * @param date
	 *            the date
	 * @param em
	 *            the em
	 */
	private void deActivateSlotForDate(String date, EntityManager em)
	{
		date = date + " 00:00:00";
		TypedQuery<ReservationsSlot> query = em.createQuery("SELECT r from ReservationsSlot r where r.date = ?", ReservationsSlot.class).setParameter(1, date);
		List<ReservationsSlot> reservationsSlotsList = query.getResultList();
		if (reservationsSlotsList != null && reservationsSlotsList.size() > 0)
		{
			for (ReservationsSlot reservationsSlot : reservationsSlotsList)
			{
				reservationsSlot.setStatus("D");

				em.merge(reservationsSlot);

			}
		}

	}

	/**
	 * Delete slots for reservation schedule id.
	 *
	 * @param reservationSchduleId
	 *            the reservation schdule id
	 * @param em
	 *            the em
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void deleteSlotsForReservationScheduleId(String reservationSchduleId, EntityManager em) throws SQLException
	{
		if (reservationSchduleId != null)
		{
			String queryString = "delete from reservation_slots where reservation_schedule_id=? ";

			em.createNativeQuery(queryString).setParameter(1, reservationSchduleId).executeUpdate();

		}
	}

	/**
	 * De activate slot for date and slot start time and end time.
	 *
	 * @param date
	 *            the date
	 * @param slotStartTime
	 *            the slot start time
	 * @param slotEndTime
	 *            the slot end time
	 * @param em
	 *            the em
	 */
	private void deActivateSlotForDateAndSlotStartTimeAndEndTime(String date, String slotStartTime, String slotEndTime, EntityManager em)
	{
		date = date + " 00:00:00";

		TypedQuery<ReservationsSlot> query = em
				.createQuery("SELECT r from ReservationsSlot r where r.date = ? and ((r.slotStartTime between ? and ?) " + "or (r.slotEndTime between ? and ?))", ReservationsSlot.class)
				.setParameter(1, date).setParameter(2, slotStartTime).setParameter(3, slotEndTime).setParameter(4, slotStartTime).setParameter(5, slotEndTime);
		List<ReservationsSlot> reservationsSlotsList = query.getResultList();
		if (reservationsSlotsList != null && reservationsSlotsList.size() > 0)
		{
			for (ReservationsSlot reservationsSlot : reservationsSlotsList)
			{
				reservationsSlot.setStatus("D");
				em.merge(reservationsSlot);
			}
		}

	}

	/**
	 * Update reservations schedule.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateReservationsSchedule")
	public String updateReservationsSchedule(ReservationSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			ReservationsSchedule reservationsSchedule = new LookupServiceBean().updateReservationsSchedule(lookupPacket.getReservationsSchedule(), httpRequest, em);
			tx.commit();
			tx.begin();
			deleteSlotsForReservationScheduleId(reservationsSchedule.getId(), em);
			if (!reservationsSchedule.getStatus().equals("I"))
			{
				addReservationSlotsByProcedure(reservationsSchedule, em, lookupPacket.getBlockDates());
			}
			tx.commit();
			lookupPacket.setReservationsSchedule(reservationsSchedule);
			
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ReservationSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateReservationsSchedule.name(), lookupPacket);
			return new JSONUtility(httpRequest).convertToJsonString(reservationsSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Delete reservations schedule.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteReservationsSchedule")
	public String deleteReservationsSchedule(ReservationSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ReservationsSchedule reservationsSchedule = new LookupServiceBean().deleteReservationsSchedule(lookupPacket.getReservationsSchedule(), httpRequest, em);

			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteReservationsSchedule.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ReservationSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(reservationsSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}
	}

	/**
	 * Update shift timer.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateShiftTimer")
	public String updateShiftTimer(ReservationSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			manageSlotforBlockDate(lookupPacket, em);
			ReservationsSchedule reservationsSchedule = new LookupServiceBean().updateShiftTimer(lookupPacket.getReservationsSchedule(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateShiftTimer.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ReservationSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(reservationsSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update shift day wise.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateShiftDayWise")
	public String updateShiftDayWise(ReservationSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ReservationsSchedule reservationsSchedule = new LookupServiceBean().updateShiftDayWise(lookupPacket.getReservationsSchedule(), httpRequest, em);

			manageSlotforBlockDate(lookupPacket, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateShiftDayWise.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ReservationSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(reservationsSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update shift date wise.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateShiftDateWise")
	public String updateShiftDateWise(ReservationSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ReservationsSchedule reservationsSchedule = new LookupServiceBean().updateShiftDateWise(lookupPacket.getReservationsSchedule(), httpRequest, em);
			manageSlotforBlockDate(lookupPacket, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateShiftDateWise.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ReservationSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(reservationsSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the order status.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addOrderStatus")
	public String addOrderStatus(OrderStatusPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderStatus orderStatus = new LookupServiceBean().addOrderStatus(lookupPacket.getOrderStatus(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addOrderStatus.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "OrderStatusPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order status.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderStatus")
	public String updateOrderStatus(OrderStatusPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderStatus orderStatus = new LookupServiceBean().updateOrderStatus(lookupPacket.getOrderStatus(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateOrderStatus.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "OrderStatusPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete order status.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteOrderStatus")
	public String deleteOrderStatus(OrderStatusPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderStatus orderStatus = new LookupServiceBean().deleteOrderStatus(lookupPacket.getOrderStatus(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteOrderStatus.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "OrderStatusPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the items attribute type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addItemsAttributeType")
	public String addItemsAttributeType(ItemsAttributeTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ItemsAttributeType itemsAttributeType = new LookupServiceBean().addItemsAttributeType(lookupPacket.getItemsAttributeType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addItemsAttributeType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsAttributeTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(itemsAttributeType);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update items attribute type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateItemsAttributeType")
	public String updateItemsAttributeType(ItemsAttributeTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ItemsAttributeType itemsAttributeType = new LookupServiceBean().updateItemsAttributeType(lookupPacket.getItemsAttributeType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateItemsAttributeType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsAttributeTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(itemsAttributeType);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete items attribute type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteItemsAttributeType")
	public String deleteItemsAttributeType(ItemsAttributeTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ItemsAttributeType itemsAttributeType = new LookupServiceBean().deleteItemsAttributeType(lookupPacket.getItemsAttributeType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteItemsAttributeType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsAttributeTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(itemsAttributeType);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the items attribute.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addItemsAttribute")
	public String addItemsAttribute(ItemsAttributePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ItemsAttribute result = new LookupServiceBean().addItemsAttribute((ItemsAttribute) lookupPacket.getItemsAttribute(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addItemsAttribute.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsAttributePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update items attribute.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateItemsAttribute")
	public String updateItemsAttribute(ItemsAttributePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ItemsAttribute result = new LookupServiceBean().updateItemsAttribute((ItemsAttribute) lookupPacket.getItemsAttribute(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateItemsAttribute.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsAttributePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update inline items attribute.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateInlineItemsAttribute")
	public String updateInlineItemsAttribute(ItemsAttributePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ItemsAttribute result = new LookupServiceBean().updateInlineItemsAttribute((ItemsAttribute) lookupPacket.getItemsAttribute(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateInlineItemsAttribute.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsAttributePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete items attribute.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteItemsAttribute")
	public String deleteItemsAttribute(ItemsAttributePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ItemsAttribute result = new LookupServiceBean().deleteItemsAttribute((ItemsAttribute) lookupPacket.getItemsAttribute(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteItemsAttribute.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsAttributePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the items char.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addItemsChar")
	public String addItemsChar(ItemsCharPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ItemsChar itemsChar = new LookupServiceBean().addItemsChar((ItemsChar) lookupPacket.getItemsChar(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addItemsChar.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsCharPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(itemsChar);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update items char.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateItemsChar")
	public String updateItemsChar(ItemsCharPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ItemsChar itemsChar = new LookupServiceBean().updateItemsChar((ItemsChar) lookupPacket.getItemsChar(), httpRequest, em,lookupPacket.getLocationId(),lookupPacket);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addItemsChar.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsCharPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(itemsChar);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete items char.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteItemsChar")
	public String deleteItemsChar(ItemsCharPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ItemsChar itemsChar = new LookupServiceBean().deleteItemsChar((ItemsChar) lookupPacket.getItemsChar(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteItemsChar.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsCharPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(itemsChar);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * *** Course ****.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */

	@POST
	@Path("/addCourse")
	public String addCourse(CoursePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			Course course = new LookupServiceBean().addCourse((Course) lookupPacket.getCourse(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addCourse.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "CoursePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(course);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update course.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateCourse")
	public String updateCourse(CoursePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			Course course = new LookupServiceBean().updateCourse((Course) lookupPacket.getCourse(), httpRequest, em,lookupPacket);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateCourse.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "CoursePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(course);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete course.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteCourse")
	public String deleteCourse(CoursePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			Course course = new LookupServiceBean().deleteCourse((Course) lookupPacket.getCourse(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteCourse.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "CoursePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(course);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the discounts.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addDiscounts")
	public String addDiscounts(DiscountPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			Discount discountResult = new LookupServiceBean().addDiscounts((Discount) lookupPacket.getDiscount(), httpRequest, em, lookupPacket.getDiscount().getDiscountToReasonsList());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addDiscounts.name(), lookupPacket);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addDiscountToReasons.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "DiscountPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(discountResult);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update discounts.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateDiscounts")
	public String updateDiscounts(DiscountPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			Discount discountResult = new LookupServiceBean().updateDiscounts((Discount) lookupPacket.getDiscount(), httpRequest, em, lookupPacket.getDiscount().getDiscountToReasonsList(),lookupPacket);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateDiscounts.name(), lookupPacket);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateDiscountToReasons.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "DiscountPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(discountResult);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete discounts.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteDiscounts")
	public String deleteDiscounts(DiscountPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			Discount discountResult = new LookupServiceBean().deleteDiscounts((Discount) lookupPacket.getDiscount(), httpRequest, em, lookupPacket.getDiscount().getDiscountToReasonsList());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteDiscounts.name(), lookupPacket);
			// todo shlok need
			// why push for every time
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteDiscountToReasons.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "DiscountPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(discountResult);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the discounts type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addDiscountsType")
	public String addDiscountsType(DiscountsTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			DiscountsType discountsTypeResult = new LookupServiceBean().addDiscountsType((DiscountsType) lookupPacket.getDiscountsType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addDiscountsType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "DiscountsTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(discountsTypeResult);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update discounts type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateDiscountsType")
	public String updateDiscountsType(DiscountsTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			DiscountsType discountsTypeResult = new LookupServiceBean().updateDiscountsType((DiscountsType) lookupPacket.getDiscountsType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateDiscountsType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "DiscountsTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(discountsTypeResult);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete discounts type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteDiscountsType")
	public String deleteDiscountsType(DiscountsTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			DiscountsType discountsTypeResult = new LookupServiceBean().deleteDiscountsType((DiscountsType) lookupPacket.getDiscountsType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteDiscountsType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "DiscountsTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(discountsTypeResult);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the roles.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addRoles")
	public String addRoles(RolePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Role result = new LookupServiceBean().addRoles((Role) lookupPacket.getRole(), lookupPacket.getFunctionsList(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addRoles.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "RolePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update role.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateRole")
	public String updateRole(RolePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Role result = new LookupServiceBean().updateRole((Role) lookupPacket.getRole(), lookupPacket.getFunctionsList(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateRole.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "RolePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete role.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteRole")
	public String deleteRole(RolePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();

			Role result = new LookupServiceBean().deleteRole((Role) lookupPacket.getRole(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteRole.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "RolePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the payment method.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addPaymentMethod")
	public String addPaymentMethod(PaymentMethodPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			PaymentMethod result = new LookupServiceBean().addPaymentMethod((PaymentMethod) lookupPacket.getPaymentMethod(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addPaymentMethod.name(), lookupPacket);
			
			lookupPacket.setPaymentMethod(result);
		    String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PaymentMethodPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update payment method.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updatePaymentMethod")
	public String updatePaymentMethod(PaymentMethodPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			PaymentMethod result = new LookupServiceBean().updatePaymentMethod((PaymentMethod) lookupPacket.getPaymentMethod(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updatePaymentMethod.name(), lookupPacket);
			lookupPacket.setPaymentMethod(result);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PaymentMethodPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete payment method.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deletePaymentMethod")
	public String deletePaymentMethod(PaymentMethodPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			PaymentMethod result = new LookupServiceBean().deletePaymentMethod((PaymentMethod) lookupPacket.getPaymentMethod(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deletePaymentMethod.name(), lookupPacket);
			lookupPacket.setPaymentMethod(result);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PaymentMethodPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the payment method type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addPaymentMethodType")
	public String addPaymentMethodType(PaymentMethodTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			PaymentMethodType result = new LookupServiceBean().addPaymentMethodType((PaymentMethodType) lookupPacket.getPaymentMethodType(), httpRequest, em);
			tx.commit();
			lookupPacket.setPaymentMethodType(result);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addPaymentMethodType.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PaymentMethodTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update payment method type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updatePaymentMethodType")
	public String updatePaymentMethodType(PaymentMethodTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			PaymentMethodType result = new LookupServiceBean().updatePaymentMethodType((PaymentMethodType) lookupPacket.getPaymentMethodType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updatePaymentMethodType.name(), lookupPacket);
			lookupPacket.setPaymentMethodType(result);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PaymentMethodTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete payment method type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deletePaymentMethodType")
	public String deletePaymentMethodType(PaymentMethodTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			PaymentMethodType result = new LookupServiceBean().deletePaymentMethodType((PaymentMethodType) lookupPacket.getPaymentMethodType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deletePaymentMethodType.name(), lookupPacket);
			lookupPacket.setPaymentMethodType(result);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PaymentMethodTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the printer.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addPrinter")
	public String addPrinter(PrinterPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Printer result = new LookupServiceBean().addPrinter((Printer) lookupPacket.getPrinter(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addPrinter.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PrinterPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update printer.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updatePrinter")
	public String updatePrinter(PrinterPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Printer result = new LookupServiceBean().updatePrinter((Printer) lookupPacket.getPrinter(), httpRequest, em,lookupPacket);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updatePrinter.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PrinterPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete printer.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deletePrinter")
	public String deletePrinter(PrinterPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Printer result = new LookupServiceBean().deletePrinter((Printer) lookupPacket.getPrinter(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deletePrinter.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PrinterPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the payment gateway to pinpad.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addPaymentGatewayToPinpad")
	public String addPaymentGatewayToPinpad(PaymentGatewayToPinpadPacket lookupPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			PaymentGatewayToPinpad result = new LookupServiceBean().addPaymentGatewayToPinpad(lookupPacket, (PaymentGatewayToPinpad) lookupPacket.getPaymentGatewayToPinpad(), httpRequest, em);
			tx.commit();
			lookupPacket.setPaymentGatewayToPinpad(result);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addPaymentGatewayToPinpad.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PaymentGatewayToPinpadPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update payment gateway to pinpad.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updatePaymentGatewayToPinpad")
	public String updatePaymentGatewayToPinpad(PaymentGatewayToPinpadPacket lookupPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			PaymentGatewayToPinpad result = new LookupServiceBean().updatePaymentGatewayToPinpad((PaymentGatewayToPinpad) lookupPacket.getPaymentGatewayToPinpad(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updatePaymentGatewayToPinpad.name(), lookupPacket);
			lookupPacket.setPaymentGatewayToPinpad(result);
			
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PaymentGatewayToPinpadPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update payment gateway to pinpad list.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updatePaymentGatewayToPinpadList")
	public String updatePaymentGatewayToPinpadList(PaymentGatewayToPinpadPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			List<PaymentGatewayToPinpad> result = new LookupServiceBean().updatePaymentGatewayToPinpadList(lookupPacket.getPinpadList(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updatePaymentGatewayToPinpad.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PaymentGatewayToPinpadPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete payment gateway to pinpad.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deletePaymentGatewayToPinpad")
	public String deletePaymentGatewayToPinpad(PaymentGatewayToPinpadPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			PaymentGatewayToPinpad result = new LookupServiceBean().deletePaymentGatewayToPinpad((PaymentGatewayToPinpad) lookupPacket.getPaymentGatewayToPinpad(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deletePaymentGatewayToPinpad.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PaymentGatewayToPinpadPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the order source group.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addOrderSourceGroup")
	public String addOrderSourceGroup(OrderSourceGroupPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceGroup result = new LookupServiceBean().addOrderSourceGroup((OrderSourceGroup) lookupPacket.getOrderSourceGroup(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addOrderSourceGroup.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "OrderSourceGroupPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order source group.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderSourceGroup")
	public String updateOrderSourceGroup(OrderSourceGroupPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceGroup result = new LookupServiceBean().updateOrderSourceGroup((OrderSourceGroup) lookupPacket.getOrderSourceGroup(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateOrderSourceGroup.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "OrderSourceGroupPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete order source group.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteOrderSourceGroup")
	public String deleteOrderSourceGroup(OrderSourceGroupPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceGroup result = new LookupServiceBean().deleteOrderSourceGroup((OrderSourceGroup) lookupPacket.getOrderSourceGroup(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteOrderSourceGroup.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "OrderSourceGroupPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the order source.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addOrderSource")
	public String addOrderSource(OrderSourcePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSource result = new LookupServiceBean().addOrderSource((OrderSource) lookupPacket.getOrderSource(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addOrderSource.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "OrderSourcePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order source.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderSource")
	public String updateOrderSource(OrderSourcePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSource result = new LookupServiceBean().updateOrderSource((OrderSource) lookupPacket.getOrderSource(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateOrderSource.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "OrderSourcePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete order source.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteOrderSource")
	public String deleteOrderSource(OrderSourcePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSource result = new LookupServiceBean().deleteOrderSource((OrderSource) lookupPacket.getOrderSource(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteOrderSource.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "OrderSourcePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the cdo mgmt.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addCdoMgmt")
	public String addCdoMgmt(CdoMgmtPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			CdoMgmt result = new LookupServiceBean().addCdoMgmt((CdoMgmt) lookupPacket.getCdoMgmt(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addCdoMgmt.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "CdoMgmtPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update cdo mgmt.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateCdoMgmt")
	public String updateCdoMgmt(CdoMgmtPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			CdoMgmt result = new LookupServiceBean().updateCdoMgmt((CdoMgmt) lookupPacket.getCdoMgmt(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateCdoMgmt.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "CdoMgmtPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete cdo mgmt.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteCdoMgmt")
	public String deleteCdoMgmt(CdoMgmtPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			CdoMgmt result = new LookupServiceBean().deleteCdoMgmt((CdoMgmt) lookupPacket.getCdoMgmt(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteCdoMgmt.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "CdoMgmtPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the printers model.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addPrintersModel")
	public String addPrintersModel(PrinterModelPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			PrintersModel result = new LookupServiceBean().addPrintersModel((PrintersModel) lookupPacket.getPrintersModel(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addPrintersModel.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PrinterModelPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update printers model.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updatePrintersModel")
	public String updatePrintersModel(PrinterModelPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			PrintersModel result = new LookupServiceBean().updatePrintersModel((PrintersModel) lookupPacket.getPrintersModel(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updatePrintersModel.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PrinterModelPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete printers model.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deletePrintersModel")
	public String deletePrintersModel(PrinterModelPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			PrintersModel result = new LookupServiceBean().deletePrintersModel((PrintersModel) lookupPacket.getPrintersModel(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deletePrintersModel.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PrinterModelPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update printer model name for IP addressses.
	 *
	 * @param printerInfoList
	 *            the printer info list
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updatePrinterModelNameForIpAddresss")
	public String updatePrinterModelNameForIPAddressses(PrinterInfoPacket printerInfoList, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, printerInfoList);

			tx = em.getTransaction();
			tx.begin();
			boolean hasAnyUpdateOnPrinterHappened = false;

			if (printerInfoList != null && printerInfoList.getPrinterInformation() != null && printerInfoList.getPrinterInformation().size() > 0)
			{

				for (Printer printerInfo : printerInfoList.getPrinterInformation())
				{
					Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, printerInfo.getId());
					// check if ip address equality is there or not
					if (printer.getIpAddress() != null && printerInfo.getIpAddress() != null)
					{
						if (printer.getIpAddress().trim().equals(printerInfo.getIpAddress().trim()))
						{
							if (printer.getPrintersModelId() != printerInfo.getPrintersModelId())
							{
								printer.setPrintersModelId(printerInfo.getPrintersModelId());
								if(printer.getId()==null)
								printer.setId(new StoreForwardUtility().generateUUID());

								em.persist(printer);

								hasAnyUpdateOnPrinterHappened = true;
							}
						}
					}
				}

			}
			tx.commit();
			if (hasAnyUpdateOnPrinterHappened)
			{
				// broadcast to client about cdo update of printers
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updatePrinterModelInfo.name(), printerInfoList);
			}
			String json = new StoreForwardUtility().returnJsonPacket(printerInfoList, "PrinterInfoPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, printerInfoList.getLocationId(), Integer.parseInt(printerInfoList.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(printerInfoList);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param operation
	 *            the operation
	 * @param postPacket
	 *            the post packet
	 */
	public void sendPacketForBroadcast(String operation, PostPacket postPacket)
	{
		try
		{
			MessageSender messageSender = new MessageSender();
			operation = ServiceOperationsUtility.getOperationName(operation);
			messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.LookupService.name(), operation, null, postPacket.getMerchantId(), postPacket.getLocationId(),
					postPacket.getEchoString(), postPacket.getSessionId());
		}

		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not send broadcast of post packet in LookupService operation", operation);
		}
	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param operation
	 *            the operation
	 * @param postPacket
	 *            the post packet
	 * @param serviceName
	 *            the service name
	 */
	private void sendPacketForBroadcast(String operation, PostPacket postPacket, String serviceName)
	{
		try
		{
			MessageSender messageSender = new MessageSender();
			operation = ServiceOperationsUtility.getOperationName(operation);
			messageSender.sendMessage(httpRequest, postPacket.getClientId(), serviceName, operation, null, postPacket.getMerchantId(), postPacket.getLocationId(), postPacket.getEchoString(),
					postPacket.getSessionId());
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not broadcast post packet in LookupService operation", operation);
		}

	}

	/**
	 * Convert to json.
	 *
	 * @param cdoMgmtObj
	 *            the cdo mgmt obj
	 * @return the string
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String convertToJson(Object cdoMgmtObj) throws JsonGenerationException, JsonMappingException, IOException
	{

		ObjectMapper objectMapper = new ObjectMapper();

		Writer strWriter = new StringWriter();
		objectMapper.writeValue(strWriter, cdoMgmtObj);
		String jsonString = strWriter.toString();
		return jsonString;

	}

	/**
	 * Gets the all item attribute info by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all item attribute info by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllItemAttributeInfoByLocationId/{locationId}")
	public String getAllItemAttributeInfoByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<AttributeDisplayService> ans = new ArrayList<AttributeDisplayService>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getItemAttributeInfo( ? )").setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList)
			{ // if this has primary key not
				// 0
				if ((String) objRow[0] != null)
				{
					AttributeDisplayService attributeDisplayService = new AttributeDisplayService();
					attributeDisplayService.setId((String) objRow[0]);
					attributeDisplayService.setItemsAttributeTypeName((String) objRow[1]);
					attributeDisplayService.setItemsAttributeId((String) objRow[2]);
					attributeDisplayService.setItemsAttributeName((String) objRow[3]);
					attributeDisplayService.setImageName((String) objRow[4]);
					attributeDisplayService.setPrice((BigDecimal) objRow[5]);
					if ((Boolean) objRow[6])
					{
						attributeDisplayService.setIsRequired(1);
					}
					else
					{
						attributeDisplayService.setIsRequired(0);
					}
					ans.add(attributeDisplayService);
				}

			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all reservations schedule by location id and date.
	 *
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @return the all reservations schedule by location id and date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsScheduleByLocationIdAndDate/{locationId}/{date}")
	public String getAllReservationsScheduleByLocationIdAndDate(@PathParam("locationId") String locationId, @PathParam("date") String date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsSchedule> criteria = builder.createQuery(ReservationsSchedule.class);
			Root<ReservationsSchedule> root = criteria.from(ReservationsSchedule.class);
			Predicate predicate1 = builder.equal(root.get(ReservationsSchedule_.locationId), locationId);
			Predicate predicate2 = builder.greaterThanOrEqualTo(root.get(ReservationsSchedule_.toDate), date);
			TypedQuery<ReservationsSchedule> query = em.createQuery(criteria.select(root).where(predicate1, predicate2));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the timezone by id.
	 *
	 * @param id
	 *            the id
	 * @return the timezone by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getTimezoneById/{id}")
	public String getTimezoneById(@PathParam("id") int id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Timezone> criteria = builder.createQuery(Timezone.class);
			Root<Timezone> r = criteria.from(Timezone.class);
			TypedQuery<Timezone> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Timezone_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the timezone by id for KDS.
	 *
	 * @param id
	 *            the id
	 * @return the timezone by id for KDS
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getTimezoneByIdForKDS/{id}")
	public String getTimezoneByIdForKDS(@PathParam("id") int id) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Timezone> criteria = builder.createQuery(Timezone.class);
			Root<Timezone> r = criteria.from(Timezone.class);
			TypedQuery<Timezone> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Timezone_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update feedback type status.
	 *
	 * @param feedbackTypePacket
	 *            the feedback type packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.services.jaxrs.ILookupService#delete(com.nirvanaxp.types
	 * .entities.FeedbackType)
	 */
	@POST
	@Path("/updateFeedbackTypeStatus")
	public String updateFeedbackTypeStatus(FeedbackTypePacket feedbackTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, feedbackTypePacket);

			tx = em.getTransaction();
			tx.begin();
			FeedbackType reservationsStatus = new LookupServiceBean().updateFeedbackTypeStatus(feedbackTypePacket.getFeedbackType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateFeedbackTypeStatus.name(), feedbackTypePacket);
			String json = new StoreForwardUtility().returnJsonPacket(feedbackTypePacket, "FeedbackTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, feedbackTypePacket.getLocationId(), Integer.parseInt(feedbackTypePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the feedback question by id.
	 *
	 * @param id
	 *            the id
	 * @return the feedback question by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getFeedbackQuestionById/{id}")
	public String getFeedbackQuestionById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<FeedbackQuestion> criteria = builder.createQuery(FeedbackQuestion.class);
			Root<FeedbackQuestion> r = criteria.from(FeedbackQuestion.class);
			TypedQuery<FeedbackQuestion> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(FeedbackQuestion_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all reservations schedule by location id and from date and to
	 * date.
	 *
	 * @param locationId
	 *            the location id
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @return the all reservations schedule by location id and from date and to
	 *         date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsScheduleByLocationIdAndFromDateAndToDate/{locationId}/{fromDate}/{toDate}")
	public String getAllReservationsScheduleByLocationIdAndFromDateAndToDate(@PathParam("locationId") String locationId, @PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			String queryString = "SELECT res FROM ReservationsSchedule res " + " " + " where  ((res.fromDate between ? and ?)" + " or (res.toDate between ? and ? )" + " "
					+ " or (res.fromDate < ? and res.toDate >?)) " + " and res.locationId=? and res.status!='D'";

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TypedQuery<ReservationsSchedule> query = em.createQuery(queryString, ReservationsSchedule.class).setParameter(1, fromDate).setParameter(2, toDate).setParameter(3, fromDate)
					.setParameter(4, toDate).setParameter(5, fromDate).setParameter(6, toDate).setParameter(7, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all reservations schedule by location id and from date and to
	 * date and order source group id.
	 *
	 * @param locationId
	 *            the location id
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the all reservations schedule by location id and from date and to
	 *         date and order source group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsScheduleByLocationIdAndFromDateAndToDateAndOrderSourceGroupId/{locationId}/{fromDate}/{toDate}/{orderSourceGroupId}")
	public String getAllReservationsScheduleByLocationIdAndFromDateAndToDateAndOrderSourceGroupId(@PathParam("locationId") String locationId, @PathParam("fromDate") String fromDate,
			@PathParam("toDate") String toDate, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("orderSourceGroupId") String orderSourceGroupId) throws Exception
	{

		EntityManager em = null;
		try
		{
			String queryString = "SELECT res FROM ReservationsSchedule res " + " where ((res.fromDate between ? and ?)" + " or (res.toDate between ? and ? )" + " "
					+ " or (res.fromDate < ? and res.toDate >?)) " + " and res.locationId=? " + "and res.orderSourceGroupId=? and res.status!='D'";

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TypedQuery<ReservationsSchedule> query = em.createQuery(queryString, ReservationsSchedule.class).setParameter(1, fromDate).setParameter(2, toDate).setParameter(3, fromDate)
					.setParameter(4, toDate).setParameter(5, fromDate).setParameter(6, toDate).setParameter(7, locationId).setParameter(8, orderSourceGroupId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the business hour.
	 *
	 * @param hourPacket
	 *            the hour packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addBusinessHour")
	public String addBusinessHour(BusinessHourPacket hourPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, hourPacket);

			tx = em.getTransaction();
			tx.begin();
			for (BusinessHour businessHour : hourPacket.getBusinessHourList())
			{
				businessHour.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if(businessHour.getId()==null)
				businessHour.setId(new StoreForwardUtility().generateUUID());

				em.persist(businessHour);
			}

			tx.commit();
			// iterate over users and add the necessary relationships
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addBusinessHour.name(), hourPacket);
			String json = new StoreForwardUtility().returnJsonPacket(hourPacket, "BusinessHourPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, hourPacket.getLocationId(), Integer.parseInt(hourPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(hourPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update business hour.
	 *
	 * @param hourPacket
	 *            the hour packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateBusinessHour")
	public String updateBusinessHour(BusinessHourPacket hourPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, hourPacket);

			tx = em.getTransaction();
			tx.begin();
			for (BusinessHour businessHour : hourPacket.getBusinessHourList())
			{
				businessHour.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				em.merge(businessHour);

			}

			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateBusinessHour.name(), hourPacket);
			String json = new StoreForwardUtility().returnJsonPacket(hourPacket, "BusinessHourPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,hourPacket.getLocationId(), Integer.parseInt(hourPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(hourPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete business hour.
	 *
	 * @param hourPacket
	 *            the hour packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteBusinessHour")
	public String deleteBusinessHour(BusinessHourPacket hourPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, hourPacket);
			tx = em.getTransaction();
			tx.begin();
			BusinessHour businessHour = hourPacket.getBusinessHour();

			businessHour = (BusinessHour) new CommonMethods().getObjectById("BusinessHour", em,BusinessHour.class, businessHour.getId());
			businessHour.setStatus("D");
			businessHour.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			em.merge(businessHour);

			tx.commit();
			// iterate over users and add the necessary relationships
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addBusinessHour.name(), hourPacket);
			String json = new StoreForwardUtility().returnJsonPacket(hourPacket, "BusinessHourPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, hourPacket.getLocationId(), Integer.parseInt(hourPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(hourPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the reasons.
	 *
	 * @param reasonsPacket
	 *            the reasons packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addReasons")
	public String addReasons(ReasonPacket reasonsPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, reasonsPacket);

			tx = em.getTransaction();
			tx.begin();
			ReasonManagementHelper reasonManagementHelper = new ReasonManagementHelper();
			reasonManagementHelper.addReasons(em, reasonsPacket.getReasons(),httpRequest);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addReasons.name(), reasonsPacket);
			String json = new StoreForwardUtility().returnJsonPacket(reasonsPacket, "ReasonPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,reasonsPacket.getLocationId(), Integer.parseInt(reasonsPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(reasonsPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update reasons.
	 *
	 * @param reasonsPacket
	 *            the reasons packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateReasons")
	public String updateReasons(ReasonPacket reasonsPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, reasonsPacket);

			tx = em.getTransaction();
			tx.begin();
			ReasonManagementHelper reasonManagementHelper = new ReasonManagementHelper();
			reasonManagementHelper.updateReasons(em, reasonsPacket.getReasons());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateReasons.name(), reasonsPacket);
			String json = new StoreForwardUtility().returnJsonPacket(reasonsPacket, "ReasonPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reasonsPacket.getLocationId(), Integer.parseInt(reasonsPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(reasonsPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete reasons.
	 *
	 * @param reasonsPacket
	 *            the reasons packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteReasons")
	public String deleteReasons(ReasonPacket reasonsPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, reasonsPacket);

			tx = em.getTransaction();
			tx.begin();
			ReasonManagementHelper reasonManagementHelper = new ReasonManagementHelper();
			reasonManagementHelper.deleteReasons(em, reasonsPacket.getReasons());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteReasons.name(), reasonsPacket);
			String json = new StoreForwardUtility().returnJsonPacket(reasonsPacket, "ReasonPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reasonsPacket.getLocationId(), Integer.parseInt(reasonsPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(reasonsPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the reason type.
	 *
	 * @param reasonTypePacket
	 *            the reason type packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addReasonType")
	public String addReasonType(ReasonTypePacket reasonTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, reasonTypePacket);

			tx = em.getTransaction();
			tx.begin();
			ReasonManagementHelper reasonManagementHelper = new ReasonManagementHelper();
			reasonManagementHelper.addReasonType(em, reasonTypePacket.getReasonType(),httpRequest,reasonTypePacket.getLocationId());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addReasonType.name(), reasonTypePacket);
			String json = new StoreForwardUtility().returnJsonPacket(reasonTypePacket, "ReasonTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reasonTypePacket.getLocationId(), Integer.parseInt(reasonTypePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(reasonTypePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update reason type.
	 *
	 * @param reasonTypePacket
	 *            the reason type packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateReasonType")
	public String updateReasonType(ReasonTypePacket reasonTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, reasonTypePacket);

			tx = em.getTransaction();
			tx.begin();
			ReasonManagementHelper reasonManagementHelper = new ReasonManagementHelper();
			reasonManagementHelper.updateReasonType(em, reasonTypePacket.getReasonType());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateReasonType.name(), reasonTypePacket);
			String json = new StoreForwardUtility().returnJsonPacket(reasonTypePacket, "ReasonTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,reasonTypePacket.getLocationId(), Integer.parseInt(reasonTypePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(reasonTypePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete reason type.
	 *
	 * @param reasonTypePacket
	 *            the reason type packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteReasonType")
	public String deleteReasonType(ReasonTypePacket reasonTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, reasonTypePacket);

			tx = em.getTransaction();
			tx.begin();
			ReasonManagementHelper reasonManagementHelper = new ReasonManagementHelper();
			reasonManagementHelper.deleteReasonType(em, reasonTypePacket.getReasonType());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteReasonType.name(), reasonTypePacket);
			String json = new StoreForwardUtility().returnJsonPacket(reasonTypePacket, "ReasonTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reasonTypePacket.getLocationId(), Integer.parseInt(reasonTypePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(reasonTypePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the sales tax.
	 *
	 * @param salesTaxPacket
	 *            the sales tax packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addSalesTax")
	public String addSalesTax(SalesTaxPacket salesTaxPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, salesTaxPacket);

			tx = em.getTransaction();
			tx.begin();
			SalesTaxHelper salesTaxHelper = new SalesTaxHelper();
			salesTaxHelper.addSalesTax(em, salesTaxPacket.getSalesTax());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addSalesTax.name(), salesTaxPacket);
			String json = new StoreForwardUtility().returnJsonPacket(salesTaxPacket, "SalesTaxPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, salesTaxPacket.getLocationId(), Integer.parseInt(salesTaxPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(salesTaxPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update sales tax.
	 *
	 * @param salesTaxPacket
	 *            the sales tax packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateSalesTax")
	public String updateSalesTax(SalesTaxPacket salesTaxPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, salesTaxPacket);

			tx = em.getTransaction();
			tx.begin();
			String json = new StoreForwardUtility().returnJsonPacket(salesTaxPacket, "SalesTaxPacket",httpRequest);
			
			SalesTaxHelper salesTaxHelper = new SalesTaxHelper();
			salesTaxHelper.updateSalesTax(em, salesTaxPacket.getSalesTax());
			tx.commit();
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, salesTaxPacket.getLocationId(), Integer.parseInt(salesTaxPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateSalesTax.name(), salesTaxPacket);
			
			return new JSONUtility(httpRequest).convertToJsonString(salesTaxPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update sales tax id.
	 *
	 * @param salesTaxPacket
	 *            the sales tax packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateSalesTaxId")
	public String updateSalesTaxId(SalesTaxPacket salesTaxPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, salesTaxPacket);

			tx = em.getTransaction();
			tx.begin();
			String json = new StoreForwardUtility().returnJsonPacket(salesTaxPacket, "SalesTaxPacket",httpRequest);
			SalesTaxHelper salesTaxHelper = new SalesTaxHelper();
			SalesTax salesTax = salesTaxHelper.updateSalesTaxId(em, salesTaxPacket.getSalesTax());
			salesTaxPacket.setSalesTax(salesTax);
			tx.commit();
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,salesTaxPacket.getLocationId(), Integer.parseInt(salesTaxPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateSalesTax.name(), salesTaxPacket);
			
			return new JSONUtility(httpRequest).convertToJsonString(salesTaxPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete sales tax.
	 *
	 * @param salesTaxPacket
	 *            the sales tax packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteSalesTax")
	public String deleteSalesTax(SalesTaxPacket salesTaxPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, salesTaxPacket);

			tx = em.getTransaction();
			tx.begin();
			SalesTaxHelper salesTaxHelper = new SalesTaxHelper();
			salesTaxHelper.deleteSalesTax(em, salesTaxPacket.getSalesTax());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteSalesTax.name(), salesTaxPacket);
			String json = new StoreForwardUtility().returnJsonPacket(salesTaxPacket, "SalesTaxPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, salesTaxPacket.getLocationId(), Integer.parseInt(salesTaxPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(salesTaxPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the business hour by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the business hour by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getBusinessHourByLocationId/{LocationId}")
	public String getBusinessHourByLocationId(@PathParam("LocationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new LookupServiceBean().getBusinessHour(locationId, httpRequest, em));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all reservations schedule by location id and week.
	 *
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @param schemaName
	 *            the schema name
	 * @return the all reservations schedule by location id and week
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsScheduleByLocationIdAndDate/{locationId}/{date}")
	public String getAllReservationsScheduleByLocationIdAndWeek(@PathParam("locationId") String locationId, @PathParam("date") String date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@CookieParam(value = "SchemaName") String schemaName) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, schemaName);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsSchedule> criteria = builder.createQuery(ReservationsSchedule.class);
			Root<ReservationsSchedule> r = criteria.from(ReservationsSchedule.class);
			TypedQuery<ReservationsSchedule> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsSchedule_.locationId), locationId),
					builder.notEqual(r.get(ReservationsSchedule_.status), "D"), builder.lessThanOrEqualTo(r.get(ReservationsSchedule_.fromDate), date),
					builder.greaterThanOrEqualTo(r.get(ReservationsSchedule_.toDate), date)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Manage slotfor block date.
	 *
	 * @param reservationSchedulePacket
	 *            the reservation schedule packet
	 * @param em
	 *            the em
	 */
	private void manageSlotforBlockDate(ReservationSchedulePacket reservationSchedulePacket, EntityManager em)
	{
		// check if reservation allowed is 0, then block the reservation for
		// all the dates sent in block date list
		if (reservationSchedulePacket.getReservationsSchedule().getIsReservationsAllowed() == 0)
		{
			if (reservationSchedulePacket.getBlockDates() != null && reservationSchedulePacket.getBlockDates().size() > 0)
			{
				for (String blockDate : reservationSchedulePacket.getBlockDates())
				{
					deActivateSlotForDate(blockDate, em);
				}

			}

		}
		else
		{
			// if client sends 1 then get block time from xref and
			// deactivate that time

			if (reservationSchedulePacket.getBlockDates() != null && reservationSchedulePacket.getBlockDates().size() > 0)
			{
				for (String blockDate : reservationSchedulePacket.getBlockDates())
				{
					Set<ReservationsScheduleXref> reservationsScheduleXrefsSet = reservationSchedulePacket.getReservationsSchedule().getReservationsScheduleXref();
					if (reservationsScheduleXrefsSet != null && reservationsScheduleXrefsSet.size() > 0)
					{
						for (ReservationsScheduleXref reservationsScheduleXref : reservationsScheduleXrefsSet)
						{
							if (reservationsScheduleXref != null)
							{
								// get from time and to time from xref for
								// clocking the time
								deActivateSlotForDateAndSlotStartTimeAndEndTime(blockDate, reservationsScheduleXref.getFromTime(), reservationsScheduleXref.getToTime(), em);
							}
						}
					}
				}

			}

		}
	}

	/**
	 * Gets the all order detail status by order source group id.
	 *
	 * @param orderSourceId
	 *            the order source id
	 * @return the all order detail status by order source group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderDetailStatusByOrderSourceGroupId/{orderSourceId}")
	public String getAllOrderDetailStatusByOrderSourceGroupId(@PathParam("orderSourceId") int orderSourceId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
			Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
			TypedQuery<OrderDetailStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.orderSourceGroupId), orderSourceId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update reservation status display sequence by id.
	 *
	 * @param reservationStatusListPacket
	 *            the reservation status list packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateReservationStatusDisplaySequenceById")
	public String updateReservationStatusDisplaySequenceById(ReservationStatusListPacket reservationStatusListPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, reservationStatusListPacket);

			tx = em.getTransaction();
			tx.begin();
			List<ReservationsStatus> result = new LookupServiceBean().updateReservationStatusDisplaySequenceById(reservationStatusListPacket.getReservationStatus(), httpRequest, em);
			tx.commit();
            sendPacketForBroadcast(POSNServiceOperations.LookupService_updateReservationsStatus.name(), reservationStatusListPacket);
            reservationStatusListPacket.setReservationStatus(result);
            String json = new StoreForwardUtility().returnJsonPacket(reservationStatusListPacket, "ReservationStatusListPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reservationStatusListPacket.getLocationId(), Integer.parseInt(reservationStatusListPacket.getMerchantId()));

            return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order status display sequence by id.
	 *
	 * @param orderStatusListPacket
	 *            the order status list packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderStatusDisplaySequenceById")
	public String updateOrderStatusDisplaySequenceById(OrderStatusListPacket orderStatusListPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, orderStatusListPacket);

			tx = em.getTransaction();
			tx.begin();
			List<OrderStatus> result = new LookupServiceBean().updateOrderStatusDisplaySequenceById(orderStatusListPacket.getOrderStatus(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateOrderStatus.name(), orderStatusListPacket);
			orderStatusListPacket.setOrderStatus(result);
	        String json = new StoreForwardUtility().returnJsonPacket(orderStatusListPacket, "OrderStatusListPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,orderStatusListPacket.getLocationId(), Integer.parseInt(orderStatusListPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the printer receipt.
	 *
	 * @param printerReceiptPacket
	 *            the printer receipt packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addPrinterReceipt")
	public String addPrinterReceipt(PrinterReceiptPacket printerReceiptPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, printerReceiptPacket);

			tx = em.getTransaction();
			tx.begin();
			PrinterReceiptHelper printerReceiptHelper = new PrinterReceiptHelper();
			printerReceiptHelper.addPrinterReceipt(em, printerReceiptPacket.getPrinterReceipt(),httpRequest);
			tx.commit();
	        String json = new StoreForwardUtility().returnJsonPacket(printerReceiptPacket, "PrinterReceiptPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, printerReceiptPacket.getLocationId(), Integer.parseInt(printerReceiptPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LookupService_addPrinterReceipt.name(), printerReceiptPacket);
			return new JSONUtility(httpRequest).convertToJsonString(printerReceiptPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete printer receipt.
	 *
	 * @param printerReceiptPacket
	 *            the printer receipt packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deletePrinterReceipt")
	public String deletePrinterReceipt(PrinterReceiptPacket printerReceiptPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, printerReceiptPacket);

			tx = em.getTransaction();
			tx.begin();
			PrinterReceiptHelper printerReceiptHelper = new PrinterReceiptHelper();
			PrinterReceipt printerReceipt = printerReceiptHelper.deletePrinterReceipt(em, printerReceiptPacket.getPrinterReceipt());
			printerReceiptPacket.setPrinterReceipt(printerReceipt);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deletePrinterReceipt.name(), printerReceiptPacket);
			 String json = new StoreForwardUtility().returnJsonPacket(printerReceiptPacket, "PrinterReceiptPacket",httpRequest);
				new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, printerReceiptPacket.getLocationId(), Integer.parseInt(printerReceiptPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(printerReceiptPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update printer receipt.
	 *
	 * @param printerReceiptPacket
	 *            the printer receipt packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updatePrinterReceipt")
	public String updatePrinterReceipt(PrinterReceiptPacket printerReceiptPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, printerReceiptPacket);

			tx = em.getTransaction();
			tx.begin();
			PrinterReceiptHelper printerReceiptHelper = new PrinterReceiptHelper();
			printerReceiptHelper.updatePrinterReceipt(em, printerReceiptPacket.getPrinterReceipt());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updatePrinterReceipt.name(), printerReceiptPacket);
			String json = new StoreForwardUtility().returnJsonPacket(printerReceiptPacket, "PrinterReceiptPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,printerReceiptPacket.getLocationId(), Integer.parseInt(printerReceiptPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(printerReceiptPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all printers receipt.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all printers receipt
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllPrintersReceiptByLocationId/{locationId}")
	public String getAllPrintersReceipt(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select l from PrinterReceipt l where l.status != 'D' and locationId =?";
			TypedQuery<PrinterReceipt> query = em.createQuery(queryString, PrinterReceipt.class).setParameter(1, locationId);
			List<PrinterReceipt> resultSet = query.getResultList();
			return convertToJson(resultSet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all printers receipt by id.
	 *
	 * @param id
	 *            the id
	 * @return the all printers receipt by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllPrintersReceiptById/{Id}")
	public String getAllPrintersReceiptById(@PathParam("Id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select l from PrinterReceipt l where  l.status != 'D'  and l.id=?";
			TypedQuery<PrinterReceipt> query = em.createQuery(queryString, PrinterReceipt.class).setParameter(1, id);
			PrinterReceipt resultSet = query.getSingleResult();
			return convertToJson(resultSet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the printer receipt by location id and display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @return the printer receipt by location id and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterReceiptByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getPrinterReceiptByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select l from PrinterReceipt l where l.status !='D'  and  l.locationId =?   and l.displaySequence=?";
			TypedQuery<PrinterReceipt> query = em.createQuery(queryString, PrinterReceipt.class).setParameter(1, locationId).setParameter(2, displaySequence);
			PrinterReceipt resultSet = null;
			resultSet = query.getSingleResult();
			return convertToJson(resultSet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the printer receipt by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the printer receipt by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterReceiptByLocationIdAndName/{locationId}/{name}")
	public String getPrinterReceiptByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrinterReceipt> criteria = builder.createQuery(PrinterReceipt.class);
			Root<PrinterReceipt> r = criteria.from(PrinterReceipt.class);
			TypedQuery<PrinterReceipt> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(PrinterReceipt_.locationId), locationId), builder.equal(r.get(PrinterReceipt_.name), name),
					builder.notEqual(r.get(PrinterReceipt_.status), "D")));
			PrinterReceipt resultSet = null;
			resultSet = query.getSingleResult();
			return convertToJson(resultSet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the reservation slots by procedure.
	 *
	 * @param reservationsSchedule
	 *            resevation schedule object
	 * @param em
	 *            connection with database
	 * @param blockDates
	 *            the block dates
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	private boolean addReservationSlotsByProcedure(ReservationsSchedule reservationsSchedule, EntityManager em, List<String> blockDates) throws Exception
	{

		boolean iscreated = true;
		String blockDate = "";

		// todo shlok need
		// can we make common class for array making
		if (blockDates != null)
		{
			for (int i = 0; i < blockDates.size(); i++)
			{
				if (i != (blockDates.size() - 1))
				{
					blockDate += blockDates.get(i) + ",";
				}
				else
				{
					blockDate += blockDates.get(i) + "";
					if (blockDates.get(i).equals(""))
					{
						blockDate = "null";
					}
				}
			}
		}
		else
		{
			blockDate = "null";
		}

		String queryString = "call proc_generatescheduleslots( ?, ?,? )";

		em.createNativeQuery(queryString).setParameter(1, reservationsSchedule.getId()).setParameter(2, reservationsSchedule.getCreatedBy()).setParameter(3, blockDate).executeUpdate();

		return iscreated;

	}

	/**
	 * Update order source group to paymentgateway type.
	 *
	 * @param orderSourceGroupToPaymentgatewayTypePacket
	 *            the order source group to paymentgateway type packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderSourceGroupToPaymentgatewayType")
	public String updateOrderSourceGroupToPaymentgatewayType(OrderSourceGroupToPaymentgatewayTypePacket orderSourceGroupToPaymentgatewayTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, orderSourceGroupToPaymentgatewayTypePacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceGroupToPaymentgatewayTypeHelper orderSourceGroupToPaymentgatewayTypeHelper = new OrderSourceGroupToPaymentgatewayTypeHelper();
			orderSourceGroupToPaymentgatewayTypeHelper
					.updateOrderSourceGroupToPaymentgatewayType(httpRequest, em, orderSourceGroupToPaymentgatewayTypePacket.getOrderSourceGroupToPaymentgatewayType());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateOrderSourceGroupToPaymentgatewayType.name(), orderSourceGroupToPaymentgatewayTypePacket);
			String json = new StoreForwardUtility().returnJsonPacket(orderSourceGroupToPaymentgatewayTypePacket, "OrderSourceGroupToPaymentgatewayTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderSourceGroupToPaymentgatewayTypePacket.getLocationId(), Integer.parseInt(orderSourceGroupToPaymentgatewayTypePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderSourceGroupToPaymentgatewayTypePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source group to paymentgateway type by order source group
	 * id.
	 *
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the order source group to paymentgateway type by order source
	 *         group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId/{orderSourceGroupId}")
	public String getOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId(@PathParam("orderSourceGroupId") String orderSourceGroupId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			OrderSourceGroupToPaymentgatewayTypeHelper orderSourceGroupToPaymentgatewayTypeHelper = new OrderSourceGroupToPaymentgatewayTypeHelper();
			List<OrderSourceGroupToPaymentgatewayType> orderSourceGroupToPaymentgatewayType = orderSourceGroupToPaymentgatewayTypeHelper.getOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId(
					httpRequest, em, orderSourceGroupId);
			if (orderSourceGroupToPaymentgatewayType.size() == 1)
			{
				return new JSONUtility(httpRequest).convertToJsonString(orderSourceGroupToPaymentgatewayType.get(0));
			}
			else if (orderSourceGroupToPaymentgatewayType == null || orderSourceGroupToPaymentgatewayType.size() == 0)
			{
				throw new NonUniqueResultException("No Payment Gateway Type found for Order source : " + orderSourceGroupId);
			}
			else
			{
				throw new NonUniqueResultException("More than one Payment Gateway Type found for Order source group : " + orderSourceGroupId);
			}

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source to paymentgateway type by order source id.
	 *
	 * @param orderSourceId
	 *            the order source id
	 * @return the order source to paymentgateway type by order source id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceToPaymentgatewayTypeByOrderSourceId/{orderSourceId}")
	public String getOrderSourceToPaymentgatewayTypeByOrderSourceId(@PathParam("orderSourceId") String orderSourceId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			OrderSourceToPaymentgatewayTypeHelper orderSourceToPaymentgatewayTypeHelper = new OrderSourceToPaymentgatewayTypeHelper();
			List<OrderSourceToPaymentgatewayType> orderSourceToPaymentgatewayType = orderSourceToPaymentgatewayTypeHelper.getOrderSourceToPaymentgatewayTypeByOrderSourceId(httpRequest, em,
					orderSourceId);
			if (orderSourceToPaymentgatewayType != null && orderSourceToPaymentgatewayType.size() == 1)
			{
				return new JSONUtility(httpRequest).convertToJsonString(orderSourceToPaymentgatewayType.get(0));
			}
			else if (orderSourceToPaymentgatewayType == null || orderSourceToPaymentgatewayType.size() == 0)
			{
				throw new NonUniqueResultException("No Payment Gateway Type found for Order source : " + orderSourceId);
			}
			else
			{
				throw new NonUniqueResultException("More than one Payment Gateway Type found for Order source : " + orderSourceId);
			}

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order source group to paymentgateway type.
	 *
	 * @return the all order source group to paymentgateway type
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderSourceGroupToPaymentgatewayType/")
	public String getAllOrderSourceGroupToPaymentgatewayType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			OrderSourceGroupToPaymentgatewayTypeHelper orderSourceGroupToPaymentgatewayTypeHelper = new OrderSourceGroupToPaymentgatewayTypeHelper();
			String orderSourceGroupToPaymentgatewayType = orderSourceGroupToPaymentgatewayTypeHelper.getAllOrderSourceGroupToPaymentgatewayType(httpRequest, em);
			return orderSourceGroupToPaymentgatewayType;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the order source to paymentgateway type.
	 *
	 * @param orderSourceToPaymentgatewayTypePacket
	 *            the order source to paymentgateway type packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addOrderSourceToPaymentgatewayType")
	public String addOrderSourceToPaymentgatewayType(OrderSourceToPaymentgatewayTypePacket orderSourceToPaymentgatewayTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null, orderSourceToPaymentgatewayTypePacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceToPaymentgatewayTypeHelper orderSourceToPaymentgatewayTypeHelper = new OrderSourceToPaymentgatewayTypeHelper();
			orderSourceToPaymentgatewayTypeHelper.addOrderSourceToPaymentgatewayType(httpRequest, em, orderSourceToPaymentgatewayTypePacket.getOrderSourceToPaymentgatewayType());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addOrderSourceToPaymentgatewayType.name(), orderSourceToPaymentgatewayTypePacket);
			String json = new StoreForwardUtility().returnJsonPacket(orderSourceToPaymentgatewayTypePacket, "OrderSourceToPaymentgatewayTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderSourceToPaymentgatewayTypePacket.getLocationId(), Integer.parseInt(orderSourceToPaymentgatewayTypePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderSourceToPaymentgatewayTypePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order source to paymentgateway type.
	 *
	 * @param orderSourceToPaymentgatewayTypePacket
	 *            the order source to paymentgateway type packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderSourceToPaymentgatewayType")
	public String updateOrderSourceToPaymentgatewayType(OrderSourceToPaymentgatewayTypePacket orderSourceToPaymentgatewayTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, orderSourceToPaymentgatewayTypePacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceToPaymentgatewayTypeHelper orderSourceToPaymentgatewayTypeHelper = new OrderSourceToPaymentgatewayTypeHelper();
			orderSourceToPaymentgatewayTypeHelper.updateOrderSourceToPaymentgatewayType(httpRequest, em, orderSourceToPaymentgatewayTypePacket.getOrderSourceToPaymentgatewayType());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateOrderSourceToPaymentgatewayType.name(), orderSourceToPaymentgatewayTypePacket);
			String json = new StoreForwardUtility().returnJsonPacket(orderSourceToPaymentgatewayTypePacket, "OrderSourceToPaymentgatewayTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderSourceToPaymentgatewayTypePacket.getLocationId(), Integer.parseInt(orderSourceToPaymentgatewayTypePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderSourceToPaymentgatewayTypePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete order source to paymentgateway type.
	 *
	 * @param orderSourceToPaymentgatewayTypePacket
	 *            the order source to paymentgateway type packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteOrderSourceToPaymentgatewayType")
	public String deleteOrderSourceToPaymentgatewayType(OrderSourceToPaymentgatewayTypePacket orderSourceToPaymentgatewayTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceToPaymentgatewayTypeHelper orderSourceToPaymentgatewayTypeHelper = new OrderSourceToPaymentgatewayTypeHelper();
			OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType = orderSourceToPaymentgatewayTypeHelper.deleteOrderSourceToPaymentgatewayType(httpRequest, em,
					orderSourceToPaymentgatewayTypePacket.getOrderSourceToPaymentgatewayType());
			orderSourceToPaymentgatewayTypePacket.setOrderSourceToPaymentgatewayType(orderSourceToPaymentgatewayType);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteOrderSourceToPaymentgatewayType.name(), orderSourceToPaymentgatewayTypePacket);
			String json = new StoreForwardUtility().returnJsonPacket(orderSourceToPaymentgatewayTypePacket, "OrderSourceToPaymentgatewayTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,orderSourceToPaymentgatewayTypePacket.getLocationId(), Integer.parseInt(orderSourceToPaymentgatewayTypePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderSourceToPaymentgatewayTypePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the printer receipt by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @return the printer receipt by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getReservationsTypesByLocationId/{locationId}")
	public String getPrinterReceiptByLocationIdAndName(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsType> criteria = builder.createQuery(ReservationsType.class);
			Root<ReservationsType> r = criteria.from(ReservationsType.class);
			TypedQuery<ReservationsType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsType_.locationsId), locationId),
					builder.notEqual(r.get(ReservationsType_.status), "D")));
			List<ReservationsType> resultSet = null;
			try
			{
				resultSet = query.getResultList();
			}
			catch (Exception e)
			{
				// todo shlok need
				// handle proper Exception
				logger.severe(httpRequest, e, e.getMessage());
			}
			return convertToJson(resultSet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.INirvanaService#isAlive()
	 */
	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	/**
	 * Update items attribute display sequence by id.
	 *
	 * @param itemsAttributeListPacket
	 *            the items attribute list packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateItemsAttributeDisplaySequenceById")
	public String updateItemsAttributeDisplaySequenceById(ItemsAttributeListPacket itemsAttributeListPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemsAttributeListPacket);

			tx = em.getTransaction();
			tx.begin();
			List<ItemsAttribute> result = new LookupServiceBean().updateItemsAttributeDisplaySequenceById(itemsAttributeListPacket.getItemsAttribute(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateItemsAttribute.name(), itemsAttributeListPacket);
			String json = new StoreForwardUtility().returnJsonPacket(itemsAttributeListPacket, "ItemsAttributeListPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, itemsAttributeListPacket.getLocationId(), Integer.parseInt(itemsAttributeListPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update items attribute type display sequence by id.
	 *
	 * @param itemsAttributeTypeListPacket
	 *            the items attribute type list packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateItemsAttributeTypeDisplaySequenceById")
	public String updateItemsAttributeTypeDisplaySequenceById(ItemsAttributeTypeListPacket itemsAttributeTypeListPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemsAttributeTypeListPacket);

			tx = em.getTransaction();
			tx.begin();
			List<ItemsAttributeType> result = new LookupServiceBean().updateItemsAttributeTypeDisplaySequenceById(itemsAttributeTypeListPacket.getItemsAttributeType(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateItemsAttributeType.name(), itemsAttributeTypeListPacket);
			itemsAttributeTypeListPacket.setItemsAttributeType(result);
			String json = new StoreForwardUtility().returnJsonPacket(itemsAttributeTypeListPacket, "ItemsAttributeTypeListPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, itemsAttributeTypeListPacket.getLocationId(), Integer.parseInt(itemsAttributeTypeListPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Turn itemise print on off.
	 *
	 * @param isItemisePrintRequired
	 *            the is itemise print required
	 * @param orderSourceId
	 *            the order source id
	 * @param locationId
	 *            the location id
	 * @param merchantId
	 *            the merchant id
	 * @param updatedBy
	 *            the updated by
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/turnItemisePrintOnOff/{isItemisePrintRequired}/{orderSourceId}/{locationId}/{merchantId}/{updatedBy}")
	public int turnItemisePrintOnOff(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("isItemisePrintRequired") int isItemisePrintRequired, @PathParam("orderSourceId") String orderSourceId,
			@PathParam("locationId") String locationId, @PathParam("merchantId") int merchantId, @PathParam("updatedBy") String updatedBy) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			// start transaction
			tx.begin();

			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderSourceId);
			orderSource.setIsItemisePrintRequired(isItemisePrintRequired);
			orderSource.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			orderSource.setUpdatedBy(updatedBy);

			em.merge(orderSource);
			tx.commit();

			// we are putting location service, as its part of sync cdo, client
			// will automatically pull through
			PostPacket postPacket = new PostPacket();
			postPacket.setMerchantId("" + merchantId);
			postPacket.setLocationId("" + locationId);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateOrderSource.name(), postPacket);

			return 1;
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update items char display sequence by id.
	 *
	 * @param itemsCharListPacket
	 *            the items char list packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateItemsCharDisplaySequenceById")
	public String updateItemsCharDisplaySequenceById(ItemsCharListPacket itemsCharListPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemsCharListPacket);
			tx = em.getTransaction();
			tx.begin();
			List<ItemsChar> result = new LookupServiceBean().updateItemsCharDisplaySequenceById(itemsCharListPacket.getItemsChar(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateItemsChar.name(), itemsCharListPacket);
			itemsCharListPacket.setItemsChar(result);
			String json = new StoreForwardUtility().returnJsonPacket(itemsCharListPacket, "ItemsCharListPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,itemsCharListPacket.getLocationId(), Integer.parseInt(itemsCharListPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update course display sequence by id.
	 *
	 * @param itemsAttributeTypeListPacket
	 *            the items attribute type list packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateCourseDisplaySequenceById")
	public String updateCourseDisplaySequenceById(CourseListPacket itemsAttributeTypeListPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemsAttributeTypeListPacket);
			tx = em.getTransaction();
			tx.begin();

			List<Course> result = new LookupServiceBean().updateCourseDisplaySequenceById(itemsAttributeTypeListPacket.getCourse(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateCourse.name(), itemsAttributeTypeListPacket);
			itemsAttributeTypeListPacket.setCourse(result);
			String json = new StoreForwardUtility().returnJsonPacket(itemsAttributeTypeListPacket, "CourseListPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, itemsAttributeTypeListPacket.getLocationId(), Integer.parseInt(itemsAttributeTypeListPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all nirvana XP order source group by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all nirvana XP order source group by location id
	 * @throws Exception
	 *             the exception
	 */
	// all discounts type by locationId
	@GET
	@Path("/getAllNirvanaXPOrderSourceGroupByLocationId/{locationId}")
	public String getAllNirvanaXPOrderSourceGroupByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.locationsId), locationId),
					builder.notEqual(orderSourceGroup.get(OrderSourceGroup_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all nirvana XP order source by location id and order source
	 * group id.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the all nirvana XP order source by location id and order source
	 *         group id
	 * @throws Exception
	 *             the exception
	 */
	// all order status by locationId and order source group
	@GET
	@Path("/getAllNirvanaXPOrderSourceByLocationIdAndOrderSourceGroupId/{locationId}/{orderSourceGroupId}")
	public String getAllNirvanaXPOrderSourceByLocationIdAndOrderSourceGroupId(@PathParam("locationId") String locationId, @PathParam("orderSourceGroupId") String orderSourceGroupId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> orderSource = criteria.from(OrderSource.class);
			TypedQuery<OrderSource> query = em.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSource_.locationsId), locationId),
					builder.equal(orderSource.get(OrderSource_.orderSourceGroupId), orderSourceGroupId), builder.notEqual(orderSource.get(OrderSource_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all nirvana XP order status by location id and order source
	 * group id.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceId
	 *            the order source id
	 * @return the all nirvana XP order status by location id and order source
	 *         group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllNirvanaXPOrderStatusByLocationIdAndOrderSourceGroupId/{locationId}/{orderSourceGroupId}")
	public String getAllNirvanaXPOrderStatusByLocationIdAndOrderSourceGroupId(@PathParam("locationId") String locationId, @PathParam("orderSourceGroupId") int orderSourceId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.locationsId), locationId),
					builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceId), builder.notEqual(r.get(OrderStatus_.status), "D")));

			List<OrderStatus> orderStatus = query.getResultList();

			// todo shlok need
			// male query for sorting
			Collections.sort(orderStatus, new Comparator<OrderStatus>()
			{
				@Override
				public int compare(OrderStatus p1, OrderStatus p2)
				{
					return p1.getDisplaySequence() - p2.getDisplaySequence();
				}

			});
			return new JSONUtility(httpRequest).convertToJsonString(orderStatus);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all nirvana XP order detail status by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all nirvana XP order detail status by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllNirvanaXPOrderDetailStatusByLocationId/{locationId}")
	public String getAllNirvanaXPOrderDetailStatusByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
			Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
			TypedQuery<OrderDetailStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.locationsId), locationId),
					builder.notEqual(r.get(OrderDetailStatus_.status), "D")));

			List<OrderDetailStatus> orderDetailStatus = query.getResultList();

			return new JSONUtility(httpRequest).convertToJsonString(orderDetailStatus);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all nirvana XP sales tax by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all nirvana XP sales tax by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllNirvanaXPSalesTaxByLocationId/{locationId}")
	public String getAllNirvanaXPSalesTaxByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
			Root<SalesTax> r = criteria.from(SalesTax.class);
			TypedQuery<SalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(SalesTax_.locationsId), locationId), builder.notEqual(r.get(SalesTax_.status), "D")));

			List<SalesTax> salesTax = query.getResultList();

			return new JSONUtility(httpRequest).convertToJsonString(salesTax);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all nirvana XP role by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all nirvana XP role by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllNirvanaXPRoleByLocationId/{locationId}")
	public String getAllNirvanaXPRoleByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> r = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Role_.locationsId), locationId), builder.notEqual(r.get(Role_.status), "D")));

			List<Role> role = query.getResultList();

			return new JSONUtility(httpRequest).convertToJsonString(role);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the nirvana XP items attribute by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the nirvana XP items attribute by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getNirvanaXPItemsAttributeByLocationId/{locationId}")
	public String getNirvanaXPItemsAttributeByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsAttribute> criteria = builder.createQuery(ItemsAttribute.class);
			Root<ItemsAttribute> ic = criteria.from(ItemsAttribute.class);
			TypedQuery<ItemsAttribute> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ItemsAttribute_.locationsId), locationId))
					.orderBy(builder.asc(ic.get(ItemsAttribute_.sortSequence))));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the nirvana XP payment transaction type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the nirvana XP payment transaction type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getNirvanaXPPaymentTransactionTypeByLocationId/{locationId}")
	public String getNirvanaXPPaymentTransactionTypeByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentTransactionType> criteria = builder.createQuery(PaymentTransactionType.class);
			Root<PaymentTransactionType> ic = criteria.from(PaymentTransactionType.class);
			TypedQuery<PaymentTransactionType> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(PaymentTransactionType_.locationsId), locationId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the nirvana XP payment transaction type.
	 *
	 * @return the nirvana XP payment transaction type
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getNirvanaXPPaymentTransactionType")
	public String getNirvanaXPPaymentTransactionType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentTransactionType> criteria = builder.createQuery(PaymentTransactionType.class);
			Root<PaymentTransactionType> ic = criteria.from(PaymentTransactionType.class);
			TypedQuery<PaymentTransactionType> query = em.createQuery(criteria.select(ic).where(builder.notEqual(ic.get(PaymentTransactionType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the nirvana XP payment method by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the nirvana XP payment method by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getNirvanaXPPaymentMethodTypeByLocationId/{locationId}")
	public String getNirvanaXPPaymentMethodByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethodType> criteria = builder.createQuery(PaymentMethodType.class);
			Root<PaymentMethodType> ic = criteria.from(PaymentMethodType.class);
			TypedQuery<PaymentMethodType> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(PaymentMethodType_.locationsId), locationId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the nirvana XP transaction status by location id.
	 *
	 * @return the nirvana XP transaction status by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllNirvanaXPTransactionStatus")
	public String getNirvanaXPTransactionStatusByLocationId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<TransactionStatus> criteria = builder.createQuery(TransactionStatus.class);
			Root<TransactionStatus> ic = criteria.from(TransactionStatus.class);
			TypedQuery<TransactionStatus> query = em.createQuery(criteria.select(ic));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the nirvana XP course by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the nirvana XP course by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getNirvanaXPCourseByLocationId/{locationId}")
	public String getNirvanaXPCourseByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
			Root<Course> ic = criteria.from(Course.class);
			TypedQuery<Course> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(Course_.locationsId), locationId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the nirvana XP order source group to paymentgateway type by order
	 * source group id.
	 *
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the nirvana XP order source group to paymentgateway type by order
	 *         source group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getNirvanaXPOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId/{orderSourceGroupId}")
	public String getNirvanaXPOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId(@PathParam("orderSourceGroupId") String orderSourceGroupId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);

			OrderSourceGroupToPaymentgatewayTypeHelper orderSourceGroupToPaymentgatewayTypeHelper = new OrderSourceGroupToPaymentgatewayTypeHelper();
			List<OrderSourceGroupToPaymentgatewayType> orderSourceGroupToPaymentgatewayType = orderSourceGroupToPaymentgatewayTypeHelper.getOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId(
					httpRequest, em, orderSourceGroupId);
			if (orderSourceGroupToPaymentgatewayType.size() == 1)
			{
				return new JSONUtility(httpRequest).convertToJsonString(orderSourceGroupToPaymentgatewayType.get(0));
			}
			else
			{
				throw new NonUniqueResultException("More than one Payment Gateway Type found for Order source group : " + orderSourceGroupId);
			}

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the nirvana XP order source to paymentgateway type by order source
	 * id.
	 *
	 * @param orderSourceId
	 *            the order source id
	 * @return the nirvana XP order source to paymentgateway type by order
	 *         source id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getNirvanaXPOrderSourceToPaymentgatewayTypeByOrderSourceId/{orderSourceId}")
	public String getNirvanaXPOrderSourceToPaymentgatewayTypeByOrderSourceId(@PathParam("orderSourceId") String orderSourceId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);

			OrderSourceToPaymentgatewayTypeHelper orderSourceToPaymentgatewayTypeHelper = new OrderSourceToPaymentgatewayTypeHelper();
			List<OrderSourceToPaymentgatewayType> orderSourceToPaymentgatewayType = orderSourceToPaymentgatewayTypeHelper.getOrderSourceToPaymentgatewayTypeByOrderSourceId(httpRequest, em,
					orderSourceId);
			if (orderSourceToPaymentgatewayType.size() == 1)
			{
				return new JSONUtility(httpRequest).convertToJsonString(orderSourceToPaymentgatewayType.get(0));
			}
			else
			{
				throw new NonUniqueResultException("More than one Payment Gateway Type found for Order source group : " + orderSourceId);
			}

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the nirvana XP payment method by location id and payment type id.
	 *
	 * @param locationId
	 *            the location id
	 * @param paymentMethodTypeId
	 *            the payment method type id
	 * @return the nirvana XP payment method by location id and payment type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getNirvanaXPPaymentMethodByLocationIdAndPaymentTypeId/{locationId}/{paymentMethodTypeId}")
	public String getNirvanaXPPaymentMethodByLocationIdAndPaymentTypeId(@PathParam("locationId") String locationId, @PathParam("paymentMethodTypeId") int paymentMethodTypeId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethod> criteria = builder.createQuery(PaymentMethod.class);
			Root<PaymentMethod> paymentMethod = criteria.from(PaymentMethod.class);
			TypedQuery<PaymentMethod> query = em.createQuery(criteria.select(paymentMethod).where(builder.equal(paymentMethod.get(PaymentMethod_.locationsId), locationId),
					builder.equal(paymentMethod.get(PaymentMethod_.paymentMethodTypeId), paymentMethodTypeId), builder.notEqual(paymentMethod.get(PaymentMethod_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the reason by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the reason by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getReasonByLocationIdAndName/{locationId}/{name}")
	public String getReasonByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Reasons> criteria = builder.createQuery(Reasons.class);
			Root<Reasons> r = criteria.from(Reasons.class);
			TypedQuery<Reasons> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Reasons_.locationsId), locationId), builder.equal(r.get(Reasons_.name), name),
					builder.notEqual(r.get(Reasons_.status), "D")));
			Reasons resultSet = null;
			try
			{
				resultSet = query.getSingleResult();
			}
			catch (NoResultException nre)
			{
				// todo shlok need
				// handle proper Exception
				logger.info("No result found for name -", name);
			}
			return convertToJson(resultSet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order status display sequence by id.
	 *
	 * @param functionListPacket
	 *            the function list packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateLocationToFunctionDisplaySequenceById")
	public String updateOrderStatusDisplaySequenceById(LocationToFunctionsListPacket functionListPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, functionListPacket);
			tx = em.getTransaction();
			tx.begin();

			List<LocationsToFunction> result = new LookupServiceBean().updateLocationToFunctionDisplaySequenceById(functionListPacket.getFunction(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateLocationToFunction.name(), functionListPacket);
			functionListPacket.setFunction(result);
			String json = new StoreForwardUtility().returnJsonPacket(functionListPacket, "LocationToFunctionsListPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, functionListPacket.getLocationId(), Integer.parseInt(functionListPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update display sequence by generic id.
	 *
	 * @param displaySequenceUpdateList
	 *            the display sequence update list
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateDisplaySequenceByGenericId")
	public String updateDisplaySequenceByGenericId(DisplaySequenceUpdateList displaySequenceUpdateList, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, displaySequenceUpdateList);
			tx = em.getTransaction();
			tx.begin();
			boolean result = new LookupServiceBean().updateDisplaySequenceByGenericId(displaySequenceUpdateList, httpRequest, em);
			tx.commit();
			sendBroadcastForUpdateDisplaySequence(displaySequenceUpdateList);
			String json = new StoreForwardUtility().returnJsonPacket(displaySequenceUpdateList, "DisplaySequenceUpdateList",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, displaySequenceUpdateList.getLocationId(), Integer.parseInt(displaySequenceUpdateList.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Send broadcast for update display sequence.
	 *
	 * @param displaySequenceUpdateList
	 *            the display sequence update list
	 */
	private void sendBroadcastForUpdateDisplaySequence(DisplaySequenceUpdateList displaySequenceUpdateList)
	{
		try

		{
			String operationName = null;
			String serviceName = null;
			if (displaySequenceUpdateList.getTableName().equals("locations_to_functions"))
			{
				operationName = POSNServiceOperations.LocationsService_updateLocationsToFunctions.name();
				serviceName = POSNServices.LocationsService.name();
			}
			else if (displaySequenceUpdateList.getTableName().equals("contact_preferences"))
			{
				operationName = POSNServiceOperations.LookupService_updateContactPreference.name();
				serviceName = POSNServices.LookupService.name();
			}
			else if (displaySequenceUpdateList.getTableName().equals("category"))
			{
				operationName = POSNServiceOperations.CatalogService_update.name();
				serviceName = POSNServices.CatalogService.name();
			}
			else if (displaySequenceUpdateList.getTableName().equals("category"))
			{
				operationName = POSNServiceOperations.CatalogService_update.name();
				serviceName = POSNServices.CatalogService.name();
			}
			else if (displaySequenceUpdateList.getTableName().equals("items"))
			{
				operationName = POSNServiceOperations.ItemsService_add.name();
				serviceName = POSNServices.ItemsService.name();
			}

			else if (displaySequenceUpdateList.getTableName().equals("request_type"))
			{
				operationName = POSNServiceOperations.LookupService_addRequestType.name();
				serviceName = POSNServices.LookupService.name();
			}
			else if (displaySequenceUpdateList.getTableName().equals("reasons"))
			{
				operationName = POSNServiceOperations.LookupService_addReasons.name();
				serviceName = POSNServices.LookupService.name();
			}
			else if (displaySequenceUpdateList.getTableName().equals("discounts"))
			{
				operationName = POSNServiceOperations.LookupService_addDiscounts.name();
				serviceName = POSNServices.LookupService.name();
			}
			else if (displaySequenceUpdateList.getTableName().equals("items_attribute_type"))
			{
				operationName = POSNServiceOperations.LookupService_addItemsAttributeType.name();
				serviceName = POSNServices.LookupService.name();
			}
			else if (displaySequenceUpdateList.getTableName().equals("items_attribute"))
			{
				operationName = POSNServiceOperations.LookupService_updateItemsAttribute.name();
				serviceName = POSNServices.LookupService.name();
			}
			else if (displaySequenceUpdateList.getTableName().equals("location_to_location_details"))
			{
				operationName = POSNServiceOperations.LocationsService_update.name();
				serviceName = POSNServices.LocationsService.name();
			}
			else if (displaySequenceUpdateList.getTableName().equals("payment_method_type"))
			{
				operationName = POSNServiceOperations.LookupService_updatePaymentMethodType.name();
				serviceName = POSNServices.LookupService.name();
			}
			sendPacketForBroadcast(operationName, displaySequenceUpdateList, serviceName);
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe(httpRequest, e, "");
		}

	}

	/**
	 * Gets the all discounts by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all discounts by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllDiscountsByLocationId/{locationId}")
	public String getAllDiscountsByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
			Root<Discount> discount = criteria.from(Discount.class);
			TypedQuery<Discount> query = em.createQuery(criteria.select(discount).where(builder.equal(discount.get(Discount_.locationsId), locationId),
					builder.notEqual(discount.get(Discount_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all states by country id.
	 *
	 * @param countryId
	 *            the country id
	 * @return the all states by country id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllStatesByCountryId/{countryId}")
	public String getAllStatesByCountryId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("countryId") int countryId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<State> criteria = builder.createQuery(State.class);
			Root<State> account = criteria.from(State.class);
			TypedQuery<State> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(State_.countryId), countryId), builder.notEqual(account.get(State_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all city by country id.
	 *
	 * @param countryId
	 *            the country id
	 * @return the all city by country id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCityByCountryId/{countryId}")
	public String getAllCityByCountryId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("countryId") int countryId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<City> criteria = builder.createQuery(City.class);
			Root<City> account = criteria.from(City.class);
			TypedQuery<City> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(City_.countryId), countryId), builder.notEqual(account.get(City_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all city by state id.
	 *
	 * @param stateId
	 *            the state id
	 * @return the all city by state id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCityByStateId/{stateId}")
	public String getAllCityByStateId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("stateId") int stateId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<City> criteria = builder.createQuery(City.class);
			Root<City> account = criteria.from(City.class);
			TypedQuery<City> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(City_.stateId), stateId), builder.notEqual(account.get(City_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Sync cdo by cdo name and updated date.
	 *
	 * @param cdoName
	 *            the cdo name
	 * @param updatedDate
	 *            the updated date
	 * @param isLogin
	 *            the is login
	 * @return CDO database
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/syncCdoByCdoNameAndUpdatedDate/{cdoName}/{updatedDate}/{isLogin}")
	public String syncCdoByCdoNameAndUpdatedDate(@PathParam("cdoName") String cdoName, @PathParam("updatedDate") String updatedDate, @PathParam("isLogin") int isLogin,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		int isLocationSpecific = 0;
		try
		{
			if (!"countries".equals(cdoName))
			{
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			}
			else
			{

				em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			}

			LookupServiceBean bean = new LookupServiceBean();
			return bean.syncCdoByCdoNameAndUpdatedDateRecords(cdoName, updatedDate, sessionId, em, httpRequest, isLogin,isLocationSpecific,"0");
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);

		}
	}

	/**
	 * Gets the tax by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the tax by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getTaxByLocationIdAndName/{locationId}/{name}")
	public String getTaxByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
			Root<SalesTax> salesTax = criteria.from(SalesTax.class);
			TypedQuery<SalesTax> query = em.createQuery(criteria.select(salesTax).where(builder.equal(salesTax.get(SalesTax_.locationsId), locationId),
					builder.equal(salesTax.get(SalesTax_.taxName), name), builder.notEqual(salesTax.get(SalesTax_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all smiley by location id and feedback type id.
	 *
	 * @param feedbackTypeId
	 *            the feedback type id
	 * @return the all smiley by location id and feedback type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllSmileyByFeedbackTypeId/{feedbackTypeId}")
	public String getAllSmileyByLocationIdAndFeedbackTypeId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("feedbackTypeId") String feedbackTypeId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Smiley> criteria = builder.createQuery(Smiley.class);
			Root<Smiley> r = criteria.from(Smiley.class);
			TypedQuery<Smiley> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Smiley_.feedbackTypeId), feedbackTypeId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order source to sales tax.
	 *
	 * @param orderSourceToSalesTaxPacket
	 *            the order source to sales tax packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderSourceToSalesTax")
	public String updateOrderSourceToSalesTax(OrderSourceToSalesTaxPacket orderSourceToSalesTaxPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, orderSourceToSalesTaxPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceToSalesTaxHelper orderSourceToSalesTaxHelper = new OrderSourceToSalesTaxHelper();
			orderSourceToSalesTaxPacket.setOrderSourceToSalesTax(orderSourceToSalesTaxHelper.updateOrderSourceToSalesTax(httpRequest, em, orderSourceToSalesTaxPacket.getOrderSourceToSalesTax()));
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateOrderSourceToPaymentgatewayType.name(), orderSourceToSalesTaxPacket);
			String json = new StoreForwardUtility().returnJsonPacket(orderSourceToSalesTaxPacket, "OrderSourceToSalesTaxPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderSourceToSalesTaxPacket.getLocationId(), Integer.parseInt(orderSourceToSalesTaxPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderSourceToSalesTaxPacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source to sales tax by order source id.
	 *
	 * @param orderSourceId
	 *            the order source id
	 * @return the order source to sales tax by order source id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceToSalesTaxByOrderSourceId/{orderSourceId}")
	public String getOrderSourceToSalesTaxByOrderSourceId(@PathParam("orderSourceId") int orderSourceId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			OrderSourceToSalesTaxHelper orderSourceToSalesTaxHelper = new OrderSourceToSalesTaxHelper();
			List<OrderSourceToSalesTax> orderSourceToSalesTax = orderSourceToSalesTaxHelper.getOrderSourceToSalesTaxByOrderSourceId(httpRequest, em, orderSourceId);
			return new JSONUtility(httpRequest).convertToJsonString(orderSourceToSalesTax);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the sales tax by name and location id.
	 *
	 * @param taxName
	 *            the tax name
	 * @param locationId
	 *            the location id
	 * @return the sales tax by name and location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getSalesTaxByNameAndLocationId/{taxName}/{locationId}")
	public String getSalesTaxByNameAndLocationId(@PathParam("taxName") String taxName, @PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
			Root<SalesTax> r = criteria.from(SalesTax.class);
			TypedQuery<SalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(SalesTax_.taxName), taxName), builder.equal(r.get(SalesTax_.locationsId), locationId),
					builder.notEqual(r.get(SalesTax_.status), "D")));

			SalesTax salesTax = query.getSingleResult();
			return new JSONUtility(httpRequest).convertToJsonString(salesTax);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the shift schedule.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addShiftSchedule")
	public String addShiftSchedule(ShiftSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ShiftSchedule shiftSchedule = new LookupServiceBean().addShiftSchedule(lookupPacket.getShiftSchedule(), httpRequest, em);
			tx.commit();
			long startime = new TimezoneTime().getGMTTimeInMilis();
			tx.begin();
			addShiftSlotsByProcedure(shiftSchedule, em);
			tx.commit();
			long delta = new TimezoneTime().getGMTTimeInMilis() - startime;
			logger.warn(httpRequest, "total time taken to add reservation slots sec = " + delta / 1000);
			
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addShiftSchedule.name(), lookupPacket);
			lookupPacket.setShiftSchedule(shiftSchedule);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ShiftSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(shiftSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}

	}

	/**
	 * Adds the shift slots by procedure.
	 *
	 * @param shiftSchedule
	 *            the shift schedule
	 * @param em
	 *            the em
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	private boolean addShiftSlotsByProcedure(ShiftSchedule shiftSchedule, EntityManager em) throws Exception
	{
		
		String queryString = "call proc_generateshiftscheduleslots('"+shiftSchedule.getId()+"','"+shiftSchedule.getCreatedBy()+"')";
		
		int updateCount=em.createNativeQuery(queryString).executeUpdate();
		if(updateCount>0){
			return true;
		}
		return false;

	}

	/**
	 * Update shift schedule.
	 *
	 * @param shiftSchedulePacket
	 *            the shift schedule packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateShiftSchedule")
	public String updateShiftSchedule(ShiftSchedulePacket shiftSchedulePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, shiftSchedulePacket);

			tx = em.getTransaction();

			ShiftSchedule shiftSchedule = new LookupServiceBean().updateShiftSchedule(shiftSchedulePacket.getShiftSchedule(), httpRequest, em);
			tx.begin();
			deleteSlotsForShiftScheduleId(shiftSchedule.getId(), em);
			tx.commit();
			tx.begin();
			if (!shiftSchedule.getStatus().equals("I"))
			{
				addShiftSlotsByProcedure(shiftSchedule, em);
			}

			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateShiftSchedule.name(), shiftSchedulePacket);
			shiftSchedulePacket.setShiftSchedule(shiftSchedule);
			String json = new StoreForwardUtility().returnJsonPacket(shiftSchedulePacket, "ShiftSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,shiftSchedulePacket.getLocationId(), Integer.parseInt(shiftSchedulePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(shiftSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Delete slots for shift schedule id.
	 *
	 * @param shiftSchduleId
	 *            the shift schdule id
	 * @param em
	 *            the em
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void deleteSlotsForShiftScheduleId(String shiftSchduleId, EntityManager em) throws SQLException
	{
		if (shiftSchduleId != null)
		{
			String queryString = "delete from shift_slots where shift_schedule_id=? ";

			em.createNativeQuery(queryString).setParameter(1, shiftSchduleId).executeUpdate();

		}
	}

	/**
	 * Delete shift schedule.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteShiftSchedule")
	public String deleteShiftSchedule(ShiftSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			ShiftSchedule shiftSchedule = new LookupServiceBean().deleteShiftSchedule(lookupPacket.getShiftSchedule(), httpRequest, em);

			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteShiftSchedule.name(), lookupPacket);
			lookupPacket.setShiftSchedule(shiftSchedule);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ShiftSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(shiftSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}
	}

	/**
	 * Gets the all shift schedule by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all shift schedule by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllShiftScheduleByLocationId/{locationId}")
	public String getAllShiftScheduleByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			String queryString = "SELECT ss FROM ShiftSchedule ss " + " " + " where ss.locationId=? and ss.status!='D'";

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TypedQuery<ShiftSchedule> query = em.createQuery(queryString, ShiftSchedule.class).setParameter(1, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the shift schedule by id.
	 *
	 * @param id
	 *            the id
	 * @return the shift schedule by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getShiftScheduleById/{id}")
	public String getShiftScheduleById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ShiftSchedule> criteria = builder.createQuery(ShiftSchedule.class);
			Root<ShiftSchedule> ic = criteria.from(ShiftSchedule.class);
			TypedQuery<ShiftSchedule> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ShiftSchedule_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the shift schedule by order source group id.
	 *
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the shift schedule by order source group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getShiftScheduleByOrderSourceGroupId/{orderSourceGroupId}")
	public String getShiftScheduleByOrderSourceGroupId(@PathParam("orderSourceGroupId") String orderSourceGroupId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ShiftSchedule> criteria = builder.createQuery(ShiftSchedule.class);
			Root<ShiftSchedule> ic = criteria.from(ShiftSchedule.class);
			TypedQuery<ShiftSchedule> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ShiftSchedule_.orderSourceGroupId), orderSourceGroupId),
					builder.notEqual(ic.get(ShiftSchedule_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Block shift schedule for order source group.
	 *
	 * @param shiftSchedulePacket
	 *            the shift schedule packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/blockShiftScheduleForOrderSourceGroup")
	public String blockShiftScheduleForOrderSourceGroup(ShiftSchedulePacket shiftSchedulePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, shiftSchedulePacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule = new LookupServiceBean().blockShiftScheduleForOrderSourceGroup(shiftSchedulePacket.getOrderSourceGroupToShiftSchedule(),
					httpRequest, em);
			tx.commit();
			shiftSchedulePacket.setOrderSourceGroupToShiftSchedule(orderSourceGroupToShiftSchedule);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateShiftSchedule.name(), shiftSchedulePacket);
			String json = new StoreForwardUtility().returnJsonPacket(shiftSchedulePacket, "ShiftSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, shiftSchedulePacket.getLocationId(), Integer.parseInt(shiftSchedulePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(shiftSchedulePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Block shift schedule for order source.
	 *
	 * @param shiftSchedulePacket
	 *            the shift schedule packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/blockShiftScheduleForOrderSource")
	public String blockShiftScheduleForOrderSource(ShiftSchedulePacket shiftSchedulePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, shiftSchedulePacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceToShiftSchedule orderSourceToShiftSchedule = new LookupServiceBean().blockShiftScheduleForOrderSource(shiftSchedulePacket.getOrderSourceToShiftSchedule(), httpRequest, em);
			tx.commit();
			shiftSchedulePacket.setOrderSourceToShiftSchedule(orderSourceToShiftSchedule);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateShiftSchedule.name(), shiftSchedulePacket);
			String json = new StoreForwardUtility().returnJsonPacket(shiftSchedulePacket, "ShiftSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, shiftSchedulePacket.getLocationId(), Integer.parseInt(shiftSchedulePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(shiftSchedulePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the block shifts by order source group id.
	 *
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the block shifts by order source group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getBlockShiftsByOrderSourceGroupId/{orderSourceGroupId}")
	public String getBlockShiftsByOrderSourceGroupId(@PathParam("orderSourceGroupId") String orderSourceGroupId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroupToShiftSchedule> criteria = builder.createQuery(OrderSourceGroupToShiftSchedule.class);
			Root<OrderSourceGroupToShiftSchedule> r = criteria.from(OrderSourceGroupToShiftSchedule.class);
			TypedQuery<OrderSourceGroupToShiftSchedule> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceGroupToShiftSchedule_.orderSourceGroupId), orderSourceGroupId),
					builder.equal(r.get(OrderSourceGroupToShiftSchedule_.status), "B")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the block shifts by order source id.
	 *
	 * @param orderSourceId
	 *            the order source id
	 * @return the block shifts by order source id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getBlockShiftsByOrderSourceId/{orderSourceId}")
	public String getBlockShiftsByOrderSourceId(@PathParam("orderSourceId") int orderSourceId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceToShiftSchedule> criteria = builder.createQuery(OrderSourceToShiftSchedule.class);
			Root<OrderSourceToShiftSchedule> r = criteria.from(OrderSourceToShiftSchedule.class);
			TypedQuery<OrderSourceToShiftSchedule> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceToShiftSchedule_.orderSourceId), orderSourceId),
					builder.equal(r.get(OrderSourceToShiftSchedule_.status), "B")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source group by location id and order source group name.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceGroupName
	 *            the order source group name
	 * @return the order source group by location id and order source group name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceGroupByLocationIdAndOrderSourceGroupName/{locationId}/{orderSourceGroupName}")
	public String getOrderSourceGroupByLocationIdAndOrderSourceGroupName(@PathParam("locationId") String locationId, @PathParam("orderSourceGroupName") String orderSourceGroupName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSource = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSourceGroup_.locationsId), locationId),
					builder.equal(orderSource.get(OrderSourceGroup_.name), orderSourceGroupName)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the shift slots by order source id.
	 *
	 * @param orderSourceId
	 *            the order source id
	 * @param date
	 *            the date
	 * @param fromTime
	 *            the from time
	 * @return the shift slots by order source id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getShiftSlotsByOrderSourceId/{orderSourceId}/{date}/{fromTime}")
	public String getShiftSlotsByOrderSourceId(@PathParam("orderSourceId") String orderSourceId, @PathParam("date") String date, @PathParam("fromTime") String fromTime,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<ShiftSlots> shiftSlotsList = new ArrayList<ShiftSlots>();
			List<OrderSourceToShiftSchedule> orderSourceToShiftSchedulesList = new ArrayList<OrderSourceToShiftSchedule>();
			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderSourceId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ShiftSchedule> criteria = builder.createQuery(ShiftSchedule.class);
			Root<ShiftSchedule> ic = criteria.from(ShiftSchedule.class);
			TypedQuery<ShiftSchedule> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ShiftSchedule_.orderSourceGroupId), orderSource.getOrderSourceGroupId())));
			List<ShiftSchedule> shiftSchedules = query.getResultList();
			for (ShiftSchedule shiftSchedule : shiftSchedules)
			{
				List<OrderSourceToShiftSchedule> newOrderSourceToShiftSchedules = new ArrayList<OrderSourceToShiftSchedule>();
				List<ShiftSlots> newShiftSlots = new ArrayList<ShiftSlots>();
				newOrderSourceToShiftSchedules = new LookupServiceBean().getOrderSourceToShiftScheduleByShiftScheduleId(em, shiftSchedule.getId(), orderSourceId, date);
				newShiftSlots = getShiftSlotsByShiftScheduleId(em, shiftSchedule.getId(), date, fromTime);

				if (newOrderSourceToShiftSchedules != null && newOrderSourceToShiftSchedules.size() != 0)
				{
					orderSourceToShiftSchedulesList.addAll(newOrderSourceToShiftSchedules);
				}
				if (newShiftSlots != null && newShiftSlots.size() != 0)
				{
					shiftSlotsList.addAll(newShiftSlots);
				}
			}

			Boolean needToAdd;
			List<ShiftSlots> shiftSlotsArray = new ArrayList<ShiftSlots>();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (shiftSlotsList != null && orderSourceToShiftSchedulesList != null)
			{

				for (ShiftSlots shiftSlots : shiftSlotsList)
				{
					needToAdd = true;

					for (OrderSourceToShiftSchedule orderSourceToShiftSchedule : orderSourceToShiftSchedulesList)
					{
						Date fromTimeSlot = format.parse(date + " " + shiftSlots.getSlotTime());
						Date fromTimeOSTSH = format.parse(date + " " + orderSourceToShiftSchedule.getFromTime());
						Date toTimeOSTSH = format.parse(date + " " + orderSourceToShiftSchedule.getToTime());

						if (fromTimeSlot.compareTo(fromTimeOSTSH) >= 0 && fromTimeSlot.compareTo(toTimeOSTSH) <= 0)
						{
							needToAdd = false;
							break;
						}
					}

					if (needToAdd)
					{
						shiftSlotsArray.add(shiftSlots);
					}
				}
			}
			else
			{
				shiftSlotsArray = shiftSlotsList;
			}

			return new JSONUtility(httpRequest).convertToJsonString(shiftSlotsArray);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the shift slots by shift schedule id.
	 *
	 * @param em
	 *            the em
	 * @param shiftId
	 *            the shift id
	 * @param date
	 *            the date
	 * @param fromTime
	 *            the from time
	 * @return the shift slots by shift schedule id
	 * @throws Exception
	 *             the exception
	 */
	private List<ShiftSlots> getShiftSlotsByShiftScheduleId(EntityManager em, String shiftId, String date, String fromTime) throws Exception
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ShiftSlots> criteria = builder.createQuery(ShiftSlots.class);
		Root<ShiftSlots> r = criteria.from(ShiftSlots.class);
		TypedQuery<ShiftSlots> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ShiftSlots_.shiftScheduleId), shiftId), builder.equal(r.get(ShiftSlots_.date), date),
				builder.greaterThanOrEqualTo(r.get(ShiftSlots_.slotTime), fromTime), builder.notEqual(r.get(ShiftSlots_.status), "H")));

		return query.getResultList();

	}

	/**
	 * Hold shift session.
	 *
	 * @param holdShiftSlotPacket
	 *            the hold shift slot packet
	 * @return the string
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@POST
	@Path("/holdShiftSlotForSlotId")
	public String holdShiftSession(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, HoldShiftSlotPacket holdShiftSlotPacket) throws NirvanaXPException, FileNotFoundException, 
			InvalidSessionException, IOException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			UserSession session = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId);

			tx = em.getTransaction();
			tx.begin();

			String schema = session.getSchema_name();
			if (schema != null)
			{
				holdShiftSlotPacket.setSchemaName(schema);
			}

			int clientID = holdShiftSlotForClient(httpRequest, em, holdShiftSlotPacket, sessionId);
			tx.commit();
			holdShiftSlotPacket.setClientId(""+clientID);;
			String json = new StoreForwardUtility().returnJsonPacket(holdShiftSlotPacket, "HoldShiftSlotPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, holdShiftSlotPacket.getLocationId(), Integer.parseInt(holdShiftSlotPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(clientID);
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Un hold shift session for client.
	 *
	 * @param shiftHoldingClientId
	 *            the shift holding client id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/unHoldShiftSlotForClientId/{shiftHoldingClientId}")
	public String unHoldShiftSessionForClient(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("shiftHoldingClientId") String shiftHoldingClientId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String schema = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId).getSchema_name();

			return "" + (new LookupServiceBean().unHoldShiftSlotForClient(httpRequest, em, sessionId, shiftHoldingClientId, schema));

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Hold shift slot for client.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param holdShiftSlotPacket
	 *            the hold shift slot packet
	 * @param sessionId
	 *            the session id
	 * @return the int
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	private int holdShiftSlotForClient(HttpServletRequest httpRequest, EntityManager em, HoldShiftSlotPacket holdShiftSlotPacket, String sessionId) throws NirvanaXPException
	{

		HoldShiftSlotResponse holdShiftSlotResponse = new LookupServiceBean().holdShiftSlotForClient(httpRequest, em, holdShiftSlotPacket.getShiftSlots().getId(), sessionId, holdShiftSlotPacket
				.getShiftSlots().getUpdatedBy(), holdShiftSlotPacket.getSchemaName());
		ShiftSlots shiftSlots = holdShiftSlotResponse.getShiftSlots();
		if (shiftSlots != null)
		{
			ShiftSlots shiftSlotForpush = new ShiftSlots();
			shiftSlotForpush.setId(shiftSlots.getId());
			shiftSlotForpush.setStatus(shiftSlots.getStatus());
			holdShiftSlotPacket.setShiftSlots(shiftSlotForpush);
			return holdShiftSlotResponse.getShiftHoldingClientId();
		}

		return 0;
	}

	/**
	 * Update shift slot to block unblock.
	 *
	 * @param shiftSlotId
	 *            the shift slot id
	 * @param isBlock
	 *            the is block
	 * @param updatedBy
	 *            the updated by
	 * @return the string
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 */
	@GET
	@Path("/updateShiftSlotToBlockUnblock/{shift_slot_id}/{is_block}/{updatedBy}")
	public String updateShiftSlotToBlockUnblock(@PathParam("shift_slot_id") int shiftSlotId, @PathParam("is_block") int isBlock, @PathParam("updatedBy") String updatedBy,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws NirvanaXPException,  IOException, InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			LookupServiceBean bean = new LookupServiceBean();
			return new JSONUtility(httpRequest).convertToJsonString(bean.updateShiftSlot(shiftSlotId, isBlock, updatedBy, httpRequest, em));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the multiple location printer.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addMultipleLocationPrinter")
	public String addMultipleLocationPrinter(PrinterPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Printer result = new LookupServiceBean().addMultipleLocationsPrinters(em, lookupPacket.getPrinter(), lookupPacket, httpRequest);
			tx.commit();
			
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_addPrinter.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update multiple location printer.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateMultipleLocationPrinter")
	public String updateMultipleLocationPrinter(PrinterPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Printer result = new LookupServiceBean().updateMultipleLocationsPrinters(em, lookupPacket.getPrinter(), lookupPacket, httpRequest);
			tx.commit();
		
			
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				if (locationId != null && locationId.length() > 0)
				{
					lookupPacket.setLocationId(locationId);
				}
				else
				{
					lookupPacket.setLocationId(lookupPacket.getPrinter().getLocationsId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updatePrinter.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the multiple locations items attribute type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addMultipleLocationsItemsAttributeType")
	public String addMultipleLocationsItemsAttributeType(ItemsAttributeTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemsAttributeType result = new LookupServiceBean().addMultipleLocationsItemsAttributeType(em, lookupPacket.getItemsAttributeType(), lookupPacket, httpRequest);
			tx.commit();

			// todo shlok need
			// modulise code for push
			lookupPacket.setItemsAttributeType(result);
			
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_addItemsAttributeType.name(), lookupPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update multiple locations items attribute type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateMultipleLocationsItemsAttributeType")
	public String updateMultipleLocationsItemsAttributeType(ItemsAttributeTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemsAttributeType result = new LookupServiceBean().updateMultipleLocationsItemsAttributeType(em, lookupPacket.getItemsAttributeType(), lookupPacket, httpRequest);
			tx.commit();
			// todo shlok need
			// modulise code for push
			
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				if (locationId != null && locationId.length() > 0)
				{
					lookupPacket.setLocationId(locationId);
				}
				else
				{
					lookupPacket.setLocationId(lookupPacket.getItemsAttributeType().getLocationsId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updateItemsAttributeType.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the printer count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the printer count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterCountByLocationId/{locationId}")
	public BigInteger getPrinterCountByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select count(p.id) " + " from printers p " + " left join printers_type pt on pt.id=p.printers_type_id "
					+ " left join printers_interface pi on pi.id=p.printers_interface_id " + " left join printers_model pm on pm.id=p.printers_model_id "
					+ " where p.locations_id=? and p.printers_name != 'No Printer' and p.status !='D' ";

			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the printer count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param displayName
	 *            the display name
	 * @return the printer count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterCountByLocationId/{locationId}/{displayName}")
	public BigInteger getPrinterCountByLocationId(@PathParam("locationId") String locationId,@PathParam("displayName") String displayName, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select count(p.id) " + " from printers p " + " left join printers_type pt on pt.id=p.printers_type_id "
					+ " left join printers_interface pi on pi.id=p.printers_interface_id " + " left join printers_model pm on pm.id=p.printers_model_id "
					+ " where p.locations_id=? and p.printers_name != 'No Printer' and p.status !='D' " + " and p.display_name like '%" + displayName + "%' ";

			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the printer by location id and printer type id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @return the printer by location id and printer type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterByLocationIdAndPrinterTypeId/{locationId}/{startIndex}/{endIndex}")
	public String getPrinterByLocationIdAndPrinterTypeId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			List<PrinterDetailDisplayPacket> ans = new ArrayList<PrinterDetailDisplayPacket>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select p.display_name as p_display_name,pm.display_name as pm_display_name ," + " pt.name as pt_name,	pi.name as pi_name , p.id as p_id , p.ip_address as p_ip_address "
					+ " from printers p " + " left join printers_type pt on pt.id=p.printers_type_id " + " left join printers_interface pi on pi.id=p.printers_interface_id "
					+ " left join printers_model pm on pm.id=p.printers_model_id " + " where p.locations_id=? and p.printers_name != 'No Printer' and p.status !='D' limit " + startIndex + ","
					+ endIndex + "";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList)
			{
				PrinterDetailDisplayPacket detailDisplayPacket = new PrinterDetailDisplayPacket();
				detailDisplayPacket.setPrintDisplayName((String) objRow[0]);
				detailDisplayPacket.setPrinterModelName((String) objRow[1]);
				detailDisplayPacket.setPrinterTypeName((String) objRow[2]);
				detailDisplayPacket.setPrintInterfaceName((String) objRow[3]);
				detailDisplayPacket.setId((String) objRow[4]);
				detailDisplayPacket.setIpAddress((String) objRow[5]);
				ans.add(detailDisplayPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	/**
	 * Gets the printer by location id and printer type id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @return the printer by location id and printer type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterByLocationIdAndPrinterTypeName/{locationId}/{name}")
	public String getPrinterByLocationIdAndPrinterTypeName(@PathParam("locationId") String locationId, 
			@PathParam("name") String name) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			List<Printer> printers = new ArrayList<Printer>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			String sql = "select p.* "
					+ " from printers p "
					+ " left join printers_type pt on pt.id=p.printers_type_id "
					+ " where p.locations_id=? and pt.name = ? and p.status !='D' ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).setParameter(2, name).getResultList();
			for (Object[] objRow : resultList)
			{
				Printer p = new Printer();
				p.setId((String) objRow[0]);
				p.setPrintersName((String) objRow[1]);
				p.setPrintersModelId((int) objRow[2]);
				p.setPrintersTypeId((String) objRow[3]);
				p.setPrintersInterfaceId((String) objRow[4]);
				p.setDisplayName((String) objRow[5]);
				p.setDisplaySequence((int) objRow[6]);
				p.setLocationsId((String) objRow[7]);
				p.setStatus(((char) objRow[8])+"");
				p.setIpAddress((String) objRow[9]);
				p.setPort((int) objRow[10]);
				p.setCashRegisterToPrinter((int) objRow[11]);
				//p.setIsActive((int) objRow[12]);
				p.setCreated((Timestamp) objRow[13]);
				p.setCreatedBy((String) objRow[14]);
				p.setUpdated((Timestamp) objRow[15]);
				p.setUpdatedBy((String) objRow[16]);
				
				p.setIsTableTransferPrint((int) objRow[18]);
				p.setGlobalPrinterId((String) objRow[19]);
				p.setIsAutoBumpOn((int) objRow[20]);
				printers.add(p);
			}

			return new JSONUtility(httpRequest).convertToJsonString(printers);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the printer by location id and printer type id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param displayName
	 *            the display name
	 * @return the printer by location id and printer type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterByLocationIdAndPrinterTypeId/{locationId}/{startIndex}/{endIndex}/{displayName}")
	public String getPrinterByLocationIdAndPrinterTypeId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@PathParam("displayName") String displayName,@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}

			List<PrinterDetailDisplayPacket> ans = new ArrayList<PrinterDetailDisplayPacket>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select p.display_name as p_display_name,pm.display_name as pm_display_name ," + " pt.name as pt_name,	pi.name as pi_name , p.id as p_id , p.ip_address as p_ip_address "
					+ " from printers p " + " left join printers_type pt on pt.id=p.printers_type_id " + " left join printers_interface pi on pi.id=p.printers_interface_id "
					+ " left join printers_model pm on pm.id=p.printers_model_id " + " where p.locations_id=? and p.printers_name != 'No Printer' " + " and p.display_name like '%" + displayName
					+ "%' " + " and p.status !='D' limit " + startIndex + "," + endIndex + "";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList)
			{
				PrinterDetailDisplayPacket detailDisplayPacket = new PrinterDetailDisplayPacket();
				detailDisplayPacket.setPrintDisplayName((String) objRow[0]);
				detailDisplayPacket.setPrinterModelName((String) objRow[1]);
				detailDisplayPacket.setPrinterTypeName((String) objRow[2]);
				detailDisplayPacket.setPrintInterfaceName((String) objRow[3]);
				detailDisplayPacket.setId((String) objRow[4]);
				detailDisplayPacket.setIpAddress((String) objRow[5]);
				ans.add(detailDisplayPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the course count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the course count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCourseCountByLocationId/{locationId}")
	public BigInteger getCourseCountByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select count(*) from course where locations_id=? and status not in ('D')";

			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the course count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param displayName
	 *            the display name
	 * @return the course count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCourseCountByLocationId/{locationId}/{displayName}")
	public BigInteger getCourseCountByLocationId(@PathParam("locationId") String locationId,@PathParam("displayName") String displayName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select count(*) from course where locations_id=?"
					+ " and display_name like '%"+displayName+"%'  "
					+ " and status not in ('D')";
			;
			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the course by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @return the course by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCourseByLocationId/{locationId}/{startIndex}/{endIndex}")
	public String getCourseByLocationId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			List<CourseDisplayPacket> ans = new ArrayList<CourseDisplayPacket>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select id,course_name,display_name,description,status  from course where locations_id=? and status not in ('D') limit " + startIndex + "," + endIndex + "";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList)
			{
				CourseDisplayPacket detailDisplayPacket = new CourseDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setDisplayName((String) objRow[2]);
				detailDisplayPacket.setDescription((String) objRow[3]);
				detailDisplayPacket.setStatus(((char) objRow[4]) + "");

				ans.add(detailDisplayPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the course by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param displayName
	 *            the display name
	 * @return the course by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCourseByLocationId/{locationId}/{startIndex}/{endIndex}/{displayName}")
	public String getCourseByLocationId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex, @PathParam("displayName") String displayName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(displayName);

			List<CourseDisplayPacket> ans = new ArrayList<CourseDisplayPacket>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select id,course_name,display_name,description,status  from course where locations_id=? and display_name like '%"+temp+"%' and status not in ('D') limit " + startIndex + "," + endIndex + "";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList)
			{
				CourseDisplayPacket detailDisplayPacket = new CourseDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setDisplayName((String) objRow[2]);
				detailDisplayPacket.setDescription((String) objRow[3]);
				detailDisplayPacket.setStatus(((char) objRow[4]) + "");

				ans.add(detailDisplayPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the multiple location course.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addMultipleLocationCourse")
	public String addMultipleLocationCourse(CoursePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			List<Course> result = new LookupServiceBean().addMultipleLocationCourse(em, lookupPacket.getCourse(), lookupPacket, httpRequest);
			tx.commit();

			
			
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_addCourse.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update multiple location course.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateMultipleLocationCourse")
	public String updateMultipleLocationCourse(CoursePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Course result = new LookupServiceBean().updateMultipleLocationCourse(em, lookupPacket.getCourse(), lookupPacket, httpRequest,tx);
			tx.commit();
		
			// todo shlok need
			// modulise code for push
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				if (locationId != null && locationId.length() > 0)
				{
					lookupPacket.setLocationId(locationId);
				}
				else
				{
					lookupPacket.setLocationId(lookupPacket.getCourse().getLocationsId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updateCourse.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete multiple location course.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteMultipleLocationCourse")
	public String deleteMultipleLocationCourse(CoursePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Course result = new LookupServiceBean().deleteMultipleLocationCourse(em, lookupPacket.getCourse(), lookupPacket, httpRequest);
			tx.commit();

			// todo shlok need
			// modulise code for push
			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteCourse.name(), lookupPacket);
			}
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "CoursePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the items char by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @return the items char by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsCharByLocationId/{locationId}/{startIndex}/{endIndex}")
	public String getItemsCharByLocationId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			List<CourseDisplayPacket> ans = new ArrayList<CourseDisplayPacket>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select id,name,display_name,description,status  from items_char where locations_id=? and status !='D' limit " + startIndex + "," + endIndex + "";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList)
			{
				CourseDisplayPacket detailDisplayPacket = new CourseDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setDisplayName((String) objRow[2]);
				detailDisplayPacket.setDescription((String) objRow[3]);
				detailDisplayPacket.setStatus(((char) objRow[4]) + "");

				ans.add(detailDisplayPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the items char by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param displayName
	 *            the display name
	 * @return the items char by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsCharByLocationId/{locationId}/{startIndex}/{endIndex}/{displayName}")
	public String getItemsCharByLocationId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@PathParam("displayName") String displayName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(displayName);

			List<CourseDisplayPacket> ans = new ArrayList<CourseDisplayPacket>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select id,name,display_name,description,status  from items_char where locations_id=?"
					+ "  and display_name like '%"+temp+"%' "
					+ " and status !='D' limit " + startIndex + "," + endIndex + "";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList)
			{
				CourseDisplayPacket detailDisplayPacket = new CourseDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setDisplayName((String) objRow[2]);
				detailDisplayPacket.setDescription((String) objRow[3]);
				detailDisplayPacket.setStatus(((char) objRow[4]) + "");

				ans.add(detailDisplayPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the items char count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the items char count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsCharCountByLocationId/{locationId}")
	public BigInteger getItemsCharCountByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select count(*) from items_char where locations_id=? and status !='D' ";
			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the items char count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param displayName
	 *            the display name
	 * @return the items char count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsCharCountByLocationId/{locationId}/{displayName}")
	public BigInteger getItemsCharCountByLocationId(@PathParam("locationId") String locationId,@PathParam("displayName") String displayName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(displayName);

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select count(*) from items_char where locations_id=? "
					+ " and display_name like '%"+temp+"%'  "
					+ "  and status !='D' ";

			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the multiple location items char.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addMultipleLocationItemsChar")
	public String addMultipleLocationItemsChar(ItemsCharPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemsChar result = new LookupServiceBean().addMultipleLocationItemsChar(em, lookupPacket.getItemsChar(), lookupPacket, httpRequest);
			tx.commit();
			// todo shlok need
			// modulise code for push
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_addItemsChar.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update multiple location items char.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateMultipleLocationItemsChar")
	public String updateMultipleLocationItemsChar(ItemsCharPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			
			ItemsChar result = new LookupServiceBean().updateMultipleLocationItemsChar(em, lookupPacket.getItemsChar(), lookupPacket, httpRequest);
			tx.commit();
			
			// todo shlok need
			// modulise code for push
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				if (locationId != null && locationId.length() > 0)
				{
					lookupPacket.setLocationId(locationId);
				}
				else
				{
					lookupPacket.setLocationId(lookupPacket.getItemsChar().getLocationsId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updateItemsChar.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete multiple location items char.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteMultipleLocationItemsChar")
	public String deleteMultipleLocationItemsChar(ItemsCharPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsCharPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			ItemsChar result = new LookupServiceBean().deleteMultipleLocationItemsChar(em, lookupPacket.getItemsChar(), lookupPacket, httpRequest);
			tx.commit();
			
			// todo shlok need
			// modulise code for push
			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteItemsChar.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete multiple location printer.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteMultipleLocationPrinter")
	public String deleteMultipleLocationPrinter(PrinterPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "PrinterPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			Printer result = new LookupServiceBean().deleteMultipleLocationPrinter(em, lookupPacket.getPrinter(), lookupPacket, httpRequest);
			tx.commit();
			
			// todo shlok need
			// modulise code for push
			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_deletePrinter.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the multiple location sales tax.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addMultipleLocationSalesTax")
	public String addMultipleLocationSalesTax(SalesTaxPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			SalesTax result = new SalesTaxHelper().addMultipleLocationSalesTax(em, lookupPacket.getSalesTax(), lookupPacket, httpRequest);
			tx.commit();
			
			lookupPacket.setSalesTax(result);
			
			// todo shlok need
			// modulise code for push
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_addSalesTax.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update multiple location sales tax.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateMultipleLocationSalesTax")
	public String updateMultipleLocationSalesTax(SalesTaxPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			String[] locationsId = null;
			if (lookupPacket.getLocationsListId()!=null &&  lookupPacket.getLocationsListId().trim().length() > 0)
			{
				locationsId = lookupPacket.getLocationsListId().split(",");
			}
			SalesTax result = new SalesTaxHelper().updateMultipleLocationsSalesTax(em, lookupPacket.getSalesTax(), lookupPacket, httpRequest);
			tx.commit();
			
			// todo shlok need
			// modulise code for push
			
			if(locationsId!=null){
			for (String locationId : locationsId)
			{
				if (locationId != null && locationId.length() > 0)
				{
					lookupPacket.setLocationId(locationId);
				}
				else
				{
					lookupPacket.setLocationId(lookupPacket.getSalesTax().getLocationsId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updateSalesTax.name(), lookupPacket);
			}
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete multiple location sales tax.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteMultipleLocationSalesTax")
	public String deleteMultipleLocationSalesTax(SalesTaxPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			SalesTax result = new SalesTaxHelper().deleteMultipleLocationSalesTax(em, lookupPacket.getSalesTax(), lookupPacket, httpRequest);
			tx.commit();

			// todo shlok need
			// modulise code for push
			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteSalesTax.name(), lookupPacket);
			}
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "SalesTaxPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the category by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @return the category by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getSalesByLocationId/{locationId}/{startIndex}/{endIndex}")
	public String getCategoryByLocationId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return (new LookupServiceBean().getSalesTaxByLocationId(locationId, startIndex, endIndex, em, httpRequest));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the sales count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the sales count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getSalesCountByLocationId/{locationId}")
	public BigInteger getSalesCountByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "  select count(*) " + " from sales_tax c where c.locations_id=?  and c.status !='D'  ";
			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the discounts by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param discountTypeId
	 *            the discount type id
	 * @param displayName
	 *            the display name
	 * @return the discounts by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountsByLocationId/{locationId}/{startIndex}/{endIndex}/{discountTypeId}/{displayName}")
	public String getDiscountsByLocationId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
	   @PathParam("discountTypeId") String discountTypeId,
	   @PathParam("displayName") String displayName,
	   @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(displayName);

	   em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return (new LookupServiceBean().getDiscountsByLocationId(locationId, startIndex, endIndex, discountTypeId, temp, em, httpRequest));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the discount count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the discount count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountCountByLocationId/{locationId}")
	public BigInteger getDiscountCountByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select count(*) " + " from  discounts d join discounts_type dt on dt.id=d.discounts_type_id  " + "  where d.locations_id=?  and d.status !='D'  ";
			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the discount count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param discountTypeId
	 *            the discount type id
	 * @param displayName
	 *            the display name
	 * @return the discount count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountCountByLocationId/{locationId}/{discountTypeId}/{displayName}")
	public BigInteger getDiscountCountByLocationId(@PathParam("locationId") String locationId,@PathParam("discountTypeId") String discountTypeId,
			@PathParam("displayName") String displayName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(displayName);

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select count(*) " + " from  discounts d join discounts_type dt on dt.id=d.discounts_type_id  " 
			+ "  where d.locations_id=?  and d.status !='D' "
					+ " and d.display_name like '%" + temp + "%' ";
			if (discountTypeId !=null && !discountTypeId.equals("null"))
			{
				sql += " and d.discounts_type_id= '" + discountTypeId+"'";
			}

			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the multiple location discounts.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addMultipleLocationDiscounts")
	public String addMultipleLocationDiscounts(DiscountPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Discount result = new LookupServiceBean().addMultipleLocationDiscount(em, lookupPacket.getDiscount(), lookupPacket, httpRequest);
			tx.commit();

			lookupPacket.setDiscount(result);
			
			// todo shlok need
			// modulise code for push
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_addDiscounts.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update multiple location discounts.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateMultipleLocationDiscounts")
	public String updateMultipleLocationDiscounts(DiscountPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Discount result = new LookupServiceBean().updateMultipleLocationDiscount(em, lookupPacket.getDiscount(), lookupPacket, httpRequest);
			tx.commit();

			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				if (locationId != null && locationId.length() > 0)
				{
					lookupPacket.setLocationId(locationId);
				}
				else
				{
					lookupPacket.setLocationId(lookupPacket.getDiscount().getLocationsId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updateDiscounts.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete multiple location discounts.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteMultipleLocationDiscounts")
	public String deleteMultipleLocationDiscounts(DiscountPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Discount result = new LookupServiceBean().deleteMultipleLocationDiscount(em, lookupPacket.getDiscount(), lookupPacket, httpRequest);
			tx.commit();

			// todo shlok need
			// modulise code for push
			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteDiscounts.name(), lookupPacket);
			}
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "DiscountPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the multiple location items attribute.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addMultipleLocationItemsAttribute")
	public String addMultipleLocationItemsAttribute(ItemsAttributePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			logger.severe("ItemsAttributePacket========================================================"+new JSONUtility(httpRequest).convertToJsonString(lookupPacket));
			ItemsAttribute result = new LookupServiceBean().addMultipleLocationItemsAttribute(em, lookupPacket.getItemsAttribute(), lookupPacket, httpRequest);
			tx.commit();
			lookupPacket.setItemsAttribute(result);
			
			// todo shlok need
			// modulise code for push
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_addItemsAttribute.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update multiple location items attribute.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateMultipleLocationItemsAttribute")
	public String updateMultipleLocationItemsAttribute(ItemsAttributePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			logger.severe("ItemsAttributePacket========================================================"+new JSONUtility(httpRequest).convertToJsonString(lookupPacket));
			
			ItemsAttribute result = new LookupServiceBean().updateMultipleLocationItemsAttribute(em, lookupPacket.getItemsAttribute(), lookupPacket, httpRequest);
			tx.commit();
			// todo shlok need
			// modulise code for push
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				if (locationId != null && locationId.length() > 0)
				{
					lookupPacket.setLocationId(locationId);
				}
				else
				{
					lookupPacket.setLocationId(lookupPacket.getItemsAttribute().getLocationsId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updateItemsAttribute.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete multiple location items attribute.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteMultipleLocationItemsAttribute")
	public String deleteMultipleLocationItemsAttribute(ItemsAttributePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemsAttribute result = new LookupServiceBean().deleteMultipleLocationItemsAttribute(em, lookupPacket.getItemsAttribute(), lookupPacket, httpRequest);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsAttributePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			// todo shlok need
			// modulise code for push
			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteItemsAttribute.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete multiple locations items attribute type.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteMultipleLocationsItemsAttributeType")
	public String deleteMultipleLocationsItemsAttributeType(ItemsAttributeTypePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemsAttributeType result = new LookupServiceBean().deleteMultipleLocationItemsAttributeType(em, lookupPacket.getItemsAttributeType(), lookupPacket, httpRequest);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsAttributeTypePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		

			// todo shlok need
			// modulise code for push
			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteItemsAttributeType.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the item attributes type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param displayName
	 *            the display name
	 * @return the item attributes type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemAttributesTypeByLocationId/{locationId}/{startIndex}/{endIndex}/{displayName}")
	public String getItemAttributesTypeByLocationId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex,
			@PathParam("endIndex") int endIndex,
			@PathParam("displayName") String displayName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(displayName);

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return (new LookupServiceBean().getItemAttributesTypeByLocationId(locationId, startIndex, endIndex, temp, em, httpRequest));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the item attributes type count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param displayName
	 *            the display name
	 * @return the item attributes type count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemAttributesTypeCountByLocationId/{locationId}/{displayName}")
	public BigInteger getItemAttributesTypeCountByLocationId(@PathParam("locationId") String locationId, 
			@PathParam("displayName") String displayName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(displayName);

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select count(*) " + " from  items_attribute_type  " + "  where locations_id=?  "
					+ " and display_name like '%"+ temp + "%' "
					+ "and status !='D'  ";
			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the item attributes by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param name
	 *            the name
	 * @param itemAttributeTypeId
	 *            the item attribute type id
	 * @return the item attributes by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemAttributesByLocationId/{locationId}/{startIndex}/{endIndex}/{name}/{itemAttributeTypeId}")
	public String getItemAttributesByLocationId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex, @PathParam("name") String name,
			@PathParam("itemAttributeTypeId") String itemAttributeTypeId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (name == null || name.equals("null") || name.equals(null))
			{
				name = "";
			}
			name = name.trim();
			String temp = Utilities.convertAllSpecialCharForSearch(name);

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return (new LookupServiceBean().getItemAttributesByLocationId(temp, itemAttributeTypeId, locationId, startIndex, endIndex, em, httpRequest));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the item attributes count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @param itemAttributeTypeId
	 *            the item attribute type id
	 * @return the item attributes count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemAttributesCountByLocationId/{locationId}/{name}/{itemAttributeTypeId}")
	public BigInteger getItemAttributesCountByLocationId(@PathParam("locationId") String locationId, @PathParam("name") String name, @PathParam("itemAttributeTypeId") String itemAttributeTypeId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			if (name == null || name.equals("null") || name.equals(null))
			{
				name = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(name);

			String sql = "  select count(*) " + " from items_attribute ia left join items_attribute_type_to_items_attribute iatt " + " on iatt.items_attribute_id=ia.id "
					+ " where  ia.display_name like '" + temp + "%' ";
			if (itemAttributeTypeId !=null && !itemAttributeTypeId.equals("null") && !itemAttributeTypeId.equals("0"))
			{
				sql += " and iatt.items_attribute_type_id =  '" + itemAttributeTypeId+"'";
			}
			sql += "  and ia.locations_id=? and ia.status !='D' ";
			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all cdo mgmt location specific.
	 *
	 * @param isLocationSpecific
	 *            the is location specific
	 * @return the all cdo mgmt location specific
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCdoMgmtLocationSpecific/{isLocationSpecific}")
	public String getAllCdoMgmtLocationSpecific(@PathParam("isLocationSpecific") int isLocationSpecific, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CdoMgmt> criteria = builder.createQuery(CdoMgmt.class);
			Root<CdoMgmt> r = criteria.from(CdoMgmt.class);
			TypedQuery<CdoMgmt> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CdoMgmt_.isLocationSpecific), isLocationSpecific)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all cdo mgmt by update date location specific.
	 *
	 * @param updatedDate
	 *            the updated date
	 * @param isLocationSpecific
	 *            the is location specific
	 * @return the all cdo mgmt by update date location specific
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCdoMgmtByUpdatedDateLocationSpecific/{updatedDate}/{isLocationSpecific}")
	public String getAllCdoMgmtByUpdatedDateLocationSpecific(@PathParam("updatedDate") Timestamp updatedDate, @PathParam("isLocationSpecific") int isLocationSpecific,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CdoMgmt> criteria = builder.createQuery(CdoMgmt.class);
			Root<CdoMgmt> r = criteria.from(CdoMgmt.class);
			TypedQuery<CdoMgmt> query = em.createQuery(criteria.select(r).where(builder.greaterThan(r.get(CdoMgmt_.updated), updatedDate),
					builder.equal(r.get(CdoMgmt_.isLocationSpecific), isLocationSpecific)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Sync cdo by cdo name and updated date location specific.
	 *
	 * @param cdoName
	 *            the cdo name
	 * @param updatedDate
	 *            the updated date
	 * @param isLogin
	 *            the is login
	 * @param isLocationSpecific
	 *            the is location specific
	 * @param locationId
	 *            the location id
	 * @return CDO database
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/syncCdoByCdoNameAndUpdatedDateLocationSpecific/{cdoName}/{updatedDate}/{isLogin}/{isLocationSpecific}/{locationId}")
	public String syncCdoByCdoNameAndUpdatedDateLocationSpecific(@PathParam("cdoName") String cdoName, @PathParam("updatedDate") String updatedDate, @PathParam("isLogin") int isLogin,
			@PathParam("isLocationSpecific") int isLocationSpecific,
			@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		

	 	if ( "posn_partners".equals(cdoName) )
			{
	 		EntityManager em = null;
	 		EntityManager globalem = null;
	 		Location l =null;
				try {
					
					em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
					l = (Location)new CommonMethods().getObjectById("Location", em, Location.class, locationId);
				} finally {
					LocalSchemaEntityManager.getInstance().closeEntityManager(em);
				}
				try {
					
					globalem = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
					LookupServiceBean bean = new LookupServiceBean();
					return bean.syncCdoByCdoNameAndUpdatedDateRecords(cdoName, updatedDate, sessionId, globalem, httpRequest, isLogin,isLocationSpecific,l.getBusinessId()+"");
				} finally {
					GlobalSchemaEntityManager.getInstance().closeEntityManager(globalem);
				}
				
			}else if(!"countries".equals(cdoName))
			{
				EntityManager em = null;
		 		
				try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				LookupServiceBean bean = new LookupServiceBean();
				return bean.syncCdoByCdoNameAndUpdatedDateRecords(cdoName, updatedDate, sessionId, em, httpRequest, isLogin,isLocationSpecific,locationId);
				}finally{
					LocalSchemaEntityManager.getInstance().closeEntityManager(em);
				}
			}else
			{
				EntityManager globalem = null;
				try {
					globalem = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				LookupServiceBean bean = new LookupServiceBean();
				return bean.syncCdoByCdoNameAndUpdatedDateRecords(cdoName, updatedDate, sessionId, globalem, httpRequest, isLogin,isLocationSpecific,locationId);
				}finally {
					GlobalSchemaEntityManager.getInstance().closeEntityManager(globalem);
				}
			}

			
		 
	}

	/**
	 * Unblock shift schedule for order source group.
	 *
	 * @param shiftSchedulePacket
	 *            the shift schedule packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/unblockShiftScheduleForOrderSourceGroup")
	public String unblockShiftScheduleForOrderSourceGroup(ShiftSchedulePacket shiftSchedulePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, shiftSchedulePacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule = new LookupServiceBean().unblockShiftScheduleForOrderSourceGroup(shiftSchedulePacket.getOrderSourceGroupToShiftSchedule(),
					httpRequest, em);
			tx.commit();
			shiftSchedulePacket.setOrderSourceGroupToShiftSchedule(orderSourceGroupToShiftSchedule);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateShiftSchedule.name(), shiftSchedulePacket);
			String json = new StoreForwardUtility().returnJsonPacket(shiftSchedulePacket, "ShiftSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, shiftSchedulePacket.getLocationId(), Integer.parseInt(shiftSchedulePacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(shiftSchedulePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all shift schedule for validation.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the all shift schedule for validation
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/getAllShiftScheduleForValidation")
	public String getAllShiftScheduleForValidation(ShiftSchedulePacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			ShiftSchedule shiftSchedule = lookupPacket.getShiftSchedule();

			String queryString = "SELECT shift FROM ShiftSchedule shift where ((shift.fromTime between '" + shiftSchedule.getFromTime() + "' and '" + shiftSchedule.getToTime() + "')"
					+ " or (shift.toTime between '" + shiftSchedule.getFromTime() + "' and '" + shiftSchedule.getToTime() + "' )" + " or (shift.fromTime < '" + shiftSchedule.getFromTime()
					+ "' and shift.toTime > '" + shiftSchedule.getToTime() + "')) " + " and ((shift.fromDate between '" + shiftSchedule.getFromDate() + "' and '" + shiftSchedule.getToDate() + "')"
					+ " or (shift.toDate between '" + shiftSchedule.getFromDate() + "' and '" + shiftSchedule.getToDate() + "' ))" + " and shift.locationId='" + shiftSchedule.getLocationId()
					+ "' and shift.status!='D' and shift.orderSourceGroupId=? ";

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TypedQuery<ShiftSchedule> query = em.createQuery(queryString, ShiftSchedule.class).setParameter(1, shiftSchedule.getOrderSourceGroupId());
			
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update shift schedule for order source group.
	 *
	 * @param shiftSchedulePacket
	 *            the shift schedule packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateShiftScheduleForOrderSourceGroup")
	public String updateShiftScheduleForOrderSourceGroup(ShiftSchedulePacket shiftSchedulePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, shiftSchedulePacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule = new LookupServiceBean().updateShiftScheduleForOrderSourceGroup(shiftSchedulePacket.getOrderSourceGroupToShiftSchedule(),
					httpRequest, em);
			tx.commit();
			shiftSchedulePacket.setOrderSourceGroupToShiftSchedule(orderSourceGroupToShiftSchedule);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateShiftSchedule.name(), shiftSchedulePacket);
			String json = new StoreForwardUtility().returnJsonPacket(shiftSchedulePacket, "ShiftSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, shiftSchedulePacket.getLocationId(), Integer.parseInt(shiftSchedulePacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(shiftSchedulePacket);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order source group wait time.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderSourceGroupWaitTime")
	public String updateOrderSourceGroupWaitTime(OrderSourceGroupPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);

			tx = em.getTransaction();
			tx.begin();
			OrderSourceGroup result = new LookupServiceBean().updateOrderSourceGroupWaitTime((OrderSourceGroup) lookupPacket.getOrderSourceGroup(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateOrderSourceGroup.name(), lookupPacket);
			lookupPacket.setOrderSourceGroup(result);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "OrderSourceGroupPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the global location id.
	 *
	 * @param em
	 *            the em
	 * @return the global location id
	 */
	private String getGlobalLocationId(EntityManager em)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> criteria = builder.createQuery(Location.class);
		Root<Location> r = criteria.from(Location.class);
		TypedQuery<Location> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Location_.isGlobalLocation), 1)));
		try
		{
			return query.getSingleResult().getId();
		}
		catch (NoResultException e)
		{
			logger.severe("Global location is not present in this account.");
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return null;
	}

	/**
	 * Adds the multiple location item group.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addMultipleLocationItemGroup")
	public String addMultipleLocationItemGroup(ItemGroupPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemGroup result = new LookupServiceBean().addMultipleLocationItemGroup(em, lookupPacket.getItemGroup(), lookupPacket, httpRequest);
			tx.commit();
			
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.CatalogService_addItemGroup.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update multiple location item group.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateMultipleLocationItemGroup")
	public String updateMultipleLocationItemGroup(ItemGroupPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemGroup result = new LookupServiceBean().updateMultipleLocationItemGroup(em, lookupPacket.getItemGroup(), lookupPacket, httpRequest);
			tx.commit();
			
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				if (locationId != null && locationId.length() > 0)
				{
					lookupPacket.setLocationId(locationId);
				}
				else
				{
					lookupPacket.setLocationId(lookupPacket.getItemGroup().getLocationsId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.CatalogService_updateItemGroup.name(), lookupPacket);
			}
		
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete multiple location item group.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteMultipleLocationItemGroup")
	public String deleteMultipleLocationItemGroup(ItemGroupPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemGroup result = new LookupServiceBean().deleteMultipleLocationItemGroup(em, lookupPacket.getItemGroup(), lookupPacket, httpRequest);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemGroupPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.CatalogService_deleteItemGroup.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the item group by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param displayName
	 *            the display name
	 * @return the item group by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemGroupByLocationId/{locationId}/{startIndex}/{endIndex}/{displayName}")
	public String getItemGroupByLocationId(@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@PathParam("displayName") String displayName, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			List<ItemGroupDisplayPacket> ans = new ArrayList<ItemGroupDisplayPacket>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(displayName);

			String sql = " select id,name,display_name,status,item_group_id  from item_group where locations_id=? and status !='D' " + " and display_name like '%" + temp + "%' " + "limit "
					+ startIndex + "," + endIndex + "";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList)
			{
				ItemGroupDisplayPacket detailItemGroupDisplayPacket = new ItemGroupDisplayPacket();
				detailItemGroupDisplayPacket.setId((String) objRow[0]);
				detailItemGroupDisplayPacket.setName((String) objRow[1]);
				detailItemGroupDisplayPacket.setDisplayName((String) objRow[2]);
				detailItemGroupDisplayPacket.setStatus(((char) objRow[3]) + "");

				if (((String) objRow[4]) != null && !((String) objRow[4]).equals("0"))
				{
					ItemGroup itemGroup = (ItemGroup) new CommonMethods().getObjectById("ItemGroup", em,ItemGroup.class, ((String) objRow[4]));
					
					detailItemGroupDisplayPacket.setItemGroupDisplayName(itemGroup.getDisplayName());
				}

				ans.add(detailItemGroupDisplayPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the item group by id.
	 *
	 * @param id
	 *            the id
	 * @return the item group by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemGroupById/{id}")
	public String getItemGroupById(@PathParam("id") String id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemGroup> criteria = builder.createQuery(ItemGroup.class);
			Root<ItemGroup> r = criteria.from(ItemGroup.class);
			ItemGroup iG = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemGroup_.id), id))).getSingleResult();

			String queryString = "select l from Location l where l.id in   (select p.locationsId from ItemGroup p where p.globalId=?  and p.status not in ('D')) ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, iG.getId());
			List<Location> resultSet = query2.getResultList();
			iG.setLocationList(resultSet);

			return new JSONUtility(httpRequest).convertToJsonString(iG);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the item group count by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param displayName
	 *            the display name
	 * @return the item group count by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemGroupCountByLocationId/{locationId}/{displayName}")
	public BigInteger getItemGroupCountByLocationId(@PathParam("locationId") String locationId,
			@PathParam("displayName") String displayName, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (displayName == null || displayName.equals("null") || displayName.equals(null))
			{
				displayName = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(displayName);

			
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select count(*) from item_group where locations_id=? "
					+ " and display_name like '%"+temp+"%'  "
					+ "and status !='D' ";

			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the optional attribute by location id.
	 *
	 * @param id
	 *            the id
	 * @return the optional attribute by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOptionalAttributeByLocationId/{id}")
	public String getOptionalAttributeByLocationId(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsAttributeType> criteria = builder.createQuery(ItemsAttributeType.class);
			Root<ItemsAttributeType> r = criteria.from(ItemsAttributeType.class);
			TypedQuery<ItemsAttributeType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsAttributeType_.locationsId), id),
					builder.equal(r.get(ItemsAttributeType_.isRequired), 0), builder.notEqual(r.get(ItemsAttributeType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the device to pinpad.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addDeviceToPinpad")
	public String addDeviceToPinpad(DeviceToPinpadPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			DeviceToPinPad result = new LookupServiceBean().addDeviceToPinPad((DeviceToPinPad) lookupPacket.getDeviceToPinPad(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addDeviceToPinPad.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "DeviceToPinpadPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update device to pinpad.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateDeviceToPinpad")
	public String updateDeviceToPinpad(DeviceToPinpadPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			List<DeviceToPinPad> result = new LookupServiceBean().updateDeviceToPinPad(lookupPacket.getDeviceToPinPad(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateDeviceToPinPad.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "DeviceToPinpadPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete device to pinpad.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteDeviceToPinpad")
	public String deleteDeviceToPinpad(DeviceToPinpadPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			DeviceToPinPad result = new LookupServiceBean().deleteDeviceToPinPad((DeviceToPinPad) lookupPacket.getDeviceToPinPad(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteDeviceToPinPad.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "DeviceToPinpadPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment gateway to pinpads by payment gateway id.
	 *
	 * @param paymentGatewayId
	 *            the payment gateway id
	 * @param orderSourceId
	 *            the order source id
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the payment gateway to pinpads by payment gateway id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentGatewayToPinpadsByPaymentGatewayId/{paymentGatewayId}/{orderSourceId}/{orderSourceGroupId}")
	public String getPaymentGatewayToPinpadsByPaymentGatewayId(@PathParam("paymentGatewayId") int paymentGatewayId,  @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("orderSourceId") String orderSourceId,@PathParam("orderSourceGroupId") String orderSourceGroupId ) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<PaymentGatewayToPinpad> resultSet = new ArrayList<PaymentGatewayToPinpad>();
			try
			{
				String queryString = "select l from PaymentGatewayToPinpad l where l.paymentGatewayId=?  and l.status != 'D'";

				if (orderSourceId != null)
				{
					queryString = queryString + " and l.orderSourceToPaymentGatewayTypeId in (" + " select os.id from OrderSourceToPaymentgatewayType os " + "where os.orderSourceId = "
							+ orderSourceId + " " + ")";
				}

				if (orderSourceGroupId !=null)
				{
					queryString = queryString + " and l.orderSourceGroupToPaymentGatewayTypeId in (" + " select os.id from OrderSourceGroupToPaymentgatewayType os " + "where os.orderSourceGroupId = "
							+ orderSourceGroupId + " " + ")";
				}

				TypedQuery<PaymentGatewayToPinpad> query = em.createQuery(queryString, PaymentGatewayToPinpad.class).setParameter(1, paymentGatewayId);
				resultSet = query.getResultList();
			}
			catch (Exception e)
			{
				// todo shlok need
				// handle proper Exception
				logger.severe(e);
			}
			return new JSONUtility(httpRequest).convertToJsonString(resultSet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all payment type.
	 *
	 * @return the all payment type
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllPaymentType")
	public String getAllPaymentType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentType> criteria = builder.createQuery(PaymentType.class);
			Root<PaymentType> paymentType = criteria.from(PaymentType.class);
			TypedQuery<PaymentType> query = em.createQuery(criteria.select(paymentType));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the printer by location id and ip.
	 *
	 * @param printerDetailPacket
	 *            the printer detail packet
	 * @return the printer by location id and ip
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/getPrinterByLocationIdAndIp")
	public String getPrinterByLocationIdAndIp(PrinterDetailPacket printerDetailPacket) throws Exception
	{
		EntityManager em = null;
		String sessionId = httpRequest.getHeader(NIRVANA_ACCESS_TOKEN_HEADER_NAME);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> r = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Printer_.locationsId), printerDetailPacket.getLocationId()),
					builder.equal(r.get(Printer_.ipAddress), printerDetailPacket.getIpAddress()), builder.notEqual(r.get(Printer_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}

		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the printer model by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the printer model by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterModelByLocationId/{locationId}")
	public String getPrinterModelByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String sessionId = httpRequest.getHeader(NIRVANA_ACCESS_TOKEN_HEADER_NAME);
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrintersModel> criteria = builder.createQuery(PrintersModel.class);
			Root<PrintersModel> r = criteria.from(PrintersModel.class);
			TypedQuery<PrintersModel> query = em.createQuery(criteria.select(r)
					.where(builder.equal(r.get(PrintersModel_.locationsId), locationId), builder.notEqual(r.get(PrintersModel_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}

		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the KDS printer by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param printerModelId
	 *            the printer model id
	 * @return the KDS printer by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getKDSPrinterByLocationIdAndModelId/{locationId}/{printerModelId1}/{printerModelId2}")
	public String getKDSPrinterByLocationId(@PathParam("locationId") String locationId,
			@PathParam("printerModelId1") int printerModelId1,@PathParam("printerModelId2") int printerModelId2) throws Exception {
		EntityManager em = null;
		String sessionId = httpRequest.getHeader(NIRVANA_ACCESS_TOKEN_HEADER_NAME);
		try {
			/*em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> r = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Printer_.locationsId), locationId), builder.equal(r.get(Printer_.printersModelId), printerModelId1),
					 builder.equal(r.get(Printer_.printersModelId), printerModelId2),builder.equal(r.get(Printer_.status), "A")));
		*/
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "SELECT p FROM Printer p where p.locationsId=? and p.status='A' and p.printersModelId in(?,?)";
			TypedQuery<Printer> query = em.createQuery(queryString, Printer.class).setParameter(1, locationId).setParameter(2, printerModelId1).setParameter(3, printerModelId2);
			
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}

		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the printer model.
	 *
	 * @return the printer model
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrinterModel")
	public String getPrinterModel() throws Exception
	{
		EntityManager em = null;
		String sessionId = httpRequest.getHeader(NIRVANA_ACCESS_TOKEN_HEADER_NAME);
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrintersModel> criteria = builder.createQuery(PrintersModel.class);
			Root<PrintersModel> r = criteria.from(PrintersModel.class);
			TypedQuery<PrintersModel> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(PrintersModel_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}

		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the all option type.
	 *
	 * @return the all option type
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOptionType")
	public String getAllOptionType() throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OptionType> criteria = builder.createQuery(OptionType.class);
			Root<OptionType> optionType = criteria.from(OptionType.class);
			TypedQuery<OptionType> query = em.createQuery(criteria.select(optionType));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the delivery option by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the delivery option by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDeliveryOptionByLocationId/{locationId}")
	public String getDeliveryOptionByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DeliveryOption> criteria = builder.createQuery(DeliveryOption.class);
			Root<DeliveryOption> r = criteria.from(DeliveryOption.class);
			TypedQuery<DeliveryOption> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(DeliveryOption_.locationId), locationId),
					builder.notEqual(r.get(DeliveryOption_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}

		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the delivery option by id.
	 *
	 * @param id
	 *            the id
	 * @return the delivery option by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDeliveryOptionById/{id}")
	public String getDeliveryOptionById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DeliveryOption> criteria = builder.createQuery(DeliveryOption.class);
			Root<DeliveryOption> r = criteria.from(DeliveryOption.class);
			TypedQuery<DeliveryOption> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(DeliveryOption_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}

		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Adds the delivery option.
	 *
	 * @param deliveryOptionPacket
	 *            the delivery option packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addDeliveryOption")
	public String addDeliveryOption(DeliveryOptionPacket deliveryOptionPacket) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			DeliveryOption deliveryOption = new LookupServiceBean().addDeliveryOption(deliveryOptionPacket.getDeliveryOption(), em,httpRequest);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addDeliveryOptionStatus.name(), deliveryOptionPacket);
			String json = new StoreForwardUtility().returnJsonPacket(deliveryOptionPacket, "DeliveryOptionPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, deliveryOptionPacket.getLocationId(), Integer.parseInt(deliveryOptionPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(deliveryOption);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update delivery option.
	 *
	 * @param deliveryOptionPacket
	 *            the delivery option packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateDeliveryOption")
	public String updateDeliveryOption(DeliveryOptionPacket deliveryOptionPacket) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			DeliveryOption deliveryOption = new LookupServiceBean().updateDeliveryOption(deliveryOptionPacket.getDeliveryOption(), em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateDeliveryOptionStatus.name(), deliveryOptionPacket);
			String json = new StoreForwardUtility().returnJsonPacket(deliveryOptionPacket, "DeliveryOptionPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, deliveryOptionPacket.getLocationId(), Integer.parseInt(deliveryOptionPacket.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(deliveryOption);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete delivery option.
	 *
	 * @param deliveryOptionPacket
	 *            the delivery option packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteDeliveryOption")
	public String deleteDeliveryOption(DeliveryOptionPacket deliveryOptionPacket) throws Exception
	{

		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			DeliveryOption deliveryOption = new LookupServiceBean().deleteDeliveryOption(deliveryOptionPacket.getDeliveryOption(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteDeliveryOptionStatus.name(), deliveryOptionPacket);
			String json = new StoreForwardUtility().returnJsonPacket(deliveryOptionPacket, "DeliveryOptionPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, deliveryOptionPacket.getLocationId(), Integer.parseInt(deliveryOptionPacket.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(deliveryOption);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all payment gateway to pinpad.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all payment gateway to pinpad
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllPaymentGatewayToPinpad/{locationId}")
	public String getAllPaymentGatewayToPinpad(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentGatewayToPinpad> criteria = builder.createQuery(PaymentGatewayToPinpad.class);
			Root<PaymentGatewayToPinpad> r = criteria.from(PaymentGatewayToPinpad.class);
			TypedQuery<PaymentGatewayToPinpad> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(PaymentGatewayToPinpad_.status), "D"),
					builder.equal(r.get(PaymentGatewayToPinpad_.locationsId), locationId)));
			List<PaymentGatewayToPinpad> groupToDeliveryOptions = query.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(groupToDeliveryOptions);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment gateway to pinpad by id.
	 *
	 * @param id
	 *            the id
	 * @return the payment gateway to pinpad by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentGatewayToPinpadById/{id}")
	public String getPaymentGatewayToPinpadById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			PaymentGatewayToPinpad pinpad = (PaymentGatewayToPinpad) new CommonMethods().getObjectById("PaymentGatewayToPinpad", em,PaymentGatewayToPinpad.class, id);
			return new JSONUtility(httpRequest).convertToJsonString(pinpad);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	/**
	 * Adds the device to pinpad.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdateLocationsToShiftPreAssignServer")
	public String addUpdateLocationsToShiftPreAssignServer(LocationsToShiftPreAssignServerPacket locationsToShiftPreAssignServerPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			List<LocationsToShiftPreAssignServer> result = new LookupServiceBean().addUpdateLocationsToShiftPreAssignServer(locationsToShiftPreAssignServerPacket.getLocationsToShiftPreAssignServerList(), httpRequest, em,locationsToShiftPreAssignServerPacket.getLocationId());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addUpdateLocationsToShiftPreAssignServer.name(), locationsToShiftPreAssignServerPacket);
			String json = new StoreForwardUtility().returnJsonPacket(locationsToShiftPreAssignServerPacket, "LocationsToShiftPreAssignServerPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, locationsToShiftPreAssignServerPacket.getLocationId(), Integer.parseInt(locationsToShiftPreAssignServerPacket.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	/**
	 * Gets the Sns Sms Template.
	 *
	 * @return the Sns Sms Template
	 * * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllSmsTemplate/{locationId}")
	public String getAllSmsTemplate(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<SMSTemplate> criteria = builder.createQuery(SMSTemplate.class);
			Root<SMSTemplate> discount = criteria.from(SMSTemplate.class);
			TypedQuery<SMSTemplate> query = em.createQuery(criteria.select(discount).where(
					builder.notEqual(discount.get(SMSTemplate_.status), "D"),
					builder.equal(discount.get(SMSTemplate_.locationId), locationId )));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	/**
	 * Gets the Sns Sms Template.
	 *
	 * @return the Sns Sms Template
	 * * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getSmsTemplateById/{id}")
	public String getSmsTemplateById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<SMSTemplate> criteria = builder.createQuery(SMSTemplate.class);
			Root<SMSTemplate> discount = criteria.from(SMSTemplate.class);
			TypedQuery<SMSTemplate> query = em.createQuery(criteria.select(discount).where(builder.equal(discount.get(SMSTemplate_.id), id )));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	
	/**
	 * Adds the order status.
	 *
	 * @param packet
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addSMSTemplate")
	public String addSMSTemplate(SMSTemplatePacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			SMSTemplate orderStatus = new LookupServiceBean().addSMSTemplate(packet.getSmsTemplate(), httpRequest, em);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(packet, "SMSTemplatePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(), Integer.parseInt(packet.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(orderStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	 
 
	
	/**
	 * Gets the Items Schedule by id.
	 *
	 * @param id
	 *            the id
	 * @return the Items schedule by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsScheduleById/{id}")
	public String getItemsScheduleById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsSchedule> criteria = builder.createQuery(ItemsSchedule.class);
			Root<ItemsSchedule> ic = criteria.from(ItemsSchedule.class);
			TypedQuery<ItemsSchedule> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ItemsSchedule_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	/**
	 * Adds the Items  schedule.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addItemsSchedule")
	public String addItemsSchedule(ItemsSchedulePacket lookupPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			ItemsSchedule reservationsSchedule = new LookupServiceBean().updateItemsSchedule(lookupPacket.getItemsSchedule(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateItemsSchedule.name(), lookupPacket);
			
			lookupPacket.setItemsSchedule(reservationsSchedule);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(reservationsSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}

	}
	
	/**
	 * Update reservations schedule.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateItemsSchedule")
	public String updateItemsSchedule(ItemsSchedulePacket lookupPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			ItemsSchedule reservationsSchedule = new LookupServiceBean().updateItemsSchedule(lookupPacket.getItemsSchedule(), httpRequest, em);
			tx.commit();
		
			sendPacketForBroadcast(POSNServiceOperations.LookupService_updateItemsSchedule.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(reservationsSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Delete reservations schedule.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteItemsSchedule")
	public String deleteItemsSchedule(ItemsSchedulePacket lookupPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			ItemsSchedule reservationsSchedule = new LookupServiceBean().deleteItemsSchedule(lookupPacket.getItemsSchedule(), httpRequest, em);

			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteItemsSchedule.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsSchedulePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(reservationsSchedule);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}
	}
	

	/**
	 * Gets the all reservations schedule by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all reservations schedule by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllItemsScheduleByLocationId/{locationId}")
	public String getAllItemsScheduleByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		try
		{
			String queryString = "SELECT res FROM ItemsSchedule res " + " " + " where res.locationId=? and res.status!='D'";

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			TypedQuery<ItemsSchedule> query = em.createQuery(queryString, ItemsSchedule.class).setParameter(1, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@POST
	@Path("/addMultipleLocationNutritions")
	public String addMultipleLocationNutritions(NutritionsPacket lookupPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Nutritions result = new LookupServiceBean().addMultipleLocationNutritions(em, lookupPacket.getNutritions(), lookupPacket, httpRequest);
			tx.commit();
	
			// todo shlok need
			// modulise code for push
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_addNutritions.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update multiple location items char.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateMultipleLocationNutritions")
	public String updateMultipleLocationNutritions(NutritionsPacket lookupPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Nutritions	 result = new LookupServiceBean().updateMultipleLocationNutritions(em, lookupPacket.getNutritions(), lookupPacket, httpRequest);
			tx.commit();

			// todo shlok need
			// modulise code for push
			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				if (locationId != null && locationId.length() > 0)
				{
					lookupPacket.setLocationId(locationId);
				}
				else
				{
					lookupPacket.setLocationId(lookupPacket.getNutritions().getLocationsId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updateNutritions.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/getAllNutritionsByLocationId/{locationId}")
	public String getAllNutritionsByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Nutritions> criteria = builder.createQuery(Nutritions.class);
			Root<Nutritions> r = criteria.from(Nutritions.class);
			TypedQuery<Nutritions> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(Nutritions_.status), "D"),
					builder.equal(r.get(Nutritions_.locationsId), locationId)));
			List<Nutritions> groupToDeliveryOptions = query.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(groupToDeliveryOptions);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	/**
	 * Delete multiple location items char.
	 *
	 * @param lookupPacket
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteMultipleLocationNutritions")
	public String deleteMultipleLocationNutritions(NutritionsPacket lookupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Nutritions result = new LookupServiceBean().deleteMultipleLocationNutritions(em, lookupPacket.getNutritions(), lookupPacket, httpRequest);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "NutritionsPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
	
			// todo shlok need
			// modulise code for push
			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId)
			{
				lookupPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteNutritions.name(), lookupPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getNutritionsById/{id}")
	public String getNutritionsById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			 
			Nutritions nutritions = (Nutritions) new CommonMethods().getObjectById("Nutritions", em,Nutritions.class, id);
			String queryString = "select l from Location l where l.id in   (select p.locationsId from Nutritions p where p.globalId=? and p.status not in ('D')) ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, nutritions.getId());
			List<Location> resultSet = query2.getResultList();
			nutritions.setLocationList(resultSet);
			return new JSONUtility(httpRequest).convertToJsonString(nutritions);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order status.
	 *
	 * @param packet
	 *            the lookup packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateSMSTemplate")
	public String updateSMSTemplate(SMSTemplatePacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			SMSTemplate orderStatus = new LookupServiceBean().updateSMSTemplate(packet.getSmsTemplate(), httpRequest, em);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(packet, "SMSTemplatePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(), Integer.parseInt(packet.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(orderStatus);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllNutritionsByLocationIdNameAndIndex/{locationId}/{name}/{startIndex}/{endIndex}")
	public String getAllNutritionsByLocationIdNameAndIndex(@PathParam("locationId") String locationId,
			@PathParam("name") String name,@PathParam("startIndex") int startIndex,@PathParam("endIndex") int endIndex) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			String nameStr="";
			List<Nutritions> nutritionList = new ArrayList<Nutritions>();
			if(name!=null && !name.equals("null") && !name.equals("NULL") && !name.equals("Null")){
				nameStr = " and l.name like '"+name+"%'";
			}
			String queryString = "select l.id from nutritions l where l.status != 'D' and l.locations_id=?  "+nameStr+"   limit   " + startIndex + "," + endIndex;
			List<String> result = em.createNativeQuery(queryString).setParameter(1, locationId).getResultList();
			for(String id:result){
				nutritionList.add((Nutritions) new CommonMethods().getObjectById("Nutritions", em,Nutritions.class, id));
			}
			return new JSONUtility(httpRequest).convertToJsonString(nutritionList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getCountOfAllNutritionsByLocationIdName/{locationId}/{name}")
	public String getCountOfAllNutritionsByLocationIdName(@PathParam("locationId") String locationId,
			@PathParam("name") String name) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			String nameStr="";
			if(name!=null && !name.equals("null") && !name.equals("NULL") && !name.equals("Null")){
				nameStr = " and l.name like '"+name+"%'";
			}
			String queryString = "select count(*) from Nutritions l where l.status != 'D' and l.locationsId=?  " +nameStr;
			return new JSONUtility(httpRequest).convertToJsonString(em.createQuery(queryString).setParameter(1, locationId).getSingleResult());

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@POST
	@Path("/addUpdateItemAttributeToDatePacket")
	public String addUpdateItemAttributeToDatePacket(ItemAttributeToDatePacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			
			tx = em.getTransaction();
			tx.begin();
			
			//#46374  I am able to add same item in the same date for scheduling
			try {
				
				String sqlForGlobalItemId = " select i from ItemAttributeToDate i where i.locationId =? and i.itemAttributeId = ? and i.status not in ('D','I') "
						+ "and i.date = ?";
				@SuppressWarnings("unchecked")
				List<ItemAttributeToDate> resultList = em.createQuery(sqlForGlobalItemId)
						.setParameter(1, packet.getItemAttributeToDate().getLocationId()).setParameter(2, packet.getItemAttributeToDate().getItemAttributeId())
						.setParameter(3, packet.getItemAttributeToDate().getDate())
						.getResultList();
				
				
				if(resultList != null && resultList.size() > 0)
				{
					return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_DUPLICATE_ITEMS_SCHEDULE_ON_SAME_DATE, MessageConstants.ERROR_MESSAGE_DUPLICATE_ITEMS_SCHEDULE_ON_SAME_DATE, null).toString();
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				logger.severe(e);
			}
			ItemAttributeToDatePacket result = new LookupServiceBean().addUpdateItemAttributeToDate(em, packet);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addUpdateItemAttributeToDate.name(), packet);
			String json = new StoreForwardUtility().returnJsonPacket(packet, "ItemAttributeToDatePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,packet.getLocationId(), Integer.parseInt(packet.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@POST
	@Path("/deleteItemAttributeToDatePacket")
	public String deleteItemAttributeToDatePacket(ItemAttributeToDatePacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			ItemAttributeToDatePacket result = new LookupServiceBean().deleteItemAttributeToDate(em, packet);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteItemAttributeToDate.name(), packet);
			String json = new StoreForwardUtility().returnJsonPacket(packet, "ItemAttributeToDatePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(), Integer.parseInt(packet.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/getItemAttributeToDate/{locationId}")
	public String getItemToDate (@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<ItemAttributeToDate> items = new LookupServiceBean().getItemAttributeToDate(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(items);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
   }
	
	@GET
	@Path("/getItemAttributesByLocationId/{locationId}/{itemAttributeTypeId}")
	public String getItemAttributesByLocationId(@PathParam("locationId") String locationId,@PathParam("itemAttributeTypeId") String itemAttributeTypeId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			return new LookupServiceBean().getItemAttributeByLocationId(itemAttributeTypeId,locationId,em,httpRequest);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@POST
	@Path("/addUpdateOrderAdditionalQuestions")
	public String addUpdateOrderAdditionalQuestions(OrderAdditionalQuestionsPacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			OrderAdditionalQuestionsPacket result = new LookupServiceBean().addUpdateOrderAdditionalQuestions(em, packet,httpRequest);
			tx.commit();
			
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addUpdateOrderAdditionalQuestions.name(), result);
			String json = new StoreForwardUtility().returnJsonPacket(result, "OrderAdditionalQuestionPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(), Integer.parseInt(packet.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@POST
	@Path("/deleteOrderAdditionalQuestionsPacket")
	public String deleteOrderAdditionalQuestionsPacket(OrderAdditionalQuestionsPacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			OrderAdditionalQuestionsPacket result = new LookupServiceBean().deleteOrderAdditionalQuestions(em, packet);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_deleteOrderAdditionalQuestions.name(), packet);
			String json = new StoreForwardUtility().returnJsonPacket(packet, "OrderAdditionalQuestionPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(), Integer.parseInt(packet.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/getAllOrderAdditionalQuestions/{locationId}")
	public String getAllOrderAdditionalQuestions(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<OrderAdditionalQuestion> orderAdditionalQuestions = new LookupServiceBean().getOrderAdditionalQuestions(em,locationId);
			return new JSONUtility(httpRequest).convertToJsonString(orderAdditionalQuestions);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
   }
	
	@POST
	@Path("/addUpdateAdditionalQuestionAnswer")
	public String addUpdateAdditionalQuestionAnswer(AdditionalQuestionAnswerPacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			AdditionalQuestionAnswerPacket result = new LookupServiceBean().addUpdateAdditionalQuestionAnswer(em, packet);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addUpdateOrderAdditionalQuestions.name(), packet);
			String json = new StoreForwardUtility().returnJsonPacket(packet, "AdditionalQuestionAnswerPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(), Integer.parseInt(packet.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/getAdditionalQuestionAnswerByOrderId/{orderId}")
	public String getAdditionalQuestionAnswerByOrderId (@PathParam("orderId") String orderId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			AdditionalQuestionAnswerPacket orderAdditionalQuestions = new LookupServiceBean().getAdditionalQuestionAnswerPacket(em,orderId);
			return new JSONUtility(httpRequest).convertToJsonString(orderAdditionalQuestions);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
   }
	
	@POST
	@Path("/updateItemsAttributeAvailability")
	public String updateItemsAttributeAvailability(ItemsAttributePacket lookupPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			ItemsAttribute result = new LookupServiceBean().updateItemsAttributeAvailability( lookupPacket.getItemsAttribute(), httpRequest, em);
			tx.commit();
			lookupPacket.setItemsAttribute(result);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addItemsAttribute.name(), lookupPacket);
			String json = new StoreForwardUtility().returnJsonPacket(lookupPacket, "ItemsAttributePacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, lookupPacket.getLocationId(), Integer.parseInt(lookupPacket.getMerchantId()));
	
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/itemAttributeToDate/{itemAttributeId}/{date}")
	public String getItemToDate (@PathParam("itemAttributeId") int itemAttributeId,@PathParam("date") String date) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<ItemAttributeToDate> items = new LookupServiceBean().getItemAttributeToDate(em, itemAttributeId,date);
			return new JSONUtility(httpRequest).convertToJsonString(items);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
   }
	
	@GET
	@Path("/orderAdditionalQuestion/{id}")
	public String orderAdditionalQuestion(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			
			return new JSONUtility(httpRequest).convertToJsonString((OrderAdditionalQuestion) new CommonMethods().getObjectById("OrderAdditionalQuestion", em,OrderAdditionalQuestion.class, id));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
}



