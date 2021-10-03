package org.isfce.pid.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.isfce.pid.dao.IModuleJpaDao;
import org.isfce.pid.dao.ISeanceJpaDao;
import org.isfce.pid.model.Module;
import org.isfce.pid.model.Module.MAS;
import org.isfce.pid.model.Seance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 
 * @author E. Fatima
 *
 */
@Service
public class SeanceServices {

	private ISeanceJpaDao seancedao;
//	private IPresenceJpaDao presenceDAO;
	private IModuleJpaDao moduledao;
//	private IEtudiantJpaDao etudiantDAO;

	// Cr�ation d'un service pour la liste des seances
	@Autowired
	public SeanceServices(ISeanceJpaDao seancedao,IModuleJpaDao moduledao) {
		this.seancedao = seancedao;
		this.moduledao = moduledao;
//		this.presenceDAO = presenceDAO;
//		this.etudiantDAO = etudiantDAO;
	}

	/**
	 * retourne la liste de toutes les seances
	 * 
	 * @return colelction de seance
	 */
	public Collection<Seance> findAll() {
		return seancedao.findAll();
	}

	/**
	 * retourne une seance à partir de son id
	 * 
	 * @param id
	 * @return un Optional de seance
	 */
	public Optional<Seance> findOne(Integer id) {
		return seancedao.findById(id);
	}

	/**
	 * Ajoute une seance
	 * 
	 * @param seance
	 */
	public void add(Seance seance) {
		seancedao.save(seance);
	}

	/**
	 * Verifie si une seance existe
	 * 
	 * @param id
	 * @return la seance
	 */
	public boolean existsById(Integer id) {
		return seancedao.existsById(id);

	}

	/**
	 * Sauvegarde la seance
	 * 
	 * @param seance
	 * @return
	 */
	public Seance insert(Seance seance) {
		return seancedao.save(seance);
	}

	/**
	 * Supprime une seance
	 * 
	 * @param seance
	 */
	public void delete(Seance seance) {
		seancedao.delete(seance);
	}

	/**
	 * RECUPERE UNE SEANCE A PARTIR DE L'ID
	 * 
	 * @param id
	 * @return
	 */
	public Optional<Seance> getById(Integer id) {
		return seancedao.findById(id);
	}

	/**
	 * SUPPRIME UNE SEANCE A PARTIR DE SON ID
	 * 
	 * @param id
	 */
	public void deleteById(Integer id) {
		seancedao.deleteById(id);
	}

	/**
	 * UPDATE UNE SEANCE
	 * 
	 * @param seance
	 * @return
	 */
	public Seance update(Seance seance) {
		return seancedao.save(seance);
	}
	/**
	 * METHODE POUR CREER ET AFFICHER LES SEANCES D'UN MODULE
	 * 
	 * @param module
	 * @return seances du module
	 */
	public List<Seance> getByModule(Module module) {
		
//		Duration duration = Duration.of(7, ChronoUnit.DAYS);		
//		LocalDate startDate = module.getDateDebut();	
//		LocalDate endDate = module.getDateFin();
		
		// STOCK DATE DEBUT DU MODULE
		LocalDate sessionDay = module.getDateDebut(); // DATE DEBUT MODULE = PREMIERE SEANCE
//		LocalDate sessionDay = startDate.plus(duration); // 2020-08-01
				
		// S'IL 0 SEANCE POUR LE MODULE ALORS CREE JUSQU'A FIN DATE MODULE
		if(seancedao.countByModule(module.getCode())==0){
			while(sessionDay.isBefore(module.getDateFin()) || (sessionDay.equals(module.getDateFin()))){
				
				Seance uneSeance = new Seance();
				
				uneSeance.setDateSeance(sessionDay);
				
				if(module.getMoment().equals(MAS.MATIN)) {
					LocalTime startTime = LocalTime.of(9,0,0);
					LocalTime endTime = LocalTime.of(13,05,0);
					uneSeance.setHeureDebut(startTime);
					uneSeance.setHeureFin(endTime);
				}else if(module.getMoment().equals(MAS.APM)) {
					LocalTime startTime = LocalTime.of(13,15,0);
					LocalTime endTime = LocalTime.of(17,10,0);
					uneSeance.setHeureDebut(startTime);
					uneSeance.setHeureFin(endTime);
				}else {
					LocalTime startTime = LocalTime.of(18,0,0);
					LocalTime endTime = LocalTime.of(22,0,0);
					uneSeance.setHeureDebut(startTime);
					uneSeance.setHeureFin(endTime);
				}
				
				uneSeance.setModule(moduledao.getOne(module.getCode()));
				seancedao.save(uneSeance);
				
				// SEANCE TOUT LES 7 JOURS
				sessionDay = sessionDay.plusDays(7);
			}
		}
		
		return seancedao.findByModule(module);
	}
}
