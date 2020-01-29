package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-26T17:35:28.175+0530")
@StaticMetamodel(DriverOrder.class)
public class DriverOrder_ {
	public static volatile SingularAttribute<DriverOrder, Integer> id;
	public static volatile SingularAttribute<DriverOrder, String> orderNumber;
	public static volatile SingularAttribute<DriverOrder, String> orderId;
	public static volatile SingularAttribute<DriverOrder, String> firstName;
	public static volatile SingularAttribute<DriverOrder, String> lastName;
	public static volatile SingularAttribute<DriverOrder, String> email;
	public static volatile SingularAttribute<DriverOrder, String> phone;
	public static volatile SingularAttribute<DriverOrder, String> statusId;
	public static volatile SingularAttribute<DriverOrder, String> statusName;
	public static volatile SingularAttribute<DriverOrder, String> businessName;
	public static volatile SingularAttribute<DriverOrder, String> businessAddress;
	public static volatile SingularAttribute<DriverOrder, String> customerAddress;
	public static volatile SingularAttribute<DriverOrder, Date> time;
	public static volatile SingularAttribute<DriverOrder, Date> created;
	public static volatile SingularAttribute<DriverOrder, String> createdBy;
	public static volatile SingularAttribute<DriverOrder, Date> updated;
	public static volatile SingularAttribute<DriverOrder, String> updatedBy;
	public static volatile SingularAttribute<DriverOrder, String> nxpAccessToken;
	public static volatile SingularAttribute<DriverOrder, Integer> businessId;
	public static volatile SingularAttribute<DriverOrder, String> locationsId;
	public static volatile SingularAttribute<DriverOrder, Integer> accountId;
	public static volatile SingularAttribute<DriverOrder, String> driverId;
	public static volatile SingularAttribute<DriverOrder, String> scheduleDateTime;
}
