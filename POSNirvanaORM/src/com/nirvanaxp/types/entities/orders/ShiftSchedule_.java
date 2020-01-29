package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-05T11:42:04.150+0530")
@StaticMetamodel(ShiftSchedule.class)
public class ShiftSchedule_ {
	public static volatile SingularAttribute<ShiftSchedule, String> id;
	public static volatile SingularAttribute<ShiftSchedule, String> shiftName;
	public static volatile SingularAttribute<ShiftSchedule, String> fromTime;
	public static volatile SingularAttribute<ShiftSchedule, String> toTime;
	public static volatile SingularAttribute<ShiftSchedule, String> fromDate;
	public static volatile SingularAttribute<ShiftSchedule, String> toDate;
	public static volatile SingularAttribute<ShiftSchedule, String> locationId;
	public static volatile SingularAttribute<ShiftSchedule, Integer> slotTime;
	public static volatile SingularAttribute<ShiftSchedule, Integer> maxOrderAllowed;
	public static volatile SingularAttribute<ShiftSchedule, Integer> holdTime;
	public static volatile SingularAttribute<ShiftSchedule, String> status;
	public static volatile SingularAttribute<ShiftSchedule, Date> created;
	public static volatile SingularAttribute<ShiftSchedule, String> createdBy;
	public static volatile SingularAttribute<ShiftSchedule, Date> updated;
	public static volatile SingularAttribute<ShiftSchedule, String> updatedBy;
	public static volatile SingularAttribute<ShiftSchedule, String> orderSourceGroupId;
}
