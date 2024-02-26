package com.orange.edu.content;

import com.orange.edu.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试使用 feign 远程上传文件
 *
 *
 */
@SpringBootTest
public class FeignUploadTest {

    @Autowired
    private MediaServiceClient mediaServiceClient;

    // 远程调用，上传文件
    @Test
    void test() {
//        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\2024\\test.html"));
//        mediaServiceClient.uploadFile(multipartFile, "course", "test.html");
//        String back = mediaServiceClient.hello("你好你好");
        mediaServiceClient.hello("你好你好");
//        System.out.println(back);
    }
}
