package org.isfce.pid.professeur;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.isfce.pid.dao.IProfesseurJpaDao;
import org.isfce.pid.dao.IUsersJpaDao;
import org.isfce.pid.model.Professeur;
import org.isfce.pid.model.Roles;
import org.isfce.pid.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles(value = "testU")
@Sql({ "/dataTestU.sql" })
@SpringBootTest
public class Test_DaoProfesseur {
	@Autowired
	IProfesseurJpaDao professeurDao;
	@Autowired
	IUsersJpaDao userDao;
	
	@Test
	@Transactional
	void testProfEmail() {
		Professeur p1 = new Professeur("X1", "Didier", "vo@isfce.org", new User("EtX1", "passwordX1", Roles.ROLE_PROF));
		Professeur p2 = new Professeur("X2", "Marie-Ange", "vo@isfce.org", new User("EtX2", "passwordX2", Roles.ROLE_PROF));
		professeurDao.save(p1);
		professeurDao.save(p2);

		Set<Professeur> res = professeurDao.readByEmailIgnoringCase("VO@isfce.org");
		assertEquals(2, res.size());
	}

	@Test
	@Transactional
	void testEmailProf() {

		Set<Professeur> res = professeurDao.readByEmailIgnoringCase("VO@isfce.be");
		assertEquals(1, res.size());

		res = professeurDao.readByEmailIsfce();
		assertEquals(2, res.size());
	}

	@Test
	@Transactional
	void testNbProf() {
		Professeur p1 = new Professeur("X1", "Didier", "vo@isfce.org", new User("EtX1", "passwordX1", Roles.ROLE_PROF));
		Professeur p2 = new Professeur("X2", "Marie-Ange", "vo@isfce.org", new User("EtX2", "passwordX2", Roles.ROLE_PROF));

		assertEquals(2, professeurDao.count());
		professeurDao.save(p1);
		professeurDao.save(p2);
		assertEquals(4, professeurDao.count());
		assertNotNull(p1.getId());
		assertNotNull(p2.getId());
		assertTrue(userDao.existsById(p1.getUser().getUsername()));
		assertTrue(userDao.existsById(p2.getUser().getUsername()));
	}
}
