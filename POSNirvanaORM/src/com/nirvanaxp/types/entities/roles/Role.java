/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.roles;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

/**
 * The persistent class for the roles database table.
 * 
 */
@Entity
@Table(name = "roles")
@XmlRootElement(name = "roles")
public class Role extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "application_name", length = 32)
	private String applicationName;

	@Column(name = "display_name", nullable = false, length = 64)
	private String displayName;

	@Column(name = "display_sequence", nullable = false)
	private int displaySequence;

	@Column(name = "function_name", length = 32)
	private String functionName;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(name = "role_name", nullable = false, length = 32)
	private String roleName;

	@Column(name = "global_roles_id")
	private int globalRoleId;

	private transient List<RolesToFunction> rolesToFunctions;

	public Role()
	{
	}

	public String getApplicationName()
	{
		return this.applicationName;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public int getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getFunctionName()
	{
		return this.functionName;
	}

	public void setFunctionName(String functionName)
	{
		this.functionName = functionName;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getRoleName()
	{
		return this.roleName;
	}

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}

	public List<RolesToFunction> getRolesToFunctions()
	{
		return rolesToFunctions;
	}

	public void setRolesToFunctions(List<RolesToFunction> rolesToFunctions)
	{
		this.rolesToFunctions = rolesToFunctions;
	}

	public int getGlobalRoleId()
	{
		return globalRoleId;
	}

	public void setGlobalRoleId(int globalRoleId)
	{
		this.globalRoleId = globalRoleId;
	}

	@Override
	public String toString() {
		return "Role [applicationName=" + applicationName + ", displayName="
				+ displayName + ", displaySequence=" + displaySequence
				+ ", functionName=" + functionName + ", locationsId="
				+ locationsId + ", roleName=" + roleName + ", globalRoleId="
				+ globalRoleId + ", rolesToFunctions=" + rolesToFunctions + "]";
	}

}