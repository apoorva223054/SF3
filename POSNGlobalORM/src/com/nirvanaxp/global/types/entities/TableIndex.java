/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the address database table.
 * 
 */
@Entity
@Table(name = "table_index")
@XmlRootElement(name = "table_index")
public class TableIndex implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private BigInteger id;

	@Column(name = "table_name")
	private String tableName;

	@Column(name = "indexing")
	private BigInteger indexing;
 

	public BigInteger getId()
	{
		return id;
	}
	public void setId(BigInteger id)
	{
		this.id = id;
	}



	public String getTableName()
	{
		return tableName;
	}



	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}



	 



	public BigInteger getIndexing()
	{
		return indexing;
	}



	public void setIndexing(BigInteger indexing)
	{
		this.indexing = indexing;
	}
	@Override
	public String toString()
	{
		return "TableIndex [id=" + id + ", tableName=" + tableName + ", indexing=" + indexing + "]";
	}



	 
 
}
