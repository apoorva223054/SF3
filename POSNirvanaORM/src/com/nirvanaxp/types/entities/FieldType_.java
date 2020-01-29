package com.nirvanaxp.types.entities;

import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:24.204+0530")
@StaticMetamodel(FieldType.class)
public class FieldType_ {
	public static volatile SingularAttribute<FieldType, Integer> id;
	public static volatile SingularAttribute<FieldType, Timestamp> created;
	public static volatile SingularAttribute<FieldType, String> createdBy;
	public static volatile SingularAttribute<FieldType, String> fieldTypeName;
	public static volatile SingularAttribute<FieldType, String> fieldDisplayName;
	public static volatile SingularAttribute<FieldType, Timestamp> updated;
	public static volatile SingularAttribute<FieldType, String> updatedBy;
}
