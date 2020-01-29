/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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

import org.codehaus.jackson.map.ObjectMapper;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.Subscription.Status;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.NameConstant;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.server.util.HTTPClient;
import com.nirvanaxp.server.util.JSONResponse;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.email.ReceiptPDFFormat;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.jaxrs.INirvanaService;
import com.nirvanaxp.services.jaxrs.OrderServiceForPost;
import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.services.util.email.EmailTemplateKeys;
import com.nirvanaxp.storeForward.PaymentBatchManager;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail_;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType;
import com.nirvanaxp.types.entities.payment.PaymentGatewayType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType_;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.payment.TransactionStatus_;
import com.nirvanaxp.types.entities.recurringPayment.AddOns;
import com.nirvanaxp.types.entities.recurringPayment.Discounts;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.User_;
import com.nirvanaxp.wallet.jio.EncryptionUtil;
import com.nirvanaxp.wallet.jio.JioInputArgument;
import com.nirvanaxp.wallet.jio.JioInputRefundPacket;
import com.nirvanaxp.wallet.jio.JioTransaction;

public class BraintreePayment  {

	private static final NirvanaLogger logger = new NirvanaLogger(
			BraintreePayment.class.getName());
	
	
	    
	public static BraintreeGateway gateway = null;

	public BraintreePayment(Environment environment,String merchantId, String publicKey,String privateKey) throws FileNotFoundException, IOException {
		gateway = new BraintreeGateway(environment,
				merchantId,
				 publicKey,
				 privateKey);
	}
	
	public BraintreePayment() throws FileNotFoundException, IOException {

	}


	public String[] initilizeGateway(EntityManager em,int orderSourceGroupToPaymentGatewayTypeId,int orderSourceToPaymentGatewayTypeId) throws FileNotFoundException,IOException{
		String gatewayName =null;
		String merchantAccountId =null;
		PaymentGatewayType gatewayType = null;
		if(orderSourceToPaymentGatewayTypeId != 0){
			OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType = em.find(OrderSourceToPaymentgatewayType.class, orderSourceToPaymentGatewayTypeId);
			gatewayType = em.find(PaymentGatewayType.class, orderSourceToPaymentgatewayType.getPaymentgatewayTypeId());
			gatewayName = gatewayType.getName();
			merchantAccountId = orderSourceToPaymentgatewayType.getParameter4();
			if(orderSourceToPaymentgatewayType != null && gatewayName.equals(NameConstant.BRAINTREE_GATEWAY) &&  orderSourceToPaymentgatewayType.getParameter3()!=null){
				new BraintreePayment(getBraintreeEnvironment(orderSourceToPaymentgatewayType.getParameter3().trim()),orderSourceToPaymentgatewayType.getMerchantId().trim(), orderSourceToPaymentgatewayType.getParameter2().trim(), orderSourceToPaymentgatewayType.getParameter1().trim());
			}
		}else if(orderSourceGroupToPaymentGatewayTypeId!=0 ){
			OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentgatewayType = em.find(OrderSourceGroupToPaymentgatewayType.class, orderSourceGroupToPaymentGatewayTypeId );
			gatewayType = em.find(PaymentGatewayType.class, orderSourceGroupToPaymentgatewayType.getPaymentgatewayTypeId());
			gatewayName = gatewayType.getName();
			merchantAccountId = orderSourceGroupToPaymentgatewayType.getParameter4();
			if(orderSourceGroupToPaymentgatewayType != null && gatewayName.equals(NameConstant.BRAINTREE_GATEWAY) && orderSourceGroupToPaymentgatewayType.getParameter3() != null ){
				new BraintreePayment(getBraintreeEnvironment(orderSourceGroupToPaymentgatewayType.getParameter3().trim()),orderSourceGroupToPaymentgatewayType.getMerchantId().trim(), orderSourceGroupToPaymentgatewayType.getParameter2().trim(), orderSourceGroupToPaymentgatewayType.getParameter1().trim());
			}
		}else{
			gateway = null;
		}
		// setting gateway name in array
		String[] arrayofString ={gatewayName,merchantAccountId};
		return arrayofString;
	}
	
	/**
	 * This operation will return the payment token needed for communicating
	 * with the gateway, to generate a nonce.
	 * 
	 * @param httpRequest
	 * @param sessionId
	 * @param globalUserId
	 * @return
	 * @throws Exception 
	 */
	public OrderPaymentDetail voidOrderPaymentForBrainTree(HttpServletRequest httpRequest,
			EntityManager em, OrderPaymentDetail orderPaymentDetail)
			throws Exception {
		
		
		String[] arrayofString= initilizeGateway(em, orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId(), orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId());
		String gatewayName =arrayofString[0];
//		String merchantAccountId =arrayofString[1];
		if( gatewayName.equals(NameConstant.BRAINTREE_GATEWAY) ){
				
				if (orderPaymentDetail != null) {
					if (orderPaymentDetail.getPnRef() != null ) {
						Result<Transaction> result = gateway.transaction().voidTransaction(orderPaymentDetail.getPnRef());
						if (result.isSuccess()) {
							OrderPaymentDetail opd = (OrderPaymentDetail) new CommonMethods().getObjectById("OrderPaymentDetail", em,OrderPaymentDetail.class, orderPaymentDetail.getId());
							return opd;
						} else {
						    for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
						        throw new NirvanaXPException(
										new NirvanaServiceErrorResponse(
												error.getMessage(),
												error.getMessage(),
												null));
						    }
						}
					} else {
							throw new NirvanaXPException(
									new NirvanaServiceErrorResponse(
											MessageConstants.ERROR_CODE_USER_NOT_PRESENT_IN_DATABASE,
											MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE,
											null));
					}
			}
		}else if( gatewayName.equals(NameConstant.JIO_GATEWAY) ){
			// refundinfo ORIG_TXN_REF|ORG_TXN_RESPONSE_TIMESTAMP|ADDITIONALINFO1
			OrderPaymentDetail opd = (OrderPaymentDetail) new CommonMethods().getObjectById("OrderPaymentDetail", em,OrderPaymentDetail.class, orderPaymentDetail.getId());
			String objectPost = createChecksumAndPostObject(em, opd, httpRequest);
			HTTPClient client = new HTTPClient();
			String refundURL = "https://stpay.rpay.co.in/reliance-webpay/jiorefund";
			String response = client.sendPostJSONObject(objectPost, refundURL);
		
			ObjectMapper mapper = new ObjectMapper();
			JSONResponse obj = mapper.readValue(response, JSONResponse.class);
			response = obj.getResponse();
			String[] responseArray = response.split("\\|");
			if(responseArray[0].equals("000")){
				// setting JioTxnRefNum
				opd.setPnRef(responseArray[5]);
				return opd;
			}else{
				throw new NirvanaXPException(
						new NirvanaServiceErrorResponse(
								responseArray[7],
								responseArray[8],
								responseArray[8]));
			}
	
		}
		return null;
	}

	private String createChecksumAndPostObject(EntityManager em,OrderPaymentDetail orderPaymentDetail,HttpServletRequest httpRequest ){
		OrderPaymentDetail opd = (OrderPaymentDetail) new CommonMethods().getObjectById("OrderPaymentDetail", em,OrderPaymentDetail.class, orderPaymentDetail.getId());
		SimpleDateFormat date_time = new SimpleDateFormat("yyyyMMddHHmmss");
		String timeStamp =date_time.format(new Date());
		
		JioInputArgument inputArgument = new JioInputArgument();
		// generating checksum
		inputArgument.setAmount(opd.getAmountPaid()+"");
		inputArgument.setChannel("WEB");
		inputArgument.setExtref(orderPaymentDetail.getOrderHeaderId()+"_"+timeStamp);
		inputArgument.setToken("");
		inputArgument.setReturnUrl("http://nirvanaxp.com/");
		inputArgument.setTxntype("REFUND");
		inputArgument.setTimeStamp(timeStamp);
		inputArgument.setExtref(opd.getOrderHeaderId()+"_"+timeStamp);
		inputArgument = setJioCredential(em,  inputArgument, orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId(), orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId());
		String checkSum = generateCheckSum(em, inputArgument);
		// getting time
		String time = opd.getTime().replace(":", "");
		String date = opd.getDate().replace("-", "");
		// creating  refundinfo values
		String refundinfo = orderPaymentDetail.getPnRef()+"|"+date+time+"|"+"NA";	
		JioInputRefundPacket jioInputRefundPacket = new JioInputRefundPacket();
		jioInputRefundPacket = jioInputRefundPacket.setJioInputRefundPacket(inputArgument);
		jioInputRefundPacket.setChecksum(checkSum);
		jioInputRefundPacket.setRefundinfo(refundinfo);
		// creating jio transaction
		JioTransaction jioTransaction = new JioTransaction();
		jioTransaction.setAmount(inputArgument.getAmount());
		jioTransaction.setExtref(orderPaymentDetail.getOrderHeaderId()+"_"+timeStamp);
		jioTransaction.setTimestamp(timeStamp);
		jioTransaction.setTxntype(inputArgument.getTxntype());
	
		jioInputRefundPacket.setTransaction(jioTransaction);
		return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(jioInputRefundPacket);
		
	}

	/**
	 * This operation will return the payment token needed for communicating
	 * with the gateway, to generate a nonce.
	 * 
	 * @param httpRequest
	 * @param sessionId
	 * @param globalUserId
	 * @return
	 * @throws InvalidSessionException
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws NirvanaXPException
	 */
	public String getBraintreePaymentToken(HttpServletRequest httpRequest,
			EntityManager em,EntityManager globalEM, String userId,int orderSourceGroupToPaymentGatewayTypeId,int orderSourceToPaymentGatewayTypeId) throws IOException,
			 InvalidSessionException, NirvanaXPException {
		
		String token = null;
		if(orderSourceToPaymentGatewayTypeId != 0){
			OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType = em.find(OrderSourceToPaymentgatewayType.class, orderSourceToPaymentGatewayTypeId);
			if(orderSourceToPaymentgatewayType != null){
				PaymentGatewayType paymentGatewayType = em.find(PaymentGatewayType.class, orderSourceToPaymentgatewayType.getPaymentgatewayTypeId());
				if(paymentGatewayType.getName().equals(NameConstant.BRAINTREE_GATEWAY)){
					new BraintreePayment(getBraintreeEnvironment(orderSourceToPaymentgatewayType.getParameter3().trim()),orderSourceToPaymentgatewayType.getMerchantId().trim(), orderSourceToPaymentgatewayType.getParameter2().trim(), orderSourceToPaymentgatewayType.getParameter1().trim());
				}
				
			}
		}else if(orderSourceGroupToPaymentGatewayTypeId!=0){
			OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentgatewayType = em.find(OrderSourceGroupToPaymentgatewayType.class, orderSourceGroupToPaymentGatewayTypeId );	
			if(orderSourceGroupToPaymentgatewayType != null){
				PaymentGatewayType paymentGatewayType = em.find(PaymentGatewayType.class, orderSourceGroupToPaymentgatewayType.getPaymentgatewayTypeId());
				if(paymentGatewayType.getName().equals(NameConstant.BRAINTREE_GATEWAY)){
					new BraintreePayment(getBraintreeEnvironment(orderSourceGroupToPaymentgatewayType.getParameter3().trim()),orderSourceGroupToPaymentgatewayType.getMerchantId().trim(), orderSourceGroupToPaymentgatewayType.getParameter2().trim(), orderSourceGroupToPaymentgatewayType.getParameter1().trim());
				}
				
			}
		}else{
			gateway = null;
		}
		if(gateway != null){

			// at this point, we must have a UserSession object in the request
			UserSession session = (UserSession) httpRequest
					.getAttribute(INirvanaService.NIRVANA_USER_SESSION);
	
			// not getting user because alreday global user passing
			com.nirvanaxp.global.types.entities.User user = null;
			try {
				user = getGlobalUserById(globalEM, userId);
			} catch (Exception e1) {
				
				throw new NirvanaXPException(
						new NirvanaServiceErrorResponse(
								MessageConstants.ERROR_CODE_USER_NOT_PRESENT_IN_DATABASE,
								MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE,
								null));
			}
			
	
			// get schema name for this session
			
			String schemaName = session.getSchema_name();
			
	
			// form the customer id for the combination of logged in user
			// and the order for which we will request token
			String btCustomerId = schemaName + "-" + userId;
			logger.fine("going to ask for a braintree payment token for : ",
					btCustomerId);
			Customer customer = null;
			String customerId = null;
			// now get client token request
			
				// find whether customer exist or not
				try {
					customer = gateway.customer().find(btCustomerId);
				} catch (Exception e) {
					// customer not present
				}
	
				if (customer == null) {
					customerId = createCustomer(user, btCustomerId);
				} else {
					customerId = customer.getId();
				}
			
			ClientTokenRequest clientTokenRequest = new ClientTokenRequest()
					.customerId(customerId);
			token = gateway.clientToken().generate(clientTokenRequest);
	
			logger.fine("generated Braintree token:", token);
	
			if (token == null) {
				throw new NirvanaXPException(
						new NirvanaServiceErrorResponse(
								MessageConstants.ERROR_CODE_BRAINTREE_TOKEN_NOT_GENERATED,
								MessageConstants.ERROR_MESSAGE_BRAINTREE_TOKEN_NOT_GENERATED,
								null));
			}
		}
		return token;

	}

	/**
	 * This operation will return the payment token needed for communicating
	 * with the gateway, to generate a nonce.
	 * 
	 * @param httpRequest
	 * @param sessionId
	 * @param globalUserId
	 * @return
	 * @throws Exception 
	 * @throws DatabaseException
	 */
	public String authorizeOrderForBrainTree(HttpServletRequest httpRequest,
			EntityManager em, String nounce, OrderPacket orderPacket,int orderSourceGroupToPaymentGatewayTypeId,int orderSourceToPaymentGatewayTypeId,String authtoken)
			throws Exception {
		
		
		OrderHeader orderHeader = orderPacket.getOrderHeader();
		String[] arrayofString= initilizeGateway(em, orderSourceGroupToPaymentGatewayTypeId, orderSourceToPaymentGatewayTypeId);
		String gatewayName =arrayofString[0];
		String merchantAccountId =arrayofString[1];
		// at this point, we must have a UserSession object in the request
		if(gatewayName.equals(NameConstant.BRAINTREE_GATEWAY)){
			UserSession session = (UserSession) httpRequest
				.getAttribute(INirvanaService.NIRVANA_USER_SESSION);

		// getting order header from the database
		if (orderHeader != null) {

			if (orderHeader.getUsersId()==null) {
				throw new NirvanaXPException(
						new NirvanaServiceErrorResponse(
								MessageConstants.ERROR_CODE_ORDERID_0_EXCEPTION,
								MessageConstants.ERROR_MESSAGE_ORDERID_0_EXCEPTION_DISPLAY_MESSAGE,
								null));

			} else {
				User user = null;
				try {
					user = getUserById(em, orderHeader.getUsersId());
				} catch (Exception e1) {
					
					throw new NirvanaXPException(
							new NirvanaServiceErrorResponse(
									MessageConstants.ERROR_CODE_USER_NOT_PRESENT_IN_DATABASE,
									MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE,
									null));
				}
				if (user != null) {

					// get schema name for this session
					String schemaName = session.getSchema_name();

					// form the customer id for the combination of logged in
					// user
					// and the order for which we will request token
					String btCustomerId = schemaName + "-"
							+ user.getGlobalUsersId();
					logger.fine(
							"going to ask for a braintree payment token for : ",
							btCustomerId);

					// find whether customer exist or not
					Customer customer = null;
					String customerId = null;

					try {
						customer = gateway.customer().find(btCustomerId);
					} catch (Exception e) {
						// customer not present
						logger.fine(
								"customer not present in gateway: ",
								btCustomerId);

					}

					if (customer == null) {
						customerId = createCustomer(user, btCustomerId);
					} else {
						customerId = customer.getId();
					}
						
					List<OrderPaymentDetail> orderPaymentDetails = orderHeader.getOrderPaymentDetails();
//					CustomerRequest request1 = new CustomerRequest().paymentMethodNonce(nounce);
//					Result<Customer> updateResult = gateway.customer()
//								.update(customerId, request1);
//						if(updateResult != null && updateResult.getMessage() != null){
//							if(updateResult.getMessage().equals(MessageConstants.ERROR_MESSAGE_CANNOT_USE_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE)){
//								logger.fine(
//										MessageConstants.ERROR_MESSAGE_CANNOT_USE_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE,
//										btCustomerId);
//								throw new NirvanaXPException(
//										new NirvanaServiceErrorResponse(
//												MessageConstants.ERROR_CODE_CANNOT_USE_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE,
//												MessageConstants.ERROR_MESSAGE_CANNOT_USE_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE ,
//												null));
//								
//							}else if(updateResult.getMessage().equals(MessageConstants.ERROR_MESSAGE_UNKNOWN_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE)){
//
//								logger.fine(
//										MessageConstants.ERROR_MESSAGE_UNKNOWN_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE,
//										btCustomerId);
//								throw new NirvanaXPException(
//										new NirvanaServiceErrorResponse(
//												MessageConstants.ERROR_CODE_UNKNOWN_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE,
//												MessageConstants.ERROR_MESSAGE_UNKNOWN_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE ,
//												null));
//								
//							
//							}
//						}
					com.nirvanaxp.types.entities.recurringPayment.Subscription subscription = orderPacket.getSubscription();
						if(orderPacket.getSubscription() == null){
						List<OrderPaymentDetail> details =new ArrayList<OrderPaymentDetail>();
						PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
						String batchNumber = batchManager.getCurrentBatchIdBySession(httpRequest,em, 
								orderHeader.getLocationsId(), true,orderPacket,orderHeader.getUpdatedBy());
						for (OrderPaymentDetail detail : orderPaymentDetails) {
							// 46661 changing flow as per new tip flow 
							// now we need submit tip at the time of settlement
							
//								BigDecimal totalAmount =detail.getAmountPaid().add(detail.getCreditcardTipAmt());
//								if(totalAmount!=null){
//									totalAmount = totalAmount.setScale(2,
//											BigDecimal.ROUND_HALF_DOWN);
//								}
							BigDecimal totalAmount =detail.getAmountPaid();
							
					 
								TransactionRequest request = null;
								// this condition execute when business specific merchant account present on braintree acount to support multiple merchant account in one braintree account
								// if merchant account is not present then it will take default account merchant account which is else condition
								if(merchantAccountId!=null && merchantAccountId.length()>0){
									 request = new TransactionRequest().paymentMethodToken("token")
									.amount(totalAmount).orderId(orderHeader.getId()+"").merchantAccountId(merchantAccountId)
									.customerId(customerId).options().storeInVaultOnSuccess(true)
									.done();
								}else{
									request = new TransactionRequest().paymentMethodNonce(nounce)
									.amount(totalAmount).orderId(orderHeader.getId()+"")
									.customerId(customerId).options().storeInVaultOnSuccess(true)
									.done();
								}
							
								
								Result<Transaction> result = gateway.transaction().sale(request);
								
								logger.severe(new NirvanaXPException(
										new NirvanaServiceErrorResponse(
												MessageConstants.ERROR_CODE_FROM_GATEWAY,
												result.getMessage(),
												null)));
								
								if(result.getMessage() != null){
									 logger.severe("Status of trasction of OrderPaymentDetails :-"+detail.getId()+" /n Message from gateway :- "+result.getMessage());
										throw new NirvanaXPException(
												new NirvanaServiceErrorResponse(
														MessageConstants.ERROR_CODE_FROM_GATEWAY,
														result.getMessage().replace("\n", ""),
														null));
								}else{
									Transaction transaction = result.getTarget();
									if(transaction != null){
									if(transaction.getProcessorResponseText().equals("Approved")){
										TransactionStatus transactionStatus = getTransactionStatusByName(em, "CC Auth");
										detail.setTransactionStatus(transactionStatus);
										detail.setPnRef(transaction.getId());
										detail.setAuthCode(transaction.getProcessorAuthorizationCode());
										detail.setCardType(transaction.getCreditCard().getCardType());
										detail.setNirvanaXpBatchNumber(batchNumber);
										details.add(detail);
										

										try {
											// insert into order history
											ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
											// 0 because mail sending from dine in
											String data = "";
											
											data = receiptPDFFormat.createReceiptPDFString(em, httpRequest, orderHeader.getId(), 1, true, false)
													.toString();
										 
											// Send email functionality :- printing order
											// number instead of orderID :- By AP 2015-12-29
											EmailTemplateKeys.sendOrderReceivedEmailToCustomer(httpRequest, em, orderHeader.getLocationsId(), orderHeader.getUsersId(),
													orderHeader.getUpdatedBy(), data, EmailTemplateKeys.ORDER_RECEIVED, orderHeader.getOrderNumber(), null,null);

										} catch (Exception e) {
											logger.severe(httpRequest, e, "Could not send email due to configuration mismatch");
										}
									}
									
								}
								}
						
						}
						
						orderPacket.getOrderHeader().setOrderPaymentDetails(details);
						orderHeader = new OrderServiceForPost().updateOrderPaymentForBraintree(httpRequest, em, orderPacket);
						return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
						}else{
							// getting customer to get payment token
							try {
								customer = gateway.customer().find(btCustomerId);
							} catch (Exception e) {
								// customer not present
							}
							Result<Subscription> result = null;
							
							// getting payment method token
							String token =customer.getDefaultPaymentMethod().getToken();
							
							// creating subscription based on plan id
							SubscriptionRequest request = new SubscriptionRequest()
						    .paymentMethodToken(token)
						    .planId(subscription.getPlanId())
						    .paymentMethodNonce(nounce).options().startImmediately(true).done();

							// checking for addons
							if(subscription.getAddOnsList() != null){
								for(AddOns addon:subscription.getAddOnsList()){
									if( addon != null){
	//									request.addOns().update(addon.getInheritedFromId()).amount(amountDeduted).done();
										if(addon.getInheritedFromId()!= null){
											request.addOns().add().inheritedFromId(addon.getInheritedFromId());
										if(addon.getAmount()!= null){
											request.addOns().add().amount(addon.getAmount());
										}
										if(addon.getQuantity()!= 0){
											request.addOns().add().quantity(addon.getQuantity());
										}
								        request.addOns().add().done(); 
										}
									}
								}
							}
							
							// checking for discounts
							if(subscription.getDiscountList() != null){
								for(Discounts discount:subscription.getDiscountList()){
									if( discount != null){
										//request.addOns().update(addon.getInheritedFromId()).amount(amountDeduted).done();
											if(discount.getInheritedFromId()!= null){
												request.discounts().add().inheritedFromId(discount.getInheritedFromId());
											if(discount.getAmount()!= null){
												request.discounts().add().amount(discount.getAmount());
											}
											if(discount.getQuantity()!= 0){
												request.discounts().add().quantity(discount.getQuantity());
											}
									        request.discounts().add().done(); 
											
										}
									}
								}
							}
						result = gateway.subscription().create(request);
						if(result != null && result.getMessage()!= null && result.getMessage().length()>0){
							return result.getMessage();
						}
						Subscription  mySubscription = result.getTarget();
						if(mySubscription.getStatus()!=null){
							Status mySubscriptionStatus= mySubscription.getStatus();
							String status = mySubscriptionStatus.toString();
							if(status.equals("Active")){
								List<OrderPaymentDetail> details =new ArrayList<OrderPaymentDetail>();
								for (OrderPaymentDetail detail : orderPaymentDetails) {
									TransactionStatus transactionStatus = getTransactionStatusByName(em, "CC Settled");
									List<Transaction> transaction= mySubscription.getTransactions();
									for(Transaction transaction2:transaction){
										detail.setAuthCode(transaction2.getProcessorAuthorizationCode());
										detail.setCardType(transaction2.getCreditCard().getCardType());
										detail.setPnRef(transaction2.getId());
									}
									detail.setTransactionStatus(transactionStatus);
									detail.setInvoiceNumber(mySubscription.getId());
									details.add(detail);
									
									
									orderPacket.getOrderHeader().setOrderPaymentDetails(details);
									orderHeader = new OrderServiceForPost().updateOrderPaymentForBraintree(httpRequest, em, orderPacket);
								}
							}
						
							return status;
						}
						}
					}
				}
			}
	}else if(gatewayName.equals(NameConstant.JIO_GATEWAY) ||
			gatewayName.equals(NameConstant.DATACAP_GATEWAY) || 
			gatewayName.equals(NameConstant.DATACAP_GATEWAY_WITH_FIRSTDATA))
	{
				orderHeader = new OrderServiceForPost().updateOrderPayment(httpRequest, em, orderPacket,false,authtoken,true);
				return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
	}
		return "";

	}


	/**
	 * This operation will return the payment token needed for communicating
	 * with the gateway, to generate a nonce.
	 * 
	 * @param httpRequest
	 * @param sessionId
	 * @param globalUserId
	 * @return
	 * @throws InvalidSessionException
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws NirvanaXPException
	 */
 public	boolean precaptureOrderForBrainTree(HttpServletRequest httpRequest,
			EntityManager em,  int updatedBy,String fromDate, String toDate,int businessId)
			throws IOException,  InvalidSessionException,
			NirvanaXPException {
		boolean result = false;
		List<OrderPaymentDetail> orderPaymentDetailToProcess = getAllAuthOrderPaymentDetails(em,fromDate,toDate,businessId);
		for (OrderPaymentDetail detail : orderPaymentDetailToProcess) {
			precaptureTransactionOnGateway(em, httpRequest, detail, updatedBy);
		}

		return result;
	}

//	private OrderHeader getOrderHeaderById(HttpServletRequest httpRequest,
//			EntityManager em, int id) {
//		OrderHeader orderHeader = null;
//		OrderManagementServiceBean bean = new OrderManagementServiceBean(
//				httpRequest);
//		orderHeader = bean.getOrderById(em, id);
//		return orderHeader;
//	}

	private Environment getBraintreeEnvironment(String braintreeEnvironment) {
		switch (braintreeEnvironment) {
		case "I": {
			return Environment.SANDBOX;
		}
		default:
		case "A": {
			return Environment.PRODUCTION;
		}
		}
	}

	private String createCustomer(User user, String customerId) {

		CustomerRequest request = new CustomerRequest()
				.firstName(user.getFirstName()).lastName(user.getLastName())
				.company(null).email(user.getEmail()).fax(null)
				.phone(user.getPhone()).website(null).id(customerId);
		Result<Customer> result = gateway.customer().create(request);

		result.isSuccess();
		// true
		return result.getTarget().getId();

	}
	
	private String createCustomer(com.nirvanaxp.global.types.entities.User user, String customerId) {

		CustomerRequest request = new CustomerRequest()
				.firstName(user.getFirstName()).lastName(user.getLastName())
				.company(null).email(user.getEmail()).fax(null)
				.phone(user.getPhone()).website(null).id(customerId);
		Result<Customer> result = gateway.customer().create(request);

		result.isSuccess();
		// true
		return result.getTarget().getId();

	}

	public User getUserById(EntityManager em, String id) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> r = criteria.from(User.class);
		TypedQuery<User> query = em.createQuery(criteria.select(r).where(
				builder.equal(r.get(User_.id), id)));
		User result = (User) query.getSingleResult();
		return result;

	}
	
	public com.nirvanaxp.global.types.entities.User getGlobalUserById(EntityManager em, String id) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<com.nirvanaxp.global.types.entities.User> criteria = builder.createQuery(com.nirvanaxp.global.types.entities.User.class);
		Root<com.nirvanaxp.global.types.entities.User> r = criteria.from(com.nirvanaxp.global.types.entities.User.class);
		TypedQuery<com.nirvanaxp.global.types.entities.User> query = em.createQuery(criteria.select(r).where(
				builder.equal(r.get(com.nirvanaxp.global.types.entities.User_.id), id)));
		com.nirvanaxp.global.types.entities.User result = (com.nirvanaxp.global.types.entities.User) query.getSingleResult();
		return result;

	}
	

	public List<OrderPaymentDetail> getOrderPaymentDetailsByOrderHeaderId(
			EntityManager em, int orderHeaderId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderPaymentDetail> criteria = builder
				.createQuery(OrderPaymentDetail.class);
		Root<OrderPaymentDetail> r = criteria.from(OrderPaymentDetail.class);
		TypedQuery<OrderPaymentDetail> query = em.createQuery(criteria
				.select(r).where(
						builder.equal(r.get(OrderPaymentDetail_.orderHeaderId),
								orderHeaderId)));
		List<OrderPaymentDetail> result = (List<OrderPaymentDetail>) query
				.getResultList();
		return result;

	}

	private OrderPaymentDetail addOrderPaymentDetails(EntityManager em,OrderPaymentDetail detail){
		EntityTransaction tx = em.getTransaction();
		try {
			// start transaction
			tx.begin();
			em.persist(detail);
			tx.commit();
		}
		catch (RuntimeException e) {
			// on error,e if transaction active, rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}
		return detail;
	}
	
	private TransactionStatus getTransactionStatusByName(
			EntityManager em, String name) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TransactionStatus> criteria = builder
				.createQuery(TransactionStatus.class);
		Root<TransactionStatus> r = criteria.from(TransactionStatus.class);
		TypedQuery<TransactionStatus> query = em.createQuery(criteria
				.select(r).where(
						builder.equal(r.get(TransactionStatus_.name),
								name)));
		TransactionStatus result = (TransactionStatus) query
				.getSingleResult();
		return result;
	}
	
	private PaymentTransactionType getPaymentTransactionTypeByName(
			EntityManager em, String name) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<PaymentTransactionType> criteria = builder
				.createQuery(PaymentTransactionType.class);
		Root<PaymentTransactionType> r = criteria.from(PaymentTransactionType.class);
		TypedQuery<PaymentTransactionType> query = em.createQuery(criteria
				.select(r).where(
						builder.equal(r.get(PaymentTransactionType_.name),
								name)));
		PaymentTransactionType result = (PaymentTransactionType) query
				.getSingleResult();
		return result;
	}
//	private TransactionStatus getTransactionStatusById(
//			EntityManager em, int id) {
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		CriteriaQuery<TransactionStatus> criteria = builder
//				.createQuery(TransactionStatus.class);
//		Root<TransactionStatus> r = criteria.from(TransactionStatus.class);
//		TypedQuery<TransactionStatus> query = em.createQuery(criteria
//				.select(r).where(
//						builder.equal(r.get(TransactionStatus_.id),
//								id)));
//		TransactionStatus result = (TransactionStatus) query
//				.getSingleResult();
//		return result;
//	}
	private List<OrderPaymentDetail> getAllAuthOrderPaymentDetails(EntityManager em,String fromdate,String toDate,int businessId){
		String queryString = "select "+objectsWithColumnStr+" from order_payment_details opd left join transaction_status ts on opd.transaction_status_id = ts.id  "
				+ " left join  payment_method pm on opd.payment_method_id= pm.id "
				+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
				+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id"
				+ " join order_header oh on oh.id = opd.order_header_id "
				+ " join locations l on l.id= oh.locations_id  "
				+ " where  ts.name = 'CC Auth'  "
				+ " and opd.id in ( SELECT max(id) FROM order_payment_details  group by order_header_id ) and l.business_id = ? and opd.date between  ? and ?   ";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, businessId).setParameter(2, fromdate).setParameter(3, toDate).getResultList();
		List<OrderPaymentDetail> details = new ArrayList<OrderPaymentDetail>();
		
		for(Object[] objRow : resultList){
			OrderPaymentDetail paymentDetail = new OrderPaymentDetail();
			paymentDetail.setOrderPaymentDetailsResultSet(objRow);
			details.add(paymentDetail);
		}
		return details;
	} 
	
	private String objectsWithColumnStr = new StringBuilder()
	.append("opd.id as  opd_id,opd.order_header_id as opd_order_header_id,opd.payment_method_id as opd_payment_method_id,opd.payment_transaction_type_id as opd_payment_transaction_type_id,opd.seat_id as opd_seat_id,opd.pn_ref as opd_pn_ref, opd.host_ref as opd_host_ref,")
	.append("opd.date as opd_date,opd.time as opd_time,opd.register as opd_register,opd.amount_paid as opd_amount_paid,opd.balance_due as opd_balance_due,opd.total_amount as opd_total_amount,opd.card_number opd_card_number,opd.expiry_month as opd_expiry_month,opd.expiry_year as opd_expiry_year,opd.security_code as opd_security_code,")
	.append("opd.auth_amount as opd_auth_amount,opd.settled_amount as opd_settled_amount,opd.tip_amount as opd_tip_amount,opd.auth_code as opd_auth_code,opd.pos_entry as opd_pos_entry,opd.batch_number as opd_batch_number,opd.avs_response as opd_avs_response,opd.cv_result as opd_cv_result,")
	.append("opd.cv_message as opd_cv_message,opd.result as opd_result,opd.message as opd_message,opd.comments as opd_comments,opd.is_refunded as opd_is_refunded,opd.created as opd_created,opd.created_by as opd_created_by,")
	.append("opd.updated as opd_updated,opd.updated_by as opd_updated_by,opd.signature_url as opd_signature_url,opd.transaction_status_id as opd_transaction_status_id,opd.cash_tip_amt as opd_cash_tip_amt,opd.creditcard_tip_amt as opd_creditcard_tip_amt,opd.change_due as opd_change_due,")
	.append("opd.card_type as opd_card_type,opd.acq_ref_data as opd_acq_ref_data,opd.order_source_group_to_paymentgatewaytype_id as opd_order_source_group_to_paymentgatewaytype_id,opd.order_source_to_paymentgatewaytype_id as opd_order_source_to_paymentgatewaytype_id,")
	.append("pm.id as pm_id,pm.payment_method_type_id as pm_payment_method_type_id,pm.name as pm_name,pm.display_name as pm_display_name, pm.description as pm_description,pm.locations_id as pm_locations_id,pm.status as pm_status,pm.is_active as pm_is_active,pm.display_sequence as pm_display_sequence, pm.created as pm_created,")
	.append("pm.created_by as pm_created_by,pm.updated as pm_updated,pm.updated_by as pm_updated_by,")
	.append("ptt.id as ptt_id,ptt.name as ptt_name,ptt.display_name as ptt_display_name,ptt.display_sequence as ptt_display_sequence,ptt.status as ptt_status,ptt.locations_id as ptt_locations_id,ptt.created as ptt_created,ptt.created_by as ptt_created_by,ptt.updated as ptt_updated,ptt.updated_by as ptt_updated_by,")
	.append("ts.id as ts_id,ts.name as ts_name,ts.display_name as ts_display_name,ts.display_sequence as ts_display_sequence,ts.paymentgateway_type_id as ts_paymentgateway_type_id , ts.created as ts_created,ts.created_by as ts_created_by,ts.updated as ts_updated,ts.updated_by ts_updated_by,ts.status as ts_status,")
	.append("pgt.id as pgt_id,pgt.name as pgt_name, pgt.display_name as pgt_display_name,pgt.created as pgt_created, pgt.created_by as pgt_created_by, pgt.updated as pgt_updated, pgt.updated_by as pgt_updated_by, pgt.status as pgt_status, pgt.location_id as pgt_location_id, ")
	.append("pgt.display_sequence as pgt_display_sequence,opd.payementgateway_id as opd_payementgateway_id,opd.host_ref_str as opd_host_ref_str,opd.invoice_number as opd_invoice_number,opd.nirvanaxp_batch_number as opd_nirvanaxp_batch_number  ").toString();

	private boolean updateOrderPaymentDetailsStatusByOrderHeaderId(EntityManager em, int transactionStatusId, int updatedBy, String orderHeaderId){
		boolean result = false;
		String sql = "update order_payment_details set transaction_status_id=? , updated_by=? where order_header_id=? ";
		int count = 0;
		EntityTransaction tx = em.getTransaction();
		try {
			// start transaction
			tx.begin();
			count = em.createNativeQuery(sql).setParameter(1, transactionStatusId).setParameter(2, updatedBy).setParameter(3, orderHeaderId).executeUpdate();
			tx.commit();
		}
		catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}
		if(count>0){
			result=true;
		}
		return result;
	}
	
	private boolean updateOrderHeaderByOrderHeaderId(EntityManager em, int updatedBy, String orderHeaderId){
		boolean result = false;
		String sql = "update order_header set updated_by=? where id=? ";
		int count = 0;
		EntityTransaction tx = em.getTransaction();
		try {
			// start transaction
			tx.begin();
			count = em.createNativeQuery(sql).setParameter(1, updatedBy).setParameter(2, orderHeaderId).executeUpdate();
			tx.commit();
		}
		catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}
		if(count>0){
			result=true;
		}
		return result;
	}
	
	private boolean precaptureTransactionOnGateway(EntityManager em,HttpServletRequest httpRequest, OrderPaymentDetail orderPaymentDetail,int updatedBy)
	throws FileNotFoundException,IOException{
		boolean result = false;
		initilizeGateway(em, orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId(),orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId());
		
		if(gateway != null){
		Result<Transaction> transactionResult = gateway.transaction()
				.submitForSettlement(orderPaymentDetail.getPnRef());
		if (transactionResult.getMessage() != null) {
			
			logger.severe("Status of trasction of OrderPaymentDetails :-"
					+ orderPaymentDetail.getId()
					+ " /n Message from gateway :- "
					+ transactionResult.getMessage());
		} else {
			Transaction transaction = transactionResult.getTarget();
			if (transaction != null) {
				if (transaction.getProcessorResponseText().equals(
						"Approved")) {
			
					TransactionStatus transactionStatus = null;
					try {
						transactionStatus = getTransactionStatusByName(
								em, "CC Pre Capture");
					} catch (Exception e) {
						
						logger.severe("unable to find CC Pre Capture status on database");
					}
					PaymentTransactionType paymentTransactionType = null;
					try {
						paymentTransactionType = getPaymentTransactionTypeByName(em, "CaptureAll");
					} catch (Exception e) {
						
						logger.severe("unable to find CaptureAll status on database");
					}
					orderPaymentDetail.setTransactionStatus(transactionStatus);
					orderPaymentDetail.setPaymentTransactionType(paymentTransactionType);
		 			orderPaymentDetail.setPnRef(transaction.getId());
			 		orderPaymentDetail.setAuthCode(transaction
			 				.getProcessorAuthorizationCode());
					OrderPaymentDetail detail = new OrderPaymentDetail();
					detail = detail.setOrderPaymentDetail(orderPaymentDetail);
					detail = addOrderPaymentDetails(em, detail);
					if (detail != null) {
						updateOrderPaymentDetailsStatusByOrderHeaderId(em,
								transactionStatus.getId(), updatedBy,
								detail.getOrderHeaderId());
						updateOrderHeaderByOrderHeaderId(em, updatedBy,
								detail.getOrderHeaderId());
						OrderHeader header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,OrderHeader.class,
								detail.getOrderHeaderId());
						new InsertIntoHistory().insertOrderIntoHistory(httpRequest,
								header, em);

					}

				}

			}
		}
		}
		return result;
	}
	
 public	String generateCheckSum(EntityManager em, JioInputArgument inputArgument){
		String data = inputArgument.getClientId()+"|"+inputArgument.getAmount()+"|"
		+inputArgument.getExtref()+"|"+inputArgument.getChannel()+"|"+ inputArgument.getMerchantId()+"|"+inputArgument.getToken()+"|"
				+inputArgument.getReturnUrl()+"|"+inputArgument.getTimeStamp()+"|"+inputArgument.getTxntype();
		
		return  new EncryptionUtil().hmacDigest(data, inputArgument.getChecksumSeed(), "HmacSHA256");
		
	}
	public JioInputArgument setJioCredential(EntityManager em,JioInputArgument inputArgument,int orderSourceGroupToPaymentGatewayTypeId,int orderSourceToPaymentGatewayTypeId){
		if(orderSourceGroupToPaymentGatewayTypeId !=0){
			OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentgatewayType = em.find(OrderSourceGroupToPaymentgatewayType.class, orderSourceGroupToPaymentGatewayTypeId);
			if(orderSourceGroupToPaymentgatewayType != null){
				inputArgument.setMerchantId(orderSourceGroupToPaymentgatewayType.getMerchantId());
				inputArgument.setClientId(orderSourceGroupToPaymentgatewayType.getParameter1());
				inputArgument.setChecksumSeed(orderSourceGroupToPaymentgatewayType.getParameter2());
			}
		}else{
			OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType = em.find(OrderSourceToPaymentgatewayType.class, orderSourceToPaymentGatewayTypeId);
			inputArgument.setMerchantId(orderSourceToPaymentgatewayType.getMerchantId());
			inputArgument.setClientId(orderSourceToPaymentgatewayType.getParameter1());
			inputArgument.setChecksumSeed(orderSourceToPaymentgatewayType.getParameter2());
		}
		return inputArgument;
	}

}
