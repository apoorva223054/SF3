package com.nirvanaxp.types.entities.printers;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-01T15:26:03.701+0530")
@StaticMetamodel(PrintersType.class)
public class PrintersType_ {
	public static volatile SingularAttribute<PrintersType, String> id;
	public static volatile SingularAttribute<PrintersType, Date> created;
	public static volatile SingularAttribute<PrintersType, String> createdBy;
	public static volatile SingularAttribute<PrintersType, String> locationsId;
	public static volatile SingularAttribute<PrintersType, String> name;
	public static volatile SingularAttribute<PrintersType, String> status;
	public static volatile SingularAttribute<PrintersType, Date> updated;
	public static volatile SingularAttribute<PrintersType, String> updatedBy;
	public static volatile SingularAttribute<PrintersType, String> globalPrintersTypeId;
}
