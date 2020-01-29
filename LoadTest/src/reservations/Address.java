package reservations;

import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the address database table.
 * 
 */
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;

	private String address1;

	private String address2;

	private String city;

	private String country;

	private Date created;

	private int createdBy;

	private String fax;

	private String latValue;

	private String longValue;

	private String phone;

	private String state;

	private Date updated;

	private int updatedBy;

	private String zip;

	public Address() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public long getCreated() {
		if(this.created !=null){
			return this.created.getTime();
		}
		return 0;
	}

	public void setCreated(long created) {
		if(created !=0){
			this.created =  new Date(created);
		}
		
	}

	public int getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getLatValue() {
		return this.latValue;
	}

	public void setLatValue(String latValue) {
		this.latValue = latValue;
	}

	public String getLongValue() {
		return this.longValue;
	}

	public void setLongValue(String longValue) {
		this.longValue = longValue;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getUpdated() {
		if(this.updated !=null){
			return this.updated.getTime();
		}
		return 0;
	}
	
	public void setUpdated(long updated) {
		if(updated !=0){
			this.updated = new Date(updated);
		}
	}

	public int getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

}
