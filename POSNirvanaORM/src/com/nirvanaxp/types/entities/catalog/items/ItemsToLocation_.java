package com.nirvanaxp.types.entities.catalog.items;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-05T13:00:05.857+0530")
@StaticMetamodel(ItemsToLocation.class)
public class ItemsToLocation_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<ItemsToLocation, String> locationId;
	public static volatile SingularAttribute<ItemsToLocation, String> itemsId;
	public static volatile SingularAttribute<ItemsToLocation, BigDecimal> price;
	public static volatile SingularAttribute<ItemsToLocation, String> itemsStatus;
}
