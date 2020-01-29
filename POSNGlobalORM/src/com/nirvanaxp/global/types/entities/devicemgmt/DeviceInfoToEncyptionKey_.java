package com.nirvanaxp.global.types.entities.devicemgmt;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-02-24T14:10:54.286+0530")
@StaticMetamodel(DeviceInfoToEncyptionKey.class)
public class DeviceInfoToEncyptionKey_ {
	public static volatile SingularAttribute<DeviceInfoToEncyptionKey, Integer> id;
	public static volatile SingularAttribute<DeviceInfoToEncyptionKey, Date> created;
	public static volatile SingularAttribute<DeviceInfoToEncyptionKey, Date> updated;
	public static volatile SingularAttribute<DeviceInfoToEncyptionKey, Integer> deviceInfoId;
	public static volatile SingularAttribute<DeviceInfoToEncyptionKey, Integer> encryptionKeyId;
}
