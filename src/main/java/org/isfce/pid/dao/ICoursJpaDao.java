package org.isfce.pid.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.isfce.pid.model.Cours;

@Repository
public interface ICoursJpaDao extends JpaRepository<Cours, String>{
	//Utilisation du Mini DSL pour charger les cours d'un section
	List<Cours> readBySectionsIgnoringCase(String section);
	
	//Utilisation du Mini DSL :renvoie le nombre de cours ayant le nom sp�cifi�
	int countByNom(String nom);
	
	//Utilisation d'un Query natif pour avoir la liste des sections d'un cours
	@Query(value="select section from TSECTION where FKCOURS=?", nativeQuery=true)
	List<String> coursSection(String codeCours);
	
	//Utilisation d'un Query natif pour avoir la liste des sections sans doublon
	@Query(value="select distinct upper(section) from TSECTION ", nativeQuery=true)
	List<String> listeSections();
	
//	@Query(value="SELECT (NB_PERIODES * 50)/60 FROM TCOURS JOIN TMODULE ON TCOURS.CODE=TMODULE.FKCOURS WHERE TMODULE.CODE=?", nativeQuery = true)
//	Integer nbHeureByCours(String code);
}
