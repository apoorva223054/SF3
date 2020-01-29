package com.nirvanaxp.services.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="tip")
public class Tip
{
	
	private String batchStartTime;
	private String shiftId;
	private int batchNumber;
	private String employeeLoginId;
	private String employeeHoursInShift;
	private String employeeName;
	private String employeePosition;
	private double directCashTip;
	private double directCardTip;
	private double directCreditTermTip;
	private double indirectCashTip;
	private double indirectCardTip;
	private double indirectCreditTermTip;
	private double autoGratuity;
	private String orderSourceGroupId;
	private String orderSourceGroupName;
	private String sectionId;
	private String sectionName;
	private String orderNumber;
	private double totalCash;
	private double totalCard;
	private double totalCreditTerm;
	private double _15EFood;
	private double _15KNonAlcoholicBeverage;
	private double _15FAlcoholic;
	public String getOrderNumber()
	{
		 if(orderNumber != null && (orderNumber.length()==0 || orderNumber.equals("0"))){return null;}else{	return orderNumber;}
	}
	public void setOrderNumber(String orderNumber)
	{
		this.orderNumber = orderNumber;
	}
	public double getTotalCash()
	{
		return totalCash;
	}
	public void setTotalCash(double totalCash)
	{
		this.totalCash = totalCash;
	}
	public double getTotalCard()
	{
		return totalCard;
	}
	public void setTotalCard(double totalCard)
	{
		this.totalCard = totalCard;
	}
	public double getTotalCreditTerm()
	{
		return totalCreditTerm;
	}
	public void setTotalCreditTerm(double totalCreditTerm)
	{
		this.totalCreditTerm = totalCreditTerm;
	}
	public double get_15EFood()
	{
		return _15EFood;
	}
	public void set_15EFood(double _15eFood)
	{
		_15EFood = _15eFood;
	}
	public double get_15KNonAlcoholicBeverage()
	{
		return _15KNonAlcoholicBeverage;
	}
	public void set_15KNonAlcoholicBeverage(double _15kNonAlcoholicBeverage)
	{
		_15KNonAlcoholicBeverage = _15kNonAlcoholicBeverage;
	}
	public double get_15FAlcoholic()
	{
		return _15FAlcoholic;
	}
	public void set_15FAlcoholic(double _15fAlcoholic)
	{
		_15FAlcoholic = _15fAlcoholic;
	}
	public double getOthers()
	{
		return others;
	}
	public void setOthers(double others)
	{
		this.others = others;
	}
	public double getTax()
	{
		return tax;
	}
	public void setTax(double tax)
	{
		this.tax = tax;
	}
	public double getDiffrence()
	{
		return diffrence;
	}
	public void setDiffrence(double diffrence)
	{
		this.diffrence = diffrence;
	}
	private double others;
	private double tax;
	private double diffrence;
	

	
	public String getBatchStartTime()
	{
		return batchStartTime;
	}
	public void setBatchStartTime(String batchStartTime)
	{
		this.batchStartTime = batchStartTime;
	}
	public String getShiftId()
	{
		 if(shiftId != null && (shiftId.length()==0 || shiftId.equals("0"))){return null;}else{	return shiftId;}
	}
	public void setShiftId(String shiftId)
	{
		this.shiftId = shiftId;
	}
	public int getBatchNumber()
	{
		return batchNumber;
	}
	public void setBatchNumber(int batchNumber)
	{
		this.batchNumber = batchNumber;
	}
	public String getEmployeeLoginId()
	{
		return employeeLoginId;
	}
	public void setEmployeeLoginId(String employeeLoginId)
	{
		this.employeeLoginId = employeeLoginId;
	}
	public String getEmployeeHoursInShift()
	{
		return employeeHoursInShift;
	}
	public void setEmployeeHoursInShift(String employeeHoursInShift)
	{
		this.employeeHoursInShift = employeeHoursInShift;
	}
	public String getEmployeeName()
	{
		return employeeName;
	}
	public void setEmployeeName(String employeeName)
	{
		this.employeeName = employeeName;
	}
	public String getEmployeePosition()
	{
		return employeePosition;
	}
	public void setEmployeePosition(String employeePosition)
	{
		this.employeePosition = employeePosition;
	}
	public double getDirectCashTip()
	{
		return directCashTip;
	}
	public void setDirectCashTip(double directCashTip)
	{
		this.directCashTip = directCashTip;
	}
	public double getDirectCardTip()
	{
		return directCardTip;
	}
	public void setDirectCardTip(double directCardTip)
	{
		this.directCardTip = directCardTip;
	}
	public double getDirectCreditTermTip()
	{
		return directCreditTermTip;
	}
	public void setDirectCreditTermTip(double directCreditTermTip)
	{
		this.directCreditTermTip = directCreditTermTip;
	}
	public double getIndirectCashTip()
	{
		return indirectCashTip;
	}
	public void setIndirectCashTip(double indirectCashTip)
	{
		this.indirectCashTip = indirectCashTip;
	}
	public double getIndirectCardTip()
	{
		return indirectCardTip;
	}
	public void setIndirectCardTip(double indirectCardTip)
	{
		this.indirectCardTip = indirectCardTip;
	}
	public double getIndirectCreditTermTip()
	{
		return indirectCreditTermTip;
	}
	public void setIndirectCreditTermTip(double indirectCreditTermTip)
	{
		this.indirectCreditTermTip = indirectCreditTermTip;
	}
	public double getAutoGratuity()
	{
		return autoGratuity;
	}
	public void setAutoGratuity(double autoGratuity)
	{
		this.autoGratuity = autoGratuity;
	}
	public String getOrderSourceGroupId()
	{
		 if(orderSourceGroupId != null && (orderSourceGroupId.length()==0 || orderSourceGroupId.equals("0"))){return null;}else{	return orderSourceGroupId;}
	}
	public void setOrderSourceGroupId(String orderSourceGroupId)
	{
		this.orderSourceGroupId = orderSourceGroupId;
	}
	public String getOrderSourceGroupName()
	{
		return orderSourceGroupName;
	}
	public void setOrderSourceGroupName(String orderSourceGroupName)
	{
		this.orderSourceGroupName = orderSourceGroupName;
	}
	public String getSectionId()
	{
		 if(sectionId != null && (sectionId.length()==0 || sectionId.equals("0"))){return null;}else{	return sectionId;}
	}
	public void setSectionId(String sectionId)
	{
		this.sectionId = sectionId;
	}
	public String getSectionName()
	{
		return sectionName;
	}
	public void setSectionName(String sectionName)
	{
		this.sectionName = sectionName;
	}
		 
	
}
