package com.nirvanaxp.types.entities.business;

import com.nirvanaxp.types.entities.Day;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:20.934+0530")
@StaticMetamodel(BusinessHour.class)
public class BusinessHour_ {
	public static volatile SingularAttribute<BusinessHour, String> id;
	public static volatile SingularAttribute<BusinessHour, Date> created;
	public static volatile SingularAttribute<BusinessHour, Date> updated;
	public static volatile SingularAttribute<BusinessHour, String> createdBy;
	public static volatile SingularAttribute<BusinessHour, Integer> isClosed;
	public static volatile SingularAttribute<BusinessHour, String> locationsId;
	public static volatile SingularAttribute<BusinessHour, String> timeFrom;
	public static volatile SingularAttribute<BusinessHour, String> timeTo;
	public static volatile SingularAttribute<BusinessHour, String> updatedBy;
	public static volatile SingularAttribute<BusinessHour, String> status;
	public static volatile SingularAttribute<BusinessHour, Day> day;
}
