package com.nirvanaxp.types.entities.orders;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:10.516+0530")
@StaticMetamodel(DeliveryRules.class)
public class DeliveryRules_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<DeliveryRules, String> name;
	public static volatile SingularAttribute<DeliveryRules, String> distanceType;
	public static volatile SingularAttribute<DeliveryRules, BigDecimal> distance;
	public static volatile SingularAttribute<DeliveryRules, BigDecimal> price;
	public static volatile SingularAttribute<DeliveryRules, String> locationId;
	public static volatile SingularAttribute<DeliveryRules, BigDecimal> freeDeliveryOrderAmount;
}
