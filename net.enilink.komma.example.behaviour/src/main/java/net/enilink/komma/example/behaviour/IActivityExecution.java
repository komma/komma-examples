package net.enilink.komma.example.behaviour;

import net.enilink.komma.example.behaviour.model.ActivityInstance;

public interface IActivityExecution {
	public boolean execute(ActivityInstance instance);

	public boolean signal(ActivityInstance instance, String transitionName);

	public boolean isWaiting(ActivityInstance instance);
}
