package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.669+0530")
@StaticMetamodel(Template.class)
public class Template_ {
	public static volatile SingularAttribute<Template, Integer> id;
	public static volatile SingularAttribute<Template, String> apiServer;
	public static volatile SingularAttribute<Template, String> apiVersion;
	public static volatile SingularAttribute<Template, Date> created;
	public static volatile SingularAttribute<Template, String> createdBy;
	public static volatile SingularAttribute<Template, String> dbpassword;
	public static volatile SingularAttribute<Template, String> dbserver;
	public static volatile SingularAttribute<Template, String> dburl;
	public static volatile SingularAttribute<Template, String> dbuser;
	public static volatile SingularAttribute<Template, String> description;
	public static volatile SingularAttribute<Template, String> name;
	public static volatile SingularAttribute<Template, Integer> region;
	public static volatile SingularAttribute<Template, Date> updated;
	public static volatile SingularAttribute<Template, String> updatedBy;
}
