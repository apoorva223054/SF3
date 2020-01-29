package com.nirvanaxp.types.entities.tip;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-03T15:37:23.506+0530")
@StaticMetamodel(EmployeeMasterToJobRoles.class)
public class EmployeeMasterToJobRoles_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<EmployeeMasterToJobRoles, String> userId;
	public static volatile SingularAttribute<EmployeeMasterToJobRoles, String> jobRoleId;
	public static volatile SingularAttribute<EmployeeMasterToJobRoles, String> locationsId;
	public static volatile SingularAttribute<EmployeeMasterToJobRoles, Integer> isDefaultRole;
}
