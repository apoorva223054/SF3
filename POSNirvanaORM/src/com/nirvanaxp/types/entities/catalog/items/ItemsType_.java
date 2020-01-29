package com.nirvanaxp.types.entities.catalog.items;

import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:23.518+0530")
@StaticMetamodel(ItemsType.class)
public class ItemsType_ {
	public static volatile SingularAttribute<ItemsType, Integer> id;
	public static volatile SingularAttribute<ItemsType, Timestamp> created;
	public static volatile SingularAttribute<ItemsType, String> createdBy;
	public static volatile SingularAttribute<ItemsType, String> displayName;
	public static volatile SingularAttribute<ItemsType, Integer> displaySequence;
	public static volatile SingularAttribute<ItemsType, String> name;
	public static volatile SingularAttribute<ItemsType, String> status;
	public static volatile SingularAttribute<ItemsType, Timestamp> updated;
	public static volatile SingularAttribute<ItemsType, String> updatedBy;
}
