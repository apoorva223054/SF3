package com.nirvanaxp.types.entities.discounts;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-25T18:08:03.302+0530")
@StaticMetamodel(DiscountsType.class)
public class DiscountsType_ {
	public static volatile SingularAttribute<DiscountsType, String> id;
	public static volatile SingularAttribute<DiscountsType, Date> created;
	public static volatile SingularAttribute<DiscountsType, String> createdBy;
	public static volatile SingularAttribute<DiscountsType, String> discountsType;
	public static volatile SingularAttribute<DiscountsType, String> displayName;
	public static volatile SingularAttribute<DiscountsType, Integer> displaySequence;
	public static volatile SingularAttribute<DiscountsType, String> locationsId;
	public static volatile SingularAttribute<DiscountsType, String> status;
	public static volatile SingularAttribute<DiscountsType, Date> updated;
	public static volatile SingularAttribute<DiscountsType, String> updatedBy;
	public static volatile SingularAttribute<DiscountsType, String> globalDiscountTypeId;
}
