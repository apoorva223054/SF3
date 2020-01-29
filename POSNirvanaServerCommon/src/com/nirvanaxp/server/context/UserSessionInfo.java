/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.context;

import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.nirvanaxp.global.types.entities.DeviceType;
import com.nirvanaxp.global.types.entities.Role;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfo;

public class UserSessionInfo
{

	private UserSession userSession;

	private User user;

	private Role role;

	public UserSession getUserSession()
	{
		return userSession;
	}

	public void setUserSession(UserSession userSession)
	{
		this.userSession = userSession;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	private void initializeUserSesion(int index, Object[] rs) throws SQLException
	{

		userSession = new UserSession();
		userSession.setId((int)rs[index]);
		index++;
		userSession.setSchema_name((String)rs[index]);
		index++;
		userSession.setSession_id((String)rs[index]);
		index++;
		userSession.setMerchant_id((int)rs[index]);
		index++;
		userSession.setUser_id((String)rs[index]);
		index++;
		userSession.setUsersRolesId((int)rs[index]);
		index++;
		index++;
		userSession.setLoginTime((Timestamp)rs[index]);
		index++;
		userSession.setLogoutTime((Timestamp)rs[index]);
		index++;

	}

	private void initializeRole(int index, Object[] rs) throws SQLException
	{

		role = new Role();
		role.setId((int)rs[index]);
		index++;
		role.setRoleName((String)rs[index]);
		index++;
		role.setDisplayName((String)rs[index]);
		index++;

	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	private void initializeUser(int index, Object[] rs) throws SQLException
	{

		user = new User();
		user.setId((String)rs[index]);
		index++;
		user.setUsername((String)rs[index]);
		index++;
		user.setFirstName((String)rs[index]);
		index++;
		user.setLastName((String)rs[index]);
		index++;
		user.setPhone((String)rs[index]);
		index++;
		user.setEmail((String)rs[index]);

	}

	private void initializeDeviceInfo(int index, Object[] rs) throws SQLException
	{
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setId((int)rs[index]);
		index++;
		deviceInfo.setDeviceId((String)rs[index]);
		index++;
		userSession.setDeviceInfo(deviceInfo);

	}

	private void initializeDeviceType(int index, Object[] rs) throws SQLException
	{

		DeviceType deviceType = new DeviceType();
		deviceType.setId((int)rs[index]);
		index++;
		deviceType.setName((String)rs[index]);
		index++;
		deviceType.setDisplayName((String)rs[index]);
		index++;
		userSession.getDeviceInfo().setDeviceType(deviceType);

	}

	public void initilizeVariablesUsingResulSet(Object[] rs) throws SQLException
	{
		int index = 0;

		initializeUserSesion(index, rs);
		index = 9;
		initializeRole(index, rs);
		index = 12;
		initializeDeviceInfo(index, rs);
		index = 14;
		initializeDeviceType(index, rs);
		index = 17;
		initializeUser(index, rs);

	}

}
