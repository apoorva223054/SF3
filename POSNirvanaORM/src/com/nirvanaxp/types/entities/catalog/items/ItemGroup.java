package com.nirvanaxp.types.entities.catalog.items;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.locations.Location;

/**
 * The persistent class for the category database table.
 * 
 */
@Entity
@Table(name = "item_group")
@XmlRootElement(name = "item_group")
public class ItemGroup extends POSNirvanaBaseClassWithoutGeneratedIds implements Serializable{
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;
	
	@Column(name = "item_group_id", nullable = false)
	private String itemGroupId;
	
	
	private transient List<Location> locationList;
	

	

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	public ItemGroup() {
	}
	
	public ItemGroup(String id) {
		this.id = id;
	}

	
	public String getName() {
		return this.name;
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

	
	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}
	
	@Column(name = "global_id")
	private String globalId;



	public String getGlobalId() {
		 if(globalId != null && (globalId.length()==0 || globalId.equals("0"))){return null;}else{	return globalId;}
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	@Override
    public boolean equals(Object obj) {
       if(obj instanceof ItemGroup){
    	   if(this.id ==  ((ItemGroup) obj).getId()){
    		   return true;
    	   }
       }
       return false;
    }
	
 
	
	
	

	public String getItemGroupId() {
		 if(itemGroupId != null && (itemGroupId.length()==0 || itemGroupId.equals("0"))){return null;}else{	return itemGroupId;}
	}

	public void setItemGroupId(String itemGroupId) {
		this.itemGroupId = itemGroupId;
	}

	@Override
	public String toString() {
		return "ItemGroup [name=" + name + ", displayName=" + displayName
				+ ", locationsId=" + locationsId + ", itemGroupId="
				+ itemGroupId + ", globalId="
				+ globalId + "]";
	}

	public ItemGroup getItemGroup(ItemGroup itemsGroup){
		ItemGroup i = new ItemGroup();
		i.setCreated(itemsGroup.getCreated());
		i.setCreatedBy(itemsGroup.getCreatedBy());
		i.setDisplayName(itemsGroup.getDisplayName());
		i.setGlobalId(itemsGroup.getGlobalId());
		i.setLocationsId(itemsGroup.getLocationsId());
		i.setName(itemsGroup.getName());
		i.setStatus(itemsGroup.getStatus());
		i.setUpdated(itemsGroup.getUpdated());
		i.setUpdatedBy(itemsGroup.getUpdatedBy());
		i.setItemGroupId(itemsGroup.getItemGroupId());
		return i;
		
	}

	
}