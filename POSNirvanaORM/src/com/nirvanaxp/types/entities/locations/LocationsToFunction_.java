package com.nirvanaxp.types.entities.locations;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-02T15:31:05.111+0530")
@StaticMetamodel(LocationsToFunction.class)
public class LocationsToFunction_ {
	public static volatile SingularAttribute<LocationsToFunction, Integer> id;
	public static volatile SingularAttribute<LocationsToFunction, Date> created;
	public static volatile SingularAttribute<LocationsToFunction, String> createdBy;
	public static volatile SingularAttribute<LocationsToFunction, String> functionsId;
	public static volatile SingularAttribute<LocationsToFunction, String> functionsName;
	public static volatile SingularAttribute<LocationsToFunction, String> locationsId;
	public static volatile SingularAttribute<LocationsToFunction, Date> updated;
	public static volatile SingularAttribute<LocationsToFunction, String> updatedBy;
	public static volatile SingularAttribute<LocationsToFunction, String> status;
	public static volatile SingularAttribute<LocationsToFunction, Integer> displaySequence;
}
