package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:26.209+0530")
@StaticMetamodel(ShiftSlotActiveClientInfo.class)
public class ShiftSlotActiveClientInfo_ {
	public static volatile SingularAttribute<ShiftSlotActiveClientInfo, Integer> id;
	public static volatile SingularAttribute<ShiftSlotActiveClientInfo, Date> created;
	public static volatile SingularAttribute<ShiftSlotActiveClientInfo, Date> updated;
	public static volatile SingularAttribute<ShiftSlotActiveClientInfo, String> sessionId;
	public static volatile SingularAttribute<ShiftSlotActiveClientInfo, Date> slotHoldStartTime;
	public static volatile SingularAttribute<ShiftSlotActiveClientInfo, String> holdTime;
	public static volatile SingularAttribute<ShiftSlotActiveClientInfo, Integer> shiftSlotId;
	public static volatile SingularAttribute<ShiftSlotActiveClientInfo, Boolean> isShiftMadeByClient;
	public static volatile SingularAttribute<ShiftSlotActiveClientInfo, String> updatedBy;
	public static volatile SingularAttribute<ShiftSlotActiveClientInfo, String> createdBy;
}
