package com.nirvanaxp.types.entities.employee;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-08T15:42:38.669+0530")
@StaticMetamodel(ClockInClockOutHistory.class)
public class ClockInClockOutHistory_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<ClockInClockOutHistory, String> usersId;
	public static volatile SingularAttribute<ClockInClockOutHistory, String> clockInOperationId;
	public static volatile SingularAttribute<ClockInClockOutHistory, Date> clockIn;
	public static volatile SingularAttribute<ClockInClockOutHistory, String> clockOutOperationId;
	public static volatile SingularAttribute<ClockInClockOutHistory, Date> clockOut;
	public static volatile SingularAttribute<ClockInClockOutHistory, String> localTime;
	public static volatile SingularAttribute<ClockInClockOutHistory, String> sessionId;
	public static volatile SingularAttribute<ClockInClockOutHistory, String> locationId;
	public static volatile SingularAttribute<ClockInClockOutHistory, String> jobRoleId;
	public static volatile SingularAttribute<ClockInClockOutHistory, Integer> clockInClockOutId;
	public static volatile SingularAttribute<ClockInClockOutHistory, Date> clockInClockOutCreated;
	public static volatile SingularAttribute<ClockInClockOutHistory, Date> clockInClockOutUpdated;
	public static volatile SingularAttribute<ClockInClockOutHistory, String> sourceName;
}
