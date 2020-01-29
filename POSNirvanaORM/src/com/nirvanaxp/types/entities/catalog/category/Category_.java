package com.nirvanaxp.types.entities.catalog.category;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T17:05:04.192+0530")
@StaticMetamodel(Category.class)
public class Category_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<Category, String> description;
	public static volatile SingularAttribute<Category, String> iconColour;
	public static volatile SingularAttribute<Category, Integer> isActive;
	public static volatile SingularAttribute<Category, Integer> isDeleted;
	public static volatile SingularAttribute<Category, String> name;
	public static volatile SingularAttribute<Category, String> displayName;
	public static volatile SingularAttribute<Category, String> imageName;
	public static volatile SingularAttribute<Category, Integer> sortSequence;
	public static volatile SingularAttribute<Category, String> locationsId;
	public static volatile SingularAttribute<Category, String> categoryId;
	public static volatile SingularAttribute<Category, Integer> isRealTimeUpdateNeeded;
	public static volatile SingularAttribute<Category, Integer> isUpdateOverridden;
	public static volatile SingularAttribute<Category, Integer> inventoryAccrual;
	public static volatile SingularAttribute<Category, Integer> isinventoryAccrualOverriden;
	public static volatile SingularAttribute<Category, Integer> isOnlineCategory;
	public static volatile SingularAttribute<Category, String> globalCategoryId;
	public static volatile SingularAttribute<Category, Integer> isManualQty;
	public static volatile SingularAttribute<Category, String> itemGroupId;
}
