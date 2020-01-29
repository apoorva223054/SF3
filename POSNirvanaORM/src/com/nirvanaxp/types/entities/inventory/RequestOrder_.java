package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-12-30T11:40:33.125+0530")
@StaticMetamodel(RequestOrder.class)
public class RequestOrder_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<RequestOrder, String> locationId;
	public static volatile SingularAttribute<RequestOrder, String> name;
	public static volatile SingularAttribute<RequestOrder, String> date;
	public static volatile SingularAttribute<RequestOrder, String> supplierId;
	public static volatile SingularAttribute<RequestOrder, String> purchaseOrderId;
	public static volatile SingularAttribute<RequestOrder, String> statusId;
	public static volatile SingularAttribute<RequestOrder, Integer> isPOOrder;
	public static volatile SingularAttribute<RequestOrder, Integer> isDirectRequestAllocation;
	public static volatile SingularAttribute<RequestOrder, Integer> grnCount;
	public static volatile SingularAttribute<RequestOrder, String> departmentId;
	public static volatile SingularAttribute<RequestOrder, String> orderSourceGroupId;
	public static volatile SingularAttribute<RequestOrder, BigDecimal> priceTax1;
	public static volatile SingularAttribute<RequestOrder, BigDecimal> priceTax2;
	public static volatile SingularAttribute<RequestOrder, BigDecimal> priceTax3;
	public static volatile SingularAttribute<RequestOrder, BigDecimal> priceTax4;
	public static volatile SingularAttribute<RequestOrder, String> taxName1;
	public static volatile SingularAttribute<RequestOrder, String> taxName2;
	public static volatile SingularAttribute<RequestOrder, String> taxName3;
	public static volatile SingularAttribute<RequestOrder, String> taxName4;
	public static volatile SingularAttribute<RequestOrder, String> taxDisplayName1;
	public static volatile SingularAttribute<RequestOrder, String> taxDisplayName2;
	public static volatile SingularAttribute<RequestOrder, String> taxDisplayName3;
	public static volatile SingularAttribute<RequestOrder, String> taxDisplayName4;
	public static volatile SingularAttribute<RequestOrder, BigDecimal> taxRate1;
	public static volatile SingularAttribute<RequestOrder, BigDecimal> taxRate2;
	public static volatile SingularAttribute<RequestOrder, BigDecimal> taxRate3;
	public static volatile SingularAttribute<RequestOrder, BigDecimal> taxRate4;
	public static volatile SingularAttribute<RequestOrder, String> localTime;
}
