package cn.offway.zeus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wxpay")
public class WxpayProperties {

	/** APPID即创建应用后生成	 **/
	private String appid;
	
	/** 商户号 **/
	private String mchId;
	
	/** 密钥 **/
	private String paternerKey;
	

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getPaternerKey() {
		return paternerKey;
	}

	public void setPaternerKey(String paternerKey) {
		this.paternerKey = paternerKey;
	}
	
}
