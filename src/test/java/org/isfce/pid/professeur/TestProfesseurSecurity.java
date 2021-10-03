package org.isfce.pid.professeur;

//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles(profiles = "testU")
@AutoConfigureMockMvc
@Sql(scripts = { "/dataTestU.sql" }// fichier SQL avec les données pour les tests
// permet de préciser d'autres paramètres de configuration
// ,config = @SqlConfig(encoding = "utf-8", transactionMode =
// TransactionMode.ISOLATED)
)
public class TestProfesseurSecurity {
	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser(username = "et1", roles = "ETUDIANT", password = "et1")
	@Transactional
	public void testListeProfesseurAsEtudiant() throws Exception {
		mvc.perform(get("/professeur/liste")).andExpect(status().isOk()).andExpect(view().name("professeur/listeProfesseur"))
				.andExpect(model().attributeExists("professeurList")).andExpect(model().attribute("professeurList", hasSize(2)));
	}

	@Test
	@WithMockUser(username = "et1", roles = "ETUDIANT", password = "et1")
	@Transactional
	public void testGetAddProfesseurAsEtudiant() throws Exception {

		mvc.perform(get("/professeur/1")).andExpect(status().is(403));

		mvc.perform(get("/professeur/add")).andExpect(status().is(403));

		//POST ADD
		mvc.perform(post("/professeur/add").with(csrf())
						.param("nom", "NouveauProf")
						.param("prenom", "Tee")
						.param("email", "tee32@isfce.com")
						.param("user.username", "Tete").param("user.password", "SecretTete1")
						.param("user.confirmPassword", "SecretTete1")
						.param("user.role", "ROLE_PROF"))
						.andExpect(status().is(403));
				
	}

	
	@Test
	@WithMockUser(username = "et1", roles = "ETUDIANT", password = "et1")
	@Transactional
	public void testUpdateProfesseurAsEtudiant() throws Exception {
	
		mvc.perform(get("/professeur/2/update")).andExpect(status().is(403));
		// POST UPDATE
		mvc.perform(post("/professeur/2/update").with(csrf())
					.param("id", "2")
					.param("nom", "DE Henau")
					.param("prenom", "MA")
					.param("email", "dh2@isfce.com")
					.param("user.username", "dh")
					.param("user.password", "SecretDh1")
					.param("user.confirmPassword", "SecretDh1")
					.param("user.role", "ROLE_PROF"))
					.andExpect(status().is(403));
	    
		}

	@Test
	@WithMockUser(username = "et1", roles = "ETUDIANT", password = "et1")
	@Transactional
	public void testDeleteProfesseurAsEtudiant() throws Exception {
   //POST DELETE
		mvc.perform(post("/professeur/2/delete").with(csrf()))
						.andExpect(status().is(403));
	}

	@Test
	@WithMockUser(username = "vo", roles = { "PROF" }, password = "SecretVo1")
	@Transactional
	public void testListeAddDeleteAsProf() throws Exception {
		mvc.perform(get("/professeur/liste")).andExpect(status().isOk());
		mvc.perform(get("/professeur/1")).andExpect(status().isOk());
		mvc.perform(get("/professeur/?username=vo")).andExpect(status().isOk());
		mvc.perform(get("/professeur/999")).andExpect(view().name("error")).andExpect(model().attributeExists("exception"));
		mvc.perform(get("/professeur/?username=trucMahin")).andExpect(view().name("error")).andExpect(model().attributeExists("exception"));
		
		//un prof ne doit pas pouvoir voir le détail d'un autre prof
		mvc.perform(get("/professeur/?username=dh")).andExpect(status().is(403));
		mvc.perform(get("/professeur/2")).andExpect(status().is(403));
		
		//POST GET
		mvc.perform(get("/professeur/add")).andExpect(status().isOk());
		//POST ADD
		mvc.perform(post("/professeur/add").with(csrf()).param("nom", "NouveauProf").param("prenom", "Tee")
				.param("email", "tee32@isfce.com").param("user.username", "Tete").param("user.password", "SecretTete1").param("user.confirmPassword", "SecretTete1").param("user.role", "ROLE_PROF")).andExpect(status().is3xxRedirection());
		//POST DELETE
		mvc.perform(post("/professeur/2/delete").with(csrf())).andExpect(status().is3xxRedirection());
	}


	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN", "PROF" }, password = "admin")
	@Transactional
	public void testUpdateAsAdmin() throws Exception {

		mvc.perform(get("/professeur/1/update")).andExpect(status().isOk());
		// POST UPDATE
		mvc.perform(post("/professeur/1/update").with(csrf()).param("id", "1").param("nom", "Van Oudenhove").param("prenom", "Dide")
				.param("email", "vo2@isfce.com").param("user.username", "vo").param("user.password", "SecretVo1").param("user.confirmPassword", "SecretVo1").param("user.role", "ROLE_PROF")).andExpect(status().is3xxRedirection());
	}
	
    @Test
	@WithMockUser(username = "vo", roles = {"PROF"}, password = "vo")
	@Transactional
	public void testUpdateAsProf() throws Exception {
	//Je dois pouvoir modifier mes données mais pas celles des autres profs
	mvc.perform(get("/professeur/1/update")).andExpect(status().isOk());
	// POST UPDATE
	mvc.perform(post("/professeur/1/update").with(csrf()).param("id", "1").param("nom", "Van Oudenhove").param("prenom", "Dide")
				.param("email", "vo2@isfce.com").param("user.username", "vo").param("user.password", "SecretVo1").param("user.confirmPassword", "SecretVo1").param("user.role", "ROLE_PROF")).andExpect(status().is3xxRedirection());
	
   //Je dois pouvoir modifier mes données mais pas celles des autres profs
	mvc.perform(get("/professeur/2/update")).andExpect(status().is(403));
	// POST UPDATE
	mvc.perform(post("/professeur/2/update").with(csrf()).param("id", "2").param("nom", "DE Henau").param("prenom", "MA")
				.param("email", "dh2@isfce.com").param("user.username", "dh").param("user.password", "SecretDh1").param("user.confirmPassword", "SecretDh1").param("user.role", "ROLE_PROF")).andExpect(status().is(403));
    }   
   
   @Test
   @Transactional
   @WithAnonymousUser
     void testCheckIDNonLogger() throws Exception {
    	//id existant
    	 mvc.perform(get("/check/vo"))
    	 .andExpect(status().is(302));	 
     }
}