package cn.offway.zeus.controller;

import cn.offway.zeus.properties.AlipayProperties;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import com.alipay.api.internal.util.AlipaySignature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 支付宝相关
 * @author wn
 *
 */
@Api(tags={"支付宝"})
@RestController
@RequestMapping("/alipay")
public class AlipayController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JsonResultHelper jsonResultHelper;

    @Autowired
    private PhUserInfoService phUserInfoService;

    @Autowired
    private AlipayProperties alipayProperties;

    @ApiOperation("获取支付宝用户信息")
    @PostMapping("/user")
    public JsonResult alipay(
            @ApiParam("用户ID") @RequestParam Long userId,
            @ApiParam("授权码") @RequestParam String code){
        return jsonResultHelper.buildSuccessJsonResult(phUserInfoService.saveAlipayUser(userId,code));
    }

    @ApiOperation("支付宝授权请求参数组装")
    @PostMapping("/auth")
    public JsonResult  alipayAuth(){
        try {
            SortedMap<String,String > map = new TreeMap<>();
            map.put("apiname","com.alipay.account.auth");
            map.put("method","alipay.open.auth.sdk.code.get");
            map.put("app_id",alipayProperties.getAppid());
            map.put("app_name","mc");
            map.put("biz_type","openservice");
            map.put("pid","2088431606265984");
            map.put("product_id","APP_FAST_LOGIN");
            map.put("scope","kuaijie");
            map.put("target_id", UUID.randomUUID().toString().replaceAll("-",""));
            map.put("auth_type","AUTHACCOUNT");
            map.put("sign_type","RSA");

            String content = AlipaySignature.getSignContent(map);
            String sign = AlipaySignature.rsaSign(content,alipayProperties.getPrivatekey(),"UTF-8");
            return jsonResultHelper.buildSuccessJsonResult(content+"&sign="+ URLEncoder.encode(sign,"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("支付宝授权请求参数组装异常",e);
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
        }

    }
}
