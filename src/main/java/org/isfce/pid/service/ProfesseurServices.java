package org.isfce.pid.service;

import java.util.Collection;
import java.util.Optional;

import org.isfce.pid.dao.IProfesseurJpaDao;
import org.isfce.pid.model.Professeur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfesseurServices {

	private IProfesseurJpaDao professeurDao;

	@Autowired
	public ProfesseurServices(IProfesseurJpaDao professeurDao) {
		this.professeurDao = professeurDao;
	}

	public Optional<Professeur> getById(Integer id) {
		return professeurDao.findById(id);
	}

	public Collection<Professeur> findAll() {
		return professeurDao.findAll();
	}

	public void add(Professeur professeur) {
		professeurDao.save(professeur);
	}

	public boolean existsById(Integer id) {
		return professeurDao.existsById(id);

	}

	public Professeur save(Professeur professeur) {
		return professeurDao.save(professeur);
	}

	public void delete(Professeur professeur) {
		professeurDao.delete(professeur);
	}

	public Optional<Professeur> getByUserName(String username) {
		return Optional.ofNullable(professeurDao.findByUsername(username));
	}

	public boolean existsByUserName(String username) {
		professeurDao.existByUserName(username);
		return false;
	}

	public void deleteById(Integer id) {
		professeurDao.deleteById(id);
	}

	public boolean existsByNomPrenom(String nom, String prenom) {
		return professeurDao.countByNomAndPrenom(nom,prenom)>0;
	}

}
