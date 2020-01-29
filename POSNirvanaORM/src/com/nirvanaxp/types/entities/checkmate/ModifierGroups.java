package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "modifier_groups")
public class ModifierGroups implements Serializable{

	private String name;
	private String id;
	private int is_required;
	private double minimum_amount;
	private boolean amount_free_is_dollars;
	private List<CheckMateModifiers> modifiers;
	
	public List<CheckMateModifiers> getModifiers() {
		return modifiers;
	}
	public void setModifiers(List<CheckMateModifiers> modifiers) {
		this.modifiers = modifiers;
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

	public double getMinimum_amount() {
		return minimum_amount;
	}
	public void setMinimum_amount(double minimum_amount) {
		this.minimum_amount = minimum_amount;
	}
	
	public boolean isAmount_free_is_dollars() {
		return amount_free_is_dollars;
	}
	public void setAmount_free_is_dollars(boolean amount_free_is_dollars) {
		this.amount_free_is_dollars = amount_free_is_dollars;
	}
	public int getIs_required() {
		return is_required;
	}
	public void setIs_required(int is_required) {
		this.is_required = is_required;
	}
	@Override
	public String toString() {
		return "ModifierGroups [name=" + name + ", id=" + id + ", is_required="
				+ is_required + ", minimum_amount=" + minimum_amount
				+ ", amount_free_is_dollars=" + amount_free_is_dollars
				+ ", modifiers=" + modifiers + "]";
	}
	
	
}
