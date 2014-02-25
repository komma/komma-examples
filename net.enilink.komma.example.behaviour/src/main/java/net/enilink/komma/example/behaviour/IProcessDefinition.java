package net.enilink.komma.example.behaviour;

import net.enilink.komma.example.behaviour.model.ProcessInstance;

public interface IProcessDefinition {
	public ProcessInstance createProcessInstance();
}
