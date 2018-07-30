package org.eaSTars.javasourcer.repository;

import java.util.Optional;

import org.eaSTars.javasourcer.model.JavaSourceProject;
import org.springframework.data.repository.CrudRepository;

public interface JavaSourceProjectRepository extends CrudRepository<JavaSourceProject, Long> {

	public Optional<JavaSourceProject> findByName(String name);
	
}