package net.enilink.komma.example.smarthome.model;

import java.util.Set;

import net.enilink.composition.annotations.Iri;

@Iri("http://enilink.net/vocab/smarthome#Switch")
public interface Switch extends Switchable {
	@Iri("http://enilink.net/vocab/smarthome#controls")
	Set<Switchable> controls();

	void controls(Set<Switchable> components);
	
	String label();
}