package com.nirvanaxp.services.exceptions.ewards;

import java.util.List;

public class Transaction {
	
	private String id;
	private String number;
	private String type;
	private String payment_type;
	private String gross_amount;
	private String net_amount;
	private String amount;
	private String discount;
	private String order_time;
	private String online_bill_source;
	private List<Item> items;
	private List<Tax> taxes;
	
	private List<Charge> charges;
	private Redemption redemption;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	public String getGross_amount() {
		return gross_amount;
	}
	public void setGross_amount(String gross_amount) {
		this.gross_amount = gross_amount;
	}
	public String getNet_amount() {
		return net_amount;
	}
	public void setNet_amount(String net_amount) {
		this.net_amount = net_amount;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getOrder_time() {
		return order_time;
	}
	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}
	public String getOnline_bill_source() {
		return online_bill_source;
	}
	public void setOnline_bill_source(String online_bill_source) {
		this.online_bill_source = online_bill_source;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	public List<Tax> getTaxes() {
		return taxes;
	}
	public void setTaxes(List<Tax> taxes) {
		this.taxes = taxes;
	}
	public List<Charge> getCharges() {
		return charges;
	}
	public void setCharges(List<Charge> charges) {
		this.charges = charges;
	}
	public Redemption getRedemption() {
		return redemption;
	}
	public void setRedemption(Redemption redemption) {
		this.redemption = redemption;
	}
	@Override
	public String toString() {
		return "Transaction [id=" + id + ", number=" + number + ", type="
				+ type + ", payment_type=" + payment_type + ", gross_amount="
				+ gross_amount + ", net_amount=" + net_amount + ", amount="
				+ amount + ", discount=" + discount + ", order_time="
				+ order_time + ", online_bill_source=" + online_bill_source
				+ ", items=" + items + ", taxes=" + taxes + ", charges="
				+ charges + ", redemption=" + redemption + "]";
	}
	
	


}
