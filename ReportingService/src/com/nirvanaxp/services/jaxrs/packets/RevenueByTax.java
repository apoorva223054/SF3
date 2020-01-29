package com.nirvanaxp.services.jaxrs.packets;

public class RevenueByTax {

	private String taxName1;
	private String taxName2;
	private String taxName3;
	private String taxName4;
	
	private String taxRate1;
	private String taxRate2;
	private String taxRate3;
	private String taxRate4;
	private String gratuity;
	private String priceGratuity;

	private String priceTax1;
	private String priceTax2;
	private String priceTax3;
	private String priceTax4;
	
	
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


	public String getPriceTax1() {
		return priceTax1;
	}


	public void setPriceTax1(String priceTax1) {
		this.priceTax1 = priceTax1;
	}


	public String getPriceTax2() {
		return priceTax2;
	}


	public void setPriceTax2(String priceTax2) {
		this.priceTax2 = priceTax2;
	}


	public String getPriceTax3() {
		return priceTax3;
	}


	public void setPriceTax3(String priceTax3) {
		this.priceTax3 = priceTax3;
	}


	public String getPriceTax4() {
		return priceTax4;
	}


	public void setPriceTax4(String priceTax4) {
		this.priceTax4 = priceTax4;
	}


	public String getTaxRate1() {
		return taxRate1;
	}


	public void setTaxRate1(String taxRate1) {
		this.taxRate1 = taxRate1;
	}


	public String getTaxRate2() {
		return taxRate2;
	}


	public void setTaxRate2(String taxRate2) {
		this.taxRate2 = taxRate2;
	}


	public String getTaxRate3() {
		return taxRate3;
	}


	public void setTaxRate3(String taxRate3) {
		this.taxRate3 = taxRate3;
	}


	public String getTaxRate4() {
		return taxRate4;
	}


	public void setTaxRate4(String taxRate4) {
		this.taxRate4 = taxRate4;
	}


	public String getGratuity() {
		return gratuity;
	}


	public void setGratuity(String gratuity) {
		this.gratuity = gratuity;
	}


	public String getPriceGratuity() {
		return priceGratuity;
	}


	public void setPriceGratuity(String priceGratuity) {
		this.priceGratuity = priceGratuity;
	}
	

	@Override
	public String toString() {
		return "RevenueByTax [taxName1=" + taxName1 + ", taxName2=" + taxName2 
				+ ", taxName3=" + taxName3 + ", taxName4=" + taxName4 + ", priceTax1=" + priceTax1 
				+ ", taxRate1=" + taxRate1 + ", taxRate2=" + taxRate2 + ", taxRate3=" + taxRate3 
				+ ", taxRate4=" + taxRate4 + ", gratuity=" + gratuity + ", priceGratuity=" + priceGratuity 
				+ ", priceTax2=" + priceTax2 + "priceTax3=" + priceTax3 + ", priceTax4=" + priceTax4 +"]";
	}	
	
}
