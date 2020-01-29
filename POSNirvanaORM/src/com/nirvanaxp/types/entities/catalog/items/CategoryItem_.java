package com.nirvanaxp.types.entities.catalog.items;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-25T14:02:16.821+0530")
@StaticMetamodel(CategoryItem.class)
public class CategoryItem_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<CategoryItem, String> categoryId;
	public static volatile SingularAttribute<CategoryItem, String> itemsId;
	public static volatile SingularAttribute<CategoryItem, Integer> isActive;
	public static volatile SingularAttribute<CategoryItem, Integer> isFeaturedProduct;
	public static volatile SingularAttribute<CategoryItem, Integer> sortSequence;
}
