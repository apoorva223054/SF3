/*
 * Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except
 * in compliance with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nirvanaxp.common.utils;

import java.util.Collections;
import java.util.Map;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.nirvanaxp.server.util.NirvanaLogger;


/**
 * <p>
 * Encrypts and then decrypts a string under a KMS key
 * 
 * <p>
 * Arguments:
 * <ol>
 * <li>Key ARN: For help finding the Amazon Resource Name (ARN) of your KMS customer master 
 *    key (CMK), see 'Viewing Keys' at http://docs.aws.amazon.com/kms/latest/developerguide/viewing-keys.html
 * <li>String to encrypt
 * </ol>
 */
public class EncryptionDecryption {
	private final static NirvanaLogger logger = new NirvanaLogger(EncryptionDecryption.class.getName());

	private static String keyArn="arn:aws:kms:us-east-1:541926382317:key/3be280d6-6366-496a-aad4-7f4a7cc1a0de";
//	private static String keyArn="arn:aws:kms:us-east-1:541926382317:key/b1edf4ad-f2df-461d-adc2-7e78bcde13ca";   
    final KmsMasterKeyProvider prov = new KmsMasterKeyProvider(keyArn);
    public String encryption(String data) {
       
    	try {
			final AwsCrypto crypto = new AwsCrypto();
			final KmsMasterKeyProvider prov = new KmsMasterKeyProvider(keyArn);
			// Encrypt the data
			//
			// Most encrypted data should have an associated encryption context
			// to protect integrity. This sample uses placeholder values.
			//
			// For more information see:
			// blogs.aws.amazon.com/security/post/Tx2LZ6WBJJANTNW/How-to-Protect-the-Integrity-of-Your-Encrypted-Data-by-Using-AWS-Key-Management
			final Map<String, String> context = Collections.singletonMap("Example", "String");

      return  crypto.encryptString(prov, data).getResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
        
        
    }
    public String decryption(String data) {
        
        // Instantiate the SDK
        final AwsCrypto crypto = new AwsCrypto();

        // Set up the KmsMasterKeyProvider backed by the default credentials
        final KmsMasterKeyProvider prov = new KmsMasterKeyProvider(keyArn);

        // Encrypt the data
        //
        // Most encrypted data should have an associated encryption context
        // to protect integrity. This sample uses placeholder values.
        //
        // For more information see:
        // blogs.aws.amazon.com/security/post/Tx2LZ6WBJJANTNW/How-to-Protect-the-Integrity-of-Your-Encrypted-Data-by-Using-AWS-Key-Management
        final Map<String, String> context = Collections.singletonMap("Example", "String");

     

        // Decrypt the data
        final CryptoResult<String, KmsMasterKey> decryptResult = crypto.decryptString(prov, data);
        
        // Before returning the plaintext, verify that the customer master key that
        // was used in the encryption operation was the one supplied to the master key provider. 
        if (!decryptResult.getMasterKeyIds().get(0).equals(keyArn)) {
            throw new IllegalStateException("Wrong key id!");
        }

        // Also, verify that the encryption context in the result contains the
        // encryption context supplied to the encryptString method. Because the
        // SDK can add values to the encryption context, don't require that 
        // the entire context matches. 
        for (final Map.Entry<String, String> e : context.entrySet()) {
            if (!e.getValue().equals(decryptResult.getEncryptionContext().get(e.getKey()))) {
                throw new IllegalStateException("Wrong Encryption Context!");
            }
        }

        return decryptResult.getResult();
        
    }
}
