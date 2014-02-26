package net.enilink.komma.example.behaviour;

import net.enilink.komma.example.behaviour.model.ActivityInstance;
import net.enilink.komma.example.behaviour.model.ProcessInstance;

public interface IActivityDefinition {
	public ActivityInstance createActivityInstance(
			ProcessInstance processInstance);
}
