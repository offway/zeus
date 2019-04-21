package cn.offway.zeus.utils;

/**
 * 通用结果枚举
 * @author wn
 *
 */
public enum CommonResultCode implements ResultCode {

    /** 成功 */
    SUCCESS("200", "SUCCESS"),

    /** 系统错误 */
    SYSTEM_ERROR("1000", "SYSTEM_ERROR"),
    
    /** 请求参数不完整 */
    PARAM_MISS("1001", "PARAM_MISS"),
    
    /** 请求参数错误 */
    PARAM_ERROR("1002", "PARAM_ERROR"),
    
    /** 不再活动时间范围 */
    ACTIVITY_END("1003", "ACTIVITY_END"),
    
    /** 活动已参加 */
    ACTIVITY_PARTICIPATED("1004", "ACTIVITY_PARTICIPATED"),
    
    /** 活动已上限 */
    ACTIVITY_LIMIT("1005", "ACTIVITY_LIMIT"),
    
    /** 用户不存在 */
    USER_NOT_EXISTS("1006", "USER_NOT_EXISTS"),
    
    /** 中奖信息不存在 */
    PRIZE_NOT_EXISTS("1007", "PRIZE_NOT_EXISTS"),
    
    /** 短信验证码错误  */
    SMS_CODE_ERROR("1008", "SMS_CODE_ERROR"),
    
    /** 短信验证码失效  */
    SMS_CODE_INVALID("1009", "SMS_CODE_INVALID"),
    
    /** 短信验证码发送失败  */
    SMS_CODE_FAIL("1010", "SMS_CODE_FAIL"),
    
    /** 用户已存在 */
    USER_EXISTS("1011", "USER_EXISTS"),
    
    /** 已经收藏 */
    COLLECT_EXISTS("1012", "COLLECT_EXISTS"),
    
    /** 购物车上线  */
    SHOPPING_CAR_LIMIT("1013", "SHOPPING_CAR_LIMIT"),
    
    /** 库存不足  */
    STOCK_SHORTAGE("1016", "STOCK_SHORTAGE"),
    
    /** 该优惠券已领取  */
    VOUCHER_GIVED("1017", "VOUCHER_GIVED"),
    
    /** 抽奖次数不足  */
    LOTTERYNUM_LESS("1018", "LOTTERYNUM_LESS"),
    
    ;
	
	
    private String errorCode;

    private String statusCode;

    CommonResultCode(String statusCode, String errorCode) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getStatusCode() {
        return statusCode;
    }
}