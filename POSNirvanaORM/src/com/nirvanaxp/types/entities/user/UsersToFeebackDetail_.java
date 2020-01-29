package com.nirvanaxp.types.entities.user;

import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-26T17:50:37.053+0530")
@StaticMetamodel(UsersToFeebackDetail.class)
public class UsersToFeebackDetail_ {
	public static volatile SingularAttribute<UsersToFeebackDetail, Integer> id;
	public static volatile SingularAttribute<UsersToFeebackDetail, Timestamp> created;
	public static volatile SingularAttribute<UsersToFeebackDetail, String> createdBy;
	public static volatile SingularAttribute<UsersToFeebackDetail, String> detailsValue;
	public static volatile SingularAttribute<UsersToFeebackDetail, Integer> feedbackDetailsId;
	public static volatile SingularAttribute<UsersToFeebackDetail, Timestamp> updated;
	public static volatile SingularAttribute<UsersToFeebackDetail, String> updatedBy;
	public static volatile SingularAttribute<UsersToFeebackDetail, String> usersId;
	public static volatile SingularAttribute<UsersToFeebackDetail, String> orderHeaderId;
	public static volatile SingularAttribute<UsersToFeebackDetail, String> localTime;
}
