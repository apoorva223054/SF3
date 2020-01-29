package com.nirvanaxp.types.entities.locations;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T11:30:47.467+0530")
@StaticMetamodel(LocationsToShiftPreAssignServerHistory.class)
public class LocationsToShiftPreAssignServerHistory_ {
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, Integer> id;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, Date> created;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, String> createdBy;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, String> shiftId;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, String> serverName;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, String> userId;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, String> locationsId;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, Date> updated;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, String> updatedBy;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, String> status;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, Integer> locationsToShiftPreassignServerId;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, Date> locationsToShiftPreassignServerCreated;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, Date> locationsToShiftPreassignServerUpdated;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, String> localTime;
	public static volatile SingularAttribute<LocationsToShiftPreAssignServerHistory, Boolean> isAutoUnassigned;
}
