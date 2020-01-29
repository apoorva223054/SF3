package com.nirvanaxp.types.entities.user;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T17:35:05.689+0530")
@StaticMetamodel(UsersToAddress.class)
public class UsersToAddress_ {
	public static volatile SingularAttribute<UsersToAddress, Integer> id;
	public static volatile SingularAttribute<UsersToAddress, String> addressId;
	public static volatile SingularAttribute<UsersToAddress, Date> created;
	public static volatile SingularAttribute<UsersToAddress, String> createdBy;
	public static volatile SingularAttribute<UsersToAddress, Date> updated;
	public static volatile SingularAttribute<UsersToAddress, String> updatedBy;
	public static volatile SingularAttribute<UsersToAddress, String> usersId;
}
