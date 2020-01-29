package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-01T15:10:20.147+0530")
@StaticMetamodel(OrderSourceGroupToShiftSchedule.class)
public class OrderSourceGroupToShiftSchedule_ {
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, Integer> id;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, String> shiftScheduleId;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, String> orderSourceGroupId;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, String> fromTime;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, String> toTime;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, String> fromDate;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, String> toDate;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, Date> created;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, String> createdBy;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, Date> updated;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, String> updatedBy;
	public static volatile SingularAttribute<OrderSourceGroupToShiftSchedule, String> status;
}
