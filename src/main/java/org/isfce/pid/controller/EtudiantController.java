package org.isfce.pid.controller;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.isfce.pid.model.Etudiant;
import org.isfce.pid.model.Roles;
import org.isfce.pid.model.User;
import org.isfce.pid.model.Module;
import org.isfce.pid.service.EtudiantServices;
import org.isfce.pid.service.ModuleServices;
import org.isfce.pid.service.UserServices;
import org.isfce.pid.service.dto.EtudiantDto;
import org.isfce.pid.service.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fatima
 *
 */
@Slf4j
@Controller
@RequestMapping("/etudiant")
public class EtudiantController {

	PasswordEncoder encodeur;
	EtudiantServices serviceEtudiant;
	UserServices userService;
	ModuleServices moduleService;

	/**
	 * Construction avec autowired des services et de l'encodeur
	 * 
	 * @param serviceEtudiant
	 * @param userService
	 * @param encodeur
	 */
	@Autowired
	public EtudiantController(EtudiantServices serviceEtudiant, UserServices userService,
			PasswordEncoder encodeur,ModuleServices moduleService) {
		this.serviceEtudiant = serviceEtudiant;
		this.userService = userService;
		this.encodeur = encodeur;
		this.moduleService = moduleService;
	}

	/**
	 * Liste des etudians
	 * 
	 * @param model
	 * @return vue
	 */
	@GetMapping("/{code}/studentList") // Code module dans lequel les étudiant seront ajoutés
	public String getEtudiants(@PathVariable String code,Authentication auth,Model model) {
		Optional<Module> module = moduleService.findOne(code);
		// JUSTE POUR SECRETAIRE OU ADMIN
		if((hasRole(auth, Roles.ROLE_SECRETARIAT) || (hasRole(auth, Roles.ROLE_ADMIN)))){
			model.addAttribute("etudiantList", serviceEtudiant.findAll());
			model.addAttribute("module", module.get());
		}
		return "etudiant/listeEtudiant";
	}

	/**
	 * D�tail d'un étudiant à partir d'un parametre username
	 * 
	 * @param username de l'etudiant
	 * @param model
	 * @return vue de l'étudiant
	 */
	@GetMapping("/")
	public String getEtudiant(@RequestParam("username") String username, Model model, Authentication auth) {
		log.info("Username de l'étudiant: " + username);

		if (!model.containsAttribute("etudiant")) {
			// Récuperation de l'étudiant Sinon exception
			Etudiant stud = serviceEtudiant.getByUserName(username)
					.orElseThrow(() -> new NotExistException("error.object.notExist", username));
			// V�rifie s'il a les droits admin ou prof logg� sinon d�clenche exception 403
			if (auth == null
					|| !(auth.getName().equals(stud.getUser().getUsername()) || hasRole(auth, Roles.ROLE_ETUDIANT)))
				throw new AccessDeniedException("Access Denied: Failed");
			model.addAttribute("etudiant", stud);
		}
		return "etudiant/etudiant";
	}

	/**
	 * D�tail d'un étudiant à partir de son id
	 * 
	 * @param id
	 * @param model
	 * @param auth
	 * @return vue de l'�tudiant
	 */
	@GetMapping("/{id}")
	public String getEtudiant(@PathVariable Integer id, Model model, Authentication auth) {
		log.info("Etudiant par id: " + id);

		if (!model.containsAttribute("etudiant")) {
			Etudiant stud = serviceEtudiant.getById(id)
					.orElseThrow(() -> new NotExistException("error.object.notExist", id.toString()));
			// V�rifie les droits
//			if (auth == null
//					|| !(auth.getName().equals(stud.getUser().getUsername()) || hasRole(auth, Roles.ROLE_ETUDIANT)))
//				throw new AccessDeniedException("Access Denied: Failed");

			model.addAttribute("etudiant", stud);
		}
		return "etudiant/etudiant";
	}

	/**
	 * M�thode Get ajout d'un etudiant
	 * 
	 * @param stud
	 * @param model
	 * @return
	 */

	@GetMapping("/{username}/add")
	public String addEtudiantGet(@ModelAttribute("etudiant") EtudiantDto stud,@PathVariable String username, Model model) {
		log.debug("affiche la vue pour ajouter un etudiant ");
		
		User user = userService.getByUsername(username);
		// charge un Dto
				UserDto userDto = UserDto.toUserDto(user);

		model.addAttribute("userDto", userDto);
		
		return "etudiant/addEtudiant";
	}

	/**
	 * Ajout d'un etudiant -> POST
	 * 
	 * @param studDto
	 * @param errors
	 * @return
	 */
	@PostMapping("/add")
	public String addEtudiantPost(@Valid @ModelAttribute(name = "etudiant") EtudiantDto studDto, BindingResult errors,Authentication auth) {
		log.debug("Post d'un Etudiant " + studDto.getNom());
		
		// Verification sur la contrainte unique nom et pr�nom
				if (serviceEtudiant.existsByNomPrenom(studDto.getNom(),studDto.getPrenom()))
					errors.reject("professeur.nom.doublon", "Existe d�j�!");

		// Gestion de la validation avec toutes les erreurs de donn�es possibles
		if (errors.hasErrors()) {
			log.debug("Erreurs dans les donn�es:" + studDto.getId());
			return "etudiant/addEtudiant";
		}

		// Conversion Dto==>Prof et crypte son pw
				User user = userService.getByUsername(auth.getName());
				// charge un Dto
						UserDto userDto = UserDto.toUserDto(user);
						studDto.setUser(userDto);
				Etudiant student = studDto.toEtudiant(encodeur);
			
				try {
					// Sauvegarde
					student = serviceEtudiant.save(student);
				} catch (Exception e) {
					log.debug("Erreur Lors de la sauvagarde: "+ e.getMessage());
					// probl�me technique
					errors.reject("error.persistance", "Exception de persistance");
					return "etudiant/addEtudiant";
				}

		return "redirect:/";
	}

	/**
	 * Get Affiche la vue pour faire un update d'un professeur un dto de prof sera
	 * envoy� avec les passwords effac�s
	 * 
	 * @param id
	 * @param model
	 * @param auth
	 * @return
	 */
	// M�thode Get pour faire un update d'un etudiant
	@GetMapping("/{id}/update")
	public String updateEtudiantGet(@PathVariable Integer id, Model model, Authentication auth) {
		log.debug("affiche la vue pour modifier un etudiant:" + id);

		// Récup' l'étudiant sinon déclenche une exception
		Etudiant stud = serviceEtudiant.getById(id)
				.orElseThrow(() -> new NotExistException("error.object.notExist", id.toString()));

		if (auth == null || !(stud.getUser().getUsername().equals(auth.getName()) || hasRole(auth, Roles.ROLE_PROF)
				|| hasRole(auth, Roles.ROLE_SECRETARIAT)))
			throw new AccessDeniedException("Access Denied: Failed");

		// charge un Dto
		EtudiantDto studDto = EtudiantDto.toDto(stud);

		// Pw bidon efface les password (crypt�s)
		studDto.getUser().setPassword("PasswordOk");
		studDto.getUser().setConfirmPassword("PasswordOk");

		// Ajout au Mod�le
		model.addAttribute("etudiant", studDto);

		return "etudiant/updateEtudiant";
	}

	/**
	 * Post r�ception de l'update d'un prof � partir d'un dto Seules les donn�es du
	 * professeur (sauf id) seront modifiables la validation ne testera pas les
	 * donn�es professeur.user du dto si tout va bien le professeur sera modifi� et
	 * on retourne � la liste des profs
	 * 
	 * @param id
	 * @param studDto sans validation du user
	 * @param errors
	 * @param model
	 * @param rModel
	 * @return
	 */
	@PostMapping("{id}/update")
	public String updateEtudiantPost(@PathVariable Integer id,
			@ModelAttribute("etudiant") @Validated(Default.class) EtudiantDto studDto, BindingResult errors,
			Authentication auth, RedirectAttributes rModel) {

		log.debug("Post update d'un etudiant");
		// Gestion de la validation
		if (errors.hasErrors()) {
			log.debug("Erreurs dans les donn�es:" + studDto.getId());
			return "etudiant/updateEtudiant";
		}

		// R�cup�re l'etudiant et déclenche une exception s'il n'existe pas
		Etudiant stud = serviceEtudiant.getById(id)
				.orElseThrow(() -> new NotExistException("error.object.notExist", id.toString()));

		// V�rifie les droits
		if (auth == null || 
				!(stud.getUser().getUsername().equals(auth.getName()) || hasRole(auth, Roles.ROLE_ADMIN)))
				throw new AccessDeniedException("Access Denied: Failed");

		// si le nom et ou le prenom ont chang�, teste s'il n'existe pas de doublon:
//		if (!stud.getNom().equals(studDto.getNom()) || !stud.getPrenom().equals(studDto.getPrenom()))
//			if (serviceEtudiant.existsByNomPrenom(studDto.getNom(), studDto.getPrenom())) {
//				errors.reject("etudiant.nom.doublon", "Existe d�j�!");
//				return "etudiant/updateEtudiant";
//			}

		// Mise à jour
		stud.setNom(studDto.getNom());
		stud.setPrenom(studDto.getPrenom());
		stud.setEmail(studDto.getEmail());
		stud.setTel(studDto.getTel());

		try {
			stud = serviceEtudiant.save(stud);
		} catch (Exception e) {
			log.debug("Erreur Lors de la sauvagarde: ", e.getMessage());
			errors.reject("error.persistance", "Exception de persistance");
			return "etudiant/updateEtudiant";
		}
		
		rModel.addFlashAttribute("etudiant", stud);
		return "redirect:/etudiant/" + id;
	}

	/**
	 * Supression d'un etudiant
	 * 
	 * @param id
	 * @return mapping de redirection
	 */
	@PostMapping("/{id}/delete")
	public String deleteEtudiant(@PathVariable Integer id) {

		if (this.serviceEtudiant.existsById(id))
			serviceEtudiant.deleteById(id);
		else
			throw new NotExistException("error.object.notExist", id.toString());

		log.debug("Supression de l'etudiant: " + id);
		return "redirect:/etudiant/liste";
	}

	/**
	 *  vérifie si auth posséde le bon role
	 * 
	 * @param auth
	 * @param role
	 * @return vrai s'il possede le role
	 */
	private boolean hasRole(Authentication auth, Roles role) {
		String roleStr = role.name();
		return auth.getAuthorities().stream().anyMatch(a -> roleStr.equals(a.getAuthority()));
	}

}