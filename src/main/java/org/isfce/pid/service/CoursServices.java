package org.isfce.pid.service;


import java.util.Collection;
import java.util.Optional;

import org.isfce.pid.dao.ICoursJpaDao;
import org.isfce.pid.model.Cours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoursServices {

  private ICoursJpaDao coursdao;

	//Cr�ation d'un service pour la liste des cours simul�e
	@Autowired
	public CoursServices(ICoursJpaDao coursdao) {
		this.coursdao=coursdao;
	}

	/**
	 * retourne la liste de tous les cours
	 * 
	 * @return colelction de cours
	 */
	public Collection<Cours> findAll() {
		return coursdao.findAll();
	}

	/**
	 * V�rifie si un cours existe
	 * 
	 * @param code
	 * @return
	 */
	public boolean exists(String code) {

		return coursdao.findById(code).isPresent();

	}

	/**
	 * retourne un cours � partir de son code
	 * 
	 * @param code
	 * @return un Optional de cours
	 */
	public Optional<Cours> findOne(String code) {
		return coursdao.findById(code);
	}

	/**
	 * Suppression du cours s'il existe
	 * 
	 * @param code
	 */
	public void delete(String code) {
		coursdao.deleteById(code);
	}

	/***
	 * Fait une mise � jour d'un cours existant le code du cours ne doit pas
	 * changer!
	 * 
	 * @param c1
	 */
	public Cours update(Cours c1) {
		return coursdao.save(c1);
	}

	/**
	 * Ajout d'un nouveau cours
	 * 
	 * @param c1
	 * @return
	 */
	public Cours insert(Cours c1) {
		return coursdao.save(c1);
	}

	/**
	 * Test s'il n'existe pas un autre cours avec le m�me nom
	 * 
	 * @param c1
	 * @return true s'il existe un autre cours avec le m�me nom que c1.getNom()
	 */
	public boolean testNomDoublon(Cours c1) {
		if (c1 == null)
			return false;
		return coursdao.countByNom(c1.getNom())>1;
	}
	
	/////////////////////////////////////////////// NOMBRE TOTAL D'HEURE DU COURS ///////////////////////////////////////////////
	
//	public Integer countHoursByCours(String code) {
//		return coursdao.nbHeureByCours(code);
//	}

}
