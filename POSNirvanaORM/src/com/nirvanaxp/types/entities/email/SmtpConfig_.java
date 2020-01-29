package com.nirvanaxp.types.entities.email;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:09.213+0530")
@StaticMetamodel(SmtpConfig.class)
public class SmtpConfig_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<SmtpConfig, String> locationId;
	public static volatile SingularAttribute<SmtpConfig, String> senderEmail;
	public static volatile SingularAttribute<SmtpConfig, String> smtpHost;
	public static volatile SingularAttribute<SmtpConfig, String> smtpPassword;
	public static volatile SingularAttribute<SmtpConfig, String> smtpPort;
	public static volatile SingularAttribute<SmtpConfig, String> smtpUsername;
}
