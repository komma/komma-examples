package net.enilink.komma.example.behaviour.runtime;

import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.example.behaviour.IActivityDefinition;
import net.enilink.komma.example.behaviour.ITransition;
import net.enilink.komma.example.behaviour.model.ProcessInstance;
import net.enilink.komma.example.behaviour.model.Transition;

public abstract class TransitionSupport implements Transition,
		Behaviour<Transition>, ITransition {

	public void activate(ProcessInstance processInstance) {
		((IActivityDefinition) getToActivity())
				.createActivityInstance(processInstance);
	}
}
