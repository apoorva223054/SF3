package com.nirvanaxp.types.entities.orders;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-25T13:50:16.872+0530")
@StaticMetamodel(ItemToSupplier.class)
public class ItemToSupplier_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<ItemToSupplier, BigDecimal> amount;
	public static volatile SingularAttribute<ItemToSupplier, String> itemId;
	public static volatile SingularAttribute<ItemToSupplier, String> primarySupplierId;
	public static volatile SingularAttribute<ItemToSupplier, String> secondarySupplierId;
	public static volatile SingularAttribute<ItemToSupplier, String> tertiarySupplierId;
}
