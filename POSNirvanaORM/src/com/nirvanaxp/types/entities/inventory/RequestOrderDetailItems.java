package com.nirvanaxp.types.entities.inventory;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

@Entity
@Table(name = "request_order_detail_items")
@XmlRootElement(name = "request_order_detail_items")
public class RequestOrderDetailItems extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "items_id")
	private String itemsId;

	@Column(name = "request_to")
	private String requestTo;

	@Column(name = "status_id")
	private int statusId;

	@Column(name = "quantity")
	private BigDecimal quantity;
	
	@Column(name = "received_quantity")
	private BigDecimal receivedQuantity;

	@Column(name = "balance")
	private BigDecimal balance;
	
	@Column(name = "request_id")
	private String requestId;

	@Column(name = "purchase_order_id")
	private String purchaseOrderId;
	
	@Column(name = "item_name")
	private String itemName;
	
	@Column(name = "uom_name")
	private String uomName;
	
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
	 
	private transient BigDecimal itemYieldPercent;
	private transient  BigDecimal availableQuantity;
	
	@Column(name = "payment_method_id")
	private int paymentMethodId;
	
	@Column(name = "supplier_id")
	private String supplierId;
	
	@Column(name = "commission")
	private BigDecimal commission;
	
	@Column(name = "commission_rate")
	private BigDecimal commissionRate;
	
	@Column(name = "local_time")
	private String localTime;
	
	 @Column(name = "department_id")
	 private String departmentId;
	
	private transient BigDecimal distributionPrice;
	

	private transient BigDecimal purchasingRate;
	
	public BigDecimal getPurchasingRate() {
		return purchasingRate;
	}

	public void setPurchasingRate(BigDecimal purchasingRate) {
		this.purchasingRate = purchasingRate;
	}


	

	public BigDecimal getDistributionPrice() {
		return distributionPrice;
	}

	public void setDistributionPrice(BigDecimal distributionPrice) {
		this.distributionPrice = distributionPrice;
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

	public BigDecimal getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(BigDecimal availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public BigDecimal getUnitPrice()
	{
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice)
	{
		this.unitPrice = unitPrice;
	}

	public BigDecimal getUnitTaxRate()
	{
		return unitTaxRate;
	}

	public void setUnitTaxRate(BigDecimal unitTaxRate)
	{
		this.unitTaxRate = unitTaxRate;
	}

	public BigDecimal getUnitPurchasedPrice()
	{
		return unitPurchasedPrice;
	}

	public void setUnitPurchasedPrice(BigDecimal unitPurchasedPrice)
	{
		this.unitPurchasedPrice = unitPurchasedPrice;
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
		if(priceTax1 == null)
		{
			return new BigDecimal(0);
		}
		
		return priceTax1;
	}

	public void setPriceTax1(BigDecimal priceTax1) {
		this.priceTax1 = priceTax1;
	}

	public BigDecimal getPriceTax2() {
		if(priceTax2 == null)
		{
			return new BigDecimal(0);
		}
		return priceTax2;
	}

	public void setPriceTax2(BigDecimal priceTax2) {
		this.priceTax2 = priceTax2;
	}

	public BigDecimal getPriceTax3() {
		if(priceTax3 == null)
		{
			return new BigDecimal(0);
		}
		return priceTax3;
	}

	public void setPriceTax3(BigDecimal priceTax3) {
		this.priceTax3 = priceTax3;
	}

	public BigDecimal getPriceTax4() {
		if(priceTax4 == null)
		{
			return new BigDecimal(0);
		}
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
	

	public BigDecimal getItemYieldPercent() {
		return itemYieldPercent;
	}

	public void setItemYieldPercent(BigDecimal itemYieldPercent) {
		this.itemYieldPercent = itemYieldPercent;
	}

	
	
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
	
	

	@Override
	public String toString() {
		return "RequestOrderDetailItems [itemsId=" + itemsId + ", requestTo="
				+ requestTo + ", statusId=" + statusId + ", quantity="
				+ quantity + ", receivedQuantity=" + receivedQuantity
				+ ", balance=" + balance + ", requestId=" + requestId
				+ ", purchaseOrderId=" + purchaseOrderId + ", itemName="
				+ itemName + ", uomName=" + uomName + ", total=" + total
				+ ", price=" + price + ", tax=" + tax + ", unitPrice="
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
				+ totalReceiveQuantity + ", paymentMethodId=" + paymentMethodId
				+ ", supplierId=" + supplierId + ", commission=" + commission
				+ ", commissionRate=" + commissionRate + ", localTime="
				+ localTime + "]";
	}

	public RequestOrderDetailItems copy(RequestOrderDetailItems from)
	{
		RequestOrderDetailItems detailItems = new RequestOrderDetailItems();
		detailItems.setItemsId(from.getItemsId());
		detailItems.setRequestTo(from.getRequestTo());
		detailItems.setStatusId(from.getStatusId());
		detailItems.setQuantity(from.getAvailableQuantity());
		detailItems.setReceivedQuantity(from.getReceivedQuantity());
		detailItems.setBalance(from.getBalance());
		detailItems.setRequestId(from.getRequestId());
		detailItems.setPurchaseOrderId(from.getPurchaseOrderId());
		detailItems.setItemName(from.getItemName());
		detailItems.setUomName(from.getUomName());
		detailItems.setTotal(from.getTotal());
		detailItems.setPrice(from.getPrice());
		detailItems.setTax(from.getTax());
		detailItems.setUnitPrice(from.getUnitPrice());
		detailItems.setUnitPurchasedPrice(from.getUnitPurchasedPrice());
		detailItems.setUnitTaxRate(from.getUnitTaxRate());
		detailItems.setAllotmentQty(from.getAllotmentQty());
		detailItems.setInTransitQty(from.getInTransitQty());
		detailItems.setPriceTax1(from.getPriceTax1());
		detailItems.setPriceTax2(from.getPriceTax2());
		detailItems.setPriceTax3(from.getPriceTax3());
		detailItems.setPriceTax4(from.getPriceTax4());
		detailItems.setTaxName1(from.getTaxName1());
		detailItems.setTaxName2(from.getTaxName2());
		detailItems.setTaxName3(from.getTaxName3());
		detailItems.setTaxName4(from.getTaxName4());
		detailItems.setTaxDisplayName1(from.getTaxDisplayName1());
		detailItems.setTaxDisplayName2(from.getTaxDisplayName2());
		detailItems.setTaxDisplayName3(from.getTaxDisplayName3());
		detailItems.setTaxDisplayName4(from.getTaxDisplayName4());
		detailItems.setTaxRate1(from.getTaxRate1());
		detailItems.setTaxRate2(from.getTaxRate2());
		detailItems.setTaxRate3(from.getTaxRate3());
		detailItems.setTaxRate4(from.getTaxRate4());
		detailItems.setYieldQuantity(from.getYieldQuantity());
		detailItems.setTotalReceiveQuantity(from.getTotalReceiveQuantity());
		detailItems.setLocalTime(from.getLocalTime());
		detailItems.setPaymentMethodId(from.getPaymentMethodId());
		detailItems.setSupplierId(from.getSupplierId());
		detailItems.setCommission(from.getCommission());
		detailItems.setCommissionRate(from.getCommissionRate());
		detailItems.setDepartmentId(from.getDepartmentId());
		return detailItems;
	
		
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	

	 

	 
}