package net.enilink.komma.example.behaviour.runtime;

import net.enilink.composition.annotations.Precedes;
import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.example.behaviour.IActivityInstance;
import net.enilink.komma.example.behaviour.ITaskInstance;
import net.enilink.komma.example.behaviour.model.TaskInstance;

@Precedes(ActivityInstanceSupport.class)
public abstract class TaskInstanceSupport implements TaskInstance,
		Behaviour<TaskInstance>, ITaskInstance {

	@Override
	public boolean execute() {
		// this is a wait-state, not a normal activity
		setState(STATE_AWAITING_COMPLETION);

		// returning true here signals the end of the method chaining; in other
		// words, ActivityInstanceSupport.execute() will NOT be called
		return true;
	}

	@Override
	public boolean complete(String transitionName) {
		if (!STATE_AWAITING_COMPLETION.equals(getState())) {
			throw new IllegalStateException("Task " + getURI()
					+ " is not awaiting completion.");
		}

		((IActivityInstance) getBehaviourDelegate()).leave(transitionName);

		return true;
	}
}
