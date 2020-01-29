package com.nirvanaxp.types.entities.user;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-15T15:34:38.864+0530")
@StaticMetamodel(UsersToPaymentHistory.class)
public class UsersToPaymentHistory_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<UsersToPaymentHistory, BigDecimal> amountPaid;
	public static volatile SingularAttribute<UsersToPaymentHistory, BigDecimal> balanceDue;
	public static volatile SingularAttribute<UsersToPaymentHistory, String> orderPaymentDetailsId;
	public static volatile SingularAttribute<UsersToPaymentHistory, Integer> paymentTypeId;
	public static volatile SingularAttribute<UsersToPaymentHistory, String> locationId;
	public static volatile SingularAttribute<UsersToPaymentHistory, String> usersToPaymentId;
	public static volatile SingularAttribute<UsersToPaymentHistory, String> localTime;
	public static volatile SingularAttribute<UsersToPaymentHistory, String> paymentMethodTypeId;
}
