package com.project2.demo.DAO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.project2.demo.beans.User;



@Component
@Transactional
public class UserRepository {
	@PersistenceContext
	private EntityManager entityManager;
	
	public UserRepository()  {
		// TODO Auto-generated constructor stub
	}
	
	public User getThing(int id) {
		return entityManager.find(User.class, id);
	}
	
	public void addThing(User user) {
		entityManager.persist(user);
	}

}
