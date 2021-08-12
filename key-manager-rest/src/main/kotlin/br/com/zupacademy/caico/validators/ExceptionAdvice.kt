package br.com.zupacademy.caico.validators


import io.micronaut.aop.Around
import io.micronaut.context.annotation.Type

import kotlin.annotation.AnnotationRetention.RUNTIME

@MustBeDocumented
@Retention(RUNTIME)
@Target(AnnotationTarget.CLASS,
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
@Around
@Type(ExceptionHandlerInterceptor::class)
annotation class ExceptionAdvice