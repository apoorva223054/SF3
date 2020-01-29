package com.nirvanaxp.global.types.entities.countries;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the cities database table.
 * 
 */
@Entity
@Table(name="cities")
@NamedQuery(name="City.findAll", query="SELECT c FROM City c")
public class City implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name="city_name")
	private String cityName;

	@Column(name="country_id")
	private int countryId;

	private double latitude;

	private double longitude;

	@Column(name="state_id")
	private int stateId;
	
	
	@Column(name="status")
	private String status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created")
	private Date created;

	@Column(name = "created_by")
	private String createdBy;	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated")
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name="is_online_city")
	private int isOnlineCity;
	
	@Column(name="city_image")
	private String cityImage;
	
	public City() {
	}

	public int getIsOnlineCity() {
		return isOnlineCity;
	}

	public void setIsOnlineCity(int isOnlineCity) {
		this.isOnlineCity = isOnlineCity;
	}

	public String getCityImage() {
		return cityImage;
	}

	public void setCityImage(String cityImage) {
		this.cityImage = cityImage;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCityName() {
		return this.cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getCountryId() {
		return this.countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getStateId() {
		return this.stateId;
	}

	public void setStateId(int stateId) {
		this.stateId = stateId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
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

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}


	@Override
	public String toString() {
		return "City [id=" + id + ", cityName=" + cityName + ", countryId="
				+ countryId + ", latitude=" + latitude + ", longitude="
				+ longitude + ", stateId=" + stateId + ", status=" + status
				+ ", created=" + created + ", createdBy=" + createdBy
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", isOnlineCity=" + isOnlineCity + ", cityImage=" + cityImage
				+ "]";
	}

	

}