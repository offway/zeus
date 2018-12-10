package cn.offway.zeus.enums;

/**
 * 来源
 * @author wn
 *
 */
public enum TicketSourceEnum {
	

	JOIN("0", "登记"),

	INVITE("1", "邀请好友"),

	SHARE("2", "分享"),

	APP_REGISTER("3", "APP注册");

    private String code;

    private String desc;

    private TicketSourceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TicketSourceEnum getByCode(String code) {
        for (TicketSourceEnum enumObj : TicketSourceEnum.values()) {
            if (enumObj.getCode().equals(code)) {
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
