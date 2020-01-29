package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:21.898+0530")
@StaticMetamodel(BusinessDetail.class)
public class BusinessDetail_ {
	public static volatile SingularAttribute<BusinessDetail, Integer> id;
	public static volatile SingularAttribute<BusinessDetail, Integer> businessDetailsTypeId;
	public static volatile SingularAttribute<BusinessDetail, Date> created;
	public static volatile SingularAttribute<BusinessDetail, String> createdBy;
	public static volatile SingularAttribute<BusinessDetail, String> displayName;
	public static volatile SingularAttribute<BusinessDetail, String> name;
	public static volatile SingularAttribute<BusinessDetail, String> status;
	public static volatile SingularAttribute<BusinessDetail, Date> updated;
	public static volatile SingularAttribute<BusinessDetail, String> updatedBy;
}
