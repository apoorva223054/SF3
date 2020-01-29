package com.nirvanaxp.types.entities.checkmate;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "dining_options_group")
public class DiningOptionsGroup {
	private String name;
	List<DiningOptions> diningOptionList;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<DiningOptions> getDiningOptionList() {
		return diningOptionList;
	}
	public void setDiningOptionList(List<DiningOptions> diningOptionList) {
		this.diningOptionList = diningOptionList;
	}
	@Override
	public String toString() {
		return "DiningOptionsGroup [name=" + name + ", diningOptionList="
				+ diningOptionList + "]";
	}
	
}
