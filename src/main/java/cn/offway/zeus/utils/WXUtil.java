package cn.offway.zeus.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class WXUtil {
    public static String getToken() {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx12d022a9493f1b26&secret=52ba3a89ae58aa6a2294806d516d6107";
        String result = HttpClientUtil.post(requestUrl, "");
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject != null) {
            return jsonObject.getString("access_token");
        } else {
            return "";
        }
    }

    public static void sendSubscribeMsg(Logger logger, Map<String, Object> template, String token) {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", token);
        String jsonString = JSONObject.toJSONString(template);

        String result = HttpClientUtil.post(requestUrl, jsonString);
        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult != null) {
            int errorCode = jsonResult.getIntValue("errcode");
            String errorMessage = jsonResult.getString("errmsg");
            if (errorCode == 0) {
                logger.info("订阅消息发送成功:" + jsonResult);
            } else {
                logger.info("订阅消息发送失败:" + errorCode + "," + errorMessage);
            }
        } else {
            logger.info("订阅消息发送失败");
        }
    }

    public static Map<String, Object> buildMsg(String openid, String url, String v2, String v3) {
        Map<String, Object> args = new HashMap<>();
        args.put("touser", openid);
        args.put("template_id", "-dSbKcxW-uDDfr31Iv9C6bClU99kYnLlv3_0sbm8Jp0");
        args.put("page", url);
        Map<String, Object> map = new HashMap<>();
        //活动名称
        Map<String, String> k2 = new HashMap<>();
        k2.put("value", v2);
        map.put("thing1", k2);
        //提醒内容
        Map<String, String> k3 = new HashMap<>();
        k3.put("value", v3);
        map.put("thing7", k3);
        //pack it
        args.put("data", map);
        return args;
    }
}