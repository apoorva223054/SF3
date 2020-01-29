package com.nirvanaxp.global.types.entities;

import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfo;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-05T11:21:37.620+0530")
@StaticMetamodel(UserSession.class)
public class UserSession_ {
	public static volatile SingularAttribute<UserSession, Integer> id;
	public static volatile SingularAttribute<UserSession, Integer> merchant_id;
	public static volatile SingularAttribute<UserSession, String> user_id;
	public static volatile SingularAttribute<UserSession, String> schema_name;
	public static volatile SingularAttribute<UserSession, String> session_id;
	public static volatile SingularAttribute<UserSession, Integer> usersRolesId;
	public static volatile SingularAttribute<UserSession, DeviceInfo> deviceInfo;
	public static volatile SingularAttribute<UserSession, Date> loginTime;
	public static volatile SingularAttribute<UserSession, Date> logoutTime;
	public static volatile SingularAttribute<UserSession, String> ipAddress;
	public static volatile SingularAttribute<UserSession, String> versionInfo;
	public static volatile SingularAttribute<UserSession, Integer> businessId;
	public static volatile SingularAttribute<UserSession, String> scope;
}
