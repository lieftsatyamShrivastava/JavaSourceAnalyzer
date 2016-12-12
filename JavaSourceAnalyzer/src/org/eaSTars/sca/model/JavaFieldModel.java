package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaField")
public class JavaFieldModel extends GenericModel {

	@Attribute(column="Modifiers")
	private Integer modifiers;
	
	@Attribute(column="ParentAssemblyID")
	@ForeignKey(table="JavaAssembly", attribute="PK")
	private Integer parentAssemblyID;
	
	@Attribute(column="JavaTypeID")
	@ForeignKey(table="JavaType", attribute="PK")
	private Integer javaTypeID;
	
	@Attribute(column="Name")
	private String name;

	public Integer getModifiers() {
		return modifiers;
	}

	public void setModifiers(Integer modifiers) {
		this.modifiers = modifiers;
	}

	public Integer getParentAssemblyID() {
		return parentAssemblyID;
	}

	public void setParentAssemblyID(Integer parentAssemblyID) {
		this.parentAssemblyID = parentAssemblyID;
	}

	public Integer getJavaTypeID() {
		return javaTypeID;
	}

	public void setJavaTypeID(Integer javaTypeID) {
		this.javaTypeID = javaTypeID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
