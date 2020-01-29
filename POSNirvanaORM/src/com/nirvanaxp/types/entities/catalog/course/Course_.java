package com.nirvanaxp.types.entities.catalog.course;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-26T18:45:51.764+0530")
@StaticMetamodel(Course.class)
public class Course_ {
	public static volatile SingularAttribute<Course, String> id;
	public static volatile SingularAttribute<Course, String> courseName;
	public static volatile SingularAttribute<Course, String> imageName;
	public static volatile SingularAttribute<Course, String> hexCodeValues;
	public static volatile SingularAttribute<Course, String> description;
	public static volatile SingularAttribute<Course, Date> created;
	public static volatile SingularAttribute<Course, String> createdBy;
	public static volatile SingularAttribute<Course, String> displayName;
	public static volatile SingularAttribute<Course, Integer> displaySequence;
	public static volatile SingularAttribute<Course, Integer> isActive;
	public static volatile SingularAttribute<Course, String> locationsId;
	public static volatile SingularAttribute<Course, Date> updated;
	public static volatile SingularAttribute<Course, String> updatedBy;
	public static volatile SingularAttribute<Course, String> status;
	public static volatile SingularAttribute<Course, String> globalCourseId;
}
