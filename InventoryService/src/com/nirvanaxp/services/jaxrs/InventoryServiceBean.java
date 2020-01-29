/**
 Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 written consent required to use, copy, share, alter, distribute or transmit
 this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.google.zxing.WriterException;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.global.types.entities.partners.POSNPartners_;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.PrinterUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.data.OrderHeaderWithUser;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.helper.InventoryItemsList;
import com.nirvanaxp.services.helper.InventoryManagementHelper;
import com.nirvanaxp.services.jaxrs.packets.AttributeItemBomDisplayPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryAttributeToBomPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryItemToBOM;
import com.nirvanaxp.services.jaxrs.packets.InventoryPostPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryPushPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryToBomPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemByLocationIdPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.RequestOrderPacket;
import com.nirvanaxp.services.jaxrs.packets.UOMPostPacket;
import com.nirvanaxp.services.jaxrs.packets.UnitConversionPostPacket;
import com.nirvanaxp.services.packet.GoodsReceiveNotesPacket;
import com.nirvanaxp.storeForward.PaymentBatchManager;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsType;
import com.nirvanaxp.types.entities.inventory.GoodsReceiveNotes;
import com.nirvanaxp.types.entities.inventory.GoodsReceiveNotes_;
import com.nirvanaxp.types.entities.inventory.Inventory;
import com.nirvanaxp.types.entities.inventory.InventoryAttributeBOM;
import com.nirvanaxp.types.entities.inventory.InventoryHistory;
import com.nirvanaxp.types.entities.inventory.InventoryItemBom;
import com.nirvanaxp.types.entities.inventory.InventoryItemDefault;
import com.nirvanaxp.types.entities.inventory.InventoryOrderReceipt;
import com.nirvanaxp.types.entities.inventory.InventoryOrderReceiptForItem;
import com.nirvanaxp.types.entities.inventory.Inventory_;
import com.nirvanaxp.types.entities.inventory.PhysicalInventory;
import com.nirvanaxp.types.entities.inventory.PhysicalInventoryHistory;
import com.nirvanaxp.types.entities.inventory.PhysicalInventory_;
import com.nirvanaxp.types.entities.inventory.RequestOrder;
import com.nirvanaxp.types.entities.inventory.RequestOrderDetailItems;
import com.nirvanaxp.types.entities.inventory.RequestOrderDetailItems_;
import com.nirvanaxp.types.entities.inventory.RequestOrder_;
import com.nirvanaxp.types.entities.inventory.UnitConversion;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement_;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.locations.LocationsType;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;
import com.nirvanaxp.types.entities.orders.ItemToSupplier_;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus_;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatusHistory;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.orders.OrderType;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;

class InventoryServiceBean {

	private static final NirvanaLogger logger = new NirvanaLogger(InventoryServiceBean.class.getName());

	InventoryOrderReceipt addInventoryOrderReceipt(HttpServletRequest httpRequest, EntityManager em,
			InventoryOrderReceipt inventoryOrderReceipt, PostPacket postPacket) throws Exception {

		BigDecimal defaultInventorythreashold = ConfigFileReader.getDefaultInventoryThreshold();

		// get the relation sets
		InventoryOrderReceipt objToReturn = addUpdateInventoryOrderReceipt(em, inventoryOrderReceipt);
		addOrUpdateInventory(httpRequest, em, inventoryOrderReceipt, defaultInventorythreashold, postPacket);
		return objToReturn;

	}

	private void addOrUpdateInventory(HttpServletRequest httpRequest, EntityManager em,
			InventoryOrderReceipt inventoryOrderReceipt, BigDecimal defaultInventorythreashold, PostPacket postPacket)
			throws Exception {
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		// get inventory object corresponding to this inventory order receipt
		// based on item id
		Inventory inventory = null;

		try {
			inventory = inventoryManagementHelper.getInventoryForItemId(em, inventoryOrderReceipt.getItemId(),
					inventoryOrderReceipt.getLocationId(), false);
		} catch (NoResultException nre) {
			// no inventory found
			logger.severe("Inventory not found for : " + inventoryOrderReceipt.getItemId());
		}

		BigDecimal inventoryPrevQty = null;
		if (inventory == null) {
			inventory = inventoryManagementHelper.addInventory(em, inventoryOrderReceipt, defaultInventorythreashold,
					inventoryOrderReceipt.getLocationId(), httpRequest);

			InventoryItemDefault inventoryItemDefaultDB = inventoryManagementHelper.getInventoryItemDefaultForItemId(em,
					inventoryOrderReceipt.getItemId());
			// add inventory item default also for this one
			if (inventoryItemDefaultDB == null) {
				InventoryItemDefault inventoryItemDefault = new InventoryItemDefault();
				inventoryItemDefault.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				inventoryItemDefault.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				inventoryItemDefault.setCreatedBy(inventoryOrderReceipt.getCreatedBy());
				inventoryItemDefault.setItemId(inventoryOrderReceipt.getItemId());
				inventoryItemDefault.setEconomicOrderQuantity(new BigDecimal(0));
				inventoryItemDefault.setMinimumOrderQuantity(new BigDecimal(0));
				inventoryItemDefault.setD86Threshold(defaultInventorythreashold);
				inventoryItemDefault.setStatus("A");
				EntityTransaction tx = em.getTransaction();
				try {
					// start transaction
					tx.begin();
					em.persist(inventoryItemDefault);
					tx.commit();
				} catch (RuntimeException e) {
					// on error, if transaction active,
					// rollback
					if (tx != null && tx.isActive()) {
						tx.rollback();
					}
					throw e;
				}
			}
		} else {

			InventoryItemDefault itemDefault = inventoryManagementHelper.getInventoryItemDefault(inventory.getItemId(),
					em);
			if (itemDefault != null && itemDefault.getD86Threshold() != null) {
				defaultInventorythreashold = itemDefault.getD86Threshold();
			}
			// we have an item inventory, we must add this amount to
			// existing one
			// take threashold of this inventory
			inventoryPrevQty = inventory.getTotalAvailableQuanity();

			inventoryManagementHelper.updateInventory(httpRequest, em, inventory, inventoryOrderReceipt, true);
		}
		Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
				inventoryOrderReceipt.getItemId(), inventory, defaultInventorythreashold.doubleValue(), false,
				inventoryPrevQty, null);

		// we must send push as item threshold or is in stock is modified
		if (item != null) {
			// send packet for broadcast as item has been modified
			InventoryPostPacket inventoryPostPacketNew = inventoryManagementHelper.getPushPacket(inventory, null,
					defaultInventorythreashold);

			inventoryPostPacketNew.setMerchantId(postPacket.getMerchantId());
			inventoryPostPacketNew.setLocationId(postPacket.getLocationId());
			inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacketNew,
					POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());

		}

	}

	private void addOrUpdateInventoryWithItem(HttpServletRequest httpRequest, EntityManager em,
			InventoryOrderReceipt inventoryOrderReceipt,
			List<InventoryOrderReceiptForItem> inventoryOrderReceiptForItemList, BigDecimal defaultInventorythreashold,
			PostPacket postPacket) throws Exception {
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		// get inventory object corresponding to this inventory order receipt
		// based on item id
		for (InventoryOrderReceiptForItem inventoryOrderReceiptForItem : inventoryOrderReceiptForItemList) {

			Inventory inventory = null;

			try {
				inventory = inventoryManagementHelper.getInventoryForItemId(em,
						inventoryOrderReceiptForItem.getItemId(), inventoryOrderReceiptForItem.getLocationId(), false);
			} catch (NoResultException nre) {
				// no inventory found
				logger.severe("Inventory not found for : " + inventoryOrderReceiptForItem.getItemId());
			}

			BigDecimal inventoryPrevQty = null;
			if (inventory == null) {
				inventory = inventoryManagementHelper.addInventoryWithItem(em, inventoryOrderReceipt,
						inventoryOrderReceiptForItem, defaultInventorythreashold,
						inventoryOrderReceiptForItem.getLocationId(), httpRequest);

				InventoryItemDefault inventoryItemDefaultDB = inventoryManagementHelper
						.getInventoryItemDefaultForItemId(em, inventoryOrderReceipt.getItemId());
				// add inventory item default also for this one
				if (inventoryItemDefaultDB == null) {
					// add inventory item default also for this one
					InventoryItemDefault inventoryItemDefault = new InventoryItemDefault();
					inventoryItemDefault.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault.setCreatedBy(inventoryOrderReceiptForItem.getCreatedBy());
					inventoryItemDefault.setItemId(inventoryOrderReceiptForItem.getItemId());
					inventoryItemDefault.setEconomicOrderQuantity(new BigDecimal(0));
					inventoryItemDefault.setMinimumOrderQuantity(new BigDecimal(0));
					inventoryItemDefault.setD86Threshold(defaultInventorythreashold);
					inventoryItemDefault.setStatus("A");
					EntityTransaction tx = em.getTransaction();
					try {
						// start transaction
						tx.begin();
						em.persist(inventoryItemDefault);
						tx.commit();
					} catch (RuntimeException e) {
						// on error, if transaction active,
						// rollback
						if (tx != null && tx.isActive()) {
							tx.rollback();
						}
						throw e;
					}
				}
			} else {

				InventoryItemDefault itemDefault = inventoryManagementHelper
						.getInventoryItemDefault(inventory.getItemId(), em);
				if (itemDefault != null && itemDefault.getD86Threshold() != null) {
					defaultInventorythreashold = itemDefault.getD86Threshold();
				}
				// we have an item inventory, we must add this amount to
				// existing one
				// take threashold of this inventory
				inventoryPrevQty = inventory.getTotalAvailableQuanity();

				inventoryManagementHelper.updateInventoryWithItem(httpRequest, em, inventory, inventoryOrderReceipt,
						inventoryOrderReceiptForItem, true);
			}
			Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
					inventoryOrderReceiptForItem.getItemId(), inventory, defaultInventorythreashold.doubleValue(),
					false, inventoryPrevQty, null);

			// we must send push as item threshold or is in stock is modified
			if (item != null) {
				// send packet for broadcast as item has been modified
				InventoryPostPacket inventoryPostPacketNew = inventoryManagementHelper.getPushPacket(inventory, null,
						defaultInventorythreashold);

				inventoryPostPacketNew.setMerchantId(postPacket.getMerchantId());
				inventoryPostPacketNew.setLocationId(postPacket.getLocationId());
				inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacketNew,
						POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());

			}
		}

	}

	/**
	 * @param inventoryOrderReceipt
	 * @return
	 * @throws Exception
	 */
	private InventoryOrderReceipt addUpdateInventoryOrderReceipt(EntityManager em,
			InventoryOrderReceipt inventoryOrderReceipt) throws Exception {

		inventoryOrderReceipt.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		EntityTransaction tx = em.getTransaction();
		try {
			em.getTransaction().begin();
			if (inventoryOrderReceipt.getId() == 0) {
				em.persist(inventoryOrderReceipt);
			} else {
				em.merge(inventoryOrderReceipt);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}

		return inventoryOrderReceipt;
	}

	InventoryOrderReceipt updateInventoryOrderReceipt(HttpServletRequest httpRequest, EntityManager em,
			InventoryOrderReceipt inventoryOrderReceipt, PostPacket postPacket) throws Exception {

		BigDecimal defaultInventorythreashold = ConfigFileReader.getDefaultInventoryThreshold();
		InventoryPostPacket inventoryPostPacket = null;
		InventoryOrderReceipt inventoryOrderReceiptPrev = em.find(InventoryOrderReceipt.class,
				inventoryOrderReceipt.getId());
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
		BigDecimal prevInventoryQty = null;
		if (inventoryOrderReceiptPrev != null) {
			// check if item id has been updated or not
			if (inventoryOrderReceiptPrev.getItemId() != inventoryOrderReceipt.getItemId()) {

				Inventory inventory = null;

				try {
					inventory = inventoryManagementHelper.getInventoryForItemId(em,
							inventoryOrderReceiptPrev.getItemId(), inventoryOrderReceiptPrev.getLocationId(), false);
				} catch (NoResultException nre) {
					// no inventory found
					logger.severe("Inventory not found for : " + inventoryOrderReceiptPrev.getItemId());
				}

				// manage the old inventory
				if (inventory != null) {
					prevInventoryQty = inventory.getTotalAvailableQuanity();
					InventoryItemDefault inventoryItemDefault = inventoryManagementHelper
							.getInventoryItemDefault(inventory.getItemId(), em);
					if (inventoryItemDefault != null && inventoryItemDefault.getD86Threshold() != null) {
						defaultInventorythreashold = inventoryItemDefault.getD86Threshold();
					}

					inventory.setUpdatedBy(inventoryOrderReceipt.getUpdatedBy());
					inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					// check if quantity needs conversion, if yes convert
					// the qty

					// entered uom for inventory
					String posunitOfMeasurement = inventoryOrderReceiptPrev.getUnitOfMeasure();
					// what is given in po is sellable and is not stock
					// hence we need the
					// conversion
					if (inventory.getUnitOfMeasurementId().equals(posunitOfMeasurement)) {
						// we need conversion, convert sellable to stock
						BigDecimal stockQty = inventoryManagementHelper.getStockQtyAfterConversion(
								inventory.getUnitOfMeasurementId(), posunitOfMeasurement,
								inventoryOrderReceipt.getPurchasedQuantity(), em);
						// remove old qty from stock
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(stockQty));

					} else {
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity()
								.subtract(inventoryOrderReceiptPrev.getPurchasedQuantity()));
					}

					Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
							inventoryOrderReceiptPrev.getItemId(), inventory, defaultInventorythreashold.doubleValue(),
							false, prevInventoryQty, null);
					// we must send push as item threshold or is in stock is
					// modified
					if (item != null) {
						inventoryPostPacket = inventoryManagementHelper.getPushPacket(inventory, null,
								defaultInventorythreashold);
					}

					// update inventory and inventory oder receipt
					EntityTransaction tx = em.getTransaction();
					try {
						// start transaction
						tx.begin();
						em.merge(inventory);
						em.merge(inventoryOrderReceipt);
						tx.commit();
						new InsertIntoHistory().insertInventoryIntoHistory(httpRequest, inventory, em);
					} catch (RuntimeException e) {
						// on error, if transaction active,
						// rollback
						if (tx != null && tx.isActive()) {
							tx.rollback();
						}
						throw e;
					}
				}

				// now manage new inventory, it will send 2 pushes for item
				// in client
				addOrUpdateInventory(httpRequest, em, inventoryOrderReceipt, defaultInventorythreashold, postPacket);

			} else if (inventoryOrderReceiptPrev.getPurchasedQuantity() != inventoryOrderReceipt.getPurchasedQuantity()
					|| inventoryOrderReceiptPrev.getUnitOfMeasure() != inventoryOrderReceipt.getUnitOfMeasure()) {
				// check if purchased quantity is modified

				Inventory inventory = null;
				// inventoryManagementHelper.getInventoryForItemId(em,
				// inventoryOrderReceiptPrev.getItemId(),
				// inventoryOrderReceipt.getLocationId());

				try {
					inventory = inventoryManagementHelper.getInventoryForItemId(em,
							inventoryOrderReceiptPrev.getItemId(), inventoryOrderReceiptPrev.getLocationId(), false);
				} catch (NoResultException nre) {
					// no inventory found
					logger.severe("Inventory not found for : " + inventoryOrderReceiptPrev.getItemId());
				}

				// manage the old inventory
				if (inventory != null) {
					prevInventoryQty = inventory.getTotalAvailableQuanity();
					InventoryItemDefault inventoryItemDefault = inventoryManagementHelper
							.getInventoryItemDefault(inventory.getItemId(), em);
					if (inventoryItemDefault != null) {
						defaultInventorythreashold = inventoryItemDefault.getD86Threshold();
					}

					inventory.setUpdatedBy(inventoryOrderReceipt.getUpdatedBy());
					inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					// get stock qty now
					// get stock qty of old po
					// get the differene

					// REMOVE PREVIOS ONE and add new one
					// entered uom for inventory
					String posunitOfMeasurement = inventoryOrderReceiptPrev.getUnitOfMeasure();
					// what is given in po is sellable and is not stock
					// hence we need the
					// conversion
					if (inventory.getUnitOfMeasurementId().equals(posunitOfMeasurement)) {
						// we need conversion, convert sellable to stock
						BigDecimal stockQty = inventoryManagementHelper.getStockQtyAfterConversion(
								inventory.getUnitOfMeasurementId(), posunitOfMeasurement,
								inventoryOrderReceiptPrev.getPurchasedQuantity(), em);
						// remove old qty from stock
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(stockQty));

					} else {
						// remove old qty from stock
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity()
								.subtract(inventoryOrderReceiptPrev.getPurchasedQuantity()));
					}

					// add new one
					String posunitOfMeasurementNow = inventoryOrderReceipt.getUnitOfMeasure();
					// what is given in pos is sellable and is not stock
					// hence we need the
					// conversion
					if (!inventory.getUnitOfMeasurementId().equals(posunitOfMeasurementNow)) {
						// we need conversion, convert sellable to stock
						BigDecimal stockQty = inventoryManagementHelper.getStockQtyAfterConversion(
								inventory.getUnitOfMeasurementId(), posunitOfMeasurementNow,
								inventoryOrderReceipt.getPurchasedQuantity(), em);
						// add stock qty to the stock
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(stockQty));

					} else {
						// add to the stock
						inventory.setTotalAvailableQuanity(
								inventory.getTotalAvailableQuanity().add(inventoryOrderReceipt.getPurchasedQuantity()));
					}

					// update inventory and inventory oder receipt
					EntityTransaction tx = em.getTransaction();
					try {
						// start transaction
						tx.begin();
						em.merge(inventory);
						em.merge(inventoryOrderReceipt);
						tx.commit();
						new InsertIntoHistory().insertInventoryIntoHistory(httpRequest, inventory, em);
					} catch (RuntimeException e) {
						// on error, if transaction active,
						// rollback
						if (tx != null && tx.isActive()) {
							tx.rollback();
						}
						throw e;
					}

					Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
							inventoryOrderReceiptPrev.getItemId(), inventory, defaultInventorythreashold.doubleValue(),
							false, prevInventoryQty, null);
					// we must send push as item threshold or is in
					// stock is modified
					if (item != null) {
						inventoryPostPacket = inventoryManagementHelper.getPushPacket(inventory, null,
								defaultInventorythreashold);
						inventoryPostPacket.setMerchantId(postPacket.getMerchantId());
						inventoryPostPacket.setLocationId(postPacket.getLocationId());

						InventoryPushPacket inventoryPushPacket = new InventoryPushPacket();
						inventoryPushPacket.setInventoryPostPacket(inventoryPostPacket);
						inventoryPushPacket.setMerchantId(postPacket.getMerchantId());
						inventoryPushPacket.setLocationId(postPacket.getLocationId());
						inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacket,
								POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());
					}
				}
			}

			addUpdateInventoryOrderReceipt(em, inventoryOrderReceipt);

		}

		return inventoryOrderReceipt;

	}

	InventoryOrderReceipt deleteInventoryOrderReceipt(HttpServletRequest httpRequest, EntityManager em,
			InventoryOrderReceipt inventoryOrderReceipt, PostPacket postPacket) throws Exception {

		BigDecimal defaultInventorythreashold = ConfigFileReader.getDefaultInventoryThreshold();
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		BigDecimal inventoryPrevQty = null;

		InventoryOrderReceipt inventoryOrderReceiptPrev = em.find(InventoryOrderReceipt.class,
				inventoryOrderReceipt.getId());
		if (inventoryOrderReceiptPrev.getStatus().trim().equalsIgnoreCase("D") == false) {
			inventoryOrderReceiptPrev.setStatus("D");
			inventoryOrderReceiptPrev.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			EntityTransaction tx = em.getTransaction();
			try {
				// start transaction
				tx.begin();
				em.merge(inventoryOrderReceiptPrev);
				tx.commit();
			} catch (RuntimeException e) {
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}

			// if it was inactive no need to update inventory
			if (inventoryOrderReceiptPrev.getStatus().trim().equalsIgnoreCase("I") == false) {
				// get the inventory for item and remove the purchased
				// quantity

				Inventory inventory = null;

				// inventoryManagementHelper.getInventoryForItemId(em,
				// inventoryOrderReceiptPrev.getItemId(),
				// inventoryOrderReceiptPrev.getLocationId());
				try {
					inventory = inventoryManagementHelper.getInventoryForItemId(em,
							inventoryOrderReceiptPrev.getItemId(), inventoryOrderReceiptPrev.getLocationId(), false);
				} catch (NoResultException nre) {
					// no inventory found
					logger.severe("Inventory not found for : " + inventoryOrderReceiptPrev.getItemId());
				}

				if (inventory != null) {
					inventoryPrevQty = inventory.getTotalAvailableQuanity();

					inventory.setUpdatedBy(inventoryOrderReceipt.getUpdatedBy());
					inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					String posunitOfMeasurementPrev = inventoryOrderReceiptPrev.getUnitOfMeasure();
					// what is given in pos is sellable and is not stock
					// hence we need the
					// conversion
					if (!inventory.getUnitOfMeasurementId().equals(posunitOfMeasurementPrev)) {
						// we need conversion, convert sellable to stock
						BigDecimal stockQty = inventoryManagementHelper.getStockQtyAfterConversion(
								inventory.getUnitOfMeasurementId(), posunitOfMeasurementPrev,
								inventoryOrderReceiptPrev.getPurchasedQuantity(), em);
						// subtract stock qty to the stock
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(stockQty));

					} else {
						// subtract from the stock
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity()
								.subtract(inventoryOrderReceiptPrev.getPurchasedQuantity()));
					}

					tx = em.getTransaction();
					try {
						// start transaction
						tx.begin();
						em.merge(inventory);
						tx.commit();
						new InsertIntoHistory().insertInventoryIntoHistory(httpRequest, inventory, em);
					} catch (RuntimeException e) {
						// on error, if transaction active,
						// rollback
						if (tx != null && tx.isActive()) {
							tx.rollback();
						}
						throw e;
					}

					InventoryItemDefault inventoryItemDefault = inventoryManagementHelper
							.getInventoryItemDefault(inventoryOrderReceiptPrev.getItemId(), em);

					if (inventoryItemDefault != null && inventoryItemDefault.getD86Threshold() != null) {
						defaultInventorythreashold = inventoryItemDefault.getD86Threshold();
					}

					Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
							inventoryOrderReceiptPrev.getItemId(), inventory, defaultInventorythreashold.doubleValue(),
							false, inventoryPrevQty, null);
					// we must send push as item threshold or is in
					// stock is modified
					if (item != null) {
						InventoryPostPacket inventoryPostPacket = inventoryManagementHelper.getPushPacket(inventory,
								null, defaultInventorythreashold);
						inventoryPostPacket.setMerchantId(postPacket.getMerchantId());
						inventoryPostPacket.setLocationId(postPacket.getLocationId());

						InventoryPushPacket inventoryPushPacket = new InventoryPushPacket();
						inventoryPushPacket.setInventoryPostPacket(inventoryPostPacket);
						inventoryPushPacket.setMerchantId(postPacket.getMerchantId());
						inventoryPushPacket.setLocationId(postPacket.getLocationId());
						inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacket,
								POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());
					}
				}

			}

		}
		return inventoryOrderReceipt;

	}

	UnitOfMeasurement addUnitOfMeasurement(EntityManager em, UnitOfMeasurement unitOfMeasurement) throws Exception {

		// final String METHOD_NAME = getClass().getSimpleName()
		// + ".addUnitOfMeasurement()";

		unitOfMeasurement.setUomTypeId(1);
		unitOfMeasurement.setSellableQty(new BigDecimal(1));
		unitOfMeasurement.setStockQty(new BigDecimal(1));

		if (unitOfMeasurement.getDisplaySequence() == null) {
			unitOfMeasurement.setDisplaySequence("1");
		}
		unitOfMeasurement.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		EntityTransaction tx = em.getTransaction();
		try {
			// start transaction
			tx.begin();

			em.persist(unitOfMeasurement);
			tx.commit();
		} catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}

		unitOfMeasurement.setStockUomId(unitOfMeasurement.getId());

		tx = em.getTransaction();
		try {
			// start transaction
			tx.begin();
			em.merge(unitOfMeasurement);
			tx.commit();
		} catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}

		return unitOfMeasurement;
	}

	UnitOfMeasurement addUnitOfMeasurement(EntityManager em, HttpServletRequest httpRequest,
			UnitOfMeasurement unitOfMeasurement) throws Exception {

		// final String METHOD_NAME = getClass().getSimpleName()
		// + ".updateUnitOfMeasurement";

		List<UnitConversion> list = unitOfMeasurement.getUnitConversionList();
		String uomId = unitOfMeasurement.getId();

		unitOfMeasurement.setUomTypeId(1);
		unitOfMeasurement.setSellableQty(BigDecimal.ONE);
		unitOfMeasurement.setStockQty(BigDecimal.ONE);

		if (unitOfMeasurement.getDisplaySequence() == null) {
			unitOfMeasurement.setDisplaySequence("1");
		}
		unitOfMeasurement.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (unitOfMeasurement.getId() == null) {
			unitOfMeasurement.setId(new StoreForwardUtility().generateUUID());
		}
		logger.severe(
				"unitOfMeasurement======================$$$$$$$$$$$$$$$$$$$$$$$$$======================================"
						+ unitOfMeasurement.toString());
		unitOfMeasurement = em.merge(unitOfMeasurement);

		if (list != null) {
			for (UnitConversion conversion : list) {
				if (uomId == null) {
					conversion.setFromUOMId(unitOfMeasurement.getId());
					conversion.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				conversion.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				conversion = em.merge(conversion);
			}
		}
		// for inserting once default entry
		insertUnitConversion(unitOfMeasurement, em);
		return unitOfMeasurement;
	}

	UnitOfMeasurement updateUnitOfMeasurement(EntityManager em, HttpServletRequest httpRequest,
			UnitOfMeasurement unitOfMeasurement) throws Exception {

		// final String METHOD_NAME = getClass().getSimpleName()
		// + ".updateUnitOfMeasurement";
		List<UnitConversion> list = unitOfMeasurement.getUnitConversionList();
		List<UnitConversion> newList = unitOfMeasurement.getUnitConversionList();

		unitOfMeasurement.setUomTypeId(1);
		unitOfMeasurement.setSellableQty(new BigDecimal(1));
		unitOfMeasurement.setStockQty(new BigDecimal(1));

		if (unitOfMeasurement.getDisplaySequence() == null) {
			unitOfMeasurement.setDisplaySequence("1");
		}
		unitOfMeasurement.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		unitOfMeasurement = em.merge(unitOfMeasurement);
		if (list != null) {
			logger.severe(
					"list===========================================================================" + list.size());
			for (UnitConversion conversion : list) {
				logger.severe("conversion==========================================================================="
						+ conversion);

				if (conversion.getCreated() == null)
					conversion.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				conversion.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				conversion = em.merge(conversion);

			}
		}
		// for inserting once default entry
		// insertUnitConversion(unitOfMeasurement, em);

		unitOfMeasurement.setUnitConversionList(newList);
		return unitOfMeasurement;
	}

	public PhysicalInventory addPhysicalInventory(HttpServletRequest httpRequest, EntityManager em,
			PhysicalInventory physicalInventory, BigDecimal totalAvailableQuantity) {
		Inventory inventory = getInventoryById(httpRequest, em, physicalInventory.getInventoryId());

		BigDecimal actualQuantity = new BigDecimal(0);
		if (inventory != null) {
			actualQuantity = inventory.getTotalAvailableQuanity().subtract(physicalInventory.getQuantity());

		}

		physicalInventory.setActualQuantity(actualQuantity);
		physicalInventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		PhysicalInventory pInventory = getPhysicalInventoryByInventoryIdAndDate(httpRequest, em,
				physicalInventory.getInventoryId(), physicalInventory.getDate());

		EntityTransaction tx = em.getTransaction();
		try {
			// start transaction
			tx.begin();
			if (pInventory != null) {
				pInventory.setQuantity(physicalInventory.getQuantity());
				pInventory.setDate(physicalInventory.getDate());
				pInventory.setStatus(physicalInventory.getStatus());
				pInventory.setReasonId(physicalInventory.getReasonId());
				pInventory.setLocalTime(
						new TimezoneTime().getLocationSpecificTimeToAdd(physicalInventory.getLocationId(), em));
				// formula to determine quantity physical_inventory - previous
				// available qty(taq) + shortage_exces = shortage exceess
				BigDecimal excessShortage = pInventory.getQuantity().subtract(totalAvailableQuantity)
						.add(pInventory.getExcessShortage());
				pInventory.setExcessShortage(excessShortage);
				logger.severe(
						"pInventory===================================1111111111111111====================================="
								+ pInventory);
				logger.severe(
						"inventory===================================1111111111111111====================================="
								+ inventory);

				physicalInventory = em.merge(pInventory);
				em.merge(inventory);

				PhysicalInventoryHistory history = PhysicalInventoryHistory.setPhysicalInventoryHistory(pInventory);
				em.persist(history);
			} else {
				physicalInventory.setLocalTime(
						new TimezoneTime().getLocationSpecificTimeToAdd(physicalInventory.getLocationId(), em));
				physicalInventory.setExcessShortage(new BigDecimal(0));
				BigDecimal excessShortage = physicalInventory.getQuantity().subtract(totalAvailableQuantity)
						.add(physicalInventory.getExcessShortage());
				physicalInventory.setExcessShortage(excessShortage);
				if (physicalInventory.getId() == null) {
					physicalInventory.setId(new StoreForwardUtility().generateUUID());
				}
				em.persist(physicalInventory);
				logger.severe(
						"physicalInventory===================================2222222222222====================================="
								+ physicalInventory);

				PhysicalInventoryHistory history = PhysicalInventoryHistory
						.setPhysicalInventoryHistory(physicalInventory);
				em.persist(history);
			}
			tx.commit();
		} catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}

		return physicalInventory;

	}

	private Inventory getInventoryById(HttpServletRequest httpRequest, EntityManager em, String inventoryId) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Inventory> criteria = builder.createQuery(Inventory.class);
			Root<Inventory> r = criteria.from(Inventory.class);
			TypedQuery<Inventory> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(Inventory_.id), inventoryId), builder.notEqual(r.get(Inventory_.status), "D")));
			return query.getSingleResult();
		} catch (NoResultException noResultException) {
			logger.info(httpRequest, "No result found when searching for a getInventoryById.");
			return null;
		}

	}

	private PhysicalInventory getPhysicalInventoryByInventoryIdAndDate(HttpServletRequest httpRequest, EntityManager em,
			String inventoryId, String date) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PhysicalInventory> criteria = builder.createQuery(PhysicalInventory.class);
			Root<PhysicalInventory> r = criteria.from(PhysicalInventory.class);
			TypedQuery<PhysicalInventory> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(PhysicalInventory_.date), date),
							builder.equal(r.get(PhysicalInventory_.inventoryId), inventoryId),
							builder.notEqual(r.get(PhysicalInventory_.status), "D")));
			return query.getSingleResult();
		} catch (NoResultException noResultException) {

			return null;
		}

	}

	public PhysicalInventory updatePhysicalInventory(HttpServletRequest httpRequest, EntityManager em,
			PhysicalInventory physicalInventory) {
		physicalInventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		EntityTransaction tx = em.getTransaction();

		Inventory inventory = getInventoryById(httpRequest, em, physicalInventory.getInventoryId());

		BigDecimal actualQuantity = new BigDecimal(0);
		if (inventory != null) {
			actualQuantity = inventory.getTotalAvailableQuanity().subtract(physicalInventory.getQuantity());
		}

		physicalInventory.setActualQuantity(actualQuantity);

		try {
			// start transaction
			tx.begin();
			em.merge(physicalInventory);
			tx.commit();
		} catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}

		return physicalInventory;
	}

	public PhysicalInventory deletePhysicalInventory(HttpServletRequest httpRequest, EntityManager em,
			PhysicalInventory physicalInventory) {
		PhysicalInventory u = (PhysicalInventory) new CommonMethods().getObjectById("PhysicalInventory", em,
				PhysicalInventory.class, physicalInventory.getId());
		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		EntityTransaction tx = em.getTransaction();
		try {
			// start transaction
			tx.begin();
			em.merge(u);
			tx.commit();
		} catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}
		return u;
	}

	UnitOfMeasurement deleteUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement, HttpServletRequest httpRequest,
			EntityManager em) throws Exception {

		UnitOfMeasurement u = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,
				UnitOfMeasurement.class, unitOfMeasurement.getId());

		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		return u;
	}

	UnitConversion deleteUnitConversion(UnitConversion unitConversion, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		UnitConversion u = em.find(UnitConversion.class, unitConversion.getId());

		u.setStatus("D");
		u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(u);

		return u;
	}

	InventoryOrderReceipt addInventoryOrderReceiptWithItems(HttpServletRequest httpRequest, EntityManager em,
			InventoryOrderReceipt inventoryOrderReceipt,
			List<InventoryOrderReceiptForItem> inventoryOrderReceiptForItemList, PostPacket postPacket)
			throws Exception {

		BigDecimal defaultInventorythreashold = ConfigFileReader.getDefaultInventoryThreshold();

		// get the relation sets
		InventoryOrderReceipt objToReturn = addUpdateInventoryOrderReceiptWithItems(em, inventoryOrderReceipt,
				inventoryOrderReceiptForItemList);
		addOrUpdateInventoryWithItem(httpRequest, em, inventoryOrderReceipt, inventoryOrderReceiptForItemList,
				defaultInventorythreashold, postPacket);
		return objToReturn;

	}

	/*
	 * private InventoryOrderReceipt UpdateInventoryOrderReceiptWithItems(
	 * EntityManager em, InventoryOrderReceipt inventoryOrderReceipt,
	 * List<InventoryOrderReceiptForItem> inventoryOrderReceiptForItemList)
	 * throws Exception {
	 * 
	 * inventoryOrderReceipt.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
	 * EntityTransaction tx = em.getTransaction(); try {
	 * em.getTransaction().begin(); if (inventoryOrderReceipt.getId() == 0) {
	 * em.persist(inventoryOrderReceipt); } else {
	 * em.merge(inventoryOrderReceipt); } em.getTransaction().commit(); } catch
	 * (Exception e) { // on error, if transaction active, // rollback if (tx !=
	 * null && tx.isActive()) { tx.rollback(); } throw e; } int
	 * inventoryOrderReceiptId = inventoryOrderReceipt.getId(); for
	 * (InventoryOrderReceiptForItem inventoryOrderReceiptForItem :
	 * inventoryOrderReceiptForItemList) {
	 * 
	 * inventoryOrderReceiptForItem
	 * .setInventoryOrderReceiptId(inventoryOrderReceiptId); try {
	 * em.getTransaction().begin(); if (inventoryOrderReceiptForItem.getId() ==
	 * 0) { em.persist(inventoryOrderReceiptForItem); } else {
	 * em.merge(inventoryOrderReceiptForItem); } em.getTransaction().commit(); }
	 * catch (Exception e) { // on error, if transaction active, // rollback if
	 * (tx != null && tx.isActive()) { tx.rollback(); } throw e; } } return
	 * inventoryOrderReceipt; }
	 */

	private InventoryOrderReceipt addUpdateInventoryOrderReceiptWithItems(EntityManager em,
			InventoryOrderReceipt inventoryOrderReceipt,
			List<InventoryOrderReceiptForItem> inventoryOrderReceiptForItemList) throws Exception {

		inventoryOrderReceipt.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		EntityTransaction tx = em.getTransaction();
		try {
			em.getTransaction().begin();
			if (inventoryOrderReceipt.getId() == 0) {
				em.persist(inventoryOrderReceipt);
			} else {
				em.merge(inventoryOrderReceipt);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}
		int inventoryOrderReceiptId = inventoryOrderReceipt.getId();
		for (InventoryOrderReceiptForItem inventoryOrderReceiptForItem : inventoryOrderReceiptForItemList) {
			inventoryOrderReceiptForItem.setInventoryOrderReceiptId(inventoryOrderReceiptId);
			InventoryOrderReceiptForItem inventoryOrderReceiptForItemPrev = em.find(InventoryOrderReceiptForItem.class,
					inventoryOrderReceiptForItem.getId());

			try {
				em.getTransaction().begin();
				if (inventoryOrderReceiptForItemPrev == null && inventoryOrderReceiptForItem.getId() == 0) {
					em.persist(inventoryOrderReceiptForItem);
				} else {
					em.merge(inventoryOrderReceiptForItem);
				}
				em.getTransaction().commit();
			} catch (Exception e) {
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
		}
		return inventoryOrderReceipt;
	}

	InventoryOrderReceipt updateInventoryOrderReceiptWithItem(HttpServletRequest httpRequest, EntityManager em,
			InventoryOrderReceipt inventoryOrderReceipt,
			List<InventoryOrderReceiptForItem> inventoryOrderReceiptForItemList, PostPacket postPacket)
			throws Exception {
		BigDecimal defaultInventorythreashold = ConfigFileReader.getDefaultInventoryThreshold();
		InventoryPostPacket inventoryPostPacket = null;
		for (InventoryOrderReceiptForItem inventoryOrderReceiptForItem : inventoryOrderReceiptForItemList) {

			InventoryOrderReceiptForItem inventoryOrderReceiptForItemPrev = em.find(InventoryOrderReceiptForItem.class,
					inventoryOrderReceiptForItem.getId());
			InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
			BigDecimal prevInventoryQty = null;
			if (inventoryOrderReceiptForItemPrev != null) {
				// check if item id has been updated or not
				if (inventoryOrderReceiptForItemPrev.getItemId() != inventoryOrderReceiptForItem.getItemId()) {
					Inventory inventory = null;
					try {
						inventory = inventoryManagementHelper.getInventoryForItemId(em,
								inventoryOrderReceiptForItemPrev.getItemId(),
								inventoryOrderReceiptForItemPrev.getLocationId(), false);
					} catch (NoResultException nre) {
						// no inventory found
						logger.severe("Inventory not found for : " + inventoryOrderReceiptForItemPrev.getItemId());
					}

					// manage the old inventory
					if (inventory != null) {
						prevInventoryQty = inventory.getTotalAvailableQuanity();
						InventoryItemDefault inventoryItemDefault = inventoryManagementHelper
								.getInventoryItemDefault(inventory.getItemId(), em);
						if (inventoryItemDefault != null && inventoryItemDefault.getD86Threshold() != null) {
							defaultInventorythreashold = inventoryItemDefault.getD86Threshold();
						}

						inventory.setUpdatedBy(inventoryOrderReceiptForItem.getUpdatedBy());
						inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

						// check if quantity needs conversion, if yes convert
						// the qty

						// entered uom for inventory
						String posunitOfMeasurement = inventoryOrderReceiptForItemPrev.getUnitOfMeasure();
						// what is given in po is sellable and is not stock
						// hence we need the
						// conversion
						if (!inventory.getUnitOfMeasurementId().equals(posunitOfMeasurement)) {
							// we need conversion, convert sellable to stock
							BigDecimal stockQty = inventoryManagementHelper.getStockQtyAfterConversion(
									inventory.getUnitOfMeasurementId(), posunitOfMeasurement,
									inventoryOrderReceiptForItem.getPurchasedQuantity(), em);
							// remove old qty from stock
							inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(stockQty));

						} else {
							inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity()
									.subtract(inventoryOrderReceiptForItemPrev.getPurchasedQuantity()));
						}

						Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
								inventoryOrderReceiptForItemPrev.getItemId(), inventory,
								defaultInventorythreashold.doubleValue(), false, prevInventoryQty, null);
						// we must send push as item threshold or is in stock is
						// modified
						if (item != null) {
							inventoryPostPacket = inventoryManagementHelper.getPushPacket(inventory, null,
									defaultInventorythreashold);
						}

						// update inventory and inventory oder receipt
						EntityTransaction tx = em.getTransaction();
						try {
							// start transaction
							tx.begin();
							em.merge(inventory);
							em.merge(inventoryOrderReceiptForItem);
							tx.commit();
							new InsertIntoHistory().insertInventoryIntoHistory(httpRequest, inventory, em);
						} catch (RuntimeException e) {
							// on error, if transaction active,
							// rollback
							if (tx != null && tx.isActive()) {
								tx.rollback();
							}
							throw e;
						}
					}

					// now manage new inventory, it will send 2 pushes for item
					// in client
					addOrUpdateInventoryWithItem(httpRequest, em, inventoryOrderReceipt,
							inventoryOrderReceiptForItemList, defaultInventorythreashold, postPacket);

				} else if (inventoryOrderReceiptForItemPrev.getPurchasedQuantity() != inventoryOrderReceiptForItem
						.getPurchasedQuantity()
						|| inventoryOrderReceiptForItemPrev.getUnitOfMeasure() != inventoryOrderReceiptForItem
								.getUnitOfMeasure()) {
					// check if purchased quantity is modified

					Inventory inventory = null;
					// inventoryManagementHelper.getInventoryForItemId(em,
					// inventoryOrderReceiptPrev.getItemId(),
					// inventoryOrderReceipt.getLocationId());

					try {
						inventory = inventoryManagementHelper.getInventoryForItemId(em,
								inventoryOrderReceiptForItemPrev.getItemId(),
								inventoryOrderReceiptForItemPrev.getLocationId(), false);
					} catch (NoResultException nre) {
						// no inventory found
						logger.severe("Inventory not found for : " + inventoryOrderReceiptForItemPrev.getItemId());
					}

					// manage the old inventory
					if (inventory != null) {
						prevInventoryQty = inventory.getTotalAvailableQuanity();
						InventoryItemDefault inventoryItemDefault = inventoryManagementHelper
								.getInventoryItemDefault(inventory.getItemId(), em);
						if (inventoryItemDefault != null) {
							defaultInventorythreashold = inventoryItemDefault.getD86Threshold();
						}

						inventory.setUpdatedBy(inventoryOrderReceiptForItem.getUpdatedBy());
						inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

						// get stock qty now
						// get stock qty of old po
						// get the differene

						// REMOVE PREVIOS ONE and add new one
						// entered uom for inventory
						String posunitOfMeasurement = inventoryOrderReceiptForItemPrev.getUnitOfMeasure();
						// what is given in po is sellable and is not stock
						// hence we need the
						// conversion
						if (!inventory.getUnitOfMeasurementId().equals(posunitOfMeasurement)) {
							// we need conversion, convert sellable to stock
							BigDecimal stockQty = inventoryManagementHelper.getStockQtyAfterConversion(
									inventory.getUnitOfMeasurementId(), posunitOfMeasurement,
									inventoryOrderReceiptForItemPrev.getPurchasedQuantity(), em);
							// remove old qty from stock
							inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(stockQty));

						} else {
							// remove old qty from stock
							inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity()
									.subtract(inventoryOrderReceiptForItemPrev.getPurchasedQuantity()));
						}

						// add new one
						String posunitOfMeasurementNow = inventoryOrderReceiptForItem.getUnitOfMeasure();
						// what is given in pos is sellable and is not stock
						// hence we need the
						// conversion
						if (!inventory.getUnitOfMeasurementId().equals(posunitOfMeasurementNow)) {
							// we need conversion, convert sellable to stock
							BigDecimal stockQty = inventoryManagementHelper.getStockQtyAfterConversion(
									inventory.getUnitOfMeasurementId(), posunitOfMeasurementNow,
									inventoryOrderReceiptForItem.getPurchasedQuantity(), em);
							// add stock qty to the stock
							inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(stockQty));

						} else {
							// add to the stock
							inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity()
									.add(inventoryOrderReceiptForItem.getPurchasedQuantity()));
						}

						// update inventory and inventory oder receipt
						EntityTransaction tx = em.getTransaction();
						try {
							// start transaction
							tx.begin();
							em.merge(inventory);
							em.merge(inventoryOrderReceiptForItem);
							tx.commit();
							new InsertIntoHistory().insertInventoryIntoHistory(httpRequest, inventory, em);
						} catch (RuntimeException e) {
							// on error, if transaction active,
							// rollback
							if (tx != null && tx.isActive()) {
								tx.rollback();
							}
							throw e;
						}

						Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
								inventoryOrderReceiptForItemPrev.getItemId(), inventory,
								defaultInventorythreashold.doubleValue(), false, prevInventoryQty, null);
						// we must send push as item threshold or is in
						// stock is modified
						if (item != null) {
							inventoryPostPacket = inventoryManagementHelper.getPushPacket(inventory, null,
									defaultInventorythreashold);
							inventoryPostPacket.setMerchantId(postPacket.getMerchantId());
							inventoryPostPacket.setLocationId(postPacket.getLocationId());

							InventoryPushPacket inventoryPushPacket = new InventoryPushPacket();
							inventoryPushPacket.setInventoryPostPacket(inventoryPostPacket);
							inventoryPushPacket.setMerchantId(postPacket.getMerchantId());
							inventoryPushPacket.setLocationId(postPacket.getLocationId());
							inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacket,
									POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());
						}
					}
				}
			} else {
				// now manage new inventory, it will send 2 pushes for item
				// in client
				addOrUpdateInventoryWithSingleItem(httpRequest, em, inventoryOrderReceipt, inventoryOrderReceiptForItem,
						defaultInventorythreashold, postPacket);
			}
		}
		addUpdateInventoryOrderReceiptWithItems(em, inventoryOrderReceipt, inventoryOrderReceiptForItemList);

		return inventoryOrderReceipt;

	}

	private void addOrUpdateInventoryWithSingleItem(HttpServletRequest httpRequest, EntityManager em,
			InventoryOrderReceipt inventoryOrderReceipt, InventoryOrderReceiptForItem inventoryOrderReceiptForItem,
			BigDecimal defaultInventorythreashold, PostPacket postPacket) throws Exception {
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		// get inventory object corresponding to this inventory order receipt
		// based on item id

		Inventory inventory = null;

		try {
			inventory = inventoryManagementHelper.getInventoryForItemId(em, inventoryOrderReceiptForItem.getItemId(),
					inventoryOrderReceiptForItem.getLocationId(), false);
		} catch (NoResultException nre) {
			// no inventory found
			logger.severe("Inventory not found for : " + inventoryOrderReceiptForItem.getItemId());
		}

		BigDecimal inventoryPrevQty = null;
		if (inventory == null) {
			inventory = inventoryManagementHelper.addInventoryWithItem(em, inventoryOrderReceipt,
					inventoryOrderReceiptForItem, defaultInventorythreashold,
					inventoryOrderReceiptForItem.getLocationId(), httpRequest);

			InventoryItemDefault inventoryItemDefaultDB = inventoryManagementHelper.getInventoryItemDefaultForItemId(em,
					inventoryOrderReceipt.getItemId());
			// add inventory item default also for this one
			if (inventoryItemDefaultDB == null) {
				// add inventory item default also for this one
				InventoryItemDefault inventoryItemDefault = new InventoryItemDefault();
				inventoryItemDefault.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				inventoryItemDefault.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				inventoryItemDefault.setCreatedBy(inventoryOrderReceiptForItem.getCreatedBy());
				inventoryItemDefault.setItemId(inventoryOrderReceiptForItem.getItemId());
				inventoryItemDefault.setEconomicOrderQuantity(new BigDecimal(0));
				inventoryItemDefault.setMinimumOrderQuantity(new BigDecimal(0));
				inventoryItemDefault.setD86Threshold(defaultInventorythreashold);
				inventoryItemDefault.setStatus("A");
				EntityTransaction tx = em.getTransaction();
				try {
					// start transaction
					tx.begin();
					em.persist(inventoryItemDefault);
					tx.commit();
				} catch (RuntimeException e) {
					// on error, if transaction active,
					// rollback
					if (tx != null && tx.isActive()) {
						tx.rollback();
					}
					throw e;
				}
			}
		} else {

			InventoryItemDefault itemDefault = inventoryManagementHelper.getInventoryItemDefault(inventory.getItemId(),
					em);
			if (itemDefault != null && itemDefault.getD86Threshold() != null) {
				defaultInventorythreashold = itemDefault.getD86Threshold();
			}
			// we have an item inventory, we must add this amount to
			// existing one
			// take threashold of this inventory
			inventoryPrevQty = inventory.getTotalAvailableQuanity();

			inventoryManagementHelper.updateInventoryWithItem(httpRequest, em, inventory, inventoryOrderReceipt,
					inventoryOrderReceiptForItem, true);
		}
		Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
				inventoryOrderReceiptForItem.getItemId(), inventory, defaultInventorythreashold.doubleValue(), false,
				inventoryPrevQty, null);

		// we must send push as item threshold or is in stock is modified
		if (item != null) {
			// send packet for broadcast as item has been modified
			InventoryPostPacket inventoryPostPacketNew = inventoryManagementHelper.getPushPacket(inventory, null,
					defaultInventorythreashold);

			inventoryPostPacketNew.setMerchantId(postPacket.getMerchantId());
			inventoryPostPacketNew.setLocationId(postPacket.getLocationId());
			inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacketNew,
					POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());

		}

	}

	UnitOfMeasurement addMultipleLocationUnitOfMeasurement(HttpServletRequest httpRequest, EntityManager em,
			UnitOfMeasurement unitOfMeasurement, UOMPostPacket uomPostPacket, HttpServletRequest request)
			throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		String[] locationIds = null;
		if (uomPostPacket.getLocationsListId() != null && uomPostPacket.getLocationsListId().length() > 0) {
			locationIds = uomPostPacket.getLocationsListId().split(",");
		}
		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (unitOfMeasurement != null && uomPostPacket.getIsBaseLocationUpdate() == 1) {

			// adding or updating global item
			List<UnitConversion> globalList = unitOfMeasurement.getUnitConversionList();
			logger.severe(
					"baseLocation.getId()======================$$$$$$$$$$$$$$$$$$$$$$$$$======================================"
							+ baseLocation.getId());
			if (unitOfMeasurement.getLocationId() == null) {
				unitOfMeasurement.setLocationId(baseLocation.getId());
			}

			UnitOfMeasurement globalUnitOfMeasurement = addUnitOfMeasurement(em, request, unitOfMeasurement);

			// now add/update child location
			if (locationIds != null) {
				uomPostPacket.setLocationsListId("");
				for (String locationId : locationIds) {
					String locationsId = locationId;
					if (locationId != null && !locationId.equals(baseLocation.getId())) {
						uomPostPacket.setLocalServerURL(0);

						String json = new StoreForwardUtility().returnJsonPacket(uomPostPacket, "UOMPostPacket",
								httpRequest);
						logger.severe("json============================================================" + json);

						new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, baseLocation.getId(),
								Integer.parseInt(uomPostPacket.getMerchantId()));

						UnitOfMeasurement localUnitOfMeasurement = new UnitOfMeasurement()
								.getUnitOfMeasurement(unitOfMeasurement);
						localUnitOfMeasurement.setGlobalId(globalUnitOfMeasurement.getId());
						localUnitOfMeasurement.setLocationId(locationId);
						// localUnitOfMeasurement.getUnitConversionList().clear();
						// localUnitOfMeasurement = addUnitOfMeasurement(em,
						// request, localUnitOfMeasurement);

						List<UnitConversion> newList = new ArrayList<UnitConversion>();
						if (globalList != null) {
							for (UnitConversion conversion : globalList) {

								UnitConversion conversion1 = new UnitConversion().getUnitConversion(conversion);
								// UnitOfMeasurement fromUOM=
								// getUnitOfMeasurementByGlobalUOMIdAndLocationId(em,
								// locationsId, conversion.getFromUOMId());
								UnitOfMeasurement toUOM = getUnitOfMeasurementByGlobalUOMIdAndLocationId(em,
										locationsId, conversion.getToUOMId(), false);

								if (toUOM != null) {
									// conversion1.setFromUOMId(fromUOM.getId());
									conversion1.setToUOMId(toUOM.getId());
									newList.add(conversion1);
								}

							}
						}

						localUnitOfMeasurement.setUnitConversionList(newList);
						addUnitOfMeasurement(em, request, localUnitOfMeasurement);
						uomPostPacket.setUnitOfMeasurement(localUnitOfMeasurement);
						uomPostPacket.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(uomPostPacket, "UOMPostPacket",
								httpRequest);
						logger.severe("json2============================================================" + json2);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, httpRequest, locationId,
								Integer.parseInt(uomPostPacket.getMerchantId()));

					}
				}
			}
		}

		return unitOfMeasurement;
	}

	UnitOfMeasurement updateMultipleLocationUnitOfMeasurement(HttpServletRequest httpRequest, EntityManager em,
			UnitOfMeasurement unitOfMeasurement, UOMPostPacket uomPostPacket, HttpServletRequest request)
			throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		String[] locationIds = null;
		if (uomPostPacket.getLocationsListId().trim().length() > 0) {
			locationIds = uomPostPacket.getLocationsListId().split(",");
		}
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (unitOfMeasurement != null && uomPostPacket.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
			List<UnitConversion> globalList = unitOfMeasurement.getUnitConversionList();

			UnitOfMeasurement globalUnitOfMeasurement = updateUnitOfMeasurement(em, request, unitOfMeasurement);
			em.getTransaction().commit();
			em.getTransaction().begin();
			// globalUnitOfMeasurement.setUnitConversionList(globalList);
			uomPostPacket.setUnitOfMeasurement(globalUnitOfMeasurement);
			uomPostPacket.setLocationsListId("");
			// now add/update child location
			if (locationIds != null) {

				for (String locationId : locationIds) {
					String locationsId = locationId;
					if (locationId.length() > 0 && !locationId.equals((baseLocation.getId()))) {

						uomPostPacket.setLocalServerURL(0);
						String json = new StoreForwardUtility().returnJsonPacket(uomPostPacket, "UOMPostPacket",
								httpRequest);

						new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, baseLocation.getId(),
								Integer.parseInt(uomPostPacket.getMerchantId()));

						UnitOfMeasurement localUnitOfMeasurement = new UnitOfMeasurement()
								.getUnitOfMeasurement(unitOfMeasurement);
						UnitOfMeasurement uoms = getUnitOfMeasurementByGlobalUOMIdAndLocationId(em, locationsId,
								globalUnitOfMeasurement.getId(), false);
						if (uoms != null && uoms.getId() != null) {

							localUnitOfMeasurement.setGlobalId(uoms.getGlobalId());
							localUnitOfMeasurement.setId(uoms.getId());
						} else {
							localUnitOfMeasurement.setGlobalId(globalUnitOfMeasurement.getId());
						}

						localUnitOfMeasurement.setLocationId(locationsId);

						List<UnitConversion> newList = new ArrayList<UnitConversion>();
						if (globalList != null) {
							for (UnitConversion conversion : globalList) {
								UnitOfMeasurement fromUOM = getUnitOfMeasurementByGlobalUOMIdAndLocationId(em,
										locationsId, conversion.getFromUOMId(), false);
								UnitOfMeasurement toUOM = getUnitOfMeasurementByGlobalUOMIdAndLocationId(em,
										locationsId, conversion.getToUOMId(), false);
								UnitConversion conversion1 = new UnitConversion().getUnitConversion(conversion);
								if (fromUOM != null && toUOM != null) {
									UnitConversion unitConversionTemp = getUnitConversion(em, fromUOM.getId(),
											toUOM.getId());

									if (unitConversionTemp != null) {
										conversion1.setId(unitConversionTemp.getId());
										conversion1.setCreated(unitConversionTemp.getCreated());
									}

									conversion1.setFromUOMId(fromUOM.getId());
									conversion1.setToUOMId(toUOM.getId());
									newList.add(conversion1);
								}
							}
						}
						localUnitOfMeasurement.setUnitConversionList(newList);
						// updating child uom
						localUnitOfMeasurement = updateUnitOfMeasurement(em, request, localUnitOfMeasurement);

						uomPostPacket.setUnitOfMeasurement(localUnitOfMeasurement);
						uomPostPacket.setLocalServerURL(0);
						logger.severe("uomPostPacket============================================================"
								+ uomPostPacket);
						String json2 = new StoreForwardUtility().returnJsonPacket(uomPostPacket, "UOMPostPacket",
								httpRequest);
						logger.severe("json2============================================================" + json2);
						new StoreForwardUtility().callSynchPacketsWithServer(json2, httpRequest, locationId,
								Integer.parseInt(uomPostPacket.getMerchantId()));
					}

				}
			}
		}
		return unitOfMeasurement;
	}

	UnitOfMeasurement deleteMultipleLocationUnitOfMeasurement(EntityManager em, UnitOfMeasurement unitOfMeasurement,
			UOMPostPacket uomPostPacket, HttpServletRequest request) throws Exception {
		// delete baselocation
		unitOfMeasurement = deleteUnitOfMeasurement(unitOfMeasurement, request, em);
		// get all sublocations
		List<UnitOfMeasurement> uoms = getAllUnitOfMeasurementByGlobalUOMId(unitOfMeasurement.getId(), em);
		// delete sublocation
		for (UnitOfMeasurement uom : uoms) {
			deleteUnitOfMeasurement(uom, request, em);
		}
		return unitOfMeasurement;
	}

	UnitConversion deleteMultipleLocationUnitConversion(EntityManager em, UnitConversion unitConversion,
			UnitConversionPostPacket unitConversionPostPacket, HttpServletRequest request) throws Exception {
		// delete baselocation
		unitConversion = deleteUnitConversion(unitConversion, request, em);
		// get all sublocations

		// getting base location
		Location baseLocation = new CommonMethods().getBaseLocation(em);

		List<String> locationsId = new CommonMethods().getAllActiveLocations(request, em);
		for (String locationId : locationsId) {
			if (!locationId.equals(baseLocation.getId())) {

				UnitOfMeasurement unitOfMeasurementFrom = getUnitOfMeasurementByGlobalUOMIdAndLocationId(em, locationId,
						unitConversion.getFromUOMId(), true);

				UnitOfMeasurement UnitOfMeasurementTo = getUnitOfMeasurementByGlobalUOMIdAndLocationId(em, locationId,
						unitConversion.getToUOMId(), true);

				if (unitOfMeasurementFrom != null && UnitOfMeasurementTo != null) {
					UnitConversion unitConversionInDbLocal = null;

					try {
						String queryString = "select s from UnitConversion s where s.fromUOMId =? and s.toUOMId=?  ";
						TypedQuery<UnitConversion> query = em.createQuery(queryString, UnitConversion.class)
								.setParameter(1, unitOfMeasurementFrom.getId())
								.setParameter(2, UnitOfMeasurementTo.getId());
						unitConversionInDbLocal = query.getSingleResult();
					} catch (Exception e) {

						logger.severe("No UnitConversion Found " + unitOfMeasurementFrom.getId() + " "
								+ UnitOfMeasurementTo.getId());
					}

					if (unitConversionInDbLocal != null) {
						deleteUnitConversion(unitConversionInDbLocal, request, em);

					}
				}

			}

		}

		return unitConversion;
	}

	private UnitOfMeasurement getUnitOfMeasurementByGlobalUOMIdAndLocationId(EntityManager em, String locationId,
			String globalUOMId, boolean isDelete) {

		UnitOfMeasurement measurement = null;
		try {
			String queryString = "select s from UnitOfMeasurement s where s.globalId = ? and s.locationId = ? and s.status != 'D'";
			TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class)
					.setParameter(1, globalUOMId).setParameter(2, locationId);
			measurement = query.getSingleResult();

		} catch (NoResultException e) {

			logger.severe(e);

		}
		if (!isDelete) {
			if (measurement == null) {
				UnitOfMeasurement global = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement",
						em, UnitOfMeasurement.class, globalUOMId);
				measurement = new UnitOfMeasurement().getUnitOfMeasurement(global);
				measurement.setLocationId(locationId);
				measurement.setGlobalId(globalUOMId);
				if (measurement.getId() == null) {
					measurement.setId(new StoreForwardUtility().generateUUID());
				}
				measurement = em.merge(measurement);
			}
		}

		return measurement;
	}

	List<UnitOfMeasurement> getAllUnitOfMeasurementByGlobalUOMId(String globalUOMId, EntityManager em) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UnitOfMeasurement> criteria = builder.createQuery(UnitOfMeasurement.class);
			Root<UnitOfMeasurement> uom = criteria.from(UnitOfMeasurement.class);
			TypedQuery<UnitOfMeasurement> query = em.createQuery(
					criteria.select(uom).where(builder.equal(uom.get(UnitOfMeasurement_.globalId), globalUOMId)));
			return query.getResultList();
		} catch (Exception e) {

			logger.severe(e, "No Result found");
		}
		return null;
	}

	public InventoryItemBom updateItemToBOM(InventoryItemBom inventoryItemToBOM, EntityManager em) throws Exception {
		if (inventoryItemToBOM != null) {
			inventoryItemToBOM.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			if (inventoryItemToBOM.getId() == 0) {
				em.persist(inventoryItemToBOM);
				inventoryItemToBOM.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			} else {
				inventoryItemToBOM = em.merge(inventoryItemToBOM);
			}

		}
		return inventoryItemToBOM;
	}

	public InventoryItemBom deleteItemToBOM(InventoryItemBom inventoryItemToBOM, EntityManager em) throws Exception {
		InventoryItemBom inventoryItemToBOMInDb = null;
		if (inventoryItemToBOM != null) {

			inventoryItemToBOMInDb = em.find(InventoryItemBom.class, inventoryItemToBOM.getId());

			if (inventoryItemToBOMInDb != null) {

				inventoryItemToBOMInDb.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				inventoryItemToBOMInDb.setUpdatedBy(inventoryItemToBOM.getUpdatedBy());
				inventoryItemToBOMInDb.setStatus("D");
				inventoryItemToBOMInDb = em.merge(inventoryItemToBOMInDb);
			}

		}
		return inventoryItemToBOMInDb;
	}

	public InventoryAttributeBOM deleteAttributeToBOM(InventoryAttributeBOM inventoryAttributeToBOM, EntityManager em)
			throws Exception {
		if (inventoryAttributeToBOM != null) {

			InventoryAttributeBOM inventoryAttributeToBOMInDb = em.find(InventoryAttributeBOM.class,
					inventoryAttributeToBOM.getId());

			if (inventoryAttributeToBOMInDb != null) {
				inventoryAttributeToBOMInDb.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				inventoryAttributeToBOMInDb.setUpdatedBy(inventoryAttributeToBOM.getUpdatedBy());
				inventoryAttributeToBOMInDb.setStatus("D");
				em.merge(inventoryAttributeToBOMInDb);

			}

		}
		return inventoryAttributeToBOM;
	}

	public InventoryAttributeBOM updateAttributeToBOM(InventoryAttributeBOM inventoryItemToBOM, EntityManager em,
			InventoryAttributeToBomPacket packet) throws Exception {
		if (inventoryItemToBOM != null) {
			inventoryItemToBOM.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			logger.severe(
					"inventoryItemToBOM.getId()========================================================================"
							+ inventoryItemToBOM.getId());
			logger.severe(
					"packet.getLocalServerURL()========================================================================"
							+ packet.getLocalServerURL());

			if (inventoryItemToBOM.getId() == 0 && packet.getLocalServerURL() == 0) {
				em.persist(inventoryItemToBOM);
				inventoryItemToBOM.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				inventoryItemToBOM = em.merge(inventoryItemToBOM);
			} else {
				inventoryItemToBOM = em.merge(inventoryItemToBOM);
			}

		}
		return inventoryItemToBOM;
	}

	InventoryItemBom updateMultipleLocationInventoryToBom(EntityManager em, InventoryItemBom inventoryItemToBOM,
			InventoryToBomPacket packet, HttpServletRequest request) throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		String[] locationIds = null;
		if (packet.getLocationId().trim().length() > 0) {
			locationIds = packet.getLocationsListId().split(",");
		}

		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		logger.severe("inventoryItemToBOM========================================================================="
				+ inventoryItemToBOM);
		if (inventoryItemToBOM != null && packet.getIsBaseLocationUpdate() == 1) {
			// adding or updating global item
			InventoryItemBom globalCourse = updateItemToBOM(inventoryItemToBOM, em);
			logger.severe(
					"globalCourse==============================2222222222222222222222==========================================="
							+ globalCourse);

			// now add/update child location
			if (locationIds != null) {
				packet.setLocationsListId("");
				List<InventoryItemBom> result = new ArrayList<InventoryItemBom>();

				for (String locationId : locationIds) {

					logger.severe(
							"locationId=================================================================" + locationId);

					String locationsId = locationId;
					if (!locationsId.equals(baseLocation.getId())) {
						String json = new StoreForwardUtility().returnJsonPacket(packet, "InventoryToBomPacket",
								request);

						logger.severe("json=================================================================" + json);
						// call synchPacket for store forward
						new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
								Integer.parseInt(packet.getMerchantId()));
						InventoryItemBom local = new InventoryItemBom().getInventoryItemBom(inventoryItemToBOM);
						Item itemIdFg = getItemByGlobalItemIdAndLocationId(em, locationsId, local.getItemIdFg());
						Item itemIdRm = getItemByGlobalItemIdAndLocationId(em, locationsId, local.getItemIdRm());

						UnitOfMeasurement uom = getUnitOfMeasurementByGlobalIdAndLocationId(em, locationsId,
								globalCourse.getRmSellableUom());
						if (itemIdFg != null && itemIdRm != null) {
							InventoryItemBom bom = getInventoryItemBOMByGlobalIdAndLocationId(em, itemIdFg.getId(),
									itemIdRm.getId());

							if (bom != null) {
								local.setId(bom.getId());
							}
							local.setItemIdFg(itemIdFg.getId());
							local.setItemIdRm(itemIdRm.getId());
							local.setRmSellableUom(uom.getId());
							updateItemToBOM(local, em);
							result.add(local);
						}

						if (result.size() > 0) {
							packet.setInventoryItemToBOMs(result);
							packet.setLocalServerURL(0);
							String json2 = new StoreForwardUtility().returnJsonPacket(packet, "InventoryToBomPacket",
									request);
							logger.severe(
									"json2=================================================================" + json2);

							// call synchPacket for store forward
							new StoreForwardUtility().callSynchPacketsWithServer(json2, request, baseLocation.getId(),
									Integer.parseInt(packet.getMerchantId()));
						}
					}

				}
			}
		}
		return inventoryItemToBOM;
	}

	private InventoryItemBom getInventoryItemBOMByGlobalIdAndLocationId(EntityManager em, String attributeIdFg,
			String itemIdRm) {
		try {
			String queryString = "select s from InventoryItemBom s where s.itemIdFg =? and s.itemIdRm=?  ";
			TypedQuery<InventoryItemBom> query = em.createQuery(queryString, InventoryItemBom.class)
					.setParameter(1, attributeIdFg).setParameter(2, itemIdRm);
			return query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);

		}
		return null;
	}

	private InventoryItemBom getInventoryItemBOMByGlobalIdAndLocationIdAndUOM(EntityManager em, String attributeIdFg,
			String itemIdRm, String uom) {
		try {
			String queryString = "select s from InventoryItemBom s where s.itemIdFg =? and s.itemIdRm=? and s.rmSellableUom=? ";
			TypedQuery<InventoryItemBom> query = em.createQuery(queryString, InventoryItemBom.class)
					.setParameter(1, attributeIdFg).setParameter(2, itemIdRm).setParameter(3, uom);
			return query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);

		}
		return null;
	}

	private InventoryAttributeBOM getInventoryAttributeBOMByGlobalIdAndLocationIdAndUOM(EntityManager em,
			String attributeIdFg, String itemIdRm, String uom) {
		try {
			String queryString = "select s from InventoryAttributeBOM s where s.attributeIdFg =? and s.itemIdRm=? and s.rmSellableUom=? ";
			TypedQuery<InventoryAttributeBOM> query = em.createQuery(queryString, InventoryAttributeBOM.class)
					.setParameter(1, attributeIdFg).setParameter(2, itemIdRm).setParameter(3, uom);
			return query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);

		}
		return null;
	}

	Item getItemByGlobalItemIdAndLocationId(EntityManager em, String locationId, String globalItemId) {
		try {
			String queryString = "select s from Item s where s.globalItemId =? and s.locationsId=? and s.status !='D'  ";
			TypedQuery<Item> query = em.createQuery(queryString, Item.class).setParameter(1, globalItemId)
					.setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			logger.severe("No Result found");
		}
		return null;
	}

	// private InventoryItemBom
	// getInventoryItemBomByGlobalInventoryItemBomIdAndLocationId(EntityManager
	// em, String locationId, String globalId)
	// {
	// try
	// {
	// String queryString =
	// "select s from InventoryItemBom s where s.globalId =? and s.locationsId=?
	// and s.status!='D' ";
	// TypedQuery<InventoryItemBom> query = em.createQuery(queryString,
	// InventoryItemBom.class).setParameter(1, globalId).setParameter(2,
	// locationId);
	// return query.getSingleResult();
	// }
	// catch (NoResultException e)
	// {
	//
	// logger.severe(e,
	// MessageConstants.ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE);
	//
	// }
	// return null;
	// }

	InventoryAttributeBOM updateMultipleLocationInventoryAttributeBOM(EntityManager em,
			InventoryAttributeBOM inventoryToBOM, InventoryAttributeToBomPacket packet, HttpServletRequest request)
			throws Exception {
		// getting location if from admin client it will not include
		// baselocation
		Location baseLocation = new CommonMethods().getBaseLocation(em);
		String[] locationIds = null;
		if (packet.getLocationsListId().trim().length() > 0) {
			locationIds = packet.getLocationsListId().split(",");
		}
		// checking two condition
		// Wther packet recieved is for global level update or location level
		// update
		// if condition handles :-
		// 1. Item add in all locations
		// 2. Item update in location
		if (inventoryToBOM != null && packet.getIsBaseLocationUpdate() == 1) {
			List<InventoryAttributeBOM> globalResult = new ArrayList<InventoryAttributeBOM>();

			// adding or updating global item
			InventoryAttributeBOM global = updateAttributeToBOM(inventoryToBOM, em, packet);
			globalResult.add(global);
			packet.setInventoryAttributeBOMList(globalResult);
			// now add/update child location
			if (locationIds != null) {
				packet.setLocationsListId("");
				List<InventoryAttributeBOM> result = new ArrayList<InventoryAttributeBOM>();

				for (String locationId : locationIds) {
					String locationsId = locationId;
					if (!locationsId.equals(baseLocation.getId())) {

						String json = new StoreForwardUtility().returnJsonPacket(packet,
								"InventoryAttributeToBomPacket", request);

						// call synchPacket for store forward
						new StoreForwardUtility().callSynchPacketsWithServer(json, request, baseLocation.getId(),
								Integer.parseInt(packet.getMerchantId()));

						InventoryAttributeBOM local = new InventoryAttributeBOM()
								.getInventoryAttributeBOM(inventoryToBOM);
						ItemsAttribute itemIdFg = getItemsAttributeByGlobalIdAndLocationId(em, locationsId,
								local.getAttributeIdFg());
						Item itemIdRm = getItemByGlobalItemIdAndLocationId(em, locationsId, local.getItemIdRm());

						UnitOfMeasurement uom = getUnitOfMeasurementByGlobalIdAndLocationId(em, locationsId,
								inventoryToBOM.getRmSellableUom());

						if (itemIdFg != null && itemIdRm != null && uom != null) {

							InventoryAttributeBOM bom = getInventoryAttributeBOMByGlobalIdAndLocationId(em,
									itemIdFg.getId(), itemIdRm.getId());
							if (bom != null) {
								local.setId(bom.getId());
							}
							local.setAttributeIdFg(itemIdFg.getId());
							local.setItemIdRm(itemIdRm.getId());
							local.setRmSellableUom(uom.getId());
							updateAttributeToBOM(local, em, packet);
							result.add(local);
						}
						packet.setInventoryAttributeBOMList(result);
						packet.setLocalServerURL(0);
						String json2 = new StoreForwardUtility().returnJsonPacket(packet,
								"InventoryAttributeToBomPacket", request);

						// call synchPacket for store forward
						new StoreForwardUtility().callSynchPacketsWithServer(json2, request, baseLocation.getId(),
								Integer.parseInt(packet.getMerchantId()));

					}
				}
			}
		}
		return inventoryToBOM;
	}

	private InventoryAttributeBOM getInventoryAttributeBOMByGlobalIdAndLocationId(EntityManager em,
			String attributeIdFg, String itemIdRm) {
		try {
			String queryString = "select s from InventoryAttributeBOM s where s.attributeIdFg =? ";
			TypedQuery<InventoryAttributeBOM> query = em.createQuery(queryString, InventoryAttributeBOM.class)
					.setParameter(1, attributeIdFg);// .setParameter(2,
													// itemIdRm);
			return query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);

		}
		return null;
	}

	private ItemsAttribute getItemsAttributeByGlobalIdAndLocationId(EntityManager em, String locationId,
			String globalId) {
		try {
			String queryString = "select s from ItemsAttribute s where s.globalId =? and s.locationsId=? and s.status!='D' ";
			TypedQuery<ItemsAttribute> query = em.createQuery(queryString, ItemsAttribute.class)
					.setParameter(1, globalId).setParameter(2, locationId);
			return query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);

		}
		return null;
	}

	private UnitOfMeasurement getUnitOfMeasurementByGlobalIdAndLocationId(EntityManager em, String locationId,
			String globalId) {
		UnitOfMeasurement measurement = null;
		try {
			logger.severe("locationId====================================================================="+locationId);
			logger.severe("globalId====================================================================="+globalId);
			
			String queryString = "select s from UnitOfMeasurement s where s.globalId =? and s.locationId=? and s.status!='D'  ";
			TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class)
					.setParameter(1, globalId).setParameter(2, locationId);
			measurement = query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);

		}
		// if UOM is not present in that location
		if (measurement == null) {
			UnitOfMeasurement unitOfMeasurement = (UnitOfMeasurement) new CommonMethods()
					.getObjectById("UnitOfMeasurement", em, UnitOfMeasurement.class, globalId);
			if(unitOfMeasurement!=null){
				measurement = unitOfMeasurement.getUnitOfMeasurement(unitOfMeasurement);
				measurement.setLocationId(locationId);
				if (measurement.getId() == null) {
					measurement.setId(new StoreForwardUtility().generateUUID());
				}
				measurement = em.merge(measurement);
			}
			
		}
		return measurement;
	}

	public String getItemsIngredientDisplayByLocationId(String name, ItemByLocationIdPacket itemByLocationIdPacket,
			EntityManager em, HttpServletRequest httpRequest) throws Exception {

		List<ItemsIngredientDisplay> ans = new ArrayList<ItemsIngredientDisplay>();
		String temp = Utilities.convertAllSpecialCharForSearch(name);

		// int inventoeyItemTypeId=getItemsType(em);
		/*
		 * String sql =
		 * "select  i.id,i.image_name,i.display_name as item_display_name,c.display_name as c_display_name,uom.display_name as uom_display_name"
		 * + " from   items i  " +
		 * " left join category_items ci on (ci.items_id = i.id and ci.status !='D') "
		 * + " left join category c on c.id=ci.category_id  " +
		 * " left join unit_of_measurement uom on uom.id=i.stock_uom  " +
		 * " left join items_type it on it.id = i.item_type  " +
		 * " where c.display_name !='Raw Material' " +
		 * " and  i.display_name like '%" + temp +
		 * "%' and it.name != 'Inventory Only'";
		 */

		String sql = "select  i.id,i.image_name,i.display_name as item_display_name,c.display_name as c_display_name,uom.display_name as uom_display_name"
				+ " from   items i  " + " left join category_items ci on (ci.items_id = i.id and ci.status !='D') "
				+ " left join category c on c.id=ci.category_id  "
				+ " left join unit_of_measurement uom on uom.id=i.stock_uom  "
				+ " left join items_type it on it.id = i.item_type  " + " where i.display_name like '%" + temp
				+ "%' and it.name != 'Inventory Only'";

		if (itemByLocationIdPacket.getCategoryId() != null && itemByLocationIdPacket.getCategoryId().length() > 0) {

			sql += " and c.id in (" + itemByLocationIdPacket.getCategoryId() + ")";
		}
		sql += "  and i.locations_id=? and i.status !='D' limit " + itemByLocationIdPacket.getStartIndex() + ","
				+ itemByLocationIdPacket.getEndIndex();

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, itemByLocationIdPacket.getLocationId())
				.getResultList();
		for (Object[] objRow : resultList) {
			ItemsIngredientDisplay detailPacket = new ItemsIngredientDisplay();
			detailPacket.setId((String) objRow[0]);
			detailPacket.setImageName((String) objRow[1]);
			detailPacket.setItemDisplayName((String) objRow[2]);
			detailPacket.setCategorydisplayName((String) objRow[3]);
			detailPacket.setStockUOMDisplayName((String) objRow[4]);

			ans.add(detailPacket);
		}

		return new JSONUtility(httpRequest).convertToJsonString(ans);
	}

	RequestOrder addRequestOrderWithItems(HttpServletRequest httpRequest, EntityManager em, RequestOrder requestOrder,
			RequestOrderPacket requestOrderPacket) throws Exception {

		// get the relation sets
		RequestOrder objToReturn = addUpdateRequestOrderWithItems(em, requestOrder, httpRequest, requestOrderPacket);
		new InsertIntoHistory().insertRequestOrderIntoHistory(httpRequest, objToReturn, em);

		return objToReturn;

	}

	RequestOrder receiveOrderWithItems(HttpServletRequest httpRequest, EntityManager em, RequestOrder requestOrder,
			PostPacket postPacket, RequestOrderPacket requestOrderPacket) throws Exception {

		// get the relation sets
		RequestOrder order = null;
		boolean isDirectRecieve = false;
		if (requestOrder.getId() != null) {
			order = getRequestOrderById(em, requestOrder.getId(), false);
		}
		String grnDateNew = requestOrder.getGrnDate();
		String departmentId = requestOrder.getDepartmentId();

		if (order == null) {
			// fem For diorect receive
			isDirectRecieve = true;
			requestOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			List<RequestOrderDetailItems> detailItemsList = requestOrder.getRequestOrderDetailItems();
			if (requestOrder.getId() == null) {
				requestOrder.setId(new StoreForwardUtility().generateNewNumber(requestOrder.getLocationId(), em,
						"request_order", httpRequest));
			}
			requestOrder = em.merge(requestOrder);
			List<RequestOrderDetailItems> tempList = new ArrayList<RequestOrderDetailItems>();
			if (detailItemsList != null) {
				for (RequestOrderDetailItems detailItems : detailItemsList) {
					if (detailItems.getBalance() == null) {
						detailItems.setBalance(new BigDecimal(0));
					}
					detailItems.setRequestId(requestOrder.getId());
					if (detailItems.getId() == null) {
						detailItems.setId(new StoreForwardUtility().generateUUID());
					}
					detailItems = em.merge(detailItems);
					tempList.add(detailItems);
				}
			}

			requestOrder.setRequestOrderDetailItems(tempList);
			order = getRequestOrderById(em, requestOrder.getId(), false);

		}
		requestOrderPacket.setRequestOrder(requestOrder);
		String json = new StoreForwardUtility().returnJsonPacket(requestOrderPacket, "RequestOrderPacket", httpRequest);

		logger.severe("order=========================$$$$$$$$$$$$$$$$$=======================================" + order);
		order.setStatusId(requestOrder.getStatusId());

		order.setRequestOrderDetailItems(requestOrder.getRequestOrderDetailItems());
		// calculation after inserting into system are as follows

		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				requestOrder.getStatusId());
		logger.severe("orderStatus=========================$$$$$$$$$$$$$$$$$======================================="
				+ orderStatus);

		String orderStatusName = orderStatus.getName();

		// deductSemiFinishedGoodsInventory(em, requestOrder, httpRequest,
		// requestOrder.getRequestOrderDetailItems());

		List<InventoryItemsList> inventoryItemsLists = new ArrayList<InventoryItemsList>();
		try {

			if (!orderStatusName.equals("PO Forcefully Closed") && !orderStatusName.equals("Request Forcefully Closed")
					&& !orderStatusName.equals("Request Cancelled") && !orderStatusName.equals("PO Cancelled")) {
				addOrUpdateInventoryWithItemForPOReceived(httpRequest, em, order,
						requestOrder.getRequestOrderDetailItems(), new BigDecimal(0), postPacket, inventoryItemsLists);
			}
		} catch (Exception e) {
			logger.severe(e);
		}
		// handle inventory for refund
		// refundSemiFinishedGoodsInventory(em, requestOrder, httpRequest,
		// order.getRequestOrderDetailItems());
		List<RequestOrderDetailItems> newList = new ArrayList<RequestOrderDetailItems>();
		List<RequestOrderDetailItems> newListForGRN = new ArrayList<RequestOrderDetailItems>();
		if (postPacket.getLocalServerURL() == 0) {
			order.setGrnCount(order.getGrnCount() + 1);
		}
		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				order.getLocationId());
		String grnNumber;

		if (order.getRequestOrderDetailItems() != null && !isDirectRecieve) {
			for (RequestOrderDetailItems detailItem : order.getRequestOrderDetailItems()) {
				RequestOrderDetailItems oldRODI = (RequestOrderDetailItems) new CommonMethods().getObjectById(
						"RequestOrderDetailItems", em, RequestOrderDetailItems.class, detailItem.getId());

				// calculating total receive
				BigDecimal receivedQty = detailItem.getReceivedQuantity().add(detailItem.getAllotmentQty());

				detailItem.setReceivedQuantity(receivedQty);
				detailItem.setAllotmentQty(detailItem.getAllotmentQty());
				// calculating balance qty
				BigDecimal balanceQty = detailItem.getQuantity().subtract(detailItem.getReceivedQuantity());
				detailItem.setBalance(balanceQty);
				newListForGRN.add(detailItem);
				if (oldRODI.getTotal() == null) {
					oldRODI.setTotal(new BigDecimal(0));
				}
				if (oldRODI.getTax() == null) {
					oldRODI.setTax(new BigDecimal(0));
				}
				if (detailItem.getTotal() == null) {
					detailItem.setTotal(new BigDecimal(0));
				}
				if (detailItem.getTax() == null) {
					detailItem.setTax(new BigDecimal(0));
				}
				if (oldRODI.getPrice() == null) {
					oldRODI.setPrice(new BigDecimal(0));
				}
				if (detailItem.getPrice() == null) {
					detailItem.setPrice(new BigDecimal(0));
				}
				detailItem.setTotal(oldRODI.getTotal().add(detailItem.getTotal()));
				detailItem.setTax(oldRODI.getTax().add(detailItem.getTax()));

				detailItem.setPriceTax1(oldRODI.getPriceTax1().add(detailItem.getPriceTax1()));
				detailItem.setPriceTax2(oldRODI.getPriceTax2().add(detailItem.getPriceTax2()));
				detailItem.setPriceTax3(oldRODI.getPriceTax3().add(detailItem.getPriceTax3()));
				detailItem.setPriceTax4(oldRODI.getPriceTax4().add(detailItem.getPriceTax4()));

				detailItem.setPrice(oldRODI.getPrice().add(detailItem.getPrice()));
				BigDecimal unitPurchasedPrice = new BigDecimal(0);

				if (detailItem.getReceivedQuantity() != null && detailItem.getReceivedQuantity().doubleValue() > 0) {
					unitPurchasedPrice = detailItem.getTotal().divide(detailItem.getReceivedQuantity(), 2,
							BigDecimal.ROUND_HALF_UP);
				}

				detailItem.setUnitPurchasedPrice(unitPurchasedPrice);
				newList.add(detailItem);

			}
		}
		if (isDirectRecieve) {
			newList = order.getRequestOrderDetailItems();
		}
		String name = location.getName().replace("(", "").replace(")", "");
		String[] locationNameString = name.split(" ");
		String locationName = "";
		for (String locationNameObj : locationNameString) {
			locationName = locationName + locationNameObj.substring(0, 1);
		}
		grnNumber = locationName + "-" + order.getId() + "-" + order.getGrnCount();

		String grnDate;
		if (requestOrder.getGrnDate() == null) {
			grnDate = requestOrder.getDate();
		} else {
			grnDate = requestOrder.getGrnDate();
		}

		try {
			logger.severe(
					"orderStatusName====================================================================================="
							+ orderStatusName);
			if (!orderStatusName.equals("PO Forcefully Closed") && !orderStatusName.equals("Request Forcefully Closed")
					&& !orderStatusName.equals("Request Cancelled") && !orderStatusName.equals("PO Cancelled")) {
				Location location2 = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
						requestOrder.getSupplierId());

				/*
				 * if(requestOrder.getSupplierId().equals(requestOrder.
				 * getLocationId())) { isDirectRecieve = true; }
				 */

				if (location2 != null && location2.getLocationsTypeId() == 1) {
					insertGoodsReceiveNotes(em, newList, grnNumber, true, grnDate, true, isDirectRecieve, false,
							inventoryItemsLists, requestOrderPacket.getSupplierRefNo(), httpRequest,grnDateNew,departmentId);
				} else {
					insertGoodsReceiveNotes(em, newList, grnNumber, false, grnDate, true, isDirectRecieve, false,
							inventoryItemsLists, requestOrderPacket.getSupplierRefNo(), httpRequest,grnDateNew,departmentId);
				}

			}
		} catch (Exception e) {
			logger.severe(e);
		}

		order.setRequestOrderDetailItems(newList);
		RequestOrder objToReturn = addUpdateRequestOrderWithItems(em, order, httpRequest, requestOrderPacket);
		new InsertIntoHistory().insertRequestOrderIntoHistory(httpRequest, objToReturn, em);
		objToReturn.setChallanNumber(grnNumber);

		// call synchPacket for store forward
		new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, requestOrderPacket.getLocationId(),
				Integer.parseInt(requestOrderPacket.getMerchantId()));

		return objToReturn;

	}

	ItemToSupplier updateItemToSupplier(HttpServletRequest httpRequest, EntityManager em, ItemToSupplier itemToSupplier)
			throws Exception {

		String queryString = "select its from ItemToSupplier its  where its.itemId =" + itemToSupplier.getItemId() + "";
		TypedQuery<ItemToSupplier> query = em.createQuery(queryString, ItemToSupplier.class);
		ItemToSupplier itemToSupplierDB = query.getSingleResult();

		itemToSupplierDB.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		itemToSupplierDB.setUpdatedBy(itemToSupplier.getUpdatedBy());

		String primary = itemToSupplierDB.getPrimarySupplierId();
		String sencond = itemToSupplierDB.getSecondarySupplierId();
		String tertiary = itemToSupplierDB.getTertiarySupplierId();

		if (itemToSupplier.getPrimarySupplierId() == itemToSupplierDB.getPrimarySupplierId()) {

			itemToSupplierDB.setPrimarySupplierId(primary);
			itemToSupplierDB.setSecondarySupplierId(sencond);
			itemToSupplierDB.setTertiarySupplierId(tertiary);

		} else if (itemToSupplier.getPrimarySupplierId() == itemToSupplierDB.getSecondarySupplierId()) {
			itemToSupplierDB.setPrimarySupplierId(sencond);
			itemToSupplierDB.setSecondarySupplierId(primary);
			itemToSupplierDB.setTertiarySupplierId(tertiary);

		} else if (itemToSupplier.getPrimarySupplierId() == itemToSupplierDB.getTertiarySupplierId()) {
			itemToSupplierDB.setPrimarySupplierId(tertiary);
			itemToSupplierDB.setSecondarySupplierId(primary);
			itemToSupplierDB.setTertiarySupplierId(sencond);
		}

		// itemToSupplierDB.setTertiarySupplierId(itemToSupplierDB.getSecondarySupplierId());
		// itemToSupplierDB.setSecondarySupplierId(itemToSupplierDB.getPrimarySupplierId());
		// itemToSupplierDB.setPrimarySupplierId(itemToSupplier.getPrimarySupplierId());

		itemToSupplierDB = em.merge(itemToSupplierDB);

		return itemToSupplierDB;

	}

	private RequestOrder addUpdateRequestOrderWithItems(EntityManager em, RequestOrder requestOrder,
			HttpServletRequest httpRequest, RequestOrderPacket requestOrderPacket) throws Exception {

		List<RequestOrderDetailItems> detailItems = requestOrder.getRequestOrderDetailItems();
		List<RequestOrderDetailItems> details = new ArrayList<RequestOrderDetailItems>();
		List<RequestOrderDetailItems> orderDetailItemsForSync = new ArrayList<RequestOrderDetailItems>();

		requestOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		if (requestOrder.getId() != null) {
			RequestOrder requestOrderTemp = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,
					RequestOrder.class, requestOrder.getId());

			if (requestOrderTemp != null) {
				requestOrder.setCreated(requestOrderTemp.getCreated());
			}
		}
		requestOrder.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getLocationId(), em));

		OrderStatus orderS = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				requestOrder.getStatusId());
		Location supplier = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				requestOrder.getLocationId());

		if (orderS != null && orderS.getName().equals("PO Created") && supplier.getIsGlobalLocation() == 0) {
			// int currentBatch = 0;
			try {
				PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
				batchManager.getCurrentBatchIdBySession(httpRequest, em, requestOrder.getLocationId(), true,
						requestOrderPacket, requestOrder.getUpdatedBy());
			} catch (IOException | InvalidSessionException e) {
				// TODO Auto-generated catch block
				logger.severe(httpRequest,
						"no active batch detail found for locationId: " + requestOrder.getSupplierId());
			}

		}

		if (requestOrder.getId() == null) {

			requestOrder.setId(new StoreForwardUtility().generateNewNumber(requestOrder.getLocationId(), em,
					"request_order", httpRequest));
		}

		requestOrder = em.merge(requestOrder);
		if (requestOrder.getIsPOOrder() == 1) {

			// generate PurchaseOrderId and insert in db
			RequestOrder requestOrderTemp = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,
					RequestOrder.class, requestOrder.getId());
			requestOrder.setPurchaseOrderId(requestOrder.getId());
			requestOrder
					.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getLocationId(), em));
			requestOrder.setCreated(requestOrderTemp.getCreated());
			requestOrder = em.merge(requestOrder);
		}

		String requestOrderId = requestOrder.getId();

		OrderDetailStatus orderDetailStatus = getOrderDetailStatusByNameAndLocationId(em, "PO Item Requested",
				requestOrder.getLocationId());
		for (RequestOrderDetailItems requestOrderDetailItem : detailItems) {

			requestOrderDetailItem
					.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getLocationId(), em));
			requestOrderDetailItem.setRequestId(requestOrderId);
			if (requestOrderDetailItem.getBalance() == null) {
				requestOrderDetailItem.setBalance(BigDecimal.ZERO);
			}
			if (requestOrderDetailItem.getId() == null) {
				requestOrderDetailItem.setId(new StoreForwardUtility().generateUUID());
			}
			requestOrderDetailItem = em.merge(requestOrderDetailItem);
			int statusId = requestOrderDetailItem.getStatusId();
			RequestOrderDetailItems orderDetailItemForSync = new RequestOrderDetailItems();
			orderDetailItemForSync = requestOrderDetailItem;
			logger.severe(
					"orderDetailItemForSync.getStatusId()==========================11111111111111====================================="
							+ orderDetailItemForSync.getStatusId());

			logger.severe(
					"requestOrderDetailItem.getStatusId()=========================1111111111111111111======================================"
							+ requestOrderDetailItem.getStatusId());
			if (requestOrderDetailItem.getStatusId() == orderDetailStatus.getId()) {
				Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
						requestOrderDetailItem.getItemsId());
				updateOrderDetailItems(em, item.getId(), requestOrder.getLocationId());
			}
			details.add(requestOrderDetailItem);
			orderDetailItemForSync.setStatusId(statusId);
			orderDetailItemsForSync.add(orderDetailItemForSync);
			logger.severe(
					"orderDetailItemForSync.getStatusId()==========================2222222222222====================================="
							+ orderDetailItemForSync.getStatusId());
			logger.severe(
					"requestOrderDetailItem.getStatusId()=============================222222222222222=================================="
							+ requestOrderDetailItem.getStatusId());

		}

		deductSemiFinishedGoodsInventory(em, requestOrder, httpRequest, details);
		refundSemiFinishedGoodsInventory(em, requestOrder, httpRequest, details);
		RequestOrderPacket orderPacketForSync = new RequestOrderPacket();
		orderPacketForSync = requestOrderPacket;
		requestOrder.setRequestOrderDetailItems(orderDetailItemsForSync);
		orderPacketForSync.setRequestOrder(requestOrder);
		String json = new StoreForwardUtility().returnJsonPacket(orderPacketForSync, "RequestOrderPacket", httpRequest);

		// call synchPacket for store forward
		logger.severe(
				"json=====================================================================================" + json);
		new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacketForSync.getLocationId(),
				Integer.parseInt(orderPacketForSync.getMerchantId()));

		requestOrder.setRequestOrderDetailItems(details);

		String orderStatusName = ((OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				requestOrder.getStatusId())).getName();

		// making sales order after request order order confirm
		if (requestOrder.getSupplierId() != null) {
			supplier = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					requestOrder.getSupplierId());

			if (requestOrder.getIsPOOrder() == 1 && supplier.getLocationsTypeId() == 1
					&& orderStatusName.equals("PO Created") && supplier.getIsGlobalLocation() == 0) {
				createOrderHeaderForRequestOrder(em, requestOrder, httpRequest, requestOrderPacket);
			}
		}

		// cancel order related to po
		try {

			if (orderStatusName.equals("PO Forcefully Closed") || orderStatusName.equals("PO Cancelled")
					|| orderStatusName.equals("Request Cancelled")
					|| orderStatusName.equals("Request Forcefully Closed")) {

				String queryString = "select l from OrderHeader l where l.poRefrenceNumber =" + requestOrder.getId();
				TypedQuery<OrderHeader> query = em.createQuery(queryString, OrderHeader.class);
				OrderHeader orderHeader = query.getSingleResult();

				OrderStatus orderHeaderStatusName = getOrderStatusByNameAndLocationAndSourceGroupId(em, "Order Paid",
						requestOrder.getSupplierId(), requestOrder.getOrderSourceGroupId());

				if (orderHeader != null && !(orderHeader.getAmountPaid().doubleValue() > 0
						|| orderHeaderStatusName.getId() == orderHeader.getOrderStatusId())) {

					String queryStringStatus = "select s from OrderStatus s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'";
					TypedQuery<OrderStatus> queryS = em.createQuery(queryStringStatus, OrderStatus.class)
							.setParameter(1, "Cancel Order").setParameter(2, requestOrder.getSupplierId())
							.setParameter(3, requestOrder.getOrderSourceGroupId());
					OrderStatus orderStatus = queryS.getSingleResult();

					orderHeader.setOrderStatusId(orderStatus.getId());
					orderHeader = em.merge(orderHeader);
					new InsertIntoHistory().insertOrderIntoHistory(httpRequest, orderHeader, em);
				}
			}
		} catch (Exception e) {
			logger.severe("No order header found for " + requestOrder.getId() + "" + e);
		}

		return requestOrder;
	}

	public void sendPushForOrder(OrderHeader header, OrderPacket orderPacket, HttpServletRequest httpRequest)
			throws NirvanaXPException {

		OrderHeader orderHeaderForPush = new OrderServiceForPost().getOrderHeaderWithMinimunRequiredDetails(header);
		orderPacket.setOrderHeader(orderHeaderForPush);
		new OrderServiceForPost().sendPacketForBroadcast(httpRequest, orderPacket,
				POSNServiceOperations.OrderManagementService_update.name(), false);
	}

	public OrderPacket createOrderPacket(OrderHeader header, RequestOrderPacket requestOrderPacket) {
		OrderPacket packet = new OrderPacket();
		packet.setClientId(requestOrderPacket.getClientId());
		packet.setEchoString(requestOrderPacket.getEchoString());
		packet.setLocationId(requestOrderPacket.getLocationId());
		packet.setMerchantId(requestOrderPacket.getMerchantId());
		packet.setSchemaName(requestOrderPacket.getSchemaName());
		packet.setSessionId(requestOrderPacket.getSessionId());
		packet.setIdOfSessionUsedByPacket(requestOrderPacket.getIdOfSessionUsedByPacket());
		packet.setOrderHeader(header);
		return packet;
	}

	private void deductSemiFinishedGoodsInventory(EntityManager em, RequestOrder requestOrder,
			HttpServletRequest httpRequest, List<RequestOrderDetailItems> detailItems) {
		// TODO Auto-generated method stub

		// This code will exucute in case semi finished goods
		// and here inventory will deduct
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				requestOrder.getStatusId());
		if (orderStatus.getName().equals("PO Received") || orderStatus.getName().equals("PO Partially Received")) {

			try {
				LocationsType locationsType = getSupplierTypeByName(em, "In House Production");
				Location supplier = getLocationById(em, requestOrder.getSupplierId());

				if (supplier.getLocationsTypeId() == locationsType.getId()) {
					addOrUpdateInventoryWithItemForPOReceived(httpRequest, em, requestOrder, detailItems,
							new BigDecimal(0), null, null);
				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.severe(e1);
			}

			if (detailItems != null && detailItems.size() > 0) {
				for (RequestOrderDetailItems detailItem : detailItems) {
					try {

						Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
								detailItem.getItemsId());

						ItemsType itemsType = getItemsTypeByName(em, "Semi Finished Goods");
						LocationsType locationsType = getSupplierTypeByName(em, "In House Production");
						Location supplier = getLocationById(em, requestOrder.getSupplierId());

						if (item.getItemType() == itemsType.getId()
								&& supplier.getLocationsTypeId() == locationsType.getId()) {
							handleInventoryForNewItem(httpRequest, detailItem, em, requestOrder);
						}

					} catch (Exception e) {
						logger.severe(e);
					}
				}
			}
		}
	}

	public void deductIntraLocationInventory(EntityManager em, RequestOrder requestOrder,
			HttpServletRequest httpRequest, List<RequestOrderDetailItems> detailItems,
			List<InventoryItemsList> inventoryItemsList) {
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				requestOrder.getStatusId());

		if (orderStatus.getName().equals("Request Partially Processed")
				|| orderStatus.getName().equals("Request In Process"))

		{
			// deduct inventory for intra location while allotment
			LocationsType grouping = getSupplierTypeByName(em, "grouping");
			Location supplier = getLocationById(em, requestOrder.getSupplierId());
			if (supplier != null && supplier.getLocationsTypeId() != null
					&& supplier.getLocationsTypeId() == grouping.getId()) {
				try {
					reduceInventoryForIntraAllotment(httpRequest, em, requestOrder, detailItems, new BigDecimal(0),
							null, inventoryItemsList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.severe(e);
				}
			}
		}
	}

	private void refundSemiFinishedGoodsInventory(EntityManager em, RequestOrder requestOrder,
			HttpServletRequest httpRequest, List<RequestOrderDetailItems> detailItems) {
		// TODO Auto-generated method stub

		// This code will exucute in case semi finished goods
		// and here inventory will deduct
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				requestOrder.getStatusId());
		if (orderStatus.getName().equals("PO Cancelled")) {
			if (detailItems != null && detailItems.size() > 0) {
				for (RequestOrderDetailItems detailItem : detailItems) {
					try {

						Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
								detailItem.getItemsId());

						ItemsType itemsType = getItemsTypeByName(em, "Semi Finished Goods");
						if (item.getItemType() == itemsType.getId()) {
							handleInventoryForItemCancel(httpRequest, detailItem, em, requestOrder);
						}

					} catch (Exception e) {
						logger.severe(e);
					}
				}
			}
		}

	}

	private String getLocalItemFromGlobalId(EntityManager em, String locationId, String globalItemId) {
		try {
			String queryString = "select s from Item s where s.globalItemId =? and s.locationsId=? and s.status!='D'  ";
			TypedQuery<Item> query = em.createQuery(queryString, Item.class).setParameter(1, globalItemId)
					.setParameter(2, locationId);
			return query.getSingleResult().getId();
		} catch (Exception e) {
			logger.severe("No Item Entry found for globalId " + globalItemId + " locationsId " + locationId);
		}
		return null;
	}

	private void handleInventoryForNewItem(HttpServletRequest httpServletRequest,
			RequestOrderDetailItems requesrOrderDetailItem, EntityManager em, RequestOrder requestOrder)
			throws NirvanaXPException {

		List<Inventory> inventoryList = new ArrayList<Inventory>();
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
		// get local location
		Location globalLocation = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				requesrOrderDetailItem.getRequestTo());
		if (globalLocation != null) {

			String localItemId;
			Location global = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					requestOrder.getLocationId());
			if (global.getIsGlobalLocation() == 1) {
				localItemId = getLocalItemFromGlobalId(em, globalLocation.getInventoryDeductionBusinessId(),
						requesrOrderDetailItem.getItemsId());
			} else {
				localItemId = requesrOrderDetailItem.getItemsId();
			}

			if (localItemId != null) {
				List<InventoryItemBom> inventoryItemBoms = inventoryManagementHelper
						.getInventoryItemBomForItemId(localItemId, em);

				if (inventoryItemBoms != null) {
					for (InventoryItemBom inventoryItemBom : inventoryItemBoms) {
						Inventory inventory = null;
						try {
							inventory = inventoryManagementHelper.getInventoryForItemId(em,
									inventoryItemBom.getItemIdRm(), requestOrder.getLocationId(), true);
						} catch (NoResultException nre) {
							// no inventory found
							logger.severe("Inventory not found for : " + inventoryItemBom.getItemIdRm());
						}

						if (inventory != null) {
							String itemNameForLog = "id: " + inventory.getId() + ", Name: "
									+ inventory.getItemDisplayName();
							inventory = getInventoryToUpdate(inventory, inventoryList);

							if (inventory == null) {
								logger.severe("could not find inventory to update in inventory list", itemNameForLog,
										inventoryList.toString());
								continue;
							}

							Item rowMaterialItem = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
									inventoryItemBom.getItemIdRm());

							BigDecimal itemHoldQuantity = inventory.getTotalAvailableQuanity();
							if (itemHoldQuantity != null) {
								itemHoldQuantity = itemHoldQuantity.subtract(
										inventoryItemBom.getQuantity().multiply(requesrOrderDetailItem.getQuantity()));

								inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								inventory.setUpdatedBy(requestOrder.getUpdatedBy());
								inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(
										inventoryItemBom.getQuantity().multiply(requesrOrderDetailItem.getQuantity())));
								inventory.setTotalAvailableQuanity(itemHoldQuantity);
								inventory.setUsedQuantity(
										inventoryItemBom.getQuantity().multiply(requesrOrderDetailItem.getQuantity()));
								inventory.setStatusId(requesrOrderDetailItem.getStatusId());
								inventoryList.add(inventory);
							}
						}

					}
				}

				if (inventoryList.size() > 0) {
					for (Inventory inventory : inventoryList) {
						em.merge(inventory);
						new InsertIntoHistory().insertInventoryIntoHistoryWithoutTransaction(httpServletRequest,
								inventory, em);
					}
				}
			}
		}

	}

	private void handleInventoryForItemCancel(HttpServletRequest httpServletRequest,
			RequestOrderDetailItems requesrOrderDetailItem, EntityManager em, RequestOrder requestOrder)
			throws NirvanaXPException {

		List<Inventory> inventoryList = new ArrayList<Inventory>();
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
		// get local location
		Location globalLocation = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				requesrOrderDetailItem.getRequestTo());
		if (globalLocation != null) {
			String localItemId = getLocalItemFromGlobalId(em, globalLocation.getInventoryDeductionBusinessId(),
					requesrOrderDetailItem.getItemsId());
			if (localItemId != null) {
				List<InventoryItemBom> inventoryItemBoms = inventoryManagementHelper
						.getInventoryItemBomForItemId(localItemId, em);

				if (inventoryItemBoms != null) {
					for (InventoryItemBom inventoryItemBom : inventoryItemBoms) {
						Inventory inventory = null;
						try {
							inventory = inventoryManagementHelper.getInventoryForItemId(em,
									inventoryItemBom.getItemIdRm(), requestOrder.getLocationId(), true);
						} catch (NoResultException nre) {
							// no inventory found
							logger.severe("Inventory not found for : " + inventoryItemBom.getItemIdRm());
						}

						if (inventory != null) {
							String itemNameForLog = "id: " + inventory.getId() + ", Name: "
									+ inventory.getItemDisplayName();
							inventory = getInventoryToUpdate(inventory, inventoryList);
							if (inventory == null) {
								logger.severe("could not find inventory to update in inventory list", itemNameForLog,
										inventoryList.toString());
								continue;
							}

							BigDecimal itemHoldQuantity = inventory.getTotalAvailableQuanity();
							itemHoldQuantity = itemHoldQuantity
									.add(inventoryItemBom.getQuantity().multiply(requesrOrderDetailItem.getQuantity()));

							inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							inventory.setUpdatedBy(requestOrder.getUpdatedBy());
							inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().subtract(
									inventoryItemBom.getQuantity().multiply(requesrOrderDetailItem.getQuantity())));
							inventory.setTotalAvailableQuanity(itemHoldQuantity);
							inventory.setUsedQuantity(itemHoldQuantity);
							inventory.setStatusId(requesrOrderDetailItem.getStatusId());
							inventoryList.add(inventory);

						}

					}
				}

				if (inventoryList.size() > 0) {
					for (Inventory inventory : inventoryList) {
						em.merge(inventory);
						new InsertIntoHistory().insertInventoryIntoHistoryWithoutTransaction(httpServletRequest,
								inventory, em);
					}
				}

			}
		}
	}

	public BigDecimal convertUnit(int fromUOMId, int toUOMId, NirvanaLogger logger, BigDecimal sellable,
			EntityManager em) {
		UnitConversion conversion = getUnitConversionByFromIdAndToId(em, fromUOMId, toUOMId, logger);
		BigDecimal conversionAmount = null;
		if (conversion != null) {
			conversionAmount = new BigDecimal(sellable.doubleValue() * conversion.getConversionRatio().doubleValue());
		} else {
			// find in reverse order toUOMID to FromUOMID
			conversion = getUnitConversionByFromIdAndToId(em, toUOMId, fromUOMId, logger);
			if (conversion != null) {
				// conversionAmount = sellable.multiply(new
				// BigDecimal(1).divide(conversion.getConversionRatio(), 2,
				// BigDecimal.ROUND_HALF_DOWN));
				double temp = 1 / conversion.getConversionRatio().doubleValue();
				conversionAmount = new BigDecimal(sellable.doubleValue() * temp);
			}

		}

		return conversionAmount;
	}

	public UnitConversion getUnitConversionByFromIdAndToId(EntityManager em, int fromUOMId, int toUOMId,
			NirvanaLogger logger) {

		try {
			String queryString = "select l from UnitConversion l where l.fromUOMId=? and l.toUOMId=? and l.status not in ('I','D') ";
			TypedQuery<UnitConversion> query = em.createQuery(queryString, UnitConversion.class)
					.setParameter(1, fromUOMId).setParameter(2, toUOMId);
			return query.getSingleResult();
		} catch (Exception e) {

			logger.severe(e);
		}
		return null;
	}

	private void updateThresholdValueAndPushToClient(List<Inventory> inventoryList,
			InventoryManagementHelper inventoryManagementHelper, EntityManager em,
			InventoryPostPacket inventoryPostPacket) {
		if (inventoryList != null) {
			ArrayList<Inventory> inventoryListForPush = new ArrayList<Inventory>();
			for (Inventory inventory : inventoryList) {
				InventoryItemDefault inventoryItemDefault = inventoryManagementHelper
						.getInventoryItemDefaultForItemId(em, inventory.getItemId());
				BigDecimal threshold = new BigDecimal(0);
				if (inventoryItemDefault != null && inventoryItemDefault.getD86Threshold() != null) {
					threshold = inventoryItemDefault.getD86Threshold();

				}
				int isBelowThreshold = 0;
				if (inventory.getTotalAvailableQuanity().compareTo(threshold) <= 0) {
					isBelowThreshold = 1;
				}

				inventory.setIsBelowThreashold(isBelowThreshold);
				Inventory inventoryForPush = new Inventory();
				inventoryForPush.setId(inventory.getId());
				inventoryForPush.setIsBelowThreashold(isBelowThreshold);
				inventoryForPush.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity());
				inventoryForPush.setItemId(inventory.getItemId());
				inventoryListForPush.add(inventoryForPush);

			}
			if (inventoryListForPush.size() > 0) {
				List<Inventory> postPacketInventoryList = inventoryPostPacket.getInventoryList();
				if (postPacketInventoryList == null) {
					inventoryPostPacket.setInventoryList(inventoryListForPush);
				} else {
					for (Inventory inventory : inventoryListForPush) {
						postPacketInventoryList.add(inventory);
					}
				}

			}
		}

	}

	private Inventory getInventoryToUpdate(Inventory inventory, List<Inventory> inventoryList) {

		Inventory temp = null;
		if (inventoryList != null && !inventoryList.isEmpty()) {
			for (Inventory fromList : inventoryList) {

				if (fromList.getId() == inventory.getId()) {
					temp = fromList;
				}
			}

			if (temp != null) {
				inventoryList.remove(temp);
			}
		}

		if (temp == null) {
			temp = inventory;
		}

		return temp;

	}

	RequestOrder deleteRequestOrder(HttpServletRequest httpRequest, EntityManager em, RequestOrder requestOrder)
			throws Exception {

		requestOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		requestOrder.setStatus("D");
		requestOrder = em.merge(requestOrder);
		return requestOrder;

	}

	public RequestOrder getRequestOrderById(EntityManager em, String id, boolean isAllotment) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RequestOrder> criteria = builder.createQuery(RequestOrder.class);
		Root<RequestOrder> r = criteria.from(RequestOrder.class);
		TypedQuery<RequestOrder> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(RequestOrder_.id), id)));
		RequestOrder requestOrder = null;
		try {
			requestOrder = query.getSingleResult();
			requestOrder.setRequestOrderDetailItems(
					getRequestOrderDetailsItemForRequestId(em, id, isAllotment, requestOrder.getLocationId()));

		} catch (Exception e) {
			logger.severe("No Entity find for RequestOrder for id " + id);
		}
		return requestOrder;
	}

	List<RequestOrderDetailItems> getRequestOrderDetailsItemForRequestId(EntityManager em, String requestId,
			boolean isAllotment, String locationId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		OrderDetailStatus orderDetailStatus = getOrderDetailStatusByNameAndLocationId(em, "Item Removed", locationId);

		CriteriaQuery<RequestOrderDetailItems> criteria = builder.createQuery(RequestOrderDetailItems.class);
		Root<RequestOrderDetailItems> requestOrderDetailItem = criteria.from(RequestOrderDetailItems.class);
		TypedQuery<RequestOrderDetailItems> query = em.createQuery(criteria.select(requestOrderDetailItem).where(
				builder.equal(requestOrderDetailItem.get(RequestOrderDetailItems_.requestId), requestId),
				builder.notEqual(requestOrderDetailItem.get(RequestOrderDetailItems_.status), "D"), builder.notEqual(
						requestOrderDetailItem.get(RequestOrderDetailItems_.statusId), orderDetailStatus.getId())));
		List<RequestOrderDetailItems> requestOrderDetailItemList = query.getResultList();

		for (RequestOrderDetailItems requestOrderDetailItems : requestOrderDetailItemList) {
			Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
					requestOrderDetailItems.getItemsId());
			if (item != null) {
				BigDecimal availableQuantity = getAvailableQuantityFromInventory(em, item.getGlobalItemId());
				requestOrderDetailItems.setAvailableQuantity(availableQuantity);
				// if (isAllotment)
				// {
				// requestOrderDetailItems.setUnitPrice(item.getDistributionPrice());
				// }
				// else
				// {
				// requestOrderDetailItems.setUnitPrice(item.getPurchasingRate());
				// }
				if (item.getSalesTax1() != null) {
					SalesTax tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em, SalesTax.class,
							item.getSalesTax1());
					if (tax != null) {
						requestOrderDetailItems.setTaxRate1(tax.getRate());
						requestOrderDetailItems.setTaxName1(tax.getTaxName());
						requestOrderDetailItems.setTaxDisplayName1(tax.getDisplayName());

					}
				}
				if (item.getSalesTax2() != null) {
					SalesTax tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em, SalesTax.class,
							item.getSalesTax2());
					if (tax != null) {
						requestOrderDetailItems.setTaxRate2(tax.getRate());
						requestOrderDetailItems.setTaxName2(tax.getTaxName());
						requestOrderDetailItems.setTaxDisplayName2(tax.getDisplayName());
					}
				}
				if (item.getSalesTax3() != null) {
					SalesTax tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em, SalesTax.class,
							item.getSalesTax3());
					if (tax != null) {
						requestOrderDetailItems.setTaxRate3(tax.getRate());
						requestOrderDetailItems.setTaxName3(tax.getTaxName());
						requestOrderDetailItems.setTaxDisplayName3(tax.getDisplayName());
					}
				}
				if (item.getSalesTax4() != null) {
					SalesTax tax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em, SalesTax.class,
							item.getSalesTax4());
					if (tax != null) {
						requestOrderDetailItems.setTaxRate4(tax.getRate());
						requestOrderDetailItems.setTaxName4(tax.getTaxName());
						requestOrderDetailItems.setTaxDisplayName4(tax.getDisplayName());
					}
				}
				if (item.getYieldPercent() != null) {
					requestOrderDetailItems.setItemYieldPercent(item.getYieldPercent());
				}

				requestOrderDetailItems.setDistributionPrice(item.getDistributionPrice());
				requestOrderDetailItems.setPurchasingRate(item.getPurchasingRate());
			}
		}

		return requestOrderDetailItemList;
	}

	public List<OrderStatus> getOrderStatusByName(EntityManager em, String statusName) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
		Root<OrderStatus> r = criteria.from(OrderStatus.class);
		TypedQuery<OrderStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.name), statusName)));
		return query.getResultList();
	}

	public ItemToSupplier getItemToSupplier(String itemId, EntityManager em) {
		try {
			if (itemId != null && em != null) {

				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<ItemToSupplier> criteria = builder.createQuery(ItemToSupplier.class);
				Root<ItemToSupplier> r = criteria.from(ItemToSupplier.class);

				TypedQuery<ItemToSupplier> query = em
						.createQuery(criteria.select(r).where(builder.equal(r.get(ItemToSupplier_.itemId), itemId)));
				return query.getSingleResult();

			}
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;

	}

	RequestOrderDetailItems updateQtyAndStatus(EntityManager em, RequestOrderDetailItems requestOrderDetailItems)
			throws Exception {

		RequestOrderDetailItems detailItems = (RequestOrderDetailItems) new CommonMethods().getObjectById(
				"RequestOrderDetailItems", em, RequestOrderDetailItems.class, requestOrderDetailItems.getId());
		detailItems.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		detailItems.setQuantity(requestOrderDetailItems.getQuantity());
		detailItems.setStatus(requestOrderDetailItems.getStatus());
		detailItems.setStatusId(requestOrderDetailItems.getStatusId());
		detailItems = em.merge(detailItems);
		return requestOrderDetailItems;
	}

	public OrderStatus getOrderStatusByNameAndLocation(EntityManager em, String statusName, String locationId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
		Root<OrderStatus> r = criteria.from(OrderStatus.class);
		TypedQuery<OrderStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.name), statusName),
						builder.equal(r.get(OrderStatus_.locationsId), locationId)));
		return query.getSingleResult();
	}

	public OrderStatus getOrderStatusByNameAndLocationAndSourceGroupId(EntityManager em, String statusName,
			String locationId, String groupId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
		Root<OrderStatus> r = criteria.from(OrderStatus.class);
		TypedQuery<OrderStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.name), statusName),
						builder.equal(r.get(OrderStatus_.locationsId), locationId),
						builder.equal(r.get(OrderStatus_.orderSourceGroupId), groupId)));
		return query.getSingleResult();
	}

	public List<RequestOrder> getAllPendingRequestOrder(EntityManager em, String locationId) {

		List<String> orderStatus = getAllOrderStatusForPendingRequestByNameAndLocation(em, locationId);
		String orderStatusIds = orderStatus.toString();
		orderStatusIds = orderStatusIds.replace("[", "").replace("]", "");

		String queryString = "select ro from RequestOrder ro where ro.locationId = ? and ro.statusId not in ("
				+ orderStatusIds + ") and ro.isPOOrder != 1";
		TypedQuery<RequestOrder> query = em.createQuery(queryString, RequestOrder.class).setParameter(1, locationId);

		List<RequestOrder> requestOrderList = query.getResultList();
		return requestOrderList;
	}

	public List<RequestOrder> getAllPendingRequestOrderAllInOne(EntityManager em, String locationId, String date) {

		// List<Integer> orderStatus =
		// getAllOrderStatusForPendingRequestByNameAndLocation(em, locationId);
		// String orderStatusIds = orderStatus.toString();
		// orderStatusIds = orderStatusIds.replace("[", "").replace("]", "");

		String queryString = "select ro from RequestOrder ro where ro.locationId = ?  and ro.date= '" + date + "'";
		TypedQuery<RequestOrder> query = em.createQuery(queryString, RequestOrder.class).setParameter(1, locationId);

		List<RequestOrder> requestOrderList = query.getResultList();
		return requestOrderList;
	}

	public List<RequestOrder> getAllPendingPOByDate(EntityManager em, String locationId, String date) {

		List<String> orderStatus = getAllOrderStatusForPendingRequestByNameAndLocation(em, locationId);
		String orderStatusIds = orderStatus.toString();
		orderStatusIds = orderStatusIds.replace("[", "").replace("]", "");

		String queryString = "select ro from RequestOrder ro where ro.locationId = ? and ro.isPOOrder = 1 and ro.statusId not in ("
				+ orderStatusIds + ") and ro.date= '" + date + "'";
		TypedQuery<RequestOrder> query = em.createQuery(queryString, RequestOrder.class).setParameter(1, locationId);

		List<RequestOrder> requestOrderList = query.getResultList();
		return requestOrderList;
	}

	public List<RequestOrder> getAllInProcessRequestOrderByLocationId(EntityManager em, String locationId) {
		OrderStatus orderStatus = getOrderStatusByNameAndLocation(em, "Request In Process", locationId);
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RequestOrder> criteria = builder.createQuery(RequestOrder.class);
		Root<RequestOrder> r = criteria.from(RequestOrder.class);
		TypedQuery<RequestOrder> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(RequestOrder_.statusId), orderStatus.getId()),
						builder.equal(r.get(RequestOrder_.locationId), locationId)));
		List<RequestOrder> requestOrderList = query.getResultList();
		return requestOrderList;
	}

	public BigDecimal getAvailableQuantityFromInventory(EntityManager em, String itemId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Inventory> criteria = builder.createQuery(Inventory.class);
			Root<Inventory> r = criteria.from(Inventory.class);
			TypedQuery<Inventory> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(Inventory_.itemId), itemId)));
			Inventory inventory = query.getSingleResult();
			return (inventory.getTotalAvailableQuanity());
		} catch (Exception e) {
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public List<RequestOrder> getAllPendingRequestOrderByDate(EntityManager em, String locationId, String date) {
		OrderStatus orderStatus = getOrderStatusByNameAndLocation(em, "Request Received", locationId);

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RequestOrder> criteria = builder.createQuery(RequestOrder.class);
		Root<RequestOrder> r = criteria.from(RequestOrder.class);
		TypedQuery<RequestOrder> query = em.createQuery(criteria.select(r).where(
				builder.notEqual(r.get(RequestOrder_.statusId), orderStatus.getId()),
				builder.equal(r.get(RequestOrder_.locationId), locationId),
				builder.equal(r.get(RequestOrder_.date), date), builder.notEqual(r.get(RequestOrder_.isPOOrder), 1)));
		List<RequestOrder> requestOrderList = query.getResultList();
		return requestOrderList;
	}

	List<OrderDetailStatus> getOrderDetailStatusForInventory(EntityManager em, String locationId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
		Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
		TypedQuery<OrderDetailStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.locationsId), locationId),
						builder.notEqual(r.get(OrderDetailStatus_.status), "D")));
		return query.getResultList();
	}

	// private OrderSourceGroup
	// getOrderSourceGroupByNameAndLocationId(EntityManager em, String name, int
	// locationId)
	// {
	// OrderSourceGroup resultSet = null;
	// try
	// {
	// String queryString =
	// "select os from OrderSourceGroup os where os.name ='" + name +
	// "' and os.locationsId= " + locationId;
	// TypedQuery<OrderSourceGroup> query = em.createQuery(queryString,
	// OrderSourceGroup.class);
	// resultSet = query.getSingleResult();
	// }
	// catch (Exception e)
	// {
	// logger.severe(e, "OrderSourceGroup not found: ", e.getMessage());
	// }
	//
	// return resultSet;
	// }

	List<OrderStatus> getOrderStatusForInventory(EntityManager em, String locationId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
		Root<OrderStatus> r = criteria.from(OrderStatus.class);
		TypedQuery<OrderStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.locationsId), locationId),
						builder.notEqual(r.get(OrderStatus_.status), "D")));
		return query.getResultList();
	}

	private void addOrUpdateInventoryWithItemForPOReceived(HttpServletRequest httpRequest, EntityManager em,
			RequestOrder requestOrder, List<RequestOrderDetailItems> requestOrderDetailItems,
			BigDecimal defaultInventorythreashold, PostPacket postPacket, List<InventoryItemsList> inventoryItemsLists)
			throws Exception {
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		// get inventory object corresponding to this inventory order receipt
		// based on item id
		for (RequestOrderDetailItems requestOrderDetailItems2 : requestOrderDetailItems) {
			Inventory inventory = null;

			try {
				inventory = inventoryManagementHelper.getInventoryForItemId(em, requestOrderDetailItems2.getItemsId(),
						requestOrder.getLocationId(), false);
			} catch (Exception nre) {
				// no inventory found
				logger.severe("No inventory found for item id: " + requestOrderDetailItems2.getItemsId());
			}

			BigDecimal inventoryPrevQty = null;
			if (inventory == null) {
				inventory = inventoryManagementHelper.addInventoryWithItemForPO(em, requestOrderDetailItems2,
						defaultInventorythreashold, requestOrder.getLocationId());
				InventoryItemDefault inventoryItemDefaultDB = inventoryManagementHelper
						.getInventoryItemDefaultForItemId(em, requestOrderDetailItems2.getItemsId());
				// add inventory item default also for this one
				if (inventoryItemDefaultDB == null) {
					// add inventory item default also for this one
					InventoryItemDefault inventoryItemDefault = new InventoryItemDefault();
					inventoryItemDefault.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault.setCreatedBy(requestOrderDetailItems2.getCreatedBy());
					inventoryItemDefault.setItemId(requestOrderDetailItems2.getItemsId());
					inventoryItemDefault.setEconomicOrderQuantity(new BigDecimal(0));
					inventoryItemDefault.setMinimumOrderQuantity(new BigDecimal(0));
					inventoryItemDefault.setD86Threshold(defaultInventorythreashold);
					inventoryItemDefault.setStatus("A");
					em.persist(inventoryItemDefault);
				}
			} else {
				InventoryItemDefault itemDefault = inventoryManagementHelper
						.getInventoryItemDefault(inventory.getItemId(), em);
				if (itemDefault != null && itemDefault.getD86Threshold() != null) {
					defaultInventorythreashold = itemDefault.getD86Threshold();
				}

				// we have an item inventory, we must add this amount to
				// existing one
				// take threashold of this inventory
				inventoryPrevQty = inventory.getTotalAvailableQuanity();
				Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
						requestOrderDetailItems2.getItemsId());
				Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
						requestOrder.getLocationId());
				if (location.getIsGlobalLocation() == 1) {

					inventory = inventoryManagementHelper.updateInventoryWithItemForPO(httpRequest, em, inventory,
							requestOrderDetailItems2, true, item, logger, true, false, false);

				} else {
					inventory = inventoryManagementHelper.updateInventoryWithItemForPO(httpRequest, em, inventory,
							requestOrderDetailItems2, true, item, logger, false, true, false);

				}

			}

			InventoryItemsList inventoryItems = new InventoryItemsList();

			Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
					requestOrderDetailItems2.getItemsId(), inventory, defaultInventorythreashold.doubleValue(), false,
					inventoryPrevQty, inventoryItems);

			// we must send push as item threshold or is in stock is modified
			if (item != null && postPacket != null) {
				// send packet for broadcast as item has been modified
				InventoryPostPacket inventoryPostPacketNew = inventoryManagementHelper.getPushPacket(inventory, null,
						defaultInventorythreashold);

				inventoryPostPacketNew.setMerchantId(postPacket.getMerchantId());
				inventoryPostPacketNew.setLocationId(postPacket.getLocationId());
				inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacketNew,
						POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());

			}

			inventoryItems.setInventoryId(inventory.getId());
			inventoryItems.setRequestOrderDetailsId(requestOrderDetailItems2.getId());
			inventoryItemsLists.add(inventoryItems);
		}

	}

	private void reduceInventoryForIntraAllotment(HttpServletRequest httpRequest, EntityManager em,
			RequestOrder requestOrder, List<RequestOrderDetailItems> requestOrderDetailItems,
			BigDecimal defaultInventorythreashold, PostPacket postPacket, List<InventoryItemsList> inventoryItemsLists)
			throws Exception {
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		// get inventory object corresponding to this inventory order receipt
		// based on item id
		for (RequestOrderDetailItems requestOrderDetailItems2 : requestOrderDetailItems) {
			Item itemAdapter = null;
			try {
				String queryStringItem = "select s from Item s where s.id =?";
				TypedQuery<Item> queryItem = em.createQuery(queryStringItem, Item.class).setParameter(1,
						requestOrderDetailItems2.getItemsId());
				Item itemsQ = queryItem.getSingleResult();

				queryStringItem = "select s from Item s where s.globalItemId =? and s.locationsId = ? and s.status!='D'";
				queryItem = em.createQuery(queryStringItem, Item.class).setParameter(1, itemsQ.getGlobalItemId())
						.setParameter(2, requestOrder.getSupplierId());
				itemAdapter = queryItem.getSingleResult();
			} catch (Exception e) {
				// TODO: handle exception
				logger.severe("No Item Entity Found");
			}
			if (itemAdapter != null) {
				Inventory inventory = null;

				try {
					inventory = inventoryManagementHelper.getInventoryForItemId(em, itemAdapter.getId(),
							requestOrder.getSupplierId(), false);
				} catch (Exception nre) {
					// no inventory found
					logger.severe("No inventory found for item id: " + itemAdapter.getId());
				}

				BigDecimal inventoryPrevQty = null;
				if (inventory == null) {
					RequestOrderDetailItems requestOrderDetailItems3 = requestOrderDetailItems2
							.copy(requestOrderDetailItems2);
					requestOrderDetailItems3.setItemsId(itemAdapter.getId());
					inventory = inventoryManagementHelper.addInventoryWithItemForPO(em, requestOrderDetailItems3,
							defaultInventorythreashold, requestOrder.getSupplierId());
					InventoryItemDefault inventoryItemDefaultDB = inventoryManagementHelper
							.getInventoryItemDefaultForItemId(em, itemAdapter.getId());
					// add inventory item default also for this one
					if (inventoryItemDefaultDB == null) {
						// add inventory item default also for this one
						InventoryItemDefault inventoryItemDefault = new InventoryItemDefault();
						inventoryItemDefault.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						inventoryItemDefault.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						inventoryItemDefault.setCreatedBy(requestOrderDetailItems3.getCreatedBy());
						inventoryItemDefault.setItemId(requestOrderDetailItems3.getItemsId());
						inventoryItemDefault.setEconomicOrderQuantity(new BigDecimal(0));
						inventoryItemDefault.setMinimumOrderQuantity(new BigDecimal(0));
						inventoryItemDefault.setD86Threshold(defaultInventorythreashold);
						inventoryItemDefault.setStatus("A");
						em.persist(inventoryItemDefault);
					}
				} else {
					InventoryItemDefault itemDefault = inventoryManagementHelper
							.getInventoryItemDefault(inventory.getItemId(), em);
					if (itemDefault != null && itemDefault.getD86Threshold() != null) {
						defaultInventorythreashold = itemDefault.getD86Threshold();
					}

					// we have an item inventory, we must add this amount to
					// existing one
					// take threashold of this inventory
					inventoryPrevQty = inventory.getTotalAvailableQuanity();
					Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class, itemAdapter.getId());
					Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
							requestOrder.getSupplierId());
					/*
					 * if (location.getIsGlobalLocation() == 1) {
					 * 
					 * inventory =
					 * inventoryManagementHelper.updateInventoryWithItemForPO(
					 * httpRequest, em, inventory, requestOrderDetailItems2,
					 * true, item, logger, true, false, false);
					 * 
					 * } else {
					 */
					inventory = inventoryManagementHelper.updateInventoryWithItemForPOIntra(httpRequest, em, inventory,
							requestOrderDetailItems2, true, item, logger, false, true, false);

					// }

				}

				InventoryItemsList inventoryItems = new InventoryItemsList();
				Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
						itemAdapter.getId(), inventory, defaultInventorythreashold.doubleValue(), false,
						inventoryPrevQty, inventoryItems);

				// we must send push as item threshold or is in stock is
				// modified
				if (item != null && postPacket != null) {
					// send packet for broadcast as item has been modified
					InventoryPostPacket inventoryPostPacketNew = inventoryManagementHelper.getPushPacket(inventory,
							null, defaultInventorythreashold);

					inventoryPostPacketNew.setMerchantId(postPacket.getMerchantId());
					inventoryPostPacketNew.setLocationId(postPacket.getLocationId());
					inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacketNew,
							POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());

				}

				inventoryItems.setInventoryId(inventory.getId());
				inventoryItems.setRequestOrderDetailsId(requestOrderDetailItems2.getId());
				inventoryItemsLists.add(inventoryItems);
			}
		}

	}

	private void returnInventoryWithItemForAllotMentCancelled(HttpServletRequest httpRequest, EntityManager em,
			RequestOrder requestOrder, List<RequestOrderDetailItems> requestOrderDetailItems,
			BigDecimal defaultInventorythreashold, PostPacket postPacket, BigDecimal allotmentQty) throws Exception {
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		// get inventory object corresponding to this inventory order receipt
		// based on item id
		for (RequestOrderDetailItems requestOrderDetailItems2 : requestOrderDetailItems) {
			Inventory inventory = null;
			Item localItem = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
					requestOrderDetailItems2.getItemsId());

			try {
				inventory = inventoryManagementHelper.getInventoryForItemId(em, localItem.getGlobalItemId(),
						requestOrder.getLocationId(), true);
			} catch (Exception nre) {
				// no inventory found
				logger.severe("Inventory not found for global item id: " + localItem.getGlobalItemId());
			}

			BigDecimal inventoryPrevQty = null;
			if (inventory == null) {
				inventory = inventoryManagementHelper.addInventoryWithItemForPO(em, requestOrderDetailItems2,
						defaultInventorythreashold, requestOrder.getLocationId(), allotmentQty, logger);

				InventoryItemDefault inventoryItemDefaultDB = inventoryManagementHelper
						.getInventoryItemDefaultForItemId(em, requestOrderDetailItems2.getItemsId());
				// add inventory item default also for this one
				if (inventoryItemDefaultDB == null) {
					// add inventory item default also for this one
					InventoryItemDefault inventoryItemDefault = new InventoryItemDefault();
					inventoryItemDefault.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault.setCreatedBy(requestOrderDetailItems2.getCreatedBy());
					inventoryItemDefault.setItemId(localItem.getGlobalItemId());
					inventoryItemDefault.setEconomicOrderQuantity(new BigDecimal(0));
					inventoryItemDefault.setMinimumOrderQuantity(new BigDecimal(0));
					inventoryItemDefault.setD86Threshold(defaultInventorythreashold);
					inventoryItemDefault.setStatus("A");
					em.persist(inventoryItemDefault);
				}
			} else {
				InventoryItemDefault itemDefault = inventoryManagementHelper
						.getInventoryItemDefault(inventory.getItemId(), em);
				if (itemDefault != null && itemDefault.getD86Threshold() != null) {
					defaultInventorythreashold = itemDefault.getD86Threshold();
				}
				// we have an item inventory, we must add this amount to
				// existing one
				// take threashold of this inventory
				inventoryPrevQty = inventory.getTotalAvailableQuanity();

				inventory = inventoryManagementHelper.updateInventoryWithItemForPO(httpRequest, em, inventory,
						requestOrderDetailItems2, true, localItem, logger, true, false, true, allotmentQty);
			}
			Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
					requestOrderDetailItems2.getItemsId(), inventory, defaultInventorythreashold.doubleValue(), false,
					inventoryPrevQty, null);

			// we must send push as item threshold or is in stock is modified
			if (item != null) {
				// send packet for broadcast as item has been modified
				InventoryPostPacket inventoryPostPacketNew = inventoryManagementHelper.getPushPacket(inventory, null,
						defaultInventorythreashold);

				inventoryPostPacketNew.setMerchantId(postPacket.getMerchantId());
				inventoryPostPacketNew.setLocationId(postPacket.getLocationId());
				inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacketNew,
						POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());

			}
		}

	}

	private void consumeInventoryWithItemForRequestAllot(HttpServletRequest httpRequest, EntityManager em,
			RequestOrder requestOrder, List<RequestOrderDetailItems> requestOrderDetailItems,
			BigDecimal defaultInventorythreashold, PostPacket postPacket, List<InventoryItemsList> inventoryItemsLists)
			throws Exception {
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		// get inventory object corresponding to this inventory order receipt
		// based on item id
		if (requestOrderDetailItems != null && requestOrderDetailItems.size() > 0) {
			for (RequestOrderDetailItems requestOrderDetailItems2 : requestOrderDetailItems) {
				// find global itemId of requested item :-
				Item localItem = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
						requestOrderDetailItems2.getItemsId());

				Inventory inventory = null;

				try {

					inventory = inventoryManagementHelper.getInventoryForItemId(em, localItem.getGlobalItemId(),
							requestOrderDetailItems2.getRequestTo(), true);

				} catch (Exception nre) {
					// no inventory found
					logger.severe("No inventory found for global item id: " + localItem.getGlobalItemId());
				}

				BigDecimal inventoryPrevQty = null;
				if (inventory == null) {
					throw new NirvanaXPException(new NirvanaServiceErrorResponse("NXP5002",
							"Insuffiecient Inventory for allocation", "Insuffiecient Inventory for allocation"));
				} else {

					inventoryPrevQty = inventory.getTotalUsedQuanity();
					Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
							localItem.getGlobalItemId());

					Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
							requestOrderDetailItems2.getRequestTo());
					if (location.getIsGlobalLocation() == 1) {
						inventory = inventoryManagementHelper.updateInventoryWithItemForPO(httpRequest, em, inventory,
								requestOrderDetailItems2, true, item, logger, false, false, false);
					} else {
						inventory = inventoryManagementHelper.updateInventoryWithItemForPO(httpRequest, em, inventory,
								requestOrderDetailItems2, true, item, logger, false, true, false);
					}

				}

				InventoryItemsList inventoryItems = new InventoryItemsList();
				Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
						requestOrderDetailItems2.getItemsId(), inventory, defaultInventorythreashold.doubleValue(),
						false, inventoryPrevQty, inventoryItems);

				// we must send push as item threshold or is in stock is
				// modified
				if (item != null) {
					// send packet for broadcast as item has been modified
					InventoryPostPacket inventoryPostPacketNew = inventoryManagementHelper.getPushPacket(inventory,
							null, defaultInventorythreashold);

					inventoryPostPacketNew.setMerchantId(postPacket.getMerchantId());
					inventoryPostPacketNew.setLocationId(postPacket.getLocationId());
					inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacketNew,
							POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());

				}

				inventoryItems.setInventoryId(inventory.getId());
				inventoryItems.setRequestOrderDetailsId(requestOrderDetailItems2.getId());
				inventoryItemsLists.add(inventoryItems);

			}
		}

	}

	private void consumeInventoryWithItemForRequestAllotForIntra(HttpServletRequest httpRequest, EntityManager em,
			RequestOrder requestOrder, List<RequestOrderDetailItems> requestOrderDetailItems,
			BigDecimal defaultInventorythreashold, PostPacket postPacket) throws Exception {
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		// get inventory object corresponding to this inventory order receipt
		// based on item id
		if (requestOrderDetailItems != null && requestOrderDetailItems.size() > 0) {
			for (RequestOrderDetailItems requestOrderDetailItems2 : requestOrderDetailItems) {
				// find global itemId of requested item :-
				Item localItem = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
						requestOrderDetailItems2.getItemsId());

				Inventory inventory = null;

				try {
					Item itemAdapter = null;
					try {
						String queryStringItems = "select s from Item s where s.globalItemId =? and s.locationsId = ? and s.status!='D' ";
						TypedQuery<Item> queryItems = em.createQuery(queryStringItems, Item.class)
								.setParameter(1, localItem.getGlobalItemId())
								.setParameter(2, requestOrder.getSupplierId());
						itemAdapter = queryItems.getSingleResult();
					} catch (Exception e) {
						// TODO: handle exception
						logger.severe("No Result Found");
					}

					inventory = inventoryManagementHelper.getInventoryForItemId(em, itemAdapter.getId(),
							requestOrder.getSupplierId(), true);
				} catch (Exception nre) {
					// no inventory found
					logger.severe("No inventory found for global item id: " + localItem.getGlobalItemId());
				}

				BigDecimal inventoryPrevQty = null;
				if (inventory == null) {
					throw new NirvanaXPException(new NirvanaServiceErrorResponse("NXP5002",
							"Insuffiecient Inventory for allocation", "Insuffiecient Inventory for allocation"));
				} else {

					inventoryPrevQty = inventory.getTotalUsedQuanity();
					Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
							localItem.getGlobalItemId());

					Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
							requestOrderDetailItems2.getRequestTo());
					if (location.getIsGlobalLocation() == 1) {
						inventory = inventoryManagementHelper.updateInventoryWithItemForPO(httpRequest, em, inventory,
								requestOrderDetailItems2, true, item, logger, false, false, false);
					} else {
						inventory = inventoryManagementHelper.updateInventoryWithItemForPO(httpRequest, em, inventory,
								requestOrderDetailItems2, true, item, logger, false, true, false);
					}

				}
				Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
						requestOrderDetailItems2.getItemsId(), inventory, defaultInventorythreashold.doubleValue(),
						false, inventoryPrevQty, null);

				// we must send push as item threshold or is in stock is
				// modified
				if (item != null) {
					// send packet for broadcast as item has been modified
					InventoryPostPacket inventoryPostPacketNew = inventoryManagementHelper.getPushPacket(inventory,
							null, defaultInventorythreashold);

					inventoryPostPacketNew.setMerchantId(postPacket.getMerchantId());
					inventoryPostPacketNew.setLocationId(postPacket.getLocationId());
					inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacketNew,
							POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());

				}
			}
		}

	}

	void updateOrderDetailItems(EntityManager em, String globalItemId, String locationId) {
		OrderDetailStatus orderDetailStatus = getOrderDetailStatusByNameAndLocationId(em, "Request In Process",
				locationId);
		List<Item> itemList = getLocalItemFromGlobalItemId(em, globalItemId);
		for (Item item : itemList) {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<RequestOrderDetailItems> criteria = builder.createQuery(RequestOrderDetailItems.class);
			Root<RequestOrderDetailItems> requestOrderDetailItem1 = criteria.from(RequestOrderDetailItems.class);
			TypedQuery<RequestOrderDetailItems> query = em.createQuery(criteria.select(requestOrderDetailItem1)
					.where(builder.equal(requestOrderDetailItem1.get(RequestOrderDetailItems_.itemsId), item.getId())));
			List<RequestOrderDetailItems> requestOrderDetailItemList = query.getResultList();
			for (RequestOrderDetailItems requestOrderDetailItems : requestOrderDetailItemList) {
				requestOrderDetailItems.setStatusId(orderDetailStatus.getId());
				em.merge(requestOrderDetailItems);
			}
		}
	}

	public OrderDetailStatus getOrderDetailStatusByNameAndLocationId(EntityManager em, String statusName,
			String locationsId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
			Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
			TypedQuery<OrderDetailStatus> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.name), statusName),
							builder.equal(r.get(OrderDetailStatus_.locationsId), locationsId),
							builder.notEqual(r.get(OrderDetailStatus_.status), "D")));

			return query.getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	private List<Item> getLocalItemFromGlobalItemId(EntityManager em, String globalItemId) {
		try {
			String queryString = "select s from Item s where s.status!='D' and s.globalItemId =? or ((s.globalItemId ='0' or s.globalItemId is null) and s.id=?) ";
			TypedQuery<Item> query = em.createQuery(queryString, Item.class).setParameter(1, globalItemId)
					.setParameter(2, globalItemId);
			return query.getResultList();
		}

		catch (NoResultException e) {
			logger.severe("No Result found");
		}
		return null;
	}

	public List<GoodsReceiveNotes> getGoodsReceiveNotesByNumber(EntityManager em, String grnNumber) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<GoodsReceiveNotes> criteria = builder.createQuery(GoodsReceiveNotes.class);
		Root<GoodsReceiveNotes> r = criteria.from(GoodsReceiveNotes.class);
		TypedQuery<GoodsReceiveNotes> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(GoodsReceiveNotes_.grnNumber), grnNumber),
						builder.equal(r.get(GoodsReceiveNotes_.isAllotment), 1),
						builder.equal(r.get(GoodsReceiveNotes_.isGRNClose), 0)));
		List<GoodsReceiveNotes> GoodsReceiveNotesList = query.getResultList();
		for (GoodsReceiveNotes goodsReceiveNotes : GoodsReceiveNotesList) {

			RequestOrderDetailItems items = (RequestOrderDetailItems) new CommonMethods().getObjectById(
					"RequestOrderDetailItems", em, RequestOrderDetailItems.class,
					goodsReceiveNotes.getRequestOrderDetailsItemId());
			goodsReceiveNotes.setUomName(items.getUomName());
			goodsReceiveNotes.setRequestOrderDetailItemName(items.getItemName());
			goodsReceiveNotes.setStatusId(items.getStatusId());
			goodsReceiveNotes.setQuantity(items.getQuantity());
		}

		return GoodsReceiveNotesList;
	}

	List<GoodsReceiveNotes> insertGoodsReceiveNotes(EntityManager em, List<RequestOrderDetailItems> itemsList,
			String grnNumber, boolean isAllotment, String grnDate, boolean isAdminReceive, boolean isDirectRecieve,
			boolean isNeedToCloseGRN, List<InventoryItemsList> inventoryItemsLists, String supplierRefNo,
			HttpServletRequest httpRequest,String grnNewDate,String departmentId) throws IOException, InvalidSessionException {
		List<GoodsReceiveNotes> goodsReceiveNotes = new ArrayList<GoodsReceiveNotes>();
		OrderDetailStatus detailStatus = null;
		for (RequestOrderDetailItems items : itemsList) {
			if (items.getReceivedQuantity() == null) {
				items.setReceivedQuantity(new BigDecimal(0));
			}
			BigDecimal balance = new BigDecimal(0);

			if (!isDirectRecieve) {
				if (isAdminReceive) {
					balance = items.getQuantity().subtract(items.getReceivedQuantity());

				} else {
					balance = items.getAllotmentQty().subtract(items.getReceivedQuantity());

				}
			}

			GoodsReceiveNotes notes = new GoodsReceiveNotes();
			notes.setBalance(balance);
			notes.setGrnDate(grnNewDate);
			notes.setAllotmentQty(items.getAllotmentQty());
			notes.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			notes.setSupplierRefNo(supplierRefNo);

			detailStatus = em.find(OrderDetailStatus.class, items.getStatusId());
			if (detailStatus != null) {
				notes.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(detailStatus.getLocationsId(), em));
			}

			notes.setCreatedBy(items.getCreatedBy());
			notes.setGrnNumber(grnNumber);

			notes.setPrice(items.getPrice());
			notes.setRate(items.getUnitPurchasedPrice());
			notes.setReceivedQuantity(items.getReceivedQuantity());
			notes.setRequestOrderDetailsItemId(items.getId());
			notes.setStatus("A");
			notes.setTax(items.getTax());
			notes.setTotal(items.getTotal());
			notes.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			notes.setUpdatedBy(items.getUpdatedBy());
			notes.setDate(grnDate);
			notes.setUnitPrice(items.getUnitPrice());
			notes.setUnitPurchasedPrice(items.getUnitPurchasedPrice());
			notes.setUnitTaxRate(items.getUnitTaxRate());
			notes.setDepartmentId(departmentId);
			if (isAllotment) {
				notes.setIsAllotment(1);
			}
			if (isNeedToCloseGRN) {
				if (notes.getBalance() != null && notes.getBalance().compareTo((new BigDecimal(0))) == 0) {
					notes.setIsGRNClose(1);
				}
			}

			if (isDirectRecieve) {
				notes.setIsGRNClose(1);
			}
			notes.setUomName(items.getUomName());
			notes.setRequestOrderDetailItemName(items.getItemName());
			notes.setStatusId(items.getStatusId());
			if (notes.getId() == null) {

				notes.setId(new StoreForwardUtility().generateNewNumber(detailStatus.getLocationsId(), em,
						"goods_receive_notes", httpRequest));
			}
			notes = em.merge(notes);

			logger.severe("notes====================================================================================="
					+ notes);

			goodsReceiveNotes.add(notes);

			if (inventoryItemsLists != null) {

				for (InventoryItemsList list : inventoryItemsLists) {
					if (list.getRequestOrderDetailsId() == items.getId()) {
						// Inventory inventory = (Inventory) new
						// CommonMethods().getObjectById("Inventory",
						// em,Inventory.class,list.getInventoryId());
						Item localItem = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
								items.getItemsId());

						Inventory inventory = null;

						try {
							inventory = new InventoryManagementHelper().getInventoryForItemId(em, items.getItemsId(),
									localItem.getLocationsId(), false);
						} catch (Exception nre) {
							// no inventory found
							logger.severe("No inventory found for global item id: " + localItem.getGlobalItemId());
						}
						inventory.setGrnNumber(notes.getId());
						inventory.setOrderDetailItemId(null);
						inventory = em.merge(inventory);

						InventoryHistory inventoryHistory = em.find(InventoryHistory.class,
								list.getInventoryHistoryId());
						inventoryHistory.setGrnNumber(notes.getId());
						inventoryHistory = em.merge(inventoryHistory);
					}
				}
			}

		}
		return goodsReceiveNotes;

	}

	GoodsReceiveNotesPacket receiveRequestOrder(HttpServletRequest httpRequest, EntityManager em,
			GoodsReceiveNotesPacket goodsReceiveNotesPacket, boolean isDirect) throws Exception {
		String locationsId = goodsReceiveNotesPacket.getLocationId();
		 
		OrderDetailStatus detailStatus = getOrderDetailStatusByNameAndLocationId(em, "Item Received", locationsId);
		OrderStatus orderStatus = getOrderStatusByNameAndLocation(em, "Request Received", locationsId);
		for (GoodsReceiveNotes goodsReceiveNotes : goodsReceiveNotesPacket.getGoodsReceiveNotesList()) {
			RequestOrderDetailItems requestOrderDetailItems = (RequestOrderDetailItems) new CommonMethods()
					.getObjectById("RequestOrderDetailItems", em, RequestOrderDetailItems.class,
							goodsReceiveNotes.getRequestOrderDetailsItemId());
			if(requestOrderDetailItems!=null){
				
			 
			requestOrderDetailItems.setAllotmentQty(requestOrderDetailItems.getQuantity());
			logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@requestOrderDetailItems:- "
					+ requestOrderDetailItems.toString());
			BigDecimal prevReceivedQty = requestOrderDetailItems.getAllotmentQty();
			// requestOrderDetailItems.setAllotmentQty(goodsReceiveNotes.getReceivedQuantity());

			if (requestOrderDetailItems != null) {

				// calculating total receive
				logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@requestOrderDetailItems1:- "
						+ requestOrderDetailItems.toString());
				RequestOrder requestOrder = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,
						RequestOrder.class, requestOrderDetailItems.getRequestId());

				try {
					receiveInventoryWithItemForRequestAllot(httpRequest, em, requestOrder.getLocationId(),
							requestOrderDetailItems, new BigDecimal(0), goodsReceiveNotesPacket, goodsReceiveNotes,
							isDirect);
				} catch (Exception e) {
					logger.severe(e);
				}
				if (!em.getTransaction().isActive()) {
					em.getTransaction().begin();
				}
				requestOrderDetailItems.setAllotmentQty(prevReceivedQty);
				requestOrderDetailItems.setInTransitQty(
						requestOrderDetailItems.getInTransitQty().subtract(goodsReceiveNotes.getReceivedQuantity()));

				BigDecimal receivedQty = requestOrderDetailItems.getReceivedQuantity()
						.add(goodsReceiveNotes.getReceivedQuantity());

				requestOrderDetailItems.setReceivedQuantity(receivedQty);

				requestOrderDetailItems.setAllotmentQty(requestOrderDetailItems.getAllotmentQty());
				// calculating balance qty
				BigDecimal balanceQty = requestOrderDetailItems.getQuantity()
						.subtract(requestOrderDetailItems.getReceivedQuantity());
				requestOrderDetailItems.setBalance(balanceQty);
				goodsReceiveNotes.setUomName(requestOrderDetailItems.getUomName());
				goodsReceiveNotes.setRequestOrderDetailItemName(requestOrderDetailItems.getItemName());
				if (balanceQty.compareTo(new BigDecimal(0)) == 0 && detailStatus != null) {
					requestOrderDetailItems.setStatusId(detailStatus.getId());
					goodsReceiveNotes.setStatusId(detailStatus.getId());

				}
				// if balance is 0 then item is fully received alse partially
				if (goodsReceiveNotes.getBalance() != null
						&& goodsReceiveNotes.getBalance().compareTo((new BigDecimal(0))) == 0) {
					goodsReceiveNotes.setIsGRNClose(1);
				}
				if (isDirect) {
					goodsReceiveNotes.setIsGRNClose(1);
				}

				logger.severe("goodsReceiveNotes.getId()======================================================="
						+ goodsReceiveNotes.getId());
				if (goodsReceiveNotesPacket.getLocalServerURL() == 1) {
					GoodsReceiveNotes note = getGoodsReceiveNoteByNumber(em, goodsReceiveNotes.getGrnNumber());
					if (note != null) {
						goodsReceiveNotes.setId(note.getId());
					}
				}
				logger.severe(
						"goodsReceiveNotes.getId()====================2222222222222==================================="
								+ goodsReceiveNotes.getId());

				if (goodsReceiveNotes.getId() != null) {
					goodsReceiveNotes.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				} else {
					goodsReceiveNotes.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					goodsReceiveNotes.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}

				goodsReceiveNotes.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationsId, em));
				 
				goodsReceiveNotes = em.merge(goodsReceiveNotes);
				requestOrderDetailItems = em.merge(requestOrderDetailItems);

				RequestOrder requestOrder1 = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,
						RequestOrder.class, requestOrderDetailItems.getRequestId());
				List<RequestOrderDetailItems> requestOrderDetailItemsList = getRequestOrderDetailItemByIdAndStatus(em,
						requestOrder1.getId(), detailStatus.getId());
				if (requestOrderDetailItemsList == null || requestOrderDetailItemsList.size() == 0) {
					requestOrder1.setStatusId(orderStatus.getId());

					requestOrder1 = em.merge(requestOrder1);

				}

			}
		}
		}
		goodsReceiveNotesPacket.getGoodsReceiveNotesList();
		return goodsReceiveNotesPacket;

	}

	public List<RequestOrderDetailItems> getRequestOrderDetailItemByIdAndStatus(EntityManager em, String id,
			int statusId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<RequestOrderDetailItems> criteria = builder.createQuery(RequestOrderDetailItems.class);
			Root<RequestOrderDetailItems> r = criteria.from(RequestOrderDetailItems.class);
			TypedQuery<RequestOrderDetailItems> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(RequestOrderDetailItems_.requestId), id),
							builder.notEqual(r.get(RequestOrderDetailItems_.statusId), statusId)));
			List<RequestOrderDetailItems> requestOrderDetailItemsList = query.getResultList();
			return requestOrderDetailItemsList;
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}

	RequestOrder allotmentRequestOrderForIntra(HttpServletRequest httpRequest, EntityManager em,
			RequestOrder requestOrder, PostPacket postPacket, RequestOrderPacket requestOrderPacket) throws Exception {

		// get the relation sets
		String grnNumber = "";
		RequestOrder order = getRequestOrderById(em, requestOrder.getId(), true);
		order.setStatusId(requestOrder.getStatusId());

		order.setRequestOrderDetailItems(requestOrder.getRequestOrderDetailItems());
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				requestOrder.getStatusId());
		String orderStatusName = orderStatus.getName();
		// calculation after inserting into system are as follows
		/*
		 * try { if ((!orderStatusName.equals("PO Forcefully Closed")) &&
		 * (!orderStatusName.equals("Request Forcefully Closed")) &&
		 * (!orderStatusName.equals("Request Cancelled")) &&
		 * (!orderStatusName.equals("PO Cancelled"))) {
		 * consumeInventoryWithItemForRequestAllotForIntra(httpRequest, em,
		 * requestOrder, order.getRequestOrderDetailItems(), new BigDecimal(0),
		 * postPacket); } } catch (Exception e) { logger.severe(e); }
		 */

		List<InventoryItemsList> inventoryItemsList = new ArrayList<InventoryItemsList>();

		deductIntraLocationInventory(em, requestOrder, httpRequest, order.getRequestOrderDetailItems(),
				inventoryItemsList);

		try

		{
			if (postPacket.getLocalServerURL() == 0) {
				order.setGrnCount(order.getGrnCount() + 1);
			}
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					order.getLocationId());

			String name = location.getName().replace("(", "").replace(")", "");
			String[] locationNameString = name.split(" ");
			String locationName = "";
			for (String locationNameObj : locationNameString) {
				locationName = locationName + locationNameObj.substring(0, 1);
			}
			grnNumber = locationName + "-" + order.getId() + "-" + order.getGrnCount();
			String grnDate = requestOrder.getGrnDate();

			if ((!orderStatusName.equals("PO Forcefully Closed"))
					&& (!orderStatusName.equals("Request Forcefully Closed"))
					&& (!orderStatusName.equals("Request Cancelled")) && (!orderStatusName.equals("PO Cancelled"))) {

				insertGoodsReceiveNotes(em, requestOrder.getRequestOrderDetailItems(), grnNumber, true, grnDate, false,
						false, true, inventoryItemsList, requestOrderPacket.getSupplierRefNo(), httpRequest,requestOrder.getGrnDate(),requestOrder.getDepartmentId());
			}
		} catch (Exception e) {
			logger.severe(e);
		}

		//
		// deductIntraLocationInventory(em, requestOrder, httpRequest,
		// order.getRequestOrderDetailItems());

		List<RequestOrderDetailItems> newList = new ArrayList<RequestOrderDetailItems>();
		if (order.getRequestOrderDetailItems() != null) {
			for (RequestOrderDetailItems detailItem : order.getRequestOrderDetailItems()) {
				RequestOrderDetailItems requestOrderDetailItemsDB = (RequestOrderDetailItems) new CommonMethods()
						.getObjectById("RequestOrderDetailItems", em, RequestOrderDetailItems.class,
								detailItem.getId());
				if (detailItem.getBalance() == null) {
					detailItem.setBalance(new BigDecimal(0));
				}
				// received+intrasit= alloted
				// calculating total receive
				if (requestOrderDetailItemsDB.getInTransitQty() == null) {
					requestOrderDetailItemsDB.setInTransitQty(new BigDecimal(0));
				}
				detailItem
						.setInTransitQty(detailItem.getAllotmentQty().add(requestOrderDetailItemsDB.getInTransitQty()));

				detailItem
						.setAllotmentQty(detailItem.getAllotmentQty().add(requestOrderDetailItemsDB.getAllotmentQty()));
				// calculating balance qty
				if (detailItem.getReceivedQuantity() == null) {
					detailItem.setReceivedQuantity(new BigDecimal(0));
				}
				detailItem.setReceivedQuantity(
						requestOrderDetailItemsDB.getReceivedQuantity().add(detailItem.getReceivedQuantity()));
				BigDecimal balanceQty = detailItem.getQuantity().subtract(detailItem.getReceivedQuantity());

				detailItem.setBalance(balanceQty);
				newList.add(detailItem);
			}
		}
		order.setRequestOrderDetailItems(newList);

		RequestOrder objToReturn = addUpdateRequestOrderWithItems(em, order, httpRequest, requestOrderPacket);
		new InsertIntoHistory().insertRequestOrderIntoHistory(httpRequest, objToReturn, em);
		objToReturn.setChallanNumber(grnNumber);
		return objToReturn;

	}

	RequestOrder allotmentRequestOrder(HttpServletRequest httpRequest, EntityManager em, RequestOrder requestOrder,
			PostPacket postPacket, RequestOrderPacket requestOrderPacket) throws Exception {

		// get the relation sets
		String grnNumber = "";
		RequestOrder order = getRequestOrderById(em, requestOrder.getId(), true);
		order.setStatusId(requestOrder.getStatusId());

		order.setRequestOrderDetailItems(requestOrder.getRequestOrderDetailItems());
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				requestOrder.getStatusId());
		String orderStatusName = orderStatus.getName();
		// calculation after inserting into system are as follows

		List<InventoryItemsList> inventoryItemsList = new ArrayList<InventoryItemsList>();

		try {
			if ((!orderStatusName.equals("PO Forcefully Closed"))
					&& (!orderStatusName.equals("Request Forcefully Closed"))
					&& (!orderStatusName.equals("Request Cancelled")) && (!orderStatusName.equals("PO Cancelled"))) {
				consumeInventoryWithItemForRequestAllot(httpRequest, em, requestOrder,
						order.getRequestOrderDetailItems(), new BigDecimal(0), postPacket, inventoryItemsList);
			}
		} catch (Exception e) {
			logger.severe(e);
		}
		try

		{
			if (postPacket.getLocalServerURL() == 0) {
				order.setGrnCount(order.getGrnCount() + 1);
			}
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					order.getLocationId());

			String name = location.getName().replace("(", "").replace(")", "");
			String[] locationNameString = name.split(" ");
			String locationName = "";
			for (String locationNameObj : locationNameString) {
				locationName = locationName + locationNameObj.substring(0, 1);
			}
			grnNumber = locationName + "-" + order.getId() + "-" + order.getGrnCount();
			String grnDate = requestOrder.getGrnDate();

			if ((!orderStatusName.equals("PO Forcefully Closed"))
					&& (!orderStatusName.equals("Request Forcefully Closed"))
					&& (!orderStatusName.equals("Request Cancelled")) && (!orderStatusName.equals("PO Cancelled"))) {

				insertGoodsReceiveNotes(em, requestOrder.getRequestOrderDetailItems(), grnNumber, true, grnDate, false,
						false, true, inventoryItemsList, requestOrderPacket.getSupplierRefNo(), httpRequest,requestOrder.getGrnDate(),requestOrder.getDepartmentId());
			}
		} catch (Exception e) {
			logger.severe(e);
		}

		//
		deductIntraLocationInventory(em, requestOrder, httpRequest, order.getRequestOrderDetailItems(),
				inventoryItemsList);

		List<RequestOrderDetailItems> newList = new ArrayList<RequestOrderDetailItems>();
		if (order.getRequestOrderDetailItems() != null) {
			for (RequestOrderDetailItems detailItem : order.getRequestOrderDetailItems()) {
				RequestOrderDetailItems requestOrderDetailItemsDB = (RequestOrderDetailItems) new CommonMethods()
						.getObjectById("RequestOrderDetailItems", em, RequestOrderDetailItems.class,
								detailItem.getId());
				if (detailItem.getBalance() == null) {
					detailItem.setBalance(new BigDecimal(0));
				}
				// received+intrasit= alloted
				// calculating total receive
				if (requestOrderDetailItemsDB.getInTransitQty() == null) {
					requestOrderDetailItemsDB.setInTransitQty(new BigDecimal(0));
				}
				detailItem
						.setInTransitQty(detailItem.getAllotmentQty().add(requestOrderDetailItemsDB.getInTransitQty()));

				detailItem
						.setAllotmentQty(detailItem.getAllotmentQty().add(requestOrderDetailItemsDB.getAllotmentQty()));
				// calculating balance qty
				if (detailItem.getReceivedQuantity() == null) {
					detailItem.setReceivedQuantity(new BigDecimal(0));
				}
				detailItem.setReceivedQuantity(
						requestOrderDetailItemsDB.getReceivedQuantity().add(detailItem.getReceivedQuantity()));
				BigDecimal balanceQty = detailItem.getQuantity().subtract(detailItem.getReceivedQuantity());

				detailItem.setBalance(balanceQty);
				newList.add(detailItem);
			}
		}
		order.setRequestOrderDetailItems(newList);

		RequestOrder objToReturn = addUpdateRequestOrderWithItems(em, order, httpRequest, requestOrderPacket);
		new InsertIntoHistory().insertRequestOrderIntoHistory(httpRequest, objToReturn, em);
		objToReturn.setChallanNumber(grnNumber);
		return objToReturn;

	}

	public List<String> getAllOrderStatusForPendingRequestByNameAndLocation(EntityManager em, String locationId) {
		String statusName = "'Request Cancelled','Request Received','Request Rejected','Request Forcefully Closed','PO Cancelled','PO Received','PO Rejected','PO Forcefully Closed'";
		String queryString = "select id from order_status os where os.locations_id = ? and os.name in (" + statusName
				+ ")";

		Query query = em.createNativeQuery(queryString).setParameter(1, locationId);

		return (List<String>) query.getResultList();
	}

	private void receiveInventoryWithItemForRequestAllot(HttpServletRequest httpRequest, EntityManager em,
			String locationId, RequestOrderDetailItems requestOrderDetailItems, BigDecimal defaultInventorythreashold,
			PostPacket postPacket, GoodsReceiveNotes goodsReceiveNotes, boolean isDirectAllot) throws Exception {
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		// get inventory object corresponding to this inventory order receipt
		// based on item id

		if (requestOrderDetailItems != null) {

			// find global itemId of requested item :-
			Item localItem = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
					requestOrderDetailItems.getItemsId());

			Inventory inventory = null;

			if (isDirectAllot) {
				try {
					Location location = null;
					if (isDirectAllot) {
						logger.severe(isDirectAllot+"@@@@@@@@@@@@@@@@@@@@@@@"+locationId+"@@@@@@@@@@@@@@@@@@@@++++++++++++"+localItem.getId());
						location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
								locationId);
						inventory = inventoryManagementHelper.getInventoryForItemId(em, localItem.getId(), locationId,
								false);
					} else {
						logger.severe(isDirectAllot+"outside@@@@@@@@@@@@@@@@@@@@@@@"+locationId+"@@@@@@@@@@@@@@@@@@@@++++++++++++"+localItem.getId());
						location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
								requestOrderDetailItems.getRequestTo());
						if (location.getLocationsTypeId() == 1) {

						} else {
							inventory = inventoryManagementHelper.getInventoryForItemId(em, localItem.getId(),
									locationId, true);
						}
					}

				} catch (Exception nre) {
					// no inventory found
					logger.severe(nre);
				}
			} else {
				try {

					Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
							requestOrderDetailItems.getRequestTo());
					logger.severe(isDirectAllot+"outside@@@@@@@@@@@@@@@@@@@@@@@"+locationId+"@@@@@@@@@@@@@@@@@@@@++++++++++++"+localItem.getId());
					
					if (location.getLocationsTypeId() == 1) {
						logger.severe(isDirectAllot+"outside11@@@@@@@@@@@@@@@@@@@@@@@"+locationId+"@@@@@@@@@@@@@@@@@@@@++++++++++++"+localItem.getId());
						inventory = inventoryManagementHelper.getInventoryForItemId(em, localItem.getId(), locationId,
								false);
					} else {
						logger.severe(isDirectAllot+"2outside@@@@@@@@@@@@@@@@@@@@@@@"+locationId+"@@@@@@@@@@@@@@@@@@@@++++++++++++"+localItem.getId());
						inventory = inventoryManagementHelper.getInventoryForItemId(em, localItem.getId(), locationId,
								true);
					}

				} catch (Exception nre) {
					// no inventory found
					logger.severe(nre);
				}
			}

			BigDecimal inventoryPrevQty = null;

			if (inventory == null) {

				inventory = inventoryManagementHelper.addInventoryWithItemForPO(em, requestOrderDetailItems,
						defaultInventorythreashold, locationId);
				// add inventory item default also for this one
				InventoryItemDefault inventoryItemDefaultDB = inventoryManagementHelper
						.getInventoryItemDefaultForItemId(em, requestOrderDetailItems.getItemsId());
				// add inventory item default also for this one
				if (inventoryItemDefaultDB == null) {
					InventoryItemDefault inventoryItemDefault = new InventoryItemDefault();
					inventoryItemDefault.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventoryItemDefault.setCreatedBy(requestOrderDetailItems.getCreatedBy());
					inventoryItemDefault.setItemId(requestOrderDetailItems.getItemsId());
					inventoryItemDefault.setEconomicOrderQuantity(new BigDecimal(0));
					inventoryItemDefault.setMinimumOrderQuantity(new BigDecimal(0));
					inventoryItemDefault.setD86Threshold(defaultInventorythreashold);
					inventoryItemDefault.setStatus("A");
					em.persist(inventoryItemDefault);

				}
			} else {
				InventoryItemDefault itemDefault = inventoryManagementHelper
						.getInventoryItemDefault(inventory.getItemId(), em);
				if (itemDefault != null && itemDefault.getD86Threshold() != null) {
					defaultInventorythreashold = itemDefault.getD86Threshold();
				}

				inventoryPrevQty = inventory.getTotalUsedQuanity();
				Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class, localItem.getId());

				Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
						requestOrderDetailItems.getRequestTo());

				/*
				 * if(location.getIsGlobalLocation() == 1) { inventory =
				 * inventoryManagementHelper.updateInventoryWithItemForPO(
				 * httpRequest, em, inventory, requestOrderDetailItems, true,
				 * item, logger, true, false, false); }else {
				 */
				inventory = inventoryManagementHelper.updateInventoryWithItemForPO(httpRequest, em, inventory,
						requestOrderDetailItems, true, item, logger, false, true, false);
				// }

			}

			if (inventory != null) {
				inventory.setGrnNumber(goodsReceiveNotes.getId());
				inventory.setOrderDetailItemId(null);
				inventory = em.merge(inventory);

			}

			Item item = inventoryManagementHelper.manageItemBasedOnInventoryOrderReceipt(httpRequest, em,
					requestOrderDetailItems.getItemsId(), inventory, defaultInventorythreashold.doubleValue(), false,
					inventoryPrevQty, null);
			em.getTransaction().commit();

			// we must send push as item threshold or is in stock is
			// modified

			if (item != null) {
				try {
					// send packet for broadcast as item has been modified
					InventoryPostPacket inventoryPostPacketNew = inventoryManagementHelper.getPushPacket(inventory,
							null, defaultInventorythreashold);

					inventoryPostPacketNew.setMerchantId(postPacket.getMerchantId());
					inventoryPostPacketNew.setLocationId(postPacket.getLocationId());
					inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacketNew,
							POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());
				} catch (Exception e) {
					logger.severe(e);
				}

			}

		}

	}

	RequestOrder directRequestAllocation(HttpServletRequest httpRequest, EntityManager em,
			RequestOrderPacket postPacket) throws Exception {
		RequestOrder requestOrder = postPacket.getRequestOrder();
		String grnNumber = "";
		List<RequestOrderDetailItems> detailItems = requestOrder.getRequestOrderDetailItems();
		List<RequestOrderDetailItems> details = new ArrayList<RequestOrderDetailItems>();
		if (requestOrder.getId() != null && requestOrder.getId() != null) {
			RequestOrder requestOrder2 = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,
					RequestOrder.class, requestOrder.getId());
			if (requestOrder2 != null) {
				requestOrder.setCreated(requestOrder2.getCreated());
			} else {
				requestOrder.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			}
		}

		requestOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (postPacket.getLocalServerURL() == 0) {
			requestOrder.setGrnCount(requestOrder.getGrnCount() + 1);
		}
		String grnDate = requestOrder.getGrnDate();
		requestOrder.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getLocationId(), em));
		if (requestOrder.getId() == null) {
			requestOrder.setId(new StoreForwardUtility().generateNewNumber(requestOrder.getLocationId(), em,
					"request_order", httpRequest));
		}
		requestOrder = em.merge(requestOrder);

		List<InventoryItemsList> inventoryItemsLists = new ArrayList<InventoryItemsList>();

		try {
			consumeInventoryWithItemForRequestAllot(httpRequest, em, requestOrder, detailItems, new BigDecimal(0),
					postPacket, inventoryItemsLists);

		} catch (Exception e) {
			logger.severe(e);
		}
		for (RequestOrderDetailItems requestOrderDetailItem : detailItems) {
			try {
				// receiveInventoryWithItemForRequestAllot(httpRequest, em,
				// requestOrder.getLocationId(), requestOrderDetailItem, new
				// BigDecimal(0), postPacket);
			} catch (Exception e) {
				logger.severe(e);
			}
			if (!em.getTransaction().isActive()) {
				em.getTransaction().begin();
			}

			OrderDetailStatus detailStatus = em.find(OrderDetailStatus.class, requestOrderDetailItem.getStatusId());
			if (detailStatus != null) {
				requestOrderDetailItem.setLocalTime(
						new TimezoneTime().getLocationSpecificTimeToAdd(detailStatus.getLocationsId(), em));
			}

			requestOrderDetailItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			requestOrderDetailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			requestOrderDetailItem.setBalance(requestOrderDetailItem.getQuantity());

			requestOrderDetailItem.setRequestId(requestOrder.getId());
			if (requestOrderDetailItem.getId() == null) {
				requestOrderDetailItem.setId(new StoreForwardUtility().generateUUID());
			}
			requestOrderDetailItem = em.merge(requestOrderDetailItem);
			details.add(requestOrderDetailItem);
		}

		try {
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					requestOrder.getLocationId());
			String name = location.getName().replace("(", "").replace(")", "");
			String[] locationNameString = name.split(" ");
			String locationName = "";
			for (String locationNameObj : locationNameString) {
				locationName = locationName + locationNameObj.substring(0, 1);
			}
			grnNumber = locationName + "-" + requestOrder.getId() + "-" + requestOrder.getGrnCount();

			OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
					OrderStatus.class, requestOrder.getStatusId());
			String orderStatusName = orderStatus.getName();

			if (!orderStatusName.equals("PO Forcefully Closed") && !orderStatusName.equals("Request Forcefully Closed")
					&& !orderStatusName.equals("Request Cancelled") && !orderStatusName.equals("PO Cancelled")) {
				insertGoodsReceiveNotes(em, details, grnNumber, true, grnDate, false, false, true, inventoryItemsLists,
						postPacket.getSupplierRefNo(), httpRequest,requestOrder.getGrnDate(),requestOrder.getDepartmentId());
			}
		} catch (Exception e) {
			logger.severe(e);
		}
		requestOrder.setChallanNumber(grnNumber);
		requestOrder.setRequestOrderDetailItems(details);

		new InsertIntoHistory().insertRequestOrderIntoHistory(httpRequest, requestOrder, em);
		return requestOrder;

	}

	RequestOrder updateDirectRequestAllocationStatus(HttpServletRequest httpRequest, EntityManager em,
			RequestOrder requestOrder) throws Exception {
		List<RequestOrderDetailItems> detailItems = requestOrder.getRequestOrderDetailItems();
		List<RequestOrderDetailItems> details = new ArrayList<RequestOrderDetailItems>();

		RequestOrder requestOrderDB = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,
				RequestOrder.class, requestOrder.getId());

		requestOrderDB.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		requestOrderDB.setStatusId(requestOrder.getStatusId());
		requestOrder = em.merge(requestOrderDB);
		/*
		 * CriteriaBuilder builder = em.getCriteriaBuilder();
		 * CriteriaQuery<RequestOrderDetailItems> criteria =
		 * builder.createQuery(RequestOrderDetailItems.class);
		 * Root<RequestOrderDetailItems> r =
		 * criteria.from(RequestOrderDetailItems.class);
		 * TypedQuery<RequestOrderDetailItems> query =
		 * em.createQuery(criteria.select(r).where(
		 * builder.equal(r.get(RequestOrderDetailItems_.requestId),
		 * requestOrder.getId()))); List<RequestOrderDetailItems>
		 * requestOrderDetailItemList = query.getResultList();
		 */
		for (RequestOrderDetailItems requestOrderDetailItem : detailItems) {
			requestOrderDetailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			if (requestOrderDetailItem.getBalance() == null) {
				requestOrderDetailItem.setBalance(new BigDecimal(0));
			}
			requestOrderDetailItem = em.merge(requestOrderDetailItem);
			details.add(requestOrderDetailItem);
		}
		requestOrder.setRequestOrderDetailItems(details);

		new InsertIntoHistory().insertRequestOrderIntoHistory(httpRequest, requestOrder, em);
		return requestOrder;

	}

	RequestOrderDetailItems cancelRequestOrderDetailItems(HttpServletRequest httpRequest, EntityManager em,
			String requestOrderDetailItemsId) throws Exception {

		RequestOrderDetailItems requestOrderDetailItem = (RequestOrderDetailItems) new CommonMethods()
				.getObjectById("RequestOrderDetailItems", em, RequestOrderDetailItems.class, requestOrderDetailItemsId);
		OrderDetailStatus orderDetailStatus = getOrderDetailStatusByName(em, "Item Rejected");
		requestOrderDetailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		requestOrderDetailItem.setStatusId(orderDetailStatus.getId());
		requestOrderDetailItem = em.merge(requestOrderDetailItem);
		new InsertIntoHistory().insertRequestOrderDetailItemsIntoHistory(httpRequest, requestOrderDetailItem, em);
		return requestOrderDetailItem;

	}

	private OrderDetailStatus getOrderDetailStatusByName(EntityManager em, String statusName) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
		Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
		TypedQuery<OrderDetailStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.name), statusName),
						builder.notEqual(r.get(OrderDetailStatus_.status), "D")));
		return query.getSingleResult();
	}

	List<GoodsReceiveNotes> cancelGoodsReceiveNotes(HttpServletRequest httpRequest, EntityManager em,
			GoodsReceiveNotesPacket postPacket) throws Exception {
		//
		BigDecimal allotmentQty = new BigDecimal(0);
		List<GoodsReceiveNotes> newGoodsReceiveNotesList = new ArrayList<GoodsReceiveNotes>();
		List<RequestOrderDetailItems> requestOrderDetailItemsList = new ArrayList<RequestOrderDetailItems>();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<GoodsReceiveNotes> criteria = builder.createQuery(GoodsReceiveNotes.class);
		Root<GoodsReceiveNotes> r = criteria.from(GoodsReceiveNotes.class);
		TypedQuery<GoodsReceiveNotes> query = em.createQuery(
				criteria.select(r).where(builder.equal(r.get(GoodsReceiveNotes_.grnNumber), postPacket.getGrnNumber()),
						builder.notEqual(r.get(GoodsReceiveNotes_.status), "D")));
		List<GoodsReceiveNotes> goodsReceiveNotesList = query.getResultList();
		RequestOrder order = new RequestOrder();
		for (GoodsReceiveNotes goodsReceiveNotes : goodsReceiveNotesList) {
			goodsReceiveNotes.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			goodsReceiveNotes.setStatus("D");
			goodsReceiveNotes = em.merge(goodsReceiveNotes);
			newGoodsReceiveNotesList.add(goodsReceiveNotes);
			RequestOrderDetailItems detailItems = (RequestOrderDetailItems) new CommonMethods().getObjectById(
					"RequestOrderDetailItems", em, RequestOrderDetailItems.class,
					goodsReceiveNotes.getRequestOrderDetailsItemId());
			order = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em, RequestOrder.class,
					detailItems.getRequestId());
			// inventory management code

			OrderDetailStatus detailStatus = getOrderDetailStatusByNameAndLocationId(em, "Item Requested",
					order.getLocationId());
			OrderStatus orderStatus = getOrderStatusByNameAndLocation(em, "Request In Process", order.getLocationId());
			allotmentQty = goodsReceiveNotes.getAllotmentQty();
			detailItems.setAllotmentQty(detailItems.getAllotmentQty().subtract(goodsReceiveNotes.getAllotmentQty()));
			detailItems.setStatusId(detailStatus.getId());
			detailItems.setBalance(detailItems.getBalance().subtract(goodsReceiveNotes.getAllotmentQty()));

			BigDecimal allotQty = new BigDecimal(0);
			if (goodsReceiveNotes.getAllotmentQty() != null) {
				allotQty = goodsReceiveNotes.getAllotmentQty();
			}

			BigDecimal inTransitQty = new BigDecimal(0);
			if (detailItems.getInTransitQty() != null) {
				inTransitQty = detailItems.getInTransitQty();
			}
			detailItems.setInTransitQty(inTransitQty.subtract(allotQty));

			em.merge(detailItems);
			if (order.getStatusId() == orderStatus.getId()) {
				OrderStatus orderStatus1 = getOrderStatusByNameAndLocation(em, "Request Sent", order.getLocationId());
				order.setStatusId(orderStatus1.getId());
				order = em.merge(order);
			}
			// for adding in global inventory
			// RequestOrderDetailItems detailItems2 =
			// (RequestOrderDetailItems) new
			// CommonMethods().getObjectById("RequestOrderDetailItems",
			// em,RequestOrderDetailItems.class,
			// goodsReceiveNotes.getRequestOrderDetailsItemId());
			//
			// detailItems2.setAllotmentQty(allotmentQty);
			requestOrderDetailItemsList.add(detailItems);

		}

		try {
			// TODO Ankur: is it ok to not update inventory if allotment is
			// cancelled?
			returnInventoryWithItemForAllotMentCancelled(httpRequest, em, order, requestOrderDetailItemsList,
					new BigDecimal(0), postPacket, allotmentQty);
		} catch (Exception e) {
			logger.severe(e);
		}

		return newGoodsReceiveNotesList;

	}

	private UnitConversion insertUnitConversion(UnitOfMeasurement unitOfMeasurement, EntityManager em) {

		UnitConversion uomTemp = getUnitConversion(em, unitOfMeasurement.getId(), unitOfMeasurement.getId());
		if (uomTemp == null) {
			uomTemp = new UnitConversion();
			uomTemp.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			uomTemp.setCreatedBy(unitOfMeasurement.getCreatedBy());
			uomTemp.setFromUOMId(unitOfMeasurement.getId());
			uomTemp.setToUOMId(unitOfMeasurement.getId());
			uomTemp.setStatus("A");
			uomTemp.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			uomTemp.setUpdatedBy(unitOfMeasurement.getUpdatedBy());
			uomTemp.setConversionRatio(BigDecimal.ONE);
			/*
			 * if(unitOfMeasurement.getUnitConversionList() != null) {
			 * globalUnitOfMeasurement.getUnitConversionList().add(uom); }else {
			 * globalUnitOfMeasurement.setUnitConversionList(new
			 * ArrayList<UnitConversion>());
			 * globalUnitOfMeasurement.getUnitConversionList().add(uom); }
			 */

			uomTemp = em.merge(uomTemp);

		}
		return uomTemp;
	}

	private UnitConversion getUnitConversion(EntityManager em, String fromId, String toId) {
		try {
			String queryString = "select s from UnitConversion s where s.fromUOMId =? and s.toUOMId=?  ";
			TypedQuery<UnitConversion> query = em.createQuery(queryString, UnitConversion.class).setParameter(1, fromId)
					.setParameter(2, toId);
			return query.getSingleResult();
		} catch (Exception e) {
			logger.severe("no result found");
		}
		return null;
	}

	public List<RequestOrder> getAllPendingPO(EntityManager em, String locationId) {
		List<String> orderStatus = getAllOrderStatusForPendingRequestByNameAndLocation(em, locationId);
		String orderStatusIds = orderStatus.toString();
		orderStatusIds = orderStatusIds.replace("[", "").replace("]", "");

		String queryString = "select ro from RequestOrder ro where ro.locationId = ? and ro.isPOOrder = 1 and ro.statusId not in ("
				+ orderStatusIds + ")";
		TypedQuery<RequestOrder> query = em.createQuery(queryString, RequestOrder.class).setParameter(1, locationId);

		List<RequestOrder> requestOrderList = query.getResultList();
		return requestOrderList;
	}

	private ItemsType getItemsTypeByName(EntityManager em, String name) {
		ItemsType itemsType = null;
		try {
			String queryString = "select s from ItemsType s where s.name = '" + name + "'";
			TypedQuery<ItemsType> query = em.createQuery(queryString, ItemsType.class);
			itemsType = query.getSingleResult();

		} catch (NoResultException e) {

			logger.severe(e);

		}

		return itemsType;
	}

	private LocationsType getSupplierTypeByName(EntityManager em, String name) {
		LocationsType locationsType = null;
		try {
			String queryString = "select s from LocationsType s where s.name = '" + name + "'";
			TypedQuery<LocationsType> query = em.createQuery(queryString, LocationsType.class);
			locationsType = query.getSingleResult();

		} catch (NoResultException e) {

			logger.severe(e);

		}

		return locationsType;
	}

	private Location getLocationById(EntityManager em, String id) {
		Location locations = null;
		try {
			String queryString = "select s from Location s where s.id = '" + id + "'";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			locations = query.getSingleResult();

		} catch (NoResultException e) {

			logger.severe(e);

		}

		return locations;
	}

	public ItemAttributeIngrediantDisplayPacket getAllIngredientByItemIdAndAttributeId(EntityManager em,
			ItemAttributeIngrediantGetPacket itemAttributeIngrediantGetPacket) {

		List<AttributeItemBomDisplayPacket> attributeItemBomDisplayPacketList = new ArrayList<AttributeItemBomDisplayPacket>();
		ItemIngrediantPacket itemIngrediantPacket = new ItemIngrediantPacket();
		List<ItemIngrediantDisplayData> itemIngrediantDisplayDatas = new ArrayList<ItemIngrediantDisplayData>();

		@SuppressWarnings("unchecked")
		List<Object[]> resultListItem = em
				.createNativeQuery(
						"call getItemIngredientUsingItemId(" + itemAttributeIngrediantGetPacket.getItemId() + " )")
				.getResultList();

		for (Object[] objRow : resultListItem) {
			int i = 0;
			if ((String) objRow[i] != null) {
				ItemIngrediantDisplayData displayData = new ItemIngrediantDisplayData();
				displayData.setIngredientId((String) objRow[i++]);
				displayData.setIngredientName((String) objRow[i++]);
				displayData.setIngredientQuantity((BigDecimal) objRow[i++]);
				displayData.setUomId((String) objRow[i++]);
				displayData.setIngredientUOMName((String) objRow[i++]);
				itemIngrediantDisplayDatas.add(displayData);

			}
			if (itemIngrediantDisplayDatas != null && itemIngrediantDisplayDatas.size() > 0) {
				itemIngrediantPacket.setItemId(itemAttributeIngrediantGetPacket.getItemId());
				itemIngrediantPacket.setItemIngredient(itemIngrediantDisplayDatas);
			}
		}

		AttributeIngrediantPacket attributeIngrediantPacket = new AttributeIngrediantPacket();
		List<AttributeIngrediantDisplayData> attributeIngrediantDisplayDatas = new ArrayList<AttributeIngrediantDisplayData>();

		if (itemAttributeIngrediantGetPacket.getAttributeId() != null
				&& itemAttributeIngrediantGetPacket.getAttributeId().size() > 0) {
			String attributeIds = "";
			for (int i = 0; i < (itemAttributeIngrediantGetPacket.getAttributeId().size()); i++) {
				List<String> id = itemAttributeIngrediantGetPacket.getAttributeId();
				if (i == (itemAttributeIngrediantGetPacket.getAttributeId().size() - 1)) {
					attributeIds += id.get(i);
				} else {
					attributeIds += id.get(i) + ",";
				}
			}
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em
					.createNativeQuery("call getAttributeItemIngredientUsingAttributeIdAndItemId('" + attributeIds
							+ "'," + itemAttributeIngrediantGetPacket.getItemId() + " )")
					.getResultList();

			for (Object[] row : resultList) {
				int i = 0;
				if ((String) row[i] != null) {
					AttributeIngrediantDisplayData displayData = new AttributeIngrediantDisplayData();
					displayData.setAttributeId((String) row[i++]);
					displayData.setIngredientId((String) row[i++]);
					displayData.setUomId((String) row[i++]);
					displayData.setIngredientQuantity((BigDecimal) row[i++]);
					displayData.setIngredientName((String) row[i++]);
					displayData.setIngredientUOMName((String) row[i++]);
					attributeIngrediantDisplayDatas.add(displayData);

				}

			}
			if (attributeIngrediantDisplayDatas != null && attributeIngrediantDisplayDatas.size() > 0) {
				attributeIngrediantPacket.setAttributeIngredient(attributeIngrediantDisplayDatas);
			}
		}
		ItemAttributeIngrediantDisplayPacket displayPacket = new ItemAttributeIngrediantDisplayPacket();
		if (attributeIngrediantPacket != null && attributeIngrediantPacket.getAttributeIngredient() != null
				&& attributeIngrediantPacket.getAttributeIngredient().size() > 0) {
			displayPacket.setAttributeIngrediantPackets(attributeIngrediantPacket);
		}
		if (itemIngrediantPacket != null && itemIngrediantPacket.getItemIngredient() != null
				&& itemIngrediantPacket.getItemIngredient().size() > 0) {
			displayPacket.setItemIngrediantPackets(itemIngrediantPacket);
		}
		return displayPacket;

	}

	public List<Location> getLocationList(HttpServletRequest httpRequest, EntityManager em,
			InventoryItemToBOM inventoryItemBom) {
		List<Location> locationsList = new ArrayList<Location>();
		List<Location> locations = getRootLocations(httpRequest, em);
		for (Location location : locations) {
			Item itemIdFg = getItemByGlobalItemIdAndLocationId(em, location.getId(), inventoryItemBom.getItemFG());
			Item itemIdRm = getItemByGlobalItemIdAndLocationId(em, location.getId(), inventoryItemBom.getItemRM());
			UnitOfMeasurement uom = getUnitOfMeasurementByGlobalIdAndLocationId(em, location.getId(),
					inventoryItemBom.getSellableUomRM());

			if (itemIdFg != null && itemIdRm != null && uom != null) {
				InventoryItemBom bom = getInventoryItemBOMByGlobalIdAndLocationIdAndUOM(em, itemIdFg.getId(),
						itemIdRm.getId(), uom.getId());

				if (bom != null) {

					locationsList.add(location);
				}

			}
		}
		return locationsList;

	}

	public List<Location> getLocationListForInventoryAttributeBOM(HttpServletRequest httpRequest, EntityManager em,
			AttributeToBOMDisplayPacket attributeToBOMDisplayPacket) {
		List<Location> locationsList = new ArrayList<Location>();
		List<Location> locations = getRootLocations(httpRequest, em);
		for (Location location : locations) {
			ItemsAttribute itemIdFg = getInventoryAttributeBOMByGlobalItemIdAndLocationId(em, location.getId(),
					attributeToBOMDisplayPacket.getAttributeIdFg());
			Item itemIdRm = getItemByGlobalItemIdAndLocationId(em, location.getId(),
					attributeToBOMDisplayPacket.getItemIdRm());
			UnitOfMeasurement uom = getUnitOfMeasurementByGlobalIdAndLocationId(em, location.getId(),
					attributeToBOMDisplayPacket.getUomId());

			if (itemIdFg != null && itemIdRm != null && uom != null) {
				InventoryAttributeBOM bom = getInventoryAttributeBOMByGlobalIdAndLocationIdAndUOM(em, itemIdFg.getId(),
						itemIdRm.getId(), uom.getId());

				if (bom != null) {
					locationsList.add(location);
				}

			}
		}
		return locationsList;

	}

	ItemsAttribute getInventoryAttributeBOMByGlobalItemIdAndLocationId(EntityManager em, String locationId,
			String globalItemAttributeId) {
		try {
			String queryString = "select s from ItemsAttribute s where s.globalId =? and s.locationsId=? and s.status !='D'  ";
			TypedQuery<ItemsAttribute> query = em.createQuery(queryString, ItemsAttribute.class)
					.setParameter(1, globalItemAttributeId).setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {
			logger.severe("No Result found");
		}
		return null;
	}

	List<Location> getRootLocations(HttpServletRequest httpRequest, EntityManager em) {

		// get root locations where location type is not supplier

		// int supplierLocationType = getLocationTypeForSupplier();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> cl = builder.createQuery(Location.class);
		Root<Location> l = cl.from(Location.class);
		TypedQuery<Location> query = em
				.createQuery(cl.select(l).where(builder.lessThanOrEqualTo(l.get(Location_.locationsId), "0"),
						builder.notEqual(l.get(Location_.status), "D"), builder.notEqual(l.get(Location_.status), "I"),
						builder.notEqual(l.get(Location_.isThirdPartyLocation), 1)));
		return query.getResultList();

	}

	private void createOrderHeaderForRequestOrder(EntityManager em, RequestOrder requestOrder,
			HttpServletRequest httpRequest, RequestOrderPacket requestOrderPacket) throws Exception {

		BigDecimal tempSubTotal = new BigDecimal(0);
		BigDecimal tempTotal = new BigDecimal(0);

		String queryString = "select s from OrderSourceGroup s where s.id = ?";
		TypedQuery<OrderSourceGroup> query = em.createQuery(queryString, OrderSourceGroup.class).setParameter(1,
				requestOrder.getOrderSourceGroupId());
		OrderSourceGroup orderSourceGroup = query.getSingleResult();

		String queryOrderType = "select s from OrderType s where s.name =?";
		TypedQuery<OrderType> queryQOrderType = em.createQuery(queryOrderType, OrderType.class).setParameter(1,
				"Intra Transfer");
		OrderType orderType = queryQOrderType.getSingleResult();

		String queryStringStatus = "select s from OrderStatus s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'";
		TypedQuery<OrderStatus> queryStatus = em.createQuery(queryStringStatus, OrderStatus.class)
				.setParameter(1, "Order Placed").setParameter(2, requestOrder.getSupplierId())
				.setParameter(3, orderSourceGroup.getId());
		OrderStatus orderStatus = queryStatus.getSingleResult();

		String queryStringDetailsStatus = "select s from OrderDetailStatus s where s.name =? and s.locationsId=? and s.status !='D'";
		TypedQuery<OrderDetailStatus> queryDetailsStatus = em
				.createQuery(queryStringDetailsStatus, OrderDetailStatus.class).setParameter(1, "KOT Not Printed")
				.setParameter(2, requestOrder.getSupplierId());
		OrderDetailStatus orderDetailStatus = queryDetailsStatus.getSingleResult();

		String queryStringSource = "select s from OrderSource s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'";
		TypedQuery<OrderSource> querySource = em.createQuery(queryStringSource, OrderSource.class)
				.setParameter(1, "Internal").setParameter(2, requestOrder.getSupplierId())
				.setParameter(3, orderSourceGroup.getId());
		OrderSource orderSource = querySource.getSingleResult();

		List<OrderDetailItem> orderDetailItemsList = new ArrayList<OrderDetailItem>();

		for (RequestOrderDetailItems requestOrderDetailItems : requestOrder.getRequestOrderDetailItems()) {
			Item itemAdapter = null;
			try {
				String queryStringItems = "select s from Item s where s.id =?";
				TypedQuery<Item> queryItems = em.createQuery(queryStringItems, Item.class).setParameter(1,
						requestOrderDetailItems.getItemsId());
				Item itemsQ = queryItems.getSingleResult();
				queryStringItems = "select s from Item s where s.globalItemId =? and s.locationsId = ? and s.status!='D' ";
				queryItems = em.createQuery(queryStringItems, Item.class).setParameter(1, itemsQ.getGlobalItemId())
						.setParameter(2, requestOrder.getSupplierId());
				itemAdapter = queryItems.getSingleResult();
			} catch (Exception e) {
				// TODO: handle exception
				logger.severe("No Result Found");
			}

			if (itemAdapter != null) {
				OrderDetailItem detailItem = new OrderDetailItem();
				detailItem.setId(null);
				// BigDecimal amount = itemAdapter.getPriceSelling();
				BigDecimal amount = itemAdapter.getDistributionPrice();
				amount = amount.multiply(requestOrderDetailItems.getQuantity());
				tempSubTotal = tempSubTotal.add(amount);
				detailItem.setSubTotal(amount);
				// detailItem.setPriceSelling(itemAdapter.getPriceSelling());
				detailItem.setPriceSelling(itemAdapter.getDistributionPrice());
				detailItem.setPoItemRefrenceNumber(requestOrderDetailItems.getId());

				detailItem.setPriceTax1(new BigDecimal(0));
				detailItem.setTaxDisplayName1("");
				detailItem.setTaxName1("");
				detailItem.setTaxRate1(new BigDecimal(0));

				detailItem.setPriceTax2(new BigDecimal(0));
				detailItem.setTaxDisplayName2("");
				detailItem.setTaxName2("");
				detailItem.setTaxRate2(new BigDecimal(0));

				detailItem.setPriceTax3(new BigDecimal(0));
				detailItem.setTaxDisplayName3("");
				detailItem.setTaxName3("");
				detailItem.setTaxRate3(new BigDecimal(0));

				detailItem.setPriceTax4(new BigDecimal(0));
				detailItem.setTaxDisplayName4("");
				detailItem.setTaxName4("");
				detailItem.setTaxRate4(new BigDecimal(0));

				detailItem.setTotalTax(new BigDecimal(0));

				BigDecimal itemTotal = amount;
				detailItem.setTotal(itemTotal);
				detailItem.setRoundOffTotal(itemTotal);
				tempTotal = tempTotal.add(itemTotal);

				detailItem.setOrderDetailStatusId(orderDetailStatus.getId());
				detailItem.setOrderDetailStatusName(orderDetailStatus.getName());
				detailItem.setSentCourseId(itemAdapter.getCourseId());
				detailItem.setItemsQty(requestOrderDetailItems.getQuantity());
				detailItem.setPointOfServiceNum(1);
				detailItem.setItemsId(itemAdapter.getId());
				detailItem.setItemsShortName(itemAdapter.getShortName());
				detailItem.setDiscountId(null);
				detailItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				detailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				detailItem.setCreatedBy(requestOrder.getCreatedBy());
				detailItem.setUpdatedBy(requestOrder.getUpdatedBy());
				detailItem.setLocalTime(
						new TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getSupplierId(), em));
				detailItem.setAmountPaid(new BigDecimal(0));
				detailItem.setBalanceDue(new BigDecimal(0));
				detailItem.setDiscountReason(null);
				detailItem.setDiscountValue(0);
				detailItem.setRecallReason(null);
				detailItem.setParentCategoryId(null);
				detailItem.setRootCategoryId(null);
				detailItem.setOrderHeaderToSeatDetailId(BigInteger.ZERO);
				detailItem.setIsTabOrderItem(0);
				detailItem.setIsInventoryHandled(0);

				List<Printer> printerArray = new PrinterUtility().getKDSListForIntraTransferByItemId(em,
						itemAdapter.getId());
				String array = "";
				if (printerArray != null && printerArray.size() > 0) {
					for (Printer printer : printerArray) {
						if (array.length() != 0) {
							array = array + "," + printer.getId();
						} else {
							array = array + printer.getId();
						}

					}

				}
				detailItem.setDeviceToKDSIds(array);
				detailItem.setInventoryAccrual(0);
				List<OrderDetailAttribute> orderDetailAttributes = new ArrayList<OrderDetailAttribute>();
				detailItem.setOrderDetailAttributes(orderDetailAttributes);
				orderDetailItemsList.add(detailItem);
			}

		}

		OrderHeader header = new OrderHeader();
		header.setId(null);
		header.setOrderDetailItems(orderDetailItemsList);
		header.setOrderStatusId(orderStatus.getId());

		header.setOrderSourceGroupId(orderSourceGroup.getId());
		header.setOrderSourceGroupName(orderSourceGroup.getName());
		header.setOrderSourceId(orderSource.getId());

		header.setOrderTypeId(orderType.getId());
		header.setLocationsId(requestOrder.getSupplierId());
		header.setRequestedLocationId(requestOrder.getLocationId());
		header.setReservationsId(null);

		header.setPointOfServiceCount(1);

		header.setPriceTax1(new BigDecimal(0));
		header.setTaxName1("");
		header.setTaxDisplayName1("");
		header.setTaxRate1(new BigDecimal(0));

		header.setPriceTax2(new BigDecimal(0));
		header.setTaxName2("");
		header.setTaxDisplayName2("");
		header.setTaxRate2(new BigDecimal(0));

		header.setPriceTax3(new BigDecimal(0));
		header.setTaxName3("");
		header.setTaxDisplayName3("");
		header.setTaxRate3(new BigDecimal(0));

		header.setPriceTax4(new BigDecimal(0));
		header.setTaxName4("");
		header.setTaxDisplayName4("");
		header.setTaxRate4(new BigDecimal(0));

		header.setSubTotal(tempSubTotal);
		header.setTotal(tempTotal);
		header.setRoundOffTotal(tempTotal);
		header.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		header.setOpenTime(new TimezoneTime().getGMTTimeInMilis());
		header.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		header.setCreatedBy(requestOrder.getCreatedBy());
		header.setUpdatedBy(requestOrder.getUpdatedBy());
		header.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getSupplierId(), em));

		TimezoneTime timezoneTime = new TimezoneTime();
		String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(requestOrder.getSupplierId(), em);
		if (currentDateTime != null && currentDateTime.length > 0) {
			header.setDate(currentDateTime[0]);

		}
		header.setScheduleDateTime(timezoneTime.getDateFromTimeStamp(new Timestamp(new TimezoneTime().getGMTTimeInMilis())));

		header.setTotalTax(new BigDecimal(0));

		// creating batch at the time of paid in /out

		String currentBatch = null;
		try {
			PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
			currentBatch = batchManager.getCurrentBatchIdBySession(httpRequest, em, requestOrder.getLocationId(), true,
					requestOrderPacket, requestOrder.getUpdatedBy());
		} catch (IOException | InvalidSessionException e) {
			// TODO Auto-generated catch block
			logger.severe(httpRequest, "no active batch detail found for locationId: " + requestOrder.getSupplierId());
		}
		header.setNirvanaXpBatchNumber(currentBatch);
		header.setIpAddress("");
		header.setUsersId(null);
		header.setAmountPaid(new BigDecimal(0));
		header.setBalanceDue(tempTotal);
		header.setServerId(null);
		header.setCashierId(null);
		header.setVoidReasonId(null);
		header.setIsTabOrder(0);
		header.setIsOrderReopened(0);
		header.setShiftSlotId(0);
		header.setIsSeatWiseOrder(0);
		header.setPreassignedServerId(requestOrder.getCreatedBy());
		header.setDeliveryOptionId(null);
		if (header.getId() == null) {
			String id = new StoreForwardUtility().generateUUID();
			header.setId(id);
			header.setOrderNumber(new StoreForwardUtility().generateOrderNumberNew(requestOrder.getLocationId(), em,
					header.getNirvanaXpBatchNumber(), "order_header"));
			header.setMergeOrderId(header.getId());
		}

		em.persist(header);

		for (OrderDetailItem detailItem : header.getOrderDetailItems()) {
			detailItem.setOrderHeaderId(header.getId());
			em.persist(detailItem);
		}

		Location l = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				requestOrder.getSupplierId());

		header.setAddressShipping(l.getAddress());
		// adding order dequence
		PaymentBatchManager batchManager = PaymentBatchManager.getInstance();

		Location baseLocation = new CommonMethods().getBaseLocation(em);
		if (baseLocation.getIsOrderNumberSequencing() == 0) {
			String id = new StoreForwardUtility().generateUUID();
			header.setOrderNumber(new StoreForwardUtility().generateOrderNumberNew(requestOrder.getLocationId(), em,
					header.getNirvanaXpBatchNumber(), "order_header"));
			header.setId(id);
		} else {
			header.setOrderNumber(new StoreForwardUtility().generateOrderNumberNew(requestOrder.getLocationId(), em,
					header.getNirvanaXpBatchNumber(), "order_header"));
		}

		header.setTaxExemptId("No Tax");
		header.setPoRefrenceNumber(requestOrder.getId());

		if (requestOrderPacket != null) {
			header.setSessionKey(requestOrderPacket.getIdOfSessionUsedByPacket());
		}

		new OrderManagementServiceBean().insertIntoKDSToOrderDetailItemStatus(httpRequest, em, header);

		if (header != null && header.getOrderDetailItems() != null && header.getOrderDetailItems().size() > 0) {
			try {

				Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
						requestOrder.getLocationId());

				new PrinterUtility().insertIntoPrintQueueForInventory(httpRequest, em, header,
						"" + requestOrderPacket.getRequestOrder().getSupplierId(), location.getName());
			} catch (Exception e) {
				logger.severe(e);
			}

		}
		header = em.merge(header);

		insertIntoOrderStatusHistory(em, header);
		new InsertIntoHistory().insertOrderIntoHistory(httpRequest, header, em);
		updateQRCodeAndHistory(httpRequest, em, header, Integer.parseInt(requestOrderPacket.getMerchantId()),
				requestOrder.getSupplierId()).getOrderHeader();

		OrderPacket orderPacket = new OrderPacket();
		OrderServiceForPost orderServiceForPost = new OrderServiceForPost();
		OrderHeader orderHeaderForPush = orderServiceForPost.getOrderHeaderWithMinimunRequiredDetails(header);
		orderPacket.setOrderHeader(orderHeaderForPush);
		orderPacket.setClientId(requestOrderPacket.getClientId());
		orderPacket.setLocationId(requestOrder.getSupplierId() + "");
		orderPacket.setMerchantId(requestOrderPacket.getMerchantId());
		orderServiceForPost.sendPacketForBroadcast(httpRequest, orderPacket,
				POSNServiceOperations.OrderManagementService_addInventoryOrder.name(), true);

	}

	/*
	 * private void createOrderHeaderForRequestOrder(EntityManager em,
	 * RequestOrder requestOrder, HttpServletRequest httpRequest,
	 * RequestOrderPacket requestOrderPacket) throws Exception {
	 * 
	 * BigDecimal tempSubTotal = new BigDecimal(0); BigDecimal tempTotal = new
	 * BigDecimal(0);
	 * 
	 * String queryString = "select s from OrderSourceGroup s where s.id = ?";
	 * TypedQuery<OrderSourceGroup> query = em.createQuery(queryString,
	 * OrderSourceGroup.class).setParameter(1,
	 * requestOrder.getOrderSourceGroupId()); OrderSourceGroup orderSourceGroup
	 * = query.getSingleResult();
	 * 
	 * 
	 * List<SalesTax> salesTaxsForOrderLevel = new ArrayList<SalesTax>(); try {
	 * String queryStringTax =
	 * "select st from SalesTax st where st.isItemSpecific = 0 and st.id in (select ots.taxId from OrderSourceGroupToSalesTax ots where ots.sourceGroupId = ?)"
	 * ; TypedQuery<SalesTax> queryTax = em.createQuery(queryStringTax,
	 * SalesTax.class).setParameter(1, orderSourceGroup.getId());
	 * salesTaxsForOrderLevel = queryTax.getResultList();
	 * 
	 * } catch (Exception e) { logger.severe("No Result found"); }
	 * 
	 * String queryOrderType = "select s from OrderType s where s.name =?";
	 * TypedQuery<OrderType> queryQOrderType = em.createQuery(queryOrderType,
	 * OrderType.class).setParameter (1, "Intra Transfer"); OrderType orderType
	 * = queryQOrderType.getSingleResult();
	 * 
	 * String queryStringStatus =
	 * "select s from OrderStatus s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'"
	 * ; TypedQuery<OrderStatus> queryStatus = em.createQuery(queryStringStatus,
	 * OrderStatus.class).setParameter(1, "Order Placed").setParameter(2,
	 * requestOrder.getSupplierId()).setParameter(3, orderSourceGroup.getId());
	 * OrderStatus orderStatus = queryStatus.getSingleResult();
	 * 
	 * String queryStringDetailsStatus =
	 * "select s from OrderDetailStatus s where s.name =? and s.locationsId=? and s.status !='D'"
	 * ; TypedQuery<OrderDetailStatus> queryDetailsStatus =
	 * em.createQuery(queryStringDetailsStatus,
	 * OrderDetailStatus.class).setParameter(1,
	 * "KOT Not Printed").setParameter(2, requestOrder.getSupplierId());
	 * OrderDetailStatus orderDetailStatus =
	 * queryDetailsStatus.getSingleResult();
	 * 
	 * String queryStringSource =
	 * "select s from OrderSource s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'"
	 * ; TypedQuery<OrderSource> querySource = em.createQuery(queryStringSource,
	 * OrderSource.class).setParameter(1, "Internal").setParameter(2,
	 * requestOrder.getSupplierId()).setParameter(3, orderSourceGroup.getId());
	 * OrderSource orderSource = querySource.getSingleResult();
	 * 
	 * 
	 * List<OrderDetailItem> orderDetailItemsList = new
	 * ArrayList<OrderDetailItem>();
	 * 
	 * SalesTax orderTax1 = new SalesTax(); SalesTax orderTax2 = new SalesTax();
	 * SalesTax orderTax3 = new SalesTax(); SalesTax orderTax4 = new SalesTax();
	 * 
	 * for (RequestOrderDetailItems requestOrderDetailItems :
	 * requestOrder.getRequestOrderDetailItems()) { Item itemAdapter = null; try
	 * { String queryStringItems = "select s from Item s where s.id =?";
	 * TypedQuery<Item> queryItems = em.createQuery(queryStringItems,
	 * Item.class).setParameter(1, requestOrderDetailItems.getItemsId()); Item
	 * itemsQ = queryItems.getSingleResult(); queryStringItems =
	 * "select s from Item s where s.globalItemId =? and s.locationsId = ?";
	 * queryItems = em.createQuery(queryStringItems, Item.class).setParameter(1,
	 * itemsQ.getGlobalItemId()) .setParameter(2, requestOrder.getSupplierId());
	 * itemAdapter = queryItems.getSingleResult(); } catch (Exception e) { //
	 * TODO: handle exception logger.severe("No Result Found"); }
	 * 
	 * if(itemAdapter != null) { OrderDetailItem detailItem = new
	 * OrderDetailItem(); detailItem.setId(0); //BigDecimal amount =
	 * itemAdapter.getPriceSelling(); BigDecimal amount =
	 * itemAdapter.getDistributionPrice(); amount =
	 * amount.multiply(requestOrderDetailItems.getQuantity()); tempSubTotal =
	 * tempSubTotal.add(amount); detailItem.setSubTotal(amount);
	 * //detailItem.setPriceSelling(itemAdapter.getPriceSelling());
	 * detailItem.setPriceSelling(itemAdapter.getDistributionPrice());
	 * detailItem.setPoItemRefrenceNumber(requestOrderDetailItems.getId());
	 * BigDecimal itemLevelTax = new BigDecimal(0); if
	 * (itemAdapter.getSalesTax1() != 0) { SalesTax salesTax = (SalesTax) new
	 * CommonMethods().getObjectById("SalesTax", em,SalesTax.class,
	 * itemAdapter.getSalesTax1());
	 * 
	 * BigDecimal tax = amount.multiply(salesTax.getRate());
	 * detailItem.setPriceTax1(tax.setScale(2, RoundingMode.CEILING).divide(new
	 * BigDecimal(100), BigDecimal.ROUND_HALF_UP)); itemLevelTax =
	 * itemLevelTax.add(detailItem.getPriceTax1());
	 * detailItem.setTaxDisplayName1(salesTax.getDisplayName());
	 * detailItem.setTaxName1(salesTax.getTaxName());
	 * detailItem.setTaxRate1(salesTax.getRate()); }
	 * 
	 * if (itemAdapter.getSalesTax2() != 0) { SalesTax salesTax = (SalesTax) new
	 * CommonMethods().getObjectById("SalesTax", em,SalesTax.class,
	 * itemAdapter.getSalesTax2());
	 * 
	 * BigDecimal tax = amount.multiply(salesTax.getRate());
	 * detailItem.setPriceTax2(tax.setScale(2, RoundingMode.CEILING).divide(new
	 * BigDecimal(100), BigDecimal.ROUND_HALF_UP)); itemLevelTax =
	 * itemLevelTax.add(detailItem.getPriceTax2());
	 * detailItem.setTaxDisplayName2(salesTax.getDisplayName());
	 * detailItem.setTaxName2(salesTax.getTaxName());
	 * detailItem.setTaxRate2(salesTax.getRate()); }
	 * 
	 * if (itemAdapter.getSalesTax3() != 0) { SalesTax salesTax = (SalesTax) new
	 * CommonMethods().getObjectById("SalesTax", em,SalesTax.class,
	 * itemAdapter.getSalesTax3());
	 * 
	 * BigDecimal tax = amount.multiply(salesTax.getRate());
	 * detailItem.setPriceTax3(tax.setScale(2, RoundingMode.CEILING).divide(new
	 * BigDecimal(100), BigDecimal.ROUND_HALF_UP)); itemLevelTax =
	 * itemLevelTax.add(detailItem.getPriceTax3());
	 * detailItem.setTaxDisplayName3(salesTax.getDisplayName());
	 * detailItem.setTaxName3(salesTax.getTaxName());
	 * detailItem.setTaxRate3(salesTax.getRate()); }
	 * 
	 * if (itemAdapter.getSalesTax4() != 0) { SalesTax salesTax = (SalesTax) new
	 * CommonMethods().getObjectById("SalesTax", em,SalesTax.class,
	 * itemAdapter.getSalesTax4());
	 * 
	 * BigDecimal tax = amount.multiply(salesTax.getRate());
	 * detailItem.setPriceTax4(tax.setScale(2, RoundingMode.CEILING).divide(new
	 * BigDecimal(100), BigDecimal.ROUND_HALF_UP)); itemLevelTax =
	 * itemLevelTax.add(detailItem.getPriceTax4());
	 * detailItem.setTaxDisplayName4(salesTax.getDisplayName());
	 * detailItem.setTaxName4(salesTax.getTaxName());
	 * detailItem.setTaxRate4(salesTax.getRate()); }
	 * 
	 * BigDecimal orderLevelTax = new BigDecimal(0); for (SalesTax serviceTax :
	 * salesTaxsForOrderLevel) { orderLevelTax = new BigDecimal(0); BigDecimal
	 * tax = amount.multiply(serviceTax.getRate()); tax = tax.setScale(2,
	 * RoundingMode.CEILING).divide(new BigDecimal(100),
	 * BigDecimal.ROUND_HALF_UP); orderLevelTax = orderLevelTax.add(tax);
	 * 
	 * if (serviceTax.getTaxName().equals("Tax1")) {
	 * orderTax1.setPriceTax(orderLevelTax);
	 * orderTax1.setDisplayName(serviceTax.getDisplayName());
	 * orderTax1.setTaxName(serviceTax.getTaxName());
	 * orderTax1.setRate(serviceTax.getRate());
	 * 
	 * }
	 * 
	 * if (serviceTax.getTaxName().equals("Tax2")) {
	 * orderTax2.setPriceTax(orderLevelTax);
	 * orderTax2.setDisplayName(serviceTax.getDisplayName());
	 * orderTax2.setTaxName(serviceTax.getTaxName());
	 * orderTax2.setRate(serviceTax.getRate()); }
	 * 
	 * if (serviceTax.getTaxName().equals("Tax3")) {
	 * orderTax3.setPriceTax(orderLevelTax);
	 * orderTax3.setDisplayName(serviceTax.getDisplayName());
	 * orderTax3.setTaxName(serviceTax.getTaxName());
	 * orderTax3.setRate(serviceTax.getRate()); }
	 * 
	 * if (serviceTax.getTaxName().equals("Tax4")) {
	 * orderTax4.setPriceTax(orderLevelTax);
	 * orderTax4.setDisplayName(serviceTax.getDisplayName());
	 * orderTax4.setTaxName(serviceTax.getTaxName());
	 * orderTax4.setRate(serviceTax.getRate()); }
	 * 
	 * }
	 * 
	 * detailItem.setTotalTax(orderLevelTax.add(itemLevelTax));
	 * 
	 * BigDecimal itemTotal = amount.add(orderLevelTax).add(itemLevelTax);
	 * detailItem.setTotal(itemTotal); detailItem.setRoundOffTotal(itemTotal);
	 * tempTotal = tempTotal.add(itemTotal);
	 * 
	 * detailItem.setOrderDetailStatusId(orderDetailStatus.getId());
	 * detailItem.setOrderDetailStatusName(orderDetailStatus.getName());
	 * detailItem.setSentCourseId(itemAdapter.getCourseId());
	 * detailItem.setItemsQty(requestOrderDetailItems.getQuantity());
	 * detailItem.setPointOfServiceNum(1);
	 * detailItem.setItemsId(itemAdapter.getId());
	 * detailItem.setItemsShortName(itemAdapter.getShortName());
	 * detailItem.setDiscountId(0); detailItem.setCreated(new
	 * Date(new TimezoneTime().getGMTTimeInMilis())); detailItem.setUpdated(new
	 * Date(new TimezoneTime().getGMTTimeInMilis()));
	 * detailItem.setCreatedBy(requestOrder.getCreatedBy());
	 * detailItem.setUpdatedBy(requestOrder.getUpdatedBy());
	 * detailItem.setLocalTime(new
	 * TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getSupplierId(),
	 * em)); detailItem.setAmountPaid(new BigDecimal(0));
	 * detailItem.setBalanceDue(new BigDecimal(0));
	 * detailItem.setDiscountReason(0); detailItem.setDiscountValue(0);
	 * detailItem.setRecallReason(0); detailItem.setParentCategoryId(0);
	 * detailItem.setRootCategoryId(0); detailItem.setIsTabOrderItem(0);
	 * detailItem.setIsInventoryHandled(0); detailItem.setInventoryAccrual(0);
	 * List<OrderDetailAttribute> orderDetailAttributes = new
	 * ArrayList<OrderDetailAttribute>();
	 * detailItem.setOrderDetailAttributes(orderDetailAttributes);
	 * orderDetailItemsList.add(detailItem); }
	 * 
	 * 
	 * }
	 * 
	 * OrderHeader header = new OrderHeader(); header.setId(0);
	 * header.setOrderDetailItems(orderDetailItemsList);
	 * header.setOrderStatusId(orderStatus.getId());
	 * 
	 * header.setOrderSourceGroupId(orderSourceGroup.getId());
	 * header.setOrderSourceGroupName(orderSourceGroup.getName());
	 * header.setOrderSourceId(orderSource.getId());
	 * 
	 * header.setOrderTypeId(orderType.getId());
	 * header.setLocationsId(requestOrder.getSupplierId());
	 * header.setRequestedLocationId(requestOrder.getLocationId());
	 * header.setReservationsId(0);
	 * 
	 * 
	 * header.setPointOfServiceCount(1);
	 * 
	 * // tax cal if (orderTax1.getPriceTax().doubleValue() > 0) {
	 * header.setPriceTax1(orderTax1.getPriceTax());
	 * header.setTaxName1(orderTax1.getTaxName());
	 * header.setTaxDisplayName1(orderTax1.getDisplayName());
	 * header.setTaxRate1(orderTax1.getRate());
	 * 
	 * }
	 * 
	 * if (orderTax2.getPriceTax().doubleValue() > 0) {
	 * header.setPriceTax2(orderTax2.getPriceTax());
	 * header.setTaxName2(orderTax2.getTaxName());
	 * header.setTaxDisplayName2(orderTax2.getDisplayName());
	 * header.setTaxRate1(orderTax2.getRate());
	 * 
	 * }
	 * 
	 * if (orderTax3.getPriceTax().doubleValue() > 0) {
	 * header.setPriceTax3(orderTax3.getPriceTax());
	 * header.setTaxName3(orderTax3.getTaxName());
	 * header.setTaxDisplayName3(orderTax3.getDisplayName());
	 * header.setTaxRate3(orderTax3.getRate());
	 * 
	 * }
	 * 
	 * if (orderTax4.getPriceTax().doubleValue() > 0) {
	 * header.setPriceTax4(orderTax4.getPriceTax());
	 * header.setTaxName4(orderTax4.getTaxName());
	 * header.setTaxDisplayName4(orderTax4.getDisplayName());
	 * header.setTaxRate4(orderTax4.getRate());
	 * 
	 * }
	 * 
	 * header.setSubTotal(tempSubTotal); header.setTotal(tempTotal);
	 * header.setRoundOffTotal(tempTotal); header.setCreated(new
	 * Date(new TimezoneTime().getGMTTimeInMilis()));
	 * header.setOpenTime(new TimezoneTime().getGMTTimeInMilis()); header.setUpdated(new
	 * Date(new TimezoneTime().getGMTTimeInMilis()));
	 * header.setCreatedBy(requestOrder.getCreatedBy());
	 * header.setUpdatedBy(requestOrder.getUpdatedBy()); header.setLocalTime(new
	 * TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getSupplierId(),
	 * em));
	 * 
	 * TimezoneTime timezoneTime = new TimezoneTime(); String currentDateTime[]
	 * = timezoneTime.getCurrentTimeofLocation(requestOrder.getSupplierId(),
	 * em); if (currentDateTime != null && currentDateTime.length > 0) {
	 * header.setDate(currentDateTime[0]);
	 * 
	 * } header.setScheduleDateTime(timezoneTime.getDateFromTimeStamp(new
	 * Timestamp(new TimezoneTime().getGMTTimeInMilis())));
	 * 
	 * header.setTotalTax(header.getPriceTax1().add(header.getPriceTax2().add(
	 * header.getPriceTax3().add(header.getPriceTax3()))));
	 * 
	 * // creating batch at the time of paid in /out
	 * 
	 * int currentBatch = 0; try { PaymentBatchManager batchManager =
	 * PaymentBatchManager.getInstance(); currentBatch =
	 * batchManager.getCurrentBatchIdBySession(httpRequest, em,
	 * requestOrder.getSupplierId(), true,requestOrderPacket); } catch
	 * (IOException | InvalidSessionException e) { // TODO Auto-generated catch
	 * block logger.severe(httpRequest,
	 * "no active batch detail found for locationId: " +
	 * requestOrder.getSupplierId()); }
	 * header.setNirvanaXpBatchNumber(currentBatch); header.setIpAddress("");
	 * header.setUsersId(0); header.setAmountPaid(new BigDecimal(0));
	 * header.setBalanceDue(tempTotal); header.setServerId(0);
	 * header.setCashierId(0); header.setVoidReasonId(0);
	 * header.setIsTabOrder(0); header.setIsOrderReopened(0);
	 * header.setShiftSlotId(0); header.setIsSeatWiseOrder(0);
	 * header.setPreassignedServerId(0); header.setDeliveryOptionId(0);
	 * 
	 * em.persist(header);
	 * 
	 * for (OrderDetailItem detailItem : header.getOrderDetailItems()) {
	 * detailItem.setOrderHeaderId(header.getId()); em.persist(detailItem); }
	 * 
	 * Location l = (Location) new CommonMethods().getObjectById("Location",
	 * em,Location.class, requestOrder.getSupplierId());
	 * 
	 * 
	 * header.setAddressShipping(l.getAddress()); // adding order dequence
	 * PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
	 * NirvanaIndex nirvanaIndex =
	 * batchManager.getAndUpdateCountOfOrderNumber(l.getBusinessId(), em, true);
	 * Location baseLocation = new CommonMethods().getBaseLocation(em);
	 * 
	 * if (baseLocation.getIsOrderNumberSequencing() == 0) {
	 * header.setOrderNumber(nirvanaIndex.getOrderNumber()); } else {
	 * header.setOrderNumber(header.getId()); }
	 * 
	 * header.setPoRefrenceNumber(requestOrder.getId());
	 * 
	 * if (requestOrderPacket != null) {
	 * header.setSessionKey(requestOrderPacket.getIdOfSessionUsedByPacket()); }
	 * if (header != null && header.getOrderDetailItems() != null &&
	 * header.getOrderDetailItems().size() > 0) { try {
	 * 
	 * Location location = (Location) new
	 * CommonMethods().getObjectById("Location", em,Location.class,
	 * requestOrder.getLocationId());
	 * 
	 * new PrinterUtility().insertIntoPrintQueueForInventory(httpRequest, em,
	 * header, "" + requestOrderPacket.getRequestOrder().getSupplierId(),
	 * location.getName()); } catch (Exception e) { logger.severe(e); }
	 * 
	 * } header = em.merge(header);
	 * 
	 * updateQRCodeAndHistory(httpRequest, em, header,
	 * Integer.parseInt(requestOrderPacket.getMerchantId()),
	 * requestOrder.getSupplierId()).getOrderHeader();
	 * 
	 * OrderPacket orderPacket = new OrderPacket(); OrderServiceForPost
	 * orderServiceForPost = new OrderServiceForPost(); OrderHeader
	 * orderHeaderForPush =
	 * orderServiceForPost.getOrderHeaderWithMinimunRequiredDetails(header);
	 * orderPacket.setOrderHeader(orderHeaderForPush);
	 * orderPacket.setClientId(requestOrderPacket.getClientId());
	 * orderPacket.setLocationId(requestOrder.getSupplierId() +"");
	 * orderPacket.setMerchantId(requestOrderPacket.getMerchantId());
	 * orderServiceForPost.sendPacketForBroadcast(httpRequest, orderPacket,
	 * POSNServiceOperations.OrderManagementService_addInventoryOrder.name(),
	 * true);
	 * 
	 * }
	 */

	/**
	 * Update QR code and history.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param merchantId
	 *            the merchant id
	 * @param parentLocationId
	 *            the parent location id
	 * @param schemaName
	 *            the schema name
	 * @return the order header with user
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws WriterException
	 *             the writer exception
	 */
	private OrderHeaderWithUser updateQRCodeAndHistory(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader order, int merchantId, String parentLocationId) throws IOException, WriterException {
		// add QR code and verification code
		String basePath = ConfigFileReader.getQRCodeUploadPathFromFile();
		String basePath2 = ConfigFileReader.getQRCodeUploadPathFromFile2();
		String adminFeedbackURL = ConfigFileReader.getAdminFeedbackURL();
		boolean isQrcodeGenerated = false;

		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				order.getLocationsId());
		if (location != null) {
			EntityManager globalEM = null;
			try {
				globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

				POSNPartners partners = getPOSNPartnersByBusinessId(httpRequest, globalEM, location.getBusinessId());
				if (partners != null) {
					String fileName = "qrcode_" + order.getId();
					if (partners != null) {
						new CommonMethods().createFileWithAllPermission(basePath + basePath2 + merchantId);

						String pathSpecificToMerchantWithSlash = "" + merchantId + "/" + parentLocationId + "/";
						String folderPath = basePath + basePath2 + pathSpecificToMerchantWithSlash;
						File childDirectory = new CommonMethods().createFileWithAllPermission(folderPath);
						String path = folderPath + fileName;

						String codeText = adminFeedbackURL + "refno=" + partners.getReferenceNumber() + "&order_id="
								+ order.getId();

						isQrcodeGenerated = new CommonMethods().generateQrcode(codeText, path + ".png",
								childDirectory.getAbsolutePath());
						if (isQrcodeGenerated) {
							// save order qr code and images
							order.setQrcode(basePath2 + pathSpecificToMerchantWithSlash + fileName);
							order.setVerificationCode("" + order.getId());

							em.merge(order);
						}
					}
				}
			} catch (Exception e1) {
				logger.severe(httpRequest, "Could not find globaldatabaseConnection for printing qrcode ");
				throw e1;
			} finally {
				GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			}
		}

		return new OrderHeaderWithUser(order, null, false);

	}

	/**
	 * Gets the POSN partners by business id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param businessId
	 *            the business id
	 * @return the POSN partners by business id
	 */
	private POSNPartners getPOSNPartnersByBusinessId(HttpServletRequest httpRequest, EntityManager em, int businessId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
		Root<POSNPartners> r = criteria.from(POSNPartners.class);
		TypedQuery<POSNPartners> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(POSNPartners_.businessId), businessId),
						builder.equal(r.get(POSNPartners_.partnerName), "BusinessApp")));
		POSNPartners result = null;
		try {
			result = (POSNPartners) query.getSingleResult();
		} catch (Exception e) {

			logger.severe(httpRequest, "Could not find POSNPartners with businessId : " + businessId);
		}
		return result;
	}

	/**
	 * Insert into order status history.
	 *
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @return the order status history
	 */
	private OrderStatusHistory insertIntoOrderStatusHistory(EntityManager em, OrderHeader order) {

		OrderStatusHistory orderStatusHistory = new OrderStatusHistory();

		orderStatusHistory.setOrderHeaderId(order.getId());
		orderStatusHistory.setOrderStatusId(order.getOrderStatusId());
		orderStatusHistory.setCreated(order.getCreated());
		orderStatusHistory.setLocalTime(order.getLocalTime());
		orderStatusHistory.setCreatedBy(order.getCreatedBy());
		orderStatusHistory.setUpdatedBy(order.getUpdatedBy());
		orderStatusHistory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.persist(orderStatusHistory);
		return orderStatusHistory;

	}

	public GoodsReceiveNotes getGoodsReceiveNoteByNumber(EntityManager em, String grnNumber) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<GoodsReceiveNotes> criteria = builder.createQuery(GoodsReceiveNotes.class);
			Root<GoodsReceiveNotes> r = criteria.from(GoodsReceiveNotes.class);
			TypedQuery<GoodsReceiveNotes> query = em.createQuery(
					criteria.select(r).where(builder.equal(r.get(GoodsReceiveNotes_.grnNumber), grnNumber)));
			GoodsReceiveNotes GoodsReceiveNote = query.getSingleResult();

			return GoodsReceiveNote;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

}
