package cn.offway.zeus.service;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.offway.zeus.utils.HttpClientUtil;

@Service
public class WxService {

	private static Logger logger = LoggerFactory.getLogger(WxService.class);
    
    private static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET";
    
    private static String TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
    
    private static String ACCESS_TOKEN_KEY = "WX_ACCESS_TOKEN";
    
    private static String TICKET_KEY = "WX_TICKET"; 
    
    @Value("${wx.appid}")
    private  String APPID;
    
    @Value("${wx.secret}")
    private String SECRET;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 获取AccessToken
     * @param appid
     * @param secret
     * @return
     */
    public String getAccessToken(){
    	
    	String access_token = stringRedisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
    	if(StringUtils.isNotBlank(access_token)){
    		return access_token;
    	}
    	
    	String url = ACCESS_TOKEN_URL;
    	url = url.replace("APPID", APPID).replace("SECRET", SECRET);
    	String result = HttpClientUtil.get(url);
    	JSONObject jsonObject = JSON.parseObject(result);
    	access_token = jsonObject.getString("access_token");
    	if(StringUtils.isNotBlank(access_token)){
    		long expires_in =jsonObject.getLong("expires_in");
    		stringRedisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, access_token, expires_in, TimeUnit.SECONDS);
    	}
    	return access_token;
    }
    
    /**
     * 获取Ticket
     * @return
     */
    public String getTicket(){
    	
    	String ticket = stringRedisTemplate.opsForValue().get(TICKET_KEY);
    	if(StringUtils.isNotBlank(ticket)){
    		return ticket;
    	}
    	
    	String url = TICKET_URL;
    	String access_token =  getAccessToken();
    	url = url.replace("ACCESS_TOKEN", access_token);
    	
    	String result = HttpClientUtil.get(url);
    	
    	JSONObject jsonObject = JSON.parseObject(result);
    	ticket = jsonObject.getString("ticket");
    	if(StringUtils.isNotBlank(ticket)){
    		long expires_in =jsonObject.getLong("expires_in");
    		stringRedisTemplate.opsForValue().set(TICKET_KEY, ticket, expires_in, TimeUnit.SECONDS);
    	}
    	return ticket;
    	
    }
    
    /**
     * 组装JSSDK初始化参数
     * @param url
     * @param appid
     * @param secret
     * @return
     */
    public Map<String, String> getJssdkinfo(String url) {
        Map<String, String> jssdkInfoMap = new HashMap<String, String>();
        try {
			String timestampStr = String.valueOf(System.currentTimeMillis()/1000);
			String nonceStr = UUID.randomUUID().toString();
			String getSHA1 = getSHA1(getTicket(), timestampStr, nonceStr, url);
			jssdkInfoMap.put("timestamp", timestampStr);
			jssdkInfoMap.put("nonce", nonceStr);
			jssdkInfoMap.put("getSHA1", getSHA1);
			jssdkInfoMap.put("appid", APPID);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return jssdkInfoMap;
    }
    
    
    
	public String getSHA1(String ticket, String timestamp, String nonce, String url)
			throws Exception {
		String[] paramArr = new String[] { "jsapi_ticket=" + ticket, "timestamp=" + timestamp, "noncestr=" + nonce,
				"url=" + url };
		Arrays.sort(paramArr);
		// 将排序后的结果拼接成一个字符串
		String content = paramArr[0].concat("&" + paramArr[1]).concat("&" + paramArr[2]).concat("&" + paramArr[3]);
		logger.warn("生成签名string1" + content);
		// SHA1签名生成
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(content.getBytes());
		byte[] digest = md.digest();

		StringBuffer hexstr = new StringBuffer();
		String shaHex = "";
		for (int i = 0; i < digest.length; i++) {
			shaHex = Integer.toHexString(digest[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexstr.append(0);
			}
			hexstr.append(shaHex);
		}
		System.out.println(hexstr.toString());
		logger.warn("生成签名SH1加密" + hexstr.toString());
		return hexstr.toString();
	}
    
    
}
