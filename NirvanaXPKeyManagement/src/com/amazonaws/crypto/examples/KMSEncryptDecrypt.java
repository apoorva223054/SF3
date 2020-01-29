package com.amazonaws.crypto.examples;

 

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.google.common.collect.ImmutableMap;
 
// TODO: Auto-generated Javadoc
/**
 * Encrypt and decrypt a simple message using AWS KMS.
 */
public class KMSEncryptDecrypt
{
    
    /** The kms. */
    private final AWSKMSClient kms;
    
    /** The key id. */
    private final String keyId = "arn:aws:kms:us-east-1:541926382317:key/b1edf4ad-f2df-461d-adc2-7e78bcde13ca";
    
    /** The encryption context. */
    private final Map<String,String> encryptionContext = 
        ImmutableMap.of("hello", "goodbye");
 
    /**
     * Instantiates a new KMS encrypt decrypt.
     */
    public KMSEncryptDecrypt() {
        kms = getClient();
    }
    
    /**
     * Gets the client.
     *
     * @return the client
     */
    private AWSKMSClient getClient() {
        AWSKMSClient kms = new AWSKMSClient(new BasicAWSCredentials(
                        "AKIAIJO2EIRUTYJEUPGA", "jdCoV+KYpiihDasTxdqsf6mDDww+nwfwgAo5K4+L"));
 
        kms.setEndpoint("https://kms.us-east-1.amazonaws.com");
 
        return kms;
    }
    
    /**
     * Encrypt.
     *
     * @param msg the msg
     * @return the byte[]
     */
    public byte[] encrypt(String msg) {
    	ByteBuffer plaintext = ByteBuffer.wrap(msg.getBytes());
 
    	EncryptRequest req = 
            new EncryptRequest().withKeyId(keyId).withPlaintext(plaintext);
    	req.setEncryptionContext(encryptionContext);
    	ByteBuffer ciphertext = kms.encrypt(req).getCiphertextBlob();
    	return ciphertext.array();
    }
    
    /**
     * Decrypt.
     *
     * @param cipherbytes the cipherbytes
     * @return the string
     */
    public String decrypt(byte[] cipherbytes) {
    	ByteBuffer ciphertext = ByteBuffer.wrap(cipherbytes);
    	DecryptRequest req = 
            new DecryptRequest().withCiphertextBlob(ciphertext);
    	req.setEncryptionContext(encryptionContext);
    	ByteBuffer plaintext = kms.decrypt(req).getPlaintext();
 
    	return new String(plaintext.array());
    }
 
    /**
     * The main method.
     *
     * @param args the arguments
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public static void main( String[] args ) throws UnsupportedEncodingException
    {
        KMSEncryptDecrypt kms = new KMSEncryptDecrypt();
        System.out.println("Encrypted message is:");
        byte[] enc = kms.encrypt("lqdFwhYIlZ0PztjHqZiRyhJw");
        ByteBuffer buf = ByteBuffer.wrap(enc);
        String s = StandardCharsets.UTF_8.decode(buf).toString();
        System.out.println(s);
        System.out.println("Decrypted message is:");
        System.out.println(kms.decrypt(enc));
    }
}