package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-12-30T11:40:33.045+0530")
@StaticMetamodel(GoodsReceiveNotes.class)
public class GoodsReceiveNotes_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<GoodsReceiveNotes, String> requestOrderDetailsItemId;
	public static volatile SingularAttribute<GoodsReceiveNotes, String> grnNumber;
	public static volatile SingularAttribute<GoodsReceiveNotes, BigDecimal> rate;
	public static volatile SingularAttribute<GoodsReceiveNotes, BigDecimal> receivedQuantity;
	public static volatile SingularAttribute<GoodsReceiveNotes, BigDecimal> balance;
	public static volatile SingularAttribute<GoodsReceiveNotes, BigDecimal> tax;
	public static volatile SingularAttribute<GoodsReceiveNotes, BigDecimal> total;
	public static volatile SingularAttribute<GoodsReceiveNotes, BigDecimal> price;
	public static volatile SingularAttribute<GoodsReceiveNotes, Integer> isAllotment;
	public static volatile SingularAttribute<GoodsReceiveNotes, BigDecimal> allotmentQty;
	public static volatile SingularAttribute<GoodsReceiveNotes, Integer> isGRNClose;
	public static volatile SingularAttribute<GoodsReceiveNotes, String> date;
	public static volatile SingularAttribute<GoodsReceiveNotes, String> grnDate;
	public static volatile SingularAttribute<GoodsReceiveNotes, BigDecimal> unitPrice;
	public static volatile SingularAttribute<GoodsReceiveNotes, BigDecimal> unitPurchasedPrice;
	public static volatile SingularAttribute<GoodsReceiveNotes, BigDecimal> unitTaxRate;
	public static volatile SingularAttribute<GoodsReceiveNotes, String> supplierRefNo;
	public static volatile SingularAttribute<GoodsReceiveNotes, String> localTime;
	public static volatile SingularAttribute<GoodsReceiveNotes, String> departmentId;
}
