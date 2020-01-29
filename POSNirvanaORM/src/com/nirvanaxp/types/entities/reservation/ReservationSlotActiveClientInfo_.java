package com.nirvanaxp.types.entities.reservation;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:27.243+0530")
@StaticMetamodel(ReservationSlotActiveClientInfo.class)
public class ReservationSlotActiveClientInfo_ {
	public static volatile SingularAttribute<ReservationSlotActiveClientInfo, Integer> id;
	public static volatile SingularAttribute<ReservationSlotActiveClientInfo, Date> created;
	public static volatile SingularAttribute<ReservationSlotActiveClientInfo, Date> updated;
	public static volatile SingularAttribute<ReservationSlotActiveClientInfo, String> sessionId;
	public static volatile SingularAttribute<ReservationSlotActiveClientInfo, Date> slotHoldStartTime;
	public static volatile SingularAttribute<ReservationSlotActiveClientInfo, Integer> holdTime;
	public static volatile SingularAttribute<ReservationSlotActiveClientInfo, Integer> reservationSlotId;
	public static volatile SingularAttribute<ReservationSlotActiveClientInfo, Boolean> isReservtionMadeByClient;
	public static volatile SingularAttribute<ReservationSlotActiveClientInfo, String> updatedBy;
	public static volatile SingularAttribute<ReservationSlotActiveClientInfo, String> createdBy;
}
