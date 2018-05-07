package com.tokenplay.ue4.www.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;

@Data
@Controller
@RequestMapping({"/health"})
public class HealthController {

    private static enum STATUS {
        OK,
        DISABLED
    }

    private STATUS status = STATUS.OK;

    /**
     * @return The status
     */
    @RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> checkHealth() {
        return currentStatus();
    }

    @RequestMapping(value = "/enable", produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> enable() {
        status = STATUS.OK;
        return currentStatus();
    }

    @RequestMapping(value = "/disable", produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> disable() {
        status = STATUS.DISABLED;
        return currentStatus();
    }

    private ResponseEntity<String> currentStatus() {
        return new ResponseEntity<>(status.name(), HttpStatus.OK);
    }

}
