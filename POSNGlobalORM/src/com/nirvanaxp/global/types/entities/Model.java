/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the model database table.
 * 
 */
@Entity
@Table(name = "model")
@XmlRootElement(name = "model")
public class Model implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "api_server")
	private String apiServer;

	@Column(name = "api_version")
	private String apiVersion;

	private String dbpassword;

	private String dbserver;

	private String dburl;

	private String dbuser;

	private String description;

	private String name;

	@Column(name = "region_id")
	private int regionId;

	public Model()
	{
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getApiServer()
	{
		return this.apiServer;
	}

	public void setApiServer(String apiServer)
	{
		this.apiServer = apiServer;
	}

	public String getApiVersion()
	{
		return this.apiVersion;
	}

	public void setApiVersion(String apiVersion)
	{
		this.apiVersion = apiVersion;
	}

	public String getDbpassword()
	{
		return this.dbpassword;
	}

	public void setDbpassword(String dbpassword)
	{
		this.dbpassword = dbpassword;
	}

	public String getDbserver()
	{
		return this.dbserver;
	}

	public void setDbserver(String dbserver)
	{
		this.dbserver = dbserver;
	}

	public String getDburl()
	{
		return this.dburl;
	}

	public void setDburl(String dburl)
	{
		this.dburl = dburl;
	}

	public String getDbuser()
	{
		return this.dbuser;
	}

	public void setDbuser(String dbuser)
	{
		this.dbuser = dbuser;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getRegionId()
	{
		return this.regionId;
	}

	public void setRegionId(int regionId)
	{
		this.regionId = regionId;
	}

	@Override
	public String toString() {
		return "Model [id=" + id + ", apiServer=" + apiServer + ", apiVersion="
				+ apiVersion + ", dbpassword=" + dbpassword + ", dbserver="
				+ dbserver + ", dburl=" + dburl + ", dbuser=" + dbuser
				+ ", description=" + description + ", name=" + name
				+ ", regionId=" + regionId + "]";
	}

}