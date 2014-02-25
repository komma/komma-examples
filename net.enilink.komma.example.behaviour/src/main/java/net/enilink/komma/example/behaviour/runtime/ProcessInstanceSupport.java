package net.enilink.komma.example.behaviour.runtime;

import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.example.behaviour.IActivityDefinition;
import net.enilink.komma.example.behaviour.IProcessInstance;
import net.enilink.komma.example.behaviour.model.ActivityDefinition;
import net.enilink.komma.example.behaviour.model.ProcessInstance;

public abstract class ProcessInstanceSupport implements ProcessInstance,
		Behaviour<ProcessInstance>, IProcessInstance {

	@Override
	public ProcessInstance start() {
		setState(STATE_ACTIVE);

		// find and activate the start activity
		// for simplicity, just use name=="start" as indicator
		for (ActivityDefinition activityDef : getUsesDefinition()
				.getHasActivity()) {
			if ("start".equals(activityDef.getName())) {
				((IActivityDefinition) activityDef)
						.createActivityInstance(getBehaviourDelegate());
			}
		}

		return getBehaviourDelegate();
	}

	@Override
	public boolean end() {
		setState(STATE_ENDED);

		return true;
	}
}
