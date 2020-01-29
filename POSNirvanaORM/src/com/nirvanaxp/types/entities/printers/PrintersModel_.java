package com.nirvanaxp.types.entities.printers;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:22.622+0530")
@StaticMetamodel(PrintersModel.class)
public class PrintersModel_ {
	public static volatile SingularAttribute<PrintersModel, Integer> id;
	public static volatile SingularAttribute<PrintersModel, Date> created;
	public static volatile SingularAttribute<PrintersModel, String> createdBy;
	public static volatile SingularAttribute<PrintersModel, String> displayName;
	public static volatile SingularAttribute<PrintersModel, Integer> displaySequence;
	public static volatile SingularAttribute<PrintersModel, String> locationsId;
	public static volatile SingularAttribute<PrintersModel, String> modelNumber;
	public static volatile SingularAttribute<PrintersModel, String> printersInterface;
	public static volatile SingularAttribute<PrintersModel, String> printersManufacturer;
	public static volatile SingularAttribute<PrintersModel, Date> updated;
	public static volatile SingularAttribute<PrintersModel, String> updatedBy;
	public static volatile SingularAttribute<PrintersModel, Integer> globalPrintersModelId;
	public static volatile SingularAttribute<PrintersModel, String> status;
}
