package com.nirvanaxp.types.entities.tip;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-23T11:10:27.794+0530")
@StaticMetamodel(EmployeeMaster.class)
public class EmployeeMaster_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<EmployeeMaster, String> userId;
	public static volatile SingularAttribute<EmployeeMaster, String> jobRoleId;
	public static volatile SingularAttribute<EmployeeMaster, String> departmentId;
	public static volatile SingularAttribute<EmployeeMaster, Integer> isTippedEmployee;
	public static volatile SingularAttribute<EmployeeMaster, Integer> tipClassId;
	public static volatile SingularAttribute<EmployeeMaster, BigDecimal> hourlyRate;
	public static volatile SingularAttribute<EmployeeMaster, String> locationsId;
	public static volatile SingularAttribute<EmployeeMaster, String> shiftId;
}
