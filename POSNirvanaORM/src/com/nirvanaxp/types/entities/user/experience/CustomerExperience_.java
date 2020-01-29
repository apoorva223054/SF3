package com.nirvanaxp.types.entities.user.experience;

import com.nirvanaxp.types.entities.feedback.FeedbackQuestion;
import com.nirvanaxp.types.entities.feedback.Smiley;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-04T14:45:39.121+0530")
@StaticMetamodel(CustomerExperience.class)
public class CustomerExperience_ {
	public static volatile SingularAttribute<CustomerExperience, String> id;
	public static volatile SingularAttribute<CustomerExperience, String> comments;
	public static volatile SingularAttribute<CustomerExperience, Date> created;
	public static volatile SingularAttribute<CustomerExperience, String> createdBy;
	public static volatile SingularAttribute<CustomerExperience, Date> updated;
	public static volatile SingularAttribute<CustomerExperience, String> usersId;
	public static volatile SingularAttribute<CustomerExperience, String> locationsId;
	public static volatile SingularAttribute<CustomerExperience, String> orderHeaderId;
	public static volatile SingularAttribute<CustomerExperience, String> updatedBy;
	public static volatile SingularAttribute<CustomerExperience, FeedbackQuestion> feedbackQuestion;
	public static volatile SingularAttribute<CustomerExperience, Smiley> smiley;
	public static volatile SingularAttribute<CustomerExperience, String> localTime;
}
