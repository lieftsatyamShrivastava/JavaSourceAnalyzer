package org.eastars.javasourcer.gui.dto;

import java.util.ArrayList;
import java.util.List;

public class ProjectDTO {

	private String name;
	
	private String basedir;
	
	private List<String> sourceFolders = new ArrayList<>();
	
	private List<String> libraries = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBasedir() {
		return basedir;
	}

	public void setBasedir(String basedir) {
		this.basedir = basedir;
	}

	public List<String> getSourceFolders() {
		return sourceFolders;
	}

	public void setSourceFolders(List<String> sourceFolders) {
		this.sourceFolders = sourceFolders;
	}

	public List<String> getLibraries() {
		return libraries;
	}

	public void setLibraries(List<String> libraries) {
		this.libraries = libraries;
	}
	
}