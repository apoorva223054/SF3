package com.nirvanaxp.types.entity.snssms;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:28.430+0530")
@StaticMetamodel(SmsConfig.class)
public class SmsConfig_ {
	public static volatile SingularAttribute<SmsConfig, Integer> id;
	public static volatile SingularAttribute<SmsConfig, String> createdBy;
	public static volatile SingularAttribute<SmsConfig, Date> created;
	public static volatile SingularAttribute<SmsConfig, Date> updated;
	public static volatile SingularAttribute<SmsConfig, String> updatedBy;
	public static volatile SingularAttribute<SmsConfig, String> status;
	public static volatile SingularAttribute<SmsConfig, String> gatewayName;
	public static volatile SingularAttribute<SmsConfig, String> gatewayUrl;
	public static volatile SingularAttribute<SmsConfig, String> senderId;
}
