package com.nirvanaxp.types.entities.printers;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-01T15:19:08.769+0530")
@StaticMetamodel(Printer.class)
public class Printer_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<Printer, String> displayName;
	public static volatile SingularAttribute<Printer, Integer> displaySequence;
	public static volatile SingularAttribute<Printer, String> ipAddress;
	public static volatile SingularAttribute<Printer, Integer> isActive;
	public static volatile SingularAttribute<Printer, String> locationsId;
	public static volatile SingularAttribute<Printer, String> printersName;
	public static volatile SingularAttribute<Printer, Integer> printersModelId;
	public static volatile SingularAttribute<Printer, String> printersTypeId;
	public static volatile SingularAttribute<Printer, String> printersInterfaceId;
	public static volatile SingularAttribute<Printer, Integer> isTableTransferPrint;
	public static volatile SingularAttribute<Printer, String> globalPrinterId;
	public static volatile SingularAttribute<Printer, Integer> isAutoBumpOn;
	public static volatile SingularAttribute<Printer, Integer> cashRegisterToPrinter;
	public static volatile SingularAttribute<Printer, Integer> port;
}
