package com.nirvanaxp.types.entities.feedback;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-03T13:28:06.055+0530")
@StaticMetamodel(CustomerExperienceToUserDetail.class)
public class CustomerExperienceToUserDetail_ {
	public static volatile SingularAttribute<CustomerExperienceToUserDetail, Integer> id;
	public static volatile SingularAttribute<CustomerExperienceToUserDetail, String> createdBy;
	public static volatile SingularAttribute<CustomerExperienceToUserDetail, Integer> customerExperienceId;
	public static volatile SingularAttribute<CustomerExperienceToUserDetail, String> detailsValue;
	public static volatile SingularAttribute<CustomerExperienceToUserDetail, String> feedbackDetailsId;
	public static volatile SingularAttribute<CustomerExperienceToUserDetail, Date> created;
	public static volatile SingularAttribute<CustomerExperienceToUserDetail, Date> updated;
	public static volatile SingularAttribute<CustomerExperienceToUserDetail, String> updatedBy;
}
