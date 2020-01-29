/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.relationalentity;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.catalog.category.CategoryToDiscount;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.ItemsToDiscount;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsChar;
import com.nirvanaxp.types.entities.catalog.items.ItemsToNutritions;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.ItemsToSchedule;
import com.nirvanaxp.types.entities.inventory.LocationsToSupplier;
import com.nirvanaxp.types.entities.protocol.RelationalEntitiesForStringId;
import com.nirvanaxp.types.entities.roles.RolesToFunction;

public class EntityRelationshipManager
{
	private static final NirvanaLogger logger = new NirvanaLogger(EntityRelationshipManager.class.getName());

	/*public void manageRelations(EntityManager em, POSNirvanaBaseClass item, List<? extends RelationalEntities> relationalEntitiesListCurrent, List<? extends POSNirvanaBaseClass> baseEntitiesListNew,
			Class<?> classType)
	{
		int indexForDiscount = 0;

		// check if ever the discount assignment hs happened for an
		// item
		if (relationalEntitiesListCurrent != null && relationalEntitiesListCurrent.size() > 0)
		{

			// yes the assignment has happened

			// check if client has applied new discounts or removed
			// all discounts
			if (baseEntitiesListNew != null && baseEntitiesListNew.size() > 0)
			{
				// this item has some discount applied, hence manage
				// relations

				// we need to alter these relationship object first

				// for loop will handle 2 scenario
				// if itemsToDiscount size > discountsList size say
				// earlier 5 discounts were applied now only 2 are
				// applied and 3 removed then, it will change the
				// first 2 rows and deactivate the other rows
				for (RelationalEntities itemsToDiscount : relationalEntitiesListCurrent)
				{

					POSNirvanaBaseClass relationobj = (POSNirvanaBaseClass) itemsToDiscount;

					if (indexForDiscount < baseEntitiesListNew.size())
					{

						// get the object sent by client
						POSNirvanaBaseClass objHavingToRelation = baseEntitiesListNew.get(indexForDiscount);

						if (objHavingToRelation != null)
						{

							// change the object relation
							((RelationalEntities) relationobj).setBaseToObjectRelation(objHavingToRelation.getId());
							relationobj.setStatus("A");
							relationobj.setUpdatedBy(item.getUpdatedBy());
							relationobj.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

							em.merge(relationobj);

						}
					}
					else
					{
						// rest all item to discount must get
						// deactivated
						relationobj.setStatus("D");
						relationobj.setUpdatedBy(item.getUpdatedBy());
						relationobj.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

						em.merge(relationobj);

					}
					indexForDiscount++;

				}

				// check if earlier 2 discounts were applied now 4 are
				// applied we need to create 2 new rows
				if (indexForDiscount < baseEntitiesListNew.size())
				{
					while (indexForDiscount < baseEntitiesListNew.size())
					{

						POSNirvanaBaseClass objHavingToRelation = baseEntitiesListNew.get(indexForDiscount);
						createRelationalEntity(objHavingToRelation.getId(), classType, em, item.getId(), item.getUpdatedBy());
						indexForDiscount++;

					}
				}

			}
			else
			{
				// all discounts are remove and the itemsToDiscount
				// relationship is there, hence deactivate all
				// relationship
				for (RelationalEntities relationobj : relationalEntitiesListCurrent)
				{
					((POSNirvanaBaseClass) relationobj).setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					((POSNirvanaBaseClass) relationobj).setUpdatedBy(item.getUpdatedBy());
					((POSNirvanaBaseClass) relationobj).setStatus("D");
					em.merge(relationobj);

				}
			}

		}
		else
		{

			// we don't have item to discount relationship yet(i.e
			// client has never assigned any discount to this item
			// ever)
			// hence item to discount relation and these row into
			// database
			if (baseEntitiesListNew != null && baseEntitiesListNew.size() > 0)
			{
				for (POSNirvanaBaseClass objHavingToRelation : baseEntitiesListNew)
				{
					createRelationalEntity(objHavingToRelation.getId(), classType, em, item.getId(), item.getUpdatedBy());
				}
			}

		}

		// apply discount relationship

	}*/
	public void manageRelations(EntityManager em, POSNirvanaBaseClassWithoutGeneratedIds item, List<? extends RelationalEntitiesForStringId> relationalEntitiesListCurrent, List<? extends POSNirvanaBaseClassWithoutGeneratedIds> baseEntitiesListNew,
			Class<?> classType)
	{
		int indexForDiscount = 0;

		// check if ever the discount assignment hs happened for an
		// item
		if (relationalEntitiesListCurrent != null && relationalEntitiesListCurrent.size() > 0)
		{

			// yes the assignment has happened

			// check if client has applied new discounts or removed
			// all discounts
			if (baseEntitiesListNew != null && baseEntitiesListNew.size() > 0)
			{
				// this item has some discount applied, hence manage
				// relations

				// we need to alter these relationship object first

				// for loop will handle 2 scenario
				// if itemsToDiscount size > discountsList size say
				// earlier 5 discounts were applied now only 2 are
				// applied and 3 removed then, it will change the
				// first 2 rows and deactivate the other rows
				for (RelationalEntitiesForStringId itemsToDiscount : relationalEntitiesListCurrent)
				{

					POSNirvanaBaseClassWithoutGeneratedIds relationobj = (POSNirvanaBaseClassWithoutGeneratedIds) itemsToDiscount;

					if (indexForDiscount < baseEntitiesListNew.size())
					{

						// get the object sent by client
						POSNirvanaBaseClassWithoutGeneratedIds objHavingToRelation = baseEntitiesListNew.get(indexForDiscount);

						if (objHavingToRelation != null)
						{

							// change the object relation
							((RelationalEntitiesForStringId) relationobj).setBaseToObjectRelation(objHavingToRelation.getId());
							relationobj.setStatus("A");
							relationobj.setUpdatedBy(item.getUpdatedBy());
							relationobj.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

							relationobj = em.merge(relationobj);
							em.getTransaction().commit();
							em.getTransaction().begin();
						}
					}
					else
					{
						// rest all item to discount must get
						// deactivated
						relationobj.setStatus("D");
						relationobj.setUpdatedBy(item.getUpdatedBy());
						relationobj.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

						em.merge(relationobj);
						em.getTransaction().commit();
						em.getTransaction().begin();
					}
					indexForDiscount++;

				}

				// check if earlier 2 discounts were applied now 4 are
				// applied we need to create 2 new rows
				if (indexForDiscount < baseEntitiesListNew.size())
				{
					while (indexForDiscount < baseEntitiesListNew.size())
					{

						POSNirvanaBaseClassWithoutGeneratedIds objHavingToRelation = baseEntitiesListNew.get(indexForDiscount);
						createRelationalEntityForStringId(objHavingToRelation.getId(), classType, em, item.getId(), item.getUpdatedBy());
						indexForDiscount++;

					}
				}

			}
			else
			{
				// all discounts are remove and the itemsToDiscount
				// relationship is there, hence deactivate all
				// relationship
				for (RelationalEntitiesForStringId relationobj : relationalEntitiesListCurrent)
				{
					((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setUpdatedBy(item.getUpdatedBy());
					((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setStatus("D");
					if(((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).getId()==null){
						((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setId(new StoreForwardUtility().generateUUID());
					}
					em.merge(relationobj);
					em.getTransaction().commit();
					em.getTransaction().begin();
				}
			}

		}
		else
		{

			// we don't have item to discount relationship yet(i.e
			// client has never assigned any discount to this item
			// ever)
			// hence item to discount relation and these row into
			// database
			if (baseEntitiesListNew != null && baseEntitiesListNew.size() > 0)
			{
				for (POSNirvanaBaseClassWithoutGeneratedIds objHavingToRelation : baseEntitiesListNew)
				{
					createRelationalEntityForStringId(objHavingToRelation.getId(), classType, em, item.getId(), item.getUpdatedBy());
				}
			}

		}

		// apply discount relationship

	}

	private void createRelationalEntity(String BaseToObjectId, Class<?> classType, EntityManager em, String itemId, String updatedBy)
	{

		RelationalEntitiesForStringId relationobj = null;

		if (classType.equals(ItemsToDiscount.class))
		{
			relationobj = new ItemsToDiscount();
		}
		else if (classType.equals(ItemsToItemsAttributeType.class))
		{
			relationobj = new ItemsToItemsAttributeType();
		}
		else if (classType.equals(ItemsToItemsChar.class))
		{
			relationobj = new ItemsToItemsChar();
		}
		else if (classType.equals(ItemsToItemsAttribute.class))
		{
			relationobj = new ItemsToItemsAttribute();
		}
		else if (classType.equals(CategoryItem.class))
		{
			relationobj = new CategoryItem();
		}
		else if (classType.equals(ItemsToPrinter.class))
		{
			relationobj = new ItemsToPrinter();
		}
		else if (classType.equals(CategoryToPrinter.class))
		{
			relationobj = new CategoryToPrinter();
		}
		else if (classType.equals(CategoryToDiscount.class))
		{
			relationobj = new CategoryToDiscount();
		}
		else if (classType.equals(LocationsToSupplier.class))
		{
			relationobj = new LocationsToSupplier();
		}
		else if (classType.equals(RolesToFunction.class))
		{
			relationobj = new RolesToFunction();
		}
		else if (classType.equals(ItemsToNutritions.class))
		{
			relationobj = new ItemsToNutritions();
		}else if (classType.equals(ItemsToSchedule.class))
		{
			relationobj = new ItemsToSchedule();
		}
		
//		else if (classType.equals(ItemToSupplier.class)) { 
//			relationobj = 	new ItemToSalesTax(); 
//		}
			 

		if (relationobj != null)
		{
			relationobj.setBaseRelation(itemId);
			relationobj.setBaseToObjectRelation(BaseToObjectId);
			((POSNirvanaBaseClass) relationobj).setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			((POSNirvanaBaseClass) relationobj).setUpdatedBy(updatedBy);
			((POSNirvanaBaseClass) relationobj).setCreatedBy(updatedBy);
			((POSNirvanaBaseClass) relationobj).setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			((POSNirvanaBaseClass) relationobj).setStatus("A");

			relationobj =em.merge(relationobj);
			

		}

	}
	private void createRelationalEntityForStringId(String BaseToObjectId, Class<?> classType, EntityManager em, String itemId, String updatedBy)
	{

		RelationalEntitiesForStringId relationobj = null;

		if (classType.equals(ItemsToDiscount.class))
		{
			relationobj = new ItemsToDiscount();
		}
		else if (classType.equals(ItemsToItemsAttributeType.class))
		{
			relationobj = new ItemsToItemsAttributeType();
		}
		else if (classType.equals(ItemsToItemsChar.class))
		{
			relationobj = new ItemsToItemsChar();
		}
		else if (classType.equals(ItemsToItemsAttribute.class))
		{
			relationobj = new ItemsToItemsAttribute();
		}
		else if (classType.equals(CategoryItem.class))
		{
			relationobj = new CategoryItem();
		}
		else if (classType.equals(ItemsToPrinter.class))
		{
			relationobj = new ItemsToPrinter();
		}
		else if (classType.equals(CategoryToPrinter.class))
		{
			relationobj = new CategoryToPrinter();
		}
		else if (classType.equals(CategoryToDiscount.class))
		{
			relationobj = new CategoryToDiscount();
		}
		else if (classType.equals(LocationsToSupplier.class))
		{
			relationobj = new LocationsToSupplier();
		}
		else if (classType.equals(RolesToFunction.class))
		{
			relationobj = new RolesToFunction();
		}
		else if (classType.equals(ItemsToNutritions.class))
		{
			relationobj = new ItemsToNutritions();
		}else if (classType.equals(ItemsToSchedule.class))
		{
			relationobj = new ItemsToSchedule();
		}
		
//		else if (classType.equals(ItemToSupplier.class)) { 
//			relationobj = 	new ItemToSalesTax(); 
//		}
			 

		if (relationobj != null)
		{
			relationobj.setBaseRelation(itemId);
			relationobj.setBaseToObjectRelation(BaseToObjectId);
			((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setUpdatedBy(updatedBy);
			((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setCreatedBy(updatedBy);
			((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setStatus("A");
			if(((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).getId()==null){
				((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setId(new StoreForwardUtility().generateUUID());
			}
			try {
				relationobj = em.merge(relationobj);
				em.getTransaction().commit();
			} catch (Exception e) {
				logger.severe(e);
			}
			em.getTransaction().begin();
		}

	}
	public void manageRelation(EntityManager em, POSNirvanaBaseClassWithoutGeneratedIds item, List<? extends RelationalEntitiesForStringId> relationalEntitiesListCurrent, List<? extends POSNirvanaBaseClassWithoutGeneratedIds> baseEntitiesListNew,
			Class<?> classType)
	{
		int indexForDiscount = 0;

		// check if ever the discount assignment hs happened for an
		// item
		if (relationalEntitiesListCurrent != null && relationalEntitiesListCurrent.size() > 0)
		{

			// yes the assignment has happened

			// check if client has applied new discounts or removed
			// all discounts
			if (baseEntitiesListNew != null && baseEntitiesListNew.size() > 0)
			{
				// this item has some discount applied, hence manage
				// relations

				// we need to alter these relationship object first

				// for loop will handle 2 scenario
				// if itemsToDiscount size > discountsList size say
				// earlier 5 discounts were applied now only 2 are
				// applied and 3 removed then, it will change the
				// first 2 rows and deactivate the other rows
				for (RelationalEntitiesForStringId itemsToDiscount : relationalEntitiesListCurrent)
				{

					POSNirvanaBaseClass relationobj = (POSNirvanaBaseClass) itemsToDiscount;

					if (indexForDiscount < baseEntitiesListNew.size())
					{

						// get the object sent by client
						POSNirvanaBaseClassWithoutGeneratedIds objHavingToRelation = baseEntitiesListNew.get(indexForDiscount);

						if (objHavingToRelation != null)
						{

							// change the object relation
							((RelationalEntitiesForStringId) relationobj).setBaseToObjectRelation(objHavingToRelation.getId());
							relationobj.setStatus("A");
							relationobj.setUpdatedBy(item.getUpdatedBy());
							relationobj.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

							em.merge(relationobj);

						}
					}
					else
					{
						// rest all item to discount must get
						// deactivated
						relationobj.setStatus("D");
						relationobj.setUpdatedBy(item.getUpdatedBy());
						relationobj.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

						em.merge(relationobj);

					}
					indexForDiscount++;

				}

				// check if earlier 2 discounts were applied now 4 are
				// applied we need to create 2 new rows
				if (indexForDiscount < baseEntitiesListNew.size())
				{
					while (indexForDiscount < baseEntitiesListNew.size())
					{

						POSNirvanaBaseClassWithoutGeneratedIds objHavingToRelation = baseEntitiesListNew.get(indexForDiscount);
						createRelationalEntity(objHavingToRelation.getId(), classType, em, item.getId(), item.getUpdatedBy());
						indexForDiscount++;

					}
				}

			}
			else
			{
				// all discounts are remove and the itemsToDiscount
				// relationship is there, hence deactivate all
				// relationship
				for (RelationalEntitiesForStringId relationobj : relationalEntitiesListCurrent)
				{
					((POSNirvanaBaseClass) relationobj).setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					((POSNirvanaBaseClass) relationobj).setUpdatedBy(item.getUpdatedBy());
					((POSNirvanaBaseClass) relationobj).setStatus("D");
					em.merge(relationobj);

				}
			}

		}
		else
		{

			// we don't have item to discount relationship yet(i.e
			// client has never assigned any discount to this item
			// ever)
			// hence item to discount relation and these row into
			// database
			if (baseEntitiesListNew != null && baseEntitiesListNew.size() > 0)
			{
				for (POSNirvanaBaseClassWithoutGeneratedIds objHavingToRelation : baseEntitiesListNew)
				{
					createRelationalEntity(objHavingToRelation.getId(), classType, em, item.getId(), item.getUpdatedBy());
				}
			}

		}

		// apply discount relationship

	}
	public void manageRelationsForBaseClass(EntityManager em, POSNirvanaBaseClassWithoutGeneratedIds item, List<? extends RelationalEntitiesForStringId> relationalEntitiesListCurrent, List<? extends POSNirvanaBaseClassWithoutGeneratedIds> baseEntitiesListNew,
			Class<?> classType)
	{
		int indexForDiscount = 0;
		
		// check if ever the discount assignment hs happened for an
		// item
		
		if (relationalEntitiesListCurrent != null && relationalEntitiesListCurrent.size() > 0)
		{

			// yes the assignment has happened

			// check if client has applied new discounts or removed
			// all discounts
			
			if (baseEntitiesListNew != null && baseEntitiesListNew.size() > 0 )
			{
				// this item has some discount applied, hence manage
				// relations

				// we need to alter these relationship object first

				// for loop will handle 2 scenario
				// if itemsToDiscount size > discountsList size say
				// earlier 5 discounts were applied now only 2 are
				// applied and 3 removed then, it will change the
				// first 2 rows and deactivate the other rows
				for (RelationalEntitiesForStringId itemsToDiscount : relationalEntitiesListCurrent)
				{

					POSNirvanaBaseClass relationobj = (POSNirvanaBaseClass) itemsToDiscount;
					if (indexForDiscount < baseEntitiesListNew.size())
					{

						// get the object sent by client
						POSNirvanaBaseClassWithoutGeneratedIds objHavingToRelation = baseEntitiesListNew.get(indexForDiscount);
						if (objHavingToRelation != null)
						{
							
							// change the object relation
							((RelationalEntitiesForStringId) relationobj).setBaseToObjectRelation(objHavingToRelation.getId());
							relationobj.setStatus("A");
							relationobj.setUpdatedBy(item.getUpdatedBy());
							relationobj.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							relationobj=em.merge(relationobj);
						}
					}
					else
					{
						// rest all item to discount must get
						// deactivated
						relationobj.setStatus("D");
						relationobj.setUpdatedBy(item.getUpdatedBy());
						relationobj.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

						em.merge(relationobj);

					}
					indexForDiscount++;

				}

				// check if earlier 2 discounts were applied now 4 are
				// applied we need to create 2 new rows
				if (indexForDiscount < baseEntitiesListNew.size())
				{
					while (indexForDiscount < baseEntitiesListNew.size())
					{

						POSNirvanaBaseClassWithoutGeneratedIds objHavingToRelation = baseEntitiesListNew.get(indexForDiscount);
						createRelationalEntity(objHavingToRelation.getId(), classType, em, item.getId(), item.getUpdatedBy());
						indexForDiscount++;

					}
				}

			}
			else
			{
				// all discounts are remove and the itemsToDiscount
				// relationship is there, hence deactivate all
				// relationship
				for (RelationalEntitiesForStringId relationobj : relationalEntitiesListCurrent)
				{
					((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setUpdatedBy(item.getUpdatedBy());
					((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setStatus("D");
					if(((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).getId()==null){
						((POSNirvanaBaseClassWithoutGeneratedIds) relationobj).setId(new StoreForwardUtility().generateUUID());
					}
					em.merge(relationobj);

				}
			}

		}
		else
		{

			// we don't have item to discount relationship yet(i.e
			// client has never assigned any discount to this item
			// ever)
			// hence item to discount relation and these row into
			// database
			if (baseEntitiesListNew != null && baseEntitiesListNew.size() > 0)
			{
				for (POSNirvanaBaseClassWithoutGeneratedIds objHavingToRelation : baseEntitiesListNew)
				{
					logger.severe("objHavingToRelation.getId()========================================================="+objHavingToRelation.getId());
					
					createRelationalEntity(objHavingToRelation.getId(), classType, em, item.getId(), item.getUpdatedBy());
				}
			}

		}
	}
}
