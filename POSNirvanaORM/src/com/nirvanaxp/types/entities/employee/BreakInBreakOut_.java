package com.nirvanaxp.types.entities.employee;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-08T15:40:43.526+0530")
@StaticMetamodel(BreakInBreakOut.class)
public class BreakInBreakOut_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<BreakInBreakOut, String> usersId;
	public static volatile SingularAttribute<BreakInBreakOut, Integer> clockInClockOutId;
	public static volatile SingularAttribute<BreakInBreakOut, String> breakInOperationId;
	public static volatile SingularAttribute<BreakInBreakOut, Date> breakIn;
	public static volatile SingularAttribute<BreakInBreakOut, String> breakOutOperationId;
	public static volatile SingularAttribute<BreakInBreakOut, Date> breakOut;
	public static volatile SingularAttribute<BreakInBreakOut, String> localTime;
	public static volatile SingularAttribute<BreakInBreakOut, String> sessionId;
	public static volatile SingularAttribute<BreakInBreakOut, String> locationId;
	public static volatile SingularAttribute<BreakInBreakOut, String> jobRoleId;
	public static volatile SingularAttribute<BreakInBreakOut, String> sourceName;
}
