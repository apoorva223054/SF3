package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T17:35:42.352+0530")
@StaticMetamodel(UnitConversion.class)
public class UnitConversion_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<UnitConversion, String> fromUOMId;
	public static volatile SingularAttribute<UnitConversion, String> toUOMId;
	public static volatile SingularAttribute<UnitConversion, BigDecimal> conversionRatio;
}
