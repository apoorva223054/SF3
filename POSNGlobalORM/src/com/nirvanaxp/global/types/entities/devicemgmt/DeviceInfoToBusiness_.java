package com.nirvanaxp.global.types.entities.devicemgmt;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.215+0530")
@StaticMetamodel(DeviceInfoToBusiness.class)
public class DeviceInfoToBusiness_ {
	public static volatile SingularAttribute<DeviceInfoToBusiness, Integer> id;
	public static volatile SingularAttribute<DeviceInfoToBusiness, Date> updated;
	public static volatile SingularAttribute<DeviceInfoToBusiness, String> updatedBy;
	public static volatile SingularAttribute<DeviceInfoToBusiness, Date> created;
	public static volatile SingularAttribute<DeviceInfoToBusiness, String> createdBy;
	public static volatile SingularAttribute<DeviceInfoToBusiness, String> status;
	public static volatile SingularAttribute<DeviceInfoToBusiness, Integer> businessId;
	public static volatile SingularAttribute<DeviceInfoToBusiness, Integer> deviceInfoId;
}
