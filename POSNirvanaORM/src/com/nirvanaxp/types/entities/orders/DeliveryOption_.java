package com.nirvanaxp.types.entities.orders;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:10.435+0530")
@StaticMetamodel(DeliveryOption.class)
public class DeliveryOption_ {
	public static volatile SingularAttribute<DeliveryOption, String> id;
	public static volatile SingularAttribute<DeliveryOption, String> createdBy;
	public static volatile SingularAttribute<DeliveryOption, String> displayName;
	public static volatile SingularAttribute<DeliveryOption, Integer> displaySequence;
	public static volatile SingularAttribute<DeliveryOption, Integer> optionTypeId;
	public static volatile SingularAttribute<DeliveryOption, String> locationId;
	public static volatile SingularAttribute<DeliveryOption, String> name;
	public static volatile SingularAttribute<DeliveryOption, String> status;
	public static volatile SingularAttribute<DeliveryOption, String> parameter1;
	public static volatile SingularAttribute<DeliveryOption, String> parameter2;
	public static volatile SingularAttribute<DeliveryOption, String> parameter3;
	public static volatile SingularAttribute<DeliveryOption, String> parameter4;
	public static volatile SingularAttribute<DeliveryOption, String> parameter5;
	public static volatile SingularAttribute<DeliveryOption, BigDecimal> amount;
	public static volatile SingularAttribute<DeliveryOption, Date> created;
	public static volatile SingularAttribute<DeliveryOption, Date> updated;
	public static volatile SingularAttribute<DeliveryOption, String> updatedBy;
	public static volatile SingularAttribute<DeliveryOption, Integer> isDefault;
}
