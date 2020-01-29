package com.nirvanaxp.global.types.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:21.829+0530")
@StaticMetamodel(Business.class)
public class Business_ {
	public static volatile SingularAttribute<Business, Integer> id;
	public static volatile SingularAttribute<Business, Integer> accountId;
	public static volatile SingularAttribute<Business, Byte> businessAuth;
	public static volatile SingularAttribute<Business, String> businessName;
	public static volatile SingularAttribute<Business, Date> created;
	public static volatile SingularAttribute<Business, String> createdBy;
	public static volatile SingularAttribute<Business, String> email;
	public static volatile SingularAttribute<Business, String> logo;
	public static volatile SingularAttribute<Business, BigDecimal> salesTaxRate1;
	public static volatile SingularAttribute<Business, BigDecimal> salesTaxRate2;
	public static volatile SingularAttribute<Business, BigDecimal> salesTaxRate3;
	public static volatile SingularAttribute<Business, String> schemaName;
	public static volatile SingularAttribute<Business, Integer> timezoneId;
	public static volatile SingularAttribute<Business, Integer> transactionalCurrencyId;
	public static volatile SingularAttribute<Business, Date> updated;
	public static volatile SingularAttribute<Business, String> updatedBy;
	public static volatile SingularAttribute<Business, String> longitude;
	public static volatile SingularAttribute<Business, String> lattitude;
	public static volatile SingularAttribute<Business, String> isAutoDatlightSaving;
	public static volatile SingularAttribute<Business, String> website;
	public static volatile SingularAttribute<Business, BusinessType> businessType;
	public static volatile SingularAttribute<Business, Address> billiAddressId;
	public static volatile SingularAttribute<Business, Address> shippingAddressId;
	public static volatile SingularAttribute<Business, Integer> maxAllowedDevices;
	public static volatile SingularAttribute<Business, String> status;
	public static volatile SingularAttribute<Business, String> isOnlineApp;
}
