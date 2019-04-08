package cn.offway.zeus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

import cn.offway.zeus.properties.AlipayProperties;



@Configuration
@EnableConfigurationProperties(AlipayProperties.class)
public class AlipayConfig {

	@Autowired
	private AlipayProperties alipayProperties;
	
	@Bean
	public AlipayClient alipayClient(){
		//实例化客户端
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", alipayProperties.getAppid(), alipayProperties.getPrivatekey(), "json", "UTF-8", alipayProperties.getPublickey(), "RSA2");
		return alipayClient;
	}
}