/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.roles;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.catalog.category.CategoryToDiscount;
import com.nirvanaxp.types.entities.protocol.RelationalEntitiesForStringId;

/**
 * The persistent class for the roles_to_functions database table.
 * 
 */
@Entity
@Table(name = "roles_to_functions")
@XmlRootElement(name = "roles_to_functions")
public class RolesToFunction extends POSNirvanaBaseClassWithoutGeneratedIds implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "functions_id")
	private String functionsId;

	@Column(name = "roles_id")
	private String rolesId;

	public RolesToFunction()
	{
	}

 

	public String getFunctionsId() {
		 if(functionsId != null && (functionsId.length()==0 || functionsId.equals("0"))){return null;}else{	return functionsId;}
	}



	public void setFunctionsId(String functionsId) {
		this.functionsId = functionsId;
	}



	public String getRolesId()
	{
		 if(rolesId != null && (rolesId.length()==0 || rolesId.equals("0"))){return null;}else{	return rolesId;}
	}

	public void setRolesId(String rolesId)
	{
		this.rolesId = rolesId;
	}

	@Override
	public void setBaseRelation(String baseId)
	{
		setRolesId(baseId);

	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId)
	{
		setFunctionsId(baseToObjectId);

	}

	@Override
	public boolean equals(Object obj)
	{
		// we do this as we need to check if printer relation exists or not
		if (obj instanceof CategoryToDiscount)
		{
			if (this.functionsId==(((RolesToFunction) obj).getFunctionsId()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "RolesToFunction [functionsId=" + functionsId + ", rolesId="
				+ rolesId + "]";
	}
}