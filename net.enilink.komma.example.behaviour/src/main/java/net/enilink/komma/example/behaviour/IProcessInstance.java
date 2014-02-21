package net.enilink.komma.example.behaviour;

import net.enilink.komma.example.behaviour.model.ProcessInstance;

public interface IProcessInstance {
	public ProcessInstance start();

	public boolean end();
}
