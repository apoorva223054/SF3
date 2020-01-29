/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.function.Function;
import com.nirvanaxp.types.entities.roles.Role;
@XmlRootElement(name = "RolePacket")
public class RolePacket extends PostPacket
{
	private Role role;

	private List<Function> functionsList;

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public List<Function> getFunctionsList()
	{
		return functionsList;
	}

	public void setFunctionsList(List<Function> functionsList)
	{
		this.functionsList = functionsList;
	}

	@Override
	public String toString()
	{
		return "RolePacket [role=" + role + ", functionsList=" + functionsList + "]";
	}

}
