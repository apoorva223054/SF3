package com.nirvanaxp.types.entities.application;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-09T16:49:07.819+0530")
@StaticMetamodel(ApplicationToFunction.class)
public class ApplicationToFunction_ {
	public static volatile SingularAttribute<ApplicationToFunction, Integer> id;
	public static volatile SingularAttribute<ApplicationToFunction, Integer> applicationsId;
	public static volatile SingularAttribute<ApplicationToFunction, Date> created;
	public static volatile SingularAttribute<ApplicationToFunction, String> createdBy;
	public static volatile SingularAttribute<ApplicationToFunction, String> functionsId;
	public static volatile SingularAttribute<ApplicationToFunction, Date> updated;
	public static volatile SingularAttribute<ApplicationToFunction, String> updatedBy;
	public static volatile SingularAttribute<ApplicationToFunction, String> status;
}
