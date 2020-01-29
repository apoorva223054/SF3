package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the delivery_option database table.
 * 
 */
@Entity
@Table(name="delivery_option")
@XmlRootElement(name="delivery_option")
public class DeliveryOption implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Column(name="created_by")
	private String createdBy;

	@Column(name="display_name")
	private String displayName;	

	@Column(name = "display_sequence")
	private Integer displaySequence;
	
	@Column(name="option_type_id")
	private int optionTypeId;

	@Column(name="location_id")
	private String locationId;

	private String name;

	private String status;	
	
	private String parameter1;

	private String parameter2;

	private String parameter3;

	private String parameter4;

	private String parameter5;

	private BigDecimal amount;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;
	
	@Column(name="updated_by")
	private String updatedBy;

	@Column(name="is_default")
	private int isDefault;

	
	public DeliveryOption() {
	}

	 

	public String getId() {
		if (id != null && (id.length() == 0 || id.equals("0"))) {
			return null;
		} else {
			return id;
		}
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public int getOptionTypeId() {
		return optionTypeId;
	}

	public void setOptionTypeId(int optionTypeId) {
		this.optionTypeId = optionTypeId;
	}
	
	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public Integer getDisplaySequence() {
		return displaySequence;
	}

	public void setDisplaySequence(Integer displaySequence) {
		this.displaySequence = displaySequence;
	}
	
	
	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}

	public String getParameter3() {
		return parameter3;
	}

	public void setParameter3(String parameter3) {
		this.parameter3 = parameter3;
	}

	public String getParameter4() {
		return parameter4;
	}

	public void setParameter4(String parameter4) {
		this.parameter4 = parameter4;
	}

	public String getParameter5() {
		return parameter5;
	}

	public void setParameter5(String parameter5) {
		this.parameter5 = parameter5;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getCreated() {
		return created;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public String toString() {
		return "DeliveryOption [id=" + id + ", createdBy=" + createdBy
				+ ", displayName=" + displayName + ", displaySequence="
				+ displaySequence + ", optionTypeId=" + optionTypeId
				+ ", locationId=" + locationId + ", name=" + name + ", status="
				+ status + ", parameter1=" + parameter1 + ", parameter2="
				+ parameter2 + ", parameter3=" + parameter3 + ", parameter4="
				+ parameter4 + ", parameter5=" + parameter5 + ", amount="
				+ amount + ", created=" + created + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", isDefault=" + isDefault + "]";
	}

	
	
}