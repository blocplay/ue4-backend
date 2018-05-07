package com.tokenplay.ue4.www.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;

/**
 * The Class Log4JController.
 */
@Data
@Controller
@RequestMapping(value = "/log4j")
public class Log4JController {

    /**
     * List.
     *
     * @return the response entity
     */
    @RequestMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<List<LogSpecification>> list() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        return new ResponseEntity<>(listLoggers(ctx), HttpStatus.OK);
    }

    /**
     * Sets the.
     *
     * @param name
     *        the name
     * @param level
     *        the level
     * @return the response entity
     */
    @RequestMapping(value = "set/{name}/{level}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET,
        headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<List<LogSpecification>> set(@PathVariable("name")
    final String name, @PathVariable("level")
    final Level level) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        synchronized (ctx) {
            final Configuration config = ctx.getConfiguration();
            LoggerConfig loggerConfig = config.getLoggerConfig(name);
            if (name.equalsIgnoreCase(loggerConfig.getName())) {
                loggerConfig.setLevel(level);
            } else {
                LoggerConfig newloggerConfig = new LoggerConfig(name, level, true);
                config.addLogger(name, newloggerConfig);
            }
            ctx.updateLoggers();
        }
        return new ResponseEntity<>(listLoggers(ctx), HttpStatus.OK);
    }

    /**
     * Unset.
     *
     * @param name
     *        the name
     * @return the response entity
     */
    @RequestMapping(value = "unset/{name}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET,
        headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<List<LogSpecification>> unset(@PathVariable("name")
    final String name) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        synchronized (ctx) {
            final Configuration config = ctx.getConfiguration();
            config.removeLogger(name);
            ctx.updateLoggers();
        }
        return new ResponseEntity<>(listLoggers(ctx), HttpStatus.OK);
    }

    /**
     * Reset.
     *
     * @return the response entity
     */
    @RequestMapping(value = "reset", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<List<LogSpecification>> reset() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        synchronized (ctx) {
            ctx.reconfigure();
        }
        return new ResponseEntity<>(listLoggers(ctx), HttpStatus.OK);
    }

    private List<LogSpecification> listLoggers(final LoggerContext ctx) {
        List<LogSpecification> result = new ArrayList<>();
        synchronized (ctx) {
            final Configuration config = ctx.getConfiguration();
            config.getLoggers().forEach((name, configuration) -> {
                result.add(new LogSpecification(name, configuration.getLevel().name()));
            });
        }
        return result;
    }

}
