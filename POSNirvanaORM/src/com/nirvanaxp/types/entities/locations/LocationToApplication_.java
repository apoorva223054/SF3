package com.nirvanaxp.types.entities.locations;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:22.002+0530")
@StaticMetamodel(LocationToApplication.class)
public class LocationToApplication_ {
	public static volatile SingularAttribute<LocationToApplication, Integer> id;
	public static volatile SingularAttribute<LocationToApplication, Date> created;
	public static volatile SingularAttribute<LocationToApplication, String> createdBy;
	public static volatile SingularAttribute<LocationToApplication, Integer> applicationsId;
	public static volatile SingularAttribute<LocationToApplication, String> locationsId;
	public static volatile SingularAttribute<LocationToApplication, Date> updated;
	public static volatile SingularAttribute<LocationToApplication, String> updatedBy;
}
