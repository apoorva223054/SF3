/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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

import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.PrinterUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.AbstractNirvanaService;
import com.nirvanaxp.services.jaxrs.INirvanaService;
import com.nirvanaxp.services.jaxrs.OrderManagementServiceBean;
import com.nirvanaxp.services.jaxrs.packets.BraintreePaymentPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType;
import com.nirvanaxp.types.entities.payment.PaymentGatewayType;
import com.nirvanaxp.wallet.jio.JioMoneyInputPacket;


@Path("/PaymentService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class PaymentService extends AbstractNirvanaService
{

	@Context
	HttpServletRequest httpRequest;

	private static final NirvanaLogger logger = new NirvanaLogger(PaymentService.class.getName());

	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}



	/**
	 * In case of braintree payment processing, the client must first get a
	 * token to talk to payment gateway to generate a nonce. That nonce is then
	 * used by the server to talk to gateway to do actual payment authorization.
	 * 
	 * @param sessionId
	 * @param globalUserId
	 * @return
	 * @throws InvalidSessionException
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws NirvanaXPException
	 */
	@GET
	@Path("/getBraintreeToken/{user_id}/{orderSourceGroupToPaymentGatewayTypeId}/{orderSourceToPaymentGatewayTypeId}")
	public String getBraintreePaymentToken(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("user_id") String globalUserId,
			@PathParam("orderSourceGroupToPaymentGatewayTypeId") int orderSourceGroupToPaymentGatewayTypeId, @PathParam("orderSourceToPaymentGatewayTypeId") int orderSourceToPaymentGatewayTypeId)
			throws FileNotFoundException, IOException,  InvalidSessionException, NirvanaXPException
	{

		EntityManager em = null;
		EntityManager globalEM = null;
		try
		{
			// is this a valid session
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new BraintreePayment().getBraintreePaymentToken(httpRequest, em, globalEM, globalUserId, orderSourceGroupToPaymentGatewayTypeId, orderSourceToPaymentGatewayTypeId));
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/authorizeOrderForBrainTree/{nounce}/{orderSourceGroupToPaymentGatewayTypeId}/{orderSourceToPaymentGatewayTypeId}")
	public String authorizeOrderForBrainTree(OrderPacket orderPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("nounce") String nounce,
			@PathParam("orderSourceGroupToPaymentGatewayTypeId") int orderSourceGroupToPaymentGatewayTypeId, @PathParam("orderSourceToPaymentGatewayTypeId") int orderSourceToPaymentGatewayTypeId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
			// is this a valid session
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new BraintreePayment().authorizeOrderForBrainTree(httpRequest, em, nounce, orderPacket,
					orderSourceGroupToPaymentGatewayTypeId, orderSourceToPaymentGatewayTypeId,auth_token));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@POST
	@Path("/authOrVoidOrderPaymentForBrainTree/{isVoid}")
	public String authOrVoidOrderPaymentForBrainTree(BraintreePaymentPacket braintreePaymentPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,@PathParam("isVoid") boolean isVoid)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			// is this a valid session
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			OrderPaymentDetail orderPaymentDetail= new BraintreePaymentService().authOrVoidOrderPaymentForBrainTree(httpRequest, em,braintreePaymentPacket,isVoid);
			braintreePaymentPacket.setOrderPaymentDetail(orderPaymentDetail);
			return new JSONUtility(httpRequest).convertToJsonString(braintreePaymentPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/precaptureOrderForBrainTree/{updatedBy}/{fromDate}/{toDate}/{business_id}")
	public String precaptureOrderForBrainTree(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("updatedBy") int updatedBy, @PathParam("fromDate") String fromDate,
			@PathParam("toDate") String toDate, @PathParam("business_id") int businessId) throws FileNotFoundException, IOException,  InvalidSessionException, NirvanaXPException
	{
		EntityManager em = null;
		try
		{
			// is this a valid session
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new BraintreePayment().precaptureOrderForBrainTree(httpRequest, em, updatedBy, fromDate, toDate, businessId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * In case of braintree payment processing, the client must first get a
	 * token to talk to payment gateway to generate a nonce. That nonce is then
	 * used by the server to talk to gateway to do actual payment authorization.
	 * 
	 * @param referenceNumber
	 * @param globalUserId
	 * @param orderSourceGroupToPaymentGatewayTypeId
	 * @param orderSourceToPaymentGatewayTypeId
	 * @return
	 * @throws Exception
	 * @throws IOException
	 * @throws DatabaseException
	 * @throws InvalidSessionException
	 * @throws NirvanaXPException
	 */
	@GET
	@Path("/getBraintreeTokenForClient/{user_id}/{orderSourceGroupToPaymentGatewayTypeId}/{orderSourceToPaymentGatewayTypeId}")
	public String getBraintreeTokenForClient( @PathParam("user_id") String globalUserId,
			@PathParam("orderSourceGroupToPaymentGatewayTypeId") int orderSourceGroupToPaymentGatewayTypeId, @PathParam("orderSourceToPaymentGatewayTypeId") int orderSourceToPaymentGatewayTypeId)
			throws Exception, IOException,  InvalidSessionException, NirvanaXPException
	{

		EntityManager em = null;
		EntityManager globalEM = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			
			boolean isProcess = false;
			if(orderSourceGroupToPaymentGatewayTypeId != 0)
			{
				OrderSourceGroupToPaymentgatewayType orderSG = em.find(OrderSourceGroupToPaymentgatewayType.class, orderSourceGroupToPaymentGatewayTypeId);
				
				if(orderSG.getStatus().equals("A"))
				{
					PaymentGatewayType gatewayType = em.find(PaymentGatewayType.class, orderSG.getPaymentgatewayTypeId());
					
					if(gatewayType.getName().equals("Braintree") && gatewayType.getStatus().equals("A"))
					{
						isProcess = true;
					}	
				}
				
				
			}
			
		
			if(orderSourceToPaymentGatewayTypeId != 0)
			{
				OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType = em.find(OrderSourceToPaymentgatewayType.class, orderSourceToPaymentGatewayTypeId);
				
				if(orderSourceToPaymentgatewayType.getStatus().equals("A"))
				{
					PaymentGatewayType gatewayType = em.find(PaymentGatewayType.class, orderSourceToPaymentgatewayType.getPaymentgatewayTypeId());
					
					if(gatewayType.getName().equals("Braintree") && gatewayType.getStatus().equals("A"))
					{
						isProcess = true;
					}
				}
				
				
			}

			
			
			if(isProcess)
			{
				globalEM = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
				return new JSONUtility(httpRequest).convertToJsonString(new BraintreePayment().getBraintreePaymentToken(httpRequest, em, globalEM, globalUserId, orderSourceGroupToPaymentGatewayTypeId, orderSourceToPaymentGatewayTypeId));	
			}else
			{
				return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_PAYMENT_GATEWAY_ASSINGED,
						MessageConstants.ERROR_MESSAGE_NO_PAYMENT_GATEWAY_ASSINGED, null)).toString());
			}
			
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/authorizeOrderForClient/{nounce}/{orderSourceGroupToPaymentGatewayTypeId}/{orderSourceToPaymentGatewayTypeId}")
	public String authorizeOrderForClient(OrderPacket orderPacket,  @PathParam("nounce") String nounce,
			@PathParam("orderSourceGroupToPaymentGatewayTypeId") int orderSourceGroupToPaymentGatewayTypeId, @PathParam("orderSourceToPaymentGatewayTypeId") int orderSourceToPaymentGatewayTypeId)
			throws Exception,NirvanaXPException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			tx =em.getTransaction();
			tx.begin();
			String returnValue = new BraintreePayment().authorizeOrderForBrainTree(httpRequest, em, nounce, orderPacket,
					orderSourceGroupToPaymentGatewayTypeId, orderSourceToPaymentGatewayTypeId,auth_token);
			OrderHeader header = new OrderManagementServiceBean().getOrderById(em, orderPacket.getOrderHeader().getId());
			if(header!=null && header.getOrderDetailItems()!=null && header.getOrderDetailItems().size()>0){
				new PrinterUtility().insertIntoPrintQueueForCustomer(httpRequest, em,header,orderPacket.getLocationId());
			}
			else
			{
				// TODO Ankur: what to do if there is no order by that id?
			}
			tx.commit();
			return returnValue;
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

	@GET
	@Path("/precaptureOrderForClient/{updatedBy}/{fromDate}/{toDate}/{business_id}")
	public String precaptureOrderForClient( @PathParam("updatedBy") int updatedBy, @PathParam("fromDate") String fromDate,
			@PathParam("toDate") String toDate, @PathParam("business_id") int businessId) throws FileNotFoundException, IOException,  Exception, NirvanaXPException
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			return new JSONUtility(httpRequest).convertToJsonString(new BraintreePayment().precaptureOrderForBrainTree(httpRequest, em, updatedBy, fromDate, toDate, businessId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/getJioMoneyCheckSum/{orderSourceGroupToPaymentGatewayTypeId}/{orderSourceToPaymentGatewayTypeId}/{locationId}")
	public String getJioMoneyCheckSum(JioMoneyInputPacket jioMoneyInputPacket,@PathParam("orderSourceGroupToPaymentGatewayTypeId") int orderSourceGroupToPaymentGatewayTypeId,
			@PathParam("orderSourceToPaymentGatewayTypeId") int orderSourceToPaymentGatewayTypeId,
			@PathParam("locationId") String locationId) throws IOException, InvalidSessionException{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			BraintreePayment braintreePayment = new BraintreePayment();
			jioMoneyInputPacket.setJioInputArgument(braintreePayment.setJioCredential(em, jioMoneyInputPacket.getJioInputArgument(), orderSourceGroupToPaymentGatewayTypeId, orderSourceToPaymentGatewayTypeId));
			return new BraintreePayment().generateCheckSum(em, jioMoneyInputPacket.getJioInputArgument());
		}finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		
	}
}
