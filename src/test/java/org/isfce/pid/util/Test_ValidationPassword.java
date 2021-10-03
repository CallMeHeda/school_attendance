package org.isfce.pid.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.isfce.pid.model.Roles;
import org.isfce.pid.service.dto.CredentialValidation;
import org.isfce.pid.service.dto.ProfesseurDto;
import org.isfce.pid.service.dto.UserDto;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class Test_ValidationPassword {

	@Test
	void testValidPassword() {
		UserDto vo=new UserDto("vo","SecretVO1","SecretVO1",Roles.ROLE_PROF);
		UserDto voBad=new UserDto("vo","vo","vo2",Roles.ROLE_PROF);
		// Création d'un Validateur
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		// Validation d'un module ok
		Set<ConstraintViolation<UserDto>> constraintViolations = validator.validate(vo,CredentialValidation.class);

		assertTrue(constraintViolations.size() == 0);
		// Validation d'un module bad
		constraintViolations = validator.validate(voBad,CredentialValidation.class);
		assertTrue(constraintViolations.size() >= 1);
	}
	
@Test
	void testConfirmPassword() {
		UserDto vo=new UserDto("vo","SecretVO1","SecretVO1",Roles.ROLE_PROF);
		UserDto voBad=new UserDto("vo","SecretVO1","SecretVO44",Roles.ROLE_PROF);
		// Création d'un Validateur
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		// Validation d'un module ok
		Set<ConstraintViolation<UserDto>> constraintViolations = validator.validate(vo,CredentialValidation.class);

		assertTrue(constraintViolations.size() == 0);
		// Validation d'un module bad
		constraintViolations = validator.validate(voBad,CredentialValidation.class);
		log.debug(constraintViolations.toString());
		assertEquals(1,constraintViolations.size());
	}

@Test
	void testProfDtoPassword() {
	ProfesseurDto pOk=new ProfesseurDto(1,new UserDto("test","SecretT1","SecretT1",Roles.ROLE_PROF),"UnMon", "UnPrenom", "email@gmail.com");
	ProfesseurDto pBad1=new ProfesseurDto(1,new UserDto(" ","SecretT1","SecretT1",Roles.ROLE_PROF),"UnMon", "UnPrenom", "email");
	ProfesseurDto pBad2=new ProfesseurDto(1,new UserDto("tralala","Secret","SecretT1",Roles.ROLE_PROF),"UnMon", "UnPrenom", "email");
	
		// Création d'un Validateur
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		// Validation d'un module ok
		Set<ConstraintViolation<ProfesseurDto>> constraintViolations = validator.validate(pOk,CredentialValidation.class,Default.class);
		assertTrue(constraintViolations.size() == 0);
		
		// Validation de bad data
		constraintViolations = validator.validate(pBad1,CredentialValidation.class,Default.class);
		log.debug(constraintViolations.toString());
		assertEquals(2,constraintViolations.size());
		
		constraintViolations = validator.validate(pBad2,CredentialValidation.class,Default.class);
		log.debug(constraintViolations.toString());
		assertEquals(3,constraintViolations.size());
		
	}

}
