package com.nirvanaxp.types.entities.user;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-26T16:08:18.306+0530")
@StaticMetamodel(UsersToDiscount.class)
public class UsersToDiscount_ {
	public static volatile SingularAttribute<UsersToDiscount, String> id;
	public static volatile SingularAttribute<UsersToDiscount, String> discountId;
	public static volatile SingularAttribute<UsersToDiscount, Integer> numberOfTimeDiscountUsed;
	public static volatile SingularAttribute<UsersToDiscount, String> discountCode;
	public static volatile SingularAttribute<UsersToDiscount, Date> created;
	public static volatile SingularAttribute<UsersToDiscount, String> createdBy;
	public static volatile SingularAttribute<UsersToDiscount, Date> updated;
	public static volatile SingularAttribute<UsersToDiscount, String> updatedBy;
	public static volatile SingularAttribute<UsersToDiscount, String> usersId;
	public static volatile SingularAttribute<UsersToDiscount, String> locationId;
}
