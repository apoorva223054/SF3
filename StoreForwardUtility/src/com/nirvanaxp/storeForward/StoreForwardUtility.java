package com.nirvanaxp.storeForward;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.fasterxml.uuid.Generators;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
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
import com.nirvanaxp.services.jaxrs.packets.ReservationAndOrderPacket;
import com.nirvanaxp.services.jaxrs.packets.StoreForwardPacket;
import com.nirvanaxp.services.util.email.SendEmail;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.orders.Publisher;
import com.nirvanaxp.types.entities.orders.PublisherHistory;
import com.nirvanaxp.types.entities.orders.TableIndex;

public class StoreForwardUtility {
	private static final NirvanaLogger logger = new NirvanaLogger(StoreForwardUtility.class.getName());

	public void callSynchPacketsWithServerForGeneric(String json, HttpServletRequest httpRequest, String locationId,
			int accountId, String serviceName) {

		if (json != null) {
			try {
				UserSession session = LocalSchemaEntityManager.getInstance().getUserSessionUsingSessionId(httpRequest,
						httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
				String schemaName = session.getSchema_name();
				if (accountId == 0) {
					accountId = session.getMerchant_id();
				}
				String serverName = httpRequest.getServerName();
				if (serviceName == null) {
					serviceName = httpRequest.getRequestURI();
				}

				if (serviceName.contains("updateOrderForDuplicateCheck")) {
					serviceName = serviceName.replace("updateOrderForDuplicateCheck", "update");
				} else if (serviceName.contains("updateOrderPaymentForDuplicateCheck")) {
					serviceName = serviceName.replace("updateOrderPaymentForDuplicateCheck", "updateOrderPayment");
				} else if (serviceName.contains("updateOrderStatusForDuplicateCheck")) {
					serviceName = serviceName.replace("updateOrderStatusForDuplicateCheck", "updateOrderStatus");
				}

				String methodType = httpRequest.getMethod();

				PacketSynchThread packetSynchThread = new PacketSynchThread(httpRequest, serverName, schemaName, json,
						serviceName, methodType, accountId, locationId, new Publisher(),
						httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));

				Thread t = new Thread(packetSynchThread);
				t.start();
			} catch (Exception e) {
				logger.severe(e);
			}
		}

	}

	public void callSynchPacketsWithServer(String json, HttpServletRequest httpRequest, String locationId,
			int accountId) {
		callSynchPacketsWithServerForGeneric(json, httpRequest, locationId, accountId, null);
	}

	public String returnJsonPacket(PostPacket postPacket, String packetName, HttpServletRequest httpRequest) {
		String json = null;
		try {
			json = null;
			if (postPacket.getLocalServerURL() == 0) {
				postPacket.setLocalServerURL(1);
				json = "{\"" + packetName + "\":" + new JSONUtility(httpRequest).convertToJsonString(postPacket) + "}";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		if (json != null && json.length() > 0) {
			json = json.replaceAll("é", "e");
		}
		return json;
	}

	public String returnJsonPacket(StoreForwardPacket postPacket, String packetName, HttpServletRequest httpRequest) {
		String json = null;
		try {
			json = null;
			if (postPacket.getLocalServerURL() == 0) {
				postPacket.setLocalServerURL(1);
				json = "{\"" + packetName + "\":" + new JSONUtility(httpRequest).convertToJsonString(postPacket) + "}";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		if (json != null && json.length() > 0) {
			json = json.replaceAll("é", "e");
		}

		return json;
	}

	public boolean insertSyncPacketWithServer(String serverNamePublisher, String schemaName, String jsonPacket,
			String serviceName, String methodType, Publisher publisher, int accountId, String locationId,
			String sessionId) throws IOException, InvalidSessionException {
		// check Live server accesible
		// get server configuration

		EntityManager globalEM = null;
		EntityManager em = null;
		try {
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
			if (accountId == 0) {
				UserSession session = GlobalSchemaEntityManager.getInstance().getUserSessionWithoutSessionCheck(null,
						sessionId);
				accountId = session.getMerchant_id();
			}
			if (locationId == null) {
				Location globalLocation = new CommonMethods().getBaseLocation(em);
				locationId = globalLocation.getId();
			}
			ServerConfig publisherServerConfig = getServerConfigByName(globalEM, serverNamePublisher, locationId);
			if (publisherServerConfig != null) {

				AccountToServerConfig subscriberAccountToServerConfig = getSubscriberAccountToServerConfig(globalEM,
						locationId, accountId, publisherServerConfig.getId());

				if (subscriberAccountToServerConfig != null) {
					ServerConfig subscriberServerConfig = globalEM.find(ServerConfig.class,
							subscriberAccountToServerConfig.getServerConfigId());
					if (subscriberServerConfig != null) {
						String serverURL = subscriberServerConfig.getType() + "://" + subscriberServerConfig.getName()
								+ ":" + subscriberServerConfig.getPort();
						String serverURLIncludingPath = serverURL + "" + serviceName;

						Location globalLocation = new CommonMethods().getBaseLocation(em);
						locationId = globalLocation.getId();
						if (publisher.getId() == 0) {
							publisher.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							publisher.setLocationId(locationId);
							publisher.setPacket(jsonPacket);
							publisher.setServiceName(serviceName);
							publisher.setStatus("NS");
							publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							publisher.setUpdatedBy(("11"));
							publisher.setServiceURL(serverURLIncludingPath);
							publisher.setAccountId(accountId);
							publisher.setMethodType(INirvanaService.POST_METHOD_TYPE);
							em.getTransaction().begin();
							em.persist(publisher);
							new PublisherHistory().insertPublisherHistory(em, publisher);
							em.getTransaction().commit();
							return true;
						}

					} else {
						logger.severe(
								"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!subscriberServerConfig not present with id="
										+ subscriberAccountToServerConfig.getServerConfigId());
					}

				} else {
					logger.severe(locationId + "!!!+" + accountId
							+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!subscriberAccountToServerConfig not present with name="
							+ accountId);
				}

			} else {
				logger.severe(locationId
						+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!publisherServerConfig not present with name="
						+ serverNamePublisher);

			}

		} catch (RuntimeException e) {
			logger.severe(e);
			// on error, if transaction active,
			// rollback
			if (globalEM.getTransaction() != null && globalEM.getTransaction().isActive()) {
				globalEM.getTransaction().rollback();
			}
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;

		} catch (Exception e) {
			logger.severe(e);
		} finally

		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return false;
	}

	public boolean syncExistingPacketWithServer(String serverNamePublisher, String schemaName, String jsonPacket,
			String serviceName, String methodType, Publisher publisher, int accountId, String locationId,
			String sessionId, HttpServletRequest httpRequest) throws IOException, InvalidSessionException {
		// check Live server accesible
		// get server configuration

		EntityManager globalEM = null;
		EntityManager em = null;
		try {
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
			if (accountId == 0) {
				UserSession session = GlobalSchemaEntityManager.getInstance().getUserSessionWithoutSessionCheck(null,
						sessionId);
				accountId = session.getMerchant_id();
			}
			if (locationId == null) {
				Location globalLocation = new CommonMethods().getBaseLocation(em);
				locationId = globalLocation.getId();
			}
			ServerConfig publisherServerConfig = getServerConfigByName(globalEM, serverNamePublisher, locationId);
			if (publisherServerConfig != null) {

				AccountToServerConfig subscriberAccountToServerConfig = getSubscriberAccountToServerConfig(globalEM,
						locationId, accountId, publisherServerConfig.getId());
				if (subscriberAccountToServerConfig != null) {
					ServerConfig subscriberServerConfig = globalEM.find(ServerConfig.class,
							subscriberAccountToServerConfig.getServerConfigId());
					if (subscriberServerConfig != null) {
						String serverURL = subscriberServerConfig.getType() + "://" + subscriberServerConfig.getName()
								+ ":" + subscriberServerConfig.getPort();
						String serverURLIncludingPath = serverURL + "" + serviceName;
						HTTPClient client = new HTTPClient();
						String liveResponse = null;
						String response = null;
						boolean connectionAvailable = false;
						// checkNetworkConnection
						// check connection code
						try {
							liveResponse = client.sendGet(serverURL + "/OrderManagementServiceV6/isAlive",
									subscriberAccountToServerConfig.getAuthenticationToken());
						} catch (Exception e1) {
							logger.severe(e1);
						}
						if (liveResponse != null) {
							connectionAvailable = true;
						}
						if (connectionAvailable) {
							if (methodType.equals(INirvanaService.GET_METHOD_TYPE)) {
								// not working on get
								response = client.sendGet(serverURLIncludingPath,
										subscriberAccountToServerConfig.getAuthenticationToken());
								return handleResponseForGet(publisher, em, response, schemaName);
							} else if (methodType.equals(INirvanaService.POST_METHOD_TYPE)) {
								try {
									// check server availability

									if (publisher.getId() > 0) {
										response = client.sendPostJSONObject(jsonPacket, serverURLIncludingPath,
												subscriberAccountToServerConfig.getAuthenticationToken());
										return handleResponse(publisher, em, response, schemaName, locationId,
												httpRequest, serverURL,
												subscriberAccountToServerConfig.getAuthenticationToken());
									} else {
									}
								} catch (Exception e) {
									logger.severe(e);
								}
							}
						}

					} else {
						logger.severe(
								"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!subscriberServerConfig not present with id="
										+ subscriberAccountToServerConfig.getServerConfigId());
					}

				} else {
					logger.severe(locationId + "!!!+" + accountId
							+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!subscriberAccountToServerConfig not present with name="
							+ accountId);
				}

			} else {
				logger.severe(
						"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!publisherServerConfig not present with name="
								+ serverNamePublisher);

			}

		} catch (RuntimeException e) {
			logger.severe(e);
			// on error, if transaction active,
			// rollback
			if (globalEM.getTransaction() != null && globalEM.getTransaction().isActive()) {
				globalEM.getTransaction().rollback();
			}
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;

		} catch (Exception e) {
			logger.severe(e);
		} finally

		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return false;
	}

	private AccountToServerConfig getSubscriberAccountToServerConfig(EntityManager em, String locationId, int accountId,
			int publisherId) {
		try {
			// elimination publisher id so that we can get subscriber account to
			// server config
			// TODO Auto-generated catch block
			logger.severe("=================================================================locationId=" + locationId
					+ " and accountId=" + accountId + " and publisherid = " + publisherId);
			String queryString = "select l from AccountToServerConfig l where l.accountsId=?  and l.locationId=? and l.subscriberServerId =? and l.isServerUrl=1 ";
			TypedQuery<AccountToServerConfig> query = em.createQuery(queryString, AccountToServerConfig.class)
					.setParameter(1, accountId).setParameter(2, locationId).setParameter(3, publisherId);
			AccountToServerConfig resultSet = query.getSingleResult();
			return resultSet;
		} catch (NoResultException e) {
			// TODO Auto-generated catch block
			logger.severe("No result found for locationId=" + locationId + " and accountId=" + accountId
					+ " and publisherid = " + publisherId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	private ServerConfig getServerConfigByName(EntityManager em, String name, String locationId) {
		try {
			String queryString = "select l from ServerConfig l where l.name=?  and l.resource='serverUrl' and l.locationId=?";
			TypedQuery<ServerConfig> query = em.createQuery(queryString, ServerConfig.class).setParameter(1, name)
					.setParameter(2, locationId);
			ServerConfig resultSet = query.getSingleResult();
			return resultSet;
		} catch (NoResultException e) {

			logger.severe(locationId + " No result found for serverConfig =" + name);
		} catch (Exception e) {

			logger.severe(e);
		}
		return null;
	}

	public void synchUtility(EntityManager em, HttpServletRequest httpRequest, String locationId) {
		// fetch datafrom db with statys NS
		List<Publisher> rejectedPublishers = getRejected(em);

		if (rejectedPublishers == null || rejectedPublishers.size() == 0) {

			List<Publisher> underProcessPubliseher = getUnderProcessPublisher(em);
			if (underProcessPubliseher != null && underProcessPubliseher.size() > 0) {
				for (Publisher publisher : underProcessPubliseher) {

					try {
						String schemaName = LocalSchemaEntityManager.getInstance().getSchemaNameUsingSessionId(
								httpRequest, httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
						String serverName = httpRequest.getServerName();
						boolean result = syncExistingPacketWithServer(serverName, schemaName, publisher.getPacket(),
								publisher.getServiceName(), publisher.getMethodType(), publisher,
								publisher.getAccountId(), publisher.getLocationId(),
								httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME), httpRequest);
						if (!result) {
							break;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.severe(e);
					} catch (InvalidSessionException e) {
						// TODO Auto-generated catch block
						logger.severe(e);
					}
				}
			} else {
				// retry rejected packet
				

				List<Publisher> publishers = getNonSynchPublisher(em, locationId);
				if (publishers != null && publishers.size() > 0) {
					for (Publisher publisher : publishers) {

						try {
							em.getTransaction().begin();
							publisher.setStatus("UP");
							new PublisherHistory().insertPublisherHistory(em, publisher);
							em.getTransaction().commit();
						} catch (Exception e1) {
							logger.severe(e1);
						}
					}
					for (Publisher publisher : publishers) {
						try {
							String schemaName = LocalSchemaEntityManager.getInstance().getSchemaNameUsingSessionId(
									httpRequest,
									httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
							String serverName = httpRequest.getServerName();
							boolean result = syncExistingPacketWithServer(serverName, schemaName, publisher.getPacket(),
									publisher.getServiceName(), publisher.getMethodType(), publisher,
									publisher.getAccountId(), publisher.getLocationId(),
									httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME),
									httpRequest);
							if (!result) {
								break;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							logger.severe(e);
						} catch (InvalidSessionException e) {
							// TODO Auto-generated catch block
							logger.severe(e);
						}
					}
				}
			}

		}

	}

	public void pullSynchUtility(EntityManager em, HttpServletRequest httpRequest, int accountId, String locationId,
			String serviceName) {
		// fetch datafrom db with statys NS
		EntityManager globalEM = null;
		try {
			String schemaName = LocalSchemaEntityManager.getInstance().getSchemaNameUsingSessionId(httpRequest,
					httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
			String serverName = httpRequest.getServerName();
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			// get cloud server details

			ServerConfig publisherServerConfig = getServerConfigByName(globalEM, serverName, locationId);
			if (publisherServerConfig != null) {

				AccountToServerConfig subscriberAccountToServerConfig = getSubscriberAccountToServerConfig(globalEM,
						locationId, accountId, publisherServerConfig.getId());
				if (subscriberAccountToServerConfig != null) {
					ServerConfig subscriberServerConfig = globalEM.find(ServerConfig.class,
							subscriberAccountToServerConfig.getServerConfigId());
					if (subscriberServerConfig != null) {
						String serverURL = subscriberServerConfig.getType() + "://" + subscriberServerConfig.getName()
								+ ":" + subscriberServerConfig.getPort();
						String serverURLIncludingPath = serverURL + "" + serviceName;
						HTTPClient client = new HTTPClient();
						String liveResponse = null;
						String response = null;
						boolean connectionAvailable = false;
						// checkNetworkConnection
						// check connection code
						try {
							liveResponse = client.sendGet(serverURL + "/OrderManagementServiceV6/isAlive",
									subscriberAccountToServerConfig.getAuthenticationToken());
						} catch (Exception e1) {
							logger.severe(e1);
						}
						if (liveResponse != null) {
							connectionAvailable = true;
						}
						if (connectionAvailable) {

							try {

								response = client.sendGet(serverURLIncludingPath,
										subscriberAccountToServerConfig.getAuthenticationToken());
								ObjectMapper mapper = new ObjectMapper();
								List<Publisher> underProcessPubliseher = mapper.readValue(response,
										new TypeReference<List<Publisher>>() {
										});

								for (Publisher publisher : underProcessPubliseher) {

									try {

										boolean result = pullExistingPacketWithServer(serverName, schemaName,
												publisher.getPacket(), publisher.getServiceName(),
												publisher.getMethodType(), publisher, publisher.getAccountId(),
												publisher.getLocationId(),
												httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME),
												publisherServerConfig,
												httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME),
												httpRequest);
										if (!result) {
											break;
										}
									} catch (IOException e) {
										// TODO Auto-generated catch block
										logger.severe(e);
									} catch (InvalidSessionException e) {
										// TODO Auto-generated catch block
										logger.severe(e);
									}
								}

							} catch (Exception e) {
								logger.severe(e);
							}

						} else {
							logger.severe(
									"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!subscriberServerConfig not present with id="
											+ subscriberAccountToServerConfig.getServerConfigId());
						}

					} else {
						logger.severe(locationId + "!!!+" + accountId
								+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!subscriberAccountToServerConfig not present with name="
								+ accountId);
					}

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		} catch (InvalidSessionException e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}

	}

	private List<Publisher> getRejected(EntityManager em) {
		List<Publisher> resultSet = null;
		try {
			String queryString = "select l from Publisher l where l.status in ('R') order by id asc";
			TypedQuery<Publisher> query = em.createQuery(queryString, Publisher.class);
			resultSet = query.getResultList();
			if (resultSet != null && resultSet.size() > 0) {
				return resultSet;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return resultSet;
	}

	public List<Publisher> getNonSynchPublisher(EntityManager em, String locationId) {
		List<Publisher> resultSet = null;
		Location globalLocation = new CommonMethods().getBaseLocation(em);
		try {
			String queryString = "select p.id from publisher p where p.status in ('NS')     order by id asc limit 0,20";
			List<Object> query = em.createNativeQuery(queryString).getResultList();
			if (query != null && query.size() > 0) {
				resultSet = new ArrayList<Publisher>();
				for (Object object : query) {
					int id = (int) object;
					Publisher publisher = em.find(Publisher.class, id);
					resultSet.add(publisher);
				}
				return resultSet;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}

		return resultSet;
	}

	public List<Publisher> getRejectedPublisherRetry(EntityManager em, String locationId) {
		List<Publisher> resultSet = null;
		EntityTransaction tx = null;
		try {
			String queryString = "select p.id from publisher p where p.status in ('R')   order by id asc";
			logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+queryString);
			List<Object> query = em.createNativeQuery(queryString).getResultList();
			tx = em.getTransaction();
			if (query != null && query.size() > 0) {
				resultSet = new ArrayList<Publisher>();
				for (Object object : query) {
					int id = (int) object;
					Publisher publisher = em.find(Publisher.class, id);
					if (publisher.getResponse()
							.contains("org.hibernate.exception.GenericJDBCException: Could not open connection")) {
						tx.begin();
						publisher.setStatus("UP");
						publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						em.merge(publisher);
						tx.commit();
					}else if(publisher.getResponse()
							.contains("No Result Found")){
						tx.begin();
						publisher.setStatus("S");
						em.merge(publisher);
						publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						tx.commit();
					}else if(publisher.getResponse()
							.contains("User already exists in global database")){
						tx.begin();
						publisher.setStatus("S");
						em.merge(publisher);
						publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						tx.commit();
					}
					//No Result Found
					else{
						if(publisher.getRetryCount()<5){
							tx.begin();
							publisher.setStatus("UP");
							publisher.setRetryCount(publisher.getRetryCount()+1);
							em.merge(publisher);
							publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							tx.commit();
						}
						
					}

					resultSet.add(publisher);
				}
				return resultSet;
			}

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}

		return resultSet;
	}

	public List<Publisher> getRejectedPublisherRetryWithoutCondition(EntityManager em, String locationId) {
		List<Publisher> resultSet = null;
		// Location globalLocation = new CommonMethods().getBaseLocation(em);
		try {
			String queryString = "select p.id from publisher p where p.status in ('R') and p.location_id in ('"
					+ locationId + "') order by id asc limit 0,10";
			List<Object> query = em.createNativeQuery(queryString).getResultList();
			if (query != null && query.size() > 0) {
				resultSet = new ArrayList<Publisher>();
				for (Object object : query) {
					int id = (int) object;
					Publisher publisher = em.find(Publisher.class, id);

					em.getTransaction().begin();
					publisher.setStatus("UP");
					em.merge(publisher);
					em.getTransaction().commit();

					resultSet.add(publisher);
				}
				return resultSet;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}

		return resultSet;
	}

	private List<Publisher> getUnderProcessPublisher(EntityManager em) {
		List<Publisher> resultSet = null;
		try {
			String queryString = "select p.id from publisher p where p.status in ('UP')  order by id asc ";
			List<Object> query = em.createNativeQuery(queryString).getResultList();
			if (query != null && query.size() > 0) {
				resultSet = new ArrayList<Publisher>();
				for (Object object : query) {
					int id = (int) object;
					Publisher publisher = em.find(Publisher.class, id);
					resultSet.add(publisher);
				}
				return resultSet;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}

		return resultSet;
	}

	public String generateOrderNumber(EntityManager em, String locationId, HttpServletRequest request, int accountId,
			PostPacket packet, String updatedBy) throws Exception {
		return generateUUID();
	}

	boolean handleResponse(Publisher publisher, EntityManager em, String response, String schemaName, String locationId,
			HttpServletRequest httpRequest, String serverURL, String accessToken) throws IOException {

		boolean responseObtained = false;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
			if (response != null && !response.contains("\"errorCode\"")
					&& (response.contains("\"merchantId\"") || response.contains("true")
							|| response.contains("\"true\"") || response.equals(true) || response.equals("1")
							|| response.equals("[]") || response.contains("\"id\"") || response.contains("ORD1020")
							|| response.contains("Inventory does not exists for update"))) {

				responseObtained = true;
				publisher.setStatus("S");
				publisher.setResponse(response);
				logger.severe(publisher.getId() + "response parsed sucessfulyy ======================");
			} else if (response != null
					&& (response.contains("org.hibernate.exception.GenericJDBCException: Could not open connection"))) {
				publisher.setStatus("UP");
				publisher.setResponse(response);
			} else if (response != null
					&& publisher.getServiceName().equals("/ReservationServiceV6/addWalkInOrderAndReservation")
					&& (response.contains("Specified Location for Order already has some order"))) {
				// check by calling getOrderById in remote server.
				HTTPClient client = new HTTPClient();
				String newResponse = null;
				String pack = publisher.getPacket().replace("{\"ReservationAndOrderPacket\":", "");
				pack = pack.substring(0, (pack.length() - 1));
				ReservationAndOrderPacket packet = new ObjectMapper().readValue(pack, ReservationAndOrderPacket.class);

				// check connection code
				try {
					newResponse = client.sendGet(serverURL + "/OrderManagementServiceV6/getOrderById/"
							+ packet.getOrderPacket().getOrderHeader().getId(), accessToken);
				} catch (Exception e) {
					logger.severe(e);
				}
				logger.severe(
						"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@------------" + newResponse);
				if (newResponse == null) {
					publisher.setStatus("UP");
					publisher.setResponse(response);
				} else if (newResponse != null && newResponse.contains("No Result Found")) {
					publisher.setStatus("R");
					publisher.setResponse(newResponse);
				} else if (newResponse != null && newResponse.contains("createdBy")) {
					responseObtained = true;
					publisher.setStatus("S");
				}

			} else if (response != null && (response.contains("[]") || response.contains("\"errorCode\"")
					|| response.contains("No user found") || response.contains("already Exists")
					|| response.equals("0"))) {
				publisher.setStatus("R");
				try {
					sendEmailAfterPacketReject(httpRequest, locationId, em, schemaName);
				} catch (Exception e) {
					logger.severe(e);
				}
				publisher.setResponse(response);
			} else {
				logger.severe(publisher.getId() + "uuuuuuuuuuuuuuuuuuuuuuuunparsed response ======================"
						+ response);
				try {
					if (response != null && response.length() > 0) {
						int res = Integer.parseInt(response);
						if (res > 0) {
							responseObtained = true;
							publisher.setStatus("S");
							logger.severe(publisher.getId() + "response parsed sucessfulyy ======================");
						}
					}
				} catch (Exception e) {
					logger.severe(e);
				}

			}
			publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.getTransaction().begin();
			if (publisher.getId() > 0 && !publisher.getStatus().equals("UP")) {
				publisher = em.merge(publisher);
			}
			if (!publisher.getStatus().equals("UP") && !publisher.getStatus().equals("NS")) {
				new PublisherHistory().insertPublisherHistory(em, publisher);
			}
			em.getTransaction().commit();
			/*
			 * if (publisher.getStatus().equals("S")) {
			 * em.getTransaction().begin(); em.remove(publisher);
			 * em.getTransaction().commit(); }
			 */

		} catch (RuntimeException e) {
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return responseObtained;
	}

	boolean handleResponseForGet(Publisher publisher, EntityManager em, String response, String schemaName)
			throws IOException {

		boolean responseObtained = false;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
			if (response != null && (response.contains("Transaction details captured by eWards successfully"))) {
				responseObtained = true;
				publisher.setStatus("ES");
				publisher.setResponse(response);
				logger.severe(publisher.getId() + "response parsed sucessfulyy ======================");
			} else {
				logger.severe(publisher.getId() + "uuuuuuuuuuuuuuuuuuuuuuuunparsed response ======================");
				publisher.setStatus("ER");
				publisher.setResponse(response);
			}
			publisher.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.getTransaction().begin();
			if (publisher.getId() > 0 && !publisher.getStatus().equals("UP")) {
				publisher = em.merge(publisher);
			}
			if (!publisher.getStatus().equals("UP") && !publisher.getStatus().equals("NS")) {
				new PublisherHistory().insertPublisherHistory(em, publisher);
			}
			em.getTransaction().commit();
			/*
			 * if (publisher.getStatus().equals("S")) {
			 * em.getTransaction().begin(); em.remove(publisher);
			 * em.getTransaction().commit(); }
			 */

		} catch (RuntimeException e) {
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return responseObtained;
	}

	public String generateDynamicBigIntId(EntityManager em, String locationId, HttpServletRequest request,
			String tableName) throws Exception {

		return generateUUID();

	}

	public String generateDynamicIntId(EntityManager em, String locationId, HttpServletRequest request,
			String tableName) throws Exception {

		return generateUUID();

	}

	/*
	 * public int getMaxIntIdByTablename(EntityManager em, String tableName) {
	 * try { String queryString = "select max(id) from " + tableName;
	 * logger.severe(
	 * "queryString================================================================"
	 * + queryString); Query query = em.createNativeQuery(queryString); int
	 * maxId = (int) query.getSingleResult(); String trimData = maxId + "";
	 * maxId = Integer.parseInt(trimData.substring(1)); return maxId; } catch
	 * (Exception e) { // TODO Auto-generated catch block logger.severe(e); }
	 * return 0;
	 * 
	 * }
	 * 
	 * public BigInteger getMaxIdByTablename(EntityManager em, String tableName)
	 * { try { String queryString = "select max(id) from " + tableName;
	 * logger.severe(
	 * "queryString================================================================"
	 * + queryString); Query query = em.createNativeQuery(queryString);
	 * BigInteger maxId = (BigInteger) query.getSingleResult(); String trimData
	 * = maxId.toString(); maxId = new BigInteger(trimData.substrinRg(1));
	 * return maxId; } catch (Exception e) { // TODO Auto-generated catch block
	 * logger.severe(e); } return BigInteger.ZERO;
	 * 
	 * }
	 */
	public synchronized Long getAndUpdateCountOfTableIndex(EntityManager em, String locationId, String tableName,
			boolean isBigInt) {
		TableIndex resultSet = null;
		long index = 0;
		try {
			String queryString = "select n from TableIndex n where n.tableName =?  and n.locationId = ?";
			TypedQuery<TableIndex> query = em.createQuery(queryString, TableIndex.class).setParameter(1, tableName)
					.setParameter(2, locationId);

			resultSet = query.getSingleResult();
		} catch (NoResultException e) {
			logger.severe("@@@@@ no result found for " + tableName);
		}
		// creating new nirvanaIndex for first time
		if (resultSet == null) {
			resultSet = new TableIndex();
			resultSet.setLocationId(locationId);
			logger.severe(resultSet.toString());
			if (isBigInt) {
				BigInteger indexx = null;
				if (!(tableName.equals("batch_detail"))) {
					indexx = getMaxCountBigIntById(em, tableName);
				}

				if (indexx == null) {
					resultSet.setIndexing(1);
				} else {
					resultSet.setIndexing(index + 1);
				}
				logger.severe("there" + resultSet.toString());

			} else {

				int indexx = 0;
				indexx = indexx + 1;
				resultSet.setIndexing(1);
				logger.severe("hreeee" + resultSet.toString());
			}

			resultSet.setTableName(tableName);
			// logger.severe("before persist"+resultSet.toString());
			em.persist(resultSet);
		} else {

			if (resultSet.getIndexing() != 0) {
				resultSet.setIndexing(resultSet.getIndexing() + 1);
			} else {
				resultSet.setIndexing(1);
			}
			resultSet = em.merge(resultSet);
		}
		index = resultSet.getIndexing();

		return index;

	}

	public synchronized long resetOrderNumber(EntityManager em, String locationId, String tableName, boolean isBigInt) {
		TableIndex resultSet = null;
		long index = 0;
		try {
			String queryString = "select n from TableIndex n where n.tableName =?  and n.locationId = ?";
			TypedQuery<TableIndex> query = em.createQuery(queryString, TableIndex.class).setParameter(1, tableName)
					.setParameter(2, locationId);
			resultSet = query.getSingleResult();
		} catch (NoResultException e) {
			logger.severe("@@@@@ no result found for " + tableName);
		}
		// creating new nirvanaIndex for first time
		if (resultSet != null) {
			resultSet.setIndexing(0);
			resultSet = em.merge(resultSet);
		}
		index = resultSet.getIndexing();

		return index;

	}

	public String generateOrderNumberWithBusinessId(EntityManager em, String locationId, HttpServletRequest request,
			PostPacket packet, String updatedBy) throws Exception {
		return generateUUID();
	}

	public String generateUUID() {
		UUID uuid1 = Generators.timeBasedGenerator().generate();
		return uuid1.toString();
	}

	public TableIndex getTableIndex(EntityManager em, String locationId, String tableName) {
		try {
			String queryString = "select n from TableIndex n where n.tableName =?  and n.locationId = ?";
			TypedQuery<TableIndex> query = em.createQuery(queryString, TableIndex.class).setParameter(1, tableName)
					.setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			logger.severe(e);
		}
		return null;
	}

	public int getMaxCountIntById(EntityManager em, String tableName) {

		String queryString = "select max(id) from " + tableName + " ";
		try {
			Query query = em.createNativeQuery(queryString);
			Object object = query.getSingleResult();
			if (object != null) {
				return (int) object;
			}

		} catch (Exception e) {
			logger.severe(e);
		}
		return 0;

	}

	public BigInteger getMaxCountBigIntById(EntityManager em, String tableName) {

		String queryString = "select max(id) from " + tableName + " ";
		try {
			Query query = em.createNativeQuery(queryString);
			return (BigInteger) query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);
		}
		return BigInteger.ZERO;

	}

	public long getIndexFromTableIndex(EntityManager em, String locationId, HttpServletRequest request,
			String tableName) throws Exception {
		return getAndUpdateCountOfTableIndex(em, locationId, tableName, false);
	}

	Location getLocationsById(HttpServletRequest httpRequest, EntityManager em, int id) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Location> cl = builder.createQuery(Location.class);
			Root<Location> l = cl.from(Location.class);
			TypedQuery<Location> query = em
					.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Location_.id), id))));

			return query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}

	public boolean pullExistingPacketWithServer(String serverNamePublisher, String schemaName, String jsonPacket,
			String serviceName, String methodType, Publisher publisher, int accountId, String locationId,
			String sessionId, ServerConfig subscriberServerConfig, String authenticationToken,
			HttpServletRequest httpRequest) throws IOException, InvalidSessionException {
		// check Live server accesible
		// get server configuration

		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);

			if (locationId == null) {
				Location globalLocation = new CommonMethods().getBaseLocation(em);
				locationId = globalLocation.getId();
			}

			if (subscriberServerConfig != null) {
				String serverURL = subscriberServerConfig.getType() + "://" + subscriberServerConfig.getName() + ":"
						+ subscriberServerConfig.getPort();
				String serverURLIncludingPath = serverURL + "" + serviceName;
				HTTPClient client = new HTTPClient();
				String response = null;

				if (true) {
					if (methodType.equals(INirvanaService.GET_METHOD_TYPE)) {
						// not working on get
					} else if (methodType.equals(INirvanaService.POST_METHOD_TYPE)) {
						try {
							// check server availability

							if (publisher.getId() > 0) {
								response = client.sendPostJSONObject(jsonPacket, serverURLIncludingPath,
										authenticationToken);
								return handleResponse(publisher, em, response, schemaName, locationId, httpRequest,
										serverURL, authenticationToken);
							} else {
							}
						} catch (Exception e) {
							logger.severe(e);
						}
					}
				}

			} else {
				logger.severe(
						"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!subscriberServerConfig not present with id="
								+ subscriberServerConfig.getId());

			}

		} catch (RuntimeException e) {
			logger.severe(e);
			// on error, if transaction active,
			// rollback

			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;

		} catch (Exception e) {
			logger.severe(e);
		} finally

		{

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return false;
	}

	/*
	 * public String generateOrderNumberNew(String locationId, EntityManager em)
	 * {
	 * 
	 * BigInteger value1 = new
	 * StoreForwardUtility().getAndUpdateCountOfTableIndex(em, locationId,
	 * "order_header", true);
	 * 
	 * Date date = new Date(); Calendar calendar = new GregorianCalendar();
	 * calendar.setTime(date); int year = calendar.get(Calendar.YEAR); // Add
	 * one to month {0 - 11} int month = calendar.get(Calendar.MONTH) + 1; int
	 * day = calendar.get(Calendar.DAY_OF_YEAR); return year + "-" + day + "-" +
	 * value1; }
	 */

	public String generatePaidInOutNew(String locationId, EntityManager em) {

		long value1 = new StoreForwardUtility().getAndUpdateCountOfTableIndex(em, locationId,
				"employee_operation_to_cash_register", false);

		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		// Add one to month {0 - 11}
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		return year + "-" + day + "-" + value1;
	}

	public String sendEmailAfterPacketReject(HttpServletRequest httpRequest, String locationId, EntityManager em,
			String schemaname) throws Exception {

		Location l = (Location) new CommonMethods().getObjectById("Location", em, Location.class, locationId);
		;
		List<String> emailList = new ArrayList<String>();
		emailList.add("naman223054@gmail.com");
		emailList.add("dipakwadekar123@gmail.com");
		emailList.add("amolbhandge@gmail.com");
		emailList.add("Prashant.chavan1812@gmail.com");
		emailList.add("vaibhavadeshmukh69@gmail.com");

		if (true) {
			// send email code
			String emailBody = "Urgent attention needed ,Packet Synch Stopped working";
			String emailSubject = "!!! URGENT !!! Schemaname :- " + schemaname + " and location name = " + l.getName()
					+ " Urgent attention needed ,Packet Synch Stopped working !! ";
			for (String emailAddress : emailList) {
				new SendEmail().sendHEBBatchCloseEmailToSupport(em, httpRequest, emailBody, locationId, emailAddress,
						emailSubject);
			}

		}

		return new JSONUtility(httpRequest)
				.convertToJsonString("Urgent attention needed ,Packet Synch Stopped working");

	}

	public String generateOrderNumberNew(String locationId, EntityManager em, String batchId, String tableName) {

		long value1 = new StoreForwardUtility().getAndUpdateCountOfTableIndex(em, locationId, tableName, true);
		BatchDetail batchDetail = getBatchsById(em, batchId);

		return batchDetail.getDayOfYear() + "-" + value1;
	}

	public String generateNewNumber(String locationId, EntityManager em, String tableName,
			HttpServletRequest httpRequest) throws IOException, InvalidSessionException {

		long value1 = new StoreForwardUtility().getAndUpdateCountOfTableIndex(em, locationId, tableName, true);

		return new StoreForwardUtility().getDayOfYear(locationId, em) + "-" + value1;
	}

	public String getDayOfYear(String locationId, EntityManager em) {

		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		// Add one to month {0 - 11}
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		return year + "-" + day;
	}

	BatchDetail getBatchsById(EntityManager em, String id) {

		try {
			BatchDetail batchDetail = (BatchDetail) new CommonMethods().getObjectById("BatchDetail", em,
					BatchDetail.class, id);
			return batchDetail;
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}
}
