package org.isfce.pid.etudiant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.transaction.Transactional;

import org.isfce.pid.dao.ICoursJpaDao;
import org.isfce.pid.dao.IEtudiantJpaDao;
import org.isfce.pid.dao.IModuleJpaDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.isfce.pid.model.Cours;
import org.isfce.pid.model.Etudiant;
import org.isfce.pid.model.Module;
import org.isfce.pid.model.Roles;
import org.isfce.pid.model.User;

@ActiveProfiles(value = "testU")
@Sql({ "/dataTestU.sql" })
@SpringBootTest
class TestEtudiant {

	@Autowired
	IEtudiantJpaDao daoEtudiant;

	@Autowired
	IModuleJpaDao daoModule;

	@Autowired
	ICoursJpaDao daoCours;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	@Transactional
	void nbEtudiants() {
		assertEquals(4, daoEtudiant.count());
	}

	@Test
	@Transactional
	void nbEmailIsfce() {
		assertEquals(4, daoEtudiant.countByEmailEndingWithIgnoringCase("Isfce.Be"));
		assertEquals(1, daoEtudiant.findEtudiantIsfce("et1@isfce.be").size());
	}

	@Transactional
	@Test
	void testInscription() {
		Optional<Module> opid1 = daoModule.findById("IPID-1-A");
		assertTrue(opid1.isPresent());
		Etudiant et1 = new Etudiant("ZZZ", "top", "EEE@GSD.com", "02/267.23.23",
				new User("ZZZ", "passwordZ1", Roles.ROLE_ETUDIANT));
		// Ajout d'un nouvel étudiant à ce module
		opid1.get().getEtudiants().add(et1);
		// Vérifie si présent dans la bd
		assertEquals(5, daoEtudiant.count());
	}

	@Transactional
	@Test
	void testCoursSansModule() {
		// Suppression d'un cours sans Module
		Optional<Cours> opid = daoCours.findById("XTAB");
		assertTrue(opid.isPresent());
		daoCours.delete(opid.get());// suppression du cours
		// vérifie si le cours a été supprimé
		assertFalse(daoCours.existsById("XTAB"));
		// vérifie si le module ipid-1-A est bien supprimé
		// assertFalse(daoModule.existsById("IPID-1-A"));
	}

	@Transactional
	@Test
	void testCoursAvecModule() {
		// Suppression d'un cours avec Module
		Optional<Cours> opid = daoCours.findById("ISO2");
		assertTrue(opid.isPresent());
		assertTrue(daoModule.countByCours(opid.get())>0);
		//On ne doit pas pouvoir  suppression un cours avec module
		daoCours.delete(opid.get());
		//applique tous les changements dans la bd
		assertThrows(DataIntegrityViolationException.class, ()->daoCours.flush());
		
	}
}
