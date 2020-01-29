package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "modifiers")
public class CheckMateModifiers implements Serializable{

	private String name;
	private String id;
	private double price;
	private boolean multiSelect;
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
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public boolean isMultiSelect() {
		return multiSelect;
	}
	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}
	@Override
	public String toString() {
		return "CheckMateModifiers [name=" + name + ", id=" + id + ", price="
				+ price + ", multiSelect=" + multiSelect + "]";
	}
	
	
}
