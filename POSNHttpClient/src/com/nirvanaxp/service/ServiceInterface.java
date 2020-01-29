/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.service;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

/**
 * This interface is used to implement callback mechanism. When we request any
 * service to get data for us, the service will return us the response using
 * this method
 * 
 * @author Pos Nirvana 2
 */
public interface ServiceInterface
{

	String technicalError = "Cannot response due to some technical Error";

	String noInternetConnetion = "No Internet Available";

	String invalidUserNamePassword = "Wrong username and password combination. Please try again";

	String errorAttempingLogin = "Error attempting Logon: No entity found for query";

	String createNewUser = "User is not registered. Please create an account";

	String responseTrue = "true";

	String phoneNumberAlreadyInDB = "Phone number already exists in database";
	String userNameAlreadyInDB = "Username already exists in database";
	String userNameAlreadyInGlobalDB = "Username exists in global database already.";
	String emailAlreadyInDB = "Email already exists in database";
	String userCanNotBlank = "username cannot be blank";
	String emailCanNotBlank = "Email cannot be blank";
	String phoneCanNotBlank = "Phone Number cannot be blank";
	String phoneNumberAlreadyInGLOBALDB = "Phone number exists in global database already.";
	String USER_NOT_FOUND = "User Not Found";

	// Object responseTrue = null;
	/**
	 * @param serviceId
	 *            - the service for which you will get the response
	 * @param object
	 *            - the parsed response object
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 * @throws Exception 
	 */
	public void responseFromService(HttpServletRequest httpRequest, EntityManager em, int serviceId, int methodId, Object response) throws JsonGenerationException, JsonMappingException, IOException, Exception;

}
