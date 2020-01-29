package com.nirvanaxp.storeForward;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.TableIndex;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.global.types.entities.accounts.AccountToServerConfig;
import com.nirvanaxp.global.types.entities.accounts.ServerConfig;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.HTTPClient;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.jaxrs.INirvanaService;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.StoreForwardPacket;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.Publisher;
import com.nirvanaxp.types.entities.orders.PublisherHistory;

public class StoreForwardUtilityForGlobal
{
	private static final NirvanaLogger logger = new NirvanaLogger(StoreForwardUtilityForGlobal.class.getName());

	public void callSynchPacketsWithServer(String json, HttpServletRequest httpRequest, String locationId, int accountId)
	{

		try
		{
			UserSession session = LocalSchemaEntityManager.getInstance().getUserSessionUsingSessionId(httpRequest, httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
			String schemaName = session.getSchema_name();
			if (accountId == 0)
			{
				accountId = session.getMerchant_id();
			}
			String serverName = httpRequest.getServerName();
			String serviceName = httpRequest.getRequestURI();
			if (serviceName.contains("updateOrderForDuplicateCheck"))
			{
				serviceName = serviceName.replace("updateOrderForDuplicateCheck", "update");
			}
			String methodType = httpRequest.getMethod();

			PacketSynchThread packetSynchThread = new PacketSynchThread(httpRequest, serverName, schemaName, json, serviceName, methodType, accountId, locationId, new Publisher(),
					httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));

			Thread t = new Thread(packetSynchThread);
			t.start();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

	}

	public String returnJsonPacket(PostPacket postPacket, String packetName, HttpServletRequest httpRequest)
	{
		String json = null;
		try
		{
			json = null;
			if (postPacket.getLocalServerURL() == 0)
			{
				postPacket.setLocalServerURL(1);
				json = "{\"" + packetName + "\":" + new JSONUtility(httpRequest).convertToJsonString(postPacket) + "}";
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return json;
	}

	public String returnJsonPacket(StoreForwardPacket postPacket, String packetName, HttpServletRequest httpRequest)
	{
		String json = null;
		try
		{
			json = null;
			if (postPacket.getLocalServerURL() == 0)
			{
				postPacket.setLocalServerURL(1);
				json = "{\"" + packetName + "\":" + new JSONUtility(httpRequest).convertToJsonString(postPacket) + "}";
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return json;
	}

	public void syncPacketWithServer(String serverNamePublisher, String schemaName, String jsonPacket, String serviceName, String methodType, Publisher publisher, int accountId, String locationId,
			String sessionId) throws IOException, InvalidSessionException
	{
		// check Live server accesible
		// get server configuration

		EntityManager globalEM = null;
		EntityManager em = null;
		try
		{
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
			if (accountId == 0)
			{
				UserSession session = GlobalSchemaEntityManager.getInstance().getUserSessionWithoutSessionCheck(null, sessionId);
				accountId = session.getMerchant_id();
			}
			if (locationId == null)
			{
				Location globalLocation = new CommonMethods().getBaseLocation(em);
				locationId = globalLocation.getId();
			}
			ServerConfig publisherServerConfig = getServerConfigByName(globalEM, serverNamePublisher);
			if (publisherServerConfig != null)
			{

				AccountToServerConfig subscriberAccountToServerConfig = getSubscriberAccountToServerConfig(globalEM, locationId, accountId, publisherServerConfig.getId());

				if (subscriberAccountToServerConfig != null)
				{
					ServerConfig subscriberServerConfig = globalEM.find(ServerConfig.class, subscriberAccountToServerConfig.getServerConfigId());
					if (subscriberServerConfig != null)
					{
						String serverURL = subscriberServerConfig.getType() + "://" + subscriberServerConfig.getName() + ":" + subscriberServerConfig.getPort();
						String serverURLIncludingPath = serverURL + "" + serviceName;
						HTTPClient client = new HTTPClient();
						String liveResponse = null;
						String response = null;
						boolean connectionAvailable = false;
						// checkNetworkConnection
						// check connection code
						try
						{
							liveResponse = client.sendGet(serverURL + "/OrderManagementServiceV6/isAlive", subscriberAccountToServerConfig.getAuthenticationToken());
						}
						catch (Exception e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (liveResponse != null)
						{
							connectionAvailable = true;
						}
						if (connectionAvailable)
						{
							if (methodType.equals(INirvanaService.GET_METHOD_TYPE))
							{

								// success response
								if (publisher.getId() > 0)
								{
									try
									{
										response = client.sendGet(serverURLIncludingPath, subscriberAccountToServerConfig.getAuthenticationToken());
									}
									catch (Exception e)
									{
										// TODO Auto-generated catch block
										logger.severe(e);
									}
									logger.severe("response===================" + response);
									handleResponse(publisher, em, response, schemaName);
								}
								else
								{
									if (publisher.getId() == 0)
									{
										logger.severe("jsonPacket===================" + jsonPacket);
										publisher.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
										publisher.setLocationId(locationId);
										publisher.setPacket(jsonPacket);
										publisher.setServiceName(serviceName);
										publisher.setStatus("NS");
										publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
										publisher.setUpdatedBy(("21"));
										publisher.setServiceURL(serverURLIncludingPath);
										publisher.setAccountId(accountId);
										publisher.setMethodType(INirvanaService.GET_METHOD_TYPE);
										em.getTransaction().begin();
										em.persist(publisher);
										em.getTransaction().commit();
									}
									else
									{
										publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
										publisher.setStatus("NS");
										em.getTransaction().begin();
										publisher = em.merge(publisher);
										new PublisherHistory().insertPublisherHistory(em, publisher);
										em.getTransaction().commit();
									}
								}

							}
							else if (methodType.equals(INirvanaService.POST_METHOD_TYPE))
							{
								try
								{
									// check server availability

									if (publisher.getId() > 0)
									{
										response = client.sendPostJSONObject(jsonPacket, serverURLIncludingPath, subscriberAccountToServerConfig.getAuthenticationToken());
										logger.severe("response===================" + response);
										handleResponse(publisher, em, response, schemaName);
									}
									else
									{
										if (publisher.getId() == 0)
										{
											publisher.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
											publisher.setLocationId(locationId);
											publisher.setPacket(jsonPacket);
											publisher.setServiceName(serviceName);
											publisher.setStatus("NS");
											publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
											publisher.setUpdatedBy(("21"));
											publisher.setServiceURL(serverURLIncludingPath);
											publisher.setAccountId(accountId);
											publisher.setMethodType(INirvanaService.POST_METHOD_TYPE);
											em.getTransaction().begin();
											em.persist(publisher);
											new PublisherHistory().insertPublisherHistory(em, publisher);
											em.getTransaction().commit();
										}
										else
										{
											publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
											publisher.setStatus("NS");
											em.getTransaction().begin();
											publisher = em.merge(publisher);
											new PublisherHistory().insertPublisherHistory(em, publisher);
											em.getTransaction().commit();
										}
									}
								}
								catch (Exception e)
								{
									logger.severe(e);
								}
							}
						}
						else
						{

							if (publisher.getId() == 0)
							{
								publisher.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								publisher.setLocationId(locationId);
								publisher.setPacket(jsonPacket);
								publisher.setServiceName(serviceName);
								publisher.setStatus("NS");
								publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								publisher.setUpdatedBy(("21"));
								publisher.setServiceURL(serverURLIncludingPath);
								publisher.setAccountId(accountId);
								publisher.setMethodType(INirvanaService.POST_METHOD_TYPE);
								em.getTransaction().begin();
								em.persist(publisher);
								new PublisherHistory().insertPublisherHistory(em, publisher);
								em.getTransaction().commit();
							}
							else
							{
								publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								publisher.setStatus("NS");
								em.getTransaction().begin();
								publisher = em.merge(publisher);
								new PublisherHistory().insertPublisherHistory(em, publisher);
								em.getTransaction().commit();
							}
						}
					}
					else
					{
						logger.severe("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!subscriberServerConfig not present with id=" + subscriberAccountToServerConfig.getServerConfigId());
					}

				}
				else
				{
					logger.severe(locationId + "!!!+" + accountId + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!subscriberAccountToServerConfig not present with name=" + accountId);
				}

			}
			else
			{
				logger.severe("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!publisherServerConfig not present with name=" + serverNamePublisher);

			}

		}
		catch (RuntimeException e)
		{
			logger.severe(e);
			// on error, if transaction active,
			// rollback
			if (globalEM.getTransaction() != null && globalEM.getTransaction().isActive())
			{
				globalEM.getTransaction().rollback();
			}
			if (em.getTransaction() != null && em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
			}
			throw e;

		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		finally

		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	private AccountToServerConfig getSubscriberAccountToServerConfig(EntityManager em, String locationId, int accountId, int publisherId)
	{
		try
		{
			// elimination publisher id so that we can get subscriber account to
			// server config
			String queryString = "select l from AccountToServerConfig l where l.accountsId=?  and l.locationId=? and l.subscriberServerId =? and l.isServerUrl=1 ";
			TypedQuery<AccountToServerConfig> query = em.createQuery(queryString, AccountToServerConfig.class).setParameter(1, accountId).setParameter(2, locationId).setParameter(3, publisherId);
			AccountToServerConfig resultSet = query.getSingleResult();
			return resultSet;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	private ServerConfig getServerConfigByName(EntityManager em, String name)
	{
		try
		{
			String queryString = "select l from ServerConfig l where l.name=?  and l.resource='serverUrl' ";
			TypedQuery<ServerConfig> query = em.createQuery(queryString, ServerConfig.class).setParameter(1, name);
			ServerConfig resultSet = query.getSingleResult();
			return resultSet;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	public void synchUtility(EntityManager em, HttpServletRequest httpRequest)
	{
		// fetch datafrom db with statys NS
		List<Publisher> publishers = getNonSynchPublisher(em);
		for (Publisher publisher : publishers)
		{
			try
			{
				String schemaName = LocalSchemaEntityManager.getInstance().getSchemaNameUsingSessionId(httpRequest, httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
				String serverName = httpRequest.getServerName();
				syncPacketWithServer(serverName, schemaName, publisher.getPacket(), publisher.getServiceName(), publisher.getMethodType(), publisher, publisher.getAccountId(),
						publisher.getLocationId(), httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				logger.severe(e);
			}
			catch (InvalidSessionException e)
			{
				// TODO Auto-generated catch block
				logger.severe(e);
			}
		}

	}

	private List<Publisher> getNonSynchPublisher(EntityManager em)
	{

		try
		{
			String queryString = "select l from Publisher l where l.status='NS'  order by id asc";
			TypedQuery<Publisher> query = em.createQuery(queryString, Publisher.class);
			List<Publisher> resultSet = query.getResultList();
			return resultSet;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	public String generateOrderNumber(EntityManager em, String locationId, HttpServletRequest request, int accountId, PostPacket packet, String updatedBy) throws Exception
	{
		 
		return new StoreForwardUtility().generateUUID();
	
	}

	private void handleResponse(Publisher publisher, EntityManager em, String response, String schemaName) throws IOException
	{

		boolean responseObtained = false;
		if (response != null && (response.contains("\"merchantId\"") || response.contains("\"true\"") || response.contains("\"id\"")))
		{
			responseObtained = true;
			publisher.setStatus("S");
		}
		if (response != null && response.contains("\"errorCode\""))
		{
			responseObtained = true;
			publisher.setStatus("R");
			publisher.setResponse(response);
		}
		if (responseObtained)
		{
			try
			{
				em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
				publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				em.getTransaction().begin();
				publisher = em.merge(publisher);
				new PublisherHistory().insertPublisherHistory(em, publisher);
				if(publisher.getStatus().equals("S")){
					em.remove(publisher);
				}	 
				
				em.getTransaction().commit();
				
				
				
			}
			catch (RuntimeException e)
			{
				if (em.getTransaction() != null && em.getTransaction().isActive())
				{
					em.getTransaction().rollback();
				}
				throw e;
			}
			finally
			{
				LocalSchemaEntityManager.getInstance().closeEntityManager(em);
			}
		}
	}

	public synchronized BigInteger getAndUpdateCountOfTableIndex(EntityManager em, String tableName)
	{
		TableIndex resultSet = null;
		BigInteger index = BigInteger.ZERO;
		try
		{
			String queryString = "select n from TableIndex n where n.tableName =?  ";
			TypedQuery<TableIndex> query = em.createQuery(queryString, TableIndex.class).setParameter(1, tableName);
			resultSet = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			logger.severe(e);
		}
		// creating new nirvanaIndex for first time
		if (resultSet == null)
		{
			resultSet = new TableIndex();
			resultSet.setIndexing(BigInteger.ZERO);
			resultSet.setTableName(tableName);
		}
		resultSet.setIndexing(resultSet.getIndexing().add(BigInteger.ONE));

		try
		{
			em.getTransaction().begin();
			resultSet = em.merge(resultSet);
			em.getTransaction().commit();
		}
		catch (RuntimeException e)
		{
			logger.severe(e);
			// on error, if transaction active,
			// rollback
			if (em.getTransaction() != null && em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
			}
			throw e;

		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		index = resultSet.getIndexing();
		return index;

	}

	public String generateDynamicIntId(EntityManager em, HttpServletRequest request, String tableName) throws Exception
	{
		return new StoreForwardUtility().generateUUID();
	}

	public String generateDynamicBigIntId(EntityManager em, HttpServletRequest request, String tableName) throws Exception
	{
		return new StoreForwardUtility().generateUUID();
	}

}
