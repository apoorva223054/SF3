package com.nirvanaxp.services.jaxrs;

import java.util.List;

import com.nirvanaxp.types.entities.employee.EmployeeOperationToCashRegister;

// TODO: Auto-generated Javadoc
/**
 * The Class PaidInPaidOutAndCashRegister.
 */
public class PaidInPaidOutAndCashRegister {

	/** The register info list. */
	private List<RegisterInfo> registerInfoList;
	
	/** The employee operation to cash register. */
	private List<EmployeeOperationToCashRegister> employeeOperationToCashRegister;
		
	

	/**
	 * Gets the register info list.
	 *
	 * @return the register info list
	 */
	public List<RegisterInfo> getRegisterInfoList() {
		return registerInfoList;
	}
	
	/**
	 * Sets the register info list.
	 *
	 * @param registerInfoList the new register info list
	 */
	public void setRegisterInfoList(List<RegisterInfo> registerInfoList) {
		this.registerInfoList = registerInfoList;
	}

	/**
	 * Gets the employee operation to cash register.
	 *
	 * @return the employee operation to cash register
	 */
	public List<EmployeeOperationToCashRegister> getEmployeeOperationToCashRegister() {
		return employeeOperationToCashRegister;
	}
	
	/**
	 * Sets the employee operation to cash register.
	 *
	 * @param employeeOperationToCashRegister the new employee operation to cash register
	 */
	public void setEmployeeOperationToCashRegister(List<EmployeeOperationToCashRegister> employeeOperationToCashRegister) {
		this.employeeOperationToCashRegister = employeeOperationToCashRegister;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaidInPaidOutAndCashRegister [registerInfoList=" + registerInfoList + ", employeeOperationToCashRegister=" + employeeOperationToCashRegister +"]";
	}	
	
}
