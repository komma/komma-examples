package net.enilink.komma.example.smarthome.model;

import net.enilink.composition.annotations.Iri;

@Iri("http://komma.github.io/komma-examples/vocab/smarthome.ttl#Switchable")
public interface Switchable {
	@Iri("http://komma.github.io/komma-examples/vocab/smarthome.ttl#on")
	boolean on();

	/**
	 * Switches this thing on or off.
	 * 
	 * @param on
	 *            <code>true</code> if this thing should be switched on, else
	 *            <code>false</code>
	 */
	void on(boolean on);
}
