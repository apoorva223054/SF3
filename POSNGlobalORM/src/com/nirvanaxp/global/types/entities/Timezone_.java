package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.747+0530")
@StaticMetamodel(Timezone.class)
public class Timezone_ {
	public static volatile SingularAttribute<Timezone, Integer> id;
	public static volatile SingularAttribute<Timezone, Date> created;
	public static volatile SingularAttribute<Timezone, String> createdBy;
	public static volatile SingularAttribute<Timezone, String> displayName;
	public static volatile SingularAttribute<Timezone, Integer> displaySequence;
	public static volatile SingularAttribute<Timezone, String> timezoneName;
	public static volatile SingularAttribute<Timezone, Date> updated;
	public static volatile SingularAttribute<Timezone, String> updatedBy;
}
