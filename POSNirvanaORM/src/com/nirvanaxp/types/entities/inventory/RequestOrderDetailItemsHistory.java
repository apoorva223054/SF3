package com.nirvanaxp.types.entities.inventory;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

@Entity
@Table(name = "request_order_detail_items_history")
@XmlRootElement(name = "request_order_detail_items_history")
public class RequestOrderDetailItemsHistory extends POSNirvanaBaseClass
{
	private static final long serialVersionUID = 1L;

	@Column(name = "items_id")
	private String itemsId;

	@Column(name = "request_to")
	private String requestTo;

	@Column(name = "status_id")
	private int statusId;

	@Column(name = "request_id")
	private String requestId;

	@Column(name = "purchase_order_id")
	private String purchaseOrderId;
	
	@Column(name = "items_name")
	private String itemName;
	
	@Column(name = "uom_name")
	private String uomName;

	@Column(name = "request_order_detail_items_id")
	private String RequestOrderDetailItemId;
	
	@Column(name = "quantity")
	private BigDecimal quantity;
	
	@Column(name = "received_quantity")
	private BigDecimal receivedQuantity;

	@Column(name = "balance")
	private BigDecimal balance;
	
	@Column(name = "challan_number")
	private String challanNumber;
	
	
	@Column(name = "total")
	private BigDecimal total;
	
	@Column(name = "price")
	private BigDecimal price;
	
	@Column(name = "tax")
	private BigDecimal tax;
	
	@Column(name = "unit_price")
	private BigDecimal unitPrice;
	
	@Column(name = "unit_purchased_price")
	private BigDecimal unitPurchasedPrice;
	
	@Column(name = "unit_tax_rate")
	private  BigDecimal unitTaxRate;
	
	@Column(name = "allotment_qty")
	private BigDecimal allotmentQty;
	
	@Column(name = "in_transit_qty")
	private BigDecimal inTransitQty;
	
	@Column(name = "price_tax_1", precision = 10, scale = 2)
	private BigDecimal priceTax1;

	@Column(name = "price_tax_2", precision = 10, scale = 2)
	private BigDecimal priceTax2;

	@Column(name = "price_tax_3", precision = 10, scale = 2)
	private BigDecimal priceTax3;

	@Column(name = "price_tax_4", precision = 10, scale = 2)
	private BigDecimal priceTax4;

	@Column(name = "tax_name_1")
	private String taxName1;

	@Column(name = "tax_name_2")
	private String taxName2;

	@Column(name = "tax_name_3")
	private String taxName3;

	@Column(name = "tax_name_4")
	private String taxName4;

	@Column(name = "tax_display_name_1")
	private String taxDisplayName1;

	@Column(name = "tax_display_name_2")
	private String taxDisplayName2;

	@Column(name = "tax_display_name_3")
	private String taxDisplayName3;

	@Column(name = "tax_display_name_4")
	private String taxDisplayName4;

	@Column(name = "tax_rate_1")
	private BigDecimal taxRate1;

	@Column(name = "tax_rate_2")
	private BigDecimal taxRate2;

	@Column(name = "tax_rate_3")
	private BigDecimal taxRate3;

	@Column(name = "tax_rate_4")
	private BigDecimal taxRate4;

	@Column(name = "yield_quantity")
	private BigDecimal yieldQuantity;
	
	@Column(name = "total_receive_quantity")
	private BigDecimal totalReceiveQuantity;
	
	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "payment_method_id")
	private int paymentMethodId;
	
	@Column(name = "supplier_id")
	private String supplierId;
	
	@Column(name = "commission")
	private BigDecimal commission;
	
	@Column(name = "commission_rate")
	private BigDecimal commissionRate;
	
	public int getPaymentMethodId() {
		return paymentMethodId;
	}

	public void setPaymentMethodId(int paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	public String getSupplierId() {
		 if(supplierId != null && (supplierId.length()==0 || supplierId.equals("0"))){return null;}else{	return supplierId;}
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getUomName() {
		return uomName;
	}

	public void setUomName(String uomName) {
		this.uomName = uomName;
	}

	public String getItemsId() {
		 if(itemsId != null && (itemsId.length()==0 || itemsId.equals("0"))){return null;}else{	return itemsId;}
	}

	public void setItemsId(String itemsId) {
		this.itemsId = itemsId;
	}

	public String getRequestTo() {
		 if(requestTo != null && (requestTo.length()==0 || requestTo.equals("0"))){return null;}else{	return requestTo;}
	}

	public void setRequestTo(String requestTo) {
		this.requestTo = requestTo;
	}

	

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}


	public String getRequestId() {
		 if(requestId != null && (requestId.length()==0 || requestId.equals("0"))){return null;}else{	return requestId;}
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}


	public String getPurchaseOrderId() {
		 if(purchaseOrderId != null && (purchaseOrderId.length()==0 || purchaseOrderId.equals("0"))){return null;}else{	return purchaseOrderId;}
	}

	public void setPurchaseOrderId(String purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public String getRequestOrderDetailItemId() {
		 if(RequestOrderDetailItemId != null && (RequestOrderDetailItemId.length()==0 || RequestOrderDetailItemId.equals("0"))){return null;}else{	return RequestOrderDetailItemId;}
	}

	public void setRequestOrderDetailItemId(String requestOrderDetailItemId) {
		RequestOrderDetailItemId = requestOrderDetailItemId;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
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

	public String getChallanNumber() {
		return challanNumber;
	}

	public void setChallanNumber(String challanNumber) {
		this.challanNumber = challanNumber;
	}

	public BigDecimal getTotal()
	{
		return total;
	}

	public void setTotal(BigDecimal total)
	{
		this.total = total;
	}

	public BigDecimal getPrice()
	{
		return price;
	}

	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}

	public BigDecimal getTax()
	{
		return tax;
	}

	public void setTax(BigDecimal tax)
	{
		this.tax = tax;
	}

	public BigDecimal getUnitPrice()
	{
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice)
	{
		this.unitPrice = unitPrice;
	}

	public BigDecimal getUnitPurchasedPrice()
	{
		return unitPurchasedPrice;
	}

	public void setUnitPurchasedPrice(BigDecimal unitPurchasedPrice)
	{
		this.unitPurchasedPrice = unitPurchasedPrice;
	}

	public BigDecimal getUnitTaxRate()
	{
		return unitTaxRate;
	}

	public void setUnitTaxRate(BigDecimal unitTaxRate)
	{
		this.unitTaxRate = unitTaxRate;
	}

	public BigDecimal getAllotmentQty()
	{
		return allotmentQty;
	}

	public void setAllotmentQty(BigDecimal allotmentQty)
	{
		this.allotmentQty = allotmentQty;
	}

	public BigDecimal getInTransitQty() {
		return inTransitQty;
	}

	public void setInTransitQty(BigDecimal inTransitQty) {
		this.inTransitQty = inTransitQty;
	}

	public BigDecimal getPriceTax1() {
		return priceTax1;
	}

	public void setPriceTax1(BigDecimal priceTax1) {
		this.priceTax1 = priceTax1;
	}

	public BigDecimal getPriceTax2() {
		return priceTax2;
	}

	public void setPriceTax2(BigDecimal priceTax2) {
		this.priceTax2 = priceTax2;
	}

	public BigDecimal getPriceTax3() {
		return priceTax3;
	}

	public void setPriceTax3(BigDecimal priceTax3) {
		this.priceTax3 = priceTax3;
	}

	public BigDecimal getPriceTax4() {
		return priceTax4;
	}

	public void setPriceTax4(BigDecimal priceTax4) {
		this.priceTax4 = priceTax4;
	}

	public String getTaxName1() {
		return taxName1;
	}

	public void setTaxName1(String taxName1) {
		this.taxName1 = taxName1;
	}

	public String getTaxName2() {
		return taxName2;
	}

	public void setTaxName2(String taxName2) {
		this.taxName2 = taxName2;
	}

	public String getTaxName3() {
		return taxName3;
	}

	public void setTaxName3(String taxName3) {
		this.taxName3 = taxName3;
	}

	public String getTaxName4() {
		return taxName4;
	}

	public void setTaxName4(String taxName4) {
		this.taxName4 = taxName4;
	}

	public String getTaxDisplayName1() {
		return taxDisplayName1;
	}

	public void setTaxDisplayName1(String taxDisplayName1) {
		this.taxDisplayName1 = taxDisplayName1;
	}

	public String getTaxDisplayName2() {
		return taxDisplayName2;
	}

	public void setTaxDisplayName2(String taxDisplayName2) {
		this.taxDisplayName2 = taxDisplayName2;
	}

	public String getTaxDisplayName3() {
		return taxDisplayName3;
	}

	public void setTaxDisplayName3(String taxDisplayName3) {
		this.taxDisplayName3 = taxDisplayName3;
	}

	public String getTaxDisplayName4() {
		return taxDisplayName4;
	}

	public void setTaxDisplayName4(String taxDisplayName4) {
		this.taxDisplayName4 = taxDisplayName4;
	}

	public BigDecimal getTaxRate1() {
		return taxRate1;
	}

	public void setTaxRate1(BigDecimal taxRate1) {
		this.taxRate1 = taxRate1;
	}

	public BigDecimal getTaxRate2() {
		return taxRate2;
	}

	public void setTaxRate2(BigDecimal taxRate2) {
		this.taxRate2 = taxRate2;
	}

	public BigDecimal getTaxRate3() {
		return taxRate3;
	}

	public void setTaxRate3(BigDecimal taxRate3) {
		this.taxRate3 = taxRate3;
	}

	public BigDecimal getTaxRate4() {
		return taxRate4;
	}

	public void setTaxRate4(BigDecimal taxRate4) {
		this.taxRate4 = taxRate4;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public BigDecimal getYieldQuantity() {
		return yieldQuantity;
	}

	public void setYieldQuantity(BigDecimal yieldQuantity) {
		this.yieldQuantity = yieldQuantity;
	}

	public BigDecimal getTotalReceiveQuantity() {
		return totalReceiveQuantity;
	}

	public void setTotalReceiveQuantity(BigDecimal totalReceiveQuantity) {
		this.totalReceiveQuantity = totalReceiveQuantity;
	}

	@Override
	public String toString() {
		return "RequestOrderDetailItemsHistory [itemsId=" + itemsId
				+ ", requestTo=" + requestTo + ", statusId=" + statusId
				+ ", requestId=" + requestId + ", purchaseOrderId="
				+ purchaseOrderId + ", itemName=" + itemName + ", uomName="
				+ uomName + ", RequestOrderDetailItemId="
				+ RequestOrderDetailItemId + ", quantity=" + quantity
				+ ", receivedQuantity=" + receivedQuantity + ", balance="
				+ balance + ", challanNumber=" + challanNumber + ", total="
				+ total + ", price=" + price + ", tax=" + tax + ", unitPrice="
				+ unitPrice + ", unitPurchasedPrice=" + unitPurchasedPrice
				+ ", unitTaxRate=" + unitTaxRate + ", allotmentQty="
				+ allotmentQty + ", inTransitQty=" + inTransitQty
				+ ", priceTax1=" + priceTax1 + ", priceTax2=" + priceTax2
				+ ", priceTax3=" + priceTax3 + ", priceTax4=" + priceTax4
				+ ", taxName1=" + taxName1 + ", taxName2=" + taxName2
				+ ", taxName3=" + taxName3 + ", taxName4=" + taxName4
				+ ", taxDisplayName1=" + taxDisplayName1 + ", taxDisplayName2="
				+ taxDisplayName2 + ", taxDisplayName3=" + taxDisplayName3
				+ ", taxDisplayName4=" + taxDisplayName4 + ", taxRate1="
				+ taxRate1 + ", taxRate2=" + taxRate2 + ", taxRate3="
				+ taxRate3 + ", taxRate4=" + taxRate4 + ", yieldQuantity="
				+ yieldQuantity + ", totalReceiveQuantity="
				+ totalReceiveQuantity + ", localTime=" + localTime
				+ ", paymentMethodId=" + paymentMethodId + ", supplierId="
				+ supplierId + ", commission=" + commission
				+ ", commissionRate=" + commissionRate + "]";
	}

	

	

	 
}
