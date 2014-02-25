package net.enilink.komma.example.behaviour;

import net.enilink.komma.example.behaviour.model.ProcessInstance;

public interface IProcessInstance {
	public final static String STATE_ACTIVE = "active";
	public final static String STATE_ENDED = "ended";

	public ProcessInstance start();

	public boolean end();
}
