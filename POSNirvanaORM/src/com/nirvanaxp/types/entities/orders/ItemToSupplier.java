package com.nirvanaxp.types.entities.orders;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

@Entity
@Table(name = "item_to_supplier")
@XmlRootElement(name = "item_to_supplier")
public class ItemToSupplier  extends POSNirvanaBaseClass
{

	private static final long serialVersionUID = -3540129585433677644L;

	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "item_id")
	private String itemId;
	
	@Column(name = "primary_supplier_id")
	private String primarySupplierId;
	
	@Column(name = "secondary_supplier_id")
	private String secondarySupplierId;
	
	@Column(name = "tertiary_supplier_id")
	private String tertiarySupplierId;
	
	
	
	

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getItemId() {
		if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	return itemId;}
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}


	

	public String getPrimarySupplierId() {
		if(primarySupplierId != null && (primarySupplierId.length()==0 || primarySupplierId.equals("0"))){return null;}else{	return primarySupplierId;}
	}

	public void setPrimarySupplierId(String primarySupplierId) {
		this.primarySupplierId = primarySupplierId;
	}

	public String getSecondarySupplierId() {
		if(secondarySupplierId != null && (secondarySupplierId.length()==0 || secondarySupplierId.equals("0"))){return null;}else{	return secondarySupplierId;}
	}

	public void setSecondarySupplierId(String secondarySupplierId) {
		this.secondarySupplierId = secondarySupplierId;
	}

	public String getTertiarySupplierId() {
		if(tertiarySupplierId != null && (tertiarySupplierId.length()==0 || tertiarySupplierId.equals("0"))){return null;}else{	return tertiarySupplierId;}
	}

	public void setTertiarySupplierId(String tertiarySupplierId) {
		this.tertiarySupplierId = tertiarySupplierId;
	}

 	public ItemToSupplier getItemToSupplier(ItemToSupplier supplier){
 		ItemToSupplier toSupplier = new ItemToSupplier();
 		toSupplier.setAmount(supplier.getAmount());
 		toSupplier.setCreated(supplier.getCreated());
 		toSupplier.setCreatedBy(supplier.getCreatedBy());
 		toSupplier.setItemId(supplier.getItemId());
 		toSupplier.setPrimarySupplierId(supplier.getPrimarySupplierId());
 		toSupplier.setSecondarySupplierId(supplier.getSecondarySupplierId());
 		toSupplier.setTertiarySupplierId(supplier.getTertiarySupplierId());
 		toSupplier.setUpdated(supplier.getUpdated());
 		toSupplier.setUpdatedBy(supplier.getUpdatedBy());
 		toSupplier.setStatus(supplier.getStatus());
 		return toSupplier;
 	}
}
