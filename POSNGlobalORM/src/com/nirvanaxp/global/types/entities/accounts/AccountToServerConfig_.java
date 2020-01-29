package com.nirvanaxp.global.types.entities.accounts;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:08.142+0530")
@StaticMetamodel(AccountToServerConfig.class)
public class AccountToServerConfig_ {
	public static volatile SingularAttribute<AccountToServerConfig, Integer> id;
	public static volatile SingularAttribute<AccountToServerConfig, Integer> accountsId;
	public static volatile SingularAttribute<AccountToServerConfig, Integer> serverConfigId;
	public static volatile SingularAttribute<AccountToServerConfig, Date> updated;
	public static volatile SingularAttribute<AccountToServerConfig, Integer> subscriberServerId;
	public static volatile SingularAttribute<AccountToServerConfig, String> authenticationToken;
	public static volatile SingularAttribute<AccountToServerConfig, Integer> isServerUrl;
	public static volatile SingularAttribute<AccountToServerConfig, String> locationId;
}
