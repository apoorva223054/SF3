package com.nirvanaxp.services.jaxrs;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.exceptions.InvalidSessionException;

public class NirvanaSecurityFilter implements Filter {
	
	private static final NirvanaLogger logger = new NirvanaLogger(NirvanaSecurityFilter.class.getName());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = ((HttpServletRequest) request);
		try
		{	
			if(!httpRequest.getRequestURI().endsWith("isAlive"))
			{
				// look for security header
				String accessToken = httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME);
							
				// if no header found then check cookie
				if(accessToken==null){
					// and log message, so we know which client is still using cookies
					logger.severe(httpRequest, "Could not find access token header, going to check cookies");
					Cookie[] allCookies = httpRequest.getCookies();
					if(allCookies!=null && allCookies.length>0)
					{
						for(Cookie c : allCookies)
						{
							if(c!=null && INirvanaService.NIRVANA_SESSION_COOKIE_NAME.equals(c.getName()))
							{
								accessToken = c.getValue();
								logger.info("Found Session:", accessToken);
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
			}

			// continue chain
			chain.doFilter(request, response);
		}
		catch(Throwable ise)
		{
			logger.severe(ise);
			throw new ServletException(ise.getMessage());
		}
		finally
		{
			// destroy any reference to EM on request object
//			clearEMOnRequest(httpRequest);
		}
	}
	

	/**
	 * close and clear entity manager and attributes form http request
	 * @param httpRequest
	 */
//	private void clearEMOnRequest(HttpServletRequest httpRequest) {
//		LocalSchemaEntityManager.getInstance().closeEntityManager((EntityManager)httpRequest.getAttribute(INirvanaService.NIRVANA_REQUEST_LOCAL_ENTITYMANAGER));
//		GlobalSchemaEntityManager.getInstance().closeEntityManager((EntityManager)httpRequest.getAttribute(INirvanaService.NIRVANA_REQUEST_GLOBAL_ENTITYMANAGER));
//		httpRequest.removeAttribute(INirvanaService.NIRVANA_REQUEST_GLOBAL_ENTITYMANAGER);
//		httpRequest.removeAttribute(INirvanaService.NIRVANA_REQUEST_LOCAL_ENTITYMANAGER);
//	}

	/**
	 * initialize and set entity manager as attributes of current request
	 * @param httpRequest
	 * @param emName
	 * @param accessToken
	 * @throws IOException
	 * @throws InvalidSessionException
	 */
//	private void initEMOnRequest(HttpServletRequest httpRequest, String emName, String accessToken) throws IOException, InvalidSessionException {
//		EntityManager em =(EntityManager) httpRequest.getAttribute(emName);
//		if(em!=null)
//		{
//			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
//			httpRequest.removeAttribute(emName);
//		}
//		
//		if(INirvanaService.NIRVANA_REQUEST_GLOBAL_ENTITYMANAGER.equals(emName))
//		{
//			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, accessToken);	
//		}
//		else
//		{
//			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, accessToken);
//		}
//		httpRequest.setAttribute(emName, em);
//	}

	@Override
	public void destroy() {
		
	}

}