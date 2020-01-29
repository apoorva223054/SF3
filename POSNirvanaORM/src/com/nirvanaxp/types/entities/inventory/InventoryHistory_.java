package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T18:00:06.858+0530")
@StaticMetamodel(InventoryHistory.class)
public class InventoryHistory_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<InventoryHistory, BigDecimal> economicOrderQuantity;
	public static volatile SingularAttribute<InventoryHistory, String> itemId;
	public static volatile SingularAttribute<InventoryHistory, String> inventoryId;
	public static volatile SingularAttribute<InventoryHistory, BigDecimal> minimumOrderQuantity;
	public static volatile SingularAttribute<InventoryHistory, BigDecimal> totalAvailableQuanity;
	public static volatile SingularAttribute<InventoryHistory, BigDecimal> totalUsedQuanity;
	public static volatile SingularAttribute<InventoryHistory, Integer> d86Threshold;
	public static volatile SingularAttribute<InventoryHistory, Integer> isBelowThreashold;
	public static volatile SingularAttribute<InventoryHistory, BigDecimal> usedQuantity;
	public static volatile SingularAttribute<InventoryHistory, Integer> inventoryStatusId;
	public static volatile SingularAttribute<InventoryHistory, BigDecimal> yieldQuantity;
	public static volatile SingularAttribute<InventoryHistory, BigDecimal> totalReceiveQuantity;
	public static volatile SingularAttribute<InventoryHistory, String> localTime;
	public static volatile SingularAttribute<InventoryHistory, BigDecimal> purchasingRate;
	public static volatile SingularAttribute<InventoryHistory, String> orderDetailItemId;
	public static volatile SingularAttribute<InventoryHistory, String> grnNumber;
}
