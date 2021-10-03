package org.isfce.pid.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author E. Fatima
 *
 */
@Data
@EqualsAndHashCode
@Entity(name = "TPRESENCE")
public class Presence {

	// Classe embarquée qui représente un Id composé des 2 clés étrangères
	@Embeddable
	@Data
	public static class Id implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@Column(name = "FKSEANCE")
		protected Integer seance_id;
		
		@Column(name = "FKETUDIANT")
		protected Integer etudiant_id;

		public Id() {}

		public Id(Integer seance_id, Integer etudiant_id) {
			this.seance_id = seance_id;
			this.etudiant_id = etudiant_id;
		}
	}

	public static enum TYPE_PRESENCE{
		P, C, CM, AJ, A;
	}
	
	@EmbeddedId
	protected Id id = new Id();
	
	@Column(nullable = true, updatable = true,columnDefinition = "enum('A')")
	@Enumerated(EnumType.STRING)
	private TYPE_PRESENCE typePresence;

	@ManyToOne
	@JoinColumn(name = "FKSEANCE", insertable = false, updatable = false)
	protected Seance seance;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FKETUDIANT", insertable = false, updatable = false)
	protected Etudiant etudiant;
	
	@Column(columnDefinition = "boolean default false")
	private boolean leftBefore = false;
	
	public Presence() {};


	/**
	 * 
	 * @param seance
	 * @param etudiant
	 * @param typeAbsence
	 * @param present
	 * @param dateDCM
	 * @param dateFCM
	 */
	public Presence(@NotNull Seance seance,@NotNull Etudiant etudiant) {
		super();
		this.seance = seance;
		this.etudiant = etudiant;
		// initialise les clés étrangères de l’ID composé
		this.id.seance_id = seance.getId();
		this.id.etudiant_id = etudiant.getId();
	}
	
}
