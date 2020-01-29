package com.nirvanaxp.types.entities.payment;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:26.510+0530")
@StaticMetamodel(TransactionStatus.class)
public class TransactionStatus_ {
	public static volatile SingularAttribute<TransactionStatus, Integer> id;
	public static volatile SingularAttribute<TransactionStatus, String> status;
	public static volatile SingularAttribute<TransactionStatus, Date> created;
	public static volatile SingularAttribute<TransactionStatus, String> createdBy;
	public static volatile SingularAttribute<TransactionStatus, Date> updated;
	public static volatile SingularAttribute<TransactionStatus, String> updatedBy;
	public static volatile SingularAttribute<TransactionStatus, String> name;
	public static volatile SingularAttribute<TransactionStatus, String> displayName;
	public static volatile SingularAttribute<TransactionStatus, Integer> displaySequence;
	public static volatile SingularAttribute<TransactionStatus, PaymentGatewayType> paymentGatewayType;
}
