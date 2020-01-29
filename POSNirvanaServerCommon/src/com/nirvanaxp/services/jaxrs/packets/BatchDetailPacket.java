package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.BatchDetail;

@XmlRootElement(name = "BatchDetailPacket")
public class BatchDetailPacket extends PostPacket{

	private BatchDetail batchDetail;

	public BatchDetail getBatchDetail() {
		return batchDetail;
	}

	public void setBatchDetail(BatchDetail batchDetail) {
		this.batchDetail = batchDetail;
	}

}
