package com.nirvanaxp.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:21.712+0530")
@StaticMetamodel(Floorplan.class)
public class Floorplan_ {
	public static volatile SingularAttribute<Floorplan, Integer> id;
	public static volatile SingularAttribute<Floorplan, Date> created;
	public static volatile SingularAttribute<Floorplan, String> createdBy;
	public static volatile SingularAttribute<Floorplan, String> imageName;
	public static volatile SingularAttribute<Floorplan, String> locationsId;
	public static volatile SingularAttribute<Floorplan, Date> updated;
	public static volatile SingularAttribute<Floorplan, String> updatedBy;
}
