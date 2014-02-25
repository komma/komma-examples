package net.enilink.komma.example.behaviour.model;

import net.enilink.composition.annotations.Iri;

@Iri("http://enilink.net/komma/example/behaviour#ProcessInstance")
public interface ProcessInstance extends RuntimeAspect {

	@Iri("http://enilink.net/komma/example/behaviour#currentActivity")
	ActivityInstance getCurrentActivity();

	void setCurrentActivity(ActivityInstance currentActivity);

	@Iri("http://enilink.net/komma/example/behaviour#usesDefinition")
	ProcessDefinition getUsesDefinition();

	void setUsesDefinition(ProcessDefinition jpdlUsesDefinition);

}
