package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:10:04.148+0530")
@StaticMetamodel(FavBusiness.class)
public class FavBusiness_ {
	public static volatile SingularAttribute<FavBusiness, Integer> id;
	public static volatile SingularAttribute<FavBusiness, Integer> businessId;
	public static volatile SingularAttribute<FavBusiness, Date> created;
	public static volatile SingularAttribute<FavBusiness, String> createdBy;
	public static volatile SingularAttribute<FavBusiness, Date> updated;
	public static volatile SingularAttribute<FavBusiness, String> updatedBy;
	public static volatile SingularAttribute<FavBusiness, String> usersId;
}
