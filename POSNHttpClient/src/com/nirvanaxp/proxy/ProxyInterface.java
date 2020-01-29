/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.proxy;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

// TODO: Auto-generated Javadoc
/**
 * The Interface ProxyInterface.
 */
public interface ProxyInterface
{

	/**
	 * Response obtained from web service.
	 *
	 * @param object
	 *            the response
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 * @throws Exception 
	 */
	public void responseObtainedFromWebService(HttpServletRequest httpRequest, EntityManager em, String str) throws JsonGenerationException, JsonMappingException, IOException, Exception;
}
