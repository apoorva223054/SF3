package com.nirvanaxp.types.entities.user;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:10:05.389+0530")
@StaticMetamodel(UsersToSocialMedia.class)
public class UsersToSocialMedia_ {
	public static volatile SingularAttribute<UsersToSocialMedia, Integer> id;
	public static volatile SingularAttribute<UsersToSocialMedia, Date> created;
	public static volatile SingularAttribute<UsersToSocialMedia, String> createdBy;
	public static volatile SingularAttribute<UsersToSocialMedia, String> status;
	public static volatile SingularAttribute<UsersToSocialMedia, Integer> socialMediaId;
	public static volatile SingularAttribute<UsersToSocialMedia, Date> updated;
	public static volatile SingularAttribute<UsersToSocialMedia, String> updatedBy;
	public static volatile SingularAttribute<UsersToSocialMedia, String> usersId;
}
