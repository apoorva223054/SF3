package com.nirvanaxp.types.entities.inventory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

@Entity
@Table(name = "request_order")
@XmlRootElement(name = "request_order")
public class RequestOrder extends POSNirvanaBaseClassWithoutGeneratedIds {
	private static final long serialVersionUID = 1L;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "name")
	private String name;
	
	@Column(name = "date")
	private String date;

	@Column(name = "supplier_id")
	private String supplierId;

	@Column(name = "purchase_order_id")
	private String purchaseOrderId;
	
    @Column(name = "status_id")
	private String statusId;
    
    @Column(name = "isPOOrder")
	private int isPOOrder;
    
    @Column(name = "is_direct_request_allocation")
   	private int isDirectRequestAllocation;
    
    @Column(name = "grn_count")
	private int grnCount;
    
    @Column(name = "department_id")
   	private String departmentId;
    
	transient List<RequestOrderDetailItems> requestOrderDetailItems;

	transient String challanNumber;
	transient String grnDate;
	
	@Column(name = "order_source_sroup_id")
	private String orderSourceGroupId;
	
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
	
	@Column(name = "local_time")
	private String localTime;

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getSupplierId() {
		 if(supplierId != null && (supplierId.length()==0 || supplierId.equals("0"))){return null;}else{	return supplierId;}
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}



	public List<RequestOrderDetailItems> getRequestOrderDetailItems() {
		return requestOrderDetailItems;
	}

	public void setRequestOrderDetailItems(List<RequestOrderDetailItems> requestOrderDetailItems) {
		this.requestOrderDetailItems = requestOrderDetailItems;
	}

	public String getChallanNumber() {
		return challanNumber;
	}

	public void setChallanNumber(String challanNumber) {
		this.challanNumber = challanNumber;
	}

	public String getStatusId() {
		 if(statusId != null && (statusId.length()==0 || statusId.equals("0"))){return null;}else{	return statusId;}
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public int getIsPOOrder() {
		return isPOOrder;
	}

	public void setIsPOOrder(int isPOOrder) {
		this.isPOOrder = isPOOrder;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getIsDirectRequestAllocation() {
		return isDirectRequestAllocation;
	}

	public void setIsDirectRequestAllocation(int isDirectRequestAllocation) {
		this.isDirectRequestAllocation = isDirectRequestAllocation;
	}

	public int getGrnCount() {
		return grnCount;
	}

	public void setGrnCount(int grnCount) {
		this.grnCount = grnCount;
	}

	public String getGrnDate() {
		return grnDate;
	}

	public void setGrnDate(String grnDate) {
		this.grnDate = grnDate;
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


	 
	

	

	public String getPurchaseOrderId() {
		 if(purchaseOrderId != null && (purchaseOrderId.length()==0 || purchaseOrderId.equals("0"))){return null;}else{	return purchaseOrderId;}
	}

	public void setPurchaseOrderId(String purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	@Override
	public String toString() {
		return "RequestOrder [locationId=" + locationId + ", name=" + name + ", date=" + date + ", supplierId="
				+ supplierId + ", purchaseOrderId=" + purchaseOrderId + ", statusId=" + statusId + ", isPOOrder="
				+ isPOOrder + ", isDirectRequestAllocation=" + isDirectRequestAllocation + ", grnCount=" + grnCount
				+ ", priceTax1=" + priceTax1 + ", priceTax2=" + priceTax2 + ", priceTax3=" + priceTax3 + ", priceTax4="
				+ priceTax4 + ", taxName1=" + taxName1 + ", taxName2=" + taxName2 + ", taxName3=" + taxName3
				+ ", taxName4=" + taxName4 + ", taxDisplayName1=" + taxDisplayName1 + ", taxDisplayName2="
				+ taxDisplayName2 + ", taxDisplayName3=" + taxDisplayName3 + ", taxDisplayName4=" + taxDisplayName4
				+ ", taxRate1=" + taxRate1 + ", taxRate2=" + taxRate2 + ", taxRate3=" + taxRate3 + ", taxRate4="
				+ taxRate4 + ", localTime=" + localTime + "]";
	}
	
	public String getOrderSourceGroupId() {
		 if(orderSourceGroupId != null && (orderSourceGroupId.length()==0 || orderSourceGroupId.equals("0"))){return null;}else{	return orderSourceGroupId;}
	}

	public void setOrderSourceGroupId(String orderSourceGroupId) {
		this.orderSourceGroupId = orderSourceGroupId;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	
	

	 
	
	
	

}
