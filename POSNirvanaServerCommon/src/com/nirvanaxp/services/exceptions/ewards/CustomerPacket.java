package com.nirvanaxp.services.exceptions.ewards;

public class CustomerPacket {
	private String merchant_id;
	private String customer_key;
	private Customer customer;
	private Transaction transaction;
	public String getMerchant_id() {
		return merchant_id;
	}
	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
	public String getCustomer_key() {
		return customer_key;
	}
	public void setCustomer_key(String customer_key) {
		this.customer_key = customer_key;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Transaction getTransaction() {
		return transaction;
	}
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	@Override
	public String toString() {
		return "CustomerPacket [merchant_id=" + merchant_id + ", customer_key="
				+ customer_key + ", customer=" + customer + ", transaction="
				+ transaction + "]";
	}
	

}
