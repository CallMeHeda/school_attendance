package org.isfce.pid.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.isfce.pid.model.Professeur;
import org.isfce.pid.model.Roles;
import org.isfce.pid.model.User;
import org.isfce.pid.service.ProfesseurServices;
import org.isfce.pid.service.UserServices;
import org.isfce.pid.service.dto.ProfesseurDto;
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
 * @author Didier
 *
 */
@Slf4j
@Controller
@RequestMapping("/professeur")
public class ProfesseurController {

	PasswordEncoder encodeur;
	ProfesseurServices serviceProfesseurs;
	UserServices userService;

	/**
	 * Construction avec autowired des services et de l'encodeur
	 * 
	 * @param serviceProfesseurs
	 * @param userService
	 * @param encodeur
	 */
	@Autowired
	public ProfesseurController(ProfesseurServices serviceProfesseurs, UserServices userService,
			PasswordEncoder encodeur) {
		this.serviceProfesseurs = serviceProfesseurs;
		this.userService = userService;
		this.encodeur = encodeur;
	}

	/**
	 * Liste des professeurs
	 * 
	 * @param model
	 * @return vue
	 */
	@GetMapping("/liste")
	public String getProfesseurs(Model model) {
		model.addAttribute("professeurList", serviceProfesseurs.findAll());
		return "professeur/listeProfesseur";
	}

	/**
	 * D�tail d'un professeur � partir d'un param�tre username
	 * 
	 * @param username le login de l'utilisateur
	 * @param model
	 * @return la vue du professeur
	 */
	@GetMapping("/")
	public String getProfesseur(@RequestParam("username") String username, Model model, Authentication auth) {
		log.info("Professeur par username: " + username);
		// Verifie si pas dans la Map suite � une redirection avec FlashAttribut
		if (!model.containsAttribute("professeur")) {
			// R�cup�re le professeur sinon d�clenche une exception
			Professeur prof = serviceProfesseurs.getByUserName(username)
					.orElseThrow(() -> new NotExistException("error.object.notExist", username));
			// V�rifie s'il a les droits admin ou prof logg� sinon d�clenche exception 403
			if (auth == null
					|| !(auth.getName().equals(prof.getUser().getUsername()) || hasRole(auth, Roles.ROLE_ADMIN)))
				throw new AccessDeniedException("Access Denied: Failed");
			model.addAttribute("professeur", prof);
		}
		return "professeur/professeur";
	}

	/**
	 * D�tail d'un prof � partir de son id via une 'path' variable
	 * 
	 * @param id
	 * @param model
	 * @param auth
	 * @return la vue du professeur
	 */
	@GetMapping("/{id}")
	public String getProfesseur(@PathVariable Integer id, Model model, Authentication auth) {
		log.info("Professeur par id: " + id);
		// Verifie si pas dans la Map suite � une redirection avec FlashAttribut
		if (!model.containsAttribute("professeur")) { // R�cup�re le professeur sinon d�clenche une exception
			Professeur prof = serviceProfesseurs.getById(id)
					.orElseThrow(() -> new NotExistException("error.object.notExist", id.toString()));
			// V�rifie s'il a les droits admin ou prof logg� sinon d�clenche exception 403
			if (auth == null
					|| !(auth.getName().equals(prof.getUser().getUsername()) || hasRole(auth, Roles.ROLE_ADMIN)))
				throw new AccessDeniedException("Access Denied: Failed");

			model.addAttribute("professeur", prof);
		}
		return "professeur/professeur";
	}
	
	/**
	 * M�thode Get pour ajouter un Professeur
	 * 
	 * @param prof
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/add")
	public String addProfesseurGet(@ModelAttribute("professeur") ProfesseurDto prof,@PathVariable String username,Model model) {
		log.debug("affiche la vue pour ajouter un professeur " + prof);
		
		User user = userService.getByUsername(username);
		//DTO USER A PARTIR DE MON USER
				UserDto userDto = UserDto.toUserDto(user);		
		log.debug("USER : " + userDto);

		model.addAttribute("userDto", userDto);
		
		return "professeur/addProfesseur";
	}

	/**
	 * Post pour Ajout d'un professeur Ici on fait tous les tests d'erreur avant
	 * l'ajout ==> plusieurs acc�s � la bd mais en pr�sence de plusieurs type
	 * d'erreur, on revient � la vue avec toutes celle-ci (�a �vite de revenir
	 * plusieurs fois sur une m�me vue avec des erreurs)
	 * 
	 * @param profDto
	 * @param errors
	 * @return
	 */
	@PostMapping("/add")
	public String addProfesseurPost(@Valid @ModelAttribute(name = "professeur") ProfesseurDto profDto,
			BindingResult errors,Authentication auth) {

		// Verification sur la contrainte unique nom et pr�nom
		if (serviceProfesseurs.existsByNomPrenom(profDto.getNom(), profDto.getPrenom()))
			errors.reject("professeur.nom.doublon", "Existe d�j�!");

		// Gestion de la validation avec toutes les erreurs de donn�es possibles
		if (errors.hasErrors()) {
			log.debug("Erreurs dans les donn�es:" + profDto.getId());
			return "professeur/addProfesseur";
		}
		
		User user = userService.getByUsername(auth.getName());
		// USER TO USERDTO
		UserDto userDto = UserDto.toUserDto(user);
		profDto.setUser(userDto);
		log.debug(" Post Professeur " + profDto);
		// Conversion Dto==>Prof et crypte son pw
		Professeur prof = profDto.toProfesseur(encodeur);
		
		log.debug("PROF" + prof);
		log.debug("USERDTO" + userDto);
	
		try {
			// Sauvegarde
			prof = serviceProfesseurs.save(prof);
		} catch (Exception e) {
			log.debug("Erreur Lors de la sauvagarde: "+ e.getMessage());
			// probl�me technique
			errors.reject("error.persistance", "Exception de persistance");
			return "professeur/addProfesseur";
		}
		
		return "redirect:/professeur/liste";
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
	// M�thode Get pour faire un update d'un Professeur
	@GetMapping("/{id}/update")
	public String updateProfesseurGet(@PathVariable Integer id, Model model, Authentication auth) {
		log.debug("affiche la vue pour modifier un prof:" + id);

		// R�cup�re le professeur sinon d�clenche une exception
		Professeur prof = serviceProfesseurs.getById(id)
				.orElseThrow(() -> new NotExistException("error.object.notExist", id.toString()));

		// V�rifie les droits
		// As-t-on le droit de modifier ce professeur?
		if (auth == null || !(prof.getUser().getUsername().equals(auth.getName()) || hasRole(auth, Roles.ROLE_ADMIN)))
			throw new AccessDeniedException("Access Denied: Failed");

		// charge un Dto
		ProfesseurDto profDto = ProfesseurDto.toDto(prof);

		// Pw bidon efface les password (crypt�s)
		profDto.getUser().setPassword("okPassword1");
		profDto.getUser().setConfirmPassword("okPassword1");

		// Ajout au Mod�le
		model.addAttribute("professeur", profDto);

		return "professeur/updateProfesseur";
	}

	/**
	 * Post r�ception de l'update d'un prof � partir d'un dto Seules les donn�es du
	 * professeur (sauf id) seront modifiables la validation ne testera pas les
	 * donn�es professeur.user du dto si tout va bien le professeur sera modifi� et
	 * on retourne � la liste des profs
	 * 
	 * @param id      du prof
	 * @param profDto le dto du prof mais sans valider le user
	 * @param errors  les erreurs mais pas celles du user
	 * @param model
	 * @param rModel  mod�le pour une redirection
	 * @return
	 */
	@PostMapping("{id}/update")
	public String updateCoursPost(@PathVariable Integer id,
			@ModelAttribute("professeur") @Validated(Default.class) ProfesseurDto profDto, BindingResult errors,
			Authentication auth, RedirectAttributes rModel) {

		log.debug("Post update Professeur");
		// Gestion de la validation
		if (errors.hasErrors()) {
			log.debug("Erreurs dans les donn�es:" + profDto.getId());
			return "professeur/updateProfesseur";
		}

		// R�cup�re le professeur et d�clenche une exception s'il n'existe pas
		Professeur prof = serviceProfesseurs.getById(id)
				.orElseThrow(() -> new NotExistException("error.object.notExist", id.toString()));

		// V�rifie les droits
		// As-t-on le droit de modifier ce professeur?
		if (auth == null || 
				!(prof.getUser().getUsername().equals(auth.getName()) || hasRole(auth, Roles.ROLE_ADMIN)))
				throw new AccessDeniedException("Access Denied: Failed");

		// si le nom et ou le prenom ont chang�, teste s'il n'existe pas de doublon:
		if (!prof.getNom().equals(profDto.getNom()) || !prof.getPrenom().equals(profDto.getPrenom()))
			if (serviceProfesseurs.existsByNomPrenom(profDto.getNom(), profDto.getPrenom())) {
				errors.reject("professeur.nom.doublon", "Existe d�j�!");
				return "professeur/updateProfesseur";
			}

		// Maj du professeur actuel
		prof.setEmail(profDto.getEmail());
		prof.setNom(profDto.getNom());
		prof.setPrenom(profDto.getPrenom());

		try {
			prof = serviceProfesseurs.save(prof);
		} catch (Exception e) {
			log.debug("Erreur Lors de la sauvagarde: ", e.getMessage());
			// probl�me technique
			errors.reject("error.persistance", "Exception de persistance");
			return "professeur/updateProfesseur";
		}

		// Pr�paration des attributs Flash pour survivre � la redirection
		// Ainsi dans l'affichage du d�tail du prof on ne devra pas chercher
		// le prof dans la BD
		rModel.addFlashAttribute("professeur", prof);
		return "redirect:/professeur/" + id;
	}

	/**
	 * Supression d'un professeur
	 * 
	 * @param id du professeur
	 * @return le mapping de redirection
	 */
	@PostMapping("/{id}/delete")
	public String deleteProfesseur(@PathVariable Integer id) {

		if (this.serviceProfesseurs.existsById(id))
			serviceProfesseurs.deleteById(id);
		else
			throw new NotExistException("error.object.notExist", id.toString());

		log.debug("Supression du professeur: " + id);
		return "redirect:/professeur/liste";
	}

	// Permet de rajouter la liste des roles � chaque Model
	// @ModelAttribute("rolesAll")
	// private List<Roles> getRoles() {
	// return List.of(Roles.values());
	// }

	/**
	 * Petite m�thode priv�e qui v�rifie si l'objet auth poss�de le role d�sign�
	 * 
	 * @param auth
	 * @param role
	 * @return vrai s'il poss�de le role
	 */
	private boolean hasRole(Authentication auth, Roles role) {
		String roleStr = role.name();
		return auth.getAuthorities().stream().anyMatch(a -> roleStr.equals(a.getAuthority()));
	}

}
