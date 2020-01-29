package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-08T15:50:25.235+0530")
@StaticMetamodel(Inventory.class)
public class Inventory_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<Inventory, Integer> displaySequence;
	public static volatile SingularAttribute<Inventory, BigDecimal> economicOrderQuantity;
	public static volatile SingularAttribute<Inventory, String> itemId;
	public static volatile SingularAttribute<Inventory, BigDecimal> minimumOrderQuantity;
	public static volatile SingularAttribute<Inventory, String> primarySupplierId;
	public static volatile SingularAttribute<Inventory, String> secondarySupplierId;
	public static volatile SingularAttribute<Inventory, String> tertiarySupplierId;
	public static volatile SingularAttribute<Inventory, String> unitOfMeasurementId;
	public static volatile SingularAttribute<Inventory, BigDecimal> totalAvailableQuanity;
	public static volatile SingularAttribute<Inventory, BigDecimal> totalUsedQuanity;
	public static volatile SingularAttribute<Inventory, Integer> d86Threshold;
	public static volatile SingularAttribute<Inventory, String> locationId;
	public static volatile SingularAttribute<Inventory, Integer> isBelowThreashold;
	public static volatile SingularAttribute<Inventory, Integer> statusId;
	public static volatile SingularAttribute<Inventory, BigDecimal> yieldQuantity;
	public static volatile SingularAttribute<Inventory, BigDecimal> totalReceiveQuantity;
	public static volatile SingularAttribute<Inventory, String> orderDetailItemId;
	public static volatile SingularAttribute<Inventory, String> grnNumber;
	public static volatile SingularAttribute<Inventory, String> localTime;
	public static volatile SingularAttribute<Inventory, BigDecimal> purchasingRate;
}
