package org.isfce.pid.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.isfce.pid.util.validation.annotation.DatesPastAndFutureValidation;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import lombok.AllArgsConstructor;

@ActiveProfiles("testU")
class Test_AnnotationDatesPastAndFutureValidation {

	@DatesPastAndFutureValidation(d1 = "date1", d2 = "date2")
	@AllArgsConstructor
	class DateTest {
		LocalDate date1;
		LocalDate date2;
	}

	@Test
	void testPastAndFutureAnnotation() {
		LocalDate d1 = LocalDate.of(2019, 12, 2);
		LocalDate d2 = LocalDate.of(2020, 11, 1);
		LocalDate d3 = LocalDate.of(1900, 12, 7);
		DateTest goodDate1 = new DateTest(d1, d2);
		DateTest goodDate2 = new DateTest(d1, null);
		DateTest goodDate3 = new DateTest(null, null);
		DateTest badDate1 = new DateTest(d1, d3);
		// Création d'un Validateur
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		// Validation d'un module ok
		Set<ConstraintViolation<DateTest>> constraintViolations = validator.validate(goodDate1);
		assertTrue(constraintViolations.size() == 0);
		
		// Validation avec une date à null
		constraintViolations = validator.validate(goodDate2);
		assertEquals(0, constraintViolations.size());
		
		// Validation avec 2 date à null
		constraintViolations = validator.validate(goodDate3);
		assertEquals(0, constraintViolations.size());
		
		
		
		// Validation d'un erreur ou d2<d1
		constraintViolations = validator.validate(badDate1);
		assertEquals(1,constraintViolations.size());

	}
}
