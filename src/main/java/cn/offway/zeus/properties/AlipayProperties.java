package cn.offway.zeus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

	/** APPID即创建应用后生成	 **/
	private String appid;
	
	/** 开发者应用私钥，由开发者自己生成 **/
	private String privatekey;
	
	/** 支付宝公钥，由支付宝生成 **/
	private String publickey;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getPrivatekey() {
		return privatekey;
	}

	public void setPrivatekey(String privatekey) {
		this.privatekey = privatekey;
	}

	public String getPublickey() {
		return publickey;
	}

	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	
}
