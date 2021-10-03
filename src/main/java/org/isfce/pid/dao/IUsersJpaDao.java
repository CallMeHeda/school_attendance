package org.isfce.pid.dao;

import org.isfce.pid.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsersJpaDao extends JpaRepository<User, String>{
	
//	@Query(value="select ROLE from TUSER", nativeQuery=true)
//	List<Integer> roleByUser(Integer role);
	
	@Query(value="SELECT * FROM TUSER WHERE USERNAME=?", nativeQuery = true)
	User getByUser(String username);

}
