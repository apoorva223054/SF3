package com.nirvanaxp.types.entities.salestax;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-07-11T18:30:47.756+0530")
@StaticMetamodel(SalesTax.class)
public class SalesTax_ {
	public static volatile SingularAttribute<SalesTax, String> id;
	public static volatile SingularAttribute<SalesTax, Date> created;
	public static volatile SingularAttribute<SalesTax, Date> updated;
	public static volatile SingularAttribute<SalesTax, String> createdBy;
	public static volatile SingularAttribute<SalesTax, String> taxName;
	public static volatile SingularAttribute<SalesTax, String> displayName;
	public static volatile SingularAttribute<SalesTax, BigDecimal> rate;
	public static volatile SingularAttribute<SalesTax, String> locationsId;
	public static volatile SingularAttribute<SalesTax, String> status;
	public static volatile SingularAttribute<SalesTax, Integer> isItemSpecific;
	public static volatile SingularAttribute<SalesTax, String> updatedBy;
	public static volatile SingularAttribute<SalesTax, String> taxId;
	public static volatile SingularAttribute<SalesTax, Integer> numberOfPeople;
	public static volatile SingularAttribute<SalesTax, String> globalId;
	public static volatile SingularAttribute<SalesTax, Integer> optionTypeId;
}
