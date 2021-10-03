package org.isfce.pid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
//Permet de d�finir les droits sur les appels de m�thode 
@EnableGlobalMethodSecurity(prePostEnabled = true )
public class ProjetWeb2021Application {

	public static void main(String[] args) {
		SpringApplication.run(ProjetWeb2021Application.class, args);
	}

}
