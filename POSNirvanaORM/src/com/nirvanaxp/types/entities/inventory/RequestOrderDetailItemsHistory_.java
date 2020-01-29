package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-08T15:57:42.369+0530")
@StaticMetamodel(RequestOrderDetailItemsHistory.class)
public class RequestOrderDetailItemsHistory_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> itemsId;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> requestTo;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, Integer> statusId;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> requestId;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> purchaseOrderId;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> itemName;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> uomName;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> RequestOrderDetailItemId;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> quantity;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> receivedQuantity;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> balance;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> challanNumber;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> total;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> price;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> tax;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> unitPrice;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> unitPurchasedPrice;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> unitTaxRate;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> allotmentQty;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> inTransitQty;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> priceTax1;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> priceTax2;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> priceTax3;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> priceTax4;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> taxName1;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> taxName2;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> taxName3;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> taxName4;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> taxDisplayName1;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> taxDisplayName2;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> taxDisplayName3;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> taxDisplayName4;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> taxRate1;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> taxRate2;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> taxRate3;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> taxRate4;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> yieldQuantity;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> totalReceiveQuantity;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> localTime;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, Integer> paymentMethodId;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, String> supplierId;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> commission;
	public static volatile SingularAttribute<RequestOrderDetailItemsHistory, BigDecimal> commissionRate;
}
