package com.nirvanaxp.types.entities.feedback;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:23.788+0530")
@StaticMetamodel(CustomerExperienceToUserFeedback.class)
public class CustomerExperienceToUserFeedback_ {
	public static volatile SingularAttribute<CustomerExperienceToUserFeedback, Integer> id;
	public static volatile SingularAttribute<CustomerExperienceToUserFeedback, Integer> customerExperienceId;
	public static volatile SingularAttribute<CustomerExperienceToUserFeedback, Integer> feedbackQuestionId;
	public static volatile SingularAttribute<CustomerExperienceToUserFeedback, Integer> smileyId;
	public static volatile SingularAttribute<CustomerExperienceToUserFeedback, String> createdBy;
	public static volatile SingularAttribute<CustomerExperienceToUserFeedback, Date> created;
	public static volatile SingularAttribute<CustomerExperienceToUserFeedback, Date> updated;
	public static volatile SingularAttribute<CustomerExperienceToUserFeedback, String> updatedBy;
}
