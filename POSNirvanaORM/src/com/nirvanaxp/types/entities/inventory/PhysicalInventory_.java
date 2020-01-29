package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-08T15:19:25.992+0530")
@StaticMetamodel(PhysicalInventory.class)
public class PhysicalInventory_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<PhysicalInventory, String> inventoryId;
	public static volatile SingularAttribute<PhysicalInventory, BigDecimal> quantity;
	public static volatile SingularAttribute<PhysicalInventory, String> date;
	public static volatile SingularAttribute<PhysicalInventory, BigDecimal> actualQuantity;
	public static volatile SingularAttribute<PhysicalInventory, String> locationId;
	public static volatile SingularAttribute<PhysicalInventory, BigDecimal> excessShortage;
	public static volatile SingularAttribute<PhysicalInventory, String> localTime;
	public static volatile SingularAttribute<PhysicalInventory, Integer> reasonId;
}
