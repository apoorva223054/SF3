/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLUtility
{

	private static final NirvanaLogger logger = new NirvanaLogger(SQLUtility.class.getName());

	public static void closeResultSetAndStatement(ResultSet rs, Statement stmt)
	{

		try
		{
			if (rs != null && !rs.isClosed())
			{
				rs.close();
			}
		}
		catch (Throwable t)
		{
			logger.severe(t);
		}
		try
		{
			if (stmt != null && !stmt.isClosed())
			{
				stmt.close();
			}
		}
		catch (Throwable t)
		{
			logger.severe(t);
		}
	}

	public static void closeConnection(Connection connection)
	{
		try
		{
			if (connection != null && !connection.isClosed())
			{
				connection.close();
			}
		}
		catch (Throwable t)
		{
			logger.severe(t);
		}
	}

	public static void closeResources(Connection con, ResultSet rs, Statement stmt)
	{
		closeResultSetAndStatement(rs, stmt);
		closeConnection(con);
	}

}
