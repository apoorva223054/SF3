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
@Table(name = "inventory_item_bom")
@XmlRootElement(name = "InventoryItemBom")
public class InventoryItemBom extends POSNirvanaBaseClass
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "item_id_fg")
	private String itemIdFg;

	@Column(name = "item_id_rm")
	private String itemIdRm;

	@Column(name = "rm_sellable_uom")
	private String rmSellableUom;

	@Column(name = "quantity")
	private BigDecimal quantity;

	public String getItemIdFg()
	{
		 if(itemIdFg != null && (itemIdFg.length()==0 || itemIdFg.equals("0"))){return null;}else{	return itemIdFg;}
	}

	public String getItemIdRm()
	{
		 if(itemIdRm != null && (itemIdRm.length()==0 || itemIdRm.equals("0"))){return null;}else{	return itemIdRm;}
	}

	public String getRmSellableUom()
	{
		 if(rmSellableUom != null && (rmSellableUom.length()==0 || rmSellableUom.equals("0"))){return null;}else{	return rmSellableUom;}
	}

	public void setItemIdFg(String itemIdFg)
	{
		this.itemIdFg = itemIdFg;
	}

	public void setItemIdRm(String itemIdRm)
	{
		this.itemIdRm = itemIdRm;
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
		return "InventoryItemBom [itemIdFg=" + itemIdFg + ", itemIdRm="
				+ itemIdRm + ", rmSellableUom=" + rmSellableUom + ", quantity="
				+ quantity + "]";
	}
	public InventoryItemBom getInventoryItemBom(InventoryItemBom bom){
		InventoryItemBom b= new InventoryItemBom();
		/*b.setId(bom.getId());*/
		b.setCreated(bom.getCreated());
		b.setCreatedBy(bom.getCreatedBy());
		b.setItemIdFg(bom.getItemIdFg());
		b.setItemIdRm(bom.getItemIdRm());
		b.setQuantity(bom.getQuantity());
		b.setStatus(bom.getStatus());
		b.setUpdated(bom.getUpdated());
		b.setUpdatedBy(bom.getUpdatedBy());
		return b;
				
	}
}
