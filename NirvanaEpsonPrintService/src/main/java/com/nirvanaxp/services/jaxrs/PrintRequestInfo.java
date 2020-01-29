/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

// TODO: Auto-generated Javadoc
/**
 * The Class PrintRequestInfo.
 */
@XmlRootElement(name = "PrintRequestInfo")
public class PrintRequestInfo
{

	
	/** The order id. */
private String orderId = null;

	/**
	 * Gets the order id.
	 *
	 * @return the order id
	 */
	public String getOrderId()
	{
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}
	
	/**
	 * Sets the order id.
	 *
	 * @param orderId the new order id
	 */
	public void setOrderId(String orderId)
	{
		this.orderId = orderId;
	}

	/**
	 * The Class POSPrint.
	 */
	@XmlRootElement(name = "ePOSPrint")
	private class POSPrint
	{

		/**
		 * The Class ParameterTag.
		 */
		@XmlRootElement(name="Parameter")
		private class ParameterTag
		{
			
			/** The devid. */
			@XmlElement(name="devid")
			private String devid;
			
			/** The timeout. */
			@XmlElement(name="timeout")
			private int timeout;
			
			

		}
		
		

	}
}
