package org.isfce.pid.cours;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.isfce.pid.dao.ICoursJpaDao;
import org.isfce.pid.dao.IEtudiantJpaDao;
import org.isfce.pid.dao.IModuleJpaDao;
import org.isfce.pid.dao.IProfesseurJpaDao;
import org.isfce.pid.dao.IUsersJpaDao;
import org.isfce.pid.model.Cours;
import org.isfce.pid.model.Etudiant;
import org.isfce.pid.model.Module;
import org.isfce.pid.model.Roles;
import org.isfce.pid.model.Module.MAS;
import org.isfce.pid.model.Professeur;
import org.isfce.pid.model.User;

@ActiveProfiles(value = "testU")
@SpringBootTest
class Test_DaoCours {
	@Autowired
	ICoursJpaDao coursDAO;
	@Autowired
	IModuleJpaDao moduleDAO;
	@Autowired
	IEtudiantJpaDao etudiantsDAO;
	@Autowired
	IProfesseurJpaDao profDAO;
	@Autowired
	IUsersJpaDao userDAO;

	static Cours c1, c2, c3;

	static Module m1, m2, m3;
	
	static Professeur p1,p2,p3;
	
//	static Professeur p1;
	static User u1,u2;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		u1 = new User("vo", "vo", Roles.ROLE_PROF);
		u2 = new User("dh", "dh", Roles.ROLE_PROF);
		c1 = new Cours("IPID", "Projet de d�veloppement Web", (short) 60);
		c2 = new Cours("ISO2", "Structure des Ordinateurs", (short) 60);
		c3 = new Cours("XTAB", "Tableau et Base de Donn�es", (short) 80);
		c1.getSections().add("Informatique");
		c2.getSections().add("Informatique");
		c3.getSections().add("Secr�tariat");
		c3.getSections().add("Comptabilit�");
		p1 = new Professeur("Van Oudenhove", "Didier", "vo@isfce.be", u1);
		p2 = new Professeur("De Hainau", "Marie-Ange", "dh@isfce.be", u2);

		m1 = new Module("IPID-1-A", LocalDate.of(2019, 12, 2), LocalDate.of(2020, 12, 2), MAS.SOIR, c1,null);
		m2 = new Module("ISO2-1-A", LocalDate.of(2019, 12, 2), LocalDate.of(2020, 12, 2), MAS.APM, c2,null);
		m3 = new Module("ISO2-1-B", LocalDate.of(2019, 12, 2), LocalDate.of(2020, 12, 2), MAS.SOIR, c2,null);
	}

	@Test
	@Transactional
	void testCoursModule() {

		Cours c11 = coursDAO.save(c1);
		Cours c22 = coursDAO.save(c2);
		coursDAO.save(c3);
		assertEquals(3, coursDAO.count());
		c11.setNbPeriodes((short) 100);
		coursDAO.save(c11);
		assertEquals(100, coursDAO.findById("IPID").get().getNbPeriodes());
		Module m11 = moduleDAO.save(m1);
		assertTrue(m11.getEtudiants().isEmpty());
		moduleDAO.save(m2);
		moduleDAO.save(m3);
		assertEquals(2, moduleDAO.countByCours(c22));
		assertEquals(1, moduleDAO.countByCours(c11));
		
	}
	
	@Test
	@Transactional
	void testInscription() {
		Etudiant et1 = new Etudiant("Et1", "et1", "et1@isfce.be", "02/647.25.69",new User("Et1", "SecretEt1", Roles.ROLE_ETUDIANT));
		Etudiant et2 = new Etudiant("Et2", "et2", "et2@isfce.be", "02/647.25.69",new User("Et2", "SecretEt2", Roles.ROLE_ETUDIANT));
		Etudiant et11 = etudiantsDAO.save(et1);
		Etudiant et22 = etudiantsDAO.save(et2);
		// Sauve le module et le cours associ�
		Module m11 = moduleDAO.save(m1);
		// inscription des �tudiants
		m11.getEtudiants().add(et11);
		m11.getEtudiants().add(et22);

		// Recharge un module et v�rifie si 2 inscriptions
		m11 = moduleDAO.getOne("IPID-1-A");
		assertEquals(2, m11.getEtudiants().size());

		// Inscription de et2 � un autre module
		// m2.getEtudiants().add(et2);// OK si cascade ALL
		Module m22 = moduleDAO.save(m2);// sauve le module
		m22.getEtudiants().add(et22);// insrit l'�tudiant

		// recharge le module et v�rifie s'il est inscrit
		assertEquals(1, moduleDAO.getOne("ISO2-1-A").getEtudiants().size());
		assertTrue(moduleDAO.getOne("ISO2-1-A").getEtudiants().contains(et22));

		// V�rifie que Et22 est bien inscrit � 2 modules
		Set<Module> mod = moduleDAO.moduleByEtudiant(et22.getId());
		assertEquals(2, mod.size());

		// Test le query natif nombre de module par �tudiant
		assertEquals(2, moduleDAO.countModulesByEtudiant(et22.getId()));

	}
}
