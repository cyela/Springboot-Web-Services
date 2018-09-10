package com.spring.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.spring.model.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long>{

	User findByEmailAndPassword(String email,String password);

}
