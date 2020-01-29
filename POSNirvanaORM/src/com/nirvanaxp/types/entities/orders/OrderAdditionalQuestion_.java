package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:10.628+0530")
@StaticMetamodel(OrderAdditionalQuestion.class)
public class OrderAdditionalQuestion_ {
	public static volatile SingularAttribute<OrderAdditionalQuestion, String> id;
	public static volatile SingularAttribute<OrderAdditionalQuestion, String> question;
	public static volatile SingularAttribute<OrderAdditionalQuestion, Integer> fieldTypeId;
	public static volatile SingularAttribute<OrderAdditionalQuestion, Date> created;
	public static volatile SingularAttribute<OrderAdditionalQuestion, String> createdBy;
	public static volatile SingularAttribute<OrderAdditionalQuestion, Date> updated;
	public static volatile SingularAttribute<OrderAdditionalQuestion, String> updatedBy;
	public static volatile SingularAttribute<OrderAdditionalQuestion, String> status;
	public static volatile SingularAttribute<OrderAdditionalQuestion, String> locationId;
	public static volatile SingularAttribute<OrderAdditionalQuestion, Integer> displaySequence;
}
