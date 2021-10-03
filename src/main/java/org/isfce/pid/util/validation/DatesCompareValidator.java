package org.isfce.pid.util.validation;

import java.lang.reflect.Field;
import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.isfce.pid.util.validation.annotation.DatesPastAndFutureValidation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatesCompareValidator implements ConstraintValidator<DatesPastAndFutureValidation, Object> {
	// valeurs des attributs de l'annotation
	private String d1;
	private String d2;

//Charge la valeur des deux attributs de l'annotation
	public void initialize(DatesPastAndFutureValidation constraintAnnotation) {
		this.d1 = constraintAnnotation.d1();
		this.d2 = constraintAnnotation.d2();
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		LocalDate lc1;
		LocalDate lc2;

		// par réflexion charge la valeur des 2 champs de l'objet.
		// d1 et d2 désignent les 2 noms de champs
		Class<? extends Object> classe = obj.getClass();

		try {
			// récupère les champs de la classe pour d1 et d2
			Field f1 = classe.getDeclaredField(d1);
			f1.trySetAccessible();

			Field f2 = classe.getDeclaredField(d2);
			f2.trySetAccessible();

			// Obtient la valeur des champs de l'objet obj
			lc1 = (LocalDate) f1.get(obj);
			lc2 = (LocalDate) f2.get(obj);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
			log.debug(e1.getMessage());
			return false;
		}

		// not set
		if ((lc1 == null) || (lc2 == null))
			return true;

		// Vérifie si d1<d2
		return lc1.isBefore(lc2);
	}

}
