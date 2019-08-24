package cn.offway.zeus.config;

import com.aliyun.mq.http.MQClient;
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
		AlipayClient alipayClient = new DefaultAlipayClient(alipayProperties.getUrl(), alipayProperties.getAppid(), alipayProperties.getPrivatekey(), "json", "UTF-8", alipayProperties.getPublickey(), "RSA2");
		return alipayClient;
	}

	@Bean
	public MQClient mqClient(){
		return  new MQClient(alipayProperties.getAccountEndpoint(),alipayProperties.getAccessId(),alipayProperties.getAccessKey());
	}
}
