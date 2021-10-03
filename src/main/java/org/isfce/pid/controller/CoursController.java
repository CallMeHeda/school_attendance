package org.isfce.pid.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.isfce.pid.model.Cours;
import org.isfce.pid.service.CoursServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/cours")
public class CoursController {
	// Pour traduire des messages au niveau du code Java
	// @Autowired
	// MessageSource message;

	private CoursServices coursDAO;

	// Cr�ation de la liste de donn�es pour le 1er exemple
	@Autowired
	public CoursController(CoursServices coursDAO) {
		this.coursDAO = coursDAO;
	}

	// Liste des cours
	@GetMapping("/liste")
	public String listeCours(Model model) {
		model.addAttribute("coursList", coursDAO.findAll());
		model.addAttribute("ecole", "ISFCE");
		return "cours/listeCours";
	}

	// M�thode Get pour ajouter un cours
	@GetMapping("/add")
	public String addCoursGet(@ModelAttribute("cours") Cours cours) {
		log.debug("affiche la vue pour ajouter un cours ");
		return "cours/addCours";
	}

	@PostMapping("/add")
	public String addCoursPost(@Valid Cours cours, BindingResult errors, RedirectAttributes rModel) {
		log.info("POST d'un cours" + cours);
		// Gestion de la validation en fonction des annotations
		if (errors.hasErrors()) {
			log.debug("Erreurs dans les donn�es du cours:" + cours.getCode());
			return "cours/addCours";
		}

		// V�rifie que ce cours n'existe pas encore
		if (coursDAO.exists(cours.getCode())) {
			errors.rejectValue("code", "cours.code.doublon", "DUPLICATE ERROR");
			return "cours/addCours";
		}

		coursDAO.insert(cours);

		// Pr�paration des attributs Flash pour survivre � la redirection
		// Ainsi dans l'affichage du d�tail du cours on ne devra pas chercher
		// le cours dans la BD
		rModel.addFlashAttribute(cours);

		log.debug("redirection cours/liste:");
		return "redirect:/cours/" + cours.getCode();
	}

	/**
	 * Supression d'un cours
	 * 
	 * @param code du cours
	 * @return le mapping de redirection
	 */
	@PostMapping("/{code}/delete")
	public String deleteCours(@PathVariable String code) {
		if (coursDAO.exists(code))
			coursDAO.delete(code);
		else
			throw new NotExistException("error.object.notExist", code);
		log.debug("Supression du cours: " + code);
		return "redirect:/cours/liste";
	}


	// Affichage du d�tail d'un cours
	@GetMapping("/{code}")
	public String detailCours(@PathVariable String code, Model model, Locale locale) {
		log.debug("Recherche le cours: " + code);

		if (!model.containsAttribute("cours")) { // recherche le cours dans la liste
			Optional<Cours> cours = coursDAO.findOne(code);
			if (cours.isEmpty())
				throw new NotExistException("error.object.notExist", code);
			// Ajout au Mod�le
			model.addAttribute("cours", cours.get());
		}
		return "cours/cours";
	}

// Un attribut "listeDesSections" sera ins�rer dans tous les "model" de ce controleur
	@ModelAttribute("listeDesSections")
	List<String> getSection() {
		return List.of("Comptabilité", "Informatique", "Marketing", "Secrétariat");
	}
	
	//////////////////////TP01///////////////////////////////////////////////////////
	
	//Affichage de la vue qui d�taille le cours � modifier
		@GetMapping("/{code}/update")
		public String updateCoursGet(@ModelAttribute("cours") Cours cours) {
			log.debug("affiche la vue pour ajouter un cours ");
			return "cours/updateCours";
		}
		
		// POST 
		@PostMapping("/{code}/update")
		public String updateCoursPost(@Valid Cours cours, BindingResult errors, RedirectAttributes rModel) {
			log.info("POST d'un cours" + cours);
			// Gestion de la validation en fonction des annotations
			if (errors.hasErrors()) {
				log.debug("Erreurs dans les donn�es du cours:" + cours.getCode());
				return "cours/updateCours";
			}

			coursDAO.update(cours);

			// Pr�paration des attributs Flash pour survivre � la redirection
			// Ainsi dans l'affichage du d�tail du cours on ne devra pas chercher
			// le cours dans la BD
			rModel.addFlashAttribute(cours);

			log.debug("redirection cours/liste:");
			return "redirect:/cours/" + cours.getCode();
		}
}
