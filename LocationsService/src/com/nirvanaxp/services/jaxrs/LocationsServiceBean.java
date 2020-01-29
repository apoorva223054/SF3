/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.sql.SQLException;
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

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Business;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.constants.DefaultBusinessRoles;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.jaxrs.packets.DeliveryRulesPacket;
import com.nirvanaxp.services.jaxrs.packets.LocationListPacket;
import com.nirvanaxp.services.jaxrs.packets.MetroNotificationPacket;
import com.nirvanaxp.services.jaxrs.packets.SupplierUpdatePacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.business.BusinessHour;
import com.nirvanaxp.types.entities.business.BusinessHour_;
import com.nirvanaxp.types.entities.catalog.category.ItemToDate;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.Item_;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.employee.EmployeeOperation;
import com.nirvanaxp.types.entities.employee.EmployeeOperation_;
import com.nirvanaxp.types.entities.inventory.Inventory;
import com.nirvanaxp.types.entities.inventory.Inventory_;
import com.nirvanaxp.types.entities.inventory.LocationsToSupplier;
import com.nirvanaxp.types.entities.locations.BusinessCompletionInfo;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationBatchTiming;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.locations.LocationToLocationDetails;
import com.nirvanaxp.types.entities.locations.LocationToLocationDetails_;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.locations.LocationsDetail;
import com.nirvanaxp.types.entities.locations.LocationsToFunction;
import com.nirvanaxp.types.entities.locations.LocationsToImages;
import com.nirvanaxp.types.entities.locations.LocationsType;
import com.nirvanaxp.types.entities.locations.LocationsType_;
import com.nirvanaxp.types.entities.orders.DeliveryRules;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.payment.Paymentgateway;
import com.nirvanaxp.types.entities.payment.Paymentgateway_;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.reasons.ReasonType;
import com.nirvanaxp.types.entities.reasons.ReasonType_;
import com.nirvanaxp.types.entities.reasons.Reasons;
import com.nirvanaxp.types.entities.reasons.Reasons_;
import com.nirvanaxp.types.entities.reservation.ReservationsSchedule;
import com.nirvanaxp.types.entities.reservation.ReservationsSchedule_;
import com.nirvanaxp.types.entities.salestax.OrderSourceGroupToSalesTax;
import com.nirvanaxp.types.entities.salestax.OrderSourceGroupToSalesTax_;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax_;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.types.entities.user.UsersToLocation_;
import com.nirvanaxp.types.entities.user.experience.CustomerExperience;
import com.nirvanaxp.types.entities.user.experience.CustomerExperience_;

/**
 * Session Bean implementation class LocationsServiceBean
 */
class LocationsServiceBean
{

	private static final NirvanaLogger logger = new NirvanaLogger(LocationsServiceBean.class.getName());

	Location add(HttpServletRequest httpRequest, EntityManager em, Location location)
	{
		location.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if(location.getId()==null){
			try {
				location.setId(new StoreForwardUtility().generateUUID());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		location = em.merge(location);
		return location;
	}

	Location update(HttpServletRequest httpRequest, EntityManager em, Location location)
	{
		location.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		// merging location details
		em.merge(location);

		return location;

	}

	List<Location> updateLocationList(HttpServletRequest httpRequest, EntityManager em, LocationListPacket locationPacket)
	{
		List<Location> list = new ArrayList<Location>();
		for (Location location : locationPacket.getLocation())
		{
			Location locationTemp = (Location) new CommonMethods().getObjectById("Location", em,Location.class, location.getId());
			if (locationTemp != null)
			{
				locationTemp.setPreassignedServerId(location.getPreassignedServerId());
				locationTemp.setPreassignedServerName(location.getPreassignedServerName());
				locationTemp.setUpdatedBy(location.getUpdatedBy());
				locationTemp.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				locationTemp = em.merge(locationTemp);
				list.add(locationTemp);
			}
			else
			{
				location.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				location = em.merge(location);
				list.add(location);
			}

		}
		return list;

	}

	/**
	 * @param httpRequest
	 * @param em
	 * @param location
	 * @param sessionId
	 * @return
	 * @throws SQLException
	 */
	Location updateRootLoc(HttpServletRequest httpRequest, EntityManager em, Location location, String sessionId) throws SQLException
	{
		Location l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, location.getId());
		if (l != null)
		{
			location.setCreated(l.getCreated());
			if(location.getTimezoneId()==null){
				location.setTimezoneId(l.getTimezoneId());
			}
		}
		location.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		location=em.merge(location);
		


		updateSubLocation(httpRequest, em, location.getId(), location.getTimezoneId(), sessionId);
		return location;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.ILocationsService#update(com.nirvanaxp
	 * .types.entities.Location)
	 */

	Location updateFloorTimer(HttpServletRequest httpRequest, EntityManager em, Location location)
	{

		Location u = (Location) new CommonMethods().getObjectById("Location", em,Location.class, location.getId());
		u.setFloorTimer(location.getFloorTimer());
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(u);

		return location;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.ILocationsService#delete(com.nirvanaxp
	 * .types.entities.Location)
	 */

	/**
	 * @param em
	 * @param location
	 * @return
	 */
	Location delete(EntityManager em, Location location)
	{

		Location u = (Location) new CommonMethods().getObjectById("Location", em,Location.class, location.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		updateChildTableByLocationId(location.getId(), em);

		return u;

	}

	List<Location> getRootLocationsByAccount(HttpServletRequest httpRequest, EntityManager em, int businessId)
	{

		int supplierLocationType = getLocationTypeByName(httpRequest, em,"Supplier");

		// get all locations that are not supplier type
	/*	CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> cl = builder.createQuery(Location.class);
		Root<Location> l = cl.from(Location.class);
		TypedQuery<Location> query = em.createQuery(cl.select(l).where(
				builder.and(builder.equal(l.get(Location_.locationsId), 0), builder.equal(l.get(Location_.businessId), businessId),
						builder.notEqual(l.get(Location_.locationsTypeId), supplierLocationType))));
*/
		TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l where (l.locationsId is null or l.locationsId='"+0+"')  and l.businessId =? and l.locationsTypeId !=?  ", Location.class)
				.setParameter(1, businessId).setParameter(2, supplierLocationType);
		 
		return query.getResultList();

	}

	Location getLocationsById(HttpServletRequest httpRequest, EntityManager em, String id)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> cl = builder.createQuery(Location.class);
		Root<Location> l = cl.from(Location.class);
		TypedQuery<Location> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Location_.id), id))));

		return query.getSingleResult();

	}

	SupplierUpdatePacket getSupplierLocationById(EntityManager em, String id, String userId) throws NoResultException
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> cl = builder.createQuery(Location.class);
		Root<Location> l = cl.from(Location.class);
		TypedQuery<Location> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Location_.id), id))));

		SupplierUpdatePacket supplierUpdatePacket = new SupplierUpdatePacket();
		Location location = query.getSingleResult();
		supplierUpdatePacket.setSupplier(location);

		LocationRelationalHelper locationRelationalHelper = new LocationRelationalHelper();
		List<LocationsToSupplier> locationsToSuppliersList = locationRelationalHelper.getAllLocationForSupplierId(id, em);
		List<Location> locationsList = new ArrayList<Location>();
		if (locationsToSuppliersList != null && locationsToSuppliersList.size() > 0)
		{
			for (LocationsToSupplier locationsToSupplier : locationsToSuppliersList)
			{
				if (locationsToSupplier.getStatus().equalsIgnoreCase("D") == false)
				{

					try
					{
						CriteriaBuilder builder1 = em.getCriteriaBuilder();
						CriteriaQuery<Location> criteria = builder1.createQuery(Location.class);
						Root<Location> r = criteria.from(Location.class);
						TypedQuery<Location> query1 = em.createQuery(criteria.select(r).where(builder1.equal(r.get(Location_.id), locationsToSupplier.getLocationsId())));
						Location supplier = query1.getSingleResult();

						if (!supplier.getStatus().equals("D") && !supplier.getStatus().equals("I"))
						{
							locationsList.add(new Location(locationsToSupplier.getLocationsId()));
						}
					}
					catch (Exception e)
					{
						logger.severe(e);

					}

				}

			}
		}

		supplierUpdatePacket.setLocationsList(locationsList);

		return supplierUpdatePacket;

	}

	List<Location> getRootLocations(HttpServletRequest httpRequest, EntityManager em)
	{

		// get root locations where location type is not supplier

		// int supplierLocationType = getLocationTypeForSupplier();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> cl = builder.createQuery(Location.class);
		Root<Location> l = cl.from(Location.class);
		TypedQuery<Location> query = em.createQuery(cl.select(l).where(builder.equal(l.get(Location_.locationsId), null), builder.notEqual(l.get(Location_.status), "D"),
				builder.notEqual(l.get(Location_.status), "I"), builder.notEqual(l.get(Location_.isThirdPartyLocation), 1)));
		return query.getResultList();

	}

	List<Location> getSubLocations(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> criteria = builder.createQuery(Location.class);
		Root<Location> r = criteria.from(Location.class);
		TypedQuery<Location> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Location_.locationsId), locationId), builder.notEqual(r.get(Location_.status), "D")));
		return query.getResultList();

	}

	List<Location> getRootLocationsByUserId(HttpServletRequest httpRequest, EntityManager em, String userId) throws SQLException
	{
		List<Location> locationsList = new ArrayList<Location>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call p_get_root_locations_for_admin( ? )").setParameter(1, userId).getResultList();
		for (Object[] objRow : resultList)
		{
			int i = 0;
			if ((String) objRow[i] != null)
			{
				Location location = new Location();
				location.setId((String) objRow[i++]);
				location.setName((String) objRow[i++]);
				location.setBusinessId((int) objRow[i++]);
				locationsList.add(location);
			}
		}

		return locationsList;

	}

	// UsersToLocation insertUsersToLocation(HttpServletRequest httpRequest,
	// EntityManager em, UsersToLocation usersToLocation) {
	//
	// }

	List<Paymentgateway> updateRootLocation(HttpServletRequest httpRequest, EntityManager em, List<Paymentgateway> paymentGatewayList)
	{

		if (paymentGatewayList != null)
		{
			for (Paymentgateway paymentgateway : paymentGatewayList)
			{
				paymentgateway.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				em.merge(paymentgateway);
			}

		}

		return paymentGatewayList;

	}

	Paymentgateway getPaymentGatewayByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Paymentgateway> cl = builder.createQuery(Paymentgateway.class);
		Root<Paymentgateway> l = cl.from(Paymentgateway.class);
		TypedQuery<Paymentgateway> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Paymentgateway_.locationsId), locationId))));
		return query.getSingleResult();

	}

	List<BusinessHour> getBusinessHour(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<BusinessHour> criteria = builder.createQuery(BusinessHour.class);
		Root<BusinessHour> r = criteria.from(BusinessHour.class);
		TypedQuery<BusinessHour> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(BusinessHour_.locationsId), locationId)));
		return query.getResultList();

	}

	/**
	 * @param httpRequest
	 * @param em
	 * @param locationId
	 * @param timezoneId
	 * @param sessionId
	 * @throws SQLException
	 */
	private void updateSubLocation(HttpServletRequest httpRequest, EntityManager em, String locationId, int timezoneId, String sessionId) throws SQLException
	{
		// for take out order
		String whereClause = "";
		String queryString = "select id from locations  where locations_id in (select id from locations where locations_id=?) or id in (select id from locations where locations_id=?)";

		@SuppressWarnings("unchecked")
		List<Object> resultList = em.createNativeQuery(queryString).setParameter(1, locationId).setParameter(2, locationId).getResultList();

		if (resultList.size() > 0)
		{
			int i = 1;
			for (Object objRow : resultList)
			{
				String getlocationId = (String) objRow;
				whereClause += "'"+ getlocationId +"'";

				if (i < resultList.size())
				{
					whereClause += ",";
				}

				i++;
			}
			// changing to where clause because in setparameter is not
			// supporting 1,2,3,4 like value
			queryString = "update locations set timezone_id =? where id in (" + whereClause + ")";
			em.createNativeQuery(queryString).setParameter(1, timezoneId).executeUpdate();
		}
	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param userId
	 * @return
	 */
	List<Location> getLocationByUserIdList(HttpServletRequest httpRequest, EntityManager em, String userId)
	{

		// get users to business list

		TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l, UsersToLocation u where u.locationsId=l.id and (l.locationsId='0' or l.locationsId is null) and u.usersId = ? ", Location.class).setParameter(1,
				userId);

		List<Location> locations = query.getResultList();
		return locations;

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param locationId
	 * @return
	 */
	List<Paymentgateway> getPaymentGatewayListByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Paymentgateway> cl = builder.createQuery(Paymentgateway.class);
		Root<Paymentgateway> l = cl.from(Paymentgateway.class);
		TypedQuery<Paymentgateway> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Paymentgateway_.locationsId), locationId))));
		return query.getResultList();

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param locationId
	 * @return
	 */
	List<SalesTax> getLocationSpecificSalesTax(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
		Root<SalesTax> r = criteria.from(SalesTax.class);
		TypedQuery<SalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(SalesTax_.isItemSpecific), 0), builder.equal(r.get(SalesTax_.locationsId), locationId)));
		return query.getResultList();

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param locationId
	 * @return
	 */
	List<SalesTax> getAllSalesTax(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
		Root<SalesTax> r = criteria.from(SalesTax.class);
		TypedQuery<SalesTax> query = em.createQuery(criteria.select(r).where((builder.notEqual(r.get(SalesTax_.status), "D")), builder.equal(r.get(SalesTax_.locationsId), locationId)));

		return query.getResultList();

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param id
	 * @return
	 */
	SalesTax getSalesTaxById(HttpServletRequest httpRequest, EntityManager em, String id)
	{

		SalesTax salesTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, id);
		salesTax.setTaxIdList(getChildTax(em, id));
		salesTax.setOrderSourceGroupToSalesTaxList(getOrderSourceGroup(em, id));
		SalesTax p = salesTax;
		String queryString = "select l from Location l where l.id in (select p.locationsId from SalesTax p where p.globalId=?) ";
		TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());
		List<Location> resultSet = query2.getResultList();
		p.setLocationList(resultSet);
		return p;

	}

	private List<String> getChildTax(EntityManager em, String taxId)
	{
		String queryString = "select id from sales_tax where tax_id = ? and status not in ('D','I') ";
		Query query = em.createNativeQuery(queryString).setParameter(1, taxId);
		@SuppressWarnings("unchecked")
		List<String> objects = query.getResultList();
		return objects;

	}

	private List<OrderSourceGroupToSalesTax> getOrderSourceGroup(EntityManager em, String taxId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroupToSalesTax> criteria = builder.createQuery(OrderSourceGroupToSalesTax.class);
			Root<OrderSourceGroupToSalesTax> r = criteria.from(OrderSourceGroupToSalesTax.class);
			TypedQuery<OrderSourceGroupToSalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceGroupToSalesTax_.taxId), taxId),
					builder.notEqual(r.get(OrderSourceGroupToSalesTax_.status), "D")));
			List<OrderSourceGroupToSalesTax> objects = query.getResultList();

			return objects;
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return null;

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param id
	 * @return
	 */
	Reasons getReasonById(HttpServletRequest httpRequest, EntityManager em, String id)
	{

		return (Reasons) new CommonMethods().getObjectById("Reasons", em,Reasons.class, id);

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @return
	 */
	List<Reasons> getAllReasons(HttpServletRequest httpRequest, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reasons> criteria = builder.createQuery(Reasons.class);
		Root<Reasons> r = criteria.from(Reasons.class);
		TypedQuery<Reasons> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(Reasons_.status), "D")));
		return query.getResultList();

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @return
	 */
	List<ReasonType> getAllReasonType(HttpServletRequest httpRequest, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ReasonType> criteria = builder.createQuery(ReasonType.class);
		Root<ReasonType> r = criteria.from(ReasonType.class);
		TypedQuery<ReasonType> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(ReasonType_.status), "D")));
		return query.getResultList();

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param id
	 * @return
	 */
	ReasonType getReasonTypeById(HttpServletRequest httpRequest, EntityManager em, String id)
	{

		return (ReasonType) new CommonMethods().getObjectById("ReasonType", em,ReasonType.class, id);

	}

	/**
	 * 
	 * @param httpRequest
	 * @param emLocal
	 * @param globalUserId
	 * @return
	 * @throws NoResultException
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws InvalidSessionException
	 */
	List<Business> getAdminLocationByUserId(HttpServletRequest httpRequest, String sessionId, EntityManager emLocal, String globalUserId) throws NoResultException, IOException, InvalidSessionException
	{

		String localUserId = getLocalUserIdUsingGlobalUserId(httpRequest, emLocal, globalUserId);
		// connects to local database and gets all locations to user,
		// where user is not a customer and is an admin
		// get the user whose global bussiness id is passed id

		String sql = "select distinct  l.id,l.name,l.business_id from users_to_locations utl left join users_to_roles utr on utl.users_id =utr.users_id "
				+ "left join locations l on l.id=utl.locations_id left join roles r on r.id=utr.roles_id where  r.role_name != ? "
				+ "and utl.status not in ('I','D') and ( l.locations_id is null or l.locations_id ='0' )  and l.is_global_location != 1 and utl.users_id=?";

		Query q = emLocal.createNativeQuery(sql).setParameter(1, DefaultBusinessRoles.POS_Customer.getRoleName()).setParameter(2, localUserId);

		@SuppressWarnings("unchecked")
		List<Object[]> l = q.getResultList();

		String bussinessIdArray = "";
		int count = 1;
		for (Object[] obj : l)
		{
			Location location = new Location();
			location.setId((String) obj[0]);
			location.setBusinessId((Integer) obj[2]);
			location.setName((String) obj[1]);

			if (count == 1)
			{
				// add without comma
				bussinessIdArray += (Integer) obj[2];
			}
			else
			{

				bussinessIdArray += "," + (Integer) obj[2];
			}
			++count;
		}

		List<Business> resultSet = null;
		if (bussinessIdArray.length() > 0)
		{

			String queryString = "select b from Business b where b.id in (" + bussinessIdArray + ")";
			EntityManager emGlobal = null;
			try
			{

				emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				TypedQuery<Business> query = emGlobal.createQuery(queryString, Business.class);
				resultSet = query.getResultList();
			}
			finally
			{
				GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
			}
		}

		return resultSet;

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param globalUserId
	 * @return
	 * @throws NoResultException
	 */
	private String getLocalUserIdUsingGlobalUserId(HttpServletRequest httpRequest, EntityManager em, String globalUserId) throws NoResultException
	{

		// get users to business list

		TypedQuery<User> query = em.createQuery("select u from User u where u.globalUsersId=?", User.class).setParameter(1, globalUserId);

		User user = query.getSingleResult();
		return user.getId();

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param typeId
	 * @param locationId
	 * @return
	 */
	List<Reasons> getAllReasonsByTypeId(HttpServletRequest httpRequest, EntityManager em, String typeId, String locationId)
	{

		String queryString = "select r from Reasons r where r.reasonTypeId= ? and r.status != 'D' and r.locationsId =? order by r.displaySequence  asc ";
		TypedQuery<Reasons> query = em.createQuery(queryString, Reasons.class).setParameter(1, typeId).setParameter(2, locationId);
		List<Reasons> resultSet = query.getResultList();
		return resultSet;

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @return
	 */
	int getLocationTypeByName(HttpServletRequest httpRequest, EntityManager em, String name)
	{
		int id = 0;
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<LocationsType> criteria = builder.createQuery(LocationsType.class);
			Root<LocationsType> r = criteria.from(LocationsType.class);
			TypedQuery<LocationsType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(LocationsType_.name), name)));
			// LocationsType supplierLocationType = query.getSingleResult();
			id = query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return id;
	}

	/*
	 * int getLocationTypeForInHouseProduction(HttpServletRequest httpRequest,
	 * EntityManager em) { int id =0;
	 * 
	 * try { CriteriaBuilder builder = em.getCriteriaBuilder();
	 * CriteriaQuery<LocationsType> criteria =
	 * builder.createQuery(LocationsType.class); Root<LocationsType> r =
	 * criteria.from(LocationsType.class); TypedQuery<LocationsType> query =
	 * em.createQuery(criteria.select(r).where
	 * (builder.equal(r.get(LocationsType_.name), "In House Production"))); id =
	 * query.getSingleResult().getId(); return id; } catch (Exception e) {
	 * 
	 * logger.severe(e); } return id;
	 * 
	 * }
	 */

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @return
	 */
	List<Location> getAllSuppliers(HttpServletRequest httpRequest, EntityManager em)
	{

		// get location type for supplier
		int locationTypeId = getLocationTypeByName(httpRequest, em,"Supplier");

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> criteria = builder.createQuery(Location.class);
		Root<Location> r = criteria.from(Location.class);
		TypedQuery<Location> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Location_.locationsTypeId), locationTypeId)));
		return query.getResultList();

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param supplierId
	 * @param updatedBy
	 * @return
	 */
	Location deleteSupplier(HttpServletRequest httpRequest, EntityManager em, String supplierId, String updatedBy)
	{

		Location supplier = (Location) new CommonMethods().getObjectById("Location", em,Location.class, supplierId);
		supplier.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		supplier.setUpdatedBy(updatedBy);

		supplier.setStatus("D");

		em.merge(supplier);

		// now also set status of location to supplier
		// get locations to suppier for this supplier id
		LocationRelationalHelper locationRelationalHelper = new LocationRelationalHelper();
		List<LocationsToSupplier> locationsToSuppliersList = locationRelationalHelper.getAllLocationForSupplierId(supplier.getId(), em);
		if (locationsToSuppliersList != null && locationsToSuppliersList.size() > 0)
		{
			for (LocationsToSupplier locationsToSupplier : locationsToSuppliersList)
			{
				locationsToSupplier.setStatus("D");
				locationsToSupplier.setUpdatedBy(updatedBy);
				em.merge(supplier);
			}
		}

		return supplier;
	}

	public MetroNotificationPacket getMetroNotificationByLocationIdAndDate(EntityManager em, String locationId, String date) throws SQLException
	{
		MetroNotificationPacket metroNotificationPacket = new MetroNotificationPacket();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call p_metro_count(?,?,?)").setParameter(1, locationId).setParameter(2, date).setParameter(3, date).getResultList();
		for (Object[] objRow : resultList)
		{
			int i = 1;
			metroNotificationPacket.setTotalReservation((int) objRow[i++]);
			metroNotificationPacket.setTotalReservationPartySize((int) objRow[i++]);
			metroNotificationPacket.setTotalWalkIn((int) objRow[i++]);
			metroNotificationPacket.setTotalWalkInPartySize((int) objRow[i++]);
			metroNotificationPacket.setTotalWaitlist((int) objRow[i++]);
			metroNotificationPacket.setTotalWaitlistPartySize((int) objRow[i++]);
			metroNotificationPacket.setTotalTableOccupiedPartySize((int) objRow[i++]);
			metroNotificationPacket.setTotalTakeoutOpened((int) objRow[i++]);
			metroNotificationPacket.setTotalTableOccupied((int) objRow[i++]);
			metroNotificationPacket.setTotalTakeout((int) objRow[i++]);
			metroNotificationPacket.setClosedOrder((int) objRow[i++]);
			metroNotificationPacket.setOpenOrder((int) objRow[i++]);
			metroNotificationPacket.setTotalDeliveryOpened((int) objRow[i++]);
			metroNotificationPacket.setTotalDelivery((int) objRow[i++]);
			metroNotificationPacket.setTotalVoidOrder((int) objRow[i++]);
			metroNotificationPacket.setTotalCancelOrder((int) objRow[i++]);
			metroNotificationPacket.setQuickOrderInStore((int) objRow[i++]);
			metroNotificationPacket.setTotalOpenTakeoutPartySize((int) objRow[i++]);
			metroNotificationPacket.setTotalOpenDeliveryPartySize((int) objRow[i++]);
			metroNotificationPacket.setTotalClockIn((int) objRow[i++]);
			metroNotificationPacket.setTotalClockOut((int) objRow[i++]);
			metroNotificationPacket.setTotalBreakIn((int) objRow[i++]);
			metroNotificationPacket.setTotalBreakOut((int) objRow[i++]);
			
			i++;
			
			metroNotificationPacket.setTotalCatering((int) objRow[i++]);
			metroNotificationPacket.setTotalOpenCateringPartySize((int) objRow[i++]);
			// metroNotificationPacket.setTotalInventoryOrders((int)
			// objRow[i++]);

		}

		return metroNotificationPacket;

	}

	List<LocationsToFunction> getAllLocationToFunctionByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationId) throws SQLException
	{

		List<LocationsToFunction> locationToFunctionArray = new ArrayList<LocationsToFunction>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getAllLocationToFunctionByLocationId(?)").setParameter(1, locationId).getResultList();
		for (Object[] objRow : resultList)
		{
			int i = 0;
			// if this has primary key not 0
			if ((int) objRow[i] != 0)
			{
				LocationsToFunction locationsToFunction = new LocationsToFunction();
				locationsToFunction.setId((int) objRow[i++]);
				locationsToFunction.setLocationsId((String) objRow[i++]);
				locationsToFunction.setFunctionsName((String) objRow[i++]);
				locationsToFunction.setDisplaySequence((int) objRow[i++]);
				locationsToFunction.setName((String) objRow[i++]);
				locationsToFunction.setStatus((Character) objRow[i++] + "");
				locationToFunctionArray.add(locationsToFunction);
			}
		}

		return locationToFunctionArray;

	}

	List<LocationsToFunction> getLocationToFunctionById(HttpServletRequest httpRequest, EntityManager em, int id) throws SQLException
	{
		List<LocationsToFunction> locationToFunctionArray = new ArrayList<LocationsToFunction>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getLocationToFunctionById(?)").setParameter(1, id).getResultList();
		for (Object[] objRow : resultList)
		{
			int i = 0;
			// if this has primary key not 0
			if ((int) objRow[i] != 0)
			{
				LocationsToFunction locationsToFunction = new LocationsToFunction();
				locationsToFunction.setId((int) objRow[i]);
				locationsToFunction.setLocationsId((String) objRow[i++]);
				locationsToFunction.setFunctionsName((String) objRow[i++]);
				locationsToFunction.setDisplaySequence((int) objRow[i++]);
				locationsToFunction.setName((String) objRow[i++]);
				locationToFunctionArray.add(locationsToFunction);
			}

		}

		return locationToFunctionArray;
	}

	LocationsToFunction updateLocationToFunctionName(HttpServletRequest httpRequest, EntityManager em, LocationsToFunction function)
	{
		LocationsToFunction locationToFunction = em.find(LocationsToFunction.class, function.getId());
		locationToFunction.setFunctionsName(function.getFunctionsName());
		locationToFunction.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		locationToFunction.setUpdatedBy(function.getUpdatedBy());
		locationToFunction.setStatus(function.getStatus());
		em.merge(locationToFunction);
		return locationToFunction;
	}

	List<LocationsToFunction> getLocationToFunctionByLocationIdAndDisplaySequence(HttpServletRequest httpRequest, EntityManager em, String locationId, int displaySequence) throws SQLException
	{

		List<LocationsToFunction> locationToFunctionArray = new ArrayList<LocationsToFunction>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getLocationToFunctionByLocationIdAndDisplaySequence(?, ?)").getResultList();

		for (Object[] objRow : resultList)
		{
			int i = 0;
			// if this has primary key not 0
			if ((int) objRow[i] != 0)
			{
				LocationsToFunction locationsToFunction = new LocationsToFunction();
				locationsToFunction.setId((int) objRow[i]);
				locationsToFunction.setLocationsId((String) objRow[i++]);
				locationsToFunction.setFunctionsName((String) objRow[i++]);
				locationsToFunction.setDisplaySequence((int) objRow[i++]);
				locationsToFunction.setName((String) objRow[i++]);
				locationToFunctionArray.add(locationsToFunction);
			}

		}

		return locationToFunctionArray;

	}

	// TODO - all these queries should be combined in a stored procedure
	/**
	 * @param httpRequest
	 * @param em
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	BusinessCompletionInfo getBusinessCompletionInfoByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationId) throws Exception
	{

		BusinessCompletionInfo businessCompletionInfo = new BusinessCompletionInfo();

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<BusinessHour> criteria = builder.createQuery(BusinessHour.class);
			Root<BusinessHour> r = criteria.from(BusinessHour.class);
			TypedQuery<BusinessHour> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(BusinessHour_.locationsId), locationId)));
			List<BusinessHour> businessHour = query.getResultList();
			if (businessHour.size() > 0)
			{
				businessCompletionInfo.setBusinessHours(businessHour.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Location> criteria = builder.createQuery(Location.class);
			Root<Location> r = criteria.from(Location.class);
			TypedQuery<Location> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(Location_.status), "D"), builder.equal(r.get(Location_.locationsId), locationId)));
			List<Location> locations = query.getResultList();
			if (locations.size() > 0)
			{
				businessCompletionInfo.setSetupLocations(locations.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsSchedule> criteria = builder.createQuery(ReservationsSchedule.class);
			Root<ReservationsSchedule> r = criteria.from(ReservationsSchedule.class);
			TypedQuery<ReservationsSchedule> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(ReservationsSchedule_.status), "D"),
					builder.equal(r.get(ReservationsSchedule_.locationId), locationId)));
			List<ReservationsSchedule> reservationsSchedule = query.getResultList();
			if (reservationsSchedule.size() > 0)
			{
				businessCompletionInfo.setReservationSchedule(reservationsSchedule.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Location> criteria = builder.createQuery(Location.class);
			Root<Location> r = criteria.from(Location.class);
			TypedQuery<Location> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(Location_.status), "D"), builder.equal(r.get(Location_.id), locationId)));
			List<Location> locations = query.getResultList();
			if (locations.size() > 0)
			{
				businessCompletionInfo.setBusinessSetting(locations.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UsersToLocation> criteria = builder.createQuery(UsersToLocation.class);
			Root<UsersToLocation> r = criteria.from(UsersToLocation.class);
			TypedQuery<UsersToLocation> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(UsersToLocation_.status), "D"),
					builder.equal(r.get(UsersToLocation_.locationsId), locationId)));
			List<UsersToLocation> usersToLocation = query.getResultList();
			if (usersToLocation.size() > 0)
			{
				businessCompletionInfo.setManageUsers(usersToLocation.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
			Root<SalesTax> r = criteria.from(SalesTax.class);
			TypedQuery<SalesTax> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(SalesTax_.status), "D"), builder.equal(r.get(SalesTax_.locationsId), locationId)));
			List<SalesTax> salesTax = query.getResultList();
			if (salesTax.size() > 0)
			{
				businessCompletionInfo.setTax(salesTax.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(OrderStatus_.status), "D"), builder.equal(r.get(OrderStatus_.locationsId), locationId)));
			List<OrderStatus> orderStatus = query.getResultList();
			if (orderStatus.size() > 0)
			{
				businessCompletionInfo.setOrderStatus(orderStatus.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Reasons> criteria = builder.createQuery(Reasons.class);
			Root<Reasons> r = criteria.from(Reasons.class);
			TypedQuery<Reasons> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(Reasons_.status), "D"), builder.equal(r.get(Reasons_.locationsId), locationId)));
			List<Reasons> reasons = query.getResultList();
			if (reasons.size() > 0)
			{
				businessCompletionInfo.setVoidReason(reasons.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
			Root<Item> r = criteria.from(Item.class);
			TypedQuery<Item> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(Item_.status), "D"), builder.equal(r.get(Item_.locationsId), locationId)));
			List<Item> item = query.getResultList();
			if (item.size() > 0)
			{
				businessCompletionInfo.setAddProducts(item.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CustomerExperience> criteria = builder.createQuery(CustomerExperience.class);
			Root<CustomerExperience> r = criteria.from(CustomerExperience.class);
			TypedQuery<CustomerExperience> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CustomerExperience_.locationsId), locationId)));
			List<CustomerExperience> customerExperience = query.getResultList();
			if (customerExperience.size() > 0)
			{
				businessCompletionInfo.setFeedback(customerExperience.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Inventory> criteria = builder.createQuery(Inventory.class);
			Root<Inventory> r = criteria.from(Inventory.class);
			TypedQuery<Inventory> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Inventory_.locationId), locationId), builder.notEqual(r.get(Inventory_.status), "D")));
			List<Inventory> inventory = query.getResultList();
			if (inventory.size() > 0)
			{
				businessCompletionInfo.setInventory(inventory.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<EmployeeOperation> criteria = builder.createQuery(EmployeeOperation.class);
			Root<EmployeeOperation> r = criteria.from(EmployeeOperation.class);
			TypedQuery<EmployeeOperation> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(EmployeeOperation_.locationsId), locationId)));
			List<EmployeeOperation> employeeOperation = query.getResultList();
			if (employeeOperation.size() > 0)
			{
				businessCompletionInfo.setEmployeeOperation(employeeOperation.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
			Root<Item> r = criteria.from(Item.class);
			TypedQuery<Item> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(Item_.status), "D"), builder.equal(r.get(Item_.locationsId), locationId)));
			List<Item> item = query.getResultList();
			if (item.size() > 0)
			{
				businessCompletionInfo.setAddProducts(item.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			TypedQuery<Discount> query = em.createQuery("select d from Discount d where d.locationsId =? and d.status != 'D'  and d.name not in ('No Discount')", Discount.class).setParameter(1,
					locationId);
			List<Discount> discount = query.getResultList();
			if (discount.size() > 0)
			{
				businessCompletionInfo.setDiscount(discount.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		try
		{
			TypedQuery<Printer> query = em.createQuery("select p from Printer p where p.locationsId =? and p.status != 'D' and p.printersName not in ('No Printer')", Printer.class).setParameter(1,
					locationId);
			List<Printer> printer = query.getResultList();
			if (printer.size() > 0)
			{
				businessCompletionInfo.setPrinters(printer.size());
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Error during get getBusinessCompletionInfoByLocationId  ", e.getMessage());
			throw e;
		}

		businessCompletionInfo.setFeedback(1); // by default true // as per
												// uzma

		return businessCompletionInfo;

	}

	Location getNirvanaXpRootLocationsByCountryId(HttpServletRequest httpRequest, EntityManager em, int contryId)
	{

		String sql = " select l.id,l.name,l.sales_tax_1,l.sales_tax_2, l.sales_tax_3,l.sales_tax_4 from locations l JOIN address a ON l.address_id = a.id WHERE country_id = ? "
				+ " AND (l.locations_id='0' or l.locations_id is null) AND l.status='A' ";

		Query q = em.createNativeQuery(sql).setParameter(1, contryId);

		@SuppressWarnings("unchecked")
		List<Object[]> l = q.getResultList();

		Location location = new Location();
		for (Object[] obj : l)
		{
			location.setId((String) obj[0]);
			location.setName((String) obj[1]);
			location.setSalesTax1((String) obj[2]);
			location.setSalesTax2((String) obj[3]);
			location.setSalesTax3((String) obj[4]);
			location.setSalesTax4((String) obj[5]);
		}

		return location;

	}

	/**
	 * @param locationId
	 * @param em
	 */
	private void updateChildTableByLocationId(String locationId, EntityManager em)
	{
		if (locationId != null)
		{
			String queryString = "update locations set status='D'  where locations_id=? ";

			em.createNativeQuery(queryString).setParameter(1, locationId).executeUpdate();

			// em.createNativeQuery(queryString).executeUpdate();
		}
	}

	SalesTax getNirvanaXpSalesTaxInfoBySalesTaxId(HttpServletRequest httpRequest, EntityManager em, int salesTaxId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SalesTax> cl = builder.createQuery(SalesTax.class);
		Root<SalesTax> l = cl.from(SalesTax.class);
		TypedQuery<SalesTax> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(SalesTax_.id), salesTaxId))));

		return query.getSingleResult();

	}

	List<LocationsDetail> getAllLocationsDetails(HttpServletRequest httpRequest, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<LocationsDetail> criteria = builder.createQuery(LocationsDetail.class);
		Root<LocationsDetail> r = criteria.from(LocationsDetail.class);
		TypedQuery<LocationsDetail> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(Location_.status), "D")));
		return query.getResultList();

	}

	public com.nirvanaxp.types.entities.locations.LocationsToImages addLocationsToImages(LocationsToImages locationsToImages, HttpServletRequest httpRequest, EntityManager em)
	{
		locationsToImages.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if(locationsToImages.getId()==null){
			try {
				locationsToImages.setId(new StoreForwardUtility().generateUUID());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		em.merge(locationsToImages);

		return locationsToImages;
	}

	public com.nirvanaxp.types.entities.locations.LocationsToImages updateLocationsToImages(LocationsToImages locationsToImages, HttpServletRequest httpRequest, EntityManager em)
	{
		locationsToImages.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(locationsToImages);

		return locationsToImages;
	}

	public com.nirvanaxp.types.entities.locations.LocationsToImages deleteLocationsToImages(LocationsToImages locationsToImages, HttpServletRequest httpRequest, EntityManager em)
	{
		LocationsToImages u = em.find(LocationsToImages.class, locationsToImages.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		return u;
	}

	List<LocationToLocationDetails> getAllLocationToLocationDetailsByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<LocationToLocationDetails> criteria = builder.createQuery(LocationToLocationDetails.class);
		Root<LocationToLocationDetails> r = criteria.from(LocationToLocationDetails.class);
		TypedQuery<LocationToLocationDetails> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(LocationToLocationDetails_.status), "D"),
				builder.equal(r.get(LocationToLocationDetails_.locationsId), locationId)));
		return query.getResultList();

	}

	LocationToLocationDetails checkLocationDetailExist(String locationDetailsId, String locationId, HttpServletRequest httpRequest, EntityManager em)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<LocationToLocationDetails> criteria = builder.createQuery(LocationToLocationDetails.class);
		Root<LocationToLocationDetails> r = criteria.from(LocationToLocationDetails.class);
		TypedQuery<LocationToLocationDetails> query = em.createQuery(criteria.select(r).where(builder.notEqual(r.get(LocationToLocationDetails_.status), "D"),
				builder.equal(r.get(LocationToLocationDetails_.locationDetailsId), locationDetailsId), builder.equal(r.get(LocationToLocationDetails_.locationsId), locationId)));

		return query.getSingleResult();
	}

	List<Location> getAllRootLocationsWithoutGlobalBusinessId(HttpServletRequest httpRequest, EntityManager em)
	{
		int supplierLocationType = getLocationTypeByName(httpRequest, em,"Supplier");
		int inHouseLocationType = getLocationTypeByName(httpRequest, em,"In House Production");
		int commercialCustomer = getLocationTypeByName(httpRequest, em,"Commercial Customer");

		// get all locations that are not supplier type
		 
		
		TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l where (l.locationsId is null or l.locationsId='"+0+"')  and l.status not in ('D','I') and l.isGlobalLocation=0  and l.locationsTypeId not in (?,?,?) ", Location.class)
				.setParameter(1, supplierLocationType).setParameter(2, inHouseLocationType).setParameter(3, commercialCustomer);
		 
		return query.getResultList();

	}

	List<Location> getMasterLocation(HttpServletRequest httpRequest, EntityManager em)
	{
		// get all locations that are not supplier type
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> cl = builder.createQuery(Location.class);
		Root<Location> l = cl.from(Location.class);
		TypedQuery<Location> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Location_.isGlobalLocation), 1))));

		return query.getResultList();

	}

	List<LocationsToFunction> getAllLocationToFunctionForCustomerByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationId) throws SQLException
	{

		List<LocationsToFunction> locationToFunctionArray = new ArrayList<LocationsToFunction>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getAllLocationToFunctionForCustomerByLocationId(?)").setParameter(1, locationId).getResultList();
		for (Object[] objRow : resultList)
		{
			int i = 0;
			// if this has primary key not 0
			if ((int) objRow[i] != 0)
			{
				LocationsToFunction locationsToFunction = new LocationsToFunction();
				locationsToFunction.setId((int) objRow[i++]);
				locationsToFunction.setLocationsId((String) objRow[i++]);
				locationsToFunction.setFunctionsName((String) objRow[i++]);
				locationsToFunction.setDisplaySequence((int) objRow[i++]);
				locationsToFunction.setName((String) objRow[i++]);
				locationsToFunction.setStatus((Character) objRow[i++] + "");
				locationToFunctionArray.add(locationsToFunction);
			}
		}

		return locationToFunctionArray;

	}

	/**
	 * 
	 * @param httpRequest
	 * @param em
	 * @param userId
	 * @return
	 */
	List<Location> getLocationByUserIdForKDS(HttpServletRequest httpRequest, EntityManager em, String userId)
	{

		// get users to business list

		TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l, UsersToLocation u where u.locationsId=l.id and u.status!='D' and (l.locationsId='0' or l.locationsId is null) and u.usersId = ? ", Location.class)
				.setParameter(1, userId);

		List<Location> locations = query.getResultList();
		return locations;

	}

	public com.nirvanaxp.types.entities.locations.LocationSetting addLocationSetting(LocationSetting locationSetting, HttpServletRequest httpRequest, EntityManager em)
	{
		List<LocationBatchTiming> list = locationSetting.getLocationBatchTimingList();
		locationSetting.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		locationSetting.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if(locationSetting.getId()==null){
			try {
				locationSetting.setId(new StoreForwardUtility().generateUUID());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		locationSetting = em.merge(locationSetting);
		if(list!=null && list.size()>0){
			for(LocationBatchTiming batchTiming:list){
				if(batchTiming.getId()==null){
					batchTiming.setLocationSettingId(locationSetting.getId());
					batchTiming.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					batchTiming.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				batchTiming =em.merge(batchTiming);
			}
		}
	

		return locationSetting;
	}

	public com.nirvanaxp.types.entities.locations.LocationSetting updateLocationSetting(LocationSetting locationSetting, HttpServletRequest httpRequest, EntityManager em)
	{
		List<LocationBatchTiming> list = locationSetting.getLocationBatchTimingList();
		locationSetting.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		locationSetting = em.merge(locationSetting);
		if(list!=null && list.size()>0){
			for(LocationBatchTiming batchTiming:list){
				if(batchTiming.getId()==null){
					batchTiming.setLocationSettingId(locationSetting.getId());
					batchTiming.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					batchTiming.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					batchTiming.setId(new StoreForwardUtility().generateUUID());
				}
				batchTiming =em.merge(batchTiming);
			}
		}

		return locationSetting;
	}

	public com.nirvanaxp.types.entities.locations.LocationSetting deleteLocationSetting(LocationSetting locationSetting, HttpServletRequest httpRequest, EntityManager em)
	{
		// TODO Auto-generated method stub
		return null;
	}

	List<Location> getAllLocationsByLocationType(HttpServletRequest httpRequest, EntityManager em, int locationTypeId)
	{

		// get location type for supplier
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> criteria = builder.createQuery(Location.class);
		Root<Location> r = criteria.from(Location.class);
		TypedQuery<Location> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Location_.locationsTypeId), locationTypeId)));
		return query.getResultList();

	}
	
	public DeliveryRulesPacket addDeliveryRules(EntityManager em,
			DeliveryRulesPacket packet) {
		DeliveryRules rule = packet.getDeliveryRules();

		if (rule.getId() > 0) {
			rule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			DeliveryRules dRules = em.find(DeliveryRules.class, rule.getId());
			rule.setCreated(dRules.getCreated());

		} else {
			rule.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			rule.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		}

		rule = em.merge(rule);
		packet.setDeliveryRules(rule);
		return packet;

	}

	public DeliveryRulesPacket deleteDeliveryRules(EntityManager em,
			DeliveryRulesPacket packet) {
		DeliveryRules rule = packet.getDeliveryRules();
		DeliveryRules dRules = em.find(DeliveryRules.class, rule.getId());
		dRules.setStatus("D");
		dRules.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		dRules.setUpdatedBy(rule.getUpdatedBy());
		dRules = em.merge(dRules);
		packet.setDeliveryRules(dRules);
		return packet;

	}

	public List<ItemToDate> getItemToDate(EntityManager em, int categoryId,
			String locationId) {

		String sqlForGlobalItemId = " select i from ItemToDate i where i.locationId =? and i.categoryId = ? and i.status not in ('D','I')";
		@SuppressWarnings("unchecked")
		List<ItemToDate> resultList = em.createQuery(sqlForGlobalItemId)
				.setParameter(1, locationId).setParameter(2, categoryId)
				.getResultList();
		List<ItemToDate> itemToDates = new ArrayList<ItemToDate>();
		for (ItemToDate itemToDate : resultList) {
			Item i = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemToDate.getItemId());
			itemToDate.setItemDisplayName(i.getDisplayName());
			itemToDates.add(itemToDate);
		}
		return itemToDates;

	}

	public List<DeliveryRules> getDeliveryRules(EntityManager em, String locationId) {

		String sqlForGlobalItemId = " select i from DeliveryRules i where i.locationId =?   and i.status not in ('D','I')";
		@SuppressWarnings("unchecked")
		List<DeliveryRules> resultList = em.createQuery(sqlForGlobalItemId)
				.setParameter(1, locationId).getResultList();
		 
		 
		return resultList;

	}

}
