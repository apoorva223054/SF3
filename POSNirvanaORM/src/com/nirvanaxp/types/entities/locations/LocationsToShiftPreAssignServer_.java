package com.nirvanaxp.types.entities.locations;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T11:30:47.382+0530")
@StaticMetamodel(LocationsToShiftPreAssignServer.class)
public class LocationsToShiftPreAssignServer_ {
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, Integer> id;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, Date> created;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, String> createdBy;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, String> shiftId;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, String> serverName;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, String> userId;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, String> locationsId;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, Date> updated;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, String> updatedBy;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, String> status;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, String> localTime;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServer, Boolean> isAutoUnassigned;
}
