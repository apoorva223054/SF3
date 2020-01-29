/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.DiscountItemReporting;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.ItemGroupReporting;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.OrderSourceGroupwiseReporting;
import com.nirvanaxp.services.jaxrs.packets.receiptPacket.PaymentMethodwiseReporting;
import com.nirvanaxp.types.entities.locations.Location;

public class ReportingServiceBean {

	public List<PaymentMethodwiseReporting> getPaymentMethodwiseReporting(EntityManager em, Location location,
			String startDate, String endDate, int isLast, int isWeekly, int isMonthly, int isYearly, int isDaily,
			int isToDate) throws Exception, InvalidSessionException, IOException {

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call p_revenue_payment_method_BI(?,?,?,?,?,?,?,?,?)")
				.setParameter(1, location.getBusinessId()).setParameter(2, startDate).setParameter(3, endDate)
				.setParameter(4, isLast).setParameter(5, isWeekly).setParameter(6, isMonthly).setParameter(7, isYearly)
				.setParameter(8, isDaily).setParameter(9, isToDate).getResultList();
		List<PaymentMethodwiseReporting> list = new ArrayList<PaymentMethodwiseReporting>();
		if (resultList != null && resultList.size() > 0) {
			for (Object[] objRow : resultList) {
				PaymentMethodwiseReporting revenue = new PaymentMethodwiseReporting();
				// if this has primary key not 0

				int i = 0;
				if (objRow[i] != null)
					revenue.setPaymentMethodTypeName(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setPaymentMethodName((String) objRow[i]);
				i++;
				if (objRow[i] != null)
					revenue.setGuestCount(((int) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setAmountPaid(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setCashTip(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setCardTip(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setFromDate(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenue.setToDate(((String) objRow[i]) + "");
				i++;
				if (revenue.getPaymentMethodName() != null) {
					list.add(revenue);
				}
			}
		}
		return list;
	}

	public List<OrderSourceGroupwiseReporting> getOrderSourceGroupReportingAccountLevel(EntityManager em,
			String startDate, String endDate, int isLast, int isWeekly, int isMonthly, int isYearly, int isDaily,
			int isToDate) throws Exception, InvalidSessionException, IOException {
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call p_Enterprise_order_source_group_BI(?,?,?,?,?,?,?,?)")
				.setParameter(1, startDate).setParameter(2, endDate).setParameter(3, isLast).setParameter(4, isWeekly)
				.setParameter(5, isMonthly).setParameter(6, isYearly).setParameter(7, isDaily).setParameter(8, isToDate)
				.getResultList();
		List<OrderSourceGroupwiseReporting> list = new ArrayList<OrderSourceGroupwiseReporting>();
		if (resultList != null && resultList.size() > 0) {
			for (Object[] objRow : resultList) {
				OrderSourceGroupwiseReporting revenue = new OrderSourceGroupwiseReporting();
				// if this has primary key not 0

				int i = 0;
				if (objRow[i] != null)
					revenue.setPointOfServiceCount(((int) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setOrderSourceGroupName(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenue.setAmountPaid(((double) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenue.setFromDate(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenue.setToDate(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenue.setLocationName(((String) objRow[i]));
				i++;
				if (revenue.getOrderSourceGroupName() != null) {
					list.add(revenue);
				}
			}
		}
		return list;

	}

	public List<ItemGroupReporting> getItemGroupReporting(EntityManager em, String startDate, String endDate,
			int isLast, int isWeekly, int isMonthly, int isYearly, int isDaily, int isToDate)
			throws Exception, InvalidSessionException, IOException {

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em
				.createNativeQuery("call sp_for_Enterprise_reporting_category_BI(?,?,?,?,?,?,?,?)")
				.setParameter(1, startDate).setParameter(2, endDate).setParameter(3, isLast).setParameter(4, isWeekly)
				.setParameter(5, isMonthly).setParameter(6, isYearly).setParameter(7, isDaily).setParameter(8, isToDate)
				.getResultList();
		List<ItemGroupReporting> list = new ArrayList<ItemGroupReporting>();
		if (resultList != null && resultList.size() > 0) {
			for (Object[] objRow : resultList) {
				ItemGroupReporting revenue = new ItemGroupReporting();
				// if this has primary key not 0

				int i = 0;
				if (objRow[i] != null)
					revenue.setCategoryId(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setCategoryName(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setItemsQty((int) objRow[i]);
				i++;
				if (objRow[i] != null)
					revenue.setTotal(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setSubTotal(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setTax1(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setTax2(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setTax3(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setTax4(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setDiscount(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setGratuity(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setFromDate(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setToDate(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setLocationName(((String) objRow[i]));
				i++;
				if (revenue.getCategoryName() != null) {
					list.add(revenue);
				}

			}
		}

		return list;

	}
	

	public List<OrderSourceGroupwiseReporting> getOrderSouceGroupReporting(EntityManager em, Location location,
			String startDate, String endDate, int isLast, int isWeekly, int isMonthly, int isYearly, int isDaily,
			int isToDate)
			throws Exception, InvalidSessionException, IOException {
	
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_order_source_group_BI(?,?,?,?,?,?,?,?,?)")
					.setParameter(1, location.getBusinessId()).setParameter(2, startDate)
					.setParameter(3, endDate).setParameter(4, isLast).setParameter(5, isWeekly)
					.setParameter(6, isMonthly).setParameter(7, isYearly).setParameter(8, isDaily).setParameter(9, isToDate).getResultList();
			List<OrderSourceGroupwiseReporting> list = new ArrayList<OrderSourceGroupwiseReporting>();
			if (resultList != null && resultList.size() > 0) {
				for (Object[] objRow : resultList) {
					OrderSourceGroupwiseReporting revenue = new OrderSourceGroupwiseReporting();
					// if this has primary key not 0

					int i = 0;
					if (objRow[i] != null)
						revenue.setPointOfServiceCount(((int) objRow[i]));
					i++;
					if (objRow[i] != null)
						revenue.setOrderSourceGroupName(((String) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setAmountPaid(((double) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setFromDate(((String) objRow[i]) + "");
					i++;
					if (objRow[i] != null)
						revenue.setToDate(((String) objRow[i]) + "");
					i++;
					if(revenue.getOrderSourceGroupName()!=null){
						list.add(revenue);
					}

					
				}
			}

			return list;

	}
	
	public List<PaymentMethodwiseReporting> getRevenuePaymentMethodwiseAccountLevelReporting(EntityManager em,  
			String startDate, String endDate, int isLast, int isWeekly, int isMonthly, int isYearly, int isDaily,
			int isToDate) throws Exception, InvalidSessionException, IOException {

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call p_Enterprise_revenue_payment_method_BI(?,?,?,?,?,?,?,?)")
				.setParameter(1, startDate).setParameter(2, endDate).setParameter(3, isLast).setParameter(4, isWeekly)
				.setParameter(5, isMonthly).setParameter(6, isYearly).setParameter(7, isDaily).setParameter(8, isToDate).getResultList();
		List<PaymentMethodwiseReporting> list = new ArrayList<PaymentMethodwiseReporting>();
		if (resultList != null && resultList.size() > 0) {
			for (Object[] objRow : resultList) {
				PaymentMethodwiseReporting revenue = new PaymentMethodwiseReporting();
				// if this has primary key not 0

				int i = 0;
				if (objRow[i] != null)
					revenue.setPaymentMethodTypeName(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setPaymentMethodName((String) objRow[i]);
				i++;
				if (objRow[i] != null)
					revenue.setGuestCount(((int) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setAmountPaid(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setCashTip(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setCardTip(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setFromDate(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenue.setToDate(((String) objRow[i]) + "");
				i++;
				if (objRow[i] != null)
					revenue.setLocationName(((String) objRow[i]));
				i++;
				if (revenue.getPaymentMethodName() != null) {
					list.add(revenue);
				}
			}
		}
		return list;
	}
	public List<DiscountItemReporting> getDiscountItemAccountLevelReporting(EntityManager em,  
			String startDate, String endDate, int isLast, int isWeekly, int isMonthly, int isYearly, int isDaily,
			int isToDate) throws Exception, InvalidSessionException, IOException {

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call p_Enterprise_discount_item_BI(?,?,?,?,?,?,?,?)")
				.setParameter(1, startDate).setParameter(2, endDate).setParameter(3, isLast).setParameter(4, isWeekly)
				.setParameter(5, isMonthly).setParameter(6, isYearly).setParameter(7, isDaily).setParameter(8, isToDate).getResultList();
		List<DiscountItemReporting> list = new ArrayList<DiscountItemReporting>();
		if (resultList != null && resultList.size() > 0) {
			for (Object[] objRow : resultList) {
				DiscountItemReporting revenue = new DiscountItemReporting();
				// if this has primary key not 0

				int i = 0;
				if (objRow[i] != null)
					revenue.setDiscountName(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setPriceDiscount(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setTotal(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setSubTotal(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setDiscountsId(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setItemsQty(((int) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setFromDate(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setToDate(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setLocationName(((String) objRow[i]));
				i++;
				if (revenue.getDiscountName() != null) {
					list.add(revenue);
				}
			}
		}
		return list;
	}
	
	public List<DiscountItemReporting> getDiscountItemLocationLevelReporting(EntityManager em, Location location,
			String startDate, String endDate, int isLast, int isWeekly, int isMonthly, int isYearly, int isDaily,
			int isToDate) throws Exception, InvalidSessionException, IOException {

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call p_discount_item_BI(?,?,?,?,?,?,?,?,?)")
				.setParameter(1, location.getBusinessId()).setParameter(2, startDate).setParameter(3, endDate)
				.setParameter(4, isLast).setParameter(5, isWeekly).setParameter(6, isMonthly).setParameter(7, isYearly)
				.setParameter(8, isDaily).setParameter(9, isToDate).getResultList();
		List<DiscountItemReporting> list = new ArrayList<DiscountItemReporting>();
		if (resultList != null && resultList.size() > 0) {
			for (Object[] objRow : resultList) {
				DiscountItemReporting revenue = new DiscountItemReporting();
				// if this has primary key not 0

				int i = 0;
				if (objRow[i] != null)
					revenue.setDiscountName(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setPriceDiscount(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setTotal(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setSubTotal(((double) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setDiscountsId(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setItemsQty(((int) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setFromDate(((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					revenue.setToDate(((String) objRow[i]));
				i++;
				
				if (revenue.getDiscountName() != null) {
					list.add(revenue);
				}
			}
		}
		return list;
	}
}