package com.nirvanaxp.types.entities.user;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T17:33:39.113+0530")
@StaticMetamodel(UsersToRole.class)
public class UsersToRole_ {
	public static volatile SingularAttribute<UsersToRole, String> id;
	public static volatile SingularAttribute<UsersToRole, Date> created;
	public static volatile SingularAttribute<UsersToRole, String> createdBy;
	public static volatile SingularAttribute<UsersToRole, String> status;
	public static volatile SingularAttribute<UsersToRole, String> primaryRoleInd;
	public static volatile SingularAttribute<UsersToRole, String> rolesId;
	public static volatile SingularAttribute<UsersToRole, Date> updated;
	public static volatile SingularAttribute<UsersToRole, String> updatedBy;
	public static volatile SingularAttribute<UsersToRole, String> usersId;
}
