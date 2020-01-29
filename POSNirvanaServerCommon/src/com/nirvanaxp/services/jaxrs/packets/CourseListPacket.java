/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.course.Course;
@XmlRootElement(name = "CourseListPacket")
public class CourseListPacket extends PostPacket
{

	List<Course> Course;

	/**
	 * @return the orderStatus
	 */
	public List<Course> getCourse()
	{
		return Course;
	}

	/**
	 * @param orderStatus
	 *            the orderStatus to set
	 */
	public void setCourse(List<Course> Course)
	{
		this.Course = Course;
	}

	@Override
	public String toString()
	{
		return "CourseListPacket [Course=" + Course + "]";
	}

}
