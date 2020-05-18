package aws.asset.util

import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ListObjectsRequest
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper



import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class AwsS3Utils {

	Logger logger = LogManager.getLogger(AwsS3Utils.class)
	

	//TODO: don't use keys here in plain text
	//TODO: refactor to single method for generating both types of URLs?
	String accessKey = "AKIA6C2QODNAYNWBMHOH"
	String secretKey = "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"

	public URL generateUrlToPutObject(String bucketName, String objectKey) {
		URL url
		try {
			
			java.util.Date expiration = new java.util.Date()
			long expTimeMillis = expiration.getTime()
			expTimeMillis += 1000 * 60 * 60
			expiration.setTime(expTimeMillis)
			
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
			
			ClientConfiguration clientConfiguration = new ClientConfiguration()
			clientConfiguration.setSignerOverride("AWSS3V4SignerType")
			
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
			    //.withForceGlobalBucketAccessEnabled(true)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.withRegion(Regions.US_EAST_1)
				.withPathStyleAccessEnabled(true)
				.withClientConfiguration(clientConfiguration)
				.build()
			
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
				.withMethod(HttpMethod.PUT)
				.withExpiration(expiration)
			
			
			url = s3Client.generatePresignedUrl(generatePresignedUrlRequest)
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (SdkClientException e) {
			e.printStackTrace();
		}
		
		
		url
	}
	
	public URL generateUrlToGetObject(String bucketName, String objectKey, Double timeout) {
		URL url
		try {
			
			java.util.Date expiration = new java.util.Date()
			long expTimeMillis = expiration.getTime()
			expTimeMillis += 1000 * timeout
			expiration.setTime(expTimeMillis)
			
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
			
			ClientConfiguration clientConfiguration = new ClientConfiguration()
			clientConfiguration.setSignerOverride("AWSS3V4SignerType")
			
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				//.withForceGlobalBucketAccessEnabled(true)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.withRegion(Regions.US_EAST_1)
				.withPathStyleAccessEnabled(true)
				.withClientConfiguration(clientConfiguration)
				.build()
			
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
				.withMethod(HttpMethod.GET)
				.withExpiration(expiration)
			
			
			url = s3Client.generatePresignedUrl(generatePresignedUrlRequest)
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (SdkClientException e) {
			e.printStackTrace();
		}
		
		
		url
	}

}
