/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.braintree.transact.amx.service.creditcard;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.payment.BraintreePayment;
import com.nirvanaxp.payment.gateway.braintree.data.ProcessCardPayment;
import com.nirvanaxp.payment.gateway.braintree.data.TransactionType;
import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.proxy.ProxyInterface;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.service.ServiceInterface;
import com.nirvanaxp.services.jaxrs.OrderManagementServiceBean;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType_;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.payment.TransactionStatus_;

public class ProcessCreditCardService extends ProcessCardPayment implements ProxyInterface
{
	private static final NirvanaLogger logger = new NirvanaLogger(ProcessCreditCardService.class.getName());
	/** The service interface. */
//	private ServiceInterface serviceInterface;

	public String userName;

	public String password;
	
	public BraintreePayment braintreePayment;
	public OrderHeader orderHeader;
	public OrderPaymentDetail orderPaymentDetail;

	/** The current method. */
//	private static int currentMethod = -1;

	public static final int processCreditCardManully = 1;

	public static final int processCreditCardThroughSwipe = 2;

	public ProcessCreditCardService(ServiceInterface serviceInterface, BraintreePayment braintreePayment)
	{
		super();
//		this.serviceInterface = serviceInterface;
		this.braintreePayment = braintreePayment;
	}


	

	/**
	 * Process ePayment Credit Card Transaction. Valid input values are:
	 * UserName & Password - Assigned by the payment gateway, <BR>
	 * TransType - Auth | Sale | Return | Void | Force | Capture | RepeatSale |
	 * CaptureAll, <BR>
	 * CardNum - Payment Card Number, ExpDate - Payment Card Expiration Date in
	 * MMYY format, <BR>
	 * MagData - Payment Card Track II Mag-Stripe Data, NameOnCard - Cardholders
	 * Name, Amount - Amount in DDDDD.CC format, <BR>
	 * InvNum - Invoice Tracking Number, PNRef - Reference Number Assigned by
	 * the payment gateway, <BR>
	 * Zip - CardHolder's billing ZipCode used with AVS, <BR>
	 * Street - CardHolder's billing Street Address used with AVS, <BR>
	 * CVNum - CardHolder's Card Verification Number used with CV Check, <BR>
	 * ExtData - Extended Data in XML, Valid Values are: CustomerID<BR>
	 * AuthCode - Original Authorization Code, <BR>
	 * CustCode - Customer Code used with Commercial Cards, <BR>
	 * TipAmt - Tip Amount <BR>
	 * TaxAmt - Tax Amount used with Commercial Cards, <BR>
	 * SequenceNum - Sequence Number used with Recurring Transactions, <BR>
	 * SequenceCount - Sequene Count used with Recurring Transactions, <BR>
	 * ServerID - Server ID, <BR>
	 * TrainingMode - Training Mode T or F, <BR>
	 * Force - Force Duplicates T or F, <BR>
	 * RegisterNum - Register Number, <BR>
	 * PONum - Purhcase Order Number, <BR>
	 * City - the City of the CardHolder's billing address<BR>
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	@Override
	public void processCatureAllCreditCardPayment(HttpServletRequest httpRequest, EntityManager em, TransactionType transType,Invoice invoice,OrderHeader orderHeader,OrderPaymentDetail orderPaymentDetail) throws JsonGenerationException, JsonMappingException,
			IOException
	{

		// "4444333322221111", "1213", "", "John Doe", "1.00"

		/*
		 * String serverUrl = PayLinkServerConstants.SERVER_URL +
		 * PayLinkServerConstants.OPERATION_CREDIT_CARD;
		 */
		this.orderHeader = orderHeader;
		this.orderPaymentDetail= orderPaymentDetail;
		sendRequestToServer(httpRequest, em, BraintreePayment.gateway,invoice);
		

	}

	/**
	 * Send request to server.
	 * 
	 * @param argumentsToPost
	 *            the arguments to post
	 * @param serverUrl
	 *            the server url
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	private void sendRequestToServer(HttpServletRequest httpRequest, EntityManager em, BraintreeGateway gateway,Invoice invoice) throws JsonGenerationException, JsonMappingException,
			IOException
	{

		if(gateway != null){
			Result<Transaction> transactionResult= null;
			try {
				BigDecimal totalAmount =orderPaymentDetail.getAmountPaid().add(orderPaymentDetail.getCreditcardTipAmt());
				// 
				if(totalAmount!=null){
					totalAmount = totalAmount.setScale(2,BigDecimal.ROUND_HALF_DOWN);
				}
				transactionResult = gateway.transaction()
						.submitForSettlement(invoice.getPnrRef(),totalAmount);
				
				logger.severe(totalAmount+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+transactionResult.getMessage());
				logger.severe("finally here");
			} catch (Exception e) {
				
				 logger.severe(e);
			}
			if(transactionResult != null){
				Transaction transaction = transactionResult.getTarget();
				EntityTransaction tx = em.getTransaction();
				if(tx.isActive()){
					responseObtainedFromWebService(httpRequest, em, transaction,orderHeader);
				}else{
					tx.begin();
					responseObtainedFromWebService(httpRequest, em, transaction,orderHeader);
					tx.commit();
				}
				
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.proxy.ProxyInterface#responseObtainedFromWebService(java
	 * .lang.String)
	 */
	@Override
	public void responseObtainedFromWebService(HttpServletRequest httpRequest, EntityManager em, Transaction transaction,OrderHeader orderheader) throws JsonGenerationException, JsonMappingException, IOException
	{

	
			// create total object which pass to server on click of credit card or
			// cash button
	
			try
			{

				if (transaction != null && transaction.getProcessorResponseText().equals("Approved") && orderheader != null)
				{
					
					OrderHeader newOrderHeader = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,OrderHeader.class, orderheader.getId());
					PaymentTransactionType orderDetailPaymentTransactionType = getPaymentTransactionType(httpRequest, em, "CaptureAll");

					TransactionStatus transactionStatusCCPreCapture = getTransactionStatusByName(httpRequest, em, "CC Settled");

					OrderPaymentDetail orderPaymentDetailToSend = new OrderPaymentDetail();
					
					orderPaymentDetailToSend.setDate(Utilities.getCurrentDateString());
					orderPaymentDetailToSend.setTime(Utilities.getCurrentTimeString());
					orderPaymentDetailToSend.setCreatedBy(orderheader.getUpdatedBy());
					orderPaymentDetailToSend.setPaymentTransactionType(orderDetailPaymentTransactionType);
					PaymentMethod paymentmethod = new PaymentMethod();
					paymentmethod = orderPaymentDetail.getPaymentMethod();
					orderPaymentDetailToSend.setPaymentMethod(paymentmethod);

					if (transaction.getId() != "" || transaction.getId() != null)
					{

						orderPaymentDetailToSend.setPnRef(transaction.getId());

					}
					

					if (transaction
			 				.getProcessorAuthorizationCode() != null && transaction
					 				.getProcessorAuthorizationCode().length() > 0)
					{
						orderPaymentDetailToSend.setAuthCode(transaction.getProcessorAuthorizationCode());
					}
					else
					{
						orderPaymentDetailToSend.setAuthCode(orderPaymentDetail.getAuthCode());
					}

					if (transaction.getPurchaseOrderNumber() != null && transaction.getPurchaseOrderNumber().length() > 0)
					{
						orderPaymentDetailToSend.setInvoiceNumber(transaction.getPurchaseOrderNumber());
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
					

					orderPaymentDetailToSend.setCashTipAmt(orderPaymentDetail.getCashTipAmt());
					orderPaymentDetailToSend.setCreditcardTipAmt(orderPaymentDetail.getCreditcardTipAmt());
					orderPaymentDetailToSend.setSeatId(orderPaymentDetail.getSeatId());
					orderPaymentDetailToSend.setExpiryMonth(orderPaymentDetail.getExpiryMonth());
					orderPaymentDetailToSend.setExpiryYear(orderPaymentDetail.getExpiryYear());
					orderPaymentDetailToSend.setCardNumber(orderPaymentDetail.getCardNumber());
					orderPaymentDetailToSend.setNirvanaXpBatchNumber(orderPaymentDetail.getNirvanaXpBatchNumber());
					
					orderPaymentDetailToSend.setChangeDue(new BigDecimal("0.00"));

					orderPaymentDetailToSend.setTransactionStatus(transactionStatusCCPreCapture);
					orderPaymentDetailToSend.setPayementGatewayId(orderPaymentDetail.getPayementGatewayId());
					orderPaymentDetailToSend.setOrderSourceGroupToPaymentGatewayTypeId(orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId());
					orderPaymentDetailToSend.setOrderSourceToPaymentGatewayTypeId(orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId());
					orderPaymentDetailToSend.setUpdatedBy(orderheader.getUpdatedBy());

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
					orderPaymentDetail.setUpdatedBy(orderheader.getUpdatedBy());
					newOrderHeader.setCashierId(orderheader.getCashierId());
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



	@Override
	public void responseObtainedFromWebService(HttpServletRequest httpRequest,
			EntityManager em, String str) throws JsonGenerationException,
			JsonMappingException, IOException {
		
		
	}

	private PaymentTransactionType getPaymentTransactionType(HttpServletRequest httpRequest, EntityManager em, String name)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<PaymentTransactionType> criteria = builder.createQuery(PaymentTransactionType.class);
		Root<PaymentTransactionType> r = criteria.from(PaymentTransactionType.class);
		TypedQuery<PaymentTransactionType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(PaymentTransactionType_.name), name)));
		return query.getSingleResult();

	}

	private TransactionStatus getTransactionStatusByName(HttpServletRequest httpRequest, EntityManager em, String name)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TransactionStatus> criteria = builder.createQuery(TransactionStatus.class);
		Root<TransactionStatus> r = criteria.from(TransactionStatus.class);
		TypedQuery<TransactionStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(TransactionStatus_.name), name)));
		return query.getSingleResult();

	}

//	private String getTimeZoneStrForJavaTimeZone(Timezone timezone)
//	{
//		String timeZone = timezone.getTimezoneName();
//		String parts[] = timeZone.split("Time");
//		String partData = (parts[1]);
//		partData = partData.trim();
//		return partData;
//	}

	

	
}
