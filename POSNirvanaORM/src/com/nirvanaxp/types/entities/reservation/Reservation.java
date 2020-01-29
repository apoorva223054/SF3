/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.reservation;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.Location;

/**
 * The persistent class for the reservations database table.
 * 
 */
@Entity
@Table(name = "reservations")
@XmlRootElement(name = "reservations")
public class Reservation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Column(length = 1024)
	private String comment;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "visitNumber")
	private int visitNumber;

	@Column(nullable = false, length = 10)
	private String date;

	private String email;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "party_size", nullable = false)
	private int partySize;

	@Column(name = "phone_number", length = 32)
	private String phoneNumber;

	@Column(name = "reservation_platform", nullable = false, length = 64)
	private String reservationPlatform;

	@Column(name = "reservation_source", nullable = false, length = 64)
	private String reservationSource;

	@Column(nullable = false, length = 8)
	private String time;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "users_id", nullable = false)
	private String usersId;

	// uni-directional many-to-one association to ContactPreference
	@ManyToOne
	@JoinColumn(name = "contact_preference_2", insertable = false, updatable = false, nullable = false)
	private ContactPreference contactPreference2;

	// uni-directional many-to-one association to ContactPreference
	@ManyToOne
	@JoinColumn(name = "contact_preference_1", insertable = false, updatable = false, nullable = false)
	private ContactPreference contactPreference1;

	// uni-directional many-to-one association to ContactPreference
	@ManyToOne
	@JoinColumn(name = "contact_preference_3", insertable = false, updatable = false, nullable = false)
	private ContactPreference contactPreference3;

	// uni-directional many-to-one association to Location
	@ManyToOne
	@JoinColumn(name = "locations_id", insertable = false, updatable = false, nullable = false)
	private Location location;

	@Column(name = "pre_assigned_location_id")
	private int preAssignedLocationId;

	// uni-directional many-to-one association to RequestType
	@ManyToOne
	@JoinColumn(name = "request_type_id", insertable = false, updatable = false, nullable = true)
	private RequestType requestType;

	// uni-directional many-to-one association to ReservationsStatus
	@ManyToOne
	@JoinColumn(name = "reservations_status_id", insertable = false, updatable = false, nullable = true)
	private ReservationsStatus reservationsStatus;

	// uni-directional many-to-one association to ReservationsType
	@ManyToOne
	@JoinColumn(name = "reservation_types_id", nullable = false, insertable = false, updatable = false)
	private ReservationsType reservationsType;

	// changes by Apoorva to load reservation fast
	@Column(name = "contact_preference_2")
	private String contactPreferenceId2;

	@Column(name = "contact_preference_1")
	private String contactPreferenceId1;

	@Column(name = "contact_preference_3")
	private String contactPreferenceId3;

	@Column(name = "locations_id")
	private String locationId;

	@Column(name = "request_type_id")
	private String requestTypeId;

	@Column(name = "reservations_status_id")
	private String reservationsStatusId;

	@Column(name = "reservation_types_id")
	private int reservationsTypeId;

	private transient String locationName;
	private transient int visitCount;

	@Column(name = "session_id")
	private Integer sessionKey;

	@Column(name = "reservation_slot_id")
	private Integer reservationSlotId;

	@Column(name = "refrence_number")
	private String referenceNumber;

	@Column(name = "local_time")
	private String localTime;

	@Column(name = "reservation_date_time")
	private Date reservationDateTime;

	@Column(name = "is_order_present")
	private int isOrderPresent;

	// this we set when reservation guest count is updated and we change the
	// order point of service count too
	private transient String orderId;

	// this we set when reservation is created with number and that number sould
	// belongs to some country, this variable will get used in user creation
	// from reservation
	private transient int countryId;

	// this we set when reservation guest count is updated and we change the
	// order point of service count too
	private transient String scheduleDateTime;

	public Reservation(Object[] obj) {
		// the passed object[] must have data in this Item

		if (obj[0] != null) {
			setId((String) obj[0]);

		}
		if (obj[1] != null) {
			setDate((String) obj[1]);
		}
		if (obj[2] != null) {
			setTime((String) obj[2]);
		}
		if (obj[3] != null) {
			setPartySize((Integer) obj[3]);
		}
		if (obj[4] != null) {
			setFirstName((String) obj[4]);
		}
		if (obj[5] != null) {
			setLastName((String) obj[5]);
		}
		if (obj[6] != null) {
			setPhoneNumber((String) obj[6]);
		}
		if (obj[7] != null) {
			setEmail((String) obj[7]);
		}
		if (obj[8] != null) {
			setUsersId((String) obj[8]);
		}
		if (obj[9] != null) {
			Location location = new Location();
			location.setId((String) obj[93]);
			setLocation(location);
		}

		if (obj[10] != null) {
			ReservationsType reservationsType = new ReservationsType(obj, 39);
			setReservationsType(reservationsType);
		}
		if (obj[11] != null) {
			ContactPreference contactPreference1 = new ContactPreference(obj, 60);// 69
			setContactPreference1(contactPreference1);
		}
		if (obj[12] != null) {
			ContactPreference contactPreference2 = new ContactPreference(obj, 71);
			contactPreference2.setId((String) obj[12]);
			setContactPreference2(contactPreference2);
		}
		if (obj[13] != null) {

			ContactPreference contactPreference3 = new ContactPreference(obj, 82);
			contactPreference3.setId((String) obj[13]);
			setContactPreference3(contactPreference3);
		}
		if (obj[14] != null) {
			setComment((String) obj[14]);
		}
		if (obj[15] != null) {
			RequestType requestType = new RequestType(obj, 49);
			setRequestType(requestType);
		}
		if (obj[16] != null) {
			ReservationsStatus reservationsStatus = new ReservationsStatus(obj, 25);
			setReservationsStatus(reservationsStatus);
		}
		if (obj[17] != null) {
			setReservationSource((String) obj[17]);
		}
		if (obj[18] != null) {
			setReservationPlatform((String) obj[18]);
		}
		if (obj[19] != null) {
			created = (Date) obj[19];
		}
		if (obj[20] != null) {
			setCreatedBy((String) obj[20]);
		}
		if (obj[21] != null) {
			updated = ((Date) obj[21]);
		}
		if (obj[22] != null) {
			setUpdatedBy((String) obj[22]);
		}
		if (obj[23] != null) {
			setVisitNumber((Integer) obj[23]);
		}
		if (obj[24] != null) {
			setLocationName((String) obj[24]);
		}

		this.setPreAssignedLocationId((Integer) obj[94]);
		ReservationsStatus reservationsStatus = getReservationsStatus();
		reservationsStatus.setIsServerDriven((Integer) obj[95]);
		this.setReservationsStatus(reservationsStatus);
		if (obj[96] != null)
			setContactPreferenceId1((String) obj[96]);
		if (obj[97] != null)
			setContactPreferenceId2((String) obj[97]);
		if (obj[98] != null)
			setContactPreferenceId3((String) obj[98]);
		if (obj[99] != null)
			setLocationId((String) obj[99]);
		if (obj[100] != null)
			setRequestTypeId((String) obj[100]);
		if (obj[101] != null)
			setReservationsStatusId((String) obj[101]);
		if (obj[102] != null)
			setReservationsTypeId((int) obj[102]);
		if (obj[103] != null)
			setSessionKey((int) obj[103]);
		if (obj[104] != null)
			setReservationSlotId((Integer) obj[104]);
		if (obj[105] != null)
			setIsOrderPresent(((BigInteger) obj[105]).intValue());
		if (obj[106] != null)
			setBusinessComment((String) obj[106]);

	}

	public int getVisitNumber() {
		return visitNumber;
	}

	public void setVisitNumber(int visitNumber) {
		this.visitNumber = visitNumber;
	}

	public int getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public Reservation() {
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

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getPartySize() {
		return this.partySize;
	}

	public void setPartySize(int partySize) {
		this.partySize = partySize;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getReservationPlatform() {
		return this.reservationPlatform;
	}

	public void setReservationPlatform(String reservationPlatform) {
		this.reservationPlatform = reservationPlatform;
	}

	public String getReservationSource() {
		return this.reservationSource;
	}

	public void setReservationSource(String reservationSource) {
		this.reservationSource = reservationSource;
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public ContactPreference getContactPreference2() {
		 if(contactPreference2 != null && ( contactPreference2.equals("0"))){return null;}else{	return contactPreference2;}
	}

	public void setContactPreference2(ContactPreference contactPreference2) {
		this.contactPreference2 = contactPreference2;
	}

	public ContactPreference getContactPreference1() {
		 if(contactPreference1 != null && ( contactPreference1.equals("0"))){return null;}else{	return contactPreference1;}
	}

	public void setContactPreference1(ContactPreference contactPreference1) {
		this.contactPreference1 = contactPreference1;
	}

	public ContactPreference getContactPreference3() {
		 if(contactPreference3 != null && ( contactPreference3.equals("0"))){return null;}else{	return contactPreference3;}
	}

	public void setContactPreference3(ContactPreference contactPreference3) {
		this.contactPreference3 = contactPreference3;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public RequestType getRequestType() {
		return this.requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public ReservationsStatus getReservationsStatus() {
		return this.reservationsStatus;
	}

	public void setReservationsStatus(ReservationsStatus reservationsStatus) {
		this.reservationsStatus = reservationsStatus;
	}

	public ReservationsType getReservationsType() {
		return this.reservationsType;
	}

	public void setReservationsType(ReservationsType reservationsType) {
		this.reservationsType = reservationsType;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Reservation(ResultSet rs) throws SQLException {

		this.setId((rs.getString(1)));

		this.setDate(rs.getString(2));

		this.setTime(rs.getString(3));

		this.setPartySize(rs.getInt(4));

		this.setFirstName(rs.getString(5));

		this.setLastName(rs.getString(6));

		this.setPhoneNumber(rs.getString(7));

		this.setEmail(rs.getString(8));

		this.setUsersId((rs.getString(9)));

		this.setComment(rs.getString(15));

		this.setReservationSource(rs.getString(18));

		this.setReservationPlatform(rs.getString(19));

		if (rs.getTimestamp(20) != null) {
			this.setCreated(new Date(rs.getTimestamp(20).getTime()));
		}

		this.setCreatedBy((rs.getString(21)));

		if (rs.getTimestamp(22) != null) {
			this.setUpdated(new Date(rs.getTimestamp(22).getTime()));
		}

		this.setUpdatedBy((rs.getString(23)));

		this.setVisitNumber(rs.getInt(24));

		// naman
		this.setLocationName(rs.getString(25));// reservation
												// location name

		ReservationsStatus reservationStatus = new ReservationsStatus();

		reservationStatus.setId(rs.getString(26));

		reservationStatus.setName(rs.getString(27));

		reservationStatus.setDisplayName(rs.getString(28));

		reservationStatus.setDisplaySequence(rs.getInt(29));

		reservationStatus.setDescription(rs.getString(30));

		reservationStatus.setShowToCustomer(rs.getInt(31));

		reservationStatus.setLocationsId(rs.getString(32)); // reservation
															// status
															// locationid

		reservationStatus.setHexCodeValues(rs.getString(33));

		reservationStatus.setStatus(rs.getString(34));

		if (rs.getTimestamp(35) != null) {
			reservationStatus.setCreated(new Date(rs.getTimestamp(35).getTime()));
		}

		reservationStatus.setCreatedBy((rs.getString(36)));

		if (rs.getTimestamp(37) != null) {
			reservationStatus.setUpdated(new Date(rs.getTimestamp(37).getTime()));
		}

		reservationStatus.setUpdatedBy((rs.getString(38)));

		reservationStatus.setIsServerDriven(rs.getInt(39));

		this.setReservationsStatus(reservationStatus);

		ReservationsType reservationsType = new ReservationsType();

		reservationsType.setId(rs.getInt(40));

		reservationsType.setName(rs.getString(41));

		reservationsType.setDisplayName(rs.getString(42));

		reservationsType.setDisplaySequence(rs.getInt(43));

		reservationsType.setLocationsId(rs.getString(44));

		reservationsType.setStatus(rs.getString(45));

		if (rs.getTimestamp(46) != null) {
			reservationsType.setCreated(new Date(rs.getTimestamp(46).getTime()));
		}

		reservationsType.setCreatedBy((rs.getString(47)));

		if (rs.getTimestamp(48) != null) {
			reservationsType.setUpdated(new Date(rs.getTimestamp(48).getTime()));
		}

		reservationsType.setUpdatedBy((rs.getString(49)));

		this.setReservationsType(reservationsType);

		RequestType requestType = new RequestType();

		requestType.setId(rs.getString(50));
		if (rs.getString(51) != null) {
			requestType.setRequestName(rs.getString(51));
		}
		if (rs.getString(52) != null) {
			requestType.setDisplayName(rs.getString(52));
		}

		requestType.setDisplaySequence(rs.getInt(53));
		if (rs.getString(54) != null) {
			requestType.setDescription(rs.getString(54));
		}

		requestType.setLocationsId(rs.getString(55));
		if (rs.getString(56) != null) {
			requestType.setStatus(rs.getString(56));
		}
		if (rs.getTimestamp(57) != null) {
			requestType.setCreated(new Date(rs.getTimestamp(57).getTime()));
		}
		requestType.setCreatedBy((rs.getString(58)));

		if (rs.getTimestamp(59) != null) {
			requestType.setUpdated(new Date(rs.getTimestamp(59).getTime()));
		}

		requestType.setUpdatedBy((rs.getString(60)));
		this.setRequestType(requestType);

		ContactPreference contactPreference = null;

		contactPreference = new ContactPreference();
		if (rs.getString(12) != null) {
			contactPreference.setId(rs.getString(61));

			contactPreference.setName(rs.getString(62));

			contactPreference.setDisplayName(rs.getString(63));

			contactPreference.setDisplaySequence(rs.getInt(64));

			contactPreference.setDescription(rs.getString(65));

			contactPreference.setLocationsId(rs.getString(66));

			contactPreference.setStatus(rs.getString(67));

			if (rs.getTimestamp(68) != null) {
				contactPreference.setCreated(new Date(rs.getTimestamp(68).getTime()));
			}

			contactPreference.setCreatedBy((rs.getString(69)));

			if (rs.getTimestamp(70) != null) {
				contactPreference.setUpdated(new Date(rs.getTimestamp(70).getTime()));
			}

			contactPreference.setUpdatedBy((rs.getString(71)));
			this.setContactPreference1(contactPreference);
		}

		if (rs.getString(13) != null) {
			contactPreference.setId(rs.getString(72));

			contactPreference.setName(rs.getString(73));

			contactPreference.setDisplayName(rs.getString(74));

			contactPreference.setDisplaySequence(rs.getInt(75));
			contactPreference.setDescription(rs.getString(76));

			contactPreference.setLocationsId(rs.getString(77));

			contactPreference.setStatus(rs.getString(78));

			if (rs.getTimestamp(79) != null) {
				contactPreference.setCreated(new Date(rs.getTimestamp(79).getTime()));
			}

			contactPreference.setCreatedBy((rs.getString(80)));

			if (rs.getTimestamp(81) != null) {
				contactPreference.setUpdated(new Date(rs.getTimestamp(81).getTime()));
			}

			contactPreference.setUpdatedBy((rs.getString(82)));
			this.setContactPreference2(contactPreference);
		}

		if (rs.getString(14) != null) {
			contactPreference.setId(rs.getString(83));

			contactPreference.setName(rs.getString(84));

			contactPreference.setDisplayName(rs.getString(85));

			contactPreference.setDisplaySequence(rs.getInt(86));
			contactPreference.setDescription(rs.getString(87));
			contactPreference.setLocationsId(rs.getString(88));

			contactPreference.setStatus(rs.getString(89));

			if (rs.getTimestamp(90) != null) {
				contactPreference.setCreated(new Date(rs.getTimestamp(90).getTime()));
			}

			contactPreference.setCreatedBy((rs.getString(91)));
			if (rs.getTimestamp(91) != null) {
				contactPreference.setUpdated(new Date(rs.getTimestamp(92).getTime()));
			}

			contactPreference.setUpdatedBy((rs.getString(93)));
			this.setContactPreference3(contactPreference);
		}

		// we want in getters tthat no integer should come
		Location location = new Location();
		location.setNullRequired(true);
		location.setId(rs.getString(94));
		this.setLocation(location);

		this.setPreAssignedLocationId(rs.getInt(95));
		this.setLocalTime(rs.getString(96));

	}

	public String getOrderId() {
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(Integer sessionKey) {
		this.sessionKey = sessionKey;
	}

	public int getPreAssignedLocationId() {
		return preAssignedLocationId;
	}

	public void setPreAssignedLocationId(int preAssignedLocationId) {
		this.preAssignedLocationId = preAssignedLocationId;
	}

	public String getScheduleDateTime() {
		return scheduleDateTime;
	}

	public void setScheduleDateTime(String scheduleDateTime) {
		this.scheduleDateTime = scheduleDateTime;
	}

	public Integer getReservationSlotId() {
		return reservationSlotId;
	}

	public void setReservationSlotId(Integer reservationSlotId) {
		this.reservationSlotId = reservationSlotId;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
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

	public Date getReservationDateTime() {
		return reservationDateTime;
	}

	public void setReservationDateTime(Date reservationDateTime) {
		this.reservationDateTime = reservationDateTime;
	}

	public String getContactPreferenceId1() {
		return contactPreferenceId1;
	}

	public void setContactPreferenceId1(String contactPreferenceId1) {
		this.contactPreferenceId1 = contactPreferenceId1;
	}

	public String getContactPreferenceId2() {
		return contactPreferenceId2;
	}

	public void setContactPreferenceId2(String contactPreferenceId2) {
		this.contactPreferenceId2 = contactPreferenceId2;
	}

	public String getContactPreferenceId3() {
		return contactPreferenceId3;
	}

	public void setContactPreferenceId3(String contactPreferenceId3) {
		this.contactPreferenceId3 = contactPreferenceId3;
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getRequestTypeId() {
		 if(requestTypeId != null && (requestTypeId.length()==0 || requestTypeId.equals("0"))){return null;}else{	return requestTypeId;}
	}

	public void setRequestTypeId(String requestTypeId) {
		this.requestTypeId = requestTypeId;
	}

	public String getReservationsStatusId() {
		if(reservationsStatusId != null && (reservationsStatusId.length()==0 || reservationsStatusId.equals("0"))){return null;}else{	return reservationsStatusId;}
	}

	public void setReservationsStatusId(String reservationsStatusId) {
		this.reservationsStatusId = reservationsStatusId;
	}

	public int getReservationsTypeId() {
		return reservationsTypeId;
	}

	public void setReservationsTypeId(int reservationsTypeId) {
		this.reservationsTypeId = reservationsTypeId;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getIsOrderPresent() {
		return isOrderPresent;
	}

	public void setIsOrderPresent(int isOrderPresent) {
		this.isOrderPresent = isOrderPresent;
	}

	@Column(name = "business_comment ")
	private String businessComment;

	public String getBusinessComment() {
		return businessComment;
	}

	public void setBusinessComment(String businessComment) {
		this.businessComment = businessComment;
	}

	public String getLocalTime() {
		return localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
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

	@Override
	public String toString() {
		return "Reservation [id=" + id + ", comment=" + comment + ", created=" + created + ", createdBy=" + createdBy
				+ ", visitNumber=" + visitNumber + ", date=" + date + ", email=" + email + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", partySize=" + partySize + ", phoneNumber=" + phoneNumber
				+ ", reservationPlatform=" + reservationPlatform + ", reservationSource=" + reservationSource
				+ ", time=" + time + ", updated=" + updated + ", updatedBy=" + updatedBy + ", usersId=" + usersId
				+ ", contactPreference2=" + contactPreference2 + ", contactPreference1=" + contactPreference1
				+ ", contactPreference3=" + contactPreference3 + ", location=" + location + ", preAssignedLocationId="
				+ preAssignedLocationId + ", requestType=" + requestType + ", reservationsStatus=" + reservationsStatus
				+ ", reservationsType=" + reservationsType + ", contactPreferenceId2=" + contactPreferenceId2
				+ ", contactPreferenceId1=" + contactPreferenceId1 + ", contactPreferenceId3=" + contactPreferenceId3
				+ ", locationId=" + locationId + ", requestTypeId=" + requestTypeId + ", reservationsStatusId="
				+ reservationsStatusId + ", reservationsTypeId=" + reservationsTypeId + ", sessionKey=" + sessionKey
				+ ", reservationSlotId=" + reservationSlotId + ", referenceNumber=" + referenceNumber + ", localTime="
				+ localTime + ", reservationDateTime=" + reservationDateTime + ", isOrderPresent=" + isOrderPresent
				+ ", businessComment=" + businessComment + "]";
	}

}