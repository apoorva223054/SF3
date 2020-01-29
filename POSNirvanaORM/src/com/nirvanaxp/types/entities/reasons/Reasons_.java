package com.nirvanaxp.types.entities.reasons;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-11-20T11:26:21.495+0530")
@StaticMetamodel(Reasons.class)
public class Reasons_ {
	public static volatile SingularAttribute<Reasons, String> id;
	public static volatile SingularAttribute<Reasons, String> status;
	public static volatile SingularAttribute<Reasons, Date> created;
	public static volatile SingularAttribute<Reasons, String> createdBy;
	public static volatile SingularAttribute<Reasons, String> name;
	public static volatile SingularAttribute<Reasons, String> displayName;
	public static volatile SingularAttribute<Reasons, Date> updated;
	public static volatile SingularAttribute<Reasons, String> updatedBy;
	public static volatile SingularAttribute<Reasons, String> reasonTypeId;
	public static volatile SingularAttribute<Reasons, String> locationsId;
	public static volatile SingularAttribute<Reasons, Integer> displaySequence;
	public static volatile SingularAttribute<Reasons, String> inventoryConsumed;
}
