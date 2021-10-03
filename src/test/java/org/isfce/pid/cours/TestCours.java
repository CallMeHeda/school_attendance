package org.isfce.pid.cours;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.isfce.pid.model.Cours;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class TestCours {

	static ValidatorFactory factory;
	static Validator validator;

	@BeforeAll
	static void InitClasse() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testCoursOK() {

		Cours cours = new Cours("IPID", "Projet d'Integration", (short) 60);
		cours.getSections().add("Informatique");
		Set<ConstraintViolation<Cours>> constraintViolations = validator.validate(cours);
		for (ConstraintViolation<Cours> contraintes : constraintViolations) {
			log.debug(contraintes.getRootBeanClass().getSimpleName()+
		    	"."+ contraintes.getPropertyPath() +
				" "+ contraintes.getMessage());
			}
		
		assertTrue(constraintViolations.isEmpty());
	}
@Test
	void testBadCoursOK() {

		Cours cours = new Cours("IPID-33-33", "", (short) -60);
		
		Set<ConstraintViolation<Cours>> constraintViolations = validator.validate(cours);
	for (ConstraintViolation<Cours> contraintes : constraintViolations) {
			log.debug(contraintes.getRootBeanClass().getSimpleName()+
		    	"."+ contraintes.getPropertyPath() +
				" "+ contraintes.getMessage());
			}
		assertEquals(5,constraintViolations.size());
	}
}
