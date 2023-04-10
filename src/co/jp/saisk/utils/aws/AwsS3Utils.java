package co.jp.saisk.utils.aws;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AwsS3Utils {


	
    private static Properties properties = new Properties();
    
    //プロパティファイルのパスを指定する
    static {
         String strpass = "properties/aws.properties";
        
			try {
				InputStream istream = new FileInputStream(strpass);
				properties.load(istream);
			} catch (IOException e) {
				e.printStackTrace();
			}

    }
	//  AWSCredentials credentials = new BasicAWSCredentials("アクセスキー","シークレットキー");
	public static AWSCredentials credentials = new BasicAWSCredentials(
			properties.getProperty("aws.accesskey"),
			properties.getProperty("aws.secretkey")
//			  "AKIA43GGCL4VARMBBIWR",//<AWS accesskey> 
//			  "hSJ3bGIJGrGVP2+jTMiLB2WwBFru1QrW4eAi8uye"//<AWS secretkey>
			);

	public static AmazonS3 s3client = AmazonS3ClientBuilder
			  .standard()
			  .withCredentials(new AWSStaticCredentialsProvider(credentials))
			  .withRegion(Regions.fromName(properties.getProperty("aws.region")))
			  //.withRegion(Regions.AP_NORTHEAST_1)
			  .build();
	
	public static void main(String[] args) {
		
		try {

			List<Bucket> buckets = s3client.listBuckets();
			for(Bucket bucket : buckets) {
			    System.out.println(bucket.getName());
			    
	            // 指定されたバケット配下のキーのオブジェクト（ファイル）リストを取得する。
	            ObjectListing objectListing = s3client.listObjects(new ListObjectsRequest()
	                    .withBucketName(bucket.getName())
	                    //.withPrefix("20")
	                    );
	            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
	                System.out.println(" - " + objectSummary.getKey() + "  " +
	                                   "(size = " + objectSummary.getSize() + ")");
	               // downLoadFile(bucket.getName(), objectSummary.getKey(), "C:\\file\\aws" + File.separator + bucket.getName() + File.separator);
	                
	            }
//	            System.out.println("---------------------");
//	            
//	            ListObjectsV2Result result = s3client.listObjectsV2(bucket.getName());
//	            List<S3ObjectSummary> objects = result.getObjectSummaries();
//	            for (S3ObjectSummary os : objects) {
//	                System.out.println("* " + os.getKey());
//	            }
			}
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		    System.err.println(e.getErrorMessage());
		    System.exit(1);
		}
	}
	
	
	public static Set<String> getFileNm(ObjectListing objectListing, boolean hasSub) {
		
		Set<String> retLst = new TreeSet<String>();
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
        	
        	String str = "";
    		String key = objectSummary.getKey();
    		if (key.endsWith("/")) {
    			// do nothing
    		} else if (key.contains("/")) {
    			if (hasSub) {
    				retLst.add(key);
				}
    		} else {
    			retLst.add(key);
    		}
        }
        return retLst;
	}
	
	
	
	public static File downLoadFile(String bucketName, String key,String toFolder) {
		
		File toFile = null;
		String toName = "";
			
		if (key.endsWith("/")) {
			toFolder = toFolder + key;
		} else if (key.contains("/")) {
			toName = key.substring(key.lastIndexOf("/")+1);
			toFolder = toFolder + File.separator + key.substring(0,key.lastIndexOf("/")).replaceAll("/", File.separator);
			
		} else {
			toName = key;
		}
		
		System.out.format("Downloading %s from S3 bucket %s...\n", key, bucketName);
		
	    // バケット名とS3のファイルパス（キー値）を指定
	    GetObjectRequest request = new GetObjectRequest(bucketName, key);

	    // ファイルダウンロード
	    try {
	    	
	    	
		    // ファイル保存先
		    new File(toFolder).mkdirs();
		    
		    if (toName.length() != 0) {
			    toFile = new File(toFolder + File.separator + toName);
		    	s3client.getObject(request, toFile);
			}

	    } catch (AmazonServiceException e) {
	    	e.printStackTrace();
	    	toName = null;
	    }
	    return toFile;
	}
}

/*
 
 C:\Users\tech>aws configure
	AWS Access Key ID [None]: AKIA43GGCL4VHWRYZXM2
	AWS Secret Access Key [None]: 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
	Default region name [None]: ap-northeast-1
	Default output format [None]: json
 
 https://blog.51cto.com/beanxyz/2578835
https://g-weblog.com/blog/26



arn:aws:s3:::bucketsaisk01
arn:aws:iam::883031170858:user/saisk


 {
    "Version": "2012-10-17",
    "Id": "Policy1680903312724",
    "Statement": [
        {
            "Sid": "Stmt1680903296939",
            "Effect": "Allow",
            "Principal": {
                "AWS": "arn:aws:iam::883031170858:user/saisk"
            },
            "Action": "s3:*",
            "Resource": "arn:aws:s3:::bucketsaisk01"
        }
    ]
}
 */
