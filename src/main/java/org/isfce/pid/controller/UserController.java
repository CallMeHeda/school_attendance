package org.isfce.pid.controller;

import java.util.List;

import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;

import org.isfce.pid.model.Roles;
import org.isfce.pid.model.User;
import org.isfce.pid.service.EtudiantServices;
import org.isfce.pid.service.ProfesseurServices;
import org.isfce.pid.service.UserServices;
import org.isfce.pid.service.dto.ChangePasswordDto;
import org.isfce.pid.service.dto.CredentialValidation;
import org.isfce.pid.service.dto.UserDto;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

	private UserServices userSrv;
	PasswordEncoder encodeur;
	ProfesseurServices serviceProfesseur;
	EtudiantServices studService;


	/**
	 * @param userSrv autoinject�
	 */
	public UserController(UserServices userSrv, PasswordEncoder encodeur,ProfesseurServices serviceProfesseur,
			EtudiantServices studService) {
		this.userSrv = userSrv;
		this.encodeur=encodeur;
		this.serviceProfesseur = serviceProfesseur;
		this.studService = studService;
	}

	@GetMapping("/{username}/update")
	public String changePWGet(@PathVariable String username, Authentication auth, Model model) {
		// v�rifie si le user existe
		User user = userSrv.findById(username)
				.orElseThrow(() -> new NotExistException("error.object.notExist", username));
		// v�rifie que c'est bien l'utilisateur connect�
		// V�rifie s'il a les droits admin ou prof logg� sinon d�clenche exception 403
		if (auth == null || (!(auth.getName().equals(user.getUsername()) || hasRole(auth, Roles.ROLE_ADMIN))))
			throw new AccessDeniedException("Access Denied: Failed");

		ChangePasswordDto cpd = ChangePasswordDto.createPwDto(user);
		model.addAttribute("userDto", cpd);
		return "user/userChangePw";
	}

	@PostMapping("/{username}/update")
	public String changePWPost(@PathVariable String username,
			@Validated(CredentialValidation.class) @ModelAttribute("userDto") ChangePasswordDto userCPwDto,
			BindingResult errors, Authentication auth, Model model) {
		// v�rifie si le user existe
		User user = userSrv.findById(username)
				.orElseThrow(() -> new NotExistException("{error.object.notExist}", username));

		// retour � la vue si erreurs
		if (errors.hasErrors()) {
			return "user/userChangePw";
		}
		// change le pw
		try {
			userSrv.changePassword(user, userCPwDto.getOldPassword(), userCPwDto.getPassword());
		} catch (CredentialException e) {
			errors.reject("user.badPassword", "Erreur X");
			return "user/userChangePw";
		}

		return "redirect:/";
	}
	
//	INSCRIPTION NOUVEL UTIILISATEUR
	@GetMapping("/signup")
    public String signUpGet(@ModelAttribute UserDto userDto,Model model) {
		
		model.addAttribute("user", new UserDto());

		// TEMPLATE FORM LOGIN + SIGNUP
        return "login";
    }
	
	// POST NOUVEL UTILISATEUR
	@PostMapping("/signup")
	public String signUpPost(@Validated({CredentialValidation.class,Default.class}) @ModelAttribute UserDto userDto,
			Model model, BindingResult errors,HttpServletRequest request) {

		log.info("Inscription d'un nouvel utilisateur : " + userDto);
		
		// CHECK DOUBLON USERNAME
		if (userSrv.existsById(userDto.getUsername()))
			errors.rejectValue("user.username", "user.username.doubon", "Existe déjà !");

		// ERREURS
		if (errors.hasErrors()) {
			log.debug("Erreurs dans les données : " + userDto.getUsername());
			return "login";
		}

		// CONVERSION DTO EN USER + CRYPTAGE MDP
		User user = userDto.toUser(encodeur);		
		try{
			user = userSrv.createUser(user);
			// SE CONNECTER DIRECTEMENT APRES INSCRIPTION
	        request.login(userDto.getUsername(), userDto.getPassword());
		}catch (Exception e) {
			log.debug("Erreur Lors de la sauvagarde : ", e.getMessage());
			// problème technique
			errors.reject("error.persistance", "Exception de persistance");
			return "login";
		}
		
		if(userDto.getRole()==Roles.ROLE_PROF) {
			// REDIRECT VERS FORM PROF
			return "redirect:/professeur/" + userDto.getUsername() + "/add";
		}else if(userDto.getRole()==Roles.ROLE_ETUDIANT){
			// EDIRECT VERS FORM ETUDIANT
			return "redirect:/etudiant/" + userDto.getUsername() + "/add";
		}else {
			// ADMIN OU SECRETAIRE -> REDIRECT VERS HOME
			return "redirect:/";
		}
	}

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
	
	@ModelAttribute("listeDesRoles")
	List<String> getRole() {
		return List.of("ROLE_ADMIN", "ROLE_PROF", "ROLE_ETUDIANT", "ROLE_SECRETARIAT");
	}
}
