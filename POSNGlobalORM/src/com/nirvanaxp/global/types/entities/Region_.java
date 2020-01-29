package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.600+0530")
@StaticMetamodel(Region.class)
public class Region_ {
	public static volatile SingularAttribute<Region, Integer> id;
	public static volatile SingularAttribute<Region, String> cloudVendor;
	public static volatile SingularAttribute<Region, String> country;
	public static volatile SingularAttribute<Region, Date> created;
	public static volatile SingularAttribute<Region, String> createdBy;
	public static volatile SingularAttribute<Region, String> region;
	public static volatile SingularAttribute<Region, String> status;
	public static volatile SingularAttribute<Region, Date> updated;
	public static volatile SingularAttribute<Region, String> updatedBy;
}
