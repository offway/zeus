package cn.offway.zeus.dto;

import java.io.Serializable;

import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

/**
 * 品牌查询条件
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-02-12 11:26:00 Exp $
 */
public class ArticleDto implements Serializable {


    @ApiModelProperty(value ="类型[0-资讯，1-专题，2-视频]")
    private String type;
    
    @ApiModelProperty(value ="标签")
    private String tag;
    
    @ApiModelProperty(value ="关键字")
    private String wd;

    @ApiModelProperty(required = true,value ="页码,从0开始")
    private int page;
    
    @ApiModelProperty(required = true,value ="页大小")
    private int size;

    
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getWd() {
		return wd;
	}

	public void setWd(String wd) {
		this.wd = wd;
	}
   
}
