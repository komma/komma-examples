package net.enilink.komma.example.behaviour.model;

import java.util.Set;

import net.enilink.composition.annotations.Iri;

@Iri("http://enilink.net/komma/example/behaviour#ActivityDefinition")
public interface ActivityDefinition extends DefinitionAspect {

	@Iri("http://enilink.net/komma/example/behaviour#hasName")
	String getName();

	void setName(String jpdlName);

	@Iri("http://enilink.net/komma/example/behaviour#hasTransition")
	Set<Transition> getHasTransition();

	void setHasTransition(Set<? extends Transition> hasTransition);

}
