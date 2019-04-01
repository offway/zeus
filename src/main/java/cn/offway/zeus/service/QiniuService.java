package cn.offway.zeus.service;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qiniu.util.Auth;
import com.qiniu.util.UrlSafeBase64;

import cn.offway.zeus.properties.QiniuProperties;



@Service
public class QiniuService {

	private static Logger logger = LoggerFactory.getLogger(QiniuService.class);

	@Autowired
	private QiniuProperties qiniuProperties;
	
	/**
	 * 资源删除
	 * @param name 资源名
	 * @return
	 */
	public  boolean qiniuDelete(String name){
		try {
			logger.info("七牛资源删除:{}",name);
			boolean result = false;
			CloseableHttpClient httpClient =  HttpClients.createDefault();
			String url = "http://rs.qiniu.com/delete/"+UrlSafeBase64.encodeToString(qiniuProperties.getBucket()+":"+name);
			HttpPost httppost = new HttpPost(url); 
			Header[] headers = new Header[2];
			headers[0] = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
			Auth auth = Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
			headers[1] = new BasicHeader("Authorization", auth.authorization(url).get("Authorization").toString());
			
			httppost.setHeaders(headers);
			try {
				CloseableHttpResponse response = httpClient.execute(httppost);
				try {  
					int statusCode = response.getStatusLine().getStatusCode();
					logger.info("七牛资源删除返回状态:{}",statusCode);
					if(200 == statusCode){
						result = true;
						logger.info("七牛资源删除成功:{}",name);
					}else{
						HttpEntity responseEntity = response.getEntity();  
						if (responseEntity != null) {  
							logger.info("七牛资源删除失败，资源名:{},异常原因:{}",name,EntityUtils.toString(responseEntity, "UTF-8"));
						}  
						result = false;
					}
			    } finally {  
			        response.close();  
			    }  
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("七牛资源删除异常",e);
			}finally {  
			    // 关闭连接,释放资源    
			    try {  
			    	httpClient.close();  
			    } catch (IOException e) {  
			        e.printStackTrace();  
			    }  
			} 
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("七牛资源删除异常",e);
			return false;
		}
	}
	
}
