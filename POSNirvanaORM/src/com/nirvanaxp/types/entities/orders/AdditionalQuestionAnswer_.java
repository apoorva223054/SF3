package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-04T15:13:57.718+0530")
@StaticMetamodel(AdditionalQuestionAnswer.class)
public class AdditionalQuestionAnswer_ {
	public static volatile SingularAttribute<AdditionalQuestionAnswer, String> id;
	public static volatile SingularAttribute<AdditionalQuestionAnswer, String> orderHeaderId;
	public static volatile SingularAttribute<AdditionalQuestionAnswer, Integer> questionId;
	public static volatile SingularAttribute<AdditionalQuestionAnswer, String> answerValue;
	public static volatile SingularAttribute<AdditionalQuestionAnswer, Date> created;
	public static volatile SingularAttribute<AdditionalQuestionAnswer, String> createdBy;
	public static volatile SingularAttribute<AdditionalQuestionAnswer, Date> updated;
	public static volatile SingularAttribute<AdditionalQuestionAnswer, String> updatedBy;
	public static volatile SingularAttribute<AdditionalQuestionAnswer, Integer> displaySequence;
}
