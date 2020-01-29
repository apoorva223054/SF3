package com.nirvanaxp.types.entities.payment;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-09T16:49:08.150+0530")
@StaticMetamodel(PaymentMethodType.class)
public class PaymentMethodType_ {
	public static volatile SingularAttribute<PaymentMethodType, String> id;
	public static volatile SingularAttribute<PaymentMethodType, Date> created;
	public static volatile SingularAttribute<PaymentMethodType, String> createdBy;
	public static volatile SingularAttribute<PaymentMethodType, String> description;
	public static volatile SingularAttribute<PaymentMethodType, String> displayName;
	public static volatile SingularAttribute<PaymentMethodType, Integer> displaySequence;
	public static volatile SingularAttribute<PaymentMethodType, String> locationsId;
	public static volatile SingularAttribute<PaymentMethodType, String> name;
	public static volatile SingularAttribute<PaymentMethodType, Date> updated;
	public static volatile SingularAttribute<PaymentMethodType, String> updatedBy;
	public static volatile SingularAttribute<PaymentMethodType, Integer> paymentTypeId;
	public static volatile SingularAttribute<PaymentMethodType, String> printersId;
	public static volatile SingularAttribute<PaymentMethodType, String> status;
}
