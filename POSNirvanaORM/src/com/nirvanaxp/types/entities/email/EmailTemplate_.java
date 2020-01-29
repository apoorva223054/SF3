package com.nirvanaxp.types.entities.email;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:08.902+0530")
@StaticMetamodel(EmailTemplate.class)
public class EmailTemplate_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<EmailTemplate, String> emailBody;
	public static volatile SingularAttribute<EmailTemplate, String> emailSubject;
	public static volatile SingularAttribute<EmailTemplate, String> locationId;
	public static volatile SingularAttribute<EmailTemplate, String> operationName;
	public static volatile SingularAttribute<EmailTemplate, String> displayName;
}
