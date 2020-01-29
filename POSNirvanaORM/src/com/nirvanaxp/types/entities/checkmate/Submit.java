package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Submit")
public class Submit implements Serializable{

	private DiningOptions dining_option;
	private String order_notes;
	private String location_id;
	private CustomerInfo customer_info;
	private List<CheckMateItems> items;
	private Payment payment;
	private List<CheckMateDiscounts> discounts;
	private List<ServiceCharges> service_charges;

	public String getOrder_notes() {
		return order_notes;
	}
	public void setOrder_notes(String order_notes) {
		this.order_notes = order_notes;
	}

	public List<CheckMateItems> getItems() {
		return items;
	}
	public void setItems(List<CheckMateItems> items) {
		this.items = items;
	}
	public Payment getPayment() {
		return payment;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	public List<CheckMateDiscounts> getDiscounts() {
		return discounts;
	}
	public void setDiscounts(List<CheckMateDiscounts> discounts) {
		this.discounts = discounts;
	}
	public CustomerInfo getCustomer_info() {
		return customer_info;
	}
	public void setCustomer_info(CustomerInfo customer_info) {
		this.customer_info = customer_info;
	}
	public List<ServiceCharges> getService_charges() {
		return service_charges;
	}
	public void setService_charges(List<ServiceCharges> service_charges) {
		this.service_charges = service_charges;
	}
	public DiningOptions getDining_option() {
		return dining_option;
	}
	public void setDining_option(DiningOptions dining_option) {
		this.dining_option = dining_option;
	}
	public String getLocation_id() {
		return location_id;
	}
	public void setLocation_id(String location_id) {
		this.location_id = location_id;
	}
	@Override
	public String toString() {
		return "Submit [dining_option=" + dining_option + ", order_notes="
				+ order_notes + ", location_id=" + location_id
				+ ", customer_info=" + customer_info + ", items=" + items
				+ ", payment=" + payment + ", discounts=" + discounts
				+ ", service_charges=" + service_charges + "]";
	}
	
	
	
	
	
}
