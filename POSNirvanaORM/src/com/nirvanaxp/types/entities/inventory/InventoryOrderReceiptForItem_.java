package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T17:15:38.143+0530")
@StaticMetamodel(InventoryOrderReceiptForItem.class)
public class InventoryOrderReceiptForItem_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<InventoryOrderReceiptForItem, String> itemId;
	public static volatile SingularAttribute<InventoryOrderReceiptForItem, Integer> inventoryOrderReceiptId;
	public static volatile SingularAttribute<InventoryOrderReceiptForItem, String> locationId;
	public static volatile SingularAttribute<InventoryOrderReceiptForItem, BigDecimal> purchasedQuantity;
	public static volatile SingularAttribute<InventoryOrderReceiptForItem, String> sellByDate;
	public static volatile SingularAttribute<InventoryOrderReceiptForItem, String> unitOfMeasure;
	public static volatile SingularAttribute<InventoryOrderReceiptForItem, BigDecimal> priceMsrp;
}
