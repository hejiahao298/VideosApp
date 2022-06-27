package com.hjh.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Component
public class OSSUtils {

    @Value("${oss.accesskeyId}")
    private String ACCESSKEYID;

    @Value("${oss.secret}")
    private String SECRET;

    /**
     * 上传文件
     */
    public String upload(File file, String path, String fileName) throws FileNotFoundException {
        return upload(new FileInputStream(file), path, fileName);
    }

    /**
     * 上传文件
     *
     * @param inputStream
     * @return
     */
    public String upload(InputStream inputStream, String path, String fileName) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, ACCESSKEYID, SECRET);

        // 上传文件流。
        String bucketName = "oss-cn-hangzhou-hjh";
        String key = path + "/" + fileName;
        ossClient.putObject(bucketName, key, inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();
        return "https://" + bucketName + ".oss-cn-hangzhou.aliyuncs.com/" + key;
    }
}
