package org.isfce.pid.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.isfce.pid.model.Module;
import org.isfce.pid.model.Seance;
import org.isfce.pid.service.EtudiantServices;
import org.isfce.pid.service.ModuleServices;
import org.isfce.pid.service.PresenceServices;
import org.isfce.pid.service.SeanceServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author E. Fatima
 *
 */
@Slf4j
@Controller
@RequestMapping("/seance")
public class SeanceController {

	SeanceServices serviceSeance;
	ModuleServices serviceModule;
	PresenceServices servicePresence;
	EtudiantServices serviceEtudiant;

	/**
	 * Construction avec autowired des services et de l'encodeur
	 * 
	 * @param serviceSeance
	 */
	@Autowired
	public SeanceController(SeanceServices serviceSeance, ModuleServices serviceModule,PresenceServices servicePresence,EtudiantServices serviceEtudiant) {
		this.serviceSeance = serviceSeance;
		this.serviceModule = serviceModule;
		this.servicePresence = servicePresence;
		this.serviceEtudiant = serviceEtudiant;
	}

	/**
	 * Liste des seances pour un module
	 * 
	 * @param model
	 * @return vue
	 */
	@GetMapping("/{code}") // code du module
	public String SeanceGet(@ModelAttribute("seance") Seance seance, Model model,@PathVariable String code) {
		
		log.debug("affiche la vue pour la liste des seances du module " + code);
		
		if (!model.containsAttribute("module")) { // recherche la seance dans la liste
			Optional<Module> module = serviceModule.findOne(code);
			Module m = module.get();
			
			// POUR QUE LES SEANCE COMMENCE A 1 A CHAQUE MODULE
			ArrayList<Integer> id = new ArrayList<Integer>();
			int nb = 1;
			for(int i=0; i<serviceSeance.getByModule(m).size(); i++) {			
				id.add(nb);
				nb++;
			}
			
			if (module.isEmpty())
				throw new NotExistException("error.object.notExist", code);
			
			// Ajout au Modele
			model.addAttribute("seanceList",serviceSeance.getByModule(module.get()));
			
			model.addAttribute("module",module.get());
			model.addAttribute("id",id);
		}
		
		return "seance/listSeance";
	}

}
