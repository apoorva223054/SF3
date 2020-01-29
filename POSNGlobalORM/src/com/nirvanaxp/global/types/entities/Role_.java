package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-05T11:22:31.180+0530")
@StaticMetamodel(Role.class)
public class Role_ {
	public static volatile SingularAttribute<Role, Integer> id;
	public static volatile SingularAttribute<Role, String> applicationName;
	public static volatile SingularAttribute<Role, Date> created;
	public static volatile SingularAttribute<Role, String> createdBy;
	public static volatile SingularAttribute<Role, String> functionName;
	public static volatile SingularAttribute<Role, String> roleName;
	public static volatile SingularAttribute<Role, String> displayName;
	public static volatile SingularAttribute<Role, String> status;
	public static volatile SingularAttribute<Role, Date> updated;
	public static volatile SingularAttribute<Role, String> updatedBy;
	public static volatile SingularAttribute<Role, Integer> accountId;
}
