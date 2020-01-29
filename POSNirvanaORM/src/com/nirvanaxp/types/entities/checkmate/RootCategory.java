package com.nirvanaxp.types.entities.checkmate;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root_category")
public class RootCategory {
	private List<SubCategory>subCategories;
	private String name;
	private String id;
	public List<SubCategory> getSubCategories() {
		return subCategories;
	}
	public void setSubCategories(List<SubCategory> subCategories) {
		this.subCategories = subCategories;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "RootCategory [subCategories=" + subCategories + ", name="
				+ name + ", id=" + id + "]";
	}
	
}
