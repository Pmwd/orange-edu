package com.orange.edu.learning.feignclient;


import com.orange.base.model.RestResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 媒资管理服务远程接口
 *
 *
 */
@FeignClient(value = "media-api", fallbackFactory = MediaServiceClient.MediaServiceClientFallbackFactory.class)
@RequestMapping("/media")
public interface MediaServiceClient {

    @GetMapping("/open/preview/{mediaId}")
    RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId);


    /**
     * MediaServiceClient 接口的降级类
     */
    @Slf4j
    class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {

        @Override
        public MediaServiceClient create(Throwable cause) {
            log.error("远程调用媒资管理服务熔断异常： {}", cause.getMessage());
            return null;
        }
    }
}
