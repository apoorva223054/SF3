package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "calculate")
public class Calculate implements Serializable{

	private String dining_option;
	private String location_id;
	private List<CheckMateItems> items;
	public String getDining_option() {
		return dining_option;
	}
	public void setDining_option(String dining_option) {
		this.dining_option = dining_option;
	}
	public List<CheckMateItems> getItems() {
		return items;
	}
	public void setItems(List<CheckMateItems> items) {
		this.items = items;
	}
	public String getLocation_id() {
		return location_id;
	}
	public void setLocation_id(String location_id) {
		this.location_id = location_id;
	}
	@Override
	public String toString() {
		return "Calculate [dining_option=" + dining_option + ", items=" + items
				+ "]";
	}
	
	
	

}
