package com.suresh.downloadfroms3.Service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;


    public String uploadFile(MultipartFile file)
    {
        File fileObj = convertMultiPartFileIntoFile(file);
        String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName,fileName,fileObj));
        fileObj.delete();
        return "File Uploaded"+fileName;
    }


    public byte[] downloadFile(String fileName) throws IOException {
        S3Object s3Object = s3Client.getObject(bucketName,fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        byte[] content = IOUtils.toByteArray(inputStream);
        return content;
    }
    private File convertMultiPartFileIntoFile(MultipartFile file)
    {
        File convertedFile = new File(file.getOriginalFilename());

        try(FileOutputStream fos = new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return convertedFile;
    }


    public String deleteFile(String fileName)
    {
        s3Client.deleteObject(bucketName,fileName);
        return fileName+" Deleted";
    }


}
/*
Features of EC2
1. Scaling (scaled up and scaled down)
2. Integrated with other services(ex: S3, RDS)
3. Pay for what you use
4. Instances can be launched in one or more regions of availability zones.
5. Support for different OS.
6. Works with Amazon VPC for secure communication.

Where EC2 is used
1. To deploy a database
2. To deploy a Web Application.
 */

/*
AWS Lambda :
Lambda is a serverless compute service that lets you run
your code without managing servers.
Lambda scales automatically.
Lambda allows developers to focus on the core buisness logic they are
developing instead of worrying about managing servers.
Example of Lambda :
1. When we upload a file to S3, it tregars a Lambda function and
this lambda function store file in the DynamoDB table
2. Whenever we raise a CR, then it sends notification to everyone
we tagged or teammates.
3. when Alexa search something it also uses lambda function to get
data from database and return it back to the user.

Features
1. can run so many languages
2. We can use any local environment to invoke lambda function.
3. max run time right now is 15 minutes.
4. Lambda can execute your code in response to events.

 */