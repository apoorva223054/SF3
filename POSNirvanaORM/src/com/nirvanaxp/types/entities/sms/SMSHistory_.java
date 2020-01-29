package com.nirvanaxp.types.entities.sms;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-01T15:40:46.894+0530")
@StaticMetamodel(SMSHistory.class)
public class SMSHistory_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<SMSHistory, String> smsText;
	public static volatile SingularAttribute<SMSHistory, String> locationId;
	public static volatile SingularAttribute<SMSHistory, String> phone;
	public static volatile SingularAttribute<SMSHistory, String> referenceId;
	public static volatile SingularAttribute<SMSHistory, Integer> templateId;
	public static volatile SingularAttribute<SMSHistory, String> responceCode;
	public static volatile SingularAttribute<SMSHistory, String> senderId;
	public static volatile SingularAttribute<SMSHistory, String> userId;
}
