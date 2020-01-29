package com.nirvanaxp.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nirvanaxp.server.util.NirvanaLogger;

public class ImageUpload {

	public String imageUpload(File file, NirvanaLogger l,String filePath,String s3Path) throws IOException {
		 String clientRegion = "us-east-1";
	        String bucketName = "nxpmedia";
	        String stringObjKeyName = "*** String object key name ***";
	        String fileObjKeyName = "images";
	        String awsKeyId="AKIAX4LKULLWQJ4MHYNE";
	        String awsKeyValue="a4Q90nyY+xkN0o3fdBrt38YWso6iXuEMMcMyL5Ue";
	        String folderPath ="images/orders/qrcodes/"+file.getName();
	      
	        l.severe("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+file.getName());
	        try {
	            //This code expects that you have AWS credentials set up per:
	            // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html
//	            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//	                    .withRegion(clientRegion)
//	                    .build();
	        	InputStream targetStream = new FileInputStream(file);
	            BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsKeyId, awsKeyValue);
	            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(clientRegion))
	                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
	            // Upload a text string as a new object.
	            s3Client.putObject(bucketName,s3Path,file );

	            // Upload a file as a new object with ContentType and title specified.
	            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, file);
	            ObjectMetadata metadata = new ObjectMetadata();
	            metadata.setContentType("plain/text");
	            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
	            request.setMetadata(metadata);
	            s3Client.putObject(request);
	            l.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+s3Client.getUrl(bucketName, "images/test.png"));
	        } catch (AmazonServiceException e) {
	            // The call was transmitted successfully, but Amazon S3 couldn't process 
	            // it, so it returned an error response.
	        	l.severe(e);
	        } catch (SdkClientException e) {
	            // Amazon S3 couldn't be contacted for a response, or the client
	            // couldn't parse the response from Amazon S3.
	            l.severe(e);
	        }
		return file.getAbsolutePath();
	}
}
