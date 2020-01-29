package com.nirvanaxp.global.types.entities.countries;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.046+0530")
@StaticMetamodel(City.class)
public class City_ {
	public static volatile SingularAttribute<City, Integer> id;
	public static volatile SingularAttribute<City, String> cityName;
	public static volatile SingularAttribute<City, Integer> countryId;
	public static volatile SingularAttribute<City, Double> latitude;
	public static volatile SingularAttribute<City, Double> longitude;
	public static volatile SingularAttribute<City, Integer> stateId;
	public static volatile SingularAttribute<City, String> status;
	public static volatile SingularAttribute<City, Date> created;
	public static volatile SingularAttribute<City, String> createdBy;
	public static volatile SingularAttribute<City, Date> updated;
	public static volatile SingularAttribute<City, String> updatedBy;
	public static volatile SingularAttribute<City, Integer> isOnlineCity;
	public static volatile SingularAttribute<City, String> cityImage;
}
