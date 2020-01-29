package com.nirvanaxp.types.entities.locations;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;



/**
 * The persistent class for the location_details_type database table.
 * 
 */
@Entity
@Table(name="location_details_type")
@XmlRootElement(name = "location_details_type")
public class LocationDetailsType extends POSNirvanaBaseClassWithoutGeneratedIds {
	private static final long serialVersionUID = 1L;

	@Column(name="display_name")
	private String displayName;

	@Column(name="global_business_details_type_id")
	private int globalBusinessDetailsTypeId;

	private String name;

	

	public LocationDetailsType() {
	}

	
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getGlobalBusinessDetailsTypeId() {
		return this.globalBusinessDetailsTypeId;
	}

	public void setGlobalBusinessDetailsTypeId(int globalBusinessDetailsTypeId) {
		this.globalBusinessDetailsTypeId = globalBusinessDetailsTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}