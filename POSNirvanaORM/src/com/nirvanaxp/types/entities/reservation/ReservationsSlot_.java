package com.nirvanaxp.types.entities.reservation;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-16T15:28:35.818+0530")
@StaticMetamodel(ReservationsSlot.class)
public class ReservationsSlot_ {
	public static volatile SingularAttribute<ReservationsSlot, Integer> id;
	public static volatile SingularAttribute<ReservationsSlot, Date> created;
	public static volatile SingularAttribute<ReservationsSlot, String> createdBy;
	public static volatile SingularAttribute<ReservationsSlot, Date> updated;
	public static volatile SingularAttribute<ReservationsSlot, String> updatedBy;
	public static volatile SingularAttribute<ReservationsSlot, String> status;
	public static volatile SingularAttribute<ReservationsSlot, String> date;
	public static volatile SingularAttribute<ReservationsSlot, String> slotStartTime;
	public static volatile SingularAttribute<ReservationsSlot, String> slotEndTime;
	public static volatile SingularAttribute<ReservationsSlot, Integer> slotTime;
	public static volatile SingularAttribute<ReservationsSlot, String> reservationScheduleId;
	public static volatile SingularAttribute<ReservationsSlot, Integer> currentlyHoldedClient;
	public static volatile SingularAttribute<ReservationsSlot, Integer> currentReservationInSlot;
	public static volatile SingularAttribute<ReservationsSlot, String> locationId;
	public static volatile SingularAttribute<ReservationsSlot, Integer> isBlocked;
}
