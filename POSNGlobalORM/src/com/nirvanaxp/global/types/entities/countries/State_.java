package com.nirvanaxp.global.types.entities.countries;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.099+0530")
@StaticMetamodel(State.class)
public class State_ {
	public static volatile SingularAttribute<State, Integer> id;
	public static volatile SingularAttribute<State, Integer> countryId;
	public static volatile SingularAttribute<State, Double> latitude;
	public static volatile SingularAttribute<State, Double> longitude;
	public static volatile SingularAttribute<State, String> stateName;
	public static volatile SingularAttribute<State, String> status;
	public static volatile SingularAttribute<State, Date> created;
	public static volatile SingularAttribute<State, String> createdBy;
	public static volatile SingularAttribute<State, Date> updated;
	public static volatile SingularAttribute<State, String> updatedBy;
}
