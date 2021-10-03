package org.isfce.pid.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.isfce.pid.model.Etudiant;
import org.isfce.pid.model.Inscription;
import org.isfce.pid.model.Module;
import org.isfce.pid.model.Roles;
import org.isfce.pid.service.EtudiantServices;
import org.isfce.pid.service.InscriptionServices;
import org.isfce.pid.service.ModuleServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author E. Fatima
 *
 */
@Slf4j
@Controller
@RequestMapping("/inscription")
public class InscriptionController {
	
	InscriptionServices serviceInscription;
	ModuleServices serviceModule;
	EtudiantServices serviceEtudiant;
	
	/**
	 * Construction avec autowired des services et de l'encodeur
	 * 
	 * @param serviceInscription
	 */
	@Autowired
	public InscriptionController(InscriptionServices serviceInscription,ModuleServices serviceModule,
			EtudiantServices serviceEtudiant) {
		this.serviceInscription = serviceInscription;
		this.serviceModule = serviceModule;
		this.serviceEtudiant= serviceEtudiant;
	}
	/**
	 * LISTE DES ETUDIANTS PAS INSCRITS AU MODULE
	 * 
	 * @param model
	 * @param code
	 * @return liste des etudiants non inscrits au module
	 */
	@GetMapping("/{code}/students")
	public String listStudGet(Model model, @PathVariable String code) {
			Optional<Module> module = serviceModule.findOne(code);
			
			model.addAttribute("inscriptions", serviceEtudiant.findStudNotRegisterToModule(code));
			model.addAttribute("module",module.get());
			model.addAttribute("localDate", LocalDate.now());
			
		return "inscription/listStudentInscription";
	}
	
	/**
	 * INSCRIPTION ETUDIANT A UN MODULE
	 * 
	 * @param id
	 * @param code
	 * @param auth
	 * @return
	 */
	@PostMapping("/{code}/{id}/register") // ID DE L'ETUDIANT et CODE MODULE
	public String registerStudent(@PathVariable Integer id,@PathVariable String code,Authentication auth) {
		
		Optional<Module> module = serviceModule.findOne(code);
		Optional<Etudiant> student = serviceEtudiant.findOne(id);
	 	
		// VERIFIE S'IL A LE BON ROLE POUR POUVOIR INSCRIRE UN ETUDIANT A UN MODULE (OK SI SECRETARIAT OU ADMIN)
		if (auth == null || (hasRole(auth, Roles.ROLE_PROF) || (hasRole(auth, Roles.ROLE_ETUDIANT))))
			throw new AccessDeniedException("Access Denied: Failed");

		// PAS D'INSCRIPTION SI LE MODULE A COMMENCE
		if(module.get().getDateDebut().isAfter(LocalDate.now())) {
			log.info("NOPE");
			// SI L'ETUDIANT EXISTE
			if (serviceEtudiant.existsById(id)) {
				Inscription uneInscription = new Inscription();
				Inscription.Id idInscription = new Inscription.Id(code,id);
			
				uneInscription.setId(idInscription);
				uneInscription.setEtudiant(student.get());
				uneInscription.setModule(module.get());
				uneInscription.setNbAbsence(0);
			
				serviceInscription.insert(uneInscription);

			}else {
				throw new NotExistException("error.object.notExist", id.toString());
			}
		}else {
			throw new AccessDeniedException("Access Denied: Failed");
		}		
		
		log.debug("AJOUT d'un etudiant AU module : " + code);
		return "redirect:/inscription/" + code + "/students";
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
}
