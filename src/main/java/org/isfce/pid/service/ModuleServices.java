package org.isfce.pid.service;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.isfce.pid.dao.IModuleJpaDao;
import org.isfce.pid.model.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModuleServices {

  private IModuleJpaDao moduledao;

	//Cr�ation d'un service pour la liste des modules simul�s
	@Autowired
	public ModuleServices(IModuleJpaDao moduledao) {
		this.moduledao=moduledao;
	}

	/**
	 * retourne la liste de tous les modules
	 * 
	 * @return colelction de module
	 */
	public Collection<Module> findAll() {
		return moduledao.findAll();
	}

	/**
	 * V�rifie si un module existe
	 * 
	 * @param code
	 * @return
	 */
	public boolean exists(String code) {

		return moduledao.findById(code).isPresent();

	}

	/**
	 * retourne un module � partir de son code
	 * 
	 * @param code
	 * @return un Optional de module
	 */
	public Optional<Module> findOne(String code) {
		return moduledao.findById(code);
	}

	/**
	 * Suppression du module s'il existe
	 * 
	 * @param code
	 */
	public void delete(String code) {
		moduledao.deleteById(code);
	}

	/***
	 * Fait une mise � jour d'un module existant le code du module ne doit pas
	 * changer!
	 * 
	 * @param mod1
	 */
	public Module update(Module mod1) {
		return moduledao.save(mod1);
	}

	/**
	 * Ajout d'un nouveau module
	 * 
	 * @param c1
	 * @return
	 */
	public Module insert(Module mod1) {
		return moduledao.save(mod1);
	}

	/**
	 * Test s'il n'existe pas un autre module avec le m�me cours
	 * 
	 * @param mod1
	 * @return true s'il existe un autre module avec le m�me cours que mod1.getCours()
	 */
	public boolean testNomDoublon(Module mod1) {
		if (mod1 == null)
			return false;
		return moduledao.countByCours(mod1.getCours())>1;
	}
	
/////////////////////////////////////////////// NOMBRE TOTAL D'HEURE DU COURS ///////////////////////////////////////////////
	
	public Integer countHoursByCours(String code) {
		return moduledao.nbHeureByCours(code);
	}
	
////////////////////////////////////////////// LISTE MODULE PAR COURS ///////////////////////////////////////////////////////
	
	public List<Module> moduleByCours(String code){
		return moduledao.moduleByCours(code);
	}
	
///////////////////////////////////////////// LISTE MODULE PAR COURS ///////////////////////////////////////////////////////
	
	public List<Module> moduleByProf(Integer id){
		return moduledao.readModuleByProf(id);
	}
	
	// LISTE DES MODULE D'UN ETUDIANT
		public List<Module> getModuleByStud(Integer id){
			return moduledao.findModuleByStud(id);
		}

}