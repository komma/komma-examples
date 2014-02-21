package net.enilink.komma.example.behaviour.runtime;

import net.enilink.composition.annotations.Precedes;
import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.example.behaviour.IActivityExecution;
import net.enilink.komma.example.behaviour.ITaskExecution;
import net.enilink.komma.example.behaviour.model.ActivityInstance;
import net.enilink.komma.example.behaviour.model.TaskDefinition;
import net.enilink.komma.example.behaviour.model.TaskInstance;

@Precedes(ActivityExecutionSupport.class)
public abstract class TaskExecutionSupport implements TaskDefinition,
		Behaviour<TaskDefinition>, ITaskExecution {

	public final static String STATE_AWAITING_COMPLETION = "awaiting_completion";

	@Override
	public boolean execute(ActivityInstance instance) {
		instance.setState(TaskExecutionSupport.STATE_AWAITING_COMPLETION);
		return true;
	}

	@Override
	public boolean signal(ActivityInstance instance, String transitionName) {
		if (TaskExecutionSupport.STATE_AWAITING_COMPLETION.equals(instance
				.getState())) {
			throw new IllegalStateException(
					"Tasks must not be signal()-ed directly. Use complete() instead.");
		}

		// task completed, fall back to the default behaviour by returning false
		return false;
	}

	@Override
	public boolean isWaiting(ActivityInstance instance) {
		return instance.getState().equals(
				TaskExecutionSupport.STATE_AWAITING_COMPLETION);
	}

	@Override
	public boolean complete(TaskInstance instance, String transitionName) {

		if (!isWaiting(instance)) {
			throw new IllegalStateException("Task is not awaiting completion.");
		}

		// set the state to waiting and signal()
		// the default activity behaviour will pick it up and continue execution
		instance.setState(ActivityExecutionSupport.STATE_WAITING);
		((IActivityExecution) getBehaviourDelegate()).signal(instance,
				transitionName);
		return true;
	}
}
