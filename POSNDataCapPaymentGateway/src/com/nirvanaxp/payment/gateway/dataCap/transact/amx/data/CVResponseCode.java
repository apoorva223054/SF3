/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.dataCap.transact.amx.data;

/**
 * @author Prachi contains the possible response values returned for a
 *         CVV2/CVC2/CID check. Note: If the response returned is blank for this
 *         specific field tag, there is a chance that your processor does not
 *         support these CVV response codes.
 * 
 */
public class CVResponseCode
{

	/**
	 * CVV2/CVC2/C ID Match
	 */
	public static final char CVV2_CVC2_CID_MATCH = 'M';

	/**
	 * CVV2/CVC2/C ID Not Match
	 */
	public static final char CVV2_CVC2_CID_NO_MATCH = 'N';
	/**
	 * Not Processed
	 */
	public static final char NOT_PROCESSED = 'P';
	/**
	 * Unknown / Issuer has not certified for CV or issuer has not provided
	 * Visa/MasterCard with the CV encryption keys.
	 */
	public static final char UNKNOWN = 'U';
	/**
	 * Server Provider did not respond
	 */
	public static final char SERVER_PROVIDER_DID_NOT_RESPOND = 'X';
	/**
	 * Issuer indicates that the CV data should be present on the card, but the
	 * merchant has indicated that the CV data is not present on the card.
	 */
	public static final char SERVER_ISSUER_INF_MISMATCH = 'S';

}
