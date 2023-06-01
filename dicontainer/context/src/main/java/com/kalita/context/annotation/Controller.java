package com.kalita.context.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String path() default "/";
}
