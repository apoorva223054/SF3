/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.tip.JobRoles;

@XmlRootElement(name = "JobRolesPacket")
public class JobRolesPacket extends PostPacket
{

	private List<JobRoles> jobRoles;

	public List<JobRoles> getJobRoles()
	{
		return jobRoles;
	}

	public void setJobRoles(List<JobRoles> jobRoles)
	{
		this.jobRoles = jobRoles;
	}

	

	

}
