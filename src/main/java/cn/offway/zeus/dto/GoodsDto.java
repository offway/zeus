package cn.offway.zeus.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * 商品表
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-02-12 11:26:00 Exp $
 */
public class GoodsDto implements Serializable {

    @ApiModelProperty(value ="名称")
    private String name;

    @ApiModelProperty(value ="品牌ID")
    private Long brandId;

    @ApiModelProperty(value ="品牌名称")
    private String brandName;

    @ApiModelProperty(value ="类别")
    private String type;

    @ApiModelProperty(value ="类目")
    private String category;
    
    @ApiModelProperty(required = true,value ="页码,从0开始")
    private int page;
    
    @ApiModelProperty(required = true,value ="页大小")
    private int size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
