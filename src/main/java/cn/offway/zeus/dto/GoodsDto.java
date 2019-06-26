package cn.offway.zeus.dto;

import java.io.Serializable;

import org.springframework.data.domain.Sort.Direction;

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
    
    @ApiModelProperty(value ="商户ID")
    private Long merchantId;
    
    @ApiModelProperty(value ="品牌名称")
    private String brandName;
    
    @ApiModelProperty(value ="品牌类型[0-国内品牌，1-国际品牌]")
    private String brandType;

    @ApiModelProperty(value ="类别[男装，女装等]")
    private String type;

    @ApiModelProperty(value ="类目[卫衣，T恤等]")
    private String category;
    
    @ApiModelProperty(value ="优选ID")
    private Long pickId;
    
    @ApiModelProperty(required = true,value ="页码,从0开始")
    private int page;
    
    @ApiModelProperty(required = true,value ="页大小")
    private int size;
    
    @ApiModelProperty(value ="排序方向[asc-顺序,desc-倒序]")
    private String sortDir;
    
    @ApiModelProperty(value ="排序类型[saleCount-销量,viewCount-人气,createTime-新品,price-价格]")
    private String sortName;
    
    @ApiModelProperty(value ="风格")
    private String style;

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

	public String getSortDir() {
		return sortDir;
	}

	public void setSortDir(String sortDir) {
		this.sortDir = sortDir;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getBrandType() {
		return brandType;
	}

	public void setBrandType(String brandType) {
		this.brandType = brandType;
	}

	public Long getPickId() {
		return pickId;
	}

	public void setPickId(Long pickId) {
		this.pickId = pickId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
    
}
