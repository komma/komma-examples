package net.enilink.komma.example.smarthome.model;

import net.enilink.commons.iterator.IExtendedIterator;
import net.enilink.composition.annotations.Iri;
import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.core.IEntity;
import net.enilink.komma.core.IQuery;
import net.enilink.komma.core.ITransaction;
import net.enilink.komma.model.ModelUtil;
import net.enilink.vocab.rdfs.RDFS;

@Iri("http://komma.github.io/komma-examples/vocab/smarthome.ttl#FloorWideSwitch")
public abstract class FloorWideSwitchSupport implements Switch, IEntity, Behaviour<Switch> {
	public static final String PREFIX = "PREFIX rdfs: <" + RDFS.NAMESPACE + "> PREFIX sh: <" + //
			SMARTHOME.NAMESPACE + "> ";

	@Override
	public String label() {
		return ModelUtil.getLabel(getBehaviourDelegate());
	}

	public void on(boolean on) {
		IQuery<?> query = getEntityManager().createQuery(PREFIX
				+ "select ?target where { ?floor a sh:Floor; sh:contains+ ?this; " + //
				"sh:contains+ ?target . ?target a [rdfs:subClassOf* sh:Switchable] filter (?target != ?this) }");
		query.setParameter("this", this);
		withTransaction(() -> {
			try (IExtendedIterator<Switchable> targets = query.evaluate(Switchable.class)) {
				targets.forEach(t -> t.on(on));
			}
		});
	}

	protected void withTransaction(Runnable r) {
		ITransaction transaction = getEntityManager().getTransaction();
		boolean active = transaction.isActive();
		if (!active) {
			transaction.begin();
		}
		try {
			r.run();
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