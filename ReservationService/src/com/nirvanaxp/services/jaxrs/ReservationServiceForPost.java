/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.data.ReservationWithUser;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.ReservationAndOrderPacket;
import com.nirvanaxp.services.jaxrs.packets.ReservationPacket;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.reservation.ReservationsSlot;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus;
import com.nirvanaxp.types.entities.sms.SMSTemplate;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entity.snssms.SmsConfig;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

 
/**
 * @author nirvanaxp
 *
 */
public class ReservationServiceForPost
{

	/**  */
	private ReservationServiceBean bean = new ReservationServiceBean();
	
	/**  */
	private static final NirvanaLogger logger = new NirvanaLogger(ReservationServiceForPost.class.getName());

	/**
	 * 
	 *
	 * @param httpRequest 
	 * @param em 
	 * @param holdReservationSlotPacket 
	 * @param sessionId 
	 * @return 
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public int holdReservationSlotForClient(HttpServletRequest httpRequest, EntityManager em, HoldReservationSlotPacket holdReservationSlotPacket, String sessionId) throws NumberFormatException, Exception
	{

		HoldReservtionSlotResponse holdReservtionSlotResponse = bean.holdReservationSlotForClient(httpRequest, em, holdReservationSlotPacket.getReservationSlot().getId(), sessionId,
				holdReservationSlotPacket.getReservationSlot().getUpdatedBy(), holdReservationSlotPacket.getSchemaName(),holdReservationSlotPacket.getLocationId());
		ReservationsSlot reservationsSlot = holdReservtionSlotResponse.getReservationsSlot();
		if (reservationsSlot != null)
		{
			ReservationsSlot reservationslotForpush = new ReservationsSlot();
			reservationslotForpush.setId(reservationsSlot.getId());
			reservationslotForpush.setStatus(reservationsSlot.getStatus());
			holdReservationSlotPacket.setReservationSlot(reservationslotForpush);
			// false will broadcast the entire packet
			sendPacketForBroadcast(httpRequest, holdReservationSlotPacket, POSNServiceOperations.ReservationService_slotUpdate.name(), false);
			return holdReservtionSlotResponse.getReservationHoldingClientId();
		}

		return 0;
	}

	/**
	 * 
	 *
	 * @param httpRequest 
	 * @param em 
	 * @param reservationPacket 
	 * @param sessionId 
	 * @param isOnline 
	 * @return 
	 * @throws Exception 
	 */
	public Reservation addReservation(HttpServletRequest httpRequest, EntityManager em, ReservationPacket reservationPacket, String sessionId, boolean isOnline) throws Exception
	{

		reservationPacket.getReservation().setSessionKey(reservationPacket.getIdOfSessionUsedByPacket());

		ReservationWithUser result = bean.add(httpRequest, em, reservationPacket.getReservation(), reservationPacket.getGlobalUserId(), sessionId,
				reservationPacket.getIdOfReservationHoldingClientObj(), reservationPacket.getWebSiteUrl(), reservationPacket, isOnline);

		if (result.getUser() != null)
		{
			reservationPacket.setUser(result.getUser());
		}
		reservationPacket.setReservation(result.getReservation());

		sendPacketForBroadcast(httpRequest, reservationPacket, POSNServiceOperations.ReservationService_add.name(), true);

		// TODO Ankur - does the client need to know about new user?
		// Apoorv :- yes they display user details by handling push in
		// reservation screen
		return result.getReservation();
	}

	/**
	 * 
	 *
	 * @param httpRequest 
	 * @param em 
	 * @param reservationPacket 
	 * @param isReservationStatusUpdate 
	 * @param sessionId 
	 * @return 
	 * @throws Exception 
	 */
	public Reservation updateReservation(HttpServletRequest httpRequest, EntityManager em, ReservationPacket reservationPacket, boolean isReservationStatusUpdate, String sessionId)
			throws Exception
	{
		if (reservationPacket.getReservation() == null)
		{
			// throw error
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_BAD_INPUT_EXCEPTION, MessageConstants.ERROR_MESSAGE_BAD_INPUT_EXCEPTION,
					"Update Reservation call did not send data for existing reservation"));
		}

		reservationPacket.getReservation().setSessionKey(reservationPacket.getIdOfSessionUsedByPacket());

		int needToSendSNS=reservationPacket.getLocalServerURL();
		ReservationWithUser result = bean.update(httpRequest, em, reservationPacket.getReservation(), isReservationStatusUpdate, reservationPacket.getReservationSlotId(),
				reservationPacket.getIdOfReservationHoldingClientObj(), reservationPacket.getWebSiteUrl(), reservationPacket.getGlobalUserId(), sessionId,reservationPacket);

		// TODO Ankur - why is the new user not broadcasted?
		String operationName = "";
		if (isReservationStatusUpdate)
		{
			// if update reservation status
			ReservationsStatus rStatus = new ReservationsStatus();
			rStatus.setId(reservationPacket.getReservation().getReservationsStatusId());
			result.getReservation().setReservationsStatus(rStatus);
			operationName = POSNServiceOperations.ReservationService_updateReservationStatus.name();
		}
		else
		{
			operationName = POSNServiceOperations.ReservationService_update.name();
		}

		reservationPacket.setReservation(result.getReservation());
		if(needToSendSNS!=1){
		sendSNSByNumber(em,result.getReservation());
		}
		sendPacketForBroadcast(httpRequest, reservationPacket, operationName, false);

		//sendSNSByNumber(em,reservationPacket.getReservation());
		
		return result.getReservation();

	}

	/**
	 * 
	 *
	 * @param httpRequest 
	 * @param em 
	 * @param sessionId 
	 * @param reservationAndOrderPacket 
	 * @param isReservationStatusUpdate 
	 * @return 
	 * @throws NirvanaXPException 
	 * @throws JsonMappingException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	public ReservationAndOrderPacket addWalkInOrderAndReservation(HttpServletRequest httpRequest, EntityManager em, String sessionId, ReservationAndOrderPacket reservationAndOrderPacket,
			boolean isReservationStatusUpdate) throws NirvanaXPException, JsonMappingException, IOException, Exception
	{
		EntityTransaction tx =null;
		tx = em.getTransaction();
		
		ReservationPacket reservationPacket = reservationAndOrderPacket.getReservationPacket();
		OrderPacket orderPacket = reservationAndOrderPacket.getOrderPacket();

		reservationPacket.getReservation().setSessionKey(reservationPacket.getIdOfSessionUsedByPacket());
		ReservationWithUser result =null;
		try
		{
			tx.begin();
			result = bean.add(httpRequest, em, reservationPacket.getReservation(), reservationPacket.getGlobalUserId(), sessionId,
					reservationPacket.getIdOfReservationHoldingClientObj(), reservationPacket.getWebSiteUrl(), reservationPacket, false);
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
		if (result.getUser() != null)
		{
			reservationPacket.setUser(result.getUser());
		}
		reservationPacket.setReservation(result.getReservation());
		
		orderPacket.getOrderHeader().setReservationsId(result.getReservation().getId());

		OrderHeader header;
		try
		{
			tx.begin();
			header = new OrderServiceForPost().insertOrder(httpRequest, sessionId, em, orderPacket, false);
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
		
		orderPacket.setOrderHeader(header);
		reservationAndOrderPacket.setReservationPacket(reservationPacket);
		reservationAndOrderPacket.setOrderPacket(orderPacket);
		 
		reservationAndOrderPacket.setReservationUpdated(result.getUpdatedReservation());
		sendPacketForBroadcast(httpRequest, reservationPacket, POSNServiceOperations.ReservationService_add.name(), true);
		return reservationAndOrderPacket;

	}

	/**
	 * 
	 *
	 * @param httpRequest 
	 * @param em 
	 * @param reservationPacket 
	 * @return 
	 */
	public Reservation deleteReservation(HttpServletRequest httpRequest, EntityManager em, ReservationPacket reservationPacket)
	{
		Reservation result = bean.delete(httpRequest, em, reservationPacket.getReservation());
		reservationPacket.setReservation(result);
		sendPacketForBroadcast(httpRequest, reservationPacket, POSNServiceOperations.ReservationService_delete.name(), false);
		return result;
	}

	/**
	 * 
	 *
	 * @param httpRequest 
	 * @param postPacket 
	 * @param operation 
	 * @param isAddOperation 
	 */
	private void sendPacketForBroadcast(HttpServletRequest httpRequest, PostPacket postPacket, String operation, boolean isAddOperation)
	{

		try
		{
			// so that these values not get broadcasted to other clients
			postPacket.setIdOfSessionUsedByPacket(0);
			postPacket.setSessionId(null);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
			String internalJson = null;
			if (isAddOperation)
			{
				internalJson = objectMapper.writeValueAsString(postPacket);
			}
			else
			{
				if (postPacket instanceof ReservationPacket)
				{
					internalJson = objectMapper.writeValueAsString(((ReservationPacket) postPacket).getReservation());
				}
				else if (postPacket instanceof HoldReservationSlotPacket)
				{
					internalJson = objectMapper.writeValueAsString(((HoldReservationSlotPacket) postPacket).getReservationSlot());
				}

			}

			operation = ServiceOperationsUtility.getOperationName(operation);
			MessageSender messageSender = new MessageSender();

			messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.ReservationService.name(), operation, internalJson, postPacket.getMerchantId(), postPacket.getLocationId(),
					postPacket.getEchoString(), postPacket.getSchemaName());
		}
		catch (IOException e)
		{
			// could not send push
			logger.severe(httpRequest, e);
		}

	}
	
	public void sendSNSByNumber(EntityManager em, Reservation reservation) {
		
		if(reservation.getUsersId()!=null)
		{
			User user = (User) new CommonMethods().getObjectById("User", em,User.class,reservation.getUsersId() );
			//em.getTransaction().begin();
			PublishResult result = null;
			try {
				
			        String message = getMessageBodyToSend(em, user,reservation);
			        
			        if(message != null)
			        {
			        	 //AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();
			        	AmazonSNS snsClient =getSNS();
					        String phoneNumber = user.getPhone();
					        //snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
							Map<String, MessageAttributeValue> smsAttributes = 
					                new java.util.HashMap<String, MessageAttributeValue>();
					        
					        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
				            .withStringValue("Transactional") //Sets the type to promotional.
				            .withDataType("String"));
					        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
				            .withStringValue("mySenderID") //The sender ID shown on the device.
				            .withDataType("String"));
					        
					        result = snsClient.publish(new PublishRequest()
					                            .withMessage(message)
					                            .withPhoneNumber(phoneNumber)
					                            .withMessageAttributes(smsAttributes));
					        System.out.println(result); // Prints the message ID.
			        }
			        
			       
			        

			} catch (Exception e) {
				
				logger.severe(e);
			} 
			
			//em.getTransaction().commit();
		}
		
		
	}
	
	private static AmazonSNS getSNS() throws IOException {
		String awsAccessKey = System.getProperty("AWS_ACCESS_KEY_ID"); // "YOUR_AWS_ACCESS_KEY";
		String awsSecretKey = System.getProperty("AWS_SECRET_KEY"); // "YOUR_AWS_SECRET_KEY";

		if (awsAccessKey == null)
			awsAccessKey = "AKIAJ4FQSALKK4XTOZOQ";
		if (awsSecretKey == null)
			awsSecretKey = "qLOsioPoWtJWp+EldQMAN2FUXkQiiMv5P0PqpEET";

		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey,
				awsSecretKey);
		AmazonSNS sns = new AmazonSNSClient(credentials);

		sns.setEndpoint("https://sns.us-west-2.amazonaws.com");
		return sns;
	}
	
	
	@SuppressWarnings("unused")
	private String getMessageBodyToSend(EntityManager em, User user, Reservation reservation)
	{
		
		String retrnMsg = null;
		
		ReservationsStatus reservationsStatus = (ReservationsStatus) new CommonMethods().getObjectById("ReservationsStatus", em,ReservationsStatus.class, reservation.getReservationsStatusId());
		
		if(reservationsStatus.getIsSendSms() == 1)
		{
			
			String queryStringT = "select ci from SMSTemplate ci where  ci.status not in ('I','D') "
					+ " and ci.id =  " + reservationsStatus.getTemplateId();
			TypedQuery<SMSTemplate> queryT = em.createQuery(queryStringT, SMSTemplate.class);
			SMSTemplate snsSmsTemplate =  queryT.getSingleResult();
		
			String queryStringConfig = "select ci from SmsConfig ci where  ci.status !='D'and "
					+ "ci.gatewayName  = 'AWS SMS' ";
			TypedQuery<SmsConfig> query = em.createQuery(queryStringConfig, SmsConfig.class);
			SmsConfig smsConfig =  query.getSingleResult();
	         
			if(snsSmsTemplate == null)
			{
				logger.severe("Sns Sms Template Not Configure");
				return null;
				
			}else if(smsConfig == null)
			 {
				 logger.severe("Sns Sms Config Not Configure");
				 return null; 
				 
			 }else 
			 {
				
				retrnMsg = snsSmsTemplate.getTemplateText() ;
				
				if(reservationsStatus.getLocationsId()!= null)
				{
					Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, reservationsStatus.getLocationsId());
					if(location != null)
					{
						retrnMsg = retrnMsg.replace("<BusinessName>", location.getName());	
					}	
				}
				
				 
				
				
		        new InsertIntoHistory().insertSMSIntoHistory(em, user, snsSmsTemplate, retrnMsg, user.getPhone(), smsConfig, null, reservation.getLocationId());
		        
			 }
		}
		
		
		
        return retrnMsg;
        
	}

}
