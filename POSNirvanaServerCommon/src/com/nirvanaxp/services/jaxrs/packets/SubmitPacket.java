package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.checkmate.Submit;

@XmlRootElement(name = "SubmitPacket")
public class SubmitPacket extends PostPacket {

	Submit submit;

	public Submit getSubmit() {
		return submit;
	}

	public void setSubmit(Submit submit) {
		this.submit = submit;
	}

	@Override
	public String toString() {
		return "SubmitPacket [submit=" + submit + "]";
	}
	
}
