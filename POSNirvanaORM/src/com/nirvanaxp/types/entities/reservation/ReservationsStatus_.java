package com.nirvanaxp.types.entities.reservation;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T18:05:59.148+0530")
@StaticMetamodel(ReservationsStatus.class)
public class ReservationsStatus_ {
	public static volatile SingularAttribute<ReservationsStatus, String> id;
	public static volatile SingularAttribute<ReservationsStatus, Date> created;
	public static volatile SingularAttribute<ReservationsStatus, String> createdBy;
	public static volatile SingularAttribute<ReservationsStatus, String> displayName;
	public static volatile SingularAttribute<ReservationsStatus, Integer> displaySequence;
	public static volatile SingularAttribute<ReservationsStatus, String> hexCodeValues;
	public static volatile SingularAttribute<ReservationsStatus, String> locationsId;
	public static volatile SingularAttribute<ReservationsStatus, String> name;
	public static volatile SingularAttribute<ReservationsStatus, Integer> showToCustomer;
	public static volatile SingularAttribute<ReservationsStatus, Date> updated;
	public static volatile SingularAttribute<ReservationsStatus, String> updatedBy;
	public static volatile SingularAttribute<ReservationsStatus, String> status;
	public static volatile SingularAttribute<ReservationsStatus, Integer> isServerDriven;
	public static volatile SingularAttribute<ReservationsStatus, String> description;
	public static volatile SingularAttribute<ReservationsStatus, Integer> isSendSms;
	public static volatile SingularAttribute<ReservationsStatus, Integer> templateId;
}
