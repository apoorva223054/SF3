package com.nirvanaxp.types.entities.reservation;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-02T16:20:22.050+0530")
@StaticMetamodel(RequestType.class)
public class RequestType_ {
	public static volatile SingularAttribute<RequestType, String> id;
	public static volatile SingularAttribute<RequestType, Date> created;
	public static volatile SingularAttribute<RequestType, String> createdBy;
	public static volatile SingularAttribute<RequestType, String> displayName;
	public static volatile SingularAttribute<RequestType, Integer> displaySequence;
	public static volatile SingularAttribute<RequestType, String> locationsId;
	public static volatile SingularAttribute<RequestType, String> requestName;
	public static volatile SingularAttribute<RequestType, Date> updated;
	public static volatile SingularAttribute<RequestType, String> updatedBy;
	public static volatile SingularAttribute<RequestType, String> status;
	public static volatile SingularAttribute<RequestType, String> description;
}
