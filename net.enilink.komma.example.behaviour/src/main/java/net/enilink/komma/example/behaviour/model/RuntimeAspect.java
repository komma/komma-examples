package net.enilink.komma.example.behaviour.model;

import net.enilink.composition.annotations.Iri;
import net.enilink.komma.core.IEntity;

@Iri("http://enilink.net/komma/example/behaviour#RuntimeAspect")
public interface RuntimeAspect extends IEntity {

	@Iri("http://enilink.net/komma/example/behaviour#state")
	String getState();

	void setState(String state);

	@Iri("http://enilink.net/komma/example/behaviour#usesDefinition")
	DefinitionAspect getUsesDefinition();

	void setUsesDefinition(DefinitionAspect usesDefinition);

}
