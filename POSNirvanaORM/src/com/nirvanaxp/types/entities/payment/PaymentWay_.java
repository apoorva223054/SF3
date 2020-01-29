package com.nirvanaxp.types.entities.payment;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:22.415+0530")
@StaticMetamodel(PaymentWay.class)
public class PaymentWay_ {
	public static volatile SingularAttribute<PaymentWay, Integer> id;
	public static volatile SingularAttribute<PaymentWay, Date> created;
	public static volatile SingularAttribute<PaymentWay, String> createdBy;
	public static volatile SingularAttribute<PaymentWay, String> displayName;
	public static volatile SingularAttribute<PaymentWay, Integer> displaySequence;
	public static volatile SingularAttribute<PaymentWay, String> locationsId;
	public static volatile SingularAttribute<PaymentWay, String> name;
	public static volatile SingularAttribute<PaymentWay, String> status;
	public static volatile SingularAttribute<PaymentWay, Date> updated;
	public static volatile SingularAttribute<PaymentWay, String> updatedBy;
}
