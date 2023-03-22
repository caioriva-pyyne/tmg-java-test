package com.example.tmgjavatest.controller.advice;

import com.example.tmgjavatest.model.dto.response.StandardResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Class that handles successful results from the application and wrap them into a
 * standard response.
 */
@ControllerAdvice
public class SuccessfulResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return (AnnotationUtils.findAnnotation(returnType.getContainingClass(), ResponseBody.class) != null ||
                returnType.getMethodAnnotation(ResponseBody.class) != null);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse) {
            var status = ((ServletServerHttpResponse) response).getServletResponse().getStatus();
            if(HttpStatus.OK.equals(HttpStatus.valueOf(status))) return new StandardResponse<>(HttpStatus.OK, body);
        }

        return body;
    }
}
