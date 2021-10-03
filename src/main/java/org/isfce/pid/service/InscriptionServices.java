package org.isfce.pid.service;

import java.util.List;
import java.util.Optional;

import org.isfce.pid.dao.IEtudiantJpaDao;
import org.isfce.pid.dao.IInscriptionJpaDao;
import org.isfce.pid.dao.IModuleJpaDao;
import org.isfce.pid.model.Inscription;
import org.isfce.pid.model.Inscription.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author E. Fatima
 *
 */
@Service
public class InscriptionServices {

	IInscriptionJpaDao inscriptionDAO;
	IEtudiantJpaDao etudiantDAO;
	IModuleJpaDao moduleDAO;

	@Autowired
	public InscriptionServices(IInscriptionJpaDao inscriptionDAO, IEtudiantJpaDao etudiantDAO,
			IModuleJpaDao moduleDAO) {
		this.inscriptionDAO = inscriptionDAO;
		this.etudiantDAO = etudiantDAO;
		this.moduleDAO = moduleDAO;
	}
	
	public Inscription findById(Id idInscription) {
        Optional<Inscription> inscription = inscriptionDAO.findById(idInscription);
        if(inscription.isPresent()) {
            return inscription.get();
        }
        return null;
    }


	public void updateInscription(Integer percent, Integer id, String code) {
		inscriptionDAO.updateInscription(percent, id, code);
	}
	
	public Inscription insert(Inscription inscription) {
		return inscriptionDAO.save(inscription);
	}
	
	public List<Inscription> findStudNotRegisterToModule(String code) {
		return inscriptionDAO.findByModule(code);
	}
}
