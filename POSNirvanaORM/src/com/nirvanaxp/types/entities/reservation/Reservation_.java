package com.nirvanaxp.types.entities.reservation;

import com.nirvanaxp.types.entities.locations.Location;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-08T11:01:45.925+0530")
@StaticMetamodel(Reservation.class)
public class Reservation_ {
	public static volatile SingularAttribute<Reservation, String> id;
	public static volatile SingularAttribute<Reservation, String> comment;
	public static volatile SingularAttribute<Reservation, Date> created;
	public static volatile SingularAttribute<Reservation, String> createdBy;
	public static volatile SingularAttribute<Reservation, Integer> visitNumber;
	public static volatile SingularAttribute<Reservation, String> date;
	public static volatile SingularAttribute<Reservation, String> email;
	public static volatile SingularAttribute<Reservation, String> firstName;
	public static volatile SingularAttribute<Reservation, String> lastName;
	public static volatile SingularAttribute<Reservation, Integer> partySize;
	public static volatile SingularAttribute<Reservation, String> phoneNumber;
	public static volatile SingularAttribute<Reservation, String> reservationPlatform;
	public static volatile SingularAttribute<Reservation, String> reservationSource;
	public static volatile SingularAttribute<Reservation, String> time;
	public static volatile SingularAttribute<Reservation, Date> updated;
	public static volatile SingularAttribute<Reservation, String> updatedBy;
	public static volatile SingularAttribute<Reservation, String> usersId;
	public static volatile SingularAttribute<Reservation, ContactPreference> contactPreference2;
	public static volatile SingularAttribute<Reservation, ContactPreference> contactPreference1;
	public static volatile SingularAttribute<Reservation, ContactPreference> contactPreference3;
	public static volatile SingularAttribute<Reservation, Location> location;
	public static volatile SingularAttribute<Reservation, Integer> preAssignedLocationId;
	public static volatile SingularAttribute<Reservation, RequestType> requestType;
	public static volatile SingularAttribute<Reservation, ReservationsStatus> reservationsStatus;
	public static volatile SingularAttribute<Reservation, ReservationsType> reservationsType;
	public static volatile SingularAttribute<Reservation, String> contactPreferenceId2;
	public static volatile SingularAttribute<Reservation, String> contactPreferenceId1;
	public static volatile SingularAttribute<Reservation, String> contactPreferenceId3;
	public static volatile SingularAttribute<Reservation, String> locationId;
	public static volatile SingularAttribute<Reservation, String> requestTypeId;
	public static volatile SingularAttribute<Reservation, String> reservationsStatusId;
	public static volatile SingularAttribute<Reservation, Integer> reservationsTypeId;
	public static volatile SingularAttribute<Reservation, Integer> sessionKey;
	public static volatile SingularAttribute<Reservation, Integer> reservationSlotId;
	public static volatile SingularAttribute<Reservation, String> referenceNumber;
	public static volatile SingularAttribute<Reservation, String> localTime;
	public static volatile SingularAttribute<Reservation, Date> reservationDateTime;
	public static volatile SingularAttribute<Reservation, Integer> isOrderPresent;
	public static volatile SingularAttribute<Reservation, String> businessComment;
}
