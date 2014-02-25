package net.enilink.komma.example.behaviour.runtime;

import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.example.behaviour.IActivityInstance;
import net.enilink.komma.example.behaviour.ITransition;
import net.enilink.komma.example.behaviour.model.ActivityInstance;
import net.enilink.komma.example.behaviour.model.Transition;

public abstract class ActivityInstanceSupport implements ActivityInstance,
		Behaviour<ActivityInstance>, IActivityInstance {

	@Override
	public boolean enter() {
		setState(STATE_OPEN);
		getProcessInstance().setCurrentActivity(getBehaviourDelegate());

		// can be implemented by more specialized support classes
		// be sure to call them and don't just use this.execute()
		((IActivityInstance) getBehaviourDelegate()).execute();

		return true;
	}

	@Override
	public boolean execute() {
		if (!STATE_OPEN.equals(getState())) {
			throw new IllegalStateException("Activity " + getURI()
					+ " is not open.");
		}

		// can be implemented by more specialized support classes
		// be sure to call them and don't just use this.leave()
		((IActivityInstance) getBehaviourDelegate()).leave(null);

		return true;
	}

	@Override
	public boolean leave(String transitionName) {
		int t = -1;
		Transition[] transitions = getUsesDefinition().getHasTransition()
				.toArray(new Transition[0]);

		if (0 == transitions.length) {
			throw new IllegalStateException("attempted to leave activity "
					+ getUsesDefinition().getName()
					+ " without valid transitions");
		} else if (null == transitionName && 1 != transitions.length) {
			throw new IllegalStateException("attempted to leave activity "
					+ getUsesDefinition().getName()
					+ " without specified transition (" + transitions.length
					+ " available)");
		} else if (null == transitionName) {
			t = 0;
		} else {
			for (int i = 0; i < transitions.length; ++i) {
				if (transitionName.equals(transitions[i].getName())) {
					t = i;
					break;
				}
			}
		}

		if (-1 == t) {
			throw new IllegalArgumentException("attempted to leave activity "
					+ getUsesDefinition().getURI() + " via unknown transition "
					+ transitionName);
		}

		setState(STATE_COMPLETED);
		setTransitionName(transitions[t].getName());

		((ITransition) transitions[t]).activate(getProcessInstance());

		return true;
	}
}
