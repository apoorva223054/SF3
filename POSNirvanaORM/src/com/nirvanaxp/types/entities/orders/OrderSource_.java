package com.nirvanaxp.types.entities.orders;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T15:35:53.194+0530")
@StaticMetamodel(OrderSource.class)
public class OrderSource_ {
	public static volatile SingularAttribute<OrderSource, String> id;
	public static volatile SingularAttribute<OrderSource, Date> created;
	public static volatile SingularAttribute<OrderSource, String> createdBy;
	public static volatile SingularAttribute<OrderSource, String> displayName;
	public static volatile SingularAttribute<OrderSource, Integer> displaySequence;
	public static volatile SingularAttribute<OrderSource, String> locationsId;
	public static volatile SingularAttribute<OrderSource, String> name;
	public static volatile SingularAttribute<OrderSource, Date> updated;
	public static volatile SingularAttribute<OrderSource, String> updatedBy;
	public static volatile SingularAttribute<OrderSource, String> orderSourceGroupId;
	public static volatile SingularAttribute<OrderSource, String> status;
	public static volatile SingularAttribute<OrderSource, Integer> isItemisePrintRequired;
	public static volatile SingularAttribute<OrderSource, String> globalId;
	public static volatile SingularAttribute<OrderSource, BigDecimal> minimumOrderAmount;
}
