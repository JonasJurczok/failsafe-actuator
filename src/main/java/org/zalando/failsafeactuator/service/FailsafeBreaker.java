package org.zalando.failsafeactuator.service;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, PARAMETER, FIELD})
@Retention(RUNTIME)
@Documented
@Deprecated
public @interface FailsafeBreaker {

  String value();
}
