/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.NameConstant;
import com.nirvanaxp.payment.util.BatchSettlementUtil;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.AtomicOperationLockManager;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.AbstractNirvanaService;
import com.nirvanaxp.services.jaxrs.OrderManagementServiceBean;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.storeForward.PaymentBatchManager;
import com.nirvanaxp.types.entities.concurrent.AtomicOperation;
import com.nirvanaxp.types.entities.concurrent.ProcessLock;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.time.Timezone;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class ProcessPayment extends AbstractNirvanaService {

	@Context
	HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(ProcessPayment.class.getName());
	private String filePath = null;

	private void initalizePaymentLogFile() {

		try {

			filePath = ConfigFileReader.readPaymentLogsPathFromFile();

			File file = new File(filePath);

			boolean dosFileExists = false;

			/*
			 * exists() method tests whether the file or directory denoted by
			 * this abstract pathname exists or not accordingly it will return
			 * TRUE / FALSE.
			 */

			if (!file.exists()) {

				/*
				 * createNewFile() method is used to creates a new, empty file
				 * mentioned by given abstract pathname if and only if a file
				 * with this name does not exist in given abstract pathname.
				 */
				try {
					dosFileExists = file.createNewFile();
					file = new File(filePath);
				} catch (IOException e) {
					logger.severe(httpRequest, e);
				}
			}

			if (dosFileExists) {
				logger.info("Empty File successfully created");
			} else {
				logger.info("Failed to create File");
			}
		} catch (Exception e) {
			logger.severe(httpRequest, e);
		}
	}

	@POST
	@Path("/precaptureTransactionsForDateLocationsIdUserIdAndGatewayTypeId")
	public String precaptureTransactionsForDateLocationsIdUserIdAndGatewayTypeId(
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, ProcessPaymentPacket processPaymentPacket)
			throws Exception {

		initalizePaymentLogFile();

		EntityManager em = null;
		ProcessLock lock = null;

		try {

			// TODO Ankur - need to understand why session check would be a
			// problem in this method
			// is session valid
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			final String schemaName = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId)
					.getSchema_name();
			final String locationId = processPaymentPacket.getLocationId();

			// can we get a lock to proceed?
			lock = AtomicOperationLockManager.getInstance().getAtomicOperationLock(httpRequest,
					AtomicOperation.Type.PRE_CAPTURE, locationId);
			if (lock == null) {
				return "Pre Capture Already In Progress";
			}

			// now proceed
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new java.util.Date(formatter.parse(processPaymentPacket.getDate()).getTime());

			String userId = processPaymentPacket.getUserId();
			String currentDate = processPaymentPacket.getCurrentDate();
			String currentTime = processPaymentPacket.getCurrentTime();
			String gatewayTypeIdString = processPaymentPacket.getPaymentGatewayTypeIdString();

			logger.severe(httpRequest,
					"Error during get precaptureTransactionsForDateLocationsIdUserIdAndGatewayTypeId: locationid= "
							+ locationId);

			List<OrderHeader> orderHeadersList = new OrderManagementServiceBean()
					.getAllOrderPaymentDetailsByUserIdLocationDate(httpRequest, em, userId, locationId, date);
			final BatchDetail bd = getActiveBatch(em, locationId);
			if (bd != null) {
				if (checkPreAuthTransaction(bd.getId(), em)) {
					throw new NirvanaXPException(new NirvanaServiceErrorResponse(
							MessageConstants.ERROR_CODE_PRE_AUTH_TRANSACTION_PRESENT_IN_CURRENT_BATCH,
							MessageConstants.ERROR_CODE_PRE_AUTH_TRANSACTION_PRESENT_IN_CURRENT_BATCH_MESSAGE, null));
				}
			}
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
			Timezone timeZone = em.find(Timezone.class, location.getTimezoneId());

			String javaTimezoneName = getTimeZoneStrForJavaTimeZone(timeZone);
			if (orderHeadersList != null && orderHeadersList.size() > 0) {
				logger.severe(httpRequest, "Found Orders to settle: " + orderHeadersList.size());

				// chunk the list into batches of max 10
				// so we do not use up max connections
				// or use up max CPU and slow down processing for other clients
				Stream<List<OrderHeader>> chunks = batches(orderHeadersList, 10);

				chunks.forEach((List<OrderHeader> subList) -> processHeaderBatch(subList, schemaName, locationId,
						userId, currentDate, currentTime, gatewayTypeIdString, location, javaTimezoneName, bd));

				// precapture of Datacap with FirstData
				try {
					List<OrderHeader> orderHeadersList2 = null;
					try {
						em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

						orderHeadersList2 = new OrderManagementServiceBean()
								.getAllOrderPaymentDetailsByUserIdLocationBatchWiseForDatacapWithFirstData(httpRequest,
										em, userId, locationId, sessionId);
					} catch (Exception e) {
						logger.severe(e);
					} finally {
						LocalSchemaEntityManager.getInstance().closeEntityManager(em);
					}

					if (orderHeadersList2 != null && orderHeadersList2.size() > 0) {
						logger.info(httpRequest, "Found Orders to settle: " + orderHeadersList2.size());

						for (OrderHeader orderHeader : orderHeadersList2) {
							BatchSettlementUtil batchSettlementUtil = new BatchSettlementUtil(null, schemaName,
									locationId, false, userId, currentDate, currentTime, httpRequest,
									bd.getIsPrecapturedError(), currentDate, orderHeader, gatewayTypeIdString, location,
									javaTimezoneName, 1,orderHeadersList2);

							try {
								em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
								batchSettlementUtil.btnPreSettledAllTransacton(httpRequest, em, gatewayTypeIdString,
										true, bd.getIsPrecapturedError(), currentDate, orderHeader);

							} catch (Exception e) {
								logger.severe(e);
							}

						}
					}
				} catch (Exception e) {
					logger.severe(e);
				}

				return "true";
			} else {
				String message = "No Orders Found";
				logger.severe(httpRequest,
						"Error during get  precaptureTransactionsForDateLocationsIdUserIdAndGatewayTypeId", message);
				return "No Orders Found";
			}
		} finally {
			if (!AtomicOperationLockManager.getInstance().removeAtomicOperationLock(httpRequest, lock)) {
				String message = "could not release lock";
				logger.severe(httpRequest,
						"Error due to get precaptureTransactionsForDateLocationsIdUserIdAndGatewayTypeId", message);
			}
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	private void processHeaderBatch(List<OrderHeader> orderHeadersList, String schemaName, String locationId, String userId,
			String currentDate, String currentTime, String gatewayTypeIdString, Location location,
			String javaTimezoneName, BatchDetail bd) {
		try {
			CountDownLatch latch = new CountDownLatch(orderHeadersList.size());
			for (OrderHeader orderHeader : orderHeadersList) {
				BatchSettlementUtil batchSettlementUtil = new BatchSettlementUtil(latch, schemaName, locationId, false,
						userId, currentDate, currentTime, httpRequest, bd.getIsPrecapturedError(), currentDate,
						orderHeader, gatewayTypeIdString, location, javaTimezoneName, 0,orderHeadersList);
				try {
					Thread t = new Thread(batchSettlementUtil);
					t.start();
				} catch (Exception e) {
					logger.severe(httpRequest, e, "Error processing payment for order:" + orderHeader.getId(),
							"at location:" + locationId, "in schema:", schemaName, e.getMessage());
				}
			}

			// wait for batch to end
			latch.await();
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Error processing payment for multiple orders at location:" + locationId,
					"in schema:", schemaName, e.getMessage());
		}
	}
	
	
	
	

	private <T> Stream<List<T>> batches(List<T> source, int length) {
		if (length <= 0)
			throw new IllegalArgumentException("length = " + length);
		int size = source.size();
		if (size <= 0)
			return Stream.empty();
		int fullChunks = (size - 1) / length;
		return IntStream.range(0, fullChunks + 1)
				.mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
	}

	@GET
	@Path("/settleBatchForDateLocationsIdUserId/{date}/{locationId}/{userId}/{currentDate}/{currentTime}")
	public String batchSettleForDateLocationsIdUserId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("date") Date date, @PathParam("locationId") String locationId, @PathParam("userId") String userId,
			@PathParam("currentDate") String currentDate, @PathParam("currentTime") String currentTime)
			throws Exception {

		EntityManager em = null;

		initalizePaymentLogFile();

		ProcessLock lock = null;

		try {

			// is session valid
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			// can we get a lock to proceed?
			lock = AtomicOperationLockManager.getInstance().getAtomicOperationLock(httpRequest,
					AtomicOperation.Type.BATCH_SETTLE, locationId);
			if (lock == null) {
				return "Batch Settle Already In Progress";
			}

			// now proceed
			List<OrderHeader> orderHeadersList = new OrderManagementServiceBean()
					.getAllOrderPaymentDetailsByUserIdLocationDate(httpRequest, em, userId, locationId, date);
			if (orderHeadersList != null && orderHeadersList.size() > 0) {
				// removing pre capture
				// BatchSettlementUtil batchSettlementUtil = new
				// BatchSettlementUtil(sessionId, locationId, false, userId,
				// currentDate, currentTime, orderHeadersList);
				// batchSettlementUtil.initializeValues(httpRequest, em);
				// batchSettlementUtil.btnPreSettledAllTransacton(httpRequest,
				// em, "", false);

				orderHeadersList = new OrderManagementServiceBean()
						.getAllOrderPaymentDetailsByUserIdLocationDate(httpRequest, em, userId, locationId, date);
				BatchSettlementUtil batchSettlementUtil = new BatchSettlementUtil(locationId, false, userId,
						currentDate, currentTime, orderHeadersList);
				batchSettlementUtil.initializeValues(httpRequest, em);
				batchSettlementUtil.captureAllTransaction(httpRequest, em);
				PaymentBatchManager.getInstance().closeCurrentActiveBatchAndInitNext(httpRequest, sessionId,
						locationId);

				return "true";

			} else {
				logger.severe(httpRequest, "Error due to get batchSettleForDateLocationsIdUserId",
						"no orders list found in batchSettleForDateLocationsIdUserId");
			}

			return "false";
		} finally {
			if (!AtomicOperationLockManager.getInstance().removeAtomicOperationLock(httpRequest, lock)) {
				logger.severe(httpRequest, "Error due to get batchSettleForDateLocationsIdUserId",
						"could not release lock");
			}
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}
	}

	@POST
	@Path("/batchSettleForDateLocationsIdUserIdAndPaymentGatewayTyepId")
	public String batchSettleForDateLocationsIdUserIdAndPaymentGatewayTypeId(
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, ProcessPaymentPacket processPaymentPacket)
			throws Exception {

		EntityManager em = null;
		String locationId = null;

		initalizePaymentLogFile();

		ProcessLock lock = null;

		try {

			// is session valid
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			// can we get a lock to proceed?
			lock = AtomicOperationLockManager.getInstance().getAtomicOperationLock(httpRequest,
					AtomicOperation.Type.BATCH_SETTLE, locationId);
			if (lock == null) {
				return "Batch Settle Already In Progress";
			}

			// now proceed
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new java.util.Date(formatter.parse(processPaymentPacket.getDate()).getTime());
			locationId = processPaymentPacket.getLocationId();
			String userId = processPaymentPacket.getUserId();
			String currentDate = processPaymentPacket.getCurrentDate();
			String currentTime = processPaymentPacket.getCurrentTime();
			String gatewayIdString = processPaymentPacket.getPaymentGatewayTypeIdString();
			String batchId = PaymentBatchManager.getInstance().getCurrentBatchIdBySession(httpRequest, em
					,locationId, false,processPaymentPacket,processPaymentPacket.getUserId());

			List<OrderHeader> orderHeadersList = new OrderManagementServiceBean()
					.getAllOrderPaymentDetailsByUserIdLocationDate(httpRequest, em, userId, locationId, date);
			if (orderHeadersList != null && orderHeadersList.size() > 0) {
				// BatchSettlementUtil batchSettlementUtil = new
				// BatchSettlementUtil(sessionId, locationId, false, userId,
				// currentDate, currentTime, orderHeadersList);
				// removing settled logic
				// batchSettlementUtil.initializeValues(httpRequest, em);
				// batchSettlementUtil.btnPreSettledAllTransacton(httpRequest,
				// em, gatewayIdString, true);

				// now get updated data from database
				orderHeadersList = new OrderManagementServiceBean()
						.getAllOrderPaymentDetailsByUserIdLocationDate(httpRequest, em, userId, locationId, date);
				BatchSettlementUtil batchSettlementUtil = new BatchSettlementUtil(locationId, false, userId,
						currentDate, currentTime, orderHeadersList);
				batchSettlementUtil.initializeValues(httpRequest, em);
				batchSettlementUtil.captureAllTransaction(httpRequest, em, gatewayIdString, date, batchId);

				PaymentBatchManager.getInstance().closeCurrentActiveBatchAndInitNext(httpRequest, sessionId,
						locationId);
				return "true";
			} else {
				logger.severe(httpRequest, "batchSettleForDateLocationsIdUserId", "No Orders Found");
			}

			return "false";
		} finally {
			if (!AtomicOperationLockManager.getInstance().removeAtomicOperationLock(httpRequest, lock)) {
				String message = "could not release lock";
				logger.severe(httpRequest, "Exception in batchSettleForDateLocationsIdUserIdAndPaymentGatewayTypeId",
						message);
			}
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive() {
		return true;
	}

	@Override
	protected NirvanaLogger getNirvanaLogger() {
		return logger;
	}

	@POST
	@Path("/precaptureTransactionsForDateLocationsIdUserIdAndGatewayTypeIdBatchwise")
	public String precaptureTransactionsForDateLocationsIdUserIdAndGatewayTypeIdBatchwise(
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, ProcessPaymentPacket processPaymentPacket)
			throws Exception {

		initalizePaymentLogFile();
		String locationId = null;
		String schemaName = null;
		EntityManager em = null;
		ProcessLock lock = null;
		// String serverSessionId = null;
		List<OrderHeader> orderHeadersList = null;
		String javaTimezoneName = null;
		BatchDetail bd = null;
		Location location = null;
		Timezone timeZone = null;
		try {

			// is session valid
			schemaName = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId)
					.getSchema_name();

			// can we get a lock to proceed?
			lock = AtomicOperationLockManager.getInstance().getAtomicOperationLock(httpRequest,
					AtomicOperation.Type.PRE_CAPTURE, locationId);
			if (lock == null) {
				return "Pre Capture Already In Progress";
			}

			// now proceed

			// get a server side session
			// serverSessionId =

			// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			// Date date = new
			// java.util.Date(formatter.parse(processPaymentPacket.getDate()).getTime());
			locationId = processPaymentPacket.getLocationId();
			String userId = processPaymentPacket.getUserId();
			String currentDate = processPaymentPacket.getCurrentDate();
			String currentTime = processPaymentPacket.getCurrentTime();
			String gatewayTypeIdString = processPaymentPacket.getPaymentGatewayTypeIdString();

			logger.severe(httpRequest,
					"precaptureTransactionsForDateLocationsIdUserIdAndGatewayTypeId process starts for schemanae :- "
							+ schemaName + " and locationid= " + locationId);
			
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				orderHeadersList = new OrderManagementServiceBean().getAllOrderPaymentDetailsByUserIdLocationBatchWise(
						httpRequest, em, userId, locationId, sessionId);
				bd = getActiveBatch(em, locationId);
				if (bd!=null) {
					if (checkPreAuthTransaction(bd.getId(), em)) {
						throw new NirvanaXPException(new NirvanaServiceErrorResponse(
								MessageConstants.ERROR_CODE_PRE_AUTH_TRANSACTION_PRESENT_IN_CURRENT_BATCH,
								MessageConstants.ERROR_CODE_PRE_AUTH_TRANSACTION_PRESENT_IN_CURRENT_BATCH_MESSAGE, null));
					}
				}
				location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
				timeZone = em.find(Timezone.class, location.getTimezoneId());
				javaTimezoneName = getTimeZoneStrForJavaTimeZone(timeZone);
			

			if (orderHeadersList != null && orderHeadersList.size() > 0) {
				logger.info(httpRequest, "Found Orders to settle: " + orderHeadersList.size());

				CountDownLatch latch = new CountDownLatch(orderHeadersList.size());

				for (OrderHeader orderHeader : orderHeadersList) {
					BatchSettlementUtil batchSettlementUtil = new BatchSettlementUtil(latch, schemaName, locationId,
							false, userId, currentDate, currentTime, httpRequest, bd.getIsPrecapturedError(),
							currentDate, orderHeader, gatewayTypeIdString, location, javaTimezoneName, 0,orderHeadersList);
					try {
						Thread t = new Thread(batchSettlementUtil);
						t.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.severe(e);
					}
				}

				latch.await();
				// precapture of Datacap with FirstData
				try {
					List<OrderHeader> orderHeadersList2 = null;
					try {
						em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

						orderHeadersList2 = new OrderManagementServiceBean()
								.getAllOrderPaymentDetailsByUserIdLocationBatchWiseForDatacapWithFirstData(httpRequest,
										em, userId, locationId, sessionId);
					} catch (Exception e) {
						logger.severe(e);
					} finally {
						LocalSchemaEntityManager.getInstance().closeEntityManager(em);
					}

					if (orderHeadersList2 != null && orderHeadersList2.size() > 0) {
						logger.info(httpRequest, "Found Orders to settle: " + orderHeadersList2.size());

						for (OrderHeader orderHeader : orderHeadersList2) {
							BatchSettlementUtil batchSettlementUtil = new BatchSettlementUtil(null, schemaName,
									locationId, false, userId, currentDate, currentTime, httpRequest,
									bd.getIsPrecapturedError(), currentDate, orderHeader, gatewayTypeIdString, location,
									javaTimezoneName, 1,orderHeadersList2);

							try {
								em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
								batchSettlementUtil.btnPreSettledAllTransacton(httpRequest, em, gatewayTypeIdString,
										true, bd.getIsPrecapturedError(), currentDate, orderHeader);

							} catch (Exception e) {
								logger.severe(e);
							}

						}
					}

					// check aut close
					boolean autoClose = false;
					LocationSetting locationSetting = null;
					try {
						em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
						locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
						if (locationSetting !=null && locationSetting.getIsAutoBatchClose()==1) {

							new OrderManagementServiceBean().closeBusiness(httpRequest, em, userId, locationId,
									sessionId,processPaymentPacket,currentDate);

							sendPacketForBroadcast(POSNServiceOperations.LookupService_updateBatchDetail.name(),
									processPaymentPacket);

						}
					} catch (NirvanaXPException e) {
						throw e;
					}
				} catch (Exception e) {
					logger.severe(e);
				} finally {
					LocalSchemaEntityManager.getInstance().closeEntityManager(em);
				}

				logger.info(
						"++++++++++++++++++++++++++---------------------------------finished pre capture--------------------------------+++++++++++++++++");
				return "true";

			} else {
				String message = "No Orders Found";
				logger.severe(httpRequest,
						"Error during get  precaptureTransactionsForDateLocationsIdUserIdAndGatewayTypeId", message);
				return "false";
			}
		} catch (Exception e) {
			bd.setIsPrecapturedError(1);
			LocalSchemaEntityManager.merge(em, bd);
			throw e;
		} finally {
			if (!AtomicOperationLockManager.getInstance().removeAtomicOperationLock(httpRequest, lock)) {
				String message = "could not release lock";
				logger.severe(httpRequest,
						"Error due to get precaptureTransactionsForDateLocationsIdUserIdAndGatewayTypeId", message);
			}
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	private String getTimeZoneStrForJavaTimeZone(Timezone timezone) {
		String timeZone = timezone.getTimezoneName();
		String parts[] = timeZone.split("Time");
		String partData = (parts[1]);
		partData = partData.trim();
		return partData;
	}

	@POST
	@Path("/batchSettleForDateLocationsIdUserIdAndPaymentGatewayTyepIdBatchwise")
	public String batchSettleForDateLocationsIdUserIdAndPaymentGatewayTyepIdBatchwise(
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, ProcessPaymentPacket processPaymentPacket)
			throws NirvanaXPException, NumberFormatException, IOException, InvalidSessionException, Exception {
		String userId = processPaymentPacket.getUserId();
		EntityManager em = null;
		EntityTransaction tx = null;
		String locationId = null;
		boolean flag = false;
		// initalizePaymentLogFile();

		ProcessLock lock = null;
		String schemaName = null;
		String currentDate = null;
		try {
			// is session valid
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			// can we get a lock to proceed?
			schemaName = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId)
					.getSchema_name();

			locationId = processPaymentPacket.getLocationId();

			lock = AtomicOperationLockManager.getInstance().getAtomicOperationLock(httpRequest,
					AtomicOperation.Type.BATCH_SETTLE, locationId);
			if (lock == null) {
				return "Batch Settle Already In Progress";
			}

			// now proceed
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new java.util.Date(formatter.parse(processPaymentPacket.getDate()).getTime());
			locationId =processPaymentPacket.getLocationId();

			 currentDate = processPaymentPacket.getCurrentDate();
			String currentTime = processPaymentPacket.getCurrentTime();
			String gatewayIdString = processPaymentPacket.getPaymentGatewayTypeIdString();
			BatchSettlementUtil batchSettlementUtil = null;

			List<OrderHeader> orderHeadersList = null;

			BatchDetail batch = PaymentBatchManager.getInstance().getCurrentBatchBySession(httpRequest, em, 
					processPaymentPacket.getLocationId(), false,processPaymentPacket,processPaymentPacket.getUserId());
			if (batch != null) {
				String batchId = batch.getId();
				if (batchId != null) {
					if (checkPreAuthTransaction(batchId, em)) {
						throw new NirvanaXPException(new NirvanaServiceErrorResponse(
								MessageConstants.ERROR_CODE_PRE_AUTH_TRANSACTION_PRESENT_IN_CURRENT_BATCH,
								MessageConstants.ERROR_CODE_PRE_AUTH_TRANSACTION_PRESENT_IN_CURRENT_BATCH_MESSAGE, null));
					}
				}
				if (getAuthTransactionCount(em, locationId) > 0) {
					throw new NirvanaXPException(new NirvanaServiceErrorResponse("NXP1002",
							"Step 1 is not done successfully. Please check transactions",
							"Step 1 is not done successfully. Please check transactions"));
				}
				orderHeadersList = new OrderManagementServiceBean().getAllOrderPaymentDetailsByUserIdLocationBatchWise(
						httpRequest, em, userId, locationId, sessionId);
				if (orderHeadersList != null && orderHeadersList.size() > 0) {
					// setting schemane in session id
					batchSettlementUtil = new BatchSettlementUtil(schemaName, locationId, false, userId, currentDate,
							currentTime, orderHeadersList);
					batchSettlementUtil.initializeValues(httpRequest, em);
					batchSettlementUtil.captureAllTransaction(httpRequest, em, gatewayIdString, date, batchId);
					flag = true;
				} else {
					logger.severe(httpRequest, "batchSettleForDateLocationsIdUserIdAndPaymentGatewayTyepIdBatchwise",
							"No Orders Found for batch settle");
				}
			}else{
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_BATCH_ID_NOT_PRESENT_WITH_DATE__EXCEPTION,
						MessageConstants.ERROR_MESSAGE_BATCH_ID_NOT_PRESENT_WITH_DATE_DISPLAY_MESSAGE, null));
			}
		} finally {
			if (!AtomicOperationLockManager.getInstance().removeAtomicOperationLock(httpRequest, lock)) {
				String message = "could not release lock";
				logger.severe(httpRequest,
						"Exception in batchSettleForDateLocationsIdUserIdAndPaymentGatewayTyepIdBatchwise", message);
			}
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

		// logic :- if autclose is on :- then we need to settle all opd

		// now clo8se business
		// TODO Ankur - is this not critical? if not, then why throw
		// exception on rollback, just log severe error.
		// we need to throw exception if close business not done that is why
		// throwing exception

		// em =
		// LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
		// sessionId);
		try {
			// tx = em.getTransaction();

			if (flag) {

				new OrderManagementServiceBean().closeBusiness(httpRequest, em, userId, locationId, sessionId,processPaymentPacket,currentDate);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updateBatchDetail.name(),
						processPaymentPacket);

			}
		} catch (NirvanaXPException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return "true";

	}

	private void sendPacketForBroadcast(String operation, PostPacket postPacket) {

		MessageSender messageSender = new MessageSender();
		operation = ServiceOperationsUtility.getOperationName(operation);
		messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.LookupService.name(), operation,
				null, postPacket.getMerchantId(), postPacket.getLocationId(), postPacket.getEchoString(),
				postPacket.getSessionId());

	}

	private int getAuthTransactionCount(EntityManager em, String locationId) {
		// removing mosambee transaction and jio
		String queryString = "select count(*) from order_payment_details opd join payment_transaction_type pt "
				+ " on pt.id =opd.payment_transaction_type_id join payment_method pm on pm.id = opd.payment_method_id "
				+ " join payment_method_type pmt on pmt.id=pm.payment_method_type_id "
				+ " join transaction_status ts on ts.id=opd.transaction_status_id "
				+ " where opd.nirvanaxp_batch_number in  (select id from batch_detail where location_id=? and status='A') "
				+ " and ts.name in ('CC Auth','Tip Saved') and pmt.name ='Credit Card' "
				+ " and opd.id not in (select opd.id from order_payment_details "
				+ " opd join transaction_status ts on opd.transaction_status_id=ts.id 		"
				+ " where (order_source_group_to_paymentgatewaytype_id in (select osgpt.id from order_source_group_to_paymentgateway_type osgpt "
				+ " join paymentgateway_type pgt on osgpt.paymentgateway_type_id =  pgt.id   where pgt.name in (?,?,?,?))"
				+ " or order_source_to_paymentgatewaytype_id in (select ospt.id from order_source_to_paymentgateway_type ospt "
				+ " join paymentgateway_type pgt on ospt.paymentgateway_type_id =  pgt.id  where pgt.name in (?,?,?,?))) and nirvanaxp_batch_number in (select id from batch_detail where location_id=? and status='A') "
				+ " and ts.name in ('CC Auth','Tip Saved')) ";
		Query q = em.createNativeQuery(queryString).setParameter(1, locationId)
				.setParameter(2, NameConstant.JIO_GATEWAY).setParameter(3, NameConstant.MOSAMBEE_GATEWAY)
				.setParameter(4, NameConstant.BRAINTREE_GATEWAY).setParameter(5, NameConstant.DATACAP_GATEWAY)
				.setParameter(6, NameConstant.JIO_GATEWAY).setParameter(7, NameConstant.MOSAMBEE_GATEWAY)
				.setParameter(8, NameConstant.BRAINTREE_GATEWAY).setParameter(9, NameConstant.DATACAP_GATEWAY)

				.setParameter(10, locationId);
		Object numberOfTransaction = q.getSingleResult();
		BigInteger bigInteger = (BigInteger) numberOfTransaction;
		return bigInteger.intValue();

	}

	private BatchDetail getActiveBatch(EntityManager em, String locationId) throws IOException, InvalidSessionException {
		String queryString = "select b from BatchDetail b where b.locationId=? and status='A' order by b.id desc  ";
		BatchDetail resultSet = null;
		try {

			TypedQuery<BatchDetail> query = em.createQuery(queryString, BatchDetail.class).setParameter(1, locationId);
			resultSet = query.getSingleResult();

		} catch (Exception e) {
			logger.severe("no active batch found");
		}
		return resultSet;
	}

	private boolean checkPreAuthTransaction(String batchId, EntityManager em) {
		try {
			String queryString = "select count(*) from order_payment_details opd join transaction_status  ts "
					+ "  on ts.id=opd.transaction_status_id where opd.nirvanaxp_batch_number =? and ts.name='CC Pre Auth' ";
			Query q = em.createNativeQuery(queryString).setParameter(1, batchId);
			BigInteger numberOfTransaction = (BigInteger) q.getSingleResult();
			if (numberOfTransaction.intValue() > 0) {
				return true;
			}

		} catch (Exception e) {
			logger.severe(httpRequest, e);
		}
		return false;
	}
}