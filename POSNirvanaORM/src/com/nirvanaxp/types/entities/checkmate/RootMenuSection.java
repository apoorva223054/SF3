package com.nirvanaxp.types.entities.checkmate;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root_menu_sections")
public class RootMenuSection {
	private String name;
	private String id;
	private List<MenuSections> menu_sections;
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
	public List<MenuSections> getMenu_sections() {
		return menu_sections;
	}
	public void setMenu_sections(List<MenuSections> menu_sections) {
		this.menu_sections = menu_sections;
	}
	@Override
	public String toString() {
		return "RootMenuSection [name=" + name + ", id=" + id
				+ ", menu_sections=" + menu_sections + "]";
	}
	

}
