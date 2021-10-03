package org.isfce.pid.cours;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.isfce.pid.controller.CentralExceptionHandle;
import org.isfce.pid.controller.CoursController;
import org.isfce.pid.controller.NotExistException;
import org.isfce.pid.model.Cours;
import org.isfce.pid.service.CoursServices;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@ActiveProfiles("testU")
public class Test_CoursController {
	//  pour les 4 premiers tests
	static CoursServices mockDAO;
	static Cours coursPID, coursSO2, coursTest;
	static MockMvc mockMvc1;

	/**
	 * Initialisation d'un mock de CoursService, use dans les x premiers tests
	 */
	@BeforeAll
	public static void initMockDAO() {
		log.debug("Création des données de test");
		coursPID = new Cours("IPID", "Projet de developpement", (short) 60);
		coursPID.setSections(Set.of("Informatique"));
		coursSO2 = new Cours("ISO2", "Structure des ordinateurs", (short) 60);
		coursSO2.setSections(Set.of("Informatique"));
		coursTest = new Cours("ITEST", "TEST", (short) 60);
		coursTest.setSections(Set.of("Informatique"));
		//Mock de la couche de service
		CoursServices mockDAO = mock(CoursServices.class);
		when(mockDAO.exists("IPID")).thenReturn(true);
		when(mockDAO.exists("ISO2")).thenReturn(true);
		when(mockDAO.findAll()).thenReturn(Arrays.asList(coursPID, coursSO2));
		when(mockDAO.findOne("IPID")).thenReturn(Optional.of(coursPID));
		when(mockDAO.findOne("ISO2")).thenReturn(Optional.of(coursSO2));
		// Cours qui n'existe pas encore
		when(mockDAO.exists("ITEST")).thenReturn(false);
		when(mockDAO.testNomDoublon(coursTest)).thenReturn(false);
		when(mockDAO.insert(coursTest)).thenReturn(coursTest);
		//Création d'un contrôleur avec un mock du serviceDao
		CoursController controller = new CoursController(mockDAO);
		mockMvc1=standaloneSetup(controller).build();
	}

	// Verifie pour chaque methode du controleur: CoursController
	// la presence du bon nom logique de la vue
	// la presence des bons attributs dans la map ou la non presence
	@Test
	public void testListeCours() throws Exception {
		mockMvc1.perform(get("/cours/liste")).andExpect(view().name("cours/listeCours"))
				.andExpect(model().attributeExists("coursList"));
	}

	@Test
	public void testAddCoursGet() throws Exception {
		mockMvc1.perform(get("/cours/add")).andExpect(view().name("cours/addCours"))
				.andExpect(model().attributeExists("cours")).andExpect(model().attributeDoesNotExist("savedID"));

	}

	@Test // Test avec des données valides
	public void testAddCoursPost() throws Exception {
		mockMvc1.perform(post("/cours/add").param("code", "ITEST").param("nom", "Test").param("nbPeriodes", "60").param("sections","Informatique"))
				.andExpect(redirectedUrl("/cours/ITEST")).andExpect(flash().attributeExists("cours"));
	}

	@Test
	public void testDetailCours() throws Exception {
		mockMvc1.perform(get("/cours/IPID")).andExpect(view().name("cours/cours"))
				// .andExpect(model().attribute("cours", hasProperty("code",equalTo("IPID"))))
				.andExpect(model().attribute("cours", coursPID));
	}
	
@Disabled
	@Test // Attention à  partir de la version d'hibernate-validator 5.3,
			// il faut ajouter en plus la dépendance: compile "org.glassfish:javax.el:3.0.0"
	
	public void testUpdateCoursGetPost() throws Exception {
		//Données utilisées pour le test
		Cours oldSaved = new Cours("SO2", "Structure", (short) 40);
		oldSaved.setSections(Set.of("Informatique"));
		Cours newSaved = new Cours("ISO2", "Structure des ordinateurs", (short) 60);
		newSaved.setSections(Set.of("Informatique"));
		//Création d'un mock
		CoursServices mockDAO = mock(CoursServices.class);
		// Définit le comportement du mock pour le scénario
		when(mockDAO.findOne("SO2")).thenReturn(Optional.of(oldSaved));
		when(mockDAO.exists("SO2")).thenReturn(true);
		when(mockDAO.exists("ISO2")).thenReturn(false);
		when(mockDAO.update(newSaved)).thenReturn(newSaved);
		when(mockDAO.insert(newSaved)).thenReturn(newSaved);
		when(mockDAO.testNomDoublon(newSaved)).thenReturn(false);
		when(mockDAO.testNomDoublon(oldSaved)).thenReturn(true);

		// Crée un controleur avec un mock du service
		CoursController controller = new CoursController(mockDAO);

		// Création d'un mockMvc pour simuler les requêtes Web
		MockMvc mockMvc = standaloneSetup(controller).build();

		mockMvc.perform(get("/cours/SO2/update")).andExpect(view().name("cours/addCours"))
				.andExpect(model().attribute("savedId", "SO2"))
				.andExpect(model().attributeExists("cours"));

		// Vérifie la bonne redirection et le flash attribut
		mockMvc.perform(post("/cours/add").param("code", "ISO2").param("nom", "Structure des ordinateurs")
				.param("nbPeriodes", "60").param("sections","Informatique").param("savedId", "SO2"))
				.andExpect(redirectedUrl("/cours/ISO2"))
				.andExpect(flash().attributeExists("cours"));

		// vérifie que les méthodes suivantes ont été appellées sur le mock du service
		verify(mockDAO, atLeastOnce()).insert(newSaved);
		verify(mockDAO, atLeastOnce()).delete(oldSaved.getCode());
	}

	@Test //Teste l'encodage avec des erreurs
	public void testAddCoursBadData() throws Exception {

		Cours saved = new Cours("SO2", "Structure", (short) 40);
		saved.setSections(Set.of("Informatique"));

		CoursServices mockDAO = mock(CoursServices.class);
		when(mockDAO.exists("SO2")).thenReturn(true);
		when(mockDAO.testNomDoublon(saved)).thenReturn(false);
		when(mockDAO.findOne("SO2")).thenReturn(Optional.of(saved));

		CoursController controller = new CoursController(mockDAO);
		MockMvc mockMvc = standaloneSetup(controller).build();

		// Test avec les 4 champs en erreur et retour à  la vue d'encodage
		mockMvc.perform(post("/cours/add").param("code", "I").param("nom", "S").param("nbPeriodes", "-1").param("sections", ""))
				.andExpect(view().name("cours/addCours")).andExpect(model().errorCount(4))
				.andExpect(model().attributeHasFieldErrors("cours", "code", "nom", "nbPeriodes","sections"))
				.andExpect(status().isOk());

		// Test avec les 4 champs vides et le type de message pour chaque champ et
		// retour à la vue d'encodage
		mockMvc.perform(post("/cours/add").param("code", "    ")).andExpect(view().name("cours/addCours"))
				.andExpect((model().attributeHasFieldErrorCode("cours", "nom", "NotBlank")))
				.andExpect((model().attributeHasFieldErrorCode("cours", "code", "Pattern")))
				.andExpect((model().attributeHasFieldErrorCode("cours", "nbPeriodes", "Min")))
				.andExpect((model().attributeHasFieldErrorCode("cours", "sections", "NotEmpty")))
				.andExpect(status().isOk());

	}

	//Test l'accés ou la modification ou suppression d'un cours inconnu
	@Disabled
	@Test
	public void testCoursInconnu() throws Exception {
		// Création d'un mock du service
		CoursServices mockDAO = mock(CoursServices.class);
		when(mockDAO.exists("BROL")).thenReturn(false);
		when(mockDAO.findOne("BROL")).thenReturn(Optional.empty());

		// Création d'un controleur avec une classe advice
		CoursController controller = new CoursController(mockDAO);
		MockMvc mockMvc = standaloneSetup(controller).setControllerAdvice(new CentralExceptionHandle()).build();

		// Vérifie le bon déclenchement et la bonne gestion de l'exception
		// dans les 3 cas
		// Cas1
		mockMvc.perform(get("/cours/BROL/update")).andExpect(status().isOk()).andExpect(view().name("error"))
				.andExpect(model().attributeExists("exception")).andExpect(model().attributeExists("path"))
				.andExpect(mvcResult -> {
					Exception exception = mvcResult.getResolvedException();
					assertThat(exception, is(instanceOf(NotExistException.class)));
				});
		// Cas2
		mockMvc.perform(post("/cours/BROL/delete")).andExpect(status().isOk()).andExpect(view().name("error"))
				.andExpect(model().attributeExists("exception")).andExpect(model().attributeExists("path"))
				.andExpect(mvcResult -> {
					Exception exception = mvcResult.getResolvedException();
					assertThat(exception, is(instanceOf(NotExistException.class)));
				});
		// Cas3
		mockMvc.perform(get("/cours/BROL/")).andExpect(status().isOk()).andExpect(view().name("error"))
				.andExpect(model().attributeExists("exception")).andExpect(model().attributeExists("path"))
				.andExpect(mvcResult -> {
					Exception exception = mvcResult.getResolvedException();
					assertThat(exception, is(instanceOf(NotExistException.class)));
				});
	}

	

	@Test
	public void testDeleteCours() throws Exception {
		CoursServices mockDAO = mock(CoursServices.class);
		Cours saved = new Cours("ISO2", "Structure des ordinateurs", (short) 60);
		saved.setSections(Set.of("Informatique"));
		when(mockDAO.findOne(saved.getCode())).thenReturn(Optional.of(saved));
		when(mockDAO.exists(saved.getCode())).thenReturn(true);
		CoursController controller = new CoursController(mockDAO);

		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(post("/cours/ISO2/delete").param("code", "ISO2"))
		.andExpect(redirectedUrlPattern("/cours/liste?*"));

		verify(mockDAO, atLeastOnce()).delete("ISO2");
	}

}
