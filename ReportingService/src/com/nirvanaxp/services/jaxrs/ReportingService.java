/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.amazonaws.crypto.examples.KMSEncryptDecrypt;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.CalculationRoundUp;
import com.nirvanaxp.services.jaxrs.packets.DailySalesForecast;
import com.nirvanaxp.services.jaxrs.packets.EodSummaryPacket;
import com.nirvanaxp.services.jaxrs.packets.FeedbackPacket;
import com.nirvanaxp.services.jaxrs.packets.PaidInOutCash;
import com.nirvanaxp.services.jaxrs.packets.RevenueByCategoryPacket;
import com.nirvanaxp.services.jaxrs.packets.RevenueByDiscountPacket;
import com.nirvanaxp.services.jaxrs.packets.RevenueByReportingCategoryPacket;
import com.nirvanaxp.services.jaxrs.packets.RevenueByTax;
import com.nirvanaxp.services.jaxrs.packets.RevenueByVoidOrderPacket;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.CategorywiseReporting;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.DiscountItemReporting;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.ItemGroupReporting;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.OrderSourceGroupwiseReporting;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.PaymentMethodwiseReporting;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RestaurantDetails;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RevenueByCategory;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RevenueByDiscount;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RevenueByOrderSource;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RevenueByPaymentMethodType;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RevenueByReportingCategory;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RevenueByVoidOrder;
import com.nirvanaxp.services.util.email.SendEmail;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.locations.LocationsType;
import com.nirvanaxp.types.entities.locations.LocationsType_;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.orders.OrderHeader;

@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class ReportingService extends AbstractNirvanaService {

	@Context
	protected HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(ReportingService.class.getName());


	@GET
	@Path("/getFeedbackDetails/{locationId}/{fromDate}/{toDate}")
	public String getFeedbackDetails(@PathParam("locationId") String locationId, @PathParam("fromDate") String fromDate,
			@PathParam("toDate") String toDate, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;

		em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		FeedbackPacket feedbackPacket = new FeedbackPacket();

		String startGmtDate = fromDate + " 00:00:00";
		String endGmtDate = toDate + " 23:59:59";

		TimezoneTime timezone = new TimezoneTime();

		startGmtDate = timezone.getDateAccordingToGMTForConnection(em, startGmtDate, locationId);
		endGmtDate = timezone.getDateAccordingToGMTForConnection(em, endGmtDate, locationId);

		int oneStar = 0;
		int twoStar = 0;
		int threeStar = 0;
		int fourStar = 0;
		int fiveStar = 0;
		int totalFeedbackCount = 0;

		try {
			String confirm_sql = "SELECT ROUND(avg(star_value)) AS rating  FROM reservations r JOIN order_header oh ON r.id = oh.reservations_id "
					+ " JOIN locations l ON oh.locations_id = l.id "
					+ " JOIN request_type rt ON r.request_type_id = rt.id "
					+ " JOIN reservations_status rs ON r.reservations_status_id = rs.id "
					+ " JOIN `customer_experience` ce ON ce.order_header_id = oh.id "
					+ " LEFT JOIN `users` u ON ce.users_id = u.id  JOIN `smileys` s ON ce.smiley_id = s.id "
					+ " LEFT JOIN `users_to_feeback_details` uf ON uf.users_id = u.id "
					+ " LEFT JOIN feedback_field ff ON uf.feedback_details_id = ff.id "
					+ " JOIN `feedback_question` fq ON ce.feedback_question_id = fq.id "
					+ " WHERE ce.created between  ? and ? AND r.locations_id = ? GROUP BY ce.created ORDER BY  ce.created desc   ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(confirm_sql).setParameter(1, startGmtDate)
					.setParameter(2, endGmtDate).setParameter(3, locationId).getResultList();
			totalFeedbackCount = resultList.size();
			for (Object[] objRow : resultList) {
				if ((Integer) objRow[1] == 1) {
					oneStar++;
				} else if ((Integer) objRow[1] == 2) {
					twoStar++;
				} else if ((Integer) objRow[1] == 3) {
					threeStar++;
				} else if ((Integer) objRow[1] == 4) {
					fourStar++;
				} else if ((Integer) objRow[1] == 5) {
					fiveStar++;
				}
			}

			feedbackPacket.setStar1(oneStar);
			feedbackPacket.setStar2(twoStar);
			feedbackPacket.setStar3(threeStar);
			feedbackPacket.setStar4(fourStar);
			feedbackPacket.setStar5(fiveStar);
			feedbackPacket.setTotalFeedback(totalFeedbackCount);
			// calculate total feedback

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

		return new JSONUtility(httpRequest).convertToJsonString(feedbackPacket);

	}


	@GET
	@Path("/getFeedbackReportByLocationIdAndToAndFromDate/{businessId}/{fromDate}/{toDate}")
	public String getFeedbackReportByLocationIdAndToAndFromDate(@PathParam("businessId") int businessId,
			@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		List<FeedbackReport> ans = new ArrayList<FeedbackReport>();
		try {
			fromDate += " 00:00:00";
			toDate += " 23:59:59";
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_feedback(?,?,?)").setParameter(1, businessId)
					.setParameter(2, fromDate).setParameter(3, toDate).getResultList();
			if (resultList != null && resultList.size() > 0) {

				for (Object[] objRow : resultList) {

					// if this has primary key not 0
					FeedbackReport feebFeedbackReport = new FeedbackReport();
					int i = 0;
					feebFeedbackReport.setOrderNumber(((String) objRow[i++]));
					feebFeedbackReport.setFirstName((String) objRow[i++]);
					feebFeedbackReport.setLastName((String) objRow[i++]);
					feebFeedbackReport.setDate((String) objRow[i++]);
					feebFeedbackReport.setTime((String) objRow[i++]);
					feebFeedbackReport.setPartySize((Integer) objRow[i++]);
					feebFeedbackReport.setUserId((String) objRow[i++]);
					feebFeedbackReport.setPhoneOrEmail((String) objRow[i++]);
					feebFeedbackReport.setComment(((String) objRow[i++]));
					feebFeedbackReport.setTableName(((String) objRow[i++]));
					feebFeedbackReport.setFeedbackTypeName(((String) objRow[i++]));
					feebFeedbackReport.setImageName(((String) objRow[i++]));
					feebFeedbackReport.setRating(new BigDecimal((int) objRow[i++]));
					feebFeedbackReport.setManagerResponse(((String) objRow[i++]));
					feebFeedbackReport.setDateOfBirth(((String) objRow[i++]));
					feebFeedbackReport.setDateOfAnniversary(((String) objRow[i++]));
					feebFeedbackReport.setCreated(((String) objRow[i++]));
					feebFeedbackReport.setId(((String) objRow[i++]));
					ans.add(feebFeedbackReport);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getReservationReportByLocationIdAndToAndFromDate/{locationId}/{fromDate}/{toDate}")
	public String getReservationReportByLocationIdAndDate(@PathParam("locationId") String locationId,
			@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {

			List<FeedbackReport> ans = new ArrayList<FeedbackReport>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String sql = "SELECT r.id, r.date, r.time, r.party_size,r.users_id, r.first_name, r.last_name, r.phone_number, r.email,  "
					+ " r.comment, rt.request_name, l.name AS table_name, rs.display_name AS reservation_status,"
					+ " oh.id AS order_number FROM reservations r "
					+ " LEFT JOIN order_header oh ON r.id = oh.reservations_id "
					+ "LEFT JOIN locations l ON oh.locations_id = l.id "
					+ "LEFT JOIN request_type rt ON r.request_type_id = rt.id "
					+ " LEFT JOIN reservations_status rs ON r.reservations_status_id = rs.id "
					+ "WHERE r.date  between  ? and ? AND rs.name not in ('Void Walkin') and r.locations_id = "
					+ locationId + " " + " ORDER BY  `r`.`time` DESC ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, fromDate).setParameter(2, toDate)
					.setParameter(3, locationId).getResultList();

			if (resultList != null && resultList.size() > 0) {

				for (Object[] objRow : resultList) {

					// if this has primary key not 0
					FeedbackReport feebFeedbackReport = new FeedbackReport();
					feebFeedbackReport.setId(((String) objRow[1]));
					feebFeedbackReport.setDate(((String) objRow[2]));
					feebFeedbackReport.setTime(((String) objRow[3]));
					feebFeedbackReport.setPartySize(((Integer) objRow[4]));
					feebFeedbackReport.setUserId(((String) objRow[5]));
					feebFeedbackReport.setFirstName(((String) objRow[6]));
					feebFeedbackReport.setLastName(((String) objRow[7]));
					feebFeedbackReport.setPhoneNumber(((String) objRow[8]));
					feebFeedbackReport.setEmail(((String) objRow[9]));
					feebFeedbackReport.setComment(((String) objRow[10]));
					feebFeedbackReport.setRequestName(((String) objRow[11]));
					feebFeedbackReport.setTableName(((String) objRow[12]));
					feebFeedbackReport.setReservationStatus(((String) objRow[13]));
					feebFeedbackReport.setOrderNumber(((String) objRow[14]));

					ans.add(feebFeedbackReport);

				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}


	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive() {
		return true;
	}

	@Override
	protected NirvanaLogger getNirvanaLogger() {
		return logger;
	}

	public String batchIdString(EntityManager em, String locationId, String sDate, String eDate) {
		String batchIds = "";
		TimezoneTime timezoneTime = new TimezoneTime();
		String startDate = timezoneTime.getDateAccordingToGMT(sDate + " 00:00:00", locationId, em);
		String endDate = timezoneTime.getDateAccordingToGMT(eDate + " 23:59:59", locationId, em);

		String queryString = " SELECT id FROM batch_detail " + "  where (startTime >= ? and startTime <= ?) "
				+ " or (startTime <=? and closetime>=?)";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, startDate)
				.setParameter(2, endDate).setParameter(3, startDate).setParameter(4, endDate).getResultList();

		for (int i = 0; i < resultList.size(); i++) {
			if (i == (resultList.size() - 1)) {
				batchIds += "'" + resultList.get(i) + "'";
			} else {
				batchIds += "'" + resultList.get(i) + "'" + ",";
			}
		}

		return batchIds;
	}

	@GET
	@Path("/getBusinessDetailPrintReceiptByFromDateToDateBusinessId/{businessId}/{fromDate}/{toDate}")
	public String getBusinessDetailPrintReceiptByFromDateToDateBusinessId(@PathParam("businessId") int businessId,
			@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate) throws Exception {
		EntityManager em = null;

		try {
			// input gmt date only
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			RestaurantDetails details = getRestaurantDetails(businessId, fromDate, toDate, em);
			if (details != null) {
				Location location = getLocationsDetails(businessId, em);
				if (location != null) {
					Address address = location.getAddress();
					if (address != null) {
						details.setAddress1(address.getAddress1());
						details.setAddress2(address.getAddress2());
						details.setName(location.getName());
					}
				}
				details.setPaymentMethodTypes(getRevenueByPaymentMethodType(businessId, fromDate, toDate, em));
				details.setRevenueByGrossCategoriesPacket(getRevenueByGrossCategory(businessId, fromDate, toDate, em));
				details.setRevenueByNetCategoriesPacket(getRevenueByNetCategory(businessId, fromDate, toDate, em));
				details.setRevenueByGrossReportingCategoriesPacket(
						getRevenueByGrossReportingCategory(businessId, fromDate, toDate, em));
				details.setRevenueByNetReportingCategoriesPacket(
						getRevenueByNetReportingCategory(businessId, fromDate, toDate, em));
				details.setRevenueByVoidOrdersPacket(getRevenueByVoidOrder(businessId, fromDate, toDate, em));
				details.setRevenueByCancelItemPacket(getRevenueByCancelItem(businessId, fromDate, toDate, em));
				details.setRevenueByDiscountPacket(getRevenueByDiscount(businessId, fromDate, toDate, em));
				details.setRevenueByDiscountItemPacket(getRevenueByDiscountItem(businessId, fromDate, toDate, em));
				details.setRevenueByTax(getRevenueByTax(businessId, fromDate, toDate, em));
				details.setPaidInOutCash(getPaidInOutCash(businessId, fromDate, toDate, em));
				details.setCashRegister(getCashRegister(businessId, fromDate, toDate, em));
				details.setUserLedger(getUserLedger(businessId, fromDate, toDate, em));
				try {
					details.setRevenueByReportingCategoriesPacket(getRevenueByReportingCategory(businessId, fromDate, toDate, em));
				} catch (Exception e) {
					logger.severe(e);
				}

			}

			return new JSONUtility(httpRequest).convertToJsonString(details);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	private RestaurantDetails getRestaurantDetails(int businessId, String fromDate, String toDate, EntityManager em) {
		RestaurantDetails details = null;

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call p_end_of_day_for_print_receipt(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();

		if (resultList != null && resultList.size() > 0) {
			for (Object[] objRow : resultList) {
				// if this has primary key not 0
				details = new RestaurantDetails();
				int i = 0;
				if (objRow[i] != null)
					details.setGuestCount(((BigDecimal) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					details.setOrderCount((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setSubtotal((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setPriceDiscount((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setTotalTaxes((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setTotal((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setGratuity((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setAmountPaid((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setBalanceDue((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setChangeDue((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setCashReceived((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setCashTips((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setTotalCashDeposit((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setCardReceived((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setTotalCardTips((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setTotalCardDeposit((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					details.setTotalCreditTerm((BigDecimal) objRow[i] + "");

			}
		}

		return details;
	}

	private Location getLocationsDetails(int businessId, EntityManager em) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Location> cl = builder.createQuery(Location.class);
			Root<Location> l = cl.from(Location.class);
			TypedQuery<Location> query = em
					.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Location_.businessId), businessId)),
							builder.and(builder.equal(l.get(Location_.locationsId), '0'))));
			return query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}

	private List<RevenueByPaymentMethodType> getRevenueByPaymentMethodType(int businessId, String fromDate,
			String toDate, EntityManager em) {
		RevenueByPaymentMethodType revenue = null;
		List<RevenueByPaymentMethodType> paymentMethodTypes = new ArrayList<RevenueByPaymentMethodType>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultListPaymentMethodType = em
				.createNativeQuery("call p_revenue_payment_method_receipt(?,?,?)").setParameter(1, businessId)
				.setParameter(2, fromDate).setParameter(3, toDate).getResultList();
		if (resultListPaymentMethodType != null && resultListPaymentMethodType.size() > 0) {
			for (Object[] objRow : resultListPaymentMethodType) {
				// if this has primary key not 0
				revenue = new RevenueByPaymentMethodType();
				int i = 0;
				if (objRow[i] != null)
					revenue.setPaymentMethodTypeName(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setGuestCount((int) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					revenue.setAmountPaid((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
				i++;
				if (objRow[i] != null)
					revenue.setCashTip((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
				i++;
				if (objRow[i] != null)
					revenue.setCardTip((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
				i++;
				if (objRow[i] != null)
					revenue.setCreditTermTip(
							(new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
				i++;
				if (objRow[i] != null)
					revenue.setSubTotal((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
				i++;
				if (objRow[i] != null)
					revenue.setPriceGratuity(
							(new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
				i++;
				if (objRow[i] != null)
					revenue.setTotalTax((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
				i++;
				if (objRow[i] != null)
					revenue.setOpdCount((int) objRow[i] + "");

				paymentMethodTypes.add(revenue);

			}

		}
		return paymentMethodTypes;
	}

	private RevenueByCategoryPacket getRevenueByGrossCategory(int businessId, String fromDate, String toDate,
			EntityManager em) {
		List<RevenueByCategory> revenueByCategories = new ArrayList<RevenueByCategory>();
		RevenueByCategory revenueByCategory = null;
		@SuppressWarnings("unchecked")
		List<Object[]> resultListCategoryRevenue = em.createNativeQuery("call p_revenue_category_receipt(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();
		RevenueByCategoryPacket revenueByCategoryPacket = new RevenueByCategoryPacket();
		int count = 0;
		BigDecimal total = new BigDecimal(0);

		if (resultListCategoryRevenue != null && resultListCategoryRevenue.size() > 0) {
			for (Object[] objRow : resultListCategoryRevenue) {
				// if this has primary key not 0
				revenueByCategory = new RevenueByCategory();
				int i = 0;
				if (objRow[i] != null)
					revenueByCategory.setCategoryId(((int) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenueByCategory.setCategoryName((String) objRow[i] + "");
				i++;
				if (objRow[i] != null) {
					revenueByCategory.setItemQty(((int) objRow[i]) + "");
					count = count + ((int) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					revenueByCategory
							.setTotal((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
					total = total.add((new BigDecimal((double) objRow[i])));
				}
				i++;

				revenueByCategories.add(revenueByCategory);

			}

		}
		revenueByCategoryPacket.setRevenueByCategory(revenueByCategories);
		revenueByCategoryPacket.setItemQty(count + "");
		revenueByCategoryPacket.setTotal(total.setScale(2, RoundingMode.HALF_UP) + "");
		return revenueByCategoryPacket;
	}

	private RevenueByCategoryPacket getRevenueByNetCategory(int businessId, String fromDate, String toDate,
			EntityManager em) {
		List<RevenueByCategory> revenueByCategories = new ArrayList<RevenueByCategory>();
		RevenueByCategory revenueByCategory = null;
		@SuppressWarnings("unchecked")
		List<Object[]> resultListCategoryRevenue = em.createNativeQuery("call p_revenue_category(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();
		RevenueByCategoryPacket revenueByCategoryPacket = new RevenueByCategoryPacket();
		int count = 0;
		BigDecimal total = new BigDecimal(0);

		if (resultListCategoryRevenue != null && resultListCategoryRevenue.size() > 0) {
			for (Object[] objRow : resultListCategoryRevenue) {
				// if this has primary key not 0
				revenueByCategory = new RevenueByCategory();
				int i = 0;
				if (objRow[i] != null)
					revenueByCategory.setCategoryId(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenueByCategory.setCategoryName((String) objRow[i] + "");
				i++;
				if (objRow[i] != null) {
					revenueByCategory.setItemQty(((int) objRow[i]) + "");
					count = count + ((int) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					revenueByCategory
							.setTotal((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
					total = total.add((new BigDecimal((double) objRow[i])));
				}
				i++;

				revenueByCategories.add(revenueByCategory);

			}

		}
		revenueByCategoryPacket.setRevenueByCategory(revenueByCategories);
		revenueByCategoryPacket.setItemQty(count + "");
		revenueByCategoryPacket.setTotal(total.setScale(2, RoundingMode.HALF_UP) + "");
		return revenueByCategoryPacket;
	}

	private RevenueByCategoryPacket getRevenueByGrossReportingCategory(int businessId, String fromDate, String toDate,
			EntityManager em) {
		List<RevenueByCategory> revenueByCategories = new ArrayList<RevenueByCategory>();
		RevenueByCategory revenueByCategory = null;
		@SuppressWarnings("unchecked")
		List<Object[]> resultListCategoryRevenue = em.createNativeQuery("call p_revenue_category_apr(?,?,?,0)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();
		RevenueByCategoryPacket revenueByCategoryPacket = new RevenueByCategoryPacket();
		int count = 0;
		BigDecimal total = new BigDecimal(0);

		if (resultListCategoryRevenue != null && resultListCategoryRevenue.size() > 0) {
			for (Object[] objRow : resultListCategoryRevenue) {
				// if this has primary key not 0
				revenueByCategory = new RevenueByCategory();
				int i = 0;
				if (objRow[i] != null)
					revenueByCategory.setCategoryId(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenueByCategory.setCategoryName((String) objRow[i] + "");
				i++;
				if (objRow[i] != null) {
					revenueByCategory.setItemQty(((int) objRow[i]) + "");
					count = count + ((int) objRow[i]);
				}
				i++;
				i++;
				if (objRow[i] != null) {
					revenueByCategory
							.setTotal((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
					total = total.add((new BigDecimal((double) objRow[i])));
				}
				i++;

				revenueByCategories.add(revenueByCategory);

			}

		}
		revenueByCategoryPacket.setRevenueByCategory(revenueByCategories);
		revenueByCategoryPacket.setItemQty(count + "");
		revenueByCategoryPacket.setTotal(total.setScale(2, RoundingMode.HALF_UP) + "");
		return revenueByCategoryPacket;
	}

	private RevenueByCategoryPacket getRevenueByNetReportingCategory(int businessId, String fromDate, String toDate,
			EntityManager em) {
		List<RevenueByCategory> revenueByCategories = new ArrayList<RevenueByCategory>();
		RevenueByCategory revenueByCategory = null;
		@SuppressWarnings("unchecked")
		List<Object[]> resultListCategoryRevenue = em.createNativeQuery("call p_revenue_category_apr(?,?,?,0)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();
		RevenueByCategoryPacket revenueByCategoryPacket = new RevenueByCategoryPacket();
		int count = 0;
		BigDecimal total = new BigDecimal(0);

		if (resultListCategoryRevenue != null && resultListCategoryRevenue.size() > 0) {
			for (Object[] objRow : resultListCategoryRevenue) {
				// if this has primary key not 0
				revenueByCategory = new RevenueByCategory();
				int i = 0;
				if (objRow[i] != null)
					revenueByCategory.setCategoryId(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenueByCategory.setCategoryName((String) objRow[i] + "");
				i++;
				if (objRow[i] != null) {
					revenueByCategory.setItemQty(((int) objRow[i]) + "");
					count = count + ((int) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					revenueByCategory
							.setTotal((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
					total = total.add((new BigDecimal((double) objRow[i])));
				}
				i++;

				revenueByCategories.add(revenueByCategory);

			}

		}
		revenueByCategoryPacket.setRevenueByCategory(revenueByCategories);
		revenueByCategoryPacket.setItemQty(count + "");
		revenueByCategoryPacket.setTotal(total.setScale(2, RoundingMode.HALF_UP) + "");
		return revenueByCategoryPacket;
	}

	private RevenueByVoidOrderPacket getRevenueByVoidOrder(int businessId, String fromDate, String toDate,
			EntityManager em) {
		RevenueByVoidOrder revenueByVoidOrder = null;
		List<RevenueByVoidOrder> revenueByVoidOrders = new ArrayList<RevenueByVoidOrder>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultListVoidRevenue = em.createNativeQuery("call p_void_orderForEODSummary(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();

		RevenueByVoidOrderPacket revenueByVoidOrderPacket = new RevenueByVoidOrderPacket();
		BigInteger count = BigInteger.valueOf(0);
		BigDecimal total = new BigDecimal(0);
		BigDecimal balanceDue = new BigDecimal(0);

		if (resultListVoidRevenue != null && resultListVoidRevenue.size() > 0) {
			for (Object[] objRow : resultListVoidRevenue) {
				// if this has primary key not 0
				revenueByVoidOrder = new RevenueByVoidOrder();
				int i = 0;
				if (objRow[i] != null) {
					revenueByVoidOrder.setOrderCount(((BigInteger) objRow[i]) + "");
					count = count.add(((BigInteger) objRow[i]));
				}
				i++;
				if (objRow[i] != null) {
					revenueByVoidOrder.setBalanceDue(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");
					balanceDue = balanceDue.add((BigDecimal) objRow[i]);
				}
				i++;
				if (objRow[i] != null)
					revenueByVoidOrder.setOrderStatusName((String) objRow[i] + "");
				i++;

				if (objRow[i] != null) {
					revenueByVoidOrder.setTotal(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");
					total = total.add(((BigDecimal) objRow[i]));
				}
				i++;

				revenueByVoidOrders.add(revenueByVoidOrder);

			}

		}
		revenueByVoidOrderPacket.setBalanceDue(balanceDue.setScale(2, RoundingMode.HALF_UP) + "");
		revenueByVoidOrderPacket.setOrderCount(count + "");
		revenueByVoidOrderPacket.setTotal(total.setScale(2, RoundingMode.HALF_UP) + "");
		revenueByVoidOrderPacket.setRevenueByVoidOrder(revenueByVoidOrders);
		return revenueByVoidOrderPacket;
	}

	private RevenueByVoidOrderPacket getRevenueByCancelItem(int businessId, String fromDate, String toDate,
			EntityManager em) {
		RevenueByVoidOrder revenueByVoidOrder = null;
		List<RevenueByVoidOrder> revenueByVoidOrders = new ArrayList<RevenueByVoidOrder>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultListVoidRevenue = em.createNativeQuery("call p_cancel_itemForEODSummary(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();

		RevenueByVoidOrderPacket revenueByVoidOrderPacket = new RevenueByVoidOrderPacket();
		int count = 0;
		BigDecimal total = new BigDecimal(0);
		BigDecimal balanceDue = new BigDecimal(0);

		if (resultListVoidRevenue != null && resultListVoidRevenue.size() > 0) {
			for (Object[] objRow : resultListVoidRevenue) {
				// if this has primary key not 0
				revenueByVoidOrder = new RevenueByVoidOrder();
				int i = 0;
				if (objRow[i] != null) {
					revenueByVoidOrder.setOrderCount(((int) objRow[i]) + "");
					count = count + ((int) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					revenueByVoidOrder.setBalanceDue(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");
					balanceDue = balanceDue.add((BigDecimal) objRow[i]);
				}
				i++;

				if (objRow[i] != null) {
					revenueByVoidOrder.setTotal(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");
					total = total.add(((BigDecimal) objRow[i]));
				}
				i++;
				if (objRow[i] != null)
					revenueByVoidOrder.setOrderStatusName((String) objRow[i] + "");
				i++;
				revenueByVoidOrders.add(revenueByVoidOrder);

			}

		}
		revenueByVoidOrderPacket.setBalanceDue(balanceDue.setScale(2, RoundingMode.HALF_UP) + "");
		revenueByVoidOrderPacket.setOrderCount(count + "");
		revenueByVoidOrderPacket.setTotal(total.setScale(2, RoundingMode.HALF_UP) + "");
		revenueByVoidOrderPacket.setRevenueByVoidOrder(revenueByVoidOrders);
		return revenueByVoidOrderPacket;
	}

	private RevenueByDiscountPacket getRevenueByDiscount(int businessId, String fromDate, String toDate,
			EntityManager em) {
		RevenueByDiscount revenueByDiscount = null;
		RevenueByDiscountPacket revenueByDiscountPacket = new RevenueByDiscountPacket();

		List<RevenueByDiscount> revenueByDiscountList = new ArrayList<RevenueByDiscount>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultListDiscountRevenue = em.createNativeQuery("call p_revenue_discount_receipt(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();

		int discountCount = 0;
		BigDecimal discountTotal = new BigDecimal(0);
		if (resultListDiscountRevenue != null && resultListDiscountRevenue.size() > 0) {
			for (Object[] objRow : resultListDiscountRevenue) {
				// if this has primary key not 0
				revenueByDiscount = new RevenueByDiscount();
				int i = 0;
				if (objRow[i] != null) {
					revenueByDiscount.setDiscountCount(((int) objRow[i]) + "");
					discountCount = discountCount + ((int) objRow[i]);
				}
				i++;
				if (objRow[i] != null)
					revenueByDiscount.setDiscountName((String) objRow[i]);
				i++;
				if (objRow[i] != null) {
					revenueByDiscount.setDiscountTotal(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");
					discountTotal = discountTotal.add((BigDecimal) objRow[i]);
				}
				i++;
				revenueByDiscountList.add(revenueByDiscount);

			}

		}
		revenueByDiscountPacket.setRevenueByDiscount(revenueByDiscountList);
		revenueByDiscountPacket.setDiscountCount(discountCount + "");
		revenueByDiscountPacket.setDiscountTotal(discountTotal.setScale(2, RoundingMode.HALF_UP) + "");
		return revenueByDiscountPacket;
	}

	private RevenueByDiscountPacket getRevenueByDiscountItem(int businessId, String fromDate, String toDate,
			EntityManager em) {
		RevenueByDiscount revenueByDiscount = null;
		RevenueByDiscountPacket revenueByDiscountPacket = new RevenueByDiscountPacket();

		List<RevenueByDiscount> revenueByDiscountList = new ArrayList<RevenueByDiscount>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultListDiscountRevenue = em.createNativeQuery("call p_revenue_discount_item_receipt(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();

		int discountCount = 0;
		BigDecimal discountTotal = new BigDecimal(0);
		if (resultListDiscountRevenue != null && resultListDiscountRevenue.size() > 0) {
			for (Object[] objRow : resultListDiscountRevenue) {
				// if this has primary key not 0
				revenueByDiscount = new RevenueByDiscount();
				int i = 0;
				if (objRow[i] != null) {
					revenueByDiscount.setDiscountCount(((int) objRow[i]) + "");
					discountCount = discountCount + ((int) objRow[i]);
				}
				i++;
				if (objRow[i] != null)
					revenueByDiscount.setDiscountName((String) objRow[i]);
				i++;
				if (objRow[i] != null) {
					revenueByDiscount.setDiscountTotal(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");
					discountTotal = discountTotal.add((BigDecimal) objRow[i]);
				}
				i++;
				revenueByDiscountList.add(revenueByDiscount);

			}

		}
		revenueByDiscountPacket.setRevenueByDiscount(revenueByDiscountList);
		revenueByDiscountPacket.setDiscountCount(discountCount + "");
		revenueByDiscountPacket.setDiscountTotal(discountTotal.setScale(2, RoundingMode.HALF_UP) + "");
		return revenueByDiscountPacket;
	}

	private RevenueByTax getRevenueByTax(int businessId, String fromDate, String toDate, EntityManager em) {
		RevenueByTax revenueByTax = new RevenueByTax();
		@SuppressWarnings("unchecked")
		List<Object[]> resultListTaxRevenue = em.createNativeQuery("call p_revenue_tax_receipt(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();

		if (resultListTaxRevenue != null && resultListTaxRevenue.size() > 0) {
			for (Object[] objRow : resultListTaxRevenue) {
				int i = 0;
				if (objRow[i] != null)
					revenueByTax.setTaxName1(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenueByTax.setTaxName2(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenueByTax.setTaxName3(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenueByTax.setTaxName4(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenueByTax.setTaxRate1((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					revenueByTax.setTaxRate2((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					revenueByTax.setTaxRate3((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					revenueByTax.setTaxRate4((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					revenueByTax.setGratuity((BigDecimal) objRow[i] + "");
				i++;
				if (objRow[i] != null)
					revenueByTax.setPriceTax1(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");
				i++;
				if (objRow[i] != null)
					revenueByTax.setPriceTax2(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");
				i++;
				if (objRow[i] != null)
					revenueByTax.setPriceTax3(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");
				i++;
				if (objRow[i] != null)
					revenueByTax.setPriceTax4(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");
				i++;
				if (objRow[i] != null)
					revenueByTax.setPriceGratuity(((BigDecimal) objRow[i]).setScale(2, RoundingMode.HALF_UP) + "");

			}

		}

		return revenueByTax;
	}

	private List<PaidInOutCash> getPaidInOutCash(int businessId, String fromDate, String toDate, EntityManager em) {

		PaidInOutCash revenue = null;

		List<PaidInOutCash> paidInOutCashList = new ArrayList<PaidInOutCash>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultListPaidInOut = em.createNativeQuery("call p_paid_in_out_receipt(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();

		if (resultListPaidInOut != null && resultListPaidInOut.size() > 0) {
			for (Object[] objRow : resultListPaidInOut) {

				// if this has primary key not 0
				revenue = new PaidInOutCash();
				int i = 0;
				if (objRow[i] != null)
					revenue.setName(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenue.setTotal((new BigDecimal((double) objRow[i])).setScale(2, RoundingMode.HALF_UP) + "");

				paidInOutCashList.add(revenue);
			}
		}

		return paidInOutCashList;
	}

	private List<PaidInOutCash> getCashRegister(int businessId, String fromDate, String toDate, EntityManager em) {

		PaidInOutCash revenue = null;

		List<PaidInOutCash> paidInOutCashList = new ArrayList<PaidInOutCash>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultListPaidInOut = em.createNativeQuery("call p_cash_register_receipt(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();

		if (resultListPaidInOut != null && resultListPaidInOut.size() > 0) {
			for (Object[] objRow : resultListPaidInOut) {

				// if this has primary key not 0
				revenue = new PaidInOutCash();
				int i = 0;
				if (objRow[i] != null)
					revenue.setName(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenue.setTotal((new BigDecimal((double) objRow[i])).setScale(2, RoundingMode.HALF_UP) + "");

				paidInOutCashList.add(revenue);
			}
		}

		return paidInOutCashList;
	}

	private List<PaidInOutCash> getUserLedger(int businessId, String fromDate, String toDate, EntityManager em) {

		PaidInOutCash revenue = null;

		List<PaidInOutCash> paidInOutCashList = new ArrayList<PaidInOutCash>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultListPaidInOut = em.createNativeQuery("call p_user_ledger_reciept(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();

		if (resultListPaidInOut != null && resultListPaidInOut.size() > 0) {
			for (Object[] objRow : resultListPaidInOut) {

				// if this has primary key not 0
				revenue = new PaidInOutCash();
				int i = 0;
				if (objRow[i] != null)
					revenue.setName(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenue.setTotal((new BigDecimal((double) objRow[i])).setScale(2, RoundingMode.HALF_UP) + "");

				paidInOutCashList.add(revenue);
			}
		}

		return paidInOutCashList;
	}

	@GET
	@Path("/gettest")
	public String gettest() throws Exception {

		KMSEncryptDecrypt kms = new KMSEncryptDecrypt();
		System.out.println("Encrypted message is:");
		byte[] enc = kms.encrypt("hello");
		System.out.println(new String(enc));
		System.out.println("Decrypted message is:");
		System.out.println(kms.decrypt(enc));

		return null;
	}

	@GET
	@Path("/getAllTipsBatchWise/{businessId}/{startDate}/{endDate}")
	public String getAllTipsBatchWise(@PathParam("businessId") int businessId, @PathParam("startDate") String startDate,
			@PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {

		List<Tip> list = new ArrayList<Tip>();

		EntityManager em = null;
		try { 
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			if (businessId > 0) {
				list = getAllBatchwiseTip(em,businessId, startDate, endDate, "0" );
				return new JSONUtility(httpRequest).convertToJsonString(list);
			} else {

				List<Location> locationList =  getAllRootLocationsWithoutGlobalBusinessId(httpRequest, em);
				for(Location location:locationList){
					
					List<Tip> localList =  getAllBatchwiseTip(em,location.getBusinessId(), startDate, endDate, "0" );
					list.addAll(localList);
				}
				return new JSONUtility(httpRequest).convertToJsonString(list);
			
			}
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

 

	}
	
	@GET
	@Path("/getAllTipsBatchWise/{businessId}/{startDate}/{endDate}/{employeeId}")
	public String getAllTipsBatchWise(@PathParam("businessId") int businessId, @PathParam("startDate") String startDate,
			@PathParam("endDate") String endDate, @PathParam("employeeId") String employeeId)
			throws Exception, InvalidSessionException, IOException {

		List<Tip> list = new ArrayList<Tip>();

		EntityManager em = null;
		try { 
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			if (businessId > 0) {
				list = getAllBatchwiseTip(em,businessId, startDate, endDate, employeeId );
				return new JSONUtility(httpRequest).convertToJsonString(list);
			} else {

				List<Location> locationList =  getAllRootLocationsWithoutGlobalBusinessId(httpRequest, em);
				for(Location location:locationList){
					
					List<Tip> localList =  getAllBatchwiseTip(em,location.getBusinessId(), startDate, endDate, employeeId );
					list.addAll(localList);
				}
				return new JSONUtility(httpRequest).convertToJsonString(list);
			
			}
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

 

	}
	
	private List<Tip> getAllBatchwiseTip(EntityManager em,int businessId, String startDate,
			String endDate,String employeeId){

		CalculationRoundUp calculationRoundUp = new CalculationRoundUp();
		List<Tip> tips = new ArrayList<Tip>();
		// Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
		List<Object[]> resultList = em.createNativeQuery("call tips_by_order_report(?,?,?,?)")
				.setParameter(1, businessId).setParameter(2, startDate).setParameter(3, endDate)
				.setParameter(4, employeeId).getResultList();

		if (resultList != null && resultList.size() > 0) {

			for (Object[] objRow : resultList) {

				int i = 0;
				Tip t = new Tip();

				if (((String) objRow[i]) != null) {
					t.setBatchStartTime(((String) objRow[i]));
				}

				i++;
				if (objRow[i] != null) {
					t.setShiftId(((int) objRow[i])+"");
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setEmployeeHoursInShift(((String) objRow[i] ));
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setBatchNumber(Integer.parseInt(((String) objRow[i])));
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setEmployeeLoginId((String) objRow[i]);
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setEmployeeName(((String) objRow[i]));
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setEmployeePosition(((String) objRow[i]));
				}

				i++;
				if (objRow[i] != null) {
					t.setOrderNumber(((String) objRow[i]));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setTotalCash(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setTotalCash(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setTotalCard(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setTotalCard(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setTotalCreditTerm(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setTotalCreditTerm(
							calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.set_15EFood(calculationRoundUp.roundOffTo5Digit(Double.parseDouble(((String) objRow[i]))));
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.set_15KNonAlcoholicBeverage(
							calculationRoundUp.roundOffTo5Digit(Double.parseDouble(((String) objRow[i]))));
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.set_15FAlcoholic(
							calculationRoundUp.roundOffTo5Digit(Double.parseDouble(((String) objRow[i]))));
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setOthers(calculationRoundUp.roundOffTo5Digit(Double.parseDouble(((String) objRow[i]))));

				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setTax(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setTax(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setDirectCashTip(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setDirectCashTip(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setDirectCardTip(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setDirectCardTip(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setDirectCreditTermTip(
							calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setDirectCreditTermTip(
							calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setIndirectCashTip(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setIndirectCashTip(
							calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setIndirectCardTip(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setIndirectCardTip(
							calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setIndirectCreditTermTip(
							calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setIndirectCreditTermTip(
							calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				/*
				 * if (objRow[i] != null && objRow[i] instanceof Double) {
				 * t.setDiffrence(((Double) objRow[i]).doubleValue()); }else
				 * if (objRow[i] != null && objRow[i] instanceof BigDecimal)
				 * { t.setDiffrence(((BigDecimal) objRow[i]).doubleValue());
				 * }
				 */
				t.setDiffrence(0);

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setAutoGratuity(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setAutoGratuity(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null) {
					t.setOrderSourceGroupId(((int) objRow[i]) + "");
				}

				i++;
				if (objRow[i] != null) {
					t.setOrderSourceGroupName(((String) objRow[i]));
				}

				i++;
				if (objRow[i] != null) {
					t.setSectionId(((String) objRow[i]));
				}

				i++;
				if (objRow[i] != null) {
					t.setSectionName(((String) objRow[i]));
				}
				if(t.getBatchNumber()>0){
					tips.add(t);
				}
				

			}
		}

		return tips;
	
	}

	@GET
	@Path("/getAllTipsBatchWiseForTesting/{businessId}/{startDate}/{endDate}/{employeeId}")
	public String getAllTipsBatchWiseForTesting(@PathParam("businessId") int businessId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate,
			@PathParam("employeeId") String employeeId) throws Exception, InvalidSessionException, IOException {

		List<Tip> tips = new ArrayList<Tip>();

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
			List<Object[]> resultList = em.createNativeQuery("call tips_by_order_report(?,?,?,?)")
					.setParameter(1, businessId).setParameter(2, startDate).setParameter(3, endDate)
					.setParameter(4, employeeId).getResultList();

			if (resultList != null && resultList.size() > 0) {

				for (Object[] objRow : resultList) {

					int i = 0;
					Tip t = new Tip();

					if (((String) objRow[i]) != null) {
						t.setBatchStartTime(((String) objRow[i]));
					}

					i++;
					if (objRow[i] != null) {
						t.setShiftId(((String) objRow[i]));
					}

					i++;
					if (((String) objRow[i]) != null) {
						t.setEmployeeHoursInShift(((String)objRow[i]));
					}

					i++;
					if (((String) objRow[i]) != null) {
						t.setBatchNumber(Integer.parseInt(((String) objRow[i])));
					}

					i++;
					if (((String) objRow[i]) != null) {
						t.setEmployeeLoginId((String) objRow[i]);
					}

					i++;
					if (((String) objRow[i]) != null) {
						t.setEmployeeName(((String) objRow[i]));
					}

					i++;
					if (((String) objRow[i]) != null) {
						t.setEmployeePosition(((String) objRow[i]));
					}

					i++;
					if (objRow[i] != null) {
						t.setOrderNumber(((String) objRow[i]));
					}

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setTotalCash(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setTotalCash(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setTotalCard(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setTotalCard(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setTotalCreditTerm(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setTotalCreditTerm(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					if (((String) objRow[i]) != null) {
						t.set_15EFood(Double.parseDouble(((String) objRow[i])));
					}

					i++;
					if (((String) objRow[i]) != null) {
						t.set_15KNonAlcoholicBeverage(Double.parseDouble(((String) objRow[i])));
					}

					i++;
					if (((String) objRow[i]) != null) {
						t.set_15FAlcoholic(Double.parseDouble(((String) objRow[i])));
					}

					i++;
					if (((String) objRow[i]) != null) {
						t.setOthers(Double.parseDouble(((String) objRow[i])));

					}

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setTax(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setTax(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setDirectCashTip(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setDirectCashTip(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setDirectCardTip(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setDirectCardTip(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setDirectCreditTermTip(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setDirectCreditTermTip(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setIndirectCashTip(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setIndirectCashTip(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setIndirectCardTip(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setIndirectCardTip(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setIndirectCreditTermTip(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setIndirectCreditTermTip(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					/*
					 * if (objRow[i] != null && objRow[i] instanceof Double) {
					 * t.setDiffrence(((Double) objRow[i]).doubleValue()); }else
					 * if (objRow[i] != null && objRow[i] instanceof BigDecimal)
					 * { t.setDiffrence(((BigDecimal) objRow[i]).doubleValue());
					 * }
					 */
					t.setDiffrence(0);

					i++;
					if (objRow[i] != null && objRow[i] instanceof Double) {
						t.setAutoGratuity(((Double) objRow[i]).doubleValue());
					} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
						t.setAutoGratuity(((BigDecimal) objRow[i]).doubleValue());
					}

					i++;
					if (objRow[i] != null) {
						t.setOrderSourceGroupId(((int) objRow[i]) + "");
					}

					i++;
					if (objRow[i] != null) {
						t.setOrderSourceGroupName(((String) objRow[i]));
					}

					i++;
					if (objRow[i] != null) {
						t.setSectionId(((String) objRow[i]));
					}

					i++;
					if (objRow[i] != null) {
						t.setSectionName(((String) objRow[i]));
					}

					tips.add(t);

				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(tips);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getTipsByEmployeeTipDistributionAndByReportingCategory/{businessId}/{startDate}/{endDate}/{employeeId}")
	public String getTipsByEmployeeTipDistributionAndByReportingCategory(@PathParam("businessId") String businessId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate,
			@PathParam("employeeId") String employeeId) throws Exception, InvalidSessionException, IOException {

		EntityManager em = null;
		List<TipByOrder> list = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			 Location location1 = (Location) new CommonMethods().getObjectById("Location", em,Location.class, businessId);
			if (businessId !=null) {
				LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, businessId);
				String restaurantCode = null;
				if(locationSetting!=null){
					restaurantCode = locationSetting.getBusinessCode();
				}
				list = getTipByOrderList(location1.getBusinessId(), startDate, endDate, employeeId, em,restaurantCode);
				return new JSONUtility(httpRequest).convertToJsonString(list);
			} else {
				list = new ArrayList<TipByOrder>();
				List<Location> locationList =  getAllRootLocationsWithoutGlobalBusinessId(httpRequest, em);
				for(Location location:locationList){
					if(location!=null){
						
						LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, location.getId());
						String restaurantCode = null;
						if(locationSetting!=null){
							restaurantCode = locationSetting.getBusinessCode();
						}
						List<TipByOrder> tipByOrdersLocal = getTipByOrderList(location.getBusinessId(), startDate, endDate, employeeId, em,restaurantCode);
						if(tipByOrdersLocal!=null && tipByOrdersLocal.size()>0){
							 list.addAll(tipByOrdersLocal);
						}
					}
					
					
				}
				return new JSONUtility(httpRequest).convertToJsonString(list);
			}
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	private List<TipByOrder> getTipByOrderList(int businessId, String startDate, String endDate, String employeeId,
			EntityManager em,String restaurantCode) {
		List<TipByOrder> tips = new ArrayList<TipByOrder>();
		CalculationRoundUp calculationRoundUp = new CalculationRoundUp();
		List<Object[]> resultList = em.createNativeQuery("call p_employee_tip_distribution_with_category(?,?,?,?)")
				.setParameter(1, businessId).setParameter(2, startDate).setParameter(3, endDate)
				.setParameter(4, employeeId).getResultList();

		if (resultList != null && resultList.size() > 0) {

			for (Object[] objRow : resultList) {

				int i = 0;
				TipByOrder t = new TipByOrder();

				if (((Timestamp) objRow[i]) != null) {
					t.setBatchStartTime(((Timestamp) objRow[i]).toString());
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setShiftName(((String) objRow[i]));
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setEmployeeHoursInShift((((String)objRow[i])));
				}

				i++;
				if (objRow[i] instanceof BigDecimal && ((BigDecimal) objRow[i]) != null) {
					t.setBatchNumber(((BigDecimal) objRow[i]).intValue());
				}else
				if ( objRow[i] instanceof Integer && ((Integer) objRow[i]) != null) {
					t.setBatchNumber(((Integer) objRow[i]).intValue());
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setEmployeeName(((String) objRow[i]));
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setEmployeePosition(((String) objRow[i]));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setTotalCash(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setTotalCash(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setTotalCard(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setTotalCard(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setTotalCreditTerm(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setTotalCreditTerm(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (((Double) objRow[i]) != null) {
					t.setDept15E(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i])));
				}

				i++;
				if (((Double) objRow[i]) != null) {
					t.setDept15K(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i])));
				}

				i++;
				if (((Double) objRow[i]) != null) {
					t.setDept15F(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i])));
				}

				i++;
				if (((Double) objRow[i]) != null) {
					t.setDeptOthers(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i])));

				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setDirectCashTip(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setDirectCashTip(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setDirectCardTip(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setDirectCardTip(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setDirectCreditTermTip(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setDirectCreditTermTip(
							calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setIndirectCashTip(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setIndirectCashTip(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setIndirectCardTip(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setIndirectCardTip(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setIndirectCreditTermTip(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setIndirectCreditTermTip(
							calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}

				i++;
				if (((String) objRow[i]) != null) {
					t.setEmployeeLoginId((String) objRow[i]);
				}

				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setTax(calculationRoundUp.roundOffTo5Digit(((Double) objRow[i]).doubleValue()));
				} else if (objRow[i] != null && objRow[i] instanceof BigDecimal) {
					t.setTax(calculationRoundUp.roundOffTo5Digit(((BigDecimal) objRow[i]).doubleValue()));
				}
				i++;
				if (((String) objRow[i]) != null) {
					t.setSectionName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					t.setSectionId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					t.setOrderSourceGroupId((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					t.setOrderSourceGroupName((String) objRow[i]);
				}
				i++;
				if (objRow[i] != null && objRow[i] instanceof Double) {
					t.setGratuity((double) objRow[i]);
				}
				t.setRestaurantCode(restaurantCode);
				if(t.getBatchNumber()>0){
					tips.add(t);
				}
			
			}

		}
		return tips;

	}

	@GET
	@Path("/getRevenueByCategory/{locationId}/{startDate}/{endDate}")
	public String getRevenueByCategory(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also
			OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();

			TimezoneTime timezoneTime = new TimezoneTime();
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
			String pickStartDate = timezoneTime.getDateAccordingToGMT(startDate, locationId, em);
			String pickEndDate = timezoneTime.getDateAccordingToGMT(endDate, locationId, em);

			@SuppressWarnings("unchecked")
			List<Object[]> result = em.createNativeQuery("call p_revenue_category_apr(?,?,?,0)")
					.setParameter(1, location.getBusinessId()).setParameter(2, pickStartDate)
					.setParameter(3, pickEndDate).getResultList();
			List<RevenueByCategory> list = new ArrayList<RevenueByCategory>();
			if (result != null && result.size() > 0) {
				for (Object[] objRow : result) {
					RevenueByCategory revenue = new RevenueByCategory();
					// if this has primary key not 0

					int i = 0;
					if (objRow[i] != null)
						revenue.setCategoryId(((String) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setCategoryName(((String) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setItemQty(((int) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setTotal(((double) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setSubTotal(((double) objRow[i]) + "");
					i++;

					list.add(revenue);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getRevenueByOrderSource/{locationId}/{startDate}/{endDate}")
	public String getRevenueByOrderSource(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also

			TimezoneTime timezoneTime = new TimezoneTime();
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
			String pickStartDate = timezoneTime.getDateAccordingToGMT(startDate, locationId, em);
			String pickEndDate = timezoneTime.getDateAccordingToGMT(endDate, locationId, em);

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_revenue_by_order_source(?,?,?)")
					.setParameter(1, location.getBusinessId()).setParameter(2, pickStartDate)
					.setParameter(3, pickEndDate).getResultList();
			List<RevenueByOrderSource> list = new ArrayList<RevenueByOrderSource>();
			if (resultList != null && resultList.size() > 0) {
				for (Object[] objRow : resultList) {
					RevenueByOrderSource revenue = new RevenueByOrderSource();
					// if this has primary key not 0

					int i = 0;
					if (objRow[i] != null)
						revenue.setGuestCount(((int) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setOrderSourceGroupName(((String) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setOrderSourceName(((String) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setAmount(((double) objRow[i]) + "");
					i++;

					list.add(revenue);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getRevenueByOrderSourceGroup/{locationId}/{startDate}/{endDate}")
	public String getRevenueByOrderSourceGroup(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also

			TimezoneTime timezoneTime = new TimezoneTime();
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
			String pickStartDate = timezoneTime.getDateAccordingToGMT(startDate, locationId, em);
			String pickEndDate = timezoneTime.getDateAccordingToGMT(endDate, locationId, em);

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_revenue_source(?,?,?)")
					.setParameter(1, location.getBusinessId()).setParameter(2, pickStartDate)
					.setParameter(3, pickEndDate).getResultList();
			List<RevenueByOrderSource> list = new ArrayList<RevenueByOrderSource>();
			if (resultList != null && resultList.size() > 0) {
				for (Object[] objRow : resultList) {
					RevenueByOrderSource revenue = new RevenueByOrderSource();
					// if this has primary key not 0

					int i = 0;
					if (objRow[i] != null)
						revenue.setGuestCount(((int) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setOrderSourceGroupName(((String) objRow[i]) + "");
					i++;

					if (objRow[i] != null)
						revenue.setAmount(((double) objRow[i]) + "");
					i++;

					list.add(revenue);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getCategorywiseReporting/{locationId}/{startDate}/{endDate}/{isLast}/{isWeekly}/{isMonthly}/{isYearly}/{isDaily}/{isToDate}")
	public String getCategorywiseReporting(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate,
			@PathParam("isLast") int isLast, @PathParam("isWeekly") int isWeekly, @PathParam("isMonthly") int isMonthly,
			@PathParam("isYearly") int isYearly, @PathParam("isDaily") int isDaily, @PathParam("isToDate") int isToDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also

			TimezoneTime timezoneTime = new TimezoneTime();
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
		 

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call sp_for_reporting_category_BI(?,?,?,?,?,?,?,?,?)")
					.setParameter(1, location.getBusinessId()).setParameter(2, startDate)
					.setParameter(3, endDate).setParameter(4, isLast).setParameter(5, isWeekly)
					.setParameter(6, isMonthly).setParameter(7, isYearly).setParameter(8, isDaily).setParameter(9, isToDate).getResultList();
			List<CategorywiseReporting> list = new ArrayList<CategorywiseReporting>();
			if (resultList != null && resultList.size() > 0) {
				for (Object[] objRow : resultList) {
					CategorywiseReporting revenue = new CategorywiseReporting();
					// if this has primary key not 0

					int i = 0;
					if (objRow[i] != null)
						revenue.setCategoryId(((String) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setCategoryName(((String) objRow[i]) + "");
					i++;

					if (objRow[i] != null)
						revenue.setItemQty(((int) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setTotal(((double) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setSubTotal(((double) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setTax1(((double) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setTax2(((double) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setTax3(((double) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setTax4(((double) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setDiscount(((double) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setGratuity(((double) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setFromDate(((String) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setToDate(((String) objRow[i]) + "");
					i++;

					list.add(revenue);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	int getLocationTypeForSupplier(HttpServletRequest httpRequest, EntityManager em) {
		int id = 0;
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<LocationsType> criteria = builder.createQuery(LocationsType.class);
			Root<LocationsType> r = criteria.from(LocationsType.class);
			TypedQuery<LocationsType> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(LocationsType_.name), "Supplier")));
			LocationsType supplierLocationType = query.getSingleResult();
			id = query.getSingleResult().getId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return id;
	}

	int getLocationTypeForInHouseProduction(HttpServletRequest httpRequest, EntityManager em) {
		int id = 0;

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<LocationsType> criteria = builder.createQuery(LocationsType.class);
			Root<LocationsType> r = criteria.from(LocationsType.class);
			TypedQuery<LocationsType> query = em.createQuery(
					criteria.select(r).where(builder.equal(r.get(LocationsType_.name), "In House Production")));
			id = query.getSingleResult().getId();
			return id;
		} catch (Exception e) {

			logger.severe(e);
		}
		return id;

	}

	List<Location> getAllRootLocationsWithoutGlobalBusinessId(HttpServletRequest httpRequest, EntityManager em) {
		int supplierLocationType = getLocationTypeForSupplier(httpRequest, em);
		int inHouseLocationType = getLocationTypeForInHouseProduction(httpRequest, em);

		// get all locations that are not supplier type
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> cl = builder.createQuery(Location.class);
		Root<Location> l = cl.from(Location.class);
		TypedQuery<Location> query = em.createQuery(cl.select(l)
				.where(builder.and(builder.equal(l.get(Location_.locationsId), 0),
						builder.equal(l.get(Location_.isGlobalLocation), 0),
						builder.notEqual(l.get(Location_.locationsTypeId), supplierLocationType),
						builder.notEqual(l.get(Location_.locationsTypeId), inHouseLocationType))));

		return query.getResultList();

	}
	
	@GET
	@Path("/getOrderSouceGroupReporting/{locationId}/{startDate}/{endDate}/{isLast}/{isWeekly}/{isMonthly}/{isYearly}/{isDaily}/{isToDate}")
	public String getOrderSouceGroupReporting(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate,
			@PathParam("isLast") int isLast, @PathParam("isWeekly") int isWeekly, @PathParam("isMonthly") int isMonthly,
			@PathParam("isYearly") int isYearly, @PathParam("isDaily") int isDaily, @PathParam("isToDate") int isToDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also

			TimezoneTime timezoneTime = new TimezoneTime();
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
			 

			List<OrderSourceGroupwiseReporting> list = new ReportingServiceBean()
					.getOrderSouceGroupReporting(em, location, startDate, endDate, isLast, isWeekly, isMonthly, isYearly, isDaily, isToDate);
					

			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@GET
	@Path("/getItemGroupReporting/{startDate}/{endDate}/{isLast}/{isWeekly}/{isMonthly}/{isYearly}/{isDaily}/{isToDate}")
	public String getItemGroupReporting(
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate,
			@PathParam("isLast") int isLast, @PathParam("isWeekly") int isWeekly, @PathParam("isMonthly") int isMonthly,
			@PathParam("isYearly") int isYearly, @PathParam("isDaily") int isDaily, @PathParam("isToDate") int isToDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also

			TimezoneTime timezoneTime = new TimezoneTime();
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
			List<ItemGroupReporting> list = new ReportingServiceBean().getItemGroupReporting(em, startDate, endDate, isLast, isWeekly, isMonthly, isYearly, isDaily, isToDate);
			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	@GET
	@Path("/getOrderSourceGroupReportingAccountLevel/{startDate}/{endDate}/{isLast}/{isWeekly}/{isMonthly}/{isYearly}/{isDaily}/{isToDate}")
	public String getOrderSourceGroupReportingAccountLevel(
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate,
			@PathParam("isLast") int isLast, @PathParam("isWeekly") int isWeekly, @PathParam("isMonthly") int isMonthly,
			@PathParam("isYearly") int isYearly, @PathParam("isDaily") int isDaily, @PathParam("isToDate") int isToDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also

			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
			
			List<OrderSourceGroupwiseReporting> list = new ReportingServiceBean().getOrderSourceGroupReportingAccountLevel(em, startDate, endDate, isLast, isWeekly, isMonthly, isYearly, isDaily, isToDate);
			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	@GET
	@Path("/getPaymentMethodwiseReporting/{locationId}/{startDate}/{endDate}/{isLast}/{isWeekly}/{isMonthly}/{isYearly}/{isDaily}/{isToDate}")
	public String getPaymentMethodwiseReporting(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate,
			@PathParam("isLast") int isLast, @PathParam("isWeekly") int isWeekly, @PathParam("isMonthly") int isMonthly,
			@PathParam("isYearly") int isYearly, @PathParam("isDaily") int isDaily, @PathParam("isToDate") int isToDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also

			TimezoneTime timezoneTime = new TimezoneTime();
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
			 

			List<PaymentMethodwiseReporting> list= new ReportingServiceBean().getPaymentMethodwiseReporting(em, location, startDate, endDate, isLast, isWeekly, isMonthly, isYearly, isDaily, isToDate);
			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@GET
	@Path("/getRevenuePaymentMethodwiseAccountLevelReporting/{startDate}/{endDate}/{isLast}/{isWeekly}/{isMonthly}/{isYearly}/{isDaily}/{isToDate}")
	public String getRevenuePaymentMethodwiseAccountLevelReporting(
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate,
			@PathParam("isLast") int isLast, @PathParam("isWeekly") int isWeekly, @PathParam("isMonthly") int isMonthly,
			@PathParam("isYearly") int isYearly, @PathParam("isDaily") int isDaily, @PathParam("isToDate") int isToDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also

			TimezoneTime timezoneTime = new TimezoneTime();
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
		

			List<PaymentMethodwiseReporting> list= new ReportingServiceBean().getRevenuePaymentMethodwiseAccountLevelReporting(em, startDate, endDate, 
					isLast, isWeekly, isMonthly, isYearly, isDaily, isToDate);
			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@GET
	@Path("/getDiscountItemAccountLevelReporting/{startDate}/{endDate}/{isLast}/{isWeekly}/{isMonthly}/{isYearly}/{isDaily}/{isToDate}")
	public String getDiscountItemAccountLevelReporting(
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate,
			@PathParam("isLast") int isLast, @PathParam("isWeekly") int isWeekly, @PathParam("isMonthly") int isMonthly,
			@PathParam("isYearly") int isYearly, @PathParam("isDaily") int isDaily, @PathParam("isToDate") int isToDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also

			TimezoneTime timezoneTime = new TimezoneTime();
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
		

			List<DiscountItemReporting> list= new ReportingServiceBean().getDiscountItemAccountLevelReporting(em, startDate, endDate, 
					isLast, isWeekly, isMonthly, isYearly, isDaily, isToDate);
			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@GET
	@Path("/getDiscountItemLocationLevelReporting/{locationId}/{startDate}/{endDate}/{isLast}/{isWeekly}/{isMonthly}/{isYearly}/{isDaily}/{isToDate}")
	public String getDiscountItemLocationLevelReporting(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate,
			@PathParam("isLast") int isLast, @PathParam("isWeekly") int isWeekly, @PathParam("isMonthly") int isMonthly,
			@PathParam("isYearly") int isYearly, @PathParam("isDaily") int isDaily, @PathParam("isToDate") int isToDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also
			TimezoneTime timezoneTime = new TimezoneTime();
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
		 

			List<DiscountItemReporting> list= new ReportingServiceBean().getDiscountItemLocationLevelReporting(em,location, startDate, endDate, 
					isLast, isWeekly, isMonthly, isYearly, isDaily, isToDate);
			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	@GET
	@Path("/sendEmailForEODSummary/{updatedBy}/{location_id}/{business_id}/{batchDetailId}/{email}")
	public boolean sendEmailForEODSummary(@PathParam("updatedBy") String updatedBy,
			@PathParam("location_id") String locationId, @PathParam("business_id") int businessId,
			@PathParam("batchDetailId") int batchDetailId, @PathParam("email") String email)
			throws FileNotFoundException, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			String sessionId = httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			BatchDetail batchDetail = null;
			try {
				batchDetail = em.find(BatchDetail.class, batchDetailId);
			} catch (Exception e1) {

				logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
			}
			List<OrderHeader> headers = new ArrayList<OrderHeader>();
			try {
				headers = new OrderManagementServiceBean().getAllOrderPaymentDetailsByUserIdLocationBatchWise(httpRequest, em, updatedBy, locationId,
						sessionId);

			} catch (Exception e) {
				logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
			}
			// boolean isDone = false;

			if (batchDetail != null) {

				// Send Email EOD summary
				try {
					tx = em.getTransaction();
					tx.begin();
					new OrderManagementServiceBean().sendEODSettledmentMail(httpRequest, em, updatedBy, locationId,
							sessionId, batchDetail, headers, email);
					tx.commit();
				} catch (Exception e) {
					logger.severe(e);
				}

				return true;
			} else {
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(
						MessageConstants.ERROR_CODE_BATCH_ID_NOT_PRESENT_WITH_DATE__EXCEPTION,
						MessageConstants.ERROR_MESSAGE_BATCH_ID_NOT_PRESENT_WITH_DATE_DISPLAY_MESSAGE, null));

			}

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/dailySalesForecast/{date}/{locationId}")
	public String dailySalesForecast(@PathParam("date") String date,
			@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		List<DailySalesForecast> ans = new ArrayList<DailySalesForecast>();
		try {
			String fromDate = date.substring(0,10)+" 00:00:00";
			String toDate =  date.substring(0,10)+" 23:59:59";
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call Daily_sales_data_for_forecast(?,?,?)").setParameter(1, locationId)
					.setParameter(2, fromDate).setParameter(3, toDate).getResultList();

			if (resultList != null && resultList.size() > 0) {

				for (Object[] objRow : resultList) {

					// if this has primary key not 0
					DailySalesForecast data = new DailySalesForecast();
					int i = 0;
					data.setDate(((String) objRow[i++]));
					data.setAmount(((String) objRow[i++]));
					
					ans.add(data);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/monthlySalesForecast/{date}/{locationId}")
	public String monthlySalesForecast(@PathParam("date") String date,
			@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		List<DailySalesForecast> ans = new ArrayList<DailySalesForecast>();
		try {
			String fromDate = date.substring(0,10)+" 00:00:00";
			String toDate =  date.substring(0,10)+" 23:59:59";
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call monthly_sales_data_for_forecast(?,?,?)").setParameter(1, locationId)
					.setParameter(2, fromDate).setParameter(3, toDate).getResultList();

			if (resultList != null && resultList.size() > 0) {

				for (Object[] objRow : resultList) {

					// if this has primary key not 0
					DailySalesForecast data = new DailySalesForecast();
					int i = 0;
					data.setDate(((String) objRow[i++]));
					data.setAmount(((String) objRow[i++]));
					
					ans.add(data);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getDailySalesDataForecasting/{startdate}/{enddate}/{locationId}")
	public String getDailySalesDataForecasting(@PathParam("startdate") String startdate,@PathParam("enddate") String enddate,
			@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		List<DailySalesForecast> ans = new ArrayList<DailySalesForecast>();
		try {
		 
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call Daily_sales_data_for_forecast(?,?,?)").setParameter(1, locationId)
					.setParameter(2, startdate).setParameter(3, enddate).getResultList();

			if (resultList != null && resultList.size() > 0) {

				for (Object[] objRow : resultList) {

					// if this has primary key not 0
					DailySalesForecast data = new DailySalesForecast();
					int i = 0;
					data.setDate(((String) objRow[i++]));
					data.setAmount(((String) objRow[i++]));
					
					ans.add(data);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/getMonthlySalesDataForecasting/{startdate}/{enddate}/{locationId}")
	public String getMonthlySalesDataForecasting(@PathParam("startdate") String startdate,@PathParam("enddate") String enddate,
			@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		List<DailySalesForecast> ans = new ArrayList<DailySalesForecast>();
		try {
		 
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call monthly_sales_data_for_forecast(?,?,?)").setParameter(1, locationId)
					.setParameter(2, startdate).setParameter(3, enddate).getResultList();

			if (resultList != null && resultList.size() > 0) {

				for (Object[] objRow : resultList) {

					// if this has primary key not 0
					DailySalesForecast data = new DailySalesForecast();
					int i = 0;
					data.setDate(((String) objRow[i++]));
					data.setAmount(((BigDecimal) objRow[i++])+"");
					
					ans.add(data);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/checkBatchCloseHEB/{locationId}")
	public String checkBatchCloseHEB(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			 
			tx = em.getTransaction();
			tx.begin();
			String batchIds  = getActiveBatch(em, locationId);
			Location l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
			List<String> emailList = new ArrayList<String>();
			emailList.add("naman223054@gmail.com");
			emailList.add("dipakwadekar123@gmail.com");
			emailList.add("amolbhandge@gmail.com");
			emailList.add("naushil@posnirvana.com");
			emailList.add("Prashant.chavan1812@gmail.com");
			emailList.add("support@nirvanaxp.com");
			emailList.add("kris@nirvanaxp.com");
			emailList.add("sharath@nirvanaxp.com");
			emailList.add("vaibhavadeshmukh69@gmail.com");
			 
			
			if(batchIds!= null && batchIds.trim().length()>0){
				// send email code
				String emailBody = "Urgent attention needed , "+l.getName()+" Batch is not closed. Batch id still open = "+batchIds;
				String emailSubject = "!!! URGENT !!! "+l.getName()+" BATCH NOT CLOSED !!! ";
				for(String emailAddress:emailList){
					new SendEmail().sendHEBBatchCloseEmailToSupport(em, httpRequest, emailBody, locationId, emailAddress,emailSubject);
				}
				return new JSONUtility(httpRequest).convertToJsonString("Urgent attention needed !! Batch having issue !!"+batchIds);
			}else{
				String emailBody = ""+l.getName()+"  batch closed successfully. Please verify reports for the current batch. Thank you!";
				String emailSubject = ""+l.getName()+" batch  closed  Successfully !!! ";
				for(String emailAddress:emailList){
					new SendEmail().sendHEBBatchCloseEmailToSupport(em, httpRequest, emailBody, locationId, emailAddress,emailSubject);
				}
			}
			
			
			
			tx.commit();
			 	
			return new JSONUtility(httpRequest).convertToJsonString("Batch Settled sucessfully");
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
	
	public String getActiveBatch(EntityManager em, String locationId) {
		String batchIds = "";
		String queryString = " select id from batch_detail where location_id=? and status='A'";
		try {
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId)
					.getResultList();

			for (int i = 0; i < resultList.size(); i++) {
				if (i == (resultList.size() - 1)) {
					batchIds += "'" + resultList.get(i) + "'";
				} else {
					batchIds += "'" + resultList.get(i) + "'" + ",";
				}
			}
		} catch (Exception e) {
			logger.severe(e);
		}

		return batchIds;
	}
	private RevenueByReportingCategoryPacket getRevenueByReportingCategory(int businessId, String fromDate, String toDate,
			EntityManager em) {
		List<RevenueByReportingCategory> revenueByReportingCategories = new ArrayList<RevenueByReportingCategory>();
		RevenueByReportingCategory revenueByReportingCategory = null;
		@SuppressWarnings("unchecked")
		List<Object[]> resultListReportingCategoryRevenue = em.createNativeQuery("call revenue_reporting_category_for_print(?,?,?)")
				.setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();
		RevenueByReportingCategoryPacket revenueByCategoryPacket = new RevenueByReportingCategoryPacket();
		int count = 0;
		BigDecimal total = new BigDecimal(0);
		BigDecimal priceSelling = new BigDecimal(0);
		if (resultListReportingCategoryRevenue != null && resultListReportingCategoryRevenue.size() > 0) {
			for (Object[] objRow : resultListReportingCategoryRevenue) {
				 logger.severe("objRow=============================================================="+objRow);
					
				revenueByReportingCategory = new RevenueByReportingCategory();
				int i = 0;
				if (objRow[i] != null)
					logger.severe("objRow[i]=============================================================="+objRow[i]);
				
					revenueByReportingCategory.setItemName(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null) {
					logger.severe("objRow[i]=============================================================="+objRow[i]);
					
					revenueByReportingCategory.setItemQty(((int) objRow[i]) + "");
					count = count + ((int) objRow[i]);
				}
				i++;
				if (objRow[i] != null) {
					logger.severe("objRow[i]=============================================================="+objRow[i]);
					
					revenueByReportingCategory.setTotal((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
					total = total.add((new BigDecimal((double) objRow[i])));
				}
				i++;
				if (objRow[i] != null) {
					logger.severe("objRow[i]=============================================================="+objRow[i]);
					
					revenueByReportingCategory.setPriceSelling((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
					priceSelling = priceSelling.add((new BigDecimal((double) objRow[i])));
				}
				i++;
				if (objRow[i] != null)
					logger.severe("objRow[i]=============================================================="+objRow[i]);
				
					revenueByReportingCategory.setCategoryName((String) objRow[i] + "");
			
				
				

				revenueByReportingCategories.add(revenueByReportingCategory);

			}

		}
		revenueByCategoryPacket.setRevenueByReportingCategories(revenueByReportingCategories);
		revenueByCategoryPacket.setItemQty(count + "");
		revenueByCategoryPacket.setTotal(total.setScale(2, RoundingMode.HALF_UP) + "");
		 logger.severe("revenueByCategoryPacket=============================================================="+revenueByCategoryPacket.toString());
			
		return revenueByCategoryPacket;
	}
	
	@GET
	@Path("/getEODSummary/{locationId}/{startDate}/{endDate}")
	public String getEODSummary(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 11:59:59";
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
		 

			@SuppressWarnings("unchecked")
			List<Object[]> result = em.createNativeQuery("call p_end_of_day_BI(?,?,?)")
					.setParameter(1, location.getBusinessId()).setParameter(2, startDate)
					.setParameter(3, endDate).getResultList();
			List<EodSummaryPacket> list = new ArrayList<EodSummaryPacket>();
			if (result != null && result.size() > 0) {
				for (Object[] objRow : result) {
					EodSummaryPacket revenue = new EodSummaryPacket();
					// if this has primary key not 0

					int i = 0;
					if (objRow[i] != null)
						revenue.setBatchId(((String) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setPointOfServiceCount(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setOrderCount(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setSubTotal(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setPriceDiscount(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalTax(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotal(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setGratuity(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setAmountPaid(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setBalanceDue(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setCashAmount(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setCashTipAmount(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalCash(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setCreditCardAmount(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setCreditCardTipAmount(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalCard(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalCreditTerm(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalOrderDuration(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalOrders(((int) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalCheque(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalCashRefund(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalCardRefund(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalDeliveryCharge(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setTotalServiceCharge(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setCreditTermTip(((BigDecimal) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setNc(((BigDecimal) objRow[i]));
					i++;
				 
					

					list.add(revenue);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
}