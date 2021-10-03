package org.isfce.pid.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author E. Fatima
 *
 */
@Data
@EqualsAndHashCode
@Entity(name = "TINSCRIPTION")
public class Inscription {

	// Classe embarquée qui représente un Id composé des 2 clés étrangères
	@Embeddable
	@Data
	public static class Id implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@Pattern(regexp = "[A-Z]{2,8}[0-9]{0,3}-[0-9]-[A-Z]", message = "{elem.code}")
		@Column(name = "FKMODULE", length = 15)
		protected String moduleId;
		
		@Column(name = "FKETUDIANT")
		protected Integer etudiantId;

		public Id() {}

		public Id(String moduleId, Integer etudiantId) {
			this.moduleId = moduleId;
			this.etudiantId = etudiantId;
		}
	}
	
	
	@EmbeddedId
	protected Id id = new Id();

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FKMODULE", insertable = false, updatable = false)
	protected Module module;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FKETUDIANT", insertable = false, updatable = false)
	protected Etudiant etudiant;
	
	@Column(nullable = true, updatable = true)
	private Integer nbAbsence;
	
	public Inscription() {};


	/**
	 * 
	 */
	public Inscription(@NotNull Module module,@NotNull Etudiant etudiant,Integer nbAbsence) {
		super();
		this.module = module;
		this.etudiant = etudiant;
		this.nbAbsence = nbAbsence;
		// initialise les clés étrangères de l’ID composé
		this.id.moduleId = module.getCode();
		this.id.etudiantId = etudiant.getId();
	}
	
}