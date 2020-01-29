package com.nirvanaxp.types.entities.payment;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:26.456+0530")
@StaticMetamodel(PaymentType.class)
public class PaymentType_ {
	public static volatile SingularAttribute<PaymentType, Integer> id;
	public static volatile SingularAttribute<PaymentType, Date> created;
	public static volatile SingularAttribute<PaymentType, String> createdBy;
	public static volatile SingularAttribute<PaymentType, String> displayName;
	public static volatile SingularAttribute<PaymentType, Integer> displaySequence;
	public static volatile SingularAttribute<PaymentType, String> name;
	public static volatile SingularAttribute<PaymentType, Date> updated;
	public static volatile SingularAttribute<PaymentType, String> updatedBy;
}
