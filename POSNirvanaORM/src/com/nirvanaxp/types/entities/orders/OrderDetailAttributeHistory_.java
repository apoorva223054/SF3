package com.nirvanaxp.types.entities.orders;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T16:01:06.936+0530")
@StaticMetamodel(OrderDetailAttributeHistory.class)
public class OrderDetailAttributeHistory_ {
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigInteger> id;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> amountPaid;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> balanceDue;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, Date> created;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, String> createdBy;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> gratuity;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, String> itemsAttributeId;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, String> itemsId;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, Integer> itemQty;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, String> itemsAttributeName;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, String> orderDetailAttributeId;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, Integer> orderDetailStatusId;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, String> orderDetailItemsId;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> priceDiscount;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> priceExtended;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> priceGratuity;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> priceMsrp;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> priceSelling;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> priceTax;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> priceTax1;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> priceTax2;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> priceTax3;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> serviceTax;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> subTotal;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> total;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, Date> updated;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, String> updatedBy;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, BigDecimal> roundOffTotal;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, String> plu;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, OrderDetailItemsHistory> orderDetailItemsHistories;
	public static volatile SingularAttribute<OrderDetailAttributeHistory, String> localTime;
}
