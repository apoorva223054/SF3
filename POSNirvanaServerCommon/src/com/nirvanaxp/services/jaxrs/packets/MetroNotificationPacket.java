/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MetroNotificationPacket")
public class MetroNotificationPacket
{
	private int totalReservation;
	private int totalReservationPartySize;
	private int totalWalkIn;
	private int totalWalkInPartySize;
	private int totalWaitlist;
	private int totalWaitlistPartySize;
	private int totalTableOccupiedPartySize;
	private int totalTakeoutOpened;
	private int totalTableOccupied;
	private int totalTakeout;
	private int closedOrder;
	private int openOrder;
	private int totalCatering;

	private int totalDeliveryOpened;
	private int totalDelivery;
	private int totalVoidOrder;
	private int totalCancelOrder;
	private int totalClockIn;
	private int totalClockOut;
	private int totalBreakIn;
	private int totalBreakOut;
	private int quickOrderInStore;
	private int totalOpenDeliveryPartySize;
	private int totalOpenTakeoutPartySize;
	private int totalOpenCateringPartySize;
	
	private int totalInventoryOrders;

	public int getTotalInventoryOrders()
	{
		return totalInventoryOrders;
	}

	public void setTotalInventoryOrders(int totalInventoryOrders)
	{
		this.totalInventoryOrders = totalInventoryOrders;
	}

	public int getTotalDeliveryOpened()
	{
		return totalDeliveryOpened;
	}

	public void setTotalDeliveryOpened(int totalDeliveryOpened)
	{
		this.totalDeliveryOpened = totalDeliveryOpened;
	}

	public int getTotalDelivery()
	{
		return totalDelivery;
	}

	public void setTotalDelivery(int totalDelivery)
	{
		this.totalDelivery = totalDelivery;
	}

	public int getTotalReservation()
	{
		return totalReservation;
	}

	public void setTotalReservation(int totalReservation)
	{
		this.totalReservation = totalReservation;
	}

	public int getTotalReservationPartySize()
	{
		return totalReservationPartySize;
	}

	public void setTotalReservationPartySize(int totalReservationPartySize)
	{
		this.totalReservationPartySize = totalReservationPartySize;
	}

	public int getTotalWaitlist()
	{
		return totalWaitlist;
	}

	public void setTotalWaitlist(int totalWaitlist)
	{
		this.totalWaitlist = totalWaitlist;
	}

	public int getTotalWaitlistPartySize()
	{
		return totalWaitlistPartySize;
	}

	public void setTotalWaitlistPartySize(int totalWaitlistPartySize)
	{
		this.totalWaitlistPartySize = totalWaitlistPartySize;
	}

	public int getTotalTableOccupiedPartySize()
	{
		return totalTableOccupiedPartySize;
	}

	public void setTotalTableOccupiedPartySize(int totalTableOccupiedPartySize)
	{
		this.totalTableOccupiedPartySize = totalTableOccupiedPartySize;
	}

	public int getTotalTakeout()
	{
		return totalTakeout;
	}

	public void setTotalTakeout(int totalTakeout)
	{
		this.totalTakeout = totalTakeout;
	}

	public int getClosedOrder()
	{
		return closedOrder;
	}

	public void setClosedOrder(int closedOrder)
	{
		this.closedOrder = closedOrder;
	}

	public int getOpenOrder()
	{
		return openOrder;
	}

	public void setOpenOrder(int openOrder)
	{
		this.openOrder = openOrder;
	}

	public int getTotalWalkIn()
	{
		return totalWalkIn;
	}

	public void setTotalWalkIn(int totalWalkIn)
	{
		this.totalWalkIn = totalWalkIn;
	}

	public int getTotalWalkInPartySize()
	{
		return totalWalkInPartySize;
	}

	public void setTotalWalkInPartySize(int totalWalkInPartySize)
	{
		this.totalWalkInPartySize = totalWalkInPartySize;
	}

	public int getTotalTakeoutOpened()
	{
		return totalTakeoutOpened;
	}

	public void setTotalTakeoutOpened(int totalTakeoutOpened)
	{
		this.totalTakeoutOpened = totalTakeoutOpened;
	}

	public int getTotalTableOccupied()
	{
		return totalTableOccupied;
	}

	public void setTotalTableOccupied(int totalTableOccupied)
	{
		this.totalTableOccupied = totalTableOccupied;
	}

	public int getTotalCancelOrder() {
		return totalCancelOrder;
	}

	public void setTotalCancelOrder(int totalCancelOrder) {
		this.totalCancelOrder = totalCancelOrder;
	}
	public int getTotalVoidOrder() {
		return totalVoidOrder;
	}

	public void setTotalVoidOrder(int totalVoidOrder) {
		this.totalVoidOrder = totalVoidOrder;
	}

	public int getQuickOrderInStore() {
		return quickOrderInStore;
	}

	public void setQuickOrderInStore(int quickOrderInStore) {
		this.quickOrderInStore = quickOrderInStore;
	}

	public int getTotalOpenDeliveryPartySize() {
		return totalOpenDeliveryPartySize;
	}

	public void setTotalOpenDeliveryPartySize(int totalOpenDeliveryPartySize) {
		this.totalOpenDeliveryPartySize = totalOpenDeliveryPartySize;
	}

	public int getTotalOpenTakeoutPartySize() {
		return totalOpenTakeoutPartySize;
	}

	public void setTotalOpenTakeoutPartySize(int totalOpenTakeoutPartySize) {
		this.totalOpenTakeoutPartySize = totalOpenTakeoutPartySize;
	}

	public int getTotalClockIn() {
		return totalClockIn;
	}

	public void setTotalClockIn(int totalClockIn) {
		this.totalClockIn = totalClockIn;
	}

	public int getTotalClockOut() {
		return totalClockOut;
	}

	public void setTotalClockOut(int totalClockOut) {
		this.totalClockOut = totalClockOut;
	}

	public int getTotalBreakIn() {
		return totalBreakIn;
	}

	public void setTotalBreakIn(int totalBreakIn) {
		this.totalBreakIn = totalBreakIn;
	}

	public int getTotalBreakOut() {
		return totalBreakOut;
	}

	public void setTotalBreakOut(int totalBreakOut) {
		this.totalBreakOut = totalBreakOut;
	}

	public int getTotalCatering() {
		return totalCatering;
	}

	public void setTotalCatering(int totalCatering) {
		this.totalCatering = totalCatering;
	}

	public int getTotalOpenCateringPartySize() {
		return totalOpenCateringPartySize;
	}

	public void setTotalOpenCateringPartySize(int totalOpenCateringPartySize) {
		this.totalOpenCateringPartySize = totalOpenCateringPartySize;
	}

	
}
