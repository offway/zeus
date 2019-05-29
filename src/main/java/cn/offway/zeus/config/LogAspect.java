package cn.offway.zeus.config;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

@Component
@Aspect
public class LogAspect {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@AfterReturning(value="execution(* cn.offway.zeus.controller..*.*(..))", returning="rvt")
	public void after(JoinPoint joinPoint, Object rvt){
		
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
 
        String method = request.getMethod();
        String uri = request.getRequestURI();
        Object[] args = joinPoint.getArgs();
        Map<String, String> paramMap = new HashMap<String, String>();
		Enumeration<?> enu=request.getParameterNames();
		while(enu.hasMoreElements()){
			String paraName=(String)enu.nextElement();
			paramMap.put(paraName, request.getParameter(paraName));
		}
		Object params = paramMap;
		if(paramMap.isEmpty() && args.length == 1){
			Object arg = args[0];
			if(null != arg && !arg.getClass().getName().equals("org.springframework.security.web.firewall.RequestWrapper")){
				params = JSON.toJSONString(arg,SerializerFeature.WriteMapNullValue);
			}
		}
 
        logger.info("请求地址:"+uri);
        logger.info("请求类型:"+method);
        logger.info("请求参数:"+params);
        logger.info("返回数据:{}",JSON.toJSONString(rvt,SerializerFeature.WriteMapNullValue));
		
	}
	
}
