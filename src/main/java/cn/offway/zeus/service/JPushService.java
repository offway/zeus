package cn.offway.zeus.service;

import java.util.Date;
import java.util.Map;

public interface JPushService {
    void sendPushUser(String title, String alert, Map<String, String> extras, String... alias);

    String createSingleSchedule(String name, Date time, String title, String alert, Map<String, String> extras, String... alias);

    void deleteSchedule(String schedule_id);
}
