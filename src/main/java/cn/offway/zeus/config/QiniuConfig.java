package cn.offway.zeus.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import cn.offway.zeus.properties.QiniuProperties;



@Configuration
@EnableConfigurationProperties(QiniuProperties.class)
public class QiniuConfig {

}
