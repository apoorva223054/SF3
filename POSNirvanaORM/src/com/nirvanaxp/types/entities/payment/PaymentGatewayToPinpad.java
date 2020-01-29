/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.payment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

@Entity
@Table(name = "payment_gateway_to_pinpad")
@XmlRootElement(name = "payment_gateway_to_pinpad")
public class PaymentGatewayToPinpad extends POSNirvanaBaseClassWithoutGeneratedIds implements Serializable
{

	@Column(name = "payment_gateway_id")
	private int paymentGatewayId;
	
	@Column(name = "location_id")
	private String locationsId;

	@Column(name = "ip_address")
	private String ipAddress;
	
	@Column(name = "mac_address")
	private String macAddress;
	
	@Column(name = "port")
	private String port;
	
	@Column(name = "terminal_id")
	private String terminalId;
	
	@Column(name = "secure_device_name")
	private String secureDeviceName;
	
	@Column(name = "trans_device_id")
	private String transDeviceId;
	
	@Column(name = "order_source_group_to_paymentgatewaytype_id")
	private int orderSourceGroupToPaymentGatewayTypeId;

	@Column(name = "order_source_to_paymentgatewaytype_id")
	private int orderSourceToPaymentGatewayTypeId;
	
	@Column(name = "emv_param")
	private int emvParam;



	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getSecureDeviceName() {
		return secureDeviceName;
	}

	public void setSecureDeviceName(String secureDeviceName) {
		this.secureDeviceName = secureDeviceName;
	}

	public String getTransDeviceId() {
		return transDeviceId;
	}

	public void setTransDeviceId(String transDeviceId) {
		this.transDeviceId = transDeviceId;
	}

	public int getPaymentGatewayId() {
		return paymentGatewayId;
	}

	public void setPaymentGatewayId(int paymentGatewayId) {
		this.paymentGatewayId = paymentGatewayId;
	}

	public int getEmvParam() {
		return emvParam;
	}

	public void setEmvParam(int emvParam) {
		this.emvParam = emvParam;
	}

	
	public int getOrderSourceGroupToPaymentGatewayTypeId()
	{
		return orderSourceGroupToPaymentGatewayTypeId;
	}

	public void setOrderSourceGroupToPaymentGatewayTypeId(int orderSourceGroupToPaymentGatewayTypeId)
	{
		this.orderSourceGroupToPaymentGatewayTypeId = orderSourceGroupToPaymentGatewayTypeId;
	}

	public int getOrderSourceToPaymentGatewayTypeId()
	{
		return orderSourceToPaymentGatewayTypeId;
	}

	public void setOrderSourceToPaymentGatewayTypeId(int orderSourceToPaymentGatewayTypeId)
	{
		this.orderSourceToPaymentGatewayTypeId = orderSourceToPaymentGatewayTypeId;
	}

	@Override
	public String toString() {
		return "PaymentGatewayToPinpad [locationsId=" + locationsId + ", ipAddress=" + ipAddress + ", macAddress="
				+ macAddress + ", port=" + port + ", terminalId=" + terminalId + ", secureDeviceName="
				+ secureDeviceName + ", transDeviceId=" + transDeviceId + ", emvParam=" + emvParam + "]";
	}

	 

 
}
