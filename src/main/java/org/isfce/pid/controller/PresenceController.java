package org.isfce.pid.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.isfce.pid.model.Etudiant;
import org.isfce.pid.model.Inscription;
import org.isfce.pid.model.Module;
import org.isfce.pid.model.Presence;
import org.isfce.pid.model.Professeur;
import org.isfce.pid.model.Roles;
import org.isfce.pid.model.Seance;
import org.isfce.pid.service.EtudiantServices;
import org.isfce.pid.service.InscriptionServices;
import org.isfce.pid.service.ModuleServices;
import org.isfce.pid.service.PresenceServices;
import org.isfce.pid.service.ProfesseurServices;
import org.isfce.pid.service.SeanceServices;
import org.isfce.pid.service.dto.PresenceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author E. Fatima
 *
 */
@Controller
@Slf4j
@RequestMapping("/presence")
public class PresenceController {

	SeanceServices serviceSeance;
	EtudiantServices serviceEtudiant;
	PresenceServices servicePresence;
	ModuleServices serviceModule;
	ProfesseurServices serviceProf;
	InscriptionServices serviceInscription;

	/**
	 * Construction avec autowired des services et de l'encodeur
	 * 
	 * @param servicePresence
	 */
	@Autowired
	public PresenceController(PresenceServices servicePresence, SeanceServices serviceSeance,
			EtudiantServices serviceEtudiant, ModuleServices serviceModule,InscriptionServices serviceInscription,
			ProfesseurServices serviceProf) {
		this.servicePresence = servicePresence;
		this.serviceSeance = serviceSeance;
		this.serviceEtudiant = serviceEtudiant;
		this.serviceModule = serviceModule;
		this.serviceProf = serviceProf;
		this.serviceInscription = serviceInscription;
	}

	/**
	 * METHODE QUI VERFIE SI L'OBJET A LE BON ROLE
	 * 
	 * @param auth
	 * @param role
	 */
	private boolean hasRole(Authentication auth, Roles role) {
		String roleStr = role.name();
		return auth.getAuthorities().stream().anyMatch(a -> roleStr.equals(a.getAuthority()));
	}
	
	/**
	 * AFFICHE LA LISTE DES ETUDIANT POUR EFFECTUER LES PRESENCES D'UNE SEANCE POUR UN MODULE
	 * 
	 * @param model
	 * @param code
	 * @param id
	 * @return
	 */
	@GetMapping("/{code}/{id}/presence") // Id de la seance, code du module
	public String listStudGet(Model model, @PathVariable Integer id,@PathVariable String code) {

		log.debug("affiche la vue pour ajouter une presence pour la seance " + id + " du module " + code);

			Optional<Seance> seance = serviceSeance.findOne(id);
			Optional<Module> module = serviceModule.findOne(code);
			
			model.addAttribute("presences", servicePresence.priseDePresence(code,id));
			model.addAttribute("seance",seance.get());
			model.addAttribute("module",module.get());
			// CONDITION THYMELEAF
			model.addAttribute("localDate", LocalDate.now());
			model.addAttribute("localTime", LocalTime.now());
			
		return "presence/listStudentPresence";
	}
	
	// GET POUR PRENDRE LES PRESENCES / AFFICHER LA VUE DES PRESENCES A MODIFIER	
	@GetMapping("/{code}/{id}/update")
    public String listPresenceGet(Model model,@PathVariable String code,@PathVariable Integer id, Authentication auth) {
		log.debug("affiche la vue pour modifier les presence pour le module :" + code + " de la seance " + id);
		
		 	List<Presence> presences = new ArrayList<>();
		 	Optional<Module> module = serviceModule.findOne(code);
		 	Optional<Seance> seance = serviceSeance.findOne(id);
		 	
		 	// CREATION PROF (EVITE QUE N'IMPORTE QUEL PROF EFFECTUE LES PRESENCES)
		 	Professeur prof = serviceProf.getById(module.get().getProf().getId())
					.orElseThrow(() -> new NotExistException("error.object.notExist", module.get().getProf().getId().toString()));
		 	
		 // VERIFIE S'IL A LE BON ROLE POUR PRENDRE LES PRESENCES
			if (auth == null || !(prof.getUser().getUsername().equals(auth.getName()) || hasRole(auth, Roles.ROLE_ADMIN)))
				throw new AccessDeniedException("Access Denied: Failed");
		 	
		 	// AJOUTE DANS MA LISTE DE PRESENCES LES PRESENCES STOCKEES DANS MA DB
		    servicePresence.priseDePresence(code, id).iterator().forEachRemaining(presences::add);

		    model.addAttribute("presences", new PresenceDto(presences));
		    model.addAttribute("module", module.get());
		    model.addAttribute("seance", seance.get());
		    
		    log.debug("Presence GET " + presences);
		    
        return "presence/prisePresence";
    }
	
	// POST POUR UPDATE LES PRESENCES
	@PostMapping("/{code}/{id}/update")
	public String updatePresencePost(@ModelAttribute @Valid PresenceDto presences,BindingResult errors,Model model,
			@PathVariable String code,@PathVariable Integer id,Authentication auth) {
		log.info("POST d'une presence " + presences.getPresencesDto());
		
	 		Optional<Module> module = serviceModule.findOne(code);

				// Gestion de la validation
				if (errors.hasErrors()) {
					log.debug("Erreurs dans les données de la presence :" + presences.getPresencesDto());
					return "presence/prisePresence";
				}
				
				// CREATION PROF (EVITE QUE N'IMPORTE QUEL PROF EFFECTUE LES PRESENCES)
			 	Professeur prof = serviceProf.getById(module.get().getProf().getId())
						.orElseThrow(() -> new NotExistException("error.object.notExist", module.get().getProf().getId().toString()));
			 	
			 // VERIFIE S'IL A LE BON ROLE POUR PRENDRE LES PRESENCES
				if (auth == null || !(prof.getUser().getUsername().equals(auth.getName()) || hasRole(auth, Roles.ROLE_ADMIN)))
					throw new AccessDeniedException("Access Denied: Failed");

				// RECUPERATION CODE MODULE + ID SEANCE VIA LE DTO
				code = presences.getPresencesDto().get(0).getSeance().getModule().getCode();
				id =  presences.getPresencesDto().get(0).getSeance().getId();

		model.addAttribute("presences", servicePresence.savePresences(presences.getPresencesDto()));
		
	    // Renvoe vers la liste des étudiants inscrits au module
	    return "redirect:/presence/" + code + "/" + id + "/presence";
	}
	
	// GET DETAIL DE L'ETUDIANT
	@GetMapping("{code}/etudiant/{id}")
    public String etudiantDetailsGET(Model model,@PathVariable Integer id,@PathVariable String code,Authentication auth) {
		log.info("Etudiant n° : " + id);
				
			// Récupère l'étudiant sinon déclenche une exception
			Etudiant stud = serviceEtudiant.getById(id)
					.orElseThrow(() -> new NotExistException("error.object.notExist", id.toString()));
			
			Inscription.Id idInscription = new Inscription.Id(code,stud.getId()); 
			Inscription inscription = serviceInscription.findById(idInscription);
			
			// VERIFIE S'IL A LE BON ROLE POUR ACCEDER A LA VUE DE L'ETUDIANT
			if (auth == null || !(stud.getUser().getUsername().equals(auth.getName()) || hasRole(auth, Roles.ROLE_ADMIN) ||
					hasRole(auth, Roles.ROLE_PROF) || hasRole(auth, Roles.ROLE_SECRETARIAT)))
				throw new AccessDeniedException("Access Denied: Failed");
			
			// COMPTEUR DE PRESENCE (Static car utilisé dans ma vue)
			model.addAttribute("nbPresence", PresenceServices.nbPresence(id, code));
			
			// COMPTEUR D'ABSENCE (Static car utilisé dans ma vue)
			model.addAttribute("nbAbsence", PresenceServices.nbAbsence(id, code));
			
			// NOMBRE D'HEURE DU COURS AUQUEL APPARTIENT LE MODULE
			model.addAttribute("nbHeuresCours", serviceModule.countHoursByCours(code));
			
			// NOMBRE D'HEURE PRESTEE PAR ETUDIANT POUR UN MODULE
//			model.addAttribute("heurePrestee", servicePresence.heuresPrestee(id,code));
			
			// QUOTA ABSENCE
			model.addAttribute("poucentageAbsence", inscription.getNbAbsence());
			
			// NOMBRE DE FOIS QUE L'ETUDIANT A QUITTE LE COURS AVANT L'HEURE
			model.addAttribute("leftBefore", servicePresence.getCountLeftBefore(code, id));

			
			model.addAttribute("etudiant", stud);
		
		return "etudiant/etudiant";
	}
	
	// DELETE ETUDIANT DE TPRESENCE ET TINSCRIPTION
	@PostMapping("/{code}/{id}/delete") // ID DE L'ETUDIANT
	public String deleteStudent(@PathVariable Integer id,@PathVariable String code,Authentication auth) {
		
		Optional<Module> module = serviceModule.findOne(code);
		
		// CREATION PROF (EVITE QUE N'IMPORTE QUI NE SUPPRIME UN ETUDIANT)
	 	Professeur prof = serviceProf.getById(module.get().getProf().getId())
				.orElseThrow(() -> new NotExistException("error.object.notExist", module.get().getProf().getId().toString()));
	 	
	 // VERIFIE S'IL A LE BON ROLE POUR POUVOIR SUPPRIMER L'ETUDIANT
		if (auth == null || !(prof.getUser().getUsername().equals(auth.getName()) || hasRole(auth, Roles.ROLE_ADMIN)))
			throw new AccessDeniedException("Access Denied: Failed");
		
		if (serviceEtudiant.existsById(id)) {
			serviceEtudiant.deleteStudentFromPresence(id, code);
		}else {
			throw new NotExistException("error.object.notExist", id.toString());
		}
		
		if (serviceEtudiant.existsById(id)) {
			serviceEtudiant.deleteStudentFromInscription(id, code);
		}else {
			throw new NotExistException("error.object.notExist", id.toString());
		}
		
		log.debug("Supression d'un etudiant du module : " + code);
		return "redirect:/seance/" + code ;
	}
}
