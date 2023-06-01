package com.kalita.context.utils;

import com.kalita.context.Context;
import com.kalita.context.annotation.Component;
import com.kalita.context.annotation.PackageScan;
import com.kalita.context.exeptions.CriticalException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReflectionUtils {

    private static final char DOT = '.';
    private final static Logger log;

    static {
        try (InputStream stream = Context.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(stream);
            log = Logger.getLogger(Context.class.getName());
        } catch (IOException ignore) {
            throw new CriticalException("Logger not initialized");
        }
    }

    public static Set<Class<?>> findComponents(Class<?> application) {
        String packageName = obtainPackageName(application);
        Set<Class<?>> classes = new HashSet<>();
        try {
            String path = packageName.replace(DOT, '/');
            Enumeration<URL> resources = Context.class.getClassLoader().getResources(path);
            List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
        } catch (Exception e) {
            log.severe("Error while scanning project package");
        }
        return classes.stream()
                .filter(ReflectionUtils::isComponent)
                .filter(definition -> !definition.isAnnotation())
                .collect(Collectors.toSet());
    }


    private static String obtainPackageName(Class<?> application) {
        PackageScan packageScan = application.getAnnotation(PackageScan.class);
        return packageScan.packageName();
    }

    private static Set<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + DOT + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + DOT + file.getName().substring(0, file.getName().indexOf(DOT))));
            }
        }
        return classes;
    }

    private static boolean isComponent(Class<?> definition) {
        return definition.isAnnotationPresent(Component.class) || Arrays.stream(definition.getAnnotations())
                .anyMatch(annotation -> annotation.annotationType().isAnnotationPresent(Component.class));
    }
}
