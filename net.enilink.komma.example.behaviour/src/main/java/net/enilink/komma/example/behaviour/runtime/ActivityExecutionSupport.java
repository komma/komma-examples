package net.enilink.komma.example.behaviour.runtime;

import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.example.behaviour.IActivityExecution;
import net.enilink.komma.example.behaviour.IActivityInstance;
import net.enilink.komma.example.behaviour.model.ActivityDefinition;
import net.enilink.komma.example.behaviour.model.ActivityInstance;

public abstract class ActivityExecutionSupport implements ActivityDefinition,
		Behaviour<ActivityDefinition>, IActivityExecution {

	public final static String STATE_OPEN = "open";
	public final static String STATE_WAITING = "waiting";
	public final static String STATE_COMPLETED = "completed";

	@Override
	public boolean execute(ActivityInstance instance) {
		if (!ProcessInstanceSupport.STATE_ACTIVE.equals(instance
				.getProcessInstance().getState())) {
			throw new IllegalStateException("Attempted to execute activity "
					+ getURI() + " while not in active process instance "
					+ instance.getProcessInstance().getURI());
		}

		((IActivityInstance) instance).leave();
		return true;
	}

	@Override
	public boolean signal(ActivityInstance instance, String transitionName) {
		if (!ActivityExecutionSupport.STATE_WAITING.equals(instance.getState())) {
			throw new IllegalStateException("Attempted to signal activity "
					+ getURI() + " while not waiting");
		}
		instance.setState(ActivityExecutionSupport.STATE_OPEN);
		instance.setTransitionName(transitionName);

		((IActivityInstance) instance).leave();

		return true;
	}

	@Override
	public boolean isWaiting(ActivityInstance instance) {
		return ActivityExecutionSupport.STATE_WAITING.equals(instance
				.getState());
	}
}
