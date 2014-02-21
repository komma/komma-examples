package net.enilink.komma.example.behaviour.model;

import java.util.Set;

import net.enilink.composition.annotations.Iri;

@Iri("http://enilink.net/komma/example/behaviour#ProcessDefinition")
public interface ProcessDefinition extends DefinitionAspect {

	@Iri("http://enilink.net/komma/example/behaviour#hasName")
	String getName();

	void setName(String name);

	@Iri("http://enilink.net/komma/example/behaviour#hasActivity")
	Set<ActivityDefinition> getHasActivity();

	void setHasActivity(Set<? extends ActivityDefinition> hasActivity);

}
