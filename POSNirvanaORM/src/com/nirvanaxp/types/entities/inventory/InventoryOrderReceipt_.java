package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T17:12:32.804+0530")
@StaticMetamodel(InventoryOrderReceipt.class)
public class InventoryOrderReceipt_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<InventoryOrderReceipt, String> itemId;
	public static volatile SingularAttribute<InventoryOrderReceipt, String> locationId;
	public static volatile SingularAttribute<InventoryOrderReceipt, String> purchaseOrderSalesId;
	public static volatile SingularAttribute<InventoryOrderReceipt, BigDecimal> purchasedQuantity;
	public static volatile SingularAttribute<InventoryOrderReceipt, String> receivedDate;
	public static volatile SingularAttribute<InventoryOrderReceipt, String> sellByDate;
	public static volatile SingularAttribute<InventoryOrderReceipt, String> supplierId;
	public static volatile SingularAttribute<InventoryOrderReceipt, String> unitOfMeasure;
	public static volatile SingularAttribute<InventoryOrderReceipt, BigDecimal> priceMsrp;
}
