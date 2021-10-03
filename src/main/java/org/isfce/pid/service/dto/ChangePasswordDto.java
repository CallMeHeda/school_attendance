package org.isfce.pid.service.dto;

import org.isfce.pid.model.User;
import org.isfce.pid.util.validation.annotation.PasswordValueMatch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@PasswordValueMatch.List({
		@PasswordValueMatch(groups = CredentialValidation.class, field = "password", fieldMatch = "confirmPassword", message = "{PasswordMatchError}") })

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChangePasswordDto extends UserDto {
	
	//Pas besoin de valider le format de l'ancien PW
	private String oldPassword;

	/**
	 * Création d'un ChangePasswordDto avec les pw initialisés à une chaine vide
	 */
	public ChangePasswordDto() {
		super();
		this.oldPassword = "";
	}

	/**
	 * Crée un ChangePasswordDto à partir d'un user
	 * 
	 * @param user
	 * @return un Changepassword Dto avec les pw vides
	 */
	public static ChangePasswordDto createPwDto(User user) {
		ChangePasswordDto cpd = new ChangePasswordDto();
		cpd.setUsername(user.getUsername());
		cpd.setRole(user.getRole());
		return cpd;
	}
	
}
