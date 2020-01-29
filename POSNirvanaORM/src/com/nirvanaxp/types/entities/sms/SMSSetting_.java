package com.nirvanaxp.types.entities.sms;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:11.276+0530")
@StaticMetamodel(SMSSetting.class)
public class SMSSetting_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<SMSSetting, String> locationId;
	public static volatile SingularAttribute<SMSSetting, String> senderName;
	public static volatile SingularAttribute<SMSSetting, String> password;
	public static volatile SingularAttribute<SMSSetting, String> smsSubscriber;
	public static volatile SingularAttribute<SMSSetting, String> url;
	public static volatile SingularAttribute<SMSSetting, String> username;
}
