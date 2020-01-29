package com.nirvanaxp.types.entities.reasons;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:04:13.544+0530")
@StaticMetamodel(ReasonType.class)
public class ReasonType_ {
	public static volatile SingularAttribute<ReasonType, String> id;
	public static volatile SingularAttribute<ReasonType, String> status;
	public static volatile SingularAttribute<ReasonType, Date> created;
	public static volatile SingularAttribute<ReasonType, String> createdBy;
	public static volatile SingularAttribute<ReasonType, String> name;
	public static volatile SingularAttribute<ReasonType, String> displayName;
	public static volatile SingularAttribute<ReasonType, Date> updated;
	public static volatile SingularAttribute<ReasonType, String> updatedBy;
}
