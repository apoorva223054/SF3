/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.DiscountsType;


/**
 * The persistent class for the users_to_address database table.
 * 
 */
@Entity
@Table(name = "users_to_discount")
@XmlRootElement(name = "users_to_discount")
public class UsersToDiscount implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Column(name = "discount_id")
	private String discountId;
	
	@Column(name = "number_of_time_discount_used")
	private int numberOfTimeDiscountUsed;
	
	@Column(name = "discount_code")
	private String discountCode;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "users_id")
	private String usersId;
	
	@Column(name = "location_id")
	private String locationId;
	
	
	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}


	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}


	transient private Discount discount;
	
	transient private DiscountsType discountType;

	public DiscountsType getDiscountType() {
		return discountType;
	}


	public void setDiscountType(DiscountsType discountType) {
		this.discountType = discountType;
	}


	public Discount getDiscount() {
		return discount;
	}


	public void setDiscount(Discount discount) {
		this.discount = discount;
	}


	public UsersToDiscount()
	{
	}

 
	 
	
	public String getId() {
		if (id != null && (id.length() == 0 || id.equals("0"))) {
			return null;
		} else {
			return id;
		}
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}


	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}


	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}


	public String getDiscountId() {
		 if(discountId != null && (discountId.length()==0 || discountId.equals("0"))){return null;}else{	return discountId;}
	}


	public int getNumberOfTimeDiscountUsed() {
		return numberOfTimeDiscountUsed;
	}


	


	public String getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	public String getUpdatedBy() {
		return updatedBy;
	}


	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}


	public void setDiscountId(String discountId) {
		this.discountId = discountId;
	}


	public void setNumberOfTimeDiscountUsed(int numberOfTimeDiscountUsed) {
		this.numberOfTimeDiscountUsed = numberOfTimeDiscountUsed;
	}


	public String getDiscountCode() {
		return discountCode;
	}


	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}


	

 

}