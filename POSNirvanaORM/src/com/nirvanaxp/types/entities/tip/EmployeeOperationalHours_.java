package com.nirvanaxp.types.entities.tip;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-08T15:44:59.997+0530")
@StaticMetamodel(EmployeeOperationalHours.class)
public class EmployeeOperationalHours_ {
	public static volatile SingularAttribute<EmployeeOperationalHours, Integer> id;
	public static volatile SingularAttribute<EmployeeOperationalHours, String> numberOfHours;
	public static volatile SingularAttribute<EmployeeOperationalHours, String> locationId;
	public static volatile SingularAttribute<EmployeeOperationalHours, Integer> employeeId;
	public static volatile SingularAttribute<EmployeeOperationalHours, String> shiftId;
	public static volatile SingularAttribute<EmployeeOperationalHours, String> status;
	public static volatile SingularAttribute<EmployeeOperationalHours, Date> created;
	public static volatile SingularAttribute<EmployeeOperationalHours, String> createdBy;
	public static volatile SingularAttribute<EmployeeOperationalHours, Date> updated;
	public static volatile SingularAttribute<EmployeeOperationalHours, String> updatedBy;
	public static volatile SingularAttribute<EmployeeOperationalHours, String> localTime;
	public static volatile SingularAttribute<EmployeeOperationalHours, String> totalShiftHr;
	public static volatile SingularAttribute<EmployeeOperationalHours, Integer> nirvanaxpBatchId;
	public static volatile SingularAttribute<EmployeeOperationalHours, String> jobRoleId;
}
