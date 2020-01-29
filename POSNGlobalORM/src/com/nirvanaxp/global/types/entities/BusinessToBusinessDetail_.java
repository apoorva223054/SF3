package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:21.967+0530")
@StaticMetamodel(BusinessToBusinessDetail.class)
public class BusinessToBusinessDetail_ {
	public static volatile SingularAttribute<BusinessToBusinessDetail, Integer> id;
	public static volatile SingularAttribute<BusinessToBusinessDetail, Integer> businessDetailsId;
	public static volatile SingularAttribute<BusinessToBusinessDetail, Integer> businessId;
	public static volatile SingularAttribute<BusinessToBusinessDetail, String> comments;
	public static volatile SingularAttribute<BusinessToBusinessDetail, Date> created;
	public static volatile SingularAttribute<BusinessToBusinessDetail, String> createdBy;
	public static volatile SingularAttribute<BusinessToBusinessDetail, Integer> displaySequence;
	public static volatile SingularAttribute<BusinessToBusinessDetail, String> status;
	public static volatile SingularAttribute<BusinessToBusinessDetail, Date> updated;
	public static volatile SingularAttribute<BusinessToBusinessDetail, String> updatedBy;
}
