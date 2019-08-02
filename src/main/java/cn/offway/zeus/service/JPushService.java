package cn.offway.zeus.service;

import java.util.Map;

public interface JPushService {

    boolean sendPush(String title, String alert, Map<String, String> extras);

    boolean sendPushUser(String title, String alert, Map<String, String> extras, String... alias);

}
