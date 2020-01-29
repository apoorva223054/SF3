package com.nirvanaxp.types.entities.orders;

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

@Entity
@Table(name = "shift_slots_active_client_info")
@XmlRootElement(name = "shift_slots_active_client_info")
public class ShiftSlotActiveClientInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "session_id")
	private String sessionId;

	// start of the slot
	@Column(name = "slot_hold_start_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date slotHoldStartTime;

	@Column(name = "hold_time")
	private String holdTime;

	// time for the slot
	@Column(name = "shift_slot_id", nullable = false)
	private int shiftSlotId;

	@Column(name = "is_shift_made_by_client")
	private boolean isShiftMadeByClient;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "created_by")
	private String createdBy;

	public ShiftSlotActiveClientInfo() {
		super();
		
	}

	public ShiftSlotActiveClientInfo(String sessionId, String holdTime, int shiftSlotId) {
		super();
		this.sessionId = sessionId;
		this.holdTime = holdTime;
		this.shiftSlotId = shiftSlotId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getSlotHoldStartTime() {
		return slotHoldStartTime;
	}

	public void setSlotHoldStartTime(Date slotHoldStartTime) {
		this.slotHoldStartTime = slotHoldStartTime;
	}


	public int getShiftSlotId() {
		return shiftSlotId;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setShiftSlotId(int shiftSlotId) {
		this.shiftSlotId = shiftSlotId;
	}

	public boolean isShiftMadeByClient() {
		return isShiftMadeByClient;
	}

	public void setShiftMadeByClient(boolean isShiftMadeByClient) {
		this.isShiftMadeByClient = isShiftMadeByClient;
	}


	public String getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(String holdTime) {
		this.holdTime = holdTime;
	}

	@Override
	public String toString() {
		return "ShiftSlotActiveClientInfo [id=" + id + ", created=" + created + ", updated=" + updated + ", sessionId="
				+ sessionId + ", slotHoldStartTime=" + slotHoldStartTime + ", holdTime=" + holdTime + ", shiftSlotId="
				+ shiftSlotId + ", isShiftMadeByClient=" + isShiftMadeByClient + ", updatedBy=" + updatedBy
				+ ", createdBy=" + createdBy + "]";
	}


	
}
