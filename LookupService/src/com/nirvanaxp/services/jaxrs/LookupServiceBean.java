/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hsqldb.lib.HashSet;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.relationalentity.EntityRelationshipManager;
import com.nirvanaxp.common.utils.relationalentity.helper.RoleRelationsHelper;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.global.types.entities.countries.Countries;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfo;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.jaxrs.packets.AdditionalQuestionAnswerPacket;
import com.nirvanaxp.services.jaxrs.packets.CoursePacket;
import com.nirvanaxp.services.jaxrs.packets.DiscountPacket;
import com.nirvanaxp.services.jaxrs.packets.DisplaySequenceData;
import com.nirvanaxp.services.jaxrs.packets.DisplaySequenceUpdateList;
import com.nirvanaxp.services.jaxrs.packets.ItemAttributeToDatePacket;
import com.nirvanaxp.services.jaxrs.packets.ItemGroupPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsAttributePacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsAttributeTypePacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsCharPacket;
import com.nirvanaxp.services.jaxrs.packets.NutritionsPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderAdditionalQuestionsPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.PrinterPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.CdoMgmt;
import com.nirvanaxp.types.entities.CdoMgmt_;
import com.nirvanaxp.types.entities.Day;
import com.nirvanaxp.types.entities.FieldType;
import com.nirvanaxp.types.entities.TransactionalCurrency;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.application.Application;
import com.nirvanaxp.types.entities.application.ApplicationToFunction;
import com.nirvanaxp.types.entities.business.BusinessHour;
import com.nirvanaxp.types.entities.business.BusinessHour_;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.category.CategoryToDiscount;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter;
import com.nirvanaxp.types.entities.catalog.category.ItemAttributeToDate;
import com.nirvanaxp.types.entities.catalog.category.ItemToDate;
import com.nirvanaxp.types.entities.catalog.course.Course;
import com.nirvanaxp.types.entities.catalog.course.Course_;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemGroup;
import com.nirvanaxp.types.entities.catalog.items.ItemGroup_;
import com.nirvanaxp.types.entities.catalog.items.Item_;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeToNutritions;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeToNutritions_;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeTypeToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeTypeToItemsAttribute_;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType_;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute_;
import com.nirvanaxp.types.entities.catalog.items.ItemsChar;
import com.nirvanaxp.types.entities.catalog.items.ItemsCharToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsChar_;
import com.nirvanaxp.types.entities.catalog.items.ItemsSchedule;
import com.nirvanaxp.types.entities.catalog.items.ItemsScheduleDay;
import com.nirvanaxp.types.entities.catalog.items.ItemsToDiscount;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttributeType_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttribute_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsChar;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsChar_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToNutritions;
import com.nirvanaxp.types.entities.catalog.items.ItemsToNutritions_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.ItemsToSchedule;
import com.nirvanaxp.types.entities.catalog.items.ItemsType;
import com.nirvanaxp.types.entities.catalog.items.Nutritions;
import com.nirvanaxp.types.entities.catalog.items.Nutritions_;
import com.nirvanaxp.types.entities.catalog.items.StorageType;
import com.nirvanaxp.types.entities.device.DeviceToPinPad;
import com.nirvanaxp.types.entities.device.DeviceToRegister;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.DiscountToReasons;
import com.nirvanaxp.types.entities.discounts.DiscountWays;
import com.nirvanaxp.types.entities.discounts.Discount_;
import com.nirvanaxp.types.entities.discounts.DiscountsType;
import com.nirvanaxp.types.entities.employee.EmployeeOperation;
import com.nirvanaxp.types.entities.employee.EmployeeOperationToAlertMessage;
import com.nirvanaxp.types.entities.feedback.FeedbackField;
import com.nirvanaxp.types.entities.feedback.FeedbackQuestion;
import com.nirvanaxp.types.entities.feedback.FeedbackType;
import com.nirvanaxp.types.entities.feedback.Smiley;
import com.nirvanaxp.types.entities.function.Function;
import com.nirvanaxp.types.entities.function.Functions;
import com.nirvanaxp.types.entities.inventory.Inventory;
import com.nirvanaxp.types.entities.inventory.InventoryAttributeBOM;
import com.nirvanaxp.types.entities.inventory.InventoryItemBom;
import com.nirvanaxp.types.entities.inventory.InventoryItemDefault;
import com.nirvanaxp.types.entities.inventory.LocationsToSupplier;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurementType;
import com.nirvanaxp.types.entities.locations.BusinessType;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.locations.LocationToApplication;
import com.nirvanaxp.types.entities.locations.LocationsToFunction;
import com.nirvanaxp.types.entities.locations.LocationsToShiftPreAssignServer;
import com.nirvanaxp.types.entities.locations.LocationsToShiftPreAssignServerHistory;
import com.nirvanaxp.types.entities.locations.receipt.PrinterReceipt;
import com.nirvanaxp.types.entities.orders.AdditionalQuestionAnswer;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.orders.DeliveryOption;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;
import com.nirvanaxp.types.entities.orders.OperationalShiftSchedule;
import com.nirvanaxp.types.entities.orders.OptionType;
import com.nirvanaxp.types.entities.orders.OrderAdditionalQuestion;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
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
import com.nirvanaxp.types.entities.orders.OrderToServerAssignment;
import com.nirvanaxp.types.entities.orders.OrderType;
import com.nirvanaxp.types.entities.orders.ShiftSchedule;
import com.nirvanaxp.types.entities.orders.ShiftSlotActiveClientInfo;
import com.nirvanaxp.types.entities.orders.ShiftSlots;
import com.nirvanaxp.types.entities.payment.PaymentGatewayToPinpad;
import com.nirvanaxp.types.entities.payment.PaymentGatewayType;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.PaymentType;
import com.nirvanaxp.types.entities.payment.PaymentWay;
import com.nirvanaxp.types.entities.payment.Paymentgateway;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.printers.Printer_;
import com.nirvanaxp.types.entities.printers.PrintersInterface;
import com.nirvanaxp.types.entities.printers.PrintersModel;
import com.nirvanaxp.types.entities.printers.PrintersType;
import com.nirvanaxp.types.entities.reasons.ReasonType;
import com.nirvanaxp.types.entities.reasons.Reasons;
import com.nirvanaxp.types.entities.reservation.ContactPreference;
import com.nirvanaxp.types.entities.reservation.RequestType;
import com.nirvanaxp.types.entities.reservation.ReservationsSchedule;
import com.nirvanaxp.types.entities.reservation.ReservationsScheduleDay;
import com.nirvanaxp.types.entities.reservation.ReservationsScheduleXref;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus;
import com.nirvanaxp.types.entities.reservation.ReservationsType;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.roles.RolesToFunction;
import com.nirvanaxp.types.entities.salestax.OrderSourceGroupToSalesTax;
import com.nirvanaxp.types.entities.salestax.OrderSourceToSalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.sms.SMSTemplate;
import com.nirvanaxp.types.entities.time.Timezone;
import com.nirvanaxp.types.entities.tip.EmployeeMasterToJobRoles;
import com.nirvanaxp.types.entities.tip.JobRoles;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.types.entities.user.UsersToRole;

// TODO: Auto-generated Javadoc
/**
 * Session Bean implementation class LookupServiceBean.
 */
class LookupServiceBean {

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(LookupServiceBean.class.getName());

	/** The session clear time in minutes. */
	private int sessionClearTimeInMinutes = 10;

	/**
	 * Adds the reservations status.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations status
	 * @throws Exception
	 *             the exception
	 */

	ReservationsStatus addReservationsStatus(ReservationsStatus rStatus, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null){
			rStatus.setId(new StoreForwardUtility().generateUUID());
			
		}

		rStatus=em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Update reservations status.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations status
	 * @throws Exception
	 *             the exception
	 */

	ReservationsStatus updateReservationsStatus(ReservationsStatus rStatus, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Delete reservations status.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations status
	 * @throws Exception
	 *             the exception
	 */

	ReservationsStatus deleteReservationsStatus(ReservationsStatus rStatus, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		ReservationsStatus u = (ReservationsStatus) new CommonMethods().getObjectById("ReservationsStatus", em,
				ReservationsStatus.class, rStatus.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		return u;
	}

	/**
	 * Adds the reservations type.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations type
	 * @throws Exception
	 *             the exception
	 */

	ReservationsType addReservationsType(ReservationsType rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		if (rStatus.getId() == 0)
			// rStatus.setId(new StoreForwardUtility().generateUUID());

			em.persist(rStatus);

		return rStatus;
	}

	/**
	 * Update reservations type.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations type
	 * @throws Exception
	 *             the exception
	 */

	ReservationsType updateReservationsType(ReservationsType rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Delete reservations type.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations type
	 * @throws Exception
	 *             the exception
	 */

	ReservationsType deleteReservationsType(ReservationsType rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		em.remove(em.merge(rStatus));

		return rStatus;
	}

	/**
	 * Adds the feedback question.
	 *
	 * @param feedbackQuestion
	 *            the feedback question
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the feedback question
	 * @throws Exception
	 *             the exception
	 */

	FeedbackQuestion addFeedbackQuestion(FeedbackQuestion feedbackQuestion, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		feedbackQuestion.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (feedbackQuestion.getId() == null)
			feedbackQuestion.setId(new StoreForwardUtility().generateUUID());

		em.persist(feedbackQuestion);

		return feedbackQuestion;
	}

	/**
	 * Update feedback question.
	 *
	 * @param feedbackQuestion
	 *            the feedback question
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the feedback question
	 * @throws Exception
	 *             the exception
	 */

	FeedbackQuestion updateFeedbackQuestion(FeedbackQuestion feedbackQuestion, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		feedbackQuestion.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(feedbackQuestion);

		return feedbackQuestion;
	}

	/**
	 * Delete feedback question.
	 *
	 * @param feedbackQuestion
	 *            the feedback question
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the feedback question
	 * @throws Exception
	 *             the exception
	 */

	FeedbackQuestion deleteFeedbackQuestion(FeedbackQuestion feedbackQuestion, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {

		FeedbackQuestion u = (FeedbackQuestion) new CommonMethods().getObjectById("FeedbackQuestion", em,
				FeedbackQuestion.class, feedbackQuestion.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		return feedbackQuestion;
	}

	/**
	 * Adds the feedback field.
	 *
	 * @param feedbackField
	 *            the feedback field
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the feedback field
	 * @throws Exception
	 *             the exception
	 */

	FeedbackField addFeedbackField(FeedbackField feedbackField, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		feedbackField.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (feedbackField.getId() == 0) {
			// feedbackField.setId(new StoreForwardUtility().generateUUID());
		}
		em.persist(feedbackField);

		return feedbackField;
	}

	/**
	 * Update feedback field.
	 *
	 * @param feedbackField
	 *            the feedback field
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the feedback field
	 * @throws Exception
	 *             the exception
	 */

	FeedbackField updateFeedbackField(FeedbackField feedbackField, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		feedbackField.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(feedbackField);

		return feedbackField;
	}

	/**
	 * Delete feedback field.
	 *
	 * @param feedbackField
	 *            the feedback field
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the feedback field
	 * @throws Exception
	 *             the exception
	 */

	FeedbackField deleteFeedbackField(FeedbackField feedbackField, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		FeedbackField u = em.find(FeedbackField.class, feedbackField.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		return u;
	}

	/**
	 * Update feedback field status.
	 *
	 * @param feedbackField
	 *            the feedback field
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the feedback field
	 * @throws Exception
	 *             the exception
	 */

	FeedbackField updateFeedbackFieldStatus(FeedbackField feedbackField, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		FeedbackField u = em.find(FeedbackField.class, feedbackField.getId());
		u.setStatus(feedbackField.getStatus());
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		return u;
	}

	/**
	 * Adds the request type.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the request type
	 * @throws Exception
	 *             the exception
	 */

	RequestType addRequestType(RequestType rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null)
			rStatus.setId(new StoreForwardUtility().generateUUID());
		em.persist(rStatus);

		return rStatus;
	}

	/**
	 * Update request type.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the request type
	 * @throws Exception
	 *             the exception
	 */

	RequestType updateRequestType(RequestType rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Delete request type.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the request type
	 * @throws Exception
	 *             the exception
	 */

	RequestType deleteRequestType(RequestType rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		RequestType u = (RequestType) new CommonMethods().getObjectById("RequestType", em, RequestType.class,
				rStatus.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		return u;
	}

	/**
	 * Adds the contact preference.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the contact preference
	 * @throws Exception
	 *             the exception
	 */

	ContactPreference addContactPreference(ContactPreference rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null) {
			rStatus.setId(new StoreForwardUtility().generateUUID());
		}
		rStatus = em.merge(rStatus);
		return rStatus;
	}

	/**
	 * Update contact preference.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the contact preference
	 * @throws Exception
	 *             the exception
	 */

	ContactPreference updateContactPreference(ContactPreference rStatus, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Delete contact preference.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the contact preference
	 * @throws Exception
	 *             the exception
	 */

	ContactPreference deleteContactPreference(ContactPreference rStatus, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		ContactPreference u = (ContactPreference) new CommonMethods().getObjectById("ContactPreference", em,
				ContactPreference.class, rStatus.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		return u;
	}

	/**
	 * Adds the reservations schedule.
	 *
	 * @param resSchedule
	 *            the res schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations schedule
	 * @throws Exception
	 *             the exception
	 */

	ReservationsSchedule addReservationsSchedule(ReservationsSchedule resSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		if (resSchedule != null) {
			resSchedule = addUpdateReservationSchedule(resSchedule, httpRequest, em);
		}

		return resSchedule;
	}

	/**
	 * Adds the update reservation schedule.
	 *
	 * @param resSchedule
	 *            the res schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @throws Exception
	 *             the exception
	 */
	private ReservationsSchedule addUpdateReservationSchedule(ReservationsSchedule resSchedule,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {
		resSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		int shiftGroupId = 0;
		shiftGroupId = getMaxGroupId(em);
		resSchedule.setShiftGroupId(shiftGroupId + 1);
		if (resSchedule.getId() == null) {
			resSchedule.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			resSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			resSchedule.setId(new StoreForwardUtility().generateUUID());

		}
		// set created and updated values
		if (resSchedule != null && resSchedule.getReservationsScheduleDays() != null
				&& resSchedule.getReservationsScheduleDays().size() > 0) {

			for (ReservationsScheduleDay reservationsScheduleDay : resSchedule.getReservationsScheduleDays()) {
				if (reservationsScheduleDay.getId() == 0) {
					reservationsScheduleDay.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
	
				if (resSchedule.getId() != null && reservationsScheduleDay.getReservationsScheduleId() == null) {
					reservationsScheduleDay.setReservationsScheduleId(resSchedule.getId());
				}
				reservationsScheduleDay.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			}
		}

		if (resSchedule != null && resSchedule.getReservationsScheduleXref() != null
				&& resSchedule.getReservationsScheduleXref().size() > 0) {

			for (ReservationsScheduleXref reservationsScheduleXref : resSchedule.getReservationsScheduleXref()) {
				logger.severe(
						"reservationsScheduleXref.getReservationsScheduleId()=============================================="
								+ reservationsScheduleXref.getReservationsScheduleId());

				if (reservationsScheduleXref.getId() == 0) {
					reservationsScheduleXref.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				if (resSchedule.getId() != null && reservationsScheduleXref.getReservationsScheduleId() == null) {
					reservationsScheduleXref.setReservationsScheduleId(resSchedule.getId());
				}
				reservationsScheduleXref.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			}
		}

		resSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		resSchedule = em.merge(resSchedule);

		return resSchedule;
	}

	/**
	 * Update reservations schedule.
	 *
	 * @param resSchedule
	 *            the res schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations schedule
	 * @throws Exception
	 *             the exception
	 */

	ReservationsSchedule updateReservationsSchedule(ReservationsSchedule resSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {

		ReservationsSchedule reservationScheduleAfterSearch = (ReservationsSchedule) new CommonMethods()
				.getObjectById("ReservationsSchedule", em, ReservationsSchedule.class, resSchedule.getId());
		if (reservationScheduleAfterSearch != null) {
	
			reservationScheduleAfterSearch.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			resSchedule.setReservationsScheduleXref(reservationScheduleAfterSearch.getReservationsScheduleXref());

		}

		resSchedule.setShiftGroupId(reservationScheduleAfterSearch.getShiftGroupId());

		resSchedule = addUpdateReservationSchedule(resSchedule, httpRequest, em);
		return resSchedule;
	}

	/**
	 * Update shift timer.
	 *
	 * @param resSchedule
	 *            the res schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations schedule
	 * @throws Exception
	 *             the exception
	 */

	ReservationsSchedule updateShiftTimer(ReservationsSchedule resSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		ReservationsSchedule u = (ReservationsSchedule) new CommonMethods().getObjectById("ReservationsSchedule", em,
				ReservationsSchedule.class, resSchedule.getId());
		u.setIsReservationsAllowed(resSchedule.getIsReservationsAllowed());
		u.setFromTime(resSchedule.getFromTime());
		u.setToTime(resSchedule.getToTime());
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		if (resSchedule.getReservationsScheduleXref() != null) {
			for (ReservationsScheduleXref r : resSchedule.getReservationsScheduleXref()) {
				r.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				em.merge(r);

			}
		}
		return u;
	}

	/**
	 * Update shift day wise.
	 *
	 * @param resSchedule
	 *            the res schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations schedule
	 * @throws Exception
	 *             the exception
	 */

	ReservationsSchedule updateShiftDayWise(ReservationsSchedule resSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		// get reservatio schedule details
		ReservationsSchedule u = (ReservationsSchedule) new CommonMethods().getObjectById("ReservationsSchedule", em,
				ReservationsSchedule.class, resSchedule.getId());
		int dayId = 0;
		for (ReservationsScheduleDay d : resSchedule.getReservationsScheduleDays()) {
			dayId = d.getDaysId();
		}
		int[] allDayId = new int[7];
		int i = 0;
		for (ReservationsScheduleDay allDay : u.getReservationsScheduleDays()) {
			allDayId[i] = allDay.getDaysId();
			i++;
		}

		if (allDayId[0] == dayId && i == 1) {
			updateExistingSchedule(u, resSchedule, em);
		} else {

			int groupId = u.getShiftGroupId();
			removeDayFromOldReservationSchedule(groupId, dayId, httpRequest, em, u);
			ReservationsSchedule new_shift = new ReservationsSchedule();
			new_shift.setCreated(u.getCreated());
			new_shift.setCreatedBy(u.getCreatedBy());
			new_shift.setEndWeek(u.getEndWeek());
			new_shift.setIsReservationsAllowed(resSchedule.getIsReservationsAllowed());
			new_shift.setFromTime(resSchedule.getFromTime());
			new_shift.setToTime(resSchedule.getToTime());
			new_shift.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			new_shift.setUpdatedBy(u.getUpdatedBy());
			new_shift.setLocationId(u.getLocationId());
			new_shift.setShiftName(u.getShiftName());
			new_shift.setStartWeek(u.getStartWeek());
			new_shift.setStatus(u.getStatus());
			new_shift.setSlotTime(u.getSlotTime());
			new_shift.setReservationAllowed(u.getReservationAllowed());

			List<ReservationsSchedule> resFromDate = getMinFromDateByGroupId(u.getShiftGroupId(), httpRequest, em);
			for (ReservationsSchedule rf : resFromDate) {
				new_shift.setFromDate(rf.getFromDate());
			}

			List<ReservationsSchedule> restoDate = getMaxToDateByGroupId(u.getShiftGroupId(), httpRequest, em);
			for (ReservationsSchedule rt : restoDate) {
				new_shift.setToDate(rt.getToDate());
			}

			// new_shift.setFromDate(u.getFromDate());
			// new_shift.setToDate(u.getToDate());
			new_shift.setShiftGroupId(u.getShiftGroupId());
			try {
				if (new_shift.getId() == null)
					new_shift.setId(new StoreForwardUtility().generateUUID());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			em.persist(new_shift);

			addResevationScheduleDay(em, resSchedule, new_shift);
			addOrUpdateReservationScheduleXref(em, resSchedule, new_shift);

		}
		return resSchedule;
	}

	/**
	 * Removes the day from old reservation schedule.
	 *
	 * @param groupId
	 *            the group id
	 * @param dayId
	 *            the day id
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param u
	 *            the u
	 * @throws Exception
	 *             the exception
	 */
	private void removeDayFromOldReservationSchedule(int groupId, int dayId, HttpServletRequest httpRequest,
			EntityManager em, ReservationsSchedule u) throws Exception {
		List<ReservationsScheduleDay> resDay = getReservationsScheduleDayByDayId(groupId, dayId, httpRequest, em);

		for (ReservationsScheduleDay rd : resDay) {
			em.remove(rd);

		}

	}

	/**
	 * Update existing schedule.
	 *
	 * @param u
	 *            the u
	 * @param resSchedule
	 *            the res schedule
	 * @param em
	 *            the em
	 * @throws Exception
	 *             the exception
	 */
	private void updateExistingSchedule(ReservationsSchedule u, ReservationsSchedule resSchedule, EntityManager em)
			throws Exception {
		u.setReservationAllowed(resSchedule.getReservationAllowed());
		u.setSlotTime(resSchedule.getSlotTime());
		u.setIsReservationsAllowed(resSchedule.getIsReservationsAllowed());
		u.setFromTime(resSchedule.getFromTime());
		u.setToTime(resSchedule.getToTime());
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		List<ReservationsSchedule> resFromDate = getMinFromDateByGroupId(u.getShiftGroupId(), null, em);
		for (ReservationsSchedule rf : resFromDate) {
			u.setFromDate(rf.getFromDate());
		}

		List<ReservationsSchedule> restoDate = getMaxToDateByGroupId(u.getShiftGroupId(), null, em);
		for (ReservationsSchedule rt : restoDate) {
			u.setToDate(rt.getToDate());
		}

		em.merge(u);

		if (resSchedule.getReservationsScheduleXref() != null) {
			for (ReservationsScheduleXref r : resSchedule.getReservationsScheduleXref()) {
				r.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				em.merge(r);

			}
		}
	}

	/**
	 * Update shift date wise.
	 *
	 * @param resSchedule
	 *            the res schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations schedule
	 * @throws Exception
	 *             the exception
	 */

	ReservationsSchedule updateShiftDateWise(ReservationsSchedule resSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		// get reservatio schedule details
		ReservationsSchedule u = (ReservationsSchedule) new CommonMethods().getObjectById("ReservationsSchedule", em,
				ReservationsSchedule.class, resSchedule.getId());
		String newDate = resSchedule.getFromDate();
		String fromDate = u.getFromDate();
		String toDate = u.getToDate();
		int slotTime = resSchedule.getSlotTime();
		int reservationAllowed = resSchedule.getReservationAllowed();
		if (newDate.equals(fromDate) && newDate.equals(toDate)) {
			updateShift(u, resSchedule, reservationAllowed, slotTime, em, newDate, toDate, true, true, httpRequest);
		} else if (newDate.equals(fromDate) && !newDate.equals(toDate)) {
			updateShift(u, resSchedule, reservationAllowed, slotTime, em, newDate, toDate, true, false, httpRequest);
		} else if (!newDate.equals(fromDate) && newDate.equals(toDate)) {
			updateShift(u, resSchedule, reservationAllowed, slotTime, em, newDate, toDate, false, true, httpRequest);
		} else {
			updateShift(u, resSchedule, reservationAllowed, slotTime, em, newDate, toDate, false, false, httpRequest);
		}
		return resSchedule;
	}

	/**
	 * Update shift.
	 *
	 * @param u
	 *            the u
	 * @param resSchedule
	 *            the res schedule
	 * @param reservationAllowed
	 *            the reservation allowed
	 * @param slotTime
	 *            the slot time
	 * @param em
	 *            the em
	 * @param newDate
	 *            the new date
	 * @param toDate
	 *            the to date
	 * @param fromDateMatch
	 *            the from date match
	 * @param toDateMatch
	 *            the to date match
	 * @throws ParseException
	 *             the parse exception
	 */
	private void updateShift(ReservationsSchedule u, ReservationsSchedule resSchedule, int reservationAllowed,
			int slotTime, EntityManager em, String newDate, String toDate, boolean fromDateMatch, boolean toDateMatch,
			HttpServletRequest httpRequest) throws ParseException {

		if (!fromDateMatch || !toDateMatch) {
			addNewScheduleForShift(u, resSchedule, em, reservationAllowed, slotTime, httpRequest);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateFormat.parse(newDate));
			if (fromDateMatch && !toDateMatch) {
				cal.add(Calendar.DATE, 1);
				String convertedDate = dateFormat.format(cal.getTime());
				u.setFromDate(convertedDate);
			} else {
				if (!fromDateMatch && !toDateMatch) {
					addRservervationSchedule(u, reservationAllowed, em, resSchedule, toDate, slotTime, newDate,
							httpRequest);
				}
				cal.add(Calendar.DATE, -1);
				String convertedDate = dateFormat.format(cal.getTime());
				u.setToDate(convertedDate);
			}
			u.setIsReservationsAllowed(u.getIsReservationsAllowed());
			u.setFromTime(u.getFromTime());
			u.setToTime(u.getToTime());
		} else {
			u.setSlotTime(slotTime);
			u.setReservationAllowed(reservationAllowed);
			u.setIsReservationsAllowed(resSchedule.getIsReservationsAllowed());
			u.setFromTime(resSchedule.getFromTime());
			u.setToTime(resSchedule.getToTime());
			u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			addOrUpdateReservationScheduleXref(em, resSchedule, null);
		}
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

	}

	/**
	 * Adds the new schedule for shift.
	 *
	 * @param u
	 *            the u
	 * @param resSchedule
	 *            the res schedule
	 * @param em
	 *            the em
	 * @param reservationAllowed
	 *            the reservation allowed
	 * @param slotTime
	 *            the slot time
	 */
	private void addNewScheduleForShift(ReservationsSchedule u, ReservationsSchedule resSchedule, EntityManager em,
			int reservationAllowed, int slotTime, HttpServletRequest httpRequest) {
		ReservationsSchedule new_shift = new ReservationsSchedule();
		new_shift.setCreated(u.getCreated());
		new_shift.setCreatedBy(u.getCreatedBy());
		new_shift.setEndWeek(u.getEndWeek());
		new_shift.setFromDate(resSchedule.getFromDate());
		new_shift.setIsReservationsAllowed(resSchedule.getIsReservationsAllowed());
		new_shift.setFromTime(resSchedule.getFromTime());
		new_shift.setToTime(resSchedule.getToTime());
		new_shift.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		new_shift.setUpdatedBy(u.getUpdatedBy());
		new_shift.setLocationId(u.getLocationId());
		new_shift.setShiftName(u.getShiftName());
		new_shift.setStartWeek(u.getStartWeek());
		new_shift.setStatus(u.getStatus());
		new_shift.setToDate(resSchedule.getFromDate());
		new_shift.setShiftGroupId(u.getShiftGroupId());
		new_shift.setReservationAllowed(reservationAllowed);
		new_shift.setSlotTime(slotTime);
		try {
			if (new_shift.getId() == null)
				new_shift.setId(new StoreForwardUtility().generateUUID());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new_shift = em.merge(new_shift);
		addResevationScheduleDay(em, resSchedule, new_shift);
		addOrUpdateReservationScheduleXref(em, resSchedule, new_shift);
	}

	/**
	 * Adds the or update reservation schedule xref.
	 *
	 * @param em
	 *            the em
	 * @param resSchedule
	 *            the res schedule
	 * @param new_shift
	 *            the new shift
	 */
	private void addOrUpdateReservationScheduleXref(EntityManager em, ReservationsSchedule resSchedule,
			ReservationsSchedule new_shift) {
		if (resSchedule.getReservationsScheduleXref() != null) {
			for (ReservationsScheduleXref r : resSchedule.getReservationsScheduleXref()) {
				r.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if (new_shift != null) {
					r.setReservationsScheduleId(new_shift.getId());
					r.setId(0);
				}

				em.merge(r);

			}
		}
	}

	/**
	 * Adds the resevation schedule day.
	 *
	 * @param em
	 *            the em
	 * @param resSchedule
	 *            the res schedule
	 * @param new_shift
	 *            the new shift
	 */
	private void addResevationScheduleDay(EntityManager em, ReservationsSchedule resSchedule,
			ReservationsSchedule new_shift) {
		if (resSchedule.getReservationsScheduleDays() != null) {
			for (ReservationsScheduleDay day : resSchedule.getReservationsScheduleDays()) {
				day.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				day.setId(0);
				day.setReservationsScheduleId(new_shift.getId());

				em.merge(day);

			}
		}
	}

	/**
	 * Adds the rservervation schedule.
	 *
	 * @param u
	 *            the u
	 * @param reservationAllowed
	 *            the reservation allowed
	 * @param em
	 *            the em
	 * @param resSchedule
	 *            the res schedule
	 * @param toDate
	 *            the to date
	 * @param slotTime
	 *            the slot time
	 * @param newDate
	 *            the new date
	 * @throws ParseException
	 *             the parse exception
	 */
	private void addRservervationSchedule(ReservationsSchedule u, int reservationAllowed, EntityManager em,
			ReservationsSchedule resSchedule, String toDate, int slotTime, String newDate,
			HttpServletRequest httpRequest) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateFormat.parse(newDate));
		cal.add(Calendar.DATE, 1);
		String convertedDate2 = dateFormat.format(cal.getTime());
		ReservationsSchedule new_shift2 = new ReservationsSchedule();
		new_shift2.setCreated(u.getCreated());
		new_shift2.setCreatedBy(u.getCreatedBy());
		new_shift2.setEndWeek(u.getEndWeek());
		new_shift2.setFromDate(convertedDate2);
		new_shift2.setFromTime(u.getFromTime());
		new_shift2.setIsReservationsAllowed(u.getIsReservationsAllowed());
		new_shift2.setToTime(u.getToTime());
		new_shift2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		new_shift2.setUpdatedBy(u.getUpdatedBy());
		new_shift2.setLocationId(u.getLocationId());
		new_shift2.setShiftName(u.getShiftName());
		new_shift2.setStartWeek(u.getStartWeek());
		new_shift2.setStatus(u.getStatus());
		new_shift2.setToDate(toDate);
		new_shift2.setShiftGroupId(u.getShiftGroupId());
		new_shift2.setReservationAllowed(reservationAllowed);
		new_shift2.setSlotTime(slotTime);
		try {
			if (new_shift2.getId() == null)
				new_shift2.setId(new StoreForwardUtility().generateUUID());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new_shift2 = em.merge(new_shift2);
		if (resSchedule.getReservationsScheduleDays() != null) {
			for (ReservationsScheduleDay day : u.getReservationsScheduleDays()) {
				ReservationsScheduleDay daySchedule = new ReservationsScheduleDay();
				daySchedule.setCreatedBy(day.getCreatedBy());
				daySchedule.setUpdatedBy(day.getUpdatedBy());
				daySchedule.setDaysId(day.getDaysId());
				daySchedule.setReservationsScheduleId(new_shift2.getId());
				daySchedule.setStatus("A");

				em.merge(daySchedule);

			}
		}

		if (u.getReservationsScheduleXref() != null) {
			for (ReservationsScheduleXref r : u.getReservationsScheduleXref()) {
				ReservationsScheduleXref rReservationsScheduleXref = new ReservationsScheduleXref();
				rReservationsScheduleXref.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				rReservationsScheduleXref.setReservationsScheduleId(new_shift2.getId());
				rReservationsScheduleXref.setId(0);
				rReservationsScheduleXref.setStatus("A");
				rReservationsScheduleXref.setFromTime(r.getFromTime());
				rReservationsScheduleXref.setToTime(r.getToTime());

				em.merge(rReservationsScheduleXref);

			}
		}
	}

	/**
	 * Gets the reservations schedule day by day id.
	 *
	 * @param groupId
	 *            the group id
	 * @param dayId
	 *            the day id
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations schedule day by day id
	 * @throws Exception
	 *             the exception
	 */
	List<ReservationsScheduleDay> getReservationsScheduleDayByDayId(int groupId, int dayId,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {

		String queryString = "SELECT rd FROM ReservationsSchedule r, ReservationsScheduleDay rd"
				+ " where r.id=rd.reservationsScheduleId and rd.daysId = ?" + " and r.shiftGroupId =?";

		TypedQuery<ReservationsScheduleDay> query = em.createQuery(queryString, ReservationsScheduleDay.class)
				.setParameter(1, dayId).setParameter(2, groupId);

		return query.getResultList();
	}

	/**
	 * Delete reservations schedule.
	 *
	 * @param resSchedule
	 *            the res schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations schedule
	 * @throws Exception
	 *             the exception
	 */

	ReservationsSchedule deleteReservationsSchedule(ReservationsSchedule resSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		ReservationsSchedule u = (ReservationsSchedule) new CommonMethods().getObjectById("ReservationsSchedule", em,
				ReservationsSchedule.class, resSchedule.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		/*
		 * Added by Apoorva C Dated Apr 30, 2014 To delete reservation slot of
		 * particular reservation schedule
		 */
		if (u.getId() != null) {
			String queryString = "delete from reservation_slots where reservation_schedule_id= ? ";
			// TODO why not use em.remove?
			// Bcoz we have 10+k records for a single reservation_schedule
			// iteration will take time thats why.
			int rowUpdated = em.createNativeQuery(queryString).setParameter(1, u.getId()).executeUpdate();
		}

		return u;
	}

	/**
	 * Adds the order status.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order status
	 * @throws Exception
	 *             the exception
	 */

	OrderStatus addOrderStatus(OrderStatus rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null)
			rStatus.setId(new StoreForwardUtility().generateUUID());
		rStatus = em.merge(rStatus);

		return rStatus;
	}

	SMSTemplate addSMSTemplate(SMSTemplate rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		rStatus = em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Update order status.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order status
	 * @throws Exception
	 *             the exception
	 */

	OrderStatus updateOrderStatus(OrderStatus rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(rStatus);

		return rStatus;
	}

	SMSTemplate updateSMSTemplate(SMSTemplate rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Delete order status.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order status
	 * @throws Exception
	 *             the exception
	 */
	OrderStatus deleteOrderStatus(OrderStatus rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		OrderStatus o = getOrderStatusById(rStatus.getId(), httpRequest, em);

		o.setStatus("D");

		o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(o);

		return o;

	}

	/**
	 * Adds the items attribute type.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items attribute type
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttributeType addItemsAttributeType(ItemsAttributeType rStatus, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null)
			rStatus.setId(new StoreForwardUtility().generateUUID());

		rStatus = em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Update items attribute type.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items attribute type
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttributeType updateItemsAttributeType(ItemsAttributeType rStatus, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {

		if (rStatus.getGlobalItemAttributeTypeId() == null) {
			ItemsAttributeType local = (ItemsAttributeType) new CommonMethods().getObjectById("ItemsAttributeType", em,
					ItemsAttributeType.class, rStatus.getId());
			if (local != null) {
				rStatus.setGlobalItemAttributeTypeId(local.getGlobalItemAttributeTypeId());
			}
		}

		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null) {
			rStatus.setId(new StoreForwardUtility().generateUUID());
		}
		rStatus = em.merge(rStatus);
		return rStatus;
	}

	/**
	 * Delete items attribute type.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items attribute type
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttributeType deleteItemsAttributeType(ItemsAttributeType rStatus, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		ItemsAttributeType u = (ItemsAttributeType) new CommonMethods().getObjectById("ItemsAttributeType", em,
				ItemsAttributeType.class, rStatus.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		u = em.merge(u);
		List<ItemsToItemsAttributeType> list = getItemsToItemsAttributeType(u.getId(), em);
		if (list != null) {
			for (ItemsToItemsAttributeType itemsToItemsAttributeType : list) {
				itemsToItemsAttributeType.setStatus("D");
				itemsToItemsAttributeType = em.merge(itemsToItemsAttributeType);
			}
		}

		List<ItemsAttributeTypeToItemsAttribute> itemsAttributeTypeToItemsAttributeList = getItemsAttributeTypeToItemsAttribute(
				u.getId(), em);
		if (itemsAttributeTypeToItemsAttributeList != null) {
			for (ItemsAttributeTypeToItemsAttribute o : itemsAttributeTypeToItemsAttributeList) {
				o.setStatus("D");
				o = em.merge(o);
			}
		}

		return u;
	}

	/**
	 * Adds the items attribute.
	 *
	 * @param itemsAttribute
	 *            the items attribute
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items attribute
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttribute addItemsAttribute(ItemsAttribute itemsAttribute, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		itemsAttribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		itemsAttribute.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (itemsAttribute.getId() == null)
			itemsAttribute.setId(new StoreForwardUtility().generateUUID());

		itemsAttribute = em.merge(itemsAttribute);
		return itemsAttribute;
	}

	/**
	 * Update items attribute.
	 *
	 * @param itemAttribute
	 *            the item attribute
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items attribute
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttribute updateItemsAttribute(ItemsAttribute itemAttribute, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		if (itemAttribute.getGlobalId() == null) {
			ItemsAttribute local = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,
					ItemsAttribute.class, itemAttribute.getId());
			if (local != null) {
				itemAttribute.setGlobalId(local.getGlobalId());
				itemAttribute.setSortSequence(local.getSortSequence());
			}
		}

		itemAttribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (itemAttribute.getId() == null) {
			itemAttribute.setId(new StoreForwardUtility().generateUUID());
		}
			if (itemAttribute.getItemsAttributeTypeToItemsAttributes() != null
					&& itemAttribute.getItemsAttributeTypeToItemsAttributes().size() > 0) {
				for (ItemsAttributeTypeToItemsAttribute iat : itemAttribute.getItemsAttributeTypeToItemsAttributes()) {
					iat.setItemsAttributeId(itemAttribute.getId());
					
				}
			}
			if (itemAttribute.getItemsCharToItemsAttributes() != null
					&& itemAttribute.getItemsCharToItemsAttributes().size() > 0) {
				for (ItemsCharToItemsAttribute iac : itemAttribute.getItemsCharToItemsAttributes()) {
					iac.setItemsAttributeId(itemAttribute.getId());
				}
			}
			if (itemAttribute.getNutritionsToItemsAttributes() != null
					&& itemAttribute.getNutritionsToItemsAttributes().size() > 0) {
				for (ItemsAttributeToNutritions inc : itemAttribute.getNutritionsToItemsAttributes()) {
					inc.setItemsAttributeId(itemAttribute.getId());
				}
			}else if (itemAttribute.getNutritionsToItemsAttributes() == null){
				Set<ItemsAttributeToNutritions> attributeToNutritions = new LinkedHashSet<ItemsAttributeToNutritions>();
				itemAttribute.setNutritionsToItemsAttributes(attributeToNutritions);
			}

		logger.severe("iat==========================================================="+itemAttribute.getItemsAttributeTypeToItemsAttributes());
		logger.severe("iac==========================================================="+itemAttribute.getItemsCharToItemsAttributes());
		logger.severe("inc==========================================================="+itemAttribute.getNutritionsToItemsAttributes());
	 logger.severe("itemAttribute==========================================================="+new JSONUtility(httpRequest).convertToJsonString(itemAttribute));
		itemAttribute = em.merge(itemAttribute);
		// todo shlok need to handle exception in below line

		em.getTransaction().commit();
		em.getTransaction().begin();
		return itemAttribute;
	}

	/**
	 * Delete items attribute.
	 *
	 * @param itemAttribute
	 *            the item attribute
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items attribute
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttribute deleteItemsAttribute(ItemsAttribute itemAttribute, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		ItemsAttribute u = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,
				ItemsAttribute.class, itemAttribute.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		List<ItemsToItemsAttribute> attributes = getItemsToItemsAttribute(u.getId(), em);
		// todo shlok need to handle exception in below line
		// handle null here
		for (ItemsToItemsAttribute attribute : attributes) {
			attribute.setStatus("D");
			attribute = em.merge(attribute);
		}

		return u;

	}

	/**
	 * Adds the items char.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */
	ItemsChar addItemsChar(ItemsChar rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null)
			rStatus.setId(new StoreForwardUtility().generateUUID());

		rStatus = em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Update items char.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */
	ItemsChar updateItemsChar(ItemsChar rStatus, HttpServletRequest httpRequest, EntityManager em, String locationId,
			ItemsCharPacket itemsCharPacket) throws Exception {

		if (rStatus.getGlobalItemCharId() == null) {
			ItemsChar local = (ItemsChar) new CommonMethods().getObjectById("ItemsChar", em, ItemsChar.class,
					rStatus.getId());
			if (local != null && itemsCharPacket.getLocalServerURL() == 0) {
				rStatus.setGlobalItemCharId(local.getGlobalItemCharId());
			}
		}
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null)
			rStatus.setId(new StoreForwardUtility().generateUUID());

		rStatus = em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Delete items char.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */

	ItemsChar deleteItemsChar(ItemsChar rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		ItemsChar u = (ItemsChar) new CommonMethods().getObjectById("ItemsChar", em, ItemsChar.class, rStatus.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		u = em.merge(u);

		List<ItemsToItemsChar> list = getItemsToItemsChar(u.getId(), em);
		// todo shlok need to handle exception in below line
		// handle null here
		for (ItemsToItemsChar itemsToItemsChar : list) {
			itemsToItemsChar.setStatus("D");
			itemsToItemsChar = em.merge(itemsToItemsChar);
		}
		return u;
	}

	/**
	 * *** Course ****.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the course
	 * @throws Exception
	 *             the exception
	 */

	Course addCourse(Course rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null) {
			rStatus.setId(new StoreForwardUtility().generateUUID());
		}
		rStatus = em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Update course.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the course
	 * @throws Exception
	 *             the exception
	 */

	Course updateCourse(Course rStatus, HttpServletRequest httpRequest, EntityManager em, CoursePacket coursePacket)
			throws Exception {
		if (rStatus.getGlobalCourseId() == null) {
			Course local = null;
			try {
				String queryString = "select s from Course s where s.id =? ";
				TypedQuery<Course> query = em.createQuery(queryString, Course.class).setParameter(1, rStatus.getId());
				local = query.getSingleResult();
			} catch (NoResultException e) {
				// todo shlok need to handle exception in below line

				logger.severe(e);

			}

			if (local != null && coursePacket.getLocalServerURL() == 0) {
				rStatus.setGlobalCourseId(local.getGlobalCourseId());
				rStatus.setDisplaySequence(local.getDisplaySequence());
			}
		}
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null) {
			rStatus.setId(new StoreForwardUtility().generateUUID());

		}
		rStatus = em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Delete course.
	 *
	 * @param c
	 *            the c
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the course
	 * @throws Exception
	 *             the exception
	 */

	Course deleteCourse(Course c, HttpServletRequest httpRequest, EntityManager em) throws Exception {

		Course u = (Course) new CommonMethods().getObjectById("Course", em, Course.class, c.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		u = em.merge(u);

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
		Root<Item> items = criteria.from(Item.class);
		TypedQuery<Item> query = em
				.createQuery(criteria.select(items).where(builder.equal(items.get(Item_.courseId), u.getId())));
		List<Item> itemList = query.getResultList();
		// todo shlok need to handle exception in below line
		// handle null here
		for (Item item : itemList) {
			item.setCourseId(null);
			item = em.merge(item);
		}
		return u;
	}

	/**
	 * Adds the discounts.
	 *
	 * @param discount
	 *            the discount
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param rList
	 *            the r list
	 * @return the discount
	 * @throws Exception
	 *             the exception
	 */

	Discount addDiscounts(Discount discount, HttpServletRequest httpRequest, EntityManager em,
			List<DiscountToReasons> rList) throws Exception {
		discount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.persist(discount);

		if (rList != null && rList.size() > 0) {
			for (DiscountToReasons dTR : rList) {
				dTR.setDiscountsId(discount.getId());
				dTR.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				em.persist(dTR);
			}

		}

		return discount;
	}

	/**
	 * Update discounts.
	 *
	 * @param d
	 *            the d
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param rList
	 *            the r list
	 * @return the discount
	 * @throws Exception
	 *             the exception
	 */

	Discount updateDiscounts(Discount d, HttpServletRequest httpRequest, EntityManager em,
			List<DiscountToReasons> rList, DiscountPacket discountPacket) throws Exception {

		if (d.getGlobalId() == null) {
			Discount local = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class, d.getId());
			if (local != null && discountPacket.getLocalServerURL() == 0) {
				d.setGlobalId(local.getGlobalId());
			}
		}
		d.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (d.getId() == null) {
			d.setId(new StoreForwardUtility().generateUUID());
		}
		d = em.merge(d);

		if (rList != null && rList.size() > 0) {
			try {
				String queryString1 = "select d from DiscountToReasons d where d.discountsId=? and d.status !='D'";
				TypedQuery<DiscountToReasons> query1 = em.createQuery(queryString1, DiscountToReasons.class)
						.setParameter(1, d.getId());

				List<DiscountToReasons> discountTR = query1.getResultList();

				if (discountTR != null && discountTR.size() > 0) {
					for (DiscountToReasons dTR : discountTR) {
						dTR.setStatus("D");
						dTR = em.merge(dTR);
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe("No Entity found for Discount To Reasons for discount id " + d.getId());
			}

			for (DiscountToReasons dTR : rList) {
				if (dTR.getId() != 0) {
					dTR.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					dTR = em.merge(dTR);
				} else {
					dTR.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					dTR.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					em.persist(dTR);
				}

			}

		}

		return d;
	}

	/**
	 * Delete discounts.
	 *
	 * @param d
	 *            the d
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param rList
	 *            the r list
	 * @return the discount
	 * @throws Exception
	 *             the exception
	 */

	Discount deleteDiscounts(Discount d, HttpServletRequest httpRequest, EntityManager em,
			List<DiscountToReasons> rList) throws Exception {
		Discount u = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class, d.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		u = em.merge(u);

		List<ItemsToDiscount> discounts = getItemsToDiscounts(em, u.getId());
		for (ItemsToDiscount discount : discounts) {
			discount.setStatus("D");
			discount = em.merge(discount);
		}

		if (rList != null && rList.size() > 0) {
			for (DiscountToReasons dTR : rList) {
				DiscountToReasons discountToReasons = em.find(DiscountToReasons.class, dTR.getId());
				discountToReasons.setStatus("D");
				discountToReasons = em.merge(discountToReasons);
			}

		}

		return u;

	}

	/**
	 * Adds the discounts type.
	 *
	 * @param discountsType
	 *            the discounts type
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the discounts type
	 * @throws Exception
	 *             the exception
	 */

	DiscountsType addDiscountsType(DiscountsType discountsType, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		discountsType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (discountsType.getId() == null) {
			discountsType.setId(new StoreForwardUtility().generateUUID());
		}
		discountsType = em.merge(discountsType);
		return discountsType;
	}

	/**
	 * Update discounts type.
	 *
	 * @param d
	 *            the d
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the discounts type
	 * @throws Exception
	 *             the exception
	 */

	DiscountsType updateDiscountsType(DiscountsType d, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		d.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		d = em.merge(d);
		return d;
	}

	/**
	 * Delete discounts type.
	 *
	 * @param d
	 *            the d
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the discounts type
	 * @throws Exception
	 *             the exception
	 */

	DiscountsType deleteDiscountsType(DiscountsType d, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		DiscountsType u = (DiscountsType) new CommonMethods().getObjectById("DiscountsType", em, DiscountsType.class,
				d.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		u = em.merge(u);

		return u;

	}

	/**
	 * Adds the roles.
	 *
	 * @param role
	 *            the role
	 * @param functionsList
	 *            the functions list
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the role
	 * @throws Exception
	 *             the exception
	 */

	Role addRoles(Role role, List<Function> functionsList, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		addUpdateRole(role, functionsList, httpRequest, em);
		return role;
	}

	/**
	 * Adds the update role.
	 *
	 * @param role
	 *            the role
	 * @param functionsList
	 *            the functions list
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @throws Exception
	 *             the exception
	 */
	private void addUpdateRole(Role role, List<Function> functionsList, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		// we make this null, so that this relation does not interfere and
		// throws exception while adding
		if (role != null) {
			role.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			role.setUpdatedBy(role.getUpdatedBy());

			if (role.getId() == null) {
				role.setCreatedBy(role.getCreatedBy());
				role.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if (role.getId() == null)
					role.setId(new StoreForwardUtility().generateUUID());

				em.persist(role);
			} else {
				em.merge(role);
			}
			em.getTransaction().commit();
			em.getTransaction().begin();
			RoleRelationsHelper roleRelationsHelper = new RoleRelationsHelper();
			List<RolesToFunction> rolesToFunctionssListCurrent = roleRelationsHelper.getRoleToFunctions(role.getId(),
					em);

			new EntityRelationshipManager().manageRelations(em, role, rolesToFunctionssListCurrent, functionsList,
					RolesToFunction.class);
		}

	}

	/**
	 * Update role.
	 *
	 * @param role
	 *            the role
	 * @param functionsList
	 *            the functions list
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the role
	 * @throws Exception
	 *             the exception
	 */

	Role updateRole(Role role, List<Function> functionsList, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		addUpdateRole(role, functionsList, httpRequest, em);
		return role;

	}

	/**
	 * Delete role.
	 *
	 * @param role
	 *            the role
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the role
	 * @throws Exception
	 *             the exception
	 */

	Role deleteRole(Role role, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		Role r = (Role) new CommonMethods().getObjectById("Role", em, Role.class, role.getId());
		r.setStatus("D");

		r.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		r = em.merge(r);

		RoleRelationsHelper roleRelationsHelper = new RoleRelationsHelper();
		List<RolesToFunction> rolesToFunctionssListCurrent = roleRelationsHelper.getRoleToFunctions(role.getId(), em);

		new EntityRelationshipManager().manageRelations(em, role, rolesToFunctionssListCurrent, null,
				RolesToFunction.class);

		return role;

	}

	/**
	 * Adds the payment method.
	 *
	 * @param paymentMethod
	 *            the payment method
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the payment method
	 * @throws Exception
	 *             the exception
	 */

	PaymentMethod addPaymentMethod(PaymentMethod paymentMethod, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		paymentMethod.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (paymentMethod.getId() == null || paymentMethod.getId().equals("0"))
			paymentMethod.setId(new StoreForwardUtility().generateUUID());

		em.persist(paymentMethod);

		return paymentMethod;

	}

	/**
	 * Update payment method.
	 *
	 * @param paymentMethod
	 *            the payment method
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the payment method
	 * @throws Exception
	 *             the exception
	 */

	PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		paymentMethod.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethod = em.merge(paymentMethod);

		return paymentMethod;

	}

	/**
	 * Delete payment method.
	 *
	 * @param paymentMethod
	 *            the payment method
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the payment method
	 * @throws Exception
	 *             the exception
	 */

	PaymentMethod deletePaymentMethod(PaymentMethod paymentMethod, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		PaymentMethod u = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em, PaymentMethod.class,
				paymentMethod.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		u = em.merge(u);
		return u;

	}

	/**
	 * Adds the payment method type.
	 *
	 * @param paymentMethodType
	 *            the payment method type
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the payment method type
	 * @throws Exception
	 *             the exception
	 */

	PaymentMethodType addPaymentMethodType(PaymentMethodType paymentMethodType, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		paymentMethodType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (paymentMethodType.getId() == null || paymentMethodType.getId().equals("0"))
			paymentMethodType.setId(new StoreForwardUtility().generateDynamicIntId(em,
					paymentMethodType.getLocationsId(), httpRequest, "payment_method_type"));

		em.persist(paymentMethodType);

		addPaymentMethod(getPaymentMethodFromPaymentMethodType(paymentMethodType), httpRequest, em);
		return paymentMethodType;

	}

	/**
	 * Gets the payment method from payment method type.
	 *
	 * @param paymentMethodType
	 *            the payment method type
	 * @return the payment method from payment method type
	 */
	private PaymentMethod getPaymentMethodFromPaymentMethodType(PaymentMethodType paymentMethodType) {

		PaymentMethod paymentMethod = new PaymentMethod();
		paymentMethod.setPaymentMethodTypeId(paymentMethodType.getId());
		paymentMethod.setName("Other");
		paymentMethod.setDisplayName("Other");
		paymentMethod.setDescription("Other");
		paymentMethod.setLocationsId(paymentMethodType.getLocationsId());
		paymentMethod.setStatus("A");
		paymentMethod.setIsActive(1);
		paymentMethod.setDisplaySequence(1);
		paymentMethod.setCreatedBy(paymentMethodType.getCreatedBy());
		paymentMethod.setCreated(paymentMethodType.getCreated());
		paymentMethod.setUpdatedBy(paymentMethodType.getUpdatedBy());
		paymentMethod.setUpdated(paymentMethodType.getUpdated());

		return paymentMethod;

	}

	/**
	 * Update payment method type.
	 *
	 * @param paymentMethodType
	 *            the payment method type
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the payment method type
	 * @throws Exception
	 *             the exception
	 */

	PaymentMethodType updatePaymentMethodType(PaymentMethodType paymentMethodType, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {

		paymentMethodType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentMethodType = em.merge(paymentMethodType);
		return paymentMethodType;
	}

	/**
	 * Delete payment method type.
	 *
	 * @param paymentMethodType
	 *            the payment method type
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the payment method type
	 * @throws Exception
	 *             the exception
	 */

	PaymentMethodType deletePaymentMethodType(PaymentMethodType paymentMethodType, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		PaymentMethodType u = (PaymentMethodType) new CommonMethods().getObjectById("PaymentMethodType", em,
				PaymentMethodType.class, paymentMethodType.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		u = em.merge(u);
		return paymentMethodType;

	}

	/**
	 * Adds the printer.
	 *
	 * @param printer
	 *            the printer
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the printer
	 * @throws Exception
	 *             the exception
	 */

	Printer addPrinter(Printer printer, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		printer.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (printer.getId() == null)
			printer.setId(new StoreForwardUtility().generateUUID());
		em.persist(printer);
		return printer;

	}

	/**
	 * Update printer.
	 *
	 * @param printer
	 *            the printer
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the printer
	 * @throws Exception
	 *             the exception
	 */

	Printer updatePrinter(Printer printer, HttpServletRequest httpRequest, EntityManager em,
			PrinterPacket printerPacket) throws Exception {
		if (printer.getGlobalPrinterId() == null) {
			Printer local = (Printer) new CommonMethods().getObjectById("Printer", em, Printer.class, printer.getId());
			if (local != null && printerPacket.getLocalServerURL() == 0) {
				printer.setGlobalPrinterId(local.getGlobalPrinterId());
			}
		}
		printer.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (printer.getId() == null)
			printer.setId(new StoreForwardUtility().generateUUID());

		printer = em.merge(printer);
		return printer;

	}

	/**
	 * Delete printer.
	 *
	 * @param printer
	 *            the printer
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the printer
	 * @throws Exception
	 *             the exception
	 */

	Printer deletePrinter(Printer printer, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		Printer p = (Printer) new CommonMethods().getObjectById("Printer", em, Printer.class, printer.getId());
		p.setStatus("D");
		p.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		p = em.merge(p);

		List<ItemsToPrinter> list = getItemsToPrinter(em, printer.getId());
		// todo shlok need to handle exception in below line
		// handle null here
		for (ItemsToPrinter itemsToPrinter : list) {
			itemsToPrinter.setStatus("D");
			itemsToPrinter = em.merge(itemsToPrinter);
		}
		return p;

	}

	/**
	 * Adds the payment gateway to pinpad.
	 *
	 * @param paymentGatewayToPinpad
	 *            the payment gateway to pinpad
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the payment gateway to pinpad
	 * @throws Exception
	 *             the exception
	 */

	PaymentGatewayToPinpad addPaymentGatewayToPinpad(PostPacket packet, PaymentGatewayToPinpad paymentGatewayToPinpad,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {
		paymentGatewayToPinpad.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (paymentGatewayToPinpad.getId() == null || paymentGatewayToPinpad.getId().equals("0"))
			paymentGatewayToPinpad.setId(new StoreForwardUtility().generateUUID());
		em.persist(paymentGatewayToPinpad);
		return paymentGatewayToPinpad;

	}

	/**
	 * Update payment gateway to pinpad.
	 *
	 * @param paymentGatewayToPinpad
	 *            the payment gateway to pinpad
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the payment gateway to pinpad
	 * @throws Exception
	 *             the exception
	 */

	PaymentGatewayToPinpad updatePaymentGatewayToPinpad(PaymentGatewayToPinpad paymentGatewayToPinpad,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {

		paymentGatewayToPinpad.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentGatewayToPinpad = em.merge(paymentGatewayToPinpad);
		return paymentGatewayToPinpad;

	}

	/**
	 * Update payment gateway to pinpad list.
	 *
	 * @param paymentGatewayToPinpad
	 *            the payment gateway to pinpad
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	List<PaymentGatewayToPinpad> updatePaymentGatewayToPinpadList(List<PaymentGatewayToPinpad> paymentGatewayToPinpad,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {
		for (PaymentGatewayToPinpad pinpad : paymentGatewayToPinpad) {
			pinpad.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			pinpad = em.merge(pinpad);
		}

		return paymentGatewayToPinpad;

	}

	/**
	 * Delete payment gateway to pinpad.
	 *
	 * @param paymentGatewayToPinpad
	 *            the payment gateway to pinpad
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the payment gateway to pinpad
	 * @throws Exception
	 *             the exception
	 */

	PaymentGatewayToPinpad deletePaymentGatewayToPinpad(PaymentGatewayToPinpad paymentGatewayToPinpad,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {
		PaymentGatewayToPinpad p = (PaymentGatewayToPinpad) new CommonMethods().getObjectById("PaymentGatewayToPinpad",
				em, PaymentGatewayToPinpad.class, paymentGatewayToPinpad.getId());
		p.setStatus("D");
		p.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		p = em.merge(p);
		return p;

	}

	/**
	 * Adds the order source group.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source group
	 * @throws Exception
	 *             the exception
	 */

	OrderSourceGroup addOrderSourceGroup(OrderSourceGroup rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null || rStatus.getId().equals("0"))
			rStatus.setId(new StoreForwardUtility().generateDynamicIntId(em, rStatus.getLocationsId(), httpRequest,
					"order_source_group"));
		rStatus = em.merge(rStatus);

		return rStatus;

	}

	/**
	 * Update order source group.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source group
	 * @throws Exception
	 *             the exception
	 */

	OrderSourceGroup updateOrderSourceGroup(OrderSourceGroup rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		rStatus = em.merge(rStatus);

		return rStatus;

	}

	/**
	 * Delete order source group.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source group
	 * @throws Exception
	 *             the exception
	 */

	OrderSourceGroup deleteOrderSourceGroup(OrderSourceGroup rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		OrderSourceGroup o = getOrderSourceGroupById(rStatus.getId(), httpRequest, em);

		o.setStatus("D");

		o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		o = em.merge(o);
		return o;

	}

	/**
	 * Adds the order source.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source
	 * @throws Exception
	 *             the exception
	 */

	OrderSource addOrderSource(OrderSource rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null || rStatus.getId().equals("0"))
			rStatus.setId(new StoreForwardUtility().generateUUID());

		rStatus = em.merge(rStatus);

		return rStatus;

	}

	/**
	 * Update order source.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source
	 * @throws Exception
	 *             the exception
	 */

	OrderSource updateOrderSource(OrderSource rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		rStatus = em.merge(rStatus);
		return rStatus;

	}

	/**
	 * Delete order source.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source
	 * @throws Exception
	 *             the exception
	 */

	OrderSource deleteOrderSource(OrderSource rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		OrderSource o = getOrderSourceById(rStatus.getId(), httpRequest, em);

		o.setStatus("D");

		o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		o = em.merge(o);
		return o;

	}

	/**
	 * Adds the cdo mgmt.
	 *
	 * @param cdo
	 *            the cdo
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the cdo mgmt
	 * @throws Exception
	 *             the exception
	 */

	CdoMgmt addCdoMgmt(CdoMgmt cdo, HttpServletRequest httpRequest, EntityManager em) throws Exception {

		em.persist(cdo);
		return cdo;

	}

	/**
	 * Update cdo mgmt.
	 *
	 * @param cdo
	 *            the cdo
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the cdo mgmt
	 * @throws Exception
	 *             the exception
	 */

	CdoMgmt updateCdoMgmt(CdoMgmt cdo, HttpServletRequest httpRequest, EntityManager em) throws Exception {

		cdo.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		cdo = em.merge(cdo);

		return cdo;

	}

	/**
	 * Delete cdo mgmt.
	 *
	 * @param cdo
	 *            the cdo
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the cdo mgmt
	 * @throws Exception
	 *             the exception
	 */

	CdoMgmt deleteCdoMgmt(CdoMgmt cdo, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		em.remove(em.merge(cdo));

		return cdo;

	}

	/**
	 * Adds the printers model.
	 *
	 * @param printersType
	 *            the printers type
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the printers model
	 * @throws Exception
	 *             the exception
	 */

	PrintersModel addPrintersModel(PrintersModel printersType, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		return null;
	}

	/**
	 * Update printers model.
	 *
	 * @param printersType
	 *            the printers type
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the printers model
	 * @throws Exception
	 *             the exception
	 */

	PrintersModel updatePrintersModel(PrintersModel printersType, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		return null;
	}

	/**
	 * Delete printers model.
	 *
	 * @param printersType
	 *            the printers type
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the printers model
	 * @throws Exception
	 *             the exception
	 */

	PrintersModel deletePrintersModel(PrintersModel printersType, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		return null;
	}

	/**
	 * Gets the order status by id.
	 *
	 * @param id
	 *            the id
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order status by id
	 * @throws Exception
	 *             the exception
	 */
	OrderStatus getOrderStatusById(String id, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
		Root<OrderStatus> orderStatus = criteria.from(OrderStatus.class);
		TypedQuery<OrderStatus> query = em
				.createQuery(criteria.select(orderStatus).where(builder.equal(orderStatus.get(OrderStatus_.id), id)));
		return query.getSingleResult();
	}

	/**
	 * Gets the order source group by id.
	 *
	 * @param id
	 *            the id
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source group by id
	 * @throws Exception
	 *             the exception
	 */
	OrderSourceGroup getOrderSourceGroupById(String id, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
		Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
		TypedQuery<OrderSourceGroup> query = em.createQuery(
				criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.id), id)));
		return query.getSingleResult();
	}

	/**
	 * Gets the order source by id.
	 *
	 * @param id
	 *            the id
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source by id
	 * @throws Exception
	 *             the exception
	 */
	OrderSource getOrderSourceById(String id, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
		Root<OrderSource> orderSource = criteria.from(OrderSource.class);
		TypedQuery<OrderSource> query = em
				.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSource_.id), id)));
		return query.getSingleResult();
	}

	/**
	 * Gets the cdo mgmt by cdo name.
	 *
	 * @param cdoName
	 *            the cdo name
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the cdo mgmt by cdo name
	 * @throws Exception
	 *             the exception
	 */
	CdoMgmt getCdoMgmtByCdoName(String cdoName, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<CdoMgmt> criteria = builder.createQuery(CdoMgmt.class);
		Root<CdoMgmt> orderSource = criteria.from(CdoMgmt.class);
		TypedQuery<CdoMgmt> query = em.createQuery(
				criteria.select(orderSource).where(builder.equal(orderSource.get(CdoMgmt_.cdoName), cdoName)));
		return query.getSingleResult();
	}

	/**
	 * Gets the max group id.
	 *
	 * @param em
	 *            the em
	 * @return the max group id
	 */
	int getMaxGroupId(EntityManager em) {
		String queryString = "SELECT max(shift_group_id ) FROM reservations_schedule";
		Query q = em.createNativeQuery(queryString);
		Object obj;
		try {
			obj = q.getSingleResult();
		} catch (NoResultException e) {
			return 0;
		} catch (PersistenceException e) {
			return 0;
		} catch (NullPointerException e) {
			return 0;
		}
		if (obj == null)
			return 0;
		return (Integer) obj;
	}

	/**
	 * Gets the min from date by group id.
	 *
	 * @param groupId
	 *            the group id
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the min from date by group id
	 * @throws Exception
	 *             the exception
	 */
	List<ReservationsSchedule> getMinFromDateByGroupId(int groupId, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		List<ReservationsSchedule> ans = new ArrayList<ReservationsSchedule>();

		String sql = "select min(reservations_schedule0_.from_date) ,reservations_schedule0_.id, reservations_schedule0_.from_time,"
				+ " reservations_schedule0_.to_time,reservations_schedule0_.to_date"
				+ " from reservations_schedule reservations_schedule0_ "
				+ "where reservations_schedule0_.shift_group_id=?";
		Query q = em.createNativeQuery(sql).setParameter(1, groupId);
		@SuppressWarnings("unchecked")
		List<Object[]> l = q.getResultList();
		for (Object[] obj : l) {
			ans.add(new ReservationsSchedule(obj));
		}

		return ans;

	}

	/**
	 * Gets the max to date by group id.
	 *
	 * @param groupId
	 *            the group id
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the max to date by group id
	 * @throws Exception
	 *             the exception
	 */
	List<ReservationsSchedule> getMaxToDateByGroupId(int groupId, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		List<ReservationsSchedule> ans = new ArrayList<ReservationsSchedule>();
		String sql = "select max(reservations_schedule0_.to_date) ,reservations_schedule0_.id, reservations_schedule0_.from_time,"
				+ " reservations_schedule0_.to_time,reservations_schedule0_.to_date"
				+ " from reservations_schedule reservations_schedule0_ where reservations_schedule0_.shift_group_id=?";
		Query q = em.createNativeQuery(sql).setParameter(1, groupId);

		@SuppressWarnings("unchecked")
		List<Object[]> l = q.getResultList();
		for (Object[] obj : l) {
			ans.add(new ReservationsSchedule(obj));
		}

		return ans;
	}

	/**
	 * Update feedback type status.
	 *
	 * @param feedbackType
	 *            the feedback type
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the feedback type
	 * @throws Exception
	 *             the exception
	 */

	FeedbackType updateFeedbackTypeStatus(FeedbackType feedbackType, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		FeedbackType u = em.find(FeedbackType.class, feedbackType.getId());
		u.setStatus(feedbackType.getStatus());
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		u.setUpdatedBy(feedbackType.getUpdatedBy());
		u.setAverageFeedbackNotification(feedbackType.getAverageFeedbackNotification());
		u = em.merge(u);
		return u;

	}

	/**
	 * Gets the business hour.
	 *
	 * @param locationId
	 *            the location id
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the business hour
	 * @throws Exception
	 *             the exception
	 */

	List<BusinessHour> getBusinessHour(String locationId, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<BusinessHour> criteria = builder.createQuery(BusinessHour.class);
		Root<BusinessHour> r = criteria.from(BusinessHour.class);
		TypedQuery<BusinessHour> query = em
				.createQuery(criteria.select(r).where(builder.notEqual(r.get(BusinessHour_.status), "D"),
						builder.equal(r.get(BusinessHour_.locationsId), locationId)));
		return query.getResultList();

	}

	/**
	 * Update reservation status display sequence by id.
	 *
	 * @param reservationsStatus
	 *            the reservations status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	List<ReservationsStatus> updateReservationStatusDisplaySequenceById(List<ReservationsStatus> reservationsStatus,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {
		List<ReservationsStatus> statusList = null;
		if (reservationsStatus != null && reservationsStatus.size() > 0) {
			statusList = new ArrayList<ReservationsStatus>();
		}

		for (ReservationsStatus status : reservationsStatus) {

			ReservationsStatus existingStatus = (ReservationsStatus) new CommonMethods()
					.getObjectById("ReservationsStatus", em, ReservationsStatus.class, status.getId());
			if (existingStatus != null) {
				existingStatus.setDisplaySequence(status.getDisplaySequence());
				existingStatus.setUpdatedBy(status.getUpdatedBy());
				existingStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				if (existingStatus.getId() == null || existingStatus.getId().equals("0")) {
					existingStatus.setId(new StoreForwardUtility().generateUUID());

					em.persist(existingStatus);
				} else {
					existingStatus = em.merge(existingStatus);
				}

				statusList.add(existingStatus);
			}

		}
		return statusList;

	}

	/**
	 * Update order status display sequence by id.
	 *
	 * @param orderStatus
	 *            the order status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	List<OrderStatus> updateOrderStatusDisplaySequenceById(List<OrderStatus> orderStatus,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {
		List<OrderStatus> statusList = null;
		if (orderStatus != null && orderStatus.size() > 0) {
			statusList = new ArrayList<OrderStatus>();
		}

		for (OrderStatus status : orderStatus) {

			OrderStatus existingStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
					OrderStatus.class, status.getId());
			if (existingStatus != null) {
				existingStatus.setDisplaySequence(status.getDisplaySequence());
				existingStatus.setUpdatedBy(status.getUpdatedBy());
				existingStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				if (existingStatus.getId() == null || existingStatus.getId().equals("0")) {
					existingStatus.setId(new StoreForwardUtility().generateUUID());

					em.persist(existingStatus);
				} else {
					em.merge(existingStatus);
				}

				statusList.add(existingStatus);
			}

		}
		return statusList;

	}

	/**
	 * Update inline items attribute.
	 *
	 * @param itemAttribute
	 *            the item attribute
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items attribute
	 * @throws Exception
	 *             the exception
	 */

	ItemsAttribute updateInlineItemsAttribute(ItemsAttribute itemAttribute, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		ItemsAttribute u = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,
				ItemsAttribute.class, itemAttribute.getId());
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		u.setName(itemAttribute.getName());
		u.setDisplayName(itemAttribute.getDisplayName());
		u.setShortName(itemAttribute.getShortName());
		u.setSellingPrice(itemAttribute.getSellingPrice());

		u = em.merge(u);
		return itemAttribute;

	}

	/**
	 * Update items attribute type display sequence by id.
	 *
	 * @param itemsAttributeType
	 *            the items attribute type
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	List<ItemsAttributeType> updateItemsAttributeTypeDisplaySequenceById(List<ItemsAttributeType> itemsAttributeType,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {
		// for display sequence assignment
		List<ItemsAttributeType> attrTypeList = null;
		if (itemsAttributeType != null && itemsAttributeType.size() > 0) {
			attrTypeList = new ArrayList<ItemsAttributeType>();
		}

		for (ItemsAttributeType attrType : itemsAttributeType) {

			ItemsAttributeType existingAttrType = (ItemsAttributeType) new CommonMethods()
					.getObjectById("ItemsAttributeType", em, ItemsAttributeType.class, attrType.getId());
			if (existingAttrType != null) {
				existingAttrType.setSortSequence(attrType.getSortSequence());
				existingAttrType.setUpdatedBy(attrType.getUpdatedBy());
				existingAttrType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				if (existingAttrType.getId() == null || existingAttrType.getId().equals("0")) {
					existingAttrType.setId(new StoreForwardUtility().generateDynamicIntId(em,
							existingAttrType.getLocationsId(), httpRequest, "items_attribute_type"));

					existingAttrType = em.merge(existingAttrType);
				} else {
					em.merge(existingAttrType);
				}
				em.getTransaction().commit();
				attrTypeList.add(existingAttrType);
			}

		}
		return attrTypeList;

	}

	/**
	 * Update items attribute display sequence by id.
	 *
	 * @param itemsAttribute
	 *            the items attribute
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	List<ItemsAttribute> updateItemsAttributeDisplaySequenceById(List<ItemsAttribute> itemsAttribute,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {
		// for display sequence assignment
		List<ItemsAttribute> attrList = null;
		if (itemsAttribute != null && itemsAttribute.size() > 0) {
			attrList = new ArrayList<ItemsAttribute>();
		}

		for (ItemsAttribute attr : itemsAttribute) {

			ItemsAttribute existingAttr = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,
					ItemsAttribute.class, attr.getId());
			if (existingAttr != null) {
				existingAttr.setSortSequence(attr.getSortSequence());
				existingAttr.setUpdatedBy(attr.getUpdatedBy());
				existingAttr.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				if (existingAttr.getId() == null) {
					existingAttr.setId(new StoreForwardUtility().generateUUID());

					existingAttr = em.merge(existingAttr);
				} else {
					em.merge(existingAttr);
				}
				attrList.add(existingAttr);
			}

		}
		return attrList;

	}

	/**
	 * Update items char display sequence by id.
	 *
	 * @param itemsChar
	 *            the items char
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	List<ItemsChar> updateItemsCharDisplaySequenceById(List<ItemsChar> itemsChar, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		List<ItemsChar> charList = null;
		if (itemsChar != null && itemsChar.size() > 0) {
			charList = new ArrayList<ItemsChar>();
		}

		for (ItemsChar attr : itemsChar) {

			ItemsChar existingAttr = (ItemsChar) new CommonMethods().getObjectById("ItemsChar", em, ItemsChar.class,
					attr.getId());
			if (existingAttr != null) {
				existingAttr.setSortSequence(attr.getSortSequence());
				existingAttr.setUpdatedBy(attr.getUpdatedBy());
				existingAttr.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				if (existingAttr.getId() == null || existingAttr.getId().equals("0")) {
					existingAttr.setId(new StoreForwardUtility().generateUUID());

					existingAttr = em.merge(existingAttr);
				} else {
					em.merge(existingAttr);
				}
				charList.add(existingAttr);
			}

		}
		return charList;

	}

	/**
	 * Update course display sequence by id.
	 *
	 * @param listCourse
	 *            the list course
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	List<Course> updateCourseDisplaySequenceById(List<Course> listCourse, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		List<Course> courseList = null;
		if (listCourse != null && listCourse.size() > 0) {
			courseList = new ArrayList<Course>();
		}

		for (Course course : listCourse) {

			Course existingCourse = (Course) new CommonMethods().getObjectById("Course", em, Course.class,
					course.getId());
			if (existingCourse != null) {
				existingCourse.setDisplaySequence(course.getDisplaySequence());
				existingCourse.setUpdatedBy(course.getUpdatedBy());
				existingCourse.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				if (existingCourse.getId() == null || existingCourse.getId().equals("0")) {
					existingCourse.setId(new StoreForwardUtility().generateUUID());
					existingCourse = em.merge(existingCourse);
				} else {
					em.merge(existingCourse);
				}

				courseList.add(existingCourse);
			}

		}
		return courseList;

	}

	/**
	 * Update location to function display sequence by id.
	 *
	 * @param functions
	 *            the functions
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	List<LocationsToFunction> updateLocationToFunctionDisplaySequenceById(List<LocationsToFunction> functions,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {
		List<LocationsToFunction> functionsList = null;
		if (functions != null && functions.size() > 0) {
			functionsList = new ArrayList<LocationsToFunction>();
		}

		for (LocationsToFunction function : functions) {

			LocationsToFunction existingFunction = em.find(LocationsToFunction.class, function.getId());
			if (existingFunction != null) {
				existingFunction.setDisplaySequence(function.getDisplaySequence());
				existingFunction.setUpdatedBy(function.getUpdatedBy());
				existingFunction.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				if (existingFunction.getId() == 0) {

					existingFunction = em.merge(existingFunction);
				} else {
					em.merge(existingFunction);
				}

				functionsList.add(existingFunction);
			}

		}
		return functionsList;

	}

	/**
	 * Update display sequence by generic id.
	 *
	 * @param displaySequenceUpdateList
	 *            the display sequence update list
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	boolean updateDisplaySequenceByGenericId(DisplaySequenceUpdateList displaySequenceUpdateList,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {

		String tableName = displaySequenceUpdateList.getTableName();
		List<DisplaySequenceData> lists = displaySequenceUpdateList.getDisplaySequenceData();
		boolean result = false;

		for (DisplaySequenceData data : lists) {
			String queryString = null;
			if (tableName.equals("category") || tableName.equals("items_attribute_type")
					|| tableName.equals("items_attribute")) {
				queryString = "update " + tableName
						+ " set sort_sequence= ? , updated_by = ?, updated=now() where id= ? ";
			} else {
				queryString = "update " + tableName
						+ " set display_sequence= ? , updated_by = ?, updated=now() where id= ? ";
			}

			logger.severe("queryString======================================================="+queryString);
			int rowUpdated = em.createNativeQuery(queryString).setParameter(1, data.getDisplaySequence())
					.setParameter(2, data.getUpdatedBy()).setParameter(3, data.getId()).executeUpdate();
			if (rowUpdated > 0) {
				result = true;
			}

		}
		return result;

	}

	/**
	 * Sync cdo by cdo name and updated date records.
	 *
	 * @param cdoName
	 *            the cdo name
	 * @param updatedDate
	 *            the updated date
	 * @param sessionId
	 *            the session id
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
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
	String syncCdoByCdoNameAndUpdatedDateRecords(String cdoName, String updatedDate, String sessionId, EntityManager em,
			HttpServletRequest httpRequest, int isLogin, int isLocationSpecific, String locationId) throws Exception {

		// todo shlok need
		// modulise code
		String logicCheckString = "";

		Timestamp updated = Timestamp.valueOf(updatedDate);

		if (cdoName.equals("users")) {
			List<User> userList = new ArrayList<User>();
			String queryString = "select distinct u.id,u.username, u.first_name,u.last_name, u.status,u.email,u.dateofbirth,u.phone,u.last_login_ts,u.auth_pin,u.global_users_id,"
					+ "u.comments,u.created,u.created_by,u.updated,u.updated_by,u.user_color,u.is_allowed_login,u.is_tipped_employee from users u left join users_to_roles utr on u.id=utr.users_id "
					+ " left join roles r on r.id=utr.roles_id where r.role_name != 'POS Customer' and u.status != 'D' ";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).getResultList();
			for (Object[] objRow : resultList) {
				// if this has primary key not 0
				User user = new User();
				user = buildResponseUserObjectResultset(objRow, user);
				userList.add(user);
			}
			return new JSONUtility(httpRequest).convertToJsonString(userList);

		} else if (cdoName.equals("locations")) {
			if (isLogin == 1) {
				logicCheckString = "and l.status != 'D'";
			} else {
				logicCheckString = "";
			}

			String queryString = "select l from Location l where l.updated>=? " + logicCheckString;
			TypedQuery<Location> query = em.createQuery(queryString, Location.class).setParameter(1, updated);
			List<Location> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("category")) {
			if (isLogin == 1) {
				logicCheckString = "and c.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and c.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select c from Category c where c.updated>=?  " + logicCheckString;
			TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, updated);
			List<Category> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("contact_preferences")) {
			if (isLogin == 1) {
				logicCheckString = " and c.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and c.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select c from ContactPreference c where c.updated>=? " + logicCheckString;
			TypedQuery<ContactPreference> query = em.createQuery(queryString, ContactPreference.class).setParameter(1,
					updated);
			List<ContactPreference> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("course")) {
			if (isLogin == 1) {
				logicCheckString = "and c.status != 'D'";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and c.locationsId =  '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select c from Course c where c.updated>=? " + logicCheckString;
			TypedQuery<Course> query = em.createQuery(queryString, Course.class).setParameter(1, updated);
			List<Course> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("discounts")) {
			if (isLogin == 1) {
				logicCheckString = " and d.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and d.locationsId =  '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select d from Discount d where d.updated>=? " + logicCheckString;
			TypedQuery<Discount> query = em.createQuery(queryString, Discount.class).setParameter(1, updated);
			List<Discount> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("discounts_type")) {
			if (isLogin == 1) {
				logicCheckString = " and d.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and d.locationsId =   '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select d from DiscountsType d where d.updated>=? " + logicCheckString;
			TypedQuery<DiscountsType> query = em.createQuery(queryString, DiscountsType.class).setParameter(1, updated);
			List<DiscountsType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items")) {

			if (isLogin == 1) {
				logicCheckString = " and i.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and i.locationsId =  '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select i from Item i where i.updated>=?  " + logicCheckString;
			TypedQuery<Item> query = em.createQuery(queryString, Item.class).setParameter(1, updated);
			List<Item> resultSet = query.getResultList();
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(resultSet);

		} else if (cdoName.equals("items_attribute")) {
			if (isLogin == 1) {
				logicCheckString = " and i.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and i.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select i from ItemsAttribute i where i.updated>=? " + logicCheckString;
			TypedQuery<ItemsAttribute> query = em.createQuery(queryString, ItemsAttribute.class).setParameter(1,
					updated);
			List<ItemsAttribute> resultSet = query.getResultList();
			// return new
			// JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(resultSet);
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(resultSet);

		} else if (cdoName.equals("items_attribute_type")) {

			if (isLogin == 1) {
				logicCheckString = " and i.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and i.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select i from ItemsAttributeType i where i.updated>=?  " + logicCheckString;
			TypedQuery<ItemsAttributeType> query = em.createQuery(queryString, ItemsAttributeType.class).setParameter(1,
					updated);
			List<ItemsAttributeType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_char")) {
			if (isLogin == 1) {
				logicCheckString = "and i.status != 'D'";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and i.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select i from ItemsChar i where i.updated>=?  " + logicCheckString;
			TypedQuery<ItemsChar> query = em.createQuery(queryString, ItemsChar.class).setParameter(1, updated);
			List<ItemsChar> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("order_detail_status")) {
			if (isLogin == 1) {
				logicCheckString = " and o.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and o.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select o from OrderDetailStatus o where o.updated>=?  " + logicCheckString;
			TypedQuery<OrderDetailStatus> query = em.createQuery(queryString, OrderDetailStatus.class).setParameter(1,
					updated);
			List<OrderDetailStatus> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("order_source")) {
			if (isLogin == 1) {
				logicCheckString = " and o.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and o.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select o from OrderSource o where o.updated>=?" + logicCheckString;
			TypedQuery<OrderSource> query = em.createQuery(queryString, OrderSource.class).setParameter(1, updated);
			List<OrderSource> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("order_source_group")) {
			if (isLogin == 1) {
				logicCheckString = " and o.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and o.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select o from OrderSourceGroup o where o.updated>=? " + logicCheckString;
			TypedQuery<OrderSourceGroup> query = em.createQuery(queryString, OrderSourceGroup.class).setParameter(1,
					updated);
			List<OrderSourceGroup> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("order_status")) {
			if (isLogin == 1) {
				logicCheckString = "  and o.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and o.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select o from OrderStatus o where o.updated>=? " + logicCheckString;
			TypedQuery<OrderStatus> query = em.createQuery(queryString, OrderStatus.class).setParameter(1, updated);
			List<OrderStatus> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("payment_method")) {
			if (isLogin == 1) {
				logicCheckString = "  and p.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from PaymentMethod p where p.updated>=?  " + logicCheckString;
			TypedQuery<PaymentMethod> query = em.createQuery(queryString, PaymentMethod.class).setParameter(1, updated);
			List<PaymentMethod> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("payment_method_type")) {
			if (isLogin == 1) {
				logicCheckString = " and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from PaymentMethodType p where p.updated>=? " + logicCheckString;
			TypedQuery<PaymentMethodType> query = em.createQuery(queryString, PaymentMethodType.class).setParameter(1,
					updated);
			List<PaymentMethodType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("payment_transaction_type")) {
			if (isLogin == 1) {
				logicCheckString = "  and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from PaymentTransactionType p where p.updated>=?  " + logicCheckString;
			TypedQuery<PaymentTransactionType> query = em.createQuery(queryString, PaymentTransactionType.class)
					.setParameter(1, updated);
			List<PaymentTransactionType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("printers")) {
			if (isLogin == 1) {
				logicCheckString = "  and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from Printer p where p.updated>=?  " + logicCheckString;
			TypedQuery<Printer> query = em.createQuery(queryString, Printer.class).setParameter(1, updated);
			List<Printer> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("printers_type")) {
			if (isLogin == 1) {
				logicCheckString = " and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from PrintersType p where p.updated>=?  " + logicCheckString;
			TypedQuery<PrintersType> query = em.createQuery(queryString, PrintersType.class).setParameter(1, updated);
			List<PrintersType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("printers_interface")) {
			if (isLogin == 1) {
				logicCheckString = "  and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from PrintersInterface p where p.updated>=?  " + logicCheckString;
			TypedQuery<PrintersInterface> query = em.createQuery(queryString, PrintersInterface.class).setParameter(1,
					updated);
			List<PrintersInterface> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("printers_model")) {
			if (isLogin == 1) {
				logicCheckString = " and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from PrintersModel p where p.updated>=?  " + logicCheckString;
			TypedQuery<PrintersModel> query = em.createQuery(queryString, PrintersModel.class).setParameter(1, updated);
			List<PrintersModel> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("request_type")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and r.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select r from RequestType r where r.updated>=?  " + logicCheckString;
			TypedQuery<RequestType> query = em.createQuery(queryString, RequestType.class).setParameter(1, updated);
			List<RequestType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("reservations_status")) {
			if (isLogin == 1) {
				logicCheckString = "and r.status != 'D'";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and r.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select r from ReservationsStatus r where r.updated>=?  " + logicCheckString;
			TypedQuery<ReservationsStatus> query = em.createQuery(queryString, ReservationsStatus.class).setParameter(1,
					updated);
			List<ReservationsStatus> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("reservations_types")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and r.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select r from ReservationsType r where r.updated>=? " + logicCheckString;
			TypedQuery<ReservationsType> query = em.createQuery(queryString, ReservationsType.class).setParameter(1,
					updated);
			List<ReservationsType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("roles")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and r.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select r from Role r where r.updated>=?  " + logicCheckString;
			TypedQuery<Role> query = em.createQuery(queryString, Role.class).setParameter(1, updated);
			List<Role> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("timezone")) {
			String queryString = "select r from Timezone r where r.updated>=?";
			TypedQuery<Timezone> query = em.createQuery(queryString, Timezone.class).setParameter(1, updated);
			List<Timezone> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("transactional_currency")) {
			String queryString = "select r from TransactionalCurrency r where r.updated>=?";
			TypedQuery<TransactionalCurrency> query = em.createQuery(queryString, TransactionalCurrency.class)
					.setParameter(1, updated);
			List<TransactionalCurrency> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("address")) {
			String queryString = " SELECT a.* FROM `address` a join locations l on a.id=l.address_id where l.locations_id='0' and l.locations_id is null and  a.updated>=?";
			Query query = em.createNativeQuery(queryString, Address.class).setParameter(1, updated);
			@SuppressWarnings("unchecked")
			List<Address> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("users_to_locations")) {
			String queryString = "select distinct utl.* from users_to_locations utl "
					+ "  join users_to_roles utr on utr.users_id=utl.users_id  "
					+ " join  roles r on r.id=utr.roles_id where r.role_name != 'POS Customer' and utl.updated>= ? and utl.status != 'D' ";
			Query query = em.createNativeQuery(queryString, UsersToLocation.class).setParameter(1, updated);
			@SuppressWarnings("unchecked")
			List<UsersToLocation> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_attribute_type_to_items_attribute")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  r.itemsAttributeId in (select i.id from ItemsAttribute i where i.locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString += "";
			}

			String queryString = "select r from ItemsAttributeTypeToItemsAttribute r where r.updated>=? "
					+ logicCheckString;
			TypedQuery<ItemsAttributeTypeToItemsAttribute> query = em
					.createQuery(queryString, ItemsAttributeTypeToItemsAttribute.class).setParameter(1, updated);
			List<ItemsAttributeTypeToItemsAttribute> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("category_items")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  r.itemsId in (select i.id from Item i where i.locationsId='" + locationId+"'"
						+ ")";
			} else {
				logicCheckString += "";
			}

			String queryString = "select r from CategoryItem r where r.updated>=?  " + logicCheckString;
			TypedQuery<CategoryItem> query = em.createQuery(queryString, CategoryItem.class).setParameter(1, updated)
					.setParameter(1, updated);
			List<CategoryItem> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_char_to_items_attribute")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  r.itemsAttributeId in (select i.id from ItemsAttribute i where i.locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString += "";
			}

			String queryString = "select r from ItemsCharToItemsAttribute r where r.updated>=? " + logicCheckString;
			TypedQuery<ItemsCharToItemsAttribute> query = em.createQuery(queryString, ItemsCharToItemsAttribute.class)
					.setParameter(1, updated);
			List<ItemsCharToItemsAttribute> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_to_items_attribute_type")) {
			if (isLogin == 1) {
				logicCheckString = "and r.status != 'D'";
			} else {
				logicCheckString = "";
			}

			if (isLocationSpecific == 1) {
				logicCheckString += " and  r.itemsId in (select i.id from Item i where i.locationsId='" + locationId+"'"
						+ ")";
			} else {
				logicCheckString += "";
			}

			String queryString = "select r from ItemsToItemsAttributeType r where r.updated>=?  " + logicCheckString;
			TypedQuery<ItemsToItemsAttributeType> query = em.createQuery(queryString, ItemsToItemsAttributeType.class)
					.setParameter(1, updated);
			List<ItemsToItemsAttributeType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_to_items_char")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  r.itemsId in (select i.id from Item i where i.locationsId='" + locationId+"'"
						+ ")";
			} else {
				logicCheckString += "";
			}

			String queryString = "select r from ItemsToItemsChar r where r.updated>=?  " + logicCheckString;
			TypedQuery<ItemsToItemsChar> query = em.createQuery(queryString, ItemsToItemsChar.class).setParameter(1,
					updated);
			List<ItemsToItemsChar> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("users_to_roles")) {
			if (isLogin == 1) {
				logicCheckString = " and utr.status != 'D' ";
			} else {
				logicCheckString = "  ";
			}
			String queryString = "select utr.*  from  users_to_roles utr "
					+ " left join roles r on r.id=utr.roles_id where r.role_name != 'POS Customer' and utr.updated>= ?  "
					+ logicCheckString;
			Query query = em.createNativeQuery(queryString, UsersToRole.class).setParameter(1, updated);
			@SuppressWarnings("unchecked")
			List<UsersToRole> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("reservations_schedule")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and r.locationId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select r from ReservationsSchedule r where r.updated>=?  " + logicCheckString;
			TypedQuery<ReservationsSchedule> query = em.createQuery(queryString, ReservationsSchedule.class)
					.setParameter(1, updated);
			List<ReservationsSchedule> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("reservations_schedule_xref")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D'";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and r.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select r from ReservationsScheduleXref r where r.updated>=?  " + logicCheckString;
			TypedQuery<ReservationsScheduleXref> query = em.createQuery(queryString, ReservationsScheduleXref.class)
					.setParameter(1, updated);
			List<ReservationsScheduleXref> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("days")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			String queryString = "select r from Day r where r.updated>=?";
			TypedQuery<Day> query = em.createQuery(queryString, Day.class).setParameter(1, updated);
			List<Day> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("reservations_schedule_days")) {
			if (isLogin == 1) {
				logicCheckString = " and r.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			String queryString = "select r from ReservationsScheduleDay r where r.updated>=?  " + logicCheckString;
			TypedQuery<ReservationsScheduleDay> query = em.createQuery(queryString, ReservationsScheduleDay.class)
					.setParameter(1, updated);
			List<ReservationsScheduleDay> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("payment_ways")) {
			if (isLogin == 1) {
				logicCheckString = " and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from PaymentWay p where p.updated>=?  " + logicCheckString;
			TypedQuery<PaymentWay> query = em.createQuery(queryString, PaymentWay.class).setParameter(1, updated);
			List<PaymentWay> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("category_to_printers")) {
			if (isLogin == 1) {
				logicCheckString = " and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}

			if (isLocationSpecific == 1) {
				logicCheckString += " and  p.categoryId in (select c.id from Category c where c.locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from CategoryToPrinter p where p.updated>=? and p.status != 'D' "
					+ logicCheckString;
			TypedQuery<CategoryToPrinter> query = em.createQuery(queryString, CategoryToPrinter.class).setParameter(1,
					updated);
			List<CategoryToPrinter> resultSet = query.getResultList();
			return convertToJson(resultSet);
		}

		else if (cdoName.equals("items_to_printers")) {
			if (isLogin == 1) {
				logicCheckString = "and p.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  p.itemsId in (select i.id from Item i where i.locationsId='" + locationId+"'"
						+ ")";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from ItemsToPrinter p where p.updated>=?  " + logicCheckString;
			TypedQuery<ItemsToPrinter> query = em.createQuery(queryString, ItemsToPrinter.class).setParameter(1,
					updated);
			List<ItemsToPrinter> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("feedback_type")) {
			if (isLogin == 1) {
				logicCheckString = " and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from FeedbackType p where p.updated>=? " + logicCheckString;
			TypedQuery<FeedbackType> query = em.createQuery(queryString, FeedbackType.class).setParameter(1, updated);
			List<FeedbackType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("feedback_question")) {
			if (isLogin == 1) {
				logicCheckString = " and p.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from FeedbackQuestion p where p.updated>=? " + logicCheckString;
			TypedQuery<FeedbackQuestion> query = em.createQuery(queryString, FeedbackQuestion.class).setParameter(1,
					updated);
			List<FeedbackQuestion> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("smileys")) {

			String queryString = "select p from Smiley p where p.updated>=?";
			TypedQuery<Smiley> query = em.createQuery(queryString, Smiley.class).setParameter(1, updated);
			List<Smiley> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("feedback_field")) {
			if (isLogin == 1) {
				logicCheckString = " and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from FeedbackField p where p.updated>=?  " + logicCheckString;
			TypedQuery<FeedbackField> query = em.createQuery(queryString, FeedbackField.class).setParameter(1, updated);
			List<FeedbackField> resultSet = query.getResultList();
			return convertToJson(resultSet);

		} else if (cdoName.equals("field_type")) {
			String queryString = "select p from FieldType p where p.updated>=?  " + " ";
			TypedQuery<FieldType> query = em.createQuery(queryString, FieldType.class).setParameter(1, updated);
			List<FieldType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("transaction_status")) {
			if (isLogin == 1) {
				logicCheckString = " and t.status != 'D' ";
			} else {
				logicCheckString = "";
			}

			String queryString = "select t from TransactionStatus t where t.updated>=?  " + logicCheckString;
			TypedQuery<TransactionStatus> query = em.createQuery(queryString, TransactionStatus.class).setParameter(1,
					updated);
			List<TransactionStatus> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("paymentgateway_type")) {

			if (isLogin == 1) {
				logicCheckString = " and p.status != 'D' ";
			} else {
				logicCheckString = "";
			}

			if (isLocationSpecific == 1) {
				logicCheckString += "and p.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select p from PaymentGatewayType p where p.updated>=? " + logicCheckString;
			TypedQuery<PaymentGatewayType> query = em.createQuery(queryString, PaymentGatewayType.class).setParameter(1,
					updated);
			List<PaymentGatewayType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("roles_to_functions")) {
			if (isLogin == 1) {
				logicCheckString = " and rtf.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			String queryString = "select rtf from RolesToFunction rtf where rtf.updated>=?  " + logicCheckString;
			TypedQuery<RolesToFunction> query = em.createQuery(queryString, RolesToFunction.class).setParameter(1,
					updated);
			List<RolesToFunction> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("paymentgateway")) {
			if (isLogin == 1) {
				logicCheckString = " and pgt.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and pgt.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select pgt from Paymentgateway pgt where pgt.updated>= ? " + logicCheckString;
			TypedQuery<Paymentgateway> query = em.createQuery(queryString, Paymentgateway.class).setParameter(1,
					updated);
			List<Paymentgateway> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_to_items_attributes")) {
			if (isLogin == 1) {
				logicCheckString = "  and itia.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  itia.itemsId in (select i.id from Item i where i.locationsId='" + locationId+"'"
						+ ")";
			} else {
				logicCheckString += "";
			}
			String queryString = "select itia from ItemsToItemsAttribute itia where itia.updated>=?  "
					+ logicCheckString;
			TypedQuery<ItemsToItemsAttribute> query = em.createQuery(queryString, ItemsToItemsAttribute.class)
					.setParameter(1, updated);
			List<ItemsToItemsAttribute> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("business_hours")) {
			if (isLogin == 1) {
				logicCheckString = "  and b.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and b.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select b from BusinessHour b where b.updated>=? " + logicCheckString;
			TypedQuery<BusinessHour> query = em.createQuery(queryString, BusinessHour.class).setParameter(1, updated);
			List<BusinessHour> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("business_type")) {
			if (isLogin == 1) {
				logicCheckString = "";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and b.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select b from BusinessType b where b.updated>=?";
			TypedQuery<BusinessType> query = em.createQuery(queryString, BusinessType.class).setParameter(1, updated);
			List<BusinessType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("reasons")) {
			if (isLogin == 1) {
				logicCheckString = "  and r.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and r.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select r from Reasons r where r.updated>=? " + logicCheckString;
			TypedQuery<Reasons> query = em.createQuery(queryString, Reasons.class).setParameter(1, updated);
			List<Reasons> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("reasons_type")) {
			if (isLogin == 1) {
				logicCheckString = "  and r.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and r.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select r from ReasonType r where r.updated>=?   " + logicCheckString;
			TypedQuery<ReasonType> query = em.createQuery(queryString, ReasonType.class).setParameter(1, updated);
			List<ReasonType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("sales_tax")) {
			if (isLogin == 1) {
				logicCheckString = "  and s.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and s.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select s from SalesTax s where s.updated>=?  " + logicCheckString;
			TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class).setParameter(1, updated);
			List<SalesTax> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("applications")) {
			if (isLogin == 1) {
				logicCheckString = "  and a.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			String queryString = "select a from Application a where a.updated>=?  " + logicCheckString;
			TypedQuery<Application> query = em.createQuery(queryString, Application.class).setParameter(1, updated);
			List<Application> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("functions")) {
			if (isLogin == 1) {
				logicCheckString = " and fun.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			String queryString = "select fun from Functions fun where fun.updated>=?   " + logicCheckString;
			TypedQuery<Functions> query = em.createQuery(queryString, Functions.class).setParameter(1, updated);
			List<Functions> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("application_to_functions")) {
			if (isLogin == 1) {
				logicCheckString = "   and atf.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			String queryString = "select atf from ApplicationToFunction atf where atf.updated>=?   " + logicCheckString;
			TypedQuery<ApplicationToFunction> query = em.createQuery(queryString, ApplicationToFunction.class)
					.setParameter(1, updated);
			List<ApplicationToFunction> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("location_to_application")) {
			if (isLogin == 1) {
				logicCheckString = "";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and lta.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select lta from LocationToApplication lta where lta.updated>=?  ";
			TypedQuery<LocationToApplication> query = em.createQuery(queryString, LocationToApplication.class)
					.setParameter(1, updated);
			List<LocationToApplication> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("location_to_function")) {
			if (isLogin == 1) {
				logicCheckString = "";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and lta.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select lta from LocationsToFunction lta where lta.updated>=?   and lta.status != 'D' "
					+ logicCheckString;
			TypedQuery<LocationsToFunction> query = em.createQuery(queryString, LocationsToFunction.class)
					.setParameter(1, updated);
			List<LocationsToFunction> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("location_to_printer_receipt")) {
			return ("[]");
		} else if (cdoName.equals("printer_receipt")) {
			if (isLogin == 1) {
				logicCheckString = " and pr.status !='D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and pr.locationId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select pr from PrinterReceipt pr where pr.updated>=? " + logicCheckString;
			TypedQuery<PrinterReceipt> query = em.createQuery(queryString, PrinterReceipt.class).setParameter(1,
					updated);
			List<PrinterReceipt> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_to_discounts")) {
			if (isLogin == 1) {
				logicCheckString = "  and itd.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  itd.itemsId in (select i.id from Item i where i.locationsId='" + locationId+"'"
						+ ")";
			} else {
				logicCheckString += "";
			}
			String queryString = "select itd from ItemsToDiscount itd where itd.updated>=?  " + logicCheckString;
			TypedQuery<ItemsToDiscount> query = em.createQuery(queryString, ItemsToDiscount.class).setParameter(1,
					updated);
			List<ItemsToDiscount> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("category_to_discounts")) {
			if (isLogin == 1) {
				logicCheckString = "  and ctd.status != 'D'   ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  ctd.categoryId in (select c.id from Category c where c.locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString += "";
			}
			String queryString = "select ctd from CategoryToDiscount ctd where ctd.updated>=?  " + logicCheckString;
			TypedQuery<CategoryToDiscount> query = em.createQuery(queryString, CategoryToDiscount.class).setParameter(1,
					updated);
			List<CategoryToDiscount> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_type")) {
			if (isLogin == 1) {
				logicCheckString = "  and ctd.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			String queryString = "select ctd from ItemsType ctd where ctd.updated>=?  " + logicCheckString;
			TypedQuery<ItemsType> query = em.createQuery(queryString, ItemsType.class).setParameter(1, updated);
			List<ItemsType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("inventory")) {
			if (isLogin == 1) {
				logicCheckString = "  and ctd.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and ctd.locationId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select ctd from Inventory ctd where ctd.updated>=?  " + logicCheckString;
			TypedQuery<Inventory> query = em.createQuery(queryString, Inventory.class).setParameter(1, updated);
			List<Inventory> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("employee_operations")) {
			if (isLogin == 1) {
				logicCheckString = "  and eo.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and eo.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select eo from EmployeeOperation eo where eo.updated>=?  " + logicCheckString;
			TypedQuery<EmployeeOperation> query = em.createQuery(queryString, EmployeeOperation.class).setParameter(1,
					updated);
			List<EmployeeOperation> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("unit_of_measurement")) {
			if (isLogin == 1) {
				logicCheckString = "  and uom.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and uom.locationId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select uom from UnitOfMeasurement uom where uom.updated>=?  " + logicCheckString;
			TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class).setParameter(1,
					updated);
			List<UnitOfMeasurement> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("unit_of_measurement_type")) {
			if (isLogin == 1) {
				logicCheckString = " and uomt.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			String queryString = "select uomt from UnitOfMeasurementType uomt where uomt.updated>=?    "
					+ logicCheckString;
			TypedQuery<UnitOfMeasurementType> query = em.createQuery(queryString, UnitOfMeasurementType.class)
					.setParameter(1, updated);
			List<UnitOfMeasurementType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("roles_to_functions")) {
			if (isLogin == 1) {
				logicCheckString = "  and rtf.status != 'D'  ";
			} else {
				logicCheckString = "";
			}
			String queryString = "select rtf from RolesToFunction rtf where rtf.updated>=?  " + logicCheckString;
			TypedQuery<RolesToFunction> query = em.createQuery(queryString, RolesToFunction.class).setParameter(1,
					updated);
			List<RolesToFunction> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("countries")) {
			if (isLogin == 1) {
				logicCheckString = "";
			} else {
				logicCheckString = "";
			}
			String queryString = "select c from Countries c";
			TypedQuery<Countries> query = em.createQuery(queryString, Countries.class);
			List<Countries> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("payment_type")) {
			if (isLogin == 1) {
				logicCheckString = "";
			} else {
				logicCheckString = "";
			}
			String queryString = "select pt from PaymentType pt where pt.updated>=?  ";
			TypedQuery<PaymentType> query = em.createQuery(queryString, PaymentType.class).setParameter(1, updated);
			List<PaymentType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("order_source_group_to_paymentgateway_type")) {
			if (isLogin == 1) {
				logicCheckString = "";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  os.orderSourceGroupId in (select osg.id from OrderSourceGroup osg where osg.locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString += "";
			}
			String queryString = "select os from OrderSourceGroupToPaymentgatewayType os where os.updated>=?";
			TypedQuery<OrderSourceGroupToPaymentgatewayType> query = em
					.createQuery(queryString, OrderSourceGroupToPaymentgatewayType.class).setParameter(1, updated);
			List<OrderSourceGroupToPaymentgatewayType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("order_source_to_paymentgateway_type")) {
			if (isLogin == 1) {
				logicCheckString = "";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  orderSourceToPaymentGatewayType.orderSourceId in (select os.id from OrderSource os where os.locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString += "";
			}
			String queryString = "select orderSourceToPaymentGatewayType "
					+ " from OrderSourceToPaymentgatewayType orderSourceToPaymentGatewayType "
					+ " where orderSourceToPaymentGatewayType.updated>=?";
			TypedQuery<OrderSourceToPaymentgatewayType> query = em
					.createQuery(queryString, OrderSourceToPaymentgatewayType.class).setParameter(1, updated);
			List<OrderSourceToPaymentgatewayType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		}

		else if (cdoName.equals("inventory_item_bom")) {
			if (isLogin == 1) {
				logicCheckString = " and inventoryItemBom.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  inventoryItemBom.itemIdFg in (select i.id from Item i where i.locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString += "";
			}
			String queryString = "select inventoryItemBom from InventoryItemBom inventoryItemBom "
					+ " where inventoryItemBom.updated >= ?  " + logicCheckString;
			TypedQuery<InventoryItemBom> query = em.createQuery(queryString, InventoryItemBom.class).setParameter(1,
					updated);
			List<InventoryItemBom> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("inventory_attribute_bom")) {
			if (isLogin == 1) {
				logicCheckString = " and inventoryAttributeBOM.status !='D'  ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  inventoryAttributeBOM.attributeIdFg in (select ia.id from ItemsAttribute ia where ia.locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString += "";
			}
			String queryString = "select inventoryAttributeBOM from InventoryAttributeBOM inventoryAttributeBOM "
					+ " where inventoryAttributeBOM.updated >= ? " + logicCheckString;
			TypedQuery<InventoryAttributeBOM> query = em.createQuery(queryString, InventoryAttributeBOM.class)
					.setParameter(1, updated);
			List<InventoryAttributeBOM> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("inventory_item_default")) {
			if (isLogin == 1) {
				logicCheckString = "and inventoryItemDefault.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += " and  inventoryItemDefault.itemId in (select i.id from Item i where i.locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString += "";
			}
			String queryString = "select inventoryItemDefault from InventoryItemDefault inventoryItemDefault"
					+ " where inventoryItemDefault.updated >= ?  " + logicCheckString;
			TypedQuery<InventoryItemDefault> query = em.createQuery(queryString, InventoryItemDefault.class)
					.setParameter(1, updated);
			List<InventoryItemDefault> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("batch_detail")) {
			if (isLogin == 1) {
				logicCheckString = "and b.status != 'C' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and b.locationId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			// because table does not have updated column
			String queryString = "select b from BatchDetail b" + " where (b.startTime >= ? or b.closeTime >= ? )  "
					+ logicCheckString;
			// b.closeTime should be there
			TypedQuery<BatchDetail> query = em.createQuery(queryString, BatchDetail.class).setParameter(1, updated)
					.setParameter(2, updated);
			List<BatchDetail> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("device_to_register")) {
			// getting usersession to get deviceID
			UserSession userSession = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId);
			String deviceId = null;
			if (userSession != null) {
				DeviceInfo deviceInfo = userSession.getDeviceInfo();
				deviceId = deviceInfo.getDeviceId();
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and d.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			// to manage relationship object for update and delete we are
			// not checking D status // By Apoorva Aug 20, 2015 #28891
			String queryString = "select d from DeviceToRegister d where d.updated >= ? and d.deviceId =? ";
			TypedQuery<DeviceToRegister> query = em.createQuery(queryString, DeviceToRegister.class)
					.setParameter(1, updated).setParameter(2, deviceId);
			List<DeviceToRegister> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("order_source_to_sales_tax")) {
			if (isLogin == 1) {
				logicCheckString = "  and s.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and s.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select s from OrderSourceToSalesTax s where s.updated>=?  " + logicCheckString;
			TypedQuery<OrderSourceToSalesTax> query = em.createQuery(queryString, OrderSourceToSalesTax.class)
					.setParameter(1, updated);
			List<OrderSourceToSalesTax> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("order_source_group_to_sales_tax")) {
			if (isLogin == 1) {
				logicCheckString = "  and s.status != 'D' ";
			} else {
				logicCheckString = "";
			}

			if (isLocationSpecific == 1) {
				logicCheckString += "and s.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select s from OrderSourceGroupToSalesTax s where s.updated>=?  " + logicCheckString;
			TypedQuery<OrderSourceGroupToSalesTax> query = em.createQuery(queryString, OrderSourceGroupToSalesTax.class)
					.setParameter(1, updated);
			List<OrderSourceGroupToSalesTax> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("location_to_supplier")) {
			if (isLogin == 1) {
				logicCheckString = "  and s.status != 'D' ";
			} else {
				logicCheckString = "";
			}
			if (isLocationSpecific == 1) {
				logicCheckString += "and s.locationsId = '" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select s from LocationsToSupplier s where s.updated>=?  " + logicCheckString;
			TypedQuery<LocationsToSupplier> query = em.createQuery(queryString, LocationsToSupplier.class)
					.setParameter(1, updated);
			List<LocationsToSupplier> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("discount_ways")) {
			if (isLogin == 1) {
				logicCheckString = "";
			} else {
				logicCheckString = "";
			}
			String queryString = "select dw from DiscountWays dw where dw.updated>=?  " + logicCheckString;
			TypedQuery<DiscountWays> query = em.createQuery(queryString, DiscountWays.class).setParameter(1, updated);
			List<DiscountWays> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("device_to_pin_pad")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and dw.locationsId = '" + locationId+"'";
			} else {
				logicCheckString = "";
			}
			String queryString = "select dw from DeviceToPinPad dw where dw.updated>=?  " + logicCheckString;
			TypedQuery<DeviceToPinPad> query = em.createQuery(queryString, DeviceToPinPad.class).setParameter(1,
					updated);
			List<DeviceToPinPad> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("payment_gateway_to_pin_pad")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and dw.locationsId = '" + locationId+"'";
			} else {
				logicCheckString = "";
			}
			String queryString = "select dw from PaymentGatewayToPinpad dw where dw.updated>=?  " + logicCheckString;
			TypedQuery<PaymentGatewayToPinpad> query = em.createQuery(queryString, PaymentGatewayToPinpad.class)
					.setParameter(1, updated);
			List<PaymentGatewayToPinpad> resultSet = query.getResultList();
			return convertToJson(resultSet);
		}

		else if (cdoName.equals("items_to_supplier")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and  dw.itemId in (select i.id from Item i where i.locationsId='" + locationId+"'"
						+ ")";
			} else {
				logicCheckString = "";
			}
			String queryString = "select dw from ItemToSupplier dw where dw.updated>=?  " + logicCheckString;
			TypedQuery<ItemToSupplier> query = em.createQuery(queryString, ItemToSupplier.class).setParameter(1,
					updated);
			List<ItemToSupplier> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("locations_to_supplier")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and dw.locationsId = '" + locationId+"'";
			} else {
				logicCheckString = "";
			}
			String queryString = "select dw from LocationsToSupplier dw where dw.updated>=?  " + logicCheckString;
			TypedQuery<LocationsToSupplier> query = em.createQuery(queryString, LocationsToSupplier.class)
					.setParameter(1, updated);
			List<LocationsToSupplier> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("location_setting")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and ls.locationId = '" + locationId+"'";
			} else {
				logicCheckString = "";
			}
			String queryString = "select ls from LocationSetting ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<LocationSetting> query = em.createQuery(queryString, LocationSetting.class).setParameter(1,
					updated);
			List<LocationSetting> resultSet = query.getResultList();
			return convertToJson(resultSet);
		}

		else if (cdoName.equals("option_type")) {

			logicCheckString = "";
			String queryString = "select ot from OptionType ot where ot.updated>=?  " + logicCheckString;
			TypedQuery<OptionType> query = em.createQuery(queryString, OptionType.class).setParameter(1, updated);
			List<OptionType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("delivery_option")) {
			logicCheckString = "";
			String queryString = "select do from DeliveryOption do where do.updated>=?  " + logicCheckString;
			TypedQuery<DeliveryOption> query = em.createQuery(queryString, DeliveryOption.class).setParameter(1,
					updated);
			List<DeliveryOption> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("employee_operation_to_alert_message")) {
			logicCheckString = "";
			String queryString = "select do from EmployeeOperationToAlertMessage do where do.updated>=?  ";
			TypedQuery<EmployeeOperationToAlertMessage> query = em
					.createQuery(queryString, EmployeeOperationToAlertMessage.class).setParameter(1, updated);
			List<EmployeeOperationToAlertMessage> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("discount_to_reasons")) {
			logicCheckString = "";
			String queryString = "select do from DiscountToReasons do where do.updated>=?  ";
			TypedQuery<DiscountToReasons> query = em.createQuery(queryString, DiscountToReasons.class).setParameter(1,
					updated);
			List<DiscountToReasons> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("order_type")) {

			String queryString = "select ot from OrderType ot where ot.updated>=?  ";
			TypedQuery<OrderType> query = em.createQuery(queryString, OrderType.class).setParameter(1, updated);
			List<OrderType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		}

		else if (cdoName.equals("locations_to_shift_pre_assign_server")) {

			logicCheckString = "";
			String queryString = "select lts from LocationsToShiftPreAssignServer lts where lts.updated>=?  "
					+ logicCheckString;
			TypedQuery<LocationsToShiftPreAssignServer> query = em
					.createQuery(queryString, LocationsToShiftPreAssignServer.class).setParameter(1, updated);
			List<LocationsToShiftPreAssignServer> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("operational_shift_schedule")) {
			logicCheckString = "";
			String queryString = "select oss from OperationalShiftSchedule oss where oss.updated>=?  "
					+ logicCheckString;
			TypedQuery<OperationalShiftSchedule> query = em.createQuery(queryString, OperationalShiftSchedule.class)
					.setParameter(1, updated);
			List<OperationalShiftSchedule> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("employee_master_to_job_roles")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and ls.locationsId = '" + locationId+"'";
			} else {
				logicCheckString = "";
			}
			String queryString = "select ls from EmployeeMasterToJobRoles ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<EmployeeMasterToJobRoles> query = em.createQuery(queryString, EmployeeMasterToJobRoles.class)
					.setParameter(1, updated);
			List<EmployeeMasterToJobRoles> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("job_roles")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and ls.locationId = '" + locationId+"'";
			} else {
				logicCheckString = "";
			}
			String queryString = "select ls from JobRoles ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<JobRoles> query = em.createQuery(queryString, JobRoles.class).setParameter(1, updated);
			List<JobRoles> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("nutritions")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and ls.locationsId = '" + locationId+"'";
			} else {
				logicCheckString = "";
			}
			String queryString = "select ls from Nutritions ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<Nutritions> query = em.createQuery(queryString, Nutritions.class).setParameter(1, updated);
			List<Nutritions> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_to_nutritions")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and ls.nutritionsId in (select id from Nutritions   where  locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString = "";
			}
			String queryString = "select ls from ItemsToNutritions ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<ItemsToNutritions> query = em.createQuery(queryString, ItemsToNutritions.class).setParameter(1,
					updated);
			List<ItemsToNutritions> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_attribute_to_nutritions")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and ls.nutritionsId in (select id from Nutritions   where  locationsId='"
						+ locationId + "')";
			} else {
				logicCheckString = "";
			}
			String queryString = "select ls from ItemsAttributeToNutritions ls where ls.updated>=?  "
					+ logicCheckString;
			TypedQuery<ItemsAttributeToNutritions> query = em.createQuery(queryString, ItemsAttributeToNutritions.class)
					.setParameter(1, updated);
			List<ItemsAttributeToNutritions> resultSet = query.getResultList();
			return convertToJson(resultSet);
		}

		else if (cdoName.equals("items_schedule")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and ls.locationId = '" + locationId+"'";
			} else {
				logicCheckString = "";
			}
			String queryString = "select ls from ItemsSchedule ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<ItemsSchedule> query = em.createQuery(queryString, ItemsSchedule.class).setParameter(1, updated);
			List<ItemsSchedule> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("items_schedule_days")) {
			if (isLocationSpecific == 1) {
				logicCheckString += "and ls.locationId = '" + locationId+"'";
			} else {
				logicCheckString = "";
			}
			String queryString = "select ls from ItemsScheduleDay ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<ItemsScheduleDay> query = em.createQuery(queryString, ItemsScheduleDay.class).setParameter(1,
					updated);
			List<ItemsScheduleDay> resultSet = query.getResultList();
			return convertToJson(resultSet);

		}

		else if (cdoName.equals("items_to_schedule")) {
			if (isLocationSpecific == 1) {
				logicCheckString += " and  ls.itemsId in (select i.id from Item i where i.locationsId='" + locationId+"'"
						+ ")";
			} else {
				logicCheckString += "";
			}
			String queryString = "select ls from ItemsToSchedule ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<ItemsToSchedule> query = em.createQuery(queryString, ItemsToSchedule.class).setParameter(1,
					updated);
			List<ItemsToSchedule> resultSet = query.getResultList();
			return convertToJson(resultSet);
		}

		else if (cdoName.equals("storage_type")) {

			String queryString = "select ls from StorageType ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<StorageType> query = em.createQuery(queryString, StorageType.class).setParameter(1, updated);
			List<StorageType> resultSet = query.getResultList();
			return convertToJson(resultSet);
		}

		else if (cdoName.equals("item_to_date")) {
			if (isLocationSpecific == 1) {
				logicCheckString += " and  ls.locationId='" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select ls from ItemToDate ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<ItemToDate> query = em.createQuery(queryString, ItemToDate.class).setParameter(1, updated);
			List<ItemToDate> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("item_attribute_to_date")) {
			if (isLocationSpecific == 1) {
				logicCheckString += " and  ls.locationId='" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select ls from ItemAttributeToDate ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<ItemAttributeToDate> query = em.createQuery(queryString, ItemAttributeToDate.class)
					.setParameter(1, updated);
			List<ItemAttributeToDate> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("order_additional_question")) {
			if (isLocationSpecific == 1) {
				logicCheckString += " and  ls.locationId='" + locationId+"'";
			} else {
				logicCheckString += "";
			}
			String queryString = "select ls from OrderAdditionalQuestion ls where ls.updated>=?  " + logicCheckString;
			TypedQuery<OrderAdditionalQuestion> query = em.createQuery(queryString, OrderAdditionalQuestion.class)
					.setParameter(1, updated);
			List<OrderAdditionalQuestion> resultSet = query.getResultList();
			return convertToJson(resultSet);
		} else if (cdoName.equals("posn_partners")) {
			 
			
			String queryString = "select ls from POSNPartners ls where ls.updated>=? and ls.businessId=? ";
			TypedQuery<POSNPartners> query = em.createQuery(queryString, POSNPartners.class)
					.setParameter(1, updated).setParameter(2, Integer.parseInt(locationId));
			List<POSNPartners> resultSet = query.getResultList();
			return convertToJson(resultSet);
		}

		else
			return null;

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
	private String convertToJson(Object cdoMgmtObj) throws JsonGenerationException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();

		Writer strWriter = new StringWriter();
		objectMapper.writeValue(strWriter, cdoMgmtObj);
		String jsonString = strWriter.toString();
		return jsonString;

	}

	/**
	 * Builds the response user object resultset.
	 *
	 * @param objRow
	 *            the obj row
	 * @param u
	 *            the u
	 * @return the user
	 */
	User buildResponseUserObjectResultset(Object[] objRow, User u) {
		int i = 0;
		u.setId((String) objRow[i++]);
		u.setUsername((String) objRow[i++]);
		u.setFirstName((String) objRow[i++]);
		u.setLastName((String) objRow[i++]);
		u.setStatus((String) objRow[i++].toString());
		u.setEmail((String) objRow[i++]);
		u.setDateofbirth((String) objRow[i++]);
		u.setPhone((String) objRow[i++]);
		if (objRow[i] != null) {
			u.setLastLoginTs(new Date(((Timestamp) objRow[i]).getTime()));
			i++;
		} else {
			i++;
		}
		u.setAuthPin((String) objRow[i++]);
		u.setGlobalUsersId((String) objRow[i++]);
		u.setComments((String) objRow[i++]);
		if ((Date) objRow[i] != null) {
			u.setCreated((Date) objRow[i]);
			i++;
		} else {
			i++;
		}
		u.setCreatedBy((String) objRow[i++]);
		if ((Date) objRow[i] != null) {
			u.setUpdated((Date) objRow[i]);
			i++;
		} else {
			i++;
		}
		u.setUpdatedBy((String) objRow[i++]);
		u.setUserColor((String) objRow[i++]);
		u.setIsAllowedLogin((Integer) objRow[i++]);
		u.setIsTippedEmployee((Integer) objRow[i++]);

		return u;
	}

	/**
	 * Adds the shift schedule.
	 *
	 * @param shiftSchedule
	 *            the shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the shift schedule
	 * @throws Exception
	 *             the exception
	 */
	ShiftSchedule addShiftSchedule(ShiftSchedule shiftSchedule, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		if (shiftSchedule != null) {
			shiftSchedule = addUpdateShiftSchedule(shiftSchedule, httpRequest, em);
		}

		return shiftSchedule;
	}

	/**
	 * Adds the update shift schedule.
	 *
	 * @param shiftSchedule
	 *            the shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @throws Exception
	 *             the exception
	 */
	private ShiftSchedule addUpdateShiftSchedule(ShiftSchedule shiftSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		if (shiftSchedule.getId() == null || shiftSchedule.getId().equals("0")) {
			shiftSchedule.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			shiftSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			if (shiftSchedule.getId() == null || shiftSchedule.getId().equals("0"))
				shiftSchedule.setId(new StoreForwardUtility().generateUUID());
			shiftSchedule = em.merge(shiftSchedule);
		} else {
			shiftSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			shiftSchedule = em.merge(shiftSchedule);
		}
		return shiftSchedule;

	}

	/**
	 * Update shift schedule.
	 *
	 * @param shiftSchedule
	 *            the shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the shift schedule
	 * @throws Exception
	 *             the exception
	 */
	ShiftSchedule updateShiftSchedule(ShiftSchedule shiftSchedule, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		if (shiftSchedule != null) {
			addUpdateShiftSchedule(shiftSchedule, httpRequest, em);
		}

		return shiftSchedule;
	}

	/**
	 * Delete shift schedule.
	 *
	 * @param shiftSchedule
	 *            the shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the shift schedule
	 * @throws Exception
	 *             the exception
	 */
	ShiftSchedule deleteShiftSchedule(ShiftSchedule shiftSchedule, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		ShiftSchedule schedule = (ShiftSchedule) new CommonMethods().getObjectById("ShiftSchedule", em,
				ShiftSchedule.class, shiftSchedule.getId());
		schedule.setStatus("D");
		schedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(schedule);

		List<OrderSourceToShiftSchedule> orderSourceToShiftSchedules = getOrderSourceToShiftScheduleByShiftScheduleId(
				em, shiftSchedule.getId());
		List<OrderSourceGroupToShiftSchedule> orderSourceGroupToShiftSchedules = getOrderSourceGroupToShiftScheduleByShiftScheduleId(
				em, shiftSchedule.getId());
		for (OrderSourceToShiftSchedule orderSourceToShiftSchedule : orderSourceToShiftSchedules) {
			orderSourceToShiftSchedule.setStatus("D");
			orderSourceToShiftSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			em.merge(orderSourceToShiftSchedule);
		}

		for (OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule : orderSourceGroupToShiftSchedules) {
			orderSourceGroupToShiftSchedule.setStatus("D");
			orderSourceGroupToShiftSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.merge(orderSourceGroupToShiftSchedule);
		}
		if (schedule.getId() != null && !schedule.getId().equals("0")) {
			String queryString = "delete from shift_slots where shift_schedule_id= ? ";
			// TODO why not use em.remove?
			// Bcoz we have 10+k records for a single shift_slots iteration will
			// take time thats why.

			int rowUpdated = em.createNativeQuery(queryString).setParameter(1, schedule.getId()).executeUpdate();
		}

		return schedule;
	}

	/**
	 * Block shift schedule for order source group.
	 *
	 * @param orderSourceGroupToShiftSchedule
	 *            the order source group to shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source group to shift schedule
	 * @throws Exception
	 *             the exception
	 */
	OrderSourceGroupToShiftSchedule blockShiftScheduleForOrderSourceGroup(
			OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {

		if (orderSourceGroupToShiftSchedule != null) {

			addUpdateOrderSourceGroupToShiftSchedule(orderSourceGroupToShiftSchedule, httpRequest, em);

		}

		return orderSourceGroupToShiftSchedule;
	}

	/**
	 * Block shift schedule for order source.
	 *
	 * @param orderSourceToShiftSchedule
	 *            the order source to shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source to shift schedule
	 * @throws Exception
	 *             the exception
	 */
	OrderSourceToShiftSchedule blockShiftScheduleForOrderSource(OrderSourceToShiftSchedule orderSourceToShiftSchedule,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {

		if (orderSourceToShiftSchedule != null) {
			addUpdateOrderSourceToShiftSchedule(orderSourceToShiftSchedule, httpRequest, em);

		}

		return orderSourceToShiftSchedule;
	}

	/**
	 * Adds the update order source group to shift schedule.
	 *
	 * @param orderSourceGroupToShiftSchedule
	 *            the order source group to shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @throws Exception
	 *             the exception
	 */
	private void addUpdateOrderSourceGroupToShiftSchedule(
			OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {

		if (orderSourceGroupToShiftSchedule.getId() == 0) {
			orderSourceGroupToShiftSchedule.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		orderSourceGroupToShiftSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		orderSourceGroupToShiftSchedule.setStatus("B");
		orderSourceGroupToShiftSchedule = em.merge(orderSourceGroupToShiftSchedule);

		List<OrderSource> orderSources = getOrderSourceFromOrderSourceGroupId(em,
				orderSourceGroupToShiftSchedule.getOrderSourceGroupId());
		// todo shlok need to handle exception in below line
		// handle null here
		for (OrderSource orderSource : orderSources) {
			OrderSourceToShiftSchedule sourceToShiftSchedule = new OrderSourceToShiftSchedule();
			OrderSourceToShiftSchedule sourceToShiftScheduleDB = getOrderSourceToShiftSchedule(em, orderSource.getId(),
					orderSourceGroupToShiftSchedule);
			if (sourceToShiftScheduleDB != null) {

				sourceToShiftSchedule.setId(sourceToShiftScheduleDB.getId());
			}

			sourceToShiftSchedule.setOrderSourceGroupToShiftScheduleId(orderSourceGroupToShiftSchedule.getId());
			sourceToShiftSchedule.setFromDate(orderSourceGroupToShiftSchedule.getFromDate());
			sourceToShiftSchedule.setToDate(orderSourceGroupToShiftSchedule.getToDate());
			sourceToShiftSchedule.setFromTime(orderSourceGroupToShiftSchedule.getFromTime());
			sourceToShiftSchedule.setToTime(orderSourceGroupToShiftSchedule.getToTime());
			sourceToShiftSchedule.setOrderSourceId(orderSource.getId());
			sourceToShiftSchedule.setShiftScheduleId(orderSourceGroupToShiftSchedule.getShiftScheduleId());
			sourceToShiftSchedule.setCreated(orderSourceGroupToShiftSchedule.getCreated());
			sourceToShiftSchedule.setCreatedBy(orderSourceGroupToShiftSchedule.getCreatedBy());
			sourceToShiftSchedule.setUpdated(orderSourceGroupToShiftSchedule.getUpdated());
			sourceToShiftSchedule.setUpdatedBy(orderSourceGroupToShiftSchedule.getUpdatedBy());
			sourceToShiftSchedule.setStatus("B");
			em.merge(sourceToShiftSchedule);
		}

	}

	/**
	 * Gets the order source to shift schedule.
	 *
	 * @param em
	 *            the em
	 * @param orderSourceId
	 *            the order source id
	 * @param orderSourceGroupToShiftSchedule
	 *            the order source group to shift schedule
	 * @return the order source to shift schedule
	 */
	private OrderSourceToShiftSchedule getOrderSourceToShiftSchedule(EntityManager em, String orderSourceId,
			OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule) {
		OrderSourceToShiftSchedule orderSourceToShiftSchedule = null;
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceToShiftSchedule> criteria = builder.createQuery(OrderSourceToShiftSchedule.class);
			Root<OrderSourceToShiftSchedule> r = criteria.from(OrderSourceToShiftSchedule.class);
			TypedQuery<OrderSourceToShiftSchedule> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(OrderSourceToShiftSchedule_.orderSourceId), orderSourceId),
					builder.notEqual(r.get(OrderSourceToShiftSchedule_.status), "D"),
					builder.equal(r.get(OrderSourceToShiftSchedule_.shiftScheduleId),
							orderSourceGroupToShiftSchedule.getShiftScheduleId()),
					builder.equal(r.get(OrderSourceToShiftSchedule_.orderSourceGroupToShiftScheduleId),
							orderSourceGroupToShiftSchedule.getId())));

			return query.getSingleResult();

		} catch (Exception e) {
			logger.severe(e);
		}
		return orderSourceToShiftSchedule;
	}

	/**
	 * Adds the update order source to shift schedule.
	 *
	 * @param orderSourceToShiftSchedule
	 *            the order source to shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @throws Exception
	 *             the exception
	 */
	private void addUpdateOrderSourceToShiftSchedule(OrderSourceToShiftSchedule orderSourceToShiftSchedule,
			HttpServletRequest httpRequest, EntityManager em) throws Exception {
		if (orderSourceToShiftSchedule.getId() == 0) {
			orderSourceToShiftSchedule.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		orderSourceToShiftSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		orderSourceToShiftSchedule.setStatus("B");
		em.persist(orderSourceToShiftSchedule);

	}

	/**
	 * Gets the order source from order source group id.
	 *
	 * @param em
	 *            the em
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the order source from order source group id
	 */
	private List<OrderSource> getOrderSourceFromOrderSourceGroupId(EntityManager em, String orderSourceGroupId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> r = criteria.from(OrderSource.class);
			TypedQuery<OrderSource> query = em.createQuery(
					criteria.select(r).where(builder.equal(r.get(OrderSource_.orderSourceGroupId), orderSourceGroupId),
							builder.notEqual(r.get(OrderSource_.status), "D"),
							builder.notEqual(r.get(OrderSource_.status), "I")));
			List<OrderSource> objects = query.getResultList();

			return objects;
		} catch (Exception e) {

			logger.severe(e);
		}
		return null;

	}

	public class ManageShiftSlotHoldClient implements Runnable {

		/** The shift slot hold time. */
		// default sleep time
		int shiftSlotHoldTime = sessionClearTimeInMinutes * 60 * 1000;

		/** The sesson id. */
		String sessonId;

		/** The shift slot id. */
		int shiftSlotId;

		/** The client shift obj id. */
		int clientShiftObjId = 0;

		/** The schema name. */
		String schemaName = null;

		/** The logger. */
		private final NirvanaLogger logger = new NirvanaLogger(ManageShiftSlotHoldClient.class.getName());

		/**
		 * Instantiates a new manage shift slot hold client.
		 *
		 * @param shiftSlotHoldTime
		 *            the shift slot hold time
		 * @param sessonId
		 *            the sesson id
		 * @param shiftSlotId
		 *            the shift slot id
		 * @param clientShiftObjId
		 *            the client shift obj id
		 * @param schemaName
		 *            the schema name
		 */
		public ManageShiftSlotHoldClient(int shiftSlotHoldTime, String sessonId, int shiftSlotId, int clientShiftObjId,
				String schemaName) {
			super();
			this.shiftSlotHoldTime = shiftSlotHoldTime;
			this.sessonId = sessonId;
			this.shiftSlotId = shiftSlotId;
			this.clientShiftObjId = clientShiftObjId;
			this.schemaName = schemaName;
		}

		public void run() {
			// todo shlok need
			// modulise code
			EntityManager entityManager = null;
			EntityTransaction tx = null;
			try {
				Thread.sleep(shiftSlotHoldTime);
				if (sessonId != null) {
					entityManager = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);

					if (entityManager != null) {
						ShiftSlotActiveClientInfo shiftSlotActiveClientInfo = entityManager
								.find(ShiftSlotActiveClientInfo.class, clientShiftObjId);
						if (shiftSlotActiveClientInfo != null) {
							// delete this client as his session is expired
							tx = entityManager.getTransaction();
							try {
								// start transaction
								tx.begin();
								entityManager.remove(entityManager.merge(shiftSlotActiveClientInfo));
								tx.commit();
							} catch (RuntimeException e) {
								// on error, if transaction active,
								// rollback
								if (tx != null && tx.isActive()) {
									tx.rollback();
								}
								throw e;
							}

							// check if the shift is already added/updated
							// by
							// client, then the slot management is already
							// handled
							// by those methods
							// if shift not made/updated, then client
							// session
							// has expired, we must now release the slot holded
							// by
							// him
							if (shiftSlotActiveClientInfo.isShiftMadeByClient() == false) {

								ShiftSlots shiftSlots = entityManager.find(ShiftSlots.class,
										shiftSlotActiveClientInfo.getShiftSlotId());
								if (shiftSlots != null) {
									// send packet to message slot jms queue, so
									// that slot
									// could be re-maintained,this we need when
									// many
									// thread will expire at same time, they all
									// take
									// same value form database and decrement
									// slot
									// by lesser value than expected, hence slot
									// starts behaving abruptly
									if (shiftSlots.getCurrentlyHoldedClient() == 1) {
										shiftSlots.setCurrentlyHoldedClient(shiftSlots.getCurrentlyHoldedClient() - 1);
										shiftSlots.setStatus("A");

									} else {
										shiftSlots.setCurrentlyHoldedClient(shiftSlots.getCurrentlyHoldedClient() - 1);
									}
									tx.begin();
									entityManager.merge(shiftSlots);
									tx.commit();
									new SendPacketToManageShiftSlotQueue().sendMessage(shiftSlots.getId(), schemaName,
											"shiftSlot");

								}

							}
						}
					}
				}
			} catch (RuntimeException e) {
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			} catch (InterruptedException e) {
				logger.severe(e, "Slot management thread interrupted: ", e.getMessage());
			} catch (Exception e) {
				logger.severe(e, "Exception in slot management thread", e.getMessage());
			} finally {
				LocalSchemaEntityManager.getInstance().closeEntityManager(entityManager);
			}

		}
	}

	/**
	 * Un hold shift slot for client.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param sessionId
	 *            the session id
	 * @param shiftHoldingClientId
	 *            the shift holding client id
	 * @param schemaName
	 *            the schema name
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	public boolean unHoldShiftSlotForClient(HttpServletRequest httpRequest, EntityManager em, String sessionId,
			String shiftHoldingClientId, String schemaName) throws Exception {

		if (schemaName != null) {

			if (em != null) {
				ShiftSlotActiveClientInfo shiftSlotActiveClientInfo = em.find(ShiftSlotActiveClientInfo.class,
						Integer.parseInt(shiftHoldingClientId));
				if (shiftSlotActiveClientInfo != null) {

					// check if the shift is already added/updated
					// by
					// client, then the slot management is already
					// handled
					// by those methods
					// if shift not made/updated, then client
					// session
					// has expired, we must now release the slot holded
					// by
					// him
					if (shiftSlotActiveClientInfo.isShiftMadeByClient() == false) {
						EntityTransaction tx = em.getTransaction();
						try {
							// start transaction
							tx.begin();
							em.remove(em.merge(shiftSlotActiveClientInfo));
							tx.commit();
						} catch (RuntimeException e) {
							// on error, if transaction active,
							// rollback
							if (tx != null && tx.isActive()) {
								tx.rollback();
							}
							throw e;
						}

						ShiftSlots shiftSlots = em.find(ShiftSlots.class, shiftSlotActiveClientInfo.getShiftSlotId());
						if (shiftSlots != null) {
							int currentShiftSlotHoldedByClient = shiftSlots.getCurrentlyHoldedClient();
							if (currentShiftSlotHoldedByClient != 0) {
								currentShiftSlotHoldedByClient = currentShiftSlotHoldedByClient - 1;
								shiftSlots.setCurrentlyHoldedClient(currentShiftSlotHoldedByClient);
								ShiftSchedule shiftSchedule = (ShiftSchedule) new CommonMethods().getObjectById(
										"ShiftSchedule", em, ShiftSchedule.class, shiftSlots.getShiftScheduleId());

								int maxShiftAllowedinslot = shiftSchedule.getMaxOrderAllowed();
								int currentOrderMadeInSlot = shiftSlots.getCurrentOrderInSlot();
								if (maxShiftAllowedinslot > currentOrderMadeInSlot + currentShiftSlotHoldedByClient) {
									if (shiftSlots.getStatus().equals("H")) {
										shiftSlots.setStatus("A");
									}

								}

								try {
									tx.begin();
									em.merge(shiftSlots);
									tx.commit();
								} catch (RuntimeException e) {
									// on error, if transaction active,
									// rollback
									if (tx != null && tx.isActive()) {
										tx.rollback();
									}
									throw e;
								}
							}

							// delete this client as his session is expired

							// send packet to message slot jms queue, so
							// that slot
							// could be re-maintained,this we need when
							// many
							// thread will expire at same time, they all
							// take
							// same value form database and decrement
							// slot
							// by lesser value than expected, hence slot
							// starts behaving abruptly
							new SendPacketToManageShiftSlotQueue().sendMessage(shiftSlots.getId(), schemaName,
									"shiftSlot");

						}
						return true;
					}
				}
			}

		}

		return false;
	}

	/**
	 * Update shift slot.
	 *
	 * @param shiftSlotId
	 *            the shift slot id
	 * @param holdFlag
	 *            the hold flag
	 * @param updatedBy
	 *            the updated by
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the shift slots
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public ShiftSlots updateShiftSlot(int shiftSlotId, int holdFlag, String updatedBy, HttpServletRequest httpRequest,
			EntityManager em) throws NirvanaXPException {
		ShiftSlots shiftSlot = em.find(ShiftSlots.class, shiftSlotId);
		if (shiftSlot != null) {
			shiftSlot.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			shiftSlot.setIsBlocked(holdFlag);
			shiftSlot.setUpdatedBy(updatedBy);
			EntityTransaction tx = em.getTransaction();
			try {
				// start transaction
				tx.begin();
				em.merge(shiftSlot);
				tx.commit();
			} catch (RuntimeException e) {
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
		} else {
			throw new NirvanaXPException(
					new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_SHIFT_SLOT_NOT_EXIST,
							MessageConstants.ERROR_MESSAGE_SHIFT_SLOT_NOT_EXIST, null));
		}

		return shiftSlot;
	}

	/**
	 * Adds the multiple locations printers.
	 *
	 * @param em
	 *            the em
	 * @param printer
	 *            the printer
	 * @param printerPacket
	 *            the printer packet
	 * @param request
	 *            the request
	 * @return the printer
	 * @throws Exception
	 *             the exception
	 */
	Printer addMultipleLocationsPrinters(EntityManager em, Printer printer, PrinterPacket printerPacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		logger.severe(
				"printerPacket.getLocationsListId()===================================================================="
						+ printerPacket.getLocationsListId());
		String[] locationIds = printerPacket.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (printer != null && printerPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
//			printer.setLocationsId(baseLocation.getId());

			Printer globalPrinter = updatePrinter(printer, request, em, printerPacket);
			printerPacket.setLocationsListId("");
			printerPacket.setPrinter(globalPrinter);
			

			em.getTransaction().commit();
			em.getTransaction().begin();
			// now add/update child location
			for (String locationsId : locationIds) {
				
				if (locationsId.length()>0 && !locationsId.equals(baseLocation.getId())) {
					String json = new StoreForwardUtility().returnJsonPacket(printerPacket, "PrinterPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
							Integer.parseInt(printerPacket.getMerchantId()));
					Printer localPrinter = new Printer().getPrinterObject(printer);
					localPrinter.setGlobalPrinterId(globalPrinter.getId());
					localPrinter.setLocationsId(locationsId);
					localPrinter.setPrintersInterfaceId(getLocalPrintersInterfaceFromGlobalId(em,
							globalPrinter.getPrintersInterfaceId(), locationsId));
					localPrinter.setPrintersTypeId(
							getLocalPrintersTypeFromGlobalId(em, globalPrinter.getPrintersTypeId(), locationsId));
					localPrinter.setLocationsId(locationsId);
					localPrinter.setCashRegisterToPrinter(printer.getCashRegisterToPrinter());
					Printer local = updatePrinter(localPrinter, request, em, printerPacket);
					printerPacket.setPrinter(local);
					printerPacket.setLocalServerURL(0);
					String json2 = new StoreForwardUtility().returnJsonPacket(printerPacket, "PrinterPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request, baseLocation.getId(),
							Integer.parseInt(printerPacket.getMerchantId()));

				}
			}
		}
		return printer;
	}

	/**
	 * Update multiple locations printers.
	 *
	 * @param em
	 *            the em
	 * @param printer
	 *            the printer
	 * @param printerPacket
	 *            the printer packet
	 * @param request
	 *            the request
	 * @return the printer
	 * @throws Exception
	 *             the exception
	 */
	Printer updateMultipleLocationsPrinters(EntityManager em, Printer printer, PrinterPacket printerPacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		if (printerPacket.getLocationId().trim().length() > 0) {
			locationIds = printerPacket.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (printer != null && printerPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item

			Printer globalPrinter = updatePrinter(printer, request, em, printerPacket);
			printerPacket.setLocationsListId("");
			printerPacket.setPrinter(globalPrinter);
			
			// now add/update child location
			if (locationIds != null) {
				for (String locationId : locationIds) {
					String locationsId = locationId;
					
					if (!locationId.equals((baseLocation.getId()))) {
						String json = new StoreForwardUtility().returnJsonPacket(printerPacket, "PrinterPacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request,baseLocation.getId(),
								Integer.parseInt(printerPacket.getMerchantId()));
						Printer localPrinter = new Printer().getPrinterObject(printer);
						Printer printers = getPrinterByGlobalPrinterIdAndLocationId(em, locationsId,
								globalPrinter.getId());
						if (printers != null && printers.getId() != null) {
							localPrinter.setGlobalPrinterId(printers.getGlobalPrinterId());
							localPrinter.setId(printers.getId());
						} else {
							localPrinter.setGlobalPrinterId(globalPrinter.getId());
						}

						localPrinter.setPrintersInterfaceId(getLocalPrintersInterfaceFromGlobalId(em,
								globalPrinter.getPrintersInterfaceId(), locationsId));
						localPrinter.setPrintersTypeId(
								getLocalPrintersTypeFromGlobalId(em, globalPrinter.getPrintersTypeId(), locationsId));
						localPrinter.setLocationsId(locationId);
						localPrinter.setCashRegisterToPrinter(printer.getCashRegisterToPrinter());
						Printer local = updatePrinter(localPrinter, request, em, printerPacket);
						printerPacket.setPrinter(local);
						printerPacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(printerPacket, "PrinterPacket",
								request);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request,
								printer.getLocationsId(), Integer.parseInt(printerPacket.getMerchantId()));
					}
				}
			}
		}
		return printer;
	}

	/**
	 * Gets the local printers interface from global id.
	 *
	 * @param em
	 *            the em
	 * @param globalId
	 *            the global id
	 * @param locationId
	 *            the location id
	 * @return the local printers interface from global id
	 */
	private String getLocalPrintersInterfaceFromGlobalId(EntityManager em, String globalId, String locationId) {
		PrintersInterface printersInterface = null;
		try {
			String queryString = "select s from PrintersInterface s where s.globalPrintersInterfaceId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<PrintersInterface> query = em.createQuery(queryString, PrintersInterface.class)
					.setParameter(1, globalId).setParameter(2, locationId);
			printersInterface = query.getSingleResult();
			
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);
		}
		if (printersInterface == null) {
			PrintersInterface local = (PrintersInterface) new CommonMethods()
					.getObjectById("PrintersInterface", em, PrintersInterface.class, globalId);
			if (local != null) {
				printersInterface = new PrintersInterface().getPrinterInterfaceObject(local);
				printersInterface.setLocationsId(locationId);
				printersInterface.setGlobalPrintersInterfaceId(globalId);
				if(printersInterface.getId()==null){
					printersInterface.setId(new StoreForwardUtility().generateUUID());
				}
				printersInterface = em.merge(printersInterface);
			}

		}
		return printersInterface.getId();
	}

	/**
	 * Gets the local printers type from global id.
	 *
	 * @param em
	 *            the em
	 * @param globalId
	 *            the global id
	 * @param locationId
	 *            the location id
	 * @return the local printers type from global id
	 */
	private String getLocalPrintersTypeFromGlobalId(EntityManager em, String globalId, String locationId) {
		try {
			logger.severe(
					"globalId======================================2222222222222222222============================================"
							+ globalId + " " + locationId);

			String queryString = "select s from PrintersType s where s.globalPrintersTypeId =? and s.locationsId=?  ";
			TypedQuery<PrintersType> query = em.createQuery(queryString, PrintersType.class).setParameter(1, globalId)
					.setParameter(2, locationId);
			return query.getSingleResult().getId();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);
		}
		return null;
	}

	/**
	 * Gets the printer by global printer id and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalPrinterId
	 *            the global printer id
	 * @return the printer by global printer id and location id
	 */
	private Printer getPrinterByGlobalPrinterIdAndLocationId(EntityManager em, String locationId,
			String globalPrinterId) {
		try {
			String queryString = "select s from Printer s where s.globalPrinterId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<Printer> query = em.createQuery(queryString, Printer.class).setParameter(1, globalPrinterId)
					.setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the course by global course id and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalCourseId
	 *            the global course id
	 * @return the course by global course id and location id
	 */
	private Course getCourseByGlobalCourseIdAndLocationId(EntityManager em, String locationId, String globalCourseId) {
		try {
			String queryString = "select s from Course s where s.globalCourseId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<Course> query = em.createQuery(queryString, Course.class).setParameter(1, globalCourseId)
					.setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line

			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the item attribute type by global item attribute type id and
	 * location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalItemAttributeTypeId
	 *            the global item attribute type id
	 * @return the item attribute type by global item attribute type id and
	 *         location id
	 */
	private ItemsAttributeType getItemAttributeTypeByGlobalItemAttributeTypeIdAndLocationId(EntityManager em,
			String locationId, String globalItemAttributeTypeId) {
		ItemsAttributeType attributeType = null;
		try {
			String queryString = "select s from ItemsAttributeType s where s.globalItemAttributeTypeId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<ItemsAttributeType> query = em.createQuery(queryString, ItemsAttributeType.class)
					.setParameter(1, globalItemAttributeTypeId).setParameter(2, locationId);
			attributeType = query.getSingleResult();
		} catch (NoResultException e) {

			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		if (attributeType == null) {
			ItemsAttributeType attributeTypeLocal = (ItemsAttributeType) new CommonMethods()
					.getObjectById("ItemsAttributeType", em, ItemsAttributeType.class, globalItemAttributeTypeId);
			if (attributeTypeLocal != null) {
				attributeType = new ItemsAttributeType().getItemsAttributeTypeObject(attributeTypeLocal);
				attributeType.setLocationsId(locationId);
				attributeType.setGlobalItemAttributeTypeId(globalItemAttributeTypeId);
				if(attributeType.getId()==null){
					attributeType.setId(new StoreForwardUtility().generateUUID());
				}
				attributeType = em.merge(attributeType);
			}

		}
		return attributeType;
	}

	/**
	 * Gets the items attribute type to items attribute by global item attribute
	 * type id item attribute id and location id.
	 *
	 * @param em
	 *            the em
	 * @param itemAttributeId
	 *            the item attribute id
	 * @return the items attribute type to items attribute by global item
	 *         attribute type id item attribute id and location id
	 */
	private ItemsAttributeTypeToItemsAttribute getItemsAttributeTypeToItemsAttributeByGlobalItemAttributeTypeIdItemAttributeIdAndLocationId(
			EntityManager em, String itemAttributeId) {
		try {
			String queryString = "select s from ItemsAttributeTypeToItemsAttribute s where   s.itemsAttributeId=?    ";
			TypedQuery<ItemsAttributeTypeToItemsAttribute> query = em
					.createQuery(queryString, ItemsAttributeTypeToItemsAttribute.class)
					.setParameter(1, itemAttributeId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the items char to items attribute by global item attribute type id
	 * item attribute id and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalId
	 *            the global id
	 * @param itemAttributeId
	 *            the item attribute id
	 * @return the items char to items attribute by global item attribute type
	 *         id item attribute id and location id
	 */
	private ItemsCharToItemsAttribute getItemsCharToItemsAttributeByGlobalItemAttributeTypeIdItemAttributeIdAndLocationId(
			EntityManager em, String locationId, String globalId, String itemAttributeId) {
		try {
			String queryString = "select s from ItemsCharToItemsAttribute s where s.itemsCharId =? and s.itemsAttributeId=?   ";
			TypedQuery<ItemsCharToItemsAttribute> query = em.createQuery(queryString, ItemsCharToItemsAttribute.class)
					.setParameter(1, globalId).setParameter(2, itemAttributeId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Adds the multiple locations items attribute type.
	 *
	 * @param em
	 *            the em
	 * @param itemsAttributeTypes
	 *            the items attribute types
	 * @param ItemsAttributeTypePacket
	 *            the items attribute type packet
	 * @param request
	 *            the request
	 * @return the items attribute type
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttributeType addMultipleLocationsItemsAttributeType(EntityManager em, ItemsAttributeType itemsAttributeTypes,
			ItemsAttributeTypePacket ItemsAttributeTypePacket, HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = ItemsAttributeTypePacket.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (itemsAttributeTypes != null && ItemsAttributeTypePacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
//			itemsAttributeTypes.setLocationsId(baseLocation.getId());

			ItemsAttributeType globalItemsAttributeType = updateItemsAttributeType(itemsAttributeTypes, request, em);
			ItemsAttributeTypePacket.setItemsAttributeType(globalItemsAttributeType);
			ItemsAttributeTypePacket.setLocationsListId("");
			
			em.getTransaction().commit();
			em.getTransaction().begin();
			// now add/update child location
			for (String locationId : locationIds) {
				
			
				
				if (locationId.length()>0 && !locationId.equals(baseLocation.getId())) {
					String json = new StoreForwardUtility().returnJsonPacket(ItemsAttributeTypePacket,
							"ItemsAttributeTypePacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json, request,
							baseLocation.getId(),
							Integer.parseInt(ItemsAttributeTypePacket.getMerchantId()));
					ItemsAttributeType localItemsAttributeType = new ItemsAttributeType()
							.getItemsAttributeTypeObject(itemsAttributeTypes);
					localItemsAttributeType.setGlobalItemAttributeTypeId(globalItemsAttributeType.getId());
					localItemsAttributeType.setLocationsId((locationId));
					ItemsAttributeType ItemsAttributeType2 = updateItemsAttributeType(localItemsAttributeType, request,
							em);
					ItemsAttributeTypePacket.setItemsAttributeType(ItemsAttributeType2);
					ItemsAttributeTypePacket.setLocalServerURL(0);
					String json2 = new StoreForwardUtility().returnJsonPacket(ItemsAttributeTypePacket,
							"ItemsAttributeTypePacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request,
							locationId,
							Integer.parseInt(ItemsAttributeTypePacket.getMerchantId()));
				}
			}
		}
		return itemsAttributeTypes;
	}

	/**
	 * Update multiple locations items attribute type.
	 *
	 * @param em
	 *            the em
	 * @param itemsAttributeTypes
	 *            the items attribute types
	 * @param ItemsAttributeTypePacket
	 *            the items attribute type packet
	 * @param request
	 *            the request
	 * @return the items attribute type
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttributeType updateMultipleLocationsItemsAttributeType(EntityManager em,
			ItemsAttributeType itemsAttributeTypes, ItemsAttributeTypePacket ItemsAttributeTypePacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		if (ItemsAttributeTypePacket.getLocationId().trim().length() > 0) {
			locationIds = ItemsAttributeTypePacket.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (itemsAttributeTypes != null && ItemsAttributeTypePacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
			ItemsAttributeType globalItemsAttributeType = updateItemsAttributeType(itemsAttributeTypes, request, em);
			ItemsAttributeTypePacket.setItemsAttributeType(globalItemsAttributeType);
			ItemsAttributeTypePacket.setLocationsListId("");
			
			// now add/update child location
			if (locationIds != null) {
				for (String locationId : locationIds) {
					
					if (locationId.length()>0 && !locationId.equals((baseLocation.getId()))) {
						String json = new StoreForwardUtility().returnJsonPacket(ItemsAttributeTypePacket,
								"ItemsAttributeTypePacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request,
								baseLocation.getId(),	Integer.parseInt(ItemsAttributeTypePacket.getMerchantId()));
						ItemsAttributeType localItemsAttributeType = new ItemsAttributeType()
								.getItemsAttributeTypeObject(itemsAttributeTypes);
						ItemsAttributeType itemsAttributeType = getItemAttributeTypeByGlobalItemAttributeTypeIdAndLocationId(
								em, locationId, globalItemsAttributeType.getId());
						if (itemsAttributeType != null && itemsAttributeType.getId() != null) {
							localItemsAttributeType
									.setGlobalItemAttributeTypeId(itemsAttributeType.getGlobalItemAttributeTypeId());
							localItemsAttributeType.setId(itemsAttributeType.getId());
							localItemsAttributeType.setSortSequence(itemsAttributeType.getSortSequence());
						} else {
							localItemsAttributeType.setGlobalItemAttributeTypeId(globalItemsAttributeType.getId());
						}

						localItemsAttributeType.setLocationsId(locationId);
						localItemsAttributeType = updateItemsAttributeType(localItemsAttributeType, request, em);
						ItemsAttributeTypePacket.setItemsAttributeType(localItemsAttributeType);
						ItemsAttributeTypePacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(ItemsAttributeTypePacket,
								"ItemsAttributeTypePacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request,
								locationId,
								Integer.parseInt(ItemsAttributeTypePacket.getMerchantId()));
					}
				}
			}
		}
		return itemsAttributeTypes;
	}

	/**
	 * Adds the multiple location course.
	 *
	 * @param em
	 *            the em
	 * @param course
	 *            the course
	 * @param coursePacket
	 *            the course packet
	 * @param request
	 *            the request
	 * @return the course
	 * @throws Exception
	 *             the exception
	 */
	List<Course> addMultipleLocationCourse(EntityManager em, Course course, CoursePacket coursePacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = coursePacket.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		List<Course> newCourseList = new ArrayList<Course>();
		Course globalCourse = null;
		if (course != null && coursePacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
//			course.setLocationsId(baseLocation.getId());

			globalCourse = updateCourse(course, request, em, coursePacket);
			coursePacket.setCourse(globalCourse);
			coursePacket.setLocationsListId("");
			

			newCourseList.add(globalCourse);
			// now add/update child location
			for (String locationId : locationIds) {
				
				if (!locationId.equals((baseLocation.getId()))) {
					String json = new StoreForwardUtility().returnJsonPacket(coursePacket, "CoursePacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
							Integer.parseInt(coursePacket.getMerchantId()));
					Course localCourse = new Course().getCourse(course);
					localCourse.setGlobalCourseId(globalCourse.getId());
					localCourse.setLocationsId(locationId);
					Course course2 = updateCourse(localCourse, request, em, coursePacket);
					coursePacket.setCourse(course2);
					coursePacket.setLocalServerURL(0);
					String json2 = new StoreForwardUtility().returnJsonPacket(coursePacket, "CoursePacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request, baseLocation.getId(),
							Integer.parseInt(coursePacket.getMerchantId()));

				}
			}
		}

		return newCourseList;
	}

	/**
	 * Update multiple location course.
	 *
	 * @param em
	 *            the em
	 * @param course
	 *            the course
	 * @param coursePacket
	 *            the course packet
	 * @param request
	 *            the request
	 * @return the course
	 * @throws Exception
	 *             the exception
	 */
	Course updateMultipleLocationCourse(EntityManager em, Course course, CoursePacket coursePacket,
			HttpServletRequest request, EntityTransaction tx) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		String[] locationIds = null;
		if (coursePacket.getLocationId().trim().length() > 0) {
			locationIds = coursePacket.getLocationsListId().split(",");
		}
	
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (course != null && coursePacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
			Course globalCourse = updateCourse(course, request, em, coursePacket);
			coursePacket.setCourse(globalCourse);
			coursePacket.setLocationsListId("");
			
			// now add/update child location
			if (locationIds != null) {
				for (String locationId : locationIds) {
					
					if (!locationId.equals((baseLocation.getId()))) {
						String json = new StoreForwardUtility().returnJsonPacket(coursePacket, "CoursePacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
								Integer.parseInt(coursePacket.getMerchantId()));
						Course localCourse = new Course().getCourse(course);
						Course courses = getCourseByGlobalCourseIdAndLocationId(em, locationId, globalCourse.getId());
						if (courses != null && course.getId() != null) {
							localCourse.setGlobalCourseId(globalCourse.getId());
							localCourse.setDisplaySequence(courses.getDisplaySequence());
							localCourse.setId(courses.getId());
						} else {
							localCourse.setGlobalCourseId(globalCourse.getId());
						}

						localCourse.setLocationsId(locationId);
						Course course2 = updateCourse(localCourse, request, em, coursePacket);
						coursePacket.setCourse(course2);
						coursePacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(coursePacket, "CoursePacket",
								request);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request, locationId,
								Integer.parseInt(coursePacket.getMerchantId()));
					}
				}
			}
		}
		return course;
	}

	/**
	 * Delete multiple location course.
	 *
	 * @param em
	 *            the em
	 * @param course
	 *            the course
	 * @param coursePacket
	 *            the course packet
	 * @param request
	 *            the request
	 * @return the course
	 * @throws Exception
	 *             the exception
	 */
	Course deleteMultipleLocationCourse(EntityManager em, Course course, CoursePacket coursePacket,
			HttpServletRequest request) throws Exception {
		// delete baselocation
		course = deleteCourse(course, request, em);

		// get all sublocations
		List<Course> courses = getAllCourseByGlobalCourseId(course.getId(), em);
		// delete sublocation
		for (Course course2 : courses) {
			Course c = deleteCourse(course2, request, em);

		}
		return course;
	}

	/**
	 * Gets the all course by global course id.
	 *
	 * @param globalCourseId
	 *            the global course id
	 * @param em
	 *            the em
	 * @return the all course by global course id
	 */
	List<Course> getAllCourseByGlobalCourseId(String globalCourseId, EntityManager em) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
			Root<Course> orderSourceGroup = criteria.from(Course.class);
			TypedQuery<Course> query = em.createQuery(criteria.select(orderSourceGroup)
					.where(builder.equal(orderSourceGroup.get(Course_.globalCourseId), globalCourseId)));
			return query.getResultList();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			logger.severe("No Result found");
		}
		return null;
	}

	/**
	 * Delete multiple location printers.
	 *
	 * @param em
	 *            the em
	 * @param printer
	 *            the printer
	 * @param printerPacket
	 *            the printer packet
	 * @param request
	 *            the request
	 * @return the printer
	 * @throws Exception
	 *             the exception
	 */
	Printer deleteMultipleLocationPrinters(EntityManager em, Printer printer, PrinterPacket printerPacket,
			HttpServletRequest request) throws Exception {
		// delete baselocation
		printer = deletePrinter(printer, request, em);

		// get all sublocations
		List<Printer> printers = getAllPrinterByGlobalPrinterId(printer.getId(), em);
		// delete sublocation
		for (Printer printer2 : printers) {
			Printer local = deletePrinter(printer2, request, em);

		}
		return printer;
	}

	/**
	 * Gets the all printer by global printer id.
	 *
	 * @param globalPrinterId
	 *            the global printer id
	 * @param em
	 *            the em
	 * @return the all printer by global printer id
	 */
	List<Printer> getAllPrinterByGlobalPrinterId(String globalPrinterId, EntityManager em) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> printerRoot = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(printerRoot)
					.where(builder.equal(printerRoot.get(Printer_.globalPrinterId), globalPrinterId)));
			return query.getResultList();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			logger.severe("No Result found");
		}
		return null;
	}

	/**
	 * Adds the multiple location items char.
	 *
	 * @param em
	 *            the em
	 * @param itemChar
	 *            the item char
	 * @param itemCharPacket
	 *            the item char packet
	 * @param request
	 *            the request
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */
	ItemsChar addMultipleLocationItemsChar(EntityManager em, ItemsChar itemChar, ItemsCharPacket itemCharPacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = itemCharPacket.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		ItemsChar globalItemChar = null;
		if (itemChar != null && itemCharPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
//			itemChar.setLocationsId(baseLocation.getId());

			globalItemChar = updateItemsChar(itemChar, request, em, itemCharPacket.getLocationId(), itemCharPacket);
			itemCharPacket.setItemsChar(globalItemChar);
			itemCharPacket.setLocationsListId("");
			
			em.getTransaction().commit();
			em.getTransaction().begin();
			// now add/update child location
			for (String locationId : locationIds) {
				
				if (!locationId.equals(baseLocation.getId())) {
					String json = new StoreForwardUtility().returnJsonPacket(itemCharPacket, "ItemsCharPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
							Integer.parseInt(itemCharPacket.getMerchantId()));
					ItemsChar localChar = new ItemsChar().getItemChar(globalItemChar);
					localChar.setGlobalItemCharId(globalItemChar.getId());
					localChar.setLocationsId((locationId));
					ItemsChar itemsChar = updateItemsChar(localChar, request, em, itemCharPacket.getLocationId(),
							itemCharPacket);
					itemCharPacket.setItemsChar(itemsChar);
					itemCharPacket.setLocalServerURL(0);
					String json2 = new StoreForwardUtility().returnJsonPacket(itemCharPacket, "ItemsCharPacket",
							request);
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request, itemChar.getLocationsId(),
							Integer.parseInt(itemCharPacket.getMerchantId()));
				}
			}

		}
		return globalItemChar;
	}

	/**
	 * Update multiple location items char.
	 *
	 * @param em
	 *            the em
	 * @param itemChar
	 *            the item char
	 * @param itemCharPacket
	 *            the item char packet
	 * @param request
	 *            the request
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */
	ItemsChar updateMultipleLocationItemsChar(EntityManager em, ItemsChar itemChar, ItemsCharPacket itemCharPacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		if (itemCharPacket.getLocationId().trim().length() > 0) {
			locationIds = itemCharPacket.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (itemChar != null && itemCharPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
			ItemsChar globalItemChar = updateItemsChar(itemChar, request, em, itemCharPacket.getLocationId(),
					itemCharPacket);
			itemCharPacket.setItemsChar(globalItemChar);
			itemCharPacket.setLocationsListId("");
			
			// now add/update child location
			if (locationIds != null) {
				for (String locationId : locationIds) {
					
					if (!locationId.equals((baseLocation.getId()))) {
						String json = new StoreForwardUtility().returnJsonPacket(itemCharPacket, "ItemsCharPacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
								Integer.parseInt(itemCharPacket.getMerchantId()));
						ItemsChar localChar = new ItemsChar().getItemChar(globalItemChar);
						ItemsChar itemChars = getItemCharByGlobalCharIdAndLocationId(em, (locationId),
								globalItemChar.getId());
						if (itemChars != null) {
							localChar.setGlobalItemCharId(globalItemChar.getId());
							localChar.setSortSequence(itemChars.getSortSequence());
							localChar.setId(itemChars.getId());
						} else {
							localChar.setGlobalItemCharId(globalItemChar.getId());
						}

						localChar.setLocationsId(locationId);
						ItemsChar char1 = updateItemsChar(localChar, request, em, itemCharPacket.getLocationId(),
								itemCharPacket);
						itemCharPacket.setItemsChar(char1);
						itemCharPacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(itemCharPacket, "ItemsCharPacket",
								request);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request,
								itemChar.getLocationsId(), Integer.parseInt(itemCharPacket.getMerchantId()));
					}
				}
			}
		}
		return itemChar;
	}

	/**
	 * Gets the item char by global char id and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalCourseId
	 *            the global course id
	 * @return the item char by global char id and location id
	 */
	private ItemsChar getItemCharByGlobalCharIdAndLocationId(EntityManager em, String locationId,
			String globalCourseId) {
		ItemsChar char1 = null;
		try {
			String queryString = "select s from ItemsChar s where s.globalItemCharId =? and s.locationsId=? and s.status !='D' ";
			TypedQuery<ItemsChar> query = em.createQuery(queryString, ItemsChar.class).setParameter(1, globalCourseId)
					.setParameter(2, locationId);
			char1 = query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		if (char1 == null) {
			ItemsChar oldItemChar = (ItemsChar) new CommonMethods().getObjectById("ItemsChar", em, ItemsChar.class,
					globalCourseId);
			if (oldItemChar != null) {
				char1 = new ItemsChar().getItemChar(oldItemChar);
				char1.setGlobalItemCharId(globalCourseId);
				char1.setLocationsId(locationId);
				if(char1.getId()==null){
					char1.setId(new StoreForwardUtility().generateUUID());
				}
				char1 = em.merge(char1);
			}

		}
		return char1;
	}

	/**
	 * Delete multiple location items char.
	 *
	 * @param em
	 *            the em
	 * @param itemsChar
	 *            the items char
	 * @param itemCharPacket
	 *            the item char packet
	 * @param request
	 *            the request
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */
	ItemsChar deleteMultipleLocationItemsChar(EntityManager em, ItemsChar itemsChar, ItemsCharPacket itemCharPacket,
			HttpServletRequest request) throws Exception {
		// delete baselocation
		itemsChar = deleteItemsChar(itemsChar, request, em);

		// get all sublocations
		List<ItemsChar> itemChars = getAllItemsCharByGlobalItemsCharId(itemsChar.getId(), em);
		// delete sublocation
		for (ItemsChar itemsChar2 : itemChars) {
			deleteItemsChar(itemsChar2, request, em);

		}
		return itemsChar;
	}

	/**
	 * Gets the all items char by global items char id.
	 *
	 * @param globalItemsCharId
	 *            the global items char id
	 * @param em
	 *            the em
	 * @return the all items char by global items char id
	 */
	List<ItemsChar> getAllItemsCharByGlobalItemsCharId(String globalItemsCharId, EntityManager em) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsChar> criteria = builder.createQuery(ItemsChar.class);
			Root<ItemsChar> printerRoot = criteria.from(ItemsChar.class);
			TypedQuery<ItemsChar> query = em.createQuery(criteria.select(printerRoot)
					.where(builder.equal(printerRoot.get(ItemsChar_.globalItemCharId), globalItemsCharId)));
			return query.getResultList();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line

			logger.severe("No Result found");
		}
		return null;
	}

	/**
	 * Delete multiple location printer.
	 *
	 * @param em
	 *            the em
	 * @param printer
	 *            the printer
	 * @param printerPacket
	 *            the printer packet
	 * @param request
	 *            the request
	 * @return the printer
	 * @throws Exception
	 *             the exception
	 */
	Printer deleteMultipleLocationPrinter(EntityManager em, Printer printer, PrinterPacket printerPacket,
			HttpServletRequest request) throws Exception {
		// delete baselocation
		printer = deletePrinter(printer, request, em);

		// get all sublocations
		List<Printer> printers = getAllPrinterByGlobalPrinterId(printer.getId(), em);
		// delete sublocation
		for (Printer printer2 : printers) {
			deletePrinter(printer2, request, em);
		}
		return printer;
	}

	/**
	 * Adds the multiple location discount.
	 *
	 * @param em
	 *            the em
	 * @param discount
	 *            the discount
	 * @param discountPacket
	 *            the discount packet
	 * @param request
	 *            the request
	 * @return the discount
	 * @throws Exception
	 *             the exception
	 */
	Discount addMultipleLocationDiscount(EntityManager em, Discount discount, DiscountPacket discountPacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = discountPacket.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (discount != null && discountPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
//			discount.setLocationsId(baseLocation.getId());

			Discount globalDiscount = updateDiscounts(discount, request, em, null, discountPacket);
			discountPacket.setDiscount(globalDiscount);
			discountPacket.setLocationsListId("");
			

			DiscountsType discountsTypeGlobal = (DiscountsType) new CommonMethods().getObjectById("DiscountsType", em,
					DiscountsType.class, globalDiscount.getDiscountsTypeId());
			em.getTransaction().commit();
			em.getTransaction().begin();
			// now add/update child location
			for (String locationId : locationIds) {
				
				if (locationId.length()>0 && !locationId.equals((baseLocation.getId()))) {
					String json = new StoreForwardUtility().returnJsonPacket(discountPacket, "DiscountPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
							Integer.parseInt(discountPacket.getMerchantId()));
					Discount local = new Discount().getDiscount(discount);
					if (discountsTypeGlobal != null) {
						DiscountsType type = getDiscountTypeByNameAndLocationId(em, locationId,
								discountsTypeGlobal.getDiscountsType());
						local.setDiscountsTypeId(type.getId());
					}
					local.setGlobalId(globalDiscount.getId());
					local.setLocationsId(locationId);
					local = updateDiscounts(local, request, em, null, discountPacket);
					discountPacket.setDiscount(local);
					discountPacket.setLocalServerURL(0);
					;
					String json2 = new StoreForwardUtility().returnJsonPacket(discountPacket, "DiscountPacket",
							request);
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request, discount.getLocationsId(),
							Integer.parseInt(discountPacket.getMerchantId()));
				}
			}
		}
		return discount;
	}

	/**
	 * Update multiple location discount.
	 *
	 * @param em
	 *            the em
	 * @param discount
	 *            the discount
	 * @param itemCharPacket
	 *            the item char packet
	 * @param request
	 *            the request
	 * @return the discount
	 * @throws Exception
	 *             the exception
	 */
	Discount updateMultipleLocationDiscount(EntityManager em, Discount discount, DiscountPacket discountPacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		if (discountPacket.getLocationId().trim().length() > 0) {
			locationIds = discountPacket.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (discount != null && discountPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item

			Discount globalDiscount = updateDiscounts(discount, request, em, null, discountPacket);
			discountPacket.setDiscount(globalDiscount);
			discountPacket.setLocationsListId("");
			
			DiscountsType discountsTypeGlobal = (DiscountsType) new CommonMethods().getObjectById("DiscountsType", em,
					DiscountsType.class, globalDiscount.getDiscountsTypeId());
			// now add/update child location
			if (locationIds != null) {
				for (String  locationId: locationIds) {
					
					if (locationId.length()>0 && !locationId.equals((baseLocation.getId()))) {
						String json = new StoreForwardUtility().returnJsonPacket(discountPacket, "DiscountPacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
								Integer.parseInt(discountPacket.getMerchantId()));
						Discount local = new Discount().getDiscount(discount);
						Discount discounts = getDiscountByGlobalIdAndLocationId(em, locationId, globalDiscount.getId());

						if (discountsTypeGlobal != null) {
							DiscountsType type = getDiscountTypeByNameAndLocationId(em, locationId,
									discountsTypeGlobal.getDiscountsType());
							local.setDiscountsTypeId(type.getId());
						}
						if (discounts != null && discount.getId() != null) {
							local.setGlobalId(globalDiscount.getId());
							local.setId(discounts.getId());
						} else {
							local.setGlobalId(globalDiscount.getId());
						}

						local.setLocationsId(locationId);
						local = updateDiscounts(local, request, em, null, discountPacket);
						discountPacket.setDiscount(local);
						discountPacket.setLocalServerURL(0);
						;
						String json2 = new StoreForwardUtility().returnJsonPacket(discountPacket, "DiscountPacket",
								request);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request,
								discount.getLocationsId(), Integer.parseInt(discountPacket.getMerchantId()));
					}
				}
			}
		}
		return discount;
	}

	/**
	 * Gets the discount by global id and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalId
	 *            the global id
	 * @return the discount by global id and location id
	 */
	private Discount getDiscountByGlobalIdAndLocationId(EntityManager em, String locationId, String globalId) {
		try {
			String queryString = "select s from Discount s where s.globalId =? and s.locationsId=? and s.status !='D' ";
			TypedQuery<Discount> query = em.createQuery(queryString, Discount.class).setParameter(1, globalId)
					.setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the discount type by name and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the discount type by name and location id
	 */
	private DiscountsType getDiscountTypeByNameAndLocationId(EntityManager em, String locationId, String name) {
		try {
			
			String queryString = "select s from DiscountsType s where s.discountsType =? and s.locationsId=? and s.status !='D' ";
			TypedQuery<DiscountsType> query = em.createQuery(queryString, DiscountsType.class).setParameter(1, name)
					.setParameter(2, locationId);
			
			return query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the items attribute type by name and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the items attribute type by name and location id
	 */
	private ItemsAttributeType getItemsAttributeTypeByNameAndLocationId(EntityManager em, String locationId,
			String name) {
		try {
			String queryString = "select s from ItemsAttributeType s where s.name =? and s.locationsId=? and s.status !='D' ";
			TypedQuery<ItemsAttributeType> query = em.createQuery(queryString, ItemsAttributeType.class)
					.setParameter(1, name).setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the items attribute by global id and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalId
	 *            the global id
	 * @return the items attribute by global id and location id
	 */
	private ItemsAttribute getItemsAttributeByGlobalIdAndLocationId(EntityManager em, String locationId,
			String globalId) {
		try {
			String queryString = "select s from ItemsAttribute s where s.globalId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<ItemsAttribute> query = em.createQuery(queryString, ItemsAttribute.class)
					.setParameter(1, globalId).setParameter(2, locationId);

			return query.getSingleResult();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the category by global printer id and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalCategoryId
	 *            the global category id
	 * @return the category by global printer id and location id
	 */
	private Category getCategoryByGlobalPrinterIdAndLocationId(EntityManager em, String locationId,
			int globalCategoryId) {
		try {
			String queryString = "select s from Category s where s.globalCategoryId =? and s.locationsId=? and s.status !='D' ";
			TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, globalCategoryId)
					.setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {

			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the sales tax by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @return the sales tax by location id
	 * @throws Exception
	 *             the exception
	 */
	public String getSalesTaxByLocationId(String locationId, int startIndex, int endIndex, EntityManager em,
			HttpServletRequest httpRequest) throws Exception {

		List<SalesTaxDisplayPacket> ans = new ArrayList<SalesTaxDisplayPacket>();
		String sql = " select s.id,s.tax_name,s.display_name,s.rate,s.is_item_specific  FROM  sales_tax s "
				+ " where s.locations_id=? and s.status !='D' limit " + startIndex + "," + endIndex;

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
		for (Object[] objRow : resultList) {
			SalesTaxDisplayPacket detailDisplayPacket = new SalesTaxDisplayPacket();
			detailDisplayPacket.setId((String) objRow[0]);
			detailDisplayPacket.setName((String) objRow[1]);
			detailDisplayPacket.setDisplayName((String) objRow[2]);
			detailDisplayPacket.setRate((BigDecimal) objRow[3]);
			detailDisplayPacket.setIsItemSpecific((int) objRow[4]);
			ans.add(detailDisplayPacket);
		}

		for (SalesTaxDisplayPacket displayPacket : ans) {
			List<OrderSourceGroupToSalesTax> list = getOrderSourceGroupToSalesTax(em, displayPacket.getId());

			String id = "";
			String name = "";

			for (int i = 0; i < list.size(); i++) {
				OrderSourceGroupToSalesTax list2 = list.get(i);
				OrderSourceGroup group = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,
						OrderSourceGroup.class, list2.getSourceGroupId());
				if (group != null) {
					if (i == (list.size() - 1)) {
						name += group.getDisplayName();
						id += list2.getId();
					} else {
						name += group.getDisplayName() + ",";
						id += list2.getId() + ",";
					}
				}
			}

			if ((displayPacket.getIsItemSpecific() == 0)) {
				displayPacket.setOrderSourceGroupToSalesTaxesId(id);
				displayPacket.setOrderSourceGroupToSalesTaxesName(name);
			}

		}
		return new JSONUtility(httpRequest).convertToJsonString(ans);
	}

	/**
	 * Gets the order source group to sales tax.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the order source group to sales tax
	 */
	private List<OrderSourceGroupToSalesTax> getOrderSourceGroupToSalesTax(EntityManager em, String id) {
		try {
			String queryString = "select p from OrderSourceGroupToSalesTax p where p.taxId=? and p.status != 'D' ";
			TypedQuery<OrderSourceGroupToSalesTax> query = em.createQuery(queryString, OrderSourceGroupToSalesTax.class)
					.setParameter(1, id);
			return query.getResultList();
		} catch (NoResultException e) {

			logger.severe("No result found for categoryid" + id);
		}
		return null;
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
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @return the discounts by location id
	 * @throws Exception
	 *             the exception
	 */
	public String getDiscountsByLocationId(String locationId, int startIndex, int endIndex, String discountTypeId,
			String displayName,

			EntityManager em, HttpServletRequest httpRequest) throws Exception {

		List<DiscountsDisplayPacket> ans = new ArrayList<DiscountsDisplayPacket>();
		String sql = " select d.id, d.name, d.display_name,d.description,d.discounts_value ,dt.discounts_type "
				+ " from  discounts d join discounts_type dt on dt.id=d.discounts_type_id  "
				+ " where d.locations_id=?  " ;
		if(displayName!=null){
			sql	+= " and d.display_name like '%" + displayName + "%' " ; 
		}
		if (discountTypeId != null && !discountTypeId.equals("null") && discountTypeId.length()>0) {
			sql += " and d.discounts_type_id= '" + discountTypeId+"'";
		}
		
		sql += "  and d.status !='D' limit " + startIndex + "," + endIndex;

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
		for (Object[] objRow : resultList) {
			DiscountsDisplayPacket detailDisplayPacket = new DiscountsDisplayPacket();
			detailDisplayPacket.setId((String) objRow[0]);
			detailDisplayPacket.setName((String) objRow[1]);
			detailDisplayPacket.setDisplayName((String) objRow[2]);
			detailDisplayPacket.setDescription((String) objRow[3]);
			detailDisplayPacket.setValue((BigDecimal) objRow[4]);
			detailDisplayPacket.setDiscountTypeName((String) objRow[5]);
			ans.add(detailDisplayPacket);
		}

		return new JSONUtility(httpRequest).convertToJsonString(ans);
	}

	/**
	 * Delete multiple location discount.
	 *
	 * @param em
	 *            the em
	 * @param discount
	 *            the discount
	 * @param discountPacket
	 *            the discount packet
	 * @param request
	 *            the request
	 * @return the discount
	 * @throws Exception
	 *             the exception
	 */
	Discount deleteMultipleLocationDiscount(EntityManager em, Discount discount, DiscountPacket discountPacket,
			HttpServletRequest request) throws Exception {
		// delete baselocation
		discount = deleteDiscounts(discount, request, em, null);
		// get all sublocations
		List<Discount> discounts = getAllDiscountByGlobalDiscountId(discount.getId(), em);
		// delete sublocation
		for (Discount discount2 : discounts) {
			deleteDiscounts(discount2, request, em, null);
		}
		return discount;
	}

	/**
	 * Gets the all discount by global discount id.
	 *
	 * @param globalId
	 *            the global id
	 * @param em
	 *            the em
	 * @return the all discount by global discount id
	 */
	List<Discount> getAllDiscountByGlobalDiscountId(String globalId, EntityManager em) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
			Root<Discount> printerRoot = criteria.from(Discount.class);
			TypedQuery<Discount> query = em.createQuery(
					criteria.select(printerRoot).where(builder.equal(printerRoot.get(Discount_.globalId), globalId)));
			return query.getResultList();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			logger.severe("No Result found");
		}
		return null;
	}

	/**
	 * Adds the multiple location items attribute.
	 *
	 * @param em
	 *            the em
	 * @param temsAttribute
	 *            the tems attribute
	 * @param itemsAttributePacket
	 *            the items attribute packet
	 * @param request
	 *            the request
	 * @return the items attribute
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttribute addMultipleLocationItemsAttribute(EntityManager em, ItemsAttribute temsAttribute,
			ItemsAttributePacket itemsAttributePacket, HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		if (itemsAttributePacket.getLocationsListId()!=null && itemsAttributePacket.getLocationsListId().trim().length() > 0) {
			locationIds = itemsAttributePacket.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (temsAttribute != null && itemsAttributePacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
			ItemsAttribute global = updateItemsAttribute(temsAttribute, request, em);
			itemsAttributePacket.setItemsAttribute(global);
			itemsAttributePacket.setLocationsListId("");
			
			em.getTransaction().commit();
			em.getTransaction().begin();
			// now add/update child location
			if (locationIds != null) {
				for (String locationId : locationIds) {
					String  locationsId= locationId;
					
					if (!locationsId.equals(baseLocation.getId())) {
						String json = new StoreForwardUtility().returnJsonPacket(itemsAttributePacket, "ItemsAttributePacket",
								request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
								Integer.parseInt(itemsAttributePacket.getMerchantId()));
						ItemsAttribute local = new ItemsAttribute().getItemsAttribute(temsAttribute);

						ItemsAttribute itemLocal = getItemsAttributeByGlobalIdAndLocationId(em, locationsId,
								global.getId());
						String localItemId = null;
						if (itemLocal != null && temsAttribute.getId() != null) {
							local.setGlobalId(global.getId());
							localItemId = itemLocal.getId();
							local.setId(localItemId);
						} else {
							local.setGlobalId(global.getId());
						}

						local.setLocationsId(locationId);

						local.setItemsAttributeTypeToItemsAttributes(getLocalItemsAttributeTypeToItemsAttributeList(
								temsAttribute.getItemsAttributeTypeToItemsAttributes(), locationsId, localItemId, em));
						if (local.getItemsAttributeTypeToItemsAttributes() != null
								&& local.getItemsAttributeTypeToItemsAttributes().size() == 0) {
							local.setItemsAttributeTypeToItemsAttributes(null);
						}
						local.setItemsCharToItemsAttributes(getLocalItemsCharToItemsAttributeList(
								temsAttribute.getItemsCharToItemsAttributes(), locationsId, localItemId, em, true));
						if (local.getItemsCharToItemsAttributes() != null
								&& local.getItemsCharToItemsAttributes().size() == 0) {
							local.setItemsCharToItemsAttributes(null);
						}
						if (temsAttribute.getNutritionsToItemsAttributes() != null
								&& temsAttribute.getNutritionsToItemsAttributes().size() > 0) {
							local.setNutritionsToItemsAttributes(getLocalItemsAttributeToNutritionsList(
									temsAttribute.getNutritionsToItemsAttributes(), locationsId, localItemId, em,
									true));
						}

						if (local.getNutritionsToItemsAttributes() != null
								&& local.getNutritionsToItemsAttributes().size() == 0) {
							local.setNutritionsToItemsAttributes(null);
						}
						local = updateItemsAttribute(local, request, em);
						itemsAttributePacket.setItemsAttribute(local);
						itemsAttributePacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(itemsAttributePacket,
								"ItemsAttributePacket", request);
						
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request,
								locationsId,
								Integer.parseInt(itemsAttributePacket.getMerchantId()));

					}
				}
			}
		}
		return temsAttribute;
	}

	/**
	 * Update multiple location items attribute.
	 *
	 * @param em
	 *            the em
	 * @param temsAttribute
	 *            the tems attribute
	 * @param itemsAttributePacket
	 *            the items attribute packet
	 * @param request
	 *            the request
	 * @return the items attribute
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttribute updateMultipleLocationItemsAttribute(EntityManager em, ItemsAttribute temsAttribute,
			ItemsAttributePacket itemsAttributePacket, HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		if (itemsAttributePacket.getLocationId().trim().length() > 0) {
			locationIds = itemsAttributePacket.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (temsAttribute != null && itemsAttributePacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
			Set<ItemsCharToItemsAttribute> attributes2 = new LinkedHashSet<ItemsCharToItemsAttribute>();
			if (temsAttribute.getItemsCharToItemsAttributes() != null
					&& temsAttribute.getItemsCharToItemsAttributes().size() > 0) {

				for (ItemsCharToItemsAttribute attribute : temsAttribute.getItemsCharToItemsAttributes()) {
					if (attribute.getItemsCharId() != null) {
						// make sure always have item attribute id
						// attribute.setItemsAttributeId(temsAttribute.getId());
						attributes2.add(attribute);
					}
				}
			}
			temsAttribute.setItemsCharToItemsAttributes(attributes2);
			ItemsAttribute global = updateItemsAttribute(temsAttribute, request, em);
			itemsAttributePacket.setItemsAttribute(global);
			itemsAttributePacket.setLocationsListId("");
			
			em.getTransaction().commit();
			em.getTransaction().begin();
			// now add/update child location
			if (locationIds != null) {
				for (String locationId : locationIds) {
					String locationsId = locationId;
					
					if (locationId.length()>0 && !locationId.equals((baseLocation.getId()))) {
						String json = new StoreForwardUtility().returnJsonPacket(itemsAttributePacket, "ItemsAttributePacket",
								request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request,baseLocation.getId(),
								Integer.parseInt(itemsAttributePacket.getMerchantId()));
						ItemsAttribute local = new ItemsAttribute().getItemsAttribute(temsAttribute);

						ItemsAttribute itemLocal = getItemsAttributeByGlobalIdAndLocationId(em, locationsId,
								global.getId());
						String localItemId = null;
						if (itemLocal != null && temsAttribute.getId() != null) {
							local.setGlobalId(global.getId());
							localItemId = itemLocal.getId();
							local.setId(localItemId);
							local.setSortSequence(itemLocal.getSortSequence());
						} else {
							local.setGlobalId(global.getId());
						}

						local.setLocationsId(locationId);

						local.setItemsAttributeTypeToItemsAttributes(getLocalItemsAttributeTypeToItemsAttributeList(
								temsAttribute.getItemsAttributeTypeToItemsAttributes(), locationsId, localItemId, em));
						if (local.getItemsAttributeTypeToItemsAttributes() != null
								&& local.getItemsAttributeTypeToItemsAttributes().size() == 0) {
							local.setItemsAttributeTypeToItemsAttributes(null);
						}
						local.setItemsCharToItemsAttributes(getLocalItemsCharToItemsAttributeList(
								temsAttribute.getItemsCharToItemsAttributes(), locationsId, localItemId, em, false));
						if (local.getItemsCharToItemsAttributes() != null
								&& local.getItemsCharToItemsAttributes().size() == 0) {
							local.setItemsCharToItemsAttributes(null);
						}
						Set<ItemsCharToItemsAttribute> attributes = new LinkedHashSet<ItemsCharToItemsAttribute>();
						if (local.getItemsCharToItemsAttributes() != null
								&& local.getItemsCharToItemsAttributes().size() > 0) {

							for (ItemsCharToItemsAttribute attribute : local.getItemsCharToItemsAttributes()) {
								if (attribute.getItemsCharId() != null) {
									attributes.add(attribute);
								}
							}
						}
						local.setItemsCharToItemsAttributes(attributes);
						if (temsAttribute.getNutritionsToItemsAttributes() != null
								&& temsAttribute.getNutritionsToItemsAttributes().size() > 0) {
							local.setNutritionsToItemsAttributes(getLocalItemsAttributeToNutritionsList(
									temsAttribute.getNutritionsToItemsAttributes(), locationsId, localItemId, em,
									false));
						}

						if (local.getItemsCharToItemsAttributes() != null
								&& local.getItemsCharToItemsAttributes().size() == 0) {
							local.setItemsCharToItemsAttributes(null);
						}
						Set<ItemsAttributeToNutritions> nutAttributes = new LinkedHashSet<ItemsAttributeToNutritions>();
						if (local.getNutritionsToItemsAttributes() != null
								&& local.getNutritionsToItemsAttributes().size() > 0) {

							for (ItemsAttributeToNutritions attribute : local.getNutritionsToItemsAttributes()) {
								if (attribute.getNutritionsId() != null) {
									nutAttributes.add(attribute);
								}
							}
						}
						if (nutAttributes != null && nutAttributes.size() > 0) {

							local.setNutritionsToItemsAttributes(nutAttributes);
						}

						local = updateItemsAttribute(local, request, em);
						itemsAttributePacket.setItemsAttribute(local);
						itemsAttributePacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(itemsAttributePacket,
								"ItemsAttributePacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request,
								locationId,
								Integer.parseInt(itemsAttributePacket.getMerchantId()));
					}
				}
			}
		}
		return temsAttribute;
	}

	/**
	 * Delete multiple location items attribute.
	 *
	 * @param em
	 *            the em
	 * @param itemAttribute
	 *            the item attribute
	 * @param printerPacket
	 *            the printer packet
	 * @param request
	 *            the request
	 * @return the items attribute
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttribute deleteMultipleLocationItemsAttribute(EntityManager em, ItemsAttribute itemAttribute,
			ItemsAttributePacket itemsAttributePacket, HttpServletRequest request) throws Exception {
		// delete baselocation
		itemAttribute = deleteItemsAttribute(itemAttribute, request, em);

		// get all sublocations
		List<ItemsAttribute> itemsAttributes = getAllItemsAttributeByGlobalId(itemAttribute.getId(), em);
		// delete sublocation
		for (ItemsAttribute itemAttribute2 : itemsAttributes) {
			itemAttribute2 = deleteItemsAttribute(itemAttribute2, request, em);
		}
		return itemAttribute;
	}

	/**
	 * Gets the all items attribute by global id.
	 *
	 * @param globalId
	 *            the global id
	 * @param em
	 *            the em
	 * @return the all items attribute by global id
	 */
	List<ItemsAttribute> getAllItemsAttributeByGlobalId(String globalId, EntityManager em) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsAttribute> criteria = builder.createQuery(ItemsAttribute.class);
			Root<ItemsAttribute> printerRoot = criteria.from(ItemsAttribute.class);
			TypedQuery<ItemsAttribute> query = em.createQuery(criteria.select(printerRoot)
					.where(builder.equal(printerRoot.get(ItemsAttribute_.globalId), globalId)));
			return query.getResultList();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line

			logger.severe("No Result found");
		}
		return null;
	}

	/**
	 * Delete multiple location items attribute type.
	 *
	 * @param em
	 *            the em
	 * @param itemAttributeType
	 *            the item attribute type
	 * @param itemPacket
	 *            the item packet
	 * @param request
	 *            the request
	 * @return the items attribute type
	 * @throws Exception
	 *             the exception
	 */
	ItemsAttributeType deleteMultipleLocationItemsAttributeType(EntityManager em, ItemsAttributeType itemAttributeType,
			ItemsAttributeTypePacket ItemsAttributeTypePacket, HttpServletRequest request) throws Exception {
		// delete baselocation
		itemAttributeType = deleteItemsAttributeType(itemAttributeType, request, em);

		// get all sublocations
		List<ItemsAttributeType> itemsAttributes = getAllItemsAttributeTypeByGlobalId(itemAttributeType.getId(), em);
		// delete sublocation
		for (ItemsAttributeType itemAttribute2 : itemsAttributes) {
			itemAttribute2 = deleteItemsAttributeType(itemAttribute2, request, em);
		}
		return itemAttributeType;
	}

	/**
	 * Gets the all items attribute type by global id.
	 *
	 * @param globalId
	 *            the global id
	 * @param em
	 *            the em
	 * @return the all items attribute type by global id
	 */
	List<ItemsAttributeType> getAllItemsAttributeTypeByGlobalId(String globalId, EntityManager em) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsAttributeType> criteria = builder.createQuery(ItemsAttributeType.class);
			Root<ItemsAttributeType> printerRoot = criteria.from(ItemsAttributeType.class);
			TypedQuery<ItemsAttributeType> query = em.createQuery(criteria.select(printerRoot)
					.where(builder.equal(printerRoot.get(ItemsAttributeType_.globalItemAttributeTypeId), globalId)));
			return query.getResultList();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			logger.severe("No Result found");
		}
		return null;
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
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @return the item attributes type by location id
	 * @throws Exception
	 *             the exception
	 */
	public String getItemAttributesTypeByLocationId(String locationId, int startIndex, int endIndex, EntityManager em,
			HttpServletRequest httpRequest) throws Exception {

		List<ItemAttributeDisplayTypePacket> ans = new ArrayList<ItemAttributeDisplayTypePacket>();
		String sql = " select id, name, display_name,description,is_required from items_attribute_type "
				+ " where locations_id=? and status !='D' limit " + startIndex + "," + endIndex;

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
		for (Object[] objRow : resultList) {
			ItemAttributeDisplayTypePacket detailPacket = new ItemAttributeDisplayTypePacket();
			detailPacket.setId((String) objRow[0]);
			detailPacket.setName((String) objRow[1]);
			detailPacket.setDisplayName((String) objRow[2]);
			detailPacket.setDescription((String) objRow[3]);
			if (((boolean) objRow[4]) == true) {
				detailPacket.setModifierTypeName("Mandatory");
			} else {
				detailPacket.setModifierTypeName("Optional");
			}

			ans.add(detailPacket);
		}

		return new JSONUtility(httpRequest).convertToJsonString(ans);
	}

	/**
	 * Gets the item attributes by location id.
	 *
	 * @param displayName
	 *            the display name
	 * @param itemAttributeTypeId
	 *            the item attribute type id
	 * @param locationId
	 *            the location id
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @return the item attributes by location id
	 * @throws Exception
	 *             the exception
	 */
	public String getItemAttributesByLocationId(String displayName, String itemAttributeTypeId, String locationId,
			int startIndex, int endIndex, EntityManager em, HttpServletRequest httpRequest) throws Exception {

		List<ItemAttributeDisplayPacket> ans = new ArrayList<ItemAttributeDisplayPacket>();
		String sql = "  select ia.id, ia.name, ia.display_name,ia.short_name,ia.selling_price,ia.image_name "
				+ " from items_attribute ia left join items_attribute_type_to_items_attribute iatt "
				+ " on iatt.items_attribute_id=ia.id where  ia.display_name like '%" + displayName + "%' " ;
		
		if (itemAttributeTypeId !=null && !itemAttributeTypeId.equals("null") && itemAttributeTypeId.length()>0  && !itemAttributeTypeId.equals("0")) {
			sql += " and iatt.items_attribute_type_id =  '" + itemAttributeTypeId +"'";
		}
		sql += "  and ia.locations_id=? and ia.status !='D' limit " + startIndex + "," + endIndex;

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
		for (Object[] objRow : resultList) {
			ItemAttributeDisplayPacket detailPacket = new ItemAttributeDisplayPacket();
			detailPacket.setId((String) objRow[0]);
			detailPacket.setName((String) objRow[1]);
			detailPacket.setDisplayName((String) objRow[2]);
			detailPacket.setShortName((String) objRow[3]);
			detailPacket.setPriceSelling((BigDecimal) objRow[4]);
			detailPacket.setImageName((String) objRow[5]);

			ans.add(detailPacket);
		}

		return new JSONUtility(httpRequest).convertToJsonString(ans);
	}

	public String getItemAttributeByLocationId(String itemAttributeTypeId, String locationId, EntityManager em,
			HttpServletRequest httpRequest) throws Exception {

		List<ItemAttributeDisplayPacket> ans = new ArrayList<ItemAttributeDisplayPacket>();

		String sql = "  select ia.id, ia.name ia_name, ia.display_name ia_display_name,ia.short_name,ia.selling_price,ia.image_name, iat.display_name, ia.availability   "
				+ "" + " from items_attribute ia left join items_attribute_type_to_items_attribute iatt "
				+ " on iatt.items_attribute_id=ia.id  "
				+ " join items_attribute_type iat on iat.id=iatt.items_attribute_type_id " + "  where "
				+ "   ia.locations_id=? and ia.status not in ('D','I') ";
		if (itemAttributeTypeId!=null) {
			sql += " and iat.id ='" + itemAttributeTypeId +"'";
		}

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
		for (Object[] objRow : resultList) {
			ItemAttributeDisplayPacket detailPacket = new ItemAttributeDisplayPacket();
			detailPacket.setId((String) objRow[0]);
			detailPacket.setName((String) objRow[1]);
			detailPacket.setDisplayName((String) objRow[2]);
			detailPacket.setShortName((String) objRow[3]);
			detailPacket.setPriceSelling((BigDecimal) objRow[4]);
			detailPacket.setImageName((String) objRow[5]);
			detailPacket.setItemAttributeTypedisplayName((String) objRow[6]);
			detailPacket.setAvailability((Integer) objRow[7]);

			ans.add(detailPacket);
		}

		return new JSONUtility(httpRequest).convertToJsonString(ans);
	}

	/**
	 * Gets the local items attribute type to items attribute list.
	 *
	 * @param globalList
	 *            the global list
	 * @param locationId
	 *            the location id
	 * @param itemAttributeId
	 *            the item attribute id
	 * @param em
	 *            the em
	 * @return the local items attribute type to items attribute list
	 */
	Set<ItemsAttributeTypeToItemsAttribute> getLocalItemsAttributeTypeToItemsAttributeList(
			Set<ItemsAttributeTypeToItemsAttribute> globalList, String locationId, String itemAttributeId,
			EntityManager em) {
		Set<ItemsAttributeTypeToItemsAttribute> localList = new LinkedHashSet<ItemsAttributeTypeToItemsAttribute>();
		for (ItemsAttributeTypeToItemsAttribute attribute : globalList) {
			ItemsAttributeType attributeType = getItemAttributeTypeByGlobalItemAttributeTypeIdAndLocationId(em,
					locationId, attribute.getItemsAttributeTypeId());
			if (attributeType != null) {
				ItemsAttributeTypeToItemsAttribute toItemsAttribute = new ItemsAttributeTypeToItemsAttribute();
				toItemsAttribute.setCreatedBy(attribute.getCreatedBy());
				toItemsAttribute.setUpdatedBy(attribute.getUpdatedBy());
				toItemsAttribute.setStatus(attribute.getStatus());
				toItemsAttribute.setItemsAttributeTypeId(attributeType.getId());
				if (itemAttributeId != null) {
					toItemsAttribute.setItemsAttributeId(itemAttributeId);
					ItemsAttributeTypeToItemsAttribute attributeTypeToItemsAttribute = getItemsAttributeTypeToItemsAttributeByGlobalItemAttributeTypeIdItemAttributeIdAndLocationId(
							em, toItemsAttribute.getItemsAttributeId());
					if (attributeTypeToItemsAttribute != null)
						toItemsAttribute.setId(attributeTypeToItemsAttribute.getId());
				}

				localList.add(toItemsAttribute);
			}
		}
		return localList;
	}

	/**
	 * Gets the local items char to items attribute list.
	 *
	 * @param globalList
	 *            the global list
	 * @param locationId
	 *            the location id
	 * @param itemAttributeId
	 *            the item attribute id
	 * @param em
	 *            the em
	 * @param isAdd
	 *            the is add
	 * @return the local items char to items attribute list
	 */
	Set<ItemsCharToItemsAttribute> getLocalItemsCharToItemsAttributeList(Set<ItemsCharToItemsAttribute> globalList,
			String locationId, String itemAttributeId, EntityManager em, boolean isAdd) {
		Set<ItemsCharToItemsAttribute> localList = new LinkedHashSet<ItemsCharToItemsAttribute>();
		for (ItemsCharToItemsAttribute attribute : globalList) {
			ItemsChar itemChars = getItemCharByGlobalCharIdAndLocationId(em, locationId, attribute.getItemsCharId());
			if (itemChars != null) {
				ItemsCharToItemsAttribute attribute2 = new ItemsCharToItemsAttribute();
				attribute2.setCreatedBy(attribute.getCreatedBy());
				attribute2.setUpdatedBy(attribute.getUpdatedBy());
				attribute2.setStatus(attribute.getStatus());
				attribute2.setItemsCharId(itemChars.getId());
				if (itemAttributeId != null) {

					attribute2.setItemsAttributeId(itemAttributeId);
					ItemsCharToItemsAttribute itemsCharToItemsAttribute = getItemsCharToItemsAttributeByGlobalItemAttributeTypeIdItemAttributeIdAndLocationId(
							em, locationId, attribute2.getItemsCharId(), attribute2.getItemsAttributeId());
					if (itemsCharToItemsAttribute != null) {
						attribute2.setId(itemsCharToItemsAttribute.getId());
					}

				}

				if (isAdd) {
					if (attribute2 != null && attribute2.getItemsCharId() != null) {
						localList.add(attribute2);
					}
				} else {
					if (attribute2 != null && attribute2.getItemsCharId() != null
							&& attribute2.getItemsCharId() != null) {
						localList.add(attribute2);
					}
				}

			}
		}
		return localList;
	}

	/**
	 * Hold shift slot for client.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param shiftSlotId
	 *            the shift slot id
	 * @param sessionId
	 *            the session id
	 * @param updatedBy
	 *            the updated by
	 * @param schemaName
	 *            the schema name
	 * @return the hold shift slot response
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public HoldShiftSlotResponse holdShiftSlotForClient(HttpServletRequest httpRequest, EntityManager em,
			int shiftSlotId, String sessionId, String updatedBy, String schemaName) throws NirvanaXPException {
		HoldShiftSlotResponse holdShiftSlotResponse = null;
		ShiftSlots shiftSlot = null;
		try {

			shiftSlot = em.find(ShiftSlots.class, shiftSlotId);
		} catch (Exception e1) {
			logger.severe(httpRequest, "No Slot found for shift slot id: " + shiftSlotId);
		}

		if (shiftSlot != null) {

			if (shiftSlot.getStatus().equals("A")) {

				// get shift schedule for the slot, so that we would
				// know
				// max shift allowed for the slot
				ShiftSchedule shiftSchedule;

				shiftSchedule = (ShiftSchedule) new CommonMethods().getObjectById("ShiftSchedule", em,
						ShiftSchedule.class, shiftSlot.getShiftScheduleId());

				if (shiftSchedule != null && shiftSchedule.getStatus().equals("A")) {
					int maxShiftAllowedinslot = shiftSchedule.getMaxOrderAllowed();
					sessionClearTimeInMinutes = shiftSchedule.getHoldTime();
					int currentOrderMadeInSlot = shiftSlot.getCurrentOrderInSlot();
					int currentShiftSlotHoldedByClient = shiftSlot.getCurrentlyHoldedClient();

					// check if shift slot is still available or
					// not
					if (maxShiftAllowedinslot > currentOrderMadeInSlot + currentShiftSlotHoldedByClient) {
						// shift slot is allowed for hold by the
						// client

						// increment the slot hold client count in
						// shift
						// slot table
						shiftSlot.setCurrentlyHoldedClient(currentShiftSlotHoldedByClient + 1);

						currentShiftSlotHoldedByClient = currentShiftSlotHoldedByClient + 1;
						// check if now the slot is still available or
						// it must
						// be put on hold as now a new client has
						// requested for
						// hold
						if (maxShiftAllowedinslot <= currentOrderMadeInSlot + currentShiftSlotHoldedByClient) {
							if (shiftSlot.getStatus().equals("D") == false
									&& shiftSlot.getStatus().equals("I") == false) {
								shiftSlot.setStatus("H");
							}

						}
						shiftSlot.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						shiftSlot.setUpdatedBy(updatedBy);
						// save new slot entry in database
						em.merge(shiftSlot);

						// now add this client so that he can do
						// Reservation
						ShiftSlotActiveClientInfo shiftSlotActiveClientInfo = new ShiftSlotActiveClientInfo(sessionId,
								shiftSlot.getSlotTime(), shiftSlotId);
						shiftSlotActiveClientInfo.setSessionId(sessionId);
						shiftSlotActiveClientInfo.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						shiftSlotActiveClientInfo.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						shiftSlotActiveClientInfo.setSlotHoldStartTime(new Date(new TimezoneTime().getGMTTimeInMilis()));
						shiftSlotActiveClientInfo.setCreatedBy(updatedBy);
						shiftSlotActiveClientInfo.setUpdatedBy(updatedBy);

						// clean up single trasaction :- By AP :- 2015-12-29

						em.persist(shiftSlotActiveClientInfo);

						// start a thread that will remove this client
						// session from database after some time
						// this is configurable param and it will change
						// accordingly, need to read from some file
						/*
						 * int threadSleeptime = sessionClearTimeInMinutes * 60
						 * * 1000; ManageShiftSlotHoldClient
						 * manageReservationSlotHoldClient = new
						 * ManageShiftSlotHoldClient(threadSleeptime,
						 * shiftSlotActiveClientInfo.getSessionId(),
						 * shiftSlotActiveClientInfo.getShiftSlotId(),
						 * shiftSlotActiveClientInfo.getId(), schemaName);
						 * Thread thread = new
						 * Thread(manageReservationSlotHoldClient);
						 * thread.start();
						 */
						holdShiftSlotResponse = new HoldShiftSlotResponse();
						holdShiftSlotResponse.setShiftSlots(shiftSlot);
						holdShiftSlotResponse.setShiftHoldingClientId(shiftSlotActiveClientInfo.getId());
					} else {
						throw new NirvanaXPException(new NirvanaServiceErrorResponse(
								MessageConstants.ERROR_CODE_SHIFT_SCHEDULE_TAKEN_EXCEPTION,
								MessageConstants.ERROR_MESSAGE_SHIFT_SCHEDULE_TAKEN_DISPLAY_MESSAGE, null));
					}
				} else {
					throw new NirvanaXPException(
							new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_SHIFT_SCHEDULE_TAKEN_EXCEPTION,
									MessageConstants.ERROR_MESSAGE_SHIFT_SCHEDULE_TAKEN_DISPLAY_MESSAGE, null));
				}

			} else {

				throw new NirvanaXPException(new NirvanaServiceErrorResponse(
						MessageConstants.ERROR_CODE_SHIFT_SCHEDULE_STATUS_INVALID_EXCEPTION,
						MessageConstants.ERROR_MESSAGE_SHIFT_SCHEDULE_STATUS_INVALID_DISPLAY_MESSAGE, null));

			}

		}

		return holdShiftSlotResponse;

	}

	/**
	 * Gets the order source to shift schedule by shift schedule id.
	 *
	 * @param em
	 *            the em
	 * @param shiftScheduleId
	 *            the shift schedule id
	 * @return the order source to shift schedule by shift schedule id
	 * @throws Exception
	 *             the exception
	 */
	public List<OrderSourceToShiftSchedule> getOrderSourceToShiftScheduleByShiftScheduleId(EntityManager em,
			String shiftScheduleId) throws Exception {
		try {

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceToShiftSchedule> criteria = builder.createQuery(OrderSourceToShiftSchedule.class);
			Root<OrderSourceToShiftSchedule> r = criteria.from(OrderSourceToShiftSchedule.class);
			TypedQuery<OrderSourceToShiftSchedule> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(OrderSourceToShiftSchedule_.shiftScheduleId), shiftScheduleId),
					builder.equal(r.get(OrderSourceToShiftSchedule_.status), "B")));
			return query.getResultList();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * Gets the order source group to shift schedule by shift schedule id.
	 *
	 * @param em
	 *            the em
	 * @param shiftScheduleId
	 *            the shift schedule id
	 * @return the order source group to shift schedule by shift schedule id
	 * @throws Exception
	 *             the exception
	 */
	public List<OrderSourceGroupToShiftSchedule> getOrderSourceGroupToShiftScheduleByShiftScheduleId(EntityManager em,
			String shiftScheduleId) throws Exception {
		try {

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroupToShiftSchedule> criteria = builder
					.createQuery(OrderSourceGroupToShiftSchedule.class);
			Root<OrderSourceGroupToShiftSchedule> r = criteria.from(OrderSourceGroupToShiftSchedule.class);
			TypedQuery<OrderSourceGroupToShiftSchedule> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(OrderSourceGroupToShiftSchedule_.shiftScheduleId), shiftScheduleId),
					builder.equal(r.get(OrderSourceGroupToShiftSchedule_.status), "B")));
			return query.getResultList();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * Unblock shift schedule for order source group.
	 *
	 * @param orderSourceGroupToShiftSchedule
	 *            the order source group to shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source group to shift schedule
	 * @throws Exception
	 *             the exception
	 */
	OrderSourceGroupToShiftSchedule unblockShiftScheduleForOrderSourceGroup(
			OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {

		if (orderSourceGroupToShiftSchedule != null) {
			updateUnblockShiftScheduleForOrderSourceGroup(orderSourceGroupToShiftSchedule, httpRequest, em);

		}

		return orderSourceGroupToShiftSchedule;
	}

	/**
	 * Update unblock shift schedule for order source group.
	 *
	 * @param orderSourceGroupToShiftSchedule
	 *            the order source group to shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @throws Exception
	 *             the exception
	 */
	private void updateUnblockShiftScheduleForOrderSourceGroup(
			OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		orderSourceGroupToShiftSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		orderSourceGroupToShiftSchedule.setStatus("D");
		em.merge(orderSourceGroupToShiftSchedule);
		List<OrderSourceToShiftSchedule> orderSourceToShiftSchedules = getOrderSourceToShiftScheduleByOrderSourceGroupToShiftSchedule(
				em, orderSourceGroupToShiftSchedule);
		for (OrderSourceToShiftSchedule orderSourceToShiftSchedule : orderSourceToShiftSchedules) {
			orderSourceToShiftSchedule.setUpdated(orderSourceGroupToShiftSchedule.getUpdated());
			orderSourceToShiftSchedule.setStatus("D");
			em.merge(orderSourceToShiftSchedule);
		}

	}

	/**
	 * Gets the order source to shift schedule by order source group to shift
	 * schedule.
	 *
	 * @param em
	 *            the em
	 * @param orderSourceGroupToShiftSchedule
	 *            the order source group to shift schedule
	 * @return the order source to shift schedule by order source group to shift
	 *         schedule
	 */
	private List<OrderSourceToShiftSchedule> getOrderSourceToShiftScheduleByOrderSourceGroupToShiftSchedule(
			EntityManager em, OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceToShiftSchedule> criteria = builder.createQuery(OrderSourceToShiftSchedule.class);
			Root<OrderSourceToShiftSchedule> r = criteria.from(OrderSourceToShiftSchedule.class);
			TypedQuery<OrderSourceToShiftSchedule> query = em.createQuery(criteria.select(r)
					.where(builder.equal(r.get(OrderSourceToShiftSchedule_.shiftScheduleId),
							orderSourceGroupToShiftSchedule.getShiftScheduleId()),
							builder.equal(r.get(OrderSourceToShiftSchedule_.fromDate),
									orderSourceGroupToShiftSchedule.getFromDate()),
							builder.equal(r.get(OrderSourceToShiftSchedule_.toDate),
									orderSourceGroupToShiftSchedule.getToDate()),
							builder.equal(r.get(OrderSourceToShiftSchedule_.fromTime),
									orderSourceGroupToShiftSchedule.getFromTime()),
							builder.equal(r.get(OrderSourceToShiftSchedule_.toTime),
									orderSourceGroupToShiftSchedule.getToTime())));
			List<OrderSourceToShiftSchedule> objects = query.getResultList();

			return objects;
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);
		}
		return null;
	}

	/**
	 * Update shift schedule for order source group.
	 *
	 * @param orderSourceGroupToShiftSchedule
	 *            the order source group to shift schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source group to shift schedule
	 * @throws Exception
	 *             the exception
	 */
	OrderSourceGroupToShiftSchedule updateShiftScheduleForOrderSourceGroup(
			OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {

		orderSourceGroupToShiftSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(orderSourceGroupToShiftSchedule);
		List<OrderSourceToShiftSchedule> orderSourceToShiftSchedules = getOrderSourceToShiftScheduleByOrderSourceGroupToShiftSchedule(
				em, orderSourceGroupToShiftSchedule);
		for (OrderSourceToShiftSchedule orderSourceToShiftSchedule : orderSourceToShiftSchedules) {
			orderSourceToShiftSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.merge(orderSourceToShiftSchedule);
		}

		return orderSourceGroupToShiftSchedule;
	}

	/**
	 * Adds the multiple location item group.
	 *
	 * @param em
	 *            the em
	 * @param itemGroup
	 *            the item group
	 * @param itemGroupPacket
	 *            the item group packet
	 * @param request
	 *            the request
	 * @return the item group
	 * @throws Exception
	 *             the exception
	 */
	ItemGroup addMultipleLocationItemGroup(EntityManager em, ItemGroup itemGroup, ItemGroupPacket itemGroupPacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = itemGroupPacket.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (itemGroup != null && itemGroupPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
//			itemGroup.setLocationsId(baseLocation.getId());

			ItemGroup globalItemGroup = updateItemsGroup(itemGroup, request, em, itemGroupPacket);
			itemGroupPacket.setItemGroup(globalItemGroup);
			itemGroupPacket.setLocationsListId("");
			

			em.getTransaction().commit();
			em.getTransaction().begin();
			// now add/update child location
			for (String locationId : locationIds) {
				
				if (!locationId.equals((baseLocation.getId()))) {
					String json = new StoreForwardUtility().returnJsonPacket(itemGroupPacket, "ItemGroupPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
							Integer.parseInt(itemGroupPacket.getMerchantId()));
					ItemGroup localItemGroup = new ItemGroup().getItemGroup(globalItemGroup);
					localItemGroup.setGlobalId(globalItemGroup.getId());

					ItemGroup itemGroup1 = null;
					if (globalItemGroup.getItemGroupId() != null) {

						try {
							String queryString = "select s from ItemGroup s where s.globalId =? and s.locationsId=? and s.status !='D' ";
							TypedQuery<ItemGroup> query = em.createQuery(queryString, ItemGroup.class)
									.setParameter(1, globalItemGroup.getItemGroupId()).setParameter(2, locationId);
							itemGroup1 = query.getSingleResult();
						} catch (NoResultException e) {
							// todo shlok need to handle exception in below line
							logger.severe(e);

						}

					}
					if (itemGroup1 != null) {
						localItemGroup.setItemGroupId(itemGroup1.getId());
					}

					localItemGroup.setLocationsId(locationId);
					ItemGroup itemsGroup = updateItemsGroup(localItemGroup, request, em, itemGroupPacket);
					itemGroupPacket.setItemGroup(itemsGroup);
					itemGroupPacket.setLocalServerURL(0);
					String json2 = new StoreForwardUtility().returnJsonPacket(itemGroupPacket, "ItemGroupPacket",
							request);
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request,
							itemGroup.getLocationsId(), Integer.parseInt(itemGroupPacket.getMerchantId()));

				}
			}
		}
		return itemGroup;
	}

	/**
	 * Update multiple location item group.
	 *
	 * @param em
	 *            the em
	 * @param itemGroup
	 *            the item group
	 * @param itemGroupPacket
	 *            the item group packet
	 * @param request
	 *            the request
	 * @return the item group
	 * @throws Exception
	 *             the exception
	 */
	ItemGroup updateMultipleLocationItemGroup(EntityManager em, ItemGroup itemGroup, ItemGroupPacket itemGroupPacket,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		if (itemGroupPacket.getLocationId().trim().length() > 0) {
			locationIds = itemGroupPacket.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (itemGroup != null && itemGroupPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
			ItemGroup globalItemGroup = updateItemsGroup(itemGroup, request, em, itemGroupPacket);
			itemGroupPacket.setItemGroup(globalItemGroup);
			itemGroupPacket.setLocationsListId("");
			
			// now add/update child location
			if (locationIds != null) {
				for (String locationId : locationIds) {
					
					if (!locationId.equals((baseLocation.getId()))) {
						String json = new StoreForwardUtility().returnJsonPacket(itemGroupPacket, "ItemGroupPacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
								Integer.parseInt(itemGroupPacket.getMerchantId()));
						ItemGroup localGroup = new ItemGroup().getItemGroup(globalItemGroup);
						ItemGroup itemGroups = getItemGroupByGlobalGroupIdAndLocationId(em, locationId,
								globalItemGroup.getId());
						if (itemGroups != null && itemGroup.getId() != null) {
							localGroup.setGlobalId(globalItemGroup.getId());
							localGroup.setId(itemGroups.getId());
							localGroup.setItemGroupId(globalItemGroup.getItemGroupId());
						} else {
							localGroup.setGlobalId(globalItemGroup.getId());
						}
						// locationId

						ItemGroup itemGroup1 = null;
						if (globalItemGroup.getItemGroupId() != null) {
							try {
								String queryString = "select s from ItemGroup s where s.globalId =? and s.locationsId=? and s.status !='D' ";
								TypedQuery<ItemGroup> query = em.createQuery(queryString, ItemGroup.class)
										.setParameter(1, globalItemGroup.getItemGroupId()).setParameter(2, locationId);
								itemGroup1 = query.getSingleResult();
							} catch (NoResultException e) {

								logger.severe(e);

							}
						}
						if (itemGroup1 != null) {
							localGroup.setItemGroupId(itemGroup1.getId());
						}

						localGroup.setLocationsId(locationId);
						localGroup = updateItemsGroup(localGroup, request, em, itemGroupPacket);
						itemGroupPacket.setItemGroup(localGroup);
						itemGroupPacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(itemGroupPacket, "ItemGroupPacket",
								request);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request,
								itemGroup.getLocationsId(), Integer.parseInt(itemGroupPacket.getMerchantId()));
					}
				}
			}
		}
		return itemGroup;
	}

	/**
	 * Delete multiple location item group.
	 *
	 * @param em
	 *            the em
	 * @param itemGroup
	 *            the item group
	 * @param itemGroupPacket
	 *            the item group packet
	 * @param request
	 *            the request
	 * @return the item group
	 * @throws Exception
	 *             the exception
	 */
	ItemGroup deleteMultipleLocationItemGroup(EntityManager em, ItemGroup itemGroup, ItemGroupPacket itemGroupPacket,
			HttpServletRequest request) throws Exception {
		// delete baselocation
		itemGroup = deleteItemGroup(em, itemGroup);

		// get all sublocations
		List<ItemGroup> itemGroups = getAllItemGroupByGlobalItemGroupId(itemGroup.getId(), em);
		// delete sublocation
		for (ItemGroup itemsChar2 : itemGroups) {
			itemsChar2 = deleteItemGroup(em, itemsChar2);
		}
		return itemGroup;
	}

	/**
	 * Adds the item group.
	 *
	 * @param em
	 *            the em
	 * @param itemGroup
	 *            the item group
	 * @return the item group
	 * @throws Exception
	 */
	ItemGroup addItemGroup(EntityManager em, ItemGroup itemGroup, HttpServletRequest httpRequest) throws Exception {
		itemGroup.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (itemGroup.getId() == null || itemGroup.getId().equals("0"))
			itemGroup.setId(new StoreForwardUtility().generateUUID());

		itemGroup = em.merge(itemGroup);

		return itemGroup;
	}

	/**
	 * Delete item group.
	 *
	 * @param em
	 *            the em
	 * @param ItemGroup
	 *            the item group
	 * @return the item group
	 */
	ItemGroup deleteItemGroup(EntityManager em, ItemGroup ItemGroup) {
		ItemGroup c = (ItemGroup) new CommonMethods().getObjectById("ItemGroup", em, ItemGroup.class,
				ItemGroup.getId());
		c.setStatus("D");
		c.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(c);

		return c;
	}

	/**
	 * Update items group.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the item group
	 * @throws Exception
	 *             the exception
	 */
	ItemGroup updateItemsGroup(ItemGroup rStatus, HttpServletRequest httpRequest, EntityManager em,
			ItemGroupPacket itemGroupPacket) throws Exception {

		if (rStatus.getGlobalId() == null) {
			ItemGroup local = null;
			try {
				String queryString = "select s from ItemGroup s where s.id =? ";
				TypedQuery<ItemGroup> query = em.createQuery(queryString, ItemGroup.class).setParameter(1,
						rStatus.getId());
				local = query.getSingleResult();
			} catch (Exception e) {
				logger.severe(e);
			}

			if (local != null && itemGroupPacket.getLocalServerURL() == 0) {
				rStatus.setGlobalId(local.getGlobalId());
			}
		}
		if (rStatus.getId() == null) {
			rStatus.setId(new StoreForwardUtility().generateUUID());
		}
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		rStatus = em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Gets the item group by global group id and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalId
	 *            the global id
	 * @return the item group by global group id and location id
	 */
	private ItemGroup getItemGroupByGlobalGroupIdAndLocationId(EntityManager em, String locationId, String globalId) {
		ItemGroup itemGroup1 = null;
		try {
			String queryString = "select s from ItemGroup s where s.globalId =? and s.locationsId=? and s.status !='D' ";
			TypedQuery<ItemGroup> query = em.createQuery(queryString, ItemGroup.class).setParameter(1, globalId)
					.setParameter(2, locationId);
			itemGroup1 = query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line

			logger.severe(e);

		}
		if (itemGroup1 == null) {
			ItemGroup oldItemGroup = (ItemGroup) new CommonMethods().getObjectById("ItemGroup", em, ItemGroup.class,
					globalId);
			if (oldItemGroup != null) {
				itemGroup1 = new ItemGroup().getItemGroup(oldItemGroup);
				itemGroup1.setId(new StoreForwardUtility().generateUUID());
				itemGroup1.setGlobalId(globalId);
				itemGroup1.setLocationsId(locationId);
				itemGroup1 = em.merge(itemGroup1);
			}

		}
		return itemGroup1;
	}

	/**
	 * Gets the all item group by global item group id.
	 *
	 * @param globalItemGroupId
	 *            the global item group id
	 * @param em
	 *            the em
	 * @return the all item group by global item group id
	 */
	List<ItemGroup> getAllItemGroupByGlobalItemGroupId(String globalItemGroupId, EntityManager em) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemGroup> criteria = builder.createQuery(ItemGroup.class);
			Root<ItemGroup> printerRoot = criteria.from(ItemGroup.class);
			TypedQuery<ItemGroup> query = em.createQuery(criteria.select(printerRoot)
					.where(builder.equal(printerRoot.get(ItemGroup_.globalId), globalItemGroupId)));
			return query.getResultList();
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			logger.severe("No Result found");
		}
		return null;
	}

	/**
	 * Update order source group wait time.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the order source group
	 * @throws Exception
	 *             the exception
	 */

	OrderSourceGroup updateOrderSourceGroupWaitTime(OrderSourceGroup rStatus, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		OrderSourceGroup orderSource = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,
				OrderSourceGroup.class, rStatus.getId());

		orderSource.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		orderSource.setAvgWaitTime(rStatus.getAvgWaitTime());
		orderSource = em.merge(orderSource);

		return orderSource;

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
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @return the item attributes type by location id
	 * @throws Exception
	 *             the exception
	 */
	public String getItemAttributesTypeByLocationId(String locationId, int startIndex, int endIndex, String displayName,
			EntityManager em, HttpServletRequest httpRequest) throws Exception {

		List<ItemAttributeDisplayTypePacket> ans = new ArrayList<ItemAttributeDisplayTypePacket>();
		String sql = " select id, name, display_name,description,is_required from items_attribute_type "
				+ " where locations_id=?" + " and display_name like '%" + displayName + "%' "
				+ " and status !='D' limit " + startIndex + "," + endIndex;

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
		for (Object[] objRow : resultList) {
			ItemAttributeDisplayTypePacket detailPacket = new ItemAttributeDisplayTypePacket();
			detailPacket.setId((String) objRow[0]);
			detailPacket.setName((String) objRow[1]);
			detailPacket.setDisplayName((String) objRow[2]);
			detailPacket.setDescription((String) objRow[3]);
			if (((boolean) objRow[4]) == true) {
				detailPacket.setModifierTypeName("Mandatory");
			} else {
				detailPacket.setModifierTypeName("Optional");
			}

			ans.add(detailPacket);
		}

		return new JSONUtility(httpRequest).convertToJsonString(ans);
	}

	/**
	 * Gets the items to printer.
	 *
	 * @param em
	 *            the em
	 * @param printerId
	 *            the printer id
	 * @return the items to printer
	 */
	private List<ItemsToPrinter> getItemsToPrinter(EntityManager em, String printerId) {
		try {
			String queryString = "select p from ItemsToPrinter p where p.printersId=? and p.status != 'D' ";
			TypedQuery<ItemsToPrinter> query = em.createQuery(queryString, ItemsToPrinter.class).setParameter(1,
					printerId);
			return query.getResultList();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe("No Result found");
		}
		return null;
	}

	/**
	 * Gets the items to discounts.
	 *
	 * @param em
	 *            the em
	 * @param discountsId
	 *            the discounts id
	 * @return the items to discounts
	 */
	private List<ItemsToDiscount> getItemsToDiscounts(EntityManager em, String discountsId) {
		try {
			String queryString = "select p from ItemsToDiscount p where p.discountsId=? and p.status != 'D' ";
			TypedQuery<ItemsToDiscount> query = em.createQuery(queryString, ItemsToDiscount.class).setParameter(1,
					discountsId);
			return query.getResultList();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe("No Result found");
		}
		return null;
	}

	/**
	 * Gets the items to items attribute.
	 *
	 * @param itemsAttributeId
	 *            the items attribute id
	 * @param em
	 *            the em
	 * @return the items to items attribute
	 */
	public List<ItemsToItemsAttribute> getItemsToItemsAttribute(String itemsAttributeId, EntityManager em) {
		if (itemsAttributeId != null && em != null) {

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToItemsAttribute> criteria = builder.createQuery(ItemsToItemsAttribute.class);
			Root<ItemsToItemsAttribute> r = criteria.from(ItemsToItemsAttribute.class);
			TypedQuery<ItemsToItemsAttribute> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(ItemsToItemsAttribute_.itemsAttributeId), itemsAttributeId),
					builder.notEqual(r.get(ItemsToItemsAttribute_.status), "D")));
			return query.getResultList();
		}
		return null;

	}

	/**
	 * Gets the items to items char.
	 *
	 * @param itemsToItemsCharId
	 *            the items to items char id
	 * @param em
	 *            the em
	 * @return the items to items char
	 */
	public List<ItemsToItemsChar> getItemsToItemsChar(String itemsToItemsCharId, EntityManager em) {
		if (itemsToItemsCharId != null && em != null) {

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToItemsChar> criteria = builder.createQuery(ItemsToItemsChar.class);
			Root<ItemsToItemsChar> r = criteria.from(ItemsToItemsChar.class);
			TypedQuery<ItemsToItemsChar> query = em.createQuery(
					criteria.select(r).where(builder.equal(r.get(ItemsToItemsChar_.itemsCharId), itemsToItemsCharId),
							builder.notEqual(r.get(ItemsToItemsChar_.status), "D")));
			return query.getResultList();
		}
		return null;

	}

	/**
	 * Gets the items to items attribute type.
	 *
	 * @param itemsAttributeTypeId
	 *            the items attribute type id
	 * @param em
	 *            the em
	 * @return the items to items attribute type
	 */
	public List<ItemsToItemsAttributeType> getItemsToItemsAttributeType(String itemsAttributeTypeId, EntityManager em) {
		try {
			if (itemsAttributeTypeId != null && em != null) {

				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<ItemsToItemsAttributeType> criteria = builder
						.createQuery(ItemsToItemsAttributeType.class);
				Root<ItemsToItemsAttributeType> r = criteria.from(ItemsToItemsAttributeType.class);
				TypedQuery<ItemsToItemsAttributeType> query = em.createQuery(criteria.select(r).where(
						builder.equal(r.get(ItemsToItemsAttributeType_.itemsAttributeTypeId), itemsAttributeTypeId),
						builder.notEqual(r.get(ItemsToItemsAttributeType_.status), "D")));
				return query.getResultList();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// todo shlok need to handle exception in below line
			logger.severe("No Result found");
		}
		return null;

	}

	/**
	 * Gets the items attribute type to items attribute.
	 *
	 * @param itemsAttributeTypeId
	 *            the items attribute type id
	 * @param em
	 *            the em
	 * @return the items attribute type to items attribute
	 */
	public List<ItemsAttributeTypeToItemsAttribute> getItemsAttributeTypeToItemsAttribute(String itemsAttributeTypeId,
			EntityManager em) {

		try {
			if (itemsAttributeTypeId != null && em != null) {

				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<ItemsAttributeTypeToItemsAttribute> criteria = builder
						.createQuery(ItemsAttributeTypeToItemsAttribute.class);
				Root<ItemsAttributeTypeToItemsAttribute> r = criteria.from(ItemsAttributeTypeToItemsAttribute.class);
				TypedQuery<ItemsAttributeTypeToItemsAttribute> query = em.createQuery(criteria.select(r)
						.where(builder.equal(r.get(ItemsAttributeTypeToItemsAttribute_.itemsAttributeTypeId),
								itemsAttributeTypeId),
								builder.notEqual(r.get(ItemsAttributeTypeToItemsAttribute_.status), "D")));
				return query.getResultList();
			}
		} catch (Exception e) {
			// todo shlok need to handle exception in below line
			logger.severe("No Result found");
		}

		return null;

	}

	/**
	 * Adds the device to pin pad.
	 *
	 * @param pinpad
	 *            the pinpad
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the device to pin pad
	 * @throws Exception
	 *             the exception
	 */

	public DeviceToPinPad addDeviceToPinPad(DeviceToPinPad pinpad, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		pinpad.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.persist(pinpad);
		return pinpad;
	}

	/**
	 * Update device to pin pad.
	 *
	 * @param pinPad
	 *            the pin pad
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	public List<DeviceToPinPad> updateDeviceToPinPad(List<DeviceToPinPad> pinPad, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		List<DeviceToPinPad> deviceToPinPads = new ArrayList<DeviceToPinPad>();
		for (DeviceToPinPad deviceToPinPad : pinPad) {
			deviceToPinPad.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			deviceToPinPad = em.merge(deviceToPinPad);
			deviceToPinPads.add(deviceToPinPad);

		}

		return deviceToPinPads;
	}

	/**
	 * Delete device to pin pad.
	 *
	 * @param deviceToPinpad
	 *            the device to pinpad
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the device to pin pad
	 * @throws Exception
	 *             the exception
	 */

	public DeviceToPinPad deleteDeviceToPinPad(DeviceToPinPad deviceToPinpad, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		DeviceToPinPad p = (DeviceToPinPad) new CommonMethods().getObjectById("DeviceToPinPad", em,
				DeviceToPinPad.class, deviceToPinpad.getId());
		p.setStatus("D");
		p.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		p = em.merge(p);
		return p;

	}

	/**
	 * Update delivery option.
	 *
	 * @param rStatus
	 *            the r status
	 * @param em
	 *            the em
	 * @return the delivery option
	 * @throws Exception
	 *             the exception
	 */

	DeliveryOption updateDeliveryOption(DeliveryOption rStatus, EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Adds the delivery option.
	 *
	 * @param rStatus
	 *            the r status
	 * @param em
	 *            the em
	 * @return the delivery option
	 * @throws Exception
	 *             the exception
	 */
	DeliveryOption addDeliveryOption(DeliveryOption rStatus, EntityManager em, HttpServletRequest httpRequest)
			throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null)
			rStatus.setId(new StoreForwardUtility().generateUUID());

		rStatus = em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Delete delivery option.
	 *
	 * @param deliveryOption
	 *            the delivery option
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the delivery option
	 * @throws Exception
	 *             the exception
	 */
	DeliveryOption deleteDeliveryOption(DeliveryOption deliveryOption, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		DeliveryOption u = (DeliveryOption) new CommonMethods().getObjectById("DeliveryOption", em,
				DeliveryOption.class, deliveryOption.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(u);

		return u;
	}

	/**
	 * Gets the order source to shift schedule by shift schedule id.
	 *
	 * @param em
	 *            the em
	 * @param shiftScheduleId
	 *            the shift schedule id
	 * @param orderSourceId
	 *            the order source id
	 * @param date
	 *            the date
	 * @return the order source to shift schedule by shift schedule id
	 * @throws Exception
	 *             the exception
	 */
	List<OrderSourceToShiftSchedule> getOrderSourceToShiftScheduleByShiftScheduleId(EntityManager em,
			String shiftScheduleId, String orderSourceId, String date) throws Exception {
		String queryString = "select c from OrderSourceToShiftSchedule c where c.shiftScheduleId=? "
				+ " and c.orderSourceId=? and c.status='B' and c.fromDate<=?  and c.toDate>=? ";
		TypedQuery<OrderSourceToShiftSchedule> query = em.createQuery(queryString, OrderSourceToShiftSchedule.class)
				.setParameter(1, shiftScheduleId).setParameter(2, orderSourceId).setParameter(3, date)
				.setParameter(4, date);

		return query.getResultList();

	}

	List<LocationsToShiftPreAssignServer> addUpdateLocationsToShiftPreAssignServer(
			List<LocationsToShiftPreAssignServer> locationsToShiftPreAssignServerList, HttpServletRequest httpRequest,
			EntityManager em, String locationId) throws Exception {
		List<LocationsToShiftPreAssignServer> newLocationsToShiftPreAssignServerList = new ArrayList<LocationsToShiftPreAssignServer>();
		for (LocationsToShiftPreAssignServer locationsToShiftPreAssignServer : locationsToShiftPreAssignServerList) {
			LocationsToShiftPreAssignServer resultSet = new LocationsToShiftPreAssignServer();
			try {
				String queryString = "select b from LocationsToShiftPreAssignServer b where b.locationsId= ? and b.shiftId=? and b.status not in('D') ";

				Query query = em.createQuery(queryString)
						.setParameter(1, locationsToShiftPreAssignServer.getLocationsId())
						.setParameter(2, locationsToShiftPreAssignServer.getShiftId());
				resultSet = (LocationsToShiftPreAssignServer) query.getSingleResult();
			} catch (Exception e) {

				resultSet = null;
			}

			LocationsToShiftPreAssignServer newLocationsToShiftPreAssignServer = new LocationsToShiftPreAssignServer();

			if (resultSet != null) {
				newLocationsToShiftPreAssignServer = resultSet;
				newLocationsToShiftPreAssignServer.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				newLocationsToShiftPreAssignServer.setUserId(locationsToShiftPreAssignServer.getUserId());
				newLocationsToShiftPreAssignServer.setServerName(locationsToShiftPreAssignServer.getServerName());
				newLocationsToShiftPreAssignServer.setLocalTime(new TimezoneTime()
						.getLocationSpecificTimeToAdd(locationsToShiftPreAssignServer.getLocationsId(), em));
				newLocationsToShiftPreAssignServer.setUpdatedBy(locationsToShiftPreAssignServer.getUpdatedBy());
				em.merge(newLocationsToShiftPreAssignServer);
				// insert into history
				new LocationsToShiftPreAssignServerHistory().createLocationsToShiftPreAssignServerHistory(em,
						newLocationsToShiftPreAssignServer);
			} else {
				newLocationsToShiftPreAssignServer = locationsToShiftPreAssignServer;
				newLocationsToShiftPreAssignServer.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				newLocationsToShiftPreAssignServer.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				newLocationsToShiftPreAssignServer.setLocalTime(new TimezoneTime()
						.getLocationSpecificTimeToAdd(locationsToShiftPreAssignServer.getLocationsId(), em));
				newLocationsToShiftPreAssignServer.setUpdatedBy(locationsToShiftPreAssignServer.getUpdatedBy());
				// if(newLocationsToShiftPreAssignServer.getId()==0)
				// newLocationsToShiftPreAssignServer.setId(new
				// StoreForwardUtility().generateUUID());

				em.merge(newLocationsToShiftPreAssignServer);
				// insert into history
				new LocationsToShiftPreAssignServerHistory().createLocationsToShiftPreAssignServerHistory(em,
						newLocationsToShiftPreAssignServer);

			}
			newLocationsToShiftPreAssignServerList.add(newLocationsToShiftPreAssignServer);
		}
		return newLocationsToShiftPreAssignServerList;

	}

	/**
	 * Adds the multiple location items char.
	 *
	 * @param em
	 *            the em
	 * @param nutrition
	 *            the item char
	 * @param packet
	 *            the item char packet
	 * @param request
	 *            the request
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */
	Nutritions addMultipleLocationNutritions(EntityManager em, Nutritions nutrition, NutritionsPacket packet,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = packet.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (nutrition != null && packet.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
//			nutrition.setLocationsId(baseLocation.getId());

			Nutritions globalNutritions = updateNutritions(nutrition, request, em, baseLocation.getId(), packet);
			packet.setNutritions(globalNutritions);
			packet.setLocationsListId("");
			
			// now add/update child location
			for (String locationId : locationIds) {
				
				if (!locationId.equals((baseLocation.getId()))) {
					String json = new StoreForwardUtility().returnJsonPacket(packet, "NutritionsPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json, request,baseLocation.getId(),
							Integer.parseInt(packet.getMerchantId()));
					Nutritions local = new Nutritions().getNutritions(globalNutritions);
					local.setId(null);
					local.setGlobalId(globalNutritions.getId());
					local.setLocationsId(locationId);
					Nutritions nutritions = updateNutritions(local, request, em, locationId, packet);
					packet.setNutritions(nutritions);
					packet.setLocalServerURL(0);
					String json2 = new StoreForwardUtility().returnJsonPacket(packet, "NutritionsPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request, nutrition.getLocationsId(),
							Integer.parseInt(packet.getMerchantId()));
				}
			}
		}
		return nutrition;
	}

	/**
	 * Update multiple location items char.
	 *
	 * @param em
	 *            the em
	 * @param nutritions
	 *            the item char
	 * @param packet
	 *            the item char packet
	 * @param request
	 *            the request
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */
	Nutritions updateMultipleLocationNutritions(EntityManager em, Nutritions nutritions, NutritionsPacket packet,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		Nutritions globalNutritions = null;
		if (packet.getLocationId().trim().length() > 0) {
			locationIds = packet.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (nutritions != null && packet.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
			globalNutritions = updateNutritions(nutritions, request, em, baseLocation.getId(), packet);
			packet.setNutritions(globalNutritions);
			packet.setLocationsListId("");
			
			// now add/update child location
			if (locationIds != null) {
				for (String locationId : locationIds) {
					
					if (!locationId.equals((baseLocation.getId()))) {
						String json = new StoreForwardUtility().returnJsonPacket(packet, "NutritionsPacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
								Integer.parseInt(packet.getMerchantId()));
						Nutritions local = new Nutritions().getNutritions(globalNutritions);
						Nutritions itemChars = getNutritionsByGlobalIdAndLocationId(em, locationId,
								globalNutritions.getId());
						if (itemChars != null && nutritions.getId() != null) {
							local.setGlobalId(globalNutritions.getId());
							local.setSortSequence(itemChars.getSortSequence());
							local.setId(itemChars.getId());
						} else {
							local.setGlobalId(globalNutritions.getId());
						}

						local.setLocationsId(locationId);
						Nutritions nutrition = updateNutritions(local, request, em, locationId, packet);
						packet.setNutritions(nutrition);
						packet.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(packet, "NutritionsPacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request, nutrition.getLocationsId(),
								Integer.parseInt(packet.getMerchantId()));
					}
				}
			}
		}
		return globalNutritions;
	}

	/**
	 * Adds the Nutritions
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */
	Nutritions addNutritions(Nutritions rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null)
			rStatus.setId(new StoreForwardUtility().generateUUID());

		rStatus = em.merge(rStatus);

		return rStatus;
	}

	/**
	 * Update Nutritions
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */
	Nutritions updateNutritions(Nutritions rStatus, HttpServletRequest httpRequest, EntityManager em, String locationId,
			NutritionsPacket packet) throws Exception {

		if (rStatus.getGlobalId() == null) {
			Nutritions local = null;
			try {
				String queryString = "select s from Nutritions s where s.id =? ";
				TypedQuery<Nutritions> query = em.createQuery(queryString, Nutritions.class).setParameter(1,
						rStatus.getId());
				local = query.getSingleResult();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe(e);
			}

			if (local != null && packet.getLocalServerURL() == 0) {
				rStatus.setGlobalId(local.getGlobalId());
			}
		}
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (rStatus.getId() == null) {
			rStatus.setId(new StoreForwardUtility().generateUUID());
		}
		rStatus = em.merge(rStatus);
		em.getTransaction().commit();
		em.getTransaction().begin();
		return rStatus;
	}

	/**
	 * Gets the item char by global char id and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalId
	 *            the global course id
	 * @return the item char by global char id and location id
	 */
	private Nutritions getNutritionsByGlobalIdAndLocationId(EntityManager em, String locationId, String globalId) {
		Nutritions char1 = null;
		try {
			String queryString = "select s from Nutritions s where s.globalId =? and s.locationsId=? and s.status !='D' ";
			TypedQuery<Nutritions> query = em.createQuery(queryString, Nutritions.class).setParameter(1, globalId)
					.setParameter(2, locationId);
			char1 = query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		if (char1 == null) {
			Nutritions old = (Nutritions) new CommonMethods().getObjectById("Nutritions", em, Nutritions.class,
					globalId);
			if (old != null) {
				char1 = new Nutritions().getNutritions(old);
				char1.setLocationsId(locationId);
				char1.setId(new StoreForwardUtility().generateUUID());
				char1 = em.merge(char1);
			}

		}
		return char1;
	}

	/**
	 * Delete multiple location Nutritions
	 *
	 * @param em
	 *            the em
	 * @param Nutritions
	 *            the items char
	 * @param packet
	 *            the item char packet
	 * @param request
	 *            the request
	 * @return the items char
	 * @throws Exception
	 *             the exception
	 */
	Nutritions deleteMultipleLocationNutritions(EntityManager em, Nutritions nutritions, NutritionsPacket packet,
			HttpServletRequest request) throws Exception {
		// delete baselocation
		nutritions = deleteNutritions(nutritions, request, em);
		// get all sublocations
		List<Nutritions> nutritionsList = getAllNutritionsByGlobalNutritionsId(nutritions.getId(), em);
		// delete sublocation
		for (Nutritions nut : nutritionsList) {
			deleteNutritions(nut, request, em);
		}
		return nutritions;
	}

	List<Nutritions> getAllNutritionsByGlobalNutritionsId(String globalId, EntityManager em) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Nutritions> criteria = builder.createQuery(Nutritions.class);
			Root<Nutritions> printerRoot = criteria.from(Nutritions.class);
			TypedQuery<Nutritions> query = em.createQuery(
					criteria.select(printerRoot).where(builder.equal(printerRoot.get(Nutritions_.globalId), globalId)));
			return query.getResultList();
		} catch (Exception e) {
			logger.severe(e, "No Result found");
		}
		return null;
	}

	Nutritions deleteNutritions(Nutritions rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception {
		Nutritions u = (Nutritions) new CommonMethods().getObjectById("Nutritions", em, Nutritions.class,
				rStatus.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		u = em.merge(u);

		List<ItemsToNutritions> list = getItemsToNutritions(u.getId(), em);
		// todo shlok need to handle exception in below line
		// handle null here
		for (ItemsToNutritions itemsToNutritions : list) {
			itemsToNutritions.setStatus("D");
			itemsToNutritions = em.merge(itemsToNutritions);
		}
		return u;
	}

	public List<ItemsToNutritions> getItemsToNutritions(String itemsToNutritionsId, EntityManager em) {
		if (itemsToNutritionsId != null && em != null) {

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToNutritions> criteria = builder.createQuery(ItemsToNutritions.class);
			Root<ItemsToNutritions> r = criteria.from(ItemsToNutritions.class);
			TypedQuery<ItemsToNutritions> query = em.createQuery(
					criteria.select(r).where(builder.equal(r.get(ItemsToNutritions_.nutritionsId), itemsToNutritionsId),
							builder.notEqual(r.get(ItemsToNutritions_.status), "D")));
			return query.getResultList();
		}
		return null;

	}

	Set<ItemsAttributeToNutritions> getLocalItemsAttributeToNutritionsList(Set<ItemsAttributeToNutritions> globalList,
			String locationId, String itemAttributeId, EntityManager em, boolean isAdd) {
		Set<ItemsAttributeToNutritions> localList = new LinkedHashSet<ItemsAttributeToNutritions>();
		for (ItemsAttributeToNutritions attribute : globalList) {
			Nutritions itemChars = getNutritionsByGlobalCharIdAndLocationId(em, locationId,
					attribute.getNutritionsId());
			if (itemChars != null) {
				ItemsAttributeToNutritions attribute2 = new ItemsAttributeToNutritions();
				attribute2.setCreatedBy(attribute.getCreatedBy());
				attribute2.setUpdatedBy(attribute.getUpdatedBy());
				attribute2.setStatus(attribute.getStatus());
				attribute2.setNutritionsValue(attribute.getNutritionsValue());
				attribute2.setNutritionsId(itemChars.getId());
				if (itemAttributeId != null) {

					attribute2.setItemsAttributeId(itemAttributeId);
					ItemsAttributeToNutritions itemsCharToItemsAttribute = getNutritionsToItemsAttributeByGlobalIdItemAttributeIdAndLocationId(
							em, locationId, attribute2.getNutritionsId(), attribute2.getItemsAttributeId());
					if (itemsCharToItemsAttribute != null) {
						attribute2.setId(itemsCharToItemsAttribute.getId());
					}

				}

				if (isAdd) {
					if (attribute2 != null && attribute2.getNutritionsId() != null) {
						localList.add(attribute2);
					}
				} else {
					if (attribute2 != null && attribute2.getNutritionsId() != null
							&& attribute2.getNutritionsId() != null) {
						localList.add(attribute2);
					}
				}

			}
		}
		return localList;
	}

	private Nutritions getNutritionsByGlobalCharIdAndLocationId(EntityManager em, String locationId,
			String globalCourseId) {
		Nutritions char1 = null;
		try {
			String queryString = "select s from Nutritions s where s.globalId =? and s.locationsId=? and s.status !='D' ";
			TypedQuery<Nutritions> query = em.createQuery(queryString, Nutritions.class).setParameter(1, globalCourseId)
					.setParameter(2, locationId);
			char1 = query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		if (char1 == null) {
			Nutritions old = (Nutritions) new CommonMethods().getObjectById("Nutritions", em, Nutritions.class,
					globalCourseId);
			if (old != null) {
				char1 = new Nutritions().getNutritions(old);
				char1.setGlobalId(globalCourseId);
				char1.setLocationsId(locationId);
				if(char1.getId()==null){
					char1.setId(new StoreForwardUtility().generateUUID());
				}
				char1 = em.merge(char1);
			}

		}
		return char1;
	}

	private ItemsAttributeToNutritions getNutritionsToItemsAttributeByGlobalIdItemAttributeIdAndLocationId(
			EntityManager em, String locationId, String globalId, String itemAttributeId) {
		try {
			String queryString = "select s from ItemsAttributeToNutritions s where s.nutritionsId =? and s.itemsAttributeId=?   ";
			TypedQuery<ItemsAttributeToNutritions> query = em.createQuery(queryString, ItemsAttributeToNutritions.class)
					.setParameter(1, globalId).setParameter(2, itemAttributeId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Adds the reservations schedule.
	 *
	 * @param resSchedule
	 *            the res schedule
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the reservations schedule
	 * @throws Exception
	 *             the exception
	 */

	ItemsSchedule addItemsSchedule(ItemsSchedule resSchedule, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		if (resSchedule.getId() == null) {
			resSchedule.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			resSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			if (resSchedule.getId() == null)
				resSchedule.setId(new StoreForwardUtility().generateDynamicIntId(em, resSchedule.getLocationId(),
						httpRequest, "items_schedule"));
			em.persist(resSchedule);
		} else {
			resSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			resSchedule = em.merge(resSchedule);
		}

		return resSchedule;
	}

	ItemsSchedule updateItemsSchedule(ItemsSchedule resSchedule, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		if (resSchedule.getId() == null) {
			resSchedule.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			resSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				resSchedule.setId(new StoreForwardUtility().generateDynamicIntId(em, resSchedule.getLocationId(),
						httpRequest, "items_schedule"));
			resSchedule = em.merge(resSchedule);
			} else {
			resSchedule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			resSchedule = em.merge(resSchedule);
		}

		return resSchedule;
	}

	ItemsSchedule deleteItemsSchedule(ItemsSchedule resSchedule, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		ItemsSchedule u = (ItemsSchedule) new CommonMethods().getObjectById("ItemsSchedule", em, ItemsSchedule.class,
				resSchedule.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		try {

			String queryString = "SELECT rd FROM ItemsScheduleDay rd where rd.itemsScheduleId=?";

			TypedQuery<ItemsScheduleDay> query = em.createQuery(queryString, ItemsScheduleDay.class).setParameter(1,
					u.getId());

			List<ItemsScheduleDay> itemsScheduleDays = query.getResultList();

			if (itemsScheduleDays != null) {
				for (ItemsScheduleDay itemsScheduleDay : itemsScheduleDays) {

					itemsScheduleDay.setStatus("D");
					itemsScheduleDay = em.merge(itemsScheduleDay);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.severe(e);
		}

		return u;
	}

	public ItemAttributeToDatePacket addUpdateItemAttributeToDate(EntityManager em,
			ItemAttributeToDatePacket itemAttributeToDatePacket) {
		ItemAttributeToDate date = itemAttributeToDatePacket.getItemAttributeToDate();
		date.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		date = em.merge(date);
		itemAttributeToDatePacket.setItemAttributeToDate(date);
		return itemAttributeToDatePacket;

	}

	public ItemAttributeToDatePacket deleteItemAttributeToDate(EntityManager em,
			ItemAttributeToDatePacket itemAttributeToDatePacket) {
		ItemAttributeToDate date = itemAttributeToDatePacket.getItemAttributeToDate();
		ItemAttributeToDate newdate = em.find(ItemAttributeToDate.class, date.getId());
		newdate.setStatus("D");
		newdate.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		newdate.setUpdatedBy(date.getUpdatedBy());
		newdate = em.merge(newdate);
		itemAttributeToDatePacket.setItemAttributeToDate(newdate);
		return itemAttributeToDatePacket;

	}

	public List<ItemAttributeToDate> getItemAttributeToDate(EntityManager em, String locationId) {

		String sqlForGlobalItemId = " select i from ItemAttributeToDate i where i.locationId =? and i.status not in ('D','I') ";
		@SuppressWarnings("unchecked")
		List<ItemAttributeToDate> resultList = em.createQuery(sqlForGlobalItemId).setParameter(1, locationId)
				.getResultList();
		List<ItemAttributeToDate> itemAttributeToDates = new ArrayList<ItemAttributeToDate>();
		for (ItemAttributeToDate itemToDate : resultList) {
			ItemsAttribute i = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,
					ItemsAttribute.class, itemToDate.getItemAttributeId());
			if (i != null) {
				itemToDate.setDisplayName(i.getDisplayName());
			}

			itemAttributeToDates.add(itemToDate);
		}
		
		return itemAttributeToDates;

	}

	public OrderAdditionalQuestionsPacket addUpdateOrderAdditionalQuestions(EntityManager em,
			OrderAdditionalQuestionsPacket packet, HttpServletRequest httpRequest) {
		try {
			List<OrderAdditionalQuestion> newAdditionalQuestionsList = new ArrayList<OrderAdditionalQuestion>();
			List<OrderAdditionalQuestion> additionalQuestionsList = packet.getOrderAdditionalQuestionsList();
			for (OrderAdditionalQuestion additionalQuestions : additionalQuestionsList) {
				if (additionalQuestions.getId() == null) {
					additionalQuestions.setId(new StoreForwardUtility().generateDynamicIntId(em,
							additionalQuestions.getLocationId(), httpRequest, "order_additional_question"));

					additionalQuestions.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				additionalQuestions.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				additionalQuestions = em.merge(additionalQuestions);
				newAdditionalQuestionsList.add(additionalQuestions);
			}
			packet.setOrderAdditionalQuestionsList(newAdditionalQuestionsList);
			return packet;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	public OrderAdditionalQuestionsPacket deleteOrderAdditionalQuestions(EntityManager em,
			OrderAdditionalQuestionsPacket packet) {
		try {
			List<OrderAdditionalQuestion> newAdditionalQuestionsList = new ArrayList<OrderAdditionalQuestion>();
			List<OrderAdditionalQuestion> additionalQuestionsList = packet.getOrderAdditionalQuestionsList();
			for (OrderAdditionalQuestion additionalQuestions : additionalQuestionsList) {
				OrderAdditionalQuestion newAdditionalQuestions = (OrderAdditionalQuestion) new CommonMethods()
						.getObjectById("OrderAdditionalQuestion", em, OrderAdditionalQuestion.class,
								additionalQuestions.getId());
				newAdditionalQuestions.setStatus("D");
				newAdditionalQuestions.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				newAdditionalQuestions.setUpdatedBy(additionalQuestions.getUpdatedBy());
				additionalQuestions = em.merge(newAdditionalQuestions);
				newAdditionalQuestionsList.add(additionalQuestions);
			}
			packet.setOrderAdditionalQuestionsList(newAdditionalQuestionsList);
			return packet;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	public List<OrderAdditionalQuestion> getOrderAdditionalQuestions(EntityManager em, String locationId) {

		try {
			String sqlForGlobalItemId = " select i from OrderAdditionalQuestion i where  i.status!= 'D' and i.locationId= '"+ locationId +"' order by i.displaySequence asc ";
			
			@SuppressWarnings("unchecked")
			List<OrderAdditionalQuestion> resultList = em.createQuery(sqlForGlobalItemId, OrderAdditionalQuestion.class)
					.getResultList();

			return resultList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	public AdditionalQuestionAnswerPacket addUpdateAdditionalQuestionAnswer(EntityManager em,
			AdditionalQuestionAnswerPacket packet) {
		try {
			List<AdditionalQuestionAnswer> newAdditionalQuestionAnswerList = new ArrayList<AdditionalQuestionAnswer>();
			List<AdditionalQuestionAnswer> additionalQuestionAnswersList = packet.getAdditionalQuestionAnswersList();
			List<OrderToServerAssignment> newOrderToServerAssignmentList = new ArrayList<OrderToServerAssignment>();
			List<OrderToServerAssignment> orderToServerAssignmentList = packet.getOrderToServerAssignmentList();

			for (AdditionalQuestionAnswer additionalQuestionAnswer : additionalQuestionAnswersList) {
				if (additionalQuestionAnswer.getId() == null) {
					additionalQuestionAnswer.setId(new StoreForwardUtility().generateUUID());
					additionalQuestionAnswer.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				additionalQuestionAnswer.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				additionalQuestionAnswer = em.merge(additionalQuestionAnswer);
				newAdditionalQuestionAnswerList.add(additionalQuestionAnswer);
			}
			if (orderToServerAssignmentList != null && orderToServerAssignmentList.size() > 0) {
				for (OrderToServerAssignment orderToServerAssignment : orderToServerAssignmentList) {
					if (orderToServerAssignment.getId() == 0) {
						orderToServerAssignment.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					}
					orderToServerAssignment.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					orderToServerAssignment = em.merge(orderToServerAssignment);
					newOrderToServerAssignmentList.add(orderToServerAssignment);
				}
			}

			packet.setAdditionalQuestionAnswersList(newAdditionalQuestionAnswerList);
			packet.setOrderToServerAssignmentList(newOrderToServerAssignmentList);
			return packet;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	public AdditionalQuestionAnswerPacket getAdditionalQuestionAnswerPacket(EntityManager em, String orderId) {

		try {
			String sqlForAdditionalQuestionAnswer = " select i from AdditionalQuestionAnswer i where  i.orderHeaderId=?";
			@SuppressWarnings("unchecked")
			List<AdditionalQuestionAnswer> additionalQuestionAnswers = em.createQuery(sqlForAdditionalQuestionAnswer)
					.setParameter(1, orderId).getResultList();

			String sqlForOrderToServerAssignment = " select i from OrderToServerAssignment i where  i.status!= 'D' and i.orderId=?";
			@SuppressWarnings("unchecked")
			List<OrderToServerAssignment> orderToServerAssignments = em.createQuery(sqlForOrderToServerAssignment)
					.setParameter(1, orderId).getResultList();

			AdditionalQuestionAnswerPacket additionalQuestionAnswerPacket = new AdditionalQuestionAnswerPacket();
			additionalQuestionAnswerPacket.setAdditionalQuestionAnswersList(additionalQuestionAnswers);
			additionalQuestionAnswerPacket.setOrderToServerAssignmentList(orderToServerAssignments);
			return additionalQuestionAnswerPacket;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	ItemsAttribute updateItemsAttributeAvailability(ItemsAttribute itemsAttribute, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {
		ItemsAttribute dbAttribute = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,
				ItemsAttribute.class, itemsAttribute.getId());
		if (dbAttribute != null) {
			dbAttribute.setAvailability(itemsAttribute.isAvailability());
			dbAttribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			dbAttribute.setUpdatedBy(itemsAttribute.getUpdatedBy());
			itemsAttribute = em.merge(dbAttribute);
			return itemsAttribute;
		}
		return null;
	}

	public List<ItemAttributeToDate> getItemAttributeToDate(EntityManager em, int itemAttributeId, String date) {

		String sqlForGlobalItemId = " select i from ItemAttributeToDate i where i.itemAttributeId =? and i.date=? and i.status not in ('D','I') ";
		@SuppressWarnings("unchecked")
		List<ItemAttributeToDate> resultList = em.createQuery(sqlForGlobalItemId).setParameter(1, itemAttributeId)
				.setParameter(2, date).getResultList();
		return resultList;

	}
}
