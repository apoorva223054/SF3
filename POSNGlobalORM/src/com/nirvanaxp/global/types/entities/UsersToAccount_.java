package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:10:04.270+0530")
@StaticMetamodel(UsersToAccount.class)
public class UsersToAccount_ {
	public static volatile SingularAttribute<UsersToAccount, Integer> id;
	public static volatile SingularAttribute<UsersToAccount, Integer> accountsId;
	public static volatile SingularAttribute<UsersToAccount, Date> created;
	public static volatile SingularAttribute<UsersToAccount, String> createdBy;
	public static volatile SingularAttribute<UsersToAccount, Date> updated;
	public static volatile SingularAttribute<UsersToAccount, String> updatedBy;
	public static volatile SingularAttribute<UsersToAccount, String> usersId;
}
