package com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCapResponse
{
	@JsonProperty("RStream")
	private RStream rStream;

	@Override
	public String toString()
	{
		return "DataCapResponse [rStream=" + rStream + "]";
	}

	public RStream getrStream()
	{
		return rStream;
	}

	public void setrStream(RStream rStream)
	{
		this.rStream = rStream;
	}

	 
	
	
}
