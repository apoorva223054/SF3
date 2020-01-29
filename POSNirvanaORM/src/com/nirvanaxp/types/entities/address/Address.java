/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.address;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the address database table.
 * 
 */
@Entity
@Table(name = "address")
@XmlRootElement(name = "address")
public class Address implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Column(nullable = false, length = 500)
	private String address1;

	@Column(length = 500)
	private String address2;

	@Column(name = "country_id")
	private int countryId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(length = 20)
	private String fax;

	@Column(name = "lat_value", length = 64)
	private String latValue;

	@Column(name = "long_value", length = 64)
	private String longValue;

	@Column(nullable = false, length = 20)
	private String phone;

	@Column(nullable = false, length = 50)
	private String state;
	
	@Column(nullable = false, length = 100)
	private String city;
	
	@Column(name = "state_id", nullable = false)
	private Integer stateId;
	
	@Column(name = "city_id", nullable = false)
	private Integer cityId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(nullable = false, length = 10)
	private String zip;

	@Column(name = "is_default_address")
	private int isDefaultAddress;

	public Address()
	{
	}

	public Address(String address1, String address2, String city, int countryId, String createdBy, String fax, String latValue, String longValue, String phone, String state, String updatedBy, String zip,
			String id, Integer stateId, Integer cityId )
	{
		super();
		this.address1 = address1;
		this.address2 = address2;
		this.city = city;
		this.countryId = countryId;
		this.createdBy = createdBy;
		this.fax = fax;
		this.latValue = latValue;
		this.longValue = longValue;
		this.phone = phone;
		this.state = state;
		this.updatedBy = updatedBy;
		this.zip = zip;
		this.id = id;
		this.stateId = stateId;
		this.cityId = cityId;
	}

	public void setAddressByResultSet(Object[] rs, int startCount) throws Exception
	{

		int index = startCount;
		if (rs[index] != null)
		{
			this.setId((String) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setAddress1((String) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setAddress2((String) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setCity((String) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setState((String) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setCountryId((Integer) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setCreated(new Date(((Timestamp) rs[index]).getTime()));
		}
		index++;
		if (rs[index] != null)
		{
			this.setCreatedBy((String) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setUpdated(new Date(((Timestamp) rs[index]).getTime()));
		}
		index++;
		if (rs[index] != null)
		{
			this.setUpdatedBy((String) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setIsDefaultAddress((Integer) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setStateId((Integer) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setCityId((Integer) rs[index]);
		}
	}

 
	public String getId() {
		if (id != null && (id.length() == 0 || id.equals("0"))) {
			return null;
		} else {
			return id;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddress1()
	{
		return this.address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	public String getAddress2()
	{
		return this.address2;
	}

	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	public String getCity()
	{
		return this.city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}


	public String getFax()
	{
		return this.fax;
	}

	public void setFax(String fax)
	{
		this.fax = fax;
	}

	public String getLatValue()
	{
		return this.latValue;
	}

	public void setLatValue(String latValue)
	{
		this.latValue = latValue;
	}

	public String getLongValue()
	{
		return this.longValue;
	}

	public void setLongValue(String longValue)
	{
		this.longValue = longValue;
	}

	public String getPhone()
	{
		return this.phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getState()
	{
		return this.state;
	}

	public void setState(String state)
	{
		this.state = state;
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

	public String getZip()
	{
		return this.zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}

	public int getCountryId()
	{
		return countryId;
	}

	public void setCountryId(int countryId)
	{
		this.countryId = countryId;
	}

	public int getIsDefaultAddress()
	{
		return isDefaultAddress;
	}

	public void setIsDefaultAddress(int isDefaultAddress)
	{
		this.isDefaultAddress = isDefaultAddress;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	
	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	@Override
	public String toString() {
		return "Address [id=" + id + ", address1=" + address1 + ", address2="
				+ address2 + ", countryId=" + countryId + ", created="
				+ created + ", createdBy=" + createdBy + ", fax=" + fax
				+ ", latValue=" + latValue + ", longValue=" + longValue
				+ ", phone=" + phone + ", state=" + state + ", city=" + city
				+ ", stateId=" + stateId + ", cityId=" + cityId + ", updated="
				+ updated + ", updatedBy=" + updatedBy + ", zip=" + zip
				+ ", isDefaultAddress=" + isDefaultAddress + "]";
	}


}
