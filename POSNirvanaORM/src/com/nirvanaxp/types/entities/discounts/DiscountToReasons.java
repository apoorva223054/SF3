/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.discounts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the discounts database table.
 * 
 */
@Entity
@Table(name = "discount_to_reasons")
@XmlRootElement(name = "discount_to_reasons")
public class DiscountToReasons extends POSNirvanaBaseClass
{

	private static final long serialVersionUID = 1L;

	
	@Column(name = "discounts_id", nullable = false)
	private String discountsId;

	@Column(name = "reasons_id")
	private String reasonsId;


	@Override
	public String toString()
	{
		return "DiscountToReasons [discountsId=" + discountsId + ", reasonsId=" + reasonsId + "]";
	}


	public String getDiscountsId() {
		 if(discountsId != null && (discountsId.length()==0 || discountsId.equals("0"))){return null;}else{	return discountsId;}
	}


	public void setDiscountsId(String discountsId) {
		this.discountsId = discountsId;
	}


	public String getReasonsId() {
		 if(reasonsId != null && (reasonsId.length()==0 || reasonsId.equals("0"))){return null;}else{	 if(reasonsId != null && (reasonsId.length()==0 || reasonsId.equals("0"))){return null;}else{	return reasonsId;}}
	}


	public void setReasonsId(String reasonsId) {
		this.reasonsId = reasonsId;
	}


	 
	
	
	
}