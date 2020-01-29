package com.nirvanaxp.types.entities.user;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:10:05.273+0530")
@StaticMetamodel(UsersToMessaging.class)
public class UsersToMessaging_ {
	public static volatile SingularAttribute<UsersToMessaging, Integer> id;
	public static volatile SingularAttribute<UsersToMessaging, Integer> staffMessagingId;
	public static volatile SingularAttribute<UsersToMessaging, Date> created;
	public static volatile SingularAttribute<UsersToMessaging, String> createdBy;
	public static volatile SingularAttribute<UsersToMessaging, Date> updated;
	public static volatile SingularAttribute<UsersToMessaging, String> updatedBy;
	public static volatile SingularAttribute<UsersToMessaging, String> usersId;
	public static volatile SingularAttribute<UsersToMessaging, String> status;
}
