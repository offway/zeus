package cn.offway.zeus.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;

/**
 * 实现自定义统一异常处理
 * @author wn
 *
 */
@Component
public class ErrorAttributes extends DefaultErrorAttributes {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes,
			boolean includeStackTrace) {
		Map<String, Object> errorAttributes = new LinkedHashMap<String, Object>();
		addStatus(errorAttributes, requestAttributes);
		addErrorDetails(errorAttributes, requestAttributes, includeStackTrace);
		addPath(errorAttributes, requestAttributes);
//		logger.error("系统异常，错误信息：{}",errorAttributes,getError(requestAttributes));
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("code", "500");
		resultMap.put("msg", "系统繁忙，请稍后再试。");
		resultMap.put("data", errorAttributes);
		return resultMap;
	}
	
	private void addStatus(Map<String, Object> errorAttributes,
			RequestAttributes requestAttributes) {
		Integer status = getAttribute(requestAttributes,
				"javax.servlet.error.status_code");
		if (status == null) {
			errorAttributes.put("status", 999);
			errorAttributes.put("error", "None");
			return;
		}
		errorAttributes.put("status", status);
		try {
			errorAttributes.put("error", HttpStatus.valueOf(status).getReasonPhrase());
		}
		catch (Exception ex) {
			// Unable to obtain a reason
			errorAttributes.put("error", "Http Status " + status);
		}
	}
	
	private void addErrorDetails(Map<String, Object> errorAttributes,
			RequestAttributes requestAttributes, boolean includeStackTrace) {
		Throwable error = getError(requestAttributes);
		if (error != null) {
			while (error instanceof ServletException && error.getCause() != null) {
				error = ((ServletException) error).getCause();
			}
			errorAttributes.put("exception", error.getClass().getName());
			addErrorMessage(errorAttributes, error);
			if (includeStackTrace) {
				addStackTrace(errorAttributes, error);
			}
		}
		Object message = getAttribute(requestAttributes, "javax.servlet.error.message");
		if ((!StringUtils.isEmpty(message) || errorAttributes.get("message") == null)
				&& !(error instanceof BindingResult)) {
			errorAttributes.put("message",
					StringUtils.isEmpty(message) ? "No message available" : message);
		}
	}

	private void addErrorMessage(Map<String, Object> errorAttributes, Throwable error) {
		BindingResult result = extractBindingResult(error);
		if (result == null) {
			errorAttributes.put("message", error.getMessage());
			return;
		}
		if (result.getErrorCount() > 0) {
			errorAttributes.put("errors", result.getAllErrors());
			errorAttributes.put("message",
					"Validation failed for object='" + result.getObjectName()
							+ "'. Error count: " + result.getErrorCount());
		}
		else {
			errorAttributes.put("message", "No errors");
		}
	}
	
	private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
		StringWriter stackTrace = new StringWriter();
		error.printStackTrace(new PrintWriter(stackTrace));
		stackTrace.flush();
		errorAttributes.put("trace", stackTrace.toString());
	}

	private void addPath(Map<String, Object> errorAttributes,
			RequestAttributes requestAttributes) {
		String path = getAttribute(requestAttributes, "javax.servlet.error.request_uri");
		if (path != null) {
			errorAttributes.put("path", path);
		}
	}
	
	private BindingResult extractBindingResult(Throwable error) {
		if (error instanceof BindingResult) {
			return (BindingResult) error;
		}
		if (error instanceof MethodArgumentNotValidException) {
			return ((MethodArgumentNotValidException) error).getBindingResult();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
		return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

}
