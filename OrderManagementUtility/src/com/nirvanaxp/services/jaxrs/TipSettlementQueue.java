package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.jaxrs.packets.ItemPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.util.ItemsServiceBean;
import com.nirvanaxp.types.entities.FutureUpdate;
import com.nirvanaxp.types.entities.FutureUpdate_;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

public class TipSettlementQueue implements Runnable
{

	private String locationId;
	private String sessionId;
	private String date;
	private BatchDetail batchDetail;
	private String updatedBy;
	private  List<OrderHeader>  headers;
	private HttpServletRequest httpRequest;

	private NirvanaLogger logger = new NirvanaLogger(TipSettlementQueue.class.getName());

	@Override
	public void run()
	{
		try
		{
			tipSettlementCalculation(httpRequest, locationId, sessionId, updatedBy, headers, batchDetail);
		}
		catch (IOException e)
		{
			logger.severe(e, "Error while saving transaction for capture all: ", e.getMessage());
		}
	}

	public TipSettlementQueue(HttpServletRequest httpRequest, String locationId, String sessionId, String updatedBy, List<OrderHeader> headers,BatchDetail batchDetail)
	{
		this.locationId = locationId;
		this.httpRequest = httpRequest;
		this.sessionId = sessionId;
		this.updatedBy = updatedBy;
		this.headers=headers;
		this.batchDetail=batchDetail;
	}

	private void tipSettlementCalculation(HttpServletRequest httpRequest, String locationId, String sessionId,String updatedBy, List<OrderHeader> headers,BatchDetail batchDetail) throws IOException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{ 
			String schemaName = LocalSchemaEntityManager.getInstance().getSchemaNameUsingWithoutSessionCheck(httpRequest, sessionId);
			em =  LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
			tx = em.getTransaction();
			tx.begin();
			LocationSetting locationSetting =null;
			try {
				locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			} catch (Exception e) {
				e.printStackTrace();
				logger.severe(httpRequest, "no location setting found for locationId: " + locationId);
			}
			if (locationSetting.getIsAutoTipSettlement() == 1) {
				new OrderManagementServiceBean().tipPoolingCalculation(httpRequest, em, updatedBy, locationId,  batchDetail, headers);
			}
			tx.commit();

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} 
		catch (Exception e)
		{
			logger.severe(e, "Error while saving transaction for capture all: ", e.getMessage());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private List<FutureUpdate> getAllFutureUpdateByLocationIdAndDate(EntityManager em, int businessId, String currentDate)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<FutureUpdate> criteria = builder.createQuery(FutureUpdate.class);
		Root<FutureUpdate> r = criteria.from(FutureUpdate.class);
		TypedQuery<FutureUpdate> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(FutureUpdate_.locationId), businessId),
						(builder.lessThanOrEqualTo(r.get(FutureUpdate_.date), currentDate)),
								(builder.notEqual(r.get(FutureUpdate_.status), "D")),(builder.notEqual(r.get(FutureUpdate_.status), "R"))));
		return query.getResultList();
	}

	private void sendPacketForBroadcast(String operation, PostPacket postPacket)
	{

		operation = ServiceOperationsUtility.getOperationName(operation);
		MessageSender messageSender = new MessageSender();

		messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.ItemsService.name(), operation, null, postPacket.getMerchantId(), postPacket.getLocationId(),
				postPacket.getEchoString(), postPacket.getSchemaName());

	}
}
