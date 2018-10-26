package cn.offway.zeus.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class JsonResultHelper {
    /** 信息帮助类 */
    @Autowired
    private MessageHelper  messageHelper;

    /**
     * 根据结果码创建失败的Json结果对象
     * <p>不会校验结果码是否为<code>NULL</code>，应用需保证</p>
     *
     * @param resultCode 结果码
     * @return Json结果对象
     * @throws org.springframework.context.NoSuchMessageException 文案配置不存在抛出
     */
    public JsonResult buildFailJsonResult(ResultCode resultCode) {
        return buildFailJsonResult(resultCode.getStatusCode(), resultCode.getErrorCode());
    }
    
    public JsonResult buildFailJsonResult(ResultCode resultCode,Object data) {
        return buildFailJsonResult(resultCode.getStatusCode(), resultCode.getErrorCode(),data);
    }

    /**
     * 根据状态码和错误码创建失败的Json结果对象
     * <p>错误码对应的文案需要应用确保存在</p>
     *
     * @param statusCode 状态码
     * @param errorCode 错误码
     * @return Json结果对象
     * @throws org.springframework.context.NoSuchMessageException 文案配置不存在抛出
     */
    public JsonResult buildFailJsonResult(String statusCode, String errorCode) {
        String message = messageHelper.getMessage(errorCode);
        JsonResult jsonResult = new JsonResult();
        jsonResult.setCode(statusCode);
        jsonResult.setMsg(message);
        return jsonResult;
    }
    
    
    public JsonResult buildFailJsonResult(String statusCode, String errorCode,Object data) {
        String message = messageHelper.getMessage(errorCode);
        JsonResult jsonResult = new JsonResult();
        jsonResult.setCode(statusCode);
        jsonResult.setMsg(message);
        jsonResult.setData(data);
        return jsonResult;
    }

    /**
     * 创建成功的Json结果对象
     *
     * @param data 结果数据，可以为<code>NULL</code>
     * @return Json结果对象
     */
    public JsonResult buildSuccessJsonResult(Object data) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setData(data);
        return jsonResult;
    }

    /**
     * 创建成功的Json结果对象
     *
     * @param data 结果数据，可以为<code>NULL</code>
     * @return Json结果对象
     */
    public JsonResult buildSuccessJsonResult(Object data,String message) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setData(data);
        jsonResult.setMsg(message);
        return jsonResult;
    }
}
