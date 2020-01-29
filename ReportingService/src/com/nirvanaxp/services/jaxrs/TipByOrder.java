package com.nirvanaxp.services.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Tip_by_order")
public class TipByOrder
{
	
	private String batchStartTime;
	private String shiftName;
	private int batchNumber;
	private String employeeLoginId;
	private String employeeHoursInShift;
	private String employeeName;
	private String employeePosition;
	private double totalCash;
	private double totalCard;
	private double totalCreditTerm;
	private double dept15E;
	private double dept15K;
	private double dept15F;
	private double deptOthers;
	private double tax;
	private double directCashTip;
	private double directCardTip;
	private double directCreditTermTip;
	private double indirectCashTip;
	private double indirectCardTip;
	private double indirectCreditTermTip;
	
	private String sectionName;
	private String sectionId;
	private String orderSourceGroupId;
	private String orderSourceGroupName;
	private double gratuity;
	private String restaurantCode;
	
	
	
	public String getBatchStartTime()
	{
		return batchStartTime;
	}
	public void setBatchStartTime(String batchStartTime)
	{
		this.batchStartTime = batchStartTime;
	}
	
	public String getShiftName()
	{
		return shiftName;
	}
	public void setShiftName(String shiftName)
	{
		this.shiftName = shiftName;
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
	public double getDept15E()
	{
		return dept15E;
	}
	public void setDept15E(double dept15e)
	{
		dept15E = dept15e;
	}
	public double getDept15K()
	{
		return dept15K;
	}
	public void setDept15K(double dept15k)
	{
		dept15K = dept15k;
	}
	public double getDept15F()
	{
		return dept15F;
	}
	public void setDept15F(double dept15f)
	{
		dept15F = dept15f;
	}
	
	public double getDeptOthers()
	{
		return deptOthers;
	}
	public void setDeptOthers(double deptOthers)
	{
		this.deptOthers = deptOthers;
	}
	public double getTax()
	{
		return tax;
	}
	public void setTax(double tax)
	{
		this.tax = tax;
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
	public String getSectionName()
	{
		return sectionName;
	}
	public void setSectionName(String sectionName)
	{
		this.sectionName = sectionName;
	}
	public String getSectionId()
	{
		 if(sectionId != null && (sectionId.length()==0 || sectionId.equals("0"))){return null;}else{	return sectionId;}
	}
	public void setSectionId(String sectionId)
	{
		this.sectionId = sectionId;
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
	 
	public String getRestaurantCode()
	{
		return restaurantCode;
	}
	public void setRestaurantCode(String restaurantCode)
	{
		this.restaurantCode = restaurantCode;
	}
	public double getGratuity()
	{
		return gratuity;
	}
	public void setGratuity(double gratuity)
	{
		this.gratuity = gratuity;
	}
	@Override
	public String toString()
	{
		return "TipByOrder [batchStartTime=" + batchStartTime + ", shiftName=" + shiftName + ", batchNumber=" + batchNumber + ", employeeLoginId=" + employeeLoginId + ", employeeHoursInShift="
				+ employeeHoursInShift + ", employeeName=" + employeeName + ", employeePosition=" + employeePosition + ", totalCash=" + totalCash + ", totalCard=" + totalCard + ", totalCreditTerm="
				+ totalCreditTerm + ", dept15E=" + dept15E + ", dept15K=" + dept15K + ", dept15F=" + dept15F + ", deptOthers=" + deptOthers + ", tax=" + tax + ", directCashTip=" + directCashTip
				+ ", directCardTip=" + directCardTip + ", directCreditTermTip=" + directCreditTermTip + ", indirectCashTip=" + indirectCashTip + ", indirectCardTip=" + indirectCardTip
				+ ", indirectCreditTermTip=" + indirectCreditTermTip + ", sectionName=" + sectionName + ", sectionId=" + sectionId + ", orderSourceGroupId=" + orderSourceGroupId
				+ ", orderSourceGroupName=" + orderSourceGroupName + ", gratuity=" + gratuity + ", restaurantCode=" + restaurantCode + "]";
	}
	 
}
