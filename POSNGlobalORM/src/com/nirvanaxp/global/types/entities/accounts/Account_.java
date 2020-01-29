package com.nirvanaxp.global.types.entities.accounts;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-25T11:30:52.085+0530")
@StaticMetamodel(Account.class)
public class Account_ {
	public static volatile SingularAttribute<Account, Integer> id;
	public static volatile SingularAttribute<Account, String> billingAddressId;
	public static volatile SingularAttribute<Account, Date> created;
	public static volatile SingularAttribute<Account, String> createdBy;
	public static volatile SingularAttribute<Account, String> email;
	public static volatile SingularAttribute<Account, String> firstName;
	public static volatile SingularAttribute<Account, String> lastName;
	public static volatile SingularAttribute<Account, String> logo;
	public static volatile SingularAttribute<Account, String> name;
	public static volatile SingularAttribute<Account, String> shippingAddressId;
	public static volatile SingularAttribute<Account, Date> updated;
	public static volatile SingularAttribute<Account, String> updatedBy;
	public static volatile SingularAttribute<Account, String> website;
	public static volatile SingularAttribute<Account, String> schemaName;
	public static volatile SingularAttribute<Account, Integer> maxAllowedDevices;
	public static volatile SingularAttribute<Account, Integer> isLocalAccount;
	public static volatile SingularAttribute<Account, String> localServerUrl;
	public static volatile SingularAttribute<Account, Integer> isSmsAccount;
	public static volatile SetAttribute<Account, ServerConfig> serverConfigs;
}
