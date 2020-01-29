package com.nirvanaxp.types.entities.orders;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;


/**
 * The persistent class for the delivery_option database table.
 * 
 */
@Entity
@Table(name="delivery_rules")
@XmlRootElement(name="delivery_rules")
public class DeliveryRules extends POSNirvanaBaseClass {
	
	private static final long serialVersionUID = 1L;

	 
	@Column(name="name")
	private String name;	

	@Column(name = "distance_type")
	private String distanceType;
	
	@Column(name="distance")
	private BigDecimal distance;

	@Column(name="price")
	private BigDecimal price;
	
	@Column(name="location_id")
	private String locationId;

	@Column(name="free_delivery_order_amount")
	private BigDecimal freeDeliveryOrderAmount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDistanceType() {
		return distanceType;
	}

	public void setDistanceType(String distanceType) {
		this.distanceType = distanceType;
	}

	public BigDecimal getDistance() {
		return distance;
	}

	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getFreeDeliveryOrderAmount() {
		return freeDeliveryOrderAmount;
	}

	public void setFreeDeliveryOrderAmount(BigDecimal freeDeliveryOrderAmount) {
		this.freeDeliveryOrderAmount = freeDeliveryOrderAmount;
	}

	
	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "DeliveryRules [name=" + name + ", distanceType=" + distanceType + ", distance=" + distance + ", price="
				+ price + ", locationId=" + locationId + ", freeDeliveryOrderAmount=" + freeDeliveryOrderAmount + "]";
	}
	 

	 
}