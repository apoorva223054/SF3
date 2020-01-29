/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.recurringPayment;

import java.math.BigDecimal;

import javax.persistence.Column;

public class Discounts {
	
	private String inheritedFromId;
	private BigDecimal amount;
	private String description;
	private int id;
	private String kind;
	private String name;
	private boolean neverExpires;
	private int numberOfCycles;
	private int quantity;
	
	@Column(name = "display_sequence")
	private int displaySequence;

	/**
	 * @return the inheritedFromId
	 */
	public String getInheritedFromId() {
		return inheritedFromId;
	}
	/**
	 * @param inheritedFromId the inheritedFromId to set
	 */
	public void setInheritedFromId(String inheritedFromId) {
		this.inheritedFromId = inheritedFromId;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @param kind the kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the neverExpires
	 */
	public boolean isNeverExpires() {
		return neverExpires;
	}
	/**
	 * @param neverExpires the neverExpires to set
	 */
	public void setNeverExpires(boolean neverExpires) {
		this.neverExpires = neverExpires;
	}
	/**
	 * @return the numberOfCycles
	 */
	public int getNumberOfCycles() {
		return numberOfCycles;
	}
	/**
	 * @param numberOfCycles the numberOfCycles to set
	 */
	public void setNumberOfCycles(int numberOfCycles) {
		this.numberOfCycles = numberOfCycles;
	}
	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getDisplaySequence() {
		return displaySequence;
	}
	public void setDisplaySequence(int displaySequence) {
		this.displaySequence = displaySequence;
	}
	@Override
	public String toString() {
		return "Discounts [inheritedFromId=" + inheritedFromId + ", amount="
				+ amount + ", description=" + description + ", id=" + id
				+ ", kind=" + kind + ", name=" + name + ", neverExpires="
				+ neverExpires + ", numberOfCycles=" + numberOfCycles
				+ ", quantity=" + quantity + ", displaySequence="
				+ displaySequence + "]";
	}
	

	
}
