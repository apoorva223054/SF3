package com.nirvanaxp.types.entities.inventory;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:24.367+0530")
@StaticMetamodel(InventoryStatus.class)
public class InventoryStatus_ {
	public static volatile SingularAttribute<InventoryStatus, Integer> id;
	public static volatile SingularAttribute<InventoryStatus, Date> created;
	public static volatile SingularAttribute<InventoryStatus, String> createdBy;
	public static volatile SingularAttribute<InventoryStatus, String> displayName;
	public static volatile SingularAttribute<InventoryStatus, String> name;
	public static volatile SingularAttribute<InventoryStatus, Date> updated;
	public static volatile SingularAttribute<InventoryStatus, String> updatedBy;
	public static volatile SingularAttribute<InventoryStatus, String> status;
}
