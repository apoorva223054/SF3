/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

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


/**
 * The persistent class for the template database table.
 * 
 */
@Entity
@Table(name = "template")
@XmlRootElement(name = "template")
public class Template implements Serializable
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

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	private String dbpassword;

	private String dbserver;

	private String dburl;

	private String dbuser;

	private String description;

	private String name;

	private Integer region;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	public Template()
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

	public long getCreated()
	{
		if (this.created != null)
		{
			return this.created.getTime();
		}
		return 0;
	}

	public void setCreated(long created)
	{
		if (created != 0)
		{
			this.created = new Date(created);
		}

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

	public Integer getRegion()
	{
		return this.region;
	}

	public void setRegion(Integer region)
	{
		this.region = region;
	}

	public long getUpdated()
	{
		if (this.updated != null)
		{
			return this.updated.getTime();
		}
		return 0;
	}
	
	public void setUpdated(long updated)
	{
		if (updated != 0)
		{
			this.updated = new Date(updated);
		}
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

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "Template [id=" + id + ", apiServer=" + apiServer
				+ ", apiVersion=" + apiVersion + ", created=" + created
				+ ", createdBy=" + createdBy + ", dbpassword=" + dbpassword
				+ ", dbserver=" + dbserver + ", dburl=" + dburl + ", dbuser="
				+ dbuser + ", description=" + description + ", name=" + name
				+ ", region=" + region + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + "]";
	}

}