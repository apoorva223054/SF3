package com.nirvanaxp.global.types.entities;

import java.sql.Timestamp;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:10:04.201+0530")
@StaticMetamodel(LoggedInUser.class)
public class LoggedInUser_ {
	public static volatile SingularAttribute<LoggedInUser, Integer> id;
	public static volatile SingularAttribute<LoggedInUser, Timestamp> loginTime;
	public static volatile SingularAttribute<LoggedInUser, String> status;
	public static volatile SingularAttribute<LoggedInUser, Date> updated;
	public static volatile SingularAttribute<LoggedInUser, String> updatedBy;
	public static volatile SingularAttribute<LoggedInUser, String> username;
	public static volatile SingularAttribute<LoggedInUser, String> usersId;
}
