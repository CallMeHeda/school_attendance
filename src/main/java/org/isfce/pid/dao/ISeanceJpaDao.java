package org.isfce.pid.dao;

import java.time.LocalDate;
import java.util.List;

import org.isfce.pid.model.Seance;
import org.isfce.pid.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * 
 * @author E. Fatima
 *
 */

public interface ISeanceJpaDao extends JpaRepository<Seance, Integer> {

	// RECUPERATION DES SEANCES D'UN MODULE
	List<Seance> findByModule(Module module);
	
	// COUNT SEANCE BY MODULE
	@Query(value="SELECT COUNT(*) FROM TSEANCE JOIN TMODULE ON TSEANCE.FKMODULE=TMODULE.CODE WHERE TMODULE.CODE=?",
			nativeQuery = true)
	Integer countByModule(String code);
}
