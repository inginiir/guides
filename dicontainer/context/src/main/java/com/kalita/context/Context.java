package com.kalita.context;

import com.kalita.context.annotation.Controller;
import com.kalita.context.annotation.Injected;
import com.kalita.context.exeptions.CriticalException;
import com.kalita.context.utils.ReflectionUtils;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Context {

    private final static Logger log;
    private static final String MESSAGE = "Class must contain only one constructor with @Injected annotation or just default constructor";
    private static final int PORT = 8080;
    private static final Set<Class<?>> components = new HashSet<>();
    private static final Map<String, Object> iocContainer = new HashMap<>();

    static {
        try (InputStream stream = Context.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(stream);
            log = Logger.getLogger(Context.class.getName());
        } catch (IOException ignore) {
            throw new CriticalException("Logger not initialized");
        }
    }

    public static void run(Class<?> application) {
        Set<Class<?>> componentsSet = ReflectionUtils.findComponents(application);

        components.addAll(componentsSet);

        createBeans();

        startHttpServer();
    }

    private static void createBeans() {
        log.info("Starting creating beans...");
        components.forEach(component -> {
            try {
                resolveDependencies(component);
            } catch (Exception e) {
                log.severe("Something went wrong: " + e.getMessage());
            }
        });
    }

    private static void resolveDependencies(Class<?> component) throws Exception {
        String componentName = component.getSimpleName();
        log.info("Creating bean " + componentName);
        Constructor<?> actualConstructor = getActualConstructor(component);

        Class<?>[] parameterTypes = actualConstructor.getParameterTypes();
        if (parameterTypes.length == 0) {
            iocContainer.put(componentName, actualConstructor.newInstance());
        } else {
            List<Object> params = new ArrayList<>();
            for (Class<?> parameterType : parameterTypes) {
                Class<?> type = getType(parameterType);
                String name = type.getSimpleName();
                Object dependency = iocContainer.get(name);
                if (dependency == null) {
                    resolveDependencies(type);
                    params.add(iocContainer.get(name));
                } else {
                    params.add(dependency);
                }
            }
            iocContainer.put(componentName, actualConstructor.newInstance(params.toArray()));
        }
    }

    private static Constructor<?> getActualConstructor(Class<?> component) {
        Constructor<?>[] constructors = component.getConstructors();
        return Arrays.stream(constructors)
                .filter(constructor -> constructor.isAnnotationPresent(Injected.class)
                        || constructor.getParameterTypes().length == 0)
                .findFirst().orElseThrow(() -> new CriticalException(MESSAGE));
    }

    private static Class<?> getType(Class<?> parameterType) {
        return components.stream()
                .filter(bean -> Arrays.stream(bean.getInterfaces()).collect(Collectors.toSet()).contains(parameterType))
                .findFirst().orElseThrow();
    }

    private static void startHttpServer() {
        List<Object> controllers = iocContainer.values().stream()
                .filter(o -> o.getClass().isAnnotationPresent(Controller.class))
                .toList();
        log.info("Found " + controllers.size() + " controllers");
        log.info("Starting server...");
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            controllers.forEach(controller -> {
                Controller annotation = controller.getClass().getAnnotation(Controller.class);
                String path = annotation.path();
                server.createContext(path, (HttpHandler) controller);
                log.info("Created context for path " + path);
            });
            server.setExecutor(null);
            server.start();
            log.info("Server started on port " + PORT);
        } catch (IOException e) {
            log.severe("Server didn't start: " + e.getMessage());
        }
    }
}