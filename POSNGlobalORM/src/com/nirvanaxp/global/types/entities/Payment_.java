package com.nirvanaxp.global.types.entities;

import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.516+0530")
@StaticMetamodel(Payment.class)
public class Payment_ {
	public static volatile SingularAttribute<Payment, Integer> id;
	public static volatile SingularAttribute<Payment, BigDecimal> amountDue;
	public static volatile SingularAttribute<Payment, Integer> accountId;
	public static volatile SingularAttribute<Payment, Timestamp> createdName;
	public static volatile SingularAttribute<Payment, String> createdBy;
	public static volatile SingularAttribute<Payment, Timestamp> lastPaymentDate;
	public static volatile SingularAttribute<Payment, Timestamp> dueDate;
	public static volatile SingularAttribute<Payment, BigDecimal> paymentAmount;
	public static volatile SingularAttribute<Payment, String> reminderTime;
	public static volatile SingularAttribute<Payment, Timestamp> updatedName;
	public static volatile SingularAttribute<Payment, String> updatedBy;
	public static volatile SingularAttribute<Payment, Timestamp> maxdueDate;
	public static volatile SingularAttribute<Payment, String> paymentReminderMessage;
	public static volatile SingularAttribute<Payment, String> applicationTerminationMessage;
}
