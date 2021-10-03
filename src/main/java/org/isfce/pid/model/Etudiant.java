package org.isfce.pid.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "TETUDIANT")
public class Etudiant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@OneToOne(optional = false, cascade = CascadeType.MERGE)
	@JoinColumn(name = "FKUSER", unique = true, nullable = false, updatable = false)
	private User user;

	@NotNull
	@Size(min = 1, max = 40, message = "{elem.nom}")
	@Column(length = 40, nullable = false)
	private String nom;

	@NotNull
	@Column(length = 30)
	private String prenom;

	@Email(message = "{email.nonValide}")
	@NotNull
	@Column(length = 50, nullable = false)
	private String email;

	@NotNull
	@Column(length = 30)
	private String tel;
	
	// Presences
//	@OneToMany(mappedBy = "etudiant") //Nom de l’attribut dans Presence
//	private Set<Presence> presences = new HashSet<>();

	public Etudiant(@NotNull String nom, @NotNull String prenom, @NotNull String email, @NotNull String tel, User user) {
		assert (user.getRole() == Roles.ROLE_ETUDIANT);
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.tel = tel;
		this.user = user;
		//this.user.setRole(Roles.ROLE_ETUDIANT);
	}
}
