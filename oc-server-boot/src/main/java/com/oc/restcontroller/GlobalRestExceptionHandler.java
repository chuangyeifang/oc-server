package com.oc.restcontroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * @author chuangyeifang
 */
@RestControllerAdvice
@Slf4j
public class GlobalRestExceptionHandler extends AbstractBasicRestController {

    @ExceptionHandler(value = ClassCastException.class)
    @ResponseBody
    public Object classCastExceptionHandler(ClassCastException e) {
        log.error("类型转化异常", e);
        return failed(5003, "类型转换错误");
    }

    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public Object nullPointerExceptionHandler(NullPointerException e) {
        log.error("空指针异常", e);
        return failed(5002, "服务内部错误");
    }

    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public Object sqlException(SQLException e) {
        log.error("SQL执行异常", e);
        return failed(6001, "服务内部错误");
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e) {
        log.error("其他类型异常", e);
        return failed(5001, "服务内部错误");
    }
}
