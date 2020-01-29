/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.accounts;

import java.io.Serializable;
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
@Table(name = "DaylightSavingTime")
@XmlRootElement(name = "daylight_saving_time")
public class DaylightSavingTime implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "country_id")
	private int countryId;

	@Column(name = "from_timezone_id")
	private int fromTimeZoneId;
	
	@Column(name = "to_timezone_id")
	private int toTimeZoneId;

	@Column(name = "execution_time_gmt")
	private String executionTimeGmt;

	public int getId()
	{
		return id;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getFromTimeZoneId() {
		return fromTimeZoneId;
	}

	public void setFromTimeZoneId(int fromTimeZoneId) {
		this.fromTimeZoneId = fromTimeZoneId;
	}

	public int getToTimeZoneId() {
		return toTimeZoneId;
	}

	public void setToTimeZoneId(int toTimeZoneId) {
		this.toTimeZoneId = toTimeZoneId;
	}

	public String getExecutionTimeGmt() {
		return executionTimeGmt;
	}

	public void setExecutionTimeGmt(String executionTimeGmt) {
		this.executionTimeGmt = executionTimeGmt;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "DaylightSavingTime [id=" + id + ", countryId=" + countryId + ", fromTimeZoneId=" + fromTimeZoneId
				+ ", toTimeZoneId=" + toTimeZoneId + ", executionTimeGmt=" + executionTimeGmt + "]";
	}
	
}
