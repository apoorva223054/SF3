package com.nirvanaxp.types.entity.snssms;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:11.976+0530")
@StaticMetamodel(SmsToLocationSetting.class)
public class SmsToLocationSetting_ {
	public static volatile SingularAttribute<SmsToLocationSetting, Integer> id;
	public static volatile SingularAttribute<SmsToLocationSetting, String> createdBy;
	public static volatile SingularAttribute<SmsToLocationSetting, Date> created;
	public static volatile SingularAttribute<SmsToLocationSetting, Date> updated;
	public static volatile SingularAttribute<SmsToLocationSetting, String> updatedBy;
	public static volatile SingularAttribute<SmsToLocationSetting, String> status;
	public static volatile SingularAttribute<SmsToLocationSetting, Integer> smsTemplateId;
	public static volatile SingularAttribute<SmsToLocationSetting, String> locationId;
}
