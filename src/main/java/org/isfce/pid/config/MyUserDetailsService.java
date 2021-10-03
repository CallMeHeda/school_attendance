package org.isfce.pid.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.isfce.pid.dao.IUsersJpaDao;
import org.isfce.pid.model.SecurityUser;
import org.isfce.pid.model.User;

/**
 * Retourne la liste des utilisateurs pour obtenir les "UserDetails" de
 * l'utilisateur connecté Nécessaire pour la configuration de la sécurité
 * 
 * @author Didier
 *
 */

@Service(value = "myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {

	private IUsersJpaDao usersDAO;

	@Autowired
	public MyUserDetailsService(IUsersJpaDao usersDAO) {
		super();
		this.usersDAO = usersDAO;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> oUser = usersDAO.findById(username);
		if (oUser.isPresent())
			return new SecurityUser(oUser.get());
		throw new UsernameNotFoundException("User '" + username + "' not found");
	}

}
