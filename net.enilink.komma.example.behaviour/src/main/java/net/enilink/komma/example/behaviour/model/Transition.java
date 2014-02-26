package net.enilink.komma.example.behaviour.model;

import net.enilink.composition.annotations.Iri;

@Iri("http://enilink.net/komma/example/behaviour#Transition")
public interface Transition extends DefinitionAspect {

	@Iri("http://enilink.net/komma/example/behaviour#hasName")
	String getName();

	void setName(String jpdlName);

	@Iri("http://enilink.net/komma/example/behaviour#toActivity")
	ActivityDefinition getToActivity();

	void setToActivity(ActivityDefinition toActivity);

}
