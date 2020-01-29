/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfo;

public final class NirvanaContext
{

	private User user;
	private DeviceInfo device;
	private HttpServletRequest httpRequest;
	private HttpServletResponse httpResponse;

	public NirvanaContext(User user, DeviceInfo device, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
	{
		this.user = user;
		this.device = device;
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;
	}

	public User getUser()
	{
		return user;
	}

	public DeviceInfo getDevice()
	{
		return device;
	}

	public HttpServletRequest getHttpRequest()
	{
		return httpRequest;
	}

	public HttpServletResponse getHttpResponse()
	{
		return httpResponse;
	}
}
