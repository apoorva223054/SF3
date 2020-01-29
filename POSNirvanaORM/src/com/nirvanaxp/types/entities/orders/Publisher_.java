package com.nirvanaxp.types.entities.orders;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2020-01-27T12:45:11.076+0530")
@StaticMetamodel(Publisher.class)
public class Publisher_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<Publisher, String> serviceName;
	public static volatile SingularAttribute<Publisher, String> serviceURL;
	public static volatile SingularAttribute<Publisher, String> packet;
	public static volatile SingularAttribute<Publisher, String> locationId;
	public static volatile SingularAttribute<Publisher, Integer> accountId;
	public static volatile SingularAttribute<Publisher, String> methodType;
	public static volatile SingularAttribute<Publisher, String> response;
	public static volatile SingularAttribute<Publisher, Integer> retryCount;
}
