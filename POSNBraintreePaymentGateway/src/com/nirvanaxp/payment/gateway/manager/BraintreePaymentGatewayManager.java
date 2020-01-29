/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.payment.BraintreePayment;
import com.nirvanaxp.payment.gateway.braintree.data.TransactionType;
import com.nirvanaxp.payment.gateway.braintree.transact.amx.service.creditcard.ProcessCreditCardService;
import com.nirvanaxp.payment.gateway.data.CreditCard;
import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.data.SupportedCardType;
import com.nirvanaxp.payment.gateway.data.SupportedPaymentAction;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.service.ServiceInterface;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType;
import com.nirvanaxp.types.entities.payment.PaymentGatewayType;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;

public class BraintreePaymentGatewayManager implements ServiceInterface
{

	private SupportedPaymentAction supportedPaymentAction;
	private SupportedCardType supportedCardType;
//	private OrderHeader orderheader;
//	private OrderPaymentDetail orderPaymentDetail;
	
	private CreditCard creditCard;
	private Invoice invoice;
	private MerchentAccount merchentAccount;
	private String transactionType;
//	private String serverUrl;
	private BraintreePayment braintreePayment;
	private static final NirvanaLogger logger = new NirvanaLogger(BraintreePaymentGatewayManager.class.getName());

	public BraintreePaymentGatewayManager(SupportedPaymentAction supportedPaymentAction, SupportedCardType supportedCardType,
			CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount,
			String transactionType,BraintreePayment braintreePayment)
	{

		this.setCreditCard(creditCard);
		this.setSupportedPaymentAction(supportedPaymentAction);
		this.setSupportedCardType(supportedCardType);
		this.setInvoice(invoice);
		this.setMerchentAccount(merchentAccount);
		this.transactionType = (transactionType);
		this.braintreePayment =braintreePayment;
		
	}

	public SupportedPaymentAction getSupportedPaymentAction()
	{
		return supportedPaymentAction;
	}

	public void setSupportedPaymentAction(SupportedPaymentAction supportedPaymentAction)
	{
		this.supportedPaymentAction = supportedPaymentAction;
	}

	public void ProcessCreditCardRequest(HttpServletRequest httpRequest, EntityManager em,OrderHeader orderHeader,OrderPaymentDetail orderPaymentDetail) throws JsonGenerationException, JsonMappingException, IOException
	{
		
		ProcessCreditCardService processCreditCardService = new ProcessCreditCardService(this,braintreePayment);
		if (supportedPaymentAction.equals(SupportedPaymentAction.ACTION_PAYMENT_SWIPE_ENCYPTED))
		{
			 if (transactionType.equals(TransactionType.TRANSACTION_TYPE_CAPTURE_ALL.getName()))
			{
//				this.orderheader = orderHeader;
				processCreditCardService.processCatureAllCreditCardPayment(httpRequest, em, TransactionType.TRANSACTION_TYPE_CAPTURE_ALL, invoice,orderHeader, orderPaymentDetail);

			}
		}

	}


	public void ProcessPayment(HttpServletRequest httpRequest, EntityManager em,List<OrderHeader> orderHeaderList ) throws JsonGenerationException, JsonMappingException, IOException
	{
		
		// required for reporting service
		PaymentMethod paymentMethod = null;
		// && paymentgateway != null
		if (orderHeaderList.size() > 0)
		{

			for (OrderHeader orderHeader : orderHeaderList)
			{

//				this.orderheader = orderHeader;
			
				List<OrderPaymentDetail> orderDetailPaymentList = orderHeader.getOrderPaymentDetails();

				if (orderDetailPaymentList != null && orderDetailPaymentList.size() > 0)
				{
//					long sizeOfList = orderHeader.getOrderPaymentDetails().size();

					
					for (OrderPaymentDetail orderPaymentDetail : orderDetailPaymentList)
					{
//						this.orderPaymentDetail = orderPaymentDetail;
						logger.severe(orderPaymentDetail.toString());
						paymentMethod = orderPaymentDetail.getPaymentMethod();
						if (isAllowToPrecaptureTransaction(httpRequest, em, paymentMethod, 0, orderPaymentDetail, true))
						{
							Invoice invoice = new Invoice();
							
							if (orderPaymentDetail.getCreditcardTipAmt() != null && orderPaymentDetail.getCreditcardTipAmt() != new BigDecimal(0))
							{
								invoice.setTipAmount("" + orderPaymentDetail.getCreditcardTipAmt());
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

							}
							else
							{
								invoice.setTotalAmount(orderPaymentDetail.getAmountPaid().doubleValue());
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
							this.invoice=invoice;
							ProcessCreditCardRequest(httpRequest, em,orderHeader,orderPaymentDetail);
						}
					}
				}
			}

		}
	
	}

	public SupportedCardType getSupportedCardType()
	{
		return supportedCardType;
	}

	public void setSupportedCardType(SupportedCardType supportedCardType)
	{
		this.supportedCardType = supportedCardType;
	}

	public CreditCard getCreditCard()
	{
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard)
	{
		this.creditCard = creditCard;
	}

	

	public Invoice getInvoice()
	{
		return invoice;
	}

	public void setInvoice(Invoice invoice)
	{
		this.invoice = invoice;
	}

	public MerchentAccount getMerchentAccount()
	{
		return merchentAccount;
	}

	public void setMerchentAccount(MerchentAccount merchentAccount)
	{
		this.merchentAccount = merchentAccount;
	}
	private boolean isAllowToPrecaptureTransaction(HttpServletRequest httpRequest, EntityManager em, PaymentMethod paymentMethod, int locationGatewayId, OrderPaymentDetail orderPaymentDetail,
			boolean isSourceSpecific)
	{
		// here transaction status CC_Auth and CC_TipSaved belongs to credit
		// card and Manual Credit cardEntry PaymentMethod Type only allowed over
		// here
		boolean isBrainteePayment = false;
		PaymentMethodType paymentMethodType = (PaymentMethodType) new CommonMethods().getObjectById("PaymentMethodType", em,PaymentMethodType.class, paymentMethod.getPaymentMethodTypeId());
		if(orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId() != 0){
			OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType = em.find(OrderSourceToPaymentgatewayType.class,orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId());
			 if( orderSourceToPaymentgatewayType.getPaymentgatewayTypeId()!= 0){
				 PaymentGatewayType gatewayType = em.find(PaymentGatewayType.class, orderSourceToPaymentgatewayType.getPaymentgatewayTypeId()) ;
				 if("Braintree".equals(gatewayType.getName())){
					 isBrainteePayment = true;
				 }
			 }
				
		}
		
		if(orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId() != 0){
			OrderSourceGroupToPaymentgatewayType sourceGroupToPaymentgatewayType = em.find(OrderSourceGroupToPaymentgatewayType.class,orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId());
			PaymentGatewayType gatewayType = em.find(PaymentGatewayType.class, sourceGroupToPaymentgatewayType.getPaymentgatewayTypeId());
			 if("Braintree".equals(gatewayType.getName())){
				 isBrainteePayment = true;
			 }
		}
		
		// && orderPaymentDetail.getPayementGatewayId() == locationGatewayId
		if (orderPaymentDetail != null

		&& orderPaymentDetail.getTransactionStatus() != null
				&& (orderPaymentDetail.getTransactionStatus().getName().equals("CC Auth") || orderPaymentDetail.getTransactionStatus().getName().equals("Tip Saved"))
				&& orderPaymentDetail.getPnRef() != null && orderPaymentDetail.getPnRef().length() > 0 && paymentMethod != null
				&& (paymentMethodType.getName().equals("Credit Card") || paymentMethodType.getName().equals("Manual CC Entry"))
				&& isBrainteePayment)
		{

			return true;

		}
		return false;
	}

	@Override
	public void responseFromService(HttpServletRequest httpRequest,
			EntityManager em, int serviceId, int methodId, Object response)
			throws JsonGenerationException, JsonMappingException, IOException {
		
		
	}

}
