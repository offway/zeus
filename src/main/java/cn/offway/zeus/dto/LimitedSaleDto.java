package cn.offway.zeus.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 限量发售
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-02-12 11:26:00 Exp $
 */
public class LimitedSaleDto implements Serializable {


    @ApiModelProperty(value ="类型[0-最新发售，1-即将发售，2-往期发售]")
    private String type;

    @ApiModelProperty(required = true,value ="页码,从0开始")
    private int page;
    
    @ApiModelProperty(required = true,value ="页大小")
    private int size;

	@ApiModelProperty(value ="渠道,该字段为二进制位运算标识,0否1是,从右到左第一位表示H5,第二位表示小程序,第三位表示APP。如要查询APP则传参为0100,查询H5和小程序则传参0011以此类推")
	private String channel = "0100";


	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
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
   
}
