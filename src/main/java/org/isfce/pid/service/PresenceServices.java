package org.isfce.pid.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.isfce.pid.dao.IEtudiantJpaDao;
import org.isfce.pid.dao.IInscriptionJpaDao;
import org.isfce.pid.dao.IModuleJpaDao;
import org.isfce.pid.dao.IPresenceJpaDao;
import org.isfce.pid.dao.ISeanceJpaDao;
import org.isfce.pid.model.Cours;
import org.isfce.pid.model.Etudiant;
import org.isfce.pid.model.Module;
import org.isfce.pid.model.Presence;
import org.isfce.pid.model.Presence.Id;
import org.isfce.pid.model.Presence.TYPE_PRESENCE;
import org.isfce.pid.model.Seance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author E. Fatima
 *
 */
@Slf4j
@Service
@Transactional
public class PresenceServices {

	private static IPresenceJpaDao presenceDAO;
	private ISeanceJpaDao seanceDAO;
	private static IEtudiantJpaDao etudiantDAO;
	private static IModuleJpaDao moduleDAO;
	private static IInscriptionJpaDao inscriptionDAO;


	// Création d'un service pour la liste des présences
	@Autowired
	public PresenceServices(IPresenceJpaDao presencedao, ISeanceJpaDao seanceDAO,IEtudiantJpaDao etudiantDAO,IModuleJpaDao moduleDAO,
			IInscriptionJpaDao inscriptionDAO) {
		PresenceServices.presenceDAO = presencedao;
		this.seanceDAO = seanceDAO;
		this.etudiantDAO = etudiantDAO;
		PresenceServices.moduleDAO = moduleDAO;
		this.inscriptionDAO = inscriptionDAO;
	}

	/////////////////////////////////////////// PRISE DE PRESENCES //////////////////////////////////////////////////////

	public List<Presence> priseDePresence(String codeModule, Integer id) {
		
		Presence unePresence = new Presence();
		List<Etudiant> studentsListPresence;
		
		// RECUPERATION DES ETUDIANTS INSCRITS A UN MODULE
		studentsListPresence = etudiantDAO.findStudByModule(codeModule);
		
		// Si countByPresence > 0 retourne ce qu'il y a dans la table Presence de la DB
		if((studentsListPresence.size()>0) && presenceDAO.countByPresence(id) == 0) {
			for(Etudiant student : studentsListPresence) {
				// Creation d'un ID Presence pour chaque etudiants inscrits au module
				Presence.Id idPresence = new Presence.Id(id,student.getId());
				
				// AFFECTATIONS
				unePresence.setId(idPresence);
				unePresence.setSeance(seanceDAO.getOne(id));
				unePresence.setEtudiant(student);
				unePresence.setTypePresence(TYPE_PRESENCE.P); //
				unePresence.setLeftBefore(false);
				
				presenceDAO.save(unePresence);
				
				// UPDATE INSCRIPTION EN AJOUTANT LE NOMBRE D'ABSENCE
				inscriptionDAO.updateInscription(calculPourcentage(student.getId(), codeModule), student.getId(), codeModule);
				
				
			}
		}else {
			for (Etudiant student : studentsListPresence) {
				unePresence.setTypePresence(TYPE_PRESENCE.P); //
				inscriptionDAO.updateInscription(calculPourcentage(student.getId(), codeModule), student.getId(), codeModule);
			}
			unePresence.setLeftBefore(false);
			log.debug("DATE SEANCE else : " + seanceDAO.getOne(id).getDateSeance());
		}

		return presenceDAO.findBySeance(id);
	}
	
	//////////////////////////////// PRESENCE D'UN ETUDIANT ///////////////////////////////////////////
		
//	public List<Presence> getByEtudiant(Etudiant etudiant){
//		return presenceDAO.findByEtudiant(etudiant);
//	}

///////////////////////////////////// UPDATE DES PRESENCES (SOURCE : https://www.baeldung.com/thymeleaf-list) /////////////////////////////
	
	public List<Presence> savePresences(List<Presence> presences) {
		
		// STOCK MA LISTE AVEC LES MODIF DANS LA MAP
		Map<Id, Presence> presenceMap = presences.stream().collect(Collectors.toMap(Presence::getId, Function.identity()));

		log.info("SAVE ALL PRESENCES : " + presenceDAO.saveAll(presenceMap.values()));
		
		// SAUVE ET RETOURNE CE QU'IL Y A DANS LA MAP
		return presenceDAO.saveAll(presenceMap.values());
	}
	
	////////////////////////////////// NOMBRE DE PRESENCE PAR UN ETUDIANT ET MODULE /////////////////////////////////////////////////////
		public static int nbPresence(Integer id,String code) {
		
			return presenceDAO.countPresence(id,code);
		}
		
	////////////////////////////////// NOMBRE D'ABSENCE PAR ETUDIANT ET MODULE //////////////////////////////////////////////////////////
		public static int nbAbsence(Integer id,String code) {
			
			return presenceDAO.countAbsence(id,code);
		}
	///////////////////////////////// NBABSENCE + NBPRESENCE (ME SERT DE CONDITION DANS THYMELEAF) //////////////////////////////////////
		public static int totalPA(Integer id,String code){
			
			return nbPresence(id, code)+nbAbsence(id,code);
		}
		
	/////////////////////////////////////////////////// POURCENTAGE ABSENCE ///////////////////////////////
	
	// STATIC POUR POUVOIR APPELER LA METHODE AVEC THYMELEAF
	public static int calculPourcentage(Integer id, String code) { // ID ETUDIANT CODE MODULE
		Cours cours = new Cours();
		Module module = moduleDAO.getOne(code);
		// AFFECTE NOMBRE DE PERIODE DU COURS GRACE AU MODULE
		cours.setNbPeriodes(module.getCours().getNbPeriodes());
		
		int nbAbsence_autorise = 0;
		int pourcent = 0;
		
		if((presenceDAO.getTypePresence(id,code).equals(TYPE_PRESENCE.A)) || (presenceDAO.getTypePresence(id,code).equals(TYPE_PRESENCE.AJ))
				|| (presenceDAO.getTypePresence(id,code).equals(TYPE_PRESENCE.CM))){
			
			// CALCUL NOMBRE D'ABSENCES AUTORISEES POUR UN COURS
			// (((NB PERIODES * 365 JOURS) / 100) / 30) -> 30 = nb d'absences autorisé
			nbAbsence_autorise = nbAbsence_autorise + ((cours.getNbPeriodes() * 365) / 100) / 30;
			
			// POURCENTAGE D'ABSENCE DE L'ETUDIANT POUR UN MODULE
			// POURCENTAGE POUR 1 ABSENCE (EX : 1 ABSENCE D'UN COURS DE 60 PERIODES = 4% -> 7 ABSENCES AUTORISEES)
			// 30 = POURCENTAGE MAX D'ABSENCE AUTORISEE
			if(nbAbsence_autorise > 0) {
				pourcent = pourcent + (30 / nbAbsence_autorise) * (nbAbsence(id, code));
			}else {
				pourcent = 30;
			}			
		}
		
		return pourcent;
	}
	
	////////////////////////////////////////////////// NB LEFT BEFORE END SEANCE ///////////////////////////////////////////////
	public Integer getCountLeftBefore(String code,Integer id) {
		return presenceDAO.countLeftBefore(code, id);
	}
}