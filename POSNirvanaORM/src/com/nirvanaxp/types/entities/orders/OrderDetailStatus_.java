package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-11T14:04:42.903+0530")
@StaticMetamodel(OrderDetailStatus.class)
public class OrderDetailStatus_ {
	public static volatile SingularAttribute<OrderDetailStatus, Integer> id;
	public static volatile SingularAttribute<OrderDetailStatus, Date> created;
	public static volatile SingularAttribute<OrderDetailStatus, String> createdBy;
	public static volatile SingularAttribute<OrderDetailStatus, String> description;
	public static volatile SingularAttribute<OrderDetailStatus, String> displayName;
	public static volatile SingularAttribute<OrderDetailStatus, Integer> displaySequence;
	public static volatile SingularAttribute<OrderDetailStatus, Integer> isServerDriven;
	public static volatile SingularAttribute<OrderDetailStatus, String> locationsId;
	public static volatile SingularAttribute<OrderDetailStatus, String> name;
	public static volatile SingularAttribute<OrderDetailStatus, String> orderSourceGroupId;
	public static volatile SingularAttribute<OrderDetailStatus, String> statusColour;
	public static volatile SingularAttribute<OrderDetailStatus, Date> updated;
	public static volatile SingularAttribute<OrderDetailStatus, String> updatedBy;
	public static volatile SingularAttribute<OrderDetailStatus, String> status;
}
