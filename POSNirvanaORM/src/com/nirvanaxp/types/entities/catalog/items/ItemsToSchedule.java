/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.items;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.protocol.RelationalEntitiesForStringId;

/**
 * The persistent class for the reservation_schedule database table.
 * 
 */
@Entity
@Table(name = "items_to_schedule")
@XmlRootElement(name = "items_to_schedule")
public class ItemsToSchedule extends POSNirvanaBaseClassWithoutGeneratedIds implements RelationalEntitiesForStringId {
	private static final long serialVersionUID = 1L;

	public ItemsToSchedule() {
		// TODO Auto-generated constructor stub
	}

	@Column(name = "items_id", nullable = false)
	private String itemsId;

	@Column(name = "schedule_id", nullable = false)
	private String scheduleId;


	public String getItemsId() {
		 if(itemsId != null && (itemsId.length()==0 || itemsId.equals("0"))){return null;}else{	return itemsId;}
	}

	public void setItemsId(String itemsId) {
		this.itemsId = itemsId;
	}

	public String getScheduleId() {
		 if(scheduleId != null && (scheduleId.length()==0 || scheduleId.equals("0"))){return null;}else{	return scheduleId;}
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	

	@Override
	public String toString() {
		return "ItemsToSchedule [itemsId=" + itemsId + ", scheduleId="
				+ scheduleId + "]";
	}

	@Override
	public void setBaseRelation(String baseId) {
		// TODO Auto-generated method stub
		setItemsId(baseId);
	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId) {
		// TODO Auto-generated method stub
		setScheduleId(baseToObjectId);
	}

}