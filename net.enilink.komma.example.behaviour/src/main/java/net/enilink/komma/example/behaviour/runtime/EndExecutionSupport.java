package net.enilink.komma.example.behaviour.runtime;

import net.enilink.composition.annotations.Precedes;
import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.example.behaviour.IActivityExecution;
import net.enilink.komma.example.behaviour.IProcessInstance;
import net.enilink.komma.example.behaviour.model.ActivityDefinition;
import net.enilink.komma.example.behaviour.model.ActivityInstance;

@Precedes(ActivityExecutionSupport.class)
public abstract class EndExecutionSupport implements ActivityDefinition,
		Behaviour<ActivityDefinition>, IActivityExecution {

	@Override
	public boolean execute(ActivityInstance instance) {

		// simplicity: name=="end" designates the end activity
		if (!"end".equals(instance.getUsesDefinition().getName())) {
			// returning false causes the invocation of this method on the next
			// behaviour in the chain (here: ActivityExecutionSupport.execute())
			return false;
		}

		// End activities will just terminate the enclosing process instance.
		// They will not be left as there are no outgoing transitions.
		instance.setState(ActivityExecutionSupport.STATE_COMPLETED);

		((IProcessInstance) instance.getProcessInstance()).end();

		return true;
	}
}
