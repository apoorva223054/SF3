package com.nirvanaxp.types.entities.payment;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:22.323+0530")
@StaticMetamodel(PaymentGatewayType.class)
public class PaymentGatewayType_ {
	public static volatile SingularAttribute<PaymentGatewayType, Integer> id;
	public static volatile SingularAttribute<PaymentGatewayType, String> status;
	public static volatile SingularAttribute<PaymentGatewayType, Date> created;
	public static volatile SingularAttribute<PaymentGatewayType, String> createdBy;
	public static volatile SingularAttribute<PaymentGatewayType, Date> updated;
	public static volatile SingularAttribute<PaymentGatewayType, String> updatedBy;
	public static volatile SingularAttribute<PaymentGatewayType, String> name;
	public static volatile SingularAttribute<PaymentGatewayType, String> displayName;
	public static volatile SingularAttribute<PaymentGatewayType, Integer> displaySequence;
	public static volatile SingularAttribute<PaymentGatewayType, String> locationsId;
	public static volatile SingularAttribute<PaymentGatewayType, String> paymentGatewayTransactionUrl;
}
