package com.nirvanaxp.types.entities.user;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:23.308+0530")
@StaticMetamodel(StaffMessaging.class)
public class StaffMessaging_ {
	public static volatile SingularAttribute<StaffMessaging, Integer> id;
	public static volatile SingularAttribute<StaffMessaging, String> message;
	public static volatile SingularAttribute<StaffMessaging, Date> created;
	public static volatile SingularAttribute<StaffMessaging, Date> scheduleDateTime;
	public static volatile SingularAttribute<StaffMessaging, String> createdBy;
	public static volatile SingularAttribute<StaffMessaging, Date> updated;
	public static volatile SingularAttribute<StaffMessaging, String> updatedBy;
	public static volatile SingularAttribute<StaffMessaging, String> locationsId;
	public static volatile SingularAttribute<StaffMessaging, String> status;
	public static volatile SingularAttribute<StaffMessaging, String> roleList;
}
