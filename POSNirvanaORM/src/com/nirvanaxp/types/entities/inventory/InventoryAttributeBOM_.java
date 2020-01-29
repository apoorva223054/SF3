package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T17:53:39.964+0530")
@StaticMetamodel(InventoryAttributeBOM.class)
public class InventoryAttributeBOM_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<InventoryAttributeBOM, String> attributeIdFg;
	public static volatile SingularAttribute<InventoryAttributeBOM, String> itemIdRm;
	public static volatile SingularAttribute<InventoryAttributeBOM, String> rmSellableUom;
	public static volatile SingularAttribute<InventoryAttributeBOM, BigDecimal> quantity;
}
