package org.isfce.pid.dao;

import java.util.List;

import org.isfce.pid.model.Presence;
import org.isfce.pid.model.Presence.Id;
import org.isfce.pid.model.Presence.TYPE_PRESENCE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 * 
 * @author E. Fatima
 *
 */
public interface IPresenceJpaDao extends JpaRepository<Presence, Id> {
	
	// TOUTES LES PRESENCES D'UNE SEANCE
	@Query(value="SELECT * FROM TPRESENCE JOIN TINSCRIPTION JOIN TMODULE JOIN TSEANCE "
			+ "JOIN TETUDIANT ON TPRESENCE.FKETUDIANT=TINSCRIPTION.FKETUDIANT "
			+ "AND TINSCRIPTION.FKMODULE=TMODULE.CODE AND TPRESENCE.FKSEANCE=TSEANCE.ID AND TSEANCE.FKMODULE=TMODULE.CODE AND TPRESENCE.FKETUDIANT=TETUDIANT.ID WHERE FKSEANCE=? "
			+ "GROUP BY TINSCRIPTION.FKMODULE,TINSCRIPTION.FKETUDIANT,TETUDIANT.NOM ORDER BY TINSCRIPTION.NB_ABSENCE,TETUDIANT.NOM",
			nativeQuery = true)
     List<Presence> findBySeance(Integer id);
	 
	 // COMPTEUR TOTAL DES PRESENCES (UTILISER DANS METHODE PriseDePresence)
	 @Query(value = "SELECT COUNT(*) FROM TPRESENCE JOIN TSEANCE ON TPRESENCE.FKSEANCE=TSEANCE.ID WHERE TSEANCE.ID=?",
			 nativeQuery = true)
	 Integer countByPresence(Integer seanceID);
	 
	  // NOMBRE DE PRESENCE PAR ETUDIANT POUR UN MODULE
	  @Query(value="SELECT COUNT(*) FROM TPRESENCE JOIN TMODULE JOIN TSEANCE ON TPRESENCE.FKSEANCE=TSEANCE.ID AND "
	  		+ "TSEANCE.FKMODULE=TMODULE.CODE WHERE (FKETUDIANT=? AND TMODULE.CODE=?) AND (TYPE_PRESENCE='P' OR TYPE_PRESENCE='C')", 
	  		nativeQuery = true)
	  Integer countPresence(Integer id,String code);
	  
	  // NOMBRE D'ABSENCE PAR ETUDIANT POUR UN MODULE
	  @Query(value="SELECT COUNT(*) FROM TPRESENCE JOIN TMODULE JOIN TSEANCE ON TPRESENCE.FKSEANCE=TSEANCE.ID AND "
	  		+ "TSEANCE.FKMODULE=TMODULE.CODE WHERE (FKETUDIANT=? AND TMODULE.CODE=?) "
	  		+ "AND (TYPE_PRESENCE='CM' OR TYPE_PRESENCE='AJ' OR TYPE_PRESENCE='A')", nativeQuery = true)
	  Integer countAbsence(Integer id,String code);
	  
	  // UNE PRESENCE D'UN ETUDIANT
	  @Query(value="SELECT TYPE_PRESENCE FROM TPRESENCE JOIN TSEANCE JOIN TMODULE ON TPRESENCE.FKSEANCE=TSEANCE.ID AND TSEANCE.FKMODULE=TMODULE.CODE "
		  		+ "WHERE TPRESENCE.FKETUDIANT=? AND FKMODULE=?", nativeQuery = true)
	  TYPE_PRESENCE getTypePresence(Integer idEtudiant,String code);
	  // UPDATE
		@Modifying
		@Query(value="UPDATE TPRESENCE SET TYPE_PRESENCE = '0' WHERE TPRESENCE.FKETUDIANT=:idEtudiant AND TPRESENCE.FKSEANCE=:idSeance",
				nativeQuery = true)
		void updateTypePresence(@Param("idEtudiant") Integer idEtudiant,@Param("idSeance") Integer idSeance);
	  
	  // TYPE_PRESENCE (METHODE calculPourcentage)
	  @Query(value="SELECT TYPE_PRESENCE FROM TPRESENCE WHERE FKETUDIANT=?", nativeQuery = true)
	  List<TYPE_PRESENCE> type_presence(Integer id);
	  
	  // NB LEFT BEFORE TRUE
	  @Query(value=("SELECT COUNT(TPRESENCE.LEFT_BEFORE) FROM TPRESENCE JOIN TSEANCE JOIN TMODULE "
	  		+ "ON TPRESENCE.FKSEANCE=TSEANCE.ID AND TSEANCE.FKMODULE=TMODULE.CODE WHERE TMODULE.CODE =? AND TPRESENCE.FKETUDIANT=? "
	  		+ "AND TPRESENCE.LEFT_BEFORE=TRUE"),nativeQuery = true)
	  Integer countLeftBefore(String code,Integer id);
	  
}
