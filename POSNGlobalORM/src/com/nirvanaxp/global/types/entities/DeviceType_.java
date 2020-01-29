package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.368+0530")
@StaticMetamodel(DeviceType.class)
public class DeviceType_ {
	public static volatile SingularAttribute<DeviceType, Integer> id;
	public static volatile SingularAttribute<DeviceType, String> name;
	public static volatile SingularAttribute<DeviceType, Date> created;
	public static volatile SingularAttribute<DeviceType, String> createdBy;
	public static volatile SingularAttribute<DeviceType, String> displayName;
	public static volatile SingularAttribute<DeviceType, Date> updated;
	public static volatile SingularAttribute<DeviceType, String> updatedBy;
	public static volatile SingularAttribute<DeviceType, Integer> isEncryptionkeyRequired;
}
