/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.helper;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.jaxrs.packets.SalesTaxPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup_;
import com.nirvanaxp.types.entities.orders.OrderSource_;
import com.nirvanaxp.types.entities.salestax.OrderSourceGroupToSalesTax;
import com.nirvanaxp.types.entities.salestax.OrderSourceGroupToSalesTax_;
import com.nirvanaxp.types.entities.salestax.OrderSourceToSalesTax;
import com.nirvanaxp.types.entities.salestax.OrderSourceToSalesTax_;
import com.nirvanaxp.types.entities.salestax.SalesTax;

// TODO: Auto-generated Javadoc
/**
 * The Class SalesTaxHelper.
 */
public class SalesTaxHelper
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(SalesTaxHelper.class.getName());

	/**
	 * Adds the sales tax.
	 *
	 * @param em
	 *            the em
	 * @param salesTax
	 *            the sales tax
	 * @return the sales tax
	 */
	public SalesTax addSalesTax(EntityManager em, SalesTax salesTax)
	{
		SalesTax salesTaxOld = salesTax;
		salesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		salesTax.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		salesTax = em.merge(salesTax);

		salesTax.setOrderSourceGroupToSalesTaxList(salesTaxOld.getOrderSourceGroupToSalesTaxList());

		String[] orderSourceGroupIds = new String[salesTax.getOrderSourceGroupToSalesTaxList().size()];
		int i = -1;
		for (OrderSourceGroupToSalesTax orderSourceGroupToSalesTax : salesTax.getOrderSourceGroupToSalesTaxList())
		{
			i++;
			orderSourceGroupIds[i] = orderSourceGroupToSalesTax.getSourceGroupId();
		}
		String orderSourceGroupIdList = Arrays.toString(orderSourceGroupIds).replace("[", "").replace("]", "");

		updateOrderSourceGroup(em, salesTax);

		if (orderSourceGroupIdList.length() > 0)
		{
			String queryString = "select os from OrderSource os  where os.status !='D' and  os.locationsId= " + salesTax.getLocationsId() + " and  os.orderSourceGroupId in (" + orderSourceGroupIdList
					+ ")";
			TypedQuery<OrderSource> query = em.createQuery(queryString, OrderSource.class);
			List<OrderSource> orderSources = query.getResultList();

			for (OrderSource orderSource : orderSources)
			{
				OrderSourceToSalesTax orderSourceToSalesTax = new OrderSourceToSalesTax();
				orderSourceToSalesTax.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				orderSourceToSalesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				orderSourceToSalesTax.setCreatedBy(salesTax.getCreatedBy());
				orderSourceToSalesTax.setUpdatedBy(salesTax.getUpdatedBy());
				orderSourceToSalesTax.setLocationsId(salesTax.getLocationsId());
				orderSourceToSalesTax.setTaxId(salesTax);
				orderSourceToSalesTax.setSourceId(orderSource.getId());
				orderSourceToSalesTax.setStatus("A");
				em.merge(orderSourceToSalesTax);
			}
		}
		return salesTax;

	}

	/**
	 * Update sales tax.
	 *
	 * @param em
	 *            the em
	 * @param salesTax
	 *            the sales tax
	 * @return the sales tax
	 */
	public SalesTax updateSalesTax(EntityManager em, SalesTax salesTax)
	{
		// SalesTax oldTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, salesTax.getId());

		salesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		salesTax = em.merge(salesTax);

		return salesTax;

	}

	/**
	 * Delete sales tax.
	 *
	 * @param em
	 *            the em
	 * @param salesTax
	 *            the sales tax
	 * @return the sales tax
	 */
	public SalesTax deleteSalesTax(EntityManager em, SalesTax salesTax)
	{
		salesTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, salesTax.getId());
		salesTax.setStatus("D");
		salesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		salesTax = em.merge(salesTax);

		if (salesTax.getIsItemSpecific() == 0)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceToSalesTax> criteria = builder.createQuery(OrderSourceToSalesTax.class);
			Root<OrderSourceToSalesTax> r = criteria.from(OrderSourceToSalesTax.class);
			TypedQuery<OrderSourceToSalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceToSalesTax_.taxId), salesTax.getId())));
			List<OrderSourceToSalesTax> orderSources = query.getResultList();
			for (OrderSourceToSalesTax orderSource : orderSources)
			{
				orderSource.setStatus("D");
				em.merge(orderSource);
			}
		}
		return salesTax;

	}

	/**
	 * Update sales tax id.
	 *
	 * @param em
	 *            the em
	 * @param salesTax
	 *            the sales tax
	 * @return the sales tax
	 * @throws Exception
	 *             the exception
	 */
	public SalesTax updateSalesTaxId(EntityManager em, SalesTax salesTax) throws Exception
	{
		// todo shlok need
		// modulise code
		List<OrderSourceGroupToSalesTax> orderSourceToSalesTaxs = updateOrderSourceGroup(em, salesTax);
		SalesTax oldTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, salesTax.getId());

		salesTax.setGlobalId(oldTax.getGlobalId());
		salesTax.setCreated(oldTax.getCreated());
		getChildTax(em, salesTax.getId());
		List<String> taxIdList = salesTax.getTaxIdList();
		if (taxIdList != null && taxIdList.size() != 0)
		{
			for (String taxId : taxIdList)
			{
				SalesTax newSalesTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, taxId);
				if(newSalesTax!= null){
					newSalesTax.setTaxId(salesTax.getId());
					newSalesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					newSalesTax = em.merge(newSalesTax);
				}
			
			}

			salesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			salesTax = em.merge(salesTax);

		}
		else
		{
			salesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			salesTax = em.merge(salesTax);
		}
		if (oldTax.getIsItemSpecific() == 1 && salesTax.getIsItemSpecific() == 0)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> r = criteria.from(OrderSource.class);
			List<OrderSource> orderSources = new ArrayList<OrderSource>();
			for (OrderSourceGroupToSalesTax orderSourceGroupToSalesTax : orderSourceToSalesTaxs)
			{
				TypedQuery<OrderSource> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSource_.locationsId), salesTax.getLocationsId()),
						builder.equal(r.get(OrderSource_.orderSourceGroupId), orderSourceGroupToSalesTax.getSourceGroupId()), builder.notEqual(r.get(OrderSource_.status), "D")));
				orderSources.addAll(query.getResultList());

			}

			if (orderSources != null)
			{
				for (OrderSource orderSource : orderSources)
				{
					OrderSourceToSalesTax orderSourceToSalesTax = null;

					try
					{
						CriteriaQuery<OrderSourceToSalesTax> criteriaForOSTST = builder.createQuery(OrderSourceToSalesTax.class);
						Root<OrderSourceToSalesTax> root = criteriaForOSTST.from(OrderSourceToSalesTax.class);
						TypedQuery<OrderSourceToSalesTax> query = em.createQuery(criteriaForOSTST.select(root).where(
								builder.equal(root.get(OrderSourceToSalesTax_.locationsId), salesTax.getLocationsId()), builder.equal(root.get(OrderSourceToSalesTax_.sourceId), orderSource.getId()),
								builder.equal(root.get(OrderSourceToSalesTax_.taxId), salesTax)));
						orderSourceToSalesTax = query.getSingleResult();
					}
					catch (Exception e)
					{
					}

					if (orderSourceToSalesTax == null)
					{
						orderSourceToSalesTax = new OrderSourceToSalesTax();
					}

					orderSourceToSalesTax.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					orderSourceToSalesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					orderSourceToSalesTax.setCreatedBy(salesTax.getCreatedBy());
					orderSourceToSalesTax.setUpdatedBy(salesTax.getUpdatedBy());
					orderSourceToSalesTax.setLocationsId(salesTax.getLocationsId());
					orderSourceToSalesTax.setTaxId(salesTax);
					orderSourceToSalesTax.setSourceId(orderSource.getId());
					orderSourceToSalesTax.setStatus("A");
					orderSourceToSalesTax = em.merge(orderSourceToSalesTax);
				}
			}

		}
		else if (oldTax.getIsItemSpecific() == 0 && salesTax.getIsItemSpecific() == 1)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceToSalesTax> criteria = builder.createQuery(OrderSourceToSalesTax.class);
			Root<OrderSourceToSalesTax> r = criteria.from(OrderSourceToSalesTax.class);
			TypedQuery<OrderSourceToSalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceToSalesTax_.taxId), salesTax.getId())));
			List<OrderSourceToSalesTax> orderSources = query.getResultList();
			for (OrderSourceToSalesTax orderSource : orderSources)
			{
				orderSource.setStatus("D");
				orderSource = em.merge(orderSource);
			}

		}
		else if (oldTax.getIsItemSpecific() == 0 && salesTax.getIsItemSpecific() == 0)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> r = criteria.from(OrderSource.class);
			List<OrderSource> orderSources = new ArrayList<OrderSource>();
			for (OrderSourceGroupToSalesTax orderSourceGroupToSalesTax : orderSourceToSalesTaxs)
			{
				if (orderSourceGroupToSalesTax.getStatus().equals("A") || orderSourceGroupToSalesTax.getStatus().equals("F"))
				{
					TypedQuery<OrderSource> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSource_.locationsId), salesTax.getLocationsId()),
							builder.equal(r.get(OrderSource_.orderSourceGroupId), orderSourceGroupToSalesTax.getSourceGroupId()), builder.notEqual(r.get(OrderSource_.status), "D")));
					orderSources.addAll(query.getResultList());
				}
			}
			List<OrderSourceToSalesTax> oldOrderSourceToSalesTaxList = new ArrayList<OrderSourceToSalesTax>();
			List<OrderSourceToSalesTax> removeOrderSourceToSalesTaxList = new ArrayList<OrderSourceToSalesTax>();
			try
			{
				CriteriaQuery<OrderSourceToSalesTax> criteriaForOSTST = builder.createQuery(OrderSourceToSalesTax.class);
				Root<OrderSourceToSalesTax> root = criteriaForOSTST.from(OrderSourceToSalesTax.class);
				TypedQuery<OrderSourceToSalesTax> query = em.createQuery(criteriaForOSTST.select(root).where(builder.equal(root.get(OrderSourceToSalesTax_.locationsId), salesTax.getLocationsId()),
						builder.equal(root.get(OrderSourceToSalesTax_.taxId), salesTax), builder.notEqual(root.get(OrderSourceToSalesTax_.status), "D")));
				oldOrderSourceToSalesTaxList = query.getResultList();
			}
			catch (Exception e)
			{
			}
			if (orderSources != null)
			{
				for (OrderSource orderSource : orderSources)
				{
					OrderSourceToSalesTax orderSourceToSalesTax = new OrderSourceToSalesTax();

					try
					{
						CriteriaQuery<OrderSourceToSalesTax> criteriaForOSTST = builder.createQuery(OrderSourceToSalesTax.class);
						Root<OrderSourceToSalesTax> root = criteriaForOSTST.from(OrderSourceToSalesTax.class);
						TypedQuery<OrderSourceToSalesTax> query = em.createQuery(criteriaForOSTST.select(root).where(
								builder.equal(root.get(OrderSourceToSalesTax_.locationsId), salesTax.getLocationsId()), builder.equal(root.get(OrderSourceToSalesTax_.sourceId), orderSource.getId()),
								builder.equal(root.get(OrderSourceToSalesTax_.taxId), salesTax)));
						orderSourceToSalesTax = query.getSingleResult();
					}
					catch (Exception e)
					{
					}
					if (oldOrderSourceToSalesTaxList != null)
					{
						if (orderSourceToSalesTax != null)
						{
							for (OrderSourceToSalesTax oldOrderSourceToSalesTax : oldOrderSourceToSalesTaxList)
							{
								if (oldOrderSourceToSalesTax.getSourceId().equals(orderSource.getId()))
								{
									removeOrderSourceToSalesTaxList.add(oldOrderSourceToSalesTax);

								}
							}
						}
					}
					if (orderSourceToSalesTax == null)
					{
						orderSourceToSalesTax = new OrderSourceToSalesTax();
					}
					orderSourceToSalesTax.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					orderSourceToSalesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					orderSourceToSalesTax.setCreatedBy(salesTax.getCreatedBy());
					orderSourceToSalesTax.setUpdatedBy(salesTax.getUpdatedBy());
					orderSourceToSalesTax.setLocationsId(salesTax.getLocationsId());
					orderSourceToSalesTax.setTaxId(salesTax);
					orderSourceToSalesTax.setSourceId(orderSource.getId());
					orderSourceToSalesTax.setStatus("A");
					orderSourceToSalesTax = em.merge(orderSourceToSalesTax);
				}

			}
			if (oldOrderSourceToSalesTaxList != null)
			{
				oldOrderSourceToSalesTaxList.removeAll(removeOrderSourceToSalesTaxList);
				for (OrderSourceToSalesTax oldOrderSourceToSalesTax : oldOrderSourceToSalesTaxList)
				{
					oldOrderSourceToSalesTax.setStatus("D");
					oldOrderSourceToSalesTax = em.merge(oldOrderSourceToSalesTax);
				}
			}

		}

		return salesTax;

	}

	/**
	 * Gets the child tax.
	 *
	 * @param em
	 *            the em
	 * @param salesTaxId
	 *            the sales tax id
	 * @return the child tax
	 */
	public void getChildTax(EntityManager em, String salesTaxId)
	{
		String queryString = "select id from sales_tax where tax_id = ? and status not in ('D','I') ";
		Query query = em.createNativeQuery(queryString).setParameter(1, salesTaxId);
		@SuppressWarnings("unchecked")
		List<String> dbTaxIdList = query.getResultList();
		for (String taxId : dbTaxIdList)
		{
			SalesTax newSalesTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, taxId);
			newSalesTax.setTaxId(null);
			newSalesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			newSalesTax = em.merge(newSalesTax);
		}

	}

	/**
	 * Update order source group.
	 *
	 * @param em
	 *            the em
	 * @param salesTax
	 *            the sales tax
	 * @return the list
	 */
	public List<OrderSourceGroupToSalesTax> updateOrderSourceGroup(EntityManager em, SalesTax salesTax)
	{
		List<OrderSourceGroupToSalesTax> list = new ArrayList<OrderSourceGroupToSalesTax>();

		if (salesTax != null && salesTax.getOrderSourceGroupToSalesTaxList() != null && salesTax.getOrderSourceGroupToSalesTaxList().size() > 0)
		{
			for (OrderSourceGroupToSalesTax orderSourceGroupToSalesTax : salesTax.getOrderSourceGroupToSalesTaxList())
			{
				String status=orderSourceGroupToSalesTax.getStatus();
				try
				{
					
					CriteriaBuilder builder = em.getCriteriaBuilder();
					CriteriaQuery<OrderSourceGroupToSalesTax> criteria = builder.createQuery(OrderSourceGroupToSalesTax.class);
					Root<OrderSourceGroupToSalesTax> root = criteria.from(OrderSourceGroupToSalesTax.class);
					TypedQuery<OrderSourceGroupToSalesTax> query = em.createQuery(criteria.select(root).where(
							builder.equal(root.get(OrderSourceGroupToSalesTax_.locationsId), salesTax.getLocationsId()),
							builder.equal(root.get(OrderSourceGroupToSalesTax_.sourceGroupId), orderSourceGroupToSalesTax.getSourceGroupId()),
							builder.equal(root.get(OrderSourceGroupToSalesTax_.taxId), salesTax.getId()), builder.notEqual(root.get(OrderSourceToSalesTax_.status), "D")));
					orderSourceGroupToSalesTax = query.getSingleResult();
				}
				catch (Exception e)
				{
					// todo shlok need
					// handle proper Exception
				}
				
				if (orderSourceGroupToSalesTax == null)
				{
					orderSourceGroupToSalesTax = new OrderSourceGroupToSalesTax();
				}
				orderSourceGroupToSalesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				orderSourceGroupToSalesTax.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				orderSourceGroupToSalesTax.setCreatedBy(salesTax.getCreatedBy());
				orderSourceGroupToSalesTax.setUpdatedBy(salesTax.getUpdatedBy());
				orderSourceGroupToSalesTax.setLocationsId(salesTax.getLocationsId());
				orderSourceGroupToSalesTax.setTaxId(salesTax.getId());
				orderSourceGroupToSalesTax.setStatus(status);
				orderSourceGroupToSalesTax = em.merge(orderSourceGroupToSalesTax);
				list.add(orderSourceGroupToSalesTax);

			}
		}
		return list;
	}

	/**
	 * Adds the multiple location sales tax.
	 *
	 * @param em
	 *            the em
	 * @param salesTax
	 *            the sales tax
	 * @param salesTaxPacket
	 *            the sales tax packet
	 * @param request
	 *            the request
	 * @return the sales tax
	 * @throws Exception
	 *             the exception
	 */
	public SalesTax addMultipleLocationSalesTax(EntityManager em, SalesTax salesTax, SalesTaxPacket salesTaxPacket, HttpServletRequest request) throws Exception
	{
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		if (salesTaxPacket.getLocationId().trim().length() > 0)
		{
			locationIds = salesTaxPacket.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (salesTax != null && salesTaxPacket.getIsBaseLocationUpdate() == 1)
		{
			// adding or updating global item
			salesTax.setLocationsId(baseLocation.getId());

			SalesTax globalSalesTax = addSalesTax(em, salesTax);
			salesTaxPacket.setSalesTax(globalSalesTax);
			salesTaxPacket.setLocationsListId("");
			String json = new StoreForwardUtility().returnJsonPacket(salesTaxPacket, "SalesTaxPacket", request);
			new StoreForwardUtility().callSynchPacketsWithServer(json, request, salesTaxPacket.getLocationId(),
					Integer.parseInt(salesTaxPacket.getMerchantId()));
			em.getTransaction().commit();
			em.getTransaction().begin();
			// now add/update child location
			for (String locationId : locationIds)
			{
				String locationsId = locationId;
				if (locationsId != baseLocation.getId())
				{
					SalesTax local = new SalesTax().getSalesTax(salesTax);
					local.setGlobalId(globalSalesTax.getId());
					local.setLocationsId(locationId);
					if (local.getTaxId() !=null)
					{
						SalesTax globalTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, local.getTaxId());
						SalesTax localTax = getSalesTaxByNameAndLocationId(em, globalTax.getTaxName(), locationsId);
						if (localTax != null)
							local.setTaxId(localTax.getId());

					}
					local.setOrderSourceGroupToSalesTaxList(getOrderSourceGroupToSalesTax(em, locationsId, local.getOrderSourceGroupToSalesTaxList(), null, false));
					local = addSalesTax(em, local);
					salesTaxPacket.setSalesTax(local);
					salesTaxPacket.setLocalServerURL(0);
					String json2 = new StoreForwardUtility().returnJsonPacket(salesTaxPacket, "SalesTaxPacket", request);
					new StoreForwardUtility().callSynchPacketsWithServer(json2, request, salesTaxPacket.getLocationId(),
							Integer.parseInt(salesTaxPacket.getMerchantId()));
				}
			}
		}
		 
		return salesTax;
	}

	/**
	 * Update multiple locations sales tax.
	 *
	 * @param em
	 *            the em
	 * @param salesTax
	 *            the sales tax
	 * @param salesTaxPacket
	 *            the sales tax packet
	 * @param request
	 *            the request
	 * @return the sales tax
	 * @throws Exception
	 *             the exception
	 */
	public SalesTax updateMultipleLocationsSalesTax(EntityManager em, SalesTax salesTax, SalesTaxPacket salesTaxPacket, HttpServletRequest request) throws Exception
	{
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		logger.severe("salesTaxPacket.getLocationsListId()====================================================================="+salesTaxPacket.getLocationsListId());
		
		if (salesTaxPacket.getLocationsListId()!=null &&  salesTaxPacket.getLocationsListId().trim().length() > 0)
		{
			locationIds = salesTaxPacket.getLocationsListId().split(",");
		}

		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (salesTax != null && salesTaxPacket.getIsBaseLocationUpdate() == 1)
		{
			List<OrderSourceGroupToSalesTax> orderSourceGroupToSalesTaxs=salesTaxPacket.getSalesTax().getOrderSourceGroupToSalesTaxList();
			// adding or updating global item
			SalesTax globalSalesTax = updateSalesTaxId(em, salesTax);
			globalSalesTax.setOrderSourceGroupToSalesTaxList(orderSourceGroupToSalesTaxs);
			salesTaxPacket.setSalesTax(globalSalesTax);
			salesTaxPacket.setLocationsListId(null);
			// now add/update child location
			if (locationIds != null )
			{
				for (String locationId : locationIds)
				{
					String locationsId = locationId;
					
					if (!locationsId.equals(baseLocation.getId()))
					{
						String json = new StoreForwardUtility().returnJsonPacket(salesTaxPacket, "SalesTaxPacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
								Integer.parseInt(salesTaxPacket.getMerchantId()));
						
						SalesTax local = new SalesTax().getSalesTax(salesTax);
						SalesTax salesTaxes = getSalesTaxByGlobalIdAndLocationId(em, locationsId, globalSalesTax.getId());
						if (salesTaxes != null && salesTaxes.getId() !=null)
						{
							local.setGlobalId(salesTaxes.getGlobalId());
							local.setId(salesTaxes.getId());
						}
						else
						{
							local.setGlobalId(globalSalesTax.getId());
						}
						if (local.getTaxId() !=null)
						{
							SalesTax globalTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, local.getTaxId());
							SalesTax localTax = getSalesTaxByNameAndLocationId(em, globalTax.getTaxName(), locationsId);
							if (localTax != null)
								local.setTaxId(localTax.getId());

						}
						List<String> taxIdList = new ArrayList<String>();
						if (local.getTaxIdList() != null)
						{
							for (String i : local.getTaxIdList())
							{
								SalesTax localSalesTaxes = getSalesTaxByGlobalIdAndLocationId(em, locationsId, i);
								if (localSalesTaxes != null)
									taxIdList.add(localSalesTaxes.getId());
							}
						}

						local.setTaxIdList(taxIdList);

						local.setLocationsId(locationId);
						if (local.getOrderSourceGroupToSalesTaxList() != null)
						{
							local.setOrderSourceGroupToSalesTaxList(getOrderSourceGroupToSalesTax(em, locationsId, local.getOrderSourceGroupToSalesTaxList(), local.getId(), true));
						}
						salesTaxPacket.setSalesTax(local);
						local = updateSalesTaxId(em, local);
						
						salesTaxPacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(salesTaxPacket, "SalesTaxPacket", request);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request, salesTaxPacket.getLocationId(),
								Integer.parseInt(salesTaxPacket.getMerchantId()));
					}
				}
			}
		}
		return salesTax;
	}

	/**
	 * Gets the sales tax by global id and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalId
	 *            the global id
	 * @return the sales tax by global id and location id
	 */
	private SalesTax getSalesTaxByGlobalIdAndLocationId(EntityManager em, String locationId, String globalId)
	{
		SalesTax salesTax = null;
		String queryString =null;
		try
		{
			if(globalId==null || globalId.equals("")|| globalId.equals("0")){
				 queryString = "select s from SalesTax s where s.locationsId='"+locationId+"' and (s.globalId is null or s.globalId ='"+0+"' or s.globalId ='"+""+"')   and status !='D' ";
					
			}else {
				 queryString = "select s from SalesTax s where s.globalId ='"+globalId+"' and s.locationsId='"+locationId+"' and status !='D' ";
					
			}
			logger.severe("queryString=============================================================="+queryString);
			TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class);
			salesTax = query.getSingleResult();
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe(e);

		}
		logger.severe("salesTax=============================================================="+salesTax);
		
		if (salesTax == null)
		{
			SalesTax global = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, globalId);
			if (global != null)
			{
				salesTax = new SalesTax().getSalesTax(global);
				salesTax.setLocationsId(locationId);
				salesTax.setGlobalId(globalId);
				salesTax.setId(new StoreForwardUtility().generateUUID());
				salesTax = em.merge(salesTax);

			}

		}
		return salesTax;
	}

	/**
	 * Gets the sales tax by name and location id.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param locationId
	 *            the location id
	 * @return the sales tax by name and location id
	 */
	private SalesTax getSalesTaxByNameAndLocationId(EntityManager em, String name, String locationId)
	{
		try
		{
			String queryString = "select s from SalesTax s where s.taxName =? and s.locationsId=?  ";
			TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class).setParameter(1, name).setParameter(2, locationId);
			return query.getSingleResult();
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the sales tax by global id.
	 *
	 * @param em
	 *            the em
	 * @param globalId
	 *            the global id
	 * @return the sales tax by global id
	 */
	private List<SalesTax> getSalesTaxByGlobalId(EntityManager em, String globalId)
	{
		try
		{
			String queryString = "select s from SalesTax s where s.globalId =?  ";
			TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class).setParameter(1, globalId);
			return query.getResultList();
		}
		catch (NoResultException e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Delete multiple location sales tax.
	 *
	 * @param em
	 *            the em
	 * @param salesTax
	 *            the sales tax
	 * @param salesTaxPacket
	 *            the sales tax packet
	 * @param request
	 *            the request
	 * @return the sales tax
	 * @throws Exception
	 *             the exception
	 */
	public SalesTax deleteMultipleLocationSalesTax(EntityManager em, SalesTax salesTax, SalesTaxPacket salesTaxPacket, HttpServletRequest request) throws Exception
	{
		// delete baselocation
		salesTax = deleteSalesTax(em, salesTax);
		// get all sublocations
		List<SalesTax> salesTaxes = getSalesTaxByGlobalId(em, salesTax.getId());
		// delete sublocation
		for (SalesTax tax : salesTaxes)
		{
			deleteSalesTax(em, tax);
		}
		return salesTax;
	}

	/**
	 * Gets the order source group to sales tax.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param globalList
	 *            the global list
	 * @param taxId
	 *            the tax id
	 * @param isUpdate
	 *            the is update
	 * @return the order source group to sales tax
	 * @throws Exception
	 *             the exception
	 */
	private List<OrderSourceGroupToSalesTax> getOrderSourceGroupToSalesTax(EntityManager em, String locationId, List<OrderSourceGroupToSalesTax> globalList, String taxId, boolean isUpdate) throws Exception
	{
		List<OrderSourceGroupToSalesTax> local = new ArrayList<OrderSourceGroupToSalesTax>();
		for (OrderSourceGroupToSalesTax global : globalList)
		{
			
			OrderSourceGroup group = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, global.getSourceGroupId());
			OrderSourceGroup localOrderGroup = getOrderSourceGroupByLocationIdAndName(em, locationId, group.getName());
			if (isUpdate && localOrderGroup != null)
			{
				OrderSourceGroupToSalesTax salesTax = getOrderSourceGroupToSalesTaxByLocationIdAndName(em, localOrderGroup.getId(), taxId);
				
				if (salesTax != null)
				{
					global.setSourceGroupId(salesTax.getSourceGroupId());
					global.setTaxId(salesTax.getTaxId());
					global.setId(salesTax.getId());
				}
				else
				{
					global.setSourceGroupId(localOrderGroup.getId());
					global.setTaxId(taxId);
				}

				local.add(global);

			}
			else
			{

				if (localOrderGroup != null)
				{
					global.setSourceGroupId(localOrderGroup.getId());
					global.setTaxId(taxId);

					local.add(global);
				}

			}
		}
		return local;
	}

	/**
	 * Gets the order source group by location id and name.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the order source group by location id and name
	 * @throws Exception
	 *             the exception
	 */
	public OrderSourceGroup getOrderSourceGroupByLocationIdAndName(EntityManager em, String locationId, String name) throws Exception
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.locationsId), locationId),
					builder.equal(orderSourceGroup.get(OrderSourceGroup_.name), name), builder.notEqual(orderSourceGroup.get(OrderSourceGroup_.status), "D")));
			return query.getSingleResult();
		}
		catch (NoResultException e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe("No Result Found -" + name);
		}
		return null;
	}

	/**
	 * Gets the order source group to sales tax by location id and name.
	 *
	 * @param em
	 *            the em
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @param taxId
	 *            the tax id
	 * @return the order source group to sales tax by location id and name
	 * @throws Exception
	 *             the exception
	 */
	public OrderSourceGroupToSalesTax getOrderSourceGroupToSalesTaxByLocationIdAndName(EntityManager em, String orderSourceGroupId, String taxId) throws Exception
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroupToSalesTax> criteria = builder.createQuery(OrderSourceGroupToSalesTax.class);
			Root<OrderSourceGroupToSalesTax> orderSourceGroup = criteria.from(OrderSourceGroupToSalesTax.class);
			TypedQuery<OrderSourceGroupToSalesTax> query = em.createQuery(criteria.select(orderSourceGroup)
					.where(builder.equal(orderSourceGroup.get(OrderSourceGroupToSalesTax_.taxId), taxId),
							builder.equal(orderSourceGroup.get(OrderSourceGroupToSalesTax_.sourceGroupId), orderSourceGroupId),
							builder.notEqual(orderSourceGroup.get(OrderSourceGroupToSalesTax_.status), "D")));
			return query.getSingleResult();
		}
		catch (NoResultException e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe("No Result Found -" + orderSourceGroupId);
		}
		return null;
	}

}
