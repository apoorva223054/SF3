package com.nirvanaxp.global.types.entities.accounts;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-02-24T14:10:54.092+0530")
@StaticMetamodel(TempAccount.class)
public class TempAccount_ {
	public static volatile SingularAttribute<TempAccount, Integer> id;
	public static volatile SingularAttribute<TempAccount, String> firstName;
	public static volatile SingularAttribute<TempAccount, String> lastName;
	public static volatile SingularAttribute<TempAccount, String> name;
	public static volatile SingularAttribute<TempAccount, String> email;
	public static volatile SingularAttribute<TempAccount, String> verificationCode;
	public static volatile SingularAttribute<TempAccount, String> phoneNo;
	public static volatile SingularAttribute<TempAccount, String> status;
	public static volatile SingularAttribute<TempAccount, Date> created;
	public static volatile SingularAttribute<TempAccount, Date> updated;
}
