package com.tokenplay.ue4.www.controllers.rest;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.tokenplay.ue4.www.controllers.CommonAPI;

public abstract class AbstractRestHandler extends CommonAPI {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public @ResponseBody Object handleUncaughtException(Exception ex, WebRequest request, HttpServletResponse response) {
        log.info("Converting Uncaught exception to RestResponse : " + ex.getMessage());

        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return new String[] {
            "Error occurred", ex.toString()};
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public @ResponseBody Object handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request, HttpServletResponse response) {
        log.info("Converting IllegalArgumentException to RestResponse : " + ex.getMessage());

        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new String[] {
            "Error occurred", ex.toString()};
    }

}
