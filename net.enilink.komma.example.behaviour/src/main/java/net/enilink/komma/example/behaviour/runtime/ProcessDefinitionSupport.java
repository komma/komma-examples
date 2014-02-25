package net.enilink.komma.example.behaviour.runtime;

import java.util.UUID;

import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.core.URI;
import net.enilink.komma.example.behaviour.IProcessDefinition;
import net.enilink.komma.example.behaviour.IProcessInstance;
import net.enilink.komma.example.behaviour.model.ProcessDefinition;
import net.enilink.komma.example.behaviour.model.ProcessInstance;

public abstract class ProcessDefinitionSupport implements ProcessDefinition,
		Behaviour<ProcessDefinition>, IProcessDefinition {

	@Override
	public ProcessInstance createProcessInstance() {
		URI piUri = getURI().appendFragment(
				"ProcessInstance_" + getURI().fragment() + "_"
						+ UUID.randomUUID());

		ProcessInstance pi = getEntityManager().createNamed(piUri,
				ProcessInstance.class);

		pi.setUsesDefinition(getBehaviourDelegate());

		((IProcessInstance) pi).start();

		return pi;
	}
}
