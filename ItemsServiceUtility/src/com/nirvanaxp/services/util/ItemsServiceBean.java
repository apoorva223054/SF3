/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.common.utils.relationalentity.EntityRelationshipManager;
import com.nirvanaxp.common.utils.relationalentity.helper.ItemRelationsHelper;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.jaxrs.packets.ItemByLocationIdPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemToDatePacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsToLocationPacket;
import com.nirvanaxp.services.packet.ItemDetailDisplayPacket;
import com.nirvanaxp.services.packet.ItemToSupplierPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.FutureUpdate;
import com.nirvanaxp.types.entities.FutureUpdate_;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter;
import com.nirvanaxp.types.entities.catalog.category.ItemToDate;
import com.nirvanaxp.types.entities.catalog.course.Course;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem_;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemGroup;
import com.nirvanaxp.types.entities.catalog.items.ItemGroup_;
import com.nirvanaxp.types.entities.catalog.items.Item_;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeTypeToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType_;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute_;
import com.nirvanaxp.types.entities.catalog.items.ItemsChar;
import com.nirvanaxp.types.entities.catalog.items.ItemsCharToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsChar_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToDiscount;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsChar;
import com.nirvanaxp.types.entities.catalog.items.ItemsToLocation;
import com.nirvanaxp.types.entities.catalog.items.ItemsToNutritions;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.ItemsToSchedule;
import com.nirvanaxp.types.entities.catalog.items.ItemsType;
import com.nirvanaxp.types.entities.catalog.items.ItemsType_;
import com.nirvanaxp.types.entities.catalog.items.Nutritions;
import com.nirvanaxp.types.entities.custom.CatalogDisplayService;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.DiscountsType;
import com.nirvanaxp.types.entities.inventory.Inventory;
import com.nirvanaxp.types.entities.inventory.InventoryAttributeBOM;
import com.nirvanaxp.types.entities.inventory.InventoryItemBom;
import com.nirvanaxp.types.entities.inventory.InventoryItemDefault;
import com.nirvanaxp.types.entities.inventory.Inventory_;
import com.nirvanaxp.types.entities.inventory.UnitConversion;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;
import com.nirvanaxp.types.entities.orders.ItemToSupplier_;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.printers.PrintersInterface;
import com.nirvanaxp.types.entities.printers.PrintersType;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax_;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;

public class ItemsServiceBean
{
	// private NirvanaLogger logger = new
	// NirvanaLogger(ItemsServiceBean.class.getName());

	private NirvanaLogger logger = new NirvanaLogger(ItemsServiceBean.class.getName());

	private void addUpdateItem(EntityManager em, Item item) throws Exception
	{
		item.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		if (item.getId() == null)
		{
			em.persist(item);
		}
		else
		{
			em.merge(item);
		}

	}

	public Item delete(EntityManager em, Item item)
	{

		Item i = (Item) new CommonMethods().getObjectById("Item", em,Item.class, item.getId());
		i.setStatus("D");
		i.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(i);

		return i;

	}

	public Item getItemById(EntityManager em, String itemId)
	{

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
			Root<Item> r = criteria.from(Item.class);
			TypedQuery<Item> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Item_.id), itemId)));
			Item item = query.getSingleResult();

			if (item.getGlobalItemId() != null)
			{
				String queryString = "select l from Location l where l.id in   (select p.locationsId from Item p where p.globalItemId=? and p.status !='D') ";
				TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, item.getGlobalItemId());
				List<Location> resultSet = query2.getResultList();
				item.setLocationList(resultSet);
			}
			else
			{
				String queryString = "select l from Location l where l.id in   (select p.locationsId from Item p where p.globalItemId=? and p.status !='D') ";
				TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, item.getId());
				List<Location> resultSet = query2.getResultList();
				item.setLocationList(resultSet);
			}

			ItemRelationsHelper itemRelationsHelper = new ItemRelationsHelper();
			itemRelationsHelper.setShouldEliminateDStatus(true);

			item.setItemsToItemsAttributes(itemRelationsHelper.getItemsToItemsAttribute(item.getId(), em));
			item.setItemsToDiscounts(itemRelationsHelper.getItemToDiscounts(item.getId(), em));
			item.setItemsToItemsAttributesAttributeTypes(itemRelationsHelper.getItemsToItemsAttributeType(item.getId(), em));
			item.setItemsToItemsChars(itemRelationsHelper.getItemsToItemsChar(item.getId(), em));

			item.setItemsToPrinters(itemRelationsHelper.getItemToPrinter(item.getId(), em));
			item.setCategoryItems(itemRelationsHelper.getCategoryItem(item.getId(), em));
			item.setItemsToNutritions(itemRelationsHelper.getItemsToNutritions(item.getId(), em));
			item.setItemsToSchedule(itemRelationsHelper.getItemsToSchedule(item.getId(), em));
			ItemToSupplier itemToSupliers = itemRelationsHelper.getItemToSupplier(item.getId(), em);

			if (itemToSupliers != null)
			{
				item.setItemToSuppliers(itemToSupliers);
			}

			return item;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	public Item getItemByIdForCustomer(EntityManager em, String itemId)
	{

		Item item = getItemforCustomer(em, itemId);
		// customer does not need to use this details

		return item;

	}

	public List<SalesTax> getItemSpecificSalesTax(EntityManager em, String locationId) throws Exception
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
		Root<SalesTax> r = criteria.from(SalesTax.class);
		TypedQuery<SalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(SalesTax_.isItemSpecific), 1), builder.equal(r.get(SalesTax_.locationsId), locationId)));
		return query.getResultList();

	}

	public List<Item> getItemsByCourseId(EntityManager em, String courseId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
		Root<Item> r = criteria.from(Item.class);
		TypedQuery<Item> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Item_.courseId), courseId)));
		return query.getResultList();

	}

	public List<Item> getItemsByCategoryId(EntityManager em, String categoryId)
	{

		List<Item> itemsList = new ArrayList<Item>();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<CategoryItem> criteria = builder.createQuery(CategoryItem.class);
		Root<CategoryItem> r = criteria.from(CategoryItem.class);
		TypedQuery<CategoryItem> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryItem_.categoryId), categoryId), (builder.equal(r.get(CategoryItem_.status), "A"))));
		List<CategoryItem> categoryItemsList = query.getResultList();

		CriteriaQuery<ItemsType> criteriaItemType = builder.createQuery(ItemsType.class);
		Root<ItemsType> rootItemType = criteriaItemType.from(ItemsType.class);
		TypedQuery<ItemsType> queryItemType = em.createQuery(criteriaItemType.select(rootItemType).where(builder.equal(rootItemType.get(ItemsType_.name), "Sale Only")));
		ItemsType itemsType = queryItemType.getSingleResult();

		if (categoryItemsList != null && categoryItemsList.size() > 0)
		{
			for (CategoryItem categoryItem : categoryItemsList)
			{

				CriteriaQuery<Item> criteriaItem = builder.createQuery(Item.class);
				Root<Item> rootItem = criteriaItem.from(Item.class);
				TypedQuery<Item> queryItem = em.createQuery(
						criteriaItem.select(rootItem).where(builder.equal(rootItem.get(Item_.id), categoryItem.getItemsId()), builder.notEqual(rootItem.get(Item_.itemType), itemsType.getId())));
				Item item = queryItem.getSingleResult();
				Item globalItem = (Item) new CommonMethods().getObjectById("Item", em,Item.class, item.getGlobalItemId());
				item.setStockUom(globalItem.getStockUom());
				itemsList.add(item);
			}
		}
		return itemsList;

	}

	private Item getItemforCustomer(EntityManager em, String itemId)
	{

		Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemId);

		ItemRelationsHelper itemRelationsHelper = new ItemRelationsHelper();
		List<ItemsToItemsAttribute> itemsToitemsAttributesList = itemRelationsHelper.getItemsToItemsAttribute(itemId, em);
		if (itemsToitemsAttributesList != null && itemsToitemsAttributesList.size() > 0)
		{
			// add item attribute
			Set<ItemsAttribute> itemsAttributesSet = new HashSet<ItemsAttribute>();
			for (ItemsToItemsAttribute itemsToItemsAttribute : itemsToitemsAttributesList)
			{
				ItemsAttribute itemsAttribute = getItemAttributeDetails(em, itemsToItemsAttribute.getItemsAttributeId());
				if (itemsAttribute != null)
				{
					itemsAttributesSet.add(itemsAttribute);
				}

			}
			item.setItemsAttributesSet(itemsAttributesSet);
		}

		List<ItemsToItemsAttributeType> itemsToItemsAttributeTypeList = itemRelationsHelper.getItemsToItemsAttributeType(itemId, em);
		if (itemsToItemsAttributeTypeList != null && itemsToItemsAttributeTypeList.size() > 0)
		{
			Set<ItemsAttributeType> itemsAttributesTypeSet = new HashSet<ItemsAttributeType>();
			for (ItemsToItemsAttributeType itemsToItemsAttribute : itemsToItemsAttributeTypeList)
			{
				ItemsAttributeType itemsAttribute = getItemAttributeType(em, itemsToItemsAttribute.getItemsAttributeTypeId());
				if (itemsAttribute != null)
				{
					itemsAttributesTypeSet.add(itemsAttribute);
				}

			}
			item.setItemsAttributeTypesSet(itemsAttributesTypeSet);
		}

		List<ItemsToItemsChar> itemsToItemsCharList = itemRelationsHelper.getItemsToItemsChar(itemId, em);
		if (itemsToItemsCharList != null && itemsToItemsCharList.size() > 0)
		{
			Set<ItemsChar> itemsCharSet = new HashSet<ItemsChar>();
			for (ItemsToItemsChar itemsToItemsChar : itemsToItemsCharList)
			{
				ItemsChar itemsChar = getItemChars(em, itemsToItemsChar.getItemsCharId());
				if (itemsChar != null)
				{
					itemsCharSet.add(itemsChar);
				}

			}
			item.setItemsCharsSet(itemsCharSet);
		}
		/*
		 * List<ItemToSalesTax> itemToSalesTaxs =
		 * itemRelationsHelper.getItemToSalesTax(item.getId(), em);
		 * if(itemToSalesTaxs!=null){ item.setItemToSalesTaxs(itemToSalesTaxs);
		 * }
		 */

		return item;

	}

	private ItemsAttribute getItemAttributeDetails(EntityManager em, String itemAttributeId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ItemsAttribute> criteriaItem = builder.createQuery(ItemsAttribute.class);
		Root<ItemsAttribute> rootItem = criteriaItem.from(ItemsAttribute.class);
		TypedQuery<ItemsAttribute> queryItem = em.createQuery(criteriaItem.select(rootItem).where(builder.equal(rootItem.get(ItemsAttribute_.id), itemAttributeId)));
		ItemsAttribute itemsAttribute = queryItem.getSingleResult();
		return itemsAttribute;

	}

	private ItemsAttributeType getItemAttributeType(EntityManager em, String itemId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ItemsAttributeType> criteriaItem = builder.createQuery(ItemsAttributeType.class);
		Root<ItemsAttributeType> rootItem = criteriaItem.from(ItemsAttributeType.class);
		TypedQuery<ItemsAttributeType> queryItem = em.createQuery(criteriaItem.select(rootItem).where(builder.equal(rootItem.get(ItemsAttributeType_.id), itemId)));
		ItemsAttributeType itemsAttributeType = queryItem.getSingleResult();
		return itemsAttributeType;

	}

	private ItemsChar getItemChars(EntityManager em, String itemId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ItemsChar> criteriaItem = builder.createQuery(ItemsChar.class);
		Root<ItemsChar> rootItem = criteriaItem.from(ItemsChar.class);
		TypedQuery<ItemsChar> queryItem = em.createQuery(criteriaItem.select(rootItem).where(builder.equal(rootItem.get(ItemsChar_.id), itemId)));
		ItemsChar itemChar = queryItem.getSingleResult();
		return itemChar;

	}

	/**
	 * @param em
	 * @param locationId
	 * @param categoryId
	 * @param name
	 * @return
	 */
	public Item getAllItemsByLocationIdAndCategoryIdAndName(EntityManager em, String locationId, String categoryId, String name)
	{

		String queryString = "select i from Item i, CategoryItem c where i.id=c.itemsId and i.status!= 'D' and i.locationsId=?  AND c.categoryId=? and i.name=? order by i.displaySequence asc ";
		TypedQuery<Item> query = em.createQuery(queryString, Item.class).setParameter(1, locationId).setParameter(2, categoryId).setParameter(3, name);

		return query.getSingleResult();

	}

	/**
	 * @param em
	 * @param locationId
	 * @param categoryId
	 * @param displaySequence
	 * @return
	 * @throws Exception
	 */
	public Item getAllItemsByLocationIdAndCategoryIdAndDisplaySequence(EntityManager em, String locationId, String categoryId, int displaySequence) throws Exception
	{

		String queryString = "select i from Item i, CategoryItem c where i.id=c.itemsId and i.status!= 'D' and i.locationsId=? AND c.categoryId=? and i.displaySequence=?";
		TypedQuery<Item> query = em.createQuery(queryString, Item.class).setParameter(1, locationId).setParameter(2, categoryId).setParameter(3, displaySequence);

		return query.getSingleResult();

	}

	public Item getItemsByItemNumber(EntityManager em, String itemNumber, String locationId) throws Exception
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
		Root<Item> r = criteria.from(Item.class);
		TypedQuery<Item> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Item_.itemNumber), itemNumber), builder.equal(r.get(Item_.locationsId), locationId)));
		return query.getSingleResult();

	}

	public ItemsAttribute updateItemsAttributePrice(EntityManager em, ItemsAttribute itemAttribute) throws Exception
	{

		ItemsAttribute u = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,ItemsAttribute.class, itemAttribute.getId());
		u.setMsrPrice(itemAttribute.getMsrPrice());
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		return u;

	}

	public Item updateItemsById(EntityManager em, ItemPacket itemPacket)
	{

		// getting item from db
		Item item = itemPacket.getItem();
		Item existingItem = (Item) new CommonMethods().getObjectById("Item", em,Item.class, item.getId());
		existingItem.setName(item.getName());
		existingItem.setShortName(item.getShortName());
		existingItem.setCourseId(item.getCourseId());
		existingItem.setPriceSelling(item.getPriceSelling());
		existingItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(existingItem);

		ItemRelationsHelper itemRelationsHelper = new ItemRelationsHelper();
		// manage printers
		List<ItemsToPrinter> itemToPrintersListCurrent = itemRelationsHelper.getItemToPrinter(item.getId(), em);
		// naman
		 new EntityRelationshipManager().manageRelationsForBaseClass(em, existingItem,
		 itemToPrintersListCurrent, itemPacket.getPrinterList(),
		 ItemsToPrinter.class);

		return item;

	}

	public List<Item> updateItemsDisplaySequenceByItemsId(EntityManager em, List<Item> items) throws Exception
	{

		// for display sequence assignment
		List<Item> itemList = null;
		if (items != null && items.size() > 0)
		{
			itemList = new ArrayList<Item>();
		}

		for (Item item : items)
		{

			Item existingItem = (Item) new CommonMethods().getObjectById("Item", em,Item.class, item.getId());
			if (existingItem != null)
			{
				existingItem.setDisplaySequence(item.getDisplaySequence());
				existingItem.setUpdatedBy(item.getUpdatedBy());
				addUpdateItem(em, existingItem);
				itemList.add(existingItem);
			}

		}
		return itemList;

	}

	public List<ItemsType> getAllItemType(EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ItemsType> criteria = builder.createQuery(ItemsType.class);
		Root<ItemsType> r = criteria.from(ItemsType.class);
		TypedQuery<ItemsType> query = em.createQuery(criteria.select(r));
		return query.getResultList();

	}

	public Item updateItemForInventory(EntityManager em, ItemPacket itemPacket)
	{

		if (itemPacket != null && itemPacket.getItem() != null)
		{
			Item itemFromDatabase = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemPacket.getItem().getId());
			if (itemFromDatabase != null)
			{
				itemFromDatabase.setItemType(itemPacket.getItem().getItemType());
				itemFromDatabase.setSellableUom(itemPacket.getItem().getSellableUom());
				itemFromDatabase.setStockUom(itemPacket.getItem().getStockUom());

				itemFromDatabase = em.merge(itemFromDatabase);

				return itemFromDatabase;
			}
		}
		return itemPacket.getItem();

	}

	public ItemGroup addItemGroup(EntityManager em, ItemGroup itemGroup)
	{
		itemGroup.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.persist(itemGroup);

		return itemGroup;
	}

	public ItemGroup updateItemGroup(EntityManager em, ItemGroup itemGroup)
	{

		if (itemGroup.getGlobalId() == null)
		{
			ItemGroup local = (ItemGroup) new CommonMethods().getObjectById("ItemGroup", em,ItemGroup.class, itemGroup.getId());
			if (local != null)
			{
				itemGroup.setGlobalId(local.getGlobalId());
				itemGroup.setItemGroupId(local.getItemGroupId());
			}
		}

		itemGroup.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		itemGroup = em.merge(itemGroup);
		return itemGroup;

	}

	public ItemGroup deleteItemGroup(EntityManager em, ItemGroup ItemGroup)
	{
		ItemGroup c = (ItemGroup) new CommonMethods().getObjectById("ItemGroup", em,ItemGroup.class, ItemGroup.getId());
		c.setStatus("D");
		c.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		c = em.merge(c);

		return c;
	}

	public ItemGroup updateItemsGroup(ItemGroup rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception
	{

		if (rStatus.getGlobalId() == null)
		{
			ItemGroup local = (ItemGroup) new CommonMethods().getObjectById("ItemGroup", em,ItemGroup.class, rStatus.getId());
			if (local != null)
			{
				rStatus.setGlobalId(local.getGlobalId());
			}
		}
		rStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		rStatus = em.merge(rStatus);

		return rStatus;
	}

	public ItemGroup deleteItemsGroup(ItemGroup rStatus, HttpServletRequest httpRequest, EntityManager em) throws Exception
	{
		ItemGroup u = (ItemGroup) new CommonMethods().getObjectById("ItemGroup", em,ItemGroup.class, rStatus.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		u = em.merge(u);

		return u;
	}

	public List<ItemGroup> getItemGroupByLocationId(EntityManager em, String locationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ItemGroup> criteria = builder.createQuery(ItemGroup.class);
		Root<ItemGroup> r = criteria.from(ItemGroup.class);
		TypedQuery<ItemGroup> query = em.createQuery(
				criteria.select(r).where(builder.equal(r.get(ItemGroup_.locationsId), locationId), builder.notEqual(r.get(ItemGroup_.status), "D"), builder.notEqual(r.get(ItemGroup_.status), "I")));

		return query.getResultList();

	}

	public List<ItemGroup> getItemGroupByLocationIdAndDStatus(EntityManager em, String locationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ItemGroup> criteria = builder.createQuery(ItemGroup.class);
		Root<ItemGroup> r = criteria.from(ItemGroup.class);
		TypedQuery<ItemGroup> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemGroup_.locationsId), locationId), builder.notEqual(r.get(ItemGroup_.status), "D")));

		return query.getResultList();

	}

	public List<ItemGroup> getSubItemGroupByIdAndLocationId(EntityManager em, String locationId, String id)
	{
		/*
		 * CriteriaBuilder builder = em.getCriteriaBuilder();
		 * CriteriaQuery<ItemGroup> criteria =
		 * builder.createQuery(ItemGroup.class); Root<ItemGroup> r =
		 * criteria.from(ItemGroup.class); TypedQuery<ItemGroup> query =
		 * em.createQuery
		 * (criteria.select(r).where(builder.equal(r.get(ItemGroup_
		 * .locationsId), locationId),
		 * builder.notEqual(r.get(ItemGroup_.status), "D")));
		 */
		TypedQuery<ItemGroup> query = null;
		try
		{
			// String queryString = "select s from ItemGroup s where s.id =? ";
			// query = em.createQuery(queryString,
			// ItemGroup.class).setParameter(1, id);

			String queryString = "select s from ItemGroup s where s.itemGroupId =? and s.locationsId=? and s.status!='D' ";
			query = em.createQuery(queryString, ItemGroup.class).setParameter(1, id).setParameter(2, locationId);

		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return query.getResultList();

	}

	public Location getItemToSupplierbuId(EntityManager em, String taxId, String locationsId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Location> criteria = builder.createQuery(Location.class);
			Root<Location> suppliers = criteria.from(Location.class);
			TypedQuery<Location> query = em
					.createQuery(criteria.select(suppliers).where(builder.equal(suppliers.get(Location_.id), taxId), builder.equal(suppliers.get(Location_.locationsId), locationsId)));
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe("No Result found");
		}
		return null;
	}

	public ItemToSupplier getItemToSupplierbyItemId(EntityManager em, String itemId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemToSupplier> criteria = builder.createQuery(ItemToSupplier.class);
			Root<ItemToSupplier> suppliers = criteria.from(ItemToSupplier.class);
			TypedQuery<ItemToSupplier> query = em
					.createQuery(criteria.select(suppliers).where(builder.equal(suppliers.get(ItemToSupplier_.itemId), itemId), builder.notEqual(suppliers.get(ItemToSupplier_.status), "D")));
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe("No Result found");
		}
		return null;
	}

	public Item addUpdateItem(EntityManager em, Item item, ItemPacket itemPacket, int isRawMaterialUpdate, ItemPacket originalItemPacket, HttpServletRequest httpRequest,boolean isAdd)
	{

		if (item != null & em != null)
		{

			ItemRelationsHelper itemRelationsHelper = new ItemRelationsHelper();
			String itemId = item.getId();
//			boolean isAdd = false;
			if (itemId == null || (isAdd && itemPacket.getLocalServerURL()==1))
			{
				if(itemId==null){
				item.setId(new StoreForwardUtility().generateUUID());
				if(!(item.getGlobalItemId() == null || item.getGlobalItemId().equals("0") )) {
					try
					{
						String queryString = "select s from DiscountsType s where s.globalDiscountTypeId =? and s.locationsId=?  ";
						TypedQuery<DiscountsType> query = em.createQuery(queryString, DiscountsType.class).setParameter(1, item.getIncentiveId()).setParameter(2, item.getLocationsId());
						DiscountsType localDiscountsType = query.getSingleResult();
						if(localDiscountsType!= null)
						item.setIncentiveId(localDiscountsType.getId());
						
					}
					catch (Exception e)
					{
						logger.severe("No Result found for DiscountsType for globalDiscountTypeId " + item.getIncentiveId() + " locationsId " + item.getLocationsId());
					}
					
					 
				}else {
					if (item.getIncentiveId() != null && item.getIncentiveId().trim().length()>0  )
					{
						item.setIncentiveId(item.getIncentiveId());
					} 
				}
				}
				em.persist(item);
				isAdd = true;
				itemId = item.getId();
			

			}
			else
			{
				Item itemFromDatabase = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemId);
				// item.setItemType(itemFromDatabase.getItemType());
				if(itemFromDatabase==null){
					itemFromDatabase=item;
				}
				if (isRawMaterialUpdate == 0)
				{
					item.setSellableUom(itemFromDatabase.getSellableUom());
					item.setSellableUomName(itemFromDatabase.getSellableUomName());
					item.setStockUom(itemFromDatabase.getStockUom());
					item.setStockUomName(itemFromDatabase.getStockUomName());
					logger.severe("packetItem.getIncentive()+++++++++++++++++++++++++++++"+item.getIncentive());
					logger.severe("itemFromDatabase.getIncentive()+++++++++++++++++++++++"+itemFromDatabase.getIncentive());
					if (item.getIncentive() != null)
					{
						item.setIncentive(item.getIncentive());
					}else
					{
						item.setIncentive(itemFromDatabase.getIncentive());
					}
					if(!(item.getGlobalItemId() == null || item.getGlobalItemId().equals("0") )) {
						try
						{
							String queryString = "select s from DiscountsType s where s.globalDiscountTypeId =? and s.locationsId=?  ";
							TypedQuery<DiscountsType> query = em.createQuery(queryString, DiscountsType.class).setParameter(1, item.getIncentiveId()).setParameter(2, item.getLocationsId());
							DiscountsType localDiscountsType = query.getSingleResult();
							if(localDiscountsType!= null)
							item.setIncentiveId(localDiscountsType.getId());
							
						}
						catch (Exception e)
						{
							logger.severe("No Result found for DiscountsType for globalDiscountTypeId " + item.getIncentiveId() + " locationsId " + item.getLocationsId());
						}
						
						 
					}else {
						if (item.getIncentiveId() != null && item.getIncentiveId().trim().length()>0  )
						{
							item.setIncentiveId(item.getIncentiveId());
						}else
						{
							item.setIncentiveId(itemFromDatabase.getIncentiveId());
						}
					}
					
					logger.severe("item.setIncentiveId+++++++++++++++++++++++"+item.getIncentiveId());
				}
				else
				{
					Item packetItem = itemPacket.getItem();
					if (packetItem.getSellableUom() != null)
					{
						item.setSellableUom(itemPacket.getItem().getSellableUom());
					}
					else
					{
						item.setSellableUom(itemFromDatabase.getSellableUom());
					}
					if (packetItem.getSellableUomName() != null)
					{
						item.setSellableUomName(itemPacket.getItem().getSellableUomName());
					}
					else
					{
						item.setSellableUomName(itemFromDatabase.getSellableUomName());
					}
					if (packetItem.getStockUom() != null)
					{
						item.setStockUom(itemPacket.getItem().getStockUom());
					}
					else
					{
						item.setStockUom(itemFromDatabase.getStockUom());
					}
					if (packetItem.getStockUomName() != null)
					{
						item.setStockUomName(itemPacket.getItem().getStockUomName());
					}
					else
					{
						item.setStockUomName(itemFromDatabase.getStockUomName());
					}
					if (packetItem.getDistributionPrice() != null)
					{
						item.setDistributionPrice(itemPacket.getItem().getDistributionPrice());
					}
					else
					{
						item.setDistributionPrice(itemFromDatabase.getDistributionPrice());
					}
					if (packetItem.getIsOnlineDisplay() != 0)
					{
						item.setIsOnlineDisplay(itemPacket.getItem().getIsOnlineDisplay());
					}
					else
					{
						item.setIsOnlineDisplay(itemFromDatabase.getIsOnlineDisplay());
					}
					
					
				 
					 
				}

				// get all inventory itemBOM

				List<InventoryItemBom> list = getInventoryItemBom(em, item.getId(), logger);
				for (InventoryItemBom bom : list)
				{
					bom.setRmSellableUom(item.getSellableUom());
					bom.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					bom.setUpdatedBy(item.getUpdatedBy());
					bom = em.merge(bom);
				}

				// get all inventory itemBOM

				List<InventoryAttributeBOM> listAttribute = getInventoryAttributeBOM(em, item.getId(), logger);
				for (InventoryAttributeBOM bom : listAttribute)
				{
					bom.setRmSellableUom(item.getSellableUom());
					bom.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					bom.setUpdatedBy(item.getUpdatedBy());
					bom = em.merge(bom);
				}

				/*
				 * if (item.getStatus() == null) {
				 * item.setStatus(itemFromDatabase.getStatus()); } if
				 * (item.getPriceSelling() == null) {
				 * item.setPriceSelling(itemFromDatabase.getPriceSelling()); }
				 */

				item.setIsInStock(itemFromDatabase.getIsInStock());
				item.setIsBelowThreashold(itemFromDatabase.getIsBelowThreashold());
				item.setGlobalItemId(itemFromDatabase.getGlobalItemId());
				item.setDisplaySequence(itemFromDatabase.getDisplaySequence());

				item = em.merge(item);

			}

			EntityRelationshipManager manager = new EntityRelationshipManager();

			// manage printers
			List<ItemsToPrinter> itemToPrintersListCurrent = itemRelationsHelper.getItemToPrinter(itemId, em);
			if (itemPacket.getPrinterList() != null && (isAdd || originalItemPacket.getItem().getIsPrinterUpdate() == 1))
			{
				// naman
				manager.manageRelation(em, item, itemToPrintersListCurrent, itemPacket.getPrinterList(), ItemsToPrinter.class);
			}

			// manage discounts
			List<ItemsToDiscount> itemsToDiscountListCurrent = itemRelationsHelper.getItemToDiscounts(itemId, em);
			if (itemPacket.getDiscountsList() != null && (isAdd || originalItemPacket.getItem().getIsDiscountUpdate() == 1))
			{
				manager.manageRelations(em, item, itemsToDiscountListCurrent, itemPacket.getDiscountsList(), ItemsToDiscount.class);
			}

			// manage category
			List<CategoryItem> categoryItemListCurrent = itemRelationsHelper.getCategoryItem(itemId, em);
			if (itemPacket.getCategoryList() != null && (isAdd || originalItemPacket.getItem().getIsCategoryItemUpdate() == 1 ))
			{
				
				manager.manageRelationsForBaseClass(em, item, categoryItemListCurrent, itemPacket.getCategoryList(), CategoryItem.class);
				item.setIsCategoryItemUpdate(1);
			}
			// manage item attribute
			List<ItemsToItemsAttribute> itemsToItemsAttributeListCurrent = itemRelationsHelper.getItemsToItemsAttribute(itemId, em);
			if (itemPacket.getItemsAttributesList() != null && (isAdd || originalItemPacket.getItem().getIsAttributeListUpdate() == 1))
			{
				manager.manageRelations(em, item, itemsToItemsAttributeListCurrent, itemPacket.getItemsAttributesList(), ItemsToItemsAttribute.class);
			}

			// manage item attribute typehh
			List<ItemsToItemsAttributeType> itemsToItemsAttributeTypeListCurrent = itemRelationsHelper.getItemsToItemsAttributeType(itemId, em);
			if (itemPacket.getItemsAttributeTypesList() != null && (isAdd || originalItemPacket.getItem().getIsAttributeTypeListUpdate() == 1))
			{
				manager.manageRelations(em, item, itemsToItemsAttributeTypeListCurrent, itemPacket.getItemsAttributeTypesList(), ItemsToItemsAttributeType.class);
			}
			// manage item char
			List<ItemsToItemsChar> itemsToItemsCharListCurrent = itemRelationsHelper.getItemsToItemsChar(itemId, em);
			if (itemPacket.getItemCharsList() != null && (isAdd || originalItemPacket.getItem().getIsItemToCharUpdate() == 1))
			{
				manager.manageRelation(em, item, itemsToItemsCharListCurrent, itemPacket.getItemCharsList(), ItemsToItemsChar.class);
			}
			em.getTransaction().commit();
			em.getTransaction().begin();
			List<ItemsToNutritions> itemsToItemsToNutritionsCurrent = itemRelationsHelper.getItemsToNutritions(itemId, em);
			if (itemPacket.getNutritionList() != null && (isAdd || originalItemPacket.getItem().getIsItemsToNutritionUpdate() == 1))
			{
				// naman
				manager.manageRelationsForBaseClass(em, item, itemsToItemsToNutritionsCurrent, itemPacket.getNutritionList(), ItemsToNutritions.class);
			}

			List<ItemsToSchedule> itemsToScheduleCurrent = itemRelationsHelper.getItemsToSchedule(itemId, em);
			if (itemPacket.getItemsToSchedule() != null && (isAdd || originalItemPacket.getItem().getIsItemsToScheduleUpdate() == 1))
			{
				manager.manageRelationsForBaseClass(em, item, itemsToScheduleCurrent, itemPacket.getItemsToSchedule(), ItemsToSchedule.class);

			}

			if (itemPacket.getNutritionList() != null)
			{
				for (Nutritions nutritions : itemPacket.getNutritionList())
				{
					ItemsToNutritions itemsToNutrition = itemRelationsHelper.getItemsToNutritionsByNutritionIdItemId(itemId, nutritions.getId(), em);
					if (itemsToNutrition != null)
					{
						itemsToNutrition.setNutritionsValue(nutritions.getNutritionsValue());
					}

				}
			}

			ItemToSupplier itemToSupplier = getItemToSupplierbyItemId(em, item.getId());
			if (itemToSupplier != null)
			{
				int id = itemToSupplier.getId();
				if (itemPacket.getItemToSupplier() != null)
				{
					itemToSupplier = new ItemToSupplier().getItemToSupplier(itemPacket.getItemToSupplier());
					itemToSupplier.setId(id);
				}

			}
			else
			{
				if (itemPacket.getItemToSupplier() != null)
				{
					itemToSupplier = new ItemToSupplier().getItemToSupplier(itemPacket.getItemToSupplier());
					itemToSupplier.setItemId(item.getId());
				}
			}

			// setting supplier
			if (itemToSupplier != null)
			{

				itemToSupplier.setItemId(item.getId());
				itemPacket.setItemToSupplier(em.merge(itemToSupplier));
			}
			manageRelationshipItemsAttributeTypeToItemsAttributeList(em, itemPacket, item);
			manageRelationshipItemsCharToItemsAttributeList(em, itemPacket, item);
			addItemToInventory(em, item, itemPacket.getItemToSupplier(), httpRequest);
		}
		return item;
	}

	public Item addUpdateItemLater(HttpServletRequest httpRequest, EntityManager em, ItemPacket itemPacket, String sessionId)
	{
		Item item = itemPacket.getItem();
		String operationName = "";
		boolean isUpdateFromLocal = false;
		if (item.getId() == null)
		{
			operationName = POSNServiceOperations.ItemsService_add.name();
		}
		else
		{
			operationName = POSNServiceOperations.ItemsService_update.name();
		}
		String[] locationsIds = null;
		if (itemPacket != null && itemPacket.getLocationId() != null && itemPacket.getLocationId().length() > 0)
		{

			locationsIds = itemPacket.getLocationsListId().split(",");
		}
		else
		{
			locationsIds = new String[]
			{ itemPacket.getItem().getLocationsId() + "" };
			isUpdateFromLocal = true;
		}
		if (locationsIds != null && locationsIds.length > 0)
		{
			for (String locationId : locationsIds)
			{
				if (!isUpdateFromLocal)
				{
					itemPacket.setLocationId(locationId);
				}
				else
				{
					itemPacket.setLocationId("");
				}
				String itemStrPacket = new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(itemPacket);

				FutureUpdate futureUpdate = new FutureUpdate();
				futureUpdate.setOperationName(operationName);
				futureUpdate.setPacketString(itemStrPacket);
				futureUpdate.setStatus("A");
				futureUpdate.setDate(itemPacket.getDate());
				futureUpdate.setSchemaName(itemPacket.getSchemaName());
				futureUpdate.setServiceName(operationName);
				futureUpdate.setSessionId(sessionId);
				futureUpdate.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				futureUpdate.setUpdatedBy(item.getUpdatedBy());
				futureUpdate.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				futureUpdate.setCreatedBy(item.getCreatedBy());
				futureUpdate.setLocationId(locationId);
				if (itemPacket != null && itemPacket.getItem() != null && itemPacket.getItem().getId() !=null)
				{
					if (itemPacket.getItem().getGlobalItemId() == null)
					{
						futureUpdate.setGlobalItemId(itemPacket.getItem().getId());
					}
					else
					{
						futureUpdate.setGlobalItemId(itemPacket.getItem().getGlobalItemId());
					}

					// by Ap 26-03
					// if we want to update existing packet then we need to find
					// the object and overrite it
					try
					{
						FutureUpdate update = getAddLaterStatusByItemIdAndLocation(em, futureUpdate.getGlobalItemId(), locationId);
						if (update != null)
						{
							futureUpdate.setId(update.getId());
						}
					}
					catch (NumberFormatException e)
					{
						logger.severe(e);
					}
					catch (Exception e)
					{
						logger.severe(e);
					}
				}

				futureUpdate = em.merge(futureUpdate);
			}
		}

		return itemPacket.getItem();

	}

	private void manageRelationshipItemsAttributeTypeToItemsAttributeList(EntityManager em, ItemPacket itemPacket, Item item)
	{
		if (itemPacket.getItemsAttributeTypeToItemsAttributeList() != null && itemPacket.getItemsAttributeTypeToItemsAttributeList().size() > 0)
		{
			for (ItemsAttributeTypeToItemsAttribute attribute : itemPacket.getItemsAttributeTypeToItemsAttributeList())
			{
				ItemsAttributeTypeToItemsAttribute attributeItemsAttributeTypeToItemsAttribute = null;
				try
				{

					if (attribute.getItemsAttributeId() !=null && attribute.getItemsAttributeTypeId() !=null)
					{
						String queryString = "select s from ItemsAttributeTypeToItemsAttribute s where s.itemsAttributeId =? and s.itemsAttributeTypeId=?";
						TypedQuery<ItemsAttributeTypeToItemsAttribute> queryIATTIA = em.createQuery(queryString, ItemsAttributeTypeToItemsAttribute.class)
								.setParameter(1, attribute.getItemsAttributeId()).setParameter(2, attribute.getItemsAttributeTypeId());
						attributeItemsAttributeTypeToItemsAttribute = queryIATTIA.getSingleResult();
					}

				}
				catch (Exception e)
				{
					logger.severe(
							"ItemsAttributeTypeToItemsAttribute not found for itemsAttributeId " + attribute.getItemsAttributeId() + " itemsAttributeTypeId " + attribute.getItemsAttributeTypeId());
				}

				if (attributeItemsAttributeTypeToItemsAttribute == null)
				{
					if (attribute.getId() > 0)
					{
						attribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						attribute.setUpdatedBy(item.getUpdatedBy());

					}
					else
					{
						attribute.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						attribute.setCreatedBy(item.getCreatedBy());
						attribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						attribute.setUpdatedBy(item.getUpdatedBy());
					}
					attribute = em.merge(attribute);
				}

			}
		}
	}

	private void manageRelationshipItemsCharToItemsAttributeList(EntityManager em, ItemPacket itemPacket, Item item)
	{
		if (itemPacket.getItemsCharToItemsAttribute() != null && itemPacket.getItemsCharToItemsAttribute().size() > 0)
		{
			for (ItemsCharToItemsAttribute itemsCharToItemsAttribute : itemPacket.getItemsCharToItemsAttribute())
			{
				ItemsCharToItemsAttribute itemsCharToItemsAttributeTemp = null;
				try
				{

					if (itemsCharToItemsAttribute.getItemsCharId() !=null && itemsCharToItemsAttribute.getItemsAttributeId() !=null)
					{
						String queryString = "select s from ItemsCharToItemsAttribute s where s.itemsCharId =? and s.itemsAttributeId=?";
						TypedQuery<ItemsCharToItemsAttribute> queryIATTIA = em.createQuery(queryString, ItemsCharToItemsAttribute.class).setParameter(1, itemsCharToItemsAttribute.getItemsCharId())
								.setParameter(2, itemsCharToItemsAttribute.getItemsAttributeId());
						itemsCharToItemsAttributeTemp = queryIATTIA.getSingleResult();
					}

				}
				catch (Exception e)
				{
					logger.severe("ItemsCharToItemsAttribute not found for itemsCharId " + itemsCharToItemsAttribute.getItemsCharId() + " itemsAttributeId "
							+ itemsCharToItemsAttribute.getItemsAttributeId());
				}

				if (itemsCharToItemsAttributeTemp == null)
				{
					if (itemsCharToItemsAttribute.getId() > 0)
					{
						itemsCharToItemsAttribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						itemsCharToItemsAttribute.setUpdatedBy(item.getUpdatedBy());

					}
					else
					{
						itemsCharToItemsAttribute.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						itemsCharToItemsAttribute.setCreatedBy(item.getCreatedBy());
						itemsCharToItemsAttribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						itemsCharToItemsAttribute.setUpdatedBy(item.getUpdatedBy());
					}
					itemsCharToItemsAttribute = em.merge(itemsCharToItemsAttribute);
				}

			}
		}
	}

	private void addItemToInventory(EntityManager em, Item item, ItemToSupplier itemToSupplier, HttpServletRequest httpRequest)
	{
		// add update item in inventory table if item type is inventory and sale
		// and inventory.
		try
		{

			ItemsType itemsType = em.find(ItemsType.class, item.getItemType());
			if (itemsType.getName().equals("Inventory Only") || itemsType.getName().equals("Sale And Inventory") || itemsType.getName().equals("Semi Finished Goods"))
			{
				String queryString = "select ig from Inventory ig where ig.itemId =?";
				TypedQuery<Inventory> query = em.createQuery(queryString, Inventory.class).setParameter(1, item.getId());

				Inventory inventory = null;

				try
				{
					inventory = query.getSingleResult();
				}
				catch (Exception e)
				{
					logger.severe("Inventory No Record Found For Item Id" + item.getId() + "Item Name " + item.getName());
				}

				if (inventory == null)
				{
					Inventory inventory2 = new Inventory();
					inventory2.setIsBelowThreashold(item.getIsBelowThreashold());
					inventory2.setLocationId(item.getLocationsId());
					inventory2.setUnitOfMeasurementId(item.getStockUom());
					inventory2.setUomName(item.getStockUomName());

					if (itemToSupplier != null)
					{
						inventory2.setTertiarySupplierId(itemToSupplier.getTertiarySupplierId());
						inventory2.setSecondarySupplierId(itemToSupplier.getSecondarySupplierId());
						inventory2.setPrimarySupplierId(itemToSupplier.getPrimarySupplierId());

					}

					inventory2.setItemId(item.getId());
					inventory2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventory2.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(item.getLocationsId(), em));
					inventory2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventory2.setUpdatedBy(item.getUpdatedBy());
					inventory2.setCreatedBy(item.getCreatedBy());
					inventory2.setStatus("A");
					inventory2.setPurchasingRate(item.getPurchasingRate());
					inventory2.setTotalAvailableQuanity(new BigDecimal(0));
					inventory2.setTotalUsedQuanity(new BigDecimal(0));
					inventory2.setYieldQuantity(new BigDecimal(0));
					inventory2.setTotalReceiveQuantity(new BigDecimal(0));
					if (inventory2.getId() == null)
					{
						Location location = new CommonMethods().getBaseLocation(em);
						inventory2.setId(new StoreForwardUtility().generateDynamicIntId(em, location.getId(), httpRequest, "inventory"));
					}
					em.merge(inventory2);

					logger.severe("Inventory Added Successfull For Item Id " + item.getId() + "Item Name " + item.getName());

				}
				else
				{
					inventory.setIsBelowThreashold(item.getIsBelowThreashold());
					inventory.setLocationId(item.getLocationsId());
					inventory.setUnitOfMeasurementId(item.getStockUom());
					inventory.setUomName(item.getStockUomName());
					if (itemToSupplier != null)
					{
						inventory.setTertiarySupplierId(itemToSupplier.getTertiarySupplierId());
						inventory.setSecondarySupplierId(itemToSupplier.getSecondarySupplierId());
						inventory.setPrimarySupplierId(itemToSupplier.getPrimarySupplierId());

					}

					inventory.setItemId(item.getId());
					inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					inventory.setUpdatedBy(item.getUpdatedBy());
					inventory.setCreatedBy(item.getCreatedBy());
					inventory.setStatus("A");
					inventory.setPurchasingRate(item.getPurchasingRate());

					inventory = em.merge(inventory);

					logger.severe("Inventory Updated Successfull For Item Id " + item.getId() + "Item Name " + item.getName());
				}

				String queryStringDefault = "select ig from InventoryItemDefault ig where ig.itemId =?";
				TypedQuery<InventoryItemDefault> queryDefault = em.createQuery(queryStringDefault, InventoryItemDefault.class).setParameter(1, item.getId());

				InventoryItemDefault inventoryItemDefault = null;

				try
				{
					inventoryItemDefault = queryDefault.getSingleResult();
				}
				catch (Exception e)
				{
					// TODO: handle exception
					inventoryItemDefault = null;
					logger.severe(e);

					logger.severe("InventoryItemDefault No Record Found For Item Id" + item.getId() + "Item Name " + item.getName());
				}

				if (inventoryItemDefault == null)
				{
					InventoryItemDefault inventoryItemDefault1 = new InventoryItemDefault();
					inventoryItemDefault1.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault1.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault1.setUpdatedBy(item.getUpdatedBy());
					inventoryItemDefault1.setCreatedBy(item.getCreatedBy());
					inventoryItemDefault1.setStatus("A");
					inventoryItemDefault1.setItemId(item.getId());
					inventoryItemDefault1.setMinimumOrderQuantity(new BigDecimal(0));
					inventoryItemDefault1.setEconomicOrderQuantity(new BigDecimal(0));
					inventoryItemDefault1.setD86Threshold(new BigDecimal(0));
					em.persist(inventoryItemDefault1);
					logger.severe("InventoryItemDefault Added Successfull For Item Id " + item.getId() + "Item Name " + item.getName());
				}
				else
				{

					inventoryItemDefault.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault.setUpdatedBy(item.getUpdatedBy());
					inventoryItemDefault.setCreatedBy(item.getCreatedBy());
					inventoryItemDefault.setStatus("A");
					inventoryItemDefault.setItemId(item.getId());
					inventoryItemDefault = em.merge(inventoryItemDefault);
					logger.severe("InventoryItemDefault Updated Successfull For Item Id " + item.getId() + "Item Name " + item.getName());
				}

			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			logger.severe(e);

			logger.severe(e.getMessage() + "Unalbe To Add Inventory Item For Item Id " + item.getId() + "Item Name " + item.getName());
		}

	}

	public void addMultipleLocationsItems(HttpServletRequest httpRequest, EntityManager em, Item item, ItemPacket itemPacket) throws Exception
	{
		// getting location if from admin client it will not include
		// baselocation
		int isRawMaterialUpdate = itemPacket.getIsRawMaterialUpdate();
		String[] locationIds = itemPacket.getLocationsListId().split(",");

		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (item != null && itemPacket.getIsBaseLocationUpdate() == 1)
		{
			// adding or updating global item
//			item.setLocationsId(baseLocation.getId());
			Item globalItem = addUpdateItem(em, item, itemPacket, isRawMaterialUpdate, itemPacket, httpRequest,true);
			itemPacket.setItem(globalItem);
			
			if(itemPacket.getLocalServerURL()==0 && (itemPacket.getLocationsListId()==null || itemPacket.getLocationsListId().length()==0)){
				String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket", httpRequest);
				new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, baseLocation.getId(),
						Integer.parseInt(itemPacket.getMerchantId()));
		
			}
			
			itemPacket.setLocationsListId("");
			

			// now add/update child location
			for (String locationId : locationIds)
			{
				
				if (locationId.length()>0 && !locationId.equals((baseLocation.getId()))) {
					String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket", httpRequest);
					new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, baseLocation.getId(),
							Integer.parseInt(itemPacket.getMerchantId()));
				item =createLocalItem(em, isRawMaterialUpdate, locationId, baseLocation, item, itemPacket, httpRequest, globalItem);
				itemPacket.setItem(item);
				itemPacket.setLocalServerURL(0);
				
				String json2 = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket", httpRequest);
				new StoreForwardUtility().callSynchPacketsWithServer(json2, httpRequest, locationId,
						Integer.parseInt(itemPacket.getMerchantId()));
			}
			}
		}
	}

	private Item createLocalItem(EntityManager em, int isRawMaterialUpdate, String locationId, Location baseLocation, Item item, ItemPacket itemPacket, HttpServletRequest httpRequest, Item globalItem)
			throws Exception
	{
//		ItemPacket packet = new ItemPacket();
		String locationsId = locationId;

		if (!locationsId.equals(baseLocation.getId()))
		{
			ItemPacket itemLocalPacket = new ItemPacket().getItemPacket(itemPacket);
			Item localItem = new Item().copy(item);
			localItem.setLocationsId(locationsId);
			localItem.setGlobalItemId(globalItem.getId());

			List<Printer> itemsToPrinters = getLocalItemToPrinters(em, locationsId, itemLocalPacket.getPrinterList());
			itemPacket.setPrinterList(itemsToPrinters);

			List<Category> categoryItems = getLocalCategoryItem(em, locationsId, itemLocalPacket.getCategoryList());
			itemPacket.setCategoryList(categoryItems);

			List<Discount> discountsItem = getLocalItemToDiscount(em, locationsId, itemLocalPacket.getDiscountsList());
			itemPacket.setDiscountsList(discountsItem);

			List<ItemsChar> itemsChars = getLocalItemTItemsChar(em, locationsId, itemLocalPacket.getItemCharsList());
			itemPacket.setItemCharsList(itemsChars);
			if (itemLocalPacket.getNutritionList() != null && itemLocalPacket.getNutritionList().size() > 0)
			{

				List<Nutritions> nutritions = getLocalItemToNutritions(em, locationsId, itemLocalPacket.getNutritionList());
				itemPacket.setNutritionList(nutritions);
			}

			List<ItemsAttribute> itemsAttributes = getLocalItemToItemsAttribute(em, locationsId, itemLocalPacket.getItemsAttributesList());
			itemPacket.setItemsAttributesList(itemsAttributes);

			String localCourseId = getLocalCourseFromGlobalId(em, localItem.getCourseId(), locationsId);
			if (localCourseId !=null)
			{
				localItem.setCourseId(localCourseId);
			}

			UnitOfMeasurement localUOMId;
			
			 
			if (localItem.getStockUom()!=null)
			{
			    UnitOfMeasurement  uom=getUnitOfMeasurementById(em, localItem.getStockUom());
			    if(uom.getGlobalId()==null || uom.getGlobalId().equals('0')){
					  localUOMId= getUnitOfMeasurementByGlobalUOMIdAndLocationId(em, localItem.getStockUom(), locationsId);;
				  }else {
					  localUOMId= getUnitOfMeasurementByGlobalUOMIdAndLocationId(em, uom.getGlobalId(), locationsId);;
						
				}
				if (localUOMId != null && localUOMId.getId() !=null)
				{
					localItem.setStockUom(localUOMId.getId());
				}
			}

			if (localItem.getSellableUom() !=null)
			{
				localUOMId = getUnitOfMeasurementByGlobalUOMIdAndLocationId(em, localItem.getSellableUom(), locationsId);
				
				 UnitOfMeasurement  uom=getUnitOfMeasurementById(em, localItem.getSellableUom());
				    if(uom.getGlobalId()==null || uom.getGlobalId().equals('0')){
						  localUOMId= getUnitOfMeasurementByGlobalUOMIdAndLocationId(em, localItem.getSellableUom(), locationsId);;
					  }else {
						  localUOMId= getUnitOfMeasurementByGlobalUOMIdAndLocationId(em, uom.getGlobalId(), locationsId);;
							
					}
				
				if (localUOMId != null && localUOMId.getId() !=null)
				{
					localItem.setSellableUom(localUOMId.getId());
				}
			}

			// worked
			List<ItemsAttributeType> itemsAttributesTypes = getLocalItemToItemsAttributeType(em, locationsId, itemLocalPacket.getItemsAttributeTypesList());
			itemPacket.setItemsAttributeTypesList(itemsAttributesTypes);

			// ItemsAttributeTypeToItemsAttribute
			List<ItemsAttributeTypeToItemsAttribute> itemsAttributeTypeToItemsAttribute = getLocalItemsAttributeTypeToItemsAttribute(httpRequest, em,
					itemLocalPacket.getItemsAttributeTypeToItemsAttributeList(), locationsId);
			itemPacket.setItemsAttributeTypeToItemsAttributeList(itemsAttributeTypeToItemsAttribute);

			// ItemsCharToItemsAttribute
			List<ItemsCharToItemsAttribute> itemsCharToItemsAttribute = getLocalItemsCharToItemsAttribute(em, itemLocalPacket.getItemsCharToItemsAttribute(), locationsId);
			itemPacket.setItemsCharToItemsAttribute(itemsCharToItemsAttribute);

			if (localItem.getSalesTax1() != null)
			{
				SalesTax c = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, localItem.getSalesTax1());
				String localTax = getLocalSalesTaxFromGlobalId(em, c.getTaxName(), locationsId, baseLocation.getId());
				localItem.setSalesTax1(localTax);
			}
			if (localItem.getSalesTax2() != null)
			{
				SalesTax c = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, localItem.getSalesTax2());
				String localTax = getLocalSalesTaxFromGlobalId(em, c.getTaxName(), locationsId, baseLocation.getId());
				localItem.setSalesTax2(localTax);
			}
			if (localItem.getSalesTax3() != null)
			{
				SalesTax c = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, localItem.getSalesTax3());
				String localTax = getLocalSalesTaxFromGlobalId(em, c.getTaxName(), locationsId, baseLocation.getId());
				localItem.setSalesTax3(localTax);
			}
			if (localItem.getSalesTax4() != null)
			{
				SalesTax c = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, localItem.getSalesTax4());
				String localTax = getLocalSalesTaxFromGlobalId(em, c.getTaxName(), locationsId, baseLocation.getId());
				localItem.setSalesTax4(localTax);
			}

			itemPacket.setItem(localItem);
			if (itemPacket.getItemToSupplier() != null)
			{
				ItemToSupplier itemToSupplier = new ItemToSupplier().getItemToSupplier(itemPacket.getItemToSupplier());
				itemToSupplier.setItemId(localItem.getId());
				itemPacket.setItemToSupplier(itemToSupplier);
			}

			if (globalItem.getItemGroupId() != null)
			{
				String queryString = "select ig from ItemGroup ig where ig.globalId =? and ig.locationsId=? and ig.status!='D' ";
				TypedQuery<ItemGroup> query = em.createQuery(queryString, ItemGroup.class).setParameter(1, globalItem.getItemGroupId()).setParameter(2, locationId);
				ItemGroup group = null;
				try
				{
					group = query.getSingleResult();
				}
				catch (Exception e)
				{

					logger.info("Item group not present");
				}

				if (group != null)
				{
					localItem.setItemGroupId(group.getId());
				}
				else
				{
					ItemGroup globalItemGroup = (ItemGroup) new CommonMethods().getObjectById("ItemGroup", em,ItemGroup.class, globalItem.getItemGroupId());
					if (globalItemGroup != null)
					{

						ItemGroup localItemGroup = new ItemGroup().getItemGroup(globalItemGroup);
						localItemGroup.setLocationsId(locationsId);
						localItemGroup.setGlobalId(globalItem.getItemGroupId());
						localItemGroup.setId(new StoreForwardUtility().generateUUID());
						localItemGroup = em.merge(localItemGroup);
						localItem.setItemGroupId(localItemGroup.getId());
					}
					else
					{
						localItem.setItemGroupId(null);
					}

				}

			}

			return addUpdateItem(em, localItem, itemPacket, isRawMaterialUpdate, itemPacket, httpRequest,true);

		}
		return null;
	}

	public void updateMultipleLocationsItems(HttpServletRequest httpRequest, EntityManager em, Item item, ItemPacket itemPacket) throws Exception
	{
		// getting location if from admin client it will not include
		// baselocation
		int isRawMaterialUpdate = itemPacket.getIsRawMaterialUpdate();

		String[] locationIds = null;
		if (itemPacket.getLocationId().trim().length() > 0)
		{
			locationIds = itemPacket.getLocationsListId().split(",");
		}

		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);

		Item globalItemFromDb = getItemById(em, item.getId());
		if(globalItemFromDb==null){
			globalItemFromDb = item;
		}
		List<CategoryItem> globalCategoryItem = globalItemFromDb.getCategoryItems();
		// finding globalCategoryId
		String categoryId = null;
		if (globalCategoryItem != null && globalCategoryItem.size() !=0)
		{
			// there should be only one category for one item
			categoryId = globalCategoryItem.get(0).getCategoryId();
		}
		ItemPacket packet = new ItemPacket();
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (item != null && itemPacket.getIsBaseLocationUpdate() == 1)
		{

			Item originalItems = new Item().copy(itemPacket.getItem());
			ItemPacket originalItemPacket = new ItemPacket().getItemPacket(itemPacket);

			setDBValuesToLatestObject(globalItemFromDb, item, originalItems, originalItemPacket, true);
			// adding or updating global item
			Item globalItem = addUpdateItem(em, item, itemPacket, isRawMaterialUpdate, originalItemPacket, httpRequest,false);
			itemPacket.setItem(globalItem);
			if(itemPacket.getLocalServerURL()==0 && (itemPacket.getLocationsListId()==null || itemPacket.getLocationsListId().length()==0)){
				String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket", httpRequest);
				
				new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, baseLocation.getId(),
						Integer.parseInt(itemPacket.getMerchantId()));
			}
			itemPacket.setLocationsListId("");
			
			// now add/update child location
			if (locationIds != null)
			{
				for (String locationId : locationIds)
				{
					
					boolean isAdd = false;
					String locationsId = locationId;
					if (locationId.length()>0 && !locationId.equals((baseLocation.getId())))
					{
						String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket", httpRequest);
						new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, baseLocation.getId(),
								Integer.parseInt(itemPacket.getMerchantId()));
						ItemPacket itemLocalPacket = new ItemPacket().getItemPacket(itemPacket);
						Item localItem = new Item().copy(item);
						Item items = getItemByGlobalItemIdAndLocationId(em, locationsId, globalItem.getId(), categoryId);

						if (items != null && items.getId() !=null)
						{
							localItem.setGlobalItemId(items.getGlobalItemId());
							localItem.setDisplaySequence(items.getDisplaySequence());
							localItem.setId(items.getId());
							List<Category> categories = new ArrayList<Category>();

							boolean needToAdd=false;
							for (CategoryItem ci : globalItem.getCategoryItems())
							{
								
								Category c = (Category) new CommonMethods().getObjectById("Category", em,Category.class, ci.getCategoryId());
								categories.add(c);
								needToAdd=true;
								
							}
							if(needToAdd){
							itemLocalPacket.setCategoryList(categories);
							}
							setDBValuesToLatestObject(items, localItem, originalItems, originalItemPacket, false);
							// If old course id not assigned to localItem then
							// course will get new entry
							// localItem.setCourseId(items.getCourseId());
						}
						else
						{
							isAdd = true;
							localItem.setGlobalItemId(globalItem.getId());
							List<Category> categories = new ArrayList<Category>();
							for (CategoryItem ci : globalItem.getCategoryItems())
							{
								Category c = (Category) new CommonMethods().getObjectById("Category", em,Category.class, ci.getCategoryId());
								categories.add(c);
							}
							itemLocalPacket.setCategoryList(categories);
							List<ItemsAttribute> itemsAttributesList = new ArrayList<ItemsAttribute>();
							for (ItemsToItemsAttribute itemsToItemsAttribute : globalItem.getItemsToItemsAttributes())
							{
								ItemsAttribute ia = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,ItemsAttribute.class, itemsToItemsAttribute.getItemsAttributeId());
								itemsAttributesList.add(ia);
							}
							itemLocalPacket.setItemsAttributesList(itemsAttributesList);

							List<ItemsAttributeType> itemsAttributesTypeList = new ArrayList<ItemsAttributeType>();
							for (ItemsToItemsAttributeType itemsToItemsAttribute : globalItem.getItemsToItemsAttributesAttributeTypes())
							{
								ItemsAttributeType ia = (ItemsAttributeType) new CommonMethods().getObjectById("ItemsAttributeType", em,ItemsAttributeType.class,  itemsToItemsAttribute.getItemsAttributeTypeId());
								itemsAttributesTypeList.add(ia);
							}
							itemLocalPacket.setItemsAttributeTypesList(itemsAttributesTypeList);

							List<ItemsChar> itemsChars = new ArrayList<ItemsChar>();
							for (ItemsToItemsChar itemsToItemsChar : globalItem.getItemsToItemsChars())
							{
								ItemsChar ia = (ItemsChar) new CommonMethods().getObjectById("ItemsChar", em,ItemsChar.class,  itemsToItemsChar.getItemsCharId());
								itemsChars.add(ia);
							}
							itemLocalPacket.setItemCharsList(itemsChars);
							List<ItemsAttributeTypeToItemsAttribute> itemsAttributeTypeToItemsAttribute = new ArrayList<ItemsAttributeTypeToItemsAttribute>();
							for (ItemsAttributeType itemsAttributeType : itemsAttributesTypeList)
							{
								String queryString = "select s from ItemsAttributeTypeToItemsAttribute s where s.itemsAttributeTypeId=?";
								TypedQuery<ItemsAttributeTypeToItemsAttribute> queryIATTIA = em.createQuery(queryString, ItemsAttributeTypeToItemsAttribute.class).setParameter(1,
										itemsAttributeType.getId());
								itemsAttributeTypeToItemsAttribute.addAll(queryIATTIA.getResultList());
							}

							List<Nutritions> nutritions = new ArrayList<Nutritions>();
							for (ItemsToNutritions itemsToNutritions : globalItem.getItemsToNutritions())
							{
								Nutritions ia = (Nutritions) new CommonMethods().getObjectById("Nutritions", em,Nutritions.class, itemsToNutritions.getNutritionsId());
								nutritions.add(ia);
							}
							itemLocalPacket.setNutritionList(nutritions);

							itemLocalPacket.setItemsAttributeTypeToItemsAttributeList(itemsAttributeTypeToItemsAttribute);

						}
						localItem.setLocationsId(locationsId);
						localItem.setCreatedBy(item.getCreatedBy());
						localItem.setUpdatedBy(item.getUpdatedBy());

						List<Printer> itemsToPrinters = getLocalItemToPrinters(em, locationsId, itemLocalPacket.getPrinterList());
						packet.setPrinterList(itemsToPrinters);

						List<Category> categoryItems = getLocalCategoryItem(em, locationsId, itemLocalPacket.getCategoryList());
						packet.setCategoryList(categoryItems);

						List<Discount> discountsItem = getLocalItemToDiscount(em, locationsId, itemLocalPacket.getDiscountsList());
						packet.setDiscountsList(discountsItem);
						
						
						 
						if (itemLocalPacket.getItemCharsList() != null)
						{
							List<ItemsChar> itemsChars = getLocalItemTItemsChar(em, locationsId, itemLocalPacket.getItemCharsList());
							packet.setItemCharsList(itemsChars);
						}

						if (itemLocalPacket.getItemsAttributesList() != null)
						{
							List<ItemsAttribute> itemsAttributes = getLocalItemToItemsAttribute(em, locationsId, itemLocalPacket.getItemsAttributesList());
							packet.setItemsAttributesList(itemsAttributes);
						}

						String localCourseId = getLocalCourseFromGlobalId(em, localItem.getCourseId(), locationsId);
						if (localCourseId !=null)
						{
							localItem.setCourseId(localCourseId);
						}
						UnitOfMeasurement localUOMId = getUnitOfMeasurementByGlobalUOMIdAndLocationId(em, localItem.getStockUom(), locationsId);
						if (localUOMId != null && localUOMId.getId() !=null)
						{
							localItem.setStockUom(localUOMId.getId());
						}

						localUOMId = getUnitOfMeasurementByGlobalUOMIdAndLocationId(em, localItem.getSellableUom(), locationsId);
						if (localUOMId != null && localUOMId.getId() !=null)
						{
							localItem.setSellableUom(localUOMId.getId());
						}
						// worked
						if (itemLocalPacket.getItemsAttributeTypesList() != null)
						{
							List<ItemsAttributeType> itemsAttributesTypes = getLocalItemToItemsAttributeType(em, locationsId, itemLocalPacket.getItemsAttributeTypesList());
							packet.setItemsAttributeTypesList(itemsAttributesTypes);
						}

						// ItemsAttributeTypeToItemsAttribute
						List<ItemsAttributeTypeToItemsAttribute> itemsAttributeTypeToItemsAttribute = getLocalItemsAttributeTypeToItemsAttribute(httpRequest, em,
								itemLocalPacket.getItemsAttributeTypeToItemsAttributeList(), locationsId);
						packet.setItemsAttributeTypeToItemsAttributeList(itemsAttributeTypeToItemsAttribute);

						// ItemsCharToItemsAttribute
						List<ItemsCharToItemsAttribute> itemsCharToItemsAttribute = getLocalItemsCharToItemsAttribute(em, itemLocalPacket.getItemsCharToItemsAttribute(), locationsId);
						packet.setItemsCharToItemsAttribute(itemsCharToItemsAttribute);

						if (itemLocalPacket.getNutritionList() != null && itemLocalPacket.getNutritionList().size() > 0)
						{
							List<Nutritions> nutritions = getLocalItemToNutritions(em, locationsId, itemLocalPacket.getNutritionList());
							packet.setNutritionList(nutritions);
						}

						if (localItem.getSalesTax1() != null)
						{
							SalesTax c = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, localItem.getSalesTax1());
							String localTax = getLocalSalesTaxFromGlobalId(em, c.getTaxName(), locationsId, baseLocation.getId());
							localItem.setSalesTax1(localTax);
						}
						if (localItem.getSalesTax2() != null)
						{
							SalesTax c = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, localItem.getSalesTax2());
							String localTax = getLocalSalesTaxFromGlobalId(em, c.getTaxName(), locationsId, baseLocation.getId());
							localItem.setSalesTax2(localTax);
						}
						if (localItem.getSalesTax3() != null)
						{
							SalesTax c = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, localItem.getSalesTax3());
							String localTax = getLocalSalesTaxFromGlobalId(em, c.getTaxName(), locationsId, baseLocation.getId());
							localItem.setSalesTax3(localTax);
						}
						if (localItem.getSalesTax4() != null)
						{
							SalesTax c = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, localItem.getSalesTax4());
							String localTax = getLocalSalesTaxFromGlobalId(em, c.getTaxName(), locationsId, baseLocation.getId());
							localItem.setSalesTax4(localTax);
						}
						
						
						
						if (itemPacket.getItemToSupplier() != null)
						{
							packet.setItemToSupplier(itemPacket.getItemToSupplier());
						}

						if (globalItem.getItemGroupId() != null /*&& (isAdd || originalItems.getItemGroupId() != -1)*/)
						{
							String queryString = "select ig from ItemGroup ig where ig.globalId =? and ig.locationsId=?  and ig.status!='D' ";
							TypedQuery<ItemGroup> query = em.createQuery(queryString, ItemGroup.class).setParameter(1, globalItem.getItemGroupId()).setParameter(2, locationId);
							ItemGroup group = null;
							try
							{
								group = query.getSingleResult();
							}
							catch (Exception e)
							{

								logger.info("Item group not present");
							}

							if (group != null)
							{
								localItem.setItemGroupId(group.getId());
							}
							else
							{
								ItemGroup globalItemGroup = (ItemGroup) new CommonMethods().getObjectById("ItemGroup", em,ItemGroup.class, globalItem.getItemGroupId());
								if (globalItemGroup != null)
								{

									ItemGroup localItemGroup = new ItemGroup().getItemGroup(globalItemGroup);
									localItemGroup.setLocationsId(locationsId);
									localItemGroup.setGlobalId(globalItem.getItemGroupId());
									if(localItemGroup.getId()==null){
										localItemGroup.setId(new StoreForwardUtility().generateUUID());
									}
									localItemGroup = em.merge(localItemGroup);
									localItem.setItemGroupId(localItemGroup.getId());
								}
								else
								{
									localItem.setItemGroupId(null);
								}

							}

						}

						packet.setItem(localItem);

						localItem = addUpdateItem(em, localItem, packet, isRawMaterialUpdate, originalItemPacket, httpRequest,false);
						itemPacket.setItem(localItem);
						itemPacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket", httpRequest);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, httpRequest, locationId,
								Integer.parseInt(itemPacket.getMerchantId()));
					}
				}
			}
		}

	}

	private Item setDBValuesToLatestObject(Item from, Item to, Item itemPacket, ItemPacket originalItemPacket, boolean isCourseUpdate)
	{
		if (itemPacket.getName() == null)
		{
			to.setName(from.getName());
		}

		if (itemPacket.getShortName() == null)
		{
			to.setShortName(from.getShortName());
		}

		if (itemPacket.getPriceSelling().doubleValue() == -1)
		{
			to.setPriceSelling(from.getPriceSelling());
		}

		if (itemPacket.getDisplayName() == null)
		{
			to.setDisplayName(from.getDisplayName());
		}

		if (itemPacket.getItemType() == -1)
		{
			to.setItemType(from.getItemType());
		}

		// if (!(itemPacket.getPriceSelling().doubleValue() > 0)) {
		// to.setPriceSelling(from.getPriceSelling());
		// }

		if (itemPacket.getCourseId() == null && isCourseUpdate)
		{
			to.setCourseId(from.getCourseId());
		}

		if (itemPacket.getStockUom() == null)
		{
			to.setStockUom(from.getStockUom());
		}

		if (itemPacket.getSellableUom() == null)
		{
			to.setSellableUom(from.getSellableUom());
		}

		if (itemPacket.getHexCodeValues() == null)
		{
			to.setHexCodeValues(from.getHexCodeValues());
		}

		if (itemPacket.getStartTime() == null)
		{
			to.setStartTime(from.getStartTime());
		}
		if (itemPacket.getEndTime() == null)
		{
			to.setEndTime(from.getEndTime());
		}

		if (itemPacket.getImageName() == null)
		{
			to.setImageName(from.getImageName());
		}

		if (itemPacket.getDescription() == null)
		{
			to.setDescription(from.getDescription());
		}

		if (itemPacket.getPlu() == null)
		{
			to.setPlu(from.getPlu());
		}

		if (itemPacket.getIsRealTimeUpdateNeeded() == 2)
		{
			to.setIsRealTimeUpdateNeeded(from.getIsRealTimeUpdateNeeded());
		}

		if (itemPacket.getIsOnlineItem() == 2)
		{
			to.setIsOnlineItem(from.getIsOnlineItem());
		}

		if (itemPacket.getIsinventoryAccrualOverriden() == 2)
		{
			to.setIsinventoryAccrualOverriden(from.getIsinventoryAccrualOverriden());
		}

		if (itemPacket.getIsScanRequired() == 2)
		{
			to.setIsScanRequired(from.getIsScanRequired());
		}

		if (itemPacket.getIsManualQuantity() == 2)
		{
			to.setIsManualQuantity(from.getIsManualQuantity());
		}
		if (itemPacket.getIsManualPrice() == 2)
		{
			to.setIsManualPrice(from.getIsManualPrice());
		}

		if (itemPacket.getInventoryAccrual() == 2)
		{
			to.setInventoryAccrual(from.getInventoryAccrual());
		}
		if (itemPacket.getStatus() == null)
		{
			to.setStatus(from.getStatus());
		}
		
		if (itemPacket.getItemGroupId()!=null && itemPacket.getItemGroupId().equals("-1"))
		{
			
			to.setItemGroupId(from.getItemGroupId());
		}
		
		if (itemPacket.getSalesTax1()!=null && itemPacket.getSalesTax1().equals("-1"))
		{
			to.setSalesTax1(from.getSalesTax1());
		}

		if (itemPacket.getSalesTax2()!=null &&  itemPacket.getSalesTax2().equals("-1"))
		{
			to.setSalesTax2(from.getSalesTax2());
		}

		if (itemPacket.getSalesTax3()!=null && itemPacket.getSalesTax3().equals("-1"))
		{
			to.setSalesTax3(from.getSalesTax3());
		}

		if (itemPacket.getSalesTax4()!=null && itemPacket.getSalesTax4().equals("-1"))
		{
			to.setSalesTax4(from.getSalesTax4());
		}
		if (itemPacket.getCategoryItems() == null)
		{
			to.setCategoryItems(from.getCategoryItems());
		}

		return to;
	}

	private Item getItemByGlobalItemIdAndLocationId(EntityManager em, String locationId, String globalItemId, String categoryId) throws Exception
	{
		try
		{
			// Category category = getChildCategory(locationId, categoryId,
			// em,false);
			// Category packetCategory = getChildCategory(locationId,
			// packeteCatId, em,false);
			// get item corresponding to that item , localcategoryid and
			// location
			// if (category != null)
			// {
			/*
			 * String sql =
			 * "select i.id from items i join category_items ci on  ci.items_id=i.id "
			 * + " left join category c on c.id=ci.category_id " +
			 * " where i.locations_id=? and i.global_item_id=? and ci.category_id in ("
			 * + packetCategory.getId() + "," + category.getId() + ") "; ;
			 * Integer resultList = (Integer)
			 * em.createNativeQuery(sql).setParameter(1,
			 * locationId).setParameter(2, globalItemId).getSingleResult();
			 * 
			 * // search for that item try { String queryString =
			 * "select s from Item s where s.id =? "; TypedQuery<Item> query =
			 * em.createQuery(queryString, Item.class).setParameter(1,
			 * resultList); return query.getSingleResult(); } catch (Exception
			 * e) { logger.severe("No Result found"); }
			 */

			try
			{
				String queryString = "select s from Item s where s.globalItemId =? and s.locationsId = ? and s.status != 'D'";
				TypedQuery<Item> query = em.createQuery(queryString, Item.class).setParameter(1, globalItemId).setParameter(2, locationId);
				return query.getSingleResult();
			}
			catch (Exception e)
			{
				logger.severe("No Result found" + e);
			}
			// }

		}
		catch (Exception e)
		{
			// TODO: handle exception\
			logger.severe("No Result found" + e);
		}

		return null;
	}

	// private Category getCategoryByGlobalCategoryIdAndLocationId(EntityManager
	// em, String locationId, int globalCategoryId,int category_id) throws
	// Exception
	// {
	// Category childCategory = null;
	// if(category_id!=0){
	// Category cate = (Category) new CommonMethods().getObjectById("Category", em,Category.class, category_id);
	// try
	// {
	// String queryString =
	// "select s from Category s where s.globalCategoryId =? and s.locationsId=?
	// and s.categoryId =? and s.status !='D' ";
	// int childCategoryId=0;
	// if(cate!=null){
	// childCategoryId = cate.getCategoryId();
	// }
	// TypedQuery<Category> query = em.createQuery(queryString,
	// Category.class).setParameter(1, globalCategoryId).setParameter(2,
	// locationId)
	// .setParameter(3, childCategoryId);
	// return query.getSingleResult();
	//
	// }
	// catch (Exception e)
	// {
	//
	// logger.severe(e);
	//
	// }
	// try
	// {
	// String queryString =
	// "select s from Category s where s.globalCategoryId =? and s.categoryId =0
	// and s.locationsId=? and s.status !='D' ";
	// TypedQuery<Category> query = em.createQuery(queryString,
	// Category.class).setParameter(1, category_id).setParameter(2, locationId);
	// childCategory= query.getSingleResult();
	//
	// }
	// catch (Exception e)
	// {
	//
	// logger.severe(e);
	//
	// }
	// }
	// try
	// {
	// String queryString =
	// "select s from Category s where s.globalCategoryId =? and s.locationsId=?
	// and s.categoryId =? and s.status !='D' ";
	// int childCategoryId=0;
	// if(childCategory!=null){
	// childCategoryId = childCategory.getId();
	// }
	// TypedQuery<Category> query = em.createQuery(queryString,
	// Category.class).setParameter(1, globalCategoryId).setParameter(2,
	// locationId)
	// .setParameter(3, childCategoryId);
	// return query.getSingleResult();
	//
	// }
	// catch (Exception e)
	// {
	//
	// logger.severe(e);
	//
	// }
	// return null;
	// }

	private String getLocalPrinterFromGlobalPrinterId(EntityManager em, String globalPrinterId, String locationId, String globalPrintersInterfaceId, String globalPrinterTypeId)
	{
		Printer printer = null;
		try
		{
			String queryString = "select s from Printer s where s.globalPrinterId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<Printer> query = em.createQuery(queryString, Printer.class).setParameter(1, globalPrinterId).setParameter(2, locationId);
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			logger.severe("No Result found");
		}
		if (printer == null)
		{

			Printer global = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, globalPrinterId);
			if (global != null)
			{
				printer = new Printer().getPrinterObject(global);
				printer.setLocationsId(locationId);
				printer.setGlobalPrinterId(globalPrinterId);

				// printer.setPrintersModelId(printersModelId);

				PrintersInterface globalPrinterInterface = null;
				try
				{
					String queryString = "select s from PrintersInterface s where s.globalPrintersInterfaceId =? and s.locationsId=?  ";
					TypedQuery<PrintersInterface> query = em.createQuery(queryString, PrintersInterface.class).setParameter(1, printer.getPrintersInterfaceId()).setParameter(2, locationId);
					globalPrinterInterface = query.getSingleResult();
				}
				catch (Exception e)
				{
					logger.severe("No Result found for printersInterface For globalPrintersInterfaceId " + globalPrintersInterfaceId + " locationId " + locationId);
				}

				if (globalPrinterInterface == null)
				{
					PrintersInterface globalPrinterInterfaceTemp = (PrintersInterface) new CommonMethods().getObjectById("PrintersInterface", em,PrintersInterface.class,  printer.getPrintersInterfaceId());
					if (globalPrinterInterfaceTemp != null)
					{
						PrintersInterface localPrinterInterface = new PrintersInterface().getPrinterInterfaceObject(globalPrinterInterfaceTemp);
						localPrinterInterface.setLocationsId(locationId);
						localPrinterInterface.setGlobalPrintersInterfaceId(printer.getPrintersInterfaceId());
						localPrinterInterface = em.merge(localPrinterInterface);

						printer.setPrintersInterfaceId(localPrinterInterface.getId());
					}

				}
				else
				{
					printer.setPrintersInterfaceId(globalPrinterInterface.getId());
				}

				PrintersType globalPrinterType = null;
				try
				{
					String queryString = "select s from PrintersType s where s.globalPrintersTypeId =? and s.locationsId=?  ";
					TypedQuery<PrintersType> query = em.createQuery(queryString, PrintersType.class).setParameter(1, printer.getPrintersTypeId()).setParameter(2, locationId);
					globalPrinterType = query.getSingleResult();
				}
				catch (Exception e)
				{
					logger.severe("No Result found for PrintersType For globalPrintersTypeId " + globalPrinterTypeId + " locationsId " + locationId);
				}

				if (globalPrinterType == null)
				{
					PrintersType globalPrinterTypeTemp = (PrintersType) new CommonMethods().getObjectById("PrintersType", em,PrintersType.class, printer.getPrintersTypeId());
					if (globalPrinterTypeTemp != null)
					{
						PrintersType localPrinterType = new PrintersType().getPrinterTypeObject(globalPrinterTypeTemp);
						localPrinterType.setLocationsId(locationId);
						localPrinterType.setGlobalPrintersTypeId(printer.getPrintersTypeId());
						localPrinterType = em.merge(localPrinterType);

						printer.setPrintersTypeId(localPrinterType.getId());
					}

				}
				else
				{
					printer.setPrintersTypeId(globalPrinterType.getId());
				}

				return em.merge(printer).getId();
			}

		}
		return null;
	}

	// private int getLocalCategoryFromGlobalCategoryId(EntityManager em, int
	// globalCategoryId, String locationId,int isRawItem)
	// {
	// int id = 0;
	// try
	// {
	// String queryString = "";
	// if(isRawItem==0){
	// queryString =
	// "select s from Category s where s.globalCategoryId =? and s.locationsId=?
	// and s.status !='R' ";
	// }else{
	// queryString =
	// "select s from Category s where s.globalCategoryId =? and s.locationsId=?
	// and s.status ='R' ";
	// }
	//
	// TypedQuery<Category> query = em.createQuery(queryString,
	// Category.class).setParameter(1, globalCategoryId).setParameter(2,
	// locationId);
	// id = query.getSingleResult().getId();
	// }
	// catch (Exception e)
	// {
	// logger.severe("No Result found");
	// }
	// return id;
	// }

	private List<Printer> getLocalItemToPrinters(EntityManager em, String locationId, List<Printer> globalPrintersList)
	{

		List<Printer> localItemsToPrinters = new ArrayList<Printer>();
		if (globalPrintersList != null)
		{
			for (Printer globalprinter : globalPrintersList)
			{
				String localId = getLocalPrinterFromGlobalPrinterId(em, globalprinter.getId(), locationId, globalprinter.getPrintersInterfaceId(), globalprinter.getPrintersTypeId());
				if (localId !=null)
				{
					Printer local = new Printer();
					local.setId(localId);
					localItemsToPrinters.add(local);
				}

			}
		}

		return localItemsToPrinters;
	}

	private List<Category> getLocalCategoryItem(EntityManager em, String locationId, List<Category> globalCategorys) throws Exception

	{
		List<Category> localCategoryItemList = new ArrayList<Category>();

		if (globalCategorys != null)
		{
			for (Category globalCategory : globalCategorys)
			{
				logger.severe("globalCategory=======================@@@@@@@@@@@@@@@@@@@@@@@@@@@========================"+globalCategory);
				Category category = getChildCategory(locationId, globalCategory.getId(), em, true);
				String localId = null;
				if (category != null)
				{
					localId = category.getId();
				}

				if (localId !=null)
				{
					Category local = new Category();
					local.setId(localId);
					localCategoryItemList.add(local);
				}

			}
		}

		return localCategoryItemList;
	}

	public String getItemByLocationId(String locationId, int startIndex, int endIndex, String categoryName, String categoryId, EntityManager em, HttpServletRequest httpRequest) throws Exception
	{
		try
		{
			List<ItemDetailDisplayPacket> ans = new ArrayList<ItemDetailDisplayPacket>();
			String sql = "select distinct i.id,i.name,i.short_name,i.price_selling,c.display_name  as course_name,i.image_name, "
					+ " i.display_name as item_display_name , co.display_name category_name , i.stock_uom , itt.name as item_type_name , stockuom.name as stock_name "
					+ "  from items i left join course c on i.course_id=c.id " + " left join category_items ci on ci.items_id =i.id left join category co on co.id=ci.category_id "
					+ " left join items_type itt on itt.id=i.item_type " + " left join unit_of_measurement stockuom on stockuom.id = i.stock_uom " + " where i.locations_id= ? ";
			if (categoryId !=null)
			{
				sql += "and ci.category_id= " + categoryId;
			}

			sql += " and i.status!= 'D' and i.status!= 'R' and i.display_name like '" + categoryName + "%'  limit " + startIndex + "," + endIndex;

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			// setParameter(2, categoryId).getResultList();

			for (Object[] objRow : resultList)
			{
				ItemDetailDisplayPacket detailDisplayPacket = new ItemDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setShortName((String) objRow[2]);
				detailDisplayPacket.setPrice((BigDecimal) objRow[3]);
				detailDisplayPacket.setCourseName((String) objRow[4]);
				detailDisplayPacket.setImageName((String) objRow[5]);
				detailDisplayPacket.setItemDisplayName((String) objRow[6]);
				detailDisplayPacket.setCategoryName((String) objRow[7]);
				detailDisplayPacket.setStockUom((String) objRow[8]);
				detailDisplayPacket.setItemTypeName((String) objRow[9]);
				detailDisplayPacket.setUomName((String) objRow[10]);
				ans.add(detailDisplayPacket);
			}

			for (ItemDetailDisplayPacket itemDetailDisplayPacket : ans)
			{
				List<ItemsToPrinter> itemToPrinters = getItemsToPrinter(em, itemDetailDisplayPacket.getId());
				String itemToPrinter = "";
				String itemToPrinterName = "";
				if (itemToPrinters == null || itemToPrinters.size() == 0)
				{
					try
					{
						String queryString = "select p from CategoryItem p where p.itemsId=? ";
						TypedQuery<CategoryItem> query = em.createQuery(queryString, CategoryItem.class).setParameter(1, itemDetailDisplayPacket.getId());
						CategoryItem categoryItem = query.getSingleResult();
						if (categoryItem != null)
						{
							List<CategoryToPrinter> toPrinters = getCategoryToPrinter(em, categoryItem.getCategoryId());
							for (int i = 0; i < toPrinters.size(); i++)
							{
								CategoryToPrinter categoryToPrinter = toPrinters.get(i);
								Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, categoryToPrinter.getPrintersId());
								if (printer != null)
								{
									if (i == (toPrinters.size() - 1))
									{
										itemToPrinterName += printer.getDisplayName();
										itemToPrinter += categoryToPrinter.getId();
									}
									else
									{
										itemToPrinterName += printer.getDisplayName() + ",";
										itemToPrinter += categoryToPrinter.getId() + ",";
									}
								}
							}
						}

					}
					catch (Exception e)
					{
						logger.severe("No Result found");
					}
				}
				else
				{
					for (int i = 0; i < itemToPrinters.size(); i++)
					{
						ItemsToPrinter itemToPrinter2 = itemToPrinters.get(i);
						Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, itemToPrinter2.getPrintersId());
						if (printer != null)
						{
							if (i == (itemToPrinters.size() - 1))
							{
								itemToPrinterName += printer.getDisplayName();
								itemToPrinter += itemToPrinter2.getId();
							}
							else
							{
								itemToPrinterName += printer.getDisplayName() + ",";
								itemToPrinter += itemToPrinter2.getId() + ",";
							}
						}
					}
				}

				itemDetailDisplayPacket.setItemToPrinter(itemToPrinter);
				itemDetailDisplayPacket.setItemToPrinterName(itemToPrinterName);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private List<ItemsToPrinter> getItemsToPrinter(EntityManager em, String itemId)
	{
		try
		{
			String queryString = "select p from ItemsToPrinter p where p.itemsId=? and p.status != 'D' ";
			TypedQuery<ItemsToPrinter> query = em.createQuery(queryString, ItemsToPrinter.class).setParameter(1, itemId);
			return query.getResultList();
		}
		catch (Exception e)
		{
			logger.severe("No Result found");
		}
		return null;
	}

	public Item deleteMultipleLocationItem(EntityManager em, Item item, ItemPacket itemPacket, HttpServletRequest request) throws Exception
	{
		// delete baselocation
		item = delete(em, item);
		// get all sublocations
		List<Item> printers = getAllItemByGlobalId(item.getId(), em);
		// delete sublocation
		for (Item item2 : printers)
		{
			delete(em, item2);
		}
		return item;
	}

	List<Item> getAllItemByGlobalId(String globalId, EntityManager em)
	{

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
			Root<Item> printerRoot = criteria.from(Item.class);
			TypedQuery<Item> query = em.createQuery(criteria.select(printerRoot).where(builder.equal(printerRoot.get(Item_.globalItemId), globalId)));
			return query.getResultList();
		}
		catch (Exception e)
		{
			logger.severe("No Result found");
		}
		return null;
	}

	private List<Discount> getLocalItemToDiscount(EntityManager em, String locationId, List<Discount> globalList)
	{
		List<Discount> localItemsToDiscount = new ArrayList<Discount>();
		if (globalList != null)
		{
			for (Discount global : globalList)
			{
				String localId = getLocalDiscountFromGlobalId(em, global.getId(), locationId, global.getDiscountsTypeId());
				if (localId !=null)
				{
					Discount local = new Discount();
					local.setId(localId);
					localItemsToDiscount.add(local);
				}

			}
		}

		return localItemsToDiscount;
	}

	private String getLocalDiscountFromGlobalId(EntityManager em, String globalId, String locationId, String globalDiscountTypeId)
	{
		Discount discount = null;
		try
		{
			String queryString = "select s from Discount s where s.globalId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<Discount> query = em.createQuery(queryString, Discount.class).setParameter(1, globalId).setParameter(2, locationId);
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			logger.severe("No Discount Entry found for globalId " + globalId + " locationsId " + locationId);
		}

		if (discount == null)
		{
			Discount global = (Discount) new CommonMethods().getObjectById("Discount", em,Discount.class, globalId);
			if (global != null)
			{
				discount = new Discount().getDiscount(global);
				discount.setLocationsId(locationId);
				discount.setGlobalId(globalId);

				DiscountsType globalDiscountsType = null;
				try
				{
					String queryString = "select s from DiscountsType s where s.globalDiscountTypeId =? and s.locationsId=?  ";
					TypedQuery<DiscountsType> query = em.createQuery(queryString, DiscountsType.class).setParameter(1, discount.getDiscountsTypeId()).setParameter(2, locationId);
					globalDiscountsType = query.getSingleResult();
				}
				catch (Exception e)
				{
					logger.severe("No Result found for DiscountsType for globalDiscountTypeId " + discount.getDiscountsTypeId() + " locationsId " + locationId);
				}

				if (globalDiscountsType == null)
				{
					DiscountsType globalDiscountsTypeTemp = (DiscountsType) new CommonMethods().getObjectById("DiscountsType", em,DiscountsType.class, discount.getDiscountsTypeId());
					if (globalDiscountsTypeTemp != null)
					{
						DiscountsType localDiscountsType = new DiscountsType().getDiscountTypeObject(globalDiscountsTypeTemp);
						localDiscountsType.setLocationsId(locationId);
						localDiscountsType.setGlobalDiscountTypeId(discount.getDiscountsTypeId());
						if(localDiscountsType.getId()==null){
							localDiscountsType.setId(new StoreForwardUtility().generateUUID());
						}
						localDiscountsType = em.merge(localDiscountsType);
						discount.setDiscountsTypeId(localDiscountsType.getId());
					}

				}
				else
				{
					discount.setDiscountsTypeId(globalDiscountsType.getId());
				}
				if(discount.getId()==null){
					discount.setId(new StoreForwardUtility().generateUUID());
				}
				return em.merge(discount).getId();
			}

		}

		return null;
	}

	private List<ItemsChar> getLocalItemTItemsChar(EntityManager em, String locationId, List<ItemsChar> globalList)
	{
		List<ItemsChar> localItemsToItemsChar = new ArrayList<ItemsChar>();
		if (globalList != null && globalList.size() > 0)
		{
			for (ItemsChar global : globalList)
			{

				String localId = getLocalItemsCharFromGlobalId(em, global.getId(), locationId);
				if (localId!=null)
				{
					ItemsChar local = new ItemsChar();
					local.setId(localId);
					localItemsToItemsChar.add(local);
				}

			}
		}

		return localItemsToItemsChar;
	}

	private String getLocalItemsCharFromGlobalId(EntityManager em, String globalId, String locationId)
	{
		ItemsChar itemsChar = null;
		try
		{
			String queryString = "select s from ItemsChar s where s.globalItemCharId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<ItemsChar> query = em.createQuery(queryString, ItemsChar.class).setParameter(1, globalId).setParameter(2, locationId);
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		if (itemsChar == null)
		{
			ItemsChar global = (ItemsChar) new CommonMethods().getObjectById("ItemsChar", em,ItemsChar.class,  globalId);
			if (global != null)
			{
				itemsChar = new ItemsChar().getItemChar(global);
				itemsChar.setLocationsId(locationId);
				itemsChar.setGlobalItemCharId(globalId);
				if(itemsChar.getId()==null){
					itemsChar.setId(new StoreForwardUtility().generateUUID());
				}
				return em.merge(itemsChar).getId();
			}

		}

		return null;
	}

	private List<ItemsAttribute> getLocalItemToItemsAttribute(EntityManager em, String locationId, List<ItemsAttribute> globalList)
	{
		List<ItemsAttribute> localItemsToItemsAttribute = new ArrayList<ItemsAttribute>();
		if (globalList != null && globalList.size() > 0)
		{
			for (ItemsAttribute global : globalList)
			{
				if (global != null)
				{
					String localId = getLocalItemsAttributeFromGlobalId(em, global.getId(), locationId);
					if (localId !=null)
					{
						ItemsAttribute local = new ItemsAttribute();
						local.setId(localId);
						localItemsToItemsAttribute.add(local);
					}
				}

			}
		}

		return localItemsToItemsAttribute;
	}

	private String getLocalItemsAttributeFromGlobalId(EntityManager em, String globalId, String locationId)
	{
		ItemsAttribute itemsAttribute = null;
		try
		{
			String queryString = "select s from ItemsAttribute s where s.globalId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<ItemsAttribute> query = em.createQuery(queryString, ItemsAttribute.class).setParameter(1, globalId).setParameter(2, locationId);
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		if (itemsAttribute == null)
		{
			ItemsAttribute global = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,ItemsAttribute.class, globalId);

			if (global != null)
			{

				itemsAttribute = new ItemsAttribute().getItemsAttribute(global);
				itemsAttribute.setItemsAttributeTypeToItemsAttributes(new HashSet<ItemsAttributeTypeToItemsAttribute>());
				itemsAttribute.setLocationsId(locationId);
				itemsAttribute.setGlobalId(globalId);
				if(itemsAttribute.getId()==null){
				itemsAttribute.setId(new StoreForwardUtility().generateUUID());
				}
				return em.merge(itemsAttribute).getId();
			}

		}

		return null;
	}

	private List<ItemsAttributeType> getLocalItemToItemsAttributeType(EntityManager em, String locationId, List<ItemsAttributeType> globalList)
	{
		List<ItemsAttributeType> localItemsToItemsAttribute = new ArrayList<ItemsAttributeType>();
		if (globalList != null && globalList.size() > 0)
		{
			for (ItemsAttributeType global : globalList)
			{
				String localId = getLocalItemsAttributeTypeFromGlobalId(em, global.getId(), locationId);
				if (localId !=null)
				{
					ItemsAttributeType local = new ItemsAttributeType();
					local.setId(localId);
					localItemsToItemsAttribute.add(local);
				}
			}
		}

		return localItemsToItemsAttribute;
	}

	private List<ItemsAttributeTypeToItemsAttribute> getLocalItemsAttributeTypeToItemsAttribute(HttpServletRequest httpRequest, EntityManager em, List<ItemsAttributeTypeToItemsAttribute> globalList,
			String locationId)
	{
		List<ItemsAttributeTypeToItemsAttribute> localItemsAttributeTypeToItemsAttribute = new ArrayList<ItemsAttributeTypeToItemsAttribute>();
		if (globalList != null && globalList.size() > 0)
		{
			for (ItemsAttributeTypeToItemsAttribute global : globalList)
			{
				ItemsAttributeTypeToItemsAttribute local = getLocalItemsAttributeTypeToItemsAttributeFromGlobalId(httpRequest, em, global, locationId);
				if (local != null && local.getId() > 0)
				{

					localItemsAttributeTypeToItemsAttribute.add(local);
				}
			}
		}

		return localItemsAttributeTypeToItemsAttribute;
	}

	private List<ItemsCharToItemsAttribute> getLocalItemsCharToItemsAttribute(EntityManager em, List<ItemsCharToItemsAttribute> globalList, String locationId)
	{
		List<ItemsCharToItemsAttribute> localItemsCharToItemsAttribute = new ArrayList<ItemsCharToItemsAttribute>();
		if (globalList != null && globalList.size() > 0)
		{
			for (ItemsCharToItemsAttribute global : globalList)
			{
				ItemsCharToItemsAttribute local = getLocalItemsCharToItemsAttributeFromGlobalId(em, global, locationId);
				if (local != null && local.getId() > 0)
				{

					localItemsCharToItemsAttribute.add(local);
				}
			}
		}

		return localItemsCharToItemsAttribute;
	}

	private ItemsAttributeTypeToItemsAttribute getLocalItemsAttributeTypeToItemsAttributeFromGlobalId(HttpServletRequest httpRequest, EntityManager em, ItemsAttributeTypeToItemsAttribute global,
			String locationsId)
	{
		String localItemsAttributeId = null;
		String localItemsAttributeTypeId = null;
		try
		{

			localItemsAttributeId = getLocalItemsAttributeFromGlobalId(em, global.getItemsAttributeId(), locationsId);
			localItemsAttributeTypeId = getLocalItemsAttributeTypeFromGlobalId(em, global.getItemsAttributeTypeId(), locationsId);

			if (localItemsAttributeId !=null && localItemsAttributeTypeId !=null )
			{
				// queryString =
				// "select s from ItemsAttributeTypeToItemsAttribute s where
				// s.itemsAttributeId =? and s.itemsAttributeTypeId=? and
				// s.status !='D' ";
				String queryString = "select s from ItemsAttributeTypeToItemsAttribute s where s.itemsAttributeId =? and s.itemsAttributeTypeId=?";
				TypedQuery<ItemsAttributeTypeToItemsAttribute> queryIATTIA = em.createQuery(queryString, ItemsAttributeTypeToItemsAttribute.class).setParameter(1, localItemsAttributeId)
						.setParameter(2, localItemsAttributeTypeId);
				return queryIATTIA.getSingleResult();
			}

		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "ItemsAttributeTypeToItemsAttribute not found for itemsAttributeId " + localItemsAttributeId + " itemsAttributeTypeId " + localItemsAttributeTypeId);
		}

		// creating object in local

		ItemsAttributeTypeToItemsAttribute globalAttributeType = new ItemsAttributeTypeToItemsAttribute();
		globalAttributeType.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		globalAttributeType.setCreatedBy(global.getCreatedBy());
		globalAttributeType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		globalAttributeType.setUpdatedBy(global.getUpdatedBy());
		globalAttributeType.setStatus("A");
		globalAttributeType.setItemsAttributeId(localItemsAttributeId);
		globalAttributeType.setItemsAttributeTypeId(localItemsAttributeTypeId);
		globalAttributeType = em.merge(globalAttributeType);

		return globalAttributeType;
	}

	private ItemsCharToItemsAttribute getLocalItemsCharToItemsAttributeFromGlobalId(EntityManager em, ItemsCharToItemsAttribute global, String locationsId)
	{

		String localItemsCharId = null;
		String localItemsAttributeId = null;
		try
		{

			localItemsCharId = getLocalItemsCharFromGlobalId(em, global.getItemsCharId(), locationsId);
			localItemsAttributeId = getLocalItemsAttributeFromGlobalId(em, global.getItemsAttributeId(), locationsId);

			if (localItemsCharId !=null && localItemsAttributeId !=null)
			{
				String queryString = "select s from ItemsCharToItemsAttribute s where s.itemsCharId =? and s.itemsAttributeId=?";
				TypedQuery<ItemsCharToItemsAttribute> queryIATTIA = em.createQuery(queryString, ItemsCharToItemsAttribute.class).setParameter(1, localItemsCharId).setParameter(2,
						localItemsAttributeId);
				return queryIATTIA.getSingleResult();
			}

		}
		catch (Exception e)
		{
			logger.severe(e);
			logger.severe("ItemsCharToItemsAttribute not found for itemsCharId " + localItemsCharId + " itemsAttributeId " + localItemsAttributeId);
		}

		// creating object in local

		ItemsCharToItemsAttribute globalItemsCharToItemsAttribute = new ItemsCharToItemsAttribute();
		globalItemsCharToItemsAttribute.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		globalItemsCharToItemsAttribute.setCreatedBy(global.getCreatedBy());
		globalItemsCharToItemsAttribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		globalItemsCharToItemsAttribute.setUpdatedBy(global.getUpdatedBy());
		globalItemsCharToItemsAttribute.setStatus("A");
		globalItemsCharToItemsAttribute.setItemsCharId(localItemsCharId);
		globalItemsCharToItemsAttribute.setItemsAttributeId(localItemsAttributeId);
		globalItemsCharToItemsAttribute = em.merge(globalItemsCharToItemsAttribute);

		return globalItemsCharToItemsAttribute;
	}

	private String getLocalItemsAttributeTypeFromGlobalId(EntityManager em, String globalId, String locationId)
	{
		String id = null;
		try
		{
			String queryString = "select s from ItemsAttributeType s where s.globalItemAttributeTypeId =? and s.locationsId=?  and s.status != 'D'";
			TypedQuery<ItemsAttributeType> query = em.createQuery(queryString, ItemsAttributeType.class).setParameter(1, globalId).setParameter(2, locationId);
			id = query.getSingleResult().getId();
		}
		catch (Exception e)
		{

			logger.severe("ItemsAttributeType not found for locationId" + globalId);
		}
		// creating object in local
		if (id == null)
		{
			ItemsAttributeType globalAttributeType = (ItemsAttributeType) new CommonMethods().getObjectById("ItemsAttributeType", em,ItemsAttributeType.class,  globalId);
			if (globalAttributeType != null)
			{
				ItemsAttributeType attributeType = new ItemsAttributeType().getItemsAttributeTypeObject(globalAttributeType);
				attributeType.setLocationsId(locationId);
				attributeType.setGlobalItemAttributeTypeId(globalAttributeType.getId());
				if(attributeType.getId()==null){
					attributeType.setId(new StoreForwardUtility().generateUUID());
				}
				attributeType = em.merge(attributeType);
				id = attributeType.getId();
			}

		}
		return id;
	}

	private UnitOfMeasurement getUnitOfMeasurementByGlobalUOMIdAndLocationId(EntityManager em, String globalUOMId, String locationId)
	{
		try
		{
			String queryString = "select s from UnitOfMeasurement s where s.globalId =? and s.locationId=? and s.status!='D' ";
			TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class).setParameter(1, globalUOMId).setParameter(2, locationId);
			return query.getSingleResult();
		}
		catch (Exception e)
		{

			logger.severe("No Result found for globalUOMId " + globalUOMId + " locationId" + locationId + " in UnitOfMeasurement");

		}
		return null;
	}

	private String getLocalCourseFromGlobalId(EntityManager em, String globalId, String locationId)
	{
		if (globalId != null)
		{
			Course course = null;
			try
			{
				String queryString = "select s from Course s where s.globalCourseId =? and s.locationsId=? and s.status!='D' ";
				TypedQuery<Course> query = em.createQuery(queryString, Course.class).setParameter(1, globalId).setParameter(2, locationId);
				return query.getSingleResult().getId();
			}
			catch (Exception e)
			{
				logger.severe("No Result found for globalId " + globalId + " locationId" + locationId + " in Course");
			}

			if (course == null)
			{
				Course global = (Course) new CommonMethods().getObjectById("Course", em,Course.class, globalId);
				if (global != null)
				{
					course = new Course().getCourse(global);
					course.setLocationsId(locationId);
					course.setGlobalCourseId(globalId);
					if(course.getId()==null){
						course.setId(new StoreForwardUtility().generateUUID());
					}
					course = em.merge(course);
					return course.getId();
				}

			}
		}

		return null;
	}

	private String getLocalSalesTaxFromGlobalId(EntityManager em, String taxName, String locationId, String globalLocationId)
	{
		SalesTax salesTax = null;
		try
		{
			String queryString = "select s from SalesTax s where s.taxName =? and s.locationsId=? and s.status!='D' ";

			TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class).setParameter(1, taxName).setParameter(2, locationId);
			salesTax = query.getSingleResult();
			return salesTax.getId();
		}
		catch (Exception e)
		{
			logger.severe("no result found for SalesTax" + " name " + taxName + " locationId " + locationId);
		}

		// All new Account have there own entry so don't need add new entry
		SalesTax globalTax = null;
		if (salesTax == null)
		{
			try
			{
				String queryString = "select s from SalesTax s where s.taxName =? and s.locationsId=? and s.status!='D' ";
				TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class).setParameter(1, taxName).setParameter(2, globalLocationId);
				globalTax = query.getSingleResult();
			}
			catch (Exception e)
			{
				logger.severe("no result found for SalesTax" + " name " + taxName + " locationId " + locationId);
			}

			if (globalTax != null)
			{
				salesTax = new SalesTax().getSalesTax(globalTax);
				salesTax.setLocationsId(locationId);
				salesTax.setGlobalId(globalTax.getId());
				if(salesTax.getId()==null){
					salesTax.setId(new StoreForwardUtility().generateUUID());
				}
				salesTax = em.merge(salesTax);
				return salesTax.getId();
			}

		}

		return null;
	}

	private List<CategoryToPrinter> getCategoryToPrinter(EntityManager em, String categoryId)
	{
		try
		{
			String queryString = "select p from CategoryToPrinter p where p.categoryId=? and p.status != 'D' ";
			TypedQuery<CategoryToPrinter> query = em.createQuery(queryString, CategoryToPrinter.class).setParameter(1, categoryId);
			return query.getResultList();
		}
		catch (Exception e)
		{

			logger.severe("no result found for categoryid" + categoryId);
		}
		return null;
	}

	public Item updateMultipleItemForInventory(EntityManager em, ItemPacket itemPacket) throws Exception
	{
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = itemPacket.getLocationsListId().split(",");
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);

		Item globalItemFromDb = getItemById(em, itemPacket.getItem().getId());
		List<CategoryItem> globalCategoryItem = globalItemFromDb.getCategoryItems();
		// finding globalCategoryId
		String categoryId = null;
		if (globalCategoryItem != null && globalCategoryItem.size() > 0)
		{
			// there should be only one category for one item
			categoryId = globalCategoryItem.get(0).getCategoryId();
		}
		Item item = itemPacket.getItem();
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (item != null && itemPacket.getIsBaseLocationUpdate() == 1)
		{
			// adding or updating global item
			// item.setLocationsId(baseLocation.getId());

			Item globalItem = updateItemForInventory(em, itemPacket);
			em.getTransaction().commit();
			em.getTransaction().begin();
			// now add/update child location
			for (String locationId : locationIds)
			{
				String locationsId = locationId;
				if (locationsId.equals(baseLocation.getId()))
				{
					Item localItem = new Item().copy(item);
					Item item1 = getItemByGlobalItemIdAndLocationId(em, locationId, globalItem.getId(), categoryId);
					if (item1 != null && item.getId() !=null)
					{
						localItem.setGlobalItemId(globalItem.getId());
						localItem.setId(item1.getId());
					}
					else
					{
						localItem.setGlobalItemId(globalItem.getId());
					}
					UnitOfMeasurement uom = getUnitOfMeasurementByGlobalIdAndLocationId(em, locationsId, localItem.getStockUom());
					UnitOfMeasurement uom1 = getUnitOfMeasurementByGlobalIdAndLocationId(em, locationsId, localItem.getSellableUom());
					ItemsType itemsType = getItemsTypeByGlobalIdAndLocationId(em, locationsId, localItem.getSellableUom());
					if (uom != null)
					{
						localItem.setStockUom(uom.getId());
					}
					if (uom1 != null)
					{
						localItem.setSellableUom(uom1.getId());
					}
					if (itemsType != null)
					{
						localItem.setItemType(itemsType.getId());
					}
					localItem.setLocationsId(locationId);
					itemPacket.setItem(localItem);
					updateItemForInventory(em, itemPacket);
				}
			}
		}
		return item;
	}

	private UnitOfMeasurement getUnitOfMeasurementByGlobalIdAndLocationId(EntityManager em, String locationId, String globalId)
	{
		try
		{
			String queryString = "select s from UnitOfMeasurement s where s.globalId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class).setParameter(1, globalId).setParameter(2, locationId);

			return query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe(e);

		}
		return null;
	}

	private ItemsType getItemsTypeByGlobalIdAndLocationId(EntityManager em, String locationId, String globalId)
	{
		try
		{
			String queryString = "select s from ItemsType s where s.globalId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<ItemsType> query = em.createQuery(queryString, ItemsType.class).setParameter(1, globalId).setParameter(2, locationId);
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe(e);

		}
		return null;
	}

	public String getItemUomById(String id, EntityManager em, HttpServletRequest httpRequest) throws Exception
	{
		try
		{
			List<ItemDetailDisplayPacket> ans = new ArrayList<ItemDetailDisplayPacket>();
			String sql = "select distinct i.id,i.name , i.display_name as item_display_name , co.display_name category_name , i.stock_uom , itt.name as item_type_name "
					+ "  from items i left join course c on i.course_id=c.id " + " left join category_items ci on ci.items_id =i.id left join category co on co.id=ci.category_id "
					+ " left join items_type itt on itt.id=i.item_type" + " where i.id= ? and i.status!= 'D'";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, id).getResultList();
			for (Object[] objRow : resultList)
			{
				ItemDetailDisplayPacket detailDisplayPacket = new ItemDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setItemDisplayName((String) objRow[2]);
				detailDisplayPacket.setCategoryName((String) objRow[3]);
				detailDisplayPacket.setStockUom((String) objRow[4]);
				detailDisplayPacket.setItemTypeName((String) objRow[5]);

				String queryString = "select l from Location l where l.id in   (select p.locationsId from Item p where p.globalItemId=?  and p.status not in ('D')) ";
				TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, detailDisplayPacket.getId());
				List<Location> resultSet = query2.getResultList();
				detailDisplayPacket.setLocationList(resultSet);
				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public int getRawMaterialItemCountByLocationId(EntityManager em, String locationId) throws Exception
	{
		String queryString = "select i from Item i where i.locationsId=? and i.status ='R' order by i.displaySequence  asc";
		TypedQuery<Item> query = em.createQuery(queryString, Item.class).setParameter(1, locationId);

		if (query.getResultList() != null)
		{
			return query.getResultList().size();
		}
		else
		{
			return 0;
		}

	}

	public List<ItemDetailDisplayPacket> getRawMaterialItemByStatusAndLocationId(EntityManager em, String locationId, int startIndex, int endIndex, String categoryId, String itemName) throws Exception
	{

		try
		{
			List<ItemDetailDisplayPacket> ans = new ArrayList<ItemDetailDisplayPacket>();
			String sql = "select distinct i.id,i.name,i.short_name,i.price_selling,c.display_name  as course_name,i.image_name, "
					+ " i.display_name as item_display_name , co.display_name category_name , i.stock_uom , itt.name as item_type_name " + "  from items i left join course c on i.course_id=c.id "
					+ " left join category_items ci on ci.items_id =i.id left join category co on co.id=ci.category_id " + " left join items_type itt on itt.id=i.item_type "
					+ " where i.locations_id= ? ";
			if (categoryId != null && categoryId.length() > 0)
			{
				sql += "and ci.category_id in (" + categoryId + ")";
			}
			sql += "and i.name like '%" + itemName + "%' and i.status= 'R' limit " + startIndex + "," + endIndex;

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();

			for (Object[] objRow : resultList)
			{
				ItemDetailDisplayPacket detailDisplayPacket = new ItemDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setShortName((String) objRow[2]);
				detailDisplayPacket.setPrice((BigDecimal) objRow[3]);
				detailDisplayPacket.setCourseName((String) objRow[4]);
				detailDisplayPacket.setImageName((String) objRow[5]);
				detailDisplayPacket.setItemDisplayName((String) objRow[6]);
				detailDisplayPacket.setCategoryName((String) objRow[7]);
				detailDisplayPacket.setStockUom((String) objRow[8]);
				detailDisplayPacket.setItemTypeName((String) objRow[9]);
				ans.add(detailDisplayPacket);
			}

			for (ItemDetailDisplayPacket itemDetailDisplayPacket : ans)
			{
				List<ItemsToPrinter> itemToPrinters = getItemsToPrinter(em, itemDetailDisplayPacket.getId());
				String itemToPrinter = "";
				String itemToPrinterName = "";
				if (itemToPrinters == null || itemToPrinters.size() == 0)
				{
					try
					{
						String queryString = "select p from CategoryItem p where p.itemsId=? ";
						TypedQuery<CategoryItem> query = em.createQuery(queryString, CategoryItem.class).setParameter(1, itemDetailDisplayPacket.getId());
						CategoryItem categoryItem = query.getSingleResult();
						if (categoryItem != null)
						{
							List<CategoryToPrinter> toPrinters = getCategoryToPrinter(em, categoryItem.getCategoryId());
							for (int i = 0; i < toPrinters.size(); i++)
							{
								CategoryToPrinter categoryToPrinter = toPrinters.get(i);
								Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, categoryToPrinter.getPrintersId());
								if (printer != null)
								{
									if (i == (toPrinters.size() - 1))
									{
										itemToPrinterName += printer.getDisplayName();
										itemToPrinter += categoryToPrinter.getId();
									}
									else
									{
										itemToPrinterName += printer.getDisplayName() + ",";
										itemToPrinter += categoryToPrinter.getId() + ",";
									}
								}
							}
						}

					}
					catch (Exception e)
					{
						logger.severe("No Result found");
					}
				}
				else
				{
					for (int i = 0; i < itemToPrinters.size(); i++)
					{
						ItemsToPrinter itemToPrinter2 = itemToPrinters.get(i);
						Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, itemToPrinter2.getPrintersId());
						if (printer != null)
						{
							if (i == (itemToPrinters.size() - 1))
							{
								itemToPrinterName += printer.getDisplayName();
								itemToPrinter += itemToPrinter2.getId();
							}
							else
							{
								itemToPrinterName += printer.getDisplayName() + ",";
								itemToPrinter += itemToPrinter2.getId() + ",";
							}
						}
					}
				}

				itemDetailDisplayPacket.setItemToPrinter(itemToPrinter);
				itemDetailDisplayPacket.setItemToPrinterName(itemToPrinterName);
			}
			return ans;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public String getItemByLocationIdAndSaleOnlyAndSaleAndInventory(ItemByLocationIdPacket itemByLocationIdPacket, EntityManager em, HttpServletRequest httpRequest) throws Exception
	{
		try
		{

			String temp = Utilities.convertAllSpecialCharForSearch(itemByLocationIdPacket.getItemName());

			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, itemByLocationIdPacket.getLocationId());

			List<ItemDetailDisplayPacket> ans = new ArrayList<ItemDetailDisplayPacket>();
			String sql = "select distinct i.id,i.name,i.short_name,i.price_selling,c.display_name  as course_name,i.image_name, "
					+ " i.display_name as item_display_name , co.display_name category_name , i.stock_uom , itt.name as item_type_name " + "  from items i left join course c on i.course_id=c.id "
					+ " left join category_items ci on (ci.items_id =i.id and ci.status !='D') " + " left join category co on co.id=ci.category_id "
					+ " left join items_type itt on itt.id=i.item_type " + "  where i.locations_id= ? and itt.name in ('Sale Only','Sale And Inventory', 'Semi Finished Goods')" + "";
			if (itemByLocationIdPacket.getCategoryId() != null && !itemByLocationIdPacket.getCategoryId().isEmpty())
			{
				sql += " and ci.category_id in (" + itemByLocationIdPacket.getCategoryId() + ")  ";
			}

			sql += " and  i.status not in ('D','R') and i.display_name like '%" + temp + "%' ";

			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				sql += " order by i.display_name asc ";
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				sql += " order by i.display_name desc ";
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Custom"))
			{
				sql += " order by i.display_name asc ";
			}
			sql += " limit " + itemByLocationIdPacket.getStartIndex() + "," + itemByLocationIdPacket.getEndIndex();
			@SuppressWarnings("unchecked")
				List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, itemByLocationIdPacket.getLocationId()).getResultList();

			for (Object[] objRow : resultList)
			{
				ItemDetailDisplayPacket detailDisplayPacket = new ItemDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setShortName((String) objRow[2]);
				detailDisplayPacket.setPrice((BigDecimal) objRow[3]);
				detailDisplayPacket.setCourseName((String) objRow[4]);
				detailDisplayPacket.setImageName((String) objRow[5]);
				detailDisplayPacket.setItemDisplayName((String) objRow[6]);
				detailDisplayPacket.setCategoryName((String) objRow[7]);
				detailDisplayPacket.setStockUom((String) objRow[8]);
				detailDisplayPacket.setItemTypeName((String) objRow[9]);
				ans.add(detailDisplayPacket);
			}

			for (ItemDetailDisplayPacket itemDetailDisplayPacket : ans)
			{
				List<ItemsToPrinter> itemToPrinters = getItemsToPrinter(em, itemDetailDisplayPacket.getId());
				String itemToPrinter = "";
				String itemToPrinterName = "";
				if (itemToPrinters == null || itemToPrinters.size() == 0)
				{
					try
					{
						String queryString = "select p from CategoryItem p where p.itemsId=? ";
						TypedQuery<CategoryItem> query = em.createQuery(queryString, CategoryItem.class).setParameter(1, itemDetailDisplayPacket.getId());
						CategoryItem categoryItem = query.getSingleResult();
						if (categoryItem != null)
						{
							List<CategoryToPrinter> toPrinters = getCategoryToPrinter(em, categoryItem.getCategoryId());
							for (int i = 0; i < toPrinters.size(); i++)
							{
								CategoryToPrinter categoryToPrinter = toPrinters.get(i);
								Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, categoryToPrinter.getPrintersId());
								if (printer != null)
								{
									if (i == (toPrinters.size() - 1))
									{
										itemToPrinterName += printer.getDisplayName();
										itemToPrinter += categoryToPrinter.getId();
									}
									else
									{
										itemToPrinterName += printer.getDisplayName() + ",";
										itemToPrinter += categoryToPrinter.getId() + ",";
									}
								}
							}
						}

					}
					catch (Exception e)
					{
						logger.severe("No Result found");
					}
				}
				else
				{
					for (int i = 0; i < itemToPrinters.size(); i++)
					{
						ItemsToPrinter itemToPrinter2 = itemToPrinters.get(i);
						Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, itemToPrinter2.getPrintersId());
						if (printer != null)
						{
							if (i == (itemToPrinters.size() - 1))
							{
								itemToPrinterName += printer.getDisplayName();
								itemToPrinter += itemToPrinter2.getId();
							}
							else
							{
								itemToPrinterName += printer.getDisplayName() + ",";
								itemToPrinter += itemToPrinter2.getId() + ",";
							}
						}
					}
				}

				itemDetailDisplayPacket.setItemToPrinter(itemToPrinter);
				itemDetailDisplayPacket.setItemToPrinterName(itemToPrinterName);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public String getItemByLocationId(ItemByLocationIdPacket itemByLocationIdPacket, EntityManager em, HttpServletRequest httpRequest) throws Exception
	{
		try
		{
			List<ItemDetailDisplayPacket> ans = new ArrayList<ItemDetailDisplayPacket>();
			String sql = "select distinct i.id,i.name,i.short_name,i.price_selling,c.display_name  as course_name,i.image_name, "
					+ " i.display_name as item_display_name , co.display_name category_name , i.stock_uom , itt.name as item_type_name " + "  from items i left join course c on i.course_id=c.id "
					+ " left join category_items ci on ci.items_id =i.id " + " left join category co on co.id=ci.category_id " + " left join items_type itt on itt.id=i.item_type "
					+ "  where i.locations_id= ? " + "";
			if (itemByLocationIdPacket.getCategoryId() != null && !itemByLocationIdPacket.getCategoryId().isEmpty())
			{
				sql += " and ci.category_id in (" + itemByLocationIdPacket.getCategoryId() + ")";
			}

			sql += " and  i.status not in ('D','R') and i.display_name like '%" + itemByLocationIdPacket.getItemName() + "%'  limit " + itemByLocationIdPacket.getStartIndex() + ","
					+ itemByLocationIdPacket.getEndIndex();

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, itemByLocationIdPacket.getLocationId()).getResultList();

			for (Object[] objRow : resultList)
			{
				ItemDetailDisplayPacket detailDisplayPacket = new ItemDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setShortName((String) objRow[2]);
				detailDisplayPacket.setPrice((BigDecimal) objRow[3]);
				detailDisplayPacket.setCourseName((String) objRow[4]);
				detailDisplayPacket.setImageName((String) objRow[5]);
				detailDisplayPacket.setItemDisplayName((String) objRow[6]);
				detailDisplayPacket.setCategoryName((String) objRow[7]);
				detailDisplayPacket.setStockUom((String) objRow[8]);
				detailDisplayPacket.setItemTypeName((String) objRow[9]);
				ans.add(detailDisplayPacket);
			}

			for (ItemDetailDisplayPacket itemDetailDisplayPacket : ans)
			{
				List<ItemsToPrinter> itemToPrinters = getItemsToPrinter(em, itemDetailDisplayPacket.getId());
				String itemToPrinter = "";
				String itemToPrinterName = "";
				if (itemToPrinters == null || itemToPrinters.size() == 0)
				{
					try
					{
						String queryString = "select p from CategoryItem p where p.itemsId=? ";
						TypedQuery<CategoryItem> query = em.createQuery(queryString, CategoryItem.class).setParameter(1, itemDetailDisplayPacket.getId());
						CategoryItem categoryItem = query.getSingleResult();
						if (categoryItem != null)
						{
							List<CategoryToPrinter> toPrinters = getCategoryToPrinter(em, categoryItem.getCategoryId());
							for (int i = 0; i < toPrinters.size(); i++)
							{
								CategoryToPrinter categoryToPrinter = toPrinters.get(i);
								Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, categoryToPrinter.getPrintersId());
								if (printer != null)
								{
									if (i == (toPrinters.size() - 1))
									{
										itemToPrinterName += printer.getDisplayName();
										itemToPrinter += categoryToPrinter.getId();
									}
									else
									{
										itemToPrinterName += printer.getDisplayName() + ",";
										itemToPrinter += categoryToPrinter.getId() + ",";
									}
								}
							}
						}

					}
					catch (Exception e)
					{
						logger.severe("No Result found");
					}
				}
				else
				{
					for (int i = 0; i < itemToPrinters.size(); i++)
					{
						ItemsToPrinter itemToPrinter2 = itemToPrinters.get(i);
						Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, itemToPrinter2.getPrintersId());
						if (printer != null)
						{
							if (i == (itemToPrinters.size() - 1))
							{
								itemToPrinterName += printer.getDisplayName();
								itemToPrinter += itemToPrinter2.getId();
							}
							else
							{
								itemToPrinterName += printer.getDisplayName() + ",";
								itemToPrinter += itemToPrinter2.getId() + ",";
							}
						}
					}
				}

				itemDetailDisplayPacket.setItemToPrinter(itemToPrinter);
				itemDetailDisplayPacket.setItemToPrinterName(itemToPrinterName);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public String getRawMaterialItemByLocationId(ItemByLocationIdPacket itemByLocationIdPacket, EntityManager em, HttpServletRequest httpRequest) throws Exception
	{
		try
		{
			List<ItemDetailDisplayPacket> ans = new ArrayList<ItemDetailDisplayPacket>();
			String sql = "select distinct i.id,i.name,i.short_name,i.price_selling,c.display_name  as course_name,i.image_name, "
					+ " i.display_name as item_display_name , co.display_name category_name , i.stock_uom , itt.name as item_type_name " + "  from items i left join course c on i.course_id=c.id "
					+ " left join category_items ci on ci.items_id =i.id left join category co on co.id=ci.category_id " + " left join items_type itt on itt.id=i.item_type and itt.name!='Sale Only'"
					+ "  where i.locations_id= ? and itt.name !='Sale only'";
			if (itemByLocationIdPacket.getCategoryId() != null && !itemByLocationIdPacket.getCategoryId().isEmpty())
			{
				sql += "  and ci.id in (" + itemByLocationIdPacket.getCategoryId() + ")";
			}

			sql += "and i.status!= 'D' and i.display_name like '%" + itemByLocationIdPacket.getItemName() + "%'  limit " + itemByLocationIdPacket.getStartIndex() + ","
					+ itemByLocationIdPacket.getEndIndex();

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, itemByLocationIdPacket.getLocationId()).getResultList();

			for (Object[] objRow : resultList)
			{
				ItemDetailDisplayPacket detailDisplayPacket = new ItemDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setShortName((String) objRow[2]);
				detailDisplayPacket.setPrice((BigDecimal) objRow[3]);
				detailDisplayPacket.setCourseName((String) objRow[4]);
				detailDisplayPacket.setImageName((String) objRow[5]);
				detailDisplayPacket.setItemDisplayName((String) objRow[6]);
				detailDisplayPacket.setCategoryName((String) objRow[7]);
				detailDisplayPacket.setStockUom((String) objRow[8]);
				detailDisplayPacket.setItemTypeName((String) objRow[9]);
				ans.add(detailDisplayPacket);
			}

			for (ItemDetailDisplayPacket itemDetailDisplayPacket : ans)
			{
				List<ItemsToPrinter> itemToPrinters = getItemsToPrinter(em, itemDetailDisplayPacket.getId());
				String itemToPrinter = "";
				String itemToPrinterName = "";
				if (itemToPrinters == null || itemToPrinters.size() == 0)
				{
					try
					{
						String queryString = "select p from CategoryItem p where p.itemsId=? ";
						TypedQuery<CategoryItem> query = em.createQuery(queryString, CategoryItem.class).setParameter(1, itemDetailDisplayPacket.getId());
						CategoryItem categoryItem = query.getSingleResult();
						if (categoryItem != null)
						{
							List<CategoryToPrinter> toPrinters = getCategoryToPrinter(em, categoryItem.getCategoryId());
							for (int i = 0; i < toPrinters.size(); i++)
							{
								CategoryToPrinter categoryToPrinter = toPrinters.get(i);
								Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, categoryToPrinter.getPrintersId());
								if (printer != null)
								{
									if (i == (toPrinters.size() - 1))
									{
										itemToPrinterName += printer.getDisplayName();
										itemToPrinter += categoryToPrinter.getId();
									}
									else
									{
										itemToPrinterName += printer.getDisplayName() + ",";
										itemToPrinter += categoryToPrinter.getId() + ",";
									}
								}
							}
						}

					}
					catch (Exception e)
					{
						logger.severe("No Result found");
					}
				}
				else
				{
					for (int i = 0; i < itemToPrinters.size(); i++)
					{
						ItemsToPrinter itemToPrinter2 = itemToPrinters.get(i);
						Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, itemToPrinter2.getPrintersId());
						if (printer != null)
						{
							if (i == (itemToPrinters.size() - 1))
							{
								itemToPrinterName += printer.getDisplayName();
								itemToPrinter += itemToPrinter2.getId();
							}
							else
							{
								itemToPrinterName += printer.getDisplayName() + ",";
								itemToPrinter += itemToPrinter2.getId() + ",";
							}
						}
					}
				}

				itemDetailDisplayPacket.setItemToPrinter(itemToPrinter);
				itemDetailDisplayPacket.setItemToPrinterName(itemToPrinterName);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public String getRawMaterialItemByLocationIdAndInventoryAndSaleAndInventory(ItemByLocationIdPacket itemByLocationIdPacket, EntityManager em, HttpServletRequest httpRequest) throws Exception
	{
		try
		{
			List<ItemDetailDisplayPacket> ans = new ArrayList<ItemDetailDisplayPacket>();
			String temp = Utilities.convertAllSpecialCharForSearch(itemByLocationIdPacket.getItemName());

			String sql = "select distinct i.id,i.name,i.short_name,i.purchasing_rate,c.display_name as course_name,i.image_name, "
					+ " i.display_name as item_display_name , co.display_name category_name , i.stock_uom , itt.name as item_type_name " + " , i.sellable_uom " + "from items i "
					+ "left join course c on i.course_id=c.id " + " left join category_items ci on ci.items_id =i.id " + "left join category co on co.id=ci.category_id "
					+ " left join items_type itt on itt.id=i.item_type and itt.name!='Sale Only'";
			String categorylist =  itemByLocationIdPacket.getCategoryId();
			String newList="";
			if(categorylist!=null){
				String[] list = categorylist.split(",");
				
				for(int i =0;i<list.length;i++){
					if(i==(list.length-1)){
						newList += "'"+list[i]+"'";
					}else{
						newList += "'"+list[i]+"',";
					}
				}
			}
			
		 
			
			if (itemByLocationIdPacket.getSupplierId() != null)
			{
				sql = sql + " left join item_to_supplier itr on itr.item_id=i.id ";
			}

			sql = sql + "  where ";

			if (itemByLocationIdPacket.getSupplierId() != null)
			{
				sql = sql + " (itr.primary_supplier_id= '" + itemByLocationIdPacket.getSupplierId() + "'  or itr.secondary_supplier_id = " + "'" + itemByLocationIdPacket.getSupplierId()
						+ "' or itr.tertiary_supplier_id = " + "'" + itemByLocationIdPacket.getSupplierId() + "' ) and ";
			}
			sql = sql + "i.locations_id= ? and itt.name !='Sale only'";

			if (newList != null && !newList.isEmpty())
			{
				sql += "  and co.id in (" + newList + ")";
			}

			sql += " and i.status!= 'D' ";
			if (temp != null && !temp.isEmpty())
			{		
				sql += " and i.display_name like '%" + temp + "%'";
			}
			sql += "  limit  " + itemByLocationIdPacket.getStartIndex() + "," + itemByLocationIdPacket.getEndIndex();

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, itemByLocationIdPacket.getLocationId()).getResultList();

			for (Object[] objRow : resultList)
			{
				ItemDetailDisplayPacket detailDisplayPacket = new ItemDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setShortName((String) objRow[2]);
				detailDisplayPacket.setPrice((BigDecimal) objRow[3]);
				detailDisplayPacket.setCourseName((String) objRow[4]);
				detailDisplayPacket.setImageName((String) objRow[5]);
				detailDisplayPacket.setItemDisplayName((String) objRow[6]);
				detailDisplayPacket.setCategoryName((String) objRow[7]);
				detailDisplayPacket.setStockUom((String) objRow[8]);
				detailDisplayPacket.setItemTypeName((String) objRow[9]);
				detailDisplayPacket.setSellableUomId((String) objRow[10]);

				// set value of primarySupplierName,
				// sellableUomName,stockUomName;

				try
				{
					String queryString = "select its from ItemToSupplier its where its.itemId = ?";
					TypedQuery<ItemToSupplier> query = em.createQuery(queryString, ItemToSupplier.class).setParameter(1, (String) objRow[0]);
					ItemToSupplier itemToSupplier = query.getSingleResult();

					if (itemToSupplier != null && itemToSupplier.getPrimarySupplierId() != null)
					{

						Location supp = (Location) new CommonMethods().getObjectById("Location", em,Location.class, itemToSupplier.getPrimarySupplierId());

						if (supp != null && supp.getName() != null)
						{
							detailDisplayPacket.setPrimarySupplierName(supp.getName());
						}

					}
				}
				catch (Exception e)
				{
					logger.severe("No Supplier found");
					detailDisplayPacket.setPrimarySupplierName("");
				}

				try
				{
					UnitOfMeasurement stockUom = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, (String) objRow[8]);

					if (stockUom != null && stockUom.getName() != null)
					{
						detailDisplayPacket.setStockUomName(stockUom.getName());
					}

				}
				catch (Exception e)
				{
					logger.severe("No stockUom found");
					detailDisplayPacket.setPrimarySupplierName("");
				}

				try
				{
					UnitOfMeasurement sellableUom = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, (String) objRow[10]);

					if (sellableUom != null && sellableUom.getName() != null)
					{
						detailDisplayPacket.setSellableUomName(sellableUom.getName());
					}

				}
				catch (Exception e)
				{
					logger.severe("No stockUom found");
					detailDisplayPacket.setPrimarySupplierName("");
				}

				ans.add(detailDisplayPacket);
			}

			for (ItemDetailDisplayPacket itemDetailDisplayPacket : ans)
			{
				List<ItemsToPrinter> itemToPrinters = getItemsToPrinter(em, itemDetailDisplayPacket.getId());
				String itemToPrinter = "";
				String itemToPrinterName = "";
				if (itemToPrinters == null || itemToPrinters.size() == 0)
				{
					try
					{
						String queryString = "select p from CategoryItem p where p.itemsId=? ";
						TypedQuery<CategoryItem> query = em.createQuery(queryString, CategoryItem.class).setParameter(1, itemDetailDisplayPacket.getId());
						CategoryItem categoryItem = query.getSingleResult();
						if (categoryItem != null)
						{
							List<CategoryToPrinter> toPrinters = getCategoryToPrinter(em, categoryItem.getCategoryId());
							for (int i = 0; i < toPrinters.size(); i++)
							{
								CategoryToPrinter categoryToPrinter = toPrinters.get(i);
								Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, categoryToPrinter.getPrintersId());
								if (printer != null)
								{
									if (i == (toPrinters.size() - 1))
									{
										itemToPrinterName += printer.getDisplayName();
										itemToPrinter += categoryToPrinter.getId();
									}
									else
									{
										itemToPrinterName += printer.getDisplayName() + ",";
										itemToPrinter += categoryToPrinter.getId() + ",";
									}
								}
							}
						}

					}
					catch (Exception e)
					{
						logger.severe("No Result found");
					}
				}
				else
				{
					for (int i = 0; i < itemToPrinters.size(); i++)
					{
						ItemsToPrinter itemToPrinter2 = itemToPrinters.get(i);
						Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, itemToPrinter2.getPrintersId());
						if (printer != null)
						{
							if (i == (itemToPrinters.size() - 1))
							{
								itemToPrinterName += printer.getDisplayName();
								itemToPrinter += itemToPrinter2.getId();
							}
							else
							{
								itemToPrinterName += printer.getDisplayName() + ",";
								itemToPrinter += itemToPrinter2.getId() + ",";
							}
						}
					}
				}

				itemDetailDisplayPacket.setItemToPrinter(itemToPrinter);
				itemDetailDisplayPacket.setItemToPrinterName(itemToPrinterName);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public int getRawMaterialItemCountByLocationIdAndInventoryAndSaleAndInventory(ItemByLocationIdPacket itemByLocationIdPacket, EntityManager em, HttpServletRequest httpRequest) throws Exception
	{
		try
		{
			List<ItemDetailDisplayPacket> ans = new ArrayList<ItemDetailDisplayPacket>();

			/*
			 * String sql =
			 * "select distinct i.id,i.name,i.short_name,i.price_selling,c.display_name  as course_name,i.image_name, "
			 * +
			 * " i.display_name as item_display_name , co.display_name category_name , i.stock_uom , itt.name as item_type_name "
			 * + "  from items i left join course c on i.course_id=c.id " +
			 * " left join category_items ci on ci.items_id =i.id left join category co on co.id=ci.category_id "
			 * +
			 * " left join items_type itt on itt.id=i.item_type and itt.name!='Sale Only'"
			 * + "  where i.locations_id= ? and itt.name !='Sale only'"; if
			 * (itemByLocationIdPacket.getCategoryId() != null &&
			 * !itemByLocationIdPacket.getCategoryId().isEmpty()) { sql +=
			 * "  and co.id in (" + itemByLocationIdPacket.getCategoryId() +
			 * ")"; }
			 * 
			 * sql += "and i.status!= 'D' and i.display_name like '%" +
			 * itemByLocationIdPacket.getItemName() + "%'";
			 */
			String temp=itemByLocationIdPacket.getItemName();
			if(temp!=null){
			 temp = Utilities.convertAllSpecialCharForSearch(temp);
			}

			String sql = "select distinct i.id,i.name,i.short_name,i.purchasing_rate,c.display_name as course_name,i.image_name, "
					+ " i.display_name as item_display_name , co.display_name category_name , i.stock_uom , itt.name as item_type_name " + " , i.sellable_uom " + "from items i "
					+ "left join course c on i.course_id=c.id " + " left join category_items ci on ci.items_id =i.id " + "left join category co on co.id=ci.category_id "
					+ " left join items_type itt on itt.id=i.item_type and itt.name!='Sale Only'";

			String categorylist =  itemByLocationIdPacket.getCategoryId();
			String newList="";
			if(categorylist!=null){
				String[] list = categorylist.split(",");
				for(int i =0;i<list.length;i++){
					if(i==(list.length-1)){
						newList += "'"+list[i]+"'";
					}else{
						newList += "'"+list[i]+"',";
					}
					
				}
			}
			
			if (itemByLocationIdPacket.getSupplierId() != null)
			{
				sql = sql + " left join item_to_supplier itr on itr.item_id=i.id ";
			}

			sql = sql + "  where ";

			if (itemByLocationIdPacket.getSupplierId()!= null)
			{
				sql = sql + "(itr.primary_supplier_id= '" + itemByLocationIdPacket.getSupplierId() + "'  or " + "itr.secondary_supplier_id= '" + itemByLocationIdPacket.getSupplierId() + "'"
						+ " or itr.tertiary_supplier_id = '" + itemByLocationIdPacket.getSupplierId() + "'" + " ) and ";
			}
			sql = sql + "i.locations_id= ? and itt.name !='Sale only'";

			if (itemByLocationIdPacket.getCategoryId() != null && !itemByLocationIdPacket.getCategoryId().isEmpty())
			{
				sql += "  and co.id in (" + newList + ")";
			}

			sql += " and i.status!= 'D' " ;
			if (temp != null && !temp.isEmpty())
			{		
				sql += " and i.display_name like '%" + temp + "%'";
			}

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, itemByLocationIdPacket.getLocationId()).getResultList();

			for (Object[] objRow : resultList)
			{
				ItemDetailDisplayPacket detailDisplayPacket = new ItemDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setShortName((String) objRow[2]);
				detailDisplayPacket.setPrice((BigDecimal) objRow[3]);
				detailDisplayPacket.setCourseName((String) objRow[4]);
				detailDisplayPacket.setImageName((String) objRow[5]);
				detailDisplayPacket.setItemDisplayName((String) objRow[6]);
				detailDisplayPacket.setCategoryName((String) objRow[7]);
				detailDisplayPacket.setStockUom((String) objRow[8]);
				detailDisplayPacket.setItemTypeName((String) objRow[9]);
				ans.add(detailDisplayPacket);
			}

			return ans.size();
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public Category getChildCategory(String locationId, String category_id, EntityManager em, boolean isInsertNew) throws Exception
	{

		Category parentCategory = (Category) new CommonMethods().getObjectById("Category", em,Category.class, category_id); // 209
		logger.severe("category_id========================================================================="+category_id);
		logger.severe("locationId========================================================================="+locationId);
		
		if (parentCategory != null)
		{
			logger.severe("parentCategory.getCategoryId()========================================================================="+parentCategory.getCategoryId());
			if(parentCategory.getGlobalCategoryId()!=null && !parentCategory.getGlobalCategoryId().equals('0')){
				category_id=parentCategory.getGlobalCategoryId();
			}
			Category childCategoryTemp = null;
			// code for getting first level category
			if (parentCategory.getCategoryId()== null)
			{
				try
				{
					String queryString = "select s from Category s where s.globalCategoryId =? and (s.categoryId ='0' or s.categoryId is null) and s.locationsId=? and s.status !='D' ";
					TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, category_id).setParameter(2, locationId);
					childCategoryTemp = query.getSingleResult();
				}
				catch (Exception e)
				{
					logger.severe("------------- No Category found for globalCategoryId " + category_id + " locationsId " + locationId);
				}
				if (childCategoryTemp == null && isInsertNew)
				{
					Category categoryItemLocal = (Category) new CommonMethods().getObjectById("Category", em,Category.class, category_id);
					Category newCategoryItem = new Category();
					if (categoryItemLocal != null)
					{
						newCategoryItem = new Category().getCategory(categoryItemLocal);
						newCategoryItem.setLocationsId(locationId);
						newCategoryItem.setGlobalCategoryId(categoryItemLocal.getId());
						newCategoryItem.setId(new StoreForwardUtility().generateUUID());
						newCategoryItem = em.merge(newCategoryItem);
						childCategoryTemp = newCategoryItem;
					}

				}
				return childCategoryTemp;
			}
			else
			{
				boolean superParentCreated = false;
				boolean parentCreated = false;
				boolean superChildCreated = false;
				// child categoryId of category_id
				// code for getting second level category
				Category subCategory = (Category) new CommonMethods().getObjectById("Category", em,Category.class, parentCategory.getCategoryId());
				if (subCategory != null && subCategory.getCategoryId() == null)
				{

					// parent cat by global and location id specific
					try
					{
						String queryString;
						TypedQuery<Category> query;
						Category parentCategoryLocation = null;

						try
						{
							queryString = "select s from Category s where s.globalCategoryId =? and s.locationsId=? and s.status !='D' ";
							query = em.createQuery(queryString, Category.class).setParameter(1, parentCategory.getId()).setParameter(2, locationId);
							parentCategoryLocation = query.getSingleResult();
						}
						catch (Exception e1)
						{
							// TODO Auto-generated catch block
							logger.severe("-------------- No Sub Category found for globalCategoryId " + parentCategory.getId() + " locationsId " + locationId);
						}
						if (parentCategoryLocation == null && isInsertNew)
						{
							Category categoryItemLocal = (Category) new CommonMethods().getObjectById("Category", em,Category.class, parentCategory.getId());
							Category newCategoryItem = new Category();
							if (categoryItemLocal != null)
							{
								newCategoryItem = new Category().getCategory(categoryItemLocal);
								newCategoryItem.setLocationsId(locationId);
								newCategoryItem.setGlobalCategoryId(categoryItemLocal.getId());
								if(newCategoryItem.getId()==null){
									newCategoryItem.setId(new StoreForwardUtility().generateUUID());
								}
								newCategoryItem = em.merge(newCategoryItem);
								
								superParentCreated = true;
								childCategoryTemp = newCategoryItem;
							}

						}
						else
						{
							childCategoryTemp = parentCategoryLocation;
						}

						// Sub cat by global and location id specific
						Category subCategoryLocation = null;
						try
						{
							try
							{
								queryString = "select s from Category s where s.globalCategoryId =? and (s.categoryId = '0' or s.categoryId is null) and s.locationsId=? and s.status !='D' ";
								query = em.createQuery(queryString, Category.class).setParameter(1, subCategory.getId()).setParameter(2, locationId);
								subCategoryLocation = query.getSingleResult();
							}
							catch (Exception e1)
							{
								// TODO Auto-generated catch block
								logger.severe("-------------- No Sub Category found for globalCategoryId " + subCategory.getId() + " locationsId " + locationId);
							}
							if (subCategoryLocation == null && isInsertNew)
							{
								Category categoryItemLocal = (Category) new CommonMethods().getObjectById("Category", em,Category.class, parentCategory.getCategoryId());
								Category newCategoryItem = new Category();
								if (categoryItemLocal != null)
								{
									newCategoryItem = new Category().getCategory(categoryItemLocal);
									newCategoryItem.setLocationsId(locationId);
									newCategoryItem.setGlobalCategoryId(categoryItemLocal.getId());
									if(newCategoryItem!=null && newCategoryItem.getId()==null){
										newCategoryItem.setId(new StoreForwardUtility().generateUUID());
									}
									newCategoryItem = em.merge(newCategoryItem);
									subCategoryLocation = newCategoryItem;
									childCategoryTemp.setCategoryId(newCategoryItem.getId());
									childCategoryTemp = em.merge(childCategoryTemp);
									parentCreated = true;
								}

							}
							else if (superParentCreated)
							{
								childCategoryTemp.setCategoryId(subCategoryLocation.getId());
								childCategoryTemp = em.merge(childCategoryTemp);
							}
							return childCategoryTemp;

						}
						catch (Exception e)
						{

							logger.severe(e);

						}
					}
					catch (Exception e)
					{

						logger.severe(e);

					}
				}
				else
				{
					// sub sub cat entry code
					// parent cat by global and location id specific
					try
					{
						String queryString;
						TypedQuery<Category> query;
						Category parentCategoryLocation = null;
						try
						{
							queryString = "select s from Category s where s.globalCategoryId =? and s.locationsId=? and s.status !='D' ";
							query = em.createQuery(queryString, Category.class).setParameter(1, parentCategory.getId()).setParameter(2, locationId);
							parentCategoryLocation = query.getSingleResult();
						}
						catch (Exception e1)
						{
							// TODO Auto-generated catch block
							logger.severe("-------------- No Sub Category found for globalCategoryId " + parentCategory.getId() + " locationsId " + locationId);
						}

						if (parentCategoryLocation == null && isInsertNew)
						{
							Category categoryItemLocal = (Category) new CommonMethods().getObjectById("Category", em,Category.class, parentCategory.getId());
							Category newCategoryItem = new Category();
							if (categoryItemLocal != null)
							{
								newCategoryItem = new Category().getCategory(categoryItemLocal);
								newCategoryItem.setLocationsId(locationId);
								newCategoryItem.setGlobalCategoryId(categoryItemLocal.getId());
								newCategoryItem = em.merge(newCategoryItem);
								superParentCreated = true;
								childCategoryTemp = newCategoryItem;
							}

						}
						else
						{
							childCategoryTemp = parentCategoryLocation;
						}

						// Sub cat by global and location id specific
						Category subCategoryLocation = null;
						try
						{
							try
							{
								queryString = "select s from Category s where s.globalCategoryId =? and s.locationsId=? and s.status !='D' ";
								query = em.createQuery(queryString, Category.class).setParameter(1, subCategory.getId()).setParameter(2, locationId);
								subCategoryLocation = query.getSingleResult();
							}
							catch (Exception e1)
							{
								// TODO Auto-generated catch block
								logger.severe("-------------- No Sub Category found for globalCategoryId " + subCategory.getId() + " locationsId " + locationId);
							}
							if (subCategoryLocation == null && isInsertNew)
							{
								Category categoryItemLocal = (Category) new CommonMethods().getObjectById("Category", em,Category.class, parentCategory.getCategoryId());
								Category newCategoryItem = new Category();
								if (categoryItemLocal != null)
								{
									newCategoryItem = new Category().getCategory(categoryItemLocal);
									newCategoryItem.setLocationsId(locationId);
									newCategoryItem.setGlobalCategoryId(categoryItemLocal.getId());

									newCategoryItem = em.merge(newCategoryItem);
									subCategoryLocation = newCategoryItem;
									childCategoryTemp.setCategoryId(newCategoryItem.getId());
									childCategoryTemp = em.merge(childCategoryTemp);
									parentCreated = true;

								}

							}
							else if (superParentCreated)
							{
								childCategoryTemp.setCategoryId(subCategoryLocation.getId());
								childCategoryTemp = em.merge(childCategoryTemp);
							}

						}
						catch (Exception e)
						{

							logger.severe(e);

						}

						// Sub Sub cat by global and location id specific
						Category subSubCategoryLocation = null;
						try
						{
							try
							{
								queryString = "select s from Category s where s.globalCategoryId =? and s.categoryId = '0' and s.locationsId=? and s.status !='D' ";
								query = em.createQuery(queryString, Category.class).setParameter(1, subCategory.getCategoryId()).setParameter(2, locationId);
								subSubCategoryLocation = query.getSingleResult();
							}
							catch (Exception e1)
							{
								// TODO Auto-generated catch block
								logger.severe("-------------- No Sub Category found for globalCategoryId " + subCategory.getCategoryId() + " locationsId " + locationId);
							}
							if (subSubCategoryLocation == null && isInsertNew)
							{
								Category categoryItemLocal = (Category) new CommonMethods().getObjectById("Category", em,Category.class, subCategory.getCategoryId());
								Category newCategoryItem = new Category();
								if (categoryItemLocal != null)
								{
									newCategoryItem = new Category().getCategory(categoryItemLocal);
									newCategoryItem.setLocationsId(locationId);
									newCategoryItem.setGlobalCategoryId(categoryItemLocal.getId());
									newCategoryItem.setId(new StoreForwardUtility().generateUUID());
									newCategoryItem = em.merge(newCategoryItem);
									subSubCategoryLocation = newCategoryItem;

									subCategoryLocation.setCategoryId(subSubCategoryLocation.getId());
									subCategoryLocation = em.merge(subCategoryLocation);

								}

							}
							else if (parentCreated)
							{
								childCategoryTemp.setCategoryId(subSubCategoryLocation.getId());
								childCategoryTemp = em.merge(childCategoryTemp);
							}
							return childCategoryTemp;

						}
						catch (Exception e)
						{

							logger.severe(e);

						}
					}
					catch (Exception e)
					{

						logger.severe(e);

					}
				}
			}
			return parentCategory;
		}
		return null;
	}

	public List<ItemToSupplierPacket> getItemBySupplierId(EntityManager em, String id)
	{
		String sqlForGlobalItemId;
		List<ItemToSupplierPacket> itemToSupplierPacketList = new ArrayList<ItemToSupplierPacket>();
		
		if(id==null || id.equals("null") || id.equals("0")){
			 sqlForGlobalItemId = " select i.name,i.id,i.stock_uom,i.purchasing_rate,   " + " i.sales_tax_1, i.sales_tax_2,i.sales_tax_3, i.sales_tax_4 "
					+ "from items i where i.id in ( select item_id from item_to_supplier its join items i " + " on its.item_id =i.id  where (its.primary_supplier_id is null "
					+ " and its.secondary_supplier_id is null and its.tertiary_supplier_id is null ) and (i.global_item_id='0' or i.global_item_id is null) and  i.status!= 'D')";

		}else {
			 sqlForGlobalItemId = " select i.name,i.id,i.stock_uom,i.purchasing_rate,   " + " i.sales_tax_1, i.sales_tax_2,i.sales_tax_3, i.sales_tax_4 "
					+ "from items i where i.id in ( select item_id from item_to_supplier its join items i " + " on its.item_id =i.id  where (its.primary_supplier_id ='"+id+"'"
					+ " or its.secondary_supplier_id='"+id+"' or its.tertiary_supplier_id= '"+id+"' ) and (i.global_item_id='0' or i.global_item_id is null) and  i.status!= 'D')";
	
		}
		@SuppressWarnings("unchecked")
		List<Object[]> resultList2 = em.createNativeQuery(sqlForGlobalItemId).getResultList();
		for (Object[] obj : resultList2)
		{
			ItemToSupplierPacket detItemToSupplierPacket = new ItemToSupplierPacket();

			detItemToSupplierPacket.setUnitPrice((BigDecimal) obj[3]);
			detItemToSupplierPacket.setUnitTaxRate(calculateTax(em, (String) obj[4], (String) obj[5], (String) obj[6], (String) obj[7]));

			SalesTax tax = null;
			if ((String) obj[4] != null)
			{
				tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[4]);
				if (tax != null)
				{
					detItemToSupplierPacket.setTaxDisplayName1(tax.getDisplayName());
					detItemToSupplierPacket.setTaxName1(tax.getTaxName());
					detItemToSupplierPacket.setTaxRate1(tax.getRate());

				}
			}
			if ((String) obj[5] != null)
			{
				tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[5]);
				if (tax != null)
				{
					detItemToSupplierPacket.setTaxDisplayName2(tax.getDisplayName());
					detItemToSupplierPacket.setTaxName2(tax.getTaxName());
					detItemToSupplierPacket.setTaxRate2(tax.getRate());
				}
			}
			if ((String) obj[6] != null)
			{
				tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[6]);
				if (tax != null)
				{
					detItemToSupplierPacket.setTaxDisplayName3(tax.getDisplayName());
					detItemToSupplierPacket.setTaxName3(tax.getTaxName());
					detItemToSupplierPacket.setTaxRate3(tax.getRate());
				}
			}
			if ((String) obj[7] != null)
			{
				tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[7]);
				if (tax != null)
				{
					detItemToSupplierPacket.setTaxDisplayName4(tax.getDisplayName());
					detItemToSupplierPacket.setTaxName4(tax.getTaxName());
					detItemToSupplierPacket.setTaxRate4(tax.getRate());
				}
			}

			detItemToSupplierPacket.setItemName((String) obj[0]);
			detItemToSupplierPacket.setItemId((String) obj[1]);

			String name = "";
			if ((((String) obj[1])) !=null)
			{
				UnitOfMeasurement uom = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, (String) obj[2]);
				if (uom != null)
				{
					name = uom.getDisplayName();
				}
			}
			detItemToSupplierPacket.setUomName(name);
			detItemToSupplierPacket.setAvailableQty(getAvailableQuantityFromInventory(em, detItemToSupplierPacket.getItemId()));

			ItemToSupplier supplier = getItemToSupplierbyItemId(em, detItemToSupplierPacket.getItemId());
			if (supplier != null)
			{
				Location location1 = getLocationsById(em, supplier.getPrimarySupplierId());
				if (location1 != null)
				{
					detItemToSupplierPacket.setPrimarySupplierId(location1.getId());
					detItemToSupplierPacket.setPrimarySupplierName(location1.getName());
				}
				Location location2 = getLocationsById(em, supplier.getSecondarySupplierId());
				if (location2 != null)
				{
					detItemToSupplierPacket.setSecondarySupplierId(location2.getId());
					detItemToSupplierPacket.setSecondarySupplierName(location2.getName());
				}
				Location location3 = getLocationsById(em, supplier.getTertiarySupplierId());
				if (location3 != null)
				{
					detItemToSupplierPacket.setTertiarySupplierId(location3.getId());
					detItemToSupplierPacket.setTertiarySupplierName(location3.getName());
				}

				itemToSupplierPacketList.add(detItemToSupplierPacket);
			}
		}
		return itemToSupplierPacketList;
	}

	public List<ItemToSupplierPacket> getAllGlobalItemsByLocationsId(EntityManager em, String locationId, String supplierId)
	{
		List<ItemToSupplierPacket> itemToSupplierPacketList = new ArrayList<ItemToSupplierPacket>();
		String sqlForGlobalItemId = " select i.name,i.id,i.stock_uom,i.purchasing_rate,   " + " i.sales_tax_1, i.sales_tax_2,i.sales_tax_3, i.sales_tax_4, i.locations_id "
				+ "from items i where i.id in ( select item_id from item_to_supplier its join items i " + " on its.item_id =i.id  where (i.global_item_id = '0' or i.global_item_id is null) and i.status!= 'D')";

		@SuppressWarnings("unchecked")
		List<Object[]> resultList2 = em.createNativeQuery(sqlForGlobalItemId).getResultList();
		for (Object[] obj : resultList2)
		{
			ItemToSupplierPacket detItemToSupplierPacket = new ItemToSupplierPacket();

			detItemToSupplierPacket.setUnitPrice((BigDecimal) obj[3]);
			detItemToSupplierPacket.setUnitTaxRate(calculateTax(em, (String) obj[4], (String) obj[5], (String) obj[6], (String) obj[7]));

			detItemToSupplierPacket.setItemName((String) obj[0]);
			detItemToSupplierPacket.setItemId((String) obj[1]);

			String name = "";
			if ((((String) obj[1]))!=null)
			{
				UnitOfMeasurement uom = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, (String) obj[2]);
				if (uom != null)
				{
					name = uom.getDisplayName();
				}
			}
			detItemToSupplierPacket.setUomName(name);
			detItemToSupplierPacket.setAvailableQty(getAvailableQuantityFromInventory(em, detItemToSupplierPacket.getItemId()));

			String queryStringItems = "select s from Item s where s.globalItemId ='"+detItemToSupplierPacket.getItemId()+"' and s.locationsId = '"+locationId+"' and s.status!='D' ";
			
			TypedQuery<Item> queryItems = em.createQuery(queryStringItems, Item.class);
			Item itemAdapter = queryItems.getSingleResult();

			BigDecimal convertedUnit = convertUnit(itemAdapter.getSellableUom(), itemAdapter.getStockUom(), logger, getAvailableQuantityFromInventory(em, itemAdapter.getId()), em);
			detItemToSupplierPacket.setCurrentAvailableQty(convertedUnit);

			ItemToSupplier supplier = getItemToSupplierbyItemId(em, detItemToSupplierPacket.getItemId());
			if (supplier != null)
			{
				Location location1 = getLocationsById(em, supplier.getPrimarySupplierId());
				if (location1 != null)
				{
					detItemToSupplierPacket.setPrimarySupplierId(location1.getId());
					detItemToSupplierPacket.setPrimarySupplierName(location1.getName());
				}
				Location location2 = getLocationsById(em, supplier.getSecondarySupplierId());
				if (location2 != null)
				{
					detItemToSupplierPacket.setSecondarySupplierId(location2.getId());
					detItemToSupplierPacket.setSecondarySupplierName(location2.getName());
				}
				Location location3 = getLocationsById(em, supplier.getTertiarySupplierId());
				if (location3 != null)
				{
					detItemToSupplierPacket.setTertiarySupplierId(location3.getId());
					detItemToSupplierPacket.setTertiarySupplierName(location3.getName());
				}

				itemToSupplierPacketList.add(detItemToSupplierPacket);

			}
		}
		return itemToSupplierPacketList;
	}

	private BigDecimal calculateTax(EntityManager em, String salesTax1, String salesTax2, String salesTax3, String salesTax4)
	{
		BigDecimal totalTax = new BigDecimal(0);
		SalesTax tax = null;
		if (salesTax1 != null)
		{
			tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, salesTax1);
			if (tax != null)
			{
				totalTax = totalTax.add(tax.getRate());
			}
		}
		if (salesTax2 != null)
		{
			tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, salesTax2);
			if (tax != null)
			{
				totalTax = totalTax.add(tax.getRate());
			}
		}
		if (salesTax3 != null)
		{
			tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, salesTax3);
			if (tax != null)
			{
				totalTax = totalTax.add(tax.getRate());
			}
		}
		if (salesTax4 != null)
		{
			tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, salesTax4);
			if (tax != null)
			{
				totalTax = totalTax.add(tax.getRate());
			}
		}

		return totalTax;
	}

	public List<ItemToSupplierPacket> getItemBySupplierIdAndLocationId(EntityManager em, String suppid, String locationId)
	{
		List<ItemToSupplierPacket> itemToSupplierPacketList = new ArrayList<ItemToSupplierPacket>();
		String sqlForGlobalItemId;
		if(suppid==null || suppid.equals("null")){
			 sqlForGlobalItemId = " select i.name,i.id,i.stock_uom ,i.locations_id," + "i.purchasing_rate,   "
						+ " i.sales_tax_1, i.sales_tax_2,i.sales_tax_3, i.sales_tax_4, i.distribution_price, i.sellable_uom," + "i.global_item_id "
						+ "from items i where i.id in ( select item_id from item_to_supplier its join items i " + " on its.item_id =i.id  where ( its.primary_supplier_id is null and its.secondary_supplier_id is null"
						+ " and its.tertiary_supplier_id is null ) and i.status!= 'D')";

		}else {
			 sqlForGlobalItemId = " select i.name,i.id,i.stock_uom ,i.locations_id," + "i.purchasing_rate,   "
						+ " i.sales_tax_1, i.sales_tax_2,i.sales_tax_3, i.sales_tax_4, i.distribution_price, i.sellable_uom," + "i.global_item_id "
						+ "from items i where i.id in ( select item_id from item_to_supplier its join items i " + " on its.item_id =i.id  where ( its.primary_supplier_id ='"+suppid+"' or its.secondary_supplier_id ='"+suppid+"'"
						+ " or its.tertiary_supplier_id ='"+suppid+"' ) and i.status!= 'D')";

		}
		Location supplierTemp =null;
		if(suppid!=null){
		 supplierTemp = (Location) new CommonMethods().getObjectById("Location", em,Location.class, suppid);
	     }

		@SuppressWarnings("unchecked")
		List<Object[]> resultList2 = em.createNativeQuery(sqlForGlobalItemId).getResultList();
		for (Object[] obj : resultList2)
		{

			
			if (((String) obj[3]).equals(locationId) && (supplierTemp != null && (supplierTemp.getLocationsTypeId() == 4 || supplierTemp.getLocationsTypeId() == 3)))
			{

				ItemToSupplierPacket detItemToSupplierPacket = new ItemToSupplierPacket();
				detItemToSupplierPacket.setItemName((String) obj[0]);
				detItemToSupplierPacket.setItemId((String) obj[1]);

				detItemToSupplierPacket.setUnitPrice((BigDecimal) obj[4]);
				detItemToSupplierPacket.setUnitTaxRate(calculateTax(em, (String) obj[5], (String) obj[6], (String) obj[7], (String) obj[8]));

				SalesTax tax = null;
				if ((String) obj[5] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[5]);
					if (tax != null)
					{
						detItemToSupplierPacket.setTaxDisplayName1(tax.getDisplayName());
						detItemToSupplierPacket.setTaxName1(tax.getTaxName());
						detItemToSupplierPacket.setTaxRate1(tax.getRate());

					}
				}
				if ((String) obj[6] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[6]);
					if (tax != null)
					{
						detItemToSupplierPacket.setTaxDisplayName2(tax.getDisplayName());
						detItemToSupplierPacket.setTaxName2(tax.getTaxName());
						detItemToSupplierPacket.setTaxRate2(tax.getRate());
					}
				}
				if ((String) obj[7] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[7]);
					if (tax != null)
					{
						detItemToSupplierPacket.setTaxDisplayName3(tax.getDisplayName());
						detItemToSupplierPacket.setTaxName3(tax.getTaxName());
						detItemToSupplierPacket.setTaxRate3(tax.getRate());
					}
				}
				if ((String) obj[8] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[8]);
					if (tax != null)
					{
						detItemToSupplierPacket.setTaxDisplayName4(tax.getDisplayName());
						detItemToSupplierPacket.setTaxName4(tax.getTaxName());
						detItemToSupplierPacket.setTaxRate4(tax.getRate());
					}
				}

				String name = "";
				if ((((String) obj[1])) != null)
				{
					UnitOfMeasurement uom = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, (String) obj[2]);
					if (uom != null)
					{
						name = uom.getDisplayName();
					}
				}
				detItemToSupplierPacket.setUomName(name);

				BigDecimal avaibleQuantity = getAvailableQuantityFromInventory(em, detItemToSupplierPacket.getItemId());
				Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, detItemToSupplierPacket.getItemId());

				UnitConversion unitConversion = getUnitConversionByFromIdAndToId(em, item.getSellableUom(), item.getStockUom(), logger);

				if (unitConversion != null)
				{
					avaibleQuantity = avaibleQuantity.multiply(unitConversion.getConversionRatio());
				}
				detItemToSupplierPacket.setAvailableQty(avaibleQuantity);

				// detItemToSupplierPacket
				// .setAvailableQty(getAvailableQuantityFromInventory(em,
				// detItemToSupplierPacket.getItemId()));

				ItemToSupplier supplier = getItemToSupplierbyItemId(em, detItemToSupplierPacket.getItemId());
				if (supplier != null)
				{
					Location location1 = getLocationsById(em, supplier.getPrimarySupplierId());
					if (location1 != null)
					{
						detItemToSupplierPacket.setPrimarySupplierId(location1.getId());
						detItemToSupplierPacket.setPrimarySupplierName(location1.getName());
					}
					Location location2 = getLocationsById(em, supplier.getSecondarySupplierId());
					if (location2 != null)
					{
						detItemToSupplierPacket.setSecondarySupplierId(location2.getId());
						detItemToSupplierPacket.setSecondarySupplierName(location2.getName());
					}
					Location location3 = getLocationsById(em, supplier.getTertiarySupplierId());
					if (location3 != null)
					{
						detItemToSupplierPacket.setTertiarySupplierId(location3.getId());
						detItemToSupplierPacket.setTertiarySupplierName(location3.getName());
					}

					detItemToSupplierPacket.setDistributionPrice((BigDecimal) obj[9]);
					itemToSupplierPacketList.add(detItemToSupplierPacket);
				}
			}
			else if (supplierTemp != null && supplierTemp.getLocationsTypeId() == 1)
			{
				if (((String) obj[3]).equals(locationId))
				{

					ItemToSupplierPacket detItemToSupplierPacket = new ItemToSupplierPacket();

					// source
					BigDecimal convertedUnit = convertUnit(((String) obj[10]), ((String) obj[2]), logger, getAvailableQuantityFromInventory(em, (String) obj[1]), em);
					// detItemToSupplierPacket.setCurrentAvailableQty(getAvailableQuantityFromInventory(em,
					// (int) obj[1]));
					detItemToSupplierPacket.setCurrentAvailableQty(convertedUnit);

					String queryStringItems = "select s from Item s where s.globalItemId =? and s.locationsId = ? and s.status!='D'";
					TypedQuery<Item> queryItems = em.createQuery(queryStringItems, Item.class).setParameter(1, (int) obj[11]).setParameter(2, suppid);
					Item itemAdapter = queryItems.getSingleResult();

					// Destination
					convertedUnit = convertUnit(itemAdapter.getSellableUom(), itemAdapter.getStockUom(), logger, getAvailableQuantityFromInventory(em, itemAdapter.getId()), em);
					// detItemToSupplierPacket.setAvailableQty(getAvailableQuantityFromInventory(em,
					// itemAdapter.getId()));
					detItemToSupplierPacket.setAvailableQty(convertedUnit);

					detItemToSupplierPacket.setCategoryName(getCategoryNameByItemId(em, (String) obj[1], locationId));
					detItemToSupplierPacket.setItemName((String) obj[0]);
					detItemToSupplierPacket.setItemId((String) obj[1]);

					detItemToSupplierPacket.setUnitPrice((BigDecimal) obj[9]);
					detItemToSupplierPacket.setUnitTaxRate(calculateTax(em, (String) obj[5], (String) obj[6], (String) obj[7], (String) obj[8]));

					String name = "";
					if ((((String) obj[1])) !=null)
					{
						UnitOfMeasurement uom = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, (String) obj[2]);
						if (uom != null)
						{
							name = uom.getDisplayName();
						}
					}
					detItemToSupplierPacket.setUomName(name);

					ItemToSupplier supplier = getItemToSupplierbyItemId(em, detItemToSupplierPacket.getItemId());
					if (supplier != null)
					{
						Location location1 = getLocationsById(em, supplier.getPrimarySupplierId());
						if (location1 != null)
						{
							detItemToSupplierPacket.setPrimarySupplierId(location1.getId());
							detItemToSupplierPacket.setPrimarySupplierName(location1.getName());
						}
						Location location2 = getLocationsById(em, supplier.getSecondarySupplierId());
						if (location2 != null)
						{
							detItemToSupplierPacket.setSecondarySupplierId(location2.getId());
							detItemToSupplierPacket.setSecondarySupplierName(location2.getName());
						}
						Location location3 = getLocationsById(em, supplier.getTertiarySupplierId());
						if (location3 != null)
						{
							detItemToSupplierPacket.setTertiarySupplierId(location3.getId());
							detItemToSupplierPacket.setTertiarySupplierName(location3.getName());
						}

						detItemToSupplierPacket.setDistributionPrice((BigDecimal) obj[9]);

						itemToSupplierPacketList.add(detItemToSupplierPacket);
					}
				}
			}

		}
		return itemToSupplierPacketList;
	}

	public List<ItemToSupplierPacket> getItemByLocationIdSelfIntraProduction(EntityManager em, String locationId)
	{

		List<ItemToSupplierPacket> itemToSupplierPacketList = new ArrayList<ItemToSupplierPacket>();
		String sqlForGlobalItemId = " select i.name,i.id,i.stock_uom ,i.locations_id," + "i.purchasing_rate,   "
				+ " i.sales_tax_1, i.sales_tax_2,i.sales_tax_3, i.sales_tax_4, i.distribution_price, i.sellable_uom "
				+ "from items i where i.id in ( select item_id from item_to_supplier its join items i " + " on its.item_id =i.id  where i.locations_id = ? and   i.status!= 'D')";

		@SuppressWarnings("unchecked")
		List<Object[]> resultList2 = em.createNativeQuery(sqlForGlobalItemId).setParameter(1, locationId).getResultList();
		for (Object[] obj : resultList2)
		{

			ItemToSupplierPacket detItemToSupplierPacket = new ItemToSupplierPacket();
			detItemToSupplierPacket.setItemName((String) obj[0]);
			detItemToSupplierPacket.setItemId((String) obj[1]);

			detItemToSupplierPacket.setUnitPrice((BigDecimal) obj[4]);
			detItemToSupplierPacket.setUnitTaxRate(calculateTax(em, (String) obj[5], (String) obj[6], (String) obj[7], (String) obj[8]));

			String name = "";
			if ((((int) obj[1])) > 0)
			{
				UnitOfMeasurement uom = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, (String) obj[2]);
				if (uom != null)
				{
					name = uom.getDisplayName();
				}
			}
			detItemToSupplierPacket.setUomName(name);

			BigDecimal convertedUnit = convertUnit(((String) obj[10]), ((String) obj[2]), logger, getAvailableQuantityFromInventory(em, detItemToSupplierPacket.getItemId()), em);

			// detItemToSupplierPacket
			// .setAvailableQty(getAvailableQuantityFromInventory(em,
			// detItemToSupplierPacket.getItemId()));

			detItemToSupplierPacket.setAvailableQty(convertedUnit);
			detItemToSupplierPacket.setCategoryName(getCategoryNameByItemId(em, (String) obj[1], locationId));

			convertedUnit = convertUnit(((String) obj[10]), ((String) obj[2]), logger, getAvailableQuantityFromInventory(em, detItemToSupplierPacket.getItemId()), em);

			detItemToSupplierPacket.setCurrentAvailableQty(convertedUnit);
			// detItemToSupplierPacket.setCurrentAvailableQty(getAvailableQuantityFromInventory(em,
			// detItemToSupplierPacket.getItemId()));

			ItemToSupplier supplier = getItemToSupplierbyItemId(em, detItemToSupplierPacket.getItemId());
			if (supplier != null)
			{
				Location location1 = getLocationsById(em, supplier.getPrimarySupplierId());
				if (location1 != null)
				{
					detItemToSupplierPacket.setPrimarySupplierId(location1.getId());
					detItemToSupplierPacket.setPrimarySupplierName(location1.getName());
				}
				Location location2 = getLocationsById(em, supplier.getSecondarySupplierId());
				if (location2 != null)
				{
					detItemToSupplierPacket.setSecondarySupplierId(location2.getId());
					detItemToSupplierPacket.setSecondarySupplierName(location2.getName());
				}
				Location location3 = getLocationsById(em, supplier.getTertiarySupplierId());
				if (location3 != null)
				{
					detItemToSupplierPacket.setTertiarySupplierId(location3.getId());
					detItemToSupplierPacket.setTertiarySupplierName(location3.getName());
				}

				detItemToSupplierPacket.setDistributionPrice((BigDecimal) obj[9]);
				itemToSupplierPacketList.add(detItemToSupplierPacket);
			}

		}
		return itemToSupplierPacketList;
	}

	/*
	 * public List<ItemToSupplierPacket>
	 * getItemByLocationIdProductionMGT(EntityManager em, String locationId) {
	 * 
	 * List<ItemToSupplierPacket> itemToSupplierPacketList = new
	 * ArrayList<ItemToSupplierPacket>(); String sqlForGlobalItemId =
	 * " select i.name,i.id,i.stock_uom ,i.locations_id,i.purchasing_rate, " +
	 * " i.sales_tax_1, i.sales_tax_2,i.sales_tax_3, i.sales_tax_4, i.distribution_price, "
	 * + " i.sellable_uom, i.global_item_id , " +
	 * " c.name as category_name ,uom.display_name  " + " from items i " +
	 * " left join category_items ci on ci.items_id = i.id " +
	 * " left join category c on c.id = ci.category_id "
	 * +" left join unit_of_measurement uom on uom.id = i.stock_uom " +
	 * " where c.status not in ('I','D') and c.locations_id = ? and i.id in " +
	 * " ( select item_id from item_to_supplier its " +
	 * " join items i on its.item_id =i.id " +
	 * " where i.locations_id = ? and   i.status!= 'D') " + "";
	 * 
	 * 
	 * 
	 * @SuppressWarnings("unchecked") List<Object[]> resultList2 =
	 * em.createNativeQuery(sqlForGlobalItemId).setParameter(1, locationId)
	 * .setParameter(2, locationId).getResultList();
	 * 
	 * CriteriaBuilder builder = em.getCriteriaBuilder();
	 * CriteriaQuery<Location> cl = builder.createQuery(Location.class);
	 * Root<Location> l = cl.from(Location.class); TypedQuery<Location> query =
	 * em.createQuery(cl.select(l).where( builder.and(
	 * builder.equal(l.get(Location_.isGlobalLocation), 0),
	 * builder.equal(l.get(Location_.locationsTypeId), 1),
	 * builder.equal(l.get(Location_.locationsId), 0),
	 * builder.notEqual(l.get(Location_.id), locationId)))); List<Location>
	 * locations = query.getResultList();
	 * 
	 * String locationArray = ""; for(Location printer : locations) {
	 * if(locationArray.length() != 0 ) { locationArray = locationArray + ","+
	 * printer.getId(); }else { locationArray = locationArray + printer.getId();
	 * }
	 * 
	 * }
	 * 
	 * for (Object[] obj : resultList2) {
	 * 
	 * 
	 * 
	 * ItemToSupplierPacket detItemToSupplierPacket = new
	 * ItemToSupplierPacket(); detItemToSupplierPacket.setItemName((String)
	 * obj[0]); detItemToSupplierPacket.setItemId((int) obj[1]);
	 * 
	 * 
	 * //detItemToSupplierPacket.setUnitPrice((BigDecimal) obj[4]);
	 * //detItemToSupplierPacket // .setUnitTaxRate(calculateTax(em, (int)
	 * obj[5], (int) obj[6], (int) obj[7], (int) obj[8]));
	 * 
	 * String name = ""; if ((((int) obj[1])) > 0) { UnitOfMeasurement uom =
	 * (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, (int) obj[2]); if (uom != null) { name =
	 * uom.getDisplayName(); } } detItemToSupplierPacket.setUomName((String)
	 * obj[13]);
	 * 
	 * BigDecimal convertedUnit = convertUnit(((int) obj[10]), ((int) obj[2]),
	 * logger, getAvailableQuantityFromInventory(em,
	 * detItemToSupplierPacket.getItemId()), em);
	 * detItemToSupplierPacket.setAvailableQty(convertedUnit);
	 * //detItemToSupplierPacket.setCategoryName(getCategoryNameByItemId(em,
	 * (int) obj[1],locationId));
	 * detItemToSupplierPacket.setCategoryName((String) obj[12]);
	 * 
	 * //convertedUnit = convertUnit(((int) obj[10]), ((int) obj[2]), logger,
	 * getAvailableQuantityFromInventory(em,
	 * detItemToSupplierPacket.getItemId()), em);
	 * detItemToSupplierPacket.setCurrentAvailableQty(convertedUnit);
	 * 
	 * 
	 * 
	 * detItemToSupplierPacket.setRequestedFromAllLocationproductionQty(
	 * requestedFromAllLocationProductdionQty(em,
	 * locationId,detItemToSupplierPacket.getItemId(), (int) obj[11],
	 * locationArray));
	 * detItemToSupplierPacket.setProductionQty(inProductionQty(em,
	 * locationId,detItemToSupplierPacket.getItemId()));
	 * 
	 * ItemToSupplier supplier = getItemToSupplierbyItemId(em,
	 * detItemToSupplierPacket.getItemId()); if (supplier != null) { Location
	 * location1 = getLocationsById(em, supplier.getPrimarySupplierId()); if
	 * (location1 != null) {
	 * detItemToSupplierPacket.setPrimarySupplierId(location1.getId());
	 * detItemToSupplierPacket.setPrimarySupplierName(location1.getName()); }
	 * Location location2 = getLocationsById(em,
	 * supplier.getSecondarySupplierId()); if (location2 != null) {
	 * detItemToSupplierPacket.setSecondarySupplierId(location2.getId());
	 * detItemToSupplierPacket.setSecondarySupplierName(location2.getName()); }
	 * Location location3 = getLocationsById(em,
	 * supplier.getTertiarySupplierId()); if (location3 != null) {
	 * detItemToSupplierPacket.setTertiarySupplierId(location3.getId());
	 * detItemToSupplierPacket.setTertiarySupplierName(location3.getName()); }
	 * 
	 * detItemToSupplierPacket.setDistributionPrice((BigDecimal) obj[9]);
	 * 
	 * } itemToSupplierPacketList.add(detItemToSupplierPacket);
	 * 
	 * 
	 * } return itemToSupplierPacketList; }
	 */

	public List<ItemToSupplierPacket> getItemByLocationIdProductionMGT(EntityManager em, String locationId)
	{

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getItemByLocationIdProductionManagement(?)").setParameter(1, locationId).getResultList();
		List<ItemToSupplierPacket> itemToSupplierPacketList = new ArrayList<ItemToSupplierPacket>();
		for (Object[] obj : resultList)
		{

			ItemToSupplierPacket detItemToSupplierPacket = new ItemToSupplierPacket();
			detItemToSupplierPacket.setItemId((String) obj[0]);
			detItemToSupplierPacket.setItemName((String) obj[1]);
			detItemToSupplierPacket.setCategoryName((String) obj[8]);
			detItemToSupplierPacket.setUomName((String) obj[9]);
			if ((Double) obj[10] != null)
			{
				detItemToSupplierPacket.setRequestedFromAllLocationproductionQty(new BigDecimal((Double) obj[10]));
			}
			else
			{
				detItemToSupplierPacket.setRequestedFromAllLocationproductionQty(new BigDecimal(0));
			}

			if ((Double) obj[11] != null)
			{
				detItemToSupplierPacket.setProductionQty(new BigDecimal((Double) obj[11]));
			}
			else
			{
				detItemToSupplierPacket.setProductionQty(new BigDecimal(0));
			}

			BigDecimal convertedUnit = new BigDecimal(0);
			if (((String) obj[14]) != null && ((String) obj[12]) != null)
			{
				convertedUnit = convertUnit(((String) obj[14]), ((String) obj[12]), logger, getAvailableQuantityFromInventory(em, detItemToSupplierPacket.getItemId()), em);
			}

			detItemToSupplierPacket.setAvailableQty(convertedUnit);
			detItemToSupplierPacket.setCurrentAvailableQty(convertedUnit);
			itemToSupplierPacketList.add(detItemToSupplierPacket);

		}
		return itemToSupplierPacketList;
	}

	@SuppressWarnings("unused")
	private BigDecimal requestedFromAllLocationProductdionQty(EntityManager em, String locationId, int itemsId, String globalId, String locationArray)
	{
		BigDecimal bigDecimal = new BigDecimal(0);
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Location> cl = builder.createQuery(Location.class);
			Root<Location> l = cl.from(Location.class);
			TypedQuery<Location> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Location_.isGlobalLocation), 0), builder.equal(l.get(Location_.locationsTypeId), 1),
					builder.equal(l.get(Location_.locationsId), 0), builder.notEqual(l.get(Location_.id), locationId))));
			List<Location> locations = query.getResultList();

			String array = "";
			for (Location printer : locations)
			{
				if (array.length() != 0)
				{
					array = array + "," + printer.getId();
				}
				else
				{
					array = array + printer.getId();
				}

			}

			List<Item> itemAdapter;
			try
			{
				String queryStringItems = "select s from Item s where s.globalItemId =" + globalId + " and s.locationsId in(" + locationArray + ")";
				TypedQuery<Item> queryItems = em.createQuery(queryStringItems, Item.class);
				itemAdapter = queryItems.getResultList();

			}
			catch (Exception e)
			{
				// TODO: handle exception
				logger.severe("No Result Found");
				return bigDecimal;
			}

			String itemsArray = "";
			for (Item printer : itemAdapter)
			{
				if (itemsArray.length() != 0)
				{
					itemsArray = itemsArray + "," + printer.getId();
				}
				else
				{
					itemsArray = itemsArray + printer.getId();
				}

			}

			@SuppressWarnings("unchecked")
			Object[] resultList = null;
			try
			{
				String sql = "select odi.items_id, sum(odi.quantity - odi.allotment_qty), " + " odi.request_id " + " from request_order_detail_items odi"
						+ " left join request_order oh on oh.id = odi.request_id" + " left join order_status os on os.id = oh.status_id"
						+ " where os.name in ('PO Created','PO Partially Received','Request Partially Processed')" + " and oh.supplier_id = '" + locationId + "' and odi.items_id in (" + itemsArray
						+ ") group by odi.items_id ";
				resultList = (Object[]) em.createNativeQuery(sql).getSingleResult();

			}
			catch (Exception e)
			{
				logger.severe(e + " ===== No Result found (Request) For locationId " + locationId + " itemsId " + itemsId);
			}

			if (resultList != null && resultList[1] != null)
			{
				bigDecimal = ((BigDecimal) resultList[1]);

			}

		}
		catch (Exception e)
		{
			logger.severe(e + " ----- No Result found For locationId " + locationId + " itemsId " + itemsId);

		}

		return bigDecimal;
	}

	@SuppressWarnings("unused")
	private BigDecimal inProductionQty(EntityManager em, String locationId, int itemsId)
	{
		BigDecimal bigDecimal = new BigDecimal(0);
		try
		{
			@SuppressWarnings("unchecked")
			Object[] resultList1 = null;
			try
			{
				/*
				 * String sql =
				 * "select odi.items_id, odi.items_qty, odi.id, odi.order_header_id, SUM(items_qty) ,odi.items_short_name "
				 * + "from order_detail_items odi" +
				 * "	left join order_header oh on oh.id = odi.order_header_id  "
				 * + "left join order_status os on os.id = oh.order_status_id "
				 * +
				 * "left join order_detail_status ods on ods.id = odi.order_detail_status_id "
				 * + "	left join order_type ot on ot.id = oh.order_type_id " +
				 * "	where ot.name in ('Production')" +
				 * "	and os.name not in ('Ready to Order','Reopen','Cancel Order','Close Production')"
				 * + "	and ods.name not in ('Item Removed','Recall') " +
				 * "and oh.locations_id in (select id from locations where id = ?)"
				 * +
				 * "	and odi.id not in (select order_detail_item_id from kds_to_order_detail_item_status where status_id in (select id from order_detail_status where name ='Item Ready'))"
				 * + "	and odi.items_id = ?";
				 */
				String sql = " select  odi.items_id, odi.items_qty, odi.id, odi.order_header_id, SUM(items_qty) ,odi.items_short_name " + " from order_detail_items odi "
						+ " left join order_header oh on oh.id = odi.order_header_id " + " left join order_status os on os.id = oh.order_status_id "
						+ " left join order_detail_status ods on ods.id = odi.order_detail_status_id " + " left join kds_to_order_detail_item_status kdsodi on odi.id=kdsodi.order_detail_item_id "
						+ " left join order_detail_status odss on kdsodi.status_id=odss.id " + " where oh.order_type_id = 2"
						+ " and os.name not in ('Ready to Order','Reopen','Cancel Order','Close Production') " + "	and ods.name not in ('Item Removed','Recall') " + "	and oh.locations_id = ? "
						+ "	and odi.items_id = ? " + "	and (odss.name is null or odss.name not in ('Item Ready')) ";
				resultList1 = (Object[]) em.createNativeQuery(sql).setParameter(1, locationId).setParameter(2, itemsId).getSingleResult();

			}
			catch (Exception e)
			{
				logger.severe(e + " ++++ No Result found (Request) For locationId " + locationId + " itemsId " + itemsId);
			}

			if (resultList1 != null && resultList1[4] != null)
			{
				bigDecimal = ((BigDecimal) resultList1[4]);
			}

		}
		catch (Exception e)
		{

			logger.severe(e + " ----- No Result found For locationId " + locationId + " itemsId " + itemsId);

		}

		return bigDecimal;
	}

	Location getLocationsById(EntityManager em, String id)
	{

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Location> cl = builder.createQuery(Location.class);
			Root<Location> l = cl.from(Location.class);
			TypedQuery<Location> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Location_.id), id))));

			return query.getSingleResult();
		}
		catch (Exception e)
		{

			logger.severe("No Result found");
		}
		return null;
	}

	List<ItemToSupplier> getItemToSupplierByPrimarySupplier(EntityManager em, String id)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ItemToSupplier> cl = builder.createQuery(ItemToSupplier.class);
		Root<ItemToSupplier> l = cl.from(ItemToSupplier.class);
		TypedQuery<ItemToSupplier> query = em.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(ItemToSupplier_.primarySupplierId), id))));

		return query.getResultList();

	}

	public BigDecimal getConvertedAvailableQuantityFromInventory(EntityManager em, String itemId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Inventory> criteria = builder.createQuery(Inventory.class);
			Root<Inventory> r = criteria.from(Inventory.class);
			TypedQuery<Inventory> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Inventory_.itemId), itemId)));
			Inventory inventory = query.getSingleResult();

			Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemId);
			UnitConversion unitConversion = getUnitConversionByFromIdAndToId(em, item.getStockUom(), item.getSellableUom(), logger);
			BigDecimal conversionAmount = inventory.getTotalAvailableQuanity();
			if (unitConversion != null)
			{
				conversionAmount = conversionAmount.multiply(new BigDecimal(1).divide(unitConversion.getConversionRatio()));
			}

			return conversionAmount;
		}
		catch (Exception e)
		{

			logger.severe("No Entity Found for item id " + itemId);
		}
		return new BigDecimal(0);
	}

	public BigDecimal getAvailableQuantityFromInventory(EntityManager em, String itemId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Inventory> criteria = builder.createQuery(Inventory.class);
			Root<Inventory> r = criteria.from(Inventory.class);
			TypedQuery<Inventory> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Inventory_.itemId), itemId)));
			Inventory inventory = query.getSingleResult();
			return inventory.getTotalAvailableQuanity();
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public BigDecimal convertUnit(String fromUOMId, String toUOMId, NirvanaLogger logger, BigDecimal sellable, EntityManager em)
	{
		UnitConversion conversion = getUnitConversionByFromIdAndToId(em, fromUOMId, toUOMId, logger);
		BigDecimal conversionAmount = null;
		if (conversion != null)
		{
			conversionAmount = new BigDecimal(sellable.doubleValue() * conversion.getConversionRatio().doubleValue());
		}
		else
		{
			// find in reverse order toUOMID to FromUOMID
			conversion = getUnitConversionByFromIdAndToId(em, toUOMId, fromUOMId, logger);
			if (conversion != null)
			{
				// conversionAmount = sellable.multiply(new
				// BigDecimal(1).divide(conversion.getConversionRatio(), 2,
				// BigDecimal.ROUND_HALF_DOWN));
				double temp = 1 / conversion.getConversionRatio().doubleValue();
				conversionAmount = new BigDecimal(sellable.doubleValue() * temp);
			}

		}

		return conversionAmount;
	}

	public String getCategoryNameByItemId(EntityManager em, String itemId, String suppid)
	{
		try
		{
			String sql = "select l.name from category l " + "left join category_items ci on ci.items_id =  " + itemId + " where l.status not in ('I','D') and l.locations_id = " + suppid
					+ " and l.id = ci.category_id;";

			String category = (String) em.createNativeQuery(sql).getSingleResult();

			return category;
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return "";
	}

	public Item getGlobalItemById(EntityManager em, String itemId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
			Root<Item> r = criteria.from(Item.class);
			TypedQuery<Item> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Item_.id), itemId), builder.equal(r.get(Item_.globalItemId), 0)));
			Item item = query.getSingleResult();
			return item;
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return null;
	}

	public String getItemsByCategory(String categoryId, EntityManager em, HttpServletRequest httpRequest) throws Exception
	{
		try
		{
			List<ItemDetailDisplayPacket> ans = new ArrayList<ItemDetailDisplayPacket>();
			String sql = "select distinct i.id,i.name,i.display_name as item_display_name ,i.stock_uom ,stockuom.name as stock_name,"
					+ " i.sales_tax_1, i.sales_tax_2,i.sales_tax_3, i.sales_tax_4, i.distribution_price,i.purchasing_rate "
					+ "  from items i left join category_items ci on ci.items_id =i.id left join category co on co.id=ci.category_id "
					+ " left join unit_of_measurement stockuom on stockuom.id = i.stock_uom " + " where ci.category_id= ?  and i.status!= 'D' and i.status!= 'R'";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, categoryId).getResultList();

			for (Object[] objRow : resultList)
			{
				/*
				 * Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, (Integer) objRow[0]);
				 * UnitConversion
				 * unitConversion=getUnitConversionByFromIdAndToId(em,
				 * item.getStockUom(), item.getSellableUom(),logger); int
				 * conversionAmount =(Integer) objRow[3];
				 * if(unitConversion!=null){ conversionAmount =
				 * conversionAmount.multiply(new
				 * BigDecimal(1).divide(unitConversion.getConversionRatio())); }
				 */
				ItemDetailDisplayPacket detailDisplayPacket = new ItemDetailDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setItemDisplayName((String) objRow[2]);
				detailDisplayPacket.setStockUom((String) objRow[3]);
				detailDisplayPacket.setUomName((String) objRow[4]);

				SalesTax tax = null;
				if ((String) objRow[5] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) objRow[5]);
					if (tax != null)
					{
						detailDisplayPacket.setTaxDisplayName1(tax.getDisplayName());
						detailDisplayPacket.setTaxName1(tax.getTaxName());
						detailDisplayPacket.setTaxRate1(tax.getRate());

					}
				}
				if ((String) objRow[6] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) objRow[6]);
					if (tax != null)
					{
						detailDisplayPacket.setTaxDisplayName2(tax.getDisplayName());
						detailDisplayPacket.setTaxName2(tax.getTaxName());
						detailDisplayPacket.setTaxRate2(tax.getRate());
					}
				}
				if ((String) objRow[7] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) objRow[7]);
					if (tax != null)
					{
						detailDisplayPacket.setTaxDisplayName3(tax.getDisplayName());
						detailDisplayPacket.setTaxName3(tax.getTaxName());
						detailDisplayPacket.setTaxRate3(tax.getRate());
					}
				}
				if ((String) objRow[8] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) objRow[8]);
					if (tax != null)
					{
						detailDisplayPacket.setTaxDisplayName4(tax.getDisplayName());
						detailDisplayPacket.setTaxName4(tax.getTaxName());
						detailDisplayPacket.setTaxRate4(tax.getRate());
					}
				}

				if (objRow[9] != null)

				{
					detailDisplayPacket.setDistributionPrice((BigDecimal) objRow[9]);
				}

				if (objRow[10] != null)
				{
					detailDisplayPacket.setPurchasingRate((BigDecimal) objRow[10]);
				}

				detailDisplayPacket.setAvailableQty(getConvertedAvailableQuantityFromInventory(em, detailDisplayPacket.getId()));
				ans.add(detailDisplayPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public UnitConversion getUnitConversionByFromIdAndToId(EntityManager em, String fromUOMId, String toUOMId, NirvanaLogger logger)
	{

		try
		{
			String queryString = "select l from UnitConversion l where l.fromUOMId=? and l.toUOMId=? and l.status not in ('I','D') ";
			TypedQuery<UnitConversion> query = em.createQuery(queryString, UnitConversion.class).setParameter(1, fromUOMId).setParameter(2, toUOMId);
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe("No result found for Unit Conversion entiry fromUOMId " + fromUOMId + " toUOMId " + toUOMId);
		}
		return null;
	}

	public List<InventoryItemBom> getInventoryItemBom(EntityManager em, String itemId, NirvanaLogger logger)
	{

		try
		{
			String queryString = "select l from InventoryItemBom l where l.itemIdRm=? ";
			TypedQuery<InventoryItemBom> query = em.createQuery(queryString, InventoryItemBom.class).setParameter(1, itemId);
			return query.getResultList();
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return null;
	}

	public List<InventoryAttributeBOM> getInventoryAttributeBOM(EntityManager em, String itemId, NirvanaLogger logger)
	{

		try
		{
			String queryString = "select l from InventoryAttributeBOM l where l.itemIdRm=? ";
			TypedQuery<InventoryAttributeBOM> query = em.createQuery(queryString, InventoryAttributeBOM.class).setParameter(1, itemId);
			return query.getResultList();
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return null;
	}

	public List<CatalogDisplayService> getAllItemsByRootCategoryIdAndNameAndLocationd(String categoryId, String name, String locationId, EntityManager em, HttpServletRequest httpRequest) throws Exception
	{
		List<Object[]> resultList = null;
		try
		{
			List<CatalogDisplayService> ans = new ArrayList<CatalogDisplayService>();

			// String itemsId = "";
			ArrayList<String> idsList = new ArrayList<String>();
			try
			{
				String queryForAllCategoryUnderRoot = "SELECT c.id FROM category c where c.category_id = '" + categoryId + "' or c.category_id in(SELECT c.id FROM category c where c.category_id = '"
						+ categoryId + "' ) or c.id = '" + categoryId+"'";
				String sql = "SELECT  i.id item_id, i.name item_name,i.image_name  i_image_name ,i.short_name i_short_name,c.display_name c_display_name, "
						+ " i.price_selling i_price_selling,ci.category_id ci_category_id," + "itp.status itp_status,p.display_name p_display_name,p.status p_status,p.id p_id, "
						+ "ctp.status ctp_status,cp.display_name cp_display_name,cp.status cp_status,cp.id cp_id," + "itd.status itd_status,d.display_name d_display_name,d.status d_status,d.id d_id, "
						+ " ctd.status ctd_status,cd.display_name cd_display_name,cd.status cd_status,cd.id cd_id" + "  FROM category_items ci  " + "  left join items i on i.id = ci.items_id "
						+ "  left join course c on c.id=i.course_id " + "  left join items_to_printers itp on itp.items_id=i.id " + "  left join printers p on p.id= itp.printers_id "
						+ " left join category_to_printers ctp on ctp.category_id= ci.category_id" + " left join printers cp on cp.id= ctp.printers_id"
						+ "  left join items_to_discounts itd on itd.items_id=i.id " + "  left join discounts d on d.id= itd.discounts_id "
						+ " left join category_to_discounts ctd on ctd.category_id= ci.category_id" + " left join discounts cd on cd.id= ctd.discounts_id" + "  where  ci.category_id in ( "
						+ queryForAllCategoryUnderRoot + " ) " + "  and i.status != 'D' and (i.display_name like '%" + name + "%' or i.short_name like '%" + name + "%' or i.name like '%" + name
						+ "%' or c.display_name like '%" + name + "%' or i.display_name like '%" + name + "%' )" + "  and i.locations_id = '" + locationId + "' " + "   " + "   order by  i.id   ";

				resultList = em.createNativeQuery(sql).getResultList();
				for (Object[] objRow : resultList)
				{
					// if this has primary key not 0
					if (((String) objRow[0] != null) && idsList.contains((String) objRow[0]) == false)
					{

						idsList.add((String) objRow[0]);
						CatalogDisplayService catalogDisplayService = new CatalogDisplayService();

						catalogDisplayService.setItemsId((String) objRow[0]);
						catalogDisplayService.setItemsName((String) objRow[1]);
						catalogDisplayService.setImageName((String) objRow[2]);
						catalogDisplayService.setShortName((String) objRow[3]);
						catalogDisplayService.setCourseName((String) objRow[4]);
						catalogDisplayService.setPriceSelling(((BigDecimal) objRow[5]).floatValue());
						catalogDisplayService.setId((String) objRow[6]);
						// itemsId += rs.getInt(1) + ",";
						ans.add(catalogDisplayService);
					}

				}
				for (CatalogDisplayService displayPacket : ans)
				{
					List<ItemsToPrinter> itemToPrinters = getItemsToPrinter(em, displayPacket.getItemsId());
					String itemToPrinter = "";
					String itemToPrinterName = "";
					if (itemToPrinters == null || itemToPrinters.size() == 0)
					{
						try
						{
							String queryString = "select p from CategoryItem p where p.itemsId=? ";
							TypedQuery<CategoryItem> query = em.createQuery(queryString, CategoryItem.class).setParameter(1, displayPacket.getItemsId());
							CategoryItem categoryItem = query.getSingleResult();
							if (categoryItem != null)
							{
								List<CategoryToPrinter> toPrinters = getCategoryToPrinter(em, categoryItem.getCategoryId());
								for (int i = 0; i < toPrinters.size(); i++)
								{
									CategoryToPrinter categoryToPrinter = toPrinters.get(i);
									Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, categoryToPrinter.getPrintersId());
									if (printer != null)
									{
										if (i == (toPrinters.size() - 1))
										{
											itemToPrinterName += printer.getDisplayName();
											itemToPrinter += categoryToPrinter.getId();
										}
										else
										{
											itemToPrinterName += printer.getDisplayName() + ",";
											itemToPrinter += categoryToPrinter.getId() + ",";
										}
									}
								}
							}

						}
						catch (Exception e)
						{
							logger.severe("No Result found");
						}
					}
					else
					{
						for (int i = 0; i < itemToPrinters.size(); i++)
						{
							ItemsToPrinter itemToPrinter2 = itemToPrinters.get(i);
							Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, itemToPrinter2.getPrintersId());
							if (printer != null)
							{
								if (i == (itemToPrinters.size() - 1))
								{
									itemToPrinterName += printer.getDisplayName();
									itemToPrinter += printer.getId();
								}
								else
								{
									itemToPrinterName += printer.getDisplayName() + ",";
									itemToPrinter += printer.getId() + ",";
								}
							}
						}
					}

					displayPacket.setCategoryPrintersId(itemToPrinter);
					displayPacket.setCategoryPrintersName(itemToPrinterName);
				}

			}
			catch (Exception ex)
			{
				logger.severe(httpRequest, ex, ex.getMessage());
			}

			return ans;
		}
		finally
		{

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}

	}

	public List<ItemToSupplierPacket> getItemBySupplierIdAndLocationIdForBusiness(EntityManager em, String suppid, String locationId)
	{
		List<ItemToSupplierPacket> itemToSupplierPacketList = new ArrayList<ItemToSupplierPacket>();
		String sqlForGlobalItemId;
		logger.severe("if(suppid==null || suppid.equals('0'))==================================================================="+suppid+"  "+((suppid==null || suppid.equals('0'))));
		
		if(suppid==null || suppid.equals('0') || suppid.equals("null")){
			 sqlForGlobalItemId = " select i.name,i.id,i.stock_uom ,i.locations_id," + "i.purchasing_rate,   " + " i.sales_tax_1, i.sales_tax_2,i.sales_tax_3, i.sales_tax_4 "
					+ "from items i where i.id in ( select item_id from item_to_supplier its join items i "
					+ " on its.item_id =i.id  where (its.primary_supplier_id is null  and its.secondary_supplier_id is null and its.tertiary_supplier_id is null ) and i.status!= 'D')";

		}else {
			 sqlForGlobalItemId = " select i.name,i.id,i.stock_uom ,i.locations_id," + "i.purchasing_rate,   " + " i.sales_tax_1, i.sales_tax_2,i.sales_tax_3, i.sales_tax_4 "
					+ "from items i where i.id in ( select item_id from item_to_supplier its join items i "
					+ " on its.item_id =i.id  where (its.primary_supplier_id ='"+suppid+"'  or its.secondary_supplier_id='"+suppid+"' or its.tertiary_supplier_id='"+suppid+"' ) and i.status!= 'D')";

		}
		logger.severe("sqlForGlobalItemId==================================================================="+sqlForGlobalItemId);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList2 = em.createNativeQuery(sqlForGlobalItemId).getResultList();
		for (Object[] obj : resultList2)
		{

			if (((String) obj[3]).equals(locationId))
			{
				ItemToSupplierPacket detItemToSupplierPacket = new ItemToSupplierPacket();
				detItemToSupplierPacket.setItemName((String) obj[0]);
				detItemToSupplierPacket.setItemId((String) obj[1]);

				detItemToSupplierPacket.setUnitPrice((BigDecimal) obj[4]);
				detItemToSupplierPacket.setUnitTaxRate(calculateTax(em, (String) obj[5], (String) obj[6], (String) obj[7], (String) obj[8]));

				SalesTax tax = null;
				if ((String) obj[5] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[5]);
					if (tax != null)
					{
						detItemToSupplierPacket.setTaxDisplayName1(tax.getDisplayName());
						detItemToSupplierPacket.setTaxName1(tax.getTaxName());
						detItemToSupplierPacket.setTaxRate1(tax.getRate());

					}
				}
				if ((String) obj[6] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[6]);
					if (tax != null)
					{
						detItemToSupplierPacket.setTaxDisplayName2(tax.getDisplayName());
						detItemToSupplierPacket.setTaxName2(tax.getTaxName());
						detItemToSupplierPacket.setTaxRate2(tax.getRate());
					}
				}
				if ((String) obj[7] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[7]);
					if (tax != null)
					{
						detItemToSupplierPacket.setTaxDisplayName3(tax.getDisplayName());
						detItemToSupplierPacket.setTaxName3(tax.getTaxName());
						detItemToSupplierPacket.setTaxRate3(tax.getRate());
					}
				}
				if ((String) obj[8] != null)
				{
					tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, (String) obj[8]);
					if (tax != null)
					{
						detItemToSupplierPacket.setTaxDisplayName4(tax.getDisplayName());
						detItemToSupplierPacket.setTaxName4(tax.getTaxName());
						detItemToSupplierPacket.setTaxRate4(tax.getRate());
					}
				}

				String name = "";
				if ((((String) obj[1])) !=null)
				{
					UnitOfMeasurement uom = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, (String) obj[2]);
					if (uom != null)
					{
						name = uom.getDisplayName();
					}
				}
				detItemToSupplierPacket.setUomName(name);
				BigDecimal avaibleQuantity = getAvailableQuantityFromInventory(em, detItemToSupplierPacket.getItemId());
				Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, detItemToSupplierPacket.getItemId());
				UnitConversion unitConversion = getUnitConversionByFromIdAndToId(em, item.getSellableUom(), item.getStockUom(), logger);

				if (unitConversion != null)
				{
					avaibleQuantity = avaibleQuantity.multiply(unitConversion.getConversionRatio());
				}
				detItemToSupplierPacket.setAvailableQty(avaibleQuantity);
				detItemToSupplierPacket.setCurrentAvailableQty(avaibleQuantity);

				ItemToSupplier supplier = getItemToSupplierbyItemId(em, detItemToSupplierPacket.getItemId());
				if (supplier != null)
				{
					Location location1 = getLocationsById(em, supplier.getPrimarySupplierId());
					if (location1 != null)
					{
						detItemToSupplierPacket.setPrimarySupplierId(location1.getId());
						detItemToSupplierPacket.setPrimarySupplierName(location1.getName());
					}
					Location location2 = getLocationsById(em, supplier.getSecondarySupplierId());
					if (location2 != null)
					{
						detItemToSupplierPacket.setSecondarySupplierId(location2.getId());
						detItemToSupplierPacket.setSecondarySupplierName(location2.getName());
					}
					Location location3 = getLocationsById(em, supplier.getTertiarySupplierId());
					if (location3 != null)
					{
						detItemToSupplierPacket.setTertiarySupplierId(location3.getId());
						detItemToSupplierPacket.setTertiarySupplierName(location3.getName());
					}

					itemToSupplierPacketList.add(detItemToSupplierPacket);
				}
			}

		}
		return itemToSupplierPacketList;
	}

	public ItemsToLocationPacket addUpdateItemsToLocations(EntityManager em, ItemsToLocationPacket itemsToLocationPacket)
	{
		List<ItemsToLocation> updatedItemsToLocationsList = new ArrayList<ItemsToLocation>();

		for (ItemsToLocation itemsToLocation : itemsToLocationPacket.getItemsToLocationList())
		{
			if (itemsToLocation.getId() == 0)
			{
				itemsToLocation.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			}
			itemsToLocation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			itemsToLocation = em.merge(itemsToLocation);
			updatedItemsToLocationsList.add(itemsToLocation);
		}
		itemsToLocationPacket.setItemsToLocationList(updatedItemsToLocationsList);
		return itemsToLocationPacket;

	}

	public List<Item> getItemsByToAndFromLocationId(EntityManager em, String itemId, String locationId, String fromLocationId)
	{
		if (itemId == null && fromLocationId == null)
		{
			String sqlForGlobalItemId = " select i from Item i where i.locationsId =? and i.status!= 'D'";
			@SuppressWarnings("unchecked")
			List<Item> resultList = em.createQuery(sqlForGlobalItemId).setParameter(1, locationId).getResultList();
			return resultList;
		}
		else if (itemId != null && fromLocationId == null)
		{
			String sqlForGlobalItemId = " select i from Item i where i.locationsId =? and i.id =? and i.status!= 'D'";
			@SuppressWarnings("unchecked")
			List<Item> resultList = em.createQuery(sqlForGlobalItemId).setParameter(1, locationId).setParameter(2, itemId).getResultList();
			return resultList;
		}
		else
		{
			String sqlForGlobalItemId = " select i from Item i where i.locationsId =? and i.status!= 'D' and  i.id in ( select itemsId from ItemsToLocation itl "
					+ "   where on itl.itemsId =? and itl.locationsId =? and itl.status!= 'D')";
			@SuppressWarnings("unchecked")
			List<Item> resultList = em.createQuery(sqlForGlobalItemId).setParameter(1, locationId).setParameter(2, itemId).setParameter(1, fromLocationId).getResultList();
			return resultList;
		}

	}

	public FutureUpdate getAddLaterStatusByItemIdAndLocation(EntityManager em, String globalItemId, String locationId) throws Exception
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<FutureUpdate> criteria = builder.createQuery(FutureUpdate.class);
		Root<FutureUpdate> r = criteria.from(FutureUpdate.class);
		TypedQuery<FutureUpdate> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(FutureUpdate_.globalItemId), globalItemId), builder.notEqual(r.get(FutureUpdate_.status), "D"),
				builder.equal(r.get(FutureUpdate_.locationId), locationId)));
		return query.getSingleResult();
	}

	private List<Nutritions> getLocalItemToNutritions(EntityManager em, String locationId, List<Nutritions> globalList)
	{
		List<Nutritions> localItemsToNutritions = new ArrayList<Nutritions>();
		if (globalList != null && globalList.size() > 0)
		{
			for (Nutritions global : globalList)
			{

				String localId = getLocalNutritionsFromGlobalId(em, global.getId(), locationId);
				if (localId !=null)
				{
					Nutritions local = new Nutritions();
					local.setId(localId);
					local.setNutritionsValue(global.getNutritionsValue());
					localItemsToNutritions.add(local);
				}

			}
		}

		return localItemsToNutritions;
	}

	private String getLocalNutritionsFromGlobalId(EntityManager em, String globalId, String locationId)
	{
		Nutritions nutrition = null;
		try
		{
			String queryString = "select s from Nutritions s where s.globalId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<Nutritions> query = em.createQuery(queryString, Nutritions.class).setParameter(1, globalId).setParameter(2, locationId);
			return query.getSingleResult().getId();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		if (nutrition == null)
		{
			Nutritions global = (Nutritions) new CommonMethods().getObjectById("Nutritions", em,Nutritions.class, globalId);
			if (global != null)
			{
				nutrition = new Nutritions().getNutritions(global);
				nutrition.setLocationsId(locationId);
				nutrition.setGlobalId(globalId);
				return em.merge(nutrition).getId();
			}

		}

		return null;
	}

	public ItemPacket createLocalItemFromGlobal(EntityManager em, String locationId, Location baseLocation, HttpServletRequest httpRequest, String globalItemId) throws Exception
	{
		Item globalItem = getItemById(em, globalItemId);
		ItemPacket itemPacket = createItemPacket(em, globalItemId);
		Item item = createLocalItem(em, 0, "" + locationId, baseLocation, globalItem, itemPacket, httpRequest, globalItem);
		itemPacket.setLocationId("" + locationId);
		itemPacket.setItem(item);
		return itemPacket;

	}

	private ItemPacket createItemPacket(EntityManager em, String globalId)
	{
		ItemPacket packet = new ItemPacket();
		Item globalItem = getItemById(em, globalId);
		packet.setItem(globalItem);
		packet.setPrinterList(createPrinterListFromGlobalItem(em, globalItem));
		packet.setItemCharsList(createItemsCharListFromGlobalItem(em, globalItem));
		packet.setItemsAttributeTypesList(createItemsAttributeTypeListFromGlobalItem(em, globalItem));
		packet.setItemsAttributesList(createItemsAttributeListFromGlobalItem(em, globalItem));
		packet.setDiscountsList(createDiscountListFromGlobalItem(em, globalItem));
		packet.setCategoryList(createCategoryListFromGlobalItem(em, globalItem));
		packet.setItemToSupplier(globalItem.getItemToSuppliers());
		return packet;
	}

	private List<Printer> createPrinterListFromGlobalItem(EntityManager em, Item item)
	{
		List<Printer> printerList = new ArrayList<Printer>();
		for (ItemsToPrinter itemsToPrinter : item.getItemsToPrinters())
		{
			Printer p = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, itemsToPrinter.getPrintersId());
			printerList.add(p);
		}
		return printerList;
	}

	private List<ItemsChar> createItemsCharListFromGlobalItem(EntityManager em, Item item)
	{
		List<ItemsChar> itemsCharList = new ArrayList<ItemsChar>();
		for (ItemsToItemsChar itemsToItemsChar : item.getItemsToItemsChars())
		{
			ItemsChar p = (ItemsChar) new CommonMethods().getObjectById("ItemsChar", em,ItemsChar.class,  itemsToItemsChar.getItemsCharId());
			itemsCharList.add(p);
		}
		return itemsCharList;
	}

	private List<ItemsAttributeType> createItemsAttributeTypeListFromGlobalItem(EntityManager em, Item item)
	{
		List<ItemsAttributeType> itemsAttributeTypeList = new ArrayList<ItemsAttributeType>();
		for (ItemsToItemsAttributeType itemsToItemsAttributeType : item.getItemsToItemsAttributesAttributeTypes())
		{
			ItemsAttributeType p = (ItemsAttributeType) new CommonMethods().getObjectById("ItemsAttributeType", em,ItemsAttributeType.class,  itemsToItemsAttributeType.getItemsAttributeTypeId());
			itemsAttributeTypeList.add(p);
		}
		return itemsAttributeTypeList;
	}

	private List<ItemsAttribute> createItemsAttributeListFromGlobalItem(EntityManager em, Item item)
	{
		List<ItemsAttribute> itemsAttributeList = new ArrayList<ItemsAttribute>();
		for (ItemsToItemsAttribute itemsToItemsAttribute : item.getItemsToItemsAttributes())
		{
			ItemsAttribute p = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,ItemsAttribute.class, itemsToItemsAttribute.getItemsAttributeId());
			itemsAttributeList.add(p);
		}
		return itemsAttributeList;
	}

	private List<Discount> createDiscountListFromGlobalItem(EntityManager em, Item item)
	{
		List<Discount> discountList = new ArrayList<Discount>();
		for (ItemsToDiscount itemsToDiscount : item.getItemsToDiscounts())
		{
			Discount p = (Discount) new CommonMethods().getObjectById("Discount", em,Discount.class, itemsToDiscount.getDiscountsId());
			discountList.add(p);
		}
		return discountList;
	}

	private List<Category> createCategoryListFromGlobalItem(EntityManager em, Item item)
	{
		List<Category> categoryList = new ArrayList<Category>();
		for (CategoryItem itemsToCategory : item.getCategoryItems())
		{
			Category p = (Category) new CommonMethods().getObjectById("Category", em,Category.class, itemsToCategory.getCategoryId());
			categoryList.add(p);
		}
		return categoryList;
	}

	public ItemToDatePacket addUpdateItemToDate(EntityManager em, ItemToDatePacket itemToDatePacket)
	{

		ItemToDate date = itemToDatePacket.getItemToDate();

		if (date.getId() > 0)
		{
			date.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			ItemToDate itemToDate = getItemToDateById(em, date.getId());
			if(itemToDate!=null){
				date.setCreated(itemToDate.getCreated());
			}else{
				date.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			}
			

		}
		else
		{
			date.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			date.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		}

		date = em.merge(date);
		itemToDatePacket.setItemToDate(date);
		return itemToDatePacket;

	}

	public ItemToDatePacket deleteItemToDate(EntityManager em, ItemToDatePacket itemToDatePacket)
	{
		ItemToDate date = itemToDatePacket.getItemToDate();
		ItemToDate newdate = em.find(ItemToDate.class, date.getId());
		newdate.setStatus("D");
		newdate.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		newdate.setUpdatedBy(date.getUpdatedBy());
		newdate = em.merge(newdate);
		itemToDatePacket.setItemToDate(newdate);
		return itemToDatePacket;

	}

	public List<ItemToDate> getItemToDateRecords(EntityManager em, String categoryId, String locationId)
	{

		String sqlForGlobalItemId = " select i from ItemToDate i where i.locationId =? and i.categoryId = ? and i.status not in ('D','I')";
		@SuppressWarnings("unchecked")
		List<ItemToDate> resultList = em.createQuery(sqlForGlobalItemId).setParameter(1, locationId).setParameter(2, categoryId).getResultList();
		List<ItemToDate> itemToDates = new ArrayList<ItemToDate>();
		for (ItemToDate itemToDate : resultList)
		{
			Item i = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemToDate.getItemId());
			itemToDate.setItemDisplayName(i.getDisplayName());
			itemToDates.add(itemToDate);
		}
		return itemToDates;

	}

	public List<ItemToDate> getItemToDate(EntityManager em, String locationId)
	{

		String sqlForGlobalItemId = " select i from ItemToDate i where i.locationId =?   and i.status not in ('D','I')";
		@SuppressWarnings("unchecked")
		List<ItemToDate> resultList = em.createQuery(sqlForGlobalItemId).setParameter(1, locationId).getResultList();
		List<ItemToDate> itemToDates = new ArrayList<ItemToDate>();
		for (ItemToDate itemToDate : resultList)
		{
			Item i = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemToDate.getItemId());
			itemToDate.setItemDisplayName(i.getDisplayName());
			itemToDates.add(itemToDate);
		}
		return itemToDates;

	}

	public Item updateItemAvailability(EntityManager em, Item item)
	{
		Item dbItem = null;
		if (item != null & em != null)
		{
			dbItem = getItemById(em, item.getId());
			dbItem.setAvailability(item.isAvailability());
			dbItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			dbItem.setUpdatedBy(item.getUpdatedBy());
			dbItem = em.merge(dbItem);
		}
		return dbItem;
	}

	public List<ItemToDate> getItemToDate(EntityManager em, String itemId, String date)
	{

		String sqlForGlobalItemId = " select i from ItemToDate i where i.itemId =? and i.date =?  and i.status not in ('D','I')";
		@SuppressWarnings("unchecked")
		List<ItemToDate> resultList = em.createQuery(sqlForGlobalItemId).setParameter(1, itemId).setParameter(2, date).getResultList();

		return resultList;

	}

	public ItemToDate getItemToDateById(EntityManager em, int id)
	{

		@SuppressWarnings("unchecked")
		ItemToDate resultList = null;
		;
		try
		{
			String sqlForGlobalItemId = " select i from ItemToDate i where i.id =?";
			resultList = (ItemToDate) em.createQuery(sqlForGlobalItemId).setParameter(1, id).getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		return resultList;

	}
	private UnitOfMeasurement getUnitOfMeasurementById(EntityManager em, String id)
	{
		try
		{
			String queryString = "select s from UnitOfMeasurement s where s.id =? and s.status!='D' ";
			TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class).setParameter(1, id);
			return query.getSingleResult();
		}
		catch (Exception e)
		{

			logger.severe("No Result found for id " + id +" in UnitOfMeasurement");

		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<ItemToSupplierPacket> getItemBySupplierId(EntityManager em, String id, String locationId, boolean needToConvert)
 {

		List<Object[]> resultList;
		
		if(id==null || id.equals("null")){
			 resultList = em
					.createNativeQuery("call get_items_by_supplier_for_no_supplier(?,?)")
					.setParameter(1, id).setParameter(2, locationId)
					.getResultList();
		}else {
			resultList = em
					.createNativeQuery("call get_items_by_supplier(?,?)")
					.setParameter(1, id).setParameter(2, locationId)
					.getResultList();
		}
		
		List<ItemToSupplierPacket> itemToSupplierPacketList = new ArrayList<ItemToSupplierPacket>();

		for (Object[] obj : resultList) {
			ItemToSupplierPacket detItemToSupplierPacket = new ItemToSupplierPacket();

			detItemToSupplierPacket.setItemName((String) obj[0]);
			detItemToSupplierPacket.setItemId((String) obj[1]);

			detItemToSupplierPacket.setUnitPrice((BigDecimal) obj[3]);

			if (obj[8] != null) {
				detItemToSupplierPacket.setUnitTaxRate((BigDecimal) obj[8]);
			}

			detItemToSupplierPacket.setTaxDisplayName1((String) obj[9]);
			detItemToSupplierPacket.setTaxDisplayName2((String) obj[10]);
			detItemToSupplierPacket.setTaxDisplayName3((String) obj[11]);
			detItemToSupplierPacket.setTaxDisplayName4((String) obj[12]);

			detItemToSupplierPacket.setTaxName1((String) obj[13]);
			detItemToSupplierPacket.setTaxName2((String) obj[14]);
			detItemToSupplierPacket.setTaxName3((String) obj[15]);
			detItemToSupplierPacket.setTaxName4((String) obj[16]);

			detItemToSupplierPacket.setTaxRate1((BigDecimal) obj[17]);
			detItemToSupplierPacket.setTaxRate2((BigDecimal) obj[18]);
			detItemToSupplierPacket.setTaxRate3((BigDecimal) obj[19]);
			detItemToSupplierPacket.setTaxRate4((BigDecimal) obj[20]);

			detItemToSupplierPacket.setUomName((String) obj[21]);

			if (obj[22] != null) {
				if (needToConvert) {
					BigDecimal avaibleQuantity = (BigDecimal) obj[22];
					if ((String) obj[23] != null && (String) obj[2] != null) {
						UnitConversion unitConversion = getUnitConversionByFromIdAndToId(
								em, (String) obj[23], (String) obj[2], logger);

						if (unitConversion != null) {
							avaibleQuantity = avaibleQuantity
									.multiply(unitConversion
											.getConversionRatio());
						}
						detItemToSupplierPacket
								.setAvailableQty(avaibleQuantity);
					}

				} else {
					detItemToSupplierPacket
							.setAvailableQty((BigDecimal) obj[22]);
				}

			} else {
				detItemToSupplierPacket.setAvailableQty(new BigDecimal(0));
			}

			itemToSupplierPacketList.add(detItemToSupplierPacket);

		}
		return itemToSupplierPacketList;
	}
}
