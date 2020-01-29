package com.nirvanaxp.services.exceptions.ewards;

public class Item {

	private String name;
	private String id;
	private String rate;
	private String quantity;
	private String subtotal;
	private String category;
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
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	@Override
	public String toString() {
		return "Item [name=" + name + ", id=" + id + ", rate=" + rate
				+ ", quantity=" + quantity + ", subtotal=" + subtotal
				+ ", category=" + category + "]";
	}
	
	
}
