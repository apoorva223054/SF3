package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "items")
public class CheckMateItems implements Serializable{
	
	private String name;
	private String id;
	private double price;
	private int quantity;
	private String special_request;
	//for menu
	private List<ModifierGroups> modifier_groups;
	//for submit
	private List<CheckMateModifiers> modifiers;
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
	
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getSpecial_request() {
		return special_request;
	}
	public void setSpecial_request(String special_request) {
		this.special_request = special_request;
	}
	
	public List<CheckMateModifiers> getModifiers() {
		return modifiers;
	}
	public void setModifiers(List<CheckMateModifiers> modifiers) {
		this.modifiers = modifiers;
	}
	public List<ModifierGroups> getModifier_groups() {
		return modifier_groups;
	}
	public void setModifier_groups(List<ModifierGroups> modifier_groups) {
		this.modifier_groups = modifier_groups;
	}
	@Override
	public String toString() {
		return "CheckMateItems [name=" + name + ", id=" + id + ", price="
				+ price + ", quantity=" + quantity + ", special_request="
				+ special_request + ", modifier_groups=" + modifier_groups
				+ ", modifiers=" + modifiers + "]";
	}
	
	
	
}
