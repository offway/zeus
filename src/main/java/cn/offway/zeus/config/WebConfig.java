package cn.offway.zeus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sensorsdata.analytics.javasdk.SensorsAnalytics;
import com.yunpian.sdk.YunpianClient;

import io.netty.util.HashedWheelTimer;
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
@EnableWebSecurity
public class WebConfig  extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
	
	/** 从神策分析获取的数据接收的 URL **/
	@Value("${sa.server.url}")
	private String SA_SERVER_URL;
	
	/** 云片APIKEY **/
	@Value("${yunpian.apikey}")
	private String APIKEY;

	@Value("${is-prd}")
	private boolean isPrd;



	@Override
	public void addCorsMappings(CorsRegistry registry) {
 
		registry.addMapping("/**")
				.allowCredentials(true)
				.allowedHeaders("*")
				.allowedOrigins("*")
				.allowedMethods("*");
 
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http = http.csrf().disable();
		if(isPrd){
			http
					.authorizeRequests()
					// swagger页面需要添加登录校验
					.antMatchers("/swagger-ui.html").authenticated()
					.and()
					.formLogin();
		}
	}

	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
		return new DefaultHttpFirewall();
	}


	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
		web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
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
		/*ParameterBuilder ticketPar = new ParameterBuilder();
		List<Parameter> pars = new ArrayList<Parameter>();
		ticketPar.name("Authorization").description("认证token")
				.modelRef(new ModelRef("string")).parameterType("header")
				.required(false).build(); //header中的ticket参数非必填，传空也可以
		pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数*/


		return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.offway"))
                .paths(PathSelectors.any())
                .build();
//				.globalOperationParameters(pars);
	}
	
	@Bean
	public HashedWheelTimer hashedWheelTimer(){
		return new HashedWheelTimer();
	}
}
