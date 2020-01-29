package com.nirvanaxp.global.types.entities.devicemgmt;

import com.nirvanaxp.global.types.entities.DeviceType;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.130+0530")
@StaticMetamodel(DeviceInfo.class)
public class DeviceInfo_ {
	public static volatile SingularAttribute<DeviceInfo, Integer> id;
	public static volatile SingularAttribute<DeviceInfo, String> deviceId;
	public static volatile SingularAttribute<DeviceInfo, String> deviceName;
	public static volatile SingularAttribute<DeviceInfo, DeviceType> deviceType;
	public static volatile SingularAttribute<DeviceInfo, Date> created;
	public static volatile SingularAttribute<DeviceInfo, String> createdBy;
	public static volatile SingularAttribute<DeviceInfo, Date> updated;
	public static volatile SingularAttribute<DeviceInfo, String> updatedBy;
	public static volatile SetAttribute<DeviceInfo, EncryptionKey> encyptionKeyinfos;
}
