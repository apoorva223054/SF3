package com.nirvanaxp.types.entities.reservation;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:11.001+0530")
@StaticMetamodel(ReservationsSchedule.class)
public class ReservationsSchedule_ {
	public static volatile SingularAttribute<ReservationsSchedule, String> id;
	public static volatile SingularAttribute<ReservationsSchedule, Date> created;
	public static volatile SingularAttribute<ReservationsSchedule, String> createdBy;
	public static volatile SingularAttribute<ReservationsSchedule, String> fromDate;
	public static volatile SingularAttribute<ReservationsSchedule, String> fromTime;
	public static volatile SingularAttribute<ReservationsSchedule, String> locationId;
	public static volatile SingularAttribute<ReservationsSchedule, String> shiftName;
	public static volatile SingularAttribute<ReservationsSchedule, String> status;
	public static volatile SingularAttribute<ReservationsSchedule, String> toDate;
	public static volatile SingularAttribute<ReservationsSchedule, String> toTime;
	public static volatile SingularAttribute<ReservationsSchedule, Integer> startWeek;
	public static volatile SingularAttribute<ReservationsSchedule, Integer> endWeek;
	public static volatile SingularAttribute<ReservationsSchedule, Date> updated;
	public static volatile SingularAttribute<ReservationsSchedule, String> updatedBy;
	public static volatile SingularAttribute<ReservationsSchedule, Integer> isReservationsAllowed;
	public static volatile SingularAttribute<ReservationsSchedule, Integer> slotTime;
	public static volatile SingularAttribute<ReservationsSchedule, Integer> reservationAllowed;
	public static volatile SetAttribute<ReservationsSchedule, ReservationsScheduleDay> reservationsScheduleDays;
	public static volatile SetAttribute<ReservationsSchedule, ReservationsScheduleXref> reservationsScheduleXref;
	public static volatile SingularAttribute<ReservationsSchedule, Integer> shiftGroupId;
}
