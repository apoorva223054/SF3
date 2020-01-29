package com.nirvanaxp.types.entities.feedback;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-03T12:44:04.660+0530")
@StaticMetamodel(FeedbackQuestion.class)
public class FeedbackQuestion_ {
	public static volatile SingularAttribute<FeedbackQuestion, String> id;
	public static volatile SingularAttribute<FeedbackQuestion, Date> created;
	public static volatile SingularAttribute<FeedbackQuestion, String> createdBy;
	public static volatile SingularAttribute<FeedbackQuestion, Integer> displaySequence;
	public static volatile SingularAttribute<FeedbackQuestion, String> feedbackQuestion;
	public static volatile SingularAttribute<FeedbackQuestion, Integer> feedbackTypeId;
	public static volatile SingularAttribute<FeedbackQuestion, String> locationsId;
	public static volatile SingularAttribute<FeedbackQuestion, String> status;
	public static volatile SingularAttribute<FeedbackQuestion, Date> updated;
	public static volatile SingularAttribute<FeedbackQuestion, String> updatedBy;
}
