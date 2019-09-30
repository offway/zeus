package cn.offway.zeus;

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

}
