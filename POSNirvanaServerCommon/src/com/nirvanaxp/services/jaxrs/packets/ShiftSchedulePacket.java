package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderSourceGroupToShiftSchedule;
import com.nirvanaxp.types.entities.orders.OrderSourceToShiftSchedule;
import com.nirvanaxp.types.entities.orders.ShiftSchedule;

@XmlRootElement(name = "ShiftSchedulePacket")
public class ShiftSchedulePacket extends PostPacket{
 
 private ShiftSchedule shiftSchedule;
 
 private OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule;
 private OrderSourceToShiftSchedule orderSourceToShiftSchedule;

 private List<String> blockDates;


 public List<String> getBlockDates() {
  return blockDates;
 }

 public void setBlockDates(List<String> blockDates) {
  this.blockDates = blockDates;
 }

public ShiftSchedule getShiftSchedule() {
	return shiftSchedule;
}

public void setShiftSchedule(ShiftSchedule shiftSchedule) {
	this.shiftSchedule = shiftSchedule;
}

public OrderSourceGroupToShiftSchedule getOrderSourceGroupToShiftSchedule() {
	return orderSourceGroupToShiftSchedule;
}

public void setOrderSourceGroupToShiftSchedule(OrderSourceGroupToShiftSchedule orderSourceGroupToShiftSchedule) {
	this.orderSourceGroupToShiftSchedule = orderSourceGroupToShiftSchedule;
}

public OrderSourceToShiftSchedule getOrderSourceToShiftSchedule() {
	return orderSourceToShiftSchedule;
}

public void setOrderSourceToShiftSchedule(OrderSourceToShiftSchedule orderSourceToShiftSchedule) {
	this.orderSourceToShiftSchedule = orderSourceToShiftSchedule;
}

@Override
public String toString() {
	return "ShiftSchedulePacket [shiftSchedule=" + shiftSchedule + ", orderSourceGroupToShiftSchedule="
			+ orderSourceGroupToShiftSchedule + ", orderSourceToShiftSchedule=" + orderSourceToShiftSchedule
			+ ", blockDates=" + blockDates + "]";
}





}