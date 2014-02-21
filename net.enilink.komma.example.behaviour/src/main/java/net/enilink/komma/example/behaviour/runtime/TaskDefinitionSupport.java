package net.enilink.komma.example.behaviour.runtime;

import java.util.UUID;

import net.enilink.composition.annotations.Precedes;
import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.core.URI;
import net.enilink.komma.example.behaviour.IActivityDefinition;
import net.enilink.komma.example.behaviour.IActivityInstance;
import net.enilink.komma.example.behaviour.model.ActivityInstance;
import net.enilink.komma.example.behaviour.model.ProcessInstance;
import net.enilink.komma.example.behaviour.model.TaskDefinition;
import net.enilink.komma.example.behaviour.model.TaskInstance;

@Precedes(ActivityDefinitionSupport.class)
public abstract class TaskDefinitionSupport implements TaskDefinition,
		Behaviour<TaskDefinition>, IActivityDefinition {

	@Override
	public ActivityInstance createActivityInstance(
			ProcessInstance processInstance) {
		URI tiUri = getURI()
				.appendFragment(
						"TaskInstance_" + getURI().fragment() + "_"
								+ UUID.randomUUID());

		ActivityInstance ti = getEntityManager().createNamed(tiUri,
				TaskInstance.class);

		ti.setUsesDefinition(getBehaviourDelegate());
		ti.setProcessInstance(processInstance);

		((IActivityInstance) ti).enter();

		return ti;
	}
}
