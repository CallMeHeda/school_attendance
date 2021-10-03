package org.isfce.pid.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.isfce.pid.dao.IEtudiantJpaDao;
import org.isfce.pid.model.Etudiant;
import org.isfce.pid.model.Inscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EtudiantServices {

	private IEtudiantJpaDao etudiantDAO;

	@Autowired
	public EtudiantServices(IEtudiantJpaDao etudiantDAO) {
		this.etudiantDAO = etudiantDAO;
	}

	public Optional<Etudiant> getById(Integer id) {
		return etudiantDAO.findById(id);
	}

	public Collection<Etudiant> findAll() {
		return etudiantDAO.findAll();
	}
	
	/**
	 * retourne un etudiant à partir de son id
	 * 
	 * @param id
	 * @return un Optional de etudiant
	 */
	public Optional<Etudiant> findOne(Integer id) {
		return etudiantDAO.findById(id);
	}

	public void add(Etudiant etudiant) {
		etudiantDAO.save(etudiant);
	}

	public boolean existsById(Integer id) {
		return etudiantDAO.existsById(id);

	}

	public Etudiant save(Etudiant student) {
		return etudiantDAO.save(student);
	}

	public void delete(Etudiant student) {
		etudiantDAO.delete(student);
	}

	public Optional<Etudiant> getByUserName(String username) {
		return Optional.ofNullable(etudiantDAO.findByUsername(username));
	}
	
	public List<Etudiant> findStudNotRegisterToModule(String code) {
		return etudiantDAO.findStudNotRegisterByModule(code);
	}

	public void deleteById(Integer id) {
		etudiantDAO.deleteById(id);
	}
	public void deleteStudentFromInscription(Integer id,String code) {
		etudiantDAO.deleteEtudiantInscription(id,code);
	}
	
	public void deleteStudentFromPresence(Integer id,String code) {
		etudiantDAO.deleteEtudiantPresence(id, code);
	}

	public boolean existsByNomPrenom(String nom, String prenom) {
		return etudiantDAO.countByNomAndPrenom(nom,prenom)>0;
	}
	
	// LISTE DES ETUDIANT INSCRITS AU MODULE
	public List<Etudiant> getStudByModule(String codeModule){
		return etudiantDAO.findStudByModule(codeModule);
	}
	
	// ETUDIANT INSCRIT A UN MODULE
	public Etudiant getByModuleAndStud(String code,Integer id) {
		return etudiantDAO.findByModuleAndStud(code, id);
	}
}
