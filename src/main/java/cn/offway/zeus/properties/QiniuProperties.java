package cn.offway.zeus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "qiniu")
public class QiniuProperties {

	private String url;
	
	private String accessKey;
	
	private String secretKey;
	
	private String bucket;
	
	private long expireSeconds = 3600;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public long getExpireSeconds() {
		return expireSeconds;
	}

	public void setExpireSeconds(long expireSeconds) {
		this.expireSeconds = expireSeconds;
	} 
	
}
