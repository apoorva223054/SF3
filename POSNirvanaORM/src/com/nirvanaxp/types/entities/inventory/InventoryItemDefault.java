/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.inventory;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

@Entity
@Table(name = "inventory_item_default")
@XmlRootElement(name = "InventoryItemDefault")
public class InventoryItemDefault extends POSNirvanaBaseClass
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "economic_ordered_qty")
	private BigDecimal economicOrderQuantity;

	@Column(name = "item_id")
	private String itemId;

	@Column(name = "min_ordered_qty")
	private BigDecimal minimumOrderQuantity;

	@Column(name = "86dthreshold")
	private BigDecimal d86Threshold;

	public BigDecimal getEconomicOrderQuantity()
	{
		return economicOrderQuantity;
	}

	public String getItemId()
	{
		if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	return itemId;}
	}

	public BigDecimal getMinimumOrderQuantity()
	{
		return minimumOrderQuantity;
	}

	public BigDecimal getD86Threshold()
	{
		return d86Threshold;
	}

	public void setEconomicOrderQuantity(BigDecimal economicOrderQuantity)
	{
		this.economicOrderQuantity = economicOrderQuantity;
	}

	public void setItemId(String itemId)
	{
		this.itemId = itemId;
	}

	public void setMinimumOrderQuantity(BigDecimal minimumOrderQuantity)
	{
		this.minimumOrderQuantity = minimumOrderQuantity;
	}

	public void setD86Threshold(BigDecimal d86Threshold)
	{
		this.d86Threshold = d86Threshold;
	}

	@Override
	public String toString() {
		return "InventoryItemDefault [economicOrderQuantity="
				+ economicOrderQuantity + ", itemId=" + itemId
				+ ", minimumOrderQuantity=" + minimumOrderQuantity
				+ ", d86Threshold=" + d86Threshold + "]";
	}

}
