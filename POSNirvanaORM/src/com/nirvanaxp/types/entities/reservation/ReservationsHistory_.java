package com.nirvanaxp.types.entities.reservation;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-02T16:45:09.000+0530")
@StaticMetamodel(ReservationsHistory.class)
public class ReservationsHistory_ {
	public static volatile SingularAttribute<ReservationsHistory, BigInteger> id;
	public static volatile SingularAttribute<ReservationsHistory, String> comment;
	public static volatile SingularAttribute<ReservationsHistory, String> contactPreference1;
	public static volatile SingularAttribute<ReservationsHistory, String> contactPreference2;
	public static volatile SingularAttribute<ReservationsHistory, String> contactPreference3;
	public static volatile SingularAttribute<ReservationsHistory, Date> created;
	public static volatile SingularAttribute<ReservationsHistory, String> createdBy;
	public static volatile SingularAttribute<ReservationsHistory, String> date;
	public static volatile SingularAttribute<ReservationsHistory, String> email;
	public static volatile SingularAttribute<ReservationsHistory, String> firstName;
	public static volatile SingularAttribute<ReservationsHistory, String> lastName;
	public static volatile SingularAttribute<ReservationsHistory, String> locationsId;
	public static volatile SingularAttribute<ReservationsHistory, Integer> partySize;
	public static volatile SingularAttribute<ReservationsHistory, String> phoneNumber;
	public static volatile SingularAttribute<ReservationsHistory, String> requestTypeId;
	public static volatile SingularAttribute<ReservationsHistory, String> reservationPlatform;
	public static volatile SingularAttribute<ReservationsHistory, String> reservationSource;
	public static volatile SingularAttribute<ReservationsHistory, Integer> reservationTypesId;
	public static volatile SingularAttribute<ReservationsHistory, String> reservationsId;
	public static volatile SingularAttribute<ReservationsHistory, String> reservationsStatusId;
	public static volatile SingularAttribute<ReservationsHistory, String> time;
	public static volatile SingularAttribute<ReservationsHistory, Date> updated;
	public static volatile SingularAttribute<ReservationsHistory, String> updatedBy;
	public static volatile SingularAttribute<ReservationsHistory, String> usersId;
	public static volatile SingularAttribute<ReservationsHistory, Integer> sessionKey;
	public static volatile SingularAttribute<ReservationsHistory, Integer> preAssignedLocationId;
	public static volatile SingularAttribute<ReservationsHistory, Integer> isOrderPresent;
	public static volatile SingularAttribute<ReservationsHistory, String> businessComment;
	public static volatile SingularAttribute<ReservationsHistory, String> localTime;
}
