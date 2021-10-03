package org.isfce.pid.service.dto;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.isfce.pid.model.Professeur;
import org.isfce.pid.model.Roles;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Data;

@Data
//@AllArgsConstructor
public class ProfesseurDto {

	private Integer id;

	@Valid // N�cessaire pour la validation en cascade Prof==>User
	private UserDto user;

	@NotNull()
	@Size(min = 1, max = 40, message = "{elem.nom}")
	private String nom;
	@NotNull()
	@Size(min = 1, max = 40, message = "{elem.prenom}")
	private String prenom;

	@Email(message = "{email.nonValide}")
	private String email;

	/**
	 * @param id
	 */
	public ProfesseurDto() {
		id = null;
		user = new UserDto();
		user.setRole(Roles.ROLE_PROF);
	}

	/**
	 * @param id
	 * @param user
	 * @param nom
	 * @param prenom
	 * @param email
	 */
	public ProfesseurDto(Integer id, UserDto user, String nom, String prenom, String email) {
		this.id = id;
		user.setRole(Roles.ROLE_PROF);// Assure que ce soit bien le role_prof
		this.user = user;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
	}

	/**
	 * Conversion Dto ==> Professeur
	 * 
	 * @return Professeur sans cryptage du PW
	 */
	public Professeur toProfesseur() {
		return new Professeur(id, nom, prenom, email, user.toUser());
	}

	/**
	 * Conversion Dto ==> Professeur cryte le pw
	 * 
	 * @return Professeur avec le pw crypt�
	 */
	public Professeur toProfesseur(PasswordEncoder encodeur) {
		return new Professeur(id, nom, prenom, email, user.toUser(encodeur));
	}

	/**
	 * Conversion Professeur ==> Dto
	 * 
	 * @param prof
	 * @return
	 */
	public static ProfesseurDto toDto(Professeur prof) {
		UserDto uDto = UserDto.toUserDto(prof.getUser());
		ProfesseurDto pDto = new ProfesseurDto(prof.getId(), uDto, prof.getNom(), prof.getPrenom(), prof.getEmail());
		return pDto;
	}

}
