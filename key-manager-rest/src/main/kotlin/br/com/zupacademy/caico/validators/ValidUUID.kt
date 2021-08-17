package br.com.zupacademy.caico.validators

import javax.validation.Constraint
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@ReportAsSingleViolation
@Constraint(validatedBy = [])
@Pattern(regexp = "/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$/i",
         flags = [Pattern.Flag.CASE_INSENSITIVE])
@Retention(RUNTIME)
@Target(FIELD, CONSTRUCTOR, PROPERTY, VALUE_PARAMETER)
annotation class ValidUUID(val message: String)
