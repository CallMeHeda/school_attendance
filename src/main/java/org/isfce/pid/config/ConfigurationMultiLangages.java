package org.isfce.pid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Configuration pour d�finir la sp�cification de la locale
 * en fonction du param�tre "lang" et d'un coockie "maLocale"
 * 
 * @author Didier
 *
 */
@Configuration
public class ConfigurationMultiLangages implements WebMvcConfigurer {

    
// Utilise un cookie pour m�moriser la locale 
    @Bean
    public LocaleResolver localeResolver() {
    	CookieLocaleResolver clr= new CookieLocaleResolver();
    	clr.setCookieName("maLocale");
        return clr;
    }
    
    /**
     * Cr�e un LCI qui rajoute un param�tre lang aux URL pour changer la locale
     * @return
     */
    @Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
	    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
	    lci.setParamName("lang");
	    return lci;
	}
    
	/**
	 * Introduit le LCI 
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(localeChangeInterceptor());
	}
}
