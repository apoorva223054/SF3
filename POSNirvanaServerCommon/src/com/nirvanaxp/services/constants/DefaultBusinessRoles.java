/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.constants;

import com.nirvanaxp.server.util.NirvanaLogger;

public enum DefaultBusinessRoles {

	Account_Admin("Account Admin", "Account Admin", true, true, false,false,false), Business_Admin("Business Admin", "Business Admin", true, true, false,false,false)
	, POS_Operator("POS Operator", "POS Operator", false, true, false,false,false), 
	POS_Supervisor("POS Supervisor", "POS Supervisor", false, true, false,false,false),
	POS_Cashier("Cashier", "Cashier", false, true, false,false,false),
	POS_Customer("POS Customer", "POS Customer", false, false, true,false,false)
	,Enterprise_Report("Enterprise Report", "Enterprise Report", true, true, false,false,false)
	,Global_Setting("Global Setting", "Global Setting", true, true, false,false,false)
	,Driver("Driver", "Driver", false, false, false,true,false),
	Analytics("Analytics", "Analytics", false, false, false,false,true);
	
	
	private String roleName;
	private String displayName;
	private boolean adminRole;
	private boolean employeeRole;
	private boolean customerRole;
	private boolean driverRole;
	private boolean analyticsRole;
	

	DefaultBusinessRoles(String roleName, String displayName, boolean isAdminRole, boolean isEmployeeRole, boolean isCustomerRole
			,boolean isDriverRole,boolean isAnalyticsRole)
	{
		this.roleName = roleName;
		this.displayName = displayName;
		this.adminRole = isAdminRole;
		this.employeeRole = isEmployeeRole;
		this.customerRole = isCustomerRole;
		this.driverRole = isDriverRole;
		this.analyticsRole=isAnalyticsRole;
	}

	public String getRoleName()
	{
		return roleName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public boolean isAdminRole()
	{
		return adminRole;
	}

	public boolean isEmployeeRole()
	{
		return employeeRole;
	}
	
	public boolean isCustomerRole()
	{
		return customerRole;
	}
	
	public boolean isDriverRole()
	{
		return driverRole;
	}
	

	public boolean isAnalyticsRole() {
		return analyticsRole;
	}

	public void setAnalyticsRole(boolean analyticsRole) {
		this.analyticsRole = analyticsRole;
	}


	private static final NirvanaLogger logger = new NirvanaLogger(DefaultBusinessRoles.class.getName());

	public static DefaultBusinessRoles getByRoleName(String roleName)
	{

		if (DefaultBusinessRoles.Account_Admin.getRoleName().equalsIgnoreCase(roleName))
		{
			return DefaultBusinessRoles.Account_Admin;
		}

		if (DefaultBusinessRoles.Business_Admin.getRoleName().equalsIgnoreCase(roleName))
		{
			return DefaultBusinessRoles.Business_Admin;
		}

		if (DefaultBusinessRoles.POS_Customer.getRoleName().equalsIgnoreCase(roleName))
		{
			return DefaultBusinessRoles.POS_Customer;
		}

		if (DefaultBusinessRoles.POS_Operator.getRoleName().equalsIgnoreCase(roleName))
		{
			return DefaultBusinessRoles.POS_Operator;
		}
		if (DefaultBusinessRoles.POS_Supervisor.getRoleName().equalsIgnoreCase(roleName))
		{
			return DefaultBusinessRoles.POS_Supervisor;
		}
		if (DefaultBusinessRoles.Enterprise_Report.getRoleName().equalsIgnoreCase(roleName))
		{
			return DefaultBusinessRoles.Enterprise_Report;
		}
		if (DefaultBusinessRoles.Global_Setting.getRoleName().equalsIgnoreCase(roleName))
		{
			return DefaultBusinessRoles.Global_Setting;
		}
		if (DefaultBusinessRoles.Driver.getRoleName().equalsIgnoreCase(roleName))
		{
			return DefaultBusinessRoles.Driver;
		}
		if (DefaultBusinessRoles.Analytics.getRoleName().equalsIgnoreCase(roleName))
		{
			return DefaultBusinessRoles.Analytics;
		}
		if (DefaultBusinessRoles.POS_Cashier.getRoleName().equalsIgnoreCase(roleName))
		  {
		   return DefaultBusinessRoles.POS_Cashier;
		  }
		return null;
	}

}
