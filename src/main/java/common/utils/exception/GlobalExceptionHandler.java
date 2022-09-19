package common.utils.exception;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fengxi
 * @date 2022年09月13日 10:58
 */
@RestControllerAdvice(
        basePackages = {"com.cecchain"},
        annotations = {RestController.class, Controller.class}
)
public class GlobalExceptionHandler {
    public GlobalExceptionHandler() {
    }

    @ResponseBody
    @ExceptionHandler({ApiException.class})
    public CommonResult handle(ApiException e) {
        return e.getErrorCode() != null ? CommonResult.failed(e.getErrorCode()) : CommonResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public CommonResult handleValidException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message = (String)allErrors.stream().map((s) -> {
            return s instanceof FieldError ? ((FieldError)s).getField() + s.getDefaultMessage() : s.getDefaultMessage();
        }).collect(Collectors.joining(";"));
        return CommonResult.validateFailed(message.toString());
    }

    @ResponseBody
    @ExceptionHandler({BindException.class})
    public CommonResult handleValidException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }

        return CommonResult.validateFailed(message);
    }

    @ResponseBody
    @ExceptionHandler
    public CommonResult handle(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        StringBuilder builder = new StringBuilder();
        Iterator var4 = violations.iterator();
        if (var4.hasNext()) {
            ConstraintViolation<?> violation = (ConstraintViolation)var4.next();
            builder.append(violation.getMessage());
        }

        return CommonResult.validateFailed(builder.toString());
    }

    @ResponseBody
    @ExceptionHandler
    public CommonResult handle(Exception exception) {
        exception.printStackTrace();
        return CommonResult.failed(exception.getMessage());
    }
}
