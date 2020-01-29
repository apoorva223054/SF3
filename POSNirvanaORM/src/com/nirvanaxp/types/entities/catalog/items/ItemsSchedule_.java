package com.nirvanaxp.types.entities.catalog.items;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T10:15:10.428+0530")
@StaticMetamodel(ItemsSchedule.class)
public class ItemsSchedule_ {
	public static volatile SingularAttribute<ItemsSchedule, String> id;
	public static volatile SingularAttribute<ItemsSchedule, Date> created;
	public static volatile SingularAttribute<ItemsSchedule, String> createdBy;
	public static volatile SingularAttribute<ItemsSchedule, String> fromDate;
	public static volatile SingularAttribute<ItemsSchedule, String> fromTime;
	public static volatile SingularAttribute<ItemsSchedule, String> locationId;
	public static volatile SingularAttribute<ItemsSchedule, Integer> priority;
	public static volatile SingularAttribute<ItemsSchedule, String> scheduleName;
	public static volatile SingularAttribute<ItemsSchedule, String> status;
	public static volatile SingularAttribute<ItemsSchedule, String> toDate;
	public static volatile SingularAttribute<ItemsSchedule, String> toTime;
	public static volatile SingularAttribute<ItemsSchedule, Date> updated;
	public static volatile SingularAttribute<ItemsSchedule, String> updatedBy;
	public static volatile SingularAttribute<ItemsSchedule, String> discountId;
	public static volatile SetAttribute<ItemsSchedule, ItemsScheduleDay> itemsScheduleDays;
}
