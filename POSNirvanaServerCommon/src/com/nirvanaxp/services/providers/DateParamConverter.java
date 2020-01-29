package com.nirvanaxp.services.providers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import com.nirvanaxp.server.util.NirvanaLogger;

@Provider
public class DateParamConverter  implements ParamConverterProvider
{
	
	private static final NirvanaLogger logger = new NirvanaLogger(DateParamConverter.class.getName());

	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
	{
		if(rawType.getName().equals(Date.class.getName()))
		{
			return new ParamConverter<T>()
			{

				@Override
				public T fromString(String value)
				{
					Date d = null;
					if(value!=null)
					{
						// if sent as "yyyy-MM-dd"
						if(value.length()==10)
						{
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							
							try
							{
								 d = sdf.parse(value);
							}
							catch (ParseException e)
							{
								logger.severe(e, "Error parsing input date param: ", value);
							}
						}
						
						// if sent as "yyyy-MM-dd%20HH:mm:ss"
//						if(value.length()==21)
//						{
//							value.replace("%20", " ");
//							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//							
//							try
//							{
//								 d = sdf.parse(value);
//							}
//							catch (ParseException e)
//							{
//								logger.severe(e, "Error parsing input date param: ", value);
//							}
//						}
					}
					return rawType.cast(d);
				}

				@Override
				public String toString(T arg0)
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss.SSSZ");
					sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
					return sdf.format(arg0);
				}
				
			};
		}
		return null;
	}

	

}
