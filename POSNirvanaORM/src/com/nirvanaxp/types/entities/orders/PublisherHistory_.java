package com.nirvanaxp.types.entities.orders;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T12:36:10.778+0530")
@StaticMetamodel(PublisherHistory.class)
public class PublisherHistory_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<PublisherHistory, Integer> publisherId;
	public static volatile SingularAttribute<PublisherHistory, String> serviceName;
	public static volatile SingularAttribute<PublisherHistory, String> serviceURL;
	public static volatile SingularAttribute<PublisherHistory, String> packet;
	public static volatile SingularAttribute<PublisherHistory, String> locationId;
	public static volatile SingularAttribute<PublisherHistory, Integer> accountId;
	public static volatile SingularAttribute<PublisherHistory, String> methodType;
	public static volatile SingularAttribute<PublisherHistory, String> response;
}
