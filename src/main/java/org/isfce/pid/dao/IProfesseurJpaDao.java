package org.isfce.pid.dao;

import java.util.Set;

import org.isfce.pid.model.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IProfesseurJpaDao extends JpaRepository<Professeur, Integer> {

	Set<Professeur> readByEmailIgnoringCase(String email);
	
	@Query("select p  from TPROFESSEUR p where p.email like '%isfce.be'")
	Set<Professeur> readByEmailIsfce();
	
     @Query("from TUSER u where u.username=?1 and u.role=2")
	 boolean existByUserName(String username);
     
     @Query("from TPROFESSEUR p join p.user u where u.username=?1")
     Professeur findByUsername(String username);

	 int countByNomAndPrenom(String nom, String prenom);
}
