package cn.offway.zeus.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

import cn.offway.zeus.properties.QiniuProperties;
import cn.offway.zeus.service.QiniuService;



/**
 * 七牛
 * @author wn
 *
 */
@RestController
@RequestMapping("/qiniu")
public class QiniuController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private QiniuProperties qiniuProperties;
	
	@Autowired
	private QiniuService qiniuService;

	@GetMapping("/token")
	public String token(){
		try {
			Auth auth = Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
			StringMap putPolicy = new StringMap();
			putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize),\"fname\":$(fname),\"param\":\"$(x:param)\"}");
			String upToken = auth.uploadToken(qiniuProperties.getBucket(), null, qiniuProperties.getExpireSeconds(), putPolicy);
			return upToken;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取七牛上传文件token异常",e);
			return "";
		}
		
	}
	
	@PostMapping("/delete")
	public boolean delete(String url){
		return qiniuService.qiniuDelete(url.replace(qiniuProperties.getUrl()+"/", ""));
	}
}
