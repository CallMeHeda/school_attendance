package org.isfce.pid.util.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.isfce.pid.util.validation.PasswordFieldsValueMatchValidator;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordFieldsValueMatchValidator.class)
@Documented
public @interface PasswordValueMatch {

	String message() default "Fields values don't match!";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String field();

	String fieldMatch();

	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@interface List {
		PasswordValueMatch[] value();
	}
}
