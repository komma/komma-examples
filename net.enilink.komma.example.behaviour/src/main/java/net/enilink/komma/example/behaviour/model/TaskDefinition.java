package net.enilink.komma.example.behaviour.model;

import net.enilink.composition.annotations.Iri;

@Iri("http://enilink.net/komma/example/behaviour#TaskDefinition")
public interface TaskDefinition extends ActivityDefinition {

	@Iri("http://enilink.net/komma/example/behaviour#forUser")
	String getForUser();

	void setForUser(String forUser);

}
