package com.nirvanaxp.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:27.813+0530")
@StaticMetamodel(SocialMedia.class)
public class SocialMedia_ {
	public static volatile SingularAttribute<SocialMedia, Integer> id;
	public static volatile SingularAttribute<SocialMedia, Date> created;
	public static volatile SingularAttribute<SocialMedia, String> createdBy;
	public static volatile SingularAttribute<SocialMedia, String> name;
	public static volatile SingularAttribute<SocialMedia, Date> updated;
	public static volatile SingularAttribute<SocialMedia, String> updatedBy;
}
