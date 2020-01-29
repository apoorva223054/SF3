package com.nirvanaxp.types.entities.payment;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:22.266+0530")
@StaticMetamodel(Paymentgateway.class)
public class Paymentgateway_ {
	public static volatile SingularAttribute<Paymentgateway, Integer> id;
	public static volatile SingularAttribute<Paymentgateway, Date> created;
	public static volatile SingularAttribute<Paymentgateway, String> createdBy;
	public static volatile SingularAttribute<Paymentgateway, Integer> defaultPaymentTransactionId;
	public static volatile SingularAttribute<Paymentgateway, String> locationsId;
	public static volatile SingularAttribute<Paymentgateway, String> merchantId;
	public static volatile SingularAttribute<Paymentgateway, String> password;
	public static volatile SingularAttribute<Paymentgateway, Integer> paymentgatewayTypeId;
	public static volatile SingularAttribute<Paymentgateway, String> status;
	public static volatile SingularAttribute<Paymentgateway, Date> updated;
	public static volatile SingularAttribute<Paymentgateway, String> updatedBy;
	public static volatile SingularAttribute<Paymentgateway, String> licenseId;
	public static volatile SingularAttribute<Paymentgateway, String> siteId;
	public static volatile SingularAttribute<Paymentgateway, String> deviceId;
	public static volatile SingularAttribute<Paymentgateway, String> developerId;
	public static volatile SingularAttribute<Paymentgateway, String> versionNumber;
	public static volatile SingularAttribute<Paymentgateway, String> paymentgatewayTransactionUrl;
}
