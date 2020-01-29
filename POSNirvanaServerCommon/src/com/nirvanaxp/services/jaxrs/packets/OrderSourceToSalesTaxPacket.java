package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.salestax.OrderSourceToSalesTax;

@XmlRootElement(name = "OrderSourceToSalesTaxPacket")
public class OrderSourceToSalesTaxPacket extends PostPacket{
	
	List<OrderSourceToSalesTax> orderSourceToSalesTax	;

	public List<OrderSourceToSalesTax> getOrderSourceToSalesTax() {
		return orderSourceToSalesTax;
	}

	public void setOrderSourceToSalesTax(
			List<OrderSourceToSalesTax> orderSourceToSalesTax) {
		this.orderSourceToSalesTax = orderSourceToSalesTax;
	}

	
}
