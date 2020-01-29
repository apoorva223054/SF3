package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderAdditionalQuestion;

@XmlRootElement(name = "OrderAdditionalQuestionPacket")
public class OrderAdditionalQuestionsPacket extends PostPacket
{
	public List<OrderAdditionalQuestion> orderAdditionalQuestionsList ;

	public List<OrderAdditionalQuestion> getOrderAdditionalQuestionsList() {
		return orderAdditionalQuestionsList;
	}

	public void setOrderAdditionalQuestionsList(
			List<OrderAdditionalQuestion> orderAdditionalQuestionsList) {
		this.orderAdditionalQuestionsList = orderAdditionalQuestionsList;
	}

	@Override
	public String toString() {
		return "OrderAdditionalQuestionsPacket [orderAdditionalQuestionsList="
				+ orderAdditionalQuestionsList + "]";
	}

	
	
}