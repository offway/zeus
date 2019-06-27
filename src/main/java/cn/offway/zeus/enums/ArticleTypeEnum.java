package cn.offway.zeus.enums;

/**
 * 类型[0-资讯，1-专题，2-视频]
 * @author wn
 *
 */
public enum ArticleTypeEnum {
	

	NEWS("0", "资讯"),

	SPECIAL_TOPIC("1", "专题"),

	VIDEO("2", "视频");

    private String code;

    private String desc;

    private ArticleTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ArticleTypeEnum getByCode(String code) {
        for (ArticleTypeEnum enumObj : ArticleTypeEnum.values()) {
            if (enumObj.getCode().equals(code)) {
                return enumObj;
            }
        }
        return null;
    }
    
    public static ArticleTypeEnum getByDesc(String desc) {
        for (ArticleTypeEnum enumObj : ArticleTypeEnum.values()) {
            if (enumObj.getDesc().equals(desc)) {
                return enumObj;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
