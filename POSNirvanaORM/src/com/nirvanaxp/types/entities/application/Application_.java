package com.nirvanaxp.types.entities.application;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:23.070+0530")
@StaticMetamodel(Application.class)
public class Application_ {
	public static volatile SingularAttribute<Application, Integer> id;
	public static volatile SingularAttribute<Application, Date> created;
	public static volatile SingularAttribute<Application, String> createdBy;
	public static volatile SingularAttribute<Application, String> displayName;
	public static volatile SingularAttribute<Application, String> name;
	public static volatile SingularAttribute<Application, String> status;
	public static volatile SingularAttribute<Application, Date> updated;
	public static volatile SingularAttribute<Application, String> updatedBy;
}
