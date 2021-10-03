package org.isfce.pid.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.security.auth.login.CredentialException;

import org.isfce.pid.dao.IUsersJpaDao;
import org.isfce.pid.model.Roles;
import org.isfce.pid.model.User;
import org.isfce.pid.service.UserServices;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("testU")
@Sql({ "/dataTestU.sql" })
@AutoConfigureMockMvc
class testUser {
	@Autowired
	MockMvc mvc;

	@Autowired
	IUsersJpaDao userDao;

	@Autowired
	UserServices userSrv;

	@Autowired
	PasswordEncoder encodeur;

	@ParameterizedTest
	@CsvSource(value = { "admin,ADMIN", "vo,PROF" })
	@Transactional
	public void modifiePwAsAdmin(String username, String role) throws Exception {
		// Get
		mvc.perform(get("/user/vo/update").with(user(username).roles(role))).andExpect(status().isOk())
				.andExpect(model().attributeExists("userDto")).andExpect(view().name("user/userChangePw"));

		mvc.perform(get("/user/inconnu/update").with(user(username).roles(role))).andExpect(status().isOk())
				.andExpect(model().attributeExists("exception")).andExpect(view().name("error"));

		// POST
		mvc.perform(post("/user/vo/update").with(user(username).roles(role)).with(csrf()).param("username", "vo")
				.param("oldPassword", "vo").param("password", "SecretVO1").param("confirmPassword", "SecretVO1")
				.param("role", "ROLE_PROF")).andExpect(status().is3xxRedirection());
		// vérifie dans BD
		User user = userDao.findById("vo").get();
		assertTrue(encodeur.matches("SecretVO1", user.getPassword()));

	}

	@ParameterizedTest
	@CsvSource(value = { "dh,PROF", "et1,ETUDIANT" })
	@Transactional
	public void modifiePwAsBadUser(String username, String role) throws Exception {
		// Get
		mvc.perform(get("/user/vo/update").with(user(username).roles(role))).andExpect(status().is(403));

		// POST
		mvc.perform(post("/user/vo/update").with(user(username).roles(role)).with(csrf()).param("username", "vo")
				.param("oldPassword", "vo").param("password", "SecretVO1").param("confirmPassword", "SecretVO1")
				.param("role", "ROLE_PROF")).andExpect(status().is(403));

	}

	@ParameterizedTest
	@CsvSource(value = { "vo,PROF" })
	@Transactional
	public void modifiePwErreur(String username, String role) throws Exception {

		// POST bad oldPw

		mvc.perform(post("/user/vo/update").with(user(username).roles(role)).with(csrf()).param("username", "vo")
				.param("oldPassword", "bad").param("password", "SecretVO1").param("confirmPassword", "SecretVO1")
				.param("role", "ROLE_PROF")).andExpect(status().isOk()).andExpect(view().name("user/userChangePw"))
				.andExpect(model().errorCount(1)).andExpect(model().attributeExists("userDto"));
		// POST bad new PW
		mvc.perform(post("/user/vo/update").with(user(username).roles(role)).with(csrf()).param("username", "vo")
				.param("oldPassword", "vo").param("password", "bad1").param("confirmPassword", "bad2")
				.param("role", "ROLE_PROF")).andExpect(status().isOk()).andExpect(view().name("user/userChangePw"))
				.andExpect(model().errorCount(3)).andExpect(model().attributeHasFieldErrors("userDto", "password"))
				.andExpect(model().attributeHasFieldErrors("userDto", "confirmPassword"))
				.andExpect(model().attributeExists("userDto"));
	}

	// Test des méthodes @PreAuthorize
	@Test
	@WithMockUser(username = "dh", roles = "PROF")
	@Transactional
	void testPreAutorizeBad1() {
		User user = new User("vo", "vo", Roles.ROLE_PROF);
		assertThrows(AccessDeniedException.class, () -> userSrv.changePassword(user, "vo", "newPw"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	@Transactional
	void testPreAutorizeBad2() {
		User user = new User("vo", "vo", Roles.ROLE_PROF);
		assertThrows(CredentialException.class, () -> userSrv.changePassword(user, "vo", "newPw"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	@Transactional
	void testPreAutorizeBad3() {
		// le password du user à modifier n'est pas correct
		User user = userDao.findById("vo").get();
		assertThrows(CredentialException.class, () -> userSrv.changePassword(user, "badPw", "newPw"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	@Transactional
	void testPreAutorizeBad4() {
		// le user envoyé n'est pas identique à celui de la bd
		User user = new User("vo", "xxx", Roles.ROLE_PROF);
		assertThrows(CredentialException.class, () -> userSrv.changePassword(user, "badPw", "newPw"));
	}

	@Test
	@WithMockUser(username = "vo", roles = "PROF")
	@Transactional
	void testPreAutorizeOkVo() {
		User user = userDao.findById("vo").get();
		assertDoesNotThrow(() -> userSrv.changePassword(user, "vo", "newPw"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	@Transactional
	void testPreAutorizeOk() {
		User user = userDao.findById("vo").get();
		assertDoesNotThrow(() -> userSrv.changePassword(user, "vo", "newPw"));
	}
}
