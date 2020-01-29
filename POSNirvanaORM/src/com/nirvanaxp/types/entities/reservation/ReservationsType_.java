package com.nirvanaxp.types.entities.reservation;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:22.890+0530")
@StaticMetamodel(ReservationsType.class)
public class ReservationsType_ {
	public static volatile SingularAttribute<ReservationsType, Integer> id;
	public static volatile SingularAttribute<ReservationsType, Date> created;
	public static volatile SingularAttribute<ReservationsType, String> createdBy;
	public static volatile SingularAttribute<ReservationsType, String> displayName;
	public static volatile SingularAttribute<ReservationsType, Integer> displaySequence;
	public static volatile SingularAttribute<ReservationsType, String> locationsId;
	public static volatile SingularAttribute<ReservationsType, String> name;
	public static volatile SingularAttribute<ReservationsType, String> status;
	public static volatile SingularAttribute<ReservationsType, Date> updated;
	public static volatile SingularAttribute<ReservationsType, String> updatedBy;
}
