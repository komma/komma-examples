package net.enilink.komma.example.behaviour;

import net.enilink.komma.example.behaviour.model.TaskInstance;

public interface ITaskExecution extends IActivityExecution {
	public boolean complete(TaskInstance instance, String transitionName);
}
