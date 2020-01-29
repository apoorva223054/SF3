/*
 * 
 */
package com.nirvana.services.tipping;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.jaxrs.EmployeeOperationalHoursWithTotalHours;
import com.nirvanaxp.services.jaxrs.OrderHeaderCalculation;
import com.nirvanaxp.services.jaxrs.packets.CoursePacket;
import com.nirvanaxp.services.jaxrs.packets.DepartmentPacket;
import com.nirvanaxp.services.jaxrs.packets.EmployeeMasterPacket;
import com.nirvanaxp.services.jaxrs.packets.JobRolesPacket;
import com.nirvanaxp.services.jaxrs.packets.OperationalShiftSchedulePacket;
import com.nirvanaxp.services.jaxrs.packets.TipClassPacket;
import com.nirvanaxp.services.jaxrs.packets.TipPoolBasisPacket;
import com.nirvanaxp.services.jaxrs.packets.TipPoolPacket;
import com.nirvanaxp.services.jaxrs.packets.TipPoolRulesPacket;
import com.nirvanaxp.services.jaxrs.packets.UserPostPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.catalog.course.Course;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.Item_;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.orders.OperationalShiftSchedule;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.tip.Department;
import com.nirvanaxp.types.entities.tip.EmployeeMaster;
import com.nirvanaxp.types.entities.tip.EmployeeMasterHistory;
import com.nirvanaxp.types.entities.tip.EmployeeMasterToJobRoles;
import com.nirvanaxp.types.entities.tip.EmployeeMaster_;
import com.nirvanaxp.types.entities.tip.JobRoles;
import com.nirvanaxp.types.entities.tip.OrderIndirectTipByTipPool;
import com.nirvanaxp.types.entities.tip.OrderIndirectTipByTipPool_;
import com.nirvanaxp.types.entities.tip.TipClass;
import com.nirvanaxp.types.entities.tip.TipDistribution;
import com.nirvanaxp.types.entities.tip.TipDistribution_;
import com.nirvanaxp.types.entities.tip.TipPool;
import com.nirvanaxp.types.entities.tip.TipPoolBasis;
import com.nirvanaxp.types.entities.tip.TipPoolRules;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

// TODO: Auto-generated Javadoc
/**
 * The Class NiravanaXpTippingBean.
 */
public class NiravanaXpTippingBean
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(OrderHeaderCalculation.class.getName());

	/**
	 * Gets the all department by location id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the all department by location id
	 */
	public List<Department> getAllDepartmentByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		String queryString = "select b from Department b where b.locationId= ? and b.status not in('D') ";
		List<Department> resultSet = null;

		Query query = em.createQuery(queryString).setParameter(1, locationId);
		resultSet = query.getResultList();

		return resultSet;

	}

	/**
	 * Gets the department by id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the department by id
	 */
	public Department getDepartmentById(HttpServletRequest httpRequest, EntityManager em, String id)
	{

		Department resultSet = em.find(Department.class, id);
		return resultSet;

	}

	/**
	 * Adds the updat department.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param departmentPacket
	 *            the department packet
	 * @return the department
	 */
	public Department addUpdatDepartment(HttpServletRequest httpRequest, EntityManager em, Department department )
	{
		// TODO Auto-generated method stub

		 
		if (department.getId() == null || department.getId().equals("null") || department.getId().equals("0"))
		{
			department.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			department.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			department.setId(new StoreForwardUtility().generateUUID());

		}
		else
		{
			department.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}

		department=em.merge(department);
		return department;

	}
	
	public List<Department> addMultipleLocationDepartment(EntityManager em, Department department, DepartmentPacket packet,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = packet.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		List<Department> newList = new ArrayList<Department>();
		Department global = null;
		if (department != null && packet.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
//			course.setLocationsId(baseLocation.getId());

			global = addUpdatDepartment(request, em, packet.getDepartments());
			packet.setDepartments(global);
			packet.setLocationsListId("");
			

			newList.add(global);
			// now add/update child location
			for (String locationId : locationIds) {
				
				if (!locationId.equals((baseLocation.getId()))) {
					String json = new StoreForwardUtility().returnJsonPacket(packet, "DepartmentPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
							Integer.parseInt(packet.getMerchantId()));
					Department local = new Department().getDepartments(department);
					local.setGlobalId(global.getId());
					local.setLocationId(locationId);
					Department department2 = addUpdatDepartment( request, em, local);
					packet.setDepartments(department2);
					packet.setLocalServerURL(0);
					String json2 = new StoreForwardUtility().returnJsonPacket(packet, "DepartmentPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request, locationId,
							Integer.parseInt(packet.getMerchantId()));

				}
			}
		}

		return newList;
	}
	private Department getDepartmentByGlobalIdAndLocationId(EntityManager em, String locationId, String globalId) {
		try {
			String queryString = "select s from Department s where s.globalId =? and s.locationId=? and s.status!='D' ";
			TypedQuery<Department> query = em.createQuery(queryString, Department.class).setParameter(1, globalId)
					.setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			// todo shlok need to handle exception in below line

			logger.severe(e);

		}
		return null;
	}
	private List<Department> getDepartmentByGlobalId(EntityManager em, String globalId) {
		try {
			String queryString = "select s from Department s where s.globalId =?  and s.status!='D' ";
			TypedQuery<Department> query = em.createQuery(queryString, Department.class).setParameter(1, globalId);
			return query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);
		}
		return null;
	}
	Department deleteDepartment(Department d, HttpServletRequest httpRequest, EntityManager em) throws Exception {

		Department u = (Department) new CommonMethods().getObjectById("Department", em, Department.class, d.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		u = em.merge(u);
		return u;
	}
	public Department deleteMultipleLocationDepartment(EntityManager em, Department department, DepartmentPacket packet,
			HttpServletRequest request) throws Exception {
		// delete baselocation
		department = deleteDepartment(department, request, em);

		// get all sublocations
		List<Department> departments = getDepartmentByGlobalId(em,department.getId());
		// delete sublocation
		for (Department d2 : departments) {
			Department c = deleteDepartment(d2, request, em);

		}
		return department;
	}
	public List<Department> updateMultipleLocationDepartment(EntityManager em, Department department, DepartmentPacket packet,
			HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = packet.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		List<Department> newList = new ArrayList<Department>();
		Department global = null;
		if (department != null && packet.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
//			course.setLocationsId(baseLocation.getId());

			global = addUpdatDepartment(request, em, packet.getDepartments());
			packet.setDepartments(global);
			packet.setLocationsListId("");
			

			newList.add(global);
			// now add/update child location
			for (String locationId : locationIds) {
				
				if (!locationId.equals((baseLocation.getId()))) {
					String json = new StoreForwardUtility().returnJsonPacket(packet, "DepartmentPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
							Integer.parseInt(packet.getMerchantId()));
					Department local = new Department().getDepartments(department);
					Department departmentLocal = getDepartmentByGlobalIdAndLocationId(em, locationId, global.getId());
					if (departmentLocal != null && departmentLocal.getId() != null) {
						local.setGlobalId(global.getId());
						local.setId(departmentLocal.getId());
					} else {
						local.setGlobalId(global.getId());
					}

					 
					 
					local.setLocationId(locationId);
					Department department2 = addUpdatDepartment( request, em, local);
					packet.setDepartments(department2);
					packet.setLocalServerURL(0);
					String json2 = new StoreForwardUtility().returnJsonPacket(packet, "DepartmentPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request, locationId,
							Integer.parseInt(packet.getMerchantId()));

				}
			}
		}

		return newList;
	}

	/**
	 * Gets the all job roles by location id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the all job roles by location id
	 */
	public List<JobRoles> getAllJobRolesByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		String queryString = "select b from JobRoles b where b.locationId= ? and b.status not in('D')";
		List<JobRoles> resultSet = null;

		Query query = em.createQuery(queryString).setParameter(1, locationId);
		resultSet = query.getResultList();

		return resultSet;

	}

	/**
	 * Gets the job roles by id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the job roles by id
	 */
	public JobRoles getJobRolesById(HttpServletRequest httpRequest, EntityManager em, String id)
	{

		JobRoles resultSet = em.find(JobRoles.class, id);
		return resultSet;

	}

	/**
	 * Adds the updat job roles.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param packet
	 *            the packet
	 * @return the list
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public List<JobRoles> addUpdatJobRoles(HttpServletRequest httpRequest, EntityManager em, JobRolesPacket packet) throws NumberFormatException, Exception
	{
		// TODO Auto-generated method stub

		List<JobRoles> list = new ArrayList<JobRoles>();
		for (JobRoles o : packet.getJobRoles())
		{
			if (o.getId() == null)
			{
				o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				o.setId(new StoreForwardUtility().generateDynamicIntId(em,packet.getLocationId(),  httpRequest, "job_roles"));
				
			}
			else
			{
				o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			}

			o=em.merge(o);
			list.add(o);

		}

		return list;

	}

	/**
	 * Gets the all tip class.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the all tip class
	 */
	public List<TipClass> getAllTipClass(HttpServletRequest httpRequest, EntityManager em)
	{

		String queryString = "select b from TipClass b where b.status not in('D')";
		List<TipClass> resultSet = null;

		Query query = em.createQuery(queryString);
		resultSet = query.getResultList();

		return resultSet;

	}

	/**
	 * Gets the tip class by id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the tip class by id
	 */
	public TipClass getTipClassById(HttpServletRequest httpRequest, EntityManager em, int id)
	{

		TipClass resultSet = em.find(TipClass.class, id);
		return resultSet;

	}

	/**
	 * Adds the updat tip class.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param packet
	 *            the packet
	 * @return the tip class
	 */
	public TipClass addUpdatTipClass(HttpServletRequest httpRequest, EntityManager em, TipClassPacket packet)
	{
		// TODO Auto-generated method stub

		TipClass o = packet.getTipClass();
		if (o.getId() == 0)
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		else
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}

		em.merge(o);
		return o;

	}

	/**
	 * Gets the all tip pool by location id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the all tip pool by location id
	 */
	public List<TipPool> getAllTipPoolByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		String queryString = "select b from TipPool b where b.locationId= ? and b.status not in('D') ";
		List<TipPool> resultSet = null;

		Query query = em.createQuery(queryString).setParameter(1, locationId);
		resultSet = query.getResultList();

		return resultSet;

	}

	/**
	 * Gets the tip pool by id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the tip pool by id
	 */
	public TipPool getTipPoolById(HttpServletRequest httpRequest, EntityManager em, int id)
	{

		TipPool resultSet = em.find(TipPool.class, id);
		return resultSet;

	}

	/**
	 * Adds the updat tip pool.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param packet
	 *            the packet
	 * @return the tip pool
	 */
	public TipPool addUpdatTipPool(HttpServletRequest httpRequest, EntityManager em, TipPoolPacket packet)
	{
		// TODO Auto-generated method stub

		TipPool o = packet.getTipPool();
		if (o.getId() == 0)
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		else
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}

		em.merge(o);
		return o;

	}

	/**
	 * Gets the all tip pool basis.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the all tip pool basis
	 */
	public List<TipPoolBasis> getAllTipPoolBasis(HttpServletRequest httpRequest, EntityManager em)
	{

		String queryString = "select b from TipPoolBasis b where b.status not in('D')";
		List<TipPoolBasis> resultSet = null;

		Query query = em.createQuery(queryString);
		resultSet = query.getResultList();

		return resultSet;

	}

	/**
	 * Gets the all tip pool rules.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the all tip pool rules
	 */
	public List<TipPoolRules> getAllTipPoolRules(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		String queryString = "select b from TipPoolRules b where b.locationId= ? and b.status not in('D')";
		List<TipPoolRules> resultSet = null;

		Query query = em.createQuery(queryString).setParameter(1, locationId);
		resultSet = query.getResultList();

		return resultSet;

	}

	/**
	 * Gets the tip pool basis by id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the tip pool basis by id
	 */
	public TipPoolBasis getTipPoolBasisById(HttpServletRequest httpRequest, EntityManager em, int id)
	{

		TipPoolBasis resultSet = em.find(TipPoolBasis.class, id);
		return resultSet;

	}

	/**
	 * Gets the tip pool rules by id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the tip pool rules by id
	 */
	public TipPoolRules getTipPoolRulesById(HttpServletRequest httpRequest, EntityManager em, int id)
	{

		TipPoolRules resultSet = em.find(TipPoolRules.class, id);
		return resultSet;

	}

	/**
	 * Adds the updat tip pool basis.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param packet
	 *            the packet
	 * @return the tip pool basis
	 */
	public TipPoolBasis addUpdatTipPoolBasis(HttpServletRequest httpRequest, EntityManager em, TipPoolBasisPacket packet)
	{
		// TODO Auto-generated method stub

		TipPoolBasis o = packet.getTipPoolBasis();
		if (o.getId() == 0)
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		else
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		em.merge(o);
		return o;

	}

	/**
	 * Adds the updat tip pool rules.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param packet
	 *            the packet
	 * @return the tip pool rules
	 */
	public TipPoolRules addUpdatTipPoolRules(HttpServletRequest httpRequest, EntityManager em, TipPoolRulesPacket packet)
	{
		// TODO Auto-generated method stub

		TipPoolRules o = packet.getTipPoolRules();

		if (o.getId() == 0)
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		else
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		em.merge(o);
		return o;

	}

	/**
	 * Gets the all employee master.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the all employee master
	 */
	public List<EmployeeMaster> getAllEmployeeMaster(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{
		List<EmployeeMaster> resultList = new ArrayList<EmployeeMaster>();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeMaster> criteria = builder.createQuery(EmployeeMaster.class);
		Root<EmployeeMaster> r = criteria.from(EmployeeMaster.class);
		TypedQuery<EmployeeMaster> query = em
				.createQuery(criteria.select(r).where(builder.notEqual(r.get(EmployeeMaster_.status), "D"), builder.equal(r.get(EmployeeMaster_.locationsId), locationId)));
		if (query.getResultList() != null)
		{
			for (EmployeeMaster empMaster : query.getResultList())
			{
				if (empMaster != null)
				{

					List<UsersToLocation> resultSet = null;
					try
					{
						String queryString = "select b from UsersToLocation b where b.status not in('D','I') and b.usersId = '" + empMaster.getUserId()+"'";

						Query query1 = em.createQuery(queryString);
						resultSet = query1.getResultList();

					}
					catch (Exception e)
					{
						// todo shlok need
						// handel proper exception

						logger.severe("No result Found for Users To Location");
					}

					
					List<EmployeeMasterToJobRoles> resultSetRolls = null;
					try
					{
						String queryStringRolls = "select b from EmployeeMasterToJobRoles b where b.status not in('D','I') and b.userId = '" + empMaster.getUserId()+"'";

						Query queryRolls = em.createQuery(queryStringRolls);
						resultSetRolls = queryRolls.getResultList();

					}
					catch (Exception e)
					{
						// todo shlok need
						// handel proper exception

						logger.severe("No result Found for Employee Master To Job Rolls ");
					}
					
					if (resultSetRolls != null && resultSetRolls.size() > 0)
					{
						empMaster.setEmployeeMasterToJobRoles(resultSetRolls);
					}
					
					
					
					
					if (resultSet != null && resultSet.size() > 0)
					{
						for (UsersToLocation locationUsersToLocation : resultSet)
						{
							if (locationUsersToLocation.getLocationsId().equals(locationId))
							{
								String queryString = "select b from User b where b.id= ? ";
								User user = null;
								Query query1 = em.createQuery(queryString).setParameter(1, empMaster.getUserId());
								user =  (User) query1.getSingleResult();
								
								if (user != null)
								{
									empMaster.setEmpName(user.getFirstName() + " " + user.getLastName());
								}

								resultList.add(empMaster);
							}

						}

					}
					
					

				}

			}
		}

		return resultList;
	}

	/**
	 * Gets the all employee master by locations id and is tipped employee.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param isTippedEmployee
	 *            the is tipped employee
	 * @return the all employee master by locations id and is tipped employee
	 */
	public List<EmployeeMaster> getAllEmployeeMasterByLocationsIdAndIsTippedEmployee(HttpServletRequest httpRequest, EntityManager em, String locationId, int isTippedEmployee)
	{
		List<EmployeeMaster> resultList = new ArrayList<EmployeeMaster>();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeMaster> criteria = builder.createQuery(EmployeeMaster.class);
		Root<EmployeeMaster> r = criteria.from(EmployeeMaster.class);
		TypedQuery<EmployeeMaster> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(EmployeeMaster_.status), "D"), builder.equal(r.get(EmployeeMaster_.locationsId), locationId),
				builder.equal(r.get(EmployeeMaster_.isTippedEmployee), isTippedEmployee)));
		if (query.getResultList() != null)
		{
			for (EmployeeMaster empMaster : query.getResultList())
			{
				if (empMaster != null)
				{

					List<UsersToLocation> resultSet = null;
					try
					{
						String queryString = "select b from UsersToLocation b where b.status not in('D','I') and b.usersId = " + empMaster.getUserId();

						Query query1 = em.createQuery(queryString);
						resultSet = query1.getResultList();

					}
					catch (Exception e)
					{
						// todo shlok need
						// handel proper exception

						logger.severe("No result Found for Users To Location");
					}

					if (resultSet != null && resultSet.size() > 0)
					{
						for (UsersToLocation locationUsersToLocation : resultSet)
						{
							if (locationUsersToLocation.getLocationsId() == locationId)
							{
								String queryString = "select b from User b where b.id= ? ";
								User user = null;
								Query query1 = em.createQuery(queryString).setParameter(1, empMaster.getUserId());
								user =  (User) query1.getSingleResult();
								if (user != null)
								{
									empMaster.setEmpName(user.getFirstName() + " " + user.getLastName());
								}

								resultList.add(empMaster);
							}

						}

					}

				}

			}
		}

		return resultList;
	}

	/**
	 * Gets the all operational shift schedule.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the all operational shift schedule
	 */
	public List<OperationalShiftSchedule> getAllOperationalShiftSchedule(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		String queryString = "select b from OperationalShiftSchedule b where b.locationId= ? and b.status not in('D')";
		List<OperationalShiftSchedule> resultSet = null;
		Query query = em.createQuery(queryString).setParameter(1, locationId);
		try
		{
			resultSet = query.getResultList();
		}
		catch (Exception e)
		{
			logger.severe("No shift define for location " + locationId);
		}

		return resultSet;

	}

	/**
	 * Gets the all operational shift schedule by id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the all operational shift schedule by id
	 */
	public OperationalShiftSchedule getAllOperationalShiftScheduleById(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		String queryString = "select b from OperationalShiftSchedule b where b.id= ? and b.status not in('D')";
		Query query = em.createQuery(queryString).setParameter(1, locationId);

		return (OperationalShiftSchedule) query.getSingleResult();
	}

	/**
	 * Adds the updat employee master.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param packet
	 *            the packet
	 * @return the employee master
	 */
	public EmployeeMaster addUpdatEmployeeMaster(HttpServletRequest httpRequest, EntityManager em, EmployeeMasterPacket packet)
	{

		EmployeeMaster o = packet.getEmployeeMaster();
		try
		{
			String queryString = "select b from EmployeeMaster b where b.userId= ? and b.status not in('D')and b.shiftId= ?";
			EmployeeMaster resultSet = null;

			Query query = em.createQuery(queryString).setParameter(1, o.getUserId()).setParameter(2, o.getShiftId());
			resultSet = (EmployeeMaster) query.getSingleResult();

			if (resultSet != null)
			{
				o.setId(resultSet.getId());
			}
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception

			logger.severe("No result Found for Employee Master"+e);
		}

		
		try
		{
			String queryString = "select b from EmployeeMasterToJobRoles b where b.userId= ? and b.status not in ('D')";
			List<EmployeeMasterToJobRoles> resultSet = null;

			Query query = em.createQuery(queryString).setParameter(1, o.getUserId());
			resultSet = query.getResultList();

			if (resultSet != null)
			{
				for(EmployeeMasterToJobRoles employeeMasterToJobRoles : resultSet)
				{
					employeeMasterToJobRoles.setStatus("D");
					employeeMasterToJobRoles.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					employeeMasterToJobRoles = em.merge(employeeMasterToJobRoles);
				}
				
				
			}
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception

			logger.severe("No result Found for Employee Master"+e);
		}
		
		if(packet.getEmployeeMaster().getEmployeeMasterToJobRoles() != null && packet.getEmployeeMaster().getEmployeeMasterToJobRoles().size() > 0)
		{
		
			for(EmployeeMasterToJobRoles employeeMasterToJobRoles : packet.getEmployeeMaster().getEmployeeMasterToJobRoles())
			{
				if (employeeMasterToJobRoles.getId() == 0)
				{
					employeeMasterToJobRoles.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					employeeMasterToJobRoles.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					em.persist(employeeMasterToJobRoles);
				}
				else
				{
					employeeMasterToJobRoles.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					employeeMasterToJobRoles = em.merge(employeeMasterToJobRoles);
				}		
				
				
			}
			
			
		}
		
		
		if (o.getId() == null)
		{
			o.setId(new StoreForwardUtility().generateUUID());
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		else
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		
		o=em.merge(o);
		// insert into history
		EmployeeMasterHistory employeeMasterHistory = new EmployeeMasterHistory().employeeMasterHistoryObject(o);
		em.persist(employeeMasterHistory);
		
		if (o.getUserId()!=null )
		{

			String queryString = "select b from User b where b.id= ? ";
			User user = null;
			Query query = em.createQuery(queryString).setParameter(1, o.getUserId());
			user =  (User) query.getSingleResult();
			
			if (user != null)
			{
				user.setIsTippedEmployee(o.getIsTippedEmployee());
				user.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				user = em.merge(user);

				UserPostPacket userPostPacket = new UserPostPacket();
				userPostPacket.setUser(user);
				userPostPacket.setMerchantId(packet.getMerchantId());
				userPostPacket.setClientId(packet.getClientId());
				userPostPacket.setLocationId(packet.getLocationId());
				userPostPacket.setEchoString(packet.getEchoString());
				userPostPacket.setSchemaName(packet.getSchemaName());
				userPostPacket.setSessionId(packet.getSessionId());
				userPostPacket.setIdOfSessionUsedByPacket(packet.getIdOfSessionUsedByPacket());

				sendPacketForBroadcast(httpRequest, userPostPacket, POSNServiceOperations.UserManagementService_add.name(), false);

			}
		}
		return o;
	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param userPostPacket
	 *            the user post packet
	 * @param operationName
	 *            the operation name
	 * @param shouldBroadcastObj
	 *            the should broadcast obj
	 */
	private void sendPacketForBroadcast(HttpServletRequest httpRequest, UserPostPacket userPostPacket, String operationName, boolean shouldBroadcastObj)
	{

		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
			String internalJson = objectMapper.writeValueAsString(userPostPacket.getUser());

			operationName = ServiceOperationsUtility.getOperationName(operationName);
			MessageSender messageSender = new MessageSender();

			if (shouldBroadcastObj)
			{
				messageSender.sendMessage(httpRequest, userPostPacket.getClientId(), POSNServices.UserManagementService.name(), operationName, internalJson, userPostPacket.getMerchantId(),
						userPostPacket.getLocationId(), userPostPacket.getEchoString(), userPostPacket.getSchemaName());
			}
			else
			{
				messageSender.sendMessage(httpRequest, userPostPacket.getClientId(), POSNServices.UserManagementService.name(), operationName, null, userPostPacket.getMerchantId(),
						userPostPacket.getLocationId(), userPostPacket.getEchoString(), userPostPacket.getSchemaName());
			}

		}
		catch (JsonGenerationException e)
		{
			// todo shlok need
			// handel proper exception

			logger.severe(httpRequest, e);
		}
		catch (JsonMappingException e)
		{

			// todo shlok need
			// handel proper exception

			logger.severe(httpRequest, e);
		}
		catch (IOException e)
		{

			// todo shlok need
			// handel proper exception
			logger.severe(httpRequest, e);
		}

	}

	/**
	 * Adds the updat operational shift schedule.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param packet
	 *            the packet
	 * @return the operational shift schedule
	 */
	public OperationalShiftSchedule addUpdatOperationalShiftSchedule(HttpServletRequest httpRequest, EntityManager em, OperationalShiftSchedulePacket packet)
	{
		OperationalShiftSchedule o = packet.getOperationalShiftSchedule();
		if (o.getId() == null)
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			o.setId(new StoreForwardUtility().generateUUID());

		}
		else
		{
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		}
		em.merge(o);
		return o;
	}

	/**
	 * Gets the employee master by id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the employee master by id
	 */
	public EmployeeMaster getEmployeeMasterById(HttpServletRequest httpRequest, EntityManager em, int id)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<EmployeeMaster> criteria = builder.createQuery(EmployeeMaster.class);
		Root<EmployeeMaster> r = criteria.from(EmployeeMaster.class);
		TypedQuery<EmployeeMaster> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(EmployeeMaster_.id), id)));
		
		
		EmployeeMaster employeeMaster = query.getSingleResult();
		
		
		List<EmployeeMasterToJobRoles> resultSetRolls = null;
		try
		{
			String queryStringRolls = "select b from EmployeeMasterToJobRoles b where b.status not in('D','I') and b.userId = " + employeeMaster.getUserId();

			Query queryRolls = em.createQuery(queryStringRolls);
			resultSetRolls = queryRolls.getResultList();

		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception

			logger.severe("No result Found for Employee Master To Job Rolls ");
		}
		
		if (resultSetRolls != null && resultSetRolls.size() > 0)
		{
			employeeMaster.setEmployeeMasterToJobRoles(resultSetRolls);
		}
		
		return employeeMaster;
	}

	/**
	 * Adds the updat tip pooling for user.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param o
	 *            the o
	 */
//	int i=0;
	public void addUpdatTipPoolingForUser(HttpServletRequest httpRequest, EntityManager em, TipDistribution newTipDistribution,BatchDetail batchDetail)
	{
		// fill tipDistribution Table with distributed tip per user for all shifts
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TipDistribution> criteria = builder.createQuery(TipDistribution.class);
		Root<TipDistribution> r = criteria.from(TipDistribution.class);
		TypedQuery<TipDistribution> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(TipDistribution_.userId), newTipDistribution.getUserId()),
				builder.equal(r.get(TipDistribution_.nirvanaxpBatchId), newTipDistribution.getNirvanaxpBatchId()), builder.equal(r.get(TipDistribution_.shiftId), newTipDistribution.getShiftId()), builder.equal(r.get(TipDistribution_.jobRoleId), newTipDistribution.getJobRoleId()), builder.equal(r.get(TipDistribution_.sectionId), newTipDistribution.getSectionId()), builder.equal(r.get(TipDistribution_.orderSourceGroupId), newTipDistribution.getOrderSourceGroupId())));

		TipDistribution tipDistribution = new TipDistribution();
		try
		{
			tipDistribution = query.getSingleResult();
		}
		catch (Exception e)
		{
			tipDistribution = null;
		}
		
		if (tipDistribution != null)
		{
			if (newTipDistribution.getDirectCardTip() != null)
				tipDistribution.setDirectCardTip(tipDistribution.getDirectCardTip().add(newTipDistribution.getDirectCardTip()));
			else
				tipDistribution.setDirectCardTip(tipDistribution.getDirectCardTip());
			if (newTipDistribution.getDirectCardTip() != null)
				tipDistribution.setDirectCashTip(tipDistribution.getDirectCashTip().add(newTipDistribution.getDirectCashTip()));
			else
				tipDistribution.setDirectCashTip(tipDistribution.getDirectCashTip());

			if (newTipDistribution.getDirectCardTip() != null){
				tipDistribution.setDirectCreditTermTip(tipDistribution.getDirectCreditTermTip().add(newTipDistribution.getDirectCreditTermTip()));
			}
			else{
				tipDistribution.setDirectCreditTermTip(tipDistribution.getDirectCreditTermTip());
			}

			tipDistribution.setIndirectCardTip(tipDistribution.getIndirectCardTip().add(newTipDistribution.getIndirectCardTip()));
			tipDistribution.setIndirectCashTip(tipDistribution.getIndirectCashTip().add(newTipDistribution.getIndirectCashTip()));
			tipDistribution.setIndirectCreditTermTip(tipDistribution.getIndirectCreditTermTip().add(newTipDistribution.getIndirectCreditTermTip()));
			tipDistribution.setCardTotal(tipDistribution.getCardTotal().add(newTipDistribution.getCardTotal()));
			tipDistribution.setCashTotal(tipDistribution.getCashTotal().add(newTipDistribution.getCashTotal()));
			tipDistribution.setCreditTotal(tipDistribution.getCreditTotal().add(newTipDistribution.getCreditTotal()));
			tipDistribution.setBatchSalary(newTipDistribution.getBatchSalary());
			tipDistribution.setJobRoleId(newTipDistribution.getJobRoleId());
			tipDistribution.setHourlyRate(newTipDistribution.getHourlyRate());
			tipDistribution.setNoOfHours(newTipDistribution.getNoOfHours());
			tipDistribution.setSectionId(newTipDistribution.getSectionId());
			tipDistribution.setOrderSourceGroupId(newTipDistribution.getOrderSourceGroupId());
			em.merge(tipDistribution);
			if(!batchDetail.isTipSettle()){
			batchDetail.setIsTipCalculated("C");
			}else {
			batchDetail.setIsTipCalculated("F");
			}
			em.merge(batchDetail);
		}
		else
		{
			
			em.persist(newTipDistribution);
			if(!batchDetail.isTipSettle()){
				batchDetail.setIsTipCalculated("C");
				}else {
				batchDetail.setIsTipCalculated("F");
				}
			em.merge(batchDetail);
		}
	}

	/**
	 * Gets the order sift by open time.
	 *
	 * @param em
	 *            the em
	 * @param l
	 *            the l
	 * @param operationalShiftList
	 *            the operational shift list
	 * @param locationId
	 *            the location id
	 * @return the order sift by open time
	 * @throws ParseException
	 *             the parse exception
	 */
	public String getOrderSiftByOpenTime(EntityManager em, long l, List<OperationalShiftSchedule> operationalShiftList, String locationId) throws ParseException
	{
		// TODO Auto-generated method stub

		String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(l);
		orderTime = new TimezoneTime().getDateTimeFromGMTToLocation(em, orderTime, locationId);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String locationOrderTime[] = orderTime.split(" ");
		String shiftId = null;
		for (OperationalShiftSchedule operationalShift : operationalShiftList)
		{
			if (sdf.parse(operationalShift.getFromTime()).compareTo(sdf.parse(locationOrderTime[1])) < 0 && sdf.parse(operationalShift.getToTime()).compareTo(sdf.parse(locationOrderTime[1])) > 0)
			{
				shiftId = operationalShift.getId();
			}
		}
		 if(shiftId != null && (shiftId.length()==0 || shiftId.equals("0"))){return null;}else{	return shiftId;}
	}

	/**
	 * Gets the tip rules by validation.
	 *
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @param sectionId
	 *            the section id
	 * @param tipClassId
	 *            the tip class id
	 * @param poolRulesList
	 *            the pool rules list
	 * @return the tip rules by validation
	 */
	public List<TipPoolRules> getTipRulesByValidation(String orderSourceGroupId, String sectionId, int tipClassId, List<TipPoolRules> poolRulesList, String jobRoleId)
	{

		
		List<TipPoolRules> results = new ArrayList<TipPoolRules>();
		for (TipPoolRules tipPoolrule : poolRulesList)
		{
			if (tipPoolrule.getTipClassId() == tipClassId && tipPoolrule.getOrderSourceGroupId() == orderSourceGroupId && tipPoolrule.getSectionId() == sectionId && tipPoolrule.getJobRoleId().equals(jobRoleId))
			{
				results.add(tipPoolrule);
			}
			
		}

		return results;

	}

	
	/**
	 * Gets the employee operational hours by batch id and employee id and shift
	 * id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param batchId
	 *            the batch id
	 * @param employeeId
	 *            the employee id
	 * @param shiftId
	 *            the shift id
	 * @return the employee operational hours by batch id and employee id and
	 *         shift id
	 */
	public EmployeeOperationalHoursWithTotalHours getEmployeeOperationalHoursByBatchIdAndEmployeeIdAndShiftId(HttpServletRequest httpRequest, EntityManager em, int batchId, int employeeId, String shiftId,int jobRoleId)
	{
		// TODO Auto-generated method stub
		String queryString = "select eoh.employee_id,eoh.shift_id, TIME_TO_SEC( number_of_hours)/3600 as employee_hr, "
				             + " (select sum(TIME_TO_SEC(number_of_hours)) from employee_operational_hours new_eoh "
                             + " where  nirvanaxp_batch_id= ?  and new_eoh.employee_id=? and new_eoh.shift_id=? )/3600 "
				             + " as total_employee_hr  from employee_operational_hours eoh left join operational_shift_schedule "
                             + " os on os.id = eoh.shift_id  where nirvanaxp_batch_id= ? and eoh.employee_id=? and "
				             + " eoh.shift_id=?";
		List<Object[]> resultList = new ArrayList<Object[]>();
		try
		{
			resultList = em.createNativeQuery(queryString).setParameter(1, batchId).setParameter(2, employeeId).setParameter(3, shiftId)
					.setParameter(4, batchId).setParameter(5, employeeId).setParameter(6, shiftId).getResultList();
		}
		catch (Exception e)
		{
			logger.severe("getEmployeeOperationalHoursByBatchIdAndEmployeeIdAndShiftId ="+e);
		}

		EmployeeOperationalHoursWithTotalHours employeeOperationalHoursWithTotalHour = new EmployeeOperationalHoursWithTotalHours();

		if (resultList.size() > 0)
		{
			for (Object[] objRow : resultList)
			{
				int i = 0;
				
				employeeOperationalHoursWithTotalHour.setEmployeeId(((String) objRow[i]));
				i++;
				employeeOperationalHoursWithTotalHour.setShiftId(((String) objRow[i]));
				i++;
				
				employeeOperationalHoursWithTotalHour.setEmployeeHrSec(((BigDecimal) objRow[i]));
				i++;
				
				employeeOperationalHoursWithTotalHour.setTotalHrSec(((BigDecimal) objRow[i]));
			}
		}
		
		return employeeOperationalHoursWithTotalHour;

	}

	/**
	 * Calculated cash amt by item group.
	 *
	 * @param orderDetailItems
	 *            the order detail items
	 * @param itemGroupId
	 *            the item group id
	 * @return the big decimal
	 */
	public BigDecimal calculatedCashAmtByItemGroup(List<OrderDetailItem> orderDetailItems, String itemGroupId)
	{

		// TODO Auto-generated method stub
		BigDecimal totalAmount = new BigDecimal(0);
		if (orderDetailItems != null)
		{
			for (OrderDetailItem orderDetailItem : orderDetailItems)
			{
				if (orderDetailItem.getItemGroupId().equals(itemGroupId))
				{
					totalAmount = totalAmount.add(orderDetailItem.getTotal());
				}
			}
		}
		return totalAmount;

	}

	/**
	 * Adds the indirect tip order info for tip pool.
	 *
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @param tipPoolId
	 *            the tip pool id
	 * @param newIndirectCashTip
	 *            the new indirect cash tip
	 * @param newIndirectCardTip
	 *            the new indirect card tip
	 * @param newIndirectCreditTermTip
	 *            the new indirect credit term tip
	 * @param pendingCashTip
	 *            the pending cash tip
	 * @param pendingCardTip
	 *            the pending card tip
	 * @param pendingCreditTermTip
	 *            the pending credit term tip
	 * @param userId
	 *            the user id
	 * @return the order indirect tip by tip pool
	 */
	public OrderIndirectTipByTipPool addIndirectTipOrderInfoForTipPool(EntityManager em, String orderId, int tipPoolId, BigDecimal newIndirectCashTip, BigDecimal newIndirectCardTip,
			BigDecimal newIndirectCreditTermTip, BigDecimal pendingCashTip, BigDecimal pendingCardTip, BigDecimal pendingCreditTermTip, String userId)
	{

		OrderIndirectTipByTipPool newOrderTipPool = new OrderIndirectTipByTipPool();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderIndirectTipByTipPool> criteria = builder.createQuery(OrderIndirectTipByTipPool.class);
		Root<OrderIndirectTipByTipPool> r = criteria.from(OrderIndirectTipByTipPool.class);
		TypedQuery<OrderIndirectTipByTipPool> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderIndirectTipByTipPool_.orderId), orderId),
				builder.equal(r.get(OrderIndirectTipByTipPool_.tipPoolId), tipPoolId)));
		OrderIndirectTipByTipPool orderTipPool = new OrderIndirectTipByTipPool();
		try
		{
			orderTipPool = query.getSingleResult();
		}
		catch (Exception e)
		{
			orderTipPool = null;
			logger.severe("order indirect tip not found for tip pool id " + tipPoolId);
		}
		if (orderTipPool != null)
		{
			if (orderTipPool.getIndirectCardTip() != null)
				orderTipPool.setIndirectCardTip(newIndirectCardTip.add(orderTipPool.getIndirectCardTip()));
			else
				orderTipPool.setIndirectCardTip(newIndirectCardTip);
			if (orderTipPool.getIndirectCashTip() != null)
				orderTipPool.setIndirectCashTip(newIndirectCashTip.add(orderTipPool.getIndirectCashTip()));
			else
				orderTipPool.setIndirectCashTip(newIndirectCashTip);
			if (orderTipPool.getIndirectCreditTermTip() != null)
				orderTipPool.setIndirectCreditTermTip(newIndirectCreditTermTip.add(orderTipPool.getIndirectCreditTermTip()));
			else
				orderTipPool.setIndirectCreditTermTip(newIndirectCreditTermTip);

			if (orderTipPool.getPendingCardTip() != null)
				orderTipPool.setPendingCardTip(pendingCardTip.add(orderTipPool.getPendingCardTip()));
			else
				orderTipPool.setPendingCardTip(pendingCardTip);
			if (orderTipPool.getPendingCashTip() != null)
				orderTipPool.setPendingCashTip(pendingCashTip.add(orderTipPool.getPendingCashTip()));
			else
				orderTipPool.setPendingCashTip(pendingCashTip);
			if (orderTipPool.getPendingCreditTip() != null)
				orderTipPool.setPendingCreditTip(pendingCreditTermTip.add(orderTipPool.getPendingCreditTip()));
			else
				orderTipPool.setPendingCreditTip(pendingCreditTermTip);

			if(userId!=null){
			orderTipPool.setUpdatedBy(userId);
			}
			orderTipPool.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.merge(orderTipPool);
			return orderTipPool;

		}
		else
		{
			newOrderTipPool.setIndirectCardTip(newIndirectCardTip);
			newOrderTipPool.setIndirectCashTip(newIndirectCashTip);
			newOrderTipPool.setIndirectCreditTermTip(newIndirectCreditTermTip);
			newOrderTipPool.setPendingCardTip(pendingCardTip);
			newOrderTipPool.setPendingCashTip(pendingCashTip);
			newOrderTipPool.setPendingCreditTip(pendingCreditTermTip);
			newOrderTipPool.setDirectCardTip(new BigDecimal(0));
			newOrderTipPool.setDirectCashTip(new BigDecimal(0));
			newOrderTipPool.setDirectCreditTermTip(new BigDecimal(0));

			newOrderTipPool.setOrderId(orderId);
			newOrderTipPool.setTipPoolId(tipPoolId);
			if(userId!=null){
			newOrderTipPool.setUpdatedBy(userId);
			newOrderTipPool.setCreatedBy(userId);
			}
			newOrderTipPool.setStatus("A");
			newOrderTipPool.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			newOrderTipPool.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			em.persist(newOrderTipPool);
			return newOrderTipPool;

		}

	}

	public OrderIndirectTipByTipPool addDirectTipOrderInfoForTipPool(EntityManager em, String orderId, int tipPoolId, BigDecimal newDirectCashTip, BigDecimal newDirectCardTip,
			BigDecimal newDirectCreditTermTip, BigDecimal pendingCashTip, BigDecimal pendingCardTip, BigDecimal pendingCreditTermTip, String userId)
	{

		OrderIndirectTipByTipPool newOrderTipPool = new OrderIndirectTipByTipPool();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderIndirectTipByTipPool> criteria = builder.createQuery(OrderIndirectTipByTipPool.class);
		Root<OrderIndirectTipByTipPool> r = criteria.from(OrderIndirectTipByTipPool.class);
		TypedQuery<OrderIndirectTipByTipPool> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderIndirectTipByTipPool_.orderId), orderId),
				builder.equal(r.get(OrderIndirectTipByTipPool_.tipPoolId), tipPoolId)));
		OrderIndirectTipByTipPool orderTipPool = new OrderIndirectTipByTipPool();
		try
		{
			orderTipPool = query.getSingleResult();
		}
		catch (Exception e)
		{
			orderTipPool = null;
			logger.severe("order indirect tip not found for tip pool id " + tipPoolId);
		}
		if (orderTipPool != null)
		{
			if (orderTipPool.getDirectCardTip() != null)
				orderTipPool.setDirectCardTip(newDirectCardTip.add(orderTipPool.getDirectCardTip()));
			else
				orderTipPool.setDirectCardTip(newDirectCardTip);
			if (orderTipPool.getDirectCashTip() != null)
				orderTipPool.setDirectCashTip(newDirectCashTip.add(orderTipPool.getDirectCashTip()));
			else
				orderTipPool.setDirectCashTip(newDirectCashTip);
			if (orderTipPool.getDirectCreditTermTip() != null)
				orderTipPool.setDirectCreditTermTip(newDirectCreditTermTip.add(orderTipPool.getDirectCreditTermTip()));
			else
				orderTipPool.setDirectCreditTermTip(newDirectCreditTermTip);

			if (orderTipPool.getPendingCardTip() != null)
				orderTipPool.setPendingCardTip(pendingCardTip.add(orderTipPool.getPendingCardTip()));
			else
				orderTipPool.setPendingCardTip(pendingCardTip);
			if (orderTipPool.getPendingCashTip() != null)
				orderTipPool.setPendingCashTip(pendingCashTip.add(orderTipPool.getPendingCashTip()));
			else
				orderTipPool.setPendingCashTip(pendingCashTip);
			if (orderTipPool.getPendingCreditTip() != null)
				orderTipPool.setPendingCreditTip(pendingCreditTermTip.add(orderTipPool.getPendingCreditTip()));
			else
				orderTipPool.setPendingCreditTip(pendingCreditTermTip);

			orderTipPool.setUpdatedBy(userId);
			orderTipPool.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.merge(orderTipPool);
			return orderTipPool;

		}
		else
		{
			newOrderTipPool.setDirectCardTip(newDirectCardTip);
			newOrderTipPool.setDirectCashTip(newDirectCashTip);
			newOrderTipPool.setDirectCreditTermTip(newDirectCreditTermTip);
			newOrderTipPool.setPendingCardTip(pendingCardTip);
			newOrderTipPool.setPendingCashTip(pendingCashTip);
			newOrderTipPool.setPendingCreditTip(pendingCreditTermTip);
			newOrderTipPool.setIndirectCardTip(new BigDecimal(0));
			newOrderTipPool.setIndirectCashTip(new BigDecimal(0));
			newOrderTipPool.setIndirectCreditTermTip(new BigDecimal(0));
			
			newOrderTipPool.setOrderId(orderId);
			newOrderTipPool.setTipPoolId(tipPoolId);

			newOrderTipPool.setUpdatedBy(userId);
			newOrderTipPool.setCreatedBy(userId);
			newOrderTipPool.setStatus("A"); 
			newOrderTipPool.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			newOrderTipPool.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			em.persist(newOrderTipPool);
			return newOrderTipPool;

		}

	}
	public void UpdatTipPoolingForUser(HttpServletRequest httpRequest, EntityManager em, TipDistribution newTipDistribution)
	{
		
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TipDistribution> criteria = builder.createQuery(TipDistribution.class);
		Root<TipDistribution> r = criteria.from(TipDistribution.class);
		TypedQuery<TipDistribution> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(TipDistribution_.userId), newTipDistribution.getUserId()),
				builder.equal(r.get(TipDistribution_.nirvanaxpBatchId), newTipDistribution.getNirvanaxpBatchId()), builder.equal(r.get(TipDistribution_.shiftId), newTipDistribution.getShiftId()), builder.equal(r.get(TipDistribution_.jobRoleId), newTipDistribution.getJobRoleId())));
		
		TipDistribution tipDistribution = new TipDistribution();
		try
		{
			tipDistribution = query.getSingleResult();
		}
		catch (Exception e)
		{
			tipDistribution = null;
		}
		
		if (tipDistribution != null)
		{
			if (newTipDistribution.getDirectCardTip() != null)
				tipDistribution.setDirectCardTip(tipDistribution.getDirectCardTip().add(newTipDistribution.getDirectCardTip()));
			else
				tipDistribution.setDirectCardTip(tipDistribution.getDirectCardTip());
			if (newTipDistribution.getDirectCardTip() != null)
				tipDistribution.setDirectCashTip(tipDistribution.getDirectCashTip().add(newTipDistribution.getDirectCashTip()));
			else
				tipDistribution.setDirectCashTip(tipDistribution.getDirectCashTip());

			if (newTipDistribution.getDirectCardTip() != null)
				tipDistribution.setDirectCreditTermTip(tipDistribution.getDirectCreditTermTip().add(newTipDistribution.getDirectCreditTermTip()));
			else
				tipDistribution.setDirectCreditTermTip(tipDistribution.getDirectCreditTermTip());

			tipDistribution.setIndirectCardTip(tipDistribution.getIndirectCardTip().add(newTipDistribution.getIndirectCardTip()));
			tipDistribution.setIndirectCashTip(tipDistribution.getIndirectCashTip().add(newTipDistribution.getIndirectCashTip()));
			tipDistribution.setIndirectCreditTermTip(tipDistribution.getIndirectCreditTermTip().add(newTipDistribution.getIndirectCreditTermTip()));
			tipDistribution.setCardTotal(tipDistribution.getCardTotal().add(newTipDistribution.getCardTotal()));
			tipDistribution.setCashTotal(tipDistribution.getCashTotal().add(newTipDistribution.getCashTotal()));
			tipDistribution.setCreditTotal(tipDistribution.getCreditTotal().add(newTipDistribution.getCreditTotal()));
			tipDistribution.setBatchSalary(newTipDistribution.getBatchSalary());
			tipDistribution.setJobRoleId(newTipDistribution.getJobRoleId());
			tipDistribution.setHourlyRate(newTipDistribution.getHourlyRate());
			tipDistribution.setNoOfHours(newTipDistribution.getNoOfHours());
			tipDistribution.setSectionId(newTipDistribution.getSectionId());
			em.merge(tipDistribution);
			
		}
		
	}
	public List<EmployeeOperationalHoursWithTotalHours> getAllEmployeeOperationalHoursByBatchId(HttpServletRequest httpRequest, EntityManager em, String batchId)
	{
		// TODO Auto-generated method stub
		String queryString = "select eoh.job_role_id , eoh.employee_id,eoh.shift_id, TIME_TO_SEC( eoh.number_of_hours)/3600 as employee_hr, "
				+ " (select sum(TIME_TO_SEC(number_of_hours)) from employee_operational_hours new_eoh where "
				+ " nirvanaxp_batch_id= ? and new_eoh.shift_id=eoh.shift_id and new_eoh.number_of_hours>=0 "
				+ " order by new_eoh.employee_id)/3600 as total_employee_hr from employee_operational_hours eoh "
				 + " left join operational_shift_schedule os on os.id = eoh.shift_id where nirvanaxp_batch_id= ? and TIME_TO_SEC(eoh.number_of_hours)>=0 ";
		
		List<Object[]> resultList = new ArrayList<Object[]>();
		try
		{

			resultList = em.createNativeQuery(queryString).setParameter(1, batchId).setParameter(2,batchId).getResultList();
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe("getAllEmployeeOperationalHoursByBatchId "+ e);
		}

		List<EmployeeOperationalHoursWithTotalHours> employeeOperationalHoursWithTotalHoursList = new ArrayList<EmployeeOperationalHoursWithTotalHours>();

		if (resultList.size() > 0)
		{
			for (Object[] objRow : resultList)
			{
				int i = 0;
				EmployeeOperationalHoursWithTotalHours employeeOperationalHoursWithTotalHour = new EmployeeOperationalHoursWithTotalHours();
				employeeOperationalHoursWithTotalHour.setJobRoleId((String) objRow[i]);
				i++;
				employeeOperationalHoursWithTotalHour.setEmployeeId(((String) objRow[i]));
				i++;
				employeeOperationalHoursWithTotalHour.setShiftId(((String) objRow[i]));
				i++;
				
				employeeOperationalHoursWithTotalHour.setEmployeeHrSec(((BigDecimal) objRow[i]));
				i++;
				
				employeeOperationalHoursWithTotalHour.setTotalHrSec(((BigDecimal) objRow[i]));
                
				
				

				employeeOperationalHoursWithTotalHoursList.add(employeeOperationalHoursWithTotalHour);
			}
		}
		return employeeOperationalHoursWithTotalHoursList;

	}
	
		
	public EmployeeOperationalHoursWithTotalHours getEmployeeMasterByUserIdandShiftId(HttpServletRequest httpRequest, EntityManager em, EmployeeOperationalHoursWithTotalHours employeeOperationalHoursWithTotalHour)
	{
		
		try
		{
			TypedQuery<TipPool> query = em.createQuery("select tp from TipPool tp where tp.jobRoleId=?",
					TipPool.class).setParameter(1, employeeOperationalHoursWithTotalHour.getJobRoleId());
			TipPool tipPool = query.getSingleResult();
			employeeOperationalHoursWithTotalHour.setTipPoolId(tipPool.getId());
			
			List<EmployeeMaster> employeeMasterList=null;
			TypedQuery<EmployeeMaster> query1 = em.createQuery("select em from EmployeeMaster em where em.userId=?",
					EmployeeMaster.class).setParameter(1, employeeOperationalHoursWithTotalHour.getEmployeeId());
			 employeeMasterList = query1.getResultList();
			 if(employeeMasterList!=null && employeeMasterList.size()>0){
			employeeOperationalHoursWithTotalHour.setHourlyRate(employeeMasterList.get(0).getHourlyRate());
			}
			
			 return employeeOperationalHoursWithTotalHour;
		}
		
		catch (Exception e)
		{
			logger.fine(e.toString());
		}

		return null;
	}

	
	public BigDecimal getTotalEmployeeOperationalHoursByBatchId(HttpServletRequest httpRequest, EntityManager em, String batchId,String shiftId,String jobRoleId)
	{

		String queryString = "SELECT sum(TIME_TO_SEC(number_of_hours))/3600 "
                             + " FROM employee_operational_hours eoh "
                             + " WHERE nirvanaxp_batch_id= ?  AND eoh.job_role_id = ? "
                             + " and eoh.shift_id = ? ";
		try
		{

			return (BigDecimal) em.createNativeQuery(queryString).setParameter(1, batchId).setParameter(2,jobRoleId).setParameter(3,shiftId).getSingleResult();
			
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.fine(e.toString());
		}
		return new BigDecimal(0);

	}
	public double getTotalEmployeesInShift(HttpServletRequest httpRequest, EntityManager em, String batchId,String shiftId,int jobRoleId)
	{
		   
		String queryString = "SELECT * "
                             + " FROM employee_operational_hours eoh "
                             + " WHERE nirvanaxp_batch_id= ?  AND eoh.job_role_id = ? "
                             + " and eoh.shift_id = ? ";
		
		try
		{

			return (double) em.createNativeQuery(queryString).setParameter(1, batchId).setParameter(2,jobRoleId).setParameter(3,shiftId).getResultList().size();
			
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe("getAllEmployeeOperationalHoursByBatchId  "+ e);
		}
		return 0;

	}
}
