/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.synchistory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.types.entities.employee.BreakInBreakOut;
import com.nirvanaxp.types.entities.employee.BreakInBreakOutHistory;
import com.nirvanaxp.types.entities.employee.ClockInClockOut;
import com.nirvanaxp.types.entities.employee.ClockInClockOutHistory;
import com.nirvanaxp.types.entities.inventory.Inventory;
import com.nirvanaxp.types.entities.inventory.InventoryHistory;
import com.nirvanaxp.types.entities.inventory.RequestOrder;
import com.nirvanaxp.types.entities.inventory.RequestOrderDetailItems;
import com.nirvanaxp.types.entities.inventory.RequestOrderDetailItemsHistory;
import com.nirvanaxp.types.entities.inventory.RequestOrderHistory;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailAttributeHistory;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailItemsHistory;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderHeaderHistory;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.reservation.ReservationsHistory;
import com.nirvanaxp.types.entities.sms.SMSHistory;
import com.nirvanaxp.types.entities.sms.SMSTemplate;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entity.snssms.SmsConfig;

public class InsertIntoHistory
{
	// persist the data in orderHeader history
	public void insertOrderIntoHistory(HttpServletRequest httpRequest, OrderHeader order, EntityManager em)
	{
		// set the fields
		OrderHeaderHistory orderHeaderHistory = new OrderHeaderHistory();

		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, order.getOrderStatusId());

		// --------------change by uzma for order header id ------------
		orderHeaderHistory.setOrderHeaderId(order.getId());

		orderHeaderHistory.setIpAddress(order.getIpAddress());
		orderHeaderHistory.setReservationsId(order.getReservationsId());
		orderHeaderHistory.setUsersId(order.getUsersId());
		orderHeaderHistory.setOrderStatusId(order.getOrderStatusId());
		orderHeaderHistory.setOrderSourceId(order.getOrderSourceId());
		orderHeaderHistory.setLocationsId(order.getLocationsId());
		if (order.getAddressShipping() != null)
			orderHeaderHistory.setAddressShippingId(order.getAddressShipping().getId());
		if (order.getAddressBilling() != null)
			orderHeaderHistory.setAddressBillingId(order.getAddressBilling().getId());
		orderHeaderHistory.setPriceGratuity(order.getPriceGratuity());
		orderHeaderHistory.setPriceExtended(order.getPriceExtended());
		orderHeaderHistory.setPriceTax1(order.getPriceTax1());
		orderHeaderHistory.setPriceTax2(order.getPriceTax2());
		orderHeaderHistory.setPriceTax3(order.getPriceTax3());
		orderHeaderHistory.setPriceTax4(order.getPriceTax4());
		orderHeaderHistory.setPriceDiscount(order.getPriceDiscount());
		orderHeaderHistory.setServiceTax(order.getServiceTax());
		orderHeaderHistory.setSubTotal(order.getSubTotal());
		orderHeaderHistory.setGratuity(order.getGratuity());
		orderHeaderHistory.setTotal(order.getTotal());
		orderHeaderHistory.setAmountPaid(order.getAmountPaid());
		orderHeaderHistory.setBalanceDue(order.getBalanceDue());
		orderHeaderHistory.setDiscountsId(order.getDiscountsId());
		orderHeaderHistory.setDiscountsName(order.getDiscountsName());
		orderHeaderHistory.setDiscountsTypeId(order.getDiscountsTypeId());
		orderHeaderHistory.setDiscountsTypeName(order.getDiscountsTypeName());
		orderHeaderHistory.setDiscountsValue(order.getDiscountsValue());

		if (orderStatus != null)
		{
			orderHeaderHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(orderStatus.getLocationsId(), em));
		}

		orderHeaderHistory.setCreated(order.getCreated());
		orderHeaderHistory.setUpdated(order.getUpdated());

		orderHeaderHistory.setCreatedBy(order.getCreatedBy());
		orderHeaderHistory.setUpdatedBy(order.getUpdatedBy());
		orderHeaderHistory.setPointOfServiceCount(order.getPointOfServiceCount());
		orderHeaderHistory.setSessionId(order.getSessionKey());
		orderHeaderHistory.setFirstName(order.getFirstName());
		orderHeaderHistory.setLastName(order.getLastName());

		orderHeaderHistory.setServerId(order.getServerId());
		orderHeaderHistory.setCashierId(order.getCashierId());
		orderHeaderHistory.setPreassignedServerId(order.getPreassignedServerId());
		orderHeaderHistory.setPoRefrenceNumber(order.getPoRefrenceNumber());
		orderHeaderHistory.setMergedLocationsId(order.getMergedLocationsId());
		orderHeaderHistory.setOpenTime(order.getOpenTime());
		orderHeaderHistory.setCloseTime(order.getCloseTime());
		orderHeaderHistory.setVoidReasonId(order.getVoidReasonId());
		orderHeaderHistory.setDiscountDisplayName(order.getDiscountDisplayName());
		orderHeaderHistory.setTaxDisplayName1(order.getTaxDisplayName1());
		orderHeaderHistory.setTaxDisplayName2(order.getTaxDisplayName2());
		orderHeaderHistory.setTaxDisplayName3(order.getTaxDisplayName3());
		orderHeaderHistory.setTaxDisplayName4(order.getTaxDisplayName4());
		orderHeaderHistory.setTaxRate1(order.getTaxRate1());
		orderHeaderHistory.setTaxRate2(order.getTaxRate2());
		orderHeaderHistory.setTaxRate3(order.getTaxRate3());
		orderHeaderHistory.setTaxRate4(order.getTaxRate4());
		orderHeaderHistory.setTotalTax(order.getTotalTax());
		orderHeaderHistory.setTaxName1(order.getTaxName1());
		orderHeaderHistory.setTaxName2(order.getTaxName2());
		orderHeaderHistory.setTaxName3(order.getTaxName3());
		orderHeaderHistory.setTaxName4(order.getTaxName4());
		orderHeaderHistory.setRoundOffTotal(order.getRoundOffTotal());
		orderHeaderHistory.setIsTabOrder(order.getIsTabOrder());

		orderHeaderHistory.setServername(order.getServername());
		orderHeaderHistory.setCashierName(order.getCashierName());

		orderHeaderHistory.setScheduleDateTime(order.getScheduleDateTime());
		order.setVoidReasonName(orderHeaderHistory.getVoidReasonName());

		orderHeaderHistory.setIsGratuityApplied(order.getIsGratuityApplied());
		orderHeaderHistory.setIsOrderReopened(order.getIsOrderReopened());
		orderHeaderHistory.setTaxExemptId(order.getTaxExemptId());
		orderHeaderHistory.setNirvanaXpBatchNumber(order.getNirvanaXpBatchNumber());
		orderHeaderHistory.setSessionId(order.getSessionKey());
		orderHeaderHistory.setOrderNumber(order.getOrderNumber());
		orderHeaderHistory.setShiftSlotId(order.getShiftSlotId());
		if (order.getPriceDiscountItemLevel() != null)
		{
			orderHeaderHistory.setPriceDiscountItemLevel(order.getPriceDiscountItemLevel());
		}
		if (order.getCalculatedDiscountValue() != null)
		{
			orderHeaderHistory.setCalculatedDiscountValue(order.getCalculatedDiscountValue());
		}
		if (order.getIsSeatWiseOrder() != -1)
		{
			orderHeaderHistory.setIsSeatWiseOrder(order.getIsSeatWiseOrder());
		}

		if (order.getPaymentWaysId() != null)
		{
			orderHeaderHistory.setPaymentWaysId(order.getPaymentWaysId());
		}

		if (order.getTaxDisplayName() != null)
		{
			orderHeaderHistory.setTaxDisplayName(order.getTaxDisplayName());
		}

		if (order.getTaxNo() != null)
		{
			orderHeaderHistory.setTaxNo(order.getTaxNo());
		}

		if (order.getCompanyName() != null)
		{
			orderHeaderHistory.setCompanyName(order.getCompanyName());
		}

		if (order.getDeliveryCharges() != null)
		{
			orderHeaderHistory.setDeliveryCharges(order.getDeliveryCharges());
		}
		else
		{
			orderHeaderHistory.setDeliveryCharges(new BigDecimal(0.00));
		}

		orderHeaderHistory.setRequestedLocationId(order.getRequestedLocationId());
		orderHeaderHistory.setOrderTypeId(order.getOrderTypeId());

		orderHeaderHistory.setDriverId(order.getDriverId());

		if (order.getDeliveryOptionId() != null)
		{
			orderHeaderHistory.setDeliveryOptionId(order.getDeliveryOptionId());
		}
		else
		{
			orderHeaderHistory.setDeliveryOptionId(null);
		}
		if (order.getServiceCharges() != null)
		{
			orderHeaderHistory.setServiceCharges(order.getServiceCharges());
		}
		else
		{
			orderHeaderHistory.setServiceCharges(new BigDecimal(0.00));
		}

		if (order.getDeliveryTax() != null)
		{
			orderHeaderHistory.setDeliveryTax(order.getDeliveryTax());
		}
		else
		{
			orderHeaderHistory.setDeliveryTax(new BigDecimal(0.00));
		}
		orderHeaderHistory.setTransferredOrderId(order.getTransferredOrderId());
		orderHeaderHistory.setIsSeatWiseOrder(order.getIsSeatWiseOrder());
		orderHeaderHistory.setStartDate(order.getStartDate());
		orderHeaderHistory.setEndDate(order.getEndDate());
		orderHeaderHistory.setEventName(order.getEventName());
		
		// check if items are added or not
		if (order.getOrderDetailItems() != null)
		{

			// create a set for logs
			Set<OrderDetailItemsHistory> detailItemSet = new HashSet<OrderDetailItemsHistory>();

			// set the fields foe detail attributes
			for (OrderDetailItem orderDetailItem : order.getOrderDetailItems())
			{

				OrderDetailItemsHistory detailItemHistory = new OrderDetailItemsHistory();

				if (orderStatus != null)
				{
					detailItemHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(orderStatus.getLocationsId(), em));
				}

				detailItemHistory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				detailItemHistory.setOrderDetailItemsId(orderDetailItem.getId());
				// end
				detailItemHistory.setOrderHeaderId(order.getId());

				detailItemHistory.setSentCourseId(orderDetailItem.getSentCourseId());
				detailItemHistory.setPointOfServiceNum(orderDetailItem.getPointOfServiceNum());

				detailItemHistory.setItemsId(orderDetailItem.getItemsId());
				detailItemHistory.setItemsShortName(orderDetailItem.getItemsShortName());
				detailItemHistory.setItemsQty(orderDetailItem.getItemsQty());
				detailItemHistory.setDiscountId(orderDetailItem.getDiscountId());
				detailItemHistory.setOrderDetailStatusId(orderDetailItem.getOrderDetailStatusId());
				detailItemHistory.setPriceSelling(orderDetailItem.getPriceSelling());
				detailItemHistory.setPriceMsrp(orderDetailItem.getPriceMsrp());
				detailItemHistory.setPriceExtended(orderDetailItem.getPriceExtended());
				detailItemHistory.setPriceGratuity(orderDetailItem.getPriceGratuity());
				detailItemHistory.setPriceTax1(orderDetailItem.getPriceTax1());
				detailItemHistory.setPriceTax1(orderDetailItem.getPriceTax1());
				detailItemHistory.setPriceTax2(orderDetailItem.getPriceTax2());
				detailItemHistory.setPriceTax3(orderDetailItem.getPriceTax3());
				detailItemHistory.setPriceDiscount(orderDetailItem.getPriceDiscount());
				detailItemHistory.setServiceTax(orderDetailItem.getServiceTax());
				detailItemHistory.setSubTotal(orderDetailItem.getSubTotal());
				detailItemHistory.setTotal(orderDetailItem.getTotal());
				detailItemHistory.setGratuity(orderDetailItem.getGratuity());
				detailItemHistory.setAmountPaid(orderDetailItem.getAmountPaid());
				detailItemHistory.setBalanceDue(orderDetailItem.getBalanceDue());
				detailItemHistory.setCreatedBy(orderDetailItem.getCreatedBy());

				detailItemHistory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				detailItemHistory.setUpdatedBy(orderDetailItem.getUpdatedBy());

				detailItemHistory.setDiscountReason(orderDetailItem.getDiscountReason());
				detailItemHistory.setDiscountReasonName(orderDetailItem.getDiscountReasonName());
				detailItemHistory.setDiscountName(orderDetailItem.getDiscountName());
				detailItemHistory.setDiscountTypeId(orderDetailItem.getDiscountTypeId());
				detailItemHistory.setDiscountTypeName(orderDetailItem.getDiscountTypeName());
				detailItemHistory.setDiscountValue(orderDetailItem.getDiscountValue());

				detailItemHistory.setTaxDisplayName1(orderDetailItem.getTaxDisplayName1());
				detailItemHistory.setTaxDisplayName2(orderDetailItem.getTaxDisplayName2());
				detailItemHistory.setTaxDisplayName3(orderDetailItem.getTaxDisplayName3());
				detailItemHistory.setTaxDisplayName4(orderDetailItem.getTaxDisplayName4());
				detailItemHistory.setTaxName1(orderDetailItem.getTaxName1());
				detailItemHistory.setTaxName2(orderDetailItem.getTaxName2());
				detailItemHistory.setTaxName3(orderDetailItem.getTaxName3());
				detailItemHistory.setTaxName4(orderDetailItem.getTaxName4());
				detailItemHistory.setTotalTax(orderDetailItem.getTotalTax());
				detailItemHistory.setTaxRate1(orderDetailItem.getTaxRate1());
				detailItemHistory.setTaxRate2(orderDetailItem.getTaxRate2());
				detailItemHistory.setTaxRate3(orderDetailItem.getTaxRate3());
				detailItemHistory.setTaxRate4(orderDetailItem.getTaxRate4());
				detailItemHistory.setDiscountDisplayName(orderDetailItem.getDiscountDisplayName());
				detailItemHistory.setRoundOffTotal(orderDetailItem.getRoundOffTotal());

				detailItemHistory.setParentCategoryId(orderDetailItem.getParentCategoryId());
				detailItemHistory.setRootCategoryId(orderDetailItem.getRootCategoryId());

				detailItemHistory.setRecallReason(orderDetailItem.getRecallReason());
				detailItemHistory.setRecallReasonName(orderDetailItem.getRecallReasonName());
				detailItemHistory.setIsTabOrderItem(orderDetailItem.getIsTabOrderItem());
				detailItemHistory.setTaxExemptId(orderDetailItem.getTaxExemptId());
				detailItemHistory.setCalculatedDiscountValue(orderDetailItem.getCalculatedDiscountValue());
				detailItemHistory.setDiscountWaysId(orderDetailItem.getDiscountWaysId());
				detailItemHistory.setPlu(orderDetailItem.getPlu());

				detailItemHistory.setDiscountCode(orderDetailItem.getDiscountCode());
				detailItemHistory.setItemTransferredFrom(orderDetailItem.getItemTransferredFrom());
				detailItemHistory.setPoItemRefrenceNumber(orderDetailItem.getPoItemRefrenceNumber());
				detailItemHistory.setServiceCharges(orderDetailItem.getServiceCharges());
				detailItemHistory.setDeliveryCharges(orderDetailItem.getDeliveryCharges());
				detailItemHistory.setScheduleDateTime(orderDetailItem.getScheduleDateTime());
				detailItemHistory.setDriverId(orderDetailItem.getDriverId());
				if (orderDetailItem.getOrderHeaderToSeatDetailId() != null)
				{
					detailItemHistory.setOrderHeaderToSeatDetailId(orderDetailItem.getOrderHeaderToSeatDetailId());
				}
				else
				{
					detailItemHistory.setOrderHeaderToSeatDetailId(BigInteger.ZERO);
				}

				// change by uzma to check if attribute is not exist
				if (orderDetailItem.getOrderDetailAttributes() != null)
				{
					Set<OrderDetailAttributeHistory> detailAttributeSet = new HashSet<OrderDetailAttributeHistory>();
					for (OrderDetailAttribute attribute : orderDetailItem.getOrderDetailAttributes())
					{

						OrderDetailAttributeHistory detailAttributeHistory = new OrderDetailAttributeHistory();

						// CHANGE BY UZMA - for attribute used getId
						// funtion
						// 2)change getOrderDeatilItem.getDiscountId to
						// getId for order details items id
						detailAttributeHistory.setOrderDetailAttributeId(attribute.getId());

						detailAttributeHistory.setOrderDetailItemsId(orderDetailItem.getId());
						// detailAttributeHistory.setOrderHeaderId(attribute.getOrderHeaderId());

						// add by uzma for item attribute id
						detailAttributeHistory.setItemsAttributeId(attribute.getItemsAttributeId());
						detailAttributeHistory.setOrderDetailStatusId(attribute.getOrderDetailStatusId());
						detailAttributeHistory.setItemsAttributeName(attribute.getItemsAttributeName());
						detailAttributeHistory.setPriceSelling(attribute.getPriceMsrp());
						detailAttributeHistory.setPriceMsrp(attribute.getPriceMsrp());
						detailAttributeHistory.setPriceExtended(attribute.getPriceExtended());
						detailAttributeHistory.setPriceGratuity(attribute.getPriceGratuity());
						detailAttributeHistory.setPriceTax(attribute.getPriceTax());
						detailAttributeHistory.setPriceTax1(attribute.getPriceTax1());
						detailAttributeHistory.setPriceTax2(attribute.getPriceTax2());
						detailAttributeHistory.setPriceTax3(attribute.getPriceTax3());
						detailAttributeHistory.setPriceDiscount(attribute.getPriceDiscount());
						detailAttributeHistory.setServiceTax(attribute.getServiceTax());
						detailAttributeHistory.setSubTotal(attribute.getSubTotal());
						detailAttributeHistory.setGratuity(attribute.getGratuity());
						detailAttributeHistory.setTotal(attribute.getTotal());
						detailAttributeHistory.setAmountPaid(attribute.getAmountPaid());
						detailAttributeHistory.setBalanceDue(attribute.getBalanceDue());
						detailAttributeHistory.setCreatedBy(attribute.getCreatedBy());
						detailAttributeHistory.setUpdatedBy(attribute.getUpdatedBy());
						detailAttributeHistory.setPlu(attribute.getPlu());
						detailAttributeHistory.setRoundOffTotal(attribute.getRoundOffTotal());
						if (orderStatus != null)
						{
							detailAttributeHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(orderStatus.getLocationsId(), em));
						}

						detailAttributeHistory.setCreated(new TimezoneTime().getGMTTimeInMilis());
						detailAttributeHistory.setUpdated(new TimezoneTime().getGMTTimeInMilis());

						detailAttributeHistory.setItemsId(attribute.getItemsId());
						detailAttributeHistory.setItemQty(attribute.getItemQty());
						detailAttributeSet.add(detailAttributeHistory);

					}
					detailItemHistory.setOrderDetailAttributesHistories(detailAttributeSet);
				}

				detailItemSet.add(detailItemHistory);

			}
			orderHeaderHistory.setOrderDetailItemsHistories(detailItemSet);

		}

		if (orderStatus != null)
		{
			orderHeaderHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(orderStatus.getLocationsId(), em));
		}

		orderHeaderHistory.setCreated(order.getCreated());
		orderHeaderHistory.setUpdated(order.getUpdated());

		em.persist(orderHeaderHistory);

	}

	
		
	public void insertReservationIntoHistory(HttpServletRequest httpRequest, Reservation res, EntityManager em)
	{
		// set the fields
		ReservationsHistory reservationHistory = new ReservationsHistory();

		// --------------change by uzma for order header id ------------
		reservationHistory.setReservationsId(res.getId());

		if (res.getComment() != null)
			reservationHistory.setComment(res.getComment());
		if (res.getContactPreferenceId1() != null)
			reservationHistory.setContactPreference1(res.getContactPreferenceId1());
		if (res.getContactPreferenceId2() != null)
			reservationHistory.setContactPreference2(res.getContactPreferenceId2());
		if (res.getContactPreferenceId3() != null)
			reservationHistory.setContactPreference3(res.getContactPreferenceId3());

		reservationHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(res.getLocationId(), em));
		reservationHistory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		reservationHistory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		reservationHistory.setCreatedBy(res.getCreatedBy());
		reservationHistory.setDate(res.getDate());
		reservationHistory.setSessionKey(res.getSessionKey());

		if (res.getEmail() != null)
			reservationHistory.setEmail(res.getEmail());
		if (res.getFirstName() != null)
			reservationHistory.setFirstName(res.getFirstName());
		if (res.getLastName() != null)
			reservationHistory.setLastName(res.getLastName());

		reservationHistory.setLocationsId(res.getLocationId());
		reservationHistory.setPartySize(res.getPartySize());

		if (res.getPhoneNumber() != null)
			reservationHistory.setPhoneNumber(res.getPhoneNumber());

		if (res.getRequestType() != null)
			reservationHistory.setRequestTypeId(res.getRequestType().getId());

		reservationHistory.setReservationPlatform(res.getReservationPlatform());
		reservationHistory.setReservationSource(res.getReservationSource());
		reservationHistory.setReservationTypesId(res.getReservationsTypeId());

		if (res.getReservationsStatus() != null)
			reservationHistory.setReservationsStatusId(res.getReservationsStatus().getId());

		reservationHistory.setTime(res.getTime());

		reservationHistory.setUpdatedBy(res.getUpdatedBy());
		reservationHistory.setUsersId(res.getUsersId());

		reservationHistory.setPreAssignedLocationId(res.getPreAssignedLocationId());
		reservationHistory.setBusinessComment(res.getBusinessComment());

		em.persist(reservationHistory);

	}

	public InventoryHistory insertInventoryIntoHistory(HttpServletRequest httpRequest, Inventory inventory, EntityManager em)
	{
		InventoryHistory inventoryHistory = new InventoryHistory();
		inventoryHistory.setInventoryId(inventory.getId());
		inventoryHistory.setD86Threshold(inventory.getD86Threshold());
		inventoryHistory.setEconomicOrderQuantity(inventory.getEconomicOrderQuantity());
		inventoryHistory.setIsBelowThreashold(inventory.getIsBelowThreashold());
		inventoryHistory.setItemId(inventory.getItemId());
		inventoryHistory.setMinimumOrderQuantity(inventoryHistory.getMinimumOrderQuantity());
		inventoryHistory.setStatus(inventory.getStatus());
		inventoryHistory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity());
		inventoryHistory.setTotalUsedQuanity(inventory.getTotalUsedQuanity());
		inventoryHistory.setCreated(inventory.getCreated());
		inventoryHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(inventory.getLocationId(), em));
		inventoryHistory.setUpdated(inventory.getUpdated());
		inventoryHistory.setUpdatedBy(inventory.getUpdatedBy());
		inventoryHistory.setCreatedBy(inventory.getCreatedBy());
		inventoryHistory.setUsedQuantity(inventory.getUsedQuantity());
		inventoryHistory.setYieldQuantity(inventory.getYieldQuantity());
		inventoryHistory.setTotalReceiveQuantity(inventory.getTotalReceiveQuantity());
		inventoryHistory.setLocalTime(inventory.getLocalTime());
		inventoryHistory.setPurchasingRate(inventory.getPurchasingRate());
		inventoryHistory.setOrderDetailItemId(inventory.getOrderDetailItemId());
		inventoryHistory.setGrnNumber(inventory.getGrnNumber());
		
		em.persist(inventoryHistory);
		return inventoryHistory;

	}

	public InventoryHistory insertInventoryIntoHistoryWithoutTransaction(HttpServletRequest httpRequest, Inventory inventory, EntityManager em)
	{
		InventoryHistory inventoryHistory = new InventoryHistory();
		inventoryHistory.setInventoryId(inventory.getId());
		inventoryHistory.setD86Threshold(inventory.getD86Threshold());
		inventoryHistory.setEconomicOrderQuantity(inventory.getEconomicOrderQuantity());
		inventoryHistory.setIsBelowThreashold(inventory.getIsBelowThreashold());
		inventoryHistory.setItemId(inventory.getItemId());
		inventoryHistory.setMinimumOrderQuantity(inventoryHistory.getMinimumOrderQuantity());
		inventoryHistory.setStatus(inventory.getStatus());
		inventoryHistory.setTotalAvailableQuanity(inventory.getTotalAvailableQuanity());
		inventoryHistory.setTotalUsedQuanity(inventory.getTotalUsedQuanity());
		inventoryHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(inventory.getLocationId(), em));
		inventoryHistory.setCreated(inventory.getCreated());
		inventoryHistory.setUpdated(inventory.getUpdated());
		inventoryHistory.setUpdatedBy(inventory.getUpdatedBy());
		inventoryHistory.setCreatedBy(inventory.getCreatedBy());
		inventoryHistory.setUsedQuantity(inventory.getUsedQuantity());
		inventoryHistory.setInventoryStatusId(inventory.getStatusId());
		inventoryHistory.setYieldQuantity(inventory.getYieldQuantity());
		inventoryHistory.setTotalReceiveQuantity(inventory.getTotalReceiveQuantity());
		inventoryHistory.setLocalTime(inventory.getLocalTime());
		inventoryHistory.setPurchasingRate(inventory.getPurchasingRate());

		inventoryHistory.setOrderDetailItemId(inventory.getOrderDetailItemId());
		inventoryHistory.setGrnNumber(inventory.getGrnNumber());
		
		em.persist(inventoryHistory);
		return inventoryHistory;

	}

	public void insertRequestOrderIntoHistory(HttpServletRequest httpRequest, RequestOrder order, EntityManager em)
	{
		// set the fields
		RequestOrderHistory requestOrderHistory = new RequestOrderHistory();
		requestOrderHistory.setRequestOrderId(order.getId());
		requestOrderHistory.setLocationId(order.getLocationId());
		requestOrderHistory.setName(order.getName());
		requestOrderHistory.setPurchaseOrderId(order.getPurchaseOrderId());
		requestOrderHistory.setStatus(order.getStatus());
		requestOrderHistory.setSupplierId(order.getSupplierId());
		requestOrderHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(requestOrderHistory.getLocationId(), em));
		requestOrderHistory.setCreated(order.getCreated());
		requestOrderHistory.setUpdated(order.getUpdated());
		requestOrderHistory.setCreatedBy(order.getCreatedBy());
		requestOrderHistory.setUpdatedBy(order.getUpdatedBy());
		requestOrderHistory.setStatusId(order.getStatusId());
		requestOrderHistory.setTaxDisplayName1(order.getTaxDisplayName1());
		requestOrderHistory.setTaxDisplayName2(order.getTaxDisplayName2());
		requestOrderHistory.setTaxDisplayName3(order.getTaxDisplayName3());
		requestOrderHistory.setTaxDisplayName4(order.getTaxDisplayName4());
		requestOrderHistory.setTaxName1(order.getTaxName1());
		requestOrderHistory.setTaxName3(order.getTaxName3());
		requestOrderHistory.setTaxName4(order.getTaxName4());
		requestOrderHistory.setTaxRate1(order.getTaxRate1());
		requestOrderHistory.setTaxRate2(order.getTaxRate2());
		requestOrderHistory.setTaxRate3(order.getTaxRate3());
		requestOrderHistory.setTaxRate4(order.getTaxRate4());
		requestOrderHistory.setPriceTax1(order.getPriceTax1());
		requestOrderHistory.setPriceTax2(order.getPriceTax2());
		requestOrderHistory.setPriceTax3(order.getPriceTax3());
		requestOrderHistory.setLocalTime(requestOrderHistory.getLocalTime());
		requestOrderHistory.setPriceTax4(order.getPriceTax4());
		// check if items are added or not
		if (order.getRequestOrderDetailItems() != null)
		{

			// create a set for logs
			// Set<OrderDetailItemsHistory> detailItemSet = new
			// HashSet<OrderDetailItemsHistory>();

			// set the fields foe detail attributes
			for (RequestOrderDetailItems requestOrderDetailItem : order.getRequestOrderDetailItems())
			{
				RequestOrderDetailItemsHistory requestOrderDetailItemsHistory = new RequestOrderDetailItemsHistory();
				requestOrderDetailItemsHistory.setItemsId(requestOrderDetailItem.getItemsId());
				requestOrderDetailItemsHistory.setRequestTo(requestOrderDetailItem.getRequestTo());
				requestOrderDetailItemsHistory.setQuantity(requestOrderDetailItem.getQuantity());
				requestOrderDetailItemsHistory.setStatusId(requestOrderDetailItem.getStatusId());
				requestOrderDetailItemsHistory.setReceivedQuantity(requestOrderDetailItem.getReceivedQuantity());
				requestOrderDetailItemsHistory.setBalance(requestOrderDetailItem.getBalance());
				requestOrderDetailItemsHistory.setAllotmentQty(requestOrderDetailItem.getAllotmentQty());
				requestOrderDetailItemsHistory.setRequestId(requestOrderDetailItem.getRequestId());
				requestOrderDetailItemsHistory.setItemName(requestOrderDetailItem.getItemName());
				requestOrderDetailItemsHistory.setUomName(requestOrderDetailItem.getUomName());
				requestOrderDetailItemsHistory.setCreated(requestOrderDetailItem.getCreated());
				requestOrderDetailItemsHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(requestOrderHistory.getLocationId(), em));
				requestOrderDetailItemsHistory.setUpdated(requestOrderDetailItem.getUpdated());
				requestOrderDetailItemsHistory.setUpdatedBy(requestOrderDetailItem.getUpdatedBy());
				requestOrderDetailItemsHistory.setCreatedBy(requestOrderDetailItem.getCreatedBy());
				requestOrderDetailItemsHistory.setRequestOrderDetailItemId(requestOrderDetailItem.getId());
				requestOrderDetailItemsHistory.setTax(requestOrderDetailItem.getTax());
				requestOrderDetailItemsHistory.setTotal(requestOrderDetailItem.getTotal());
				requestOrderDetailItemsHistory.setPrice(requestOrderDetailItem.getPrice());
				requestOrderDetailItemsHistory.setUnitPrice(requestOrderDetailItem.getUnitPrice());
				requestOrderDetailItemsHistory.setUnitPurchasedPrice(requestOrderDetailItem.getUnitPurchasedPrice());
				requestOrderDetailItemsHistory.setUnitTaxRate(requestOrderDetailItem.getTax());
				requestOrderDetailItemsHistory.setUpdated(requestOrderDetailItem.getUpdated());
				requestOrderDetailItemsHistory.setCreatedBy(requestOrderDetailItem.getCreatedBy());
				requestOrderDetailItemsHistory.setUpdatedBy(requestOrderDetailItem.getUpdatedBy());
				requestOrderDetailItemsHistory.setStatus(requestOrderDetailItem.getStatus());
				requestOrderDetailItemsHistory.setInTransitQty(requestOrderDetailItem.getInTransitQty());
				if (order.getChallanNumber() != null && order.getChallanNumber().length() != 0)
				{
					requestOrderDetailItemsHistory.setChallanNumber(order.getChallanNumber());
				}
				requestOrderDetailItemsHistory.setTaxDisplayName1(requestOrderDetailItem.getTaxDisplayName1());
				requestOrderDetailItemsHistory.setTaxDisplayName2(requestOrderDetailItem.getTaxDisplayName2());
				requestOrderDetailItemsHistory.setTaxDisplayName3(requestOrderDetailItem.getTaxDisplayName3());
				requestOrderDetailItemsHistory.setTaxDisplayName4(requestOrderDetailItem.getTaxDisplayName4());
				requestOrderDetailItemsHistory.setTaxName1(requestOrderDetailItem.getTaxName1());
				requestOrderDetailItemsHistory.setTaxName2(requestOrderDetailItem.getTaxName2());
				requestOrderDetailItemsHistory.setTaxName3(requestOrderDetailItem.getTaxName3());
				requestOrderDetailItemsHistory.setTaxName4(requestOrderDetailItem.getTaxName4());
				requestOrderDetailItemsHistory.setTaxRate1(requestOrderDetailItem.getTaxRate1());
				requestOrderDetailItemsHistory.setTaxRate2(requestOrderDetailItem.getTaxRate2());
				requestOrderDetailItemsHistory.setTaxRate3(requestOrderDetailItem.getTaxRate3());
				requestOrderDetailItemsHistory.setTaxRate4(requestOrderDetailItem.getTaxRate4());
				requestOrderDetailItemsHistory.setPriceTax1(requestOrderDetailItem.getPriceTax1());
				requestOrderDetailItemsHistory.setPriceTax2(requestOrderDetailItem.getPriceTax2());
				requestOrderDetailItemsHistory.setPriceTax3(requestOrderDetailItem.getPriceTax3());
				requestOrderDetailItemsHistory.setPriceTax4(requestOrderDetailItem.getPriceTax4());
				requestOrderDetailItemsHistory.setYieldQuantity(requestOrderDetailItem.getYieldQuantity());
				requestOrderDetailItemsHistory.setLocalTime(requestOrderDetailItem.getLocalTime());
				requestOrderDetailItemsHistory.setTotalReceiveQuantity(requestOrderDetailItem.getTotalReceiveQuantity());
				
				requestOrderDetailItemsHistory.setPaymentMethodId(requestOrderDetailItem.getPaymentMethodId());
				requestOrderDetailItemsHistory.setSupplierId(requestOrderDetailItem.getSupplierId());
				requestOrderDetailItemsHistory.setCommission(requestOrderDetailItem.getCommission());
				requestOrderDetailItemsHistory.setCommissionRate(requestOrderDetailItem.getCommissionRate());
				
				em.persist(requestOrderDetailItemsHistory);
			}

			em.persist(requestOrderHistory);
		}

	}

	public void insertRequestOrderDetailItemsIntoHistory(HttpServletRequest httpRequest, RequestOrderDetailItems requestOrderDetailItem, EntityManager em)
	{
		// set the fields
		RequestOrderDetailItemsHistory requestOrderDetailItemsHistory = new RequestOrderDetailItemsHistory();

		requestOrderDetailItemsHistory.setItemsId(requestOrderDetailItem.getItemsId());
		requestOrderDetailItemsHistory.setItemsId(requestOrderDetailItem.getItemsId());
		requestOrderDetailItemsHistory.setRequestTo(requestOrderDetailItem.getRequestTo());
		requestOrderDetailItemsHistory.setQuantity(requestOrderDetailItem.getQuantity());
		requestOrderDetailItemsHistory.setStatusId(requestOrderDetailItem.getStatusId());
		requestOrderDetailItemsHistory.setReceivedQuantity(requestOrderDetailItem.getReceivedQuantity());
		requestOrderDetailItemsHistory.setBalance(requestOrderDetailItem.getBalance());
		requestOrderDetailItemsHistory.setAllotmentQty(requestOrderDetailItem.getAllotmentQty());
		requestOrderDetailItemsHistory.setRequestId(requestOrderDetailItem.getRequestId());
		requestOrderDetailItemsHistory.setItemName(requestOrderDetailItem.getItemName());
		requestOrderDetailItemsHistory.setUomName(requestOrderDetailItem.getUomName());

		OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class, requestOrderDetailItemsHistory.getStatusId());
		if (orderDetailStatus != null)
		{
			requestOrderDetailItemsHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(orderDetailStatus.getLocationsId(), em));
		}
		requestOrderDetailItemsHistory.setCreated(requestOrderDetailItem.getCreated());
		requestOrderDetailItemsHistory.setUpdated(requestOrderDetailItem.getUpdated());
		requestOrderDetailItemsHistory.setUpdatedBy(requestOrderDetailItem.getUpdatedBy());
		requestOrderDetailItemsHistory.setCreatedBy(requestOrderDetailItem.getCreatedBy());
		requestOrderDetailItemsHistory.setRequestOrderDetailItemId(requestOrderDetailItem.getId());
		requestOrderDetailItemsHistory.setTax(requestOrderDetailItem.getTax());
		requestOrderDetailItemsHistory.setTotal(requestOrderDetailItem.getTotal());
		requestOrderDetailItemsHistory.setPrice(requestOrderDetailItem.getPrice());
		requestOrderDetailItemsHistory.setUnitPrice(requestOrderDetailItem.getUnitPrice());
		requestOrderDetailItemsHistory.setUnitPurchasedPrice(requestOrderDetailItem.getUnitPurchasedPrice());
		requestOrderDetailItemsHistory.setUnitTaxRate(requestOrderDetailItem.getTax());
		requestOrderDetailItemsHistory.setStatus(requestOrderDetailItem.getStatus());
		requestOrderDetailItemsHistory.setInTransitQty(requestOrderDetailItem.getInTransitQty());
		requestOrderDetailItemsHistory.setTaxDisplayName1(requestOrderDetailItem.getTaxDisplayName1());
		requestOrderDetailItemsHistory.setTaxDisplayName2(requestOrderDetailItem.getTaxDisplayName2());
		requestOrderDetailItemsHistory.setTaxDisplayName3(requestOrderDetailItem.getTaxDisplayName3());
		requestOrderDetailItemsHistory.setTaxDisplayName4(requestOrderDetailItem.getTaxDisplayName4());
		requestOrderDetailItemsHistory.setTaxName1(requestOrderDetailItem.getTaxName1());
		requestOrderDetailItemsHistory.setTaxName2(requestOrderDetailItem.getTaxName2());
		requestOrderDetailItemsHistory.setTaxName3(requestOrderDetailItem.getTaxName3());
		requestOrderDetailItemsHistory.setTaxName4(requestOrderDetailItem.getTaxName4());
		requestOrderDetailItemsHistory.setTaxRate1(requestOrderDetailItem.getTaxRate1());
		requestOrderDetailItemsHistory.setTaxRate2(requestOrderDetailItem.getTaxRate2());
		requestOrderDetailItemsHistory.setTaxRate3(requestOrderDetailItem.getTaxRate3());
		requestOrderDetailItemsHistory.setTaxRate4(requestOrderDetailItem.getTaxRate4());
		requestOrderDetailItemsHistory.setPriceTax1(requestOrderDetailItem.getPriceTax1());
		requestOrderDetailItemsHistory.setPriceTax2(requestOrderDetailItem.getPriceTax2());
		requestOrderDetailItemsHistory.setPriceTax3(requestOrderDetailItem.getPriceTax3());
		requestOrderDetailItemsHistory.setPriceTax4(requestOrderDetailItem.getPriceTax4());
		requestOrderDetailItemsHistory.setYieldQuantity(requestOrderDetailItem.getYieldQuantity());
		requestOrderDetailItemsHistory.setTotalReceiveQuantity(requestOrderDetailItem.getTotalReceiveQuantity());

		requestOrderDetailItemsHistory.setPaymentMethodId(requestOrderDetailItem.getPaymentMethodId());
		requestOrderDetailItemsHistory.setSupplierId(requestOrderDetailItem.getSupplierId());
		requestOrderDetailItemsHistory.setCommission(requestOrderDetailItem.getCommission());
		requestOrderDetailItemsHistory.setCommissionRate(requestOrderDetailItem.getCommissionRate());
		
		
		em.persist(requestOrderDetailItemsHistory);

	}

	public void insertClockInClockOutHistory(HttpServletRequest httpRequest, ClockInClockOut from, EntityManager em)
	{
		// set the fields
		ClockInClockOutHistory to = new ClockInClockOutHistory();

		to.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		to.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		to.setCreatedBy(from.getCreatedBy());
		to.setUpdatedBy(from.getUpdatedBy());
		to.setStatus(from.getStatus());

		to.setUsersId(from.getUsersId());

		to.setClockInOperationId(from.getClockInOperationId());
		to.setClockIn(from.getClockIn());

		to.setClockOutOperationId(from.getClockOutOperationId());
		to.setClockOut(from.getClockOut());

		to.setSessionId(from.getSessionId());
		to.setLocationId(from.getLocationId());
		to.setLocalTime(from.getLocalTime());
		to.setJobRoleId(from.getJobRoleId());
		to.setClockInClockOutId(from.getId());
		to.setClockInClockOutCreated(from.getCreated());
		to.setClockInClockOutUpdated(from.getUpdated());
		to.setSourceName(from.getSourceName());

		em.persist(to);

	}

	public void insertBreakInBreakOutHistory(HttpServletRequest httpRequest, BreakInBreakOut from, EntityManager em)
	{
		// set the fields
		
		
		String queryString = "select l from ClockInClockOutHistory l "
				+ "where l.locationId=? and l.usersId=? order by l.id desc";

		TypedQuery<ClockInClockOutHistory> query = em.createQuery(queryString, ClockInClockOutHistory.class)
				.setParameter(1, from.getLocationId()).
				setParameter(2, from.getUsersId());
		List<ClockInClockOutHistory> clockInClockOutDB = query.getResultList();
		
		
		
		BreakInBreakOutHistory to = new BreakInBreakOutHistory();

		if(clockInClockOutDB != null && clockInClockOutDB.size() > 0)
		{
			
			to.setClockInClockOutHistoryId(clockInClockOutDB.get(0).getId());
			
		}
		
		to.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		to.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		to.setCreatedBy(from.getCreatedBy());
		to.setUpdatedBy(from.getUpdatedBy());
		to.setStatus(from.getStatus());

		to.setUsersId(from.getUsersId());

		to.setBreakInOperationId(from.getBreakInOperationId());
		to.setBreakIn(from.getBreakIn());

		to.setBreakOutOperationId(from.getBreakOutOperationId());
		to.setBreakOut(from.getBreakOut());

		to.setSessionId(from.getSessionId());
		to.setLocationId(from.getLocationId());
		to.setLocalTime(from.getLocalTime());
		to.setJobRoleId(from.getJobRoleId());
		to.setClockInClockOutId(from.getClockInClockOutId());
		to.setBreakInBreakOutCreated(from.getCreated());
		to.setBreakInBreakOutUpdated(from.getUpdated());
		to.setBreakInBreakOutId(from.getId());
		to.setSourceName(from.getSourceName());

		em.persist(to);

	}

	
	public void insertSMSIntoHistory( EntityManager em,
			User user,SMSTemplate snsSmsTemplate,String message,
			String phoneNumber,SmsConfig smsConfig,String result, String locationId)
	{
		 
        SMSHistory history = new SMSHistory();
        history.setStatus("A");
        history.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
        history.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
        history.setCreatedBy(user.getId());
        history.setUpdatedBy(user.getId());
        
        if(snsSmsTemplate != null)
        {
        	history.setTemplateId(snsSmsTemplate.getId());	
        }
        
        history.setSmsText(message);
        history.setPhone(phoneNumber);
        if(smsConfig != null)
        {
        	history.setSenderId(smsConfig.getSenderId());	
        }
        
        history.setResponceCode(result);
        history.setLocationId(locationId);
        history.setUserId(user.getId());
        em.persist(history);

	}

}