package com.nirvanaxp.types.entities.payment;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-01T13:57:22.297+0530")
@StaticMetamodel(PaymentMethod.class)
public class PaymentMethod_ {
	public static volatile SingularAttribute<PaymentMethod, String> id;
	public static volatile SingularAttribute<PaymentMethod, Date> created;
	public static volatile SingularAttribute<PaymentMethod, String> createdBy;
	public static volatile SingularAttribute<PaymentMethod, String> description;
	public static volatile SingularAttribute<PaymentMethod, String> displayName;
	public static volatile SingularAttribute<PaymentMethod, Integer> displaySequence;
	public static volatile SingularAttribute<PaymentMethod, Integer> isActive;
	public static volatile SingularAttribute<PaymentMethod, String> locationsId;
	public static volatile SingularAttribute<PaymentMethod, String> name;
	public static volatile SingularAttribute<PaymentMethod, Date> updated;
	public static volatile SingularAttribute<PaymentMethod, String> updatedBy;
	public static volatile SingularAttribute<PaymentMethod, String> paymentMethodTypeId;
	public static volatile SingularAttribute<PaymentMethod, String> status;
}
