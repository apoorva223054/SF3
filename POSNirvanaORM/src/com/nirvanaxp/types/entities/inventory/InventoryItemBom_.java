package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T17:49:14.277+0530")
@StaticMetamodel(InventoryItemBom.class)
public class InventoryItemBom_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<InventoryItemBom, String> itemIdFg;
	public static volatile SingularAttribute<InventoryItemBom, String> itemIdRm;
	public static volatile SingularAttribute<InventoryItemBom, String> rmSellableUom;
	public static volatile SingularAttribute<InventoryItemBom, BigDecimal> quantity;
}
