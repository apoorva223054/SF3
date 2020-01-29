package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-06-21T18:19:01.319+0530")
@StaticMetamodel(ClientLog.class)
public class ClientLog_ {
	public static volatile SingularAttribute<ClientLog, Integer> id;
	public static volatile SingularAttribute<ClientLog, String> logLevel;
	public static volatile SingularAttribute<ClientLog, Integer> accountId;
	public static volatile SingularAttribute<ClientLog, String> businessId;
	public static volatile SingularAttribute<ClientLog, String> loggedInUserId;
	public static volatile SingularAttribute<ClientLog, String> pinInUserId;
	public static volatile SingularAttribute<ClientLog, String> sessionId;
	public static volatile SingularAttribute<ClientLog, String> deviceId;
	public static volatile SingularAttribute<ClientLog, String> deviceType;
	public static volatile SingularAttribute<ClientLog, String> remoteIPAddress;
	public static volatile SingularAttribute<ClientLog, String> functionName;
	public static volatile SingularAttribute<ClientLog, String> className;
	public static volatile SingularAttribute<ClientLog, Date> clientLogTime;
	public static volatile SingularAttribute<ClientLog, Date> serverDateTime;
	public static volatile SingularAttribute<ClientLog, String> logString;
	public static volatile SingularAttribute<ClientLog, String> ipAddress;
}
