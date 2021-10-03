package org.isfce.pid.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.isfce.pid.model.Roles;
import org.isfce.pid.service.dto.CredentialValidation;
import org.isfce.pid.service.dto.ProfesseurDto;
import org.isfce.pid.service.dto.UserDto;
import org.junit.jupiter.api.Test;

class Test_Prof_User_Validation {

	@Test
	void test() {
		UserDto user=new UserDto( "  i","aa","bb",Roles.ROLE_PROF);
		ProfesseurDto p=new ProfesseurDto(2,  user,"nom","prenom", "email@ok.com");
		// Création d'un Validateur
				ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
				Validator validator = factory.getValidator();
				// Validation d'un module ok
				Set<ConstraintViolation<ProfesseurDto>> constraintViolations = validator.validate(p,CredentialValidation.class);

				assertEquals(3,constraintViolations.size());
	}

}
