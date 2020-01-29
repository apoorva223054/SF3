package com.nirvanaxp.types.entities.tip;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-23T11:10:51.314+0530")
@StaticMetamodel(EmployeeMasterHistory.class)
public class EmployeeMasterHistory_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<EmployeeMasterHistory, String> userId;
	public static volatile SingularAttribute<EmployeeMasterHistory, String> jobRoleId;
	public static volatile SingularAttribute<EmployeeMasterHistory, String> employeeMasterId;
	public static volatile SingularAttribute<EmployeeMasterHistory, String> departmentId;
	public static volatile SingularAttribute<EmployeeMasterHistory, Integer> isTippedEmployee;
	public static volatile SingularAttribute<EmployeeMasterHistory, Integer> tipClassId;
	public static volatile SingularAttribute<EmployeeMasterHistory, BigDecimal> hourlyRate;
	public static volatile SingularAttribute<EmployeeMasterHistory, String> locationsId;
}
