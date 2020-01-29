package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CategoryIdPacket")
public class CategoryIdPacket extends PostPacket{

	private String[] categoryIds;

	public String[] getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String[] categoryIds) {
		this.categoryIds = categoryIds;
	}

	
}
