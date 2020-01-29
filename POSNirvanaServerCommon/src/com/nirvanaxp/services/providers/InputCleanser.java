/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.owasp.esapi.ESAPI;

import com.nirvanaxp.server.util.NirvanaLogger;

@Provider
public class InputCleanser implements ContainerRequestFilter, ContainerResponseFilter
{

	private static final NirvanaLogger logger = new NirvanaLogger(InputCleanser.class.getName());

	// ContainerRequestFilter
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException
	{
		logger.fine("Filter for incoming invoked");

		clean(requestContext);
	}
	
	// ContainerResponseFilter
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
	{
//		logger.fine("Filter for outgoing invoked");
//		logOutgoing(responseContext);
		// encode?
	}


	private void clean(ContainerRequestContext requestContext)
	{
		// SecurityContext securityContext =
		// requestContext.getSecurityContext();
		// String authentication = securityContext.getAuthenticationScheme();
		// Principal userPrincipal = securityContext.getUserPrincipal();
		
		UriInfo uriInfo = requestContext.getUriInfo();
		if(uriInfo.getAbsolutePath()!=null && uriInfo.getAbsolutePath().getPath()!=null
				&& uriInfo.getAbsolutePath().getPath().endsWith("isAlive"))
		{
			// skip all logging for isAlive calls
			return;
		}
		
		logURI(uriInfo);
		
		Map<String, Cookie> cookieMap = requestContext.getCookies();
		logCookies(cookieMap);
		
		MultivaluedMap<String, String> headers = requestContext.getHeaders();
		logHeaders(headers);
		
		// String method = requestContext.getMethod();
		// List<Object> matchedResources = uriInfo.getMatchedResources();
		// ...
				
		// Clean the query strings
        cleanParams("QueryParams:", uriInfo.getQueryParameters());
 
        // Clean the path parameters
        cleanParams("PathParams:", uriInfo.getPathParameters());
        
        // clean cookies
        cleanCookies(cookieMap);
        
        // clean headers
        cleanParams("Headers:", headers);
 
	}

	

	private void cleanCookies(Map<String, Cookie> cookies) {
		for(Map.Entry<String, Cookie> cookie : cookies.entrySet())
		{
			String value = cookie.getValue().getValue();
			value = cleanValue(value);
			logger.fine("Cleaned Cookie:{", cookie.getKey(), ":", value, "}");
		}
	}

	private void logURI(UriInfo uriInfo)
	{
		if (uriInfo == null)
		{
			logger.fine("There is no incoming URI Info!!");
			return;
		}

		if(logger.isLoggable(Level.FINE))
		{		
			logger.fine("Absolute Path:", uriInfo.getAbsolutePath() != null ? uriInfo.getAbsolutePath().toString() : "No Abs Path", "\nPath:", uriInfo.getPath(), "\nPath Params:",
					extractFromMap(uriInfo.getPathParameters()), "\nQuery Params: ", extractFromMap(uriInfo.getQueryParameters()));
		}
	}

	private void logCookies(Map<String, Cookie> cookieMap) {
		if(logger.isLoggable(Level.FINE))
		{
			for(Map.Entry<String, Cookie> cookie : cookieMap.entrySet())
			{
				String value = cookie.getValue().getValue();
				logger.fine("Incoming Cookie:{", cookie.getKey(), ":", value, "}");
			}
		}
	}
	
	private void logHeaders(MultivaluedMap<String, String> headers) {
		if(logger.isLoggable(Level.FINE))
		{
			for(Map.Entry<String, List<String>> header : headers.entrySet())
		
			{
				List<String> valueList = header.getValue();
				for(String value : valueList)
				{
					logger.fine("Incoming Header:{", header.getKey(), ":", value, "}");
				}
			}
		}
	}

	private String extractFromMap(MultivaluedMap<String, String> map)
	{
		if (map == null)
		{
			return "[NULL]";
		}
		
		StringBuilder data = new StringBuilder();
		data.append("[");
		for (String key : map.keySet())
		{
			data.append(key);
			data.append(":");
			data.append(map.get(key));
		}

		data.append("]");

		return data.toString();
	}

	
	/**
	 * clean the data from the passed in map.
	 * @param parameters
	 */
    private void cleanParams(String name, MultivaluedMap<String, String> parameters )
    {
     
    	StringBuilder builder = new StringBuilder();
    	builder.append(name);
        for( Map.Entry<String, List<String>> params : parameters.entrySet() )
        {
            String key = params.getKey();
            builder.append("{");
            builder.append(key);
            builder.append("=");
            List<String> values = params.getValue();
            
            List<String> cleanValues = new ArrayList<String>();
            for( String value : values )
            {
            	String cleanValue = cleanValue(value);
                cleanValues.add(cleanValue);
                builder.append(cleanValue);
                builder.append(",");
            }
 
            parameters.put( key, cleanValues );
            builder.append("}");
        }
 
        logger.fine("XSS Vulnerabilities removed: [", builder.toString(), "]");
    }


    /**
     * use ESAPI and jsoup to clean values.
     * @param value
     * @return
     */
    private String cleanValue(String value)
    {
        if(value == null)
            return null;
     
        // Use the ESAPI library to avoid encoded attacks.
        value = ESAPI.encoder().canonicalize(value);
 
        // Avoid null characters
        value = value.replaceAll("\0", "");
 
        // Clean out HTML, only simple text is allowed
        value = Jsoup.clean(value, Whitelist.none());
 
        return value;
    }
    
//	private void logOutgoing(ContainerResponseContext responseContext)
//	{
//		// MultivaluedMap<String, String> stringHeaders =
//		// responseContext.getStringHeaders();
//		// Object entity = responseContext.getEntity();
//		// ...
//	}

}
