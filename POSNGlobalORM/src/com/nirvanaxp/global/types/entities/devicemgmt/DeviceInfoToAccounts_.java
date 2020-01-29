package com.nirvanaxp.global.types.entities.devicemgmt;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.184+0530")
@StaticMetamodel(DeviceInfoToAccounts.class)
public class DeviceInfoToAccounts_ {
	public static volatile SingularAttribute<DeviceInfoToAccounts, Integer> id;
	public static volatile SingularAttribute<DeviceInfoToAccounts, Date> updated;
	public static volatile SingularAttribute<DeviceInfoToAccounts, String> updatedBy;
	public static volatile SingularAttribute<DeviceInfoToAccounts, Date> created;
	public static volatile SingularAttribute<DeviceInfoToAccounts, String> createdBy;
	public static volatile SingularAttribute<DeviceInfoToAccounts, String> status;
	public static volatile SingularAttribute<DeviceInfoToAccounts, Integer> accountId;
	public static volatile SingularAttribute<DeviceInfoToAccounts, Integer> deviceInfoId;
}
