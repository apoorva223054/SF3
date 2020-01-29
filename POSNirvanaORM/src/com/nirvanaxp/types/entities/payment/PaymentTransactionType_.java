package com.nirvanaxp.types.entities.payment;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:22.390+0530")
@StaticMetamodel(PaymentTransactionType.class)
public class PaymentTransactionType_ {
	public static volatile SingularAttribute<PaymentTransactionType, Integer> id;
	public static volatile SingularAttribute<PaymentTransactionType, Date> created;
	public static volatile SingularAttribute<PaymentTransactionType, String> createdBy;
	public static volatile SingularAttribute<PaymentTransactionType, String> displayName;
	public static volatile SingularAttribute<PaymentTransactionType, Integer> displaySequence;
	public static volatile SingularAttribute<PaymentTransactionType, String> name;
	public static volatile SingularAttribute<PaymentTransactionType, Date> updated;
	public static volatile SingularAttribute<PaymentTransactionType, String> updatedBy;
	public static volatile SingularAttribute<PaymentTransactionType, String> locationsId;
	public static volatile SingularAttribute<PaymentTransactionType, String> status;
}
