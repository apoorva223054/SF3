package com.nirvanaxp.global.types.entities;

import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfo;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T15:25:21.117+0530")
@StaticMetamodel(UserSessionHistory.class)
public class UserSessionHistory_ {
	public static volatile SingularAttribute<UserSessionHistory, Integer> id;
	public static volatile SingularAttribute<UserSessionHistory, Integer> merchant_id;
	public static volatile SingularAttribute<UserSessionHistory, Integer> userSessionId;
	public static volatile SingularAttribute<UserSessionHistory, String> user_id;
	public static volatile SingularAttribute<UserSessionHistory, String> schema_name;
	public static volatile SingularAttribute<UserSessionHistory, String> session_id;
	public static volatile SingularAttribute<UserSessionHistory, Integer> usersRolesId;
	public static volatile SingularAttribute<UserSessionHistory, DeviceInfo> deviceInfo;
	public static volatile SingularAttribute<UserSessionHistory, Date> loginTime;
	public static volatile SingularAttribute<UserSessionHistory, Date> logoutTime;
	public static volatile SingularAttribute<UserSessionHistory, String> ipAddress;
	public static volatile SingularAttribute<UserSessionHistory, String> versionInfo;
}
