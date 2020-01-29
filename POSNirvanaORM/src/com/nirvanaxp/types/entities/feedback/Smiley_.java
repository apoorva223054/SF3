package com.nirvanaxp.types.entities.feedback;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-03T12:45:01.471+0530")
@StaticMetamodel(Smiley.class)
public class Smiley_ {
	public static volatile SingularAttribute<Smiley, Integer> id;
	public static volatile SingularAttribute<Smiley, Integer> feedbackTypeId;
	public static volatile SingularAttribute<Smiley, Date> created;
	public static volatile SingularAttribute<Smiley, String> createdBy;
	public static volatile SingularAttribute<Smiley, String> simleyName;
	public static volatile SingularAttribute<Smiley, String> imageName;
	public static volatile SingularAttribute<Smiley, Date> updated;
	public static volatile SingularAttribute<Smiley, String> updatedBy;
	public static volatile SingularAttribute<Smiley, Integer> starValue;
}
