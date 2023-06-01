package com.kalita.context.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface Component {
}
