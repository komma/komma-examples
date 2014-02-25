package net.enilink.komma.example.behaviour.model;

import net.enilink.composition.annotations.Iri;

@Iri("http://enilink.net/komma/example/behaviour#TaskInstance")
public interface TaskInstance extends ActivityInstance {

	@Iri("http://enilink.net/komma/example/behaviour#usesDefinition")
	TaskDefinition getUsesDefinition();

	void setUsesDefinition(TaskDefinition jpdlUsesDefinition);

}
