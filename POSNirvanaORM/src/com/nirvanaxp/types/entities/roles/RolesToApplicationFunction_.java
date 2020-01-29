package com.nirvanaxp.types.entities.roles;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-09T16:49:08.203+0530")
@StaticMetamodel(RolesToApplicationFunction.class)
public class RolesToApplicationFunction_ {
	public static volatile SingularAttribute<RolesToApplicationFunction, Integer> id;
	public static volatile SingularAttribute<RolesToApplicationFunction, Integer> applicationId;
	public static volatile SingularAttribute<RolesToApplicationFunction, Date> created;
	public static volatile SingularAttribute<RolesToApplicationFunction, String> createdBy;
	public static volatile SingularAttribute<RolesToApplicationFunction, String> functionsId;
	public static volatile SingularAttribute<RolesToApplicationFunction, Integer> roleId;
	public static volatile SingularAttribute<RolesToApplicationFunction, Date> updated;
	public static volatile SingularAttribute<RolesToApplicationFunction, String> updatedBy;
}
