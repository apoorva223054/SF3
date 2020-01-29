package com.nirvanaxp.types.entities.employee;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-07-17T12:47:35.098+0530")
@StaticMetamodel(ClockInClockOut.class)
public class ClockInClockOut_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<ClockInClockOut, String> usersId;
	public static volatile SingularAttribute<ClockInClockOut, String> clockInOperationId;
	public static volatile SingularAttribute<ClockInClockOut, Date> clockIn;
	public static volatile SingularAttribute<ClockInClockOut, String> clockOutOperationId;
	public static volatile SingularAttribute<ClockInClockOut, Date> clockOut;
	public static volatile SingularAttribute<ClockInClockOut, String> localTime;
	public static volatile SingularAttribute<ClockInClockOut, String> sessionId;
	public static volatile SingularAttribute<ClockInClockOut, String> locationId;
	public static volatile SingularAttribute<ClockInClockOut, String> jobRoleId;
	public static volatile SingularAttribute<ClockInClockOut, String> sourceName;
	public static volatile SingularAttribute<ClockInClockOut, String> clockInClockOutId;
}
