package cn.offway.zeus;

import cn.offway.zeus.domain.PhConfig;
import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.service.PhBannerService;
import cn.offway.zeus.service.PhConfigService;
import cn.offway.zeus.service.PhGoodsService;
import cn.offway.zeus.utils.JsonResultHelper;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("prd")
public class ZeusApplicationTests {

    @Autowired
    private PhBannerService phBannerService;

    @Autowired
    private PhConfigService phConfigService;

    @Autowired
    private PhGoodsService phGoodsService;

    @Autowired
    private JsonResultHelper jsonResultHelper;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testDate() {
        DateTime now = new DateTime();
        boolean a = now.getMonthOfYear() != 10 || now.getDayOfMonth() > 7;
    }

    @Test
    public void testStat() {
        //事件行为消息体
        GIOEventMessage eventMessage = new GIOEventMessage.Builder()
                .eventTime(System.currentTimeMillis())            // 事件时间，默认为系统时间（选填）
                .eventKey("elevenProductClick")                           // 事件标识 (必填)
                .loginUserId("5588")                   // 登录用户ID (必填)
                .addEventVariable("place", "ni cai")          // 事件级变量 (选填)
                .addEventVariable("channel", "1")      // 事件级变量 (选填)
                .addEventVariable("productId", 666)            // 事件级变量 (选填)
                .build();
        //上传事件行为消息到服务器
        GrowingAPI.send(eventMessage);
    }

    @Test
    public void testJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("banners", phBannerService.banners("4"));
        List<PhConfig> configs = phConfigService.findByNameIn("INDEX_IMAGES_MINI", "INDEX_BRAND_GOODS_MINI", "INDEX_CATEGORY_MINI");
        for (PhConfig phConfig : configs) {
            String name = phConfig.getName().toLowerCase();
            String content = phConfig.getContent();
            if ("index_brand_goods_mini".equals(name)) {
                List<Map> brands = JSON.parseArray(content, Map.class);
                for (Map<String, Object> brand : brands) {
                    Long brandId = Long.parseLong(brand.get("id").toString());
                    List<Object> l = new ArrayList<>();
                    for (PhGoods tmp : phGoodsService.findBrandRecommend(brandId)) {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> m = mapper.convertValue(tmp, Map.class);
                        for (String k : m.keySet()) {
                            m.put(k, StringEscapeUtils.unescapeJava(StringEscapeUtils.escapeJava(String.valueOf(m.get(k))).replaceAll("\\\\u2028", "")));
                        }
                        l.add(m);
                    }
                    brand.put("goods", l);
                }
                map.put(name, brands);
            } else {
                map.put(name, JSON.parse(content));
            }
        }
        jsonResultHelper.buildSuccessJsonResult(map);
    }

}
