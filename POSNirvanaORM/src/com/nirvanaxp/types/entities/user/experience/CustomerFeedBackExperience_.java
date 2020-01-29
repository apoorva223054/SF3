package com.nirvanaxp.types.entities.user.experience;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-26T17:50:37.023+0530")
@StaticMetamodel(CustomerFeedBackExperience.class)
public class CustomerFeedBackExperience_ {
	public static volatile SingularAttribute<CustomerFeedBackExperience, Integer> id;
	public static volatile SingularAttribute<CustomerFeedBackExperience, String> comments;
	public static volatile SingularAttribute<CustomerFeedBackExperience, Date> created;
	public static volatile SingularAttribute<CustomerFeedBackExperience, String> createdBy;
	public static volatile SingularAttribute<CustomerFeedBackExperience, Date> updated;
	public static volatile SingularAttribute<CustomerFeedBackExperience, String> usersId;
	public static volatile SingularAttribute<CustomerFeedBackExperience, String> locationsId;
	public static volatile SingularAttribute<CustomerFeedBackExperience, String> orderHeaderId;
	public static volatile SingularAttribute<CustomerFeedBackExperience, String> updatedBy;
	public static volatile SingularAttribute<CustomerFeedBackExperience, Integer> managerResponse;
}
