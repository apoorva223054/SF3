package com.nirvanaxp.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:23.603+0530")
@StaticMetamodel(Day.class)
public class Day_ {
	public static volatile SingularAttribute<Day, Integer> id;
	public static volatile SingularAttribute<Day, Date> created;
	public static volatile SingularAttribute<Day, String> createdBy;
	public static volatile SingularAttribute<Day, String> name;
	public static volatile SingularAttribute<Day, Date> updated;
	public static volatile SingularAttribute<Day, String> updatedBy;
}
