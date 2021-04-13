package common.utils.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Optional;

/**
 * @author :fengxi
 * @date :Created in 2021/4/13 5:44 下午
 * @description：
 * @modified By:
 */
@ControllerAdvice
@Slf4j
public class ResponseIntercepter implements ResponseBodyAdvice<Object> {

    private final static String  SUCCESS = "SUCCESS";

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        if (o instanceof Response) {
            Response response = (Response) o;
            if (SUCCESS.equalsIgnoreCase(response.getMsg())) {
                response.setMsg(SUCCESS);
            }
        }
        return o;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity processMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Response failure=new Response();
        failure.setCode(BizExceptionEnum.PARAM_ERROR.getCode());
        failure.setMsg(this.bindResultSimpleStr(e.getBindingResult()));

        log.error(failure.getMsg(), e);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(failure);
    }

    /**
     * 【获取校验失败参数提示】
     * @param bindingResult
     * @return
     */
    private String bindResultSimpleStr(BindingResult bindingResult) {
        Optional<String> optional = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).reduce((s1, s2) -> {
            return s1.concat(",").concat(s2);
        });
        return optional.isPresent() ? (String)optional.get() : "validation error";
    }
}
