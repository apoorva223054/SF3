/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mosambee.transact.amx.data;

/**
 * @author Prachi class contains the possible response values returned for
 *         address verification (AVS). Note: If the response returned is blank
 *         for this specific field tag, there is a chance that your processor
 *         does not support these AVS codes. These values are Visa specific.
 *         These values are returned by the Payment Server and not the
 *         processor.
 * 
 */

public class AvsResponseCode
{

	public static final char EXACT_ADDRESS_aND_NINE_DIDGIT_MATCH = 'X';
	public static final char YES_ADDRESS_AND_FIVE_DIGIT_ZIP_MATCH = 'Y';
	public static final char ADDRESS_MATCHES_ZIP_DOES_NOT_MATCH = 'A';
	public static final char FIVE_DIGIT_ZIP_MATCHES_ADDRESS_DOESNOT = 'Z';
	public static final char NINE_DIGIT_ZIP_MATCHES_ADDRESS_DOESNOT = 'W';

	public static final char NEITHER_ADDRESS_NOR_ZIP_MATCHES = 'N';
	public static final char ADDRESS_INFO_NOT_AVAILABLE = 'U';
	public static final char ADDRESS_INFO_NOT_AVAILABLE_FOR_CHARERNATIONAL_TANSACTION = 'G';
	public static final char RETRY_SYSTEM_UNAVAILABLE_OR_TIME_OUT = 'R';

	public static final char ERROR_TRANSACTION_UNCHARELLIGIBLE_FOR_AVS_EDIT_ERROR_IN_AVS = 'E';

	public static final char NOT_SUPPORTED_ISSUE_DOESNOT_SUPPORT_AVS_SERVICE = 'S';
	public static final char STREET_MATCH_ADRESS_MATCH_BUT_POSTAL_CODE_DOESNOT = 'B';
	public static final char STREET_ADDRESS_POSTAL_CODE_NOT_VERIFIES = 'C';
	public static final char MATCH_STREET_ADDRESS_AND_POSTAL_CODE_MATCH_CHARERNATIONAL_TRANSACTION = 'D';
	public static final char NOT_VERIFIED_ADDRESS_INFO_NOT_VARIFIED_FOR_INTERNATIONAL_TRANCTION = 'I';

	public static final char MATCH_STREET_ADDRESS_AND_POSTAL_CODES_MATCH = 'M';
	public static final char POSTAL_MATCH_POSTAL_COSE_MATCHES_STREET_DOESNOT = 'P';
	public static final char NO_RESPONSE_SENT = '0';
	public static final char INVALID_AVS_RESPONSE = '5';

}
