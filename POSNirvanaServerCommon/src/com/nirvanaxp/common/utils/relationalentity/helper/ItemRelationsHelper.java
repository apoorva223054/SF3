/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.relationalentity.helper;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToDiscount;
import com.nirvanaxp.types.entities.catalog.items.ItemsToDiscount_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttributeType_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttribute_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsChar;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsChar_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToLocation;
import com.nirvanaxp.types.entities.catalog.items.ItemsToLocation_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToNutritions;
import com.nirvanaxp.types.entities.catalog.items.ItemsToNutritions_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToSchedule;
import com.nirvanaxp.types.entities.catalog.items.ItemsToSchedule_;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;
import com.nirvanaxp.types.entities.orders.ItemToSupplier_;


public class ItemRelationsHelper
{
	private static final NirvanaLogger logger = new NirvanaLogger(ItemRelationsHelper.class.getName());
	public boolean shouldEliminateDStatus = false;

	public boolean isShouldEliminateDStatus()
	{
		return shouldEliminateDStatus;
	}

	public void setShouldEliminateDStatus(boolean shouldEliminateDStatus)
	{
		this.shouldEliminateDStatus = shouldEliminateDStatus;
	}

	public List<ItemsToPrinter> getItemToPrinter(String itemId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToPrinter> criteria = builder.createQuery(ItemsToPrinter.class);
			Root<ItemsToPrinter> r = criteria.from(ItemsToPrinter.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<ItemsToPrinter> query = em
						.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToPrinter_.itemsId), itemId), builder.notEqual(r.get(ItemsToPrinter_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<ItemsToPrinter> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToPrinter_.itemsId), itemId)));
				return query.getResultList();
			}
		}
		return null;

	}

	public List<ItemsToDiscount> getItemToDiscounts(String itemId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToDiscount> criteria = builder.createQuery(ItemsToDiscount.class);
			Root<ItemsToDiscount> r = criteria.from(ItemsToDiscount.class);

			if (shouldEliminateDStatus)
			{
				TypedQuery<ItemsToDiscount> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToDiscount_.itemsId), itemId),
						builder.notEqual(r.get(ItemsToDiscount_.status), "D")));
				return query.getResultList();

			}
			else
			{
				TypedQuery<ItemsToDiscount> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToDiscount_.itemsId), itemId)));
				return query.getResultList();

			}

		}
		return null;

	}

	public List<ItemsToItemsAttribute> getItemsToItemsAttribute(String itemId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToItemsAttribute> criteria = builder.createQuery(ItemsToItemsAttribute.class);
			Root<ItemsToItemsAttribute> r = criteria.from(ItemsToItemsAttribute.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<ItemsToItemsAttribute> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToItemsAttribute_.itemsId), itemId),
						builder.notEqual(r.get(ItemsToItemsAttribute_.status), "D")));
				return query.getResultList();

			}
			else
			{
				TypedQuery<ItemsToItemsAttribute> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToItemsAttribute_.itemsId), itemId)));
				return query.getResultList();

			}

		}
		return null;

	}

	public List<ItemsToItemsChar> getItemsToItemsChar(String itemId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToItemsChar> criteria = builder.createQuery(ItemsToItemsChar.class);
			Root<ItemsToItemsChar> r = criteria.from(ItemsToItemsChar.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<ItemsToItemsChar> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToItemsChar_.itemsId), itemId),
						builder.notEqual(r.get(ItemsToItemsChar_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<ItemsToItemsChar> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToItemsChar_.itemsId), itemId)));
				return query.getResultList();
			}

		}
		return null;

	}
	
	
	public List<ItemsToNutritions> getItemsToNutritions(String itemId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToNutritions> criteria = builder.createQuery(ItemsToNutritions.class);
			Root<ItemsToNutritions> r = criteria.from(ItemsToNutritions.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<ItemsToNutritions> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToNutritions_.itemsId), itemId),
						builder.notEqual(r.get(ItemsToNutritions_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<ItemsToNutritions> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToNutritions_.itemsId), itemId)));
				return query.getResultList();
			}

		}
		return null;

	}
	
	public List<ItemsToSchedule> getItemsToSchedule(String itemId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToSchedule> criteria = builder.createQuery(ItemsToSchedule.class);
			Root<ItemsToSchedule> r = criteria.from(ItemsToSchedule.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<ItemsToSchedule> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToSchedule_.itemsId), itemId),
						builder.notEqual(r.get(ItemsToSchedule_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<ItemsToSchedule> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToSchedule_.itemsId), itemId)));
				return query.getResultList();
			}

		}
		return null;

	}
	
	
	
	
	public ItemToSupplier getItemToSupplier(String itemId, EntityManager em)
	{
		try {
			if (itemId != null && em != null)
			{

				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<ItemToSupplier> criteria = builder.createQuery(ItemToSupplier.class);
				Root<ItemToSupplier> r = criteria.from(ItemToSupplier.class);
				if (shouldEliminateDStatus)
				{
					TypedQuery<ItemToSupplier> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemToSupplier_.itemId), itemId),
							builder.notEqual(r.get(ItemToSupplier_.status), "D")));
					return query.getSingleResult();
				}
				else
				{
					TypedQuery<ItemToSupplier> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemToSupplier_.itemId), itemId)));
					return query.getSingleResult();
				}

			}
		} catch (Exception e) {
			
			logger.severe("No Result found for itemId " + itemId + " in ItemToSupplier");
		}
		return null;

	}

	public List<ItemsToItemsAttributeType> getItemsToItemsAttributeType(String itemId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToItemsAttributeType> criteria = builder.createQuery(ItemsToItemsAttributeType.class);
			Root<ItemsToItemsAttributeType> r = criteria.from(ItemsToItemsAttributeType.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<ItemsToItemsAttributeType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToItemsAttributeType_.itemsId), itemId),
						builder.notEqual(r.get(ItemsToItemsAttributeType_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<ItemsToItemsAttributeType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToItemsAttributeType_.itemsId), itemId),
						builder.notEqual(r.get(ItemsToItemsAttributeType_.status), "D")));
				return query.getResultList();
			}

		}
		return null;

	}

	public List<CategoryItem> getCategoryItem(String itemId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CategoryItem> criteria = builder.createQuery(CategoryItem.class);
			Root<CategoryItem> r = criteria.from(CategoryItem.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<CategoryItem> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryItem_.itemsId), itemId), builder.notEqual(r.get(CategoryItem_.status), "D")));
				return query.getResultList();

			}
			else
			{
				TypedQuery<CategoryItem> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryItem_.itemsId), itemId)));
				return query.getResultList();

			}

		}
		return null;

	}
	public List<ItemsToLocation> getItemsToLocation(String itemId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToLocation> criteria = builder.createQuery(ItemsToLocation.class);
			Root<ItemsToLocation> r = criteria.from(ItemsToLocation.class);

			if (shouldEliminateDStatus)
			{
				TypedQuery<ItemsToLocation> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToLocation_.itemsId), itemId),
						builder.notEqual(r.get(ItemsToLocation_.status), "D")));
				return query.getResultList();

			}
			else
			{
				TypedQuery<ItemsToLocation> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToLocation_.itemsId), itemId)));
				return query.getResultList();

			}

		}
		return null;

	}
	public ItemsToNutritions getItemsToNutritionsByNutritionIdItemId(String itemId,String nutritionId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			try {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<ItemsToNutritions> criteria = builder.createQuery(ItemsToNutritions.class);
				Root<ItemsToNutritions> r = criteria.from(ItemsToNutritions.class);
				 
				TypedQuery<ItemsToNutritions> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToNutritions_.itemsId), itemId),
						builder.equal(r.get(ItemsToNutritions_.nutritionsId), nutritionId),
						builder.notEqual(r.get(ItemsToNutritions_.status), "D")));
				return query.getSingleResult();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe(e);
			}
				
			 
		}
		return null;

	}
}
