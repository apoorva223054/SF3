package com.nirvanaxp.types.entities.email;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:22:46.695+0530")
@StaticMetamodel(EmailHistory.class)
public class EmailHistory_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<EmailHistory, String> emailBody;
	public static volatile SingularAttribute<EmailHistory, String> emailSubject;
	public static volatile SingularAttribute<EmailHistory, Integer> emailTemplateId;
	public static volatile SingularAttribute<EmailHistory, String> fromEmail;
	public static volatile SingularAttribute<EmailHistory, String> locationId;
	public static volatile SingularAttribute<EmailHistory, String> toEmail;
	public static volatile SingularAttribute<EmailHistory, String> ccEmail;
	public static volatile SingularAttribute<EmailHistory, String> referenceId;
	public static volatile SingularAttribute<EmailHistory, String> localTime;
}
