package com.nirvanaxp.types.entities.user;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2017-02-24T14:10:57.464+0530")
@StaticMetamodel(UserDetails.class)
public class UserDetails_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<UserDetails, String> dateOfBirth;
	public static volatile SingularAttribute<UserDetails, String> dateOfAnniversary;
	public static volatile SingularAttribute<UserDetails, String> referenceId;
}
