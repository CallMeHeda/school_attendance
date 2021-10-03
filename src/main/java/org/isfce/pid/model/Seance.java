package org.isfce.pid.model;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author E. Fatima
 *
 */

@Data
@NoArgsConstructor
@Entity(name = "TSEANCE")
public class Seance {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Column(nullable = false)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDate dateSeance;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FKMODULE", nullable = false)
	@NotNull
	private Module module;
	
	
	@Column(nullable = false)
	@DateTimeFormat(pattern = "HH-mm-ss")
	private LocalTime heureDebut;
	
	@Column(nullable = false)
	@DateTimeFormat(pattern = "HH-mm-ss")
	private LocalTime heureFin;

	/**
	 * @param dateSeance
	 * @param module
	 * @param heureDebut
	 * @param heureFin
	 */
	public Seance(@NotNull LocalDate dateSeance,@NotNull Module module,LocalTime heureDebut,LocalTime heureFin) {
		super();
		this.dateSeance = dateSeance;
		this.module = module;
		this.heureDebut = heureDebut;
		this.heureFin = heureFin;
	}

}
