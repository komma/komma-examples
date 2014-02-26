package net.enilink.komma.example.behaviour;

import net.enilink.komma.example.behaviour.model.ProcessInstance;

public interface ITransition {
	public boolean activate(ProcessInstance processInstance);
}
