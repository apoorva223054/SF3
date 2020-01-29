package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;

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
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

public class FutureUpdateQueue implements Runnable
{

	private String locationId;
	private String sessionId;
	private String date;
	private HttpServletRequest httpRequest;

	private NirvanaLogger logger = new NirvanaLogger(FutureUpdateQueue.class.getName());

	@Override
	public void run()
	{

		try
		{
			saveFutureUpdateByDate(httpRequest, locationId, sessionId, date);
		}
		catch (IOException e)
		{
			logger.severe(e, "Error while saving transaction for capture all: ", e.getMessage());
		}
	}

	public FutureUpdateQueue(HttpServletRequest httpRequest, String locationId, String sessionId, String date)
	{
		this.locationId = locationId;
		this.httpRequest = httpRequest;
		this.sessionId = sessionId;
		this.date = date;
	}

	private void saveFutureUpdateByDate(HttpServletRequest httpRequest, String locationId, String sessionId, String currentDate) throws IOException
	{

		EntityManager em = null;
		try
		{ 
			String schemaName = LocalSchemaEntityManager.getInstance().getSchemaNameUsingWithoutSessionCheck(httpRequest, sessionId);
			em =  LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);

			List<FutureUpdate> futureUpdateList = getAllFutureUpdateByLocationIdAndDate(em, locationId, currentDate);
			for (FutureUpdate futureUpdate : futureUpdateList)
			{

				EntityTransaction tx = null;
				try
				{
					ItemPacket itemPacket = new ObjectMapper().readValue(futureUpdate.getPacketString(), ItemPacket.class);

					em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(futureUpdate.getSchemaName());
				 
					if (futureUpdate.getOperationName().equals(POSNServiceOperations.ItemsService_update.name()))
					{
						
						tx = em.getTransaction();
						tx.begin();
						new ItemsServiceBean().updateMultipleLocationsItems(httpRequest, em, itemPacket.getItem(), itemPacket);
						tx.commit();
						String[] locationsId = itemPacket.getLocationsListId().split(",");
						for (String packetlocationId : locationsId)
						{
							if (packetlocationId != null && packetlocationId.length() > 0)
							{
								itemPacket.setLocationId(packetlocationId);
							}
							else
							{
								itemPacket.setLocationId(itemPacket.getItem().getLocationsId() + "");
							}

							sendPacketForBroadcast(POSNServiceOperations.ItemsService_update.name(), itemPacket);
							futureUpdate.setStatus("D");
							em.getTransaction().begin();
							em.merge(futureUpdate);
							em.getTransaction().commit();
						}
					}
					else if (futureUpdate.getOperationName().equals(POSNServiceOperations.ItemsService_add.name()))
					{
						tx = em.getTransaction();
						tx.begin();
						new ItemsServiceBean().addMultipleLocationsItems(httpRequest, em, itemPacket.getItem(), itemPacket);
						tx.commit();
						String[] locationsId = itemPacket.getLocationsListId().split(",");
						for (String packetlocationId : locationsId)
						{
							if (packetlocationId != null && packetlocationId.length() > 0)
							{
								itemPacket.setLocationId(packetlocationId);
							}
							else
							{
								itemPacket.setLocationId(itemPacket.getItem().getLocationsId() + "");
							}
							
							sendPacketForBroadcast(POSNServiceOperations.ItemsService_add.name(), itemPacket);
							futureUpdate.setStatus("D");
							em.getTransaction().begin();
							em.merge(futureUpdate);
							em.getTransaction().commit();
						}
					}

				}
				catch (RuntimeException e)
				{
					if (tx != null && tx.isActive())
					{
						tx.rollback();
					}
					futureUpdate.setStatus("R");
					em.getTransaction().begin();
					em.merge(futureUpdate);
					em.getTransaction().commit();
					throw e;
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
		catch (Exception e)
		{
			logger.severe(e, "Error while saving transaction for capture all: ", e.getMessage());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private List<FutureUpdate> getAllFutureUpdateByLocationIdAndDate(EntityManager em, String businessId, String currentDate)
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
