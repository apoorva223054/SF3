package com.nirvanaxp.types.entities.reservation;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T18:34:00.965+0530")
@StaticMetamodel(ReservationsScheduleDay.class)
public class ReservationsScheduleDay_ {
	public static volatile SingularAttribute<ReservationsScheduleDay, Integer> id;
	public static volatile SingularAttribute<ReservationsScheduleDay, Date> created;
	public static volatile SingularAttribute<ReservationsScheduleDay, String> createdBy;
	public static volatile SingularAttribute<ReservationsScheduleDay, Integer> daysId;
	public static volatile SingularAttribute<ReservationsScheduleDay, String> status;
	public static volatile SingularAttribute<ReservationsScheduleDay, String> reservationsScheduleId;
	public static volatile SingularAttribute<ReservationsScheduleDay, Date> updated;
	public static volatile SingularAttribute<ReservationsScheduleDay, String> updatedBy;
}
