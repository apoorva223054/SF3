package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T16:28:22.779+0530")
@StaticMetamodel(InventoryItemDefault.class)
public class InventoryItemDefault_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<InventoryItemDefault, BigDecimal> economicOrderQuantity;
	public static volatile SingularAttribute<InventoryItemDefault, String> itemId;
	public static volatile SingularAttribute<InventoryItemDefault, BigDecimal> minimumOrderQuantity;
	public static volatile SingularAttribute<InventoryItemDefault, BigDecimal> d86Threshold;
}
