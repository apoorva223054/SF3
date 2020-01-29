package com.nirvanaxp.types.entities.locations;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;


/**
 * The persistent class for the locations_details database table.
 * 
 */
@Entity
@Table(name="locations_details")
@XmlRootElement(name = "locations_details")
public class LocationsDetail  extends POSNirvanaBaseClassWithoutGeneratedIds {
	private static final long serialVersionUID = 1L;

	
	@Column(name="display_name")
	private String displayName;

	@Column(name="global_business_details_id")
	private int globalBusinessDetailsId;

	
	@OneToOne(cascade =
	{ CascadeType.ALL })
	@JoinColumn(name = "location_details_type_id")
	private LocationDetailsType locationDetailsType;


	private String name;


	public LocationsDetail() {
	}

	

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getGlobalBusinessDetailsId() {
		return this.globalBusinessDetailsId;
	}

	public void setGlobalBusinessDetailsId(int globalBusinessDetailsId) {
		this.globalBusinessDetailsId = globalBusinessDetailsId;
	}

	

	public LocationDetailsType getLocationDetailsType() {
		return locationDetailsType;
	}



	public void setLocationDetailsType(LocationDetailsType locationDetailsType) {
		this.locationDetailsType = locationDetailsType;
	}



	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "LocationsDetail [displayName=" + displayName + ", globalBusinessDetailsId=" + globalBusinessDetailsId
				+ ", locationDetailsType=" + locationDetailsType + ", name="
				+ name  + "]";
	}




}