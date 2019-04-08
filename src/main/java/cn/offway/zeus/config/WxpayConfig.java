package cn.offway.zeus.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import cn.offway.zeus.properties.WxpayProperties;



@Configuration
@EnableConfigurationProperties(WxpayProperties.class)
public class WxpayConfig {

}
