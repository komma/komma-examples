package net.enilink.komma.example.behaviour.runtime;

import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.example.behaviour.IActivityExecution;
import net.enilink.komma.example.behaviour.IActivityInstance;
import net.enilink.komma.example.behaviour.ITransition;
import net.enilink.komma.example.behaviour.model.ActivityInstance;
import net.enilink.komma.example.behaviour.model.Transition;

public abstract class ActivityInstanceSupport implements ActivityInstance,
		Behaviour<ActivityInstance>, IActivityInstance {

	@Override
	public boolean enter() {
		setState(ActivityExecutionSupport.STATE_OPEN);
		getProcessInstance().setCurrentActivity(getBehaviourDelegate());

		// can be implemented by more specialized support classes
		((IActivityExecution) getUsesDefinition())
				.execute(getBehaviourDelegate());

		return true;
	}

	@Override
	public boolean leave() {
		int t = -1;
		Transition[] transitions = getUsesDefinition().getHasTransition()
				.toArray(new Transition[0]);

		if (0 == transitions.length) {
			throw new IllegalStateException("attempted to leave activity "
					+ getUsesDefinition().getName()
					+ " without valid transitions");
		} else if (null == getTransitionName() && 1 != transitions.length) {
			throw new IllegalStateException("attempted to leave activity "
					+ getUsesDefinition().getName()
					+ " without specified transition (" + transitions.length
					+ " available)");
		} else if (null == getTransitionName()) {
			t = 0;
		} else {
			for (int i = 0; i < transitions.length; ++i) {
				if (getTransitionName().equals(transitions[i].getName())) {
					t = i;
					break;
				}
			}
		}

		if (-1 == t) {
			throw new IllegalArgumentException("attempted to leave activity "
					+ getUsesDefinition().getURI() + " via unknown transition "
					+ getTransitionName());
		}

		setState(ActivityExecutionSupport.STATE_COMPLETED);
		setTransitionName(transitions[t].getName());

		((ITransition) transitions[t]).activate(getProcessInstance());

		return true;
	}
}
