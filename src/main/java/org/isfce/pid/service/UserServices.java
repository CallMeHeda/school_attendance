package org.isfce.pid.service;

import java.util.Optional;

import javax.security.auth.login.CredentialException;

import org.isfce.pid.controller.NotExistException;
import org.isfce.pid.dao.IUsersJpaDao;
import org.isfce.pid.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServices {
	@Autowired
	PasswordEncoder encodeur;
	
	@Autowired
	private IUsersJpaDao userDao;
	
	User user;

	
	public Optional<User> findById(String username) throws UsernameNotFoundException {
		return userDao.findById(username);
	}

	/**
	 * Un utilisateur est g�n�ralement li� � une personne
	 * sauf dans des cas particuliers comme pour l'administrateur
	 * 
	 * @param user
	 * @return le user sauv� qui peut avoir �t� modifi� suite � la persistance
	 */
	public User createUser(User user) {
		return userDao.save(user);
	}

	/**
	 * Supprime l'utilisateur 
	 * cependant il ne devra plus �tre li� � une personne (etudiant,...)
	 * 
	 * @param username
	 */
	public void deleteUser(String username) {
		userDao.deleteById(username);
	}

	/**
	 * 
	 * @param user 
	 * @param oldPassword non crypt�
	 * @param newPassword non crypt� et suppos� valid�
	 * @throws CredentialException 
	 */
	@PreAuthorize("hasRole('ADMIN') or  #user.username eq authentication.name")
	public void changePassword(User user, String oldPassword, String newPassword) throws CredentialException {
		//v�rifie l'existance du user
		//r�cup�re le user dans la BD pour voir s'il existe bien 
		User userDb=userDao.findById(user.getUsername()).orElseThrow(
				()->new NotExistException("{user.inexistant}", user.getUsername()));
		//V�rifie qu'il correspond avec celui � modifier
		if(! user.equals(userDb))
			throw  new CredentialException("{user.different}");
		
		//v�rification du pw
		if(!encodeur.matches(oldPassword, userDb.getPassword()))
			throw new CredentialException("{user.badPassword}");	
		userDao.save(new User(user.getUsername(),encodeur.encode(newPassword),user.getRole()));
	}

	public boolean userExists(String username) {
		return userDao.existsById(username);	
	}

	public boolean existsById(String username) {
		return userDao.existsById(username);
	}
	
	public User getByUsername(String username) {
		return userDao.getByUser(username);
	}

}
