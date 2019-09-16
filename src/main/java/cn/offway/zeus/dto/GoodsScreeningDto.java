package cn.offway.zeus.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

/**
 * 商品筛选
 *
 * @author wn
 */
public class GoodsScreeningDto implements Serializable {

    @ApiModelProperty("排序:[0-默认，1-价格降序，2-价格升序，3-销量，4-新品]")
    private int sort;

    @ApiModelProperty("最低价格")
    private Long priceMini;

    @ApiModelProperty("最高价格")
    private Long priceMax;

    @ApiModelProperty("风格")
    private String style;

    @ApiModelProperty("商品属性")
    private String attribute;

    @ApiModelProperty("品类")
    private String category;

    @ApiModelProperty("分类")
    private String type;

    @ApiModelProperty("页码,从0开始")
    private int page;

    @ApiModelProperty("页大小")
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public Long getPriceMini() {
        return priceMini;
    }

    public void setPriceMini(Long priceMini) {
        this.priceMini = priceMini;
    }

    public Long getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(Long priceMax) {
        this.priceMax = priceMax;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
