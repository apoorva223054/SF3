package com.nirvanaxp.types.entities.checkmate;


public class CustomerAddress {
	private String addressLine1;
	private String addressLine2;
	private String country;
	private String state;
	private String city;
	private String zip;
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	@Override
	public String toString() {
		return "CustomerAddress [addressLine1=" + addressLine1
				+ ", addressLine2=" + addressLine2 + ", country=" + country
				+ ", state=" + state + ", city=" + city + ", zip=" + zip + "]";
	}
	

}
