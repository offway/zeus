package cn.offway.zeus.service;

import cn.offway.zeus.properties.AlipayProperties;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQProducer;
import com.aliyun.mq.http.model.TopicMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AlipayService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayProperties alipayProperties;

    /**
     * 系统地址
     **/
    @Value("${system.url}")
    private String systemUrl;

    @Autowired
    private MQClient mqClient;

    @Value("${is-prd}")
    private boolean isPrd;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String ALIPAY_ACCESS_TOKEN = "zeus.alipay.accessToken";

    private static final String ALIPAY_REFRESH_TOKEN = "zeus.alipay.refreshToken";


    /**
     * 手机网站支付请求
     */
    public String tradeByWeb(String outtradeno, Double amount, String subject, String returnUrl) {
        String form = null; //调用SDK生成表单
        try {
            AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
            alipayRequest.setReturnUrl(returnUrl);
            alipayRequest.setNotifyUrl(systemUrl + "/notify/alipay");//在公共参数中设置回跳和通知地址
            alipayRequest.setBizContent("{" +
                    "    \"out_trade_no\":\"" + outtradeno + "\"," +
                    "    \"total_amount\":" + amount + "," +
                    "    \"subject\":\"" + subject + "\"," +
                    "    \"product_code\":\"QUICK_WAP_WAY\"" +
                    "  }");//填充业务参数
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("手机网站支付请求异常,订单号：{}", outtradeno, e);
        }
        return form;
    }

    /**
     * 生成 APP支付订单信息
     */
    public AlipayTradeAppPayResponse trade(String outtradeno, String body, String subject, Double amount) {
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(body);
        model.setSubject(subject);
        model.setOutTradeNo(outtradeno);
        model.setTimeoutExpress("30m");
        model.setTotalAmount("" + amount);
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(systemUrl + "/notify/alipay");
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            logger.info("支付宝生成 APP支付订单信息响应:{}", JSON.toJSONString(response));
            return response;
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 支付宝换取授权访问令牌
     */
    public String oauthToken(Long userId, String code) {
        try {
            String accessToken = stringRedisTemplate.opsForValue().get(ALIPAY_ACCESS_TOKEN + "." + userId);
            if (StringUtils.isBlank(accessToken)) {
                String refreshToken = stringRedisTemplate.opsForValue().get(ALIPAY_REFRESH_TOKEN + "." + userId);

                AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();

                String grantType = "authorization_code";
                if (StringUtils.isNotBlank(refreshToken)) {
                    grantType = "refresh_token";
                    request.setRefreshToken(refreshToken);
                } else {
                    request.setCode(code);
                }
                request.setGrantType(grantType);
                AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
                if (response.isSuccess()) {
                    logger.info("支付宝换取授权访问令牌响应:{}", JSON.toJSONString(response));
                    accessToken = response.getAccessToken();
                    long expiresIn = Long.parseLong(response.getExpiresIn());
                    refreshToken = response.getRefreshToken();
                    long reExpiresIn = Long.parseLong(response.getReExpiresIn());
                    stringRedisTemplate.opsForValue().set(ALIPAY_ACCESS_TOKEN + "." + userId, accessToken, expiresIn, TimeUnit.SECONDS);
                    stringRedisTemplate.opsForValue().set(ALIPAY_REFRESH_TOKEN + "." + userId, refreshToken, reExpiresIn, TimeUnit.SECONDS);
                } else {
                    logger.error("支付宝换取授权访问令牌响应:{}", JSON.toJSONString(response));
                }
            }
            return accessToken;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("支付宝换取授权访问令牌异常", e);
        }
        return null;
    }

    /**
     * 支付宝会员授权信息查询接口
     */
    public AlipayUserInfoShareResponse userInfoShare(String accessToken) {
        try {
            AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
            AlipayUserInfoShareResponse response = alipayClient.execute(request, accessToken);
            if (response.isSuccess()) {
                logger.info("支付宝会员授权信息查询接口响应:{}", JSON.toJSONString(response));
                return response;
            } else {
                logger.error("支付宝会员授权信息查询接口响应:{}", JSON.toJSONString(response));

            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("支付宝会员授权信息查询接口异常", e);
        }
        return null;
    }


    public void publishMessage(String message, String topicName) {
        if (isPrd) {
            try {
                // 获取Topic的生产者
                MQProducer producer = mqClient.getProducer(alipayProperties.getInstanceId(), topicName);
                TopicMessage pubMsg = new TopicMessage(message.getBytes(), "A");
                // 同步发送消息，只要不抛异常就是成功
                TopicMessage pubResultMsg = producer.publishMessage(pubMsg);
                // 同步发送消息，只要不抛异常就是成功
                logger.info(" Send mq message success.  msgId is: " + pubResultMsg.getMessageId()
                        + ", bodyMD5 is: " + pubResultMsg.getMessageBodyMD5());
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Send mq message Exception. message:{}", message, e);
            }
        }
    }

    public boolean transfer() {
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        request.setBizContent("{" +
                "\"out_biz_no\":\"3142321423432\"," +//商户转账唯一订单号
                "\"payee_type\":\"ALIPAY_LOGONID\"," +//收款方账户类型。可取值： 1、ALIPAY_USERID：支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。 2、ALIPAY_LOGONID：支付宝登录号，支持邮箱和手机号格式。
                "\"payee_account\":\"abc@sina.com\"," +//收款方账户。与payee_type配合使用。付款方和收款方不能是同一个账户
                "\"amount\":\"12.23\"," +//转账金额，单位：元。 只支持2位小数，小数点前最大支持13位，金额必须大于等于0.1元。
                "\"payer_show_name\":\"上海交通卡退款\"," +//付款方姓名（最长支持100个英文/50个汉字）。显示在收款方的账单详情页。如果该字段不传，则默认显示付款方的支付宝认证姓名或单位名称
                "\"payee_real_name\":\"张三\"," +//收款方真实姓名（最长支持100个英文/50个汉字）。如果本参数不为空，则会校验该账户在支付宝登记的实名是否与收款方真实姓名一致。
                "\"remark\":\"转账备注\"" +//转账备注（支持200个英文/100个汉字）。 当付款方为企业账户，且转账金额达到（大于等于）50000元，remark不能为空。收款方可见，会展示在收款用户的收支详情中。
                "  }");
        try {
            AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
            boolean isSuccess = response.isSuccess();

            if (isSuccess) {
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
            return isSuccess;
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 服务端验证异步通知信息参数示例
     */
    public boolean validate(HttpServletRequest request) {
        try {
            Map<String, String> params = new HashMap<String, String>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }
            return AlipaySignature.rsaCheckV1(params, alipayProperties.getPublickey(), "UTF-8", "RSA2");
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return false;
        }
    }
}
