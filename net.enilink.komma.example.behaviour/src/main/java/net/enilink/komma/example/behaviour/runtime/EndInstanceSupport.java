package net.enilink.komma.example.behaviour.runtime;

import net.enilink.composition.annotations.Precedes;
import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.example.behaviour.IActivityInstance;
import net.enilink.komma.example.behaviour.IProcessInstance;
import net.enilink.komma.example.behaviour.model.ActivityInstance;

@Precedes(ActivityInstanceSupport.class)
public abstract class EndInstanceSupport implements ActivityInstance,
		Behaviour<ActivityInstance>, IActivityInstance {

	@Override
	public boolean execute() {
		// simplicity: name=="end" designates the end activity
		if (!"end".equals(getUsesDefinition().getName())) {
			// returning false causes the invocation of this method on the next
			// behaviour in the chain (here: ActivityInstanceSupport.execute())
			return false;
		}

		setState(STATE_COMPLETED);

		// End activities will just terminate the enclosing process instance.
		// They will not be left as there are no outgoing transitions.
		((IProcessInstance) getProcessInstance()).end();

		return true;
	}
}
