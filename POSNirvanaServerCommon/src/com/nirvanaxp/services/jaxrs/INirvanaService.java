/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface INirvanaService
{

	// the security cookie name
	public static final String NIRVANA_SESSION_COOKIE_NAME = "SessionId";
	public static final String NIRVANA_ACCESS_TOKEN_HEADER_NAME = "nxp-access-token";
	public static final String NIRVANA_SEC_WEBSOCKET_HEADER_NAME = "Sec-WebSocket-Protocol";
	
	public static final String GET_METHOD_TYPE = "GET";
	public static final String POST_METHOD_TYPE = "POST";

	// the http request attribute that will store the EntityManager for this
	// request thread
	// public static final String NIRVANA_REQUEST_LOCAL_ENTITYMANAGER =
	// "LocalEM";
	// public static final String NIRVANA_REQUEST_GLOBAL_ENTITYMANAGER =
	// "GlobalEM";

	// the http request attribute that will have the UserSession object
	public static final String NIRVANA_USER_SESSION = "NirvanaUserSession";

	// for online ordering
	public static final String NIRVANA_AUTH_TOKEN = "auth-token";

	@Path("/isAlive")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isAlive();

}
