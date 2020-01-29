package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dining_options")
public class DiningOptions implements Serializable{

	private String name;
	private String id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "DiningOptions [name=" + name + ", id=" + id + "]";
	}
	
}
