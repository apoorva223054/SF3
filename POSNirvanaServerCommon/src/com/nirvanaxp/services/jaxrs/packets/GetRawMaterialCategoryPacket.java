package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetRawMaterialCategoryPacket")
public class GetRawMaterialCategoryPacket{

	String categoryName;
	int startIndex;
	int endIndex;
    String locationId;
    
	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	@Override
	public String toString() {
		return "GetRawMaterialCategoryPacket [categoryName=" + categoryName + ", startIndex=" + startIndex
				+ ", endIndex=" + endIndex + ", locationId=" + locationId + "]";
	}
	
}
