package com.nirvanaxp.types.entities.employee;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-08T15:41:44.073+0530")
@StaticMetamodel(BreakInBreakOutHistory.class)
public class BreakInBreakOutHistory_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<BreakInBreakOutHistory, String> usersId;
	public static volatile SingularAttribute<BreakInBreakOutHistory, Integer> breakInBreakOutId;
	public static volatile SingularAttribute<BreakInBreakOutHistory, Integer> clockInClockOutId;
	public static volatile SingularAttribute<BreakInBreakOutHistory, String> breakInOperationId;
	public static volatile SingularAttribute<BreakInBreakOutHistory, Date> breakIn;
	public static volatile SingularAttribute<BreakInBreakOutHistory, String> breakOutOperationId;
	public static volatile SingularAttribute<BreakInBreakOutHistory, Date> breakOut;
	public static volatile SingularAttribute<BreakInBreakOutHistory, String> localTime;
	public static volatile SingularAttribute<BreakInBreakOutHistory, String> sessionId;
	public static volatile SingularAttribute<BreakInBreakOutHistory, String> locationId;
	public static volatile SingularAttribute<BreakInBreakOutHistory, String> jobRoleId;
	public static volatile SingularAttribute<BreakInBreakOutHistory, String> sourceName;
	public static volatile SingularAttribute<BreakInBreakOutHistory, Date> breakInBreakOutCreated;
	public static volatile SingularAttribute<BreakInBreakOutHistory, Date> breakInBreakOutUpdated;
	public static volatile SingularAttribute<BreakInBreakOutHistory, Integer> clockInClockOutHistoryId;
}
