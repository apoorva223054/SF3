package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "menu_sections")
public class MenuSections2 implements Serializable{
	private String name;
	private String id;
	private List<MenuSections2> menu_sectionsList;
	private List<CheckMateItems> items;
	
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
	public List<CheckMateItems> getItems() {
		return items;
	}
	public void setItems(List<CheckMateItems> items) {
		this.items = items;
	}
	
	public List<MenuSections2> getMenu_sections() {
		return menu_sectionsList;
	}
	public void setMenu_sections(List<MenuSections2> menu_sections) {
		this.menu_sectionsList = menu_sections;
	}
	@Override
	public String toString() {
		return "MenuSections [name=" + name + ", id=" + id + ", items=" + items
				+ "]";
	}
	
	
	
}
