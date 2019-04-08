package cn.offway.zeus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.sensorsdata.analytics.javasdk.SensorsAnalytics;
import com.yunpian.sdk.YunpianClient;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 配置
 * @author wn
 *
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{
	
	/** 从神策分析获取的数据接收的 URL **/
	@Value("${sa.server.url}")
	private String SA_SERVER_URL;
	
	/** 云片APIKEY **/
	@Value("${yunpian.apikey}")
	private String APIKEY;
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
 
		registry.addMapping("/**")
				.allowCredentials(true)
				.allowedHeaders("*")
				.allowedOrigins("*")
				.allowedMethods("*");
 
	}
	
	@Bean
	public SensorsAnalytics sensorsAnalytics(){
		return new SensorsAnalytics(
				//使用 Debug 模式，并且导入 Debug 模式下所发送的数据
				new SensorsAnalytics.DebugConsumer(SA_SERVER_URL, true));
	}
	
	@Bean
	public YunpianClient yunpianClient(){
		return new YunpianClient(APIKEY).init();
	}
	
	@Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.offway"))
                .paths(PathSelectors.any())
                .build();
	}
}
