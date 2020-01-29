package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T17:38:16.146+0530")
@StaticMetamodel(UnitOfMeasurement.class)
public class UnitOfMeasurement_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<UnitOfMeasurement, String> displayName;
	public static volatile SingularAttribute<UnitOfMeasurement, String> displaySequence;
	public static volatile SingularAttribute<UnitOfMeasurement, String> name;
	public static volatile SingularAttribute<UnitOfMeasurement, Integer> uomTypeId;
	public static volatile SingularAttribute<UnitOfMeasurement, String> stockUomId;
	public static volatile SingularAttribute<UnitOfMeasurement, String> locationId;
	public static volatile SingularAttribute<UnitOfMeasurement, BigDecimal> sellableQty;
	public static volatile SingularAttribute<UnitOfMeasurement, BigDecimal> stockQty;
	public static volatile SingularAttribute<UnitOfMeasurement, String> globalId;
}
