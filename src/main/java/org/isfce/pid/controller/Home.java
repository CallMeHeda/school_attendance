package org.isfce.pid.controller;

import org.isfce.pid.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@RequestMapping("/")
public class Home {
    @Autowired
	private UserServices userService;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("prof", "VO");
		model.addAttribute("cours","PID");
		return "home";
	}
	
	/**
	 * 
	 * Permet de vérifier si un username existe déjà actuellement en mode Get, on
	 * peut la transformer en Post pour éviter que n'importe qui teste l'existance
	 * des usernames
	 * 
	 * @param username
	 * @return true ou false
	 */
	@GetMapping("/check/{username}")
	public @ResponseBody boolean isValid(@PathVariable("username") String username) {
		return userService.existsById(username);
	}

}
