package org.isfce.pid.dao;

import java.util.List;
import java.util.Set;

import org.isfce.pid.model.Cours;
import org.isfce.pid.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IModuleJpaDao extends JpaRepository<Module, String> {

	int countByCours(Cours cours);
	
	@Query(value="SELECT * FROM TMODULE WHERE FKCOURS=?", nativeQuery = true)
	List<Module> moduleByCours(String code);

	@Query(value = "SELECT m FROM TMODULE m JOIN m.etudiants e WHERE e.id=?1")
	Set<Module> moduleByEtudiant(Integer id);

	@Query(value = "SELECT COUNT(*) FROM  TINSCRIPTION i   WHERE FKETUDIANT=?1 ", nativeQuery = true)
	int countModulesByEtudiant(Integer id);
	
	@Query(value="SELECT * FROM TMODULE JOIN TPROFESSEUR ON TMODULE.FKPROF=TPROFESSEUR.ID WHERE TPROFESSEUR.ID=?"
			,nativeQuery = true)
	List<Module> readModuleByProf(Integer id);

	// CALCUL NOMBRE D'HEURE POURS UN COURS AUQUEL LE MODULE APPARTIENT (50 = 1 PERIODE)
	@Query(value = "SELECT (NB_PERIODES * 50)/60 FROM TCOURS JOIN TMODULE ON TCOURS.CODE=TMODULE.FKCOURS WHERE TMODULE.CODE=?", 
			nativeQuery = true)
	Integer nbHeureByCours(String code);
	
	  // MODULES AUQUELS EST INSCRITS UN ETUDIANT
	  @Query(value = "SELECT * FROM TINSCRIPTION JOIN TETUDIANT JOIN TMODULE on TINSCRIPTION.FKETUDIANT=TETUDIANT.ID "
	 		+ "AND TINSCRIPTION.FKMODULE=TMODULE.CODE where TETUDIANT.ID=?", nativeQuery = true)
	  List<Module> findModuleByStud(Integer id);
}
