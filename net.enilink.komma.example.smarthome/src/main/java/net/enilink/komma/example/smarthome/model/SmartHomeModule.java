package net.enilink.komma.example.smarthome.model;

import net.enilink.komma.core.KommaModule;

public class SmartHomeModule extends KommaModule {
	{
		addConcept(Lamp.class);
		addConcept(Switch.class);
		addConcept(Switchable.class);

		addBehaviour(SwitchSupport.class);
		addBehaviour(FloorSwitchSupport.class);
	}
}
