package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-03T18:39:18.111+0530")
@StaticMetamodel(ShiftSlots.class)
public class ShiftSlots_ {
	public static volatile SingularAttribute<ShiftSlots, Integer> id;
	public static volatile SingularAttribute<ShiftSlots, String> shiftScheduleId;
	public static volatile SingularAttribute<ShiftSlots, String> date;
	public static volatile SingularAttribute<ShiftSlots, String> slotTime;
	public static volatile SingularAttribute<ShiftSlots, Integer> slotInterval;
	public static volatile SingularAttribute<ShiftSlots, Integer> currentlyHoldedClient;
	public static volatile SingularAttribute<ShiftSlots, Integer> currentOrderInSlot;
	public static volatile SingularAttribute<ShiftSlots, Date> created;
	public static volatile SingularAttribute<ShiftSlots, String> createdBy;
	public static volatile SingularAttribute<ShiftSlots, Date> updated;
	public static volatile SingularAttribute<ShiftSlots, String> updatedBy;
	public static volatile SingularAttribute<ShiftSlots, String> status;
	public static volatile SingularAttribute<ShiftSlots, String> locationId;
	public static volatile SingularAttribute<ShiftSlots, Integer> isBlocked;
}
