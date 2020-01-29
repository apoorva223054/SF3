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
@Table(name = "inventory_attribute_bom")
@XmlRootElement(name = "InventoryAttributeBOM")
public class InventoryAttributeBOM extends POSNirvanaBaseClass
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "attribute_id_fg")
	private String attributeIdFg;

	@Column(name = "item_id_rm")
	private String itemIdRm;

	@Column(name = "rm_sellable_uom")
	private String rmSellableUom;

	@Column(name = "quantity")
	private BigDecimal quantity;

	public InventoryAttributeBOM getInventoryAttributeBOM(InventoryAttributeBOM bom){
		InventoryAttributeBOM b = new InventoryAttributeBOM();
		b.setAttributeIdFg(bom.getAttributeIdFg());
		b.setCreated(bom.getCreated());
		b.setCreatedBy(bom.getCreatedBy());
		b.setItemIdRm(bom.getItemIdRm());
		b.setQuantity(bom.getQuantity());
		b.setRmSellableUom(bom.getRmSellableUom());
		b.setStatus(bom.getStatus());
		b.setUpdated(bom.getUpdated());
		b.setUpdatedBy(bom.getUpdatedBy());
		return b;
	}
	public String getAttributeIdFg()
	{
		return attributeIdFg;
	}

	public void setAttributeIdFg(String attributeIdFg)
	{
		this.attributeIdFg = attributeIdFg;
	}

	public String getItemIdRm()
	{
		 if(itemIdRm != null && (itemIdRm.length()==0 || itemIdRm.equals("0"))){return null;}else{	return itemIdRm;}
	}

	public void setItemIdRm(String itemIdRm)
	{
		this.itemIdRm = itemIdRm;
	}

	public String getRmSellableUom()
	{
		 if(rmSellableUom != null && (rmSellableUom.length()==0 || rmSellableUom.equals("0"))){return null;}else{	return rmSellableUom;}
	}

	public void setRmSellableUom(String rmSellableUom)
	{
		this.rmSellableUom = rmSellableUom;
	}

	public BigDecimal getQuantity()
	{
		return quantity;
	}

	public void setQuantity(BigDecimal quantity)
	{
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "InventoryAttributeBOM [attributeIdFg=" + attributeIdFg
				+ ", itemIdRm=" + itemIdRm + ", rmSellableUom=" + rmSellableUom
				+ ", quantity=" + quantity + "]";
	}

}
