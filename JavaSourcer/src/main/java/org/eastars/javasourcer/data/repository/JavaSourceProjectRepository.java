package org.eastars.javasourcer.data.repository;

import java.util.List;
import java.util.Optional;

import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.springframework.data.repository.CrudRepository;

public interface JavaSourceProjectRepository extends CrudRepository<JavaSourceProject, Long> {

	public List<JavaSourceProject> findAllByOrderByCreationDateAsc();
	
	public Optional<JavaSourceProject> findByName(String name);
	
}
