package org.isfce.pid.dao;

import java.util.List;

import org.isfce.pid.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IEtudiantJpaDao extends JpaRepository<Etudiant, Integer> {

 int countByEmailEndingWithIgnoringCase(String domaine);
 
  @Query("from TETUDIANT e where e.email = ?1")
  List<Etudiant> findEtudiantIsfce(String domaineEt);
  
  @Query("from TETUDIANT e join e.user u where u.username=?1")
  Etudiant findByUsername(String username);
 
//  // AFFICHE LES PRESENCES ET ABSENCES D'UN ETUDIANT (a partir de l'id etudiant et id seance)
//  @Query(value="select present,typeAbsence from TPRESENCE where FKETUDIANT=? and FKSEANCE=?", nativeQuery=true)
//	List<String> presenceEtudiant(int idEtudiant, int idSeance);
  
  // LISTE DES ETUDIANTS INSCRITS A UN MODULE
  @Query(value = "SELECT * FROM TETUDIANT JOIN TINSCRIPTION on TETUDIANT.ID=TINSCRIPTION.FKETUDIANT where TINSCRIPTION.FKMODULE=?", 
		  nativeQuery = true)
  List<Etudiant> findStudByModule(String code);
  
  // ETUDIANT INSCRIT AU MODULE
  @Query(value = "SELECT * FROM TINSCRIPTION JOIN TETUDIANT on TINSCRIPTION.FKETUDIANT=TETUDIANT.ID "
  		+ "where TINSCRIPTION.FKMODULE=? AND TINSCRIPTION.FKETUDIANT=?", nativeQuery = true)
  Etudiant findByModuleAndStud(String code,Integer id);

  // DELETE ETUDIANT QUAND ABSENCE > 30%
  @Modifying
  @Query(value="DELETE FROM TINSCRIPTION WHERE FKETUDIANT=? AND FKMODULE=?", nativeQuery=true)
  void deleteEtudiantInscription(Integer id,String code);
  
  @Modifying
  @Query(value="DELETE FROM TPRESENCE p WHERE EXISTS(SELECT * FROM TSEANCE s JOIN TMODULE m ON s.FKMODULE=m.CODE AND s.ID=p.FKSEANCE "
  		+ "WHERE p.FKETUDIANT=? AND m.CODE=?)", nativeQuery = true)
  void deleteEtudiantPresence(Integer id, String code);
  
  int countByNomAndPrenom(String nom, String prenom);
  
//ETUDIANTS PAS INSCRITS AU MODULE
	@Query(value = "SELECT * FROM TUSER JOIN TETUDIANT JOIN TMODULE ON TUSER.USERNAME=TETUDIANT.FKUSER WHERE ROLE=2 AND TMODULE.CODE=:code "
			+ "AND TETUDIANT.ID NOT IN (SELECT TINSCRIPTION.FKETUDIANT FROM TINSCRIPTION JOIN TETUDIANT JOIN TMODULE on TINSCRIPTION.FKETUDIANT=TETUDIANT.ID "
			+ "AND TINSCRIPTION.FKMODULE=TMODULE.CODE where TMODULE.CODE=:code)", nativeQuery = true)
	List<Etudiant> findStudNotRegisterByModule(@Param ("code") String code);
}
