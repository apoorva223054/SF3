package com.nirvanaxp.global.types.entities.accounts;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-10-13T15:30:01.260+0530")
@StaticMetamodel(DaylightSavingTime.class)
public class DaylightSavingTime_ {
	public static volatile SingularAttribute<DaylightSavingTime, Integer> id;
	public static volatile SingularAttribute<DaylightSavingTime, Integer> countryId;
	public static volatile SingularAttribute<DaylightSavingTime, Integer> fromTimeZoneId;
	public static volatile SingularAttribute<DaylightSavingTime, Integer> toTimeZoneId;
	public static volatile SingularAttribute<DaylightSavingTime, String> executionTimeGmt;
}
