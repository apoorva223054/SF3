package com.nirvanaxp.types.entities.locations.receipt;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T17:00:19.630+0530")
@StaticMetamodel(PrinterReceipt.class)
public class PrinterReceipt_ {
	public static volatile SingularAttribute<PrinterReceipt, String> id;
	public static volatile SingularAttribute<PrinterReceipt, String> status;
	public static volatile SingularAttribute<PrinterReceipt, Date> created;
	public static volatile SingularAttribute<PrinterReceipt, String> createdBy;
	public static volatile SingularAttribute<PrinterReceipt, String> name;
	public static volatile SingularAttribute<PrinterReceipt, String> displayName;
	public static volatile SingularAttribute<PrinterReceipt, String> value;
	public static volatile SingularAttribute<PrinterReceipt, String> alignment;
	public static volatile SingularAttribute<PrinterReceipt, String> position;
	public static volatile SingularAttribute<PrinterReceipt, Date> updated;
	public static volatile SingularAttribute<PrinterReceipt, String> updatedBy;
	public static volatile SingularAttribute<PrinterReceipt, Integer> displaySequence;
	public static volatile SingularAttribute<PrinterReceipt, String> locationId;
}
