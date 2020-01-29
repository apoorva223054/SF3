package com.nirvanaxp.global.types.entities;

import com.nirvanaxp.global.types.entities.accounts.Account;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:26:04.030+0530")
@StaticMetamodel(User.class)
public class User_ {
	public static volatile SingularAttribute<User, String> id;
	public static volatile SingularAttribute<User, Date> created;
	public static volatile SingularAttribute<User, String> createdBy;
	public static volatile SingularAttribute<User, Date> lastLoginTs;
	public static volatile SingularAttribute<User, String> password;
	public static volatile SingularAttribute<User, String> status;
	public static volatile SingularAttribute<User, Date> updated;
	public static volatile SingularAttribute<User, String> updatedBy;
	public static volatile SingularAttribute<User, String> username;
	public static volatile SingularAttribute<User, String> authPin;
	public static volatile SingularAttribute<User, String> firstName;
	public static volatile SingularAttribute<User, String> lastName;
	public static volatile SingularAttribute<User, String> dateofbirth;
	public static volatile SingularAttribute<User, String> email;
	public static volatile SingularAttribute<User, String> phone;
	public static volatile SingularAttribute<User, String> qrCodePath;
	public static volatile SingularAttribute<User, Integer> countryId;
	public static volatile SetAttribute<User, Address> addressesSet;
	public static volatile SetAttribute<User, Role> rolesSet;
	public static volatile SetAttribute<User, Business> businessesSet;
	public static volatile SetAttribute<User, Account> accountsSet;
}
