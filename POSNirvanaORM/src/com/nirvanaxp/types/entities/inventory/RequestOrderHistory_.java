package com.nirvanaxp.types.entities.inventory;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithBigIntWithGeneratedId_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-26T15:46:02.317+0530")
@StaticMetamodel(RequestOrderHistory.class)
public class RequestOrderHistory_ extends POSNirvanaBaseClassWithBigIntWithGeneratedId_ {
	public static volatile SingularAttribute<RequestOrderHistory, String> locationId;
	public static volatile SingularAttribute<RequestOrderHistory, String> name;
	public static volatile SingularAttribute<RequestOrderHistory, String> supplierId;
	public static volatile SingularAttribute<RequestOrderHistory, String> purchaseOrderId;
	public static volatile SingularAttribute<RequestOrderHistory, String> requestOrderId;
	public static volatile SingularAttribute<RequestOrderHistory, String> date;
	public static volatile SingularAttribute<RequestOrderHistory, String> statusId;
	public static volatile SingularAttribute<RequestOrderHistory, Integer> isPOOrder;
	public static volatile SingularAttribute<RequestOrderHistory, Integer> isDirectRequestAllocation;
	public static volatile SingularAttribute<RequestOrderHistory, Integer> grnCount;
	public static volatile SingularAttribute<RequestOrderHistory, BigDecimal> priceTax1;
	public static volatile SingularAttribute<RequestOrderHistory, BigDecimal> priceTax2;
	public static volatile SingularAttribute<RequestOrderHistory, BigDecimal> priceTax3;
	public static volatile SingularAttribute<RequestOrderHistory, BigDecimal> priceTax4;
	public static volatile SingularAttribute<RequestOrderHistory, String> taxName1;
	public static volatile SingularAttribute<RequestOrderHistory, String> taxName2;
	public static volatile SingularAttribute<RequestOrderHistory, String> taxName3;
	public static volatile SingularAttribute<RequestOrderHistory, String> taxName4;
	public static volatile SingularAttribute<RequestOrderHistory, String> taxDisplayName1;
	public static volatile SingularAttribute<RequestOrderHistory, String> taxDisplayName2;
	public static volatile SingularAttribute<RequestOrderHistory, String> taxDisplayName3;
	public static volatile SingularAttribute<RequestOrderHistory, String> taxDisplayName4;
	public static volatile SingularAttribute<RequestOrderHistory, BigDecimal> taxRate1;
	public static volatile SingularAttribute<RequestOrderHistory, BigDecimal> taxRate2;
	public static volatile SingularAttribute<RequestOrderHistory, BigDecimal> taxRate3;
	public static volatile SingularAttribute<RequestOrderHistory, BigDecimal> taxRate4;
	public static volatile SingularAttribute<RequestOrderHistory, String> localTime;
}
