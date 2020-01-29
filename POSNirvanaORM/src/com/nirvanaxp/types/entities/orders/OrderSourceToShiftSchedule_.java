package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-01T14:27:57.655+0530")
@StaticMetamodel(OrderSourceToShiftSchedule.class)
public class OrderSourceToShiftSchedule_ {
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, Integer> id;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, String> shiftScheduleId;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, String> orderSourceId;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, String> fromTime;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, String> toTime;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, String> fromDate;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, String> toDate;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, Date> created;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, String> createdBy;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, Date> updated;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, String> updatedBy;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, String> status;
	public static volatile SingularAttribute<OrderSourceToShiftSchedule, Integer> orderSourceGroupToShiftScheduleId;
}
