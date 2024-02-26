package com.orange.edu.content.feignclient;


import com.orange.edu.content.config.MultipartSupportConfig;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 媒资管理服务远程接口
 *
 */
@FeignClient(value = "media",
        configuration = MultipartSupportConfig.class,
        fallbackFactory = MediaServiceClient.MediaServiceClientFallbackFactory.class)
//@FeignClient(value = "media",configuration = MultipartSupportConfig.class, url = "http://localhost:63050")
public interface MediaServiceClient {

    /**
     * 上传文件
     */
    @RequestMapping(value = "/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    String uploadFile(@RequestPart("filedata") MultipartFile filedata,
                      @RequestParam(value = "folder", required = false) String folder,
                      @RequestParam(value = "objectName", required = false) String objectName);

    @RequestMapping(value = "/say/hello")
    @ResponseBody
    String hello(@RequestBody String s);

    // 服务降级处理，可以获取异常信息
    @Slf4j
    @Component
    class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
        // 降级方法
        @Override
        public MediaServiceClient create(Throwable cause) {
            log.error("远程调用媒资管理服务上传文件时发生熔断，熔断异常是：{}", cause.getMessage());
            return null;
        }
    }
}
