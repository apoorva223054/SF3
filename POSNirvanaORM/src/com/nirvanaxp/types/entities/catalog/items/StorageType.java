package com.nirvanaxp.types.entities.catalog.items;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

@Entity
@Table(name = "storage_type")
@XmlRootElement(name = "storage_type")
public class StorageType extends POSNirvanaBaseClassWithoutGeneratedIds{

	@Column(name = "name")
	private String name;

	@Column(name = "display_name")
	private String displayName;
	
	@Column(name = "disclaimer")
	private String disclaimer;
	
	@Column(name = "use_before")
	private String useBefore;
	
	@Column(name = "should_display")
	private int shouldDisplay;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

	public String getUseBefore() {
		return useBefore;
	}

	public void setUseBefore(String useBefore) {
		this.useBefore = useBefore;
	}

	public int getShouldDisplay() {
		return shouldDisplay;
	}

	public void setShouldDisplay(int shouldDisplay) {
		this.shouldDisplay = shouldDisplay;
	}

	@Override
	public String toString() {
		return "StorageType [name=" + name + ", displayName=" + displayName
				+ ", disclaimer=" + disclaimer + ", useBefore=" + useBefore
				+ ", shouldDisplay=" + shouldDisplay + "]";
	}

	
	

	

}
