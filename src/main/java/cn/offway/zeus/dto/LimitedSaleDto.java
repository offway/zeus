package cn.offway.zeus.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * 限量发售
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-02-12 11:26:00 Exp $
 */
public class LimitedSaleDto implements Serializable {


    @ApiModelProperty(value ="类型[0-最新发售，1-即将发售]")
    private String type;

    @ApiModelProperty(required = true,value ="页码,从0开始")
    private int page;
    
    @ApiModelProperty(required = true,value ="页大小")
    private int size;

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
   
}
