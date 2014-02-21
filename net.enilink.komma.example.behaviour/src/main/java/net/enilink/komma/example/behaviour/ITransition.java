package net.enilink.komma.example.behaviour;

import net.enilink.komma.example.behaviour.model.ProcessInstance;

public interface ITransition {
	public void activate(ProcessInstance processInstance);
}
