package com.nirvanaxp.services.exceptions.ewards;

public class Charge {
	
	private String name;
	private String amount;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "Charge [name=" + name + ", amount=" + amount + "]";
	}
	
	
}
