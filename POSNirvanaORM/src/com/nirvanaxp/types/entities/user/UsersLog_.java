package com.nirvanaxp.types.entities.user;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:28.198+0530")
@StaticMetamodel(UsersLog.class)
public class UsersLog_ {
	public static volatile SingularAttribute<UsersLog, Integer> id;
	public static volatile SingularAttribute<UsersLog, Date> created;
	public static volatile SingularAttribute<UsersLog, String> createdBy;
	public static volatile SingularAttribute<UsersLog, Date> loginTime;
	public static volatile SingularAttribute<UsersLog, Date> logoutTime;
	public static volatile SingularAttribute<UsersLog, Date> updated;
	public static volatile SingularAttribute<UsersLog, String> updatedBy;
	public static volatile SingularAttribute<UsersLog, User> user;
}
