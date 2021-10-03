package org.isfce.pid.service.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.isfce.pid.model.Etudiant;
import org.isfce.pid.model.Presence;
import org.isfce.pid.model.Roles;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Data;

@Data
//@AllArgsConstructor
public class EtudiantDto {

	private Integer id;

	@NotNull()
	@Size(min = 1, max = 40, message = "{elem.nom}")
	private String nom;
	
	@NotNull()
	@Size(min = 1, max = 30, message = "{elem.prenom}")
	private String prenom;
	
	@Email(message = "{email.nonValide}")
	private String email;
	
	@Size(min = 10, max = 30, message = "{elem.tel}")
	private String tel;
	
	private Set<Presence> presences = new HashSet<>();

	@Valid // Etudiant ==> User
	private UserDto user;

	/**
	 * @param id
	 */
	public EtudiantDto() {
		id = null;
		user = new UserDto();
		user.setRole(Roles.ROLE_ETUDIANT);
	}

	/**
	 * @param id
	 * @param nom
	 * @param prenom
	 * @param email
	 * @param tel
	 * @param user
	 */
	public EtudiantDto(Integer id, String nom, String prenom, String email, String tel, UserDto user) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.tel = tel;
		user.setRole(Roles.ROLE_ETUDIANT);
		this.user = user;
	}

	/**
	 * Conversion Dto -> Etudiant
	 * 
	 * @return Etudiant sans cryptage du password
	 */
	public Etudiant toEtudiant() {
		return new Etudiant(nom, prenom, email,tel, user.toUser());
	}

	/**
	 * Conversion Dto -> Etudiant
	 * 
	 * @return Etudiant avec password crypté
	 */
	public Etudiant toEtudiant(PasswordEncoder encodeur) {
		return new Etudiant(nom, prenom, email,tel, user.toUser(encodeur));
	}

	/**
	 * Conversion Etudiant -> Dto
	 * 
	 * @param stud
	 * @return
	 */
	public static EtudiantDto toDto(Etudiant stud) {
		UserDto uDto = UserDto.toUserDto(stud.getUser());
		EtudiantDto studDto = new EtudiantDto(stud.getId(), stud.getNom(), stud.getPrenom(), stud.getEmail(), stud.getTel(), uDto);
		return studDto;
	}

}