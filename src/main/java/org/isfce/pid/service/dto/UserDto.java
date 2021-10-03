package org.isfce.pid.service.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.isfce.pid.model.Roles;
import org.isfce.pid.model.User;
import org.isfce.pid.util.validation.annotation.PasswordValueMatch;
import org.isfce.pid.util.validation.annotation.ValidPassword;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;
import lombok.Data;

@PasswordValueMatch.List({
		@PasswordValueMatch(groups = CredentialValidation.class, field = "password", fieldMatch = "confirmPassword", message = "{PasswordMatchError}") })

@Data
@AllArgsConstructor
public class UserDto {

	@NotBlank(groups = CredentialValidation.class)
	private String username;

	@ValidPassword(groups = CredentialValidation.class)
	private String password;

	@ValidPassword(groups = CredentialValidation.class)
	private String confirmPassword;

	@NotNull(groups = CredentialValidation.class)
	private Roles role;

	@Valid
	public UserDto() {
		username = "";
		password = "";
		confirmPassword = "";
	}

	/**
	 * Cr�er un user � partir d'un Dto sans crypter le password
	 * 
	 * @param encodeur
	 * @return User
	 */
	public User toUser() {
		return new User(username, password, role);
	}

	/**
	 * Cr�er un user � partir d'un Dto et crypte le password
	 * 
	 * @param encodeur
	 * @return user avec le pw crypt�
	 */
	public User toUser(PasswordEncoder encodeur) {
		return new User(username, encodeur.encode(password), role);
	}

	/**
	 * Cr�e un userDto � partir d'un user
	 * 
	 * @param user
	 * @return
	 */
	public static UserDto toUserDto(User user) {
		return new UserDto(user.getUsername(), user.getPassword(), user.getPassword(), user.getRole());
	}
}
