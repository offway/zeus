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
    
    /** 您已经签过到了  */
    SIGNED("1019", "SIGNED"),
    
    /** 您已经打过call了  */
    CALL_LIMIT("1020", "CALL_LIMIT"),
    
    /** 一笔订单只能提交一次退款申请  */
    REFUND_APPLIED("1021", "REFUND_APPLIED"),
    
    /** 确认收货后七天不能退款  */
    REFUND_TIMEOUT("1022", "REFUND_TIMEOUT"),
    
    /** 兑换码错误或已使用  */
    CODE_ERROR("1023", "CODE_ERROR"),

    /** ta的助力已满，已经获得奖励啦~  */
    FREE_LIMIT("1024", "FREE_LIMIT"),

    /** 不能重复助力  */
    FREE_BOOSTED("1025", "FREE_BOOSTED"),

    /** 已抢光  */
    FREE_LESS("1026", "FREE_LESS"),

    /** 不能给自己助力  */
    FREE_BOOST_MY("1027", "FREE_BOOST_MY"),

    /** 余额不足  */
    BALANCE_LESS("1028", "BALANCE_LESS"),

    /** 已提交退换货申请 */
    REFUNDING("1029", "REFUNDING"),

    /** 该笔订单已全额退款，无法申请售后  */
    REFUND_ALL("1030", "REFUND_ALL"),

    /** 订单中包含已下架的商品,请确认后操作  */
    GOODS_OFF("1031", "GOODS_OFF"),

    /** 商品加构件数（含已加购件数）已超过库存  */
    ADD_SHOPCART_STOCK_LESS("1032", "ADD_SHOPCART_STOCK_LESS"),

    /** 购物车含有库存不足的商品，请重新选择数量~  */
    INIT_STOCK_LESS("1033", "INIT_STOCK_LESS"),

    /** 限量商品无法收藏 */
    LIMITEDSALE_NOT_COLLECT("1034", "LIMITEDSALE_NOT_COLLECT"),

    /** 限量商品不能查看详情 */
    LIMITEDSALE_NOT_INFO("1035", "LIMITEDSALE_NOT_INFO"),

    /** 限量商品不能加入购物车 */
    LIMITEDSALE_NOT_CAR("1036", "LIMITEDSALE_NOT_CAR"),



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