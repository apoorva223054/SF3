package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sub_sub_category")
public class SubSubCategory implements Serializable{
	private String name;
	private String id;
	private List<CheckMateItems> items;
	
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
	public List<CheckMateItems> getItems() {
		return items;
	}
	public void setItems(List<CheckMateItems> items) {
		this.items = items;
	}
	@Override
	public String toString() {
		return "SubSubCategory [name=" + name + ", id=" + id + ", items="
				+ items + "]";
	}
	
	
	
}
