package com.nirvanaxp.storeForward;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.jaxrs.packets.BatchDetailPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.time.Timezone;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

// TODO: Auto-generated Javadoc
/**
 * The Class PaymentBatchManager.
 */
public final class PaymentBatchManager
{

	/** The request. */
	@Context
	HttpServletRequest request;

	/** The index. */
	private static BigInteger index = BigInteger.ZERO;

	/** The business id variable. */
	private static int businessIdVariable = 0;

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(PaymentBatchManager.class.getName());

	/**
	 * The Class SingletonHolder.
	 */
	private static class SingletonHolder
	{

		/** The Constant INSTANCE. */
		private static final PaymentBatchManager INSTANCE = new PaymentBatchManager();
	}

	/** The Constant CURRENT_BATCH_ID_MAP. */
	// this is the local cache to avoid going to database every time
	private static final Map<String, BatchDetail> CURRENT_BATCH_ID_MAP = new HashMap<String, BatchDetail>();

	/**
	 * Instantiates a new payment batch manager.
	 */
	private PaymentBatchManager()
	{

	}

	/**
	 * Gets the single instance of PaymentBatchManager.
	 *
	 * @return single instance of PaymentBatchManager
	 */
	public static PaymentBatchManager getInstance()
	{
		logger.finest("returning PaymentBatchManager instance: " + SingletonHolder.INSTANCE);
		return SingletonHolder.INSTANCE;
	}

	/**
	 * Creates the new batch details.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param rootLocationId
	 *            the root location id
	 * @param postPacket
	 *            the post packet
	 * @return the batch detail
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public BatchDetail createNewBatches(HttpServletRequest httpRequest, EntityManager em, String rootLocationId, BatchDetailPacket postPacket,String updatedBy)
			throws IOException, InvalidSessionException, NirvanaXPException
	{

		BatchDetail fromDatabase = null;

		// checking in database whether batch exist or not

		String queryString = "select b from BatchDetail b where b.locationId=? and status='A' order by b.id desc  ";
		List<BatchDetail> resultSet = null;
		try
		{
			TypedQuery<BatchDetail> query = em.createQuery(queryString, BatchDetail.class).setParameter(1, rootLocationId);
			resultSet = query.getResultList();
		}
		catch (Exception e)
		{
			// todo shlok need to handle
			// proper exception
			logger.severe(request, "Could not find order with batch : " + e);
		}
		if (resultSet != null && resultSet.size() > 0)
		{
			// we should have only on active batch for 1 location
			throw new NirvanaXPException(
					new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_ACTIVE_BATCH_ALREADY_PRESENT, MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BATCH_ALREADY_PRESENT, null));

		}
		else
		{
			fromDatabase = createNewBatchForStoreForward(em, rootLocationId, updatedBy, postPacket,httpRequest,0);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addBatchDetail.name(), postPacket);
		}
		
		return fromDatabase;
	}

	public BatchDetail createNewBatchDetails(HttpServletRequest httpRequest, EntityManager em, String rootLocationId, PostPacket postPacket)
			throws IOException, InvalidSessionException, NirvanaXPException
	{

		BatchDetail fromDatabase = null;

		// checking in database whether batch exist or not

		String queryString = "select b from BatchDetail b where b.locationId=? and status='A' order by b.id desc  ";
		List<BatchDetail> resultSet = null;
		try
		{
			TypedQuery<BatchDetail> query = em.createQuery(queryString, BatchDetail.class).setParameter(1, rootLocationId);
			resultSet = query.getResultList();
		}
		catch (Exception e)
		{
			// todo shlok need to handle
			// proper exception
			logger.severe(request, "Could not find order with batch : " + e);
		}
		if (resultSet != null && resultSet.size() > 0)
		{
			// we should have only on active batch for 1 location
			throw new NirvanaXPException(
					new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_ACTIVE_BATCH_ALREADY_PRESENT, MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BATCH_ALREADY_PRESENT, null));

		}
		else
		{
			fromDatabase = createNewBatch(em, rootLocationId, "21", postPacket,httpRequest,null);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addBatchDetail.name(), postPacket);
		}
		
		return fromDatabase;
	}

	/**
	 * This returns the current batch id for the session. It finds out the
	 * location for the session and then finds the batch for that location. The
	 * session must be current and active.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            - the EntityManager
	 * @param rootLocationId
	 *            the root location id
	 * @param isCreateBatch
	 *            the is create batch
	 * @return BatchDetail
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 */
	public String getCurrentBatchIdBySession(HttpServletRequest httpRequest, EntityManager em, String rootLocationId, boolean isCreateBatch, PostPacket packet,String updatedBy) throws IOException, InvalidSessionException
	{

		// to do need to handle null pointer in this condition
		BatchDetail fromDatabase = getCurrentBatchBySession(httpRequest, em, rootLocationId, isCreateBatch, packet,updatedBy);

		return fromDatabase.getId();
	}

	/**
	 * Gets the current batch by session.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param rootLocationId
	 *            the root location id
	 * @param isCreateBatch
	 *            the is create batch
	 * @return the current batch by session
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 */
	public synchronized BatchDetail getCurrentBatchBySession(HttpServletRequest httpRequest, EntityManager em, String rootLocationId, boolean isCreateBatch, PostPacket packet,String updatedBy)
			throws IOException, InvalidSessionException
	{

		BatchDetail fromDatabase = null;

		// checking in database whether batch exist or not

		String queryString = "select b from BatchDetail b where b.locationId=? and status='A'  ";
		try
		{
			TypedQuery<BatchDetail> query = em.createQuery(queryString, BatchDetail.class).setParameter(1, rootLocationId);
			fromDatabase = query.getSingleResult();

		}
		catch (Exception e)
		{
			// todo shlok need to handle
			// proper exception
			logger.severe(request, "Could not find order with batch : " + e);
		}
		if (fromDatabase != null)
		{
			return fromDatabase;
		}
		else
		{
			if (isCreateBatch)
			{

				fromDatabase = createNewBatch(em, rootLocationId,updatedBy, packet,httpRequest,null);
			}
		}
		return fromDatabase;
	}

	/**
	 * This method is to close current active batch and start the next one. This
	 * MUST be called after the payment batch has been settled. Only the batch
	 * settlement process should call this method.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param sessionId
	 *            the session id
	 * @param rootLocationId
	 *            the root location id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 */
	public synchronized void closeCurrentActiveBatchAndInitNext(HttpServletRequest httpRequest, String sessionId, String rootLocationId) throws IOException, InvalidSessionException
	{
		try
		{
			// connect to database and mark the current batch as closed
			UserSession session = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId);

			// schema name for this session id
			String schemaName = session.getSchema_name();
			// check if local cache has the current batch id
			String key = schemaName + "~" + rootLocationId;
			CURRENT_BATCH_ID_MAP.put(key, null);
			// TODO Ankur - why a new batch was not started?

			// then start a new batch, because there is always a current batch

		}
		finally
		{
			// todo shlok need to handle
			// blank finally
		}
	}

	/**
	 * Creates the new batch.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param updatedBy
	 *            the updated by
	 * @return the batch detail
	 */
	private BatchDetail createNewBatch(EntityManager em, String locationId, String updatedBy, PostPacket packet,HttpServletRequest request,String batchId)
	{
		BatchDetail batchDetailNew = null;
		// Integer resultList = null;
		try
		{
 
			batchDetailNew = new BatchDetail();
			if(batchId==null){
				batchDetailNew.setId(locationId+"-"+ new StoreForwardUtility().getAndUpdateCountOfTableIndex(em, locationId, "batch_detail", true).intValue());
				batchDetailNew.setDayOfYear(new StoreForwardUtility().getDayOfYear(locationId, em));
					
			}else{
				batchDetailNew.setId(batchId);
				
			}
			batchDetailNew.setIsBatchSettledError(0);
			batchDetailNew.setIsPrecapturedError(0);
			batchDetailNew.setLocationId(locationId);
			batchDetailNew.setStartTime(new TimezoneTime().getGMTTimeInMilis());
			batchDetailNew.setStatus("A");
			batchDetailNew.setTipSettle(false);
			batchDetailNew.setUpdatedBy(updatedBy);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Location> cl = builder.createQuery(Location.class);
			Root<Location> l = cl.from(Location.class);
			TypedQuery<Location> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Location_.id), locationId))));

			Location locations = query.getSingleResult();
			Timezone t = em.find(Timezone.class, locations.getTimezoneId());
			String timezone = t.getDisplayName().substring(7).trim();
			batchDetailNew.setLocalTime(timezone);
			batchDetailNew = em.merge(batchDetailNew);
			 
			try {
				BatchDetailPacket newBDP =createPacket(packet);
				newBDP.setBatchDetail(batchDetailNew);
				String json = new StoreForwardUtility().returnJsonPacket(newBDP, "BatchDetailPacket",request);
				new StoreForwardUtility().callSynchPacketsWithServerForGeneric(json, request, packet.getLocationId(), Integer.parseInt(packet.getMerchantId()),"/OrderManagementServiceV6/createBatchForSynch");
			} catch (Exception e) {
				logger.severe(e);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// todo shlok need to handle
			// proper exception
			logger.severe(e);
		}
		 
		sendPacketForBroadcast(POSNServiceOperations.LookupService_addBatchDetail.name(), packet);

		return batchDetailNew;
	}
	public BatchDetailPacket createPacket(PostPacket packet){
		BatchDetailPacket newBDP = new BatchDetailPacket();
		newBDP.setClientId(packet.getClientId());
		newBDP.setEchoString(packet.getEchoString());
		newBDP.setIdOfSessionUsedByPacket(packet.getIdOfSessionUsedByPacket());
		newBDP.setLocationId(packet.getLocationId());
		newBDP.setMerchantId(packet.getMerchantId());
		newBDP.setSchemaName(packet.getSchemaName());
		return newBDP;
		
	}
	public BatchDetail createNewBatch(EntityManager em, BatchDetail batchDetailNew)
	{
		try
		{
			batchDetailNew = em.merge(batchDetailNew);
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		return batchDetailNew;
	}
	
	private BatchDetail createNewBatchForStoreForward(EntityManager em, String locationId, String updatedBy, BatchDetailPacket packet,HttpServletRequest request,int batchId)
	{
		BatchDetail batchDetailNew = null;
		// Integer resultList = null;
		try
		{
//			resultList = (Integer) em.createNativeQuery("call addBatch(?,?,?)").setParameter(1, locationId).setParameter(2, updatedBy)
//					.setParameter(3, new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em)).getSingleResult();
			batchDetailNew = packet.getBatchDetail();
			if(batchDetailNew.getId()==null){
				batchDetailNew.setId(locationId+"-"+ new StoreForwardUtility().getIndexFromTableIndex(em, locationId, request, "batch_detail"));
				batchDetailNew.setIsBatchSettledError(0);
				batchDetailNew.setIsPrecapturedError(0);
				batchDetailNew.setLocationId(locationId);
				batchDetailNew.setStartTime(new TimezoneTime().getGMTTimeInMilis());
				batchDetailNew.setStatus("A");
				batchDetailNew.setTipSettle(false);
				batchDetailNew.setDayOfYear(new StoreForwardUtility().getDayOfYear(locationId, em));
				batchDetailNew.setUpdatedBy(updatedBy);
			} 
			
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Location> cl = builder.createQuery(Location.class);
			Root<Location> l = cl.from(Location.class);
			TypedQuery<Location> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Location_.id), locationId))));

			Location locations = query.getSingleResult();
			Timezone t = em.find(Timezone.class, locations.getTimezoneId());
			String timezone = t.getDisplayName().substring(7).trim();
			batchDetailNew.setLocalTime(timezone);
			
			
			batchDetailNew = em.merge(batchDetailNew);
			
			try {
				BatchDetailPacket newBDP =createPacket(packet);
				newBDP.setBatchDetail(batchDetailNew);
				String json = new StoreForwardUtility().returnJsonPacket(newBDP, "BatchDetailPacket",request);
				new StoreForwardUtility().callSynchPacketsWithServerForGeneric(json, request, packet.getLocationId(), Integer.parseInt(packet.getMerchantId()),"/OrderManagementServiceV6/createBatchForSynch");
			} catch (Exception e) {
				logger.severe(e);
			}
			
 
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// todo shlok need to handle
			// proper exception
			logger.severe(e);
		}
		 
		sendPacketForBroadcast(POSNServiceOperations.LookupService_addBatchDetail.name(), packet);

		return batchDetailNew;
	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param operation
	 *            the operation
	 * @param postPacket
	 *            the post packet
	 */
	private void sendPacketForBroadcast(String operation, PostPacket postPacket)
	{

		MessageSender messageSender = new MessageSender();
		operation = ServiceOperationsUtility.getOperationName(operation);
		// to broadcast packet to all client
		String clientId = "jwebsocket";
		messageSender.sendMessage(request, clientId, POSNServices.LookupService.name(), operation, null, postPacket.getMerchantId(), postPacket.getLocationId(), postPacket.getEchoString(),
				postPacket.getSessionId());

	}

 

}
