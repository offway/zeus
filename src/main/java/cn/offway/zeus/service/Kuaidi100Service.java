package cn.offway.zeus.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import cn.offway.zeus.utils.HttpClientUtil;
import cn.offway.zeus.utils.Md5Util;

@Service
public class Kuaidi100Service {

	
	public String query(String expressCode,String mailNo,String phone){
		
		String param ="{\"com\":\""+expressCode+"\",\"num\":\""+mailNo+"\",\"from\":\"\",\"phone\":\""+phone+"\",\"to\":\"\",\"resultv2\":0}";
		String customer ="28B3DE9A2485E14FE0DAD40604A8922C";
		String key = "uyUDaSuE5009";
		String sign = Md5Util.encode(param+key+customer);
		Map<String, String> params = new HashMap<>();
		params.put("param",param);
		params.put("sign",sign);
		params.put("customer",customer);
		String result = HttpClientUtil.post("http://poll.kuaidi100.com/poll/query.do", params);
		//{"message":"ok","nu":"805283162742333582","ischeck":"0","condition":"00","com":"yuantong","status":"200","state":"0","data":[{"time":"2019-04-10 02:38:48","ftime":"2019-04-10 02:38:48","context":"【江门转运中心】 已发出 下一站 【上海转运中心】"},{"time":"2019-04-10 02:37:46","ftime":"2019-04-10 02:37:46","context":"【江门转运中心】 已收入"},{"time":"2019-04-10 02:22:42","ftime":"2019-04-10 02:22:42","context":"【广东省中山市板芙镇公司】 已收件"},{"time":"2019-04-09 19:34:30","ftime":"2019-04-09 19:34:30","context":"【广东省中山市板芙镇公司】 取件人: 朱华文 已收件"}]}

		return result;
	}
	
	public static void main(String[] args) {
		String param ="{\"com\":\"yuantong\",\"num\":\"805283162742333582\",\"from\":\"\",\"phone\":\"\",\"to\":\"\",\"resultv2\":0}";
		String customer ="28B3DE9A2485E14FE0DAD40604A8922C";
		String key = "uyUDaSuE5009";
		String sign = Md5Util.encode(param+key+customer);
		Map<String, String> params = new HashMap<>();
		params.put("param",param);
		params.put("sign",sign);
		params.put("customer",customer);
		String result = HttpClientUtil.post("http://poll.kuaidi100.com/poll/query.do", params);
		System.out.println(result);

	}
}
