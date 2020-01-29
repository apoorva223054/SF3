package com.nirvanaxp.types.entity.snssms;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:28.599+0530")
@StaticMetamodel(SmsToFunction.class)
public class SmsToFunction_ {
	public static volatile SingularAttribute<SmsToFunction, Integer> id;
	public static volatile SingularAttribute<SmsToFunction, String> createdBy;
	public static volatile SingularAttribute<SmsToFunction, Date> created;
	public static volatile SingularAttribute<SmsToFunction, Date> updated;
	public static volatile SingularAttribute<SmsToFunction, String> updatedBy;
	public static volatile SingularAttribute<SmsToFunction, String> status;
	public static volatile SingularAttribute<SmsToFunction, Integer> smsTemplateId;
	public static volatile SingularAttribute<SmsToFunction, Integer> functionId;
}
