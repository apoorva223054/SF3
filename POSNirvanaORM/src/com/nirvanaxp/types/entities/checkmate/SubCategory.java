package com.nirvanaxp.types.entities.checkmate;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "sub_category")
public class SubCategory implements Serializable{

private List<SubSubCategory>subSubCategories;
private String name;
private String id;
public List<SubSubCategory> getSubSubCategories() {
	return subSubCategories;
}
public void setSubSubCategories(List<SubSubCategory> subSubCategories) {
	this.subSubCategories = subSubCategories;
}
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
	return "SubCategory [subSubCategories=" + subSubCategories + ", name="
			+ name + ", id=" + id + "]";
}
}