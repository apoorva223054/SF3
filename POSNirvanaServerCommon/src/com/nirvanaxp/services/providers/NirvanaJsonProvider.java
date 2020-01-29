/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.providers;

import static org.codehaus.jackson.map.SerializationConfig.Feature.WRAP_ROOT_VALUE;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class NirvanaJsonProvider extends JacksonJsonProvider
{

	public NirvanaJsonProvider()
	{
		_mapperConfig.setMapper(getObjectMapper());
		_mapperConfig.getConfiguredMapper().setAnnotationIntrospector(new JaxbAnnotationIntrospector());
	}
	
	
	public ObjectMapper getObjectMapper() 
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.UNWRAP_ROOT_VALUE, true);
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.configure(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		mapper.configure(WRAP_ROOT_VALUE, true);
		
		return mapper;
	}

}
