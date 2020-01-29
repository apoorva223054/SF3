package com.nirvanaxp.types.entities.feedback;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-03T12:41:56.861+0530")
@StaticMetamodel(FeedbackType.class)
public class FeedbackType_ {
	public static volatile SingularAttribute<FeedbackType, Integer> id;
	public static volatile SingularAttribute<FeedbackType, Date> created;
	public static volatile SingularAttribute<FeedbackType, String> createdBy;
	public static volatile SingularAttribute<FeedbackType, String> feedbackTypeName;
	public static volatile SingularAttribute<FeedbackType, String> locationsId;
	public static volatile SingularAttribute<FeedbackType, String> status;
	public static volatile SingularAttribute<FeedbackType, Date> updated;
	public static volatile SingularAttribute<FeedbackType, String> updatedBy;
	public static volatile SingularAttribute<FeedbackType, Integer> averageFeedbackNotification;
}
