/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.helper.InventoryManagementHelper;
import com.nirvanaxp.services.jaxrs.packets.InventoryPostPacket;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.inventory.Inventory;
import com.nirvanaxp.types.entities.inventory.InventoryAttributeBOM;
import com.nirvanaxp.types.entities.inventory.InventoryItemBom;
import com.nirvanaxp.types.entities.inventory.InventoryItemDefault;
import com.nirvanaxp.types.entities.inventory.UnitConversion;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;

// TODO: Auto-generated Javadoc
/**
 * The Class ItemInventoryManagementHelper.
 */
public class ItemInventoryManagementHelper {

	/** The stock UOM. */
	int stockUOM;

	/** The sellable UOM. */
	int sellableUOM;

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(ItemInventoryManagementHelper.class.getName());

	/**
	 * Manage item inventory for oder detail item.
	 *
	 * @param httpServletRequest
	 *            the http servlet request
	 * @param orderDetailItem
	 *            the order detail item
	 * @param em
	 *            the em
	 * @param isRealTimeInventoryOn
	 *            the is real time inventory on
	 * @param updatedBy
	 *            the updated by
	 * @param inventoryPostPacket
	 *            the inventory post packet
	 * @param locationId
	 *            the location id
	 * @param isDeductibleForOnlineAndPostPayment
	 *            the is deductible for online and post payment
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void manageItemInventoryForOderDetailItem(HttpServletRequest httpServletRequest,
			OrderDetailItem orderDetailItem, EntityManager em, int isRealTimeInventoryOn, String updatedBy,
			InventoryPostPacket inventoryPostPacket, String locationId, boolean isDeductibleForOnlineAndPostPayment,
			boolean considerRemoveRecall) throws NirvanaXPException, Exception {
		if (orderDetailItem != null) {
			// get item for this order detail item

			Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class, orderDetailItem.getItemsId());
			if (item != null) {

				{
					if (orderDetailItem.getId() == null) {
						// addition hence quantity
						if (orderDetailItem.getIsInventoryHandled() == 0) {
							handleInventoryForNewItem(httpServletRequest, orderDetailItem, locationId, em,
									isRealTimeInventoryOn, updatedBy, true, inventoryPostPacket, false);
						}

					} else {
						// its an old item we must check its status
						// get the item status for this order detail item
						if (orderDetailItem.getOrderDetailStatusId() != null
								&& orderDetailItem.getOrderDetailStatusId() != 0) {
							OrderDetailItem prevOrderDetailItem = (OrderDetailItem) new CommonMethods().getObjectById(
									"OrderDetailItem", em, OrderDetailItem.class, orderDetailItem.getId());
							OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
									orderDetailItem.getOrderDetailStatusId());
							if (orderDetailStatus != null
									&& (orderDetailStatus.getName().equalsIgnoreCase("KOT Not Printed") || orderDetailStatus.getName().equalsIgnoreCase("Item saved") || orderDetailStatus.getName().equalsIgnoreCase("Sent to Kitchen") || orderDetailStatus.getName().equalsIgnoreCase("KOT Printed") )) {
								
							 
								if(orderDetailItem.getItemsQty()!=null && orderDetailItem.getPreviousOrderQuantity()!=null && orderDetailItem.getItemsQty().intValue()!=orderDetailItem.getPreviousOrderQuantity().intValue()){
									logger.severe( orderDetailItem.getItemsQty().intValue()+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+orderDetailItem.getPreviousOrderQuantity().intValue());
									orderDetailItem = getQty(httpServletRequest,orderDetailItem.getId(),orderDetailItem);
									BigDecimal newQty = orderDetailItem.getItemsQty().subtract(orderDetailItem.getPreviousOrderQuantity());
									if(newQty!=null){
										orderDetailItem.setItemsQty(newQty);
									}
								}
								
								
								handleInventoryForNewItem(httpServletRequest, orderDetailItem, locationId, em,
										isRealTimeInventoryOn, updatedBy, true, inventoryPostPacket, false);

							}else if (orderDetailStatus != null
									&& (orderDetailStatus.getName().equalsIgnoreCase("Item Removed")
											|| orderDetailStatus.getName().equalsIgnoreCase("Recall"))) {
								// remove or recall on item has come, hence we
								// must reduce the inventory
								if (prevOrderDetailItem != null && considerRemoveRecall) {
									handleInventoryForNewItem(httpServletRequest, orderDetailItem, locationId, em,
											isRealTimeInventoryOn, updatedBy, false, inventoryPostPacket, false);

								}

							} else {
								if (isDeductibleForOnlineAndPostPayment) {
									handleInventoryForNewItem(httpServletRequest, prevOrderDetailItem, locationId, em,
											isRealTimeInventoryOn, updatedBy, true, inventoryPostPacket, false);
								} else {
									handleInventoryForNewItem(httpServletRequest, prevOrderDetailItem, locationId, em,
											isRealTimeInventoryOn, updatedBy, false, inventoryPostPacket, false);
									handleInventoryForNewItem(httpServletRequest, orderDetailItem, locationId, em,
											isRealTimeInventoryOn, updatedBy, true, inventoryPostPacket, false);
								}

							}
						}
					}
				}
			}
		}
	}

	/**
	 * Handle inventory for new item.
	 *
	 * @param httpServletRequest
	 *            the http servlet request
	 * @param orderDetailItem
	 *            the order detail item
	 * @param locationId
	 *            the location id
	 * @param em
	 *            the em
	 * @param isRealTimeInventoryOn
	 *            the is real time inventory on
	 * @param updatedBy
	 *            the updated by
	 * @param isDeductInventory
	 *            the is deduct inventory
	 * @param inventoryPostPacket
	 *            the inventory post packet
	 * @param increaseInventory
	 *            the increase inventory
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	private void handleInventoryForNewItem(HttpServletRequest httpServletRequest, OrderDetailItem orderDetailItem,
			String locationId, EntityManager em, int isRealTimeInventoryOn, String updatedBy, boolean isDeductInventory,
			InventoryPostPacket inventoryPostPacket, boolean increaseInventory) throws NirvanaXPException {

		boolean isAllowedToUpdateInventory = true;
		List<Inventory> inventoryList = new ArrayList<Inventory>();
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
		if (orderDetailItem != null) {
			List<InventoryItemBom> inventoryItemBoms = inventoryManagementHelper
					.getInventoryItemBomForItemId(orderDetailItem.getItemsId(), em);
			if (inventoryItemBoms != null) {
				try {
					for (InventoryItemBom inventoryItemBom : inventoryItemBoms) {

						Inventory inventory = null;
						try {
							inventory = inventoryManagementHelper.getInventoryForItemId(em,
									inventoryItemBom.getItemIdRm(), locationId, false);
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
							// BigDecimal availableQty =
							// inventory.getTotalAvailableQuanity();
							BigDecimal itemHoldQuantity = orderDetailItem.getItemsQty();
							itemHoldQuantity = itemHoldQuantity.multiply(inventoryItemBom.getQuantity());

							if (!isDeductInventory) {
								itemHoldQuantity = itemHoldQuantity.negate();
							}

							// int compareValue =
							// availableQty.compareTo(itemHoldQuantity);
							// hardcoding this value to allow inventory to go
							// negative
							// task :- 25944
							int compareValue = 1;
							if (compareValue >= 0) {
								inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								inventory.setUpdatedBy(updatedBy);

								inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(itemHoldQuantity));

								if (increaseInventory) {
									inventory.setTotalAvailableQuanity(
											inventory.getTotalAvailableQuanity().add(itemHoldQuantity));

								} else {

									inventory.setTotalAvailableQuanity(
											inventory.getTotalAvailableQuanity().subtract(itemHoldQuantity));

								}

								inventory.setUsedQuantity(itemHoldQuantity);
								if (orderDetailItem.getOrderDetailStatusId() > 0) {
									inventory.setStatusId(orderDetailItem.getOrderDetailStatusId());
								}

								inventoryList.add(inventory);
							} else {
								// for making inventory deduct in all condition
								isAllowedToUpdateInventory = false;
							}

						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// todo shlok need
					// handle proper exception
					logger.severe(e);
				}
			}

			if (orderDetailItem.getOrderDetailAttributes() != null) {
				for (OrderDetailAttribute orderDetailAttribute : orderDetailItem.getOrderDetailAttributes()) {
					boolean isAllowed = handleInventoryForNewItemAtt(orderDetailAttribute, em, locationId, updatedBy,
							inventoryList, inventoryManagementHelper, orderDetailItem.getItemsQty(), isDeductInventory);
					if (isAllowedToUpdateInventory) {
						isAllowedToUpdateInventory = isAllowed;

					}
				}
			}
			if (isAllowedToUpdateInventory) {
				updateThresholdValueAndPushToClient(inventoryList, inventoryManagementHelper, em, inventoryPostPacket);
				if (inventoryList.size() > 0) {
					orderDetailItem.setInventory(inventoryList);

					/*
					 * for (Inventory inventory : inventoryList) {
					 * em.merge(inventory); new InsertIntoHistory().
					 * insertInventoryIntoHistoryWithoutTransaction(
					 * httpServletRequest, inventory, em); }
					 */
				}

			} else {

				throw new NirvanaXPException(
						new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_INVENTORY_OUT_OF_STOCK_EXCEPTION,
								MessageConstants.ERROR_MESSAGE_INVENTORY_OUT_OF_STOCK_DISPLAY_MESSAGE, null));

			}
		}
	}

	/**
	 * Update threshold value and push to client.
	 *
	 * @param inventoryList
	 *            the inventory list
	 * @param inventoryManagementHelper
	 *            the inventory management helper
	 * @param em
	 *            the em
	 * @param inventoryPostPacket
	 *            the inventory post packet
	 */
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
				if (threshold.doubleValue() > 0) {
					if (inventory.getTotalAvailableQuanity().compareTo(threshold) <= 0) {
						isBelowThreshold = 1;
					}
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

	/**
	 * Gets the inventory to update.
	 *
	 * @param inventory
	 *            the inventory
	 * @param inventoryList
	 *            the inventory list
	 * @return the inventory to update
	 */
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

	/**
	 * Handle inventory for new item att.
	 *
	 * @param orderDetailAttribute
	 *            the order detail attribute
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param updatedBy
	 *            the updated by
	 * @param inventoryList
	 *            the inventory list
	 * @param inventoryManagementHelper
	 *            the inventory management helper
	 * @param itemHoldQuantity
	 *            the item hold quantity
	 * @param isDeductInventory
	 *            the is deduct inventory
	 * @return true, if successful
	 */
	private boolean handleInventoryForNewItemAtt(OrderDetailAttribute orderDetailAttribute, EntityManager em,
			String locationId, String updatedBy, List<Inventory> inventoryList,
			InventoryManagementHelper inventoryManagementHelper, BigDecimal itemHoldQuantity,
			boolean isDeductInventory) {

		boolean isAllowedToUpdateInventory = true;

		List<InventoryAttributeBOM> inventoryAttItemBoms = inventoryManagementHelper
				.getInventoryBomForItemAtt(orderDetailAttribute.getItemsAttributeId(), em);
		if (inventoryAttItemBoms != null) {
			for (InventoryAttributeBOM inventoryItemBom : inventoryAttItemBoms) {
				Inventory inventory = null;
				try {
					inventory = inventoryManagementHelper.getInventoryForItemId(em, inventoryItemBom.getItemIdRm(),
							locationId, false);
				} catch (NoResultException nre) {
					// no inventory found
					logger.severe("Inventory not found for : " + inventoryItemBom.getItemIdRm());
				}

				if (inventory != null) {
					String itemNameForLog = "id: " + inventory.getId() + ", Name: " + inventory.getItemDisplayName();
					inventory = getInventoryToUpdate(inventory, inventoryList);
					if (inventory == null) {
						logger.severe("could not find inventory to update in inventory list", itemNameForLog,
								inventoryList.toString());
						continue;
					}

					BigDecimal inventoryQty = itemHoldQuantity.multiply(inventoryItemBom.getQuantity());
					if (!isDeductInventory) {
						inventoryQty = inventoryQty.negate();
					}
					if (orderDetailAttribute.getItemsAttributeName().startsWith("No ")) {
						inventoryQty = inventoryQty.negate();
					}
					// int compareValue = availableQty.compareTo(inventoryQty);
					// to make inventory to go negative as per discussion woth
					// kris annd danish #26557
					int compareValue = 1;
					if (compareValue >= 0) {
						inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						inventory.setUpdatedBy(updatedBy);
						inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(inventoryQty));
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(inventoryQty));
						inventoryList.add(inventory);
					} else {
						isAllowedToUpdateInventory = false;
					}
				}

			}
		}
		return isAllowedToUpdateInventory;

	}

	/**
	 * Handle inventory for advance order item att.
	 *
	 * @param orderDetailAttribute
	 *            the order detail attribute
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param updatedBy
	 *            the updated by
	 * @param inventoryList
	 *            the inventory list
	 * @param inventoryManagementHelper
	 *            the inventory management helper
	 * @param itemHoldQuantity
	 *            the item hold quantity
	 * @param isDeductInventory
	 *            the is deduct inventory
	 */
	private void handleInventoryForAdvanceOrderItemAtt(OrderDetailAttribute orderDetailAttribute, EntityManager em,
			String locationId, String updatedBy, List<Inventory> inventoryList,
			InventoryManagementHelper inventoryManagementHelper, BigDecimal itemHoldQuantity,
			boolean isDeductInventory) {

		List<InventoryAttributeBOM> inventoryAttItemBoms = inventoryManagementHelper
				.getInventoryBomForItemAtt(orderDetailAttribute.getItemsAttributeId(), em);
		if (inventoryAttItemBoms != null) {
			for (InventoryAttributeBOM inventoryItemBom : inventoryAttItemBoms) {

				Inventory inventory = null;
				try {
					inventory = inventoryManagementHelper.getInventoryForItemId(em, inventoryItemBom.getItemIdRm(),
							locationId, false);
				} catch (NoResultException nre) {
					// no inventory found
					logger.severe("Inventory not found for : " + inventoryItemBom.getItemIdRm());
				}

				if (inventory != null) {
					String itemNameForLog = "id: " + inventory.getId() + ", Name: " + inventory.getItemDisplayName();
					inventory = getInventoryToUpdate(inventory, inventoryList);

					// todo shlok need
					// why continue?
					if (inventory == null) {
						logger.severe("could not find inventory to update in inventory list", itemNameForLog,
								inventoryList.toString());
						continue;
					}
					BigDecimal inventoryQty = itemHoldQuantity.multiply(inventoryItemBom.getQuantity());
					if (!isDeductInventory) {
						inventoryQty = inventoryQty.negate();
					}
					if (orderDetailAttribute.getItemsAttributeName().startsWith("No ")) {
						inventoryQty = inventoryQty.negate();
					}
					inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventory.setUpdatedBy(updatedBy);
					inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(inventoryQty));
					inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(inventoryQty));
					inventoryList.add(inventory);
				}
			}
		}

	}

	/**
	 * Manage item inventory for advance order.
	 *
	 * @param httpServletRequest
	 *            the http servlet request
	 * @param orderDetailItem
	 *            the order detail item
	 * @param em
	 *            the em
	 * @param isRealTimeInventoryOn
	 *            the is real time inventory on
	 * @param updatedBy
	 *            the updated by
	 * @param inventoryPostPacket
	 *            the inventory post packet
	 * @param locationId
	 *            the location id
	 * @param isDeductInventory
	 *            the is deduct inventory
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	synchronized void manageItemInventoryForAdvanceOrder(HttpServletRequest httpServletRequest,
			OrderDetailItem orderDetailItem, EntityManager em, int isRealTimeInventoryOn, String updatedBy,
			InventoryPostPacket inventoryPostPacket, String locationId, boolean isDeductInventory)
			throws NirvanaXPException {
		if (orderDetailItem != null) {
			// get item for this order detail item
			Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class, orderDetailItem.getItemsId());
			if (item != null) {
				// its an old item we must check its status
				// get the item status for this order detail item
				if (orderDetailItem.getOrderDetailStatusId() != 0) {

					OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
							orderDetailItem.getOrderDetailStatusId());
					if (orderDetailStatus != null && (orderDetailStatus.getName().equalsIgnoreCase("Item Removed")
							|| orderDetailStatus.getName().equalsIgnoreCase("Recall"))) {
						// remove or recall on item has come, hence we
						// no need to perform inventory operation
					} else {
						handleInventoryForAdvanceOrderItem(httpServletRequest, orderDetailItem, locationId, em,
								isRealTimeInventoryOn, updatedBy, isDeductInventory, inventoryPostPacket);
					}
				}
			}
		}
	}

	/**
	 * Handle inventory for advance order item.
	 *
	 * @param httpServletRequest
	 *            the http servlet request
	 * @param orderDetailItem
	 *            the order detail item
	 * @param locationId
	 *            the location id
	 * @param em
	 *            the em
	 * @param isRealTimeInventoryOn
	 *            the is real time inventory on
	 * @param updatedBy
	 *            the updated by
	 * @param isDeductInventory
	 *            the is deduct inventory
	 * @param inventoryPostPacket
	 *            the inventory post packet
	 */
	private void handleInventoryForAdvanceOrderItem(HttpServletRequest httpServletRequest,
			OrderDetailItem orderDetailItem, String locationId, EntityManager em, int isRealTimeInventoryOn,
			String updatedBy, boolean isDeductInventory, InventoryPostPacket inventoryPostPacket) {

		List<Inventory> inventoryList = new ArrayList<Inventory>();
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
		List<InventoryItemBom> inventoryItemBoms = inventoryManagementHelper
				.getInventoryItemBomForItemId(orderDetailItem.getItemsId(), em);

		if (inventoryItemBoms != null) {
			for (InventoryItemBom inventoryItemBom : inventoryItemBoms) {

				Inventory inventory = null;
				try {
					inventory = inventoryManagementHelper.getInventoryForItemId(em, inventoryItemBom.getItemIdRm(),
							locationId, false);
				} catch (NoResultException nre) {
					// no inventory found
					logger.severe("Inventory not found for : " + inventoryItemBom.getItemIdRm());

				}

				if (inventory != null) {
					// this call here makes no sense, the list is empty right
					// now
					// this method call will only return null

					BigDecimal itemHoldQuantity = orderDetailItem.getItemsQty();
					itemHoldQuantity = itemHoldQuantity.multiply(inventoryItemBom.getQuantity());
					if (!isDeductInventory) {
						itemHoldQuantity = itemHoldQuantity.negate();
					}
					inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventory.setUpdatedBy(updatedBy);
					inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(itemHoldQuantity));
					inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(itemHoldQuantity));
					inventory.setUsedQuantity(itemHoldQuantity);
					inventoryList.add(inventory);
				}

			}
		}
		if (orderDetailItem.getOrderDetailAttributes() != null) {
			for (OrderDetailAttribute orderDetailAttribute : orderDetailItem.getOrderDetailAttributes()) {
				handleInventoryForAdvanceOrderItemAtt(orderDetailAttribute, em, locationId, updatedBy, inventoryList,
						inventoryManagementHelper, orderDetailItem.getItemsQty(), isDeductInventory);
			}
		}
		updateThresholdValueAndPushToClient(inventoryList, inventoryManagementHelper, em, inventoryPostPacket);
		if (inventoryList.size() > 0) {

			for (Inventory inventory : inventoryList) {
				em.merge(inventory);
				new InsertIntoHistory().insertInventoryIntoHistory(httpServletRequest, inventory, em);
			}
		}

	}

	public synchronized void manageItemInventoryForKDS(HttpServletRequest httpServletRequest,
			OrderDetailItem orderDetailItem, EntityManager em, String updatedBy,
			InventoryPostPacket inventoryPostPacket, String locationId, boolean isOrderBumped)
			throws NirvanaXPException, Exception {
		if (orderDetailItem != null) {
			// get item for this order detail item

			Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class, orderDetailItem.getItemsId());
			if (item != null) {
				handleInventoryForKDSItemIngredients(httpServletRequest, orderDetailItem, locationId, em, updatedBy,
						inventoryPostPacket, isOrderBumped);
				handleInventoryForKDSItem(httpServletRequest, orderDetailItem, locationId, em, updatedBy,
						inventoryPostPacket, isOrderBumped);

			}
		}
	}

	private void handleInventoryForKDSItemIngredients(HttpServletRequest httpServletRequest,
			OrderDetailItem orderDetailItem, String locationId, EntityManager em, String updatedBy,
			InventoryPostPacket inventoryPostPacket, boolean isOrderBumped) throws NirvanaXPException {

		List<Inventory> inventoryList = new ArrayList<Inventory>();
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
		List<InventoryItemBom> inventoryItemBoms = inventoryManagementHelper
				.getInventoryItemBomForItemId(orderDetailItem.getItemsId(), em);
		if (inventoryItemBoms != null) {
			try {
				for (InventoryItemBom inventoryItemBom : inventoryItemBoms) {

					Inventory inventory = null;
					try {
						inventory = inventoryManagementHelper.getInventoryForItemId(em, inventoryItemBom.getItemIdRm(),
								locationId, false);
					} catch (NoResultException nre) {
						// no inventory found
						logger.severe("Inventory not found for : " + inventoryItemBom.getItemIdRm());
					}
					if (inventory != null) {
						/*
						 * String itemNameForLog = "id: " + inventory.getId() +
						 * ", Name: " + inventory.getItemDisplayName();
						 * inventory = getInventoryToUpdate(inventory,
						 * inventoryList); if (inventory == null) { logger.
						 * severe("could not find inventory to update in inventory list"
						 * , itemNameForLog, inventoryList.toString());
						 * continue; }
						 */
						// BigDecimal availableQty =
						// inventory.getTotalAvailableQuanity();
						BigDecimal itemHoldQuantity = orderDetailItem.getItemsQty();
						itemHoldQuantity = itemHoldQuantity.multiply(inventoryItemBom.getQuantity());

						inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						inventory.setUpdatedBy(updatedBy);
						if (orderDetailItem.getOrderDetailStatusId() > 0) {
							inventory.setStatusId(orderDetailItem.getOrderDetailStatusId());
						}
						if (isOrderBumped) {
							inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(itemHoldQuantity));
							inventory.setTotalAvailableQuanity(
									inventory.getTotalAvailableQuanity().subtract(itemHoldQuantity));
							inventory.setUsedQuantity(itemHoldQuantity);
						} else {
							inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().subtract(itemHoldQuantity));
							inventory.setTotalAvailableQuanity(
									inventory.getTotalAvailableQuanity().add(itemHoldQuantity));
						}
						inventoryList.add(inventory);
					}

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// todo shlok need
				// handle proper exception
				logger.severe(e);
			}
		}
		if (orderDetailItem.getOrderDetailAttributes() != null) {
			for (OrderDetailAttribute orderDetailAttribute : orderDetailItem.getOrderDetailAttributes()) {
				handleInventoryForKDSNewItemIngredientAtt(orderDetailAttribute, em, locationId, updatedBy,
						inventoryList, inventoryManagementHelper, orderDetailItem.getItemsQty(), isOrderBumped);

			}
		}
		updateThresholdValueAndPushToClient(inventoryList, inventoryManagementHelper, em, inventoryPostPacket);
		if (inventoryList.size() > 0) {
			for (Inventory inventory : inventoryList) {
				inventory.setOrderDetailItemId(orderDetailItem.getId());
				inventory.setGrnNumber(null);
				em.merge(inventory);
				new InsertIntoHistory().insertInventoryIntoHistoryWithoutTransaction(httpServletRequest, inventory, em);
			}
		}

	}

	private void handleInventoryForKDSItem(HttpServletRequest httpServletRequest, OrderDetailItem orderDetailItem,
			String locationId, EntityManager em, String updatedBy, InventoryPostPacket inventoryPostPacket,
			boolean isOrderBumped) throws NirvanaXPException {

		List<Inventory> inventoryList = new ArrayList<Inventory>();
		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();

		try {
			Inventory inventory = null;
			try {
				inventory = inventoryManagementHelper.getInventoryForItemId(em, orderDetailItem.getItemsId(),
						locationId, false);
			} catch (NoResultException nre) {
				// no inventory found
				logger.severe("Inventory not found for : " + orderDetailItem.getItemsId());
			}

			if (inventory != null) {
				String itemNameForLog = "id: " + inventory.getId() + ", Name: " + inventory.getItemDisplayName();
				inventory = getInventoryToUpdate(inventory, inventoryList);
				if (inventory == null) {
					logger.severe("could not find inventory to update in inventory list", itemNameForLog,
							inventoryList.toString());
				}
				// BigDecimal availableQty =
				// inventory.getTotalAvailableQuanity();
				BigDecimal itemHoldQuantity = orderDetailItem.getItemsQty();

				Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class, inventory.getItemId());

				if (item.getStockUom() != item.getSellableUom()) {
					UnitConversion unitConversion = getUnitConversionByFromIdAndToId(em, item.getStockUom(),
							item.getSellableUom(), logger);

					if (unitConversion != null) {
						// BigDecimal coverted=new
						// BigDecimal(1).divide(unitConversion.getConversionRatio(),2,BigDecimal.ROUND_HALF_DOWN);
						// itemHoldQuantity = itemHoldQuantity.multiply(new
						// BigDecimal(1).divide(unitConversion.getConversionRatio(),2,BigDecimal.ROUND_HALF_DOWN)).setScale(2,
						// BigDecimal.ROUND_HALF_UP);

						// itemHoldQuantity = new
						// BigDecimal(itemHoldQuantity.doubleValue() * (1
						// /unitConversion.getConversionRatio().doubleValue()));
						itemHoldQuantity = new BigDecimal(
								itemHoldQuantity.doubleValue() * unitConversion.getConversionRatio().doubleValue());

					}
				}

				inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				inventory.setUpdatedBy(updatedBy);
				if (orderDetailItem.getOrderDetailStatusId() > 0) {
					inventory.setStatusId(orderDetailItem.getOrderDetailStatusId());
				}
				if (isOrderBumped) {
					inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(itemHoldQuantity));
				} else {
					inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(itemHoldQuantity));
				}
				inventoryList.add(inventory);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// todo shlok need
			// handle proper exception
			logger.severe(e);
		}

		if (orderDetailItem.getOrderDetailAttributes() != null) {
			for (OrderDetailAttribute orderDetailAttribute : orderDetailItem.getOrderDetailAttributes()) {
				handleInventoryForKDSNewItemAtt(orderDetailAttribute, em, locationId, updatedBy, inventoryList,
						inventoryManagementHelper, orderDetailItem.getItemsQty(), isOrderBumped);
			}
		}
		updateThresholdValueAndPushToClient(inventoryList, inventoryManagementHelper, em, inventoryPostPacket);
		if (inventoryList.size() > 0) {
			for (Inventory inventory : inventoryList) {
				inventory.setOrderDetailItemId(orderDetailItem.getId());
				inventory.setGrnNumber(null);
				em.merge(inventory);
				new InsertIntoHistory().insertInventoryIntoHistoryWithoutTransaction(httpServletRequest, inventory, em);
			}
		}

	}

	private void handleInventoryForKDSNewItemAtt(OrderDetailAttribute orderDetailAttribute, EntityManager em,
			String locationId, String updatedBy, List<Inventory> inventoryList,
			InventoryManagementHelper inventoryManagementHelper, BigDecimal itemHoldQuantity, boolean isOrderBumped) {

		List<InventoryAttributeBOM> inventoryAttItemBoms = inventoryManagementHelper
				.getInventoryBomForItemAtt(orderDetailAttribute.getItemsAttributeId(), em);
		if (inventoryAttItemBoms != null) {
			for (InventoryAttributeBOM inventoryItemBom : inventoryAttItemBoms) {
				Inventory inventory = null;
				try {
					inventory = inventoryManagementHelper.getInventoryForItemId(em, inventoryItemBom.getAttributeIdFg(),
							locationId, false);
				} catch (NoResultException nre) {
					// no inventory found
					logger.severe("Inventory not found for : " + inventoryItemBom.getItemIdRm());
				}

				if (inventory != null) {
					String itemNameForLog = "id: " + inventory.getId() + ", Name: " + inventory.getItemDisplayName();
					inventory = getInventoryToUpdate(inventory, inventoryList);
					if (inventory == null) {
						logger.severe("could not find inventory to update in inventory list", itemNameForLog,
								inventoryList.toString());
						continue;
					}

					BigDecimal inventoryQty = inventoryItemBom.getQuantity();
					if (orderDetailAttribute.getItemsAttributeName().startsWith("No ")) {
						inventoryQty = inventoryQty.negate();
					}
					inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventory.setUpdatedBy(updatedBy);
					if (isOrderBumped) {
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(inventoryQty));
					} else {
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(inventoryQty));

					}
					inventoryList.add(inventory);
				}

			}
		}
	}

	private void handleInventoryForKDSNewItemIngredientAtt(OrderDetailAttribute orderDetailAttribute, EntityManager em,
			String locationId, String updatedBy, List<Inventory> inventoryList,
			InventoryManagementHelper inventoryManagementHelper, BigDecimal itemHoldQuantity, boolean isOrderBumped) {

		List<InventoryAttributeBOM> inventoryAttItemBoms = inventoryManagementHelper
				.getInventoryBomForItemAtt(orderDetailAttribute.getItemsAttributeId(), em);
		if (inventoryAttItemBoms != null) {
			for (InventoryAttributeBOM inventoryItemBom : inventoryAttItemBoms) {
				Inventory inventory = null;
				try {
					inventory = inventoryManagementHelper.getInventoryForItemId(em, inventoryItemBom.getAttributeIdFg(),
							locationId, false);
				} catch (NoResultException nre) {
					// no inventory found
					logger.severe("Inventory not found for : " + inventoryItemBom.getItemIdRm());
				}

				if (inventory != null) {
					String itemNameForLog = "id: " + inventory.getId() + ", Name: " + inventory.getItemDisplayName();
					inventory = getInventoryToUpdate(inventory, inventoryList);
					if (inventory == null) {
						logger.severe("could not find inventory to update in inventory list", itemNameForLog,
								inventoryList.toString());
						continue;
					}

					BigDecimal inventoryQty = itemHoldQuantity.multiply(inventoryItemBom.getQuantity());
					if (orderDetailAttribute.getItemsAttributeName().startsWith("No ")) {
						inventoryQty = inventoryQty.negate();
					}
					inventory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					inventory.setUpdatedBy(updatedBy);
					if (isOrderBumped) {
						inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().add(inventoryQty));
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().subtract(inventoryQty));

					} else {
						inventory.setTotalUsedQuanity(inventory.getTotalUsedQuanity().subtract(inventoryQty));
						inventory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity().add(inventoryQty));
					}
					inventoryList.add(inventory);
				}

			}
		}
	}

	public UnitConversion getUnitConversionByFromIdAndToId(EntityManager em, String fromUOMId, String toUOMId,
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

	synchronized String getItemInventoryForOrder(HttpServletRequest httpServletRequest, OrderDetailItem orderDetailItem,
			EntityManager em, int isRealTimeInventoryOn, String updatedBy, InventoryPostPacket inventoryPostPacket,
			String locationId) throws NirvanaXPException {
		if (orderDetailItem != null) {
			// get item for this order detail item
			Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class, orderDetailItem.getItemsId());
			if (item != null) {
				// its an old item we must check its status
				// get the item status for this order detail item
				if (orderDetailItem.getOrderDetailStatusId() != 0) {

					OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
							orderDetailItem.getOrderDetailStatusId());
					if (orderDetailStatus != null && (orderDetailStatus.getName().equalsIgnoreCase("Item Removed")
							|| orderDetailStatus.getName().equalsIgnoreCase("Recall"))) {
						// remove or recall on item has come, hence we
						// no need to perform inventory operation
						return null;
					} else {
						return checkInventoryForOrderItem(httpServletRequest, orderDetailItem, locationId, em,
								isRealTimeInventoryOn, updatedBy, inventoryPostPacket);
					}
				}
			}
		}
		return null;
	}

	private String checkInventoryForOrderItem(HttpServletRequest httpServletRequest, OrderDetailItem orderDetailItem,
			String locationId, EntityManager em, int isRealTimeInventoryOn, String updatedBy,
			InventoryPostPacket inventoryPostPacket) {

		InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
		List<InventoryItemBom> inventoryItemBoms = inventoryManagementHelper
				.getInventoryItemBomForItemId(orderDetailItem.getItemsId(), em);

		if (inventoryItemBoms != null) {
			for (InventoryItemBom inventoryItemBom : inventoryItemBoms) {

				Inventory inventory = null;
				try {
					inventory = inventoryManagementHelper.getInventoryForItemId(em, inventoryItemBom.getItemIdRm(),
							locationId, false);
				} catch (NoResultException nre) {
					// no inventory found
					logger.severe("Inventory not found for : " + inventoryItemBom.getItemIdRm());

				}

				if (inventory != null && inventory.getIsBelowThreashold() == 1) {
					// this call here makes no sense, the list is empty right
					// now
					// this method call will only return null

					BigDecimal itemHoldQuantity = orderDetailItem.getItemsQty().setScale(0, RoundingMode.DOWN);
					;
					BigDecimal availableQty = inventory.getTotalAvailableQuanity()
							.divide(inventoryItemBom.getQuantity());
					availableQty = availableQty.setScale(0, RoundingMode.DOWN);

					if (itemHoldQuantity.subtract(availableQty).intValue() > 0) {

						int quantity = (itemHoldQuantity.subtract(availableQty)).intValue();
						String errorMessage = orderDetailItem.getItemsShortName() + "  available quantity in stock is "
								+ availableQty + ". Please remove " + quantity + " quantity from the cart to continue.";
						return errorMessage;

					} else {
						return null;
					}

				}

			}
		}
		/*
		 * if (orderDetailItem.getOrderDetailAttributes() != null) { for
		 * (OrderDetailAttribute orderDetailAttribute :
		 * orderDetailItem.getOrderDetailAttributes()) {
		 * handleInventoryForAdvanceOrderItemAtt(orderDetailAttribute, em,
		 * locationId, updatedBy, inventoryList, inventoryManagementHelper,
		 * orderDetailItem.getItemsQty(), isDeductInventory); } }
		 * updateThresholdValueAndPushToClient(inventoryList,
		 * inventoryManagementHelper, em, inventoryPostPacket); if
		 * (inventoryList.size() > 0) {
		 * 
		 * for (Inventory inventory : inventoryList) { em.merge(inventory); new
		 * InsertIntoHistory().insertInventoryIntoHistory(httpServletRequest,
		 * inventory, em); } }
		 */
		return null;
	}

	private OrderDetailItem getQty(HttpServletRequest httpRequest, String orderDetailItemsId,OrderDetailItem detailItem) throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			String queryString1 = "select items_qty from order_detail_items where id =?;";

			Object result1=null;
			try {
				result1 = (Object) em.createNativeQuery(queryString1).setParameter(1, orderDetailItemsId)
						.getSingleResult();
			} catch (Exception e) {
				logger.severe(e);
			}

			if (result1 != null) {
				BigDecimal previousOrderQuantity=  ((BigDecimal) result1);
				detailItem.setPreviousOrderQuantity(previousOrderQuantity);
				return detailItem;
			}
		 
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

		return null;
	}
}
