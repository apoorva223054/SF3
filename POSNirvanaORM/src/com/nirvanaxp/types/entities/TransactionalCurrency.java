/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the transactional_currency database table.
 * 
 */
@Entity
@Table(name = "transactional_currency")
@XmlRootElement(name = "transactional_currency")
public class TransactionalCurrency implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "currency_name", nullable = false, length = 128)
	private String currencyName;

	@Column(name = "display_name", nullable = false, length = 64)
	private String displayName;

	@Column(name = "display_sequence", nullable = false)
	private int displaySequence;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;
	
	@Column(name = "symbol")
	private String symbol;

	public TransactionalCurrency()
	{
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public long getCreated()
	{
		if (this.created != null)
		{
			return this.created.getTime();
		}
		return 0;
	}

	public void setCreated(long created)
	{
		if (created != 0)
		{
			this.created = new Date(created);
		}

	}

	public String getCurrencyName()
	{
		return this.currencyName;
	}

	public void setCurrencyName(String currencyName)
	{
		this.currencyName = currencyName;
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public int getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public long getUpdated()
	{
		if (this.updated != null)
		{
			return this.updated.getTime();
		}
		return 0;
	}

	public void setUpdated(long updated)
	{
		if (updated != 0)
		{
			this.updated = new Date(updated);
		}
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



	public String getSymbol()
	{
		return symbol;
	}

	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}

	@Override
	public String toString()
	{
		return "TransactionalCurrency [id=" + id + ", created=" + created + ", createdBy=" + createdBy + ", currencyName=" + currencyName + ", displayName=" + displayName + ", displaySequence="
				+ displaySequence + ", updated=" + updated + ", updatedBy=" + updatedBy + ", symbol=" + symbol + "]";
	}

	 

}