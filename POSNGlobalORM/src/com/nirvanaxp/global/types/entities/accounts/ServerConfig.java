/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.accounts;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "server_config")
@XmlRootElement(name = "server_config")
public class ServerConfig implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "name")
	private String name;

	@Column(name = "port")
	private String port;

	@Column(name = "resource")
	private String resource;

	@Column(name = "type")
	private String type;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "location_id")
	private String locationId;
	
	@Column(name = "socket_port")
	private String socketPort;
	
	@Column(name = "show_server_config")
	private int showServerConfig;
	
	@Column(name = "server_config_name")
	private String serverConfigName;
	
	@Column(name = "is_live_server")
	private int isLiveServer;
 
	
	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getPort()
	{
		return port;
	}

	public String getResource()
	{
		return resource;
	}

	public String getType()
	{
		return type;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setPort(String port)
	{
		this.port = port;
	}

	public void setResource(String resource)
	{
		this.resource = resource;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
 

	public long getCreated()
	{
		if (this.created != null)
		{
			return this.created.getTime();
		}
		return 0;
	}



	public long getUpdated()
	{
		if (this.updated != null)
		{
			return this.updated.getTime();
		}
		return 0;
	}
	
	

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	
	public int getIsLiveServer()
	{
		return isLiveServer;
	}

	public void setIsLiveServer(int isLiveServer)
	{
		this.isLiveServer = isLiveServer;
	}

	 

	 
	
	public int getShowServerConfig()
	{
		return showServerConfig;
	}

	public void setShowServerConfig(int showServerConfig)
	{
		this.showServerConfig = showServerConfig;
	}

	public String getServerConfigName()
	{
		return serverConfigName;
	}

	public void setServerConfigName(String serverConfigName)
	{
		this.serverConfigName = serverConfigName;
	}

	public String getSocketPort()
	{
		return socketPort;
	}

	public void setSocketPort(String socketPort)
	{
		this.socketPort = socketPort;
	}

	@Override
	public String toString()
	{
		return "ServerConfig [id=" + id + ", name=" + name + ", port=" + port + ", resource=" + resource + ", type=" + type + ", created=" + created + ", createdBy=" + createdBy + ", updated="
				+ updated + ", updatedBy=" + updatedBy + ", locationId=" + locationId + ", socketPort=" + socketPort + ", showServerConfig=" + showServerConfig + ", serverConfigName="
				+ serverConfigName + ", isLiveServer=" + isLiveServer + "]";
	}

	 
}
