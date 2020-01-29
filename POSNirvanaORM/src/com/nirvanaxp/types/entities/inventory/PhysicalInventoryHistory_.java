package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-08T15:20:07.447+0530")
@StaticMetamodel(PhysicalInventoryHistory.class)
public class PhysicalInventoryHistory_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<PhysicalInventoryHistory, String> inventoryId;
	public static volatile SingularAttribute<PhysicalInventoryHistory, String> physicalInventoryId;
	public static volatile SingularAttribute<PhysicalInventoryHistory, BigDecimal> quantity;
	public static volatile SingularAttribute<PhysicalInventoryHistory, String> date;
	public static volatile SingularAttribute<PhysicalInventoryHistory, BigDecimal> actualQuantity;
	public static volatile SingularAttribute<PhysicalInventoryHistory, String> locationId;
	public static volatile SingularAttribute<PhysicalInventoryHistory, BigDecimal> excessShortage;
	public static volatile SingularAttribute<PhysicalInventoryHistory, String> localTime;
	public static volatile SingularAttribute<PhysicalInventoryHistory, Integer> reasonId;
}
