package com.kalita.app.controllers;

import com.kalita.app.service.OrderService;
import com.kalita.app.service.UserService;
import com.kalita.context.Context;
import com.kalita.context.annotation.Controller;
import com.kalita.context.annotation.Injected;
import com.kalita.context.exeptions.CriticalException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.kalita.app.utils.RequestUtils.getParamValue;


@Controller(path = "/buy")
public class OrderController implements HttpHandler {

    private final static Logger log;

    static {
        try (InputStream stream = Context.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(stream);
            log = Logger.getLogger(Context.class.getName());
        } catch (IOException ignore) {
            throw new CriticalException("Logger not initialized");
        }
    }
    private final UserService userService;
    private final OrderService orderService;

    @Injected
    public OrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @Override
    public void handle(HttpExchange request) {
        String[] splitParams = request.getRequestURI().getQuery().split("&");
        String username = getParamValue(splitParams,"user");
        String item = getParamValue(splitParams, "item");
        String user = userService.getUser(username);
        String result = orderService.buyItem(user, item);
        try (OutputStream os = request.getResponseBody()) {
            request.sendResponseHeaders(200, result.length());
            os.write(result.getBytes());
        } catch (IOException e) {
            log.severe("Something went wrong: " + e.getMessage());
        }
    }
}