package org.isfce.pid.dao;

import java.util.List;

import org.isfce.pid.model.Inscription;
import org.isfce.pid.model.Inscription.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 
 * @author E. Fatima
 *
 */
public interface IInscriptionJpaDao extends JpaRepository<Inscription, Id> {

	// REQUETE AJOUT QUOTA ABSENCE TINSCRIPTION
	@Modifying
	@Query(value = "UPDATE TINSCRIPTION SET NB_ABSENCE = :percent WHERE FKETUDIANT=:id AND FKMODULE=:code", nativeQuery = true)
	void updateInscription(@Param("percent") Integer percent, @Param("id") Integer id, @Param("code") String code);

	// COMPTEUR INSCRIPTIONS POUR UN MODULE
	@Query(value = "SELECT COUNT(*) FROM TINSCRIPTION JOIN TMODULE ON TINSCRIPTION.FKMODULE=TMODULE.CODE "
			+ "WHERE TMODULE.CODE=? AND FKETUDIANT=?", nativeQuery = true)
	Integer countByModule(String code, Integer id);

	// TOUTES LES INSCRIPTION D'UN MODULE
	@Query(value = "SELECT * FROM TUSER JOIN TETUDIANT JOIN TMODULE ON TUSER.USERNAME=TETUDIANT.FKUSER WHERE ROLE=2 AND TMODULE.CODE=:code "
			+ "AND TETUDIANT.ID NOT IN (SELECT TINSCRIPTION.FKETUDIANT FROM TINSCRIPTION JOIN TETUDIANT JOIN TMODULE on TINSCRIPTION.FKETUDIANT=TETUDIANT.ID "
			+ "AND TINSCRIPTION.FKMODULE=TMODULE.CODE where TMODULE.CODE=:code)", nativeQuery = true)
	List<Inscription> findByModule(@Param ("code") String code);
	
//	@Query(value="SELECT * FROM TINSCRIPTION JOIN TMODULE ON TINSCRIPTION.FKMODULE=TMODULE.CODE WHERE TINSCRIPTION.FKMODULE=?",
//			nativeQuery = true)
//	List<Inscription> findByModule(String code);

}
