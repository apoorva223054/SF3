/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;
import java.math.BigInteger;

// TODO: Auto-generated Javadoc
/**
 * The Class EmployeeOperationalHoursWithTotalHours.
 */
public class EmployeeOperationalHoursWithTotalHours
{

	/** The employee id. */
	private String employeeId;

	/** The shift id. */
	private String shiftId;

	/** The job role id. */
	private String jobRoleId;

	/** The employee hr sec. */
	private BigDecimal employeeHrSec;

	/** The tip pool id. */
	private int tipPoolId;

	/** The hourly rate. */
	private BigDecimal hourlyRate;

	/** The Total hr sec. */
	private BigDecimal TotalHrSec;


	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * Gets the shift id.
	 *
	 * @return the shift id
	 */
	public String getShiftId()
	{
		 if(shiftId != null && (shiftId.length()==0 || shiftId.equals("0"))){return null;}else{	return shiftId;}
	}

	/**
	 * Sets the shift id.
	 *
	 * @param shiftId
	 *            the new shift id
	 */
	public void setShiftId(String shiftId)
	{
		this.shiftId = shiftId;
	}

	/**
	 * Gets the employee hr sec.
	 *
	 * @return the employee hr sec
	 */
	public BigDecimal getEmployeeHrSec()
	{
		return employeeHrSec;
	}

	/**
	 * Sets the employee hr sec.
	 *
	 * @param employeeHrSec
	 *            the new employee hr sec
	 */
	public void setEmployeeHrSec(BigDecimal employeeHrSec)
	{
		this.employeeHrSec = employeeHrSec;
	}

	/**
	 * Gets the tip pool id.
	 *
	 * @return the tip pool id
	 */
	public int getTipPoolId()
	{
		return tipPoolId;
	}

	/**
	 * Sets the tip pool id.
	 *
	 * @param tipPoolId
	 *            the new tip pool id
	 */
	public void setTipPoolId(int tipPoolId)
	{
		this.tipPoolId = tipPoolId;
	}

	/**
	 * Gets the total hr sec.
	 *
	 * @return the total hr sec
	 */
	public BigDecimal getTotalHrSec()
	{
		return TotalHrSec;
	}

	/**
	 * Sets the total hr sec.
	 *
	 * @param totalHrSec
	 *            the new total hr sec
	 */
	public void setTotalHrSec(BigDecimal totalHrSec)
	{
		TotalHrSec = totalHrSec;
	}

	/**
	 * Gets the hourly rate.
	 *
	 * @return the hourly rate
	 */
	public BigDecimal getHourlyRate()
	{
		return hourlyRate;
	}

	/**
	 * Sets the hourly rate.
	 *
	 * @param hourlyRate
	 *            the new hourly rate
	 */
	public void setHourlyRate(BigDecimal hourlyRate)
	{
		this.hourlyRate = hourlyRate;
	}

	/**
	 * Gets the job role id.
	 *
	 * @return the job role id
	 */
	public String getJobRoleId()
	{
		 if(jobRoleId != null && (jobRoleId.length()==0 || jobRoleId.equals("0"))){return null;}else{	return jobRoleId;}
	}

	/**
	 * Sets the job role id.
	 *
	 * @param jobRoleId
	 *            the new job role id
	 */
	public void setJobRoleId(String jobRoleId)
	{
		this.jobRoleId = jobRoleId;
	}

}
