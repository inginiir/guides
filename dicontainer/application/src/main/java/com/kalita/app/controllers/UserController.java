package com.kalita.app.controllers;

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


@Controller(path = "/save")
public class UserController implements HttpHandler {

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

    @Injected
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange request) {
        String query = request.getRequestURI().getQuery();
        String response = userService.saveUser(query.substring(query.indexOf("=") + 1));
        try (OutputStream os = request.getResponseBody()) {
            request.sendResponseHeaders(200, response.length());
            os.write(response.getBytes());
        } catch (IOException e) {
            log.severe("Something went wrong: " + e.getMessage());
        }
    }
}