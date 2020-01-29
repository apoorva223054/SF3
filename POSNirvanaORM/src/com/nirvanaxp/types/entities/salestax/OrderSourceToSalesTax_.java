package com.nirvanaxp.types.entities.salestax;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T15:08:22.783+0530")
@StaticMetamodel(OrderSourceToSalesTax.class)
public class OrderSourceToSalesTax_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<OrderSourceToSalesTax, String> sourceId;
	public static volatile SingularAttribute<OrderSourceToSalesTax, SalesTax> taxId;
	public static volatile SingularAttribute<OrderSourceToSalesTax, String> locationsId;
}
