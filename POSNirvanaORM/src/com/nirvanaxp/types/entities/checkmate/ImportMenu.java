package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;
import java.util.List;

public class ImportMenu implements Serializable{

private List<MenuSections>menuSections;
private String name;
private String id;
public List<MenuSections> getMenuSections() {
	return menuSections;
}
public void setMenuSections(List<MenuSections> menuSections) {
	this.menuSections = menuSections;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
@Override
public String toString() {
	return "ImportMenu [menuSections=" + menuSections + ", name=" + name + "]";
}



}
