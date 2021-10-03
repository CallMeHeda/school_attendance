package org.isfce.pid.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.isfce.pid.util.validation.annotation.DatesPastAndFutureValidation;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TMODULE")
@DatesPastAndFutureValidation(d1 = "dateDebut", d2 = "dateFin", message = "{date.compare}")
public class Module {
	public static enum MAS {
		MATIN, APM, SOIR
	}

	@Id
	@Pattern(regexp = "[A-Z]{2,8}[0-9]{0,3}-[0-9]-[A-Z]", message = "{elem.code}")
	@Column(length = 15)
	private String code;

	@NotNull
	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateDebut;

	@NotNull
	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateFin;

	@Column(nullable = false)
	private MAS moment;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FKCOURS", nullable = false)
	@NotNull
	private Cours cours;
	
//	// ALLOW DELETE
//	@OneToMany(orphanRemoval = true, cascade = CascadeType.PERSIST, mappedBy = "module")
//    private List<Seance> seance;
	
	//D�finition de la relation avec la table interm�diaire
	 @ManyToMany(cascade= CascadeType.PERSIST)
	 @JoinTable(name= "TINSCRIPTION", joinColumns= @JoinColumn(name= "FKMODULE"), 
	            inverseJoinColumns=   @JoinColumn(name= "FKETUDIANT"))
	 protected Set<Etudiant> etudiants=new HashSet<Etudiant>();
	 
	 @ManyToOne(cascade = CascadeType.PERSIST)
	 @JoinColumn(name = "FKPROF", nullable = true)
		private Professeur prof;

	/**
	 * @param code
	 * @param dateDebut
	 * @param dateFin
	 * @param moment
	 * @param cours
	 * @param professeur
	 */
	public Module(@Pattern(regexp = "[A-Z]{2,8}[0-9]{0,3}-[0-9]-[A-Z]", message = "{elem.code}") String code,
			@NotNull LocalDate dateDebut, @NotNull LocalDate dateFin, MAS moment, @NotNull Cours cours, Professeur professeur) {
		super();
		this.code = code;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.moment = moment;
		this.cours = cours;
		this.prof = professeur;
	}
	 
}
