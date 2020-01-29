package com.nirvanaxp.types.entities.locations;

import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:24.674+0530")
@StaticMetamodel(MetaBusinessTypeToApplication.class)
public class MetaBusinessTypeToApplication_ {
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, Integer> id;
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, String> applicationName;
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, Integer> applicationsId;
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, Integer> businessTypeId;
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, Timestamp> created;
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, String> createdBy;
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, String> displayName;
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, String> status;
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, Timestamp> updated;
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, String> updatedBy;
	public static volatile SingularAttribute<MetaBusinessTypeToApplication, String> imageUrl;
}
