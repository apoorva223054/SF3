/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.sms;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the email_template database table.
 * 
 */
@Entity
@Table(name = "sms_predefined_constants")
@NamedQuery(name = "SMSPredefinedConstant.findAll", query = "SELECT e FROM SMSPredefinedConstant e")
public class SMSPredefinedConstant extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;

	@Column(name = "template_id")
	private int template_id;

	
	public SMSPredefinedConstant()
	{
	}


	public int getTemplate_id() {
		return template_id;
	}


	public void setTemplate_id(int template_id) {
		this.template_id = template_id;
	}


	@Override
	public String toString() {
		return "SMSPredefinedConstant [name=" + name + "]";
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

		
}