package com.nirvanaxp.types.entities.salestax;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;


@StaticMetamodel(OrderSourceGroupToSalesTax.class)
public class OrderSourceGroupToSalesTax_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<OrderSourceGroupToSalesTax, Integer> sourceGroupId;
	public static volatile SingularAttribute<OrderSourceGroupToSalesTax, Integer> taxId;
	public static volatile SingularAttribute<OrderSourceGroupToSalesTax, Integer> locationsId;
}
