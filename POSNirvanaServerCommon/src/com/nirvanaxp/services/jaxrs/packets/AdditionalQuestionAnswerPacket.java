package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.AdditionalQuestionAnswer;
import com.nirvanaxp.types.entities.orders.OrderToServerAssignment;

@XmlRootElement(name = "AdditionalQuestionAnswerPacket")
public class AdditionalQuestionAnswerPacket extends PostPacket{
	
	public List<AdditionalQuestionAnswer> additionalQuestionAnswersList ;
	public List<OrderToServerAssignment> orderToServerAssignmentList ;
	public List<AdditionalQuestionAnswer> getAdditionalQuestionAnswersList() {
		return additionalQuestionAnswersList;
	}
	public void setAdditionalQuestionAnswersList(
			List<AdditionalQuestionAnswer> additionalQuestionAnswersList) {
		this.additionalQuestionAnswersList = additionalQuestionAnswersList;
	}
	public List<OrderToServerAssignment> getOrderToServerAssignmentList() {
		return orderToServerAssignmentList;
	}
	public void setOrderToServerAssignmentList(
			List<OrderToServerAssignment> orderToServerAssignmentList) {
		this.orderToServerAssignmentList = orderToServerAssignmentList;
	}
	@Override
	public String toString() {
		return "AdditionalQuestionAnswerPacket [additionalQuestionAnswersList="
				+ additionalQuestionAnswersList
				+ ", orderToServerAssignmentList="
				+ orderToServerAssignmentList
				+ ", getAdditionalQuestionAnswersList()="
				+ getAdditionalQuestionAnswersList()
				+ ", getOrderToServerAssignmentList()="
				+ getOrderToServerAssignmentList() + ", getMerchantId()="
				+ getMerchantId() + ", getClientId()=" + getClientId()
				+ ", getLocationId()=" + getLocationId() + ", getEchoString()="
				+ getEchoString() + ", getSchemaName()=" + getSchemaName()
				+ ", getSessionId()=" + getSessionId()
				+ ", getIdOfSessionUsedByPacket()="
				+ getIdOfSessionUsedByPacket() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + "]";
	}
	
	
	

}
