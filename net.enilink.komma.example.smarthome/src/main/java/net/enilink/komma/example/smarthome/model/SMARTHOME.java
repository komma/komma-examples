package net.enilink.komma.example.smarthome.model;

import net.enilink.komma.core.URI;
import net.enilink.komma.core.URIs;

public class SMARTHOME {
	public final static String NAMESPACE = "http://enilink.net/vocab/smarthome#";
	public final static URI NAMESPACE_URI = URIs.createURI(NAMESPACE);

	public final static URI TYPE_SWITCH = NAMESPACE_URI.appendLocalPart("Switch");
	public final static URI TYPE_LAMP = NAMESPACE_URI.appendLocalPart("Lamp");
}