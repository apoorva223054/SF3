/**
e * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.NameConstant;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.payment.BraintreePayment;
import com.nirvanaxp.payment.PathLinkTransaction;
import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.data.SupportedCardType;
import com.nirvanaxp.payment.gateway.data.SupportedPaymentAction;
import com.nirvanaxp.payment.gateway.data.SupportedPaymentGateway;
import com.nirvanaxp.payment.gateway.exception.POSNirvanaGatewayException;
import com.nirvanaxp.payment.gateway.listener.PaymentGatewayResponseListener;
import com.nirvanaxp.payment.gateway.manager.PaymentGatewayManager;
import com.nirvanaxp.payment.gateway.parser.PaymentGatewayResponse;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.TimeZoneUtil;
import com.nirvanaxp.services.jaxrs.OrderManagementServiceBean;
import com.nirvanaxp.types.entities.device.DeviceToPinPad;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType;
import com.nirvanaxp.types.entities.payment.PaymentGatewayToPinpad;
import com.nirvanaxp.types.entities.payment.PaymentGatewayType;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.Paymentgateway;
import com.nirvanaxp.types.entities.payment.Paymentgateway_;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.time.Timezone;

public class BatchSettlementUtil implements PaymentGatewayResponseListener, Runnable
{

	private final static NirvanaLogger logger = new NirvanaLogger(BatchSettlementUtil.class.getName());
	private static final String force = "Force";
	private static final String captureall = "CaptureAll";

	// private static final String captureAllDataCap = "CaptureAllDataCap";
	private OrderPaymentDetail orderPaymentDetail;
	private List<OrderHeader> orderHeaderList = null;
	private String locationId;
	private String userId;
	private String currentDate;
	private String currentTime;
	private Location location;
	private Timezone timeZone;
	private String javaTimezoneName;
	private String currentModeOfExecution;
	//private String sessionId;
	private String schemaName;
	private int isPrecaptured;
	private String beginDate;
	private String endDate;
	private int checkPrecaptureTransaction;
	private HttpServletRequest httpServletRequest;
	private OrderHeader orderheader;
	private String gatewayTypeIdString;
	private final CountDownLatch doneSignal;
	private int isDatacapFirstData;
	public BatchSettlementUtil(String locationId, boolean isCapture, String userId, String currentDate, String currentTime, List<OrderHeader> orderHeaderList) throws IOException
	{
		this.locationId = locationId;
		this.userId = userId;
		this.currentDate = currentDate;
		this.currentTime = currentTime;
		this.orderHeaderList = orderHeaderList;
		this.doneSignal = null;
	}
	
	public BatchSettlementUtil(String schemaName, String locationId, boolean isCapture, String userId, String currentDate, String currentTime, List<OrderHeader> orderHeaderList) throws IOException
	{
		this.schemaName = schemaName;
		this.locationId = locationId;
		this.userId = userId;
		this.currentDate = currentDate;
		this.currentTime = currentTime;
		this.orderHeaderList = orderHeaderList;
		this.doneSignal = null;
	}

	public BatchSettlementUtil(CountDownLatch doneSignal, String schemaName, String locationId, boolean isCapture, String userId, String currentDate, String currentTime, 
			HttpServletRequest httpRequest, int checkPrecaptureTransaction, String endDate, OrderHeader orderHeader, String gatewayTypeIdString, Location location,
			String javaTimezoneName,int isDatacapFirstData, List<OrderHeader> orderHeaderList) throws IOException
	{
		this.schemaName = schemaName;
		this.locationId = locationId;
		this.userId = userId;
		this.currentDate = currentDate;
		this.currentTime = currentTime;
		this.checkPrecaptureTransaction = checkPrecaptureTransaction;
		this.endDate = endDate;
		this.httpServletRequest = httpRequest;
		this.orderheader = orderHeader;
		this.gatewayTypeIdString = gatewayTypeIdString;
		this.location = location;
		this.javaTimezoneName = javaTimezoneName;
		this.doneSignal = doneSignal;
		this.isDatacapFirstData=isDatacapFirstData;
		this.orderHeaderList =orderHeaderList;
	}

	public void initializeValues(HttpServletRequest httpRequest, EntityManager em)
	{
		// get payment gateway for location id
		location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
		logger.info("locationId from location object :-" + location.getId());
		logger.info("timezoneid from location object :-" + location.getTimezoneId());

		timeZone = em.find(Timezone.class, location.getTimezoneId());
		logger.info("timezone from location object :-" + timeZone);
		logger.info("timezone from location object :-" + timeZone.getTimezoneName());
		javaTimezoneName = getTimeZoneStrForJavaTimeZone(timeZone);
		
	}

	public void btnPreSettledAllTransacton(HttpServletRequest httpRequest, EntityManager em, String gatewayTypeIdString, boolean isSourceSpecific, int checkPrecaptureTransaction, String endDate,
			OrderHeader orderHeader) throws Exception
	{

		this.endDate = TimeZoneUtil.convertDateFormat(endDate);
		this.orderheader = orderHeader;

		try
		{

			currentModeOfExecution = force;
			PaymentMethod paymentMethod = null;
			this.isPrecaptured = checkPrecaptureTransaction;
			ArrayList<String> gatewayTypeIds = getGetWayIdFormGatewayIdString(gatewayTypeIdString, isSourceSpecific);
			// && paymentgateway != null
			List<OrderPaymentDetail> orderDetailPaymentList = orderHeader.getOrderPaymentDetails();

			if (orderDetailPaymentList != null && orderDetailPaymentList.size() > 0)
			{
				logger.severe("total orderDetailPaymentList size is -------" + orderDetailPaymentList.size());
				long sizeOfList = orderHeader.getOrderPaymentDetails().size();

				for (OrderPaymentDetail orderPaymentDetail : orderDetailPaymentList)
				{
					this.orderPaymentDetail = orderPaymentDetail;
					paymentMethod = orderPaymentDetail.getPaymentMethod();
					try
					{
						if (isAllowToPrecaptureTransaction(httpRequest, em, paymentMethod, 0, orderPaymentDetail, isSourceSpecific))
						{
							logger.severe("order payment detail id executing -------" + orderPaymentDetail.getId());
							preSettleTransaction(httpRequest, em, orderPaymentDetail, orderHeader, javaTimezoneName, "" + sizeOfList, isSourceSpecific, gatewayTypeIds);
						}
					}
					catch (Exception e)
					{
						logger.severe(e);
					}

					sizeOfList++;
				}
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private ArrayList<String> getGetWayIdFormGatewayIdString(String gatewayTypeIdString, boolean isSourceSpecific)
	{
		ArrayList<String> gatewayTypeIds = new ArrayList<String>();

		if (isSourceSpecific && gatewayTypeIdString != null)
		{
			String[] gatewayIdsArray = gatewayTypeIdString.split(",");
			if (gatewayIdsArray != null)
			{
				for (String id : gatewayIdsArray)
				{

					gatewayTypeIds.add(id);
				}
			}
		}

		return gatewayTypeIds;
	}

	private boolean isAllowToPrecaptureTransaction(HttpServletRequest httpRequest, EntityManager em, PaymentMethod paymentMethod, int locationGatewayId, OrderPaymentDetail orderPaymentDetail,
			boolean isSourceSpecific)
	{
		// here transaction status CC_Auth and CC_TipSaved belongs to credit
		// card and Manual Credit cardEntry PaymentMethod Type only allowed over
		// here

		PaymentMethodType paymentMethodType = (PaymentMethodType) new CommonMethods().getObjectById("PaymentMethodType", em,PaymentMethodType.class, paymentMethod.getPaymentMethodTypeId());

		// && orderPaymentDetail.getPayementGatewayId() == locationGatewayId
		if (orderPaymentDetail != null

				&& orderPaymentDetail.getTransactionStatus() != null
				&& (orderPaymentDetail.getTransactionStatus().getName().equals("CC Auth") || orderPaymentDetail.getTransactionStatus().getName().equals("Tip Saved"))
				&& orderPaymentDetail.getPnRef() != null && orderPaymentDetail.getPnRef().length() > 0 && paymentMethod != null
				&& (paymentMethodType.getName().equals("Credit Card") || paymentMethodType.getName().equals("Manual CC Entry")))
		{

			return true;

		}
		return false;
	}

	private void preSettleTransaction(HttpServletRequest httpRequest, EntityManager em, OrderPaymentDetail orderPaymentDetail, OrderHeader orderHeader, String javaTimezoneName, String sizeOfList,
			boolean isSourceSpecific, ArrayList<String> gatewayTypeIds) throws Exception
	{

		Invoice invoice = new Invoice();
		if (orderPaymentDetail.getCreditcardTipAmt() != null && orderPaymentDetail.getCreditcardTipAmt() != new BigDecimal(0))
		{ 
			invoice.setTipAmount(""+ orderPaymentDetail.getCreditcardTipAmt());
			BigDecimal totalAmount = new BigDecimal(0);
			if (orderPaymentDetail.getAmountPaid() != null)
			{
				totalAmount = orderPaymentDetail.getAmountPaid().add(orderPaymentDetail.getCreditcardTipAmt());
			}
			else
			{
				totalAmount = orderPaymentDetail.getCreditcardTipAmt();
			}
			invoice.setTotalAmount(totalAmount.doubleValue());
			invoice.setTipAmountWithFirstData(orderPaymentDetail.getCreditcardTipAmt());
				 
			 

		}
		else
		{
			invoice.setTotalAmount(orderPaymentDetail.getAmountPaid().doubleValue());
		}
		if(orderPaymentDetail.getAmountPaid()!=null){
			invoice.setTotalAmountWithoutTip(orderPaymentDetail.getAmountPaid());
			 
		}
		
 
		if (orderPaymentDetail.getPnRef() != null)
		{
			invoice.setPnrRef("" + orderPaymentDetail.getPnRef());
		}
		else
		{
			invoice.setInvoiceNumber("" + orderPaymentDetail.getOrderHeaderId());
		}

		if (orderPaymentDetail.getAcqRefData() != null)
		{
			invoice.setAcqRefData("" + orderPaymentDetail.getAcqRefData());
		}

		if (orderPaymentDetail.getAuthCode() != null)
		{
			invoice.setAuthCode("" + orderPaymentDetail.getAuthCode());
		}

		if (orderPaymentDetail.getHostRefStr() != null)
		{
			// changed by Apoorva Chourasiya for making hostref as int
			invoice.setRecordNumber(orderPaymentDetail.getHostRefStr() + "");
		}
		// required for reporting service
		Date today = new Date(new TimezoneTime().getGMTTimeInMilis());
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
		logger.severe("timezone form functio----" + javaTimezoneName);
		format.setTimeZone(TimeZone.getTimeZone(javaTimezoneName));
		String curDate = format.format(today);
		invoice.setBeginDate(curDate);
		invoice.setEndDate(curDate);
		this.beginDate = TimeZoneUtil.convertDateFormat(orderPaymentDetail.getDate());
		// auto generate invoice number logic
		// String sizeOfList = ""
		// + orderHeader.getOrderPaymentDetails().size();
		String invNo = "" + orderHeader.getId() + sizeOfList + sizeOfList.length();
		invoice.setInvoiceNumber("" + invNo);
		logger.severe("pnr reg:=" + invoice.getPnrRef());
		if (isSourceSpecific)
		{
			processSourceSpecificPayment(httpRequest, em, invoice, orderPaymentDetail, gatewayTypeIds);
		}
		else
		{
			// this method to support old build
			processPaymentForOldBuild(httpRequest, em, invoice, orderPaymentDetail);
		}

	}

	private void processSourceSpecificPayment(HttpServletRequest httpRequest, EntityManager em, Invoice invoice, OrderPaymentDetail orderPaymentDetail, ArrayList<String> gatewayTypeIds)
			throws Exception
	{

		MerchentAccount merchentAccount = null;
		SupportedPaymentGateway supportedPaymentGateway = null;
		if (orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId() != 0)
		{
			OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentGatewayType = em.find(OrderSourceGroupToPaymentgatewayType.class,
					orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId());
			if (orderSourceGroupToPaymentGatewayType != null)
			{
				merchentAccount = getMerchantAccountDetailForOrderSourceGroupToPaymentGatewayType(orderSourceGroupToPaymentGatewayType);
				supportedPaymentGateway = getSupportedGatewayForOrderSourceGroupToPaymentGatewayType(em, orderSourceGroupToPaymentGatewayType);
			}
		}
		else if (orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId() != 0)
		{

			OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType = em.find(OrderSourceToPaymentgatewayType.class, orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId());
			if (orderSourceToPaymentgatewayType != null)
			{
				merchentAccount = getMerchantAccountDetailForOrderSourceToPaymentgatewayType(orderSourceToPaymentgatewayType);
				supportedPaymentGateway = getSupportedGatewayForOrderSourceToPaymentgatewayType(em, orderSourceToPaymentgatewayType);
			}
		}
		//
		String queryString = "select dw from DeviceToPinPad dw where dw.id =?  ";
		TypedQuery<DeviceToPinPad> query = em.createQuery(queryString, DeviceToPinPad.class).setParameter(1, orderPaymentDetail.getDeviceToPinPadId());
		List<DeviceToPinPad> resultSet=null;
		try {
			resultSet = query.getResultList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
	    if(resultSet != null && resultSet.size()>0){
	    	for(DeviceToPinPad deviceToPinPad : resultSet){
				PaymentGatewayToPinpad pinPad = (PaymentGatewayToPinpad) new CommonMethods().getObjectById("PaymentGatewayToPinpad", em,PaymentGatewayToPinpad.class, deviceToPinPad.getPinPad());
				if (pinPad != null)
				{
					merchentAccount.setTerminalId(pinPad.getTerminalId());
					merchentAccount.setPinPadIpAddress(pinPad.getIpAddress());
					merchentAccount.setPinPadIpPort(pinPad.getPort());
				}
				if (merchentAccount != null && supportedPaymentGateway != null && isDatacapFirstData==1)
				{
					if (gatewayTypeIds.indexOf("" + supportedPaymentGateway.getId()) != -1)
					{

						processedDataForCreditCardForce(httpRequest, em, invoice, merchentAccount, supportedPaymentGateway);
					}
				}
			}
	    }else{
	    	 
				if (merchentAccount != null && supportedPaymentGateway != null)
				{
					if (gatewayTypeIds.indexOf("" + supportedPaymentGateway.getId()) != -1)
					{
							processedDataForCreditCardForce(httpRequest, em, invoice, merchentAccount, supportedPaymentGateway);
							
					}
				}
			 
	    }
		
		
		
	}

	private SupportedPaymentGateway getSupportedGatewayForOrderSourceToPaymentgatewayType(EntityManager em, OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType)
	{

		PaymentGatewayType paymentGatewayType = em.find(PaymentGatewayType.class, orderSourceToPaymentgatewayType.getPaymentgatewayTypeId());
		if (paymentGatewayType != null && paymentGatewayType.getName().equals(NameConstant.DATACAP_GATEWAY))
		{

		}
		return new SupportedPaymentGateway(paymentGatewayType.getName(), paymentGatewayType.getPaymentGatewayTransactionUrl(), paymentGatewayType.getId());

	}

	private MerchentAccount getMerchantAccountDetailForOrderSourceToPaymentgatewayType(OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType)
	{

		return new MerchentAccount(orderSourceToPaymentgatewayType.getMerchantId(), orderSourceToPaymentgatewayType.getPassword(), orderSourceToPaymentgatewayType.getParameter1(),
				orderSourceToPaymentgatewayType.getParameter2(), orderSourceToPaymentgatewayType.getParameter3(), orderSourceToPaymentgatewayType.getParameter4(),
				orderSourceToPaymentgatewayType.getParameter5(), 0, orderSourceToPaymentgatewayType.getId());

	}

	private SupportedPaymentGateway getSupportedGatewayForOrderSourceGroupToPaymentGatewayType(EntityManager em, OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentGatewayType)
	{

		PaymentGatewayType paymentGatewayType = em.find(PaymentGatewayType.class, orderSourceGroupToPaymentGatewayType.getPaymentgatewayTypeId());
		return new SupportedPaymentGateway(paymentGatewayType.getName(), paymentGatewayType.getPaymentGatewayTransactionUrl(), paymentGatewayType.getId());

	}

	private MerchentAccount getMerchantAccountDetailForOrderSourceGroupToPaymentGatewayType(OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentGatewayType)
	{

		return new MerchentAccount(orderSourceGroupToPaymentGatewayType.getMerchantId(), orderSourceGroupToPaymentGatewayType.getPassword(), orderSourceGroupToPaymentGatewayType.getParameter1(),
				orderSourceGroupToPaymentGatewayType.getParameter2(), orderSourceGroupToPaymentGatewayType.getParameter3(), orderSourceGroupToPaymentGatewayType.getParameter4(),
				orderSourceGroupToPaymentGatewayType.getParameter5(), orderSourceGroupToPaymentGatewayType.getId(), 0);

	}

	private void processPaymentForOldBuild(HttpServletRequest httpRequest, EntityManager em, Invoice invoice, OrderPaymentDetail orderPaymentDetail) throws Exception
	{
		Paymentgateway paymentgateway = em.find(Paymentgateway.class, orderPaymentDetail.getPayementGatewayId());
		MerchentAccount merchentAccount = getMerchantAccountDetailForPaymentGateway(httpRequest, em, paymentgateway);
		SupportedPaymentGateway supportedPaymentGateway = getSupportedGatewayForPaymentGateway(httpRequest, em, paymentgateway);
		processedDataForCreditCardForce(httpRequest, em, invoice, merchentAccount, supportedPaymentGateway);
	}

	private SupportedPaymentGateway getSupportedGatewayForPaymentGateway(HttpServletRequest httpRequest, EntityManager em, Paymentgateway paymentgateway)
	{

		PaymentGatewayType paymentGatewayType = em.find(PaymentGatewayType.class, paymentgateway.getPaymentgatewayTypeId());
		return new SupportedPaymentGateway(paymentGatewayType.getName(), paymentgateway.getPaymentgatewayTransactionUrl(), paymentgateway.getId());

	}

	private MerchentAccount getMerchantAccountDetailForPaymentGateway(HttpServletRequest httpRequest, EntityManager em, Paymentgateway paymentgateway)
	{

		return new MerchentAccount(paymentgateway.getMerchantId(), paymentgateway.getPassword(), paymentgateway.getLicenseId(), paymentgateway.getSiteId(), paymentgateway.getDeviceId(),
				paymentgateway.getDeveloperId(), paymentgateway.getVersionNumber(), 0, 0);

	}

	public void captureAllTransaction(HttpServletRequest httpRequest, EntityManager em) throws Exception
	{

		currentModeOfExecution = captureall;
		// for capture all i need to do coding
		// force the transaction wrt pnr-ref no before capture
		Paymentgateway paymentgateway = getPaymentGatewayForLocationId(httpRequest, em, locationId);
		MerchentAccount merchentAccount = getMerchantAccountDetailForPaymentGateway(httpRequest, em, paymentgateway);
		SupportedPaymentGateway supportedPaymentGateway = getSupportedGatewayForPaymentGateway(httpRequest, em, paymentgateway);

		if (supportedPaymentGateway != null && merchentAccount != null)
		{
			PaymentGatewayManager paymentGatewayManager = new PaymentGatewayManager(supportedPaymentGateway, SupportedPaymentAction.ACTION_PAYMENT_SWIPE_ENCYPTED, SupportedCardType.CARD_TYPE_CREDIT,
					captureall, null, null, merchentAccount, this, null);
			paymentGatewayManager.ProcessPayment(httpRequest, em, orderHeaderList, isPrecaptured);
		}
		else
		{
			throw new POSNirvanaGatewayException("Gateway is not configured for the location");
		}

	}

	private Paymentgateway getPaymentGatewayForLocationId(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Paymentgateway> criteria = builder.createQuery(Paymentgateway.class);
		Root<Paymentgateway> r = criteria.from(Paymentgateway.class);
		TypedQuery<Paymentgateway> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Paymentgateway_.locationsId), locationId), builder.equal(r.get(Paymentgateway_.status), "A")));

		return query.getSingleResult();

	}

	private String getTimeZoneStrForJavaTimeZone(Timezone timezone)
	{
		String timeZone = timezone.getTimezoneName();
		String parts[] = timeZone.split("Time");
		String partData = (parts[1]);
		partData = partData.trim();
		return partData;
	}

	private void processedDataForCreditCardForce(HttpServletRequest httpRequest, EntityManager em, Invoice invoice, MerchentAccount merchentAccount, SupportedPaymentGateway supportedPaymentGateway)
			throws Exception
	{
		// force the transaction wrt pnr-ref no before capture
		if (supportedPaymentGateway != null && merchentAccount != null)
		{
			logger.severe("about to process");
			PaymentGatewayManager paymentGatewayManager = new PaymentGatewayManager(supportedPaymentGateway, SupportedPaymentAction.ACTION_PAYMENT_SWIPE_ENCYPTED, SupportedCardType.CARD_TYPE_CREDIT,
					force, null, invoice, merchentAccount, this, null);
			logger.severe("SupportedPaymentGateway.CURRENT_GATEWAY---" + supportedPaymentGateway);
			if (isPrecaptured == 0)
			{
				paymentGatewayManager.ProcessPayment(httpRequest, em, orderHeaderList, isPrecaptured);
			}
			else
			{
				if (checkTransactionForPrecapture(invoice, merchentAccount.getUserName(), merchentAccount.getPassword(), beginDate, endDate, invoice.getAuthCode(), merchentAccount.getLicenseId(),
						supportedPaymentGateway.getSupportedPaymentGatewayUrl()))
				{
					// df // insert entry in db pending
				}
				else
				{
					paymentGatewayManager.ProcessPayment(httpRequest, em, orderHeaderList, isPrecaptured);
				}

			}
		}
		else
		{
			logger.severe("Gateway is not configured for the location");
			throw new POSNirvanaGatewayException("Gateway is not configured for the location");

		}

	}

	@Override
	public void proessPaymentGatewayResponse(HttpServletRequest httpRequest, EntityManager em, int id, int currentMethod, PaymentGatewayResponse response,
			SupportedPaymentGateway supportedPaymentGateway, MerchentAccount merchentAccount) throws Exception
	{
		logger.severe(response.getMessage());
		
		if (response.getMessage() != null && (response.getMessage().equals("Success") ||  response.getMessage().equals("APPROVED")))
		{

			EntityTransaction tx = em.getTransaction();
			try
			{
				// start transaction

				if (orderheader != null)
				{
					// add order header history

					// change the cashier id of this order
					orderheader.setCashierId(userId);
					orderheader.setUpdatedBy(userId);
					tx.begin();
					em.merge(orderheader);
					tx.commit();

				}
				if (currentModeOfExecution.equals(force))
				{
					// we do this for force transaction
					tx.begin();
					new PaymentCommonUtil().getPaymentTransactionType(httpRequest, em, "Force");
					createObjectForPaymentDetailsForForce(httpRequest, em, response, userId, currentDate, currentTime, supportedPaymentGateway, merchentAccount);
					tx.commit();
				}
				else
				{
					// we need to store the capture all data in the
					// database
					tx.begin();
					saveTransactionForCaptureAllInDatabase(httpRequest, schemaName, supportedPaymentGateway, merchentAccount);
					tx.commit();
				}

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
		}
		else if (response.getMessage() != null)
		{
			logger.severe(response.getMessage());
			try
			{
				String error = "";
				if (response.getResponseMsg() != null && !(response.getResponseMsg().equals("")))
				{
					error = response.getResponseMsg();
				}
				else
				{
					error = response.getMessage();
				}
				logger.severe(error);
			}
			catch (Exception e)
			{
				logger.severe(e);

			}
		}

	}

	private void createObjectForPaymentDetailsForForce(HttpServletRequest httpRequest, EntityManager em, PaymentGatewayResponse paymentForceResponse, String userId, String currentDate,
			String currentTime, SupportedPaymentGateway supportedPaymentGateway, MerchentAccount merchentAccount)
	{
		// create total object which pass to server on click of credit card or
		// cash button
		try
		{

			if (orderheader != null)
			{
				OrderHeader newOrderHeader = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,OrderHeader.class, orderheader.getId());

				PaymentTransactionType orderDetailPaymentTransactionType = new PaymentCommonUtil().getPaymentTransactionType(httpRequest, em, "Force");

				TransactionStatus transactionStatusCCPreCapture = new PaymentCommonUtil().getTransactionStatusByName(httpRequest, em, "CC Pre Capture");

				OrderPaymentDetail orderPaymentDetailToSend = new OrderPaymentDetail();

				orderPaymentDetailToSend.setDate(currentDate);
				orderPaymentDetailToSend.setTime(currentTime);
				orderPaymentDetailToSend.setCreatedBy(userId);
				orderPaymentDetailToSend.setPaymentTransactionType(orderDetailPaymentTransactionType);
				PaymentMethod paymentmethod = new PaymentMethod();
				paymentmethod = orderPaymentDetail.getPaymentMethod();
				orderPaymentDetailToSend.setPaymentMethod(paymentmethod);

				if (paymentForceResponse.getPnrRef() != "" || paymentForceResponse.getPnrRef() != null)
				{

					orderPaymentDetailToSend.setPnRef(paymentForceResponse.getPnrRef());

				}
				if (paymentForceResponse.getHostCode() != "" || paymentForceResponse.getHostCode() != null)
				{

					orderPaymentDetailToSend.setHostRefStr(paymentForceResponse.getHostCode());
				}

				if (paymentForceResponse.getAcqRefData() != null)
				{
					orderPaymentDetailToSend.setAcqRefData(paymentForceResponse.getAcqRefData());
				}

				if (paymentForceResponse.getAuthCode() != null && paymentForceResponse.getAuthCode().length() > 0)
				{
					orderPaymentDetailToSend.setAuthCode(paymentForceResponse.getAuthCode());
				}
				else
				{
					orderPaymentDetailToSend.setAuthCode(orderPaymentDetail.getAuthCode());
				}

				if (paymentForceResponse.getInvNum() != null && paymentForceResponse.getInvNum().length() > 0)
				{
					orderPaymentDetailToSend.setInvoiceNumber(paymentForceResponse.getInvNum());
				}
				else
				{
					orderPaymentDetailToSend.setInvoiceNumber(orderPaymentDetail.getInvoiceNumber());
				}
				orderPaymentDetailToSend.setTotalAmount(orderheader.getTotal());

				orderPaymentDetailToSend.setAmountPaid(orderPaymentDetail.getAmountPaid());
				orderPaymentDetailToSend.setBalanceDue((orderPaymentDetail.getBalanceDue()));
				orderPaymentDetailToSend.setSettledAmount(orderPaymentDetail.getAmountPaid());

				if (orderPaymentDetail.getCardType() != null)
				{
					orderPaymentDetailToSend.setCardType(orderPaymentDetail.getCardType());
				}
				else
				{
					orderPaymentDetailToSend.setCardType("");
				}
				// added by prachi
				if (paymentForceResponse.getInvNum() != null && paymentForceResponse.getInvNum().length() > 0)
				{
					orderPaymentDetailToSend.setInvoiceNumber(paymentForceResponse.getInvNum());
				}

				orderPaymentDetailToSend.setCashTipAmt(orderPaymentDetail.getCashTipAmt());
				orderPaymentDetailToSend.setCreditcardTipAmt(orderPaymentDetail.getCreditcardTipAmt());
				orderPaymentDetailToSend.setSeatId(orderPaymentDetail.getSeatId());
				orderPaymentDetailToSend.setExpiryMonth(orderPaymentDetail.getExpiryMonth());
				orderPaymentDetailToSend.setExpiryYear(orderPaymentDetail.getExpiryYear());
				orderPaymentDetailToSend.setCardNumber(orderPaymentDetail.getCardNumber());
				orderPaymentDetailToSend.setNirvanaXpBatchNumber(orderPaymentDetail.getNirvanaXpBatchNumber());

				orderPaymentDetailToSend.setChangeDue(new BigDecimal("0.00"));

				orderPaymentDetailToSend.setTransactionStatus(transactionStatusCCPreCapture);
				orderPaymentDetailToSend.setPayementGatewayId(supportedPaymentGateway.getId());
				orderPaymentDetailToSend.setOrderSourceGroupToPaymentGatewayTypeId(merchentAccount.getOrderSourceGroupToPaymentGatewayTypeId());
				orderPaymentDetailToSend.setOrderSourceToPaymentGatewayTypeId(merchentAccount.getOrderSourceToPaymentGatewayTypeId());
				orderPaymentDetailToSend.setUpdatedBy(userId);
				orderPaymentDetailToSend.setTaxDisplayName1(orderPaymentDetail.getTaxDisplayName1());
				orderPaymentDetailToSend.setTaxDisplayName2(orderPaymentDetail.getTaxDisplayName2());
				orderPaymentDetailToSend.setTaxDisplayName3(orderPaymentDetail.getTaxDisplayName3());
				orderPaymentDetailToSend.setTaxDisplayName4(orderPaymentDetail.getTaxDisplayName4());
				orderPaymentDetailToSend.setTaxName1(orderPaymentDetail.getTaxName1());
				orderPaymentDetailToSend.setTaxName2(orderPaymentDetail.getTaxName2());
				orderPaymentDetailToSend.setTaxName3(orderPaymentDetail.getTaxName3());
				orderPaymentDetailToSend.setTaxName4(orderPaymentDetail.getTaxName4());
				orderPaymentDetailToSend.setTaxRate1(orderPaymentDetail.getTaxRate1());
				orderPaymentDetailToSend.setTaxRate2(orderPaymentDetail.getTaxRate2());
				orderPaymentDetailToSend.setTaxRate3(orderPaymentDetail.getTaxRate3());
				orderPaymentDetailToSend.setTaxRate4(orderPaymentDetail.getTaxRate4());
				orderPaymentDetailToSend.setPriceTax1(orderPaymentDetail.getPriceTax1());
				orderPaymentDetailToSend.setPriceTax2(orderPaymentDetail.getPriceTax2());
				orderPaymentDetailToSend.setPriceTax3(orderPaymentDetail.getPriceTax3());
				orderPaymentDetailToSend.setPriceTax4(orderPaymentDetail.getPriceTax4());
				orderPaymentDetailToSend.setPriceGratuity(orderPaymentDetail.getPriceGratuity());
				orderPaymentDetailToSend.setGratuity(orderPaymentDetail.getGratuity());
				orderPaymentDetailToSend.setRegister(orderPaymentDetail.getRegister());
				orderPaymentDetailToSend.setSecurityCode(orderPaymentDetail.getSecurityCode());
				orderPaymentDetailToSend.setAuthAmount(orderPaymentDetail.getAuthAmount());
				orderPaymentDetailToSend.setTipAmount(orderPaymentDetail.getTipAmount());
				orderPaymentDetailToSend.setDiscountsName(orderPaymentDetail.getDiscountsName());
				orderPaymentDetailToSend.setDiscountsValue(orderPaymentDetail.getDiscountsValue());
				orderPaymentDetailToSend.setCalculatedDiscountValue(orderPaymentDetail.getCalculatedDiscountValue());
				orderPaymentDetailToSend.setPriceDiscount(orderPaymentDetail.getPriceDiscount());
				// issue is posentry not going
				if(orderPaymentDetail.getPosEntry()!=null){
					orderPaymentDetailToSend.setPosEntry(orderPaymentDetail.getPosEntry());
				}
				
				// checking datacap transactions
				if(orderPaymentDetail.getDeviceToPinPadId()!= null){
					orderPaymentDetailToSend.setAcqRefData(orderPaymentDetail.getAcqRefData());
					orderPaymentDetailToSend.setAuthCode(orderPaymentDetail.getAuthCode());
					orderPaymentDetailToSend.setProcessData(orderPaymentDetail.getProcessData());
					orderPaymentDetailToSend.setHostRefStr(orderPaymentDetail.getHostRefStr());
					orderPaymentDetailToSend.setPnRef(orderPaymentDetail.getPnRef());
					orderPaymentDetailToSend.setHostRef(orderPaymentDetail.getHostRef());
					orderPaymentDetailToSend.setSequenceNo(orderPaymentDetail.getSequenceNo());
					orderPaymentDetailToSend.setDiscountCode(orderPaymentDetail.getDiscountCode());
					orderPaymentDetailToSend.setLocalTime(orderPaymentDetail.getLocalTime());
					orderPaymentDetailToSend.setCustomerFirstName(orderPaymentDetail.getCustomerFirstName());
					orderPaymentDetailToSend.setCustomerLastName(orderPaymentDetail.getCustomerLastName());
					orderPaymentDetailToSend.setCreditTermTip(orderPaymentDetail.getCreditTermTip());
					orderPaymentDetailToSend.setDeviceToPinPadId(orderPaymentDetail.getDeviceToPinPadId());
					
				}
				if (newOrderHeader.getOrderPaymentDetails() != null)
				{

					newOrderHeader.getOrderPaymentDetails().clear();
				}
				else
				{
					List<OrderPaymentDetail> orderPaymentDetailsList = new ArrayList<OrderPaymentDetail>();
					newOrderHeader.setOrderPaymentDetails(orderPaymentDetailsList);
				}

				newOrderHeader.getOrderPaymentDetails().add(orderPaymentDetailToSend);
				newOrderHeader.getOrderPaymentDetails().add(orderPaymentDetail);
				orderPaymentDetail.setUpdatedBy(userId);
				newOrderHeader.setCashierId(userId);
				orderPaymentDetail.setTransactionStatus(transactionStatusCCPreCapture);

				OrderManagementServiceBean bean = new OrderManagementServiceBean();

				bean.updateOrderPaymentForBatchSettle(httpRequest, em, newOrderHeader);

			}

		}
		catch (Exception e)
		{
			logger.severe(e);

		}

	}

	private void saveTransactionForCaptureAllInDatabase(HttpServletRequest httpRequest, String schemaName, SupportedPaymentGateway supportedPaymentGateway, MerchentAccount merchentAccount)
	{
		if (orderHeaderList.size() > 0)
		{
			CountDownLatch latch = new CountDownLatch(orderHeaderList.size());

			for (OrderHeader orderHeader : orderHeaderList)
			{
				SettlementUpdateQueue queue = new SettlementUpdateQueue(latch, schemaName, httpRequest, supportedPaymentGateway, merchentAccount, orderHeader, userId, currentDate, currentTime);
				Thread t = new Thread(queue);
				t.start();
				
			}
			
			try
			{
				latch.await();
			}
			catch (InterruptedException e)
			{
				logger.severe(e, "Error while waiting for save transaction to database during capture all: ", e.getMessage());
			}
		}
	}

	public void captureAllTransaction(HttpServletRequest httpRequest, EntityManager em, String gatewayIdString, java.util.Date date, String batchId) throws Exception
	{

		currentModeOfExecution = captureall;
		ArrayList<String> gatewayTypeIds = getGetWayIdFormGatewayIdString(gatewayIdString, true);

		try
		{
			if (gatewayTypeIds != null && gatewayTypeIds.size() > 0)
			{

				for (String id : gatewayTypeIds)
				{
					BraintreePayment braintreePayment = new BraintreePayment();
					List<OrderSourceGroupToPaymentgatewayType> orderSourceGroupToGatwayTypeList = getOrderSourceGroupToPaymentGatewayType(httpRequest, em, id, date, batchId);
					List<OrderSourceToPaymentgatewayType> orderSourceToGatwayTypeList = getOrderSourceToPaymentGatewayType(httpRequest, em, id, date, batchId);

					for (OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentGatewayType : orderSourceGroupToGatwayTypeList)

					{
						MerchentAccount merchentAccount;
						SupportedPaymentGateway supportedPaymentGateway;
						braintreePayment.initilizeGateway(em, orderSourceGroupToPaymentGatewayType.getId(), 0);
						merchentAccount = getMerchantAccountDetailForOrderSourceGroupToPaymentGatewayType(orderSourceGroupToPaymentGatewayType);
						supportedPaymentGateway = getSupportedGatewayForOrderSourceGroupToPaymentGatewayType(em, orderSourceGroupToPaymentGatewayType);
						List<PaymentGatewayToPinpad> pinPads = null;
						if (supportedPaymentGateway != null && (supportedPaymentGateway.getSupportedPaymentGateway().equals(NameConstant.DATACAP_GATEWAY) || supportedPaymentGateway.getSupportedPaymentGateway().equals(NameConstant.DATACAP_GATEWAY_WITH_FIRSTDATA) ))
						{
							merchentAccount.setDeviceId(orderSourceGroupToPaymentGatewayType.getParameter2());
							pinPads = getPaymentGatewayTypeToPinPad(em, supportedPaymentGateway.getId());

						}
						// naman
						if (pinPads != null && pinPads.size() > 0)
						{
							for (PaymentGatewayToPinpad pinPad : pinPads)
							{
								if (pinPad != null)
								{
									merchentAccount.setTerminalId(pinPad.getTerminalId());
									merchentAccount.setPinPadIpAddress(pinPad.getIpAddress());
									merchentAccount.setPinPadIpPort(pinPad.getPort());
								}
								PaymentGatewayManager paymentGatewayManager = new PaymentGatewayManager(supportedPaymentGateway, SupportedPaymentAction.ACTION_PAYMENT_SWIPE_ENCYPTED,
										SupportedCardType.CARD_TYPE_CREDIT, captureall, null, null, merchentAccount, this, braintreePayment);
								paymentGatewayManager.ProcessPayment(httpRequest, em, orderHeaderList, isPrecaptured);
							}

						}
						else
						{
							PaymentGatewayManager paymentGatewayManager = new PaymentGatewayManager(supportedPaymentGateway, SupportedPaymentAction.ACTION_PAYMENT_SWIPE_ENCYPTED,
									SupportedCardType.CARD_TYPE_CREDIT, captureall, null, null, merchentAccount, this, braintreePayment);
							paymentGatewayManager.ProcessPayment(httpRequest, em, orderHeaderList, isPrecaptured);

						}

					}
					for (OrderSourceToPaymentgatewayType orderSourceToPaymentGatewayType : orderSourceToGatwayTypeList)

					{
						MerchentAccount merchentAccount;
						SupportedPaymentGateway supportedPaymentGateway;

						braintreePayment.initilizeGateway(em, 0, orderSourceToPaymentGatewayType.getId());

						merchentAccount = getMerchantAccountDetailForOrderSourceToPaymentgatewayType(orderSourceToPaymentGatewayType);
						supportedPaymentGateway = getSupportedGatewayForOrderSourceToPaymentgatewayType(em, orderSourceToPaymentGatewayType);
						List<PaymentGatewayToPinpad> pinPads = null;
						if (supportedPaymentGateway != null && ((supportedPaymentGateway.getSupportedPaymentGateway().equals(NameConstant.DATACAP_GATEWAY)) || (supportedPaymentGateway.getSupportedPaymentGateway().equals(NameConstant.DATACAP_GATEWAY_WITH_FIRSTDATA) )))
						{
							merchentAccount.setDeviceId(orderSourceToPaymentGatewayType.getParameter2());
							pinPads = getPaymentGatewayTypeToPinPad(em, supportedPaymentGateway.getId());

						}
						// naman
						if (pinPads != null && pinPads.size() > 0)
						{
							for (PaymentGatewayToPinpad pinPad : pinPads)
							{
								if (pinPad != null)
								{
									merchentAccount.setTerminalId(pinPad.getTerminalId());
									merchentAccount.setPinPadIpAddress(pinPad.getIpAddress());
									merchentAccount.setPinPadIpPort(pinPad.getPort());
								}
								PaymentGatewayManager paymentGatewayManager = new PaymentGatewayManager(supportedPaymentGateway, SupportedPaymentAction.ACTION_PAYMENT_SWIPE_ENCYPTED,
										SupportedCardType.CARD_TYPE_CREDIT, captureall, null, null, merchentAccount, this, braintreePayment);
								paymentGatewayManager.ProcessPayment(httpRequest, em, orderHeaderList, isPrecaptured);
							}

						}
						else
						{
							PaymentGatewayManager paymentGatewayManager = new PaymentGatewayManager(supportedPaymentGateway, SupportedPaymentAction.ACTION_PAYMENT_SWIPE_ENCYPTED,
									SupportedCardType.CARD_TYPE_CREDIT, captureall, null, null, merchentAccount, this, braintreePayment);
							paymentGatewayManager.ProcessPayment(httpRequest, em, orderHeaderList, isPrecaptured);

						}

					}
				}
			}
			else
			{
				throw new POSNirvanaGatewayException("Gateway is not configured for the location");
			}
		}
		catch (Exception e)
		{

			logger.severe(e);
		}

	}

	private List<PaymentGatewayToPinpad> getPaymentGatewayTypeToPinPad(EntityManager em, int paymentGatewayId)
	{
		String queryString = "select l from PaymentGatewayToPinpad l where l.paymentGatewayId=?  and l.status != 'D'";
		try
		{
			TypedQuery<PaymentGatewayToPinpad> query = em.createQuery(queryString, PaymentGatewayToPinpad.class).setParameter(1, paymentGatewayId);
			return query.getResultList();
		}
		catch (NonUniqueResultException e)
		{
			logger.info(e.toString());
		}
		catch (NoResultException e)
		{
			logger.info(e.toString());
		}
		catch (Exception e)
		{
			logger.info(e.toString());
		}
		return null;
	}

	private List<OrderSourceGroupToPaymentgatewayType> getOrderSourceGroupToPaymentGatewayType(HttpServletRequest httpRequest, EntityManager em, String paymentGatewayType, java.util.Date date,
			String batchId)
	{

		List<OrderSourceGroupToPaymentgatewayType> orderSourceGroupToPaymentgatewayTypes = new ArrayList<OrderSourceGroupToPaymentgatewayType>();

		String queryString = "select * from order_source_group_to_paymentgateway_type where id "
				+ "in(select order_source_group_to_paymentgatewaytype_id from order_payment_details where nirvanaxp_batch_number like ? ) and paymentgateway_type_id=?";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, batchId).setParameter(2, paymentGatewayType).getResultList();
		for (Object[] objRow : resultList)
		{

			OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentgatewayType = new OrderSourceGroupToPaymentgatewayType();
			orderSourceGroupToPaymentgatewayType.setId((Integer) objRow[0]);
			orderSourceGroupToPaymentgatewayType.setMerchantId((String) objRow[3]);
			orderSourceGroupToPaymentgatewayType.setPaymentgatewayTypeId((Integer) objRow[2]);
			orderSourceGroupToPaymentgatewayType.setPassword((String) objRow[4]);

			orderSourceGroupToPaymentgatewayType.setParameter1((String) objRow[5]);
			orderSourceGroupToPaymentgatewayType.setParameter2((String) objRow[6]);
			orderSourceGroupToPaymentgatewayType.setParameter3((String) objRow[7]);
			orderSourceGroupToPaymentgatewayType.setParameter4((String) objRow[8]);
			orderSourceGroupToPaymentgatewayType.setParameter5((String) objRow[9]);
			orderSourceGroupToPaymentgatewayTypes.add(orderSourceGroupToPaymentgatewayType);

		}
		return orderSourceGroupToPaymentgatewayTypes;

	}

	private List<OrderSourceToPaymentgatewayType> getOrderSourceToPaymentGatewayType(HttpServletRequest httpRequest, EntityManager em, String paymentGatewayType, java.util.Date date, String batchId)
	{

		List<OrderSourceToPaymentgatewayType> orderSourceToPaymentgatewayTypes = new ArrayList<OrderSourceToPaymentgatewayType>();
		// Change by Apoorva for matching date format issue after ankur fix july
		// 21,2015 sprint 7 release
		// SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
		// String dateChange =dmyFormat.format(date);

		String queryString = "select * from order_source_to_paymentgateway_type where id in(select order_source_to_paymentgatewaytype_id "
				+ "from order_payment_details where nirvanaxp_batch_number like ? ) and paymentgateway_type_id=?";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, batchId).setParameter(2, paymentGatewayType).getResultList();
		for (Object[] objRow : resultList)
		{

			OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType = new OrderSourceToPaymentgatewayType();
			orderSourceToPaymentgatewayType.setId((Integer) objRow[0]);
			orderSourceToPaymentgatewayType.setMerchantId((String) objRow[3]);
			orderSourceToPaymentgatewayType.setPaymentgatewayTypeId((Integer) objRow[2]);
			orderSourceToPaymentgatewayType.setPassword((String) objRow[4]);

			orderSourceToPaymentgatewayType.setParameter1((String) objRow[5]);
			orderSourceToPaymentgatewayType.setParameter2((String) objRow[6]);
			orderSourceToPaymentgatewayType.setParameter3((String) objRow[7]);
			orderSourceToPaymentgatewayType.setParameter4((String) objRow[8]);
			orderSourceToPaymentgatewayType.setParameter5((String) objRow[9]);

			orderSourceToPaymentgatewayTypes.add(orderSourceToPaymentgatewayType);

		}
		return orderSourceToPaymentgatewayTypes;

	}

	public String checkTransactionStatus(String userName, String password, String beginDate, String endDate, String approvalCode, String rpNum, String url) throws Exception
	{

		if (url.contains("gatewaystage"))
		{
			url = "https://gatewaystage.itstgate.com/admin/ws/trxdetail.asmx/GetCardTrxSummary";
		}
		else
		{
			url = "https://gateway.itstgate.com/admin/ws/trxdetail.asmx/GetCardTrxSummary";
		}

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("UserName", userName));
		nameValuePairs.add(new BasicNameValuePair("Password", password));
		nameValuePairs.add(new BasicNameValuePair("PNRef", ""));
		nameValuePairs.add(new BasicNameValuePair("RPNum", rpNum));
		nameValuePairs.add(new BasicNameValuePair("BeginDt", beginDate));
		nameValuePairs.add(new BasicNameValuePair("EndDt", endDate));
		nameValuePairs.add(new BasicNameValuePair("NameOnCheck", ""));
		nameValuePairs.add(new BasicNameValuePair("ExcludeVoid", "TRUE"));

		nameValuePairs.add(new BasicNameValuePair("invoiceId", ""));

		nameValuePairs.add(new BasicNameValuePair("PaymentType", ""));
		nameValuePairs.add(new BasicNameValuePair("TransType", ""));

		nameValuePairs.add(new BasicNameValuePair("ApprovalCode", approvalCode));
		nameValuePairs.add(new BasicNameValuePair("Result", ""));

		nameValuePairs.add(new BasicNameValuePair("ExcludeTransType", ""));
		nameValuePairs.add(new BasicNameValuePair("ExcludePaymentType", ""));
		nameValuePairs.add(new BasicNameValuePair("ExcludeResult", ""));
		nameValuePairs.add(new BasicNameValuePair("ExcludeCardType", ""));

		nameValuePairs.add(new BasicNameValuePair("Register", ""));
		nameValuePairs.add(new BasicNameValuePair("NameOnCard", ""));
		nameValuePairs.add(new BasicNameValuePair("CardNum", ""));
		nameValuePairs.add(new BasicNameValuePair("CardType", ""));
		nameValuePairs.add(new BasicNameValuePair("User", ""));
		nameValuePairs.add(new BasicNameValuePair("SettleFlag", ""));
		nameValuePairs.add(new BasicNameValuePair("SettleMsg", ""));
		nameValuePairs.add(new BasicNameValuePair("SettleDt", ""));
		nameValuePairs.add(new BasicNameValuePair("TransformType", ""));
		nameValuePairs.add(new BasicNameValuePair("Xsl", "xml"));
		nameValuePairs.add(new BasicNameValuePair("ColDelim", ""));
		nameValuePairs.add(new BasicNameValuePair("RowDelim", ""));
		nameValuePairs.add(new BasicNameValuePair("IncludeHeader", ""));

		nameValuePairs.add(new BasicNameValuePair("CheckNum", ""));
		nameValuePairs.add(new BasicNameValuePair("AcctNum", ""));
		nameValuePairs.add(new BasicNameValuePair("RouteNum", ""));
		nameValuePairs.add(new BasicNameValuePair("ExtData", ""));
		nameValuePairs.add(new BasicNameValuePair("ExtData", ""));
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		logger.severe("!@#$%^&*!@#$%^@#$%" + nameValuePairs.toString());

		HttpResponse response = client.execute(post);
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null)
		{
			result.append(line);
		}
		String responseData = result.toString();
		responseData = responseData.replaceAll("&lt;", "<");
		responseData = responseData.replaceAll("&gt;", ">");

		return responseData;

	}

	private boolean checkTransactionForPrecapture(Invoice invoice, String userName, String password, String beginDate, String endDate, String approvalCode, String rpNum, String url) throws Exception
	{
		String xml = checkTransactionStatus(userName, password, beginDate, endDate, approvalCode, rpNum, url);
		try
		{
			PathLinkTransaction path = getTransactionByAuthCode(xml);
			logger.severe("beginDate" + beginDate + "endDate" + endDate + "approvalCode" + approvalCode + "begrpNuminDate" + rpNum + "beginDate" + beginDate + "beginDate" + beginDate
					+ "xml String----" + xml);
			logger.severe("sdfghjksdfgxml String----" + (invoice.getTotalAmount().equals(new Double(path.getForceCapture()))));
			if (path != null && path.getPayment_Type_ID() != null && path.getForceCapture_Cnt() != null && Integer.parseInt(path.getForceCapture_Cnt()) > 0)
			{

				logger.severe("xml String----" + invoice.getAuthCode());
				return true;

			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return false;
	}

	private PathLinkTransaction getTransactionByAuthCode(String xmlString)
	{
		PathLinkTransaction linkTransaction = new PathLinkTransaction();
		try
		{

			Document document = null;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try
			{
				builder = factory.newDocumentBuilder();
				document = builder.parse(new InputSource(new StringReader(xmlString)));
			}
			catch (Exception e)
			{
				logger.severe(e);
			}
			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			document.getDocumentElement().normalize();

			System.out.println("Root element :" + document.getDocumentElement().getNodeName());

			NodeList nList = document.getElementsByTagName("PaymentMethod");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++)
			{

				Node nNode = nList.item(temp);

				System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{

					Element eElement = (Element) nNode;

					linkTransaction.setPayment_Type_ID(eElement.getElementsByTagName("Payment_Type_ID").item(0).getTextContent());
					linkTransaction.setAuthorization(eElement.getElementsByTagName("Authorization").item(0).getTextContent());
					linkTransaction.setCapture(eElement.getElementsByTagName("Capture").item(0).getTextContent());
					linkTransaction.setForceCapture(eElement.getElementsByTagName("ForceCapture").item(0).getTextContent());
					linkTransaction.setPostAuth(eElement.getElementsByTagName("PostAuth").item(0).getTextContent());
					linkTransaction.setReturn(eElement.getElementsByTagName("Return").item(0).getTextContent());
					linkTransaction.setSale(eElement.getElementsByTagName("Sale").item(0).getTextContent());
					linkTransaction.setReceipt(eElement.getElementsByTagName("Receipt").item(0).getTextContent());
					linkTransaction.setRepeatSale(eElement.getElementsByTagName("RepeatSale").item(0).getTextContent());
					linkTransaction.setActivate(eElement.getElementsByTagName("Activate").item(0).getTextContent());
					linkTransaction.setDeactivate(eElement.getElementsByTagName("Deactivate").item(0).getTextContent());
					linkTransaction.setReload(eElement.getElementsByTagName("Reload").item(0).getTextContent());
					linkTransaction.setAuthorization_Cnt(eElement.getElementsByTagName("Authorization_Cnt").item(0).getTextContent());
					linkTransaction.setCapture_Cnt(eElement.getElementsByTagName("Capture_Cnt").item(0).getTextContent());
					linkTransaction.setForceCapture_Cnt(eElement.getElementsByTagName("ForceCapture_Cnt").item(0).getTextContent());
					linkTransaction.setPostAuth_Cnt(eElement.getElementsByTagName("PostAuth_Cnt").item(0).getTextContent());
					linkTransaction.setReturn_Cnt(eElement.getElementsByTagName("Return_Cnt").item(0).getTextContent());
					linkTransaction.setSale_Cnt(eElement.getElementsByTagName("Sale_Cnt").item(0).getTextContent());
					linkTransaction.setReceipt_Cnt(eElement.getElementsByTagName("Receipt_Cnt").item(0).getTextContent());
					linkTransaction.setRepeatSale_Cnt(eElement.getElementsByTagName("RepeatSale_Cnt").item(0).getTextContent());
					linkTransaction.setActivate_Cnt(eElement.getElementsByTagName("Activate_Cnt").item(0).getTextContent());
					linkTransaction.setDeactivate_Cnt(eElement.getElementsByTagName("Deactivate_Cnt").item(0).getTextContent());
					linkTransaction.setReload_Cnt(eElement.getElementsByTagName("Reload_Cnt").item(0).getTextContent());
					linkTransaction.setCnt(eElement.getElementsByTagName("Cnt").item(0).getTextContent());

				}

			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return linkTransaction;
	}

	@Override
	public void run()
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
			
			btnPreSettledAllTransacton(httpServletRequest, em, gatewayTypeIdString, true, checkPrecaptureTransaction, currentDate, orderheader);
			
			doneSignal.countDown();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
}
