package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.BatchDetail;

@XmlRootElement(name = "OrderWithBatchDetailPacket")
public class OrderPaymentWithBatchDetailPacket extends PostPacket{

	private String[] orderPaymentIds;
	private String batchId;

	public String[] getOrderPaymentIds() {
		return orderPaymentIds;
	}

	public void setOrderPaymentIds(String[] orderPaymentIds) {
		this.orderPaymentIds = orderPaymentIds;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
}
