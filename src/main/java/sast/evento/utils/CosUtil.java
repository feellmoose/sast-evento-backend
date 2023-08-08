package sast.evento.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import org.springframework.web.multipart.MultipartFile;
import sast.evento.exception.LocalRunTimeException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static sast.evento.config.CosConfig.*;


/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/2 22:10
 */
public class CosUtil {
    private static final COSClient cosClient = createCOSClient();
    /* 上传图片，同一张图片同一个名字会覆盖，防止重复上传 */
    public static String upload(MultipartFile file,String dir) {
        String dirPrefix = dir.isEmpty() ? "" : (dir+"/");
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("size of file is out of limit 10M.");
        }
        /* get key */
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("name of upload file is empty.");
        }
        int idx = originalFileName.lastIndexOf(".");
        String prefix = originalFileName.substring(0, idx);
        prefix = prefix.length() < 3 ? prefix + "_file" : prefix;
        String key = null;
        /* upload */
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        try {
            InputStream inputStream = file.getInputStream();
            key = dirPrefix + prefix + "_" + md5HashCode(inputStream);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            putObjectRequest.setStorageClass(StorageClass.Standard);
            cosClient.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new LocalRunTimeException(e.getMessage());
        }
        return "https://"+bucketName+".cos."+COS_REGION+".myqcloud.com/"+key;
    }

    /* 通过上一次获得的最后一个URL来获取下一个分页 */
    public static List<String> getURLs(String dir,String lastURL,Integer size){
        String dirPrefix = dir.isEmpty() ? "" : (dir+"/");
        String lastKey = lastURL.isEmpty() ? "" : lastURL.substring(lastURL.indexOf("/",9)+1);
        ListObjectsRequest request = new ListObjectsRequest(bucketName,dirPrefix,lastKey,"/",size);
        ObjectListing objectListing = null;
        try {
            objectListing = cosClient.listObjects(request);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(e.getMessage());
        }
        List<COSObjectSummary> summaryList = objectListing.getObjectSummaries();
        return summaryList.stream()
                .map(COSObjectSummary::getKey)
                .map(key -> "https://"+bucketName+".cos."+COS_REGION+".myqcloud.com/"+key)
                .toList();
    }

    /* 获取当前桶下所有目录 */
    public static List<String> getDirs(String dir){
        String dirPrefix = dir.isEmpty() ? "" : (dir+"/");
        ListObjectsRequest request = new ListObjectsRequest(bucketName,dirPrefix,"","/",1000);
        ObjectListing objectListing = null;
        try {
            objectListing = cosClient.listObjects(request);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(e.getMessage());
        }
        return objectListing.getCommonPrefixes().stream()
                .map(s -> s.substring(0,s.length()-1))
                .toList();
    }

    /* 根据url删除图片 */
    public static void delete(String url) {
        String key = url.substring(url.indexOf("/",9)+1);
        try {
            cosClient.deleteObject(bucketName, key);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(e.getMessage());
        }
    }

    private static COSClient createCOSClient() {
        /* 由于上传的并发量和数量都的不多，这里不浪费资源，直接使用简单实例 */
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(COS_REGION));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);
    }

    private static TransferManager createTransferManager() {
        /* 关于cos-sdk的使用 */
        /* https://cloud.tencent.com/document/product/436/65938 */
        COSClient cosClient = createCOSClient();
        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        TransferManager transferManager = new TransferManager(cosClient, threadPool);
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);
        return transferManager;
    }
    private static String md5HashCode(InputStream inputStream) throws Exception{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = inputStream.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            inputStream.close();
            byte[] md5Bytes  = md.digest();
            BigInteger bigInt = new BigInteger(1, md5Bytes);
            return bigInt.toString(16);
    }
}