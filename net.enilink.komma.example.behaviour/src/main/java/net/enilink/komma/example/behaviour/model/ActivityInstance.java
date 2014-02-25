package net.enilink.komma.example.behaviour.model;

import net.enilink.composition.annotations.Iri;

@Iri("http://enilink.net/komma/example/behaviour#ActivityInstance")
public interface ActivityInstance extends RuntimeAspect {

	@Iri("http://enilink.net/komma/example/behaviour#processInstance")
	ProcessInstance getProcessInstance();

	void setProcessInstance(ProcessInstance processInstance);

	@Iri("http://enilink.net/komma/example/behaviour#transitionName")
	String getTransitionName();

	void setTransitionName(String transitionName);

	@Iri("http://enilink.net/komma/example/behaviour#usesDefinition")
	ActivityDefinition getUsesDefinition();

	void setUsesDefinition(ActivityDefinition usesDefinition);

}
