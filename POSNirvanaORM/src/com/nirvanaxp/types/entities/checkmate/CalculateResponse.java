package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "calculate_response")
public class CalculateResponse implements Serializable{

	private double total;
	private double subtotal;
	private double taxes;
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	public double getTaxes() {
		return taxes;
	}
	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}
	@Override
	public String toString() {
		return "CalculateResponse [total=" + total + ", subtotal=" + subtotal
				+ ", taxes=" + taxes + "]";
	}
	
}
