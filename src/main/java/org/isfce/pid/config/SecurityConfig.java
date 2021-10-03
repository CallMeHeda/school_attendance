package org.isfce.pid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	// Encodeur pour les passwords lors du login
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
//    @Autowired
//	private PasswordEncoder pw;
	
    @Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
	}
    
	@Override
	public void configure(WebSecurity web) {
	//URL pour lesquels il n'y a pas de s�curit�
	web.ignoring().antMatchers("/h2/**")
	   .antMatchers("/images/**","/webjars/**","/js/**","/css/**");
	}

	// A red�finir pour configurer la mani�re dont les requ�tes doivent �tre
	// s�curis�es
	// hasAnyRole==> !! PROF ==> ROLE_PROF (ROLE_ est rajout� automatiquement)
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.formLogin().loginPage("/login").defaultSuccessUrl("/", false).
		and().logout().logoutSuccessUrl("/login");
		
		http.authorizeRequests() 
		 //.regexMatchers("(/cours/)*^(/cours/add)").authenticated()
		 .antMatchers("/cours/liste","/professeur/liste").authenticated()
		 .antMatchers("/cours/add","/cours/*/update","/cours/*/delete").hasAnyRole("PROF","ADMIN")
		 .antMatchers("/cours/*").authenticated()
		 .antMatchers("/professeur/*/update","/professeur/*/delete").hasAnyRole("PROF","ADMIN")
		 .antMatchers("/professeur/*").hasAnyRole("PROF","ADMIN")
		 .antMatchers("/check/*").fullyAuthenticated()
		 .antMatchers("/user/*/update").fullyAuthenticated()
		 ///////////////////////////////////////////////// SECURITY ////////////////////////////////////////////////////////
		 // MODULE
		 .antMatchers("/module/*/add","/module/*/update","/module/*/delete").hasAnyRole("PROF","ADMIN")
		 .antMatchers("/module/*/liste","/module/liste").authenticated()
		 // PRESENCE
		 .antMatchers("/presence/*/*/presence","/presence/*/*/update").hasAnyRole("PROF","ADMIN")
		 .antMatchers("/presence/*").authenticated()
		 // PRESENCE VUE ETUDIANT
		 .antMatchers("/presence/*/etudiant/*").authenticated()
		 .antMatchers("/presence/*").authenticated()
		 // SEANCE
//		 .antMatchers("/seance/*").hasAnyRole("PROF","ADMIN")
		 .antMatchers("/seance/*").authenticated()
		 // INSCRIPTION A UN MODULE
		 .antMatchers("/inscription/*/students").hasAnyRole("SECRETARIAT","ADMIN")
		 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		 .antMatchers("/").permitAll();	
	}

}
