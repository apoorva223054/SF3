package com.nirvanaxp.types.entities.inventory;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

@Entity
@Table(name = "goods_receive_notes")
@XmlRootElement(name = "goods_receive_notes")
public class GoodsReceiveNotes extends POSNirvanaBaseClassWithoutGeneratedIds {
	private static final long serialVersionUID = 1L;

	@Column(name = "request_order_details_item_id")
	private String requestOrderDetailsItemId;

	@Column(name = "grn_number")
	private String grnNumber;

	@Column(name = "rate")
	private BigDecimal rate;

	@Column(name = "received_quantity")
	private BigDecimal receivedQuantity;

	@Column(name = "balance")
	private BigDecimal balance;

	@Column(name = "tax")
	private BigDecimal tax;

	@Column(name = "total")
	private BigDecimal total;

	@Column(name = "price")
	private BigDecimal price;

	@Column(name = "is_allotment")
	private int isAllotment;

	@Column(name = "allotment_qty")
	private BigDecimal allotmentQty;

	@Column(name = "is_grn_close")
	private int isGRNClose;

	@Column(name = "date")
	private String date;

	@Column(name = "grn_date")
	private String grnDate;

	@Column(name = "unit_price")
	private BigDecimal unitPrice;

	@Column(name = "unit_purchased_price")
	private BigDecimal unitPurchasedPrice;

	@Column(name = "unit_tax_rate")
	private BigDecimal unitTaxRate;

	transient String requestOrderDetailItemName;
	transient String uomName;
	transient int statusId;
	transient BigDecimal quantity;

	@Column(name = "supplier_ref_no")
	private String supplierRefNo;

	@Column(name = "local_time")
	private String localTime;

	@Column(name = "department_id")
	private String departmentId;

	public String getLocalTime() {
		return localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getRequestOrderDetailItemName() {
		return requestOrderDetailItemName;
	}

	public void setRequestOrderDetailItemName(String requestOrderDetailItemName) {
		this.requestOrderDetailItemName = requestOrderDetailItemName;
	}

	public String getUomName() {
		return uomName;
	}

	public void setUomName(String uomName) {
		this.uomName = uomName;
	}

	public String getRequestOrderDetailsItemId() {
		if (requestOrderDetailsItemId != null
				&& (requestOrderDetailsItemId.length() == 0 || requestOrderDetailsItemId.equals("0"))) {
			return null;
		} else {
			return requestOrderDetailsItemId;
		}
	}

	public void setRequestOrderDetailsItemId(String requestOrderDetailsItemId) {
		this.requestOrderDetailsItemId = requestOrderDetailsItemId;
	}

	public String getGrnNumber() {
		if (grnNumber != null && (grnNumber.length() == 0 || grnNumber.equals("0"))) {
			return null;
		} else {
			return grnNumber;
		}
	}

	public void setGrnNumber(String grnNumber) {
		this.grnNumber = grnNumber;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getReceivedQuantity() {
		return receivedQuantity;
	}

	public void setReceivedQuantity(BigDecimal receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public int getIsAllotment() {
		return isAllotment;
	}

	public void setIsAllotment(int isAllotment) {
		this.isAllotment = isAllotment;
	}

	public BigDecimal getAllotmentQty() {
		return allotmentQty;
	}

	public void setAllotmentQty(BigDecimal allotmentQty) {
		this.allotmentQty = allotmentQty;
	}

	public int getIsGRNClose() {
		return isGRNClose;
	}

	public void setIsGRNClose(int isGRNClose) {
		this.isGRNClose = isGRNClose;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getUnitPurchasedPrice() {
		return unitPurchasedPrice;
	}

	public void setUnitPurchasedPrice(BigDecimal unitPurchasedPrice) {
		this.unitPurchasedPrice = unitPurchasedPrice;
	}

	public BigDecimal getUnitTaxRate() {
		return unitTaxRate;
	}

	public void setUnitTaxRate(BigDecimal unitTaxRate) {
		this.unitTaxRate = unitTaxRate;
	}

	public String getSupplierRefNo() {
		return supplierRefNo;
	}

	public void setSupplierRefNo(String supplierRefNo) {
		this.supplierRefNo = supplierRefNo;
	}

	public String getGrnDate() {
		return grnDate;
	}

	public void setGrnDate(String grnDate) {
		this.grnDate = grnDate;
	}

	@Override
	public String toString() {
		return "GoodsReceiveNotes [requestOrderDetailsItemId=" + requestOrderDetailsItemId + ", grnNumber=" + grnNumber
				+ ", rate=" + rate + ", receivedQuantity=" + receivedQuantity + ", balance=" + balance + ", tax=" + tax
				+ ", total=" + total + ", price=" + price + ", isAllotment=" + isAllotment + ", allotmentQty="
				+ allotmentQty + ", isGRNClose=" + isGRNClose + ", date=" + date + ", grnDate=" + grnDate
				+ ", unitPrice=" + unitPrice + ", unitPurchasedPrice=" + unitPurchasedPrice + ", unitTaxRate="
				+ unitTaxRate + ", supplierRefNo=" + supplierRefNo + ", localTime=" + localTime + ", departmentId="
				+ departmentId + "]";
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	
	

}
