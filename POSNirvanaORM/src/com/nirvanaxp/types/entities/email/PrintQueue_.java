package com.nirvanaxp.types.entities.email;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithBigIntWithGeneratedId_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-26T15:05:31.111+0530")
@StaticMetamodel(PrintQueue.class)
public class PrintQueue_ extends POSNirvanaBaseClassWithBigIntWithGeneratedId_ {
	public static volatile SingularAttribute<PrintQueue, String> printString;
	public static volatile SingularAttribute<PrintQueue, String> orderId;
	public static volatile SingularAttribute<PrintQueue, Integer> accountId;
	public static volatile SingularAttribute<PrintQueue, String> locationId;
	public static volatile SingularAttribute<PrintQueue, String> orderDetailItemId;
	public static volatile SingularAttribute<PrintQueue, String> printerId;
	public static volatile SingularAttribute<PrintQueue, String> localTime;
	public static volatile SingularAttribute<PrintQueue, Integer> isOrderAhead;
	public static volatile SingularAttribute<PrintQueue, String> scheduleDateTime;
}
