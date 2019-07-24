package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.Map;

public class SaTrack implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2267692126947512943L;

	/** 用户 ID **/
	private String distinctId;
	
	/** 用户 ID 是否是登录 ID，false 表示该 ID 是一个匿名 ID **/
	private boolean isLoginId;
	
	/** 事件名称 **/
	private String eventName;
	
	/** 事件的属性 **/
	private Map<String, Object> properties;

	public String getDistinctId() {
		return distinctId;
	}

	public void setDistinctId(String distinctId) {
		this.distinctId = distinctId;
	}

	public boolean isLoginId() {
		return isLoginId;
	}

	public void setLoginId(boolean isLoginId) {
		this.isLoginId = isLoginId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
}
