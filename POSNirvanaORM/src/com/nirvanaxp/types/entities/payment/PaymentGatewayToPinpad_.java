package com.nirvanaxp.types.entities.payment;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-22T13:53:22.299+0530")
@StaticMetamodel(PaymentGatewayToPinpad.class)
public class PaymentGatewayToPinpad_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<PaymentGatewayToPinpad, Integer> paymentGatewayId;
	public static volatile SingularAttribute<PaymentGatewayToPinpad, String> locationsId;
	public static volatile SingularAttribute<PaymentGatewayToPinpad, String> ipAddress;
	public static volatile SingularAttribute<PaymentGatewayToPinpad, String> macAddress;
	public static volatile SingularAttribute<PaymentGatewayToPinpad, String> port;
	public static volatile SingularAttribute<PaymentGatewayToPinpad, String> terminalId;
	public static volatile SingularAttribute<PaymentGatewayToPinpad, String> secureDeviceName;
	public static volatile SingularAttribute<PaymentGatewayToPinpad, String> transDeviceId;
	public static volatile SingularAttribute<PaymentGatewayToPinpad, Integer> orderSourceGroupToPaymentGatewayTypeId;
	public static volatile SingularAttribute<PaymentGatewayToPinpad, Integer> orderSourceToPaymentGatewayTypeId;
	public static volatile SingularAttribute<PaymentGatewayToPinpad, Integer> emvParam;
}
