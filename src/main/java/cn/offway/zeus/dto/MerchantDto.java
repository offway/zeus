package cn.offway.zeus.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * 品牌查询条件
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-02-12 11:26:00 Exp $
 */
public class MerchantDto implements Serializable {


    @ApiModelProperty(value ="商户类型[0-品牌商,1-买手店]")
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
