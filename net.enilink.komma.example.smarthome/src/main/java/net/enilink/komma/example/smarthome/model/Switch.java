package net.enilink.komma.example.smarthome.model;

import java.util.Set;

import net.enilink.composition.annotations.Iri;

@Iri("http://komma.github.io/komma-examples/vocab/smarthome.ttl#Switch")
public interface Switch extends Switchable {
	@Iri("http://komma.github.io/komma-examples/vocab/smarthome.ttl#controls")
	Set<Switchable> controls();

	void controls(Set<Switchable> components);
	
	String label();
}