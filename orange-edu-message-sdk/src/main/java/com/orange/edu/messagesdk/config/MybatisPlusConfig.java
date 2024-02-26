package com.orange.edu.messagesdk.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * <P>
 * Mybatis-Plus 配置
 * </p>
 */
//@Configuration("messagesdk_mpconfig")
@Configuration("messagesdk_mpconfig")
@MapperScan("com.orange.edu.messagesdk.mapper")
public class MybatisPlusConfig {

}