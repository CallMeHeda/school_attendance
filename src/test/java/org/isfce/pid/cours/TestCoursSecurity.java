package org.isfce.pid.cours;

//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles(profiles = "testU")
@AutoConfigureMockMvc

//Fichier SQL avec les données pour les tests
@Sql(scripts = { "/dataTestU.sql" }
/* 	permet de préciser d'autres paramètres de configuration
 	,config = @SqlConfig(encoding = "utf-8", transactionMode =TransactionMode.ISOLATED)
 */
)
public class TestCoursSecurity {
	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser(value = "et1", roles = "ETUDIANT", password = "et1")
	@Transactional
	public void testListeCoursAsEtudiant() throws Exception {
		mvc.perform(get("/cours/liste")).andExpect(status().isOk()).andExpect(view().name("cours/listeCours"))
				.andExpect(model().attributeExists("coursList")).andExpect(model().attribute("coursList", hasSize(5)));
	}

	@Test
	@WithMockUser(value = "et1", roles = "ETUDIANT", password = "et1")
	@Transactional
	public void testGetAddCoursAsEtudiant() throws Exception {

		mvc.perform(get("/cours/IPID")).andExpect(status().isOk());

		mvc.perform(get("/cours/add")).andExpect(status().is4xxClientError());

		// POST ADD
		mvc.perform(post("/cours/add").with(csrf()).param("code", "ITEST").param("nom", "Test")
				.param("nbPeriodes", "60").param("sections", "Informatique")).andExpect(status().is(403));
	}

	@Disabled
	@Test
	@WithMockUser(value = "et1", roles = "ETUDIANT", password = "et1")
	@Transactional
	public void testUpdateCoursAsEtudiant() throws Exception {
		// Get Update
		mvc.perform(get("/cours/ISO2/update")).andExpect(status().is(403));
		// POST UPDATE
		mvc.perform(post("/cours/update").with(csrf()).param("code", "ISO2").param("nom", "Structure des ordinateurs")
				.param("nbPeriodes", "60").param("sections", "Informatique")).andExpect(status().is(403));
	}

	@Test
	@WithMockUser(value = "et1", roles = "ETUDIANT", password = "et1")
	@Transactional
	public void testDeleteCoursAsEtudiant() throws Exception {
   //POST DELETE
		mvc.perform(post("/cours/XTAB/delete").with(csrf())).andExpect(status().is(403));
	}

	@Test
	@WithMockUser(value = "admin", roles = { "ADMIN", "PROF" }, password = "admin")
	@Transactional
	public void testListeAddDeleteAsAdminProf() throws Exception {
		mvc.perform(get("/cours/liste")).andExpect(status().isOk());
		mvc.perform(get("/cours/IGEB")).andExpect(status().isOk());
		mvc.perform(get("/cours/coursInconnu")).andExpect(view().name("error")).andExpect(model().attributeExists("exception"));
		mvc.perform(get("/cours/add")).andExpect(status().isOk());
		// POST ADD
		mvc.perform(post("/cours/add").with(csrf()).param("code", "ITEST").param("nom", "Test")
				.param("nbPeriodes", "60").param("sections", "Informatique")).andExpect(status().is3xxRedirection());
		// POST DELETE
		mvc.perform(post("/cours/ITEST/delete").with(csrf())).andExpect(status().is3xxRedirection());
	}

	@Disabled
	@Test
	@WithMockUser(value = "admin", roles = { "ADMIN", "PROF" }, password = "admin")
	@Transactional
	public void testUpdateAsAdminProf() throws Exception {

		mvc.perform(get("/cours/IGEB/update")).andExpect(status().isOk());
		// POST UPDATE
		mvc.perform(post("/cours/IGEB/update").with(csrf()).param("code", "ITEST").param("nom", "Test2")
				.param("nbPeriodes", "60").param("sections", "Informatique")).andExpect(status().is3xxRedirection());
	}

}
