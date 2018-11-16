package cn.offway.zeus.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.offway.zeus.utils.HttpClientUtil;

@RestController
@RequestMapping("/mini")
public class MiniController {

//	private static String[] token = {"","0"}; 
	
	private static String access_token = "";
	
	@Value("${mini.appid}")
	private String APPID;

	@Value("${mini.secret}")
	private String SECRET;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@GetMapping(value = "/getwxacodeunlimit",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getwxacodeunlimit(String page,String scene,String width) throws IOException {
//		long time = System.currentTimeMillis();
//		String access_token = "";
//		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		logger.info("过期时间:"+sf.format(new Date(Long.parseLong(token[1]))));
//		logger.info("当前时间:"+sf.format(new Date(time)));
//		if(token[0].length() > 0 & Long.parseLong(token[1]) > time){
//			access_token = token[0];
//		}else{
//			String accessTokenResult = HttpClientUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APPID+"&secret="+SECRET);		
//			logger.info(accessTokenResult);
//			JSONObject jsonObject = JSON.parseObject(accessTokenResult);
//			access_token = jsonObject.getString("access_token");
//			time += 1800000L;
//			token[0] = access_token;
//			token[1] = String.valueOf(time);
//			logger.info("过期时间:"+sf.format(new Date(time)));
//		}
		JSONObject params = new JSONObject();
		params.put("page", page);
		params.put("scene", scene);
		params.put("width", width);
        byte[] result = HttpClientUtil.postByteArray("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+access_token,params.toJSONString());
        String resultStr = new String(result, "UTF-8");
        if(resultStr.indexOf("errcode")>=0){
			String accessTokenResult = HttpClientUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APPID+"&secret="+SECRET);		
			logger.info(accessTokenResult);
			JSONObject jsonObject = JSON.parseObject(accessTokenResult);
			access_token = jsonObject.getString("access_token");
	        result = HttpClientUtil.postByteArray("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+access_token,params.toJSONString());
        }
        return result;
	}
	
	/*@GetMapping(value = "/getwxacode")
    @ResponseBody
    public String getwxacode(String page,String scene,String width) throws IOException {
		
		InputStream inputStream = null;
        OutputStream outputStream = null;
		try {
			long time = System.currentTimeMillis();
			String access_token = "";
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println("过期时间:"+sf.format(new Date(Long.parseLong(token[1]))));
			System.out.println("当前时间:"+sf.format(new Date(time)));
			if(token.length > 0 && Long.parseLong(token[1]) > time){
				access_token = token[0];
			}else{
				String accessTokenResult = HttpClientUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APPID+"&secret="+SECRET);		
				System.out.println(accessTokenResult);
				JSONObject jsonObject = JSON.parseObject(accessTokenResult);
				access_token = jsonObject.getString("access_token");
				time += jsonObject.getLongValue("expires_in") * 500;
				token[0] = access_token;
				token[1] = String.valueOf(time);
				System.out.println("过期时间:"+sf.format(new Date(time)));
			}
			JSONObject params = new JSONObject();
			params.put("page", page);
			params.put("scene", scene);
			params.put("width", width);
			byte[] result = HttpClientUtil.postByteArray("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+access_token,params.toJSONString());
			
			inputStream = new ByteArrayInputStream(result);

			File file = new File("/mnt/offway/getwxacodeunlimit.jpg");
			if (!file.exists()){
			    file.createNewFile();
			}
			outputStream = new FileOutputStream(file);
			int len = 0;
			byte[] buf = new byte[1024];
			while ((len = inputStream.read(buf, 0, 1024)) != -1) {
			    outputStream.write(buf, 0, len);
			}
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	
		return "http://47.101.34.253/getwxacodeunlimit.jpg";
	
	}*/
	
}
