package com.nirvanaxp.global.types.entities.accounts;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-04T17:30:37.719+0530")
@StaticMetamodel(ServerConfig.class)
public class ServerConfig_ {
	public static volatile SingularAttribute<ServerConfig, Integer> id;
	public static volatile SingularAttribute<ServerConfig, String> name;
	public static volatile SingularAttribute<ServerConfig, String> port;
	public static volatile SingularAttribute<ServerConfig, String> resource;
	public static volatile SingularAttribute<ServerConfig, String> type;
	public static volatile SingularAttribute<ServerConfig, Date> created;
	public static volatile SingularAttribute<ServerConfig, String> createdBy;
	public static volatile SingularAttribute<ServerConfig, Date> updated;
	public static volatile SingularAttribute<ServerConfig, String> updatedBy;
	public static volatile SingularAttribute<ServerConfig, String> locationId;
	public static volatile SingularAttribute<ServerConfig, String> socketPort;
	public static volatile SingularAttribute<ServerConfig, Integer> showServerConfig;
	public static volatile SingularAttribute<ServerConfig, String> serverConfigName;
	public static volatile SingularAttribute<ServerConfig, Integer> isLiveServer;
}
