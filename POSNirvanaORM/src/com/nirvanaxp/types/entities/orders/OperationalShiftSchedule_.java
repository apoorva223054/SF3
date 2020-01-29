package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:10.574+0530")
@StaticMetamodel(OperationalShiftSchedule.class)
public class OperationalShiftSchedule_ {
	public static volatile SingularAttribute<OperationalShiftSchedule, String> id;
	public static volatile SingularAttribute<OperationalShiftSchedule, String> shiftName;
	public static volatile SingularAttribute<OperationalShiftSchedule, String> fromTime;
	public static volatile SingularAttribute<OperationalShiftSchedule, String> toTime;
	public static volatile SingularAttribute<OperationalShiftSchedule, String> fromDate;
	public static volatile SingularAttribute<OperationalShiftSchedule, String> toDate;
	public static volatile SingularAttribute<OperationalShiftSchedule, String> locationId;
	public static volatile SingularAttribute<OperationalShiftSchedule, Integer> slotTime;
	public static volatile SingularAttribute<OperationalShiftSchedule, Integer> maxOrderAllowed;
	public static volatile SingularAttribute<OperationalShiftSchedule, Integer> holdTime;
	public static volatile SingularAttribute<OperationalShiftSchedule, String> status;
	public static volatile SingularAttribute<OperationalShiftSchedule, Date> created;
	public static volatile SingularAttribute<OperationalShiftSchedule, String> createdBy;
	public static volatile SingularAttribute<OperationalShiftSchedule, Date> updated;
	public static volatile SingularAttribute<OperationalShiftSchedule, String> updatedBy;
}
