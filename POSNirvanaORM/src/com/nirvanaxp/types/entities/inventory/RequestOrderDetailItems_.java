package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-12-20T18:11:16.590+0530")
@StaticMetamodel(RequestOrderDetailItems.class)
public class RequestOrderDetailItems_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<RequestOrderDetailItems, String> itemsId;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> requestTo;
	public static volatile SingularAttribute<RequestOrderDetailItems, Integer> statusId;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> quantity;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> receivedQuantity;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> balance;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> requestId;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> purchaseOrderId;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> itemName;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> uomName;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> total;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> price;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> tax;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> unitPrice;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> unitPurchasedPrice;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> unitTaxRate;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> allotmentQty;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> inTransitQty;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> priceTax1;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> priceTax2;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> priceTax3;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> priceTax4;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> taxName1;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> taxName2;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> taxName3;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> taxName4;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> taxDisplayName1;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> taxDisplayName2;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> taxDisplayName3;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> taxDisplayName4;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> taxRate1;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> taxRate2;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> taxRate3;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> taxRate4;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> yieldQuantity;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> totalReceiveQuantity;
	public static volatile SingularAttribute<RequestOrderDetailItems, Integer> paymentMethodId;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> supplierId;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> commission;
	public static volatile SingularAttribute<RequestOrderDetailItems, BigDecimal> commissionRate;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> localTime;
	public static volatile SingularAttribute<RequestOrderDetailItems, String> departmentId;
}
