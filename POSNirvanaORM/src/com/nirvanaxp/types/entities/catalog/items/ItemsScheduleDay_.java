package com.nirvanaxp.types.entities.catalog.items;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-01T10:56:00.669+0530")
@StaticMetamodel(ItemsScheduleDay.class)
public class ItemsScheduleDay_ {
	public static volatile SingularAttribute<ItemsScheduleDay, Integer> id;
	public static volatile SingularAttribute<ItemsScheduleDay, Date> created;
	public static volatile SingularAttribute<ItemsScheduleDay, String> createdBy;
	public static volatile SingularAttribute<ItemsScheduleDay, Integer> daysId;
	public static volatile SingularAttribute<ItemsScheduleDay, String> status;
	public static volatile SingularAttribute<ItemsScheduleDay, String> itemsScheduleId;
	public static volatile SingularAttribute<ItemsScheduleDay, Date> updated;
	public static volatile SingularAttribute<ItemsScheduleDay, String> updatedBy;
	public static volatile SingularAttribute<ItemsScheduleDay, String> locationId;
}
