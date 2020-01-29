package com.nirvanaxp.types.entities.feedback;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:21.627+0530")
@StaticMetamodel(FeedbackField.class)
public class FeedbackField_ {
	public static volatile SingularAttribute<FeedbackField, Integer> id;
	public static volatile SingularAttribute<FeedbackField, Date> created;
	public static volatile SingularAttribute<FeedbackField, String> createdBy;
	public static volatile SingularAttribute<FeedbackField, Integer> displaySequence;
	public static volatile SingularAttribute<FeedbackField, String> fieldName;
	public static volatile SingularAttribute<FeedbackField, String> displayName;
	public static volatile SingularAttribute<FeedbackField, Integer> fieldTypeId;
	public static volatile SingularAttribute<FeedbackField, String> locationsId;
	public static volatile SingularAttribute<FeedbackField, String> status;
	public static volatile SingularAttribute<FeedbackField, Date> updated;
	public static volatile SingularAttribute<FeedbackField, String> updatedBy;
}
