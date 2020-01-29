package com.nirvanaxp.types.entities.locations;

import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-02T15:44:40.056+0530")
@StaticMetamodel(MetaBusinessTypeToFunction.class)
public class MetaBusinessTypeToFunction_ {
	public static volatile SingularAttribute<MetaBusinessTypeToFunction, Integer> id;
	public static volatile SingularAttribute<MetaBusinessTypeToFunction, Integer> businessTypeId;
	public static volatile SingularAttribute<MetaBusinessTypeToFunction, Timestamp> created;
	public static volatile SingularAttribute<MetaBusinessTypeToFunction, String> createdBy;
	public static volatile SingularAttribute<MetaBusinessTypeToFunction, String> displayName;
	public static volatile SingularAttribute<MetaBusinessTypeToFunction, String> functionsId;
	public static volatile SingularAttribute<MetaBusinessTypeToFunction, String> functionsName;
	public static volatile SingularAttribute<MetaBusinessTypeToFunction, String> status;
	public static volatile SingularAttribute<MetaBusinessTypeToFunction, Timestamp> updated;
	public static volatile SingularAttribute<MetaBusinessTypeToFunction, String> updatedBy;
}
