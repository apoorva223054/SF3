/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.relationalentity.helper.ItemRelationsHelper;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.types.entities.catalog.category.CategoryToDiscount;
import com.nirvanaxp.types.entities.catalog.category.CategoryToDiscount_;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.Item_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToDiscount;
import com.nirvanaxp.types.entities.catalog.items.ItemsToDiscount_;
import com.nirvanaxp.types.entities.checkmate.Calculate;
import com.nirvanaxp.types.entities.checkmate.CheckMateDiscounts;
import com.nirvanaxp.types.entities.checkmate.Submit;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.DiscountWays;
import com.nirvanaxp.types.entities.discounts.DiscountWays_;
import com.nirvanaxp.types.entities.discounts.DiscountsType;
import com.nirvanaxp.types.entities.discounts.DiscountsType_;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;
import com.nirvanaxp.types.entities.payment.PaymentMethodType_;
import com.nirvanaxp.types.entities.payment.PaymentMethod_;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType_;
import com.nirvanaxp.types.entities.payment.PaymentWay;
import com.nirvanaxp.types.entities.payment.PaymentWay_;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.payment.TransactionStatus_;
import com.nirvanaxp.types.entities.salestax.OrderSourceToSalesTax;
import com.nirvanaxp.types.entities.salestax.OrderSourceToSalesTax_;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax_;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderHeaderCalculation.
 */
public class OrderHeaderCalculation {

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(OrderHeaderCalculation.class.getName());

	/**
	 * Gets the item by id.
	 *
	 * @param em
	 *            the em
	 * @param itemId
	 *            the item id
	 * @return the item by id
	 */

	Item getItemById(EntityManager em, String itemId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
		Root<Item> r = criteria.from(Item.class);
		TypedQuery<Item> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Item_.id), itemId)));
		Item item = query.getSingleResult();

		ItemRelationsHelper itemRelationsHelper = new ItemRelationsHelper();
		itemRelationsHelper.setShouldEliminateDStatus(true);

		item.setItemsToItemsAttributes(itemRelationsHelper.getItemsToItemsAttribute(item.getId(), em));
		item.setItemsToDiscounts(itemRelationsHelper.getItemToDiscounts(item.getId(), em));
		item.setItemsToItemsAttributesAttributeTypes(
				itemRelationsHelper.getItemsToItemsAttributeType(item.getId(), em));
		item.setItemsToItemsChars(itemRelationsHelper.getItemsToItemsChar(item.getId(), em));
		item.setItemsToPrinters(itemRelationsHelper.getItemToPrinter(item.getId(), em));
		item.setCategoryItems(itemRelationsHelper.getCategoryItem(item.getId(), em));

		return item;

	}

	/**
	 * Gets the order source to sales tax by id and location id.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @param locationId
	 *            the location id
	 * @return the order source to sales tax by id and location id
	 */
	public List<OrderSourceToSalesTax> getOrderSourceToSalesTaxByIdAndLocationId(EntityManager em, String id,
			String locationId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceToSalesTax> criteria = builder.createQuery(OrderSourceToSalesTax.class);
			Root<OrderSourceToSalesTax> orderSourceToSalesTax = criteria.from(OrderSourceToSalesTax.class);
			TypedQuery<OrderSourceToSalesTax> query = em.createQuery(criteria.select(orderSourceToSalesTax).where(
					builder.equal(orderSourceToSalesTax.get(OrderSourceToSalesTax_.locationsId), locationId),
					builder.notEqual(orderSourceToSalesTax.get(OrderSourceToSalesTax_.status), "D"),
					builder.notEqual(orderSourceToSalesTax.get(OrderSourceToSalesTax_.status), "I"),
					builder.equal(orderSourceToSalesTax.get(OrderSourceToSalesTax_.sourceId), id)));
			return query.getResultList();
		} catch (Exception e) {

			logger.severe(e,
					"No Result found for locationId " + locationId + " sourceId " + id + "In OrderSourceToSalesTax");
		}
		return null;
	}

	/**
	 * Gets the sales tax by id and location id.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @param locationId
	 *            the location id
	 * @param isItemSpecific
	 *            the is item specific
	 * @return the sales tax by id and location id
	 */
	public SalesTax getSalesTaxByIdAndLocationId(EntityManager em, String id, String locationId, int isItemSpecific) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
			Root<SalesTax> salesTax = criteria.from(SalesTax.class);
			TypedQuery<SalesTax> query = em.createQuery(
					criteria.select(salesTax).where(builder.equal(salesTax.get(SalesTax_.locationsId), locationId),
							builder.equal(salesTax.get(SalesTax_.id), id),
							builder.equal(salesTax.get(SalesTax_.isItemSpecific), isItemSpecific),
							builder.notEqual(salesTax.get(SalesTax_.status), "D"),
							builder.notEqual(salesTax.get(SalesTax_.status), "I")));
			return query.getSingleResult();
		} catch (Exception e) {

			logger.severe(e, "No Result found for locationId " + locationId + " isItemSpecific " + isItemSpecific
					+ "In Sales Tax");
		}
		return null;
	}

	/**
	 * Gets the gratuity by name and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the gratuity by name and location id
	 */
	public SalesTax getGratuityByNameAndLocationId(EntityManager em, String locationId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
			Root<SalesTax> salesTax = criteria.from(SalesTax.class);
			TypedQuery<SalesTax> query = em.createQuery(
					criteria.select(salesTax).where(builder.equal(salesTax.get(SalesTax_.locationsId), locationId),
							builder.notEqual(salesTax.get(SalesTax_.status), "D"),
							builder.notEqual(salesTax.get(SalesTax_.status), "I"),
							builder.equal(salesTax.get(SalesTax_.taxName), "Gratuity")));
			return query.getSingleResult();
		} catch (Exception e) {

			logger.severe(e, "No Result found for locationId " + locationId + "In Sales Tax");
		}
		return null;
	}

	/**
	 * Gets the order header calculation.
	 *
	 * @param em
	 *            the em
	 * @param orderHeader
	 *            the order header
	 * @return the order header calculation
	 */

	public OrderHeader getOrderHeaderCalculation(EntityManager em, OrderHeader orderHeader, OrderPacket orderPacket) {
		// todo shlok need
		// modilarise method

		List<OrderDetailItem> orderDetailItemsList = orderHeader.getOrderDetailItems();

		BigDecimal allItemSubTotal = new BigDecimal(0);
		BigDecimal allItemPriceGratuity = new BigDecimal(0);
		BigDecimal allItemTax = new BigDecimal(0);
		BigDecimal itemPriceGratuity = new BigDecimal(0);
		// location info for order header
		Location locationDetails = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				orderHeader.getLocationsId());

		BigDecimal baseAmount = new BigDecimal(0);
		List<String> list = new ArrayList<String>();
		Discount selectedDiscount = null;
		if (orderHeader.getDiscountsId() != null ) {
			selectedDiscount = (Discount) new CommonMethods().getObjectById("Discount", em,Discount.class, orderHeader.getDiscountsId());

			list = getItemListForGroupDiscount(em, orderHeader, selectedDiscount);
			baseAmount = getsubtotalAmmountForCalculatePercentageAmmountOfDollerOff(em, orderHeader, list,
					selectedDiscount);
		}

		// BigDecimal percentageOfDiscount = new BigDecimal(0);
		// BigDecimal priceDiscount = new BigDecimal(0);
		// BigDecimal subTotalOfBalaceDue = new BigDecimal(0);

		if (selectedDiscount != null) {
			selectedDiscount.setCalculatedDiscountValue(selectedDiscount.getDiscountsValue());
		}

		/*
		 * if (selectedDiscount != null &&
		 * orderHeader.getAmountPaid().doubleValue() > 0.0) {
		 * 
		 * BigDecimal balanceDue = orderHeader.getBalanceDue();
		 * 
		 * BigDecimal totalTax = orderHeader.getTotalTax().add(
		 * orderHeader.getPriceGratuity()); BigDecimal overAllTax = new
		 * BigDecimal( (totalTax.doubleValue() * 100) /
		 * (baseAmount.doubleValue() - orderHeader
		 * .getPriceDiscount().doubleValue())); BigDecimal diveident = new
		 * BigDecimal( ((overAllTax.doubleValue() / 100) + 1)); BigDecimal
		 * taxOnDiscount = new BigDecimal(0); if
		 * (orderHeader.getPriceDiscount().doubleValue() > 0.00) { taxOnDiscount
		 * = new BigDecimal((orderHeader.getPriceDiscount() .doubleValue() *
		 * overAllTax.doubleValue()) / 100); }
		 * 
		 * subTotalOfBalaceDue = new BigDecimal( (balanceDue.doubleValue() +
		 * orderHeader.getPriceDiscount().doubleValue() + taxOnDiscount
		 * .doubleValue()) / diveident.doubleValue());
		 * 
		 * priceDiscount = getDiscountOnAmount(em,subTotalOfBalaceDue,
		 * selectedDiscount, subTotalOfBalaceDue);
		 * 
		 * if (selectedDiscount.getDiscountsTypeId() ==
		 * getDiscountTypeByLocationIdAndName(em, orderHeader.getLocationsId(),
		 * "Percentage Off").getId()) { BigDecimal subtotalAfterdiscount = new
		 * BigDecimal(baseAmount.doubleValue() - priceDiscount.doubleValue());
		 * 
		 * if (baseAmount.doubleValue() > 0.0) { percentageOfDiscount = new
		 * BigDecimal((subtotalAfterdiscount.doubleValue() * 100) /
		 * baseAmount.doubleValue()); }
		 * 
		 * orderHeader.setCalculatedDiscountValue(new
		 * BigDecimal(100).subtract(percentageOfDiscount));
		 * selectedDiscount.setCalculatedDiscountValue(new
		 * BigDecimal(100).subtract(percentageOfDiscount));
		 * 
		 * 
		 * } else if (selectedDiscount.getDiscountsTypeId() ==
		 * getDiscountTypeByLocationIdAndName(em, orderHeader.getLocationsId(),
		 * "Amount Off").getId()) { if (subTotalOfBalaceDue.doubleValue() <
		 * priceDiscount.doubleValue()) { percentageOfDiscount =
		 * subTotalOfBalaceDue; } else { percentageOfDiscount = priceDiscount; }
		 * selectedDiscount.setCalculatedDiscountValue(percentageOfDiscount);
		 * orderHeader.setCalculatedDiscountValue(percentageOfDiscount); }
		 * 
		 * logger.severe("---------------------------  percentageOfDiscount " +
		 * percentageOfDiscount); }
		 */
		SalesTax orderSalesTax1 = null;
		SalesTax orderSalesTax2 = null;
		SalesTax orderSalesTax3 = null;
		SalesTax orderSalesTax4 = null;

		List<OrderSourceToSalesTax> orderSourceToSalesTaxList = getOrderSourceToSalesTaxByIdAndLocationId(em,
				orderHeader.getOrderSourceId(), orderHeader.getLocationsId());
		SalesTax gratuity = getGratuityByNameAndLocationId(em, orderHeader.getLocationsId());
		List<SalesTax> orderSalesTaxList = getSalesTaxList(em, orderHeader, orderSourceToSalesTaxList, orderPacket);
		if (orderSalesTaxList != null) {
			for (SalesTax salesTax : orderSalesTaxList) {
				if (salesTax.getTaxName().equals("Tax1") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax1 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax2") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax2 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax3") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax3 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax4") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax4 = salesTax;
				}
			}
		}
		int isRoundOffRequired = locationDetails.getIsRoundOffRequired();

		BigDecimal orderSaleTax1 = new BigDecimal(0);
		BigDecimal orderSaleTax2 = new BigDecimal(0);
		BigDecimal orderSaleTax3 = new BigDecimal(0);
		BigDecimal orderSaleTax4 = new BigDecimal(0);

		BigDecimal sumItemSaleTax1 = new BigDecimal(0);
		BigDecimal sumItemSaleTax2 = new BigDecimal(0);
		BigDecimal sumItemSaleTax3 = new BigDecimal(0);
		BigDecimal sumItemSaleTax4 = new BigDecimal(0);
		SalesTax finalSalesTax1 = new SalesTax();
		SalesTax finalSalesTax2 = new SalesTax();
		SalesTax finalSalesTax3 = new SalesTax();
		SalesTax finalSalesTax4 = new SalesTax();
		BigDecimal priceGratuity = new BigDecimal(0);
		BigDecimal discountTotal = new BigDecimal(0);
		BigDecimal itemisedDiscount = new BigDecimal(0);
		int count = 0;
		double deliveryCharge = 0;
		if (orderHeader.getDeliveryCharges() != null) {
			deliveryCharge = orderHeader.getDeliveryCharges().doubleValue();
		}
		List<OrderDetailItem> detailItemsList = new ArrayList<OrderDetailItem>();
		for (OrderDetailItem orderDetailItem : orderDetailItemsList) {
			count++;
			OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
					orderDetailItem.getOrderDetailStatusId());
			if (orderDetailStatus != null && !(orderDetailStatus.getName().equalsIgnoreCase("Item Removed"))
					&& !(orderDetailStatus.getName().equalsIgnoreCase("Recall"))) {
				detailItemsList.add(orderDetailItem);
			}
		}
		int noOfItems = detailItemsList.size();
		double itemDeliveryCharge = 0;
		if (deliveryCharge > 0) {
			itemDeliveryCharge = deliveryCharge / noOfItems;
		}
		for (OrderDetailItem orderDetailItem : orderDetailItemsList) {
			count++;
			OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
					orderDetailItem.getOrderDetailStatusId());
			if (orderDetailStatus != null && !(orderDetailStatus.getName().equalsIgnoreCase("Item Removed"))
					&& !(orderDetailStatus.getName().equalsIgnoreCase("Recall"))) {
				BigDecimal itemSubTotal = new BigDecimal(orderDetailItem.getSubTotal().doubleValue());
				allItemSubTotal = allItemSubTotal.add(itemSubTotal);
				Item item = getItemById(em, orderDetailItem.getItemsId());

				SalesTax salesTax1 = null;
				SalesTax salesTax2 = null;
				SalesTax salesTax3 = null;
				SalesTax salesTax4 = null;

				orderDetailItem.setDeliveryCharges(new BigDecimal(itemDeliveryCharge));
				BigDecimal itemDiscount = new BigDecimal(0);

				if (orderDetailItem.getDiscountWaysId() != getDiscountWaysByName(em, "Item Level").getId()
						&& orderDetailItem.getDiscountWaysId() != getDiscountWaysByName(em, "Seat Level").getId()) {
					itemDiscount = getOrderLevelDiscountForItem(em, orderDetailItem, itemSubTotal, list,
							selectedDiscount, baseAmount);
					itemSubTotal = itemSubTotal.subtract(itemDiscount);
				}
				/*
				 * else {
				 * 
				 * itemisedDiscount =
				 * itemisedDiscount.add(orderDetailItem.getPriceDiscount());
				 * itemSubTotal =
				 * itemSubTotal.subtract(orderDetailItem.getPriceDiscount()); }
				 */

				discountTotal = discountTotal.add(itemDiscount);

				if (selectedDiscount != null) {
					DiscountsType discountType = (DiscountsType) new CommonMethods().getObjectById("DiscountsType", em,DiscountsType.class, selectedDiscount.getDiscountsTypeId());

					if (discountType != null) {
						if (count == orderHeader.getOrderDetailItems().size() && discountType.equals(
								getDiscountTypeByLocationIdAndName(em, locationDetails.getId(), "Amount Off"))) {
							if (orderHeader.getAmountPaid().doubleValue() > 0.0) {
								if (discountTotal.doubleValue() != selectedDiscount.getCalculatedDiscountValue()
										.doubleValue()) {
									BigDecimal discountDiffrence = new BigDecimal(0);

									// added condition if discount value greator
									// than subtotal then do not calculate
									// discount difference as it cause negative
									// calculations
									if (orderHeader.getSubTotal().doubleValue() >= selectedDiscount
											.getCalculatedDiscountValue().doubleValue()) {
										discountDiffrence = selectedDiscount.getCalculatedDiscountValue()
												.subtract(discountTotal);
									}

									discountTotal = discountDiffrence.add(discountTotal);
								}
							} else {
								if (discountTotal.doubleValue() != selectedDiscount.getDiscountsValue().doubleValue()) {
									BigDecimal discountDiffrence = new BigDecimal(0);

									// added condition if discount value greator
									// than subtotal then do not calculate
									// discount difference as it cause negative
									// calculations
									if (orderHeader.getSubTotal().doubleValue() >= selectedDiscount.getDiscountsValue()
											.doubleValue()) {
										discountDiffrence = selectedDiscount.getDiscountsValue()
												.subtract(discountTotal);
									}

									discountTotal = discountDiffrence.add(discountTotal);
								}
							}

						}
					}
				}

				if (itemDiscount.doubleValue() > 0
						&& selectedDiscount != null /* && !isGratuityCal */) {
					orderDetailItem.setPriceDiscount(itemDiscount);
					orderDetailItem.setDiscountId(selectedDiscount.getId());
					orderDetailItem.setDiscountCode(selectedDiscount.getCoupanCode());

					orderDetailItem.setDiscountValue(selectedDiscount.getDiscountsValue().intValue());
					orderDetailItem.setDiscountDisplayName(selectedDiscount.getDisplayName());
					orderDetailItem.setCalculatedDiscountValue(selectedDiscount.getCalculatedDiscountValue());
					orderDetailItem.setDiscountName(selectedDiscount.getName());
					orderDetailItem.setDiscountTypeId(selectedDiscount.getDiscountsTypeId());
					orderDetailItem.setDiscountWaysId(getDiscountWaysByName(em, "Order Level").getId());
					orderDetailItem.setDiscountTypeName(
							getDiscountTypeByLocationIdAndId(em, selectedDiscount.getDiscountsTypeId())
									.getDiscountsType());
				} else if (itemisedDiscount.doubleValue() <= 0.00) {
					orderDetailItem.setPriceDiscount(new BigDecimal(0));
					orderDetailItem.setDiscountValue(0);
					orderDetailItem.setDiscountId(null);
					orderDetailItem.setDiscountCode("");
					orderDetailItem.setCalculatedDiscountValue(new BigDecimal(0));
					orderDetailItem.setDiscountName("");
					orderDetailItem.setDiscountDisplayName("");
					orderDetailItem.setDiscountTypeId(null);
					orderDetailItem.setDiscountWaysId(0);
					orderDetailItem.setDiscountTypeName("");

				} else {
					orderDetailItem.setPriceDiscount(new BigDecimal(0));
					orderDetailItem.setDiscountValue(0);
					orderDetailItem.setCalculatedDiscountValue(new BigDecimal(0));
					orderDetailItem.setDiscountName("");
					orderDetailItem.setDiscountId(null);
					orderDetailItem.setDiscountCode("");
					orderDetailItem.setDiscountDisplayName("");
					orderDetailItem.setDiscountTypeId(null);
					orderDetailItem.setDiscountWaysId(0);
					orderDetailItem.setDiscountTypeName("");
				}

				List<SalesTax> salesTaxList = getSalesTaxForOrderDetailItem(em, item);

				BigDecimal itemSaleTax1 = new BigDecimal(0);
				BigDecimal itemSaleTax2 = new BigDecimal(0);
				BigDecimal itemSaleTax3 = new BigDecimal(0);
				BigDecimal itemSaleTax4 = new BigDecimal(0);
				for (SalesTax salesTax : salesTaxList) {
					if (salesTax.getTaxName().equals("Tax1") && salesTax.getIsItemSpecific() == 1) {
						salesTax1 = salesTax;
					} else if (salesTax.getTaxName().equals("Tax2") && salesTax.getIsItemSpecific() == 1) {
						salesTax2 = salesTax;
					} else if (salesTax.getTaxName().equals("Tax3") && salesTax.getIsItemSpecific() == 1) {
						salesTax3 = salesTax;
					} else if (salesTax.getTaxName().equals("Tax4") && salesTax.getIsItemSpecific() == 1) {
						salesTax4 = salesTax;
					}
				}
				if (gratuity != null && orderHeader.getIsGratuityApplied() == 1) {
					priceGratuity = itemSubTotal.multiply(gratuity.getRate().divide(new BigDecimal(100)));
				}
				if (orderSalesTax1 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (gratuity != null && orderHeader.getIsGratuityApplied() == 1
							&& orderSalesTax1.getTaxId().equals(gratuity.getId())) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					orderSaleTax1 = newItemSubTotal.multiply(orderSalesTax1.getRate().divide(new BigDecimal(100)));
				}
				if (orderSalesTax2 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (gratuity != null && orderHeader.getIsGratuityApplied() == 1
							&& orderSalesTax2.getTaxId().equals(gratuity.getId())) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					orderSaleTax2 = newItemSubTotal.multiply(orderSalesTax2.getRate().divide(new BigDecimal(100)));
				}

				if (orderSalesTax3 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (gratuity != null && orderHeader.getIsGratuityApplied() == 1
							&& orderSalesTax3.getTaxId().equals(gratuity.getId())) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					orderSaleTax3 = newItemSubTotal.multiply(orderSalesTax3.getRate().divide(new BigDecimal(100)));
				}

				if (orderSalesTax4 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (gratuity != null && orderHeader.getIsGratuityApplied() == 1
							&& orderSalesTax4.getTaxId() == gratuity.getId()) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					orderSaleTax4 = newItemSubTotal.multiply(orderSalesTax4.getRate().divide(new BigDecimal(100)));
				}
				if (salesTax1 != null || orderSalesTax1 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (salesTax1 != null && gratuity != null && orderHeader.getIsGratuityApplied() == 1
							&& salesTax1.getTaxId().equals(gratuity.getId())) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}

					if (salesTax1 != null) {
						itemSaleTax1 = newItemSubTotal.multiply(salesTax1.getRate().divide(new BigDecimal(100)));
						finalSalesTax1.setTaxName(salesTax1.getTaxName());
						finalSalesTax1.setDisplayName(salesTax1.getDisplayName());
						finalSalesTax1.setRate(salesTax1.getRate());

					} else {
						finalSalesTax1.setTaxName(orderSalesTax1.getTaxName());
						finalSalesTax1.setDisplayName(orderSalesTax1.getDisplayName());
						finalSalesTax1.setRate(orderSalesTax1.getRate());
					}
					itemSaleTax1 = itemSaleTax1.add(orderSaleTax1);
					sumItemSaleTax1 = sumItemSaleTax1.add(itemSaleTax1);
					orderDetailItem.setTaxName1(finalSalesTax1.getTaxName());
					orderDetailItem.setTaxDisplayName1(finalSalesTax1.getDisplayName());
					orderDetailItem.setTaxRate1(finalSalesTax1.getRate());
					orderDetailItem.setPriceTax1(itemSaleTax1);
				}
				if (salesTax2 != null || orderSalesTax2 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (salesTax2 != null && gratuity != null && orderHeader.getIsGratuityApplied() == 1
							&& salesTax2.getTaxId().equals(gratuity.getId())) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					if (salesTax2 != null) {
						itemSaleTax2 = newItemSubTotal.multiply(salesTax2.getRate().divide(new BigDecimal(100)));
						finalSalesTax2.setTaxName(salesTax2.getTaxName());
						finalSalesTax2.setDisplayName(salesTax2.getDisplayName());
						finalSalesTax2.setRate(salesTax2.getRate());
					} else {
						finalSalesTax2.setTaxName(orderSalesTax2.getTaxName());
						finalSalesTax2.setDisplayName(orderSalesTax2.getDisplayName());
						finalSalesTax2.setRate(orderSalesTax2.getRate());
					}
					itemSaleTax2 = itemSaleTax2.add(orderSaleTax2);
					sumItemSaleTax2 = sumItemSaleTax2.add(itemSaleTax2);
					orderDetailItem.setTaxName2(finalSalesTax2.getTaxName());
					orderDetailItem.setTaxDisplayName2(finalSalesTax2.getDisplayName());
					orderDetailItem.setTaxRate2(finalSalesTax2.getRate());
					orderDetailItem.setPriceTax2(itemSaleTax2);
				}
				if (salesTax3 != null || orderSalesTax3 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (salesTax3 != null && gratuity != null && orderHeader.getIsGratuityApplied() == 1
							&& salesTax3.getTaxId().equals(gratuity.getId())) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					if (salesTax3 != null) {
						itemSaleTax3 = newItemSubTotal.multiply(salesTax3.getRate().divide(new BigDecimal(100)));
						finalSalesTax3.setTaxName(salesTax3.getTaxName());
						finalSalesTax3.setDisplayName(salesTax3.getDisplayName());
						finalSalesTax3.setRate(salesTax3.getRate());
					} else {
						finalSalesTax3.setTaxName(orderSalesTax3.getTaxName());
						finalSalesTax3.setDisplayName(orderSalesTax3.getDisplayName());
						finalSalesTax3.setRate(orderSalesTax3.getRate());
					}
					itemSaleTax3 = itemSaleTax3.add(orderSaleTax3);
					sumItemSaleTax3 = sumItemSaleTax3.add(itemSaleTax3);
					orderDetailItem.setTaxName3(finalSalesTax3.getTaxName());
					orderDetailItem.setTaxDisplayName3(finalSalesTax3.getDisplayName());
					orderDetailItem.setTaxRate3(finalSalesTax3.getRate());
					orderDetailItem.setPriceTax3(itemSaleTax3);
				}

				if (salesTax4 != null || orderSalesTax4 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (salesTax4 != null && gratuity != null && orderHeader.getIsGratuityApplied() == 1
							&& salesTax4.getTaxId().equals(gratuity.getId())) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					if (salesTax4 != null) {
						itemSaleTax4 = newItemSubTotal.multiply(salesTax4.getRate().divide(new BigDecimal(100)));
						finalSalesTax4.setTaxName(salesTax4.getTaxName());
						finalSalesTax4.setDisplayName(salesTax4.getDisplayName());
						finalSalesTax4.setRate(salesTax4.getRate());

					} else {
						finalSalesTax4.setTaxName(orderSalesTax4.getTaxName());
						finalSalesTax4.setDisplayName(orderSalesTax4.getDisplayName());
						finalSalesTax4.setRate(orderSalesTax4.getRate());
					}
					itemSaleTax4 = itemSaleTax4.add(orderSaleTax4);
					sumItemSaleTax4 = sumItemSaleTax4.add(itemSaleTax4);
					orderDetailItem.setTaxName4(finalSalesTax4.getTaxName());
					orderDetailItem.setTaxDisplayName4(finalSalesTax4.getDisplayName());
					orderDetailItem.setTaxRate4(finalSalesTax4.getRate());
					orderDetailItem.setPriceTax4(itemSaleTax4);
				}
				BigDecimal itemTotalTax = itemSaleTax1.add(itemSaleTax2).add(itemSaleTax3).add(itemSaleTax4);

				orderDetailItem.setTotalTax(itemTotalTax);
				allItemTax = allItemTax.add(itemTotalTax);

				if (orderHeader.getIsGratuityApplied() == 1) {

					itemPriceGratuity = itemSubTotal.multiply(orderHeader.getGratuity().divide(new BigDecimal(100)));
					orderDetailItem.setPriceGratuity(itemPriceGratuity);
					allItemPriceGratuity = allItemPriceGratuity.add(itemPriceGratuity);

					orderDetailItem.setGratuity(orderHeader.getGratuity());

				}

				BigDecimal total = itemTotalTax.add(itemSubTotal).add(itemPriceGratuity);

				orderDetailItem.setTotal(total);
				if (isRoundOffRequired == 1)
					orderDetailItem.setRoundOffTotal(total.setScale(0, RoundingMode.DOWN));
				else
					orderDetailItem.setRoundOffTotal(total.setScale(2, total.ROUND_HALF_DOWN));
			}

		}

		orderHeader.setPriceDiscountItemLevel(itemisedDiscount);
		orderHeader.setPriceDiscount(discountTotal);

		if (selectedDiscount != null /* && !isGratuityCal */) {
			orderHeader.setDiscountsValue(selectedDiscount.getDiscountsValue());
			orderHeader.setCalculatedDiscountValue(selectedDiscount.getCalculatedDiscountValue());
			orderHeader.setDiscountsId(selectedDiscount.getId());
			orderHeader.setDiscountsName(selectedDiscount.getName());
			orderHeader.setDiscountDisplayName(selectedDiscount.getDisplayName());
			orderHeader.setDiscountsTypeId(selectedDiscount.getDiscountsTypeId());
			orderHeader.setDiscountsTypeName(
					getDiscountTypeByLocationIdAndId(em, selectedDiscount.getDiscountsTypeId()).getDiscountsType());

		} else {
			orderHeader.setDiscountsValue(new BigDecimal(0));
			orderHeader.setCalculatedDiscountValue(new BigDecimal(0));
			orderHeader.setDiscountsId(null);
			orderHeader.setDiscountsName("");
			orderHeader.setDiscountDisplayName("");
			orderHeader.setDiscountsTypeId(null);
			orderHeader.setDiscountsTypeName("");
		}

		orderHeader.setSubTotal(allItemSubTotal);
		orderHeader.setPriceGratuity(allItemPriceGratuity);

		if (finalSalesTax1.getTaxName() != null) {
			orderHeader.setPriceTax1(sumItemSaleTax1);
			orderHeader.setTaxName1(finalSalesTax1.getTaxName());
			orderHeader.setTaxDisplayName1(finalSalesTax1.getDisplayName());
			orderHeader.setTaxRate1(finalSalesTax1.getRate());
		}
		if (finalSalesTax2.getTaxName() != null) {
			orderHeader.setPriceTax2(sumItemSaleTax2);
			orderHeader.setTaxName2(finalSalesTax2.getTaxName());
			orderHeader.setTaxDisplayName2(finalSalesTax2.getDisplayName());
			orderHeader.setTaxRate2(finalSalesTax2.getRate());
		}

		if (finalSalesTax3.getTaxName() != null) {
			orderHeader.setPriceTax3(sumItemSaleTax3);
			orderHeader.setTaxName3(finalSalesTax3.getTaxName());
			orderHeader.setTaxDisplayName3(finalSalesTax3.getDisplayName());
			orderHeader.setTaxRate3(finalSalesTax3.getRate());
		}

		if (finalSalesTax4.getTaxName() != null) {
			orderHeader.setPriceTax4(sumItemSaleTax4);
			orderHeader.setTaxName4(finalSalesTax4.getTaxName());
			orderHeader.setTaxDisplayName4(finalSalesTax4.getDisplayName());
			orderHeader.setTaxRate4(finalSalesTax4.getRate());
		}

		BigDecimal orderTotalTax = sumItemSaleTax1.add(sumItemSaleTax2).add(sumItemSaleTax3).add(sumItemSaleTax4);
		// BigDecimal totalTax = orderTotalTax.add(allItemTax);
		orderHeader.setTotalTax(orderTotalTax);

		BigDecimal orderTotal = orderTotalTax.add(allItemPriceGratuity).add(allItemSubTotal).subtract(discountTotal);

		if (orderHeader.getDeliveryCharges() != null) {
			orderTotal = orderTotal.add(orderHeader.getDeliveryCharges());
		}

		orderHeader.setTotal(orderTotal);

		if (isRoundOffRequired == 1)
			orderHeader.setRoundOffTotal(orderTotal.setScale(0, RoundingMode.DOWN));
		else
			orderHeader.setRoundOffTotal(orderTotal.setScale(2, orderTotal.ROUND_HALF_DOWN));

		BigDecimal balanceDue = orderTotal.subtract(orderHeader.getAmountPaid());

		orderHeader.setBalanceDue(balanceDue.setScale(2, balanceDue.ROUND_HALF_DOWN));

		orderHeader = createObjectForOrderPaymentForDiscount(em, orderHeader, orderHeader, selectedDiscount,
				orderHeader.getPriceDiscount(), orderPacket);

		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		if (orderPacket.getIsDiscountRemove() == 1) {
			orderHeader.setOrderStatusId(getOrderStatuByNameSourseGroupIdAndLocationId(em, "Discount Removed",
					orderSource.getOrderSourceGroupId(), orderHeader.getLocationsId()).getId());
		} else if (orderHeader.getPriceDiscount().doubleValue() > 0.0) {

			if (orderHeader.getBalanceDue().doubleValue() <= 0) {
				// if balance due get zero after applying discount
				orderHeader.setOrderStatusId(getOrderStatuByNameSourseGroupIdAndLocationId(em, "Order Paid",
						orderSource.getOrderSourceGroupId(), orderHeader.getLocationsId()).getId());
				orderHeader.setBalanceDue(new BigDecimal(0));

			} else {
				// apply status as per discount operation selected
				orderHeader.setOrderStatusId(getOrderStatuByNameSourseGroupIdAndLocationId(em, "Discount Applied",
						orderSource.getOrderSourceGroupId(), orderHeader.getLocationsId()).getId());

			}
		}
		orderHeader.setOrderDetailItems(orderDetailItemsList);

		// #47168: when we do check Present from order screen then the discount
		// getting removed. iOS.
		// logger.severe(orderHeader.getCalculatedDiscountValue()+"first
		// level+++++++++++++++++++++++++++++++++++++++++++++++++++++"+orderHeader.getDiscountsValue());
		// orderHeader.setCalculatedDiscountValue(orderHeader.getDiscountsValue());

		orderHeader.setPaymentWaysId(
				getPaymentWaysByNameAndLocationId(em, orderHeader.getLocationsId(), "All in One").getId());

		return orderHeader;

	}

	private BigDecimal calulateDiscountOnItemPrice(EntityManager em, BigDecimal itemsellingPrice,
			Discount selectedDiscount, OrderDetailItem itemToApplyDiscount) {
		BigDecimal discount = new BigDecimal(0);
		try {

			if (selectedDiscount != null) {
				DiscountsType discountType = getDiscountTypeByLocationIdAndId(em,
						selectedDiscount.getDiscountsTypeId());
				if (discountType != null) {
					itemToApplyDiscount.setDiscountTypeName(discountType.getDiscountsType());
					if (discountType.getId() == (getDiscountTypeByLocationIdAndName(em,
							selectedDiscount.getLocationsId(), "Percentage Off")).getId()) {
						discount = new BigDecimal((itemsellingPrice.doubleValue()
								* selectedDiscount.getCalculatedDiscountValue().doubleValue()) / 100);

					}
					if (discountType.getId() == getDiscountTypeByLocationIdAndName(em,
							selectedDiscount.getLocationsId(), "Amount Off").getId()) {
						discount = selectedDiscount.getCalculatedDiscountValue();

					}
				}

			} else {
				discount = new BigDecimal(0);
			}
			if (selectedDiscount.getName() != null) {
				itemToApplyDiscount.setDiscountName(selectedDiscount.getName());
			}
			if (discount.doubleValue() >= itemToApplyDiscount.getSubTotal().doubleValue()) {
				return itemToApplyDiscount.getSubTotal();
			} else {
				return discount;
			}
		} catch (Exception e) {
			logger.severe(e);
		}
		return discount;
	}

	public OrderHeader createObjectForOrderPaymentForDiscount(EntityManager em, OrderHeader newOrderHeaderAdapter,
			OrderHeader oldOrderHeaderAdapter, Discount selectedDiscount, BigDecimal priceDiscountValue,
			OrderPacket orderPacket) {

		if (newOrderHeaderAdapter.getId() != null && newOrderHeaderAdapter.getId() == null) {
			try {

				PaymentMethod paymentMethod = new PaymentMethod();
				OrderPaymentDetail orderPaymentDetail;

				int ifOPDExist = -1;

				if (oldOrderHeaderAdapter.getOrderPaymentDetails() != null
						&& oldOrderHeaderAdapter.getOrderPaymentDetails().size() > 0) {
					List<OrderPaymentDetail> orderPaymentDetailList = new ArrayList<>(
							oldOrderHeaderAdapter.getOrderPaymentDetails());
					int size = oldOrderHeaderAdapter.getOrderPaymentDetails().size();
					for (int i = 0; i < size; i++) {
						if (orderPaymentDetailList.get(i).getPaymentTransactionType()
								.getId() == getPaymentTransactionTypeByLocationIdAndName(em,
										oldOrderHeaderAdapter.getLocationsId(), "discount")
												.getId() /*
															 * && orderPaymentDetailList
															 * .get(i).getSeatId
															 * ().equals(seatNo)
															 */) {
							ifOPDExist = i;
							break;
						}
					}
				}

				if (ifOPDExist != -1 && oldOrderHeaderAdapter.getOrderPaymentDetails() != null) {
					orderPaymentDetail = new ArrayList<>(oldOrderHeaderAdapter.getOrderPaymentDetails())
							.get(ifOPDExist);
				} else {
					orderPaymentDetail = new OrderPaymentDetail();
				}

				orderPaymentDetail.setSeatId("S0");
				orderPaymentDetail.setLocalTime(
						new TimezoneTime().getLocationSpecificTimeToAdd(oldOrderHeaderAdapter.getLocationsId(), em));
				orderPaymentDetail.setNirvanaXpBatchNumber(oldOrderHeaderAdapter.getLocationsId()+"-"+newOrderHeaderAdapter.getNirvanaXpBatchNumber());
				orderPaymentDetail.setPaymentTransactionType(getPaymentTransactionTypeByLocationIdAndName(em,
						oldOrderHeaderAdapter.getLocationsId(), "discount"));

				paymentMethod = getPaymentMethodByLocationIdAndPaymentMethodTypeId(em,
						oldOrderHeaderAdapter.getLocationsId(),
						getPaymentMethodTypeByLocationIdAndName(em, oldOrderHeaderAdapter.getLocationsId(), "discount")
								.getId());

				orderPaymentDetail.setTransactionStatus(getTransactionStatusByName(em, "discount"));
				orderPaymentDetail.setPaymentMethod(paymentMethod);

				if (selectedDiscount != null) {
					orderPaymentDetail.setDiscountId(selectedDiscount.getId());
					orderPaymentDetail.setDiscountsName(selectedDiscount.getName());
					orderPaymentDetail.setDiscountsValue(selectedDiscount.getDiscountsValue());
					orderPaymentDetail.setCalculatedDiscountValue(selectedDiscount.getCalculatedDiscountValue());
					orderPaymentDetail.setPriceDiscount(priceDiscountValue);
					orderPaymentDetail.setIsRefunded(0);

					orderPaymentDetail
							.setDiscountCode(orderPacket.getOrderHeader().getUsersToDiscounts().getDiscountCode());

				} else {
					orderPaymentDetail.setIsRefunded(1);
					orderPaymentDetail.setPriceDiscount(new BigDecimal(0));
				}

				orderPaymentDetail = updateOrderPaymentDetail(em, orderPaymentDetail, oldOrderHeaderAdapter);

				// new
				// OrderDetailUtility().applyTaxesToOrderPaymentDetail(orderPaymentDetail,
				// newOrderHeaderAdapter, seat);

				if (newOrderHeaderAdapter.getOrderPaymentDetails() != null) {

					newOrderHeaderAdapter.getOrderPaymentDetails().clear();
				}

				if (newOrderHeaderAdapter.getOrderPaymentDetails() == null) {
					List<OrderPaymentDetail> details = new ArrayList<OrderPaymentDetail>();
					details.add(orderPaymentDetail);
					newOrderHeaderAdapter.setOrderPaymentDetails(details);

				} else {
					newOrderHeaderAdapter.getOrderPaymentDetails().add(orderPaymentDetail);
				}

				return newOrderHeaderAdapter;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe(e);
			}
		}

		return newOrderHeaderAdapter;

	}

	private OrderPaymentDetail updateOrderPaymentDetail(EntityManager em, OrderPaymentDetail orderPaymentDetail,
			OrderHeader orderHeaderAdapter) {
		orderPaymentDetail.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		orderPaymentDetail.setUpdatedBy(orderHeaderAdapter.getUpdatedBy());
		TimezoneTime timezoneTime = new TimezoneTime();
		String currentDate = timezoneTime.getCurrentDate(em, orderHeaderAdapter.getLocationsId());
		String currentTime = timezoneTime.getCurrentTime(em, orderHeaderAdapter.getLocationsId());

		orderPaymentDetail.setDate(currentDate);
		orderPaymentDetail.setTime(currentTime);

		orderPaymentDetail.setCreatedBy(orderHeaderAdapter.getUpdatedBy());
		orderPaymentDetail.setOrderSourceGroupToPaymentGatewayTypeId(0);
		orderPaymentDetail.setOrderSourceToPaymentGatewayTypeId(0);
		orderPaymentDetail.setCashTipAmt(new BigDecimal(0));
		orderPaymentDetail.setPayementGatewayId(0);
		orderPaymentDetail.setCreditcardTipAmt(new BigDecimal(0));
		orderPaymentDetail.setTotalAmount(orderHeaderAdapter.getTotal());
		orderPaymentDetail.setChangeDue(new BigDecimal(0));
		orderPaymentDetail.setAmountPaid(new BigDecimal(0));
		orderPaymentDetail.setBalanceDue(orderHeaderAdapter.getBalanceDue());
		orderPaymentDetail.setSettledAmount(new BigDecimal(0));

		return orderPaymentDetail;

	}

	public BigDecimal getDiscountOnAmount(EntityManager em, BigDecimal amount, Discount selectedDiscount,
			BigDecimal baseAmount) {
		DiscountsType discountType = (DiscountsType) new CommonMethods().getObjectById("DiscountsType", em,DiscountsType.class, selectedDiscount.getDiscountsTypeId());
		BigDecimal discountAmount = new BigDecimal(0);

		if (discountType != null) {
		
			if (discountType.getId() == getDiscountTypeByLocationIdAndName(em, selectedDiscount.getLocationsId(),
					"Percentage Off").getId()) {

				discountAmount = new BigDecimal(
						(amount.doubleValue() * selectedDiscount.getCalculatedDiscountValue().doubleValue()) / 100);
			} else if (discountType
					.getId() == getDiscountTypeByLocationIdAndName(em, selectedDiscount.getLocationsId(), "Amount Off")
							.getId()) {
				if (baseAmount.doubleValue() == 0) {
					discountAmount = selectedDiscount.getCalculatedDiscountValue();
				} else {
					discountAmount = selectedDiscount.getCalculatedDiscountValue();
					BigDecimal parcentage = new BigDecimal(0);

					if (baseAmount.doubleValue() > 0.0) {
						parcentage = new BigDecimal(discountAmount.doubleValue() / baseAmount.doubleValue());
					}

					discountAmount = new BigDecimal(amount.doubleValue() * parcentage.doubleValue());
					if (discountAmount.doubleValue() > amount.doubleValue()) {
						discountAmount = amount;
					}
				}

			}
		}
		return discountAmount;
	}

	private BigDecimal getsubtotalAmmountForCalculatePercentageAmmountOfDollerOff(EntityManager em,
			OrderHeader orderHeader, List<String> list, Discount selectedDiscount) {
		BigDecimal baseAmount = new BigDecimal(0);
		try {
			for (OrderDetailItem orderDetailItemAdapter : orderHeader.getOrderDetailItems()) {

				if ((orderDetailItemAdapter.getDiscountWaysId() != getDiscountWaysByName(em, "Item Level").getId()
						&& orderDetailItemAdapter.getDiscountWaysId() != getDiscountWaysByName(em, "Seat Level")
								.getId())) {

					if (selectedDiscount.getIsGroup() == 1) {
						if (list.contains(orderDetailItemAdapter.getItemsId())) {
							BigDecimal ammount = orderDetailItemAdapter.getSubTotal();

							baseAmount = baseAmount.add(ammount);
						}

					} else {
						BigDecimal ammount = orderDetailItemAdapter.getSubTotal();
						baseAmount = baseAmount.add(ammount);
					}
				}
			}
		} catch (Exception e) {
			logger.severe(e);
		}
		return baseAmount;
	}

	public BigDecimal getOrderLevelDiscountForItem(EntityManager em, OrderDetailItem itemAdapter, BigDecimal ammount,
			List<String> list, Discount selectedDiscount, BigDecimal baseAmount) {
		try {
			if (selectedDiscount != null) {
				if (selectedDiscount.getIsGroup() == 1) {
					if (list != null) {
						String itemid = itemAdapter.getItemsId();
						if (list.contains(itemid)) {
							return getDiscountOnAmount(em, ammount, selectedDiscount, baseAmount);
						}
					}
				} else {
					return getDiscountOnAmount(em, ammount, selectedDiscount, baseAmount);
				}
			}
		} catch (Exception e) {
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public List<String> getItemListForGroupDiscount(EntityManager em, OrderHeader orderHeader,
			Discount selectedDiscount) {

		String itemsId = "";
		List<String> allowedItemListForDiscount = new ArrayList<String>();
		List<ItemsToDiscount> itemDiscountList = new ArrayList<ItemsToDiscount>();
		List<ItemsToDiscount> discountAllowedToItems = new ArrayList<ItemsToDiscount>();
		List<CategoryToDiscount> categoryDiscountList = new ArrayList<CategoryToDiscount>();
		try {
			for (OrderDetailItem orderDetailItemAdapter : orderHeader.getOrderDetailItems()) {
				allowedItemListForDiscount.add(orderDetailItemAdapter.getItemsId());
			}

			itemsId = StringUtils.join(allowedItemListForDiscount, ',');

			if (selectedDiscount != null && selectedDiscount.getIsGroup() == 1) {

				// fetch all item to discount for selected items
				itemDiscountList = getDiscountListForSelectedItems(em, itemsId);
				// fetch item to discount only for selected item and selected
				// discount
				discountAllowedToItems = getGroupListForSelectedDiscount(em, itemsId, selectedDiscount.getId());
				// get categoryToDiscount List For Selected Discount
				categoryDiscountList = getGroupListForSelectedDiscount(em, selectedDiscount.getId());
				allowedItemListForDiscount.clear();

				for (OrderDetailItem orderDetailItemAdapter : orderHeader.getOrderDetailItems()) {
					ItemsToDiscount itemsToDiscountsAdapter = new ItemsToDiscount(orderDetailItemAdapter.getItemsId());
					// check is should allow to apply discount , if yes
					// then add this item to list.
					if (!discountAllowedToItems.contains(itemsToDiscountsAdapter)) {
						// check is other discount applied to item ,if yes then
						// do nothing else check at category Level
						if (!itemDiscountList.contains(itemsToDiscountsAdapter)) {
							CategoryToDiscount categoryToDiscount = new CategoryToDiscount(
									orderDetailItemAdapter.getParentCategoryId());
							// check is discount allowed to category
							if (categoryDiscountList.contains(categoryToDiscount)) {
								allowedItemListForDiscount.add(orderDetailItemAdapter.getItemsId());
							}
						}
					} else {
						allowedItemListForDiscount.add(orderDetailItemAdapter.getItemsId());
					}
				}
			}
		} catch (Exception e) {
			logger.severe(e);
		}

		return allowedItemListForDiscount;
	}

	public List<CategoryToDiscount> getGroupListForSelectedDiscount(EntityManager em, String discountId) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CategoryToDiscount> criteria = builder.createQuery(CategoryToDiscount.class);
			Root<CategoryToDiscount> salesTax = criteria.from(CategoryToDiscount.class);
			TypedQuery<CategoryToDiscount> query = em.createQuery(criteria.select(salesTax).where(

					builder.equal(salesTax.get(CategoryToDiscount_.discountsId), discountId),
					builder.notEqual(salesTax.get(CategoryToDiscount_.status), "D"),
					builder.notEqual(salesTax.get(CategoryToDiscount_.status), "I")));
			return query.getResultList();

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;
	}

	public OrderStatus getOrderStatuByNameSourseGroupIdAndLocationId(EntityManager em, String name,
			String sourseGroupId, String locationId) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> salesTax = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(salesTax).where(

					builder.equal(salesTax.get(OrderStatus_.name), name),
					builder.equal(salesTax.get(OrderStatus_.locationsId), locationId),
					builder.equal(salesTax.get(OrderStatus_.orderSourceGroupId), sourseGroupId),
					builder.notEqual(salesTax.get(OrderStatus_.status), "D"),
					builder.notEqual(salesTax.get(OrderStatus_.status), "I")));
			return query.getSingleResult();

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;
	}

	public PaymentTransactionType getPaymentTransactionTypeByLocationIdAndName(EntityManager em, String locationId,
			String name) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentTransactionType> criteria = builder.createQuery(PaymentTransactionType.class);
			Root<PaymentTransactionType> salesTax = criteria.from(PaymentTransactionType.class);
			TypedQuery<PaymentTransactionType> query = em.createQuery(criteria.select(salesTax).where(

					builder.equal(salesTax.get(PaymentTransactionType_.locationsId), locationId),
					builder.equal(salesTax.get(PaymentTransactionType_.name), name),
					builder.notEqual(salesTax.get(PaymentTransactionType_.status), "D"),
					builder.notEqual(salesTax.get(PaymentTransactionType_.status), "I")));

			return query.getSingleResult();

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;
	}

	public PaymentMethodType getPaymentMethodTypeByLocationIdAndName(EntityManager em, String locationId, String name) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethodType> criteria = builder.createQuery(PaymentMethodType.class);
			Root<PaymentMethodType> salesTax = criteria.from(PaymentMethodType.class);
			TypedQuery<PaymentMethodType> query = em.createQuery(criteria.select(salesTax).where(

					builder.equal(salesTax.get(PaymentMethodType_.locationsId), locationId),
					builder.equal(salesTax.get(PaymentMethodType_.name), name),
					builder.notEqual(salesTax.get(PaymentMethodType_.status), "D")));

			return query.getSingleResult();

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;
	}

	public TransactionStatus getTransactionStatusByName(EntityManager em, String name) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<TransactionStatus> criteria = builder.createQuery(TransactionStatus.class);
			Root<TransactionStatus> salesTax = criteria.from(TransactionStatus.class);
			TypedQuery<TransactionStatus> query = em.createQuery(criteria.select(salesTax).where(

					builder.equal(salesTax.get(TransactionStatus_.name), name),
					builder.notEqual(salesTax.get(TransactionStatus_.status), "D"),
					builder.notEqual(salesTax.get(TransactionStatus_.status), "I")));

			return query.getSingleResult();

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;
	}

	public PaymentMethod getPaymentMethodByLocationIdAndPaymentMethodTypeId(EntityManager em, String locationId,
			String paymentMethodTypeId) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethod> criteria = builder.createQuery(PaymentMethod.class);
			Root<PaymentMethod> salesTax = criteria.from(PaymentMethod.class);
			TypedQuery<PaymentMethod> query = em.createQuery(criteria.select(salesTax).where(

					builder.equal(salesTax.get(PaymentMethod_.locationsId), locationId),
					builder.equal(salesTax.get(PaymentMethod_.paymentMethodTypeId), paymentMethodTypeId),
					builder.notEqual(salesTax.get(PaymentMethod_.status), "D"),
					builder.notEqual(salesTax.get(PaymentMethod_.status), "I")));

			return query.getResultList().get(0);

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;
	}

	public DiscountsType getDiscountTypeByLocationIdAndName(EntityManager em, String locationId, String name) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DiscountsType> criteria = builder.createQuery(DiscountsType.class);
			Root<DiscountsType> salesTax = criteria.from(DiscountsType.class);
			TypedQuery<DiscountsType> query = em.createQuery(criteria.select(salesTax).where(

					builder.equal(salesTax.get(DiscountsType_.discountsType), name),
					builder.equal(salesTax.get(DiscountsType_.locationsId), locationId),
					builder.notEqual(salesTax.get(DiscountsType_.status), "D"),
					builder.notEqual(salesTax.get(DiscountsType_.status), "I")));
			return query.getSingleResult();

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;
	}

	public DiscountsType getDiscountTypeByLocationIdAndId(EntityManager em, String id) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DiscountsType> criteria = builder.createQuery(DiscountsType.class);
			Root<DiscountsType> salesTax = criteria.from(DiscountsType.class);
			TypedQuery<DiscountsType> query = em
					.createQuery(criteria.select(salesTax).where(builder.equal(salesTax.get(DiscountsType_.id), id),
							builder.notEqual(salesTax.get(DiscountsType_.status), "D"),
							builder.notEqual(salesTax.get(DiscountsType_.status), "I")));
			return query.getSingleResult();

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;
	}

	public PaymentWay getPaymentWaysByNameAndLocationId(EntityManager em, String locationId, String name) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentWay> criteria = builder.createQuery(PaymentWay.class);
			Root<PaymentWay> salesTax = criteria.from(PaymentWay.class);
			TypedQuery<PaymentWay> query = em.createQuery(
					criteria.select(salesTax).where(builder.equal(salesTax.get(PaymentWay_.locationsId), locationId),
							builder.equal(salesTax.get(PaymentWay_.name), name),
							builder.notEqual(salesTax.get(PaymentWay_.status), "D"),
							builder.notEqual(salesTax.get(PaymentWay_.status), "I")));
			return query.getSingleResult();

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;
	}

	public List<ItemsToDiscount> getGroupListForSelectedDiscount(EntityManager em, String ids, String discountId) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToDiscount> criteria = builder.createQuery(ItemsToDiscount.class);
			Root<ItemsToDiscount> salesTax = criteria.from(ItemsToDiscount.class);
			TypedQuery<ItemsToDiscount> query = em.createQuery(
					criteria.select(salesTax).where(builder.equal(salesTax.get(ItemsToDiscount_.itemsId), ids),
							builder.equal(salesTax.get(ItemsToDiscount_.discountsId), discountId),
							builder.notEqual(salesTax.get(ItemsToDiscount_.status), "D"),
							builder.notEqual(salesTax.get(ItemsToDiscount_.status), "I")));
			return query.getResultList();

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;

	}

	public DiscountWays getDiscountWaysByName(EntityManager em, String name) {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DiscountWays> criteria = builder.createQuery(DiscountWays.class);
			Root<DiscountWays> salesTax = criteria.from(DiscountWays.class);
			TypedQuery<DiscountWays> query = em.createQuery(
					criteria.select(salesTax).where(builder.equal(salesTax.get(DiscountWays_.name), name)));
			return query.getResultList().get(0);

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;

	}

	public List<ItemsToDiscount> getDiscountListForSelectedItems(EntityManager em, String ids) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsToDiscount> criteria = builder.createQuery(ItemsToDiscount.class);
			Root<ItemsToDiscount> salesTax = criteria.from(ItemsToDiscount.class);
			TypedQuery<ItemsToDiscount> query = em.createQuery(
					criteria.select(salesTax).where(builder.equal(salesTax.get(ItemsToDiscount_.itemsId), ids),
							builder.notEqual(salesTax.get(ItemsToDiscount_.status), "D"),
							builder.notEqual(salesTax.get(ItemsToDiscount_.status), "I")));
			return query.getResultList();

		} catch (Exception e) {

			logger.severe(e, "No Result found ");
		}
		return null;
	}

	/**
	 * Gets the sales tax for order detail item.
	 *
	 * @param em
	 *            the em
	 * @param item
	 *            the item
	 * @return the sales tax for order detail item
	 */
	public List<SalesTax> getSalesTaxForOrderDetailItem(EntityManager em, Item item) {
		try {
			String queryString = "select s from SalesTax s where s.id in(" + item.getSalesTax1() + ","
					+ item.getSalesTax2() + "," + item.getSalesTax3() + "," + item.getSalesTax4() + ")";
			TypedQuery<SalesTax> query2 = em.createQuery(queryString, SalesTax.class);
			return query2.getResultList();

		} catch (Exception e) {
			// todo shlok need
			// handle proper exception
			logger.severe(e, "No Result in SalesTax");
		}
		return null;
	}

	/**
	 * Gets the sales tax list.
	 *
	 * @param em
	 *            the em
	 * @param orderHeader
	 *            the order header
	 * @param orderSourceToSalesTaxList
	 *            the order source to sales tax list
	 * @return the sales tax list
	 */
	public List<SalesTax> getSalesTaxList(EntityManager em, OrderHeader orderHeader,
			List<OrderSourceToSalesTax> orderSourceToSalesTaxList, OrderPacket orderPacket) {
		if (orderSourceToSalesTaxList != null) {
			List<SalesTax> salesTaxsList = new ArrayList<SalesTax>();
			for (OrderSourceToSalesTax orderSourceToSalesTax : orderSourceToSalesTaxList) {
				SalesTax orderSalesTax = getSalesTaxByIdAndLocationId(em, orderSourceToSalesTax.getTaxId().getId(),
						orderHeader.getLocationsId(), 0);

				if (orderSalesTax != null && orderSalesTax.getTaxName().equalsIgnoreCase("Gratuity")) {

					if (orderHeader.getIsGratuityApplied() == 1) {
						if (orderHeader.getPointOfServiceCount() >= orderSalesTax.getNumberOfPeople()) {
							orderHeader.setIsGratuityApplied(1);
							orderHeader.setGratuity(orderSalesTax.getRate());
						}
					}

				} else if (orderSalesTax != null) {

					salesTaxsList.add(orderSalesTax);
				}
			}
			return salesTaxsList;
		}
		return null;

	}

	public OrderHeader getOrderHeaderCalculationForCM(EntityManager em, Submit submit, OrderHeader order) {
		// todo shlok need
		// modilarise method

		List<OrderDetailItem> orderDetailItemsList = order.getOrderDetailItems();
		BigDecimal allItemSubTotal = new BigDecimal(0);
		BigDecimal allItemPriceGratuity = new BigDecimal(0);
		BigDecimal allItemTax = new BigDecimal(0);
		BigDecimal itemPriceGratuity = new BigDecimal(0);
		// location info for order header
		Location locationDetails = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				submit.getLocation_id());

		BigDecimal baseAmount = new BigDecimal(0);
		List<String> list = new ArrayList<String>();
		Discount selectedDiscount = null;
		if (submit.getDiscounts() != null) {
			for (CheckMateDiscounts discount : submit.getDiscounts()) {
				if (discount != null && discount.getId() != null) {
					String discountId = discount.getId();
					selectedDiscount = (Discount) new CommonMethods().getObjectById("Discount", em,Discount.class, discountId);

					list = getItemListForGroupDiscount(em, order, selectedDiscount);
					baseAmount = getsubtotalAmmountForCalculatePercentageAmmountOfDollerOff(em, order, list,
							selectedDiscount);
				}
			}
		}

		if (selectedDiscount != null) {
			selectedDiscount.setCalculatedDiscountValue(selectedDiscount.getDiscountsValue());
		}

		SalesTax orderSalesTax1 = null;
		SalesTax orderSalesTax2 = null;
		SalesTax orderSalesTax3 = null;
		SalesTax orderSalesTax4 = null;

		List<OrderSourceToSalesTax> orderSourceToSalesTaxList = getOrderSourceToSalesTaxByIdAndLocationId(em,
				order.getOrderSourceId(), order.getLocationsId());
		SalesTax gratuity = getGratuityByNameAndLocationId(em, order.getLocationsId());
		List<SalesTax> orderSalesTaxList = getSalesTaxList(em, order, orderSourceToSalesTaxList, null);
		if (orderSalesTaxList != null) {
			for (SalesTax salesTax : orderSalesTaxList) {
				if (salesTax.getTaxName().equals("Tax1") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax1 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax2") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax2 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax3") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax3 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax4") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax4 = salesTax;
				}
			}
		}
		int isRoundOffRequired = locationDetails.getIsRoundOffRequired();

		BigDecimal orderSaleTax1 = new BigDecimal(0);
		BigDecimal orderSaleTax2 = new BigDecimal(0);
		BigDecimal orderSaleTax3 = new BigDecimal(0);
		BigDecimal orderSaleTax4 = new BigDecimal(0);

		BigDecimal sumItemSaleTax1 = new BigDecimal(0);
		BigDecimal sumItemSaleTax2 = new BigDecimal(0);
		BigDecimal sumItemSaleTax3 = new BigDecimal(0);
		BigDecimal sumItemSaleTax4 = new BigDecimal(0);
		SalesTax finalSalesTax1 = new SalesTax();
		SalesTax finalSalesTax2 = new SalesTax();
		SalesTax finalSalesTax3 = new SalesTax();
		SalesTax finalSalesTax4 = new SalesTax();
		BigDecimal priceGratuity = new BigDecimal(0);
		BigDecimal discountTotal = new BigDecimal(0);
		BigDecimal itemisedDiscount = new BigDecimal(0);
		int count = 0;
		double deliveryCharge = 0;
		if (submit.getService_charges() != null && submit.getService_charges().size() > 0) {

			deliveryCharge = submit.getService_charges().get(0).getAmount();
		}
		List<OrderDetailItem> detailItemsList = new ArrayList<OrderDetailItem>();
		for (OrderDetailItem orderDetailItem : orderDetailItemsList) {
			count++;
			OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
					orderDetailItem.getOrderDetailStatusId());
			if (orderDetailStatus != null && !(orderDetailStatus.getName().equalsIgnoreCase("Item Removed"))
					&& !(orderDetailStatus.getName().equalsIgnoreCase("Recall"))) {
				detailItemsList.add(orderDetailItem);
			}
		}
		int noOfItems = detailItemsList.size();
		double itemDeliveryCharge = 0;
		if (deliveryCharge > 0) {
			itemDeliveryCharge = deliveryCharge / noOfItems;
		}
		for (OrderDetailItem orderDetailItem : orderDetailItemsList) {
			logger.severe(
					"orderDetailItem.getOrderDetailAttributes()==================1111111111111======================================="
							+ orderDetailItem.getOrderDetailAttributes());

			count++;
			OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
					orderDetailItem.getOrderDetailStatusId());
			if (orderDetailStatus != null && !(orderDetailStatus.getName().equalsIgnoreCase("Item Removed"))
					&& !(orderDetailStatus.getName().equalsIgnoreCase("Recall"))) {
				BigDecimal itemSubTotal = new BigDecimal(orderDetailItem.getSubTotal().doubleValue());
				allItemSubTotal = allItemSubTotal.add(itemSubTotal);
				Item item = getItemById(em, orderDetailItem.getItemsId());

				SalesTax salesTax1 = null;
				SalesTax salesTax2 = null;
				SalesTax salesTax3 = null;
				SalesTax salesTax4 = null;

				orderDetailItem.setDeliveryCharges(new BigDecimal(itemDeliveryCharge));
				BigDecimal itemDiscount = new BigDecimal(0);

				if (orderDetailItem.getDiscountWaysId() != getDiscountWaysByName(em, "Item Level").getId()
						&& orderDetailItem.getDiscountWaysId() != getDiscountWaysByName(em, "Seat Level").getId()) {
					itemDiscount = getOrderLevelDiscountForItem(em, orderDetailItem, itemSubTotal, list,
							selectedDiscount, baseAmount);
					itemSubTotal = itemSubTotal.subtract(itemDiscount);
				}
				discountTotal = discountTotal.add(itemDiscount);
				if (selectedDiscount != null) {
					DiscountsType discountType = (DiscountsType) new CommonMethods().getObjectById("DiscountsType", em,DiscountsType.class, selectedDiscount.getDiscountsTypeId());

					if (discountType != null) {
						if (count == order.getOrderDetailItems().size() && discountType.equals(
								getDiscountTypeByLocationIdAndName(em, locationDetails.getId(), "Amount Off"))) {
							if (submit.getPayment().getAmount() > 0.0) {
								if (discountTotal.doubleValue() != selectedDiscount.getCalculatedDiscountValue()
										.doubleValue()) {
									BigDecimal discountDiffrence = new BigDecimal(0);

									// added condition if discount value greator
									// than subtotal then do not calculate
									// discount difference as it cause negative
									// calculations
									if (order.getSubTotal().doubleValue() >= selectedDiscount
											.getCalculatedDiscountValue().doubleValue()) {
										discountDiffrence = selectedDiscount.getCalculatedDiscountValue()
												.subtract(discountTotal);
									}

									discountTotal = discountDiffrence.add(discountTotal);
								}
							} else {
								if (discountTotal.doubleValue() != selectedDiscount.getDiscountsValue().doubleValue()) {
									BigDecimal discountDiffrence = new BigDecimal(0);

									// added condition if discount value greator
									// than subtotal then do not calculate
									// discount difference as it cause negative
									// calculations
									if (order.getSubTotal().doubleValue() >= selectedDiscount.getDiscountsValue()
											.doubleValue()) {
										discountDiffrence = selectedDiscount.getDiscountsValue()
												.subtract(discountTotal);
									}

									discountTotal = discountDiffrence.add(discountTotal);
								}
							}

						}
					}
				}

				if (itemDiscount.doubleValue() > 0
						&& selectedDiscount != null /*
													 * && ! isGratuityCal
													 */) {
					orderDetailItem.setPriceDiscount(itemDiscount);
					orderDetailItem.setDiscountId(selectedDiscount.getId());
					orderDetailItem.setDiscountCode(selectedDiscount.getCoupanCode());

					orderDetailItem.setDiscountValue(selectedDiscount.getDiscountsValue().intValue());
					orderDetailItem.setDiscountDisplayName(selectedDiscount.getDisplayName());
					orderDetailItem.setCalculatedDiscountValue(selectedDiscount.getCalculatedDiscountValue());
					orderDetailItem.setDiscountName(selectedDiscount.getName());
					orderDetailItem.setDiscountTypeId(selectedDiscount.getDiscountsTypeId());
					orderDetailItem.setDiscountWaysId(getDiscountWaysByName(em, "Order Level").getId());
					orderDetailItem.setDiscountTypeName(
							getDiscountTypeByLocationIdAndId(em, selectedDiscount.getDiscountsTypeId())
									.getDiscountsType());
				} else if (itemisedDiscount.doubleValue() <= 0.00) {
					orderDetailItem.setPriceDiscount(new BigDecimal(0));
					orderDetailItem.setDiscountValue(0);
					orderDetailItem.setDiscountId(null);
					orderDetailItem.setDiscountCode("");
					orderDetailItem.setCalculatedDiscountValue(new BigDecimal(0));
					orderDetailItem.setDiscountName("");
					orderDetailItem.setDiscountDisplayName("");
					orderDetailItem.setDiscountTypeId(null);
					orderDetailItem.setDiscountWaysId(0);
					orderDetailItem.setDiscountTypeName("");

				} else {
					orderDetailItem.setPriceDiscount(new BigDecimal(0));
					orderDetailItem.setDiscountValue(0);
					orderDetailItem.setCalculatedDiscountValue(new BigDecimal(0));
					orderDetailItem.setDiscountName("");
					orderDetailItem.setDiscountId(null);
					orderDetailItem.setDiscountCode("");
					orderDetailItem.setDiscountDisplayName("");
					orderDetailItem.setDiscountTypeId(null);
					orderDetailItem.setDiscountWaysId(0);
					orderDetailItem.setDiscountTypeName("");
				}

				List<SalesTax> salesTaxList = getSalesTaxForOrderDetailItem(em, item);

				BigDecimal itemSaleTax1 = new BigDecimal(0);
				BigDecimal itemSaleTax2 = new BigDecimal(0);
				BigDecimal itemSaleTax3 = new BigDecimal(0);
				BigDecimal itemSaleTax4 = new BigDecimal(0);
				for (SalesTax salesTax : salesTaxList) {
					if (salesTax.getTaxName().equals("Tax1") && salesTax.getIsItemSpecific() == 1) {
						salesTax1 = salesTax;
					} else if (salesTax.getTaxName().equals("Tax2") && salesTax.getIsItemSpecific() == 1) {
						salesTax2 = salesTax;
					} else if (salesTax.getTaxName().equals("Tax3") && salesTax.getIsItemSpecific() == 1) {
						salesTax3 = salesTax;
					} else if (salesTax.getTaxName().equals("Tax4") && salesTax.getIsItemSpecific() == 1) {
						salesTax4 = salesTax;
					}
				}
				order.setIsGratuityApplied(0);
				if (gratuity != null && order.getIsGratuityApplied() == 1) {
					priceGratuity = itemSubTotal.multiply(gratuity.getRate().divide(new BigDecimal(100)));
				}
				if (orderSalesTax1 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (gratuity != null && order.getIsGratuityApplied() == 1
							&& orderSalesTax1.getTaxId() == gratuity.getId()) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					orderSaleTax1 = newItemSubTotal.multiply(orderSalesTax1.getRate().divide(new BigDecimal(100)));
				}
				if (orderSalesTax2 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (gratuity != null && order.getIsGratuityApplied() == 1
							&& orderSalesTax2.getTaxId() == gratuity.getId()) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					orderSaleTax2 = newItemSubTotal.multiply(orderSalesTax2.getRate().divide(new BigDecimal(100)));
				}

				if (orderSalesTax3 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (gratuity != null && order.getIsGratuityApplied() == 1
							&& orderSalesTax3.getTaxId() == gratuity.getId()) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					orderSaleTax3 = newItemSubTotal.multiply(orderSalesTax3.getRate().divide(new BigDecimal(100)));
				}

				if (orderSalesTax4 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (gratuity != null && order.getIsGratuityApplied() == 1
							&& orderSalesTax4.getTaxId() == gratuity.getId()) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					orderSaleTax4 = newItemSubTotal.multiply(orderSalesTax4.getRate().divide(new BigDecimal(100)));
				}
				if (salesTax1 != null || orderSalesTax1 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (salesTax1 != null && gratuity != null && order.getIsGratuityApplied() == 1
							&& salesTax1.getTaxId() == gratuity.getId()) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}

					if (salesTax1 != null) {
						itemSaleTax1 = newItemSubTotal.multiply(salesTax1.getRate().divide(new BigDecimal(100)));
						finalSalesTax1.setTaxName(salesTax1.getTaxName());
						finalSalesTax1.setDisplayName(salesTax1.getDisplayName());
						finalSalesTax1.setRate(salesTax1.getRate());

					} else {
						finalSalesTax1.setTaxName(orderSalesTax1.getTaxName());
						finalSalesTax1.setDisplayName(orderSalesTax1.getDisplayName());
						finalSalesTax1.setRate(orderSalesTax1.getRate());
					}
					itemSaleTax1 = itemSaleTax1.add(orderSaleTax1);
					sumItemSaleTax1 = sumItemSaleTax1.add(itemSaleTax1);
					orderDetailItem.setTaxName1(finalSalesTax1.getTaxName());
					orderDetailItem.setTaxDisplayName1(finalSalesTax1.getDisplayName());
					orderDetailItem.setTaxRate1(finalSalesTax1.getRate());
					orderDetailItem.setPriceTax1(itemSaleTax1);
				}
				if (salesTax2 != null || orderSalesTax2 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (salesTax2 != null && gratuity != null && order.getIsGratuityApplied() == 1
							&& salesTax2.getTaxId() == gratuity.getId()) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					if (salesTax2 != null) {
						itemSaleTax2 = newItemSubTotal.multiply(salesTax2.getRate().divide(new BigDecimal(100)));
						finalSalesTax2.setTaxName(salesTax2.getTaxName());
						finalSalesTax2.setDisplayName(salesTax2.getDisplayName());
						finalSalesTax2.setRate(salesTax2.getRate());
					} else {
						finalSalesTax2.setTaxName(orderSalesTax2.getTaxName());
						finalSalesTax2.setDisplayName(orderSalesTax2.getDisplayName());
						finalSalesTax2.setRate(orderSalesTax2.getRate());
					}
					itemSaleTax2 = itemSaleTax2.add(orderSaleTax2);
					sumItemSaleTax2 = sumItemSaleTax2.add(itemSaleTax2);
					orderDetailItem.setTaxName2(finalSalesTax2.getTaxName());
					orderDetailItem.setTaxDisplayName2(finalSalesTax2.getDisplayName());
					orderDetailItem.setTaxRate2(finalSalesTax2.getRate());
					orderDetailItem.setPriceTax2(itemSaleTax2);
				}
				if (salesTax3 != null || orderSalesTax3 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (salesTax3 != null && gratuity != null && order.getIsGratuityApplied() == 1
							&& salesTax3.getTaxId() == gratuity.getId()) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					if (salesTax3 != null) {
						itemSaleTax3 = newItemSubTotal.multiply(salesTax3.getRate().divide(new BigDecimal(100)));
						finalSalesTax3.setTaxName(salesTax3.getTaxName());
						finalSalesTax3.setDisplayName(salesTax3.getDisplayName());
						finalSalesTax3.setRate(salesTax3.getRate());
					} else {
						finalSalesTax3.setTaxName(orderSalesTax3.getTaxName());
						finalSalesTax3.setDisplayName(orderSalesTax3.getDisplayName());
						finalSalesTax3.setRate(orderSalesTax3.getRate());
					}
					itemSaleTax3 = itemSaleTax3.add(orderSaleTax3);
					sumItemSaleTax3 = sumItemSaleTax3.add(itemSaleTax3);
					orderDetailItem.setTaxName3(finalSalesTax3.getTaxName());
					orderDetailItem.setTaxDisplayName3(finalSalesTax3.getDisplayName());
					orderDetailItem.setTaxRate3(finalSalesTax3.getRate());
					orderDetailItem.setPriceTax3(itemSaleTax3);
				}

				if (salesTax4 != null || orderSalesTax4 != null) {
					BigDecimal newItemSubTotal = itemSubTotal;
					if (salesTax4 != null && gratuity != null && order.getIsGratuityApplied() == 1
							&& salesTax4.getTaxId() == gratuity.getId()) {
						newItemSubTotal = itemSubTotal.add(priceGratuity);
					}
					if (salesTax4 != null) {
						itemSaleTax4 = newItemSubTotal.multiply(salesTax4.getRate().divide(new BigDecimal(100)));
						finalSalesTax4.setTaxName(salesTax4.getTaxName());
						finalSalesTax4.setDisplayName(salesTax4.getDisplayName());
						finalSalesTax4.setRate(salesTax4.getRate());

					} else {
						finalSalesTax4.setTaxName(orderSalesTax4.getTaxName());
						finalSalesTax4.setDisplayName(orderSalesTax4.getDisplayName());
						finalSalesTax4.setRate(orderSalesTax4.getRate());
					}
					itemSaleTax4 = itemSaleTax4.add(orderSaleTax4);
					sumItemSaleTax4 = sumItemSaleTax4.add(itemSaleTax4);
					orderDetailItem.setTaxName4(finalSalesTax4.getTaxName());
					orderDetailItem.setTaxDisplayName4(finalSalesTax4.getDisplayName());
					orderDetailItem.setTaxRate4(finalSalesTax4.getRate());
					orderDetailItem.setPriceTax4(itemSaleTax4);
				}
				BigDecimal itemTotalTax = itemSaleTax1.add(itemSaleTax2).add(itemSaleTax3).add(itemSaleTax4);

				orderDetailItem.setTotalTax(itemTotalTax);
				allItemTax = allItemTax.add(itemTotalTax);

				if (order.getIsGratuityApplied() == 1) {

					itemPriceGratuity = itemSubTotal.multiply(order.getGratuity().divide(new BigDecimal(100)));
					orderDetailItem.setPriceGratuity(itemPriceGratuity);
					allItemPriceGratuity = allItemPriceGratuity.add(itemPriceGratuity);

					orderDetailItem.setGratuity(order.getGratuity());

				}

				BigDecimal total = itemTotalTax.add(itemSubTotal).add(itemPriceGratuity);

				orderDetailItem.setTotal(total);
				orderDetailItem.setAmountPaid(total);
				orderDetailItem.setBalanceDue(new BigDecimal(0));
				if (isRoundOffRequired == 1)
					orderDetailItem.setRoundOffTotal(total.setScale(0, RoundingMode.DOWN));
				else
					orderDetailItem.setRoundOffTotal(total.setScale(2, total.ROUND_HALF_DOWN));
			}

		}

		order.setPriceDiscountItemLevel(itemisedDiscount);
		order.setPriceDiscount(discountTotal);

		if (selectedDiscount != null /* && !isGratuityCal */) {
			order.setDiscountsValue(selectedDiscount.getDiscountsValue());
			order.setCalculatedDiscountValue(selectedDiscount.getCalculatedDiscountValue());
			order.setDiscountsId(selectedDiscount.getId());
			order.setDiscountsName(selectedDiscount.getName());
			order.setDiscountDisplayName(selectedDiscount.getDisplayName());
			order.setDiscountsTypeId(selectedDiscount.getDiscountsTypeId());
			order.setDiscountsTypeName(
					getDiscountTypeByLocationIdAndId(em, selectedDiscount.getDiscountsTypeId()).getDiscountsType());

		} else {
			order.setDiscountsValue(new BigDecimal(0));
			order.setCalculatedDiscountValue(new BigDecimal(0));
			order.setDiscountsId(null);
			order.setDiscountsName("");
			order.setDiscountDisplayName("");
			order.setDiscountsTypeId(null);
			order.setDiscountsTypeName("");
		}

		order.setSubTotal(allItemSubTotal);
		order.setPriceGratuity(allItemPriceGratuity);

		if (finalSalesTax1.getTaxName() != null) {
			order.setPriceTax1(sumItemSaleTax1);
			order.setTaxName1(finalSalesTax1.getTaxName());
			order.setTaxDisplayName1(finalSalesTax1.getDisplayName());
			order.setTaxRate1(finalSalesTax1.getRate());
		}
		if (finalSalesTax2.getTaxName() != null) {
			order.setPriceTax2(sumItemSaleTax2);
			order.setTaxName2(finalSalesTax2.getTaxName());
			order.setTaxDisplayName2(finalSalesTax2.getDisplayName());
			order.setTaxRate2(finalSalesTax2.getRate());
		}

		if (finalSalesTax3.getTaxName() != null) {
			order.setPriceTax3(sumItemSaleTax3);
			order.setTaxName3(finalSalesTax3.getTaxName());
			order.setTaxDisplayName3(finalSalesTax3.getDisplayName());
			order.setTaxRate3(finalSalesTax3.getRate());
		}

		if (finalSalesTax4.getTaxName() != null) {
			order.setPriceTax4(sumItemSaleTax4);
			order.setTaxName4(finalSalesTax4.getTaxName());
			order.setTaxDisplayName4(finalSalesTax4.getDisplayName());
			order.setTaxRate4(finalSalesTax4.getRate());
		}

		BigDecimal orderTotalTax = sumItemSaleTax1.add(sumItemSaleTax2).add(sumItemSaleTax3).add(sumItemSaleTax4);
		// BigDecimal totalTax = orderTotalTax.add(allItemTax);
		order.setTotalTax(orderTotalTax);

		BigDecimal orderTotal = orderTotalTax.add(allItemPriceGratuity).add(allItemSubTotal).subtract(discountTotal);

	
		if (order.getDeliveryCharges() != null) {
			orderTotal = orderTotal.add(order.getDeliveryCharges());
		}

		order.setTotal(orderTotal);

		if (isRoundOffRequired == 1)
			order.setRoundOffTotal(orderTotal.setScale(0, RoundingMode.DOWN));
		else
			order.setRoundOffTotal(orderTotal.setScale(2, orderTotal.ROUND_HALF_DOWN));

		BigDecimal amountPaid = new BigDecimal(submit.getPayment().getAmount());
		BigDecimal balanceDue = orderTotal.subtract(amountPaid);

		order.setBalanceDue(balanceDue.setScale(2, balanceDue.ROUND_HALF_DOWN));
		order.setAmountPaid(amountPaid);

		// submit = createObjectForOrderPaymentForDiscount(em, submit, submit,
		// selectedDiscount, order.getPriceDiscount(),orderPacket);

		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, order.getOrderSourceId());
		if (order.getPriceDiscount().doubleValue() > 0.0) {

			if (order.getBalanceDue().doubleValue() <= 0) {
				// if balance due get zero after applying discount
				order.setOrderStatusId(getOrderStatuByNameSourseGroupIdAndLocationId(em, "Order Paid",
						orderSource.getOrderSourceGroupId(), order.getLocationsId()).getId());
				order.setBalanceDue(new BigDecimal(0));

			} else {
				// apply status as per discount operation selected
				order.setOrderStatusId(getOrderStatuByNameSourseGroupIdAndLocationId(em, "Discount Applied",
						orderSource.getOrderSourceGroupId(), order.getLocationsId()).getId());

			}
		}
		order.setOrderDetailItems(orderDetailItemsList);

		order.setPaymentWaysId(getPaymentWaysByNameAndLocationId(em, order.getLocationsId(), "All in One").getId());

		return order;

	}

	public OrderHeader getCalculationForCM(EntityManager em, Calculate calculate, OrderHeader order) {

		List<OrderDetailItem> orderDetailItemsList = order.getOrderDetailItems();
		BigDecimal allItemSubTotal = new BigDecimal(0);
		BigDecimal allItemTax = new BigDecimal(0);
		// location info for order header
		Location locationDetails = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				order.getLocationsId());

		BigDecimal baseAmount = new BigDecimal(0);
		List<Integer> list = new ArrayList<Integer>();
		SalesTax orderSalesTax1 = null;
		SalesTax orderSalesTax2 = null;
		SalesTax orderSalesTax3 = null;
		SalesTax orderSalesTax4 = null;

		List<OrderSourceToSalesTax> orderSourceToSalesTaxList = getOrderSourceToSalesTaxByIdAndLocationId(em,
				order.getOrderSourceId(), order.getLocationsId());
		SalesTax gratuity = getGratuityByNameAndLocationId(em, order.getLocationsId());
		List<SalesTax> orderSalesTaxList = getSalesTaxList(em, order, orderSourceToSalesTaxList, null);
		if (orderSalesTaxList != null) {
			for (SalesTax salesTax : orderSalesTaxList) {
				if (salesTax.getTaxName().equals("Tax1") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax1 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax2") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax2 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax3") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax3 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax4") && salesTax.getIsItemSpecific() == 0) {
					orderSalesTax4 = salesTax;
				}
			}
		}
		int isRoundOffRequired = locationDetails.getIsRoundOffRequired();

		BigDecimal orderSaleTax1 = new BigDecimal(0);
		BigDecimal orderSaleTax2 = new BigDecimal(0);
		BigDecimal orderSaleTax3 = new BigDecimal(0);
		BigDecimal orderSaleTax4 = new BigDecimal(0);

		BigDecimal sumItemSaleTax1 = new BigDecimal(0);
		BigDecimal sumItemSaleTax2 = new BigDecimal(0);
		BigDecimal sumItemSaleTax3 = new BigDecimal(0);
		BigDecimal sumItemSaleTax4 = new BigDecimal(0);
		SalesTax finalSalesTax1 = new SalesTax();
		SalesTax finalSalesTax2 = new SalesTax();
		SalesTax finalSalesTax3 = new SalesTax();
		SalesTax finalSalesTax4 = new SalesTax();
		int count = 0;

		List<OrderDetailItem> detailItemsList = new ArrayList<OrderDetailItem>();
		for (OrderDetailItem orderDetailItem : orderDetailItemsList) {
			count++;
			OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
					orderDetailItem.getOrderDetailStatusId());
			if (orderDetailStatus != null && !(orderDetailStatus.getName().equalsIgnoreCase("Item Removed"))
					&& !(orderDetailStatus.getName().equalsIgnoreCase("Recall"))) {
				detailItemsList.add(orderDetailItem);
			}
		}

		for (OrderDetailItem orderDetailItem : orderDetailItemsList) {
			logger.severe(
					"orderDetailItem.getOrderDetailAttributes()==================1111111111111======================================="
							+ orderDetailItem.getOrderDetailAttributes());

			count++;
			OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
					orderDetailItem.getOrderDetailStatusId());

			BigDecimal itemSubTotal = new BigDecimal(orderDetailItem.getSubTotal().doubleValue());
			allItemSubTotal = allItemSubTotal.add(itemSubTotal);
			Item item = getItemById(em, orderDetailItem.getItemsId());

			SalesTax salesTax1 = null;
			SalesTax salesTax2 = null;
			SalesTax salesTax3 = null;
			SalesTax salesTax4 = null;

			List<SalesTax> salesTaxList = getSalesTaxForOrderDetailItem(em, item);

			BigDecimal itemSaleTax1 = new BigDecimal(0);
			BigDecimal itemSaleTax2 = new BigDecimal(0);
			BigDecimal itemSaleTax3 = new BigDecimal(0);
			BigDecimal itemSaleTax4 = new BigDecimal(0);
			for (SalesTax salesTax : salesTaxList) {
				if (salesTax.getTaxName().equals("Tax1") && salesTax.getIsItemSpecific() == 1) {
					salesTax1 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax2") && salesTax.getIsItemSpecific() == 1) {
					salesTax2 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax3") && salesTax.getIsItemSpecific() == 1) {
					salesTax3 = salesTax;
				} else if (salesTax.getTaxName().equals("Tax4") && salesTax.getIsItemSpecific() == 1) {
					salesTax4 = salesTax;
				}
			}

			if (orderSalesTax1 != null) {
				BigDecimal newItemSubTotal = itemSubTotal;
				orderSaleTax1 = newItemSubTotal.multiply(orderSalesTax1.getRate().divide(new BigDecimal(100)));
			}
			if (orderSalesTax2 != null) {
				BigDecimal newItemSubTotal = itemSubTotal;
				orderSaleTax2 = newItemSubTotal.multiply(orderSalesTax2.getRate().divide(new BigDecimal(100)));
			}

			if (orderSalesTax3 != null) {
				BigDecimal newItemSubTotal = itemSubTotal;
				orderSaleTax3 = newItemSubTotal.multiply(orderSalesTax3.getRate().divide(new BigDecimal(100)));
			}

			if (orderSalesTax4 != null) {
				BigDecimal newItemSubTotal = itemSubTotal;
				orderSaleTax4 = newItemSubTotal.multiply(orderSalesTax4.getRate().divide(new BigDecimal(100)));
			}
			if (salesTax1 != null || orderSalesTax1 != null) {
				BigDecimal newItemSubTotal = itemSubTotal;

				if (salesTax1 != null) {
					itemSaleTax1 = newItemSubTotal.multiply(salesTax1.getRate().divide(new BigDecimal(100)));
					finalSalesTax1.setTaxName(salesTax1.getTaxName());
					finalSalesTax1.setDisplayName(salesTax1.getDisplayName());
					finalSalesTax1.setRate(salesTax1.getRate());

				} else {
					finalSalesTax1.setTaxName(orderSalesTax1.getTaxName());
					finalSalesTax1.setDisplayName(orderSalesTax1.getDisplayName());
					finalSalesTax1.setRate(orderSalesTax1.getRate());
				}
				itemSaleTax1 = itemSaleTax1.add(orderSaleTax1);
				sumItemSaleTax1 = sumItemSaleTax1.add(itemSaleTax1);
				orderDetailItem.setTaxName1(finalSalesTax1.getTaxName());
				orderDetailItem.setTaxDisplayName1(finalSalesTax1.getDisplayName());
				orderDetailItem.setTaxRate1(finalSalesTax1.getRate());
				orderDetailItem.setPriceTax1(itemSaleTax1);
			}
			if (salesTax2 != null || orderSalesTax2 != null) {
				BigDecimal newItemSubTotal = itemSubTotal;
				if (salesTax2 != null) {
					itemSaleTax2 = newItemSubTotal.multiply(salesTax2.getRate().divide(new BigDecimal(100)));
					finalSalesTax2.setTaxName(salesTax2.getTaxName());
					finalSalesTax2.setDisplayName(salesTax2.getDisplayName());
					finalSalesTax2.setRate(salesTax2.getRate());
				} else {
					finalSalesTax2.setTaxName(orderSalesTax2.getTaxName());
					finalSalesTax2.setDisplayName(orderSalesTax2.getDisplayName());
					finalSalesTax2.setRate(orderSalesTax2.getRate());
				}
				itemSaleTax2 = itemSaleTax2.add(orderSaleTax2);
				sumItemSaleTax2 = sumItemSaleTax2.add(itemSaleTax2);
				orderDetailItem.setTaxName2(finalSalesTax2.getTaxName());
				orderDetailItem.setTaxDisplayName2(finalSalesTax2.getDisplayName());
				orderDetailItem.setTaxRate2(finalSalesTax2.getRate());
				orderDetailItem.setPriceTax2(itemSaleTax2);
			}
			if (salesTax3 != null || orderSalesTax3 != null) {
				BigDecimal newItemSubTotal = itemSubTotal;
				if (salesTax3 != null) {
					itemSaleTax3 = newItemSubTotal.multiply(salesTax3.getRate().divide(new BigDecimal(100)));
					finalSalesTax3.setTaxName(salesTax3.getTaxName());
					finalSalesTax3.setDisplayName(salesTax3.getDisplayName());
					finalSalesTax3.setRate(salesTax3.getRate());
				} else {
					finalSalesTax3.setTaxName(orderSalesTax3.getTaxName());
					finalSalesTax3.setDisplayName(orderSalesTax3.getDisplayName());
					finalSalesTax3.setRate(orderSalesTax3.getRate());
				}
				itemSaleTax3 = itemSaleTax3.add(orderSaleTax3);
				sumItemSaleTax3 = sumItemSaleTax3.add(itemSaleTax3);
				orderDetailItem.setTaxName3(finalSalesTax3.getTaxName());
				orderDetailItem.setTaxDisplayName3(finalSalesTax3.getDisplayName());
				orderDetailItem.setTaxRate3(finalSalesTax3.getRate());
				orderDetailItem.setPriceTax3(itemSaleTax3);
			}

			if (salesTax4 != null || orderSalesTax4 != null) {
				BigDecimal newItemSubTotal = itemSubTotal;
				if (salesTax4 != null) {
					itemSaleTax4 = newItemSubTotal.multiply(salesTax4.getRate().divide(new BigDecimal(100)));
					finalSalesTax4.setTaxName(salesTax4.getTaxName());
					finalSalesTax4.setDisplayName(salesTax4.getDisplayName());
					finalSalesTax4.setRate(salesTax4.getRate());

				} else {
					finalSalesTax4.setTaxName(orderSalesTax4.getTaxName());
					finalSalesTax4.setDisplayName(orderSalesTax4.getDisplayName());
					finalSalesTax4.setRate(orderSalesTax4.getRate());
				}
				itemSaleTax4 = itemSaleTax4.add(orderSaleTax4);
				sumItemSaleTax4 = sumItemSaleTax4.add(itemSaleTax4);
				orderDetailItem.setTaxName4(finalSalesTax4.getTaxName());
				orderDetailItem.setTaxDisplayName4(finalSalesTax4.getDisplayName());
				orderDetailItem.setTaxRate4(finalSalesTax4.getRate());
				orderDetailItem.setPriceTax4(itemSaleTax4);
			}
			BigDecimal itemTotalTax = itemSaleTax1.add(itemSaleTax2).add(itemSaleTax3).add(itemSaleTax4);

			orderDetailItem.setTotalTax(itemTotalTax);
			allItemTax = allItemTax.add(itemTotalTax);

			BigDecimal total = itemTotalTax.add(itemSubTotal);

			orderDetailItem.setTotal(total);
			if (isRoundOffRequired == 1)
				orderDetailItem.setRoundOffTotal(total.setScale(0, RoundingMode.DOWN));
			else
				orderDetailItem.setRoundOffTotal(total.setScale(2, total.ROUND_HALF_DOWN));

		}

		order.setSubTotal(allItemSubTotal);

		if (finalSalesTax1.getTaxName() != null) {
			order.setPriceTax1(sumItemSaleTax1);
			order.setTaxName1(finalSalesTax1.getTaxName());
			order.setTaxDisplayName1(finalSalesTax1.getDisplayName());
			order.setTaxRate1(finalSalesTax1.getRate());
		}
		if (finalSalesTax2.getTaxName() != null) {
			order.setPriceTax2(sumItemSaleTax2);
			order.setTaxName2(finalSalesTax2.getTaxName());
			order.setTaxDisplayName2(finalSalesTax2.getDisplayName());
			order.setTaxRate2(finalSalesTax2.getRate());
		}

		if (finalSalesTax3.getTaxName() != null) {
			order.setPriceTax3(sumItemSaleTax3);
			order.setTaxName3(finalSalesTax3.getTaxName());
			order.setTaxDisplayName3(finalSalesTax3.getDisplayName());
			order.setTaxRate3(finalSalesTax3.getRate());
		}

		if (finalSalesTax4.getTaxName() != null) {
			order.setPriceTax4(sumItemSaleTax4);
			order.setTaxName4(finalSalesTax4.getTaxName());
			order.setTaxDisplayName4(finalSalesTax4.getDisplayName());
			order.setTaxRate4(finalSalesTax4.getRate());
		}

		BigDecimal orderTotalTax = sumItemSaleTax1.add(sumItemSaleTax2).add(sumItemSaleTax3).add(sumItemSaleTax4);
		// BigDecimal totalTax = orderTotalTax.add(allItemTax);
		order.setTotalTax(orderTotalTax);

		BigDecimal orderTotal = orderTotalTax.add(allItemSubTotal);

		if (order.getDeliveryCharges() != null) {
			orderTotal = orderTotal.add(order.getDeliveryCharges());
		}

		order.setTotal(orderTotal);

		if (isRoundOffRequired == 1)
			order.setRoundOffTotal(orderTotal.setScale(0, RoundingMode.DOWN));
		else
			order.setRoundOffTotal(orderTotal.setScale(2, orderTotal.ROUND_HALF_DOWN));

		return order;

	}

}
