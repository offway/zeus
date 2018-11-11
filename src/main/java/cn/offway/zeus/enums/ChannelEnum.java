package cn.offway.zeus.enums;

/**
 * 来源
 * @author wn
 *
 */
public enum ChannelEnum {
	

	H5("0", "公众号"),

	MINI("1", "小程序"),

	IOS("2", "苹果"),

	ANDROID("3", "安卓");

    private String code;

    private String desc;

    private ChannelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ChannelEnum getByCode(String code) {
        for (ChannelEnum enumObj : ChannelEnum.values()) {
            if (enumObj.getCode().equals(code)) {
                return enumObj;
            }
        }
        return null;
    }
    
    public static ChannelEnum getByDesc(String desc) {
        for (ChannelEnum enumObj : ChannelEnum.values()) {
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
