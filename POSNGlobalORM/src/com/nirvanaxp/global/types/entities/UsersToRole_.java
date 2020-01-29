package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-13T17:12:57.229+0530")
@StaticMetamodel(UsersToRole.class)
public class UsersToRole_ {
	public static volatile SingularAttribute<UsersToRole, Integer> id;
	public static volatile SingularAttribute<UsersToRole, Date> created;
	public static volatile SingularAttribute<UsersToRole, String> createdBy;
	public static volatile SingularAttribute<UsersToRole, String> primaryRoleInd;
	public static volatile SingularAttribute<UsersToRole, Integer> rolesId;
	public static volatile SingularAttribute<UsersToRole, Date> updated;
	public static volatile SingularAttribute<UsersToRole, String> updatedBy;
	public static volatile SingularAttribute<UsersToRole, String> usersId;
}
