package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "payment")
public class Payment implements Serializable{
	
	private String payment_option;
	private double amount;
	private double tip;
	
	
	public String getPayment_option() {
		return payment_option;
	}


	public void setPayment_option(String payment_option) {
		this.payment_option = payment_option;
	}


	public double getAmount() {
		return amount;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}


	public double getTip() {
		return tip;
	}


	public void setTip(double tip) {
		this.tip = tip;
	}


	@Override
	public String toString() {
		return "Payment [payment_option=" + payment_option + ", amount="
				+ amount + ", tip=" + tip + "]";
	}
	
	
}
