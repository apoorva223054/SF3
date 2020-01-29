package com.nirvanaxp.types.entities.user;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-15T15:43:11.681+0530")
@StaticMetamodel(UsersToPayment.class)
public class UsersToPayment_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<UsersToPayment, BigDecimal> amount;
	public static volatile SingularAttribute<UsersToPayment, Integer> paymentTypeId;
	public static volatile SingularAttribute<UsersToPayment, String> usersId;
	public static volatile SingularAttribute<UsersToPayment, String> paymentMethodTypeId;
}
