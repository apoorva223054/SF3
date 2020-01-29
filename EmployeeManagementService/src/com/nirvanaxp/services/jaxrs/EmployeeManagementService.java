/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.annotation.WebListener;
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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.constants.EmployeeOperationName;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.BreakAndClockInOutGetPacket;
import com.nirvanaxp.services.jaxrs.packets.BreakInBreakOutPacket;
import com.nirvanaxp.services.jaxrs.packets.ClockInClockOutBreakInBreakOutPacket;
import com.nirvanaxp.services.jaxrs.packets.ClockInClockOutGetPacket;
import com.nirvanaxp.services.jaxrs.packets.ClockInClockOutPacket;
import com.nirvanaxp.services.jaxrs.packets.EmployeeOperationPacket;
import com.nirvanaxp.services.jaxrs.packets.EmployeeOperationToCashRegisterPacket;
import com.nirvanaxp.services.jaxrs.packets.EmployeeToEmployeeOperationGetPacket;
import com.nirvanaxp.services.jaxrs.packets.EmployeeToEmployeeOperationPacket;
import com.nirvanaxp.services.jaxrs.packets.EmployeeToEmployeesOperationPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.StaffMessagingPacket;
import com.nirvanaxp.storeForward.PaymentBatchManager;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.employee.BreakInBreakOut;
import com.nirvanaxp.types.entities.employee.ClockInClockOut;
import com.nirvanaxp.types.entities.employee.EmployeeOperation;
import com.nirvanaxp.types.entities.employee.EmployeeOperationToAlertMessage;
import com.nirvanaxp.types.entities.employee.EmployeeOperationToCashRegister;
import com.nirvanaxp.types.entities.employee.EmployeesToEmployeesOperation;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.tip.EmployeeMasterToJobRoles;
import com.nirvanaxp.types.entities.user.StaffMessaging;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToPaymentHistory;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

// TODO: Auto-generated Javadoc
/**
 * The Class EmployeeManagementService.
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class EmployeeManagementService extends AbstractNirvanaService
{

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The employee operation helper. */
	EmployeeOperationHelper employeeOperationHelper = new EmployeeOperationHelper();

	/** The Constant logger. */
	private final static NirvanaLogger logger = new NirvanaLogger(EmployeeManagementService.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.services.jaxrs.AbstractNirvanaService#getNirvanaLogger()
	 */
	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	// ************************************RESERVATION
	// STATUS****************************************//

	/**
	 * Adds the employee operation.
	 *
	 * @param employeeOperationPacket
	 *            the employee operation packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addEmployeeOperation")
	public String addEmployeeOperation(EmployeeOperationPacket employeeOperationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, employeeOperationPacket);
			EmployeeOperationHelper.addEmployeeOperation(em, employeeOperationPacket.getEmployeeOperation(),httpRequest);
			employeeOperationPacket.setEmployeeOperation(employeeOperationPacket.getEmployeeOperation());
			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_addEmployeeOperation.name(), employeeOperationPacket);
			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updatePaidInPaidOut.name(), employeeOperationPacket);
			
			String json = new StoreForwardUtility().returnJsonPacket(employeeOperationPacket, "EmployeeOperationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					employeeOperationPacket.getLocationId(), Integer.parseInt(employeeOperationPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperationPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete printer receipt.
	 *
	 * @param employeeOperationPacket
	 *            the employee operation packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteEmployeeOperation")
	public String deletePrinterReceipt(EmployeeOperationPacket employeeOperationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, employeeOperationPacket);

			EmployeeOperationHelper.deleteEmployeeOperation(em, employeeOperationPacket.getEmployeeOperation());
			employeeOperationPacket.setEmployeeOperation(employeeOperationPacket.getEmployeeOperation());
			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_deleteEmployeeOperation.name(), employeeOperationPacket);
			
			String json = new StoreForwardUtility().returnJsonPacket(employeeOperationPacket, "EmployeeOperationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					employeeOperationPacket.getLocationId(), Integer.parseInt(employeeOperationPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperationPacket);

		}
		// TODO handle catch if some error occur
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all employee operation by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the all employee operation by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllEmployeeOperationByLocationId/{locationId}")
	public String getAllEmployeeOperationByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<EmployeeOperation> employeeOperations = EmployeeOperationHelper.getAllEmployeeOperationByLocationId(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperations);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the employee operation by id.
	 *
	 * @param id
	 *            the id
	 * @param sessionId
	 *            the session id
	 * @return the employee operation by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getEmployeeOperationById/{Id}")
	public String getEmployeeOperationById(@PathParam("Id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			EmployeeOperation employeeOperations = EmployeeOperationHelper.getEmployeeOperationById(em, id);
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperations);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the employee operation by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @param sessionId
	 *            the session id
	 * @return the employee operation by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getEmployeeOperationByLocationIdAndName/{locationId}/{name}")
	public String getEmployeeOperationByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			EmployeeOperation employeeOperations = EmployeeOperationHelper.getEmployeeOperationByLocationIdAndName(em, locationId, name);
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperations);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the employee operation by location id and display sequence.
	 *
	 * @param locationId
	 *            the location id
	 * @param displaySequence
	 *            the display sequence
	 * @param sessionId
	 *            the session id
	 * @return the employee operation by location id and display sequence
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getemployeeOperationByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getEmployeeOperationByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("displaySequence") int displaySequence,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			EmployeeOperation employeeOperations = EmployeeOperationHelper.getEmployeesOperationByLocationIdAndDisplaySequence(em, locationId, displaySequence);
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperations);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the employees to employees operation.
	 *
	 * @param userId
	 *            the user id
	 * @param employeeOperationId
	 *            the employee operation id
	 * @param createdBy
	 *            the created by
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/addEmployeesToEmployeesOperation/{userId}/{employeeOperationId}/{createdBy}")
	public String addEmployeesToEmployeesOperation(@PathParam("userId") String userId, @PathParam("employeeOperationId") String employeeOperationId, @PathParam("createdBy") String createdBy,
		 @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			boolean result = false;
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			EmployeeOperation userOperation = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperationId);
			if (userOperation == null)
			{
				// unknown operation
				throw new IllegalArgumentException("Unknown Employee Operation : " + employeeOperationId);
			}

			// get current Allowed Operations at this location
			List<EmployeeOperation> empOpsList = EmployeeOperationHelper.getAllEmployeeOperationByLocationId(em, userOperation.getLocationsId());

			if (empOpsList == null || empOpsList.isEmpty())
			{
				// unknown operation
				throw new IllegalArgumentException("There are no employee operations defined for location : " + userOperation.getLocationsId());
			}

			// what is the current operation name
			EmployeeOperationName opName = EmployeeOperationName.getByOperationName(userOperation.getOperationName());

			boolean sequenceControlledOp = false;

			if (opName != null)
			{
				// is current operation in the list of operations allowed at
				// current location
				sequenceControlledOp = empOpsList.stream().anyMatch(e -> e.getOperationName().equals(opName.getOperationName()));
			}

			if (!sequenceControlledOp)
			{
				logger.fine(httpRequest, "Current Chosen Operation is", opName == null ? userOperation.getOperationName() : opName.getOperationName(),
						"which is not a sequence controlled Operation at current location: " + userOperation.getLocationsId());
			}
			else
			{

				logger.fine(httpRequest, "current attempted employee operation is", opName.getOperationName(), "which is a sequence controlled operation");

				// get his previous entry for last sequence controlled operation
				EmployeesToEmployeesOperation employeesToEmployeesOperation = EmployeeOperationHelper.getPreviousSequenceControlledOperationForEmployee(em, userOperation.getLocationsId(), userId);

				String previousOpName = null;

				if (employeesToEmployeesOperation != null)
				{
					// get name of previous op
					previousOpName = getPreviousOpNameForId(empOpsList, employeesToEmployeesOperation.getEmployeeOperationId());
				}

				logger.fine(httpRequest, "Previous Operation", previousOpName, ",for Employee: " + userId);

				EmployeeOperationName previousOp = null;
				if (previousOpName != null)
				{
					previousOp = EmployeeOperationName.getByOperationName(previousOpName);
				}

				// switch on it
				switch (opName)
				{
				case BreakIn:
				{
					// previous operation must not be ClockOut
					if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, null)).toString());
					}
					else if (previousOp == null || EmployeeOperationName.ClockIn.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT)).toString());
					}
					else if (previousOp == null || EmployeeOperationName.BreakIn.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT)).toString());
					}
					break;

				}
				case BreakOut:
				{
					// previous operation must be BreakIn
					if (previousOp == null || EmployeeOperationName.ClockIn.equals(previousOp))
					{

					}
					else if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN)).toString());
					}
					else if (previousOp == null || EmployeeOperationName.BreakOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN, null)).toString());
					}
					break;
				}
				case ClockIn:
				{
					// previous operation must be null or ClockOut

					if (null != previousOp && !EmployeeOperationName.ClockOut.equals(previousOp))
					{

						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT, null)).toString());
					}
					break;
				}
				case ClockOut:
				{
					// previous operation must not be Clockout
					//

					if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
					{
						return (new NirvanaXPException(
								new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN, MessageConstants.ERROR_MESSAGE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN, null))
										.toString());
					}
					else if (previousOp == null || EmployeeOperationName.BreakOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN)).toString());
					}
					break;
				}
				}

			}

			// if we got here then all conditions have been met, add the
			// relation
			EmployeesToEmployeesOperation employeeOperationPacket = EmployeeOperationHelper.addEmployeesToEmployeesOperation(em, userId, employeeOperationId, createdBy, null);

			if (employeeOperationPacket != null && employeeOperationPacket.getId() != 0)
			{
				result = true;
			}

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the update employees to employees operation.
	 *
	 * @param employeeToEmployeesOperationPacket
	 *            the employee to employees operation packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdateEmployeesToEmployeesOperation")
	public String addUpdateEmployeesToEmployeesOperation(EmployeeToEmployeesOperationPacket employeeToEmployeesOperationPacket) throws Exception
	{
		EntityManager em = null;
		EmployeesToEmployeesOperation empOperation = employeeToEmployeesOperationPacket.getEmployeesToEmployeesOperation();
		try
		{
			boolean result = false;
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			EmployeeOperation userOperation = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, empOperation.getEmployeeOperationId());
			if (userOperation == null)
			{
				// unknown operation
				throw new IllegalArgumentException("Unknown Employee Operation : " + empOperation.getEmployeeOperationId());
			}

			// get current Allowed Operations at this location
			List<EmployeeOperation> empOpsList = EmployeeOperationHelper.getAllEmployeeOperationByLocationId(em, userOperation.getLocationsId());

			if (empOpsList == null || empOpsList.isEmpty())
			{
				// unknown operation
				throw new IllegalArgumentException("There are no employee operations defined for location : " + userOperation.getLocationsId());
			}

			// what is the current operation name
			EmployeeOperationName opName = EmployeeOperationName.getByOperationName(userOperation.getOperationName());

			boolean sequenceControlledOp = false;

			if (opName != null)
			{
				// is current operation in the list of operations allowed at
				// current location
				sequenceControlledOp = empOpsList.stream().anyMatch(e -> e.getOperationName().equals(opName.getOperationName()));
			}

			if (!sequenceControlledOp)
			{
				logger.fine(httpRequest, "Current Chosen Operation is", opName == null ? userOperation.getOperationName() : opName.getOperationName(),
						"which is not a sequence controlled Operation at current location: " + userOperation.getLocationsId());
			}
			else
			{

				logger.fine(httpRequest, "current attempted employee operation is", opName.getOperationName(), "which is a sequence controlled operation");

				// get his previous entry for last sequence controlled operation
				EmployeesToEmployeesOperation employeesToEmployeesOperation = EmployeeOperationHelper.getPreviousSequenceControlledOperationForEmployee(em, userOperation.getLocationsId(),
						empOperation.getUsersId());

				String previousOpName = null;

				if (employeesToEmployeesOperation != null)
				{
					// get name of previous op
					previousOpName = getPreviousOpNameForId(empOpsList, employeesToEmployeesOperation.getEmployeeOperationId());
				}

				logger.fine(httpRequest, "Previous Operation", previousOpName, ",for Employee: " + empOperation.getUsersId());

				EmployeeOperationName previousOp = null;
				if (previousOpName != null)
				{
					previousOp = EmployeeOperationName.getByOperationName(previousOpName);
				}

				// switch on it
				switch (opName)
				{
				case BreakIn:
				{
					// previous operation must not be ClockOut
					if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, null)).toString());
					}
					else if (previousOp == null || EmployeeOperationName.ClockIn.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT)).toString());
					}
					else if (previousOp == null || EmployeeOperationName.BreakIn.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT)).toString());
					}
					break;

				}
				case BreakOut:
				{
					// previous operation must be BreakIn
					if (previousOp == null || EmployeeOperationName.ClockIn.equals(previousOp))
					{

					}
					else if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN)).toString());
					}
					else if (previousOp == null || EmployeeOperationName.BreakOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN, null)).toString());
					}
					break;
				}
				case ClockIn:
				{
					// previous operation must be null or ClockOut

					if (null != previousOp && !EmployeeOperationName.ClockOut.equals(previousOp))
					{

						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT, null)).toString());
					}
					break;
				}
				case ClockOut:
				{
					// previous operation must not be Clockout
					//

					if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
					{
						return (new NirvanaXPException(
								new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN, MessageConstants.ERROR_MESSAGE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN, null))
										.toString());
					}
					else if (previousOp == null || EmployeeOperationName.BreakOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN)).toString());
					}
					break;
				}
				}

			}

			User user = (User) new CommonMethods().getObjectById("User", em,User.class, empOperation.getUsersId());
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, userOperation.getLocationsId());

			if (opName != null && locationSetting.getIsClockinValidation() == 1)
			{
				int loginStatus = 0;
				if (opName.getOperationName().equals("Clock In") || opName.getOperationName().equals("Break In"))
				{
					loginStatus = 1;
					user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
				}
				if (opName.getOperationName().equals("Clock Out") || opName.getOperationName().equals("Break Out"))
				{
					user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
				}
			}

			if (opName != null && opName.getOperationName().equals("Clock In"))
			{
				PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
				batchManager.getCurrentBatchIdBySession(httpRequest, em, userOperation.getLocationsId(), true, employeeToEmployeesOperationPacket,userOperation.getUpdatedBy());

			}

			// if we got here then all conditions have been met, add the
			// relation
			EmployeesToEmployeesOperation employeeOperationPacket = EmployeeOperationHelper.addEmployeesToEmployeesOperation(em, empOperation.getUsersId(), empOperation.getEmployeeOperationId(),
					empOperation.getCreatedBy(), empOperation.getJobRoleId());
			User userForPush = new User();
			userForPush.setFirstName(user.getFirstName());
			userForPush.setLastName(user.getLastName());
			userForPush.setEmail(user.getEmail());
			userForPush.setPhone(user.getPhone());
			userForPush.setId(user.getId());
			userForPush.setIsAllowedLogin(user.getIsAllowedLogin());
			userForPush.setGlobalUsersId(user.getGlobalUsersId());
			userForPush.setIsTippedEmployee(user.getIsTippedEmployee());
			employeeOperationPacket.setUser(userForPush);
			employeeToEmployeesOperationPacket.setEmployeesToEmployeesOperation(employeeOperationPacket);
			if (employeeOperationPacket != null && employeeOperationPacket.getId() != 0)
			{
				result = true;
			}
			sendPacketForBroadcastWithObj(POSNServiceOperations.EmployeeManagementService_updateEmployeeOperationToEmployeesOperation.name(), employeeToEmployeesOperationPacket);

			String json = new StoreForwardUtility().returnJsonPacket(employeeToEmployeesOperationPacket, "EmployeeToEmployeesOperationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					employeeToEmployeesOperationPacket.getLocationId(), Integer.parseInt(employeeToEmployeesOperationPacket.getMerchantId()));
			
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the update employees to employees operation.
	 *
	 * @param employeeToEmployeesOperationPacket
	 *            the employee to employees operation packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdateEmployeesToEmployeesOperationForClockIn")
	public String addUpdateEmployeesToEmployeesOperationForClockIn(EmployeeToEmployeesOperationPacket employeeToEmployeesOperationPacket) throws Exception
	{
		EntityManager em = null;
		EmployeesToEmployeesOperation empOperation = employeeToEmployeesOperationPacket.getEmployeesToEmployeesOperation();
		try
		{
			boolean result = false;
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			EmployeeOperation userOperation = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, empOperation.getEmployeeOperationId());
			if (userOperation == null)
			{
				// unknown operation
				throw new IllegalArgumentException("Unknown Employee Operation : " + empOperation.getEmployeeOperationId());
			}

			// get current Allowed Operations at this location
			List<EmployeeOperation> empOpsList = EmployeeOperationHelper.getAllEmployeeOperationByLocationId(em, userOperation.getLocationsId());

			if (empOpsList == null || empOpsList.isEmpty())
			{
				// unknown operation
				throw new IllegalArgumentException("There are no employee operations defined for location : " + userOperation.getLocationsId());
			}

			// what is the current operation name
			EmployeeOperationName opName = EmployeeOperationName.getByOperationName(userOperation.getOperationName());

			boolean sequenceControlledOp = false;

			if (opName != null)
			{
				// is current operation in the list of operations allowed at
				// current location
				sequenceControlledOp = empOpsList.stream().anyMatch(e -> e.getOperationName().equals(opName.getOperationName()));
			}

			if (!sequenceControlledOp)
			{
				logger.fine(httpRequest, "Current Chosen Operation is", opName == null ? userOperation.getOperationName() : opName.getOperationName(),
						"which is not a sequence controlled Operation at current location: " + userOperation.getLocationsId());
			}
			else
			{

				logger.fine(httpRequest, "current attempted employee operation is", opName.getOperationName(), "which is a sequence controlled operation");

				// get his previous entry for last sequence controlled operation
				EmployeesToEmployeesOperation employeesToEmployeesOperation = EmployeeOperationHelper.getPreviousSequenceControlledOperationForEmployee(em, userOperation.getLocationsId(),
						empOperation.getUsersId());

				String previousOpName = null;

				if (employeesToEmployeesOperation != null)
				{
					// get name of previous op
					previousOpName = getPreviousOpNameForId(empOpsList, employeesToEmployeesOperation.getEmployeeOperationId());
				}

				logger.fine(httpRequest, "Previous Operation", previousOpName, ",for Employee: " + empOperation.getUsersId());

				EmployeeOperationName previousOp = null;
				if (previousOpName != null)
				{
					previousOp = EmployeeOperationName.getByOperationName(previousOpName);
				}

				// switch on it
				switch (opName)
				{
				case BreakIn:
				{
					// previous operation must not be ClockOut
					if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, null)).toString());
					}
					else if (previousOp == null || EmployeeOperationName.ClockIn.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT)).toString());
					}
					else if (previousOp == null || EmployeeOperationName.BreakIn.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT)).toString());
					}
					break;

				}
				case BreakOut:
				{
					// previous operation must be BreakIn
					if (previousOp == null || EmployeeOperationName.ClockIn.equals(previousOp))
					{

					}
					else if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN)).toString());
					}
					else if (previousOp == null || EmployeeOperationName.BreakOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN, null)).toString());
					}
					break;
				}
				case ClockIn:
				{
					// previous operation must be null or ClockOut

					if (null != previousOp && !EmployeeOperationName.ClockOut.equals(previousOp))
					{

						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT, null)).toString());
					}
					break;
				}
				case ClockOut:
				{
					// previous operation must not be Clockout
					//

					if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
					{
						return (new NirvanaXPException(
								new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN, MessageConstants.ERROR_MESSAGE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN, null))
										.toString());
					}
					else if (previousOp == null || EmployeeOperationName.BreakOut.equals(previousOp))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN)).toString());
					}
					break;
				}
				}

			}
			User user = (User) new CommonMethods().getObjectById("User", em,User.class, empOperation.getUsersId());
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, userOperation.getLocationsId());

			if (opName != null && locationSetting.getIsClockinValidation() == 1)
			{
				int loginStatus = 0;
				if (opName.getOperationName().equals("Clock In") || opName.getOperationName().equals("Break In"))
				{
					loginStatus = 1;
					user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
				}
				if (opName.getOperationName().equals("Clock Out") || opName.getOperationName().equals("Break Out"))
				{
					user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
				}
			}

			List<EmployeeMasterToJobRoles> resultSetRolls = null;
			if (opName != null && opName.getOperationName().equals("Clock In"))
			{
				PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
				batchManager.getCurrentBatchIdBySession(httpRequest, em, userOperation.getLocationsId(), true, employeeToEmployeesOperationPacket,userOperation.getUpdatedBy());

				try
				{
					String queryStringRolls = "select b from EmployeeMasterToJobRoles b where b.status not in('D','I') and b.userId = " + empOperation.getUsersId();

					Query queryRolls = em.createQuery(queryStringRolls);
					resultSetRolls = queryRolls.getResultList();

				}
				catch (Exception e)
				{
					// todo shlok need
					// handel proper exception

					logger.severe(e);
				}

				if (resultSetRolls != null && resultSetRolls.size() > 0)
				{
					for (EmployeeMasterToJobRoles employeeMasterToJobRoles : resultSetRolls)
					{

						if (employeeMasterToJobRoles.getJobRoleId().equals(employeeToEmployeesOperationPacket.getEmployeesToEmployeesOperation().getJobRoleId()))
						{
							employeeMasterToJobRoles.setIsDefaultRole(1);
						}
						else
						{
							employeeMasterToJobRoles.setIsDefaultRole(0);
						}

						employeeMasterToJobRoles.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						employeeMasterToJobRoles = em.merge(employeeMasterToJobRoles);
					}

				}

			}

			// if we got here then all conditions have been met, add the
			// relation

			EmployeesToEmployeesOperation employeeOperationPacket = EmployeeOperationHelper.addEmployeesToEmployeesOperation(em, empOperation.getUsersId(), empOperation.getEmployeeOperationId(),
					empOperation.getCreatedBy(), empOperation.getJobRoleId());
			User userForPush = new User();
			userForPush.setFirstName(user.getFirstName());
			userForPush.setLastName(user.getLastName());
			userForPush.setEmail(user.getEmail());
			userForPush.setPhone(user.getPhone());
			userForPush.setId(user.getId());
			userForPush.setIsAllowedLogin(user.getIsAllowedLogin());
			userForPush.setGlobalUsersId(user.getGlobalUsersId());
			userForPush.setIsTippedEmployee(user.getIsTippedEmployee());
			employeeOperationPacket.setUser(userForPush);
			if (resultSetRolls != null && resultSetRolls.size() > 0)
			{
				employeeOperationPacket.setEmployeeMasterToJobRoles(resultSetRolls);
			}
			employeeToEmployeesOperationPacket.setEmployeesToEmployeesOperation(employeeOperationPacket);
			if (employeeOperationPacket != null && employeeOperationPacket.getId() != 0)
			{
				result = true;
			}
			sendPacketForBroadcastWithObj(POSNServiceOperations.EmployeeManagementService_updateEmployeeOperationToEmployeesOperation.name(), employeeToEmployeesOperationPacket);
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addEmployeeMasterToJobRoles.name(), employeeToEmployeesOperationPacket);
			
			String json = new StoreForwardUtility().returnJsonPacket(employeeToEmployeesOperationPacket, "EmployeeToEmployeesOperationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					employeeToEmployeesOperationPacket.getLocationId(), Integer.parseInt(employeeToEmployeesOperationPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(employeeToEmployeesOperationPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the previous op name for id.
	 *
	 * @param empOpsList
	 *            the emp ops list
	 * @param employeeOperationId
	 *            the employee operation id
	 * @return the previous op name for id
	 */
	private String getPreviousOpNameForId(List<EmployeeOperation> empOpsList, String employeeOperationId)
	{
		List<EmployeeOperation> l = empOpsList.stream().filter(e -> e.getId().equals(employeeOperationId)).collect(Collectors.toList());
		if (l != null && !l.isEmpty())
		{
			return l.get(0).getOperationName();
		}
		return null;
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

		try
		{

			MessageSender messageSender = new MessageSender();
			operation = ServiceOperationsUtility.getOperationName(operation);
			messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.EmployeeManagementService.name(), operation, null, postPacket.getMerchantId(), postPacket.getLocationId(),
					postPacket.getEchoString(), postPacket.getSessionId());
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e);
		}

	}

	/**
	 * Send packet for broadcast with obj.
	 *
	 * @param operation
	 *            the operation
	 * @param postPacket
	 *            the post packet
	 */
	private void sendPacketForBroadcastWithObj(String operation, EmployeeToEmployeesOperationPacket postPacket)
	{

		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			String internalJson = null;
			MessageSender messageSender = new MessageSender();
			operation = ServiceOperationsUtility.getOperationName(operation);
			internalJson = objectMapper.writeValueAsString(postPacket.getEmployeesToEmployeesOperation());
			messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.EmployeeManagementService.name(), operation, internalJson, postPacket.getMerchantId(),
					postPacket.getLocationId(), postPacket.getEchoString(), postPacket.getSessionId());
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.INirvanaService#isAlive()
	 */
	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	/**
	 * Adds the paid in paid out.
	 *
	 * @param employeeOperationPacket
	 *            the employee operation packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addPaidInPaidOut")
	public String addPaidInPaidOut(EmployeeOperationToCashRegisterPacket employeeOperationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, employeeOperationPacket);
			// creating batch at the time of paid in /out
			PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
			String currentBatch = batchManager.getCurrentBatchIdBySession(httpRequest, em, employeeOperationPacket.getLocationId(), true, employeeOperationPacket,"21");

			EmployeeOperationToCashRegister employeeOperationToCashRegister = employeeOperationPacket.getEmployeeOperationToCashRegister();
			employeeOperationToCashRegister.setNirvanaXpBatchNumber(currentBatch);
			EmployeeOperation empOperation = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperationToCashRegister.getEmployeeOperationId());

			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, employeeOperationToCashRegister.getLocationsId());

			if (empOperation.getOperationName().equals("Paid Out") && locationSetting.getIsPaidInPaidOutValidation() == 1)
			{

				boolean validatedPaidOut = employeeOperationHelper.checkEmployeeOperationToCashRegisterByRegisterIdAndOperationId(httpRequest, em, employeeOperationToCashRegister.getRegisterId(),
						logger);
				if (!validatedPaidOut)
					return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_PAID_OUT_BEFORE_PAID_IN,
							MessageConstants.ERROR_MESSAGE_USER_CANNOT_PAID_OUT_BEFORE_PAID_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_PAID_OUT_BEFORE_PAID_IN)).toString());
			}

			EmployeeOperationToCashRegister cashRegister = employeeOperationHelper.addPaidInPaidOut(em, employeeOperationToCashRegister, currentBatch, locationSetting.getLocationId(),httpRequest);
			employeeOperationPacket.setEmployeeOperationToCashRegister(cashRegister);
			sendPacketForBroadcast(httpRequest, employeeOperationPacket, POSNServiceOperations.EmployeeManagementService_addPaidInPaidOut.name());
			
			String json = new StoreForwardUtility().returnJsonPacket(employeeOperationPacket, "EmployeeOperationToCashRegisterPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					employeeOperationPacket.getLocationId(), Integer.parseInt(employeeOperationPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperationPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}	

	/**
	 * Update paid in paid out.
	 *
	 * @param employeeOperationPacket
	 *            the employee operation packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updatePaidInPaidOut")
	public String updatePaidInPaidOut(EmployeeOperationToCashRegisterPacket employeeOperationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, employeeOperationPacket);
			EmployeeOperationHelper employeeOperationHelper = new EmployeeOperationHelper();

			EmployeeOperationToCashRegister cashRegister = employeeOperationHelper.updatePaidInPaidOut(em, employeeOperationPacket.getEmployeeOperationToCashRegister());
			employeeOperationPacket.setEmployeeOperationToCashRegister(cashRegister);
			sendPacketForBroadcast(httpRequest, employeeOperationPacket, POSNServiceOperations.EmployeeManagementService_updatePaidInPaidOut.name());
			
			String json = new StoreForwardUtility().returnJsonPacket(employeeOperationPacket, "EmployeeOperationToCashRegisterPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					employeeOperationPacket.getLocationId(), Integer.parseInt(employeeOperationPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperationPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete paid in paid out.
	 *
	 * @param employeeOperationPacket
	 *            the employee operation packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deletePaidInPaidOut")
	public String deletePaidInPaidOut(EmployeeOperationToCashRegisterPacket employeeOperationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, employeeOperationPacket);

			EmployeeOperationToCashRegister employeeOperationToCashRegister = employeeOperationHelper.deletePaidInPaidOut(em, employeeOperationPacket.getEmployeeOperationToCashRegister());
			employeeOperationPacket.setEmployeeOperationToCashRegister(employeeOperationToCashRegister);
			sendPacketForBroadcast(httpRequest, employeeOperationPacket, POSNServiceOperations.EmployeeManagementService_deletePaidInPaidOut.name());
			
			String json = new StoreForwardUtility().returnJsonPacket(employeeOperationPacket, "EmployeeOperationToCashRegisterPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					employeeOperationPacket.getLocationId(), Integer.parseInt(employeeOperationPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperationPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the paid in paid out by location id and date.
	 *
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @param sessionId
	 *            the session id
	 * @return the paid in paid out by location id and date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaidInPaidOutByLocationIdAndDate/{locationId}/{date}")
	public String getPaidInPaidOutByLocationIdAndDate(@PathParam("locationId") String locationId, @PathParam("date") String date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			EmployeeOperationHelper employeeOperationHelper = new EmployeeOperationHelper();
			List<EmployeeOperationToCashRegister> employeeOperations = employeeOperationHelper.getAllEmployeeOperationToCashRegisterByLocationId(httpRequest, em, locationId, date, false);
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperations);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the paid in paid out by location id and date batch wise.
	 *
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @param sessionId
	 *            the session id
	 * @return the paid in paid out by location id and date batch wise
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaidInPaidOutByLocationIdAndDateBatchWise/{locationId}/{date}")
	public String getPaidInPaidOutByLocationIdAndDateBatchWise(@PathParam("locationId") String locationId, @PathParam("date") String date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			EmployeeOperationHelper employeeOperationHelper = new EmployeeOperationHelper();
			List<EmployeeOperationToCashRegister> employeeOperations = employeeOperationHelper.getAllEmployeeOperationToCashRegisterByLocationId(httpRequest, em, locationId, date, true);
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperations);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the paid in paid out and cash register by location id and date batch
	 * wise.
	 *
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @return the paid in paid out and cash register by location id and date
	 *         batch wise
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaidInPaidOutAndCashRegisterByLocationIdAndDateBatchWise/{locationId}/{date}")
	public String getPaidInPaidOutAndCashRegisterByLocationIdAndDateBatchWise(@PathParam("locationId") String locationId, @PathParam("date") String date) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			EmployeeOperationHelper employeeOperationHelper = new EmployeeOperationHelper();
			List<EmployeeOperationToCashRegister> employeeOperations = employeeOperationHelper.getAllEmployeeOperationToCashRegisterByLocationId(httpRequest, em, locationId, date, true);
			List<RegisterInfo> registerInfo = employeeOperationHelper.getCashRegister(locationId, date, em, true);
			PaidInPaidOutAndCashRegister paidInPaidOutAngCashRegister = new PaidInPaidOutAndCashRegister();
			paidInPaidOutAngCashRegister.setEmployeeOperationToCashRegister(employeeOperations);
			paidInPaidOutAngCashRegister.setRegisterInfoList(registerInfo);
			return new JSONUtility(httpRequest).convertToJsonString(paidInPaidOutAngCashRegister);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param postPacket
	 *            the post packet
	 * @param operationName
	 *            the operation name
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	private void sendPacketForBroadcast(HttpServletRequest httpRequest, PostPacket postPacket, String operationName) throws NirvanaXPException
	{
		try
		{
			// so that session id value does not get broadcasted

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			if (!operationName.equals(POSNServiceOperations.OrderManagementService_mergeOrder.name()))
			{
				objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
			}
			String internalJson = null;
			internalJson = objectMapper.writeValueAsString(postPacket);

			operationName = ServiceOperationsUtility.getOperationName(operationName);
			MessageSender messageSender = new MessageSender();

			messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.EmployeeManagementService.name(), operationName, internalJson, postPacket.getMerchantId(),
					postPacket.getLocationId(), postPacket.getEchoString(), postPacket.getSchemaName());
		}
		catch (IOException e)
		{
			// could not send push
			logger.severe(httpRequest, e);
		}
	}

	/**
	 * Update employee operation.
	 *
	 * @param employeeToEmployeeOperationPacket
	 *            the employee to employee operation packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateEmployeeOperation")
	public String updateEmployeeOperation(EmployeeToEmployeeOperationPacket employeeToEmployeeOperationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, employeeToEmployeeOperationPacket);

			User user = null;
			try
			{

				// check clock in clock out validation
				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					if (employeeOperation.getId() == 0)
					{

						EmployeeOperation userOperation = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
						if (userOperation == null)
						{
							// unknown operation
							throw new IllegalArgumentException("Unknown Employee Operation : " + employeeOperation.getEmployeeOperationId());
						}

						// get current Allowed Operations at this location
						List<EmployeeOperation> empOpsList = EmployeeOperationHelper.getAllEmployeeOperationByLocationId(em, userOperation.getLocationsId());

						if (empOpsList == null || empOpsList.isEmpty())
						{
							// unknown operation
							throw new IllegalArgumentException("There are no employee operations defined for location : " + userOperation.getLocationsId());
						}

						// what is the current operation name
						EmployeeOperationName opName = EmployeeOperationName.getByOperationName(userOperation.getOperationName());

						boolean sequenceControlledOp = false;

						if (opName != null)
						{
							// is current operation in the list of operations
							// allowed at
							// current location
							sequenceControlledOp = empOpsList.stream().anyMatch(e -> e.getOperationName().equals(opName.getOperationName()));
						}

						if (!sequenceControlledOp)
						{
							logger.fine(httpRequest, "Current Chosen Operation is", opName == null ? userOperation.getOperationName() : opName.getOperationName(),
									"which is not a sequence controlled Operation at current location: " + userOperation.getLocationsId());
						}
						else
						{

							logger.fine(httpRequest, "current attempted employee operation is", opName.getOperationName(), "which is a sequence controlled operation");

							// get his previous entry for last sequence
							// controlled
							// operation
							EmployeesToEmployeesOperation employeesToEmployeesOperation = EmployeeOperationHelper.getPreviousSequenceControlledOperationForEmployee(em, userOperation.getLocationsId(),
									employeeOperation.getUsersId());

							String previousOpName = null;

							if (employeesToEmployeesOperation != null)
							{
								// get name of previous op
								previousOpName = getPreviousOpNameForId(empOpsList, employeesToEmployeesOperation.getEmployeeOperationId());
							}

							logger.fine(httpRequest, "Previous Operation", previousOpName, ",for Employee: " + employeeOperation.getUsersId());

							EmployeeOperationName previousOp = null;
							if (previousOpName != null)
							{
								previousOp = EmployeeOperationName.getByOperationName(previousOpName);
							}

							// switch on it
							switch (opName)
							{
							case BreakIn:
							{
								// previous operation must not be ClockOut
								if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, null)).toString());
								}
								else if (previousOp == null || EmployeeOperationName.ClockIn.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT))
													.toString());
								}
								else if (previousOp == null || EmployeeOperationName.BreakIn.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT))
													.toString());
								}
								break;

							}
							case BreakOut:
							{
								// previous operation must be BreakIn
								if (previousOp == null || EmployeeOperationName.ClockIn.equals(previousOp))
								{

								}
								else if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN)).toString());
								}
								else if (previousOp == null || EmployeeOperationName.BreakOut.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN, null)).toString());
								}
								break;
							}
							case ClockIn:
							{
								// previous operation must be null or ClockOut

								if (null != previousOp && !EmployeeOperationName.ClockOut.equals(previousOp))
								{

									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT, null)).toString());
								}
								break;
							}
							case ClockOut:
							{
								// previous operation must not be Clockout
								//

								if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN,
											MessageConstants.ERROR_MESSAGE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN, null)).toString());
								}
								else if (previousOp == null || EmployeeOperationName.BreakOut.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN)).toString());
								}
								break;
							}
							}

						}
					}

				}

				TimezoneTime time = new TimezoneTime();
				String clockInTime = null;
				String clockOutTime = null;

				LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, employeeToEmployeeOperationPacket.getLocationId());

				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					EmployeeOperation employeeOperation2 = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());

					if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && employeeOperation2.getOperationName().equals("Clock In"))
					{
						clockInTime = employeeOperation.getUpdatedStr();
					}
					else if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && employeeOperation2.getOperationName().equals("Clock Out"))
					{
						clockOutTime = employeeOperation.getUpdatedStr();

					}

				}

				// login Status for user

				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					user = (User) new CommonMethods().getObjectById("User", em,User.class, employeeOperation.getUsersId());

					EmployeeOperation employeeOperation2 = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
					if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && locationSetting.getIsClockinValidation() == 1)
					{
						int loginStatus = 0;
						if (employeeOperation2.getOperationName().equals("Clock In"))
						{
							loginStatus = 1;
							user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
						}

					}
				}

				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					user = (User) new CommonMethods().getObjectById("User", em,User.class, employeeOperation.getUsersId());
					EmployeeOperation employeeOperation2 = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
					if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && locationSetting.getIsClockinValidation() == 1)
					{
						int loginStatus = 0;

						if (employeeOperation2.getOperationName().equals("Break Out"))
						{
							user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
						}
					}
				}

				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					user = (User) new CommonMethods().getObjectById("User", em,User.class, employeeOperation.getUsersId());
					EmployeeOperation employeeOperation2 = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
					if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && locationSetting.getIsClockinValidation() == 1)
					{
						int loginStatus = 0;
						if (employeeOperation2.getOperationName().equals("Break In"))
						{
							loginStatus = 1;
							user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
						}

					}
				}

				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					user = (User) new CommonMethods().getObjectById("User", em,User.class, employeeOperation.getUsersId());
					EmployeeOperation employeeOperation2 = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
					if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && locationSetting.getIsClockinValidation() == 1)
					{
						int loginStatus = 0;

						if (employeeOperation2.getOperationName().equals("Clock Out"))
						{
							user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
						}
					}
				}

				// check Existing batch Tip status and give user proper msg
				if (clockInTime != null)
				{

					clockInTime = time.getDateAccordingToGMT(clockInTime, employeeToEmployeeOperationPacket.getLocationId(), em);
					if (clockOutTime == null)
					{
						clockOutTime = new Timestamp(new TimezoneTime().getGMTTimeInMilis()).toString();
					}
					else
					{
						clockOutTime = time.getDateAccordingToGMT(clockOutTime, employeeToEmployeeOperationPacket.getLocationId(), em);
					}


					OrderManagementServiceBean bean = new OrderManagementServiceBean();
					List<BatchDetail> batchDetailList = bean.getBatchForClockInClockOut(httpRequest, em, employeeToEmployeeOperationPacket.getLocationId(), clockInTime,
							clockOutTime);

					for (BatchDetail batchDetail : batchDetailList)
					{

							if (batchDetail.getIsTipCalculated().equals("F"))
							{
								return (new NirvanaXPException(
										new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_TIP_CALCULATED_DONE, MessageConstants.ERROR_MESSAGE_TIP_CALCULATED_DONE, null)).toString());

							}

					}
				}

			}
			catch (Exception e)
			{
				// TODO: handle exception
				logger.severe(e);
			}

			List<EmployeesToEmployeesOperation> employeesToEmployeesOperation = EmployeeOperationHelper.updateEmployeeToEmployeeOperation(em,
					employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation(), employeeToEmployeeOperationPacket.getLocationId());
			employeeToEmployeeOperationPacket.setEmployeesToEmployeesOperation(employeesToEmployeesOperation);

			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updateEmployeeOperation.name(), employeeToEmployeeOperationPacket);

			if (user != null)
			{
				employeeToEmployeeOperationPacket.setUser(user);
			}

			String json = new StoreForwardUtility().returnJsonPacket(employeeToEmployeeOperationPacket, "EmployeeToEmployeeOperationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					employeeToEmployeeOperationPacket.getLocationId(), Integer.parseInt(employeeToEmployeeOperationPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(employeeToEmployeeOperationPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the employee to employee operation.
	 *
	 * @param employeeToEmployeeOperationPacket
	 *            the employee to employee operation packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addEmployeeToEmployeeOperation")
	public String addEmployeeToEmployeeOperation(EmployeeToEmployeeOperationPacket employeeToEmployeeOperationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, employeeToEmployeeOperationPacket);

			User user = null;
			try
			{

				// check clock in clock out validation
				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					if (employeeOperation.getId() == 0)
					{
						EmployeeOperation userOperation = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
						if (userOperation == null)
						{
							// unknown operation
							throw new IllegalArgumentException("Unknown Employee Operation : " + employeeOperation.getEmployeeOperationId());
						}

						// get current Allowed Operations at this location
						List<EmployeeOperation> empOpsList = EmployeeOperationHelper.getAllEmployeeOperationByLocationId(em, userOperation.getLocationsId());

						if (empOpsList == null || empOpsList.isEmpty())
						{
							// unknown operation
							throw new IllegalArgumentException("There are no employee operations defined for location : " + userOperation.getLocationsId());
						}

						// what is the current operation name
						EmployeeOperationName opName = EmployeeOperationName.getByOperationName(userOperation.getOperationName());

						boolean sequenceControlledOp = false;

						if (opName != null)
						{
							// is current operation in the list of operations
							// allowed at
							// current location
							sequenceControlledOp = empOpsList.stream().anyMatch(e -> e.getOperationName().equals(opName.getOperationName()));
						}

						if (!sequenceControlledOp)
						{
							logger.fine(httpRequest, "Current Chosen Operation is", opName == null ? userOperation.getOperationName() : opName.getOperationName(),
									"which is not a sequence controlled Operation at current location: " + userOperation.getLocationsId());
						}
						else
						{

							logger.fine(httpRequest, "current attempted employee operation is", opName.getOperationName(), "which is a sequence controlled operation");

							// get his previous entry for last sequence
							// controlled
							// operation
							EmployeesToEmployeesOperation employeesToEmployeesOperation = EmployeeOperationHelper.getPreviousSequenceControlledOperationForEmployee(em, userOperation.getLocationsId(),
									employeeOperation.getUsersId());

							String previousOpName = null;

							if (employeesToEmployeesOperation != null)
							{
								// get name of previous op
								previousOpName = getPreviousOpNameForId(empOpsList, employeesToEmployeesOperation.getEmployeeOperationId());
							}

							logger.fine(httpRequest, "Previous Operation", previousOpName, ",for Employee: " + employeeOperation.getUsersId());

							EmployeeOperationName previousOp = null;
							if (previousOpName != null)
							{
								previousOp = EmployeeOperationName.getByOperationName(previousOpName);
							}

							// switch on it
							switch (opName)
							{
							case BreakIn:
							{
								// previous operation must not be ClockOut
								if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, null)).toString());
								}
								else if (previousOp == null || EmployeeOperationName.ClockIn.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT))
													.toString());
								}
								else if (previousOp == null || EmployeeOperationName.BreakIn.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT))
													.toString());
								}
								break;

							}
							case BreakOut:
							{
								// previous operation must be BreakIn
								if (previousOp == null || EmployeeOperationName.ClockIn.equals(previousOp))
								{

								}
								else if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN)).toString());
								}
								else if (previousOp == null || EmployeeOperationName.BreakOut.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN, null)).toString());
								}
								break;
							}
							case ClockIn:
							{
								// previous operation must be null or ClockOut

								if (null != previousOp && !EmployeeOperationName.ClockOut.equals(previousOp))
								{

									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT, null)).toString());
								}
								break;
							}
							case ClockOut:
							{
								// previous operation must not be Clockout
								//

								if (previousOp == null || EmployeeOperationName.ClockOut.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN,
											MessageConstants.ERROR_MESSAGE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN, null)).toString());
								}
								else if (previousOp == null || EmployeeOperationName.BreakOut.equals(previousOp))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN)).toString());
								}
								break;
							}
							}

						}
					}

				}
				TimezoneTime time = new TimezoneTime();
				String clockInTime = null;
				String clockOutTime = null;

				LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, employeeToEmployeeOperationPacket.getLocationId());

				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					EmployeeOperation employeeOperation2 = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
					if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && employeeOperation2.getOperationName().equals("Clock In"))
					{
						clockInTime = employeeOperation.getUpdatedStr();
					}
					else if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && employeeOperation2.getOperationName().equals("Clock Out"))
					{
						clockOutTime = employeeOperation.getUpdatedStr();

					}

				}

				// login Status for user

				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					user = (User) new CommonMethods().getObjectById("User", em,User.class, employeeOperation.getUsersId());

					EmployeeOperation employeeOperation2 = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
					if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && locationSetting.getIsClockinValidation() == 1)
					{
						int loginStatus = 0;
						if (employeeOperation2.getOperationName().equals("Clock In"))
						{
							loginStatus = 1;
							user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
						}

					}
				}

				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					user = (User) new CommonMethods().getObjectById("User", em,User.class, employeeOperation.getUsersId());
					EmployeeOperation employeeOperation2 = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
					if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && locationSetting.getIsClockinValidation() == 1)
					{
						int loginStatus = 0;

						if (employeeOperation2.getOperationName().equals("Break Out"))
						{
							user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
						}
					}
				}

				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					user = (User) new CommonMethods().getObjectById("User", em,User.class, employeeOperation.getUsersId());
					EmployeeOperation employeeOperation2 = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
					if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && locationSetting.getIsClockinValidation() == 1)
					{
						int loginStatus = 0;
						if (employeeOperation2.getOperationName().equals("Break In"))
						{
							loginStatus = 1;
							user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
						}

					}
				}

				for (EmployeesToEmployeesOperation employeeOperation : employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation())
				{

					user = (User) new CommonMethods().getObjectById("User", em,User.class, employeeOperation.getUsersId());
					EmployeeOperation employeeOperation2 = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getEmployeeOperationId());
					if (employeeOperation2 != null && employeeOperation2.getOperationName() != null && locationSetting.getIsClockinValidation() == 1)
					{
						int loginStatus = 0;

						if (employeeOperation2.getOperationName().equals("Clock Out"))
						{
							user = EmployeeOperationHelper.updateUserForLogin(user, loginStatus, em);
						}
					}
				}

				if (clockInTime != null)
				{

					clockInTime = time.getDateAccordingToGMT(clockInTime, employeeToEmployeeOperationPacket.getLocationId(), em);
					if (clockOutTime == null)
					{
						clockOutTime = new Timestamp(new TimezoneTime().getGMTTimeInMilis()).toString();
					}
					else
					{
						clockOutTime = time.getDateAccordingToGMT(clockOutTime, employeeToEmployeeOperationPacket.getLocationId(), em);
					}

					OrderManagementServiceBean bean = new OrderManagementServiceBean();
					List<BatchDetail> batchDetailList = bean.getBatchForClockInClockOut(httpRequest, em, employeeToEmployeeOperationPacket.getLocationId(), clockInTime,
							clockOutTime);

					for (BatchDetail batchDetail : batchDetailList)
					{

							if (batchDetail.getIsTipCalculated().equals("F"))
							{
								return (new NirvanaXPException(
										new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_TIP_CALCULATED_DONE, MessageConstants.ERROR_MESSAGE_TIP_CALCULATED_DONE, null)).toString());

							}

					}
				}

			}
			catch (Exception e)
			{
				// TODO: handle exception
				logger.severe(e);
			}

			List<EmployeesToEmployeesOperation> employeesToEmployeesOperation = EmployeeOperationHelper.updateEmployeeToEmployeeOperation(em,
					employeeToEmployeeOperationPacket.getEmployeesToEmployeesOperation(), employeeToEmployeeOperationPacket.getLocationId());
			employeeToEmployeeOperationPacket.setEmployeesToEmployeesOperation(employeesToEmployeesOperation);

			if (user != null)
			{
				employeeToEmployeeOperationPacket.setUser(user);
			}

			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updateEmployeeOperation.name(), employeeToEmployeeOperationPacket);

			String json = new StoreForwardUtility().returnJsonPacket(employeeToEmployeeOperationPacket, "EmployeeToEmployeeOperationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					employeeToEmployeeOperationPacket.getLocationId(), Integer.parseInt(employeeToEmployeeOperationPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(employeeToEmployeeOperationPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the employee to employee operation get packet.
	 *
	 * @param businessId
	 *            the business id
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @param sessionId
	 *            the session id
	 * @return the employee to employee operation get packet
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getEmployeeToEmployeeOperationGetPacket/{businessId}/{fromDate}/{toDate}")
	public String getEmployeeToEmployeeOperationGetPacket(@PathParam("businessId") int businessId, @PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			toDate = toDate + " 23:59:59";
			fromDate = fromDate + " 00:00:00";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_employee_operation(?,?,?)").setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(new EmployeeToEmployeeOperationGetPacket().setEmployeeToEmployeeOperationGetPacket(resultList));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the clock in clock out get packet.
	 *
	 * @param businessId
	 *            the business id
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @param sessionId
	 *            the session id
	 * @return the clock in clock out get packet
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getClockInClockOutGetPacket/{businessId}/{fromDate}/{toDate}")
	public String getClockInClockOutGetPacket(@PathParam("businessId") int businessId, @PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			toDate = toDate + " 23:59:59";
			fromDate = fromDate + " 00:00:00";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call sp_clockInclockOut(?,?,?)").setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(new ClockInClockOutGetPacket().setEmployeeToEmployeeOperationGetPacket(resultList));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update employee operation for admin.
	 *
	 * @param employeeOperationPacket
	 *            the employee operation packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateEmployeeOperationForAdmin")
	public String updateEmployeeOperationForAdmin(EmployeeOperationPacket employeeOperationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, employeeOperationPacket);
			EmployeeOperationHelper.updateEmployeeOperation(em, employeeOperationPacket.getEmployeeOperation());
			employeeOperationPacket.setEmployeeOperation(employeeOperationPacket.getEmployeeOperation());
			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updateEmployeeOperation.name(), employeeOperationPacket);
			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updateEmployeeOperationToAlertMessage.name(), employeeOperationPacket);
			
			String json = new StoreForwardUtility().returnJsonPacket(employeeOperationPacket, "EmployeeOperationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					employeeOperationPacket.getLocationId(), Integer.parseInt(employeeOperationPacket.getMerchantId()));
		
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperationPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete employee operation to alert message.
	 *
	 * @param employeeOperationPacket
	 *            the employee operation packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteEmployeeOperationToAlertMessage")
	public String deleteEmployeeOperationToAlertMessage(EmployeeOperationPacket employeeOperationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, employeeOperationPacket);

			for (EmployeeOperationToAlertMessage e : employeeOperationPacket.getEmployeeOperation().getEmployeeOperationToAlertMessage())
			{

				e = em.find(EmployeeOperationToAlertMessage.class, e.getId());
				e.setStatus("D");
				e.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				LocalSchemaEntityManager.merge(em, e);
				// TODO - don't break loop if merge has some error
			}

			employeeOperationPacket.setEmployeeOperation(employeeOperationPacket.getEmployeeOperation());
			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updateEmployeeOperationToAlertMessage.name(), employeeOperationPacket);
			return new JSONUtility(httpRequest).convertToJsonString(employeeOperationPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the break and clock in out get packet.
	 *
	 * @param businessId
	 *            the business id
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @param usersId
	 *            the users id
	 * @param sessionId
	 *            the session id
	 * @return the break and clock in out get packet
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getBreakAndClockInOutGetPacket/{businessId}/{fromDate}/{toDate}/{usersId}")
	public String getBreakAndClockInOutGetPacket(@PathParam("businessId") int businessId, @PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate,
			@PathParam("usersId") String usersId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			usersId = usersId.replace("-", ",");
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call sp_BreakInBreakOut(?,?,?,?)").setParameter(1, businessId).setParameter(2, fromDate).setParameter(3, toDate).setParameter(4, usersId)
					.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(new BreakAndClockInOutGetPacket().setBreakAndClockInOutGetPacket(resultList));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the users to payment history by location id and date batch wise.
	 *
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @param sessionId
	 *            the session id
	 * @return the users to payment history by location id and date batch wise
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getUsersToPaymentHistoryByLocationIdAndDateBatchWise/{locationId}/{date}")
	public String getUsersToPaymentHistoryByLocationIdAndDateBatchWise(@PathParam("locationId") String locationId, @PathParam("date") String date,
			@CookieParam(NIRVANA_ACCESS_TOKEN_HEADER_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			EmployeeOperationHelper employeeOperationHelper = new EmployeeOperationHelper();
			List<UsersToPaymentHistory> usersToPaymentHistories = employeeOperationHelper.getUsersToPaymentHistoryByLocationId(httpRequest, em, locationId, date, true, logger);
			return new JSONUtility(httpRequest).convertToJsonString(usersToPaymentHistories);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the users to payment history by location id and date.
	 *
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @param sessionId
	 *            the session id
	 * @return the users to payment history by location id and date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getUsersToPaymentHistoryByLocationIdAndDate/{locationId}/{date}")
	public String getUsersToPaymentHistoryByLocationIdAndDate(@PathParam("locationId") String locationId, @PathParam("date") String date, @CookieParam(NIRVANA_ACCESS_TOKEN_HEADER_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			EmployeeOperationHelper employeeOperationHelper = new EmployeeOperationHelper();
			List<UsersToPaymentHistory> usersToPaymentHistories = employeeOperationHelper.getAllUsersToPaymentHistoryByLocationId(httpRequest, em, locationId, date, false, logger);
			return new JSONUtility(httpRequest).convertToJsonString(usersToPaymentHistories);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Check employee operation to cash register by register id and operation
	 * id.
	 *
	 * @param registerId
	 *            the register id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/checkEmployeeOperationToCashRegisterByRegisterId/{registerId}")
	public String checkEmployeeOperationToCashRegisterByRegisterIdAndOperationId(@PathParam("registerId") String registerId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			EmployeeOperationHelper employeeOperationHelper = new EmployeeOperationHelper();
			boolean result = employeeOperationHelper.checkEmployeeOperationToCashRegisterByRegisterIdAndOperationId(httpRequest, em, registerId, logger);
			return new JSONUtility(httpRequest).convertToJsonString(result);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	

	/**
	 * Adds the ClockInClockOut
	 *
	 * @param packet
	 *            the ClockInClockOut packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdateClockInClockOut")
	public String addUpdateClockInClockOut(ClockInClockOutPacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		boolean jobRolePush= false;
		boolean updateBreakInBreakOutPush= false;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			

			// validations
			ClockInClockOut clockInClockOutPacket = packet.getClockInClockOut();

			// Check Batch Fixed status validation
			String clockInTime = null;
			String clockOutTime = null;
			TimezoneTime time = new TimezoneTime();
			if (clockInClockOutPacket.getClockInOperationId() != null)
			{

				clockInTime = time.getDateAccordingToGMT(packet.getClockInClockOut().getClockInStr(), packet.getClockInClockOut().getLocationId(), em);
			}

			if (clockInClockOutPacket.getClockOutOperationId() != null)
			{
				clockOutTime = time.getDateAccordingToGMT(packet.getClockInClockOut().getClockOutStr(), packet.getClockInClockOut().getLocationId(), em);
			}

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

			if (clockInTime == null)
			{
				if (packet.getClockInClockOut().getId() > 0)
				{

					ClockInClockOut clockInClockOutDB = em.find(ClockInClockOut.class, packet.getClockInClockOut().getId());

					if (clockInClockOutDB != null && packet.getClockInClockOut().getClockInOperationId() != null)
					{
						calendar.setTimeInMillis(clockInClockOutDB.getClockIn().getTime());
						clockInTime = formatter.format(calendar.getTime());
					}

				}
				else
				{
					String queryString = "select l from ClockInClockOut l " + "where l.locationId=? and l.usersId=? order by l.id desc";

					TypedQuery<ClockInClockOut> query = em.createQuery(queryString, ClockInClockOut.class).setParameter(1, packet.getClockInClockOut().getLocationId()).setParameter(2,
							packet.getClockInClockOut().getUsersId());
					List<ClockInClockOut> clockInClockOutDB = query.getResultList();

					if (clockInClockOutDB != null && clockInClockOutDB.size() > 0)
					{
						calendar.setTimeInMillis(clockInClockOutDB.get(0).getClockIn().getTime());
						clockInTime = formatter.format(calendar.getTime());
						;

					}
				}

			}

			if (clockInTime != null && clockOutTime != null)
			{

				OrderManagementServiceBean bean = new OrderManagementServiceBean();
				List<BatchDetail> batchDetailList = bean.getBatchForClockInClockOut(httpRequest, em, packet.getClockInClockOut().getLocationId(), clockInTime, clockOutTime);

				for (BatchDetail batchDetail : batchDetailList)
				{

					if (batchDetail.getIsTipCalculated() != null && batchDetail.getIsTipCalculated().equals("F"))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_TIP_CALCULATED_DONE, MessageConstants.ERROR_MESSAGE_TIP_CALCULATED_DONE, null))
								.toString());
					}
				}
			}

			/// check already active clock in
			// clock in check
			if (packet.getIsUpdate() == 0)
			{
				if (clockInClockOutPacket.getClockInOperationId() !=null)
				{
					if (checkAlreadyClockIn(em, clockInClockOutPacket.getClockInOperationId(), clockInClockOutPacket.getUsersId(), clockInClockOutPacket.getLocationId()))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT, null)).toString());
					}
				} // for clock out6
				else if (clockInClockOutPacket.getClockOutOperationId() != null)
				{
					// get clockin id for location
					EmployeeOperation clockInEmployeeOperation = EmployeeOperationHelper.getEmployeeOperationByLocationIdAndName(em, clockInClockOutPacket.getLocationId(), "Clock In");
					if (!checkAlreadyClockIn(em, clockInEmployeeOperation.getId(), clockInClockOutPacket.getUsersId(), clockInClockOutPacket.getLocationId()))
					{
						return (new NirvanaXPException(
								new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN, MessageConstants.ERROR_MESSAGE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN, null))
										.toString());
					}
					else
					{
						ClockInClockOut alreadyClockInClockOutData = getAlreadyClockInClockOut(em, clockInEmployeeOperation.getId(), clockInClockOutPacket.getUsersId(),
								clockInClockOutPacket.getLocationId());
						if (checkBreakInByClockInId(em, clockInClockOutPacket.getLocationId(), clockInClockOutPacket.getUsersId(), alreadyClockInClockOutData.getId()))
						{
							return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN,
									MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN)).toString());
						}
					}

				}
			}
			if (packet.getIsUpdate() == 1)
			{
				// validation for clockout without breakin
				if (clockInClockOutPacket.getClockOutOperationId() !=null && clockInClockOutPacket.getId() > 0)
				{
					// get clockin id for location
					EmployeeOperation clockInEmployeeOperation = EmployeeOperationHelper.getEmployeeOperationByLocationIdAndName(em, clockInClockOutPacket.getLocationId(), "Clock In");
					if (!checkAlreadyClockIn(em, clockInEmployeeOperation.getId(), clockInClockOutPacket.getUsersId(), clockInClockOutPacket.getLocationId()))
					{
						// return (new NirvanaXPException(
						// new
						// NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN,
						// MessageConstants.ERROR_MESSAGE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN,
						// null))
						// .toString());
					}
					else
					{
						ClockInClockOut alreadyClockInClockOutData = getAlreadyClockInClockOut(em, clockInEmployeeOperation.getId(), clockInClockOutPacket.getUsersId(),
								clockInClockOutPacket.getLocationId());
						if (checkBreakInByClockInId(em, clockInClockOutPacket.getLocationId(), clockInClockOutPacket.getUsersId(), alreadyClockInClockOutData.getId()))
						{
							return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN,
									MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN)).toString());
						}
					}

				}
			}
			tx = em.getTransaction();
			tx.begin();
			User user = (User) new CommonMethods().getObjectById("User", em,User.class, packet.getClockInClockOut().getUsersId());
			List<EmployeeMasterToJobRoles> resultSetRolls = null;
			// if (locationSetting.getIsClockinValidation() == 1)
			{
				int loginStatus = 0;
				if (packet.getClockInClockOut().getClockInOperationId() != null)
				{
					// Clock In
					loginStatus = 1;
					user = EmployeeOperationHelper.updateUserForLoginWithoutTransaction(user, loginStatus, em);

					if(packet.getClockInClockOut().getClockOutOperationId()==null){
						// Add new Batch If clock In
						PaymentBatchManager batchManager = PaymentBatchManager.getInstance();

						batchManager.getCurrentBatchIdBySession(httpRequest, em, packet.getClockInClockOut().getLocationId(), true, packet,user.getId());
	
					}
					
					try
					{
						String queryStringRolls = "select b from EmployeeMasterToJobRoles b where b.status not in('D','I') and b.userId = ?" ;

						Query queryRolls = em.createQuery(queryStringRolls).setParameter(1, packet.getClockInClockOut().getUsersId());
						resultSetRolls = queryRolls.getResultList();

					}
					catch (Exception e)
					{
						logger.severe(e);
					}
				

					if (resultSetRolls != null && resultSetRolls.size() > 0)
					{
						for (EmployeeMasterToJobRoles employeeMasterToJobRoles : resultSetRolls)
						{

							if (employeeMasterToJobRoles.getJobRoleId().equals(packet.getClockInClockOut().getJobRoleId()))
							{
								employeeMasterToJobRoles.setIsDefaultRole(1);
							}
							else
							{
								employeeMasterToJobRoles.setIsDefaultRole(0);
							}

							employeeMasterToJobRoles.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							employeeMasterToJobRoles = em.merge(employeeMasterToJobRoles);
						}
						jobRolePush=true;
						

					}

				}

				if (packet.getClockInClockOut().getClockOutOperationId() != null)
				{
					loginStatus = 0;
					// Clock Out
					/*
					 * if(packet.getClockInClockOut().getId()>0){
					 * 
					 * ClockInClockOut clockInClockOutDB =
					 * em.find(ClockInClockOut.class,
					 * packet.getClockInClockOut().getId());
					 * 
					 * if (clockInClockOutDB != null &&
					 * packet.getClockInClockOut().getClockInOperationId() != 0)
					 * {
					 * 
					 * packet.getClockInClockOut().setId(clockInClockOutDB.getId
					 * ());
					 * packet.getClockInClockOut().setUsersId(clockInClockOutDB.
					 * getUsersId());
					 * packet.getClockInClockOut().setCreated(clockInClockOutDB.
					 * getCreated());
					 * packet.getClockInClockOut().setClockIn(clockInClockOutDB.
					 * getClockIn());
					 * packet.getClockInClockOut().setClockIn(clockInClockOutDB.
					 * getClockIn());
					 * packet.getClockInClockOut().setClockInOperationId(
					 * clockInClockOutDB.getClockInOperationId());
					 * packet.getClockInClockOut().setCreatedBy(
					 * clockInClockOutDB.getCreatedBy());
					 * //packet.getClockInClockOut().
					 * setJobRoleIdclockInClockOutDB.getJobRoleId());
					 * 
					 * 
					 * } }else
					 */ if (packet.getIsAddUpdateFromMetro() == 1)
					{
						String queryString = "select l from ClockInClockOut l " + "where l.locationId=? and l.usersId=? order by l.id desc";

						TypedQuery<ClockInClockOut> query = em.createQuery(queryString, ClockInClockOut.class).setParameter(1, packet.getClockInClockOut().getLocationId()).setParameter(2,
								packet.getClockInClockOut().getUsersId());
						List<ClockInClockOut> clockInClockOutDB = query.getResultList();

						if (clockInClockOutDB != null && clockInClockOutDB.size() > 0)
						{

							packet.getClockInClockOut().setId(clockInClockOutDB.get(0).getId());
							packet.getClockInClockOut().setUsersId(clockInClockOutDB.get(0).getUsersId());
							packet.getClockInClockOut().setCreated(clockInClockOutDB.get(0).getCreated());
							packet.getClockInClockOut().setClockIn(clockInClockOutDB.get(0).getClockIn());
							packet.getClockInClockOut().setClockInOperationId(clockInClockOutDB.get(0).getClockInOperationId());
							packet.getClockInClockOut().setCreatedBy(clockInClockOutDB.get(0).getCreatedBy());
							// packet.getClockInClockOut().setJobRoleId(clockInClockOutDB.get(0).getJobRoleId());

						}
					}

					user = EmployeeOperationHelper.updateUserForLoginWithoutTransaction(user, loginStatus, em);
				}
			}
			tx.commit();
			tx.begin();
			ClockInClockOut clockInClockOut = new EmployeeOperationHelper().addUpdateClockInClockOut(httpRequest, em, packet.getClockInClockOut());
			tx.commit();
			tx.begin();
			if (packet.getClockInClockOut().getBreakInBreakOuts() != null && packet.getClockInClockOut().getBreakInBreakOuts().size() > 0)
			{
				for (BreakInBreakOut breakInBreakOut : packet.getClockInClockOut().getBreakInBreakOuts())
				{
					int loginStatus = 0;
					try
					{
						BreakInBreakOut breakInBreakOutPacket = breakInBreakOut;
						EmployeeOperation clockInEmployeeOperation = EmployeeOperationHelper.getEmployeeOperationByLocationIdAndName(em, breakInBreakOutPacket.getLocationId(), "Clock In");
						// user didnt clockin and doing break out
						// validation for breakout
						if (!breakInBreakOut.getStatus().equals("D"))
						{
							if (breakInBreakOutPacket.getBreakOutOperationId() !=null && breakInBreakOutPacket.getBreakInOperationId() == null)
							{

								if (checkAlreadyBreakOut(em, breakInBreakOutPacket.getBreakOutOperationId(), breakInBreakOutPacket.getUsersId(), breakInBreakOutPacket.getLocationId()))
								{
									return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN,
											MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN, null)).toString());
								}

							}
							else if (breakInBreakOutPacket.getBreakInOperationId() != null && breakInBreakOutPacket.getId() == 0)
							{

								// EmployeeOperation breakOutEmployeeOperation =
								// EmployeeOperationHelper.getEmployeeOperationByLocationIdAndName(em,
								// breakInBreakOutPacket.getLocationId(), "Break
								// Out");
								// if (!checkAlreadyBreakOut(em,
								// breakOutEmployeeOperation.getId(),
								// breakInBreakOutPacket.getUsersId(),
								// breakInBreakOutPacket.getLocationId()))
								// {
								// return (new NirvanaXPException(new
								// NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								// MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								// MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT)).toString());
								// }
							}

							if (breakInBreakOutPacket.getBreakOutOperationId() != null)
							{

								loginStatus = 0;
							}

							if (breakInBreakOutPacket.getBreakInOperationId() != null)
							{
								loginStatus = 1;
							}

						}
						else
						{
							loginStatus = 0;
						}

						if (packet.getClockInClockOut().getClockOutOperationId() != null)
						{
							loginStatus = 0;
						}
						user = EmployeeOperationHelper.updateUserForLoginWithoutTransaction(user, loginStatus, em);

						breakInBreakOut.setClockInClockOutId(clockInClockOut.getId());
						breakInBreakOut = new EmployeeOperationHelper().addUpdateBreakInBreakOut(httpRequest, em, breakInBreakOut);
						updateBreakInBreakOutPush=true;
					}
					catch (RuntimeException e)
					{
						if (tx != null && tx.isActive())
						{
							tx.rollback();
						}
						throw e;
					}
					catch (Exception e)
					{
						logger.severe(e);
					}

				}

				
			}
			tx.commit();
			clockInClockOut.setUser(user);
			if (resultSetRolls != null && resultSetRolls.size() > 0)
			{
				clockInClockOut.setEmployeeMasterToJobRoles(resultSetRolls);
			}

			packet.getClockInClockOut().setUser(user);
			if(jobRolePush){
				sendPacketForBroadcast(POSNServiceOperations.LookupService_addEmployeeMasterToJobRoles.name(), packet);
			}
			if(updateBreakInBreakOutPush){
				sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updateBreakInBreakOut.name(), packet);
			}
			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updateClockInClockOut.name(), packet);
			
			return new JSONUtility(httpRequest).convertToJsonString(packet);

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

	
	@POST
	@Path("/deleteClockInClockOut")
	public String deleteClockInClockOut(ClockInClockOutPacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx=null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx=em.getTransaction();
			tx.begin();
			ClockInClockOut clockInClockOut = em.find(ClockInClockOut.class, packet.getClockInClockOut().getId());
			String queryString1 ;
			if(clockInClockOut.getClockOutOperationId()!= null){
				 queryString1 = "select count(*) from order_header where preassigned_server_id=? and open_time between '" + clockInClockOut.getClockIn() + "' and '" + clockInClockOut.getClockOut() + "'";
				
			}else {
				 queryString1 = "select count(*) from order_header where preassigned_server_id=? and open_time between '" + clockInClockOut.getClockIn() + "' and now() ";
					
			}
				
				Query query1 = em.createNativeQuery(queryString1).setParameter(1, clockInClockOut.getUsersId());
				BigInteger bigInteger = (BigInteger) query1.getSingleResult();
				int count=bigInteger.intValue();

				if(count>0 ){
					if(clockInClockOut.getClockOutOperationId()!=null){
					throw (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.DELETE_OPTION_NOT_ALLOWED,
							 MessageConstants.DELETE_OPTION_NOT_ALLOWED , null)));
				}else {
					throw (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ONLY_CLOCKOUT_OPERATION_ALLOWED,
							 MessageConstants.ONLY_CLOCKOUT_OPERATION_ALLOWED , null)));
				}
				}
			clockInClockOut.setUpdatedBy(packet.getClockInClockOut().getUpdatedBy());
			clockInClockOut.setStatus(packet.getClockInClockOut().getStatus());
			clockInClockOut.setUpdated(new  Date(new TimezoneTime().getGMTTimeInMilis()));
			clockInClockOut = em.merge(clockInClockOut);
			
			User user = (User) new CommonMethods().getObjectById("User", em,User.class, packet.getClockInClockOut().getUsersId());
			ClockInClockOut lastClockInClockOutDB = null;
			try
			{
				String queryString = "select l from ClockInClockOut l " + "where l.locationId=? and l.usersId=? order by l.id desc";

				TypedQuery<ClockInClockOut> query = em.createQuery(queryString, ClockInClockOut.class).setParameter(1, packet.getClockInClockOut().getLocationId()).setParameter(2,
						packet.getClockInClockOut().getUsersId());
				lastClockInClockOutDB = query.getResultList().get(0);

				if (lastClockInClockOutDB != null 
						&& clockInClockOut.getStatus().equals("D")
						&& clockInClockOut.getId() == lastClockInClockOutDB.getId())
				{
					
					int loginStatus = 0;
					user = EmployeeOperationHelper.updateUserForLoginWithoutTransaction(user, loginStatus, em);
				}
			}catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			} 
			catch (Exception e)
			{
				logger.severe(e);
			}
			
			
			BreakInBreakOut lastBreakInBreakOutDB = null;
			try
			{
				//Last Record of break in break out
				//break Out
				String queryString = "select l from BreakInBreakOut l "
						+ "where l.locationId= "+ packet.getClockInClockOut().getLocationId() 
						+" and l.usersId= "+ packet.getClockInClockOut().getUsersId()
						+" order by l.id desc";

				TypedQuery<BreakInBreakOut> query = em.createQuery(queryString, BreakInBreakOut.class);

				lastBreakInBreakOutDB = query.getResultList().get(0);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				logger.severe(e);
			}
			
			
			
			List<BreakInBreakOut> list = new ArrayList<BreakInBreakOut>();
			if(packet.getClockInClockOut().getBreakInBreakOuts() != null && packet.getClockInClockOut().getBreakInBreakOuts().size() > 0)
			{
				for(BreakInBreakOut breakInBreakOut:packet.getClockInClockOut().getBreakInBreakOuts())
				{
						BreakInBreakOut  breakInBreakOutDB = em.find(BreakInBreakOut.class, breakInBreakOut.getId());
						breakInBreakOutDB.setUpdatedBy(breakInBreakOutDB.getUpdatedBy());
						breakInBreakOutDB.setStatus(breakInBreakOut.getStatus());
						breakInBreakOutDB.setUpdated(new  Date(new TimezoneTime().getGMTTimeInMilis()));
						breakInBreakOutDB = em.merge(breakInBreakOutDB);
						list.add(breakInBreakOutDB);
						
						
						if (lastBreakInBreakOutDB != null && 
								lastBreakInBreakOutDB.getClockInClockOutId() == clockInClockOut.getId()
								&& breakInBreakOutDB.getId() == lastBreakInBreakOutDB.getId())
						{
						
							if(clockInClockOut.getStatus().equals("U"))
							{
								int loginStatus = 0;
								if (clockInClockOut.getClockInOperationId() != null)
								{
									loginStatus = 1;
								}
								if (clockInClockOut.getClockOutOperationId() != null)
								{
									loginStatus = 0;
								}
								user = EmployeeOperationHelper.updateUserForLoginWithoutTransaction(user, loginStatus, em);
								
								
							}else
							{
								int loginStatus = 0;
								/*if(lastClockInClockOutDB != null)
								{
								
									if(lastClockInClockOutDB.getClockInOperationId() != 0)
									{
										loginStatus = 1;
									}
									if(lastClockInClockOutDB.getClockOutOperationId() != 0)
									{
										loginStatus = 0;
									}
								}*/
								
								user = EmployeeOperationHelper.updateUserForLoginWithoutTransaction(user, loginStatus, em);
							}
							
						}
						
						
						new InsertIntoHistory().insertBreakInBreakOutHistory(httpRequest, breakInBreakOutDB, em);
						
						
				}
				sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updateBreakInBreakOut.name(), packet);
			}
			
			
			
			if(user != null)
			{
				clockInClockOut.setUser(user);
				packet.getClockInClockOut().setUser(user);
			}
			
			new InsertIntoHistory().insertClockInClockOutHistory(httpRequest, clockInClockOut, em);
			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updateClockInClockOut.name(), packet);
			clockInClockOut.setBreakInBreakOuts(list);
			tx.commit();
			packet.setClockInClockOut(clockInClockOut);
			return new JSONUtility(httpRequest).convertToJsonString(packet);

		}catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} 
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the ClockInClockOut
	 *
	 * @param packet
	 *            the ClockInClockOut packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdateBreakInBreakOut")
	public String addBreakInBreakOut(BreakInBreakOutPacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx =null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx=em.getTransaction();
			if (packet.getIsUpdate() == 0)
			{
				// validation
				// user cant break out without clockin
				BreakInBreakOut breakInBreakOutPacket = packet.getBreakInBreakOut();
				EmployeeOperation clockInEmployeeOperation = EmployeeOperationHelper.getEmployeeOperationByLocationIdAndName(em, breakInBreakOutPacket.getLocationId(), "Clock In");
				// user didnt clockin and doing break out
				// validation for breakout
				if (breakInBreakOutPacket.getBreakOutOperationId() != null)
				{
					if (!checkAlreadyClockIn(em, clockInEmployeeOperation.getId(), breakInBreakOutPacket.getUsersId(), breakInBreakOutPacket.getLocationId()))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN)).toString());
					}
					if (checkAlreadyBreakOut(em, breakInBreakOutPacket.getBreakOutOperationId(), breakInBreakOutPacket.getUsersId(), breakInBreakOutPacket.getLocationId()))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN, null)).toString());
					}
				}
				else if (breakInBreakOutPacket.getBreakInOperationId() !=  null)
				{
					if (!checkAlreadyClockIn(em, clockInEmployeeOperation.getId(), breakInBreakOutPacket.getUsersId(), breakInBreakOutPacket.getLocationId()))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_CLOCK_IN,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_CLOCK_IN, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_CLOCK_IN)).toString());
					}
					EmployeeOperation breakOutEmployeeOperation = EmployeeOperationHelper.getEmployeeOperationByLocationIdAndName(em, breakInBreakOutPacket.getLocationId(), "Break Out");
					if (!checkAlreadyBreakOut(em, breakOutEmployeeOperation.getId(), breakInBreakOutPacket.getUsersId(), breakInBreakOutPacket.getLocationId()))
					{
						return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT,
								MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT, MessageConstants.ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT)).toString());
					}
				}
			}
			tx.begin();
			User user = (User) new CommonMethods().getObjectById("User", em,User.class, packet.getBreakInBreakOut().getUsersId());
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, packet.getBreakInBreakOut().getLocationId());

			//if (locationSetting.getIsClockinValidation() == 1)
			{
				int loginStatus = 0;
				if (packet.getBreakInBreakOut().getBreakInOperationId() != null)
				{
					// break In
					loginStatus = 1;
					user = EmployeeOperationHelper.updateUserForLoginWithoutTransaction(user, loginStatus, em);
 
					
					//break Out
					String queryString = "select l from BreakInBreakOut l "
							+ "where l.locationId= '"+ packet.getBreakInBreakOut().getLocationId() +"' and l.usersId= '"+ packet.getBreakInBreakOut().getUsersId()+"' order by l.id desc";
  
					TypedQuery<BreakInBreakOut> query = em.createQuery(queryString, BreakInBreakOut.class);
 
					List<BreakInBreakOut> breakInBreakOutDB = query.getResultList();

					if (breakInBreakOutDB != null && breakInBreakOutDB.size() > 0)
					{

						packet.getBreakInBreakOut().setId(breakInBreakOutDB.get(0).getId());
						packet.getBreakInBreakOut().setUsersId(breakInBreakOutDB.get(0).getUsersId());
						packet.getBreakInBreakOut().setCreated(breakInBreakOutDB.get(0).getCreated());
						packet.getBreakInBreakOut().setBreakOut(breakInBreakOutDB.get(0).getBreakOut());
						packet.getBreakInBreakOut().setBreakOutOperationId(breakInBreakOutDB.get(0).getBreakOutOperationId());
						packet.getBreakInBreakOut().setCreatedBy(breakInBreakOutDB.get(0).getCreatedBy());
						packet.getBreakInBreakOut().setJobRoleId(breakInBreakOutDB.get(0).getJobRoleId());
						packet.getBreakInBreakOut().setStatus(breakInBreakOutDB.get(0).getStatus());

					}

				}
				else if (packet.getBreakInBreakOut().getBreakOutOperationId() != null)
				{

					user = EmployeeOperationHelper.updateUserForLoginWithoutTransaction(user, loginStatus, em);
				}
			}

			String queryString = "select l from ClockInClockOut l " + "where l.locationId=? and l.usersId=? order by l.id desc";

			TypedQuery<ClockInClockOut> query = em.createQuery(queryString, ClockInClockOut.class).setParameter(1, packet.getBreakInBreakOut().getLocationId()).setParameter(2,
					packet.getBreakInBreakOut().getUsersId());
			List<ClockInClockOut> clockInClockOutDB = query.getResultList();

			if (clockInClockOutDB != null && clockInClockOutDB.size() > 0)
			{

				packet.getBreakInBreakOut().setClockInClockOutId(clockInClockOutDB.get(0).getId());

			}

			

			BreakInBreakOut breakInBreakOut = new EmployeeOperationHelper().addUpdateBreakInBreakOut(httpRequest, em, packet.getBreakInBreakOut());
			tx.commit();
			
			packet.setBreakInBreakOut(breakInBreakOut);

			breakInBreakOut.setUser(user);

			sendPacketForBroadcast(POSNServiceOperations.EmployeeManagementService_updateBreakInBreakOut.name(), packet);

			return new JSONUtility(httpRequest).convertToJsonString(packet);

		}catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/getClockInClockOutBreakInBreakOut")
	public String getClockInClockOutBreakInBreakOut(ClockInClockOutBreakInBreakOutPacket packet) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			packet.setToDate(packet.getToDate() + " 23:59:59");
			packet.setFromDate(packet.getFromDate() + " 00:00:00");
			TimezoneTime time = new TimezoneTime();
			String gmtFromTime = time.getDateAccordingToGMT(packet.getFromDate(), packet.getLocationsId(), em);
			String gmtToTime = time.getDateAccordingToGMT(packet.getToDate(), packet.getLocationsId(), em);
		  
			List<ClockInClockOut> clockInClockOuts = new ArrayList<ClockInClockOut>();

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			String append = "";
			if (packet.getEmployeeIds() != null && !packet.getEmployeeIds().equals("0"))
			{
				append = " and users_id in (" + packet.getEmployeeIds() + ")";
			}

			String queryString = "select id from clock_in_clock_out where status !='D' and location_id=? " + " and clock_in between '" +gmtFromTime + "' and  '" + gmtToTime + "'  " ;
			List<Object> result = em.createNativeQuery(queryString).setParameter(1, packet.getLocationsId()).getResultList();
			for (Object object : result)
			{
				ClockInClockOut clockInClockOut = em.find(ClockInClockOut.class, (int) object);
				List<BreakInBreakOut> resultset = null;
				String queryStringBreakInBreakOut = "select b from BreakInBreakOut b " + "where b.status !='D' and  b.locationId= ? and b.clockInClockOutId = ?";

				Query query = em.createQuery(queryStringBreakInBreakOut).setParameter(1, packet.getLocationsId()).setParameter(2, clockInClockOut.getId());
				resultset = query.getResultList();

				 

				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date updated = null;

				if (resultset != null)
				{
					for (BreakInBreakOut breakInBreakOut : resultset)
					{
						if (breakInBreakOut.getBreakIn() != null)
						{
							updated = formatter.parse(time.getDateTimeFromGMTToLocation(em, breakInBreakOut.getBreakIn().toString(), breakInBreakOut.getLocationId()));
							//For ios we convert date to string
							breakInBreakOut.setBreakInStr(time.getDateFromTimeStamp(new Timestamp(updated.getTime())));
							//breakInBreakOut.setBreakIn(updated);
						}

						if (breakInBreakOut.getBreakOut() != null)
						{
							updated = formatter.parse(time.getDateTimeFromGMTToLocation(em, breakInBreakOut.getBreakOut().toString(), breakInBreakOut.getLocationId()));
							//For ios we convert date to string
							breakInBreakOut.setBreakOutStr(time.getDateFromTimeStamp(new Timestamp(updated.getTime())));
							//breakInBreakOut.setBreakOut(updated);
						}
						
						if(breakInBreakOut.getBreakIn() != null && breakInBreakOut.getBreakOut() != null)
						{
							long diff = breakInBreakOut.getBreakIn().getTime() - breakInBreakOut.getBreakOut().getTime();
							 long diffMinutes = (diff / 1000) / 60;
							breakInBreakOut.setBreakInBreakOutMinutes(new BigDecimal(diffMinutes));
						}
					}
					clockInClockOut.setBreakInBreakOuts(resultset);

				}

				User user = (User) new CommonMethods().getObjectById("User", em,User.class, clockInClockOut.getUsersId());
				clockInClockOut.setFirstName(user.getFirstName());
				clockInClockOut.setLastName(user.getLastName());

				if (clockInClockOut.getClockIn() != null)
				{
					updated = formatter.parse(time.getDateTimeFromGMTToLocation(em, clockInClockOut.getClockIn().toString(), clockInClockOut.getLocationId()));
					//For ios we convert date to string
					clockInClockOut.setClockInStr(time.getDateFromTimeStamp(new Timestamp(updated.getTime())));
					//clockInClockOut.setClockIn(updated);
				}

				if (clockInClockOut.getClockOut() != null)
				{
					updated = formatter.parse(time.getDateTimeFromGMTToLocation(em, clockInClockOut.getClockOut().toString(), clockInClockOut.getLocationId()));
					//For ios we convert date to string
					clockInClockOut.setClockOutStr(time.getDateFromTimeStamp(new Timestamp(updated.getTime())));
					//clockInClockOut.setClockOut(updated);
				}
  
				//Set Shift id and hours
				try
				{
					String queryString1 = "select oss.shift_name, "
							+ " oss.id from clock_in_clock_out clck "
							+ " join operational_shift_schedule oss "
							+ " where clck.status !='D'   and  clck.location_id = ? "
							+ " and clck.id in (?) and oss.status !='D' "
							+ " and cast(date_format(ADDTIME(clck.clock_in,REPLACE(clck.local_time,'+','')),'%Y-%m-%d %H:%i:%s') as time ) "
							+ " between oss.from_time and oss.to_time ";
					
					Object[] result1 = (Object[]) em.createNativeQuery(queryString1).setParameter(1, packet.getLocationsId())
							.setParameter(2, clockInClockOut.getId()).getSingleResult();
					
					if(result1 != null)
					{
						if(result1[0] != null)
						{
							clockInClockOut.setShiftName( (String)result1[0]);	
						}
						
					}
				}
				catch (Exception e)
				{
					logger.severe(e);
				}
				
				if(clockInClockOut.getClockIn() != null && clockInClockOut.getClockOut() != null)
				{
					long diff = clockInClockOut.getClockOut().getTime() - clockInClockOut.getClockIn().getTime();
					 long diffMinutes = (diff / 1000) / 60;
					clockInClockOut.setClockInClockOutMinutes(new BigDecimal(diffMinutes));
				}
						
				
				
				clockInClockOuts.add(clockInClockOut);
			}

			return new JSONUtility(httpRequest).convertToJsonString(clockInClockOuts);

		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	public boolean checkAlreadyClockIn(EntityManager em, String clockInOperationId, String userId, String locationId) throws Exception
	{
		try
		{
			String queryString = " select id from clock_in_clock_out where status !='D' and  clock_in_operation_id=? and clock_out_operation_id=0 and users_id=?   and location_id=?  ";
			List<Object> result = em.createNativeQuery(queryString).setParameter(1, clockInOperationId).setParameter(2, userId).setParameter(3, locationId).getResultList();
			if (result.size() > 0)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		return false;
	}

	public ClockInClockOut getAlreadyClockInClockOut(EntityManager em, String clockInOperationId, String userId, String locationId) throws Exception
	{
		try
		{
			String queryString = " select id from clock_in_clock_out where status !='D' and  clock_in_operation_id=? and clock_out_operation_id=0 and users_id=? and   location_id=?  ";
			Object result = em.createNativeQuery(queryString).setParameter(1, clockInOperationId).setParameter(2, userId).setParameter(3, locationId).getSingleResult();
			return em.find(ClockInClockOut.class, (int) result);
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		return null;
	}

	public boolean checkAlreadyClockOut(EntityManager em, String locationId, String userId) throws Exception
	{
		try
		{
			String queryString = " select id from clock_in_clock_out where status !='D' and   clock_out_operation_id=0 and users_id=? and location_id=?  ";
			List<Object> result = em.createNativeQuery(queryString).setParameter(1, userId).setParameter(2, locationId).getResultList();
			if (result.size() > 0)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		return false;
	}

	public boolean checkBreakInByClockInId(EntityManager em, String locationId, String userId, int clockInOutId) throws Exception
	{
		try
		{
			String queryString = " select id from break_in_break_out where status !='D' and clock_in_clock_out_id=? and break_in_operation_id=0 and users_id=? and location_id=? ";
			List<Object> result = em.createNativeQuery(queryString).setParameter(1, clockInOutId).setParameter(2, userId).setParameter(3, locationId).getResultList();
			if (result.size() > 0)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		return false;
	}

	public boolean checkAlreadyBreakOut(EntityManager em, String breakOutOperationId, String userId, String locationId) throws Exception
	{
		try
		{
			String queryString = "select id from break_in_break_out where status !='D' and break_out_operation_id=? and break_in_operation_id=0 and users_id=?   and location_id=?  ";
			List<Object> result = em.createNativeQuery(queryString).setParameter(1, breakOutOperationId).setParameter(2, userId).setParameter(3, locationId).getResultList();
			if (result.size() > 0)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		return false;
	}
	@GET
	@Path("/checkBreakInBreakOutOverlapping/{breakIn}/{breakOut}/{userId}/{locationId}/{id}")
	public String checkBreakInBreakOutOverlapping(@PathParam("breakIn") String breakIn,@PathParam("breakOut") String breakOut,
			@PathParam("userId") String userId,@PathParam("locationId") String locationId,@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		try
		{
		 
			
			if(breakOut!=null && !(breakOut.equals("null"))){
				 boolean breakOutResponse = checkBreakInBreakOutOverlappingForAdmin(breakOut, userId, locationId,id);
				 if(breakOutResponse){
					 return (new NirvanaXPException(new NirvanaServiceErrorResponse("EOM1009",
								"Break Out time is already register for this date and time.","Break Out time is already register for this date and time."))).toString();
				 }
			}
			if(breakIn!=null && !(breakIn.equals("null"))){
				 boolean breakInResponse = checkBreakInBreakOutOverlappingForAdmin(breakIn, userId, locationId,id);
				 if(breakInResponse){
					 return (new NirvanaXPException(new NirvanaServiceErrorResponse("EOM1009",
								"Break In time is already register for this date and time.","Break Out time is already register for this date and time."))).toString();
				 }
				
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		} 

		return false+"";
	}
	@GET
	@Path("/checkBreakInBreakOutOverlappingForAdmin/{dateTime}/{userId}/{locationId}/{id}")
	public boolean checkBreakInBreakOutOverlappingForAdmin(@PathParam("dateTime") String dateTime,
			@PathParam("userId") String userId,@PathParam("locationId") String locationId,@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
	 
			String data= "";
			if(id>0){
				data = " and id != "+id;
			}
			TimezoneTime time = new TimezoneTime();
			dateTime =time.getDateAccordingToGMT(dateTime, locationId,em);
			String queryString = "select id from break_in_break_out where users_id =? and status!='D' and  break_out<= ? and  ifnull(break_in ,now()) >=? "+data;
			List<Object> result = em.createNativeQuery(queryString).setParameter(1, userId).setParameter(2, dateTime).setParameter(3, dateTime).getResultList();
			if (result.size() > 0)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		}finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

		return false;
	}
	
	@GET
	@Path("/checkClockInClockOutOverlapping/{clockIn}/{clockOut}/{userId}/{locationId}/{id}")
	public String checkClockInClockOutOverlapping(@PathParam("clockIn") String clockIn,@PathParam("clockOut") String clockOut,
			@PathParam("userId") String userId,@PathParam("locationId") String locationId,@PathParam("id") int id) throws Exception
	{
		try
		{
		 
			
			if(clockIn!=null && !(clockIn.equals("null"))){
				 boolean outResponse = checkClockInClockOutOverlappingForAdmin(clockIn, userId, locationId,id);
				 if(outResponse){
					 return (new NirvanaXPException(new NirvanaServiceErrorResponse("EOM1009",
								"Clock Out time is already register for this date and time.","Clock Out time is already register for this date and time."))).toString();
				 }
				
			}
			if(clockOut!=null && !(clockOut.equals("null"))){
				 boolean inResponse = checkClockInClockOutOverlappingForAdmin(clockOut, userId, locationId,id);
				 if(inResponse){
					 return (new NirvanaXPException(new NirvanaServiceErrorResponse("EOM1009",
								"Clock In time is already register for this date and time.","Clock Out time is already register for this date and time."))).toString();
				 }
				 boolean checkBreakInOutOverlapping =checkBreakInBreakOutOverlappingForAdmin(clockOut, userId, locationId, id);
				 if(checkBreakInOutOverlapping){
					 return (new NirvanaXPException(new NirvanaServiceErrorResponse("EOM1009",
								"Clock out time is overlapping break in - break out for this date and time.","Clock out time is overlapping break in - break out for this date and time."))).toString();
				 }
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		} 

		return false+"";
	}
	@GET
	@Path("/checkClockInClockOutOverlappingForAdmin/{dateTime}/{userId}/{locationId}/{id}")
	public boolean checkClockInClockOutOverlappingForAdmin(@PathParam("dateTime") String dateTime,
			@PathParam("userId") String userId,@PathParam("locationId") String locationId,@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
	 
			 
			TimezoneTime time = new TimezoneTime();
			dateTime =time.getDateAccordingToGMT(dateTime, locationId,em);
			String data= "";
			if(id>0){
				data = " and id != "+id;
			}
			String queryString = " select id from clock_in_clock_out where users_id =? and status!='D' and  clock_in<= ? and  ifnull( clock_out,now()) >=? "+data;
			List<Object> result = em.createNativeQuery(queryString).setParameter(1, userId).setParameter(2, dateTime).setParameter(3, dateTime).getResultList();
			if (result.size() > 0)
			{
				return true;
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		}finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

		return false;
	}
	
	@GET
	@Path("/getStaffMessagingByUserId/{id}/{locationsId}")
	public String getUsersToMessagingByUserId(@PathParam("id") String id,@PathParam("locationsId") String locationsId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<StaffMessaging> list = new EmployeeOperationHelper().getUsersToMessagingByUserId(em, id,locationsId);
			return new JSONUtility(httpRequest).convertToJsonString(list);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getStaffMessagingByLocationId/{locationsId}")
	public String getUsersToMessagingByLocationId(@PathParam("locationsId") String locationsId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<StaffMessaging> list = new EmployeeOperationHelper().getUsersToMessagingBylocationId(em, locationsId);
			return new JSONUtility(httpRequest).convertToJsonString(list);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getStaffMessagingById/{id}")
	public String getStaffMessagingById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			StaffMessaging list = em.find(StaffMessaging.class, id);
			return new JSONUtility(httpRequest).convertToJsonString(list);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@POST
	@Path("/addStaffMessaging")
	public String addStaffMessaging(StaffMessagingPacket staffMessagingPacket) throws FileNotFoundException, InvalidSessionException, IOException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			StaffMessagingPacket result = new EmployeeOperationHelper().addStaffMessaging(em, staffMessagingPacket);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(staffMessagingPacket, "StaffMessagingPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, staffMessagingPacket.getLocationId(), Integer.parseInt(staffMessagingPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
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
	@POST
	@Path("/updateStaffMessaging")
	public String updateStaffMessaging(StaffMessagingPacket staffMessagingPacket) throws FileNotFoundException, InvalidSessionException, IOException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			StaffMessagingPacket result = new EmployeeOperationHelper().updateStaffMessaging(em, staffMessagingPacket);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(staffMessagingPacket, "StaffMessagingPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, staffMessagingPacket.getLocationId(), Integer.parseInt(staffMessagingPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
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
	@POST
	@Path("/deleteStaffMessaging")
	public String deleteStaffMessaging(StaffMessagingPacket staffMessagingPacket) throws FileNotFoundException, InvalidSessionException, IOException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			StaffMessagingPacket result = new EmployeeOperationHelper().deleteStaffMessaging(em, staffMessagingPacket);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(staffMessagingPacket, "StaffMessagingPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, staffMessagingPacket.getLocationId(), Integer.parseInt(staffMessagingPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
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
	
	
}
