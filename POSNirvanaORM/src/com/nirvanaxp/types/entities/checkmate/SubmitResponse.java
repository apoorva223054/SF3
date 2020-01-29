package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "submit_response")
public class SubmitResponse implements Serializable{
	
	private String order_id;

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	@Override
	public String toString() {
		return "SubmitResponse [order_id=" + order_id + "]";
	}
	
}
