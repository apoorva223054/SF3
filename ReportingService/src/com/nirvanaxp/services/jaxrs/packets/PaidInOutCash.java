package com.nirvanaxp.services.jaxrs.packets;

public class PaidInOutCash {

	private String name;
	private String total;
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "PaidInOutCash [name=" + name + ", total=" + total +"]";
	}	
	
}
