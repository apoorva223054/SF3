package com.nirvanaxp.types.entities.orders;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T12:19:30.528+0530")
@StaticMetamodel(OrderDetailAttribute.class)
public class OrderDetailAttribute_ {
	public static volatile SingularAttribute<OrderDetailAttribute, String> id;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> amountPaid;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> balanceDue;
	public static volatile SingularAttribute<OrderDetailAttribute, Date> created;
	public static volatile SingularAttribute<OrderDetailAttribute, String> createdBy;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> gratuity;
	public static volatile SingularAttribute<OrderDetailAttribute, String> itemsAttributeId;
	public static volatile SingularAttribute<OrderDetailAttribute, String> itemsId;
	public static volatile SingularAttribute<OrderDetailAttribute, Integer> itemQty;
	public static volatile SingularAttribute<OrderDetailAttribute, Integer> orderDetailStatusId;
	public static volatile SingularAttribute<OrderDetailAttribute, String> itemsAttributeName;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> priceDiscount;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> priceExtended;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> priceGratuity;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> priceMsrp;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> priceSelling;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> priceTax;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> priceTax1;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> priceTax2;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> priceTax3;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> serviceTax;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> subTotal;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> total;
	public static volatile SingularAttribute<OrderDetailAttribute, Date> updated;
	public static volatile SingularAttribute<OrderDetailAttribute, String> updatedBy;
	public static volatile SingularAttribute<OrderDetailAttribute, String> orderDetailItemId;
	public static volatile SingularAttribute<OrderDetailAttribute, BigDecimal> roundOffTotal;
	public static volatile SingularAttribute<OrderDetailAttribute, String> plu;
	public static volatile SingularAttribute<OrderDetailAttribute, String> localTime;
}
