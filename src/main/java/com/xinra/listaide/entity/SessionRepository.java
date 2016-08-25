package com.xinra.listaide.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends CrudRepository<Session, String> {
	
	List<Session> findByUserId(String userId);
	
}