package org.isfce.pid.controller;

import org.isfce.pid.service.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
public class SecurityController {
	
	@GetMapping("/login")
    public String login(@ModelAttribute("user") UserDto user) {
        log.info("Login with SecurityController");
        return "login";
    }
	
	// Login form with error
	  @GetMapping("/login-error")
	  public String loginError(Model model) {  
		  log.warn("Login erreur with SecurityController");
	    model.addAttribute("loginError", true);
	    return "login";
	  }
}
