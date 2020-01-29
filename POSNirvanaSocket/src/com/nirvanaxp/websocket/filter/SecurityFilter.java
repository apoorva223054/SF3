package com.nirvanaxp.websocket.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.jaxrs.INirvanaService;

public class SecurityFilter implements Filter {
	
	public static final String NXP_WEBSOCKET_SESSION_DATA = "NXP-WebSocketSession-Data";
	
	private static final NirvanaLogger logger = new NirvanaLogger(SecurityFilter.class.getName());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		// look for access token
		String accessToken = httpRequest.getHeader(INirvanaService.NIRVANA_SEC_WEBSOCKET_HEADER_NAME);
		
		if(accessToken!=null)
		{
			httpResponse.setHeader(INirvanaService.NIRVANA_SEC_WEBSOCKET_HEADER_NAME, accessToken);
		}
		
		// if not in header then look in cookie
		else
		{
			Cookie[] cookies = httpRequest.getCookies();
			
			if(cookies!=null && cookies.length>0)
			{
				for(Cookie c : cookies)
				{
					if(c!=null && INirvanaService.NIRVANA_SESSION_COOKIE_NAME.equals(c.getName()))
					{
						accessToken = c.getValue();
						logger.info("Found Session: ", accessToken);
						break;
					}
					else
					{
						logger.fine("Skipping cookie: ",c.getName(), c.getValue());
					}
				}
			}
			else
			{
				logger.fine("No cookies found on request");
			}
		}
	
		logger.info("Working with access token:", accessToken);
		
		try
		{
			// inspect query string
			String qs = httpRequest.getQueryString();
			logger.info("Incoming socket request has QS:", qs);
			// if query string is from internal MDB, then skip security check
			if(qs!=null && qs.toUpperCase().contains("CLIENTID=JWEBSOCKET&CLIENTTYPE=JWEBSOCKET") && httpRequest.getServerName().equalsIgnoreCase("localhost"))
			{
				// do nothing, this is broadcast MDB connecting
				logger.fine("Found Incoming socket request for MDB with Query String:",qs);
				
				chain.doFilter(httpRequest, response);
			}
			else
			{
				// if query string is null, then get query string from improper client request 
				if(qs==null){
					qs = getQueryStringFromIncorrectRequestURL(httpRequest);
				}
				
				// if access token is still null
				if(accessToken==null)
				{
					// try to get it from query string
					// TODO - This needs to be removed, session id must come from cookies, or from access token in header
					accessToken = getSessionFromQueryString(qs);
					logger.fine("Found session id from query string", accessToken);
					if(accessToken==null)
					{
						throw new InvalidSessionException();
					}
				}
				
				logger.fine("getting user session for session id:", accessToken);
				UserSession us = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, accessToken);
				logger.fine("Got User Session for session id:", us.toString());
								
				final Map<String, String[]> fakedParams = Collections.singletonMap(NXP_WEBSOCKET_SESSION_DATA, 
						new String[] { "SessionId="+accessToken+";MerchantId="+us.getMerchant_id() });
		        HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
		            @Override
		            public Map<String, String[]> getParameterMap() {
		                return fakedParams;
		            }
		        };
				logger.fine("completed socket filter processing, continue with chain");
		        chain.doFilter(wrappedRequest, response);				
			}	        
		}
		catch(Throwable ise)
		{
			logger.severe(ise);
			throw new ServletException(ise.getMessage());
		}		
	}

	private String getQueryStringFromIncorrectRequestURL(HttpServletRequest httpRequest) {
		StringBuffer url = httpRequest.getRequestURL();
		int index =  url.indexOf(";");
		if(url!=null && index>0)
		{
			String qs = url.substring(++index);
			logger.fine("Returning QS from incorrect request as: "+qs);
			return qs;
		}
		throw new IllegalArgumentException("Could not extract query string from URL: "+url);
	}

	private String getSessionFromQueryString(String qs) {
		String paramsArr[] = qs.split("&");
		if(paramsArr!=null && paramsArr.length>0)
		{
			for (int index = 0; index < paramsArr.length; index++)
			{
				paramsArr[index].trim();
				String key = paramsArr[index].substring(0, paramsArr[index].indexOf("=")).toLowerCase();
				String data = paramsArr[index].substring(paramsArr[index].indexOf("=") + 1);
				logger.fine("Processing key and data", key, data);
				switch (key)
				{
					case "sessionid" :
					{
						return data;
					}
					default :
					{
						logger.info("Skipping query string param for websocket session init: ",paramsArr[index]);
					}
				}
	
			}
		}
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
