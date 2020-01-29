/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.constants.EmployeeOperationName;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.jaxrs.packets.StaffMessagingPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.employee.BreakInBreakOut;
import com.nirvanaxp.types.entities.employee.CashRegisterRunningBalance;
import com.nirvanaxp.types.entities.employee.ClockInClockOut;
import com.nirvanaxp.types.entities.employee.EmployeeOperation;
import com.nirvanaxp.types.entities.employee.EmployeeOperationToAlertMessage;
import com.nirvanaxp.types.entities.employee.EmployeeOperationToCashRegister;
import com.nirvanaxp.types.entities.employee.EmployeeOperationToCashRegisterHistory;
import com.nirvanaxp.types.entities.employee.EmployeeOperation_;
import com.nirvanaxp.types.entities.employee.EmployeesToEmployeesOperation;
import com.nirvanaxp.types.entities.employee.EmployeesToEmployeesOperationHistory;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.reasons.Reasons;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.user.StaffMessaging;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.types.entities.user.UsersToLocation_;
import com.nirvanaxp.types.entities.user.UsersToMessaging;
import com.nirvanaxp.types.entities.user.UsersToPaymentHistory;


// TODO: Auto-generated Javadoc
/**
 * This class is used for performing database operations related to 
 * Employee Operation.
 * @author Ankur
 *
 */
final class EmployeeOperationHelper
{
	
	/** The Constant ERROR_CODE_INSUFFICIENT_FUND. */
	public static final String ERROR_CODE_INSUFFICIENT_FUND = "EOM1005";
	
	/** The Constant ERROR_MESSAGE_INSUFFICIENT_FUND. */
	public static final String ERROR_MESSAGE_INSUFFICIENT_FUND = "Paid Out cannot be more than balance.";
	
	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(EmployeeOperationHelper.class.getName());
	
	/** The Constant SEQUENCE_CONTROLLED_OPERATION_NAME_LIST. */
	//This is a list of employee operation names that must be done in a specific sequence
	private static final List<String> SEQUENCE_CONTROLLED_OPERATION_NAME_LIST = Arrays.asList(new String[]
	{ EmployeeOperationName.BreakIn.getOperationName(), EmployeeOperationName.BreakOut.getOperationName(), EmployeeOperationName.ClockIn.getOperationName(),
			EmployeeOperationName.ClockOut.getOperationName() });

	/**
	 * This is used to add a new employee operation.
	 *
	 * @param em the em
	 * @param employeeOperation the employee operation
	 */
	static void addEmployeeOperation(EntityManager em, EmployeeOperation employeeOperation,HttpServletRequest httpRequest)
	{
		employeeOperation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperation.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		try {
			if(employeeOperation.getId()==null)
			employeeOperation.setId(new StoreForwardUtility().generateDynamicIntId(em, employeeOperation.getLocationsId(), httpRequest, "employee_operations"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.severe(e1);
		}

		LocalSchemaEntityManager.merge(em, employeeOperation);
		//TODO handle if persist has some error
		for(EmployeeOperationToAlertMessage e:employeeOperation.getEmployeeOperationToAlertMessage()){
			e.setEmployeeOperationId(employeeOperation.getId());
			e.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			LocalSchemaEntityManager.merge(em, e);
			//TODO -  don't break loop if merge has some error
		}
	}

	/**
	 * This is used to update an existing employee operation.
	 *
	 * @param em the em
	 * @param employeeOperation the employee operation
	 */
	static void updateEmployeeOperation(EntityManager em, EmployeeOperation employeeOperation)
	{
		employeeOperation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		LocalSchemaEntityManager.merge(em, employeeOperation);
		//TODO handle if merge has some error

		for(EmployeeOperationToAlertMessage e:employeeOperation.getEmployeeOperationToAlertMessage()){
			e.setEmployeeOperationId(employeeOperation.getId());
			e.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			LocalSchemaEntityManager.merge(em, e);
			//TODO -  don't break loop if merge has some error

		}
	}

	
	/**
	 * This is used to update an existing employee operation.
	 *
	 * @param em the em
	 * @param employeeOperations the employee operations
	 * @param locationId the location id
	 * @return the list
	 * @throws ParseException the parse exception
	 */
	static List<EmployeesToEmployeesOperation> updateEmployeeToEmployeeOperation(EntityManager em, List<EmployeesToEmployeesOperation> employeeOperations,String locationId) throws ParseException
	{
		
		TimezoneTime time = new TimezoneTime();
		
		List<EmployeesToEmployeesOperation> employeesToEmployeesOperations = new ArrayList<EmployeesToEmployeesOperation>();
		for(EmployeesToEmployeesOperation employeeOperation:employeeOperations){
			
			
			EmployeesToEmployeesOperation employeesToEmployeesOperation = em.find(EmployeesToEmployeesOperation.class, employeeOperation.getId());
			if(employeesToEmployeesOperation!=null){
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				// converting location to gmt time
				employeeOperation.setUpdatedStr(time.getDateAccordingToGMT(employeeOperation.getUpdatedStr(), locationId, em));
				Date updated = formatter.parse(employeeOperation.getUpdatedStr());
				
				employeesToEmployeesOperation.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
				employeesToEmployeesOperation.setUpdated(updated);
				if(employeeOperation.getStatus() != null)
				{
					employeesToEmployeesOperation.setStatus(employeeOperation.getStatus());	
				}
				
				employeesToEmployeesOperation.setJobRoleId(employeeOperation.getJobRoleId());
				employeesToEmployeesOperation.setUpdatedBy(employeeOperation.getUpdatedBy());
				LocalSchemaEntityManager.merge(em, employeesToEmployeesOperation);
				employeesToEmployeesOperations.add(employeesToEmployeesOperation);
				
				// insert into history
				EmployeesToEmployeesOperationHistory operationHistory=  EmployeesToEmployeesOperationHistory.setEmployeesToEmployeesOperationHistory(employeesToEmployeesOperation, em);
				LocalSchemaEntityManager.persist(em, operationHistory);
			}else{
				// using object from packet
				//registering new boject in database
				employeesToEmployeesOperation =employeeOperation;
				if(employeeOperation.getEmployeeOperationId()!=null){
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					// converting location to gmt time
					employeeOperation.setUpdatedStr(time.getDateAccordingToGMT(employeeOperation.getUpdatedStr(), locationId, em));
					Date updated = formatter.parse(employeeOperation.getUpdatedStr());
					
					
					employeesToEmployeesOperation.setUpdated(updated);
					if(employeeOperation.getStatus() != null)
					{
						employeesToEmployeesOperation.setStatus(employeeOperation.getStatus());	
					}
					employeesToEmployeesOperation.setJobRoleId(employeeOperation.getJobRoleId());
					employeesToEmployeesOperation.setUpdatedBy(employeeOperation.getUpdatedBy());
					employeesToEmployeesOperation.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
					employeesToEmployeesOperation.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					employeesToEmployeesOperations.add(employeesToEmployeesOperation);
					LocalSchemaEntityManager.persist(em, employeesToEmployeesOperation);
					// insert into history
					EmployeesToEmployeesOperationHistory operationHistory=  EmployeesToEmployeesOperationHistory.setEmployeesToEmployeesOperationHistory(employeesToEmployeesOperation, em);
					LocalSchemaEntityManager.persist(em, operationHistory);
				}
			}
		}
		
		
		
		
		
		return employeesToEmployeesOperations;
	
	}

	/**
	 * This is used to delete an existing employee operation.
	 *
	 * @param em the em
	 * @param employeeOperation the employee operation
	 */
	static void deleteEmployeeOperation(EntityManager em, EmployeeOperation employeeOperation)
	{
		employeeOperation = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperation.getId());
		employeeOperation.setStatus("D");
		employeeOperation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		LocalSchemaEntityManager.merge(em, employeeOperation);
	}

	/**
	 * This is used to get all employee operations for a given location.
	 *
	 * @param em the em
	 * @param locationsId the locations id
	 * @return the all employee operation by location id
	 */
	static List<EmployeeOperation> getAllEmployeeOperationByLocationId(EntityManager em, String locationsId)
	{
		String queryString = "select eo from EmployeeOperation eo where eo.status != 'D' and locationsId=? " ;
		TypedQuery<EmployeeOperation> query = em.createQuery(queryString, EmployeeOperation.class).setParameter(1, locationsId);
		List<EmployeeOperation> resultSet = query.getResultList();
		List<EmployeeOperation> newEmployeeOperation = new ArrayList<EmployeeOperation>();
		for(EmployeeOperation e:resultSet){
			e.setEmployeeOperationToAlertMessage(EmployeeOperationHelper.getEmployeeOperationToAlertMessage(em,  e.getId()));
			newEmployeeOperation.add(e);
		}
		return newEmployeeOperation;
	}

	/**
	 * This is used to get clock-in and clock-out employee operation entities.
	 * It will exclude operations that have a deleted (D) status.
	 *
	 * @param em the em
	 * @param locationsId the locations id
	 * @return the employee operation for clock in clockout
	 */
	static List<EmployeeOperation> getEmployeeOperationForClockInClockout(EntityManager em, String locationsId)
	{
		String queryString = "select eo from EmployeeOperation eo where eo.status != 'D' and eo.locationsId= ? and eo.operationName in (?,?)";
		TypedQuery<EmployeeOperation> query = em.createQuery(queryString, EmployeeOperation.class).setParameter(1, locationsId).setParameter(2, EmployeeOperationName.ClockIn)
				.setParameter(3, EmployeeOperationName.ClockOut);
		List<EmployeeOperation> resultSet = query.getResultList();
		return resultSet;
	}
	
	/**
	 * Gets the employee operation to alert message.
	 *
	 * @param em the em
	 * @param employeeOperationId the employee operation id
	 * @return the employee operation to alert message
	 */
	static  List<EmployeeOperationToAlertMessage> getEmployeeOperationToAlertMessage(EntityManager em, String employeeOperationId)
		{
			List<EmployeeOperationToAlertMessage> resultSet=null;
			try {
				String queryString = "select eo from EmployeeOperationToAlertMessage eo where eo.status != 'D' and eo.employeeOperationId= ? ";
				TypedQuery<EmployeeOperationToAlertMessage> query = em.createQuery(queryString, EmployeeOperationToAlertMessage.class).setParameter(1, employeeOperationId);
				resultSet = query.getResultList();
			} catch (Exception e) {
				logger.severe(e);
			}
			return resultSet;
		}

	/**
	 * This is used to get the relation between employees and employee operations.
	 * This will exclude employee operations that have a deleted (D) status.
	 *
	 * @param em the em
	 * @param locationsId the locations id
	 * @param userId the user id
	 * @param clockIn the clock in
	 * @param clockOut the clock out
	 * @return the employees to employees operation for user id
	 */
	static EmployeesToEmployeesOperation getEmployeesToEmployeesOperationForUserId(EntityManager em, String locationsId, String userId, int clockIn, int clockOut)
	{
		String queryString = "select eo from EmployeesToEmployeesOperation eo where eo.status != 'D' and eo.usersId =? and eo.employeeOperationId in (?,?) order by eo.id desc";
		TypedQuery<EmployeesToEmployeesOperation> query = em.createQuery(queryString, EmployeesToEmployeesOperation.class).setParameter(1, userId).setParameter(2, clockIn).setParameter(3, clockOut);
		List<EmployeesToEmployeesOperation> resultSet = query.getResultList();
		if (resultSet != null && resultSet.size() > 0)
		{
			return resultSet.get(0);
		}
		return null;
	}

	/**
	 * This is used to get a specific employee operation for given Id.
	 * It will exclude operation if it is marked as deleted (D) status.
	 *
	 * @param em the em
	 * @param employeeOperationId the employee operation id
	 * @return the employee operation by id
	 */
	static EmployeeOperation getEmployeeOperationById(EntityManager em, String employeeOperationId)
	{
		String queryString = "select eo from EmployeeOperation eo where eo.status != 'D' and eo.id=? " ;
		TypedQuery<EmployeeOperation> query = em.createQuery(queryString, EmployeeOperation.class).setParameter(1, employeeOperationId);
		EmployeeOperation resultSet = query.getSingleResult();
		resultSet.setEmployeeOperationToAlertMessage(EmployeeOperationHelper.getEmployeeOperationToAlertMessage(em,  resultSet.getId()));
	 	return resultSet;
	}

	/**
	 * This is used to add a new relation between an employee and employee operation.
	 * This will also set the satus of the relation as active (A).
	 *
	 * @param em - the entitymanager to use
	 * @param userId - the employee's id
	 * @param employeeOperationId - the operation id
	 * @param createdBy - who is trying to add this relation
	 * @return the employees to employees operation
	 */

	static EmployeesToEmployeesOperation addEmployeesToEmployeesOperation(EntityManager em, String userId, String employeeOperationId, String createdBy,
			String jobRoleId)
	{
		EmployeesToEmployeesOperation employeesToEmployeesOperation = new EmployeesToEmployeesOperation();
		employeesToEmployeesOperation.setUsersId(userId);
		employeesToEmployeesOperation.setEmployeeOperationId(employeeOperationId);
		employeesToEmployeesOperation.setCreatedBy(createdBy);
		employeesToEmployeesOperation.setUpdatedBy(createdBy);
		employeesToEmployeesOperation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeesToEmployeesOperation.setJobRoleId(jobRoleId);
		EmployeeOperation employeeOperation = (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeesToEmployeesOperation.getEmployeeOperationId());
		if(employeeOperation != null)
		{
			employeesToEmployeesOperation.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(employeeOperation.getLocationsId(), em));
		}
		
		
		employeesToEmployeesOperation.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeesToEmployeesOperation.setStatus("A");
		LocalSchemaEntityManager.persist(em, employeesToEmployeesOperation);
		// insert into history
		EmployeesToEmployeesOperationHistory operationHistory=  EmployeesToEmployeesOperationHistory.setEmployeesToEmployeesOperationHistory(employeesToEmployeesOperation, em);
		LocalSchemaEntityManager.persist(em, operationHistory);
		
		return employeesToEmployeesOperation;
	}

	/**
	 * This is used to get employee operation for a given location that has the given display sequence.
	 * This will exclude the location if it is marked as deleted (D) status.
	 *
	 * @param em the em
	 * @param locationId - The location Id
	 * @param displaySequence - the display sequence number
	 * @return the employees operation by location id and display sequence
	 */
	static EmployeeOperation getEmployeesOperationByLocationIdAndDisplaySequence(EntityManager em, String locationId, int displaySequence)
	{
		String queryString = "select l from EmployeeOperation  l where l.status !='D'  and  l.locationsId =?  and l.displaySequence=?" ;
		TypedQuery<EmployeeOperation> query = em.createQuery(queryString, EmployeeOperation.class).setParameter(1, locationId).setParameter(2, displaySequence);

		List<EmployeeOperation> resultSet = null;
		EmployeeOperation employeeOperation = null;

		resultSet = query.getResultList();
		if (resultSet.size() > 0)
		{
			employeeOperation = resultSet.get(0);
		}

		return employeeOperation;
	}

	/**
	 * This is used to get the employee operation for a location that has the given name.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @param name the name
	 * @return the employee operation by location id and name
	 */
	static EmployeeOperation getEmployeeOperationByLocationIdAndName(EntityManager em, String locationId, String name)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeOperation> criteria = builder.createQuery(EmployeeOperation.class);
		Root<EmployeeOperation> r = criteria.from(EmployeeOperation.class);
		TypedQuery<EmployeeOperation> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(EmployeeOperation_.locationsId), locationId),
				builder.equal(r.get(EmployeeOperation_.operationName), name), builder.notEqual(r.get(EmployeeOperation_.status), "D")));
		EmployeeOperation resultSet = null;
		resultSet = query.getSingleResult();

		return resultSet;
	}

	/**
	 * This method is used to get the last employee operation that the employee did at the given 
	 * location which was also a "sequence controlled" operation.
	 * This will exclude employee operations that are deleted (D) status 
	 * and also excludes deleted (D) status employee. 
	 *
	 * @param em the em
	 * @param locationsId the locations id
	 * @param userId the user id
	 * @return the previous sequence controlled operation for employee
	 */
	static EmployeesToEmployeesOperation getPreviousSequenceControlledOperationForEmployee(EntityManager em, String locationsId, String userId)
	{

		String sql = "SELECT * FROM employees_to_employees_operations eo inner join employee_operations e on eo.employee_operation_id = e.id and e.operation_name in (";

		for (int i = 0; i < SEQUENCE_CONTROLLED_OPERATION_NAME_LIST.size(); ++i)
		{
			sql += "?,";
		}

		// remove the last ","
		sql = sql.substring(0, sql.length() - 1);
		sql += ") where eo.status != 'D' and eo.users_id = ? and e.status != 'D' and e.locations_id= ? order by eo.id desc";

		Query query = em.createNativeQuery(sql, EmployeesToEmployeesOperation.class);

		int i = 0;
		for (String s : SEQUENCE_CONTROLLED_OPERATION_NAME_LIST)
		{
			query.setParameter(++i, s);
		}

		query.setParameter(++i, userId).setParameter(++i, locationsId);
		// only get upto 2 results
		query.setMaxResults(2);

		@SuppressWarnings("unchecked")
		List<EmployeesToEmployeesOperation> resultList = (List<EmployeesToEmployeesOperation>) query.getResultList();
		if (resultList != null && !resultList.isEmpty())
		{
			// return the first result from the list
			return resultList.get(0);
		}
		return null;
	}
	
	
	/**
	 * This is used to add a new employeeOperationToCashRegister.
	 *
	 * @param em the em
	 * @param employeeOperationToCashRegister the employee operation to cash register
	 * @param currentBatch the current batch
	 * @param businessId the business id
	 * @return the employee operation to cash register
	 * @throws Exception 
	 */
	EmployeeOperationToCashRegister addPaidInPaidOut(EntityManager em, 
			EmployeeOperationToCashRegister employeeOperationToCashRegister,
			String currentBatch,String businessId,HttpServletRequest request) throws Exception
	{
		TimezoneTime time = new TimezoneTime();
	
		EmployeeOperation operation= (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperationToCashRegister.getEmployeeOperationId());
		Reasons	 reason =   (Reasons) new CommonMethods().getObjectById("Reasons", em,Reasons.class, employeeOperationToCashRegister.getReasonsId());
		if(reason!=null && reason.getName().equals("Close Out Without Cash Reconcile")){
			
		}
		CashRegisterRunningBalance resultSet =null;
		List<CashRegisterRunningBalance> result =null;
		CashRegisterRunningBalance balance = new CashRegisterRunningBalance();
		balance.setIsAmountCarryForwarded(0);
		balance.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		balance.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(employeeOperationToCashRegister.getLocationsId(), em));
		balance.setCreatedBy(employeeOperationToCashRegister.getCreatedBy());
		balance.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		balance.setUpdatedBy(employeeOperationToCashRegister.getUpdatedBy());
		balance.setEmployeeOperationToCashRegisterId(employeeOperationToCashRegister.getId());
		balance.setNirvanaXpBatchNumber(currentBatch);
		balance.setOpdId(null);
		balance.setRegisterId(employeeOperationToCashRegister.getRegisterId());
		balance.setStatus("A");
		balance.setTransactionAmount(new BigDecimal(employeeOperationToCashRegister.getAmount()));
	
		try {
			String queryString = "select l from CashRegisterRunningBalance l where l.registerId =? order by id asc ";
			TypedQuery<CashRegisterRunningBalance> query = em.createQuery(queryString, CashRegisterRunningBalance.class).setParameter(1, balance.getRegisterId());
			result = query.getResultList();
		} catch (Exception e) {
			logger.severe(e);
		}
		if(result !=null){
			for(CashRegisterRunningBalance c :result){
				resultSet =c;
				continue;
			}
		}
		
	
		if(resultSet!= null){
			balance.setRunningBalance(resultSet.getRunningBalance()); 
			if(operation.getOperationName().equals("Paid Out") && 
					((balance.getRunningBalance().doubleValue()-employeeOperationToCashRegister.getAmount()) < new BigDecimal(0).doubleValue())){
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(ERROR_CODE_INSUFFICIENT_FUND, ERROR_MESSAGE_INSUFFICIENT_FUND, ERROR_MESSAGE_INSUFFICIENT_FUND));
			}
		}else{
			balance.setRunningBalance(new BigDecimal(0));
		}
		if(operation!=null && operation.getOperationName().equals("Paid In")){
			balance.setTransactionStatus("CR");
			balance.setRunningBalance(balance.getRunningBalance().add(balance.getTransactionAmount()));
			
		}else{
			balance.setRunningBalance(balance.getRunningBalance().subtract(balance.getTransactionAmount()));
			balance.setTransactionStatus("DR");
		}
		 
		if(reason!=null && reason.getName().equals("Close Out Without Cash Reconcile")){
		 
			employeeOperationToCashRegister.setAmount(balance.getRunningBalance().doubleValue());
			balance.setTransactionAmount(balance.getRunningBalance());
			balance.setRunningBalance(new BigDecimal(0));
		}
		
		employeeOperationToCashRegister.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeOperationToCashRegister.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(employeeOperationToCashRegister.getLocationsId(), em));
		employeeOperationToCashRegister.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		
		em.getTransaction().begin();
		employeeOperationToCashRegister.setId(new StoreForwardUtility().generatePaidInOutNew(employeeOperationToCashRegister.getLocationsId(), em));
		em.getTransaction().commit();
		 
		LocalSchemaEntityManager.persist(em, employeeOperationToCashRegister);
		balance.setEmployeeOperationToCashRegisterId(employeeOperationToCashRegister.getId());
		LocalSchemaEntityManager.persist(em, balance);
		insertIntoHistory(em, employeeOperationToCashRegister);
	
		return employeeOperationToCashRegister;
	}

	/**
	 * This is used to update an existing employeeOperationToCashRegister.
	 *
	 * @param em the em
	 * @param employeeOperationToCashRegister the employee operation to cash register
	 * @return the employee operation to cash register
	 * @throws NirvanaXPException the nirvana XP exception
	 */
	 EmployeeOperationToCashRegister updatePaidInPaidOut(EntityManager em, EmployeeOperationToCashRegister employeeOperationToCashRegister) throws NirvanaXPException
	{
		 boolean isEditReasonOnly = false;
		 EmployeeOperationToCashRegister cashRegister = em .find(EmployeeOperationToCashRegister.class, employeeOperationToCashRegister.getId());
		if(cashRegister!=null && employeeOperationToCashRegister.getAmount()== cashRegister.getAmount()){
			isEditReasonOnly =true;
		}
		CashRegisterRunningBalance resultSet =null;
		List<CashRegisterRunningBalance> result =null;
		if(!isEditReasonOnly){
			 EmployeeOperation operation= (EmployeeOperation) new CommonMethods().getObjectById("EmployeeOperation", em,EmployeeOperation.class, employeeOperationToCashRegister.getEmployeeOperationId());
				
				
				CashRegisterRunningBalance balance = new CashRegisterRunningBalance();
				balance.setIsAmountCarryForwarded(0);
				balance.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				balance.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(employeeOperationToCashRegister.getLocationsId(), em));
				balance.setCreatedBy(employeeOperationToCashRegister.getCreatedBy());
				balance.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				balance.setUpdatedBy(employeeOperationToCashRegister.getUpdatedBy());
				balance.setEmployeeOperationToCashRegisterId(employeeOperationToCashRegister.getId());
				
				balance.setOpdId(null);
				balance.setRegisterId(employeeOperationToCashRegister.getRegisterId());
				balance.setStatus("A");
				balance.setTransactionAmount(new BigDecimal(employeeOperationToCashRegister.getAmount()));
				
				balance.setNirvanaXpBatchNumber(cashRegister.getNirvanaXpBatchNumber());
				try {
					String queryString = "select l from CashRegisterRunningBalance l where l.registerId =? order by id asc ";
					TypedQuery<CashRegisterRunningBalance> query = em.createQuery(queryString, CashRegisterRunningBalance.class).setParameter(1, balance.getRegisterId());
					result = query.getResultList();
				} catch (Exception e) {
					logger.severe(e);
				}
				for(CashRegisterRunningBalance c :result){
					resultSet =c;
					continue;
				}
			
				if(resultSet!= null){
					balance.setRunningBalance(resultSet.getRunningBalance());
					if(operation.getOperationName().equals("Paid Out") && balance.getRunningBalance().add(new BigDecimal(cashRegister.getAmount())).subtract(new BigDecimal(employeeOperationToCashRegister.getAmount())).doubleValue()<0){
						throw new NirvanaXPException(new NirvanaServiceErrorResponse(ERROR_CODE_INSUFFICIENT_FUND, ERROR_MESSAGE_INSUFFICIENT_FUND, ERROR_MESSAGE_INSUFFICIENT_FUND));
					}
				}else{
					balance.setRunningBalance(new BigDecimal(0));
				}
				if(operation!=null && operation.getOperationName().equals("Paid In")){
					balance.setTransactionStatus("CR");
					balance.setRunningBalance(balance.getRunningBalance().subtract(new BigDecimal(cashRegister.getAmount())).add(balance.getTransactionAmount()));
					
				}else{
					balance.setRunningBalance(balance.getRunningBalance().add(new BigDecimal(cashRegister.getAmount())).subtract(balance.getTransactionAmount()));
					balance.setTransactionStatus("DR");
				}
			 
			LocalSchemaEntityManager.persist(em, balance);
		}
		if(!isEditReasonOnly){
			cashRegister.setAmount(employeeOperationToCashRegister.getAmount());
		}
	
		cashRegister.setComments(employeeOperationToCashRegister.getComments());
		cashRegister.setEmployeeOperationId(employeeOperationToCashRegister.getEmployeeOperationId());
		cashRegister.setLocationsId(employeeOperationToCashRegister.getLocationsId());
		cashRegister.setReasonsId(employeeOperationToCashRegister.getReasonsId());
		cashRegister.setRegisterId(employeeOperationToCashRegister.getRegisterId());
		cashRegister.setStatus(employeeOperationToCashRegister.getStatus());
		cashRegister.setUpdatedBy(employeeOperationToCashRegister.getUpdatedBy());
		cashRegister.setUserId(employeeOperationToCashRegister.getUserId());
		cashRegister.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		
		LocalSchemaEntityManager.merge(em, cashRegister);
		insertIntoHistory(em, cashRegister);
		return cashRegister;
	}

	/**
	 * This is used to delete an existing employee operation.
	 *
	 * @param em the em
	 * @param employeeOperationToCashRegister the employee operation to cash register
	 * @return the employee operation to cash register
	 */
	 EmployeeOperationToCashRegister  deletePaidInPaidOut(EntityManager em, EmployeeOperationToCashRegister employeeOperationToCashRegister)
	{
		employeeOperationToCashRegister = em.find(EmployeeOperationToCashRegister.class, employeeOperationToCashRegister.getId());
		employeeOperationToCashRegister.setStatus("D");
		employeeOperationToCashRegister.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis())	);
		LocalSchemaEntityManager.merge(em, employeeOperationToCashRegister);
		return employeeOperationToCashRegister;
	}

	/**
	 * This is used to get all  employeeOperationToCashRegister for a given location.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationsId the locations id
	 * @param date the date
	 * @param isDateWise the is date wise
	 * @return the all employee operation to cash register by location id
	 * @throws ParseException the parse exception
	 */	
	List<EmployeeOperationToCashRegister> getAllEmployeeOperationToCashRegisterByLocationId(HttpServletRequest httpRequest,EntityManager em, String locationsId,
			String date,boolean isDateWise) throws ParseException
	{
		List<EmployeeOperationToCashRegister> resultSet = new ArrayList<EmployeeOperationToCashRegister>();
		OrderManagementServiceBean bean = new OrderManagementServiceBean();
		List<Date> dates = bean.batchMaxMinString(em, locationsId, date,isDateWise);
		
		if(dates !=null && dates.size()>0){
		
			String queryString = "select eo from EmployeeOperationToCashRegister eo where eo.status  not in ('D') and eo.locationsId=? and eo.created between ? and ? " ;
			TypedQuery<EmployeeOperationToCashRegister> query = em.createQuery(queryString, EmployeeOperationToCashRegister.class).setParameter(1, locationsId).setParameter(2, (dates.get(0))).setParameter(3, (dates.get(1)));
			resultSet = query.getResultList();
		}
		return resultSet;
	}

	/**
	 * Insert into history.
	 *
	 * @param em the em
	 * @param employeeOperationToCashRegister the employee operation to cash register
	 * @return true, if successful
	 */
	private boolean insertIntoHistory(EntityManager em , EmployeeOperationToCashRegister employeeOperationToCashRegister){
		boolean result = false;
		EmployeeOperationToCashRegisterHistory history = new EmployeeOperationToCashRegisterHistory();
		history.setAmount(employeeOperationToCashRegister.getAmount());
		history.setComments(employeeOperationToCashRegister.getComments());
		history.setCreated(employeeOperationToCashRegister.getCreated());
		history.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(employeeOperationToCashRegister.getLocationsId(), em));
		history.setUpdated(employeeOperationToCashRegister.getUpdated());
		history.setEmployeeOperationId(employeeOperationToCashRegister.getEmployeeOperationId());
		history.setEmployeeOperationToCashRegisterId(employeeOperationToCashRegister.getId());
		history.setLocationsId(employeeOperationToCashRegister.getLocationsId());
		history.setReasonsId(employeeOperationToCashRegister.getReasonsId());
		history.setRegisterId(employeeOperationToCashRegister.getRegisterId());
		history.setStatus(employeeOperationToCashRegister.getStatus());
		history.setUpdatedBy(employeeOperationToCashRegister.getUpdatedBy());
		history.setUserId(employeeOperationToCashRegister.getUserId());
		history.setCreatedBy(employeeOperationToCashRegister.getCreatedBy());
		history.setNirvanaXpBatchNumber(employeeOperationToCashRegister.getNirvanaXpBatchNumber());
		
		LocalSchemaEntityManager.persist(em, history);
		return result;
	}
	
	/**
	 * Gets the users to payment history by location id.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationsId the locations id
	 * @param date the date
	 * @param isDateWise the is date wise
	 * @param logger the logger
	 * @return the users to payment history by location id
	 * @throws ParseException the parse exception
	 */
	List<UsersToPaymentHistory> getUsersToPaymentHistoryByLocationId(HttpServletRequest httpRequest,EntityManager em, String locationsId,
			String date,boolean isDateWise,NirvanaLogger logger) throws ParseException
	{
		List<UsersToPaymentHistory> resultSet = new ArrayList<UsersToPaymentHistory>();
		OrderManagementServiceBean bean = new OrderManagementServiceBean();
		List<Date> dates = bean.batchMaxMinString(em, locationsId, date,isDateWise);
		if(dates !=null && dates.size()>0){
		
			String queryString = "select eo from UsersToPaymentHistory eo where eo.status  not in ('D') and eo.amountPaid<0 and eo.locationId=? and eo.created between ? and ?" ;
			TypedQuery<UsersToPaymentHistory> query = em.createQuery(queryString, UsersToPaymentHistory.class).setParameter(1, locationsId).setParameter(2, (dates.get(0))).setParameter(3, (dates.get(1)));
			resultSet = query.getResultList();
		}
		return resultSet;
	}
	
	/**
	 * Gets the all users to payment history by location id.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationsId the locations id
	 * @param date the date
	 * @param isDateWise the is date wise
	 * @param logger the logger
	 * @return the all users to payment history by location id
	 * @throws ParseException the parse exception
	 */
	List<UsersToPaymentHistory> getAllUsersToPaymentHistoryByLocationId(HttpServletRequest httpRequest,EntityManager em, String locationsId,
			String date,boolean isDateWise,NirvanaLogger logger) throws ParseException
	{
		List<UsersToPaymentHistory> resultSet = new ArrayList<UsersToPaymentHistory>();
		OrderManagementServiceBean bean = new OrderManagementServiceBean();
		List<Date> dates = bean.batchMaxMinString(em, locationsId, date,isDateWise);
		if(dates !=null && dates.size()>0){
		
			String queryString = "select eo from UsersToPaymentHistory eo where eo.status  not in ('D') and eo.amountPaid<0 and eo.locationId=? and eo.created between ? and ? " ;
			TypedQuery<UsersToPaymentHistory> query = em.createQuery(queryString, UsersToPaymentHistory.class).setParameter(1, locationsId).setParameter(2, (dates.get(0))).setParameter(3, (dates.get(1)));
			resultSet = query.getResultList();
		}
		return resultSet;
	}

	/**
	 * Update user for login.
	 *
	 * @param u the u
	 * @param loginStatus the login status
	 * @param em the em
	 * @return the user
	 */
	public static User updateUserForLogin(User u, int loginStatus, EntityManager em) {

		u.setIsAllowedLogin(loginStatus);
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.merge(u);
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
		return u;

	}
	
	/**
	 * Gets the register info by id.
	 *
	 * @param registerId the register id
	 * @param amount the amount
	 * @param currentBatchId the current batch id
	 * @param em the em
	 * @return the register info by id
	 * @throws ParseException the parse exception
	 */
	boolean getRegisterInfoById(int registerId, double amount, int currentBatchId,EntityManager em) throws ParseException {
		
	

		boolean validateRegisterId	=	false;
		String sql = "Select sum(opd.amount_paid) + (select COALESCE(sum(eotcr.amount),0) "
				+ "from employee_operation_to_cash_register eotcr"
				+ " left join employee_operations eo on eo.id = eotcr.employee_operation_id"
				+ " where eotcr.register_id="+registerId+" and eotcr.nirvanaxp_batch_number="+currentBatchId+" and eo.operation_name='Paid In')"
				+ " - (select COALESCE(sum(eotcr.amount),0) from employee_operation_to_cash_register eotcr "
				+ " left join employee_operations eo on eo.id = eotcr.employee_operation_id"
				+ " where eotcr.register_id="+registerId+" and eotcr.nirvanaxp_batch_number="+currentBatchId+" and eo.operation_name='Paid Out')"
				+ " from order_payment_details opd "
				+ "left join order_header oh on oh.id = opd.order_header_id "
				+ "left join printers p on p.id = opd.register "
				+ "left join transaction_status ts on ts.id = opd.transaction_status_id "
				+ "LEFT JOIN order_status oss ON oss.id = oh.order_status_id "
				+ "left join payment_transaction_type ptt on ptt.id = opd.payment_transaction_type_id"
				+ " where (((ts.name in('CC Auth','Manual CC Auth','Tip Saved','Cash Sale', 'Credit') "
				+ "and opd.is_refunded =0) or (ts.name ='CC Pre Capture' and ptt.name = 'Force' "
				+ "and opd.is_refunded =0) or (ts.name ='CC Settled' and ptt.name in( 'Capture','CaptureAll', 'Credit') "
				+ "and opd.is_refunded =0)) ) "
				+ "and opd.nirvanaxp_batch_number= "+currentBatchId+" and  opd.register="+registerId+" and oss.name not in ('Void Order');";
		Object resultList =  em.createNativeQuery(sql).getSingleResult();
		if(resultList!=null){
			double result = (double)resultList;
			if(result>0)
				validateRegisterId	=	true;
		}
		

		return validateRegisterId;
	}
	
	/**
	 * Gets the cash register.
	 *
	 * @param locationsId the locations id
	 * @param date the date
	 * @param em the em
	 * @param isDateWise the is date wise
	 * @return the cash register
	 * @throws ParseException the parse exception
	 */
/*	List<RegisterInfo> getCashRegister(String locationsId, String date,EntityManager em, boolean isDateWise) throws ParseException {
		
		RegisterInfo revenue = null;
		OrderManagementServiceBean bean = new OrderManagementServiceBean();
		List<Date> dates = bean.batchMaxMinString(em, locationsId, date,isDateWise);

		List<RegisterInfo> registerInfoList = new ArrayList<RegisterInfo>();
		@SuppressWarnings("unchecked")
		List<Object> resultListPaidInOut = em.createNativeQuery("select distinct register_id from cash_register_running_balance crrb"
				+ " left join printers p on p.locations_id = " + locationsId
						+ " where crrb.created between ? and ? ")
		.setParameter(1, (dates.get(0))).setParameter(2, (dates.get(1))).getResultList();
		
		if (resultListPaidInOut != null ) 
		{
				// if this has primary key not 0
			for(Object object:resultListPaidInOut)
			{
				int registerId =(int)object;
				Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, registerId);
				revenue = new RegisterInfo();
			 
				if (printer != null)
					revenue.setName(printer.getDisplayName());
					
				CashRegisterRunningBalance resultSet =null;
				List<CashRegisterRunningBalance> result =null;
				try 
				{
					String queryString = "select l from CashRegisterRunningBalance l " +
							" where l.registerId = ? order by l.id asc ";
					TypedQuery<CashRegisterRunningBalance> query = em.createQuery(queryString, CashRegisterRunningBalance.class)
							.setParameter(1, registerId);
					result = query.getResultList();
				} catch (Exception e) 
				{
					logger.severe(e);
				}
				if(result != null)
				{
					for(CashRegisterRunningBalance c :result){
						
						Printer printer2 = null;
						try 
						{
							String queryString = "select l from Printer l where l.id = ?";
							TypedQuery<Printer> query = em.createQuery(queryString, Printer.class)
									.setParameter(1, registerId);
							printer2 = query.getSingleResult();
						} catch (Exception e) 
						{
							logger.severe(e);
						}
						
						if(printer2 != null && printer2.getLocationsId() == locationsId)
						{
							resultSet =c;
							continue;	
						}
						
					}
				}
				
				if (resultSet != null)
					revenue.setTotal(resultSet.getRunningBalance()+"");
				
				registerInfoList.add(revenue);
			 
			}


			}
			
		return registerInfoList;
	}*/
	
	List<RegisterInfo> getCashRegister(String locationsId, String date,EntityManager em, boolean isDateWise) throws ParseException {
		
		RegisterInfo revenue = null;
		OrderManagementServiceBean bean = new OrderManagementServiceBean();
		List<Date> dates = bean.batchMaxMinString(em, locationsId, date,isDateWise);
		Location l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationsId);
		List<RegisterInfo> registerInfoList = new ArrayList<RegisterInfo>();
		@SuppressWarnings("unchecked")
		List<Object> resultListPaidInOut = em.createNativeQuery(""
				+ "select distinct register_id from cash_register_running_balance crrb "
				+ " left join printers p on p.id = crrb.register_id "
				+ " where crrb.created between ? and ? and p.locations_id = '"+locationsId+"'" )
		.setParameter(1, (dates.get(0))).setParameter(2, (dates.get(1))).getResultList();
		
		
		if (resultListPaidInOut != null ) {
				// if this has primary key not 0
			for(Object object:resultListPaidInOut){
				String registerId =(String)object;
				Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, registerId);
				revenue = new RegisterInfo();
			 
				if (printer != null)
					revenue.setName(printer.getDisplayName());
					
				CashRegisterRunningBalance resultSet =null;
				List<CashRegisterRunningBalance> result =null;
				try {
					String queryString = "select l from CashRegisterRunningBalance l where l.registerId =? order by id asc ";
					TypedQuery<CashRegisterRunningBalance> query = em.createQuery(queryString, CashRegisterRunningBalance.class).setParameter(1, registerId);
					result = query.getResultList();
				} catch (Exception e) {
					logger.severe(e);
				}
				for(CashRegisterRunningBalance c :result){
					
					resultSet =c;
					continue;	
					
				}
				if (resultSet != null)
					revenue.setTotal(resultSet.getRunningBalance()+"");
				
				registerInfoList.add(revenue);
			 
		}


	}
			
		return registerInfoList;
	}

	/**
	 * Check employee operation to cash register by register id and operation id.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param registerId the register id
	 * @param logger the logger
	 * @return true, if successful
	 * @throws ParseException the parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException the invalid session exception
	 */
	boolean checkEmployeeOperationToCashRegisterByRegisterIdAndOperationId(HttpServletRequest httpRequest,EntityManager em, String registerId,
			NirvanaLogger logger) throws ParseException, IOException, InvalidSessionException
	{
		//PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
	//	int batchId = batchManager.getCurrentBatchIdBySession(httpRequest, em, null, locationId, false, null);
		CashRegisterRunningBalance resultSet =null;
		List<CashRegisterRunningBalance> result =null;
		
		//TODO - remove try catch and handle in service 
		try {
			String queryString = "select l from CashRegisterRunningBalance l where l.registerId =? order by id asc ";
			TypedQuery<CashRegisterRunningBalance> query = em.createQuery(queryString, CashRegisterRunningBalance.class).setParameter(1, registerId);
			result = query.getResultList();
		} catch (Exception e) {
			logger.severe(e);
		}
		for(CashRegisterRunningBalance c :result){
			resultSet =c;
			continue;
		}
		
		if (resultSet!=null && resultSet.getRunningBalance().doubleValue() > 0) {
			return true;
		}
		
		
		return false;
	}
	
	/**
	 * This is used to add a new employee operation.
	 *
	 * @param em the em
	 * @param clockInClockOut the clockInClockOut
	 * @throws InvalidSessionException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ClockInClockOut addUpdateClockInClockOut(HttpServletRequest httpRequest,EntityManager em, ClockInClockOut clockInClockOut) throws FileNotFoundException, IOException, InvalidSessionException
	{
		
		
		UserSession session = GlobalSchemaEntityManager.getInstance().getUserSessionWithoutSessionCheck(httpRequest, httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
		clockInClockOut.setSessionId(session.getId()+"");
		clockInClockOut.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(clockInClockOut.getLocationId(), em));
		
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date updated = null;
		
		
		if(clockInClockOut.getClockInStr() != null && !clockInClockOut.getClockInStr().isEmpty())
		{
			try
			{
				TimezoneTime time = new TimezoneTime();
				clockInClockOut.setClockInStr(time.getDateAccordingToGMT(clockInClockOut.getClockInStr(), clockInClockOut.getLocationId(), em));
				
				updated = formatter.parse(clockInClockOut.getClockInStr());
			}
			catch (ParseException e)
			{
				logger.severe(e);
			}
			clockInClockOut.setClockIn(updated);
		}
		
		
		
		if(clockInClockOut.getClockOutStr() != null && !clockInClockOut.getClockOutStr().isEmpty())
		{
			updated = null;
			
			try
			{
				TimezoneTime time = new TimezoneTime();
				clockInClockOut.setClockOutStr(time.getDateAccordingToGMT(clockInClockOut.getClockOutStr(), clockInClockOut.getLocationId(), em));
				
				updated = formatter.parse(clockInClockOut.getClockOutStr());
			}
			catch (ParseException e)
			{
				logger.severe(e);
			}
			clockInClockOut.setClockOut(updated);
		}
		
		
		
		
		if(clockInClockOut.getId() > 0)
		{
			ClockInClockOut old = em.find(ClockInClockOut.class,clockInClockOut.getId() );
			clockInClockOut.setClockInClockOutId(old.getClockInClockOutId());
			clockInClockOut.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			clockInClockOut = em.merge(clockInClockOut);
			
			
		}else
		{
			clockInClockOut.setClockInClockOutId(""+ new StoreForwardUtility().getAndUpdateCountOfTableIndex(em, clockInClockOut.getLocationId(),"clock_in_clock_out", false));
			clockInClockOut.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			clockInClockOut.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.persist(clockInClockOut);
		}
		
		
		//need to Check here updated fields 
		// if find then it should insert in histroy table
		new InsertIntoHistory().insertClockInClockOutHistory(httpRequest, clockInClockOut, em);
		
		return clockInClockOut;
		
	}
	
	/**
	 * This is used to add a new employee operation.
	 *
	 * @param em the em
	 * @param employeeOperation the employee operation
	 * @throws InvalidSessionException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public BreakInBreakOut addUpdateBreakInBreakOut(HttpServletRequest httpRequest,EntityManager em, BreakInBreakOut breakInBreakOut) throws FileNotFoundException, IOException, InvalidSessionException
	{
		UserSession session = GlobalSchemaEntityManager.getInstance().getUserSessionWithoutSessionCheck(httpRequest, httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
		breakInBreakOut.setSessionId(session.getId()+"");
		breakInBreakOut.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(breakInBreakOut.getLocationId(), em));
		
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date updated = null;
		
		
		if(breakInBreakOut.getBreakInStr() != null && !breakInBreakOut.getBreakInStr().isEmpty())
		{
			try
			{
				TimezoneTime time = new TimezoneTime();
				breakInBreakOut.setBreakInStr(time.getDateAccordingToGMT(breakInBreakOut.getBreakInStr(), breakInBreakOut.getLocationId(), em));
				
				updated = formatter.parse(breakInBreakOut.getBreakInStr());
			}
			catch (ParseException e)
			{
				logger.severe(e);
			}
			breakInBreakOut.setBreakIn(updated);
		}
		
		
		
		if(breakInBreakOut.getBreakOutStr() != null && !breakInBreakOut.getBreakOutStr().isEmpty())
		{
			updated = null;
			try
			{
				TimezoneTime time = new TimezoneTime();
				breakInBreakOut.setBreakOutStr(time.getDateAccordingToGMT(breakInBreakOut.getBreakOutStr(), breakInBreakOut.getLocationId(), em));
				
				updated = formatter.parse(breakInBreakOut.getBreakOutStr());
			}
			catch (ParseException e)
			{
				logger.severe(e);
			}
			breakInBreakOut.setBreakOut(updated);
		}
		
		if(breakInBreakOut.getId() >0)
		{
			breakInBreakOut.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			breakInBreakOut = em.merge(breakInBreakOut);
			
			
		}else
		{
			breakInBreakOut.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			breakInBreakOut.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.persist(breakInBreakOut);
		}
		
		
		//need to Check here updated fields 
		// if find then it should insert in histroy table
		new InsertIntoHistory().insertBreakInBreakOutHistory(httpRequest, breakInBreakOut, em);
		
		return breakInBreakOut;
		
	}
	public static User updateUserForLoginWithoutTransaction(User u, int loginStatus, EntityManager em) {

		u.setIsAllowedLogin(loginStatus);
		u= em.merge(u);
		return u;

	}

	public List<UsersToLocation> getUsersToLocationByUserId(EntityManager em, String userId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UsersToLocation> criteria = builder.createQuery(UsersToLocation.class);
		Root<UsersToLocation> r = criteria.from(UsersToLocation.class);
		TypedQuery<UsersToLocation> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToLocation_.usersId), userId)));
		List<UsersToLocation> result = (List<UsersToLocation>) query.getResultList();
		return result;
	}

	public  List<StaffMessaging> getUsersToMessagingByUserId(EntityManager em, String userId,String locationsId)
	{
		List<StaffMessaging> list =null;
		
		List<Integer>  result = getAllStaffMessaging(em, userId, locationsId);
		if(result!=null && result.size()>0){
			list = new ArrayList<StaffMessaging>();
			for(int messagingId :result){
				StaffMessaging staffMessaging = em.find(StaffMessaging.class, messagingId);
				if(staffMessaging!=null){
					list.add(staffMessaging);
				}
				
			}
		}
		
		 
		return list;
	}
	
	public  List<StaffMessaging> getUsersToMessagingBylocationId(EntityManager em, String locationsId)
	{
		List<StaffMessaging> list =null;
		
		List<Integer>  result = getAllStaffMessagingForLocationId(em,  locationsId);
		if(result!=null && result.size()>0){
			list = new ArrayList<StaffMessaging>();
			for(int messagingId :result){
				StaffMessaging staffMessaging = em.find(StaffMessaging.class, messagingId);
				if(staffMessaging!=null){
					list.add(staffMessaging);
				}
				
				
			}
		}
		
		 
		return list;
	}
	public  List<Role> getAllManagerRole(EntityManager em)
	{
	String query = "SELECT r FROM `roles` r  "
			+ " where r.role_name in ('Account Admin','Business Admin','POS Supervisor')";

	List<Role> result = em.createQuery(query,Role.class).getResultList();

	 return result;
	}
	
	public  List<String> getAllUserToRole(EntityManager em, String roleIds)
	{
		String query = "select distinct users_id from users_to_roles where roles_id in (" + roleIds + ") ";
		List<String> result = em.createNativeQuery(query).getResultList();
		return result;
	}
	
	public  List<Integer> getAllStaffMessaging(EntityManager em, String userId,String locationId)
	{
		String query = "SELECT distinct sm.id FROM staff_messaging sm "
				+ " join users_to_messaging utm on utm.staff_messaging_id=sm.id "
				+ " where utm.users_id =? and utm.status='A' and sm.status ='A' and sm.locations_id= ? order by sm.id desc limit 0,10 ";
		List<Integer> result = em.createNativeQuery(query).setParameter(1, userId).setParameter(2, locationId).getResultList();
		return result;
	}
	
	public  List<Integer> getAllStaffMessagingForLocationId(EntityManager em,String locationId)
	{
		String query = "SELECT distinct sm.id FROM staff_messaging sm "
				+ " join users_to_messaging utm on utm.staff_messaging_id=sm.id "
				+ " where   utm.status='A' and sm.status ='A' and sm.locations_id= ? order by sm.id desc limit 0,10 ";
		List<Integer> result = em.createNativeQuery(query) .setParameter(1, locationId).getResultList();
		return result;
	}
	
	public  StaffMessagingPacket addStaffMessaging(EntityManager em, StaffMessagingPacket staffMessagingPacket)
	{
		StaffMessaging messaging = staffMessagingPacket.getStaffMessaging();
		messaging.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		messaging.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		
		em.persist(messaging);
		
		staffMessagingPacket.setStaffMessaging(messaging);
		
			List<String> usersId =getAllUserToRole(em, messaging.getRoleList());
			if(usersId!=null && usersId.size()>0){
				for(String id :usersId){
					UsersToMessaging usersToMessaging = new UsersToMessaging();
					usersToMessaging.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					usersToMessaging.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					usersToMessaging.setCreatedBy(messaging.getCreatedBy());
					usersToMessaging.setUpdatedBy(messaging.getUpdatedBy());
					usersToMessaging.setUsersId(id);
					usersToMessaging.setStatus("A");
					usersToMessaging.setStaffMessagingId(messaging.getId());
					em.persist(usersToMessaging);
				}
			}
		
		
		 return staffMessagingPacket;
	}
	
	public  StaffMessagingPacket updateStaffMessaging(EntityManager em, StaffMessagingPacket staffMessagingPacket)
	{
		StaffMessaging messaging = staffMessagingPacket.getStaffMessaging();
		messaging.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		messaging.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		
		messaging = em.merge(messaging);
		
		staffMessagingPacket.setStaffMessaging(messaging);
		// get all users corresponding to roles and insert data for that into db
		// delete all previous user_to_message by updating their staus to D
		deleteAllUserToMessage(em, messaging.getId());
		List<String> usersId =getAllUserToRole(em, messaging.getRoleList());
			if(usersId!=null && usersId.size()>0){
				for(String id :usersId){ 
					UsersToMessaging usersToMessaging  = getUserToMessageByUserIdMessageId(em, messaging.getId(), id);
					if(usersToMessaging!=null){
						usersToMessaging.setStatus("A");
						usersToMessaging= em.merge(usersToMessaging);
						
					}else{
						UsersToMessaging usersToMessaging2 = new UsersToMessaging();
						usersToMessaging2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						usersToMessaging2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						usersToMessaging2.setCreatedBy(messaging.getCreatedBy());
						usersToMessaging2.setUpdatedBy(messaging.getUpdatedBy());
						usersToMessaging2.setUsersId(id);
						usersToMessaging2.setStatus("A");
						usersToMessaging2.setStaffMessagingId(messaging.getId());
						em.persist(usersToMessaging2);
					}
					
				}
			}
			
		
		
		 return staffMessagingPacket;
	}
	
	
	public  StaffMessagingPacket deleteStaffMessaging(EntityManager em, StaffMessagingPacket staffMessagingPacket)
	{
		StaffMessaging messaging = em.find(StaffMessaging.class, staffMessagingPacket.getStaffMessaging().getId()) ;
		messaging.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		messaging.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		messaging.setStatus("D");
		messaging = em.merge(messaging);
		
		staffMessagingPacket.setStaffMessaging(messaging);
		// get all users corresponding to roles and insert data for that into db
		// delete all previous user_to_message by updating their staus to D
		deleteAllUserToMessage(em, messaging.getId());
		
		 return staffMessagingPacket;
	}
	
	private boolean deleteAllUserToMessage(EntityManager em, int id)
	{
		String query = "update users_to_messaging set status='D' where staff_messaging_id=? ";
		int result = em.createNativeQuery(query).setParameter(1, id).executeUpdate();
		if(result>0){
			return true;
		}
		return false;
	}
	public  UsersToMessaging getUserToMessageByUserIdMessageId(EntityManager em, int messageId,String userId)
	{
		try {
			String queryString = "SELECT u FROM UsersToMessaging u  where u.usersId = ? and u.staffMessagingId =?  ";
			TypedQuery<UsersToMessaging> query = em.createQuery(queryString, UsersToMessaging.class)
					.setParameter(1, userId).setParameter(2, messageId);
			return query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}
}

	