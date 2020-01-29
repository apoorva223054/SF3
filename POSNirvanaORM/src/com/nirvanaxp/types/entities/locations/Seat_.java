package com.nirvanaxp.types.entities.locations;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:24.790+0530")
@StaticMetamodel(Seat.class)
public class Seat_ {
	public static volatile SingularAttribute<Seat, Integer> id;
	public static volatile SingularAttribute<Seat, Date> created;
	public static volatile SingularAttribute<Seat, String> createdBy;
	public static volatile SingularAttribute<Seat, Integer> seatNumber;
	public static volatile SingularAttribute<Seat, Date> updated;
	public static volatile SingularAttribute<Seat, String> updatedBy;
}
