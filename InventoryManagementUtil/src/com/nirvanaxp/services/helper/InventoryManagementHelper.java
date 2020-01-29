/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.helper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
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
import com.nirvanaxp.common.utils.relationalentity.helper.ItemRelationsHelper;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.jaxrs.packets.InventoryPostPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.Item_;
import com.nirvanaxp.types.entities.inventory.Inventory;
import com.nirvanaxp.types.entities.inventory.InventoryAttributeBOM;
import com.nirvanaxp.types.entities.inventory.InventoryAttributeBOM_;
import com.nirvanaxp.types.entities.inventory.InventoryHistory;
import com.nirvanaxp.types.entities.inventory.InventoryItemBom;
import com.nirvanaxp.types.entities.inventory.InventoryItemBom_;
import com.nirvanaxp.types.entities.inventory.InventoryItemDefault;
import com.nirvanaxp.types.entities.inventory.InventoryItemDefault_;
import com.nirvanaxp.types.entities.inventory.InventoryOrderReceipt;
import com.nirvanaxp.types.entities.inventory.InventoryOrderReceiptForItem;
import com.nirvanaxp.types.entities.inventory.Inventory_;
import com.nirvanaxp.types.entities.inventory.RequestOrderDetailItems;
import com.nirvanaxp.types.entities.inventory.UnitConversion;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;
import com.nirvanaxp.websocket.protocol.POSNServices;

public class InventoryManagementHelper
{
	private static final NirvanaLogger logger = new NirvanaLogger(InventoryManagementHelper.class.getName());
	public Inventory getInventoryForItemId(EntityManager em, String itemId, String locationId,boolean isGlobalItem) throws NoResultException
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Inventory> criteria = builder.createQuery(Inventory.class);
		Root<Inventory> r = criteria.from(Inventory.class);
		TypedQuery<Inventory> query = null;
		
		if(isGlobalItem){
			query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Inventory_.itemId), itemId),
					builder.notEqual(r.get(Inventory_.status), "D")));
		}else{
			query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Inventory_.itemId), itemId), builder.equal(r.get(Inventory_.locationId), locationId),
					builder.notEqual(r.get(Inventory_.status), "D")));
		}
		 

		Inventory inventory = query.getSingleResult();

		return inventory;

	}

	public InventoryItemDefault getInventoryItemDefaultForItemId(EntityManager em, String itemId) throws NoResultException
	{

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<InventoryItemDefault> criteria = builder.createQuery(InventoryItemDefault.class);
			Root<InventoryItemDefault> r = criteria.from(InventoryItemDefault.class);
			TypedQuery<InventoryItemDefault> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(InventoryItemDefault_.itemId), "" + itemId),
					builder.notEqual(r.get(InventoryItemDefault_.status), "D")));
			InventoryItemDefault inventoryItemDefault = query.getSingleResult();
			return inventoryItemDefault;
		} catch (Exception e) {
			
			 logger.severe(e);
		}
		return null;

	}

	public Inventory addInventory(EntityManager em, InventoryOrderReceipt inventoryOrderReceipt, BigDecimal defaultInventorythreashold, String locationId,HttpServletRequest httpRequest) throws Exception
	{
		// inventory is not yet created for it, hence we need to create
		// one for this
		Inventory inventory = new Inventory();
		inventory.setCreatedBy(inventoryOrderReceipt.getCreatedBy());
		inventory.setUpdatedBy(inventoryOrderReceipt.getUpdatedBy());
		inventory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
		inventory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		inventory.setItemId(inventoryOrderReceipt.getItemId());
		inventory.setStatus("A");

		// entered uom for inventory
		String posunitOfMeasurement = inventoryOrderReceipt.getUnitOfMeasure();
		Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, inventoryOrderReceipt.getItemId());

		boolean shouldCommitItem = false;

		if (item.getStockUom() == null)
		{
			shouldCommitItem = true;
			item.setStockUom(inventoryOrderReceipt.getUnitOfMeasure());
		}
		if (item.getSellableUom() == null)
		{
			shouldCommitItem = true;
			item.setSellableUom(item.getStockUom());
		}
		if (shouldCommitItem)
		{
			EntityTransaction tx = em.getTransaction();
			try
			{
				// start transaction
				tx.begin();
				em.persist(item);
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
		}

		// stock uom is one thats defined in item
		inventory.setUnitOfMeasurementId(item.getStockUom());

		// what is given in po is sellable and is not stock hence we need the
		// conversion
		if (item.getStockUom() != posunitOfMeasurement)
		{
			// we need conversion, convert sellable to stock
			BigDecimal stockQty = getStockQtyAfterConversion(item.getStockUom(), posunitOfMeasurement, inventoryOrderReceipt.getPurchasedQuantity(), em);
			inventory.setTotalAvailableQuanity(stockQty);
		}
		else
		{
			// while adding to inventory, check the stock inventory and
			inventory.setTotalAvailableQuanity(inventoryOrderReceipt.getPurchasedQuantity());
		}

		inventory.setTotalUsedQuanity(new BigDecimal(0));

		inventory.setInventoryThreashold(defaultInventorythreashold);

		inventory.setLocationId(locationId);

		// manage supplier for new entity
		manageSupplierForInvetory(inventory,inventoryOrderReceipt);

		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			if(inventory.getId()==null)
			inventory.setId(new StoreForwardUtility().generateDynamicIntId(em, inventory.getLocationId(), httpRequest, "inventory"));

			em.persist(inventory);
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

		return inventory;
	}

	public void updateInventory(HttpServletRequest httpServletRequest, EntityManager em, Inventory inventory, InventoryOrderReceipt inventoryOrderReceipt, boolean shouldManageSupplier)
	{
		// we have an item inventory, we must add this amount to
		// existing one

		inventory.setUpdatedBy(inventoryOrderReceipt.getUpdatedBy());
		inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		// entered uom for inventory
		String posunitOfMeasurement = inventoryOrderReceipt.getUnitOfMeasure();
		// what is given in po is sellable and is not stock hence we need
		// the
		// conversion
		if (!inventory.getUnitOfMeasurementId().equals(posunitOfMeasurement))
		{
			// we need conversion, convert sellable to stock
			BigDecimal stockQty = getStockQtyAfterConversion(inventory.getUnitOfMeasurementId(), posunitOfMeasurement, inventoryOrderReceipt.getPurchasedQuantity(), em);
			inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(stockQty));
		}
		else
		{
			// while adding to inventory, check the stock inventory and
			inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(inventoryOrderReceipt.getPurchasedQuantity()));
		}

		/*
		 * // remove the old inventory and add new one to it
		 * inventory.setTotalAvailableQuanity(inventory
		 * .getTotalAvailableQuanity().add(
		 * inventoryOrderReceipt.getPurchasedQuantity()));
		 */

		if (shouldManageSupplier)
		{
			manageSupplierForInvetory(inventory,inventoryOrderReceipt);
		}

		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.merge(inventory);
			tx.commit();
			new InsertIntoHistory().insertInventoryIntoHistory(httpServletRequest, inventory, em);
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

	}

	public void manageSupplierForInvetory(Inventory inventory, InventoryOrderReceipt inventoryOrderReceipt)
	{
		// inventory is not yet created for it, hence we need to create
		// one for this
		// add and update packet
		if (inventory.getPrimarySupplierId() == null)
		{
			inventory.setPrimarySupplierId(inventoryOrderReceipt.getSupplierId());
		}
		else if (inventory.getSecondarySupplierId() == null && !inventory.getPrimarySupplierId().equals(inventoryOrderReceipt.getSupplierId()))
		{
			inventory.setSecondarySupplierId(inventoryOrderReceipt.getSupplierId());
		}
		else if (inventory.getTertiarySupplierId() == null && !inventory.getPrimarySupplierId().equals(inventoryOrderReceipt.getSupplierId())
				&& !inventory.getSecondarySupplierId().equals(inventoryOrderReceipt.getSupplierId()))
		{
			inventory.setTertiarySupplierId(inventoryOrderReceipt.getSupplierId());
		}

	}
	public void manageSupplierForInvetoryByItem(Inventory inventory, ItemToSupplier itemToSupplier)
	{
		// inventory is not yet created for it, hence we need to create
		// one for this
		// add and update packet
		if(itemToSupplier!= null){
			if (inventory.getPrimarySupplierId() == null)
			{
				inventory.setPrimarySupplierId(itemToSupplier.getPrimarySupplierId());
			}
			else if (inventory.getSecondarySupplierId() == null)
			{
				inventory.setSecondarySupplierId(itemToSupplier.getSecondarySupplierId());
			}
			else if (inventory.getTertiarySupplierId() == null )
			{
				inventory.setTertiarySupplierId(itemToSupplier.getTertiarySupplierId());
			}
		}
	}

	public Item manageItemBasedOnInventoryOrderReceipt(HttpServletRequest httpServletRequest, EntityManager em, String itemId, Inventory inventory, double defaultInventorythreashold,
			boolean isFromOrders, BigDecimal prevQtyInInventory,InventoryItemsList inventoryItemsList )
	{
		// get item for this inventory and see if the item threshold and is
		// in stock needs to get updated or not
		Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemId);
		boolean isPushNeeded = false;
		if (item != null)
		{

			double totalavaiQty = inventory.getTotalAvailableQuanity().doubleValue();
			// item threshold has changed
			if (totalavaiQty >= defaultInventorythreashold && inventory.getIsBelowThreashold() == 1)
			{
				isPushNeeded = true;
				inventory.setIsBelowThreashold(0);
			}

			if (totalavaiQty < defaultInventorythreashold && inventory.getIsBelowThreashold() == 0)
			{
				isPushNeeded = true;
				inventory.setIsBelowThreashold(1);
			}

			if (prevQtyInInventory != null)
			{
				double prevInventoryTotalQty = prevQtyInInventory.doubleValue();

				if (totalavaiQty != prevInventoryTotalQty && totalavaiQty < defaultInventorythreashold)
				{
					isPushNeeded = true;
				}
			}

			/*
			 * if (totalavaiQty > 0 && item.getIsInStock() == 0) { isPushNeeded
			 * = true; item.setIsInStock(1); }
			 * 
			 * if (totalavaiQty <= 0 && item.getIsInStock() == 1) { isPushNeeded
			 * = true; item.setIsInStock(0); }
			 */

			// if its from orders and below threashold, then we need to push, as
			// inventory must have been increased or decreased
			if (isFromOrders && totalavaiQty < defaultInventorythreashold)
			{
				isPushNeeded = true;
			}

			if (isPushNeeded)
			{
				inventory= em.merge(inventory);
				

				//return item;
			}
			InventoryHistory history = new InsertIntoHistory().insertInventoryIntoHistory(httpServletRequest, inventory, em);
			
			
			if(inventoryItemsList != null)
			{
				inventoryItemsList.setInventoryHistoryId(history.getId());
			}
			
			if (isPushNeeded)
			{
				return item;
			}

		}
		new InsertIntoHistory().insertInventoryIntoHistory(httpServletRequest, inventory, em);

		return null;
	}

	public InventoryPostPacket getPushPacket(Inventory inventory, Item item, BigDecimal threashold)
	{

		InventoryPostPacket inventoryPostPacket = new InventoryPostPacket();
		Item itemPush = null;
		if (item != null)
		{
			itemPush = new Item();
			itemPush.setId(item.getId());
			itemPush.setIsInStock(item.getIsInStock());
			itemPush.setIsBelowThreashold(item.getIsBelowThreashold());
		}

		Inventory inventoryPush = new Inventory();

		inventoryPush.setInventoryThreashold(threashold);

		inventoryPush.setItemId(inventory.getItemId());

		inventoryPush.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity());
		inventoryPush.setTotalUsedQuanity(inventory.getTotalUsedQuanity());
		if (item != null)
		{
			inventoryPush.setItemId(item.getId());
		}
		inventoryPush.setId(inventory.getId());

		if (inventoryPostPacket.getInventoryList() == null)
		{
			List<Inventory> inventories = new ArrayList<Inventory>();
			inventoryPostPacket.setInventoryList(inventories);
		}

		if (inventoryPostPacket.getItemList() == null)
		{
			List<Item> itemsList = new ArrayList<Item>();
			inventoryPostPacket.setItemList(itemsList);
		}
		inventoryPostPacket.getInventoryList().add(inventoryPush);
		if (itemPush != null)
		{
			inventoryPostPacket.getItemList().add(itemPush);
		}

		return inventoryPostPacket;
	}

	public void sendPacketForBroadcast(HttpServletRequest httpRequest, PostPacket inventoryPostPacket, String operationName) throws JsonGenerationException, JsonMappingException, IOException,
			NirvanaXPException, JMSException
	{

		// so that session id value does not get broadcasted
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);

		objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

		String internalJson = objectMapper.writeValueAsString(inventoryPostPacket);

		operationName = ServiceOperationsUtility.getOperationName(operationName);
		MessageSender messageSender = new MessageSender();

		messageSender.sendMessage(httpRequest, inventoryPostPacket.getClientId(), POSNServices.InventoryManagementService.name(), operationName, internalJson, inventoryPostPacket.getMerchantId(),
				inventoryPostPacket.getLocationId(), inventoryPostPacket.getEchoString(), inventoryPostPacket.getSchemaName());

	}

	public BigDecimal getStockQtyAfterConversion(String stockUomId, String sellbaleUomId, BigDecimal qtytoConvert, EntityManager em)
	{
		// what is given in po is sellable and is not stock hence we need the
		// conversion
		if (!stockUomId.equals(sellbaleUomId))
		{
			// we need conversion, convert sellable to stock
			UnitOfMeasurement sellableUOM = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, sellbaleUomId);
			// convert the item and the add to inventory
			BigDecimal stockQty = sellableUOM.getStockQty();
			BigDecimal sellableQty = sellableUOM.getSellableQty();

			BigDecimal divisionResult = stockQty.divide(sellableQty, BigDecimal.ROUND_HALF_UP);
			if (divisionResult != null && divisionResult != new BigDecimal(0.0))
			{
				BigDecimal totalStockQty = qtytoConvert.multiply(divisionResult);
				return totalStockQty;
			}
			/*
			 * BigDecimal totalStockQty = qtytoConvert.multiply((stockQty
			 * .divide(sellableQty))); return totalStockQty;
			 */
		}
		return null;
	}
	
	public BigDecimal convertUnit(String fromUOMId, String toUOMId, NirvanaLogger logger,BigDecimal sellable, EntityManager em ){
		UnitConversion conversion = getUnitConversionByFromIdAndToId(em, fromUOMId, toUOMId, logger);
		BigDecimal conversionAmount = null;
		if(conversion!= null){
			 conversionAmount = new BigDecimal( sellable.doubleValue() * conversion.getConversionRatio().doubleValue());
		}else{
			// find in reverse order toUOMID to FromUOMID
			 conversion = getUnitConversionByFromIdAndToId(em,toUOMId ,fromUOMId , logger);
			 if(conversion!= null)
			 {
				 //conversionAmount = sellable.multiply(new BigDecimal(1).divide(conversion.getConversionRatio(),2,BigDecimal.ROUND_HALF_DOWN));
				 double temp = 1/conversion.getConversionRatio().doubleValue();
				 	conversionAmount =  new BigDecimal(sellable.doubleValue() * temp );
			 }
			 
			 
		}
		
		
		return conversionAmount;
	}


	public InventoryItemDefault getInventoryItemDefault(String itemId, EntityManager em)
	{

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<InventoryItemDefault> criteria = builder.createQuery(InventoryItemDefault.class);
			Root<InventoryItemDefault> r = criteria.from(InventoryItemDefault.class);
			TypedQuery<InventoryItemDefault> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(InventoryItemDefault_.itemId), itemId),
					builder.notEqual(r.get(InventoryItemDefault_.status), "D")));
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			
			 logger.severe(e);
		}
		return null;
	}

	public List<InventoryItemBom> getInventoryItemBomForItemId(String itemId, EntityManager em) throws NoResultException
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<InventoryItemBom> criteria = builder.createQuery(InventoryItemBom.class);
		Root<InventoryItemBom> r = criteria.from(InventoryItemBom.class);
		TypedQuery<InventoryItemBom> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(InventoryItemBom_.itemIdFg), itemId), builder.notEqual(r.get(InventoryItemBom_.status), "D")));
		List<InventoryItemBom> inventoryItemBom = query.getResultList();
		return inventoryItemBom;

	}
	public List<InventoryItemBom> getInventoryItemBomForItemIdForKDS(String itemId, EntityManager em) throws NoResultException
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<InventoryItemBom> criteria = builder.createQuery(InventoryItemBom.class);
		Root<InventoryItemBom> r = criteria.from(InventoryItemBom.class);
		TypedQuery<InventoryItemBom> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(InventoryItemBom_.itemIdFg), itemId), builder.notEqual(r.get(InventoryItemBom_.itemIdRm), itemId), builder.notEqual(r.get(InventoryItemBom_.status), "D")));
		List<InventoryItemBom> inventoryItemBom = query.getResultList();
		return inventoryItemBom;

	}

	public List<InventoryAttributeBOM> getInventoryBomForItemAtt(String itemAttId, EntityManager em) throws NoResultException
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<InventoryAttributeBOM> criteria = builder.createQuery(InventoryAttributeBOM.class);
		Root<InventoryAttributeBOM> r = criteria.from(InventoryAttributeBOM.class);
		TypedQuery<InventoryAttributeBOM> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(InventoryAttributeBOM_.attributeIdFg), itemAttId),
				builder.notEqual(r.get(InventoryItemBom_.status), "D")));
		List<InventoryAttributeBOM> inventoryItemBom = query.getResultList();
		return inventoryItemBom;

	}
	
	public void updateInventoryWithItem(HttpServletRequest httpServletRequest, EntityManager em, Inventory inventory, InventoryOrderReceipt inventoryOrderReceipt, InventoryOrderReceiptForItem inventoryOrderReceiptForItem, boolean shouldManageSupplier)
	{
		// we have an item inventory, we must add this amount to
		// existing one

		inventory.setUpdatedBy(inventoryOrderReceiptForItem.getUpdatedBy());
		inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		// entered uom for inventory
		String posunitOfMeasurement = inventoryOrderReceiptForItem.getUnitOfMeasure();
		// what is given in po is sellable and is not stock hence we need
		// the
		// conversion
		if (!inventory.getUnitOfMeasurementId().equals(posunitOfMeasurement))
		{
			// we need conversion, convert sellable to stock
			BigDecimal stockQty = getStockQtyAfterConversion(inventory.getUnitOfMeasurementId(), posunitOfMeasurement, inventoryOrderReceiptForItem.getPurchasedQuantity(), em);
			inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(stockQty));
		}
		else
		{
			// while adding to inventory, check the stock inventory and
			inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(inventoryOrderReceiptForItem.getPurchasedQuantity()));
		}

		//calculateUnitPrice(inventory, em);
		/*
		 * // remove the old inventory and add new one to it
		 * inventory.setTotalAvailableQuanity(inventory
		 * .getTotalAvailableQuanity().add(
		 * inventoryOrderReceipt.getPurchasedQuantity()));
		 */

		if (shouldManageSupplier)
		{
			manageSupplierForInvetory(inventory,inventoryOrderReceipt);
		}

		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.merge(inventory);
//			em.merge(inventoryOrderReceiptForItem);
			tx.commit();
			new InsertIntoHistory().insertInventoryIntoHistory(httpServletRequest, inventory, em);
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

	}
	public Inventory addInventoryWithItem(EntityManager em, InventoryOrderReceipt inventoryOrderReceipt,InventoryOrderReceiptForItem inventoryOrderReceiptForItem, BigDecimal defaultInventorythreashold, String locationId,HttpServletRequest httpRequest) throws Exception
	{
		// inventory is not yet created for it, hence we need to create
		// one for this
		Inventory inventory = new Inventory();
		inventory.setCreatedBy(inventoryOrderReceiptForItem.getCreatedBy());
		inventory.setUpdatedBy(inventoryOrderReceiptForItem.getUpdatedBy());
		inventory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
		inventory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		inventory.setItemId(inventoryOrderReceiptForItem.getItemId());
		inventory.setStatus("A");

		// entered uom for inventory
		String posunitOfMeasurement = inventoryOrderReceiptForItem.getUnitOfMeasure();
		Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, inventoryOrderReceiptForItem.getItemId());

		boolean shouldCommitItem = false;

		if (item.getStockUom() == null)
		{
			shouldCommitItem = true;
			item.setStockUom(inventoryOrderReceiptForItem.getUnitOfMeasure());
		}
		if (item.getSellableUom() == null)
		{
			shouldCommitItem = true;
			item.setSellableUom(item.getStockUom());
		}
		if (shouldCommitItem)
		{
			EntityTransaction tx = em.getTransaction();
			try
			{
				// start transaction
				tx.begin();
				em.persist(item);
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
		}

		// stock uom is one thats defined in item
		inventory.setUnitOfMeasurementId(item.getStockUom());

		// what is given in po is sellable and is not stock hence we need the
		// conversion
		if (item.getStockUom() != posunitOfMeasurement)
		{
			// we need conversion, convert sellable to stock
			BigDecimal stockQty = getStockQtyAfterConversion(item.getStockUom(), posunitOfMeasurement, inventoryOrderReceiptForItem.getPurchasedQuantity(), em);
			inventory.setTotalAvailableQuanity(stockQty);
		}
		else
		{
			// while adding to inventory, check the stock inventory and
			inventory.setTotalAvailableQuanity(inventoryOrderReceiptForItem.getPurchasedQuantity());
		}
		
		//calculateUnitPrice(inventory, em);

		inventory.setTotalUsedQuanity(new BigDecimal(0));

		inventory.setInventoryThreashold(defaultInventorythreashold);

		inventory.setLocationId(locationId);

		// manage supplier for new entity
		manageSupplierForInvetory(inventory,inventoryOrderReceipt);

		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			if(inventory.getId()==null)
			inventory.setId(new StoreForwardUtility().generateDynamicIntId(em, inventory.getLocationId(), httpRequest, "inventory"));

			em.persist(inventory);
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

		return inventory;
	}
	
 
	public Inventory addInventoryWithItemForPO(EntityManager em, RequestOrderDetailItems requestOrderDetailItems, BigDecimal defaultInventorythreashold, String locationId)
	{
		// inventory is not yet created for it, hence we need to create
		// one for this
		Inventory inventory = new Inventory();
		inventory.setCreatedBy(requestOrderDetailItems.getCreatedBy());
		inventory.setUpdatedBy(requestOrderDetailItems.getUpdatedBy());
		inventory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
		inventory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		inventory.setItemId(requestOrderDetailItems.getItemsId());
		inventory.setStatus("A");

		// entered uom for inventory
		
		Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, requestOrderDetailItems.getItemsId());
		String posunitOfMeasurement = item.getSellableUom();
		 

		// stock uom is one thats defined in item
		inventory.setUnitOfMeasurementId(item.getStockUom());

		// what is given in po is sellable and is not stock hence we need the
		// conversion
		if (!item.getStockUom().equals(posunitOfMeasurement))
		{
			// we need conversion, convert sellable to stock
			BigDecimal stockQty = getStockQtyAfterConversion(item.getStockUom(), posunitOfMeasurement, requestOrderDetailItems.getAllotmentQty(), em);
			//BigDecimal stockQty = convertUnit(item.getStockUom(), item.getSellableUom(), logger, requestOrderDetailItems.getAllotmentQty(), em);
			inventory.setTotalAvailableQuanity(stockQty);
		}
		else
		{
			// while adding to inventory, check the stock inventory and
			inventory.setTotalAvailableQuanity(requestOrderDetailItems.getAllotmentQty());
		}
		inventory.setYieldQuantity(requestOrderDetailItems.getYieldQuantity());
		inventory.setTotalReceiveQuantity(requestOrderDetailItems.getTotalReceiveQuantity());
		//calculateUnitPrice(inventory, em);

		inventory.setTotalUsedQuanity(new BigDecimal(0));

		inventory.setInventoryThreashold(defaultInventorythreashold);

		inventory.setLocationId(locationId);

		// manage supplier for new entity
		manageSupplierForInvetoryByItem(inventory,getItemById(em, item.getId()).getItemToSuppliers());

		if(inventory.getId()==null){
			inventory.setId(new StoreForwardUtility().generateUUID());
		}
		inventory =em.merge(inventory);
		 

		return inventory;
	}
	public Inventory addInventoryWithItemForPO(EntityManager em, RequestOrderDetailItems requestOrderDetailItems, BigDecimal defaultInventorythreashold, String locationId,BigDecimal allotmentQty, NirvanaLogger logger)
	{
		// inventory is not yet created for it, hence we need to create
		// one for this
		Inventory inventory = new Inventory();
		inventory.setCreatedBy(requestOrderDetailItems.getCreatedBy());
		inventory.setUpdatedBy(requestOrderDetailItems.getUpdatedBy());
		inventory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
		inventory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		inventory.setItemId(requestOrderDetailItems.getItemsId());
		inventory.setStatus("A");

		// entered uom for inventory
		
		Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, requestOrderDetailItems.getItemsId());
		String posunitOfMeasurement = item.getSellableUom();
		 

		// stock uom is one thats defined in item
		inventory.setUnitOfMeasurementId(item.getStockUom());

		// what is given in po is sellable and is not stock hence we need the
		// conversion
		if (!item.getStockUom().equals(posunitOfMeasurement))
		{
			// we need conversion, convert sellable to stock
			BigDecimal stockQty = getStockQtyAfterConversion(item.getStockUom(), posunitOfMeasurement, allotmentQty, em);
			inventory.setTotalAvailableQuanity(stockQty);
		}
		else
		{
			// while adding to inventory, check the stock inventory and
			inventory.setTotalAvailableQuanity(allotmentQty);
			
		}
		
		//calculateUnitPrice(inventory, em);

		inventory.setTotalUsedQuanity(new BigDecimal(0));

		inventory.setInventoryThreashold(defaultInventorythreashold);

		inventory.setLocationId(locationId);
		
		// manage supplier for new entity
		manageSupplierForInvetoryByItem(inventory,getItemById(em, item.getId()).getItemToSuppliers());

		inventory =em.merge(inventory);
		return inventory;
	}
	Item getItemById(EntityManager em, String itemId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
		Root<Item> r = criteria.from(Item.class);
		TypedQuery<Item> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Item_.id), itemId)));
		Item item = query.getSingleResult();

		 
		ItemRelationsHelper itemRelationsHelper = new ItemRelationsHelper();
		 
		ItemToSupplier itemToSupliers = itemRelationsHelper.getItemToSupplier(item.getId(), em);
		if (itemToSupliers != null)
		{
			item.setItemToSuppliers(itemToSupliers);
		}

		return item;

	}
	public Inventory updateInventoryWithItemForPO(HttpServletRequest httpServletRequest, EntityManager em, Inventory inventory,  RequestOrderDetailItems requestOrderDetailItems, boolean shouldManageSupplier,Item item, NirvanaLogger logger,boolean isAdd,boolean isConversionNeeded,boolean isReturned)
	{
		// we have an item inventory, we must add this amount to
		// existing one

		inventory.setUpdatedBy(requestOrderDetailItems.getUpdatedBy());
		inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		// entered uom for inventory
		 
		// what is given in po is sellable and is not stock hence we need
		// the
		// conversion
		if(isConversionNeeded){
			// need to do coding

			
			
			
//			UnitOfMeasurement stockUOM = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, item.getStockUom());
//			UnitOfMeasurement sellableUOM = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, item.getSellableUom()); 
			BigDecimal allotmentqty = new BigDecimal(0);
			if(item.getStockUom()!=null && (!item.getStockUom().equals(item.getSellableUom())) ){
				//find conversion and add conversion in total avaiable quantity 
				// find conversion ratio from  item.getStockUom()!=item.getSellableUom()  --- 1000
				// converted unit = allomentQty x conversion ratio
				
				UnitConversion unitConversion=getUnitConversionByFromIdAndToId(em, item.getStockUom(), item.getSellableUom(),logger);

				if(unitConversion!=null){
					allotmentqty=requestOrderDetailItems.getAllotmentQty().multiply(unitConversion.getConversionRatio());
				}
				
		
			}else {
				allotmentqty=requestOrderDetailItems.getAllotmentQty();
			}

			inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(allotmentqty));
			
		}else{
			
			// while adding to inventory, check the stock inventory and
			if(isAdd){
				
				inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(requestOrderDetailItems.getAllotmentQty()));
				if(isReturned){
					inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().subtract(requestOrderDetailItems.getAllotmentQty()));
				}
			}else{
				inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(requestOrderDetailItems.getAllotmentQty()));
				inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(requestOrderDetailItems.getAllotmentQty()));
			}
			
		}
		 
		
		 
		//calculateUnitPrice(inventory, em);
		/*
		 * // remove the old inventory and add new one to it
		 * inventory.setTotalAvailableQuanity(inventory
		 * .getTotalAvailableQuanity().add(
		 * inventoryOrderReceipt.getPurchasedQuantity()));
		 */
		
		inventory.setYieldQuantity(requestOrderDetailItems.getYieldQuantity());
		inventory.setTotalReceiveQuantity(requestOrderDetailItems.getTotalReceiveQuantity());
		
		inventory= em.merge(inventory);
		
		
		if (shouldManageSupplier)
		{
			manageSupplierForInvetoryByItem(inventory,getItemById(em,item.getId()).getItemToSuppliers());
		}
		
		return inventory;
	}
	
	public Inventory updateInventoryWithItemForPOIntra(HttpServletRequest httpServletRequest, EntityManager em, Inventory inventory,  RequestOrderDetailItems requestOrderDetailItems, boolean shouldManageSupplier,Item item, NirvanaLogger logger,boolean isAdd,boolean isConversionNeeded,boolean isReturned)
	{
		// we have an item inventory, we must add this amount to
		// existing one

		inventory.setUpdatedBy(requestOrderDetailItems.getUpdatedBy());
		inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		// entered uom for inventory
		 
		// what is given in po is sellable and is not stock hence we need
		// the
		// conversion
		if(isConversionNeeded){
			// need to do coding

			
			
			
//			UnitOfMeasurement stockUOM = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, item.getStockUom());
//			UnitOfMeasurement sellableUOM = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,UnitOfMeasurement.class, item.getSellableUom()); 
			BigDecimal allotmentqty = new BigDecimal(0);
			if(item.getStockUom()!=item.getSellableUom() ){
				//find conversion and add conversion in total avaiable quantity 
				// find conversion ratio from  item.getStockUom()!=item.getSellableUom()  --- 1000
				// converted unit = allomentQty x conversion ratio
				
				UnitConversion unitConversion=getUnitConversionByFromIdAndToId(em, item.getStockUom(), item.getSellableUom(),logger);

				if(unitConversion!=null){
					allotmentqty=requestOrderDetailItems.getAllotmentQty().multiply(unitConversion.getConversionRatio());
				}
				
		
			}else {
				allotmentqty=requestOrderDetailItems.getAllotmentQty();
			}
			
			if(isAdd)
			{
				inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(allotmentqty));	
			}else
			{
				
				inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(allotmentqty));
				inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(allotmentqty));
			}

			
			
		}else{
			
			// while adding to inventory, check the stock inventory and
			if(isAdd){
				
				inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(requestOrderDetailItems.getAllotmentQty()));
				if(isReturned){
					inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().subtract(requestOrderDetailItems.getAllotmentQty()));
				}
			}else{
				inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(requestOrderDetailItems.getAllotmentQty()));
				inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(requestOrderDetailItems.getAllotmentQty()));
			}
			
		}
		 
		
		 
		//calculateUnitPrice(inventory, em);
		/*
		 * // remove the old inventory and add new one to it
		 * inventory.setTotalAvailableQuanity(inventory
		 * .getTotalAvailableQuanity().add(
		 * inventoryOrderReceipt.getPurchasedQuantity()));
		 */
		
		inventory.setYieldQuantity(requestOrderDetailItems.getYieldQuantity());
		inventory.setTotalReceiveQuantity(requestOrderDetailItems.getTotalReceiveQuantity());
		
		inventory= em.merge(inventory);
		
		
		if (shouldManageSupplier)
		{
			manageSupplierForInvetoryByItem(inventory,getItemById(em,item.getId()).getItemToSuppliers());
		}
		
		return inventory;
	}
	public Inventory updateInventoryWithItemForPO(HttpServletRequest httpServletRequest, EntityManager em, Inventory inventory,  RequestOrderDetailItems requestOrderDetailItems, boolean shouldManageSupplier,Item item, NirvanaLogger logger,boolean isAdd,boolean isConversionNeeded,boolean isReturned,BigDecimal allotmentQty)
	{
		// we have an item inventory, we must add this amount to
		// existing one

		inventory.setUpdatedBy(requestOrderDetailItems.getUpdatedBy());
		inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		// entered uom for inventory
		 
		// what is given in po is sellable and is not stock hence we need
		// the
		// conversion
		if(isConversionNeeded){
			// need to do coding
			
			BigDecimal allotmentqty = new BigDecimal(0);
			if(item.getStockUom()!=item.getSellableUom() ){
				//find conversion and add conversion in total avaiable quantity 
				// find conversion ratio from  item.getStockUom()!=item.getSellableUom()  --- 1000
				// converted unit = allomentQty x conversion ratio
				UnitConversion unitConversion=getUnitConversionByFromIdAndToId(em, item.getStockUom(), item.getSellableUom(),logger);
				
				if(unitConversion!=null){
					allotmentqty=allotmentQty.multiply(unitConversion.getConversionRatio());
				}
		
			}else {
				allotmentqty=allotmentQty;
			}
			inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(allotmentqty));
		}else{
			
			// while adding to inventory, check the stock inventory and
			if(isAdd){
				
				inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(allotmentQty));
				if(isReturned){
					inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().subtract(allotmentQty));
				}
			}else{
				inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(allotmentQty));
				inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(allotmentQty));
			}
			
		}
		 
		
		 
		//calculateUnitPrice(inventory, em);
		/*
		 * // remove the old inventory and add new one to it
		 * inventory.setTotalAvailableQuanity(inventory
		 * .getTotalAvailableQuanity().add(
		 * inventoryOrderReceipt.getPurchasedQuantity()));
		 */
		
		inventory= em.merge(inventory);

		if (shouldManageSupplier)
		{
			manageSupplierForInvetoryByItem(inventory,getItemById(em,item.getId()).getItemToSuppliers());
		}
		
		return inventory;
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
			
			 logger.severe(e);
		}
		return null;
	}
}
