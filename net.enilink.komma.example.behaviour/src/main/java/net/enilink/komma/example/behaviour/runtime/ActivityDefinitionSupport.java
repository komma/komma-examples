package net.enilink.komma.example.behaviour.runtime;

import java.util.UUID;

import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.core.URI;
import net.enilink.komma.example.behaviour.IActivityDefinition;
import net.enilink.komma.example.behaviour.IActivityInstance;
import net.enilink.komma.example.behaviour.model.ActivityDefinition;
import net.enilink.komma.example.behaviour.model.ActivityInstance;
import net.enilink.komma.example.behaviour.model.ProcessInstance;

public abstract class ActivityDefinitionSupport implements ActivityDefinition,
		Behaviour<ActivityDefinition>, IActivityDefinition {

	@Override
	public ActivityInstance createActivityInstance(
			ProcessInstance processInstance) {
		URI aiUri = getURI().appendFragment(
				"ActivityInstance_" + getURI().fragment() + "_"
						+ UUID.randomUUID());

		ActivityInstance ai = getEntityManager().createNamed(aiUri,
				ActivityInstance.class);

		ai.setUsesDefinition(getBehaviourDelegate());
		ai.setProcessInstance(processInstance);

		((IActivityInstance) ai).enter();

		return ai;
	}
}
