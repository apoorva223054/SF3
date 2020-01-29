package com.nirvanaxp.types.entities.locations;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:24.436+0530")
@StaticMetamodel(BusinessType.class)
public class BusinessType_ {
	public static volatile SingularAttribute<BusinessType, Integer> id;
	public static volatile SingularAttribute<BusinessType, Date> created;
	public static volatile SingularAttribute<BusinessType, String> createdBy;
	public static volatile SingularAttribute<BusinessType, String> shortName;
	public static volatile SingularAttribute<BusinessType, String> name;
	public static volatile SingularAttribute<BusinessType, String> imageName;
	public static volatile SingularAttribute<BusinessType, Date> updated;
	public static volatile SingularAttribute<BusinessType, String> updatedBy;
}
