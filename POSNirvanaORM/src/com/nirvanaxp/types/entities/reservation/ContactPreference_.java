package com.nirvanaxp.types.entities.reservation;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:22.713+0530")
@StaticMetamodel(ContactPreference.class)
public class ContactPreference_ {
	public static volatile SingularAttribute<ContactPreference, String> id;
	public static volatile SingularAttribute<ContactPreference, Date> created;
	public static volatile SingularAttribute<ContactPreference, String> createdBy;
	public static volatile SingularAttribute<ContactPreference, String> displayName;
	public static volatile SingularAttribute<ContactPreference, Integer> displaySequence;
	public static volatile SingularAttribute<ContactPreference, String> locationsId;
	public static volatile SingularAttribute<ContactPreference, String> name;
	public static volatile SingularAttribute<ContactPreference, Date> updated;
	public static volatile SingularAttribute<ContactPreference, String> updatedBy;
	public static volatile SingularAttribute<ContactPreference, String> status;
	public static volatile SingularAttribute<ContactPreference, String> description;
}
