package org.isfce.pid.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.isfce.pid.model.Cours;
import org.isfce.pid.model.Etudiant;
import org.isfce.pid.model.Module;
import org.isfce.pid.model.Professeur;
import org.isfce.pid.model.Roles;
import org.isfce.pid.service.CoursServices;
import org.isfce.pid.service.EtudiantServices;
import org.isfce.pid.service.ModuleServices;
import org.isfce.pid.service.ProfesseurServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
/**
 * 
 * @author E. Fatima
 *
 */

@Slf4j
@Controller
@RequestMapping("/module")
public class ModuleController {

	private ModuleServices moduleDAO;
	private CoursServices coursDAO;
	private ProfesseurServices professeurDAO;
	private EtudiantServices etudiantDAO;

	// Liste de données
	@Autowired
	public ModuleController(ModuleServices moduleDAO, ProfesseurServices profDAO, CoursServices coursDAO,EtudiantServices etudiantDAO) {
		this.moduleDAO = moduleDAO;
		this.professeurDAO = profDAO;
		this.coursDAO = coursDAO;
		this.etudiantDAO = etudiantDAO;
	}

	// Liste des modules
	@GetMapping("/liste")
	public String listeModule(Authentication auth,Model model) {
		
			// IF ROLES AUTRE QUE PROF AFFICHE TOUT LES MODULES
			if((hasRole(auth, Roles.ROLE_ADMIN))|| (hasRole(auth, Roles.ROLE_SECRETARIAT))) {
				model.addAttribute("moduleList", moduleDAO.findAll());
				model.addAttribute("etudiant", etudiantDAO.findAll());
			}else if(hasRole(auth, Roles.ROLE_ETUDIANT)){
				Etudiant etudiant = etudiantDAO.getByUserName(auth.getName())
						.orElseThrow(() -> new NotExistException("error.object.notExist", auth.getName()));
				
				model.addAttribute("moduleList",moduleDAO.getModuleByStud(etudiant.getId()));
			}else { // SINON AFFICHE LES MODULES DONNES PAR LE PROF
				Professeur prof = professeurDAO.getByUserName(auth.getName())
						.orElseThrow(() -> new NotExistException("error.object.notExist", auth.getName()));
				
				model.addAttribute("moduleList", moduleDAO.moduleByProf(prof.getId()));
			}
	
		
		return "module/listeModule";
	}

	// Méthode GET pour ajouter un module
	@GetMapping("/{code}/add") // Code du cours auquel se rapporte le module
	public String addModuleGet(@ModelAttribute("module") Module module, Model model,@PathVariable String code) {
		log.debug("affiche la vue pour ajouter un module pour le cours " + code);
		
//		Récupéation des professeurs
		model.addAttribute("listeProfesseurs",professeurDAO.findAll());
		
		//if (!model.containsAttribute("cours")) { // recherche le cours dans la liste
			Optional<Cours> cours = coursDAO.findOne(code);
			log.debug("COURS : " + cours);
						
			if (cours.isEmpty())
				throw new NotExistException("error.object.notExist", code);
			// Ajout au Mod�le
			model.addAttribute("listeCours", cours.get());
		//}
//		model.addAttribute("listeCours",coursDAO.findAll());
		return "module/addModule";
		
		
	}

	@PostMapping("/add")
	public String addModulePost(@Valid Module module, BindingResult errors, RedirectAttributes rModel) {
		log.info("POST d'un module " + module);
		// Gestion de la validation en fonction des annotations
		if (errors.hasErrors()) {
			log.debug("Erreurs dans les données du module: " + module.getCode());
			return "module/addModule";
		}

		// V�rifie que ce module n'existe pas encore
		if (moduleDAO.exists(module.getCode())) {
			errors.rejectValue("code", "module.code.doublon", "DUPLICATE ERROR");
			return "module/addModule";
		}

		moduleDAO.insert(module);

		// Pr�paration des attributs Flash pour survivre � la redirection
		// Ainsi dans l'affichage du d�tail du module on ne devra pas chercher
		// le module dans la BD
		rModel.addFlashAttribute(module);

		log.debug("redirection module/liste:");
		return "redirect:/module/" + module.getCode();
	}

	/**
	 * Supression d'un module
	 * 
	 * @param code du module
	 * @return le mapping de redirection
	 */
	@PostMapping("/{code}/delete")
	public String deleteModule(@PathVariable String code) {
		if (moduleDAO.exists(code))
			moduleDAO.delete(code);
		else
			throw new NotExistException("error.object.notExist", code);
		log.debug("Supression du cours: " + code);
		return "redirect:/module/liste";
	}

	// Affichage du détail du module
	@GetMapping("/{code}")
	public String detailModule(@PathVariable String code, Model model, Locale locale,Authentication auth) {
		log.debug("Recherche le module: " + code);

		if (!model.containsAttribute("module")) { // recherche le module dans la liste
			Optional<Module> module = moduleDAO.findOne(code);
			if (module.isEmpty())
				throw new NotExistException("error.object.notExist", code);
			// Ajout au Mod�le
			model.addAttribute("module", module.get());
			
			// GET L'ETUDIANT INSCRIT AU MODULE POUR AFFICHER SA VUE S'IL A LE ROLE ETUDIANT
			if(hasRole(auth, Roles.ROLE_ETUDIANT)) {
				Optional<Etudiant> etudiant = etudiantDAO.getByUserName(auth.getName());
			
				// VERIFIE S'IL A LE BON ROLE POUR ACCEDER A LA VUE DE L'ETUDIANT
				if ((etudiant.get().getUser().getUsername().equals(auth.getName()))) {
					// AJOUT DE L'ETUDIANT AU MODEL
					model.addAttribute("etudiant", etudiantDAO.getByModuleAndStud(code, etudiant.get().getId()));
				}	
				log.debug("ETUDIANT : " +  etudiantDAO.getByModuleAndStud(code, etudiant.get().getId()));
			}
		}
		return "module/module";
	}
	
	// Afficher les moments possible (Matin / jour / soir)
	@ModelAttribute("listeDesMoments")
	List<String> getMoment() {
		return List.of("MATIN", "APM", "SOIR");
	}
	
	//Affichage de la vue qui d�taille le cours � modifier
		@GetMapping("/{code}/update")
		public String updateModuleGet(@ModelAttribute("module") Module module, Model model,@PathVariable String code) {
			log.debug("affiche la vue pour modifier un module ");
			
//			Récupéation des professeurs
			model.addAttribute("listeProfesseurs",professeurDAO.findAll());
			model.addAttribute("listeCours",coursDAO.findAll());

			
			return "module/updateModule";
		}
		
		// POST 
		@PostMapping("/{code}/update")
		public String updateModulePost(@Valid Module module, BindingResult errors, RedirectAttributes rModel,Model model) {
			log.info("POST d'un module" + module);
			// Gestion de la validation en fonction des annotations
			if (errors.hasErrors()) {
				log.debug("Erreurs dans les donn�es du module:" + module.getCode());
				return "module/updateModule";
			}

			moduleDAO.update(module);

			// Pr�paration des attributs Flash pour survivre � la redirection
			// Ainsi dans l'affichage du d�tail du module on ne devra pas chercher
			// le module dans la BD
			model.addAttribute("listeProfesseurs",professeurDAO.findAll());
			model.addAttribute("listeCours",coursDAO.findOne(module.getCours().getCode()));
			rModel.addFlashAttribute(module);

			log.debug("redirection module/liste:");
			return "redirect:/module/" + module.getCode();
		}
		
		// Liste des modules
		@GetMapping("{code}/liste")
		public String listeModuleByCours(Model model,@PathVariable String code) {

			model.addAttribute("moduleList", moduleDAO.moduleByCours(code));

			return "module/listeModule";
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