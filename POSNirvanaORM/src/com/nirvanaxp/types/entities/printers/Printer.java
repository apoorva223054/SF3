/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.printers;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.locations.Location;

/**
 * The persistent class for the printers database table.
 * 
 */
@Entity
@Table(name = "printers")
@XmlRootElement(name = "printers")
public class Printer extends POSNirvanaBaseClassWithoutGeneratedIds
{
 
	
	private static final long serialVersionUID = 1L;

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "display_sequence")
	private Integer displaySequence;

	@Column(name = "ip_address", length = 64)
	private String ipAddress;

	@Column(name = "is_active")
	private int isActive;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(name = "printers_name", nullable = false, length = 100)
	private String printersName;

	// uni-directional many-to-one association to PrintersModel
	@Column(name = "printers_model_id", nullable = false)
	private int printersModelId;

	// uni-directional many-to-one association to PrintersModel
	@Column(name = "printers_type_id")
	private String printersTypeId;

	// uni-directional many-to-one association to PrintersModel
	@Column(name = "printers_interface_id")
	private String printersInterfaceId;
	
	
	@Column(name = "is_table_transfer_print")
	private int isTableTransferPrint;
	
	@Column(name = "global_printer_id")
	private String globalPrinterId;
	
	@Column(name = "is_auto_bump_on")
	private int isAutoBumpOn;

	@Column(name = "cash_register_to_printer")
	private int cashRegisterToPrinter;

	private Integer port;
	private transient List<Location> locationList;
	private transient PrintersType printersType;

	

	public PrintersType getPrintersType()
	{
		return printersType;
	}

	public void setPrintersType(PrintersType printersType)
	{
		this.printersType = printersType;
	}

	public Printer()
	{
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public Integer getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(Integer displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getIpAddress()
	{
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public int getIsActive()
	{
		return this.isActive;
	}

	public void setIsActive(int isActive)
	{
		this.isActive = isActive;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getPrintersName()
	{
		return this.printersName;
	}

	public void setPrintersName(String printersName)
	{
		this.printersName = printersName;
	}

	public int getPrintersModelId()
	{
		return printersModelId;
	}

	public void setPrintersModelId(int printersModelId)
	{
		this.printersModelId = printersModelId;
	}

	public Integer getPort()
	{
		return port;
	}

	public void setPort(Integer port)
	{
		this.port = port;
	}

	
	
	public String getPrintersTypeId() {
		 if(printersTypeId != null && (printersTypeId.length()==0 || printersTypeId.equals("0"))){return null;}else{	return printersTypeId;}
	}

	public void setPrintersTypeId(String printersTypeId) {
		this.printersTypeId = printersTypeId;
	}

	public String getPrintersInterfaceId() {
		 if(printersInterfaceId != null && (printersInterfaceId.length()==0 || printersInterfaceId.equals("0"))){return null;}else{	return printersInterfaceId;}
	}

	public void setPrintersInterfaceId(String printersInterfaceId) {
		this.printersInterfaceId = printersInterfaceId;
	}

	public int getIsTableTransferPrint() {
		return isTableTransferPrint;
	}

	public void setIsTableTransferPrint(int isTableTransferPrint) {
		this.isTableTransferPrint = isTableTransferPrint;
	}

	public String getGlobalPrinterId() {
		 if(globalPrinterId != null && (globalPrinterId.length()==0 || globalPrinterId.equals("0"))){return null;}else{	return globalPrinterId;}
	}

	public void setGlobalPrinterId(String globalPrinterId) {
		this.globalPrinterId = globalPrinterId;
	}
	
	public int getCashRegisterToPrinter() {
		return cashRegisterToPrinter;
	}

	public void setCashRegisterToPrinter(int cashRegisterToPrinter) {
		this.cashRegisterToPrinter = cashRegisterToPrinter;
	}

	public Printer getPrinterObject(Printer p){
		Printer printer = new Printer();
		printer.setCreated(p.getCreated());
		printer.setCreatedBy(p.getCreatedBy());
		printer.setDisplayName(p.getDisplayName());
		printer.setDisplaySequence(p.getDisplaySequence());
		printer.setGlobalPrinterId(p.getGlobalPrinterId());
		printer.setIpAddress(p.getIpAddress());
		printer.setIsActive(p.getIsActive());
		printer.setIsTableTransferPrint(p.getIsTableTransferPrint());
		printer.setLocationsId(p.getLocationsId());
		printer.setPort(p.getPort());
		printer.setPrintersInterfaceId(p.getPrintersInterfaceId());
		printer.setPrintersModelId(p.getPrintersModelId());
		printer.setPrintersName(p.getPrintersName());
		printer.setPrintersTypeId(p.getPrintersTypeId());
		printer.setStatus(p.getStatus());
		printer.setUpdated(p.getUpdated());
		printer.setUpdatedBy(p.getUpdatedBy());
		printer.setIsAutoBumpOn(p.getIsAutoBumpOn());
		return printer;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	public int getIsAutoBumpOn() {
		return isAutoBumpOn;
	}

	public void setIsAutoBumpOn(int isAutoBumpOn) {
		this.isAutoBumpOn = isAutoBumpOn;
	}

	@Override
	public String toString() {
		return "Printer [displayName=" + displayName + ", displaySequence="
				+ displaySequence + ", ipAddress=" + ipAddress + ", isActive="
				+ isActive + ", locationsId=" + locationsId + ", printersName="
				+ printersName + ", printersModelId=" + printersModelId
				+ ", printersTypeId=" + printersTypeId
				+ ", printersInterfaceId=" + printersInterfaceId
				+ ", isTableTransferPrint=" + isTableTransferPrint
				+ ", globalPrinterId=" + globalPrinterId + ", isAutoBumpOn="
				+ isAutoBumpOn + ", cashRegisterToPrinter="
				+ cashRegisterToPrinter + ", port=" + port + "]";
	}

	
}