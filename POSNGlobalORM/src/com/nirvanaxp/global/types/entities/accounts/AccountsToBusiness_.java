package com.nirvanaxp.global.types.entities.accounts;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:21.698+0530")
@StaticMetamodel(AccountsToBusiness.class)
public class AccountsToBusiness_ {
	public static volatile SingularAttribute<AccountsToBusiness, Integer> id;
	public static volatile SingularAttribute<AccountsToBusiness, Integer> accountsId;
	public static volatile SingularAttribute<AccountsToBusiness, Integer> businessId;
	public static volatile SingularAttribute<AccountsToBusiness, Date> created;
	public static volatile SingularAttribute<AccountsToBusiness, String> createdBy;
	public static volatile SingularAttribute<AccountsToBusiness, Date> updated;
	public static volatile SingularAttribute<AccountsToBusiness, String> updatedBy;
}
