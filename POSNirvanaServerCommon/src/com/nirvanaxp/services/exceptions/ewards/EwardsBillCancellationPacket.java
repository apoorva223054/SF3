package com.nirvanaxp.services.exceptions.ewards;

public class EwardsBillCancellationPacket {
	private String merchant_id;
	private String account_email;
	private String bill_number;
	private String mobile;
	public String getMerchant_id() {
		return merchant_id;
	}
	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}
	public String getAccount_email() {
		return account_email;
	}
	public void setAccount_email(String account_email) {
		this.account_email = account_email;
	}
	public String getBill_number() {
		return bill_number;
	}
	public void setBill_number(String bill_number) {
		this.bill_number = bill_number;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Override
	public String toString() {
		return "EwardsBillCancellationPacket [merchant_id=" + merchant_id
				+ ", account_email=" + account_email + ", bill_number="
				+ bill_number + ", mobile=" + mobile + "]";
	}
	
	
}
