package com.nirvanaxp.types.entities.discounts;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T18:00:06.674+0530")
@StaticMetamodel(Discount.class)
public class Discount_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<Discount, String> code;
	public static volatile SingularAttribute<Discount, String> comments;
	public static volatile SingularAttribute<Discount, String> description;
	public static volatile SingularAttribute<Discount, String> discountsTypeId;
	public static volatile SingularAttribute<Discount, BigDecimal> discountsValue;
	public static volatile SingularAttribute<Discount, String> displayName;
	public static volatile SingularAttribute<Discount, Integer> displaySequence;
	public static volatile SingularAttribute<Discount, Date> effectiveEndDate;
	public static volatile SingularAttribute<Discount, Date> effectiveStartDate;
	public static volatile SingularAttribute<Discount, Integer> isActive;
	public static volatile SingularAttribute<Discount, Integer> isFeatured;
	public static volatile SingularAttribute<Discount, String> locationsId;
	public static volatile SingularAttribute<Discount, String> name;
	public static volatile SingularAttribute<Discount, String> shortDescription;
	public static volatile SingularAttribute<Discount, Integer> isGroup;
	public static volatile SingularAttribute<Discount, String> globalId;
	public static volatile SingularAttribute<Discount, Integer> isCoupan;
	public static volatile SingularAttribute<Discount, Integer> isAutoGenerated;
	public static volatile SingularAttribute<Discount, String> coupanCode;
	public static volatile SingularAttribute<Discount, BigDecimal> minOrderAmount;
	public static volatile SingularAttribute<Discount, Integer> isScanCode;
	public static volatile SingularAttribute<Discount, Integer> emailTemplateId;
	public static volatile SingularAttribute<Discount, Integer> smsTemplateId;
	public static volatile SingularAttribute<Discount, Integer> numberOfTimeDiscountUsed;
	public static volatile SingularAttribute<Discount, Integer> isAllCustomer;
}
