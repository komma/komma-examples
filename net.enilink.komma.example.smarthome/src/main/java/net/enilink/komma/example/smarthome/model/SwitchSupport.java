package net.enilink.komma.example.smarthome.model;

import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.core.IEntity;
import net.enilink.komma.core.ITransaction;
import net.enilink.komma.model.ModelUtil;

public abstract class SwitchSupport implements Switch, IEntity, Behaviour<Switch> {
	@Override
	public String label() {
		return ModelUtil.getLabel(getBehaviourDelegate()) + " - controls " + controls().size() + " devices.";
	}

	@Override
	public void on(boolean on) {
		ITransaction transaction = getEntityManager().getTransaction();
		boolean active = transaction.isActive();
		if (!active) {
			transaction.begin();
		}
		try {
			for (Switchable control : controls()) {
				control.on(on);
			}
			if (!active) {
				transaction.commit();
			}
		} catch (Exception e) {
			if (!active && transaction.isActive()) {
				transaction.rollback();
			}
		}
	}
}