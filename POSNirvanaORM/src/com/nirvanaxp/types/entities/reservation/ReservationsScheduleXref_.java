package com.nirvanaxp.types.entities.reservation;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-01T09:53:27.180+0530")
@StaticMetamodel(ReservationsScheduleXref.class)
public class ReservationsScheduleXref_ {
	public static volatile SingularAttribute<ReservationsScheduleXref, Integer> id;
	public static volatile SingularAttribute<ReservationsScheduleXref, Date> created;
	public static volatile SingularAttribute<ReservationsScheduleXref, String> createdBy;
	public static volatile SingularAttribute<ReservationsScheduleXref, String> fromTime;
	public static volatile SingularAttribute<ReservationsScheduleXref, String> status;
	public static volatile SingularAttribute<ReservationsScheduleXref, String> reservationsScheduleId;
	public static volatile SingularAttribute<ReservationsScheduleXref, String> toTime;
	public static volatile SingularAttribute<ReservationsScheduleXref, Date> updated;
	public static volatile SingularAttribute<ReservationsScheduleXref, String> updatedBy;
}
