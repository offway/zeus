package cn.offway.zeus;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZeusApplicationTests {

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
                .loginUserId("uuuuserId")                   // 登录用户ID (必填)
                .addEventVariable("place", "ni cai")          // 事件级变量 (选填)
                .addEventVariable("channel", "1")      // 事件级变量 (选填)
                .addEventVariable("productId", 666)            // 事件级变量 (选填)
                .build();
        //上传事件行为消息到服务器
        GrowingAPI.send(eventMessage);
    }

}
