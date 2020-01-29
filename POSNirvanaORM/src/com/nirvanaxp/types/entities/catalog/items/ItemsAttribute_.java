package com.nirvanaxp.types.entities.catalog.items;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-11-15T16:17:17.190+0530")
@StaticMetamodel(ItemsAttribute.class)
public class ItemsAttribute_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<ItemsAttribute, Integer> isActive;
	public static volatile SingularAttribute<ItemsAttribute, BigDecimal> msrPrice;
	public static volatile SingularAttribute<ItemsAttribute, Integer> multiSelect;
	public static volatile SingularAttribute<ItemsAttribute, String> name;
	public static volatile SingularAttribute<ItemsAttribute, BigDecimal> sellingPrice;
	public static volatile SingularAttribute<ItemsAttribute, Integer> sortSequence;
	public static volatile SingularAttribute<ItemsAttribute, String> displayName;
	public static volatile SingularAttribute<ItemsAttribute, String> shortName;
	public static volatile SingularAttribute<ItemsAttribute, String> locationsId;
	public static volatile SingularAttribute<ItemsAttribute, String> imageName;
	public static volatile SingularAttribute<ItemsAttribute, String> hexCodeValues;
	public static volatile SingularAttribute<ItemsAttribute, String> description;
	public static volatile SingularAttribute<ItemsAttribute, String> globalId;
	public static volatile SingularAttribute<ItemsAttribute, Boolean> availability;
	public static volatile SetAttribute<ItemsAttribute, ItemsCharToItemsAttribute> itemsCharToItemsAttributes;
	public static volatile SetAttribute<ItemsAttribute, ItemsAttributeTypeToItemsAttribute> itemsAttributeTypeToItemsAttributes;
	public static volatile SetAttribute<ItemsAttribute, ItemsAttributeToNutritions> nutritionsToItemsAttributes;
	public static volatile SingularAttribute<ItemsAttribute, String> stockUom;
	public static volatile SingularAttribute<ItemsAttribute, String> sellableUom;
	public static volatile SingularAttribute<ItemsAttribute, Integer> isOnlineAttribute;
	public static volatile SingularAttribute<ItemsAttribute, BigDecimal> incentive;
	public static volatile SingularAttribute<ItemsAttribute, String> incentiveId;
	public static volatile SingularAttribute<ItemsAttribute, String> plu;
	public static volatile SingularAttribute<ItemsAttribute, BigDecimal> priceInclusiveTax;
}
